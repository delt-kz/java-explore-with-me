import { useState } from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import './App.css'
import type { SessionData } from './api/types'
import Shell from './components/Shell'
import { clearSession, createSession, loadSession, saveSession } from './lib/auth'
import AuthPage from './pages/AuthPage'
import CabinetPage from './pages/CabinetPage'
import EventPage from './pages/EventPage'
import HomePage from './pages/HomePage'

function App() {
  const [session, setSession] = useState<SessionData | null>(() => loadSession())

  const handleAuthenticated = (token: string, profile?: Partial<SessionData>) => {
    const nextSession = createSession(token, profile)
    saveSession(nextSession)
    setSession(nextSession)
  }

  const handleLogout = () => {
    clearSession()
    setSession(null)
  }

  return (
    <BrowserRouter>
      <Shell session={session} onLogout={handleLogout}>
        <Routes>
          <Route path="/" element={<HomePage session={session} />} />
          <Route
            path="/auth"
            element={
              <AuthPage
                session={session}
                onAuthenticated={handleAuthenticated}
              />
            }
          />
          <Route path="/events/:eventId" element={<EventPage session={session} />} />
          <Route path="/cabinet" element={<CabinetPage session={session} />} />
        </Routes>
      </Shell>
    </BrowserRouter>
  )
}

export default App
