"use client";

import { useEffect, useState, useCallback } from "react";

import { useRouter } from "next/navigation";

import axios from "axios";

import "./page.css";

const UserProfile = () => {
  const router = useRouter();

  const [loading, setLoading] = useState(true);

  const [user, setUser] = useState({
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

      const username = localStorage.getItem("username");

      if (!token || !username) {
        router.push("/login");
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

      setUser({
        id: data?.id || "",
        name: data?.name || "",
        phoneNo: data?.phoneNo || "",
        username: data?.username || "",
        roles: data?.roles || [],
      });
    } catch (error) {
      console.error("PROFILE FETCH ERROR:", error);

      alert("Failed to load profile");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]);

  if (loading) {
    return <div className="profile-loading">Loading profile...</div>;
  }

  return (
    <div className="profile-wrapper">
      <div className="profile-card">
        <div className="profile-header">
          <div className="profile-avatar">
            {user.name?.charAt(0)?.toUpperCase() || "U"}
          </div>

          <div>
            <h2>{user.name}</h2>
            <p>User Profile</p>
          </div>
        </div>

        <div className="profile-section">
          <div className="profile-row">
            <span>Name</span>
            <span>{user.name || "-"}</span>
          </div>

          <div className="profile-row">
            <span>Phone No</span>
            <span>{user.phoneNo || "-"}</span>
          </div>

          <div className="profile-row">
            <span>Username</span>
            <span>{user.username || "-"}</span>
          </div>

          <div className="profile-row">
            <span>Roles</span>
            <span>{user.roles.length > 0 ? user.roles.join(", ") : "-"}</span>
          </div>
        </div>

        <div className="profile-actions">
          <button
            className="profile-btn"
            onClick={() => router.push("/dashboard1")}
          >
            ← Back to Dashboard
          </button>

          <button
            className="profile-btn update-btn"
            onClick={() =>
              router.push(`/user/update?username=${user.username}`)
            }
          >
            Update Profile
          </button>
        </div>
      </div>
    </div>
  );
};

export default UserProfile;
