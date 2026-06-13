'use client';
 
import { useEffect, useState } from 'react';
 
import api from '@/app/services/api';
import CommonUpdate from '@/components/UpdatePage';
 
const UpdateUser = ({
  data,
  closeModal,
  refreshData
}) => {
 
  const [message, setMessage] = useState('');
 
  const [user, setUser] = useState({
    id: '',
    username: '',
    name: '',
    phoneNo: '',
    roles: []
  });
 
  const [roles, setRoles] = useState([]);
 
  useEffect(() => {
    fetchRoles();
  }, []);
 
  useEffect(() => {
 
    if (data) {
 
      setUser({
        id: data.id || '',
        username: data.username || '',
        name: data.name || '',
        phoneNo: data.phoneNo || '',
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
        response.data.dtoList || []
      );
 
    } catch (error) {
 
      console.error(error);
 
    }
 
  };
 
  const handleChange = (e) => {
 
    const { name, value } = e.target;
 
    setUser((prev) => ({
      ...prev,
      [name]: value
    }));
 
  };
 
  const handleCheckboxChange = (e) => {
 
    const { value, checked } = e.target;
 
    setUser((prev) => ({
 
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
        '/user/update',
        user,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json'
          }
        }
      );
 
      setMessage(
        'User updated successfully'
      );
 
      refreshData?.();
 
      setTimeout(() => {
        closeModal?.();
      }, 500);
 
    } catch (error) {
 
      console.error(error);
 
      setMessage(
        'Failed to update user'
      );
 
    }
 
  };
 
  return (
 
    <div className="h-[90vh] overflow-y-auto px-4 py-6">
 
      <div className="max-w-3xl mx-auto bg-white shadow-lg rounded-2xl border-t-4 border-cyan-500 overflow-hidden">
 
        <div className="px-8 py-6 bg-slate-50 border-b border-slate-200">
          <h2 className="text-2xl font-bold text-slate-800">
            Update User
          </h2>

          <p className="text-sm text-slate-500 mt-1">
            Modify user details and assigned roles.
          </p>
        </div>
 
        {message && (
          <div
            className={`mb-4 px-4 py-3 rounded-xl border text-sm ${message.toLowerCase().includes('failed')
                ? 'bg-red-50 border-red-200 text-red-600'
                : 'bg-cyan-50 border-cyan-200 text-cyan-700'
              }`}
          >
          </div>
        )}
 
        <form
          onSubmit={handleSubmit}
          className="p-8 space-y-6"
        >
 
          <div>
            <label className="block mb-2 text-sm font-medium text-slate-700">
              ID
            </label>
 
            <input
              type="text"
              value={user.id}
              readOnly
              className="
                w-full
                px-4
                py-3
                border
                border-slate-300
                rounded-xl
                bg-slate-100
                text-slate-600
                font-medium
                cursor-not-allowed
              "
            />
          </div>
 
          <CommonUpdate
            data={user}
            handleChange={handleChange}
            showIdentifier={false}
            showName={true}
          />
 
          <div>
            <label className="block mb-2 text-sm font-medium text-slate-700">
              Username
            </label>
 
            <input
              type="email"
              name="username"
              value={user.username}
              readOnly
              className="
              w-full
              px-4
              py-3
              border
              border-slate-300
              rounded-xl
              bg-slate-100
              text-slate-600
              font-medium
              cursor-not-allowed
              "
            />
          </div>
 
          <div>
            <label className="block mb-2 text-sm font-medium text-slate-700">
              Phone Number
            </label>
 
            <input
            type="text"
            name="phoneNo"
            value={user.phoneNo}
            onChange={(e) => {
            const value = e.target.value.replace(/\D/g, '').slice(0, 10);
            setUser((prev) => ({
            ...prev,
            phoneNo: value
          }));
        }}
              maxLength={10}
              className="
              w-full
              px-4
              py-3
              border
              border-slate-300
              rounded-xl
              bg-slate-50
              focus:outline-none
              focus:ring-2
              focus:ring-cyan-500
              focus:border-cyan-500
              "
            />
          </div>
 
          <div>
 
            <label className="block mb-3 text-sm font-medium text-slate-700">
              Assigned Roles
            </label>
 
            <div className="border border-slate-200 rounded-2xl p-5 max-h-64 overflow-y-auto bg-slate-50">
 
              {roles.map((role) => (
 
                <label
                  key={role.identifier}
                  className="
                    flex
                    items-center
                    gap-3
                    p-3
                    bg-white
                    border
                    mb-2
                    border-slate-200
                    rounded-xl
                    hover:border-cyan-400
                    cursor-pointer
                    transition
                  "
                >
 
                  <input
                    type="checkbox"
                    value={role.identifier}
                    checked={user.roles.includes(
                      role.identifier
                    )}
                    onChange={handleCheckboxChange}
                    className="h-4 w-4"
                  />
 
                  <span className="text-sm text-slate-700">
                    {role.identifier}
                  </span>
 
                </label>
 
              ))}
 
            </div>
 
          </div>
 
          <div className="flex gap-3 pt-4">
 
            <button
              type="submit"
              className="
                flex-1
                bg-cyan-500
                text-white
                py-3
                rounded-xl
                fonr-medium
                hover:bg-cyan-600
                transition
              "
            >
              Update User
            </button>
 
            <button
              type="button"
              onClick={closeModal}
              className="
                flex-1
                border-slate-300
                text-slate-700
                py-3
                rounded-xl
                font-medium
                hover:bg-slate-100
                transition
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
 
export default UpdateUser;