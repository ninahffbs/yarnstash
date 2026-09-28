import { Link, Navigate, Route, Routes, useLocation } from 'react-router-dom'
import YarnListPage from './features/yarn/YarnListPage'
import ProjectListPage from './features/project/ProjectListPage';
import YarnFormPage from './features/yarn/YarnFormPage';

function NavLink({ to, children }: { to: string; children: React.ReactNode }) {
  const { pathname } = useLocation()
  const active = pathname.startsWith(to)

  return (
    <Link
      to={to}
      className={
        active
          ? 'text-sm font-medium text-stone-900'
          : 'text-sm text-stone-500 hover:text-stone-900'
      }
    >
      {children}
    </Link>
  )
}

export default function App() {
  return (
    <div className="min-h-screen bg-stone-50 text-stone-900 antialiased">
      <header className="border-b border-stone-200 bg-white">
        <nav className="mx-auto flex max-w-5xl items-center gap-6 px-6 py-4">
          <span className="font-semibold">Yarnstash</span>
          <NavLink to="/yarns">Stash</NavLink>
          <NavLink to="/projects">Projects</NavLink>
        </nav>
      </header>

      <main className="mx-auto max-w-5xl px-6 py-8">
        <Routes>
          <Route path="/" element={<Navigate to="/yarns" replace />} />
          <Route path="/yarns" element={<YarnListPage />} />
          <Route path="/yarns/new" element={<YarnFormPage />} />
          <Route path="/yarns/:id/edit" element={<YarnFormPage />} />
          <Route path="/projects" element={<ProjectListPage />} />
          <Route path="*" element={<p className="text-sm text-stone-500">Page not found.</p>} />
        </Routes>
      </main>
    </div>
  )
}
