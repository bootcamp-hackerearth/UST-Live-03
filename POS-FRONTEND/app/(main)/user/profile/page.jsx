"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getItem } from "@/services/api";
import "./profile.css";

export default function UserProfile() {
  const router = useRouter();

  const [loading, setLoading] = useState(true);

  const [user, setUser] = useState({
    name: "",
    phoneNo: "",
    username: "",
    roles: [],
  });

  useEffect(() => {
    fetchUser();
  }, []);

  const fetchUser = async () => {
    try {
      const username =
        localStorage.getItem("username");

      if (!username) {
        router.push("/login");
        return;
      }

      const userData = await getItem(
        "user",
        username
      );

      setUser({
        name: userData?.name || "-",
        phoneNo: userData?.phoneNo || "-",
        username:
          userData?.username || username,
        roles: userData?.roles || [],
      });
    } catch (err) {
      console.error(
        "PROFILE FETCH ERROR:",
        err
      );
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="profile-loading">
        Loading profile...
      </div>
    );
  }

  return (
    <div className="profile-wrapper">
      <div className="profile-card">

        <div className="profile-header">
          <div className="profile-avatar">
            {user.name?.charAt(0)?.toUpperCase() ||
              "A"}
          </div>

          <div>
            <h2>{user.name}</h2>
            <p>User Profile</p>
          </div>
        </div>

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
              {user.roles.length > 0
                ? user.roles.join(", ")
                : "-"}
            </span>
          </div>

        </div>

        <button
          className="profile-btn"
          onClick={() =>
            router.push("/dashboard1")
          }
        >
          ← Back to Dashboard
        </button>

      </div>
    </div>
  );
}