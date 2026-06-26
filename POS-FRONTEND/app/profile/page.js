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

  const [isEditing, setIsEditing] = useState(false);
  const [editName, setEditName] = useState("");
  const [editPhone, setEditPhone] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState("");
  const [saveSuccess, setSaveSuccess] = useState("");

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

  function startEditing() {
    setEditName(user.name || "");
    setEditPhone(user.phoneNo || "");
    setFieldErrors({});
    setSaveError("");
    setSaveSuccess("");
    setIsEditing(true);
  }

  function cancelEditing() {
    setIsEditing(false);
    setFieldErrors({});
    setSaveError("");
  }

  function validate() {
    const errs = {};
    if (!editName.trim()) errs.name = "Full name is required.";
    if (editPhone.trim() && !/^\d{10}$/.test(editPhone.trim())) {
      errs.phone = "Enter a valid 10-digit phone number.";
    }
    setFieldErrors(errs);
    return Object.keys(errs).length === 0;
  }

  async function handleSaveProfile() {
    setSaveError("");
    setSaveSuccess("");
    if (!validate()) return;
    setSaving(true);
    try {
      const res = await api.put("/user/update", {
        id: user.id,
        username: user.username,
        name: editName.trim(),
        phoneNo: editPhone.trim(),
      });

      const updatedUser = res.data;

      if (updatedUser?.username) {
        setUser(updatedUser);
        setSaveSuccess("Profile updated successfully");
        setIsEditing(false);
      } else {
        setSaveError("Failed to update profile.");
      }
    } catch (err) {
      setSaveError(err?.response?.data?.message || "Unable to connect to server.");
    } finally {
      setSaving(false);
    }
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

            {!loading && !error && user && !isEditing && (
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

                {saveSuccess && (
                  <div className="bg-[#f0fff4] border border-solid border-[#9ae6b4] text-[#276749] rounded-lg p-2.5 text-center text-xs mb-4">
                    {saveSuccess}
                  </div>
                )}

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
                  className="w-full p-2.5 mt-5 bg-brand text-white border-none rounded-lg text-sm font-semibold cursor-pointer transition-colors hover:bg-brand-hover"
                  onClick={startEditing}
                >
                  Edit Profile
                </button>

                <button
                  className="w-full p-2.5 mt-2.5 bg-white text-red-700 border-[1.5px] border-solid border-red-200 rounded-lg text-sm font-semibold cursor-pointer transition-colors hover:bg-red-50"
                  onClick={handleLogout}
                >
                  Sign Out
                </button>
              </>
            )}

            {!loading && !error && user && isEditing && (
              <>
                <h1 className="text-lg font-bold text-[#1a1a1a] m-0 mb-1">Edit Profile</h1>
                <p className="text-xs text-gray-500 m-0 mb-5">Update your name and phone number</p>

                {saveError && (
                  <div className="bg-[#fff5f5] border border-solid border-red-200 text-red-700 rounded-lg p-2.5 text-center text-xs mb-4">
                    {saveError}
                  </div>
                )}

                <div className="flex flex-col gap-1.25 mb-3.5">
                  <label htmlFor="edit-name" className="text-xs font-semibold text-gray-600">Full Name</label>
                  <input
                    id="edit-name"
                    type="text"
                    value={editName}
                    onChange={(e) => {
                      setEditName(e.target.value);
                      if (fieldErrors.name) setFieldErrors((p) => ({ ...p, name: "" }));
                    }}
                    placeholder="Enter your full name"
                    className={`py-2 px-3 border-[1.5px] rounded-lg text-sm outline-none bg-[#fafaf8] box-border w-full transition-all focus:border-brand ${fieldErrors.name ? "border-[#e53e3e]" : "border-gray-300"}`}
                  />
                  {fieldErrors.name && <span className="text-[11px] text-[#e53e3e]">{fieldErrors.name}</span>}
                </div>

                <div className="flex flex-col gap-1.25 mb-5">
                  <label htmlFor="edit-phone" className="text-xs font-semibold text-gray-600">Phone</label>
                  <input
                    id="edit-phone"
                    type="text"
                    inputMode="numeric"
                    value={editPhone}
                    onChange={(e) => {
                      setEditPhone(e.target.value.replaceAll(/\D/g, "").slice(0, 10));
                      if (fieldErrors.phone) setFieldErrors((p) => ({ ...p, phone: "" }));
                    }}
                    placeholder="Enter your phone number"
                    className={`py-2 px-3 border-[1.5px] rounded-lg text-sm outline-none bg-[#fafaf8] box-border w-full transition-all focus:border-brand ${fieldErrors.phone ? "border-[#e53e3e]" : "border-gray-300"}`}
                  />
                  {fieldErrors.phone && <span className="text-[11px] text-[#e53e3e]">{fieldErrors.phone}</span>}
                </div>

                <div className="flex gap-3">
                  <button
                    type="button"
                    className="flex-1 p-2.5 bg-gray-100 text-gray-600 border-none rounded-lg text-sm font-semibold cursor-pointer transition-colors hover:bg-gray-200"
                    onClick={cancelEditing}
                  >
                    Cancel
                  </button>
                  <button
                    type="button"
                    className={`flex-1 p-2.5 text-white border-none rounded-lg text-sm font-semibold transition-all ${saving ? "bg-gray-400 cursor-not-allowed" : "bg-brand cursor-pointer hover:bg-brand-hover"}`}
                    disabled={saving}
                    onClick={handleSaveProfile}
                  >
                    {saving ? "Saving…" : "Save Changes"}
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}