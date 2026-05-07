import type { ReactNode } from 'react'
import { NavLink } from 'react-router-dom'
import type { SessionData } from '../api/types'

type ShellProps = {
  children: ReactNode
  session: SessionData | null
  onLogout: () => void
}

function Shell({ children, session, onLogout }: ShellProps) {
  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand-block">
          <NavLink className="brand" to="/">
            <span className="brand__eyebrow">Explore With Me</span>
            <span className="brand__title">Мини сайт для демонстраций api backend</span>
          </NavLink>
          <p className="brand__subtitle">
            Витрина событий и пдоборок
          </p>
        </div>

        <div className="topbar__controls">
          <nav className="main-nav" aria-label="Основная навигация">
            <NavLink className="main-nav__link" to="/">
              Афиша
            </NavLink>
            <NavLink className="main-nav__link" to="/cabinet">
              Кабинет
            </NavLink>
            <NavLink className="main-nav__link" to="/auth">
              {session ? 'Сменить пользователя' : 'Войти'}
            </NavLink>
          </nav>

          <div className="session-card">
            <span className="session-card__label">
              {session ? 'Сессия активна' : 'Гость'}
            </span>
            <strong>{session?.displayName || session?.email || 'Без авторизации'}</strong>
            {session ? (
              <button className="ghost-button" type="button" onClick={onLogout}>
                Выйти
              </button>
            ) : null}
          </div>
        </div>
      </header>

      <main className="page">{children}</main>

    </div>
  )
}

export default Shell
