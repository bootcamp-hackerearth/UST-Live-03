'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from "../../services/api";

const RegisterPage = () => {
  const router = useRouter();
  const [roles, setRoles] = useState([]);
  const [form, setForm] = useState({
    name: '',
    username: '',
    phoneNo: '',
    password: '',
    roles: [],
  });

  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    api
      .post('/role/list')
      .then((res) => setRoles(res.data))
      .catch((err) => console.error('Error fetching roles:', err));
  }, []);

  const validate = () => {
  const nextErrors = {};

  const nameRegex = /^[A-Za-z\s]+$/;
  const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
  const phoneRegex = /^[6-9]\d{9}$/;
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,}$/
  const hint = 'must be at least 6 characters and include letters and numbers';

    if (form.name.trim().length < 3) {
      nextErrors.name = 'Name must be at least 3 characters';
    } else if (!nameRegex.test(form.name.trim())) {
      nextErrors.name = 'Name should contain only letters';
    }

    if (!emailRegex.test(form.username.trim())) {
      nextErrors.username = 'Enter valid email address';
    }

    if (form.roles.length === 0) {
      nextErrors.roles = 'Select at least one role';
    }

    if (!phoneRegex.test(form.phoneNo.trim())) {
      nextErrors.phoneNo = 'Enter valid phone number';
    }

   if (!passwordRegex.test(form.password)) {
      nextErrors.password = hint;
     }

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const toggleRole = (role) => {
    setForm((current) => {
      const hasRole = current.roles.includes(role);
      return {
        ...current,
        roles: hasRole
          ? current.roles.filter((r) => r !== role)
          : [...current.roles, role],
      };
    });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    if (!validate()) return;
    setLoading(true);
    try {
      const response = await api.post(
        '/user/register',
        form,
        {
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );

      if (response?.data?.success === false) {
        setMessage(response?.data?.message);
        return;
      }
      setMessage('Registration successful');

      setTimeout(() => {
        router.push('/');
      }, 1200);

    } catch (error) {
      setMessage(
        error?.response?.data?.message || 'Registration failed'
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex justify-center items-center bg-gradient-to-r from-blue-400 to-indigo-600 p-4">
      <div className="w-full max-w-md bg-white p-8 rounded-2xl shadow-2xl">

        {message && (
          <div className="bg-blue-100 text-center p-2 mb-4 rounded">
            {message}
          </div>
        )}

        <h2 className="text-center text-blue-600 text-2xl font-semibold mb-4">
          User Registration
        </h2>

        <form onSubmit={handleSubmit}>
          <div className="mb-4">
            <label htmlFor="name" className="text-sm font-medium text-gray-700 block mb-1">
              Name
            </label>
            <input
              id="name"
              className="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              name="name"
              value={form.name}
              onChange={handleChange}
            />
            {errors.name && (
              <small className="text-red-500">{errors.name}</small>
            )}
          </div>
          <div className="mb-4">
            <label htmlFor="username" className="text-sm font-medium text-gray-700 block mb-1">
              Email
            </label>
            <input
              id="username"
              type="email"
              className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-400"
              name="username"
              value={form.username}
              onChange={handleChange}
            />
            {errors.username && (
              <small className="text-red-500">{errors.username}</small>
            )}
          </div>
          <div className="mb-4">
            <fieldset>
              <legend className="text-sm font-medium text-gray-700 block mb-1">
                Roles *
              </legend>
              <div className="border p-3 rounded-lg max-h-32 overflow-y-auto">
                {roles.map((role) => (
                  <div
                    key={role.identifier}
                    className="flex items-center gap-2 py-1"
                  >
                    <input
                      id={`role-${role.identifier}`}
                      type="checkbox"
                      checked={form.roles.includes(role.identifier)}
                      onChange={() => toggleRole(role.identifier)}
                    />

                    <label htmlFor={`role-${role.identifier}`}>
                      {role.identifier}
                    </label>
                  </div>
                ))}
              </div>
            </fieldset>

            {errors.roles && (
              <small className="text-red-500">
                {errors.roles}
              </small>
            )}
          </div>
          <div className="mb-4">
            <label htmlFor="phoneNo" className="text-sm font-medium text-gray-700 block mb-1">
              Phone Number *
            </label>
            <input
              id="phoneNo"
              className="w-full p-3 border rounded-lg"
              name="phoneNo"
              value={form.phoneNo}
              onChange={(e) => {
                const value = e.target.value
                  .split('')
                  .filter((char) => char >= '0' && char <= '9')
                  .join('');

                setForm((prev) => ({
                  ...prev,
                  phoneNo: value,
                }));
              }}
              maxLength={10}
            />
            {errors.phoneNo && (
              <small className="text-red-500">
                {errors.phoneNo}
              </small>
            )}
          </div>
          <div className="mb-4">
            <label htmlFor="password" className="text-sm font-medium text-gray-700 block mb-1">
              Password
            </label>
            <input
              id="password"
              type="password"
              className="w-full p-3 border rounded-lg"
              name="password"
              value={form.password}
              onChange={handleChange}
            />
            {errors.password && (
              <small className="text-red-500">
                {errors.password}
              </small>
            )}
          </div>
          <button
            disabled={loading}
            className="w-full bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 transition disabled:opacity-70"
          >
            {loading ? 'Registering...' : 'Register'}
          </button>
          <div className="text-center mt-4">
            <button
              type="button"
              onClick={() => router.push('/login')}
              className="px-4 py-2 border border-blue-500 rounded-full hover:bg-blue-50"
            >
              Back
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default RegisterPage;
