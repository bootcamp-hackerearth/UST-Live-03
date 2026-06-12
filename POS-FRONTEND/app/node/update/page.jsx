'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';
import CommonUpdate from '@/components/UpadatePage';

const UpdateNode = ({
  data,
  closeModal,
  refreshData
}) => {

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

  useEffect(() => {

    if (data) {

      setNode({
        id: data.id || '',
        identifier: data.identifier || '',
        path: data.path || '',
        roles: data.roles || []
      });

    }

  }, [data]);

  const fetchRoles = async () => {

    try {

      const response = await api.post(
        '/role/list',
        {
          page: 0,
          sizePerPage: 100
        }
      );

      setRoles(
        response.data.dtoList || response.data || []
      );

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

  const handleRoleChange = (e) => {

    const { value, checked } = e.target;

    setNode((prev) => ({

      ...prev,

      roles: checked
        ? [...prev.roles, value]
        : prev.roles.filter(
            (role) => role !== value
          )

    }));

  };

  const validate = () => {

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

    if (!validate()) {
      return;
    }

    try {

      await api.post(
        '/node/update',
        node,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        }
      );

      setMessageType('success');
      setMessage(
        'Node updated successfully'
      );

      refreshData?.();

      setTimeout(() => {
        closeModal?.();
      }, 500);

    } catch (error) {

      console.error(error);

      setMessageType('error');
      setMessage(
        'Failed to update node'
      );

    }
  };

  return (

    <div className="h-[90vh] overflow-y-auto px-4 py-6">

      <div className="max-w-3xl mx-auto bg-white shadow-lg rounded-xl p-6 border border-slate-200">

        <h2 className="text-xl font-semibold text-slate-800 mb-4">
          Update Node
        </h2>

        {message && (
          <div
            className={`mb-4 p-3 rounded-lg text-sm border ${
              messageType === 'error'
                ? 'bg-red-50 border-red-200 text-red-600'
                : 'bg-green-50 border-green-200 text-green-600'
            }`}
          >
            {message}
          </div>
        )}

        <form
          onSubmit={handleSubmit}
          className="space-y-4"
        >

          <CommonUpdate
            data={node}
            handleChange={handleChange}
            showIdentifier={true}
            identifierReadOnly={true}
            showName={false}
          />

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
                px-3
                py-2
                border
                rounded-lg
                text-sm
              "
            />
          </div>

          <div>

            <legend className="block mb-3 text-sm font-medium text-slate-700">
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
                    value={role.identifier}
                    checked={node.roles.includes(
                      role.identifier
                    )}
                    onChange={handleRoleChange}
                    className="h-4 w-4"
                  />

                  <span className="text-sm text-slate-700">
                    {role.identifier}
                  </span>

                </label>

              ))}

            </div>

          </div>

          <div className="flex gap-3 pt-2">

            <button
              type="submit"
              className="
                flex-1
                bg-indigo-600
                text-white
                py-2
                rounded-lg
                text-sm
              "
            >
              Update Node
            </button>

            <button
              type="button"
              onClick={closeModal}
              className="
                flex-1
                bg-slate-200
                py-2
                rounded-lg
                text-sm
              "
            >
              Cancel
            </button>

          </div>

        </form>

      </div>

    </div>

  );
};

UpdateNode.propTypes = {
  data: PropTypes.shape({
    id: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number
    ]),
    identifier: PropTypes.string,
    path: PropTypes.string,
    roles: PropTypes.arrayOf(PropTypes.string)
  }),
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default UpdateNode;