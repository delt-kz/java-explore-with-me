export type SessionData = {
  token: string
  email: string
  userId: number | null
  displayName?: string
  expiresAt: number | null
}

export type Category = {
  id: number
  name: string
}

export type UserShort = {
  id: number
  name: string
}

export type Location = {
  lat: number
  lon: number
}

export type EventShort = {
  id: number
  annotation: string
  category: Category
  confirmedRequests: number
  eventDate: string
  initiator: UserShort
  paid: boolean
  title: string
  views: number
}

export type EventFull = EventShort & {
  createdOn: string
  description: string
  location: Location
  participantLimit: number
  publishedOn: string | null
  requestModeration: boolean
  state: string
}

export type Compilation = {
  id: number
  events: EventShort[]
  pinned: boolean
  title: string
}

export type ParticipationRequest = {
  id: number
  created: string
  event: number
  requester: number
  status: string
}

export type EventReview = {
  id: number
  eventId: number
  comment: string
  status: string
  createdAt: string
}

export type EventRequestStatusUpdateResult = {
  confirmedRequests: ParticipationRequest[]
  rejectedRequests: ParticipationRequest[]
}

export type PublicEventFilters = {
  text?: string
  categoryIds?: number[]
  paid?: boolean
  onlyAvailable?: boolean
  sort?: 'EVENT_DATE' | 'VIEWS'
  from?: number
  size?: number
}

export type LoginPayload = {
  email: string
  password: string
}

export type RegisterPayload = LoginPayload & {
  name: string
}

export type AuthResponse = {
  token: string
}

export type NewEventPayload = {
  annotation: string
  category: number
  description: string
  eventDate: string
  location: Location
  paid: boolean
  participantLimit: number
  requestModeration: boolean
  title: string
}

export type ApiErrorPayload = {
  message?: string
  reason?: string
  errors?: string[]
}
