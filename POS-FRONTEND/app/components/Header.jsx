"use client";

import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";

function Header({
  sidebarOpen,
  setSidebarOpen,
  profileOpen,
  setProfileOpen,
  handleLogout,
}) {
  const [username, setUsername] = useState("User");

  const router = useRouter();

  useEffect(() => {
    const storedUsername = localStorage.getItem("username");
    setUsername(storedUsername || "User");
  }, []);

  return (
    <header
      className="
        fixed top-0 right-0 left-0
        h-[60px]
        bg-white
        border-b border-gray-200
        flex items-center justify-between
        px-4
        z-40
        shadow-sm
        transition-all duration-300
      "
      style={{
        left: sidebarOpen ? "220px" : "64px",
      }}
    >
      <div className="flex items-center gap-3">
        <button
          onClick={() => setSidebarOpen((prev) => !prev)}
          className="p-1.5 rounded-lg text-gray-500 hover:bg-gray-100"
        >
          ☰
        </button>
      </div>

      <div className="relative">
        <button
          onClick={() => setProfileOpen((prev) => !prev)}
          className="flex items-center gap-2"
        >
          <div className="text-right hidden sm:block">
            <div className="text-xs text-gray-400">
              Hello 👋
            </div>

            <div className="text-sm font-semibold text-gray-700">
              {username}
            </div>
          </div>

          <div className="w-8 h-8 rounded-full bg-red-600 flex items-center justify-center text-white font-semibold">
            {username ? username.charAt(0).toUpperCase() : "U"}
          </div>
        </button>

        {profileOpen && (
          <div className="absolute right-0 top-11 w-52 bg-white rounded-xl shadow-xl border border-gray-100 overflow-hidden z-50">

            <button
              onClick={() => {
                setProfileOpen(false);
                router.push("/profile");
              }}
              className="w-full px-4 py-3 text-left text-gray-700 hover:bg-gray-50 border-b"
            >
              👤 My Profile
            </button>

            <button
              onClick={handleLogout}
              className="w-full px-4 py-3 text-left text-red-500 hover:bg-red-50"
            >
              🚪 Logout
            </button>

          </div>
        )}
      </div>
    </header>
  );
}

Header.propTypes = {
  sidebarOpen: PropTypes.bool.isRequired,
  setSidebarOpen: PropTypes.func.isRequired,
  profileOpen: PropTypes.bool.isRequired,
  setProfileOpen: PropTypes.func.isRequired,
  handleLogout: PropTypes.func.isRequired,
};

export default Header;