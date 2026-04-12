import type {
  ApiErrorPayload,
  AuthResponse,
  Category,
  Compilation,
  EventFull,
  EventRequestStatusUpdateResult,
  EventReview,
  EventShort,
  LoginPayload,
  NewEventPayload,
  ParticipationRequest,
  PublicEventFilters,
  RegisterPayload,
} from './types'

type QueryValue = string | number | boolean | Array<string | number | boolean> | undefined

type RequestOptions = {
  method?: 'GET' | 'POST' | 'PATCH' | 'DELETE'
  token?: string
  body?: unknown
  query?: Record<string, QueryValue>
}

const API_BASE_URL =
  (import.meta.env.VITE_API_BASE_URL as string | undefined)?.replace(/\/$/, '') ??
  'http://localhost:8080'

function buildUrl(path: string, query?: Record<string, QueryValue>) {
  const url = new URL(path, API_BASE_URL)

  if (!query) {
    return url
  }

  for (const [key, value] of Object.entries(query)) {
    if (value === undefined || value === '') {
      continue
    }

    if (Array.isArray(value)) {
      for (const item of value) {
        url.searchParams.append(key, String(item))
      }
      continue
    }

    url.searchParams.set(key, String(value))
  }

  return url
}

async function request<T>(path: string, options: RequestOptions = {}) {
  const { method = 'GET', token, body, query } = options
  const headers = new Headers()

  if (body !== undefined) {
    headers.set('Content-Type', 'application/json')
  }

  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const response = await fetch(buildUrl(path, query), {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  if (!response.ok) {
    let message = response.statusText

    try {
      const payload = (await response.json()) as ApiErrorPayload
      const details = payload.errors?.[0]
      message = payload.message || payload.reason || response.statusText
      if (details && details !== message) {
        message = `${message}: ${details}`
      }
    } catch {
      const text = await response.text()
      if (text) {
        message = text
      }
    }

    throw new Error(message)
  }

  if (response.status === 204) {
    return undefined as T
  }

  const contentType = response.headers.get('content-type')
  if (contentType?.includes('application/json')) {
    return (await response.json()) as T
  }

  return undefined as T
}

export const api = {
  getCategories() {
    return request<Category[]>('/categories', {
      query: { from: 0, size: 20 },
    })
  },

  getPinnedCompilations() {
    return request<Compilation[]>('/compilations', {
      query: { pinned: true, from: 0, size: 6 },
    })
  },

  getEvents(filters: PublicEventFilters = {}) {
    return request<EventShort[]>('/events', {
      query: {
        text: filters.text,
        categories: filters.categoryIds,
        paid: filters.paid,
        onlyAvailable: filters.onlyAvailable ?? false,
        sort: filters.sort ?? 'EVENT_DATE',
        from: filters.from ?? 0,
        size: filters.size ?? 12,
      },
    })
  },

  getEvent(eventId: number) {
    return request<EventFull>(`/events/${eventId}`)
  },

  register(payload: RegisterPayload) {
    return request<AuthResponse>('/auth/register', {
      method: 'POST',
      body: payload,
    })
  },

  authenticate(payload: LoginPayload) {
    return request<AuthResponse>('/auth/authenticate', {
      method: 'POST',
      body: payload,
    })
  },

  getMyEvents(token: string) {
    return request<EventShort[]>('/users/me/events', {
      token,
      query: { from: 0, size: 20 },
    })
  },

  getMyEvent(token: string, eventId: number) {
    return request<EventFull>(`/users/me/events/${eventId}`, { token })
  },

  createEvent(token: string, payload: NewEventPayload) {
    return request<EventFull>('/users/me/events', {
      method: 'POST',
      token,
      body: payload,
    })
  },

  updateMyEventState(token: string, eventId: number, stateAction: string) {
    return request<EventFull>(`/users/me/events/${eventId}`, {
      method: 'PATCH',
      token,
      body: { stateAction },
    })
  },

  getMyRequests(token: string) {
    return request<ParticipationRequest[]>('/users/me/requests', { token })
  },

  createParticipationRequest(token: string, eventId: number) {
    return request<ParticipationRequest>('/users/me/requests', {
      method: 'POST',
      token,
      query: { eventId },
    })
  },

  cancelRequest(token: string, requestId: number) {
    return request<ParticipationRequest>(`/users/me/requests/${requestId}/cancel`, {
      method: 'PATCH',
      token,
    })
  },

  getEventRequests(token: string, eventId: number) {
    return request<ParticipationRequest[]>(`/users/me/events/${eventId}/requests`, {
      token,
    })
  },

  manageEventRequest(token: string, eventId: number, requestId: number, status: string) {
    return request<EventRequestStatusUpdateResult>(`/users/me/events/${eventId}/requests`, {
      method: 'PATCH',
      token,
      body: {
        requestIds: [requestId],
        status,
      },
    })
  },

  getEventReviews(token: string, eventId: number) {
    return request<EventReview[]>(`/users/me/events/${eventId}/reviews`, { token })
  },
}
