"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import axiosInstance from "../api/axiosInstance";

const DISABLED_INPUT_CLS =
  "cursor-not-allowed rounded-lg border border-slate-200 bg-slate-100 px-4 py-2.5 text-sm font-medium text-slate-500 outline-none";

const ProfileField = ({ label, id, children }) => (
  <div className="flex flex-col gap-1.5">
    <label htmlFor={id} className="text-sm font-bold text-slate-700">
      {label}
    </label>
    {children}
  </div>
);

ProfileField.propTypes = {
  label: PropTypes.string.isRequired,
  id: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
};

export default function ProfilePage() {
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [profile, setProfile] = useState({ name: "", phoneNo: "", roles: [] });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const storedUsername = localStorage.getItem("username");
    if (!storedUsername) { router.push("/login"); return; }
    queueMicrotask(() => setUsername(storedUsername));
    const fetchProfile = async () => {
      try {
        const response = await axiosInstance.get("/user/identifier", {
          params: { username: storedUsername },
        });
        const data = response.data || {};
        setProfile({
          name: data.name || "",
          phoneNo: data.phoneNo || "",
          roles: Array.isArray(data.roles) ? data.roles : [],
        });
      } catch (err) {
        setError(err.response?.data?.message || err.message || "Unable to load profile.");
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [router]);

  if (loading) {
    return (
      <div className="mx-auto flex w-full max-w-2xl items-center justify-center py-20">
        <div className="text-sm font-semibold text-slate-500">Loading profile...</div>
      </div>
    );
  }

  return (
    <div className="mx-auto w-full max-w-2xl">
      <div className="mb-6">
        <h2 className="text-2xl font-black tracking-tight text-slate-950">My Profile</h2>
        <p className="mt-1 text-sm font-medium text-slate-500">View your account information.</p>
      </div>
      <div className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm md:p-8">
        {error && (
          <div className="mb-5 rounded-lg border border-rose-100 bg-rose-50 px-4 py-3 text-sm font-bold text-rose-600">
            {error}
          </div>
        )}
        <div className="grid gap-5 md:grid-cols-2">
          <ProfileField label="Username" id="username">
            <input id="username" type="text" value={username} disabled className={DISABLED_INPUT_CLS} />
          </ProfileField>

          <ProfileField label="Full Name" id="name">
            <input id="name" type="text" value={profile.name} disabled className={DISABLED_INPUT_CLS} />
          </ProfileField>

          <div className="flex flex-col gap-1.5 md:col-span-2">
            <label htmlFor="phoneNo" className="text-sm font-bold text-slate-700">
              Phone Number
            </label>
            <input id="phoneNo" type="text" value={profile.phoneNo} disabled className={DISABLED_INPUT_CLS} />
          </div>

          <div className="md:col-span-2">
            <label htmlFor="roles" className="text-sm font-bold text-slate-700">
              Roles
            </label>
            <div
              id="roles"
              className="mt-1.5 rounded-lg border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm font-medium text-slate-700"
            >
              {profile.roles.length > 0 ? profile.roles.join(", ") : "No roles assigned"}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
