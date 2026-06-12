"use client";
import { useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import axiosInstance from "../api/axiosInstance";

function Header() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [showProfile, setShowProfile] = useState(false);
  const [profileData, setProfileData] = useState(null);
  const [profileLoading, setProfileLoading] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    setUsername(localStorage.getItem("username") || "");
  }, []);

  useEffect(() => {
    function handleClickOutside(e) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setShowProfile(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const renderProfileContent = () => {
    if (profileLoading) {
      return <p className="text-sm text-slate-400 text-center py-2">Loading...</p>;
    }
    if (!profileData) {
      return <p className="text-sm text-slate-400 text-center py-2">Could not load profile.</p>;
    }
    return (
      <>
        <div className="flex flex-col gap-1">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Full Name</span>
          <span className="text-sm text-slate-800 font-medium">{profileData.name || "—"}</span>
        </div>
        <div className="flex flex-col gap-1">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Phone</span>
          <span className="text-sm text-slate-800 font-medium">{profileData.phoneNo || "—"}</span>
        </div>
        <div className="flex flex-col gap-1">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Roles</span>
          <div className="flex flex-wrap gap-1">
            {profileData.roles?.length > 0
              ? profileData.roles.map((role) => (
                  <span key={role} className="bg-blue-100 text-blue-700 text-xs font-semibold px-2 py-1 rounded-full">
                    {role}
                  </span>
                ))
              : <span className="text-sm text-slate-500">No roles</span>
            }
          </div>
        </div>
        <div className="flex flex-col gap-1">
          <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Status</span>
          <span className={`text-xs font-semibold px-2 py-1 rounded-full w-fit ${profileData.status ? "bg-emerald-100 text-emerald-700" : "bg-rose-100 text-rose-600"}`}>
            {profileData.status ? "Active" : "Inactive"}
          </span>
        </div>
      </>
    );
  };

  async function handleProfileClick() {
    if (showProfile) {
      setShowProfile(false);
      return;
    }
    setShowProfile(true);
    if (profileData) return; 

    setProfileLoading(true);
    try {
      const res = await axiosInstance.get("/user/update", {
        params: { username },
      });
      setProfileData(res.data);
    } catch {
      setProfileData(null);
    } finally {
      setProfileLoading(false);
    }
  }

  function handleLogout() {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    router.push("/Login");
  }

  const initials = username ? username.charAt(0).toUpperCase() : "U";

  return (
    <header className="flex items-center justify-between gap-4 border-b bg-white px-6 py-3">
      <div className="flex items-center gap-4">
        <div className="text-lg font-bold">POS</div>
        <p className="text-sm text-slate-500">Point of Sale Dashboard</p>
      </div>

      <div className="flex items-center gap-3 text-sm text-slate-600">
        <span className="hidden sm:block">{username}</span>

      
        <div className="relative" ref={dropdownRef}>
          <button
            onClick={handleProfileClick}
            className="w-9 h-9 rounded-full bg-blue-600 text-white font-bold text-sm flex items-center justify-center hover:bg-blue-700 transition"
          >
            {initials}
          </button>

          
          {showProfile && (
            <div className="fixed top-14 right-4 w-72 bg-white rounded-2xl shadow-xl border border-slate-200 z-50 overflow-hidden">
              {/* Header */}
              <div className="bg-blue-600 px-5 py-5 text-center">
                <div className="w-14 h-14 rounded-full bg-white flex items-center justify-center text-blue-600 font-black text-xl mx-auto mb-3">
                  {initials}
                </div>
                <p className="text-white font-bold text-sm">{profileData?.name || username}</p>
                <p className="text-blue-200 text-xs mt-1">{username}</p>
              </div>

          
              <div className="px-5 py-4 flex flex-col gap-3">
                {renderProfileContent()}
              </div>

              
              <div className="border-t border-slate-100 px-5 py-3">
                <button
                  onClick={handleLogout}
                  className="w-full rounded-xl bg-rose-500 py-2 text-sm font-semibold text-white hover:bg-rose-600 transition"
                >
                  Logout
                </button>
              </div>
            </div>
          )}
        </div>

       
        <button
          onClick={handleLogout}
          className="rounded-lg bg-red-500 px-4 py-2 text-sm font-semibold text-white hover:bg-red-600 transition"
        >
          Logout
        </button>
      </div>
    </header>
  );
}

export default Header;