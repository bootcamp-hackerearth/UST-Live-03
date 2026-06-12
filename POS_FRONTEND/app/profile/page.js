"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import PropTypes from "prop-types";

export default function Profile() {
  const router = useRouter();

  const [user, setUser] = useState({
    id: "",
    name: "",
    username: "",
    phoneNo: "",
    roles: [],
  });

  const [originalUser, setOriginalUser] = useState(null);
  const [allRoles, setAllRoles] = useState([]);

  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [token, setToken] = useState(null);
  const [loggedUsername, setLoggedUsername] = useState(null);

  /* ✅ LOAD TOKEN */
  useEffect(() => {
    const t = localStorage.getItem("token");
    const u = localStorage.getItem("username");

    if (t && u) {
      setToken(t);
      setLoggedUsername(u);
    } else {
      router.push("/login");
    }
  }, []);

  /* ✅ LOAD DATA */
  useEffect(() => {
    if (token && loggedUsername) {
      fetchProfile();
      fetchRoles();
    }
  }, [token, loggedUsername]);

  /* ✅ FETCH PROFILE */
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

      const normalizedRoles =
        data.roles?.map((r) =>
          typeof r === "string" ? r : r?.identifier
        ).filter(Boolean) || [];

      const normalized = {
        ...data,
        roles: normalizedRoles,
      };

      setUser(normalized);
      setOriginalUser(normalized);
    } catch (err) {
      handleAuthError(err);
    }
  };

  /* ✅ FETCH ROLES */
  const fetchRoles = async () => {
    try {
      const res = await axios.get(
        "http://localhost:8080/api/role/findallactive",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      // ✅ normalize role response
      const roles =
        res.data?.map((r) => ({
          identifier: r.identifier || r.name || r,
        })) || [];

      setAllRoles(roles);
    } catch (err) {
      handleAuthError(err);
    }
  };

  const handleAuthError = (err) => {
    if (err.response?.status === 403) {
      alert("Session expired ❌");
      localStorage.clear();
      router.push("/login");
    } else {
      setError("Something went wrong");
    }
  };

  /* ✅ INPUT CHANGE */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser((prev) => ({
      ...prev,
      value,
    }));
  };

  /* ✅ ROLE CHANGE */
  const handleRoleChange = (e) => {
    const { value, checked } = e.target;

    setUser((prev) => ({
      ...prev,
      roles: checked
        ? [...prev.roles, value]
        : prev.roles.filter((r) => r !== value),
    }));
  };

  /* ✅ UPDATE */
  const handleUpdate = async () => {
    try {
      setLoading(true);

      await axios.post(
        "http://localhost:8080/api/user/update",
        user,
        {
          params: { oldUsername: originalUser.username },
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      alert("Profile updated ✅");
      localStorage.setItem("username", user.username);

      setEditMode(false);
      router.push("/home");
    } catch (err) {
      handleAuthError(err);
    } finally {
      setLoading(false);
    }
  };

  /* ✅ CANCEL */
  const handleCancel = () => {
    setUser(originalUser);
    setEditMode(false);
  };

  /* ✅ LOGOUT */
  const handleLogout = () => {
    localStorage.clear();
    router.push("/login");
  };

  return (
    <div className="p-6">
      <div className="max-w-xl mx-auto bg-white rounded-2xl shadow border">

        {/* HEADER */}
        <div className="p-6 border-b flex items-center gap-4">
          <div className="w-12 h-12 rounded-full bg-blue-700 text-white flex items-center justify-center text-lg font-bold">
            {user.name?.charAt(0)?.toUpperCase()}
          </div>

          <div>
            <h2 className="text-lg font-semibold">{user.name}</h2>
            <p className="text-gray-500 text-sm">{user.username}</p>
          </div>
        </div>

        {/* CONTENT */}
        <div className="p-6 space-y-4">

          {error && <p className="text-red-500">{error}</p>}

          {/* ✅ VERTICAL INPUTS */}
          <Input label="Name" name="name" value={user.name} onChange={handleChange} disabled={!editMode} />
          <Input label="Email" name="username" value={user.username} onChange={handleChange} disabled={!editMode} />
          <Input label="Phone" name="phoneNo" value={user.phoneNo} onChange={handleChange} disabled={!editMode} />

          {/* ✅ ROLES */}
          <div>
            <p className="text-sm font-medium mb-2">Roles</p>

            {editMode ? (
              <div className="flex flex-col gap-2">
                {allRoles.length === 0 && (
                  <p className="text-gray-400 text-sm">No roles available</p>
                )}

                {allRoles.map((role) => {
                  const identifier = role.identifier;
                  const isActive = user.roles.includes(identifier);

                  return (
                    <label
                      key={identifier}
                      className={`flex items-center gap-2 px-3 py-2 rounded border cursor-pointer
                        ${isActive ? "bg-blue-600 text-white" : "bg-gray-100"}`}
                    >
                      <input
                        type="checkbox"
                        value={identifier}
                        checked={isActive}
                        onChange={handleRoleChange}
                      />
                      {identifier}
                    </label>
                  );
                })}
              </div>
            ) : (
              <div className="flex flex-col gap-2">
                {user.roles.map((r) => (
                  <span key={r} className="bg-blue-50 px-3 py-1 rounded">
                    {r}
                  </span>
                ))}
              </div>
            )}
          </div>

          {/* ✅ ACTIONS */}
          <div className="flex gap-3 pt-4">
            {editMode ? (
              <>
                <button
                  onClick={handleUpdate}
                  disabled={loading}
                  className="bg-blue-700 text-white px-4 py-2 rounded"
                >
                  {loading ? "Saving..." : "Save"}
                </button>

                <button
                  onClick={handleCancel}
                  className="border px-4 py-2 rounded"
                >
                  Cancel
                </button>
              </>
            ) : (
              <button
                onClick={() => setEditMode(true)}
                className="bg-blue-700 text-white px-4 py-2 rounded"
              >
                Edit Profile
              </button>
            )}

            <button
              onClick={handleLogout}
              className="border px-4 py-2 rounded"
            >
              Logout
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

/* ✅ INPUT */
function Input({ label, name, value, onChange, disabled }) {
  return (
    <div>
      <label className="text-sm text-gray-600">{label}</label>

      <input
        name={name}
        value={value || ""}
        onChange={onChange}
        disabled={disabled}
        className="w-full mt-1 px-3 py-2 border rounded focus:ring-2 focus:ring-blue-600"
      />
    </div>
  );
}

Input.propTypes = {
  label: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  value: PropTypes.any,
  onChange: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
};