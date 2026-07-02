"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter } from "next/navigation";
import { fetchWithAuth } from "@/lib/api";
import { PATHS, ERROR_MESSAGES, STORAGE_KEYS } from "@/config/constants";

const getStorageItem = (key) => {
  try {
    if (globalThis.window?.localStorage) {
      return globalThis.window.localStorage.getItem(key);
    }
  } catch {
    return null;
  }
  return null;
};

function validateProfile({ name, phoneNo }) {
  if (!name?.trim()) return "Full name is required.";
  if (!phoneNo?.trim()) return "Phone number is required.";
  if (!/^\d{10}$/.test(phoneNo.trim()))
    return "Phone must be exactly 10 digits.";
  return null;
}

export default function ProfilePage() {
  const router = useRouter();
  const [user, setUser] = useState(null);
  const [form, setForm] = useState({ name: "", phoneNo: "" });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    const username = getStorageItem(STORAGE_KEYS.USERNAME) ?? null;
    if (!username) {
      router.replace(PATHS.LOGIN);
      return;
    }

    const fetchProfile = async () => {
      try {
        const data = await fetchWithAuth(`/api/user/${encodeURIComponent(username)}`);
        setUser(data);
        setForm({
          name: data?.name ?? "",
          phoneNo: data?.phoneNo ?? "",
        });
      } catch (err) {
        const errorMsg = err.message || ERROR_MESSAGES.SERVER_ERROR;
        setError(errorMsg);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [router]);

  const handleChange = useCallback((e) => {
    const { name, value } = e.target;
    if (name === "phoneNo") {
      const digits = value.replaceAll(/\D/g, "").slice(0, 10);
      setForm((prev) => ({ ...prev, [name]: digits }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value }));
    }
    setError("");
    setMessage("");
  }, []);

  const handleSave = useCallback(async (e) => {
    e.preventDefault();
    const validationError = validateProfile(form);
    if (validationError) {
      setError(validationError);
      return;
    }

    setSaving(true);
    setError("");
    setMessage("");

    try {
      const response = await fetchWithAuth(
        `/api/user/update/${encodeURIComponent(user.username)}`,
        {
          method: "PUT",
          body: JSON.stringify({
            id: user.id,
            username: user.username,
            name: form.name.trim(),
            phoneNo: form.phoneNo.trim(),
          }),
        },
      );

      setUser({
        ...user,
        name: response?.name ?? form.name.trim(),
        phoneNo: response?.phoneNo ?? form.phoneNo.trim(),
      });
      setMessage("Profile updated successfully.");
      setEditMode(false);
    } catch (err) {
      const errorMsg = err.message || ERROR_MESSAGES.SERVER_ERROR;
      setError(errorMsg);
    } finally {
      setSaving(false);
    }
  }, [form, user]);

  if (loading) {
    return (
      <>
        <style>{`
          @import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');
          .pr-root{font-family:'Barlow',sans-serif;min-height:calc(100vh - 56px);background:#f0f2f5;padding:24px}
          .pr-card{max-width:760px;margin:0 auto;padding:24px;background:#fff;border:1px solid #e6e6e6;border-radius:14px;box-shadow:0 10px 30px rgba(0,0,0,0.06)}
          .pr-loading{font-size:14px;color:#555;line-height:1.6}
        `}</style>
        <div className="pr-root">
          <div className="pr-card">
            <div className="pr-loading">Loading profile…</div>
          </div>
        </div>
      </>
    );
  }

  if (!user) return null;

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');
        .pr-root{font-family:'Barlow',sans-serif;min-height:calc(100vh - 56px);background:#f0f2f5;padding:24px}
        .pr-card{max-width:760px;margin:0 auto;padding:28px 26px 26px;background:#fff;border:1px solid #e6e6e6;border-radius:14px;box-shadow:0 10px 30px rgba(0,0,0,0.06)}
        .pr-header{display:flex;align-items:flex-start;gap:16px;flex-wrap:wrap;justify-content:space-between;margin-bottom:22px}
        .pr-title{font-size:22px;font-weight:800;color:#111;letter-spacing:-0.03em;margin-bottom:4px}
        .pr-sub{font-size:13px;color:#666;line-height:1.75}
        .pr-actions{display:flex;gap:10px;align-items:center;flex-wrap:wrap}
        .pr-alert{padding:12px 14px;border-radius:8px;font-size:13px;font-weight:600;line-height:1.5;margin-bottom:18px;color:#a02a2a;background:#fff0f3;border:1px solid #f0c7d3}
        .pr-alert-success{color:#0b5f34;background:#ecf7ed;border:1px solid #b8e3c7}
        .pr-grid{display:grid;grid-template-columns:repeat(2,minmax(220px,1fr));gap:18px}
        .pr-field{display:flex;flex-direction:column;gap:6px}
        .m-label{font-size:11px;font-weight:700;color:#444;text-transform:uppercase;letter-spacing:0.08em}
        .fi{width:100%;padding:11px 12px;font-family:'Barlow',sans-serif;font-size:14px;font-weight:500;color:#111;background:#fff;border:1.5px solid #ddd;border-radius:8px;outline:none;transition:border-color .15s,box-shadow .15s}
        .fi:focus{border-color:#005dab;box-shadow:0 0 0 3px rgba(0,93,171,0.1)}
        .fi.fi-err{border-color:#e31837;background:#fffafa}
        .fi.fi-ro{background:#f5f5f5;color:#888;cursor:not-allowed}
        .pr-form-actions{display:flex;justify-content:flex-end;margin-top:20px}
        .btn-save{padding:11px 18px;border:none;border-radius:8px;background:#e31837;color:#fff;font-family:'Barlow',sans-serif;font-size:13px;font-weight:800;cursor:pointer;transition:background .15s;text-transform:uppercase;letter-spacing:0.04em}
        .btn-save:hover:not(:disabled){background:#c0152a}
        .btn-save:disabled{opacity:.55;cursor:not-allowed}
        .pr-link{font-size:13px;color:#005dab;text-decoration:none;font-weight:700}
        .pr-link:hover{text-decoration:underline}
        @media(max-width:720px){.pr-grid{grid-template-columns:1fr}.pr-form-actions{justify-content:stretch}.pr-form-actions .btn-save{width:100%}}
      `}</style>
      <div className="pr-root">
        <div className="pr-card">
          <div className="pr-header">
            <div>
              <div className="pr-title">My Profile</div>
              <div className="pr-sub">
                Review your account details and edit your name or phone number.
              </div>
            </div>
            <div className="pr-actions">
              <button
                type="button"
                className="btn-save"
                onClick={() => {
                  setEditMode((prev) => !prev);
                  setError("");
                  setMessage("");
                  setForm({
                    name: user.name ?? "",
                    phoneNo: user.phoneNo ?? "",
                  });
                }}
              >
                {editMode ? "Cancel" : "Edit Profile"}
              </button>
            </div>
          </div>
          {message && (
            <div className="pr-alert pr-alert-success">{message}</div>
          )}
          {error && <div className="pr-alert">{error}</div>}
          <form onSubmit={handleSave} noValidate>
            <div className="pr-grid">
              <div className="pr-field">
                <label className="m-label" htmlFor="profile-email">
                  Email
                </label>
                <input
                  id="profile-email"
                  className="fi fi-ro"
                  type="email"
                  value={user.username || ""}
                  disabled
                />
              </div>
              <div className="pr-field">
                <label className="m-label" htmlFor="profile-name">
                  Full Name
                </label>
                <input
                  id="profile-name"
                  className={`fi${editMode ? "" : " fi-ro"}`}
                  name="name"
                  type="text"
                  value={form.name}
                  onChange={handleChange}
                  disabled={!editMode}
                  placeholder="Enter your full name"
                />
              </div>
              <div className="pr-field">
                <label className="m-label" htmlFor="profile-phone">
                  Phone
                </label>
                <input
                  id="profile-phone"
                  className={`fi${editMode ? "" : " fi-ro"}`}
                  name="phoneNo"
                  type="tel"
                  value={form.phoneNo}
                  onChange={handleChange}
                  disabled={!editMode}
                  placeholder="10 digits"
                  maxLength={10}
                />
              </div>
            </div>
            {editMode && (
              <div className="pr-form-actions">
                <button className="btn-save" type="submit" disabled={saving}>
                  {saving ? "Saving…" : "Save Changes"}
                </button>
              </div>
            )}
          </form>
        </div>
      </div>
    </>
  );
}