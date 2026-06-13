'use client';
 
import { useState } from 'react';
import api from '@/app/services/api';
 
const AddRole = ({ closeModal, refreshData }) => {
  const [message, setMessage] = useState('');
 
  const [role, setRole] = useState({
    identifier: '',
    description: ''
  });
 
  const handleChange = (e) => {
    const { name, value } = e.target;
 
    setRole((prev) => ({
      ...prev,
      [name]: value
    }));
  };
 
  const handleSubmit = async (e) => {
    e.preventDefault();
 
    try {
      const response = await api.post('/role/add', role);
 
      const data = response.data;
 
      setMessage(data.message);
 
      if (!data.success) return;
 
      refreshData?.();
      closeModal?.();
    } catch (error) {
      console.error(error);
      setMessage('Failed to add role');
    }
  };
 
  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white rounded-2xl border-t-4 border-cyan-500 shadow-lg"
    >
 
      <div className="px-8 pt-8 pb-6 border-b border-slate-200 bg-slate-50">
        <h2 className="text-3xl font-bold text-slate-800">
          Add Role
        </h2>
 
        <p className="mt-2 text-slate-500">
          Create a new role for your system.
        </p>
      </div>
 
      <div className="p-8 space-y-6">
        {message && (
          <div
            className={`
              rounded-xl px-4 py-3 text-sm
              ${
                message.toLowerCase().includes('failed')
                  ? 'bg-red-50 border border-red-200 text-red-600'
                  : 'bg-cyan-50 border border-cyan-200 text-cyan-700'
              }
            `}
          >
            {message}
          </div>
        )}
 
        <div>
          <label className="block mb-2 text-sm font-semibold text-slate-700">
            Identifier
          </label>
 
          <input
            type="text"
            name="identifier"
            value={role.identifier}
            onChange={handleChange}
            placeholder="ROLE_ADMIN"
            required
            className="
              w-full
              h-12
              px-4
              bg-slate-50
              rounded-xl
              border border-slate-300
              focus:outline-none
              focus:ring-2
              focus:ring-cyan-500
              focus:border-cyan-500
              transition
            "
          />
        </div>
 
        <div>
          <label className="block mb-2 text-sm font-semibold text-slate-700">
            Description
          </label>
 
          <textarea
            name="description"
            value={role.description}
            onChange={handleChange}
            rows={3}
            placeholder="Enter role description"
            className="
              w-full
              px-4
              py-3
              rounded-xl
              bg-slate-50
              border border-slate-300
              resize-none
              focus:outline-none
              focus:ring-2
              focus:ring-cyan-500
              focus:border-cyan-500
              transition
            "
          />
        </div>
      </div>
 
      <div className="px-8 py-5 border-t border-slate-200 flex justify-end gap-3">
        <button
          type="button"
          onClick={closeModal}
          className="
            px-6 py-2.5
            rounded-xl
            border border-slate-300
            text-slate-700
            font-medium
            hover:bg-slate-100
            transition
          "
        >
          Cancel
        </button>
 
        <button
          type="submit"
          className="
            px-6 py-2.5
            rounded-xl
            bg-cyan-500
            text-white
            hover:bg-cyan-600
            transition
            shadow-md
          "
        >
          Save Role
        </button>
      </div>
    </form>
  );
};
 
export default AddRole;