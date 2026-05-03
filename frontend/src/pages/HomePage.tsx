import type { FormEvent } from 'react'
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import type { Category, Compilation, EventShort, SessionData } from '../api/types'
import EventCard from '../components/EventCard'

type HomePageProps = {
  session: SessionData | null
}

type FilterState = {
  text: string
  categoryId: string
  paid: string
  onlyAvailable: boolean
  sort: 'EVENT_DATE' | 'VIEWS'
}

const initialFilters: FilterState = {
  text: '',
  categoryId: '',
  paid: '',
  onlyAvailable: false,
  sort: 'EVENT_DATE',
}

function HomePage({ session }: HomePageProps) {
  const [categories, setCategories] = useState<Category[]>([])
  const [compilations, setCompilations] = useState<Compilation[]>([])
  const [events, setEvents] = useState<EventShort[]>([])
  const [filters, setFilters] = useState<FilterState>(initialFilters)
  const [draftFilters, setDraftFilters] = useState<FilterState>(initialFilters)
  const [loading, setLoading] = useState(true)
  const [eventsLoading, setEventsLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true

    const loadReferenceData = async () => {
      try {
        const [nextCategories, nextCompilations] = await Promise.all([
          api.getCategories(),
          api.getPinnedCompilations(),
        ])

        if (!active) {
          return
        }

        setCategories(nextCategories)
        setCompilations(nextCompilations)
      } catch (loadError) {
        if (!active) {
          return
        }

        setError(loadError instanceof Error ? loadError.message : 'Не удалось загрузить каталог')
      } finally {
        if (active) {
          setLoading(false)
        }
      }
    }

    void loadReferenceData()

    return () => {
      active = false
    }
  }, [])

  useEffect(() => {
    let active = true

    const loadEvents = async () => {
      setEventsLoading(true)
      setError('')

      try {
        const nextEvents = await api.getEvents({
          text: filters.text || undefined,
          categoryIds: filters.categoryId ? [Number(filters.categoryId)] : undefined,
          paid: filters.paid === '' ? undefined : filters.paid === 'paid',
          onlyAvailable: filters.onlyAvailable,
          sort: filters.sort,
        })

        if (!active) {
          return
        }

        setEvents(nextEvents)
      } catch (loadError) {
        if (!active) {
          return
        }

        setError(loadError instanceof Error ? loadError.message : 'Не удалось загрузить события')
      } finally {
        if (active) {
          setEventsLoading(false)
        }
      }
    }

    void loadEvents()

    return () => {
      active = false
    }
  }, [filters])

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setFilters({ ...draftFilters })
  }

  const handleReset = () => {
    setDraftFilters(initialFilters)
    setFilters(initialFilters)
  }

  return (
    <div className="stack">
      <section className="hero-panel">
        <div className="hero-panel__content">
          <span className="eyebrow">Публичная витрина</span>
          <h1>Сайт показывает бэкенд через реальные сценарии</h1>
          <p className="hero-panel__lead">
            Сайт реализует не все эндпойнты, только пользовательские сценарии. Эндпойнты для админов не реализованы. 
          </p>

          <div className="hero-panel__actions">
            <Link className="primary-button" to={session ? '/cabinet' : '/auth'}>
              {session ? 'Открыть кабинет' : 'Зарегистрироваться'}
            </Link>
            <a className="ghost-link" href="#catalog">
              Перейти к событиям
            </a>
          </div>
        </div>

        <div className="info-card-grid">
          <article className="info-card">
            <span>Категорий</span>
            <strong>{categories.length}</strong>
            <p>Нужны для фильтрации и создания события в личном кабинете.</p>
          </article>
          <article className="info-card">
            <span>Подборок</span>
            <strong>{compilations.length}</strong>
            <p>Бэк умеет не только отдавать списки, но и собирать витрины.</p>
          </article>
          <article className="info-card">
            <span>Текущий режим</span>
            <strong>{session ? 'Вошел' : 'Гость'}</strong>
            <p>После входа поверх афиши открываются личные действия пользователя.</p>
          </article>
        </div>
      </section>

      <section className="section-panel">
        <div className="section-panel__header">
          <div>
            <span className="eyebrow">Подборки</span>
            <h2>Сценарии на главной</h2>
          </div>
        </div>

        {loading ? <p className="muted-box">Загружаю подборки и категории…</p> : null}

        {!loading && compilations.length > 0 ? (
          <div className="compilation-grid">
            {compilations.map((compilation) => (
              <article className="compilation-card" key={compilation.id}>
                <div className="compilation-card__topline">
                  <span>Подборка #{compilation.id}</span>
                  <span>{compilation.pinned ? 'На главной' : 'Обычная'}</span>
                </div>
                <h3>{compilation.title}</h3>
                <ul className="inline-list">
                  {compilation.events.slice(0, 3).map((item) => (
                    <li key={item.id}>
                      <Link to={`/events/${item.id}`}>{item.title}</Link>
                    </li>
                  ))}
                </ul>
              </article>
            ))}
          </div>
        ) : null}
      </section>

      <section className="section-panel" id="catalog">
        <div className="section-panel__header">
          <div>
            <span className="eyebrow">Каталог</span>
            <h2>Найти событие</h2>
          </div>
        </div>

        <form className="filters" onSubmit={handleSubmit}>
          <label>
            Поиск
            <input
              type="text"
              value={draftFilters.text}
              onChange={(event) =>
                setDraftFilters((current) => ({ ...current, text: event.target.value }))
              }
              placeholder="Например, Java, music или lecture"
            />
          </label>

          <label>
            Категория
            <select
              value={draftFilters.categoryId}
              onChange={(event) =>
                setDraftFilters((current) => ({ ...current, categoryId: event.target.value }))
              }
            >
              <option value="">Все категории</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </label>

          <label>
            Тип участия
            <select
              value={draftFilters.paid}
              onChange={(event) =>
                setDraftFilters((current) => ({ ...current, paid: event.target.value }))
              }
            >
              <option value="">Любой</option>
              <option value="free">Только бесплатные</option>
              <option value="paid">Только платные</option>
            </select>
          </label>

          <label>
            Сортировка
            <select
              value={draftFilters.sort}
              onChange={(event) =>
                setDraftFilters((current) => ({
                  ...current,
                  sort: event.target.value as FilterState['sort'],
                }))
              }
            >
              <option value="EVENT_DATE">По дате события</option>
              <option value="VIEWS">По просмотрам</option>
            </select>
          </label>

          <label className="checkbox-field">
            <input
              type="checkbox"
              checked={draftFilters.onlyAvailable}
              onChange={(event) =>
                setDraftFilters((current) => ({
                  ...current,
                  onlyAvailable: event.target.checked,
                }))
              }
            />
            <span>Только с доступными местами</span>
          </label>

          <div className="filters__actions">
            <button className="primary-button" type="submit">
              Применить
            </button>
            <button className="ghost-button" type="button" onClick={handleReset}>
              Сбросить
            </button>
          </div>
        </form>

        {error ? <p className="message-box message-box--error">{error}</p> : null}
        {eventsLoading ? <p className="muted-box">Загружаю события…</p> : null}

        {!eventsLoading && events.length === 0 ? (
          <p className="muted-box">
            По текущим фильтрам ничего не нашлось. Это тоже хороший сценарий для проверки API.
          </p>
        ) : null}

        {!eventsLoading && events.length > 0 ? (
          <div className="event-grid">
            {events.map((item) => (
              <EventCard
                event={item}
                key={item.id}
                footer={
                  <Link className="text-link" to={`/events/${item.id}`}>
                    Открыть карточку
                  </Link>
                }
              />
            ))}
          </div>
        ) : null}
      </section>
    </div>
  )
}

export default HomePage
