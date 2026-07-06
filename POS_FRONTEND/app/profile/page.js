"use client";

import React, { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation";

export default function Profile() {
  const router = useRouter();

  const [roles, setRoles] = useState([]);
  const [editMode, setEditMode] = useState(false);

  const [formData, setFormData] = useState({
    name: "",
    username: "",
    phoneNo: "",
    roles: [],
  });

  const [originalData, setOriginalData] = useState(null);

  const [token, setToken] = useState(null);
  const [loggedUsername, setLoggedUsername] = useState(null);

  useEffect(() => {
    const t = localStorage.getItem("token");
    const u = localStorage.getItem("username");

    if (!t || !u) {
      router.push("/login");
      return;
    }

    setToken(t);
    setLoggedUsername(u);
  }, []);

  useEffect(() => {
    if (token && loggedUsername) {
      fetchProfile();
    }
  }, [token, loggedUsername]);

  useEffect(() => {
    if (editMode) {
      fetchRoles();
    }
  }, [editMode]);

  const fetchProfile = async () => {
    try {
      const res = await axios.get(
        "http://localhost:8080/api/user/get",
        {
          params: { username: loggedUsername },
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      const data = res.data;

      const roleList =
        data.roles?.map((r) =>
          typeof r === "string" ? r : r.identifier
        ) || [];

      const normalized = { ...data, roles: roleList };

      setFormData(normalized);
      setOriginalData(normalized);
    } catch (err) {
      console.log(err);
    }
  };

  const fetchRoles = async () => {
    try {
      const res = await axios.post(
        "/api/role/list",
        {
          page: 0,
          sizePerPage: 50,
          sortDirection: "ASC",
          sortField: "identifier",
        }
      );

      setRoles(res.data?.dtoList || []);
    } catch (err) {
      console.log(err);
      setRoles([]);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleRoleChange = (e) => {
    const { value, checked } = e.target;

    setFormData((prev) => ({
      ...prev,
      roles: checked
        ? [...prev.roles, value]
        : prev.roles.filter((r) => r !== value),
    }));
  };

  const handleUpdate = async () => {
    try {
      await axios.post(
        "/api/user/update",
        formData,
        {
          params: { oldUsername: originalData.username },
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      alert("Profile updated ");
      router.push("/home"); 
    } catch (err) {
      console.log(err);
    }
  };

  const handleCancel = () => {
    setFormData(originalData);
    setEditMode(false);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100 px-6">

      <div className="w-full max-w-md bg-gradient-to-br from-[#020617] via-[#020c2f] to-[#0a1f66] border border-white/10 shadow-2xl rounded-2xl text-white">

        <div className="text-center p-6 border-b border-white/10">
          <h2 className="text-2xl font-bold">Profile</h2>
          <p className="text-blue-100 text-sm mt-1">
            View / Edit your details
          </p>
        </div>

        <div className="p-6 space-y-4">

          <input
            name="name"
            value={formData.name}
            onChange={handleChange}
            disabled={!editMode}
            placeholder="Full Name"
            className="w-full px-3 py-2 rounded-lg bg-white/10 border border-white/20 text-white"
          />

          <input
            name="username"
            value={formData.username}
            disabled
            className="w-full px-3 py-2 rounded-lg bg-white/5 border border-white/20 text-gray-300 cursor-not-allowed"
          />

          <input
            name="phoneNo"
            value={formData.phoneNo}
            onChange={handleChange}
            disabled={!editMode}
            placeholder="Phone Number"
            className="w-full px-3 py-2 rounded-lg bg-white/10 border border-white/20 text-white"
          />

          <div>
            <p className="text-xs text-blue-200 mb-2 uppercase">
              Roles
            </p>

            {editMode ? (
              <div className="flex flex-wrap gap-2">
                {roles.map((role) => {
                  const active = formData.roles.includes(role.identifier);

                  return (
                    <label
                      key={role.identifier}
                      className={`px-3 py-1.5 text-sm rounded-lg cursor-pointer border
                        ${
                          active
                            ? "bg-white text-blue-900 border-white"
                            : "bg-white/10 text-blue-100 border-white/20"
                        }`}
                    >
                      <input
                        type="checkbox"
                        value={role.identifier}
                        checked={active}
                        onChange={handleRoleChange}
                        className="hidden"
                      />
                      {role.identifier}
                    </label>
                  );
                })}
              </div>
            ) : (
              <div className="flex flex-wrap gap-2">
                {formData.roles.map((role) => (
                  <span
                    key={role}
                    className="px-3 py-1 text-sm rounded-lg bg-white text-blue-900"
                  >
                    {role}
                  </span>
                ))}
              </div>
            )}
          </div>

          <div className="flex flex-col gap-2 pt-3">

            {editMode ? (
              <>
                <button
                  onClick={handleUpdate}
                  className="w-full py-2.5 rounded-lg bg-white text-blue-900 font-semibold"
                >
                  Save
                </button>

                <button
                  onClick={handleCancel}
                  className="w-full border border-white/20 py-2.5 rounded-lg text-blue-100"
                >
                  Cancel
                </button>
              </>
            ) : (
              <button
                onClick={() => setEditMode(true)}
                className="w-full py-2.5 rounded-lg bg-white text-blue-900 font-semibold"
              >
                Edit Profile
              </button>
            )}

            <button
              onClick={() => {
                localStorage.clear();
                router.push("/login");
              }}
              className="w-full border border-white/20 py-2.5 rounded-lg text-red-300"
            >
              Logout
            </button>

          </div>

        </div>
      </div>
    </div>
  );
}
