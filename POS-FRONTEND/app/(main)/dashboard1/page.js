"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import "./Dashboard1.css";

const Dashboard1 = () => {
  const router = useRouter();

  const [user, setUser] = useState({
    name: "Admin",
    role: "Administrator",
  });

  // USER API
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
        name: res.data?.name || username,
        role: Array.isArray(res.data?.role)
          ? res.data.role.join(", ")
          : "Administrator",
      });
    } catch (err) {
      console.error("USER FETCH ERROR:", err);

      const username = localStorage.getItem("username");

      setUser({
        name: username || "Admin",
        role: "Administrator",
      });
    }
  }, [router]);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]);

  return (
    <div>
      {/* TOPBAR */}
      <div className="topbar">
        <div>
          <h1 className="pageTitle">Dashboard</h1>
          <p className="pageSubtitle">Welcome back</p>
        </div>

        {/* USER PROFILE */}
        <button
          type="button"
          className="profile"
          onClick={() => router.push("/profile")}
        >
          <div className="avatar">
            {user.name?.charAt(0)?.toUpperCase() || "A"}
          </div>

          <div>
            <h4 className="profileName">{user.name}</h4>
            <p className="profileRole">{user.role}</p>
          </div>
        </button>
      </div>

      {/* HERO */}
      <div className="heroCard">
        <div>
          <h2 className="heroTitle">
            Manage your POS business
          </h2>

          <p className="heroText">
            Track sales, orders and users in one place.
          </p>
        </div>
      </div>
    </div>
  );
};

export default Dashboard1;