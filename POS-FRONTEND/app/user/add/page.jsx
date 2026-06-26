'use client';
 
import PropTypes from 'prop-types';
import { useEffect, useState } from 'react';
import api from '@/app/services/api';
 
const AddUser = ({ closeModal, refreshData }) => {
 
  const [message, setMessage] = useState('');
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
      return false;
    }
 
    const emailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
    if (!emailRegex.test(user.username)) {
      setMessage('Enter valid Gmail address');
      return false;
    }
 
    const phoneRegex = /^\d{10}$/;
    if (!phoneRegex.test(user.phoneNo)) {
      setMessage('Phone number must be 10 digits');
      return false;
    }
 
    const passwordRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{8,}$/;
    if (!passwordRegex.test(user.password)) {
      setMessage('Password must contain uppercase, lowercase, number, and 8+ characters');
      return false;
    }
 
    if (user.roles.length === 0) {
      setMessage('Select at least one role');
      return false;
    }
 
    return true;
  };
 
  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
 
    if (!validate()) return;
 
    setLoading(true);
 
    try {
      const response = await api.post('/user/add', user);
      const data = response.data;
 
      setMessage(data.message);
 
      if (!data.success) return;
 
      refreshData?.();
      closeModal?.();
 
    } catch (err) {
      console.error(err);
      setMessage('Failed to add user');
    } finally {
      setLoading(false);
    }
  };
 
  return (
  <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
 
    <div className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg overflow-hidden flex flex-col">
 
      <div className="px-8 py-6 bg-slate-50 border-b border-slate-200">
        <h2 className="text-2xl font-bold text-slate-800">
          Add User
        </h2>
        <p className="text-xs text-slate-500 mt-1">
          Create system user & assign roles
        </p>
      </div>
 
      <form
        onSubmit={handleSubmit}
        className="flex-1 overflow-y-auto px-8 py-6 space-y-6"
        style={{ maxHeight: '65vh' }}  
      >
 
        {message && (
            <div
              className={`text-sm px-4 py-3 rounded-xl border ${message.toLowerCase().includes('failed')
                  ? 'bg-red-50 border-red-200 text-red-600'
                  : 'bg-cyan-50 border-cyan-200 text-cyan-700'
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
            className="h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500 "
          />
 
          <input
            name="phoneNo"
            value={user.phoneNo}
            onChange={(e) =>
              setUser({
                ...user,
                phoneNo: e.target.value.replaceAll(/\D/g, '')
              })
            }
            placeholder="Phone"
            maxLength={10}
              className="h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500 "
          />
 
        </div>
        
        <div className="grid grid-cols-2 gap-3">
        <input
          name="username"
          value={user.username}
          onChange={handleChange}
          placeholder="Email (gmail)"
            className="h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500 "
        />
 
        <input
          type="password"
          name="password"
          value={user.password}
          onChange={handleChange}
          placeholder="Password"
            className="h-12 px-4 rounded-xl border border-slate-300 bg-slate-50 text-sm focus:outline-none focus:ring-2 focus:ring-cyan-500 focus:border-cyan-500 "
        />
        </div>
 
        <div className="border border-slate-200 rounded-2xl p-5 bg-slate-50">
 
          <p className="text-base font-semibold text-slate-800 mb-4">
            Assign Roles
          </p>
 
          <div className="grid grid-cols-2 gap-2 max-h-28 overflow-y-auto pr-1">
 
            {roles.map((role) => (
              <label
                key={role.identifier}
                className="flex items-center gap-2 text-sm bg-white px-3 py-2 rounded-xl border border-slate-200 hover:border-cyan-400 transition"
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
 
      <div className="px-8 py-5 border-t border-slate-200 flex justify-end gap-3">
 
        <button
          type="button"
          onClick={closeModal}
          className="px-6 py-2.5 rounded-xl border hover:bg-slate-300 text-slate-700 font-medium hover:bg-slate-100 transition"
        >
          Cancel
        </button>
 
        <button
          type="submit"
          onClick={handleSubmit}
          disabled={loading}
          className="px-4 py-2 text-sm rounded-lg bg-cyan-500 text-white hover:bg-cyan-600 font-medium transition"
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