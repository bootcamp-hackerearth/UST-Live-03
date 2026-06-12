'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const AddUser = ({ closeModal, refreshData }) => {

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');
  const [loading, setLoading] = useState(false);

  const [roles, setRoles] = useState([]);

  const [user, setUser] = useState({
    name: '',
    username: '',
    password: '',
    phoneNo: '',
    roles: []
  });

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchRoles = async () => {
    try {
      const response = await api.post('/role/list', {
        page: 0,
        sizePerPage: 50,
        sortDirection: 'ASC',
        sortField: 'identifier'
      });

      setRoles(response.data.dtoList || []);
    } catch (err) {
      console.error(err);
      setMessage('Failed to load roles');
    }
  };

  const handleChange = (e) => {
    setUser({
      ...user,
      [e.target.name]: e.target.value
    });
  };

  const handleRoleChange = (e) => {
    const { value, checked } = e.target;

    if (checked) {
      setUser({
        ...user,
        roles: [...user.roles, value]
      });
    } else {
      setUser({
        ...user,
        roles: user.roles.filter(r => r !== value)
      });
    }
  };

  const validate = () => {

    if (user.name.trim().length < 3) {
      setMessage('Name must be minimum 3 characters');
      setMessageType('error');
      return false;
    }

    const emailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
    if (!emailRegex.test(user.username)) {
      setMessage('Enter valid Gmail address');
      setMessageType('error');
      return false;
    }

    const phoneRegex = /^[6-9]\d{9}$/;
    if (!phoneRegex.test(user.phoneNo)) {
      setMessage('Phone number must start with 6, 7, 8, or 9 and contain exactly 10 digits');
      setMessageType('error');
      return false;
    }

    const passwordRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{8,}$/;
    if (!passwordRegex.test(user.password)) {
      setMessage('Password must contain uppercase, lowercase, number, and 8+ characters');
      setMessageType('error');
      return false;
    }

    if (user.roles.length === 0) {
      setMessage('Select at least one role');
      setMessageType('error');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {

  e.preventDefault();

  setMessage('');
  setMessageType('');

  if (!validate()) return;

  setLoading(true);

  try {

    const response = await api.post('/user/add', user);

    const data = response.data;

    setMessage(data.message);

    if (data.success) {
      setMessageType('success');

      refreshData?.();

      setTimeout(() => {
        closeModal?.();
      }, 1000);

    } else {
      setMessageType('error');
    }

  } catch (err) {

    console.error(err);

    setMessage('Failed to add user');
    setMessageType('error');

  } finally {

    setLoading(false);
  }
};

  return (
  <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">

    <div className="bg-white w-full max-w-xl rounded-2xl shadow-2xl overflow-hidden flex flex-col">

      <div className="px-6 py-4 border-b border-slate-200 shrink-0">
        <h2 className="text-xl font-bold text-slate-800">
          Add User
        </h2>
        <p className="text-xs text-slate-500 mt-1">
          Create system user & assign roles
        </p>
      </div>

      <form
        onSubmit={handleSubmit}
        className="flex-1 overflow-y-auto px-6 py-5 space-y-4"
        style={{ maxHeight: '65vh' }}   
      >

        {message && (
          <div
            className={`text-sm px-3 py-2 rounded-lg border font-medium ${
            messageType === 'error'
            ? 'bg-red-100 border-red-300 text-red-700'
            : 'bg-green-100 border-green-300 text-green-700'
          }`}
          >
           {message}
          </div>
        )}

        <div className="grid grid-cols-2 gap-3">

          <input
            name="name"
            value={user.name}
            onChange={handleChange}
            placeholder="Full Name"
            className="h-11 px-3 rounded-lg border text-sm"
          />

          <input
           name="phoneNo"
           value={user.phoneNo}
           onChange={(e) => {
           let value = e.target.value.replaceAll(/\D/g, '');
           if (value.length > 0 && !/^[6-9]/.test(value)) {
           return;}

           if (value.length > 10) {
           value = value.slice(0, 10);
           }

           setUser({
           ...user,
           phoneNo: value
           });
           }}
           placeholder="Phone"
           maxLength={10}
           inputMode="numeric"
           pattern="[6-9][0-9]{9}"
           className="h-11 px-3 rounded-lg border text-sm"
          />

        </div>

        <input
          name="username"
          value={user.username}
          onChange={handleChange}
          placeholder="Email (gmail)"
          className="h-11 w-full px-3 rounded-lg border text-sm"
        />

        <input
          type="password"
          name="password"
          value={user.password}
          onChange={handleChange}
          placeholder="Password"
          className="h-11 w-full px-3 rounded-lg border text-sm"
        />

        <div className="border rounded-xl p-3 bg-slate-50">

          <p className="text-sm font-semibold text-slate-700 mb-2">
            Assign Roles
          </p>

          <div className="grid grid-cols-2 gap-2 max-h-28 overflow-y-auto pr-1">

            {roles.map((role) => (
              <label
                key={role.identifier}
                className="flex items-center gap-2 text-sm bg-white px-2 py-1 rounded-md border"
              >
                <input
                  type="checkbox"
                  value={role.identifier}
                  checked={user.roles.includes(role.identifier)}
                  onChange={handleRoleChange}
                />
                {role.identifier}
              </label>
            ))}

          </div>

        </div>

      </form>

      <div className="px-6 py-3 border-t flex justify-end gap-2 bg-white shrink-0">

        <button
          type="button"
          onClick={closeModal}
          className="px-4 py-2 text-sm rounded-lg border hover:bg-slate-100"
        >
          Cancel
        </button>

        <button
          type="submit"
          onClick={handleSubmit}
          disabled={loading}
          className="px-4 py-2 text-sm rounded-lg bg-indigo-600 text-white hover:bg-indigo-700"
        >
          {loading ? "Saving..." : "Save"}
        </button>

      </div>

    </div>
  </div>
);
};

AddUser.propTypes = {
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default AddUser;