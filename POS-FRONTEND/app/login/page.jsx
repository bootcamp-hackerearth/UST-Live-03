"use client"; 

import { useState } from 'react';
import { useRouter } from 'next/navigation';

const Login = () => {

  const router = useRouter(); 

  const API =
    process.env.NEXT_PUBLIC_API_URL 

  const [credentials, setCredentials] = useState({
    username: '',
    password: ''
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {

    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value
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

    if (typeof role !== 'string') {
      return null;
    }

    return role
      .replace(/^ROLE_/i, '')
      .toLowerCase();

  };

  const normalizeRole = (roleValue) => {

    if (!roleValue) {
      return null;
    }

    if (Array.isArray(roleValue)) {

      for (const role of roleValue) {

        const normalized =
          resolveSingleRole(role);

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

      const res = await fetch(
        `${API}/authenticate`,
        {
          method: 'POST',

          headers: {
            'Content-Type': 'application/json'
          },

          body: JSON.stringify(credentials)
        }
      );

      const data = await res.json();

      const token = data?.token;

      const role = normalizeRole(
        data?.roles ||
        data?.role ||
        data?.user?.roles ||
        data?.user?.role
      );

      const loginFailed =
  !res.ok ||
  !token ||
  token === 'Error';

const roleExists = Boolean(role);

if (loginFailed) {

  setError(
    data?.message ||
    data?.error ||
    'Invalid username or password'
  );

} else if (roleExists) {

  localStorage.setItem(
    'token',
    token
  );

  localStorage.setItem(
    'username',
    credentials.username
  );

  localStorage.setItem(
    'userRole',
    role
  );

  router.push('/home');

} else {

  setError(
    'Login succeeded but no role returned from server.'
  );

}
    } catch (err) {

      console.error(err);

      setError(
        'Login failed. Please try again.'
      );

    } finally {

      setLoading(false);

    }
  };

  return (

    <div className="flex h-screen bg-slate-100 text-slate-800">

      <div className="flex-1 hidden md:flex flex-col justify-center px-20 bg-gradient-to-br from-slate-200 to-slate-50">

        <h1 className="text-6xl font-extrabold leading-tight mb-5 text-slate-800">
          POS MADE SIMPLE
        </h1>

        <p className="text-lg text-slate-600 max-w-lg">
          Modern retail application
        </p>

      </div>

      <div className="w-full md:w-[460px] flex items-center justify-center p-10">

        <div className="w-full bg-white p-9 rounded-xl shadow-2xl border-t-[6px] border-slate-800">

          <h2 className="text-center text-2xl font-bold mb-7">
            Login to your account
          </h2>

          {error && (

            <div className="mb-4 p-3 rounded-md bg-red-100 text-red-700 text-sm text-center border border-red-300">
              {error}
            </div>

          )}

          <form onSubmit={handleSubmit}>

            <input
              type="text"
              name="username"
              placeholder="Enter username"
              required
              value={credentials.username}
              onChange={handleChange}
              className="w-full p-3 mb-4 rounded-md border border-slate-300 text-sm bg-white placeholder:text-slate-400 focus:outline-none focus:border-slate-800 focus:ring-4 focus:ring-slate-300"
            />

            <input
              type="password"
              name="password"
              placeholder="Enter password"
              required
              value={credentials.password}
              onChange={handleChange}
              className="w-full p-3 mb-4 rounded-md border border-slate-300 text-sm bg-white placeholder:text-slate-400 focus:outline-none focus:border-slate-800 focus:ring-4 focus:ring-slate-300"
            />

            <button
              type="submit"
              disabled={loading}
              className="w-full p-3 rounded-md border-none text-sm font-semibold bg-slate-800 text-white cursor-pointer hover:bg-slate-950 hover:shadow-lg transition duration-200"
            >

              {loading
                ? 'Signing In...'
                : 'Sign In'}

            </button>

          </form>

          <div className="text-center mt-5 text-sm text-slate-500">

            Don&apos;t have an account?{' '}

            <button
              onClick={() =>
                router.push('/register')
              }
              className="text-slate-800 font-semibold hover:underline"
            >
              Register here
            </button>

          </div>

        </div>

      </div>

    </div>

  );
};

export default Login;