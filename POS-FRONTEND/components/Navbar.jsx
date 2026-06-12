"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

export default function Navbar() {
  const router = useRouter();
  const [mounted, setMounted] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const [userData, setUserData] = useState({
    name: "User",
    username: "user@gmail.com",
    role: "USER",
    phoneNo: "Not Available",
  });

  useEffect(() => {
    setMounted(true);
    setUserData({
      name: localStorage.getItem("name") || "User",
      username: localStorage.getItem("username") || "user@gmail.com",
      role: localStorage.getItem("role") || "USER",
      phoneNo: localStorage.getItem("phoneNo") || "Not Available",
    });
  }, []);

  if (!mounted) {
    return null;
  }

  const userInitial = userData.name?.charAt(0)?.toUpperCase();

  return (
    <>
      <style>{`
        .navbar-wrapper {
          position: sticky;
          top: 0;
          z-index: 30;
          height: 88px;
          padding: 0 40px;
          background: transparent;
          border-bottom: 1px solid #ebebf5;
          display: flex;
          align-items: center;
          justify-content: space-between;
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
          box-sizing: border-box;
        }

        .welcome-title {
          font-size: 22px;
          font-weight: 600;
          color: #2d2d6e;
          letter-spacing: -0.5px;
          margin: 0;
        }

        .welcome-link {
          background: none;
          border: none;
          padding: 0;
          font-size: inherit;
          font-weight: inherit;
          font-family: inherit;
          letter-spacing: inherit;
          color: inherit;
          cursor: pointer;
          text-align: left;
          transition: opacity 0.15s ease;
        }

        .welcome-link:hover {
          opacity: 0.85;
        }

        .welcome-link span {
          color: #6c63ff;
        }

        .navbar-subtitle {
          font-size: 13px;
          color: #8888a0;
          margin: 4px 0 0 0;
        }

        .profile-container {
          position: relative;
          z-index: 50;
        }

        .avatar-trigger {
          width: 44px;
          height: 44px;
          border-radius: 50%;
          background: #6c63ff;
          color: #ffffff;
          font-size: 15px;
          font-weight: 600;
          border: none;
          cursor: pointer;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: transform 0.15s ease;
        }

        .avatar-trigger:hover {
          transform: scale(1.04);
        }

        .dropdown-overlay {
          position: fixed;
          inset: 0;
          z-index: 40;
          background: transparent;
        }

        .profile-dropdown {
          position: absolute;
          right: 0;
          top: 56px;
          width: 300px;
          background: #ffffff;
          border: 1px solid #ebebf5;
          border-radius: 12px;
          box-shadow: 0 10px 30px rgba(45, 45, 110, 0.08);
          padding: 24px;
          box-sizing: border-box;
          transition: all 0.2s ease;
        }

        .dropdown-hidden {
          opacity: 0;
          visibility: hidden;
          transform: translateY(-8px);
          pointer-events: none;
        }

        .dropdown-visible {
          opacity: 1;
          visibility: visible;
          transform: translateY(0);
        }

        .dropdown-header {
          display: flex;
          align-items: center;
          gap: 14px;
          padding-bottom: 16px;
          border-bottom: 1px solid #ebebf5;
          margin-bottom: 16px;
        }

        .header-avatar {
          width: 48px;
          height: 48px;
          border-radius: 50%;
          background: #f4f5fa;
          color: #6c63ff;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 18px;
          font-weight: 700;
        }

        .header-info {
          display: flex;
          flex-direction: column;
        }

        .info-name {
          font-size: 15px;
          font-weight: 600;
          color: #2d2d6e;
          margin: 0;
        }

        .info-role {
          font-size: 12px;
          font-weight: 500;
          color: #6c63ff;
          margin: 2px 0 0 0;
          text-transform: uppercase;
          letter-spacing: 0.05em;
        }

        .info-stack {
          display: flex;
          flex-direction: column;
          gap: 12px;
        }

        .stack-item {
          display: flex;
          flex-direction: column;
        }

        .item-label {
          font-size: 11px;
          font-weight: 600;
          color: #b0b0c8;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          margin-bottom: 2px;
        }

        .item-value {
          font-size: 13px;
          color: #4b4b75;
          margin: 0;
          word-break: break-all;
        }

        .profile-action-btn {
          width: 100%;
          height: 38px;
          background: #6c63ff;
          border: none;
          border-radius: 8px;
          color: #ffffff;
          font-size: 13px;
          font-weight: 500;
          margin-top: 20px;
          cursor: pointer;
          transition: background-color 0.15s ease;
        }

        .profile-action-btn:hover {
          background-color: #5850ec;
        }
      `}</style>

      {profileOpen && (
        <button
          type="button"
          className="dropdown-overlay"
          onClick={() => setProfileOpen(false)}
          onKeyDown={(e) => e.key === "Escape" && setProfileOpen(false)}
          aria-label="Close profile overlay"
        />
      )}

      <nav className="navbar-wrapper">
        <button
          type="button"
          onClick={() => router.push("/home")}
          className="welcome-button"
          style={{
            background: "none",
            border: "none",
            padding: 0,
            cursor: "pointer",
            textAlign: "left",
          }}
        >
          <h1 className="welcome-title">
            Welcome <span>{userData.name}</span>
          </h1>
          <p className="navbar-subtitle">POS Dashboard</p>
        </button>

        <div className="profile-container">
          <button
            type="button"
            onClick={() => setProfileOpen(!profileOpen)}
            className="avatar-trigger"
          >
            {userInitial}
          </button>

          <div
            className={`profile-dropdown ${
              profileOpen ? "dropdown-visible" : "dropdown-hidden"
            }`}
          >
            <div className="dropdown-header">
              <div className="header-avatar">{userInitial}</div>
              <div className="header-info">
                <h3 className="info-name">{userData.name}</h3>
                <span className="info-role">{userData.role}</span>
              </div>
            </div>

            <div className="info-stack">
              <div className="stack-item">
                <span className="item-label">Email</span>
                <p className="item-value">{userData.username}</p>
              </div>

              <div className="stack-item">
                <span className="item-label">Role</span>
                <p className="item-value">{userData.role}</p>
              </div>

              <div className="stack-item">
                <span className="item-label">Phone</span>
                <p className="item-value">{userData.phoneNo}</p>
              </div>
            </div>

            <button
              type="button"
              onClick={() => {
                router.push("/home/profile");
                setProfileOpen(false);
              }}
              className="profile-action-btn"
            >
              Edit Profile
            </button>
          </div>
        </div>
      </nav>
    </>
  );
}

