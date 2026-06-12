"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import api from "../components/Axios";

function getInitials(name) {
  if (!name) return "?";
  return name
    .split(" ")
    .map((w) => w[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();
}

export default function Profile() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const router = useRouter();

  useEffect(() => {
    async function fetchProfile() {
      const token = localStorage.getItem("token");
      if (!token) {
        router.push("/login");
        return;
      }
      try {
        const res = await api.get("/user/api/profile");
        setUser(res.data);
      } catch (err) {
        if (err.response?.status === 401 || err.response?.status === 403) {
          localStorage.removeItem("token");
          router.push("/login");
        } else {
          setError("Could not load your profile. Please try again.");
        }
      } finally {
        setLoading(false);
      }
    }
    fetchProfile();
  }, [router]);

  function handleLogout() {
    localStorage.removeItem("token");
    router.push("/");
  }

  return (
    <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f9fafb] font-sans flex flex-col overflow-hidden">
      <div className="flex-1 p-5 px-6 flex flex-col overflow-hidden">
        <div className="flex items-center gap-3 mb-4 shrink-0 relative">
          <button 
            className="py-2 px-4 bg-transparent text-brand border-[1.5px] border-solid border-brand rounded-lg text-xs font-semibold shrink-0 cursor-pointer transition-colors hover:bg-emerald-50" 
            onClick={() => router.push("/home")}
          >
            &larr; Home
          </button>
          <h2 className="absolute left-1/2 -translate-x-1/2 m-0 text-xl font-bold text-brand whitespace-nowrap">
            My Profile
          </h2>
        </div>

        <div className="flex-1 flex items-center justify-center overflow-hidden">
          <div className="bg-white rounded-xl py-8 px-9 w-full max-w-[460px] shadow-[0_4px_20px_rgba(0,0,0,0.08)] max-h-full overflow-y-auto">
            {loading && <p className="text-center py-5 text-gray-500 text-sm">Loading your profile…</p>}
            {!loading && error && <div className="bg-[#fff5f5] border border-solid border-red-200 text-red-700 rounded-lg p-3.5 text-center text-sm">{error}</div>}

            {!loading && !error && user && (
              <>
                <div className="flex items-center gap-4 mb-5.5">
                  <div className="w-13 h-13 rounded-full bg-brand text-white flex items-center justify-center text-lg font-bold shrink-0">
                    {getInitials(user.name)}
                  </div>
                  <div>
                    <h1 className="text-lg font-bold text-[#1a1a1a] m-0 mb-1">Hi, {user.name} 👋</h1>
                    <p className="text-xs text-gray-500 m-0">Welcome back to your dashboard</p>
                  </div>
                </div>

                <div className="h-px bg-gray-100 mb-4.5" />

                <div className="flex items-start gap-3.5 mb-3.5">
                  <span className="text-sm w-6 shrink-0 pt-0.5">📧</span>
                  <div>
                    <p className="text-[11px] text-gray-400 font-bold uppercase tracking-wide mb-0.5">Username</p>
                    <p className="text-sm text-[#1a1a1a] font-medium m-0">{user.username}</p>
                  </div>
                </div>

                <div className="flex items-start gap-3.5 mb-3.5">
                  <span className="text-sm w-6 shrink-0 pt-0.5">👤</span>
                  <div>
                    <p className="text-[11px] text-gray-400 font-bold uppercase tracking-wide mb-0.5">Full Name</p>
                    <p className="text-sm text-[#1a1a1a] font-medium m-0">{user.name}</p>
                  </div>
                </div>

                <div className="flex items-start gap-3.5 mb-3.5">
                  <span className="text-sm w-6 shrink-0 pt-0.5">📞</span>
                  <div>
                    <p className="text-[11px] text-gray-400 font-bold uppercase tracking-wide mb-0.5">Phone</p>
                    <p className="text-sm text-[#1a1a1a] font-medium m-0">{user.phoneNo || "—"}</p>
                  </div>
                </div>

                <div className="flex items-start gap-3.5 mb-3.5">
                  <span className="text-sm w-6 shrink-0 pt-0.5">🔰</span>
                  <div>
                    <p className="text-[11px] text-gray-400 font-bold uppercase tracking-wide mb-0.5">Roles</p>
                    {user.roles?.length > 0 ? (
                      <div className="flex flex-wrap gap-1.5 mt-1">
                        {user.roles.map((role) => (
                          <span key={role} className="py-0.5 px-3 rounded-2xl bg-[#e8f5e9] text-brand text-xs font-semibold border border-solid border-[#a5d6a7]">
                            {role}
                          </span>
                        ))}
                      </div>
                    ) : (
                      <p className="text-sm text-[#1a1a1a] font-medium m-0">No roles assigned</p>
                    )}
                  </div>
                </div>

                <button 
                  className="w-full p-2.5 mt-5 bg-white text-red-700 border-[1.5px] border-solid border-red-200 rounded-lg text-sm font-semibold cursor-pointer transition-colors hover:bg-red-50" 
                  onClick={handleLogout}
                >
                  Sign Out
                </button>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}