"use client";

import React, { useState, useRef, useEffect } from "react";
import PropTypes from "prop-types";
import { usePathname, useRouter } from "next/navigation";

const Header = ({ username }) => {
  const pathname = usePathname();
  const router = useRouter();
  const pathSegments = pathname.split("/").find(Boolean);
  const currentModule = pathSegments
    ? pathSegments.charAt(0).toUpperCase() + pathSegments.slice(1)
    : "Overview";

  const [showMenu, setShowMenu] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setShowMenu(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = async () => {
  await fetch("/api/auth/logout", { method: "POST" });

  if ("cookieStore" in window) {
    await cookieStore.delete("token");
    await cookieStore.delete("username");
  }

  window.location.href = "/login";
};

  return (
    <header className="h-14 bg-white border-b border-slate-200 px-6 flex justify-between items-center flex-shrink-0 select-none">

      <div className="flex items-center gap-2 text-sm font-medium">
        <span className="text-slate-400">App</span>
        <svg className="w-3 h-3 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5">
          <path strokeLinecap="round" strokeLinejoin="round" d="M9 5l7 7-7 7" />
        </svg>
        <span className="text-slate-800 font-semibold tracking-tight">{currentModule}</span>
      </div>

      <div className="flex items-center gap-4">

        <div className="hidden sm:flex items-center gap-2 text-xs font-medium text-slate-500 bg-slate-50 border border-slate-200/60 px-2.5 py-1 rounded-md">
          <svg className="w-3.5 h-3.5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          <span>
            {new Date().toLocaleDateString("en-GB", { day: "2-digit", month: "short", year: "numeric" })}
          </span>
        </div>

        <span className="w-px h-4 bg-slate-200 hidden sm:block" />
        <div className="relative" ref={dropdownRef}>
          <button
            onClick={() => setShowMenu((p) => !p)}
            className="flex items-center gap-2 cursor-pointer rounded-lg px-1.5 py-1 hover:bg-slate-50 transition-colors"
          >
            <div className="w-7 h-7 rounded-full bg-blue-50 border border-blue-200 flex items-center justify-center text-xs font-bold text-blue-600 uppercase">
              {username ? username.charAt(0) : "U"}
            </div>
            <div className="flex flex-col text-left">
              <span className="text-xs font-semibold text-slate-700 leading-none">
                {username ? username.split("@")[0] : "User"}
              </span>
            </div>
            <svg
              className={`w-3.5 h-3.5 text-slate-400 transition-transform duration-200 ${showMenu ? "rotate-180" : ""}`}
              fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5"
            >
              <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
            </svg>
          </button>

          {showMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-white border border-slate-200 rounded-xl shadow-lg shadow-slate-200/60 z-50 overflow-hidden">
              <div className="px-4 py-3 border-b border-slate-100 bg-gradient-to-r from-blue-50 to-slate-50">
                <div className="flex items-center gap-2.5">
                  <div className="w-8 h-8 rounded-full bg-blue-100 border-2 border-blue-200 flex items-center justify-center text-xs font-bold text-blue-600 uppercase">
                    {username ? username.charAt(0) : "U"}
                  </div>
                  <div>
                    <p className="text-xs font-semibold text-slate-800 leading-none">
                      {username ? username.split("@")[0] : "User"}
                    </p>
                    <span className="inline-flex items-center gap-1 text-[10px] font-medium text-green-600 mt-0.5">
                      <span className="w-1.5 h-1.5 rounded-full bg-green-500 inline-block" />{''}
                      Active
                    </span>
                  </div>
                </div>
              </div>

              <div className="p-1.5">
                <button
                  onClick={() => { setShowMenu(false); router.push("/profile"); }}
                  className="w-full flex items-center gap-2.5 px-3 py-2 rounded-lg text-xs font-medium text-slate-700 hover:bg-slate-50 hover:text-blue-600 transition-colors text-left"
                >
                  <svg className="w-3.5 h-3.5 text-slate-400" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                  </svg>
                  My Profile
                </button>
              </div>
              <div className="border-t border-slate-100 p-1.5">
                <button
                  onClick={handleLogout}
                  className="w-full flex items-center gap-2.5 px-3 py-2 rounded-lg text-xs font-medium text-red-500 hover:text-red-600 hover:bg-red-50 transition-colors"
                >
                  <svg className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                  Sign Out
                </button>
              </div>

            </div>
          )}
        </div>

      </div>
    </header>
  );
};

Header.propTypes = {
  username: PropTypes.string,
};

export default Header;
