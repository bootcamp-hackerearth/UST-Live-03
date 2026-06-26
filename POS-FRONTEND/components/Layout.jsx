'use client';

import { useEffect, useState } from 'react';
import {
  UserCircleIcon,
  PowerIcon,
  Bars3Icon,
} from '@heroicons/react/24/outline';
import { usePathname, useRouter } from 'next/navigation';
import PropTypes from 'prop-types';

import api from '@/app/services/api';
import Profile from '@/components/Profile';

const SIDEBAR_EXPANDED = 220;
const SIDEBAR_COLLAPSED = 68;

const BADGE_COLORS = [
  { bg: '#3B4754', text: '#B8C4CF' },
  { bg: '#2C424E', text: '#90B4C3' },
  { bg: '#414854', text: '#B0B8C5' },
  { bg: '#2C4D43', text: '#88B8A8' },
  { bg: '#42394F', text: '#B0A8C8' },
  { bg: '#4D422C', text: '#C8B090' },
];

const colorFor = (key = '') => {
  let hash = 0;

  for (let i = 0; i < key.length; i += 1) {
    hash = Math.trunc(
      hash * 31 + (key.codePointAt(i) ?? 0)
    );
  }

  return BADGE_COLORS[
    Math.abs(hash) % BADGE_COLORS.length
  ];
};

const Layout = ({ children }) => {
  const router = useRouter();
  const pathname = usePathname();

  const [username, setUsername] = useState('');
  const [nodes, setNodes] = useState([]);
  const [showProfileModal, setShowProfileModal] = useState(false);
  const [collapsed, setCollapsed] = useState(false);

  useEffect(() => {
    setUsername(localStorage.getItem('username') || '');
    const stored = localStorage.getItem('sidebarCollapsed');
    if (stored !== null) setCollapsed(stored === 'true');
    void fetchNodes();
  }, []);

  const fetchNodes = async () => {
    try {
      const response = await api.get('/home');
      setNodes(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const toggleCollapsed = () => {
    setCollapsed((prev) => {
      localStorage.setItem('sidebarCollapsed', String(!prev));
      return !prev;
    });
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('userRole');
    router.push('/login');
  };

  const sidebarWidth = collapsed ? SIDEBAR_COLLAPSED : SIDEBAR_EXPANDED;
  const initials = username ? username.trim().charAt(0).toUpperCase() : '?';

  return (
    <div
      className="min-h-screen grid bg-[#F4F5F7]"
      style={{
        gridTemplateColumns: `${sidebarWidth}px 1fr`,
        transition: 'grid-template-columns 280ms ease-in-out',
        fontFamily: "'Inter', 'Poppins', sans-serif",
      }}
    >
      <aside className="h-screen sticky top-0 bg-[#1F2430] flex flex-col overflow-hidden">

        <button
          type="button"
          onClick={() => router.push('/home')}
          className="px-4 py-[18px] border-b border-white/10 hover:bg-white/5 transition w-full text-left shrink-0"
        >
          <div className="flex items-center gap-3">
            <div className="min-w-[36px] h-9 bg-white/10 rounded-lg flex items-center justify-center shrink-0">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                strokeWidth={2}
                stroke="white"
                className="w-5 h-5"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M2.25 12 11.204 3.045a1.125 1.125 0 0 1 1.592 0L21.75 12M4.5 9.75V19.5A2.25 2.25 0 0 0 6.75 21.75h10.5A2.25 2.25 0 0 0 19.5 19.5V9.75"
                />
              </svg>
            </div>
            {!collapsed && (
              <div className="overflow-hidden whitespace-nowrap min-w-0">
                <h1 className="text-white text-[14px] font-semibold truncate leading-tight">
                  POS
                </h1>
                <p className="text-[10px] text-slate-500 truncate tracking-wider uppercase">
                  Retail 
                </p>
              </div>
            )}
          </div>
        </button>

        <nav className="flex-1 overflow-y-auto overflow-x-hidden px-2 py-3 space-y-0.5">
          {!collapsed && (
            <p className="px-3 pb-2 pt-1 text-[9px] font-semibold uppercase tracking-widest text-slate-600">
              Modules
            </p>
          )}

          {nodes.map((node) => {
            const path = node.path?.toLowerCase();
            const isActive = path && pathname?.startsWith(path);
            const label = node.identifier || '?';
            const badge = colorFor(node.identifier || node.path || String(node.id));

            return (
              <button
                key={node.id || node.identifier}
                type="button"
                onClick={() => router.push(path)}
                title={collapsed ? label : undefined}
                className={`
                  relative w-full flex items-center gap-2.5 rounded-lg
                  text-[13px] font-medium transition-all duration-150
                  ${collapsed ? 'justify-center px-0 py-2.5' : 'px-3 py-2'}
                  ${isActive
                    ? 'bg-white/10 text-white'
                    : 'text-slate-400 hover:bg-white/5 hover:text-slate-200'}
                `}
              >
                {isActive && !collapsed && (
                  <span className="absolute left-0 top-2 bottom-2 w-[2px] rounded-full bg-white/60" />
                )}

                <span
                  className="flex items-center justify-center h-7 w-7 rounded-md shrink-0 text-[11px] font-semibold"
                  style={{ background: badge.bg, color: badge.text }}
                >
                  {label.trim().charAt(0).toUpperCase()}
                </span>

                {!collapsed && (
                  <span className="truncate">{label}</span>
                )}
              </button>
            );
          })}
        </nav>

        <div className="p-2 border-t border-white/10 shrink-0">
          <button
            type="button"
            onClick={logout}
            title={collapsed ? 'Logout' : undefined}
            className={`
              w-full flex items-center gap-2 rounded-lg
              text-slate-400 hover:bg-white/5 hover:text-slate-200
              text-[13px] font-medium transition-colors
              ${collapsed ? 'justify-center py-2.5' : 'px-3 py-2.5'}
            `}
          >
            <PowerIcon className="h-[17px] w-[17px] shrink-0" />
            {!collapsed && <span>Logout</span>}
          </button>
        </div>
      </aside>

      <div className="min-h-screen flex flex-col min-w-0">

        <header className="bg-white border-b border-slate-200 px-5 py-3 flex justify-between items-center shrink-0 h-[52px]">
          <div className="flex items-center gap-3">
            <button
              type="button"
              onClick={toggleCollapsed}
              title={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
              className="h-8 w-8 rounded-lg flex items-center justify-center text-slate-400 hover:bg-slate-100 hover:text-slate-600 transition shrink-0"
            >
              <Bars3Icon className="h-[18px] w-[18px]" />
            </button>

            <div>
              <h1 className="text-[15px] font-semibold text-slate-800 leading-tight">
                Point of Sale System
              </h1>
              <p className="text-[11px] text-slate-400">
                Manage products, customers &amp; billing
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={() => setShowProfileModal(true)}
            className="flex items-center gap-2.5 bg-slate-50 hover:bg-slate-100 pl-2 pr-3 py-1.5 rounded-xl border border-slate-200 transition"
          >
            <div className="h-7 w-7 rounded-full bg-[#1F2430] text-white flex items-center justify-center text-xs font-semibold shrink-0">
              {initials}
            </div>
            <div className="text-left hidden sm:block">
              <p className="text-[10px] text-slate-400 leading-none">Logged in as</p>
              <p className="text-[12px] font-semibold text-slate-700 leading-tight">
                {username || 'Guest'}
              </p>
            </div>
            <UserCircleIcon className="h-4 w-4 text-slate-400" />
          </button>
        </header>

        <main className="flex-1 min-w-0">
          {children}
        </main>
      </div>

      {showProfileModal && (
        <Profile closeModal={() => setShowProfileModal(false)} />
      )}
    </div>
  );
};

Layout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default Layout;
