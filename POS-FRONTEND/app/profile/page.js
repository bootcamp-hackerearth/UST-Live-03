"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import Layout from "@/app/components/Layout";

export default function Profile() {
  const router = useRouter();

  const [user, setUser] = useState({
    id: "",
    name: "",
    username: "",
    phoneNo: "",
    roles: [],
  });

  const [allRoles, setAllRoles] = useState([]);
  const [originalUsername, setOriginalUsername] = useState("");
  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [token, setToken] = useState(null);
  const [username, setUsername] = useState(null);

  useEffect(() => {
    const t = localStorage.getItem("token");
    const u = localStorage.getItem("username");

    if (!t || !u) {
      router.push("/");
    } else {
      setToken(t);
      setUsername(u);
    }
  }, []);

  useEffect(() => {
    if (token && username) {
      fetchProfile();
      fetchRoles();
    }
  }, [token, username]);

  const fetchProfile = async () => {
    try {
      const res = await axios.get(
        "http://localhost:8080/api/user/get",
        {
          params: { username },
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      const data = res.data;

      setUser({
        ...data,
        roles:
          data.roles?.map((r) =>
            typeof r === "string" ? r : r.identifier
          ) || [],
      });

      setOriginalUsername(data.username);
      setLoading(false);

    } catch (err) {
      console.error(err);
      setError("Failed to load profile");
    }
  };

  const fetchRoles = async () => {
    try {
      const res = await axios.post(
        "http://localhost:8080/api/role/list",
        {
          page: 0,
          sizePerPage: 100,
          sortField: "identifier",
          sortDirection: "ASC",
        },
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

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

      const formattedRoles = rolesArray.map((r) => ({
        identifier: typeof r === "string" ? r : r?.identifier,
      }));

      setAllRoles(formattedRoles);

    } catch (err) {
      console.error("ROLES ERROR:", err);
      setError("Failed to load roles");
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setUser((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleRoleChange = (e) => {
    const { value, checked } = e.target;

    setUser((prev) => ({
      ...prev,
      roles: checked
        ? [...prev.roles, value]
        : prev.roles.filter((r) => r !== value),
    }));
  };

  const handleUpdate = async () => {
    try {
      const res = await axios.post(
        "http://localhost:8080/api/user/update",
        user,
        {
          params: { oldUsername: originalUsername },
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      if (res.status === 200) {
        alert("Profile updated ");
        setEditMode(false);
      }

    } catch (err) {
      console.error(err);
      setError("Update failed");
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    router.push("/");
  };

  if (loading) {
    return (
      <Layout>
        <p className="text-center mt-10">Loading...</p>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="min-h-screen flex justify-center items-center bg-gray-100 p-4">
        <div className="w-full max-w-lg bg-white shadow-xl rounded-xl p-6">

          <h2 className="text-2xl font-bold mb-4 text-center">
            My Profile 👤
          </h2>

          {error && <p className="text-red-500">{error}</p>}

          <div className="mb-3">
            <label htmlFor="name">Name</label>
            <input
              id="name"
              name="name"
              value={user.name}
              onChange={handleChange}
              disabled={!editMode}
              className={`w-full border p-2 rounded ${
                !editMode && "bg-gray-200"
              }`}
            />
          </div>

          <div className="mb-3">
            <label htmlFor="username">Email</label>
            <input
              id="username"
              name="username"
              value={user.username}
              onChange={handleChange}
              disabled={!editMode}
              className={`w-full border p-2 rounded ${
                !editMode && "bg-gray-200"
              }`}
            />
          </div>

          <div className="mb-3">
            <label htmlFor="phoneNo">Phone</label>
            <input
              id="phoneNo"
              name="phoneNo"
              value={user.phoneNo}
              onChange={handleChange}
              disabled={!editMode}
              className={`w-full border p-2 rounded ${
                !editMode && "bg-gray-200"
              }`}
            />
          </div>

          <div className="mb-4">
            <fieldset>
              <legend className="font-bold">Roles</legend>

              {editMode ? (
                <div className="flex flex-wrap gap-2 mt-2">
                {allRoles.length > 0 ? (
                  allRoles.map((role) => (
                    <label key={role.identifier} className="flex gap-1">
                      <input
                        type="checkbox"
                        value={role.identifier}
                        checked={user.roles.includes(
                          String(role.identifier)
                        )}
                        onChange={handleRoleChange}
                      />
                      {role.identifier}
                    </label>
                  ))
                ) : (
                  <p>No Roles</p>
                )}
                </div>
              ) : (
                <p>{user.roles.join(", ") || "No roles"}</p>
              )}
            </fieldset>
          </div>

          <div className="flex flex-col gap-2">
            {editMode ? (
              <button
                onClick={handleUpdate}
                className="bg-green-600 text-white p-2 rounded"
              >
                Save Changes
              </button>
            ) : (
              <button
                onClick={() => setEditMode(true)}
                className="bg-blue-600 text-white p-2 rounded"
              >
                Edit Profile
              </button>
            )}

            <button
              onClick={handleLogout}
              className="border p-2 rounded"
            >
              Logout
            </button>
          </div>

        </div>
      </div>
    </Layout>
  );
}
