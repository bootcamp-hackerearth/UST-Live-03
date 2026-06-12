
"use client";

import { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import Layout from "@/app/components/Layout";
import {
  User, Mail, Phone, Shield, Clock, Store,
  Pencil, X, Check, AlertCircle, CheckCircle,
  ArrowLeft, Loader2, KeyRound,
} from "lucide-react";


export default function ProfilePage() {
  const router = useRouter();
  const username = globalThis.window === undefined ? null : localStorage.getItem("username");



  if (!username) {
    if (globalThis.window !== undefined) {
      router.push("/login");
    }
    return null;
  }

  return (
    <Layout username={username}>
      <ProfileContent username={username} />
    </Layout>
  );
}



function ProfileContent({ username }) {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({});
  const [feedback, setFeedback] = useState(null); 
  const router = useRouter();

  
  const fetchProfile = useCallback(async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(
        `http://localhost:8080/api/user/get?username=${encodeURIComponent(username)}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      if (!res.ok) throw new Error("Failed to load profile");
      const data = await res.json();
      setProfile(data);
      setForm({
        name: data.name || "",
        username: data.username || "",
        phoneNo: data.phoneNo || "",
      });
    } catch (err) {
      setFeedback({ type: "error", msg: err.message || "Could not load profile." });
    } finally {
      setLoading(false);
    }
  }, [username]);

  useEffect(() => { fetchProfile(); }, [fetchProfile]);


  const handleSave = async () => {
    if (!form.name?.trim()) return setFeedback({ type: "error", msg: "Name is required." });
    if (form.phoneNo && !/^\d{10}$/.test(form.phoneNo))
      return setFeedback({ type: "error", msg: "Phone must be exactly 10 digits." });

    setSaving(true);
    setFeedback(null);
    try {
      const token = localStorage.getItem("token");
      const res = await fetch("http://localhost:8080/api/user/update", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ ...profile, ...form }),
      });
      if (!res.ok) throw new Error("Update failed");
      const updated = await res.json();
      setProfile(updated);
      setEditing(false);
      setFeedback({ type: "success", msg: "Profile updated successfully." });
    } catch (err) {
      setFeedback({ type: "error", msg: err.message || "Update failed. Try again." });
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    setForm({ name: profile?.name || "", username: profile?.username || "", phoneNo: profile?.phoneNo || "" });
    setEditing(false);
    setFeedback(null);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="flex flex-col items-center gap-3 text-slate-400">
          <Loader2 size={28} className="animate-spin text-blue-500" />
          <p className="text-sm font-medium">Loading profile…</p>
        </div>
      </div>
    );
  }

  const usernameParts = username?.split("@");
  const fallbackName = usernameParts && usernameParts.length > 0 ? usernameParts[0] : "Operator";
  const displayName = profile?.name || fallbackName;
  const initials = displayName.slice(0, 2).toUpperCase();
  const roles = profile?.roles || [];

  return (
    <div className="w-full space-y-5">

      <div className="flex items-center gap-3">
        <button
          onClick={() => router.push("/home")}
          className="flex items-center gap-1.5 text-xs font-medium text-slate-500 hover:text-blue-600 transition-colors"
        >
          <ArrowLeft size={14} />
          Back to Home
        </button>
      </div>

      {feedback && (
        <div className={`flex items-start gap-3 text-xs rounded-xl p-3.5 border ${
          feedback.type === "success"
            ? "bg-emerald-50 border-emerald-100 text-emerald-700"
            : "bg-red-50 border-red-100 text-red-700"
        }`}>
          {feedback.type === "success" ? (
            <CheckCircle size={15} className="shrink-0 mt-0.5" />
          ) : (
            <AlertCircle size={15} className="shrink-0 mt-0.5" />
          )}
          <span>{feedback.msg}</span>
          <button className="ml-auto" onClick={() => setFeedback(null)}><X size={13} /></button>
        </div>
      )}

      <div className="relative overflow-hidden rounded-2xl bg-gradient-to-br from-blue-600 via-blue-500 to-indigo-600 p-6 text-white shadow-lg shadow-blue-200">
        <div className="absolute inset-0 bg-[linear-gradient(to_right,#ffffff08_1px,transparent_1px),linear-gradient(to_bottom,#ffffff08_1px,transparent_1px)] bg-[size:28px_28px]" />
        <div className="absolute -top-8 -right-8 w-40 h-40 rounded-full bg-white/5 blur-2xl" />

        <div className="relative z-10 flex items-center gap-5">
          <div className="w-16 h-16 rounded-2xl bg-white/20 border-2 border-white/30 flex items-center justify-center text-xl font-bold text-white backdrop-blur-sm flex-shrink-0">
            {initials}
          </div>

          <div className="flex-1 min-w-0">
            <h1 className="text-xl font-bold tracking-tight">{displayName}</h1>
            <p className="text-blue-100/80 text-sm mt-0.5">{profile?.username || username}</p>

            <div className="flex flex-wrap gap-1.5 mt-2">
              {roles.length > 0 ? roles.map((role) => (
                <span key={role} className="text-[10px] font-semibold uppercase tracking-wide bg-white/15 border border-white/20 px-2 py-0.5 rounded-full text-white/90">
                  {role}
                </span>
              )) : (
                <span className="text-[10px] font-semibold uppercase tracking-wide bg-white/15 border border-white/20 px-2 py-0.5 rounded-full text-white/90">
                  Operator
                </span>
              )}
            </div>
          </div>

          
          {editing ? (
            <div className="flex items-center gap-2 flex-shrink-0">
              <button
                onClick={handleCancel}
                className="flex items-center gap-1.5 bg-white/10 hover:bg-white/20 border border-white/20 text-white/80 text-xs font-semibold px-3 py-2 rounded-xl transition-colors"
              >
                <X size={13} />
                Cancel
              </button>
              <button
                onClick={handleSave}
                disabled={saving}
                className="flex items-center gap-1.5 bg-white text-blue-600 hover:bg-blue-50 text-xs font-bold px-3 py-2 rounded-xl transition-colors disabled:opacity-60"
              >
                {saving ? <Loader2 size={13} className="animate-spin" /> : <Check size={13} />}
                Save
              </button>
            </div>
          ) : (
            <button
              onClick={() => { setEditing(true); setFeedback(null); }}
              className="flex items-center gap-1.5 bg-white/15 hover:bg-white/25 border border-white/20 text-white text-xs font-semibold px-3 py-2 rounded-xl transition-colors flex-shrink-0"
            >
              <Pencil size={13} />
              Edit Profile
            </button>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">

        <div className="bg-white rounded-2xl border border-slate-200 overflow-hidden">
          <div className="px-5 py-4 border-b border-slate-100 flex items-center gap-2">
            <div className="w-7 h-7 rounded-lg bg-blue-50 border border-blue-100 flex items-center justify-center">
              <User size={13} className="text-blue-500" />
            </div>
            <h2 className="text-sm font-bold text-slate-800">Personal Information</h2>
          </div>

          <div className="p-5 space-y-4">
            <ProfileField
              label="Full Name"
              value={profile?.name}
              icon={<User size={13} className="text-slate-400" />}
              editing={editing}
              inputValue={form.name || ""}
              onChange={(v) => setForm((f) => ({ ...f, name: v }))}
              placeholder="Enter full name"
            />

            <ProfileField
              label="Email Address"
              value={profile?.username}
              icon={<Mail size={13} className="text-slate-400" />}
              editing={false}
              inputValue={form.username || ""}
              onChange={(v) => setForm((f) => ({ ...f, username: v }))}
              placeholder="Email address"
              readonlyHint="Login identifier — cannot be changed"
            />
            <ProfileField
              label="Phone Number"
              value={profile?.phoneNo}
              icon={<Phone size={13} className="text-slate-400" />}
              editing={editing}
              inputValue={form.phoneNo || ""}
              onChange={(v) => setForm((f) => ({ ...f, phoneNo: v }))}
              placeholder="10-digit phone number"
              inputType="tel"
            />
          </div>
        </div>

        <div className="bg-white rounded-2xl border border-slate-200 overflow-hidden">
          <div className="px-5 py-4 border-b border-slate-100 flex items-center gap-2">
            <div className="w-7 h-7 rounded-lg bg-violet-50 border border-violet-100 flex items-center justify-center">
              <Shield size={13} className="text-violet-500" />
            </div>
            <h2 className="text-sm font-bold text-slate-800">Account & Access</h2>
          </div>

          <div className="p-5 space-y-4">

            <div>
              <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-2 flex items-center gap-1.5">
                <KeyRound size={11} />
                Assigned Roles
              </p>
              <div className="flex flex-wrap gap-2">
                {roles.length > 0 ? roles.map((role) => (
                  <span key={role} className="inline-flex items-center gap-1.5 text-xs font-semibold bg-violet-50 text-violet-700 border border-violet-100 px-2.5 py-1 rounded-lg">
                    <Shield size={10} />
                    {role}
                  </span>
                )) : (
                  <span className="inline-flex items-center gap-1.5 text-xs font-semibold bg-slate-50 text-slate-600 border border-slate-200 px-2.5 py-1 rounded-lg">
                    <Shield size={10} />
                    Operator
                  </span>
                )}
              </div>
              <p className="text-[10px] text-slate-400 mt-2">Role assignments are managed by administrators.</p>
            </div>

            {profile?.lastLogin && (
              <div>
                <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-1.5 flex items-center gap-1.5">
                  <Clock size={11} />
                  Last Login
                </p>
                <p className="text-sm font-medium text-slate-700">{profile.lastLogin}</p>
              </div>
            )}

            {profile?.store && (
              <div>
                <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-1.5 flex items-center gap-1.5">
                  <Store size={11} />
                  Assigned Store
                </p>
                <p className="text-sm font-medium text-slate-700">{profile.store}</p>
              </div>
            )}

            <div>
              <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-1.5">Status</p>
              <span className="inline-flex items-center gap-1.5 text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-100 px-2.5 py-1 rounded-lg">
                <span className="w-1.5 h-1.5 rounded-full bg-emerald-500" />{''}
                Active
              </span>
            </div>

          </div>
        </div>

      </div>
    </div>
  );
}

ProfileContent.propTypes = {
  username: PropTypes.string.isRequired,
};

function ProfileField({ label, value, icon, editing, inputValue, onChange, placeholder, inputType = "text", readonlyHint }) {
  return (
    <div>
      <p className="text-[10px] font-bold uppercase tracking-widest text-slate-400 mb-1.5 flex items-center gap-1.5">
        {icon}
        {label}
      </p>

      {editing && !readonlyHint ? (
        <input
          type={inputType}
          value={inputValue}
          onChange={(e) => onChange(e.target.value)}
          placeholder={placeholder}
          className="w-full px-3 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm text-slate-800 placeholder:text-slate-400 focus:bg-white focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10 outline-none transition-all"
        />
      ) : (
        <div>
          <p className="text-sm font-medium text-slate-700">{value || <span className="text-slate-400 italic">Not set</span>}</p>
          {readonlyHint && (
            <p className="text-[10px] text-slate-400 mt-0.5">{readonlyHint}</p>
          )}
        </div>
      )}
    </div>
  );
}

ProfileField.propTypes = {
  label: PropTypes.string.isRequired,
  value: PropTypes.string,
  icon: PropTypes.node.isRequired,
  editing: PropTypes.bool.isRequired,
  inputValue: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  inputType: PropTypes.string,
  readonlyHint: PropTypes.string,
};
