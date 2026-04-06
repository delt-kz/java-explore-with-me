import type { FormEvent } from 'react'
import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import type { SessionData } from '../api/types'

type AuthPageProps = {
  session: SessionData | null
  onAuthenticated: (token: string, profile?: Partial<SessionData>) => void
}

function AuthPage({ session, onAuthenticated }: AuthPageProps) {
  const navigate = useNavigate()
  const [mode, setMode] = useState<'login' | 'register'>('register')
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  if (session) {
    return <Navigate to="/cabinet" replace />
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setLoading(true)
    setError('')

    try {
      const response =
        mode === 'register'
          ? await api.register({ name, email, password })
          : await api.authenticate({ email, password })

      onAuthenticated(response.token, {
        email,
        displayName: mode === 'register' ? name : undefined,
      })
      navigate('/cabinet')
    } catch (authError) {
      setError(authError instanceof Error ? authError.message : 'Не удалось выполнить вход')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="stack">
      <section className="section-panel auth-layout">
        <div className="auth-copy">
          <span className="eyebrow">Авторизация</span>
          <h1>{mode === 'register' ? 'Создай пользователя' : 'Вернись в кабинет'}</h1>
          <p className="hero-panel__lead">
            Здесь фронт работает с твоими `POST /auth/register` и `POST /auth/authenticate`.
            После входа JWT сохраняется в браузере, а личные страницы начинают ходить в
            защищённые endpoint&apos;ы.
          </p>

          <div className="mode-switch">
            <button
              type="button"
              className={mode === 'register' ? 'mode-switch__button is-active' : 'mode-switch__button'}
              onClick={() => setMode('register')}
            >
              Регистрация
            </button>
            <button
              type="button"
              className={mode === 'login' ? 'mode-switch__button is-active' : 'mode-switch__button'}
              onClick={() => setMode('login')}
            >
              Вход
            </button>
          </div>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          {mode === 'register' ? (
            <label>
              Имя
              <input
                type="text"
                value={name}
                onChange={(event) => setName(event.target.value)}
                minLength={2}
                required
              />
            </label>
          ) : null}

          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </label>

          <label>
            Пароль
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </label>

          {error ? <p className="message-box message-box--error">{error}</p> : null}

          <button className="primary-button" type="submit" disabled={loading}>
            {loading
              ? 'Подождать…'
              : mode === 'register'
                ? 'Зарегистрировать и войти'
                : 'Войти'}
          </button>
        </form>
      </section>
    </div>
  )
}

export default AuthPage
