"use client";

import React, { useEffect, useState } from "react";

import POSLayout from "../components/PosLayout";
import commonApi from "../services/commonApi";
import axiosInstance from "../services/axiosInstance";

function ProfilePage() {
  const [user, setUser] = useState(null);
  const [formData, setFormData] = useState({});
  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const username = localStorage.getItem("username");
      const res = await commonApi.get("user", "identifier", username);

      setUser(res.data);
      setFormData(res.data);
    } catch (err) {
      console.log(err);
    }
  };

  const handleChange = (key, value) => {
    setFormData((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  const handleCancel = () => {
    setFormData(user);
    setEditMode(false);
  };

  const handleUpdate = async () => {
    try {
      setLoading(true);

      const payload = {
        ...formData,
        identifier: user.identifier,
        username: user.username,
        roles: user.roles,
      };

      const res = await axiosInstance.post("/user/update", payload);

      if (res.data?.success === false) {
        alert(res.data?.message || "Update failed");
        return;
      }

      alert(res.data?.message || "Profile updated successfully");

      setUser(res.data);
      setFormData(res.data);
      setEditMode(false);
    } catch (err) {
      console.log(err);
      alert(err.response?.data?.message || "Something went wrong");
    } finally {
      setLoading(false);
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
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold text-black">My Profile</h1>

          {editMode ? (
            <div className="flex gap-2">
              <button
                type="button"
                onClick={handleCancel}
                className="px-4 py-2 rounded-lg border border-gray-300 text-gray-700 text-sm font-medium hover:bg-gray-50"
              >
                Cancel
              </button>

              <button
                type="button"
                onClick={handleUpdate}
                disabled={loading}
                className="px-4 py-2 rounded-lg bg-red-600 text-white text-sm font-medium hover:bg-red-700 disabled:opacity-50"
              >
                {loading ? "Updating..." : "Update"}
              </button>
            </div>
          ) : (
            <button
              type="button"
              onClick={() => setEditMode(true)}
              className="px-4 py-2 rounded-lg bg-red-600 text-white text-sm font-medium hover:bg-red-700"
            >
              Edit
            </button>
          )}
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div>
            <p className="text-sm text-gray-500">Name</p>

            {editMode ? (
              <input
                type="text"
                value={formData.name || ""}
                onChange={(e) => handleChange("name", e.target.value)}
                className="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-red-200"
              />
            ) : (
              <p className="text-base font-semibold text-black">
                {user.name}
              </p>
            )}
          </div>

          <div>
            <p className="text-sm text-gray-500">Email</p>

            <p className="text-base font-semibold text-black">
              {user.username}
            </p>
          </div>

          <div>
            <p className="text-sm text-gray-500">Phone Number</p>

            {editMode ? (
              <input
                type="text"
                value={formData.phoneNo || ""}
                onChange={(e) => handleChange("phoneNo", e.target.value)}
                className="mt-1 w-full border border-gray-300 rounded-lg px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-red-200"
              />
            ) : (
              <p className="text-base font-semibold text-black">
                {user.phoneNo}
              </p>
            )}
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