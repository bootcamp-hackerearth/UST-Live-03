'use client';
 
import { useEffect, useState } from 'react';
 
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
 
      const response = await api.get(
        `/user/get?username=${username}`
      );
 
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
 
      <div className="p-10 text-center text-xl font-semibold text-cyan-600">
 
        Loading Profile...
 
      </div>
    );
  }
 
 
  if (error) {
 
    return (
 
      <div className="p-10 text-center text-red-600 text-xl">
 
        {error}
 
      </div>
    );
  }
 
  return (
 
    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50">
 
 
      <div className="w-full max-w-2xl bg-white rounded-2xl shadow-xl overflow-hidden border-t-4 border-cyan-500">
 
 
        <div className="bg-cyan-500 text-white p-6 flex justify-between items-center">
 
          <div>
 
            <h1 className="text-2xl font-semibold">
              User Profile
            </h1>
 
            <p className="text-slate-300 text-sm">
              Account Information
            </p>
 
          </div>
 
          <button
            onClick={closeModal}
            className="
              bg-red-500
              hover:bg-red-600
              px-3
              py-1
              rounded-lg
              text-white
            "
          >
            ✕
          </button>
 
        </div>
 
 
        <div className="p-6 space-y-5">
 
 
          <div className="flex justify-between items-center border-b border-slate-200 pb-3">
 
            <span className="text-slate-500">
              Full Name
            </span>
 
            <span className="font-medium text-slate-800">
              {user?.name}
            </span>
 
          </div>
 
 
          <div className="flex justify-between border-b pb-2">
 
            <span className="text-slate-500">
              Email
            </span>
 
            <span className="font-medium text-slate-800">
              {user?.username}
            </span>
 
          </div>
 
 
          <div className="flex justify-between border-b pb-2">
 
            <span className="text-slate-500">
              Phone
            </span>
 
            <span className="font-medium text-slate-800">
              {user?.phoneNo}
            </span>
 
          </div>
 
 
          <div>
 
            <p className="text-slate-500 mb-2">
              Roles
            </p>
 
            <div className="flex gap-2 flex-wrap">
 
              {user?.roles?.map((role, index) => (
 
                <span
                  key={index}
                  className="
                    bg-cyan-100
                    text-cyan-700
                    px-3
                    py-1
                    rounded-md
                    text-sm
                    font-medium
                  "
                >
 
                  {
                    typeof role === 'object'
                      ? role.identifier || role.name
                      : role
                  }
 
                </span>
 
              ))}
 
            </div>
 
          </div>
 
 
          <button
            onClick={closeModal}
            className="
              w-full
              bg-cyan-500
              text-white
              py-2
              rounded-md
              hover:bg-cyan-600
              transition
              font-medium
            "
          >
            Close
          </button>
 
        </div>
 
      </div>
 
    </div>
  );
};
 
export default Profile;