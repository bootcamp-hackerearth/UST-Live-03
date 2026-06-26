'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';
import CommonUpdate from '@/components/UpadatePage';

const UpdateUser = ({
  data,
  closeModal,
  refreshData
}) => {

  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const [user, setUser] = useState({
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
      setMessage('Failed to load roles');
      setMessageType('error');

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

  const validate = () => {

    if (user.name.trim().length < 3) {
      setMessage('Name must be minimum 3 characters');
      setMessageType('error');
      return false;
    }

    const phoneRegex = /^[6-9]\d{9}$/;

    if (!phoneRegex.test(user.phoneNo)) {
      setMessage(
        'Phone number must start with 6, 7, 8, or 9 and contain exactly 10 digits'
      );
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

    try {

      await api.put(
        'user/update',
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
      setMessageType('success');

      refreshData?.();

      setTimeout(() => {
        closeModal?.();
      }, 1000);

    } catch (error) {

      console.error(error);

      setMessage(
        'Failed to update user'
      );
      setMessageType('error');

    }

  };

  return (

    <div className="h-[90vh] overflow-y-auto px-4 py-6">

      <div className="max-w-3xl mx-auto bg-white shadow-lg rounded-xl p-6 border border-slate-200">

        <h2 className="text-xl font-semibold text-slate-800 mb-4">
          Update User
        </h2>

        {message && (
          <div
            className={`mb-4 p-2 rounded text-sm border ${
              messageType === 'error'
                ? 'bg-red-100 text-red-700 border-red-300'
                : 'bg-green-100 text-green-700 border-green-300'
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
            data={user}
            handleChange={handleChange}
            showIdentifier={false}
            showName={true}
          />

          <div>
            <label 
            htmlFor="username"
            className="block mb-2 text-sm font-medium text-slate-700">
              Username
            </label>

            <input
              type="email"
              name="username"
              value={user.username}
              readOnly
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
            <label 
            htmlFor="phoneNo"
            className="block mb-2 text-sm font-medium text-slate-700">
              Phone Number
            </label>

            <input
              type="text"
              name="phoneNo"
              value={user.phoneNo}
              onChange={(e) => {

                let value = e.target.value.replaceAll(/\D/g, '');

                if (value.length > 0 && !/^[6-9]/.test(value)) {
                  return;
                }

                if (value.length > 10) {
                  value = value.slice(0, 10);
                }

                setUser((prev) => ({
                  ...prev,
                  phoneNo: value
                }));
              }}
              maxLength={10}
              inputMode="numeric"
              pattern="[6-9][0-9]{9}"
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
              Update User
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

UpdateUser.propTypes = {
  data: PropTypes.shape({
    id: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number
    ]),
    username: PropTypes.string,
    name: PropTypes.string,
    phoneNo: PropTypes.string,
    roles: PropTypes.arrayOf(PropTypes.string)
  }),
  closeModal: PropTypes.func,
  refreshData: PropTypes.func
};

export default UpdateUser;