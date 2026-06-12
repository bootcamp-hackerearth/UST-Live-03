"use client";

import PropTypes from "prop-types";
import { useState, useRef, useEffect } from "react";
import { Menu, User, LogOut, ChevronDown } from "lucide-react";
import { useRouter } from "next/navigation";

const Header = ({ sidebarOpen, setSidebarOpen, username }) => {
  const router = useRouter();

  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    router.replace("/login");
  };

  /* ✅ Close dropdown on outside click */
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (ref.current && !ref.current.contains(e.target)) {
        setOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <header className="h-[75px] bg-white border-b border-blue-100 flex items-center justify-between px-6 shadow-sm">

      {/* ✅ LEFT: MENU BUTTON */}
      <button
        onClick={() => setSidebarOpen(!sidebarOpen)}
        className="
          w-10 h-10 
          bg-gradient-to-br from-blue-700 to-blue-900 
          hover:from-blue-800 hover:to-blue-950 
          text-white 
          rounded-lg 
          flex items-center justify-center 
          transition shadow-md
        "
      >
        <Menu size={20} />
      </button>

      {/* ✅ RIGHT: PROFILE DROPDOWN */}
      <div ref={ref} className="relative">

        <button
          onClick={() => setOpen(!open)}
          className="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-blue-50 transition"
        >
          {/* Avatar */}
          <div className="w-9 h-9 bg-gradient-to-br from-blue-700 to-blue-900 text-white rounded-full flex items-center justify-center font-semibold">
            {username?.charAt(0)?.toUpperCase() || "G"}
          </div>

          {/* Name */}
          <div className="text-left">
            <div className="text-sm font-semibold text-blue-900">
              {username || "Guest"}
            </div>
            <div className="text-xs text-gray-500">
              User
            </div>
          </div>

          <ChevronDown size={16} className="text-gray-500" />
        </button>

        {/* ✅ DROPDOWN MENU */}
        {open && (
          <div className="absolute right-0 mt-2 w-44 bg-white border border-gray-200 rounded-xl shadow-lg overflow-hidden z-50">

            {/* Profile */}
            <button
              onClick={() => router.push("/profile")}
              className="w-full text-left px-4 py-2 text-sm flex items-center gap-2 hover:bg-gray-100"
            >
              <User size={16} /> Profile
            </button>

            {/* Divider */}
            <div className="border-t border-gray-100"></div>

            {/* Logout */}
            <button
              onClick={handleLogout}
              className="w-full text-left px-4 py-2 text-sm flex items-center gap-2 text-red-500 hover:bg-red-50"
            >
              <LogOut size={16} /> Logout
            </button>

          </div>
        )}
      </div>

    </header>
  );
};

/* ✅ PROP TYPES */
Header.propTypes = {
  sidebarOpen: PropTypes.bool.isRequired,
  setSidebarOpen: PropTypes.func.isRequired,
  username: PropTypes.string,
};

export default Header;