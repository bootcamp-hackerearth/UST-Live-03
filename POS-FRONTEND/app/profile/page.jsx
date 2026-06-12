"use client";

import React, { useEffect, useState } from "react";

import POSLayout from "../components/PosLayout";
import commonApi from "../services/commonApi";

function ProfilePage() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const username = localStorage.getItem("username");

      const res = await commonApi.get("user", "identifier", username);

      setUser(res.data);
    } catch (err) {
      console.log(err);
    }
  };

  if (!user) {
    return (
      <POSLayout>
        <div className="bg-white rounded-xl p-6 shadow-sm">
          Loading profile...
        </div>
      </POSLayout>
    );
  }

  return (
    <POSLayout>
      <div className="bg-white rounded-xl shadow-sm p-6 max-w-3xl">
        <h1 className="text-2xl font-bold text-black mb-6">
          My Profile
        </h1>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div>
            <p className="text-sm text-gray-500">Name</p>
            <p className="text-base font-semibold text-black">
              {user.name}
            </p>
          </div>

          <div>
            <p className="text-sm text-gray-500">Email</p>
            <p className="text-base font-semibold text-black">
              {user.username}
            </p>
          </div>

          <div>
            <p className="text-sm text-gray-500">Phone Number</p>
            <p className="text-base font-semibold text-black">
              {user.phoneNo}
            </p>
          </div>

          <div className="md:col-span-2">
            <p className="text-sm text-gray-500 mb-2">Roles</p>

            <div className="flex flex-wrap gap-2">
              {user.roles?.map((role) => (
                <span
                  key={typeof role === "object" ? role.identifier : role}
                  className="px-3 py-1 rounded-full bg-red-50 text-red-600 text-sm border border-red-100"
                >
                  {typeof role === "object" ? role.identifier : role}
                </span>
              ))}
            </div>
          </div>
        </div>
      </div>
    </POSLayout>
  );
}

export default ProfilePage;