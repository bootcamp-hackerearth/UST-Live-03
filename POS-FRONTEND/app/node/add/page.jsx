'use client';

import { useState, useEffect } from 'react';
import api from '@/app/services/api';
import PropTypes from 'prop-types';


const AddNode = ({ closeModal, refreshData }) => {

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

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
      setMessageType('error');
      setMessage('Failed to load roles');
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

  const validate = () => {

    if (!node.identifier.trim()) {
      setMessageType('error');
      setMessage('Identifier is required');
      return false;
    }

    if (!node.path.trim()) {
      setMessageType('error');
      setMessage('Path is required');
      return false;
    }

    if (node.roles.length === 0) {
      setMessageType('error');
      setMessage('Please assign at least one role');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage('');
    setMessageType('');

    if (!validate()) return;

    try {
      const response = await api.post('/node/add', node);

      if (response.data.success) {

        setMessageType('success');
        setMessage(
          response.data.message || 'Node added successfully'
        );

        refreshData?.();
        closeModal?.();

      } else {

        setMessageType('error');
        setMessage(
          response.data.message || 'Failed to add node'
        );
      }

    } catch (error) {
      console.error(error);

      setMessageType('error');
      setMessage('Failed to add node');
    }
  };

  return (
    <div className="h-[90vh] w-full flex flex-col bg-white rounded-2xl shadow-md">

      <div className="flex-1 overflow-y-auto p-6 space-y-6">

        <div>
          <h2 className="text-2xl font-semibold text-slate-800">
            Add Node
          </h2>

          <p className="text-sm text-slate-500 mt-1">
            Create a new node and assign roles.
          </p>
        </div>

        {message && (
          <div
            className={`rounded-lg px-4 py-3 text-sm border ${
              messageType === 'error'
                ? 'border-red-200 bg-red-50 text-red-600'
                : 'border-green-200 bg-green-50 text-green-600'
            }`}
          >
            {message}
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          className="space-y-5 pb-24"
        >

          <div>
            <label 
            htmlFor="identifier"
            className="block mb-2 text-sm font-medium text-slate-700">
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
                rounded-lg
                border
                border-slate-300
                px-4
                py-3
                text-sm
                focus:outline-none
                focus:ring-2
                focus:ring-indigo-500
                focus:border-indigo-500
              "
            />
          </div>

          <div>
            <label 
            htmlFor="path"
            className="block mb-2 text-sm font-medium text-slate-700">
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
                rounded-lg
                border
                border-slate-300
                px-4
                py-3
                text-sm
                focus:outline-none
                focus:ring-2
                focus:ring-indigo-500
                focus:border-indigo-500
              "
            />
          </div>

          <div>
            <legend
            className="block mb-3 text-sm font-medium text-slate-700">
              Assigned Roles
            </legend>

            <div className="border rounded-lg p-4 max-h-64 overflow-y-auto">

              {roles.map((role) => (
                <label
                  key={role.identifier}
                  className="
                    flex
                    items-center
                    gap-3
                    py-2
                    cursor-pointer
                  "
                >
                  <input
                    type="checkbox"
                    checked={node.roles.includes(role.identifier)}
                    onChange={() =>
                      handleRoleChange(role.identifier)
                    }
                    className="h-4 w-4"
                  />

                  <span className="text-sm text-slate-700">
                    {role.identifier}
                  </span>
                </label>
              ))}

            </div>
          </div>

        </form>

      </div>

      <div className="sticky bottom-0 bg-white border-t px-6 py-4 flex flex-col sm:flex-row justify-end gap-3">

        <button
          type="button"
          onClick={() => closeModal?.()}
          className="
            w-full sm:w-auto
            px-5 py-2.5
            rounded-lg
            border border-slate-300
            text-slate-700
            hover:bg-slate-100
          "
        >
          Cancel
        </button>

        <button
          type="submit"
          onClick={handleSubmit}
          className="
            w-full sm:w-auto
            px-5 py-2.5
            rounded-lg
            bg-indigo-600
            text-white
            hover:bg-indigo-700
          "
        >
          Save Node
        </button>

      </div>

    </div>
  );
};

AddNode.propTypes = {
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default AddNode;