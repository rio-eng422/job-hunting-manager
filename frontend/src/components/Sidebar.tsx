import { NavLink } from 'react-router-dom'

const NAV = [
  { to: '/',             label: 'ダッシュボード', icon: '📊', end: true },
  { to: '/applications', label: '応募一覧',       icon: '📋', end: false },
  { to: '/companies',    label: '企業マスタ',     icon: '🏢', end: false },
]

export default function Sidebar() {
  return (
    <aside className="w-52 shrink-0 bg-blue-900 text-white flex flex-col">
      <div className="px-4 py-5 text-base font-bold tracking-wide border-b border-blue-800">
        就活管理ツール
      </div>
      <nav className="flex-1 py-3">
        {NAV.map(item => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 text-sm transition-colors ${
                isActive
                  ? 'bg-blue-700 text-white font-medium'
                  : 'text-blue-200 hover:bg-blue-800 hover:text-white'
              }`
            }
          >
            <span className="text-base">{item.icon}</span>
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}
