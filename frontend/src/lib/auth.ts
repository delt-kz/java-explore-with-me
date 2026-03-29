import type { SessionData } from '../api/types'

const STORAGE_KEY = 'ewm.frontend.session'

type JwtPayload = {
  sub?: string
  exp?: number
  userId?: number
}

function decodeBase64Url(value: string) {
  const normalized = value.replace(/-/g, '+').replace(/_/g, '/')
  const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=')

  return decodeURIComponent(
    atob(padded)
      .split('')
      .map((char) => `%${char.charCodeAt(0).toString(16).padStart(2, '0')}`)
      .join(''),
  )
}

function parseJwt(token: string): JwtPayload | null {
  try {
    const payload = token.split('.')[1]
    if (!payload) {
      return null
    }

    return JSON.parse(decodeBase64Url(payload)) as JwtPayload
  } catch {
    return null
  }
}

export function createSession(token: string, profile?: Partial<SessionData>): SessionData {
  const payload = parseJwt(token)

  return {
    token,
    email: profile?.email || payload?.sub || '',
    userId: typeof payload?.userId === 'number' ? payload.userId : null,
    displayName: profile?.displayName,
    expiresAt: typeof payload?.exp === 'number' ? payload.exp * 1000 : null,
  }
}

export function loadSession() {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) {
    return null
  }

  try {
    const parsed = JSON.parse(raw) as SessionData

    if (parsed.expiresAt && parsed.expiresAt < Date.now()) {
      localStorage.removeItem(STORAGE_KEY)
      return null
    }

    return parsed
  } catch {
    localStorage.removeItem(STORAGE_KEY)
    return null
  }
}

export function saveSession(session: SessionData) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
}

export function clearSession() {
  localStorage.removeItem(STORAGE_KEY)
}
