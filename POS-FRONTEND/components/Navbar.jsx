"use client";

import React, { useState, useEffect } from "react";
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
      {profileOpen && (
        <button
          type="button"
          className="fixed inset-0 z-40 bg-transparent"
          onClick={() => setProfileOpen(false)}
          onKeyDown={(e) => e.key === "Escape" && setProfileOpen(false)}
          aria-label="Close profile overlay"
        />
      )}

      <nav className="sticky top-0 z-30 h-[88px] px-10 bg-transparent border-b border-[#ebebf5] flex items-center justify-between font-sans box-border">
        <button
          type="button"
          onClick={() => router.push("/home")}
          className="bg-transparent border-none p-0 cursor-pointer text-left transition-opacity duration-150 hover:opacity-85"
        >
          <h1 className="text-[22px] font-semibold text-[#2d2d6e] tracking-tight m-0">
            Welcome <span className="text-[#6c63ff]">{userData.name}</span>
          </h1>
          <p className="text-xs text-[#8888a0] mt-1 m-0">POS Dashboard</p>
        </button>

        <div className="relative z-50">
          <button
            type="button"
            onClick={() => setProfileOpen(!profileOpen)}
            className="w-11 h-11 rounded-full bg-[#6c63ff] text-white text-md font-semibold border-none cursor-pointer flex items-center justify-center transition-transform duration-150 hover:scale-104"
          >
            {userInitial}
          </button>

          <div
            className={`absolute right-0 top-14 w-[300px] bg-white border border-[#ebebf5] rounded-xl shadow-xl p-6 box-border transition-all duration-200 ${profileOpen
                ? "opacity-100 visible translate-y-0"
                : "opacity-0 invisible -translate-y-2 pointer-events-none"
              }`}
          >
            <div className="flex items-center gap-3.5 pb-4 border-b border-[#ebebf5] mb-4">
              <div className="w-12 h-12 rounded-full bg-[#f4f5fa] text-[#6c63ff] flex items-center justify-center text-lg font-bold">
                {userInitial}
              </div>
              <div className="flex flex-col">
                <h3 className="text-sm font-semibold text-[#2d2d6e] m-0">{userData.name}</h3>
                <span className="text-[11px] font-medium text-[#6c63ff] mt-0.5 uppercase tracking-wider">
                  {userData.role}
                </span>
              </div>
            </div>

            <div className="flex flex-col gap-3">
              <div className="flex flex-col">
                <span className="text-[10px] font-semibold text-[#b0b0c8] uppercase tracking-wider mb-0.5">Email</span>
                <p className="text-xs text-[#4b4b75] m-0 break-all">{userData.username}</p>
              </div>

              <div className="flex flex-col">
                <span className="text-[10px] font-semibold text-[#b0b0c8] uppercase tracking-wider mb-0.5">Role</span>
                <p className="text-xs text-[#4b4b75] m-0 break-all">{userData.role}</p>
              </div>

              <div className="flex flex-col">
                <span className="text-[10px] font-semibold text-[#b0b0c8] uppercase tracking-wider mb-0.5">Phone</span>
                <p className="text-xs text-[#4b4b75] m-0 break-all">{userData.phoneNo}</p>
              </div>
            </div>

            <button
              type="button"
              onClick={() => {
                router.push("/home/profile");
                setProfileOpen(false);
              }}
              className="w-full h-[38px] bg-[#6c63ff] border-none rounded-lg text-white text-xs font-medium mt-5 cursor-pointer transition-colors duration-150 hover:bg-[#5850ec]"
            >
              Edit Profile
            </button>
          </div>
        </div>
      </nav>
    </>
  );
}