'use client';
 
import { useState, useEffect } from 'react';
import api from '@/app/services/api';
 
const AddNode = ({ closeModal, refreshData }) => {
 
  const [message, setMessage] = useState('');
 
  const [node, setNode] = useState({
    identifier: '',
    path: '',
    roles: []
  });
 
  const [roles, setRoles] = useState([]);
 
  useEffect(() => {
    fetchRoles();
  }, []);
 
  const fetchRoles = async () => {
    try {
      const response = await api.post('/role/list', {
        page: 0,
        sizePerPage: 100
      });
 
      setRoles(response.data.dtoList || []);
    } catch (error) {
      console.error(error);
    }
  };
 
  const handleChange = (e) => {
    const { name, value } = e.target;
 
    setNode((prev) => ({
      ...prev,
      [name]: value
    }));
  };
 
  const handleRoleChange = (roleId) => {
    setNode((prev) => ({
      ...prev,
      roles: prev.roles.includes(roleId)
        ? prev.roles.filter((r) => r !== roleId)
        : [...prev.roles, roleId]
    }));
  };
 
  const handleSubmit = async (e) => {
    e.preventDefault();
 
    try {
      const response = await api.post('/node/add', node);
 
      setMessage(response.data.message);
 
      if (!response.data.success) return;
 
      refreshData?.();
      closeModal?.();
 
    } catch (error) {
      console.error(error);
      setMessage('Failed to add node');
    }
  };
 
  return (
    <div className="flex flex-col bg-white rounded-2xl overflow-hidden max-h-[85vh] border-t-4 border-cyan-500 shadow-lg">

      <div className="px-8 pt-6 pb-5 border-b border-slate-200 flex-shrink-0">
        <h2 className="text-2xl font-bold text-slate-800">
          Add Node
        </h2>

        <p className="text-sm text-slate-500 mt-1">
          Create a new node and assign roles.
        </p>
      </div>

      <form
        id="add-node-form"
        onSubmit={handleSubmit}
        className="flex-1 overflow-y-auto px-8 py-6 space-y-8"
      >

        {message && (
          <div
            className={`
            rounded-xl px-4 py-3 text-sm
            ${message.toLowerCase().includes('failed')
                ? 'bg-red-50 border border-red-200 text-red-600'
                : 'bg-cyan-50 border border-cyan-200 text-cyan-600'
              }
          `}
          >
            {message}
          </div>
        )}

        <div className="grid md:grid-cols-2 gap-5">

          <div>
            <label className="block mb-2 text-sm font-medium text-slate-700">
              Identifier
            </label>

            <input
              type="text"
              name="identifier"
              value={node.identifier}
              onChange={handleChange}
              placeholder="Enter identifier"
              className="
              w-full
              px-4
              py-3
              border
              border-slate-300
              rounded-xl
              focus:outline-none
              focus:ring-2
              focus:ring-cyan-500
            "
            />
          </div>

          <div>
            <label className="block mb-2 text-sm font-medium text-slate-700">
              Path
            </label>

            <input
              type="text"
              name="path"
              value={node.path}
              onChange={handleChange}
              placeholder="/admin/dashboard"
              className="
              w-full
              px-4
              py-3
              border
              border-slate-300
              rounded-xl
              focus:outline-none
              focus:ring-2
              focus:ring-cyan-500
            "
            />
          </div>

        </div>

        <div>
          <label className="block mb-3 text-sm font-medium text-slate-700">
            Assigned Roles
          </label>

          <div className="border border-slate-300 rounded-xl p-4 max-h-64 overflow-y-auto bg-slate-50">

            {roles.map((role) => (
              <label
                key={role.identifier}
                className="flex items-center gap-3 py-2 cursor-pointer"
              >
                <input
                  type="checkbox"
                  checked={node.roles.includes(role.identifier)}
                  onChange={() => handleRoleChange(role.identifier)}
                  className="h-4 w-4 accent-cyan-500"
                />

                <span className="text-sm text-slate-700">
                  {role.identifier}
                </span>
              </label>
            ))}

          </div>
        </div>

      </form>

      <div className="px-8 py-5 border-t border-slate-200 flex justify-end gap-3 flex-shrink-0">

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
          form="add-node-form"
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
          Save Node
        </button>

      </div>

    </div>
  );
};
 
export default AddNode;