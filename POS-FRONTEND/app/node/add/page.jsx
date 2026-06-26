'use client';

import PropTypes from 'prop-types';
import { useState, useEffect } from 'react';

import api from '@/app/services/api';

const AddNode = ({ closeModal, refreshData }) => {
  const [message, setMessage] = useState('');
  const [errors, setErrors] = useState({});
  const [validPaths, setValidPaths] = useState([]);
  const [loadingPaths, setLoadingPaths] = useState(true);

  const [node, setNode] = useState({
    identifier: '',
    path: '',
    roles: []
  });

  const [roles, setRoles] = useState([]);

  useEffect(() => {
    fetchRoles();
    fetchPaths();
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

  const fetchPaths = async () => {
    try {
      const response = await api.post('/node/list', {
        page: 0,
        sizePerPage: 1000
      });

      const data = response.data.dtoList || [];

      const paths = [...new Set(data.map((item) => item.path))];
      setValidPaths(paths);

    } catch (error) {
      console.error(error);
    } finally {
      setLoadingPaths(false);
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

  const normalizePath = (p) => p?.trim().replace(/\/$/, '');

  const validate = () => {
    const newErrors = {};

    if (!node.identifier.trim()) {
      newErrors.identifier = 'Identifier is required';
    }

    if (!node.path.trim()) {
      newErrors.path = 'Path is required';
    }
    else if (!node.path.startsWith('/')) {
      newErrors.path = 'Path must start with "/"';
    }
    else if (node.path.includes(' ')) {
      newErrors.path = 'Path must not contain spaces';
    }
    else if (
      validPaths
        .map(normalizePath)
        .includes(normalizePath(node.path))
    ) {
      newErrors.path = 'Path already exists for another node';
    }

    if (!node.roles || node.roles.length === 0) {
      newErrors.roles = 'At least one role must be selected';
    }

    setErrors(newErrors);

    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (loadingPaths) {
      setMessage('Loading system data, please wait...');
      return;
    }

    if (!validate()) return;

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
          <div className="rounded-xl px-4 py-3 text-sm bg-cyan-50 border border-cyan-200 text-cyan-600">
            {message}
          </div>
        )}

        <div className="grid md:grid-cols-2 gap-5">
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
              className="w-full px-4 py-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-cyan-500"
            />
            {errors.identifier && (
              <p className="text-red-500 text-sm mt-1">{errors.identifier}</p>
            )}
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
              className="w-full px-4 py-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-cyan-500"
            />
            {errors.path && (
              <p className="text-red-500 text-sm mt-1">{errors.path}</p>
            )}
          </div>
        </div>

        <div>
          <p className="block mb-3 text-sm font-medium text-slate-700">
            Assigned Roles
          </p>

          <div className="border border-slate-300 rounded-xl p-4 max-h-64 overflow-y-auto bg-slate-50">
            {roles.map((role) => (
              <label key={role.identifier} className="flex items-center gap-3 py-2 cursor-pointer">

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
          {errors.roles && (
            <p className="text-red-500 text-sm mt-2">{errors.roles}</p>
          )}
        </div>
      </form>

      <div className="px-8 py-5 border-t border-slate-200 flex justify-end gap-3">
        <button
          type="button"
          onClick={closeModal}
          className="px-6 py-2.5 rounded-xl border border-slate-300 text-slate-700 hover:bg-slate-100"
        >
          Cancel
        </button>

        <button
          type="submit"
          form="add-node-form"
          className="px-6 py-2.5 rounded-xl bg-cyan-500 text-white hover:bg-cyan-600"
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