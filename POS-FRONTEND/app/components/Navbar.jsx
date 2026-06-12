"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";

export default function Navbar({ toggleSidebar, logout }) {
  const [open, setOpen] = useState(false);
  const router = useRouter();

  const goToProfile = () => {
    setOpen(false);
    router.push("/profile");
  };

  const handleLogout = () => {
    setOpen(false);
    logout();
  };

  return (
    <nav
      style={{
        height: 56,
        display: "flex",
        alignItems: "center",
        padding: "0 16px",
        background: "linear-gradient(to right, #0f766e, #134e4a)",
        color: "white",
        position: "relative",
      }}
    >
      <button
        onClick={toggleSidebar}
        style={{
          background: "none",
          border: "none",
          color: "white",
          fontSize: 20,
          marginRight: 12,
          cursor: "pointer",
        }}
      >
        ☰
      </button>

      <span style={{ fontWeight: 600, fontSize: 18 }}>
        POS SYSTEM
      </span>

      <div style={{ marginLeft: "auto", position: "relative" }}>
        <button
          onClick={() => setOpen(!open)}
          style={{
            backgroundColor: "black",
            border: "none",
            color: "white",
            padding: "6px 14px",
            borderRadius: 6,
            cursor: "pointer",
          }}
        >
          Profile ▼
        </button>

        {open && (
          <div
            style={{
              position: "absolute",
              right: 0,
              top: 40,
              background: "white",
              color: "black",
              borderRadius: 8,
              boxShadow: "0 4px 10px rgba(0,0,0,0.2)",
              overflow: "hidden",
              minWidth: 150,
            }}
          >
            <button
              onClick={goToProfile}
              style={{
                width: "100%",
                textAlign: "left",
                padding: 10,
                border: "none",
                background: "transparent",
                cursor: "pointer",
                borderBottom: "1px solid #eee",
              }}
            >
              My Profile
            </button>

            <button
              onClick={handleLogout}
              style={{
                width: "100%",
                textAlign: "left",
                padding: 10,
                border: "none",
                background: "transparent",
                cursor: "pointer",
                color: "red",
              }}
            >
              Logout
            </button>
          </div>
        )}
      </div>
    </nav>
  );
}

Navbar.propTypes = {
  toggleSidebar: PropTypes.func.isRequired,
  logout: PropTypes.func.isRequired,
};