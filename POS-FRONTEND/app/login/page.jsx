'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from "next/link"; 
import api from "../../services/api";

const LoginPage = () => {
  const router = useRouter();

  const [credentials, setCredentials] = useState({
    username: '',
    password: '',
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value,
    });
  };

  const resolveSingleRole = (role) => {
    if (!role) return null;

    if (typeof role === 'object' && role !== null) {
      role =
        role.role ||
        role.authority ||
        role.name ||
        role.value ||
        role;
    }

    if (typeof role === 'string') {
      return role.replace(/^ROLE_/i, '').toLowerCase();
    }

    return null;
  };

  const normalizeRole = (roleValue) => {
    if (!roleValue) return null;
    if (Array.isArray(roleValue)) {
      for (const role of roleValue) {
        const normalized = resolveSingleRole(role);
        if (normalized) {
          return normalized;
        }
      }
      return null;
    }

    return resolveSingleRole(roleValue);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const res = await api.post('/authenticate', credentials, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      const data = res.data;
      const token = data?.token;
      const role = normalizeRole(
        data?.roles ||
        data?.role ||
        data?.user?.roles ||
        data?.user?.role
      );

      const isValidToken = token && token !== 'Error';

      if (isValidToken) {
        if (role) {
          localStorage.setItem('token', token);
          localStorage.setItem('username', credentials.username);
          localStorage.setItem('name', data?.user?.name || '');
          localStorage.setItem('phone', data?.user?.phone || '');
          localStorage.setItem('userRole', role);
          router.push('/home');
        } else {
          setError('Login succeeded but no role returned');
        }
      } else {
        setError(
          data?.message ||
          data?.error ||
          'Invalid username or password'
        );
      }
    } catch (error) {
      setError(
        error?.response?.data?.message ||
        'Login failed. Please try again.'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex justify-center items-center bg-gray-100">
      <div className="w-full max-w-md bg-white p-8 rounded-2xl shadow-2xl border-t-4 border-blue-600">
        <h2 className="text-center text-blue-700 text-2xl font-bold mb-5">
          Login
        </h2>

        {error && (
          <div className="bg-red-100 text-red-700 border border-red-200 p-2 rounded text-center text-sm mb-4 font-semibold">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label
              htmlFor="username"
              className="text-sm font-semibold text-blue-900 block mb-1"
            >
              Username
            </label>

            <input
              id="username"
              type="text"
              name="username"
              placeholder="Enter username"
              value={credentials.username}
              onChange={handleChange}
              required
              className="w-full p-3 border border-blue-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          <div className="mb-4">
            <label
              htmlFor="password"
              className="text-sm font-semibold text-blue-900 block mb-1"
            >
              Password
            </label>

            <input
              id="password"
              type="password"
              name="password"
              placeholder="Enter password"
              value={credentials.password}
              onChange={handleChange}
              required
              className="w-full p-3 border border-blue-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
          </div>

          <div className="flex gap-3 mt-6">
            <button
              type="submit"
              disabled={loading}
              className="flex-1 py-2 rounded-lg text-white font-semibold bg-gradient-to-r from-blue-600 to-blue-700 hover:from-blue-700 hover:to-blue-800 transition disabled:opacity-70"
            >
              {loading ? 'Logging in...' : 'Login'}
            </button>

            <Link
              href="/register"
              className="flex-1 py-2 rounded-lg text-center font-semibold border border-blue-200 bg-blue-50 text-blue-700 hover:bg-blue-100 transition"
            >
              Register
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;