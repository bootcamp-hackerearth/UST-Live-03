"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axios from "axios";
import "./page.css";

const UpdateProfile = () => {
  const router = useRouter();
  const searchParams = useSearchParams();

  const username = searchParams.get("username");

  const [loading, setLoading] = useState(true);

  const [formData, setFormData] = useState({
    id: "",
    name: "",
    phoneNo: "",
    username: "",
    roles: [],
  });

  const fetchUser = useCallback(async () => {
    try {
      setLoading(true);

      const token = localStorage.getItem("token");

      if (!token) {
        router.push("/login");
        return;
      }

      if (!username) {
        alert("Username not found");
        router.push("/user/profile");
        return;
      }

      const response = await axios.get(
        `http://localhost:8080/api/user/get?username=${username}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        },
      );

      const data = response.data;

      setFormData({
        id: data?.id || "",
        name: data?.name || "",
        phoneNo: data?.phoneNo || "",
        username: data?.username || "",
        roles: data?.roles || [],
      });
    } catch (error) {
      console.error("FETCH USER ERROR:", error);

      alert("Failed to fetch user details");
    } finally {
      setLoading(false);
    }
  }, [router, username]);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const validateForm = () => {
    if (!formData.name.trim()) {
      alert("Name is required");
      return false;
    }

    if (!/^\d{10}$/.test(formData.phoneNo)) {
      alert("Phone number must be 10 digits");
      return false;
    }

    return true;
  };

  const handleUpdate = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    try {
      const token = localStorage.getItem("token");

      const payload = {
        id: formData.id,
        name: formData.name,
        phoneNo: formData.phoneNo,
        username: formData.username,
        roles: formData.roles,
      };

      await axios.post("http://localhost:8080/api/user/update", payload, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      alert("Profile updated successfully");

      router.push("/user/profile");
    } catch (error) {
      console.error("UPDATE ERROR:", error);

      alert("Failed to update profile");
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
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              placeholder="Enter your name"
            />
          </div>

          <div className="update-row">
            <label htmlFor="phoneNo">Phone No</label>

            <input
              type="text"
              name="phoneNo"
              value={formData.phoneNo}
              onChange={handleChange}
              placeholder="Enter phone number"
            />
          </div>

          <div className="update-row">
            <label htmlFor="username">Username</label>

            <input
              id="username"
              type="text"
              value={formData.username}
              disabled
            />
          </div>

          <div className="update-row">
            <div>Roles</div>

            <span>
              {formData.roles.length > 0 ? formData.roles.join(", ") : "-"}
            </span>
          </div>

          <div className="update-actions">
            <button type="submit" className="update-btn">
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
