"use client";
import { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import api from '../api/axios';
import ProtectedRoute from '@/component/ProtectedRoute';

const avatarGradients = [
  'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
  'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
];

const InfoField = ({ icon, label, value }) => (
  <div style={{ display: 'flex', flexDirection: 'column', gap: '3px' }}>
    <span style={{
      fontSize: '11px', fontWeight: '600', color: '#9ca3af',
      textTransform: 'uppercase', letterSpacing: '0.6px',
    }}>
      {icon}&nbsp; {label}
    </span>
    <span style={{
      fontSize: '14px', color: '#111827', fontWeight: '500',
      maxWidth: '220px', overflow: 'hidden',
      textOverflow: 'ellipsis', whiteSpace: 'nowrap',
    }}>
      {value || '—'}
    </span>
  </div>
);

const UserProfileCard = ({ user }) => {
  const initial = user.name?.charAt(0).toUpperCase() ?? '?';
  const gradient = avatarGradients[(user.name?.codePointAt(0) ?? 0) % avatarGradients.length];
  const roles = Array.isArray(user.roles) ? user.roles : [user.roles].filter(Boolean);
  const primaryRole = roles[0] ?? 'User';

  return (
    <div style={{
      background: '#fff', border: '1px solid #e5e7eb',
      borderRadius: '16px', overflow: 'hidden',
      boxShadow: '0 1px 4px rgba(0,0,0,0.06)', marginBottom: '24px',
    }}>
      <div style={{ height: '4px', background: 'linear-gradient(90deg, #6366f1, #8b5cf6, #a78bfa)' }} />
      <div style={{ padding: '24px 28px', display: 'flex', alignItems: 'center', gap: '24px', flexWrap: 'wrap' }}>
        <div style={{ position: 'relative', flexShrink: 0 }}>
          <div style={{
            width: '70px', height: '70px', borderRadius: '50%',
            background: gradient, display: 'flex', alignItems: 'center',
            justifyContent: 'center', fontSize: '28px', fontWeight: '700',
            color: '#fff', boxShadow: '0 4px 14px rgba(99,102,241,0.3)',
          }}>
            {initial}
          </div>
          <div style={{
            position: 'absolute', bottom: '3px', right: '3px',
            width: '13px', height: '13px', borderRadius: '50%',
            background: '#22c55e', border: '2px solid #fff',
          }} />
        </div>
        <div style={{ flex: 1, minWidth: '150px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' }}>
            <span style={{ fontSize: '18px', fontWeight: '700', color: '#111827' }}>
              {user.name}
            </span>
            <span style={{
              fontSize: '11px', fontWeight: '600', padding: '3px 10px',
              borderRadius: '20px', background: '#f3f4f6', color: '#374151',
              border: '1px solid #e5e7eb', textTransform: 'uppercase',
            }}>
              {primaryRole}
            </span>
          </div>
          <p style={{ fontSize: '13px', color: '#6b7280', marginTop: '4px' }}>Active account</p>
        </div>
        <div style={{ width: '1px', height: '52px', background: '#f3f4f6', flexShrink: 0 }} />
        <div style={{ display: 'flex', gap: '36px', flexWrap: 'wrap' }}>
          <InfoField icon="✉️" label="Email" value={user.username} />
          <InfoField icon="📱" label="Phone" value={user.phoneNo} />
          <InfoField icon="🛡️" label="Roles" value={roles.join(', ')} />
        </div>
      </div>
    </div>
  );
};
InfoField.propTypes = {
  icon: PropTypes.node.isRequired,
  label: PropTypes.string.isRequired,
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number
  ])
};

UserProfileCard.propTypes = {
  user: PropTypes.shape({
    name: PropTypes.string,
    username: PropTypes.string,
    phoneNo: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number
    ]),
    roles: PropTypes.oneOfType([
      PropTypes.arrayOf(PropTypes.string),
      PropTypes.string
    ])
  }).isRequired
};

export default function DashboardPage() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const fetchUser = async () => {
      if (globalThis.window !== undefined) {
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
          try {
            const response = await api.get('/api/user/getcurrentuser', {
              params: { username: storedUser },
            });
            setUser(response.data);
            localStorage.setItem("roles",JSON.stringify(response.data.roles || []));
            globalThis.dispatchEvent(new Event("refreshSideBar"));
          } catch (err) {
            console.error('Failed to fetch user:', err);
          }
        }
      }
    };
    fetchUser();
  }, []);

  return (
    <ProtectedRoute>
    <>
      {user && (
        <>
          <div className="bg-white border border-gray-200 rounded-xl shadow-sm p-5 mb-4">
            <h1 className="text-2xl font-bold text-gray-800">
              Welcome back, {user.name} 
            </h1>
            <p className="text-gray-500 mt-1 text-sm">
              Delighted to see you again! Explore the sidebar for efficient business management.
            </p>
          </div>
          <UserProfileCard user={user} />
        </>
      )}
    </>
  </ProtectedRoute>);
}