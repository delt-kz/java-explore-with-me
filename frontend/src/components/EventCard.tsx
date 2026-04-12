import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import type { EventFull, EventShort } from '../api/types'
import { formatDateTime } from '../lib/date'
import {
  describeAccess,
  describeEventState,
  describeModerationMode,
  formatRequests,
  formatViews,
} from '../lib/presenters'
import StatusPill from './StatusPill'

type EventCardProps = {
  event: EventShort | EventFull
  footer?: ReactNode
  showState?: boolean
}

function EventCard({ event, footer, showState = false }: EventCardProps) {
  const fullEvent = event as EventFull

  return (
    <article className="event-card">
      <div className="event-card__header">
        <div>
          <span className="event-card__category">{event.category.name}</span>
          <h3>
            <Link to={`/events/${event.id}`}>{event.title}</Link>
          </h3>
        </div>
        {showState && fullEvent.state ? (
          <StatusPill label={describeEventState(fullEvent.state)} tone={fullEvent.state} />
        ) : null}
      </div>

      <p className="event-card__annotation">{event.annotation}</p>

      {'description' in fullEvent ? (
        <p className="event-card__description">{fullEvent.description}</p>
      ) : null}

      <dl className="event-card__meta">
        <div>
          <dt>Когда</dt>
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
          <dt>Отклик</dt>
          <dd>
            {formatViews(event.views)} · {formatRequests(event.confirmedRequests)}
          </dd>
        </div>
        {'participantLimit' in fullEvent ? (
          <div>
            <dt>Лимит</dt>
            <dd>
              {fullEvent.participantLimit === 0
                ? 'Без лимита'
                : `${fullEvent.participantLimit} мест`}
            </dd>
          </div>
        ) : null}
        {'requestModeration' in fullEvent ? (
          <div>
            <dt>Заявки</dt>
            <dd>{describeModerationMode(fullEvent.requestModeration)}</dd>
          </div>
        ) : null}
      </dl>

      {footer ? <div className="event-card__footer">{footer}</div> : null}
    </article>
  )
}

export default EventCard
