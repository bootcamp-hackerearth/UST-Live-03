'use client';

import { useEffect, useState } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

const UpdateRole = ({
  data,
  closeModal,
  refreshData
}) => {

  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);

  const [role, setRole] = useState({
    id: '',
    identifier: '',
    description: ''
  });

  useEffect(() => {
    if (data) {
      setRole({
        id: data.id || '',
        identifier: data.identifier || '',
        description: data.description || ''
      });
    }
  }, [data]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setRole(prev => ({
      ...prev,
      [name]: value
    }));

    if (message) {
      setMessage('');
      setIsError(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!role.description?.trim()) {
      setMessage('Description is required');
      setIsError(true);
      return;
    }

    try {
      const response = await api.post('/role/update', role);
      const result = response.data;

      setMessage(result.message || 'Role updated successfully');
      setIsError(!result.success);

      if (!result.success) return;

      refreshData?.();

      setTimeout(() => {
        closeModal?.();
      }, 500);

    } catch (error) {
      console.error(error);
      setMessage('Failed to update role');
      setIsError(true);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white rounded-2xl"
    >
      <div className="px-8 pt-8 pb-6 border-b border-slate-200">
        <h2 className="text-3xl font-bold text-slate-800">
          Update Role
        </h2>

        <p className="mt-2 text-slate-500">
          Modify role details.
        </p>
      </div>

      <div className="p-8 space-y-6">

        {message && (
          <div
            className={`
              rounded-xl px-4 py-3 text-sm border
              ${isError
                ? 'bg-red-50 border-red-200 text-red-600'
                : 'bg-green-50 border-green-200 text-green-600'
              }
            `}
          >
            {message}
          </div>
        )}

        <div>
          <label 
          htmlFor='identifier'
          className="block mb-2 text-sm font-semibold text-slate-700">
            Identifier
          </label>

          <input
            type="text"
            value={role.identifier}
            readOnly
            className="
              w-full
              h-12
              px-4
              rounded-xl
              border border-slate-300
              bg-slate-100
              text-slate-500
              cursor-not-allowed
            "
          />
        </div>

        <div>
          <label 
          htmlFor='description'
          className="block mb-2 text-sm font-semibold text-slate-700">
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
              border border-slate-300
              resize-none
              focus:outline-none
              focus:ring-2
              focus:ring-indigo-500
              focus:border-indigo-500
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
            bg-indigo-600
            text-white
            hover:bg-indigo-700
            transition
            shadow-md
          "
        >
          Update Role
        </button>

      </div>

    </form>
  );
};

UpdateRole.propTypes = {
  data: PropTypes.shape({
    id: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number
    ]),
    identifier: PropTypes.string,
    description: PropTypes.string
  }),
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default UpdateRole;