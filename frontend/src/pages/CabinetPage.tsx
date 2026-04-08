import type { FormEvent } from 'react'
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import type {
  Category,
  EventFull,
  EventReview,
  ParticipationRequest,
  SessionData,
} from '../api/types'
import EventCard from '../components/EventCard'
import StatusPill from '../components/StatusPill'
import { formatDateTime, toBackendDateTime } from '../lib/date'
import {
  describeEventState,
  describeRequestStatus,
  describeReviewStatus,
} from '../lib/presenters'

type CabinetPageProps = {
  session: SessionData | null
}

type NewEventForm = {
  title: string
  category: string
  eventDate: string
  annotation: string
  description: string
  lat: string
  lon: string
  paid: boolean
  participantLimit: string
  requestModeration: boolean
}

const initialForm: NewEventForm = {
  title: '',
  category: '',
  eventDate: '',
  annotation: '',
  description: '',
  lat: '51.1605',
  lon: '71.4704',
  paid: false,
  participantLimit: '0',
  requestModeration: true,
}

function CabinetPage({ session }: CabinetPageProps) {
  const [categories, setCategories] = useState<Category[]>([])
  const [events, setEvents] = useState<EventFull[]>([])
  const [requests, setRequests] = useState<ParticipationRequest[]>([])
  const [eventRequests, setEventRequests] = useState<Record<number, ParticipationRequest[]>>({})
  const [eventReviews, setEventReviews] = useState<Record<number, EventReview[]>>({})
  const [expandedEventId, setExpandedEventId] = useState<number | null>(null)
  const [reviewsEventId, setReviewsEventId] = useState<number | null>(null)
  const [form, setForm] = useState<NewEventForm>(initialForm)
  const [loading, setLoading] = useState(false)
  const [submitLoading, setSubmitLoading] = useState(false)
  const [message, setMessage] = useState('')

  useEffect(() => {
    if (!session) {
      return
    }

    let active = true

    const loadCabinet = async () => {
      setLoading(true)
      setMessage('')

      try {
        const [nextCategories, eventShortList, nextRequests] = await Promise.all([
          api.getCategories(),
          api.getMyEvents(session.token),
          api.getMyRequests(session.token),
        ])

        const detailedEvents = await Promise.all(
          eventShortList.map((item) => api.getMyEvent(session.token, item.id)),
        )

        if (!active) {
          return
        }

        setCategories(nextCategories)
        setEvents(detailedEvents)
        setRequests(nextRequests)
      } catch (loadError) {
        if (!active) {
          return
        }

        setMessage(loadError instanceof Error ? loadError.message : 'Не удалось загрузить кабинет')
      } finally {
        if (active) {
          setLoading(false)
        }
      }
    }

    void loadCabinet()

    return () => {
      active = false
    }
  }, [session])

  if (!session) {
    return (
      <section className="section-panel">
        <span className="eyebrow">Кабинет</span>
        <h1>Сначала нужен вход</h1>
        <p className="hero-panel__lead">
          Личный раздел работает только с JWT. После авторизации здесь появятся защищённые
          запросы к `/users/me/...`.
        </p>
        <Link className="primary-button" to="/auth">
          Перейти ко входу
        </Link>
      </section>
    )
  }

  const refreshEvents = async () => {
    const eventShortList = await api.getMyEvents(session.token)
    const detailedEvents = await Promise.all(
      eventShortList.map((item) => api.getMyEvent(session.token, item.id)),
    )
    setEvents(detailedEvents)
  }

  const refreshRequests = async () => {
    const nextRequests = await api.getMyRequests(session.token)
    setRequests(nextRequests)
  }

  const handleCreateEvent = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setSubmitLoading(true)
    setMessage('')

    try {
      await api.createEvent(session.token, {
        title: form.title,
        category: Number(form.category),
        eventDate: toBackendDateTime(form.eventDate),
        annotation: form.annotation,
        description: form.description,
        location: {
          lat: Number(form.lat),
          lon: Number(form.lon),
        },
        paid: form.paid,
        participantLimit: Number(form.participantLimit || '0'),
        requestModeration: form.requestModeration,
      })

      setForm(initialForm)
      await refreshEvents()
      setMessage('Событие создано. Теперь оно появилось в твоём кабинете и ждёт дальнейших действий.')
    } catch (submitError) {
      setMessage(submitError instanceof Error ? submitError.message : 'Не удалось создать событие')
    } finally {
      setSubmitLoading(false)
    }
  }

  const handleStateAction = async (eventId: number, action: string) => {
    setMessage('')

    try {
      const updated = await api.updateMyEventState(session.token, eventId, action)
      setEvents((current) => current.map((item) => (item.id === updated.id ? updated : item)))
      setMessage(`Событие #${eventId}: ${describeEventState(updated.state)}.`)
    } catch (actionError) {
      setMessage(actionError instanceof Error ? actionError.message : 'Не удалось изменить состояние')
    }
  }

  const handleToggleRequests = async (eventId: number) => {
    if (expandedEventId === eventId) {
      setExpandedEventId(null)
      return
    }

    setExpandedEventId(eventId)

    if (eventRequests[eventId]) {
      return
    }

    try {
      const nextRequests = await api.getEventRequests(session.token, eventId)
      setEventRequests((current) => ({ ...current, [eventId]: nextRequests }))
    } catch (loadError) {
      setMessage(loadError instanceof Error ? loadError.message : 'Не удалось загрузить заявки события')
    }
  }

  const handleModerateRequest = async (eventId: number, requestId: number, status: string) => {
    setMessage('')

    try {
      await api.manageEventRequest(session.token, eventId, requestId, status)
      const nextEventRequests = await api.getEventRequests(session.token, eventId)
      await refreshEvents()
      setEventRequests((current) => ({ ...current, [eventId]: nextEventRequests }))
      setMessage(`Заявка #${requestId} обновлена.`)
    } catch (moderateError) {
      setMessage(moderateError instanceof Error ? moderateError.message : 'Не удалось обновить заявку')
    }
  }

  const handleToggleReviews = async (eventId: number) => {
    if (reviewsEventId === eventId) {
      setReviewsEventId(null)
      return
    }

    setReviewsEventId(eventId)

    if (eventReviews[eventId]) {
      return
    }

    try {
      const nextReviews = await api.getEventReviews(session.token, eventId)
      setEventReviews((current) => ({ ...current, [eventId]: nextReviews }))
    } catch (loadError) {
      setMessage(loadError instanceof Error ? loadError.message : 'Не удалось загрузить историю модерации')
    }
  }

  const handleCancelRequest = async (requestId: number) => {
    setMessage('')

    try {
      await api.cancelRequest(session.token, requestId)
      await refreshRequests()
      setMessage(`Заявка #${requestId} отменена.`)
    } catch (cancelError) {
      setMessage(cancelError instanceof Error ? cancelError.message : 'Не удалось отменить заявку')
    }
  }

  return (
    <div className="stack">
      <section className="section-panel">
        <div className="section-panel__header">
          <div>
            <span className="eyebrow">Личный раздел</span>
            <h1>Кабинет организатора и участника</h1>
          </div>
        </div>

        <p className="hero-panel__lead">
          Здесь один экран покрывает несколько приватных сценариев: создание события,
          повторная отправка на модерацию, просмотр заявок и личных откликов.
        </p>

        {message ? <p className="message-box">{message}</p> : null}
        {loading ? <p className="muted-box">Собираю твои данные…</p> : null}
      </section>

      <section className="section-panel">
        <div className="section-panel__header">
          <div>
            <span className="eyebrow">Создание</span>
            <h2>Новое событие</h2>
          </div>
        </div>

        <form className="event-form" onSubmit={handleCreateEvent}>
          <label>
            Название
            <input
              type="text"
              minLength={3}
              maxLength={120}
              value={form.title}
              onChange={(event) => setForm((current) => ({ ...current, title: event.target.value }))}
              required
            />
          </label>

          <label>
            Категория
            <select
              value={form.category}
              onChange={(event) => setForm((current) => ({ ...current, category: event.target.value }))}
              required
            >
              <option value="">Выбрать категорию</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </label>

          <label>
            Дата и время
            <input
              type="datetime-local"
              value={form.eventDate}
              onChange={(event) => setForm((current) => ({ ...current, eventDate: event.target.value }))}
              required
            />
          </label>

          <label>
            Аннотация
            <textarea
              minLength={20}
              maxLength={2000}
              rows={4}
              value={form.annotation}
              onChange={(event) =>
                setForm((current) => ({ ...current, annotation: event.target.value }))
              }
              required
            />
          </label>

          <label className="event-form__wide">
            Полное описание
            <textarea
              minLength={20}
              maxLength={7000}
              rows={6}
              value={form.description}
              onChange={(event) =>
                setForm((current) => ({ ...current, description: event.target.value }))
              }
              required
            />
          </label>

          <label>
            Широта
            <input
              type="number"
              step="0.0001"
              value={form.lat}
              onChange={(event) => setForm((current) => ({ ...current, lat: event.target.value }))}
              required
            />
          </label>

          <label>
            Долгота
            <input
              type="number"
              step="0.0001"
              value={form.lon}
              onChange={(event) => setForm((current) => ({ ...current, lon: event.target.value }))}
              required
            />
          </label>

          <label>
            Лимит участников
            <input
              type="number"
              min="0"
              value={form.participantLimit}
              onChange={(event) =>
                setForm((current) => ({ ...current, participantLimit: event.target.value }))
              }
              required
            />
          </label>

          <label className="checkbox-field">
            <input
              type="checkbox"
              checked={form.paid}
              onChange={(event) => setForm((current) => ({ ...current, paid: event.target.checked }))}
            />
            <span>Платное участие</span>
          </label>

          <label className="checkbox-field">
            <input
              type="checkbox"
              checked={form.requestModeration}
              onChange={(event) =>
                setForm((current) => ({ ...current, requestModeration: event.target.checked }))
              }
            />
            <span>Подтверждать заявки вручную</span>
          </label>

          <button className="primary-button" type="submit" disabled={submitLoading}>
            {submitLoading ? 'Создаю событие…' : 'Создать событие'}
          </button>
        </form>
      </section>

      <section className="section-panel">
        <div className="section-panel__header">
          <div>
            <span className="eyebrow">Мои события</span>
            <h2>Управление публикацией</h2>
          </div>
        </div>

        {events.length === 0 ? (
          <p className="muted-box">Пока событий нет. Создай первое прямо в форме выше.</p>
        ) : (
          <div className="event-grid">
            {events.map((item) => (
              <EventCard
                event={item}
                key={item.id}
                showState
                footer={
                  <div className="inline-actions">
                    {item.state === 'PENDING' ? (
                      <button
                        className="ghost-button"
                        type="button"
                        onClick={() => handleStateAction(item.id, 'CANCEL_REVIEW')}
                      >
                        Снять с модерации
                      </button>
                    ) : null}

                    {(item.state === 'CANCELED' || item.state === 'REVISION_REQUIRED') ? (
                      <button
                        className="ghost-button"
                        type="button"
                        onClick={() => handleStateAction(item.id, 'SEND_TO_REVIEW')}
                      >
                        Отправить снова
                      </button>
                    ) : null}

                    <button
                      className="ghost-button"
                      type="button"
                      onClick={() => handleToggleRequests(item.id)}
                    >
                      {expandedEventId === item.id ? 'Скрыть заявки' : 'Показать заявки'}
                    </button>

                    {item.state === 'REVISION_REQUIRED' ? (
                      <button
                        className="ghost-button"
                        type="button"
                        onClick={() => handleToggleReviews(item.id)}
                      >
                        {reviewsEventId === item.id ? 'Скрыть отзыв' : 'Почему вернули'}
                      </button>
                    ) : null}
                  </div>
                }
              />
            ))}
          </div>
        )}

        {expandedEventId ? (
          <div className="nested-panel">
            <h3>Заявки по событию #{expandedEventId}</h3>
            {(eventRequests[expandedEventId] ?? []).length === 0 ? (
              <p className="muted-box">Пока заявок нет.</p>
            ) : (
              <div className="request-list">
                {(eventRequests[expandedEventId] ?? []).map((request) => (
                  <article className="request-card" key={request.id}>
                    <div>
                      <strong>Заявка #{request.id}</strong>
                      <p>Участник #{request.requester}</p>
                      <p>{formatDateTime(request.created)}</p>
                    </div>
                    <div className="request-card__actions">
                      <StatusPill
                        label={describeRequestStatus(request.status)}
                        tone={request.status === 'CANCELED' ? 'CANCELED_REQUEST' : request.status}
                      />
                      {request.status === 'PENDING' ? (
                        <>
                          <button
                            className="ghost-button"
                            type="button"
                            onClick={() =>
                              handleModerateRequest(expandedEventId, request.id, 'CONFIRMED')
                            }
                          >
                            Подтвердить
                          </button>
                          <button
                            className="ghost-button ghost-button--danger"
                            type="button"
                            onClick={() =>
                              handleModerateRequest(expandedEventId, request.id, 'REJECTED')
                            }
                          >
                            Отклонить
                          </button>
                        </>
                      ) : null}
                    </div>
                  </article>
                ))}
              </div>
            )}
          </div>
        ) : null}

        {reviewsEventId ? (
          <div className="nested-panel">
            <h3>История модерации события #{reviewsEventId}</h3>
            {(eventReviews[reviewsEventId] ?? []).length === 0 ? (
              <p className="muted-box">Комментарии модерации пока не пришли.</p>
            ) : (
              <div className="review-list">
                {(eventReviews[reviewsEventId] ?? []).map((review) => (
                  <article className="review-card" key={review.id}>
                    <div className="review-card__topline">
                      <StatusPill
                        label={describeReviewStatus(review.status)}
                        tone={review.status}
                      />
                      <span>{formatDateTime(review.createdAt)}</span>
                    </div>
                    <p>{review.comment}</p>
                  </article>
                ))}
              </div>
            )}
          </div>
        ) : null}
      </section>

      <section className="section-panel">
        <div className="section-panel__header">
          <div>
            <span className="eyebrow">Мои отклики</span>
            <h2>Участие в чужих событиях</h2>
          </div>
        </div>

        {requests.length === 0 ? (
          <p className="muted-box">Ты ещё не отправлял заявки на участие.</p>
        ) : (
          <div className="request-list">
            {requests.map((request) => (
              <article className="request-card" key={request.id}>
                <div>
                  <strong>Событие #{request.event}</strong>
                  <p>Заявка #{request.id}</p>
                  <p>{formatDateTime(request.created)}</p>
                </div>
                <div className="request-card__actions">
                  <StatusPill
                    label={describeRequestStatus(request.status)}
                    tone={request.status === 'CANCELED' ? 'CANCELED_REQUEST' : request.status}
                  />
                  {request.status !== 'CANCELED' ? (
                    <button
                      className="ghost-button ghost-button--danger"
                      type="button"
                      onClick={() => handleCancelRequest(request.id)}
                    >
                      Отменить
                    </button>
                  ) : null}
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  )
}

export default CabinetPage
