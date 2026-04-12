import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client'
import type { EventFull, SessionData } from '../api/types'
import StatusPill from '../components/StatusPill'
import { formatDateTime } from '../lib/date'
import {
  describeAccess,
  describeEventState,
  describeModerationMode,
  formatRequests,
  formatViews,
} from '../lib/presenters'

type EventPageProps = {
  session: SessionData | null
}

function EventPage({ session }: EventPageProps) {
  const navigate = useNavigate()
  const params = useParams()
  const [event, setEvent] = useState<EventFull | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionMessage, setActionMessage] = useState('')
  const [actionLoading, setActionLoading] = useState(false)

  useEffect(() => {
    let active = true

    const loadEvent = async () => {
      const numericId = Number(params.eventId)
      if (Number.isNaN(numericId)) {
        setError('Некорректный идентификатор события')
        setLoading(false)
        return
      }

      try {
        const nextEvent = await api.getEvent(numericId)
        if (!active) {
          return
        }

        setEvent(nextEvent)
      } catch (loadError) {
        if (!active) {
          return
        }

        setError(loadError instanceof Error ? loadError.message : 'Не удалось загрузить событие')
      } finally {
        if (active) {
          setLoading(false)
        }
      }
    }

    void loadEvent()

    return () => {
      active = false
    }
  }, [params.eventId])

  const handleJoin = async () => {
    if (!event) {
      return
    }

    if (!session) {
      navigate('/auth')
      return
    }

    setActionLoading(true)
    setActionMessage('')

    try {
      const request = await api.createParticipationRequest(session.token, event.id)
      setActionMessage(`Готово: заявка создана со статусом ${request.status}.`)
    } catch (joinError) {
      setActionMessage(joinError instanceof Error ? joinError.message : 'Не удалось отправить заявку')
    } finally {
      setActionLoading(false)
    }
  }

  if (loading) {
    return <p className="muted-box">Открываю карточку события…</p>
  }

  if (error) {
    return <p className="message-box message-box--error">{error}</p>
  }

  if (!event) {
    return <p className="muted-box">Событие не найдено.</p>
  }

  const isOwner = session?.userId === event.initiator.id

  return (
    <div className="stack">
      <section className="section-panel detail-hero">
        <div className="detail-hero__content">
          <div className="detail-hero__topline">
            <Link className="text-link" to="/">
              Назад к афише
            </Link>
            <StatusPill label={describeEventState(event.state)} tone={event.state} />
          </div>

          <span className="eyebrow">{event.category.name}</span>
          <h1>{event.title}</h1>
          <p className="hero-panel__lead">{event.annotation}</p>
        </div>

        <aside className="detail-side-card">
          <dl className="detail-list">
            <div>
              <dt>Дата</dt>
              <dd>{formatDateTime(event.eventDate)}</dd>
            </div>
            <div>
              <dt>Организатор</dt>
              <dd>{event.initiator.name}</dd>
            </div>
            <div>
              <dt>Формат</dt>
              <dd>{describeAccess(event.paid)}</dd>
            </div>
            <div>
              <dt>Модерация</dt>
              <dd>{describeModerationMode(event.requestModeration)}</dd>
            </div>
            <div>
              <dt>Просмотры</dt>
              <dd>{formatViews(event.views)}</dd>
            </div>
            <div>
              <dt>Подтверждено</dt>
              <dd>{formatRequests(event.confirmedRequests)}</dd>
            </div>
          </dl>

          {isOwner ? (
            <p className="message-box">
              Это твоё событие. Управлять им удобнее из раздела «Кабинет».
            </p>
          ) : (
            <button
              className="primary-button"
              type="button"
              onClick={handleJoin}
              disabled={actionLoading}
            >
              {actionLoading ? 'Отправляю заявку…' : 'Записаться на участие'}
            </button>
          )}

          {actionMessage ? <p className="message-box">{actionMessage}</p> : null}
        </aside>
      </section>

      <section className="detail-grid">
        <article className="section-panel">
          <div className="section-panel__header">
            <div>
              <span className="eyebrow">Описание</span>
              <h2>Что увидит пользователь</h2>
            </div>
          </div>
          <p className="rich-text">{event.description}</p>
        </article>

        <article className="section-panel">
          <div className="section-panel__header">
            <div>
              <span className="eyebrow">Локация</span>
              <h2>Координаты события</h2>
            </div>
          </div>

          <div className="location-card">
            <strong>{event.location.lat.toFixed(4)}</strong>
            <span>широта</span>
            <strong>{event.location.lon.toFixed(4)}</strong>
            <span>долгота</span>
          </div>
        </article>
      </section>
    </div>
  )
}

export default EventPage
