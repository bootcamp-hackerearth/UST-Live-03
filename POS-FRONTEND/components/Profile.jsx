'use client';

import { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import api from '@/app/services/api';

const Profile = ({ closeModal }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const username = localStorage.getItem('username');

      console.log("Username:", username);

      const response = await api.get(
        `/user/get?identifier=${username}`
      );

      console.log("Profile Response:", response.data);

      setUser(response.data);
    } catch (err) {
      console.error(err);
      setError('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="w-full max-w-2xl bg-white rounded-2xl shadow-2xl p-10 text-center">
        <p className="text-xl font-semibold text-cyan-600">
          Loading Profile...
        </p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="w-full max-w-2xl bg-white rounded-2xl shadow-2xl p-10 text-center">
        <p className="text-xl text-red-600">{error}</p>
      </div>
    );
  }

  return (
    <div className="w-full max-w-2xl bg-white rounded-2xl shadow-2xl overflow-hidden animate-scale-in">

      {/* Header */}
      <div className="bg-cyan-500 text-white px-8 py-6 flex justify-between items-center">
        <div>
          <h2 className="text-3xl font-bold">User Profile</h2>
          <p className="text-cyan-100 mt-1">
            Account Information
          </p>
        </div>

        <button
          onClick={closeModal}
          className="w-10 h-10 rounded-lg bg-red-500 hover:bg-red-600 text-white text-2xl transition"
        >
          ×
        </button>
      </div>

      {/* Body */}
      <div className="p-8 space-y-6">

        <div className="border-b pb-4">
          <p className="text-sm text-gray-500">Full Name</p>
          <p className="text-lg font-semibold text-gray-800">
            {user?.name || "-"}
          </p>
        </div>

        <div className="border-b pb-4">
          <p className="text-sm text-gray-500">Email</p>
          <p className="text-lg font-semibold text-gray-800">
            {user?.username || "-"}
          </p>
        </div>

        <div className="border-b pb-4">
          <p className="text-sm text-gray-500">Phone</p>
          <p className="text-lg font-semibold text-gray-800">
            {user?.phoneNo || "-"}
          </p>
        </div>

        <div>
          <p className="text-sm text-gray-500 mb-3">Roles</p>

          <div className="flex flex-wrap gap-2">
            {user?.roles?.length ? (
              user.roles.map((role) => (
                <span
                  key={role}
                  className="bg-cyan-100 text-cyan-700 px-4 py-1 rounded-full font-medium"
                >
                  {role}
                </span>
              ))
            ) : (
              <span className="text-gray-500">No Roles</span>
            )}
          </div>
        </div>

        <button
          onClick={closeModal}
          className="w-full bg-cyan-500 hover:bg-cyan-600 text-white py-3 rounded-xl font-semibold transition"
        >
          Close
        </button>

      </div>
    </div>
  );
};

Profile.propTypes = {
  closeModal: PropTypes.func.isRequired
};

export default Profile;