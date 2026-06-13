'use client';
 
import { useEffect, useState } from 'react';
 
import api from '@/app/services/api';
import CommonUpdate from '@/components/UpdatePage';
 
const UpdateNode = ({
  data,
  closeModal,
  refreshData
}) => {
 
  const [message, setMessage] = useState('');
 
  const [node, setNode] = useState({
    id: '',
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
 
  const handleSubmit = async (e) => {
 
    e.preventDefault();
 
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
 
      setMessage(
        'Node updated successfully'
      );
 
      refreshData?.();
 
      setTimeout(() => {
        closeModal?.();
      }, 500);
 
    } catch (error) {
 
      console.error(error);
 
      setMessage(
        'Failed to update node'
      );
 
    }
  };
 
  return (
    <div className="flex flex-col bg-white rounded-2xl overflow-hidden max-h-[85vh] border-t-4 border-cyan-500 shadow-lg">

      <div className="px-8 pt-6 pb-5 border-b border-slate-200 flex-shrink-0">
        <h2 className="text-2xl font-bold text-slate-800">
          Update Node
        </h2>

        <p className="text-sm text-slate-500 mt-1">
          Modify node details and assigned roles.
        </p>
      </div>

      <form
        id="update-node-form"
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

        <div>
          <label className="block mb-2 text-sm font-medium text-slate-700">
            Node ID
          </label>

          <input
            type="text"
            value={node.id}
            readOnly
            className="
            w-full
            px-4
            py-3
            bg-slate-100
            border
            border-slate-300
            rounded-xl
            text-slate-600
          "
          />
        </div>

        <CommonUpdate
          data={node}
          handleChange={handleChange}
          showIdentifier={true}
          identifierReadOnly={true}
          showName={false}
        />

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
                  value={role.identifier}
                  checked={node.roles.includes(role.identifier)}
                  onChange={handleRoleChange}
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
          form="update-node-form"
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
          Update Node
        </button>

      </div>

    </div>
  );
};
 
export default UpdateNode;