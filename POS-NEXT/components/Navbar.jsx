"use client";

import { useEffect, useState } from "react";
import { SidebarOpenIcon } from "lucide-react";
import Link from "next/link";
import Sidebar from "./Sidebar";

const Navbar = () => {
  const [userDetails, setUserDetails] = useState({
    username: "",
    name: "",
    phoneNo: "",
    roles: [],
  });
  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://3.104.111.192/api";
  const [showProfile, setShowProfile] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  useEffect(() => {
    const userName = localStorage.getItem("username");
    if (!userName) return;

    const fetchUser = async () => {
      try {
        const res = await fetch(`${baseUrl}/user/${userName}`, {
          method: "GET",
          headers: { "Content-Type": "text/plain" },
          credentials: "include",
        });

        const data = await res.json();

        setUserDetails({
          username: data.username,
          name: data.name,
          phoneNo: data.phoneNo,
          roles: data.roles || [],
        });
      } catch (err) {
        console.error(err);
      }
    };

    fetchUser();
  }, []);

  const handleLogout = async () => {
    try {
      await fetch("/api/logout", { method: "POST" });
      localStorage.removeItem("username");
      globalThis.location.href = "/login";
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <>
      <div className="flex justify-between items-center px-6 py-3 bg-[#F4F5FB] shadow-sm border-b border-gray-200">

        <div className="flex items-center gap-4">
          <button
            onClick={() => setSidebarOpen(true)}
            className="p-2 rounded-lg hover:bg-gray-200 transition"
          >
            <SidebarOpenIcon size={18} className="text-gray-600" />
          </button>

          <Link href="/">
            <h4 className="text-md font-semibold text-gray-700 tracking-wide">
              <span className="text-indigo-500">POS</span>
              <span className="mx-1">-</span>
              <span>APPLICATION</span>
            </h4>
          </Link>
        </div>

        <button
          onClick={() => setShowProfile(!showProfile)}
          className="w-9 h-9 rounded-full bg-violet-500 text-white flex items-center justify-center text-sm font-semibold shadow-sm hover:bg-indigo-600 transition"
        >
          {userDetails.name
            ? userDetails.name.charAt(0).toUpperCase()
            : "U"}
        </button>
      </div>

      {showProfile && (
        <div className="absolute right-6 top-16 w-72 bg-white border border-gray-200 rounded-2xl shadow-md p-5 z-50">

          <h2 className="text-md font-semibold text-gray-800">
            {userDetails.name}
          </h2>

          <p className="text-xs text-gray-500 mb-3">
            {userDetails.roles?.[0] || "User"}
          </p>

          <div className="border-t my-3"></div>

          <div className="text-sm space-y-2 text-gray-600">
            <p>
              <span className="text-gray-400">Username:</span>{" "}
              {userDetails.username}
            </p>

            <p>
              <span className="text-gray-400">Phone:</span>{" "}
              {userDetails.phoneNo}
            </p>
          </div>

          <button
            onClick={handleLogout}
            className="w-full mt-4 bg-red-400 text-white py-2 rounded-xl text-sm font-medium hover:bg-red-500 transition"
          >
            Logout
          </button>
        </div>
      )}

      <Sidebar
        sidebarOpen={sidebarOpen}
        setSidebarOpen={setSidebarOpen}
      />
    </>
  );
};

export default Navbar;
