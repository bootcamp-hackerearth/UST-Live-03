"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import "./page.css";

const UpdateProfile = () => {
  const router = useRouter();

  const [loading, setLoading] = useState(true);
  const [username, setUsername] = useState("");

  const [availableRoles, setAvailableRoles] = useState([]);

  const [formData, setFormData] = useState({
    id: "",
    name: "",
    phoneNo: "",
    username: "",
    roles: [],
  });

  useEffect(() => {
    if (typeof globalThis !== "undefined") {
      const params = new URLSearchParams(globalThis.location.search);
      const uname = params.get("username");

      if (!uname) {
        alert("Username not found");
        router.push("/user/profile");
        return;
      }

      setUsername(uname);
    }
  }, [router]);

  const fetchRoles = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");

      const response = await axios.get(
        "http://localhost:8080/api/role/list",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      console.log("Roles Response:", response.data);

      setAvailableRoles(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error("Failed to fetch roles");
      console.error("Status:", err.response?.status);
      console.error("Response:", err.response?.data);
      console.error(err);

      setAvailableRoles([]);
    }
  }, []);

  const fetchUser = useCallback(async (uname) => {
    try {
      const token = localStorage.getItem("token");

      const response = await axios.get(
        `http://localhost:8080/api/user/get?username=${encodeURIComponent(
          uname
        )}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      const user = response.data;

      console.log("User Response:", user);

      setFormData({
        id: user.id,
        name: user.name ?? "",
        phoneNo: user.phoneNo ?? "",
        username: user.username ?? "",
        roles:
          user.roles?.map((role) =>
            typeof role === "string"
              ? role
              : role.identifier
          ) ?? [],
      });
    } catch (err) {
      console.error(err);
      alert("Failed to fetch user");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchRoles();
  }, [fetchRoles]);

  useEffect(() => {
    if (username) {
      fetchUser(username);
    }
  }, [username, fetchUser]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleRoleChange = (e) => {
    const selectedRoles = Array.from(
      e.target.selectedOptions,
      (option) => option.value
    );

    setFormData((prev) => ({
      ...prev,
      roles: selectedRoles,
    }));
  };

  const validateForm = () => {
    if (!formData.name.trim()) {
      alert("Name is required");
      return false;
    }

    if (!/^\d{10}$/.test(formData.phoneNo)) {
      alert("Phone number must contain exactly 10 digits");
      return false;
    }

    if (formData.roles.length === 0) {
      alert("Please select at least one role");
      return false;
    }

    return true;
  };

  const handleUpdate = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    try {
      const token = localStorage.getItem("token");

      const payload = {
        id: formData.id,
        name: formData.name,
        phoneNo: formData.phoneNo,
        username: formData.username,
        roles: formData.roles,
      };

      console.log("Update Payload:", payload);

      await axios.put(
        "http://localhost:8080/api/user/update",
        payload,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      alert("Profile updated successfully");
      router.push("/user/profile");
    } catch (err) {
      console.error(err);
      console.error("Status:", err.response?.status);
      console.error("Response:", err.response?.data);

      alert("Update failed");
    }
  };

  if (loading) {
    return <div className="update-loading">Loading...</div>;
  }

  return (
    <div className="update-wrapper">
      <div className="update-card">
        <h2 className="update-title">Update Profile</h2>

        <form onSubmit={handleUpdate}>
          <div className="update-row">
            <label htmlFor="name">Name</label>
            <input
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
            />
          </div>

          <div className="update-row">
            <label htmlFor="phoneNo">Phone Number</label>
            <input
              id="phoneNo"
              name="phoneNo"
              value={formData.phoneNo}
              onChange={handleChange}
            />
          </div>

          <div className="update-row">
            <label htmlFor="username">Username</label>
            <input
              id="username"
              value={formData.username}
              disabled
            />
          </div>

          <div className="update-row">
            <label htmlFor="roles">Roles</label>

            <select
              multiple
              className="role-select"
              value={formData.roles}
              onChange={handleRoleChange}
            >
              {availableRoles.length > 0 ? (
                availableRoles.map((role) => (
                  <option
                    key={role.id}
                    value={role.identifier}
                  >
                    {role.identifier}
                  </option>
                ))
              ) : (
                <option disabled>No Roles Available</option>
              )}
            </select>

            <small>
              Hold Ctrl (Windows) or Cmd (Mac) to select multiple roles.
            </small>
          </div>

          <div className="update-actions">
            <button
              type="submit"
              className="update-btn"
            >
              Save Changes
            </button>

            <button
              type="button"
              className="update-btn cancel"
              onClick={() => router.push("/user/profile")}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default UpdateProfile;