"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "../api";

const Profile = () => {
  const router = useRouter();

  const [userDetails, setUserDetails] = useState({
    id: "",
    name: "",
    username: "",
    phoneNo: "",
    roles: [],
  });

  const [originalUsername, setOriginalUsername] = useState("");
  const [allRoles, setAllRoles] = useState([]);

  const [editMode, setEditMode] = useState(false);
  const [saving, setSaving] = useState(false);

  const handleLogout = () => {
    globalThis.localStorage.removeItem("token");
    globalThis.localStorage.removeItem("username");
    globalThis.location.replace("/login");
  };

  const goToDashboard = () => {
    router.push("/dashboard1");
  };

  useEffect(() => {
    const username = globalThis.localStorage.getItem("username");

    if (!username) {
      router.push("/login");
      return;
    }

    fetchProfile(username);
    fetchRoles();
  }, [router]);

  const fetchProfile = async (username) => {
    try {
      const res = await api.get("/user/get", {
        params: { identifier: username },
      });

      const data = res.data;

      const normalized = {
        id: data.id,
        name: data.name || "",
        username: data.username || "",
        phoneNo: data.phoneNo || "",
        roles: (data.roles || []).map(
          (r) => r.identifier || r
        ),
      };

      setUserDetails(normalized);
      setOriginalUsername(data.username);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchRoles = async () => {
    try {
      const res = await api.post("/role/list", {
        page: 0,
        sizePerPage: 100,
        sortField: "identifier",
        sortDirection: "ASC",
      });

      const findArray = (obj) => {
        if (!obj) return [];
        if (Array.isArray(obj)) return obj;

        if (typeof obj === "object") {
          for (const key in obj) {
            const result = findArray(obj[key]);
            if (result.length > 0) return result;
          }
        }
        return [];
      };

      const rolesArray = findArray(res.data);

      const formatted = rolesArray.map((r) => ({
        identifier: typeof r === "string" ? r : r.identifier,
      }));

      setAllRoles(formatted);
    } catch (err) {
      console.log(err);
      setAllRoles([]);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setUserDetails((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleRoleChange = (roleId) => {
    setUserDetails((prev) => ({
      ...prev,
      roles: prev.roles.includes(roleId)
        ? prev.roles.filter((r) => r !== roleId)
        : [...prev.roles, roleId],
    }));
  };

  const handleSave = async () => {
    try {
      setSaving(true);

      await api.post("/user/update", userDetails, {
        params: { oldUsername: originalUsername },
      });

      setEditMode(false);
      alert("Profile updated ");
    } catch (err) {
      console.error(err);
      alert("Update failed ");
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    fetchProfile(userDetails.username);
    setEditMode(false);
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center bg-linear-to-br from-gray-100 to-gray-200">

      <button
        onClick={goToDashboard}
        className="absolute top-6 left-6 bg-white shadow px-4 py-2 rounded-lg text-sm"
      >
        ← Back to Dashboard
      </button>

      <div className="bg-white shadow-2xl rounded-2xl p-8 w-full max-w-md">

        <div className="flex flex-col items-center mb-6">
          <div className="w-24 h-24 rounded-full bg-blue-600 text-white flex items-center justify-center text-3xl font-bold">
            {userDetails.name?.charAt(0) || "U"}
          </div>

          {editMode ? (
            <input
              name="name"
              value={userDetails.name}
              onChange={handleChange}
              className="mt-4 text-xl text-center border rounded p-1"
            />
          ) : (
            <h2 className="mt-4 text-xl font-semibold">
              {userDetails.name}
            </h2>
          )}

          <p className="text-gray-500 text-sm">
            {userDetails.username}
          </p>
        </div>

        <div className="border-t mb-4"></div>

        <div className="space-y-4 text-sm">

          <div className="flex justify-between">
            <span>User ID</span>
            <span>{userDetails.id || "-"}</span>
          </div>

          <div className="flex justify-between items-center">
            <span>Phone</span>
            {editMode ? (
              <input
                name="phoneNo"
                value={userDetails.phoneNo}
                onChange={handleChange}
                className="border rounded px-2"
              />
            ) : (
              <span>{userDetails.phoneNo || "-"}</span>
            )}
          </div>

          <div>
            <span>Roles</span>

            {editMode ? (
              <div className="flex flex-wrap gap-2 mt-2">
                {allRoles.map((role) => {
                  const roleId = role.identifier;
                  const active = userDetails.roles.includes(roleId);

                  return (
                    <button
                      type="button"
                      key={roleId}
                      onClick={() => handleRoleChange(roleId)}
                      className={`px-3 py-1 rounded-full text-xs ${
                        active
                          ? "bg-blue-600 text-white"
                          : "bg-gray-200"
                      }`}
                    >
                      {roleId}
                    </button>
                  );
                })}
              </div>
            ) : (
              <div className="flex flex-wrap gap-2 mt-2">
                {userDetails.roles.map((role) => (
                  <span
                    key={role}
                    className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full text-xs"
                  >
                    {role}
                  </span>
                ))}
              </div>
            )}
          </div>

        </div>

        <div className="mt-6 flex flex-col gap-3">

          {editMode ? (
            <>
              <button
                onClick={handleSave}
                disabled={saving}
                className="bg-blue-600 text-white py-2 rounded"
              >
                {saving ? "Saving..." : "Save"}
              </button>

              <button
                onClick={handleCancel}
                className="bg-gray-300 py-2 rounded"
              >
                Cancel
              </button>
            </>
          ) : (
            <button
              onClick={() => setEditMode(true)}
              className="bg-blue-600 text-white py-2 rounded"
            >
              Edit Profile
            </button>
          )}

          <button
            onClick={handleLogout}
            className="border py-2 rounded text-red-600"
          >
            Logout
          </button>

        </div>

      </div>
    </div>
  );
};

export default Profile;