"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import "./UserProfile.css";

const UserProfile = () => {
  const router = useRouter();

  const [loading, setLoading] = useState(true);

  const [user, setUser] = useState({
    name: "",
    phoneNo: "",
    username: "",
    roles: [],
  });

  // ================= FETCH USER =================
  const fetchUser = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");
      const username = localStorage.getItem("username");

      if (!token || !username) {
        router.push("/login");
        return;
      }

      const res = await axios.get(
        `http://localhost:8080/api/user/get?username=${username}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setUser({
        name: res.data?.name || "-",
        phoneNo: res.data?.phoneNo || "-",
        username: res.data?.username || username,
        roles: res.data?.roles || [],
      });

    } catch (err) {
      console.error("PROFILE FETCH ERROR:", err);

      // fallback (prevents blank UI crash)
      const username = localStorage.getItem("username");

      setUser({
        name: "-",
        phoneNo: "-",
        username: username || "-",
        roles: [],
      });

    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]);

  // ================= LOADING =================
  if (loading) {
    return <div className="profile-loading">Loading profile...</div>;
  }

  return (
    <div className="profile-wrapper">

      <div className="profile-card">

        {/* HEADER */}
        <div className="profile-header">

          <div className="profile-avatar">
            {user.name?.charAt(0)?.toUpperCase() || "A"}
          </div>

          <div>
            <h2>{user.name}</h2>
            <p>User Profile</p>
          </div>

        </div>

        {/* DETAILS */}
        <div className="profile-section">

          <div className="profile-row">
            <span>Name</span>
            <span>{user.name}</span>
          </div>

          <div className="profile-row">
            <span>Phone No</span>
            <span>{user.phoneNo}</span>
          </div>

          <div className="profile-row">
            <span>Username</span>
            <span>{user.username}</span>
          </div>

          <div className="profile-row">
            <span>Roles</span>
            <span>
              {(user.roles || []).length > 0
                ? user.roles.join(", ")
                : "-"}
            </span>
          </div>

        </div>

        {/* BUTTONS */}
        <div className="profile-actions">

          <button
            className="profile-btn"
            onClick={() => router.push("/dashboard1")}
          >
            ← Back to Dashboard
          </button>

          {/* <button
            className="profile-btn"
            onClick={() => router.push("/user/update")}
          >
            Update Profile
          </button> */}

        </div>

      </div>

    </div>
  );
};

export default UserProfile;