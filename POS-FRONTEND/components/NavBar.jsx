"use client";

import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function Navbar({ setMenuOpen }) {
  const router = useRouter();
  const [username, setUsername] = useState("");

  useEffect(() => {
    const fullUsername = localStorage.getItem("username") || "";
    setUsername(fullUsername.split("@")[0]);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    router.replace("/login");
    fetch("api/logout", {
      method: "POST",
    }).catch((err) => {
      console.error("Logout failed:", err);
    });
  };

  const goProfile = () => {
    router.push("/profile");
  };

  return (
    <div className="flex items-center justify-between bg-slate-700 px-5 py-3 text-white">

      <div className="flex items-center gap-3 text-lg font-semibold">
        <button
          type="button"
          onClick={() => setMenuOpen((prev) => !prev)}
          className="text-2xl"
        >
          ☰
        </button>
        <span>POS Dashboard</span>
      </div>

      <div className="flex items-center gap-3">

        <button
          type="button"
          onClick={goProfile}
          aria-label="Open profile"
          className="w-8 h-8 flex items-center justify-center rounded-full bg-indigo-500"
        >
          {username?.charAt(0).toUpperCase()}
        </button>

        <button
          type="button"
          onClick={handleLogout}
          className="px-3 py-1 bg-red-500 rounded hover:bg-red-600"
        >
          Logout
        </button>

      </div>
    </div>
  );
}

Navbar.propTypes = {
  setMenuOpen: PropTypes.func.isRequired,
};