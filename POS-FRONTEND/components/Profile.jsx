'use client';

import { useEffect, useState, useRef, useCallback } from 'react';
import {
  UserCircleIcon,
  EnvelopeIcon,
  PhoneIcon,
  ShieldCheckIcon,
  XMarkIcon,
} from '@heroicons/react/24/outline';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const BADGE_COLORS = [
  { bg: '#DBEAFE', text: '#1D4ED8' },
  { bg: '#DCFCE7', text: '#15803D' },
  { bg: '#F3E8FF', text: '#7E22CE' },
  { bg: '#FEF3C7', text: '#B45309' },
  { bg: '#FCE7F3', text: '#BE185D' },
  { bg: '#E0F2FE', text: '#0369A1' },
];

const colorFor = (key = '') => {
  let hash = 0;
  for (let i = 0; i < key.length; i += 1) {
    hash = Math.trunc(hash * 31 + (key.codePointAt(i) ?? 0));
  }
  return BADGE_COLORS[Math.abs(hash) % BADGE_COLORS.length];
};

const InfoRow = ({ icon: Icon, label, value }) => (
  <div className="flex items-center gap-4 px-5 py-4 border-b border-slate-100 last:border-b-0">
    <div className="h-10 w-10 rounded-xl bg-slate-100 flex items-center justify-center shrink-0">
      <Icon className="h-5 w-5 text-slate-500" />
    </div>
    <div className="w-20 shrink-0">
      <p className="text-[11px] font-semibold uppercase tracking-wider text-slate-400">
        {label}
      </p>
    </div>
    <div className="flex-1 min-w-0">
      <p className="text-sm font-medium text-slate-700 truncate">
        {value || '—'}
      </p>
    </div>
  </div>
);

InfoRow.propTypes = {
  icon: PropTypes.elementType.isRequired,
  label: PropTypes.string.isRequired,
  value: PropTypes.string,
};

const Profile = ({ closeModal }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const dialogRef = useRef(null);

  const fetchProfile = useCallback(async () => {
    try {
      const username = localStorage.getItem('username');
      const response = await api.get(`/user/get?username=${username}`);
      setUser(response.data);
    } catch (err) {
      console.error(err);
      setError('Failed to load profile');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  useEffect(() => {
    const dialog = dialogRef.current;
    if (dialog && !dialog.open) {
      dialog.showModal();
    }

    const handleCancel = (e) => {
      e.preventDefault();
      closeModal();
    };

    dialog?.addEventListener('cancel', handleCancel);
    return () => dialog?.removeEventListener('cancel', handleCancel);
  }, [closeModal]);


  return (
    <dialog
      ref={dialogRef}
      className="fixed inset-0 z-50 p-0 bg-transparent backdrop:bg-slate-900/55 backdrop:backdrop-blur-sm w-full max-w-[430px] rounded-3xl overflow-visible outline-none"
      style={{ fontFamily: "'Inter', sans-serif" }}
      aria-labelledby="profile-dialog-title"
    >
      
      <button
        type="button"
        aria-label="Close profile dialog"
        onClick={closeModal}
        className="fixed inset-0 w-full h-full cursor-default bg-transparent border-0 p-0 m-0"
        style={{ zIndex: -1 }}
      />

      <div
        className="relative w-full bg-slate-50 rounded-3xl overflow-hidden"
        style={{ boxShadow: '0 25px 60px rgba(0,0,0,0.25)' }}
      >

        <div
          className="relative px-6 pt-6 pb-14"
          style={{
            background: 'linear-gradient(135deg, #111827 0%, #1F2937 100%)',
          }}
        >
          <button
            type="button"
            onClick={closeModal}
            className="absolute top-4 right-4 h-9 w-9 rounded-full bg-white/10 hover:bg-white/20 flex items-center justify-center transition-all"
            aria-label="Close profile"
          >
            <XMarkIcon className="h-5 w-5 text-white" />
          </button>

          <div className="flex items-center gap-4">
            <div
              className="relative h-16 w-16 rounded-2xl flex items-center justify-center text-2xl font-bold text-white shadow-lg"
              style={{ background: 'linear-gradient(135deg, #4F46E5, #6366F1)' }}
              aria-hidden="true"
            >
              {user?.name?.charAt(0)?.toUpperCase() ?? '?'}
              <span className="absolute bottom-0 right-0 h-4 w-4 rounded-full bg-emerald-400 border-[3px] border-[#1F2937]" />
            </div>

            <div>
              {loading ? (
                <div className="h-5 w-32 rounded bg-white/10 animate-pulse" aria-hidden="true" />
              ) : (
                <h2 id="profile-dialog-title" className="text-xl font-bold text-white">
                  {user?.name ?? 'Unknown User'}
                </h2>
              )}
              <p className="text-sm text-slate-400 mt-1">User Profile</p>
            </div>
          </div>
        </div>

        <div className="relative px-5 pb-5 -mt-8">
          <div className="bg-white rounded-2xl shadow-lg border border-slate-100 overflow-hidden">
            {loading && (
              <div className="p-5 space-y-3" aria-busy="true" aria-label="Loading profile">
                {[1, 2, 3, 4].map((item) => (
                  <div
                    key={item}
                    className="h-12 bg-slate-100 rounded-xl animate-pulse"
                    aria-hidden="true"
                  />
                ))}
              </div>
            )}

            {!loading && error && (
              <div className="p-8 text-center" role="alert">
                <XMarkIcon className="h-8 w-8 text-red-400 mx-auto mb-3" aria-hidden="true" />
                <p className="text-sm text-slate-500">{error}</p>
                <button
                  type="button"
                  onClick={fetchProfile}
                  className="mt-3 text-indigo-600 text-sm font-medium hover:underline"
                >
                  Retry
                </button>
              </div>
            )}

            {!loading && !error && user && (
              <>
                <InfoRow icon={UserCircleIcon} label="Name" value={user.name} />
                <InfoRow icon={EnvelopeIcon} label="Email" value={user.username} />
                <InfoRow icon={PhoneIcon} label="Phone" value={user.phoneNo} />

                {user.roles?.length > 0 && (
                  <div className="flex items-start gap-4 px-5 py-4">
                    <div
                      className="h-10 w-10 rounded-xl bg-slate-100 flex items-center justify-center shrink-0"
                      aria-hidden="true"
                    >
                      <ShieldCheckIcon className="h-5 w-5 text-slate-500" />
                    </div>
                    <div className="w-20 shrink-0">
                      <p className="text-[11px] font-semibold uppercase tracking-wider text-slate-400">
                        Roles
                      </p>
                    </div>

                    <ul className="flex flex-wrap gap-2 list-none p-0 m-0" aria-label="User roles">
                      {user.roles.map((role) => {
                        const key =
                          typeof role === 'object'
                            ? role.identifier || role.name
                            : role;
                        const badge = colorFor(key ?? '');
                        return (
                          <li
                            key={key}
                            className="px-3 py-1 rounded-full text-xs font-semibold"
                            style={{ backgroundColor: badge.bg, color: badge.text }}
                          >
                            {key}
                          </li>
                        );
                      })}
                    </ul>
                  </div>
                )}
              </>
            )}
          </div>

          <div className="mt-5 flex justify-end">
            <button
              type="button"
              onClick={closeModal}
              className="px-6 py-2.5 rounded-xl text-sm font-semibold text-white transition-all hover:scale-105"
              style={{ background: 'linear-gradient(135deg, #111827, #374151)' }}
            >
              Close
            </button>
          </div>
        </div>
      </div>
    </dialog>
  );
};

Profile.propTypes = {
  closeModal: PropTypes.func.isRequired,
};

export default Profile;