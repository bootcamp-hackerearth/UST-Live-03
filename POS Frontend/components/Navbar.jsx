"use client";
 
import { useState, useEffect, useRef } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import api from "@/api/axios";
 
function getRoleLabel(role) {
  if (!role) return "Unknown";
  if (typeof role === "string") return role;
  return role.name || role.identifier || role.label || role.role || JSON.stringify(role);
}

export default function Navbar() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [displayName, setDisplayName] = useState("");
  const [profileOpen, setProfileOpen] = useState(false);
  const [profileData, setProfileData] = useState(null);
  const [profileLoading, setProfileLoading] = useState(false);
  const [profileError, setProfileError] = useState("");
  const pathname = usePathname();
  const panelRef = useRef(null);

  const initialLetter = displayName ? displayName.charAt(0).toUpperCase() : "";

  function toggleProfilePanel() {
    setProfileOpen((open) => !open);
  }
 
  useEffect(() => {
    setIsLoggedIn(!!localStorage.getItem("token"));
  }, [pathname]); 

  async function loadProfile() {
    setProfileError("");
    setProfileLoading(true);
    try {
      const res = await api.get("/user/profile");
      const profile = res.data;
      const username = profile?.username || profile?.name || "";
      setDisplayName((username || "Account").split(" ")[0]);
      setProfileData(profile);
    } catch {
      setProfileError("Unable to load profile details.");
    } finally {
      setProfileLoading(false);
    }
  }

  useEffect(() => {
    if (isLoggedIn && !displayName) {
      loadProfile();
    } else if (!isLoggedIn) {
      setDisplayName("");
      setProfileData(null);
    }
  }, [isLoggedIn, displayName]);

  useEffect(() => {
    if (isLoggedIn && profileOpen) {
      loadProfile();
    }
  }, [isLoggedIn, profileOpen]);

  let profileContent;
  if (profileLoading) {
    profileContent = <p style={{ margin: 0, color: "#444", fontSize: "14px" }}>Loading profile…</p>;
  } else if (profileError) {
    profileContent = <div style={{ color: "#b91c1c", fontSize: "13px" }}>{profileError}</div>;
  } else {
    profileContent = (
      <>
        <div style={{ marginBottom: "12px" }}>
          <label htmlFor="username" style={{ display: "block", marginBottom: "6px", fontSize: "12px", color: "#444", fontWeight: 600 }}>Username</label>
          <input
            id="username"
            type="text"
            value={profileData?.username || ""}
            disabled
            style={{
              width: "100%",
              padding: "10px 12px",
              border: "1px solid #d1d5db",
              borderRadius: "8px",
              backgroundColor: "#f5f5f5",
              color: "#6b7280",
              fontSize: "14px",
            }}
          />
        </div>
        <div style={{ marginBottom: "12px" }}>
          <div style={{ display: "block", marginBottom: "6px", fontSize: "12px", color: "#444", fontWeight: 600 }}>Full Name</div>
          <div style={{ margin: 0, padding: "10px 12px", fontSize: "14px", color: "#111" }} aria-label="Full Name">{profileData?.name || "—"}</div>
        </div>
        <div style={{ marginBottom: "16px" }}>
          <div style={{ display: "block", marginBottom: "6px", fontSize: "12px", color: "#444", fontWeight: 600 }}>Phone</div>
          <div style={{ margin: 0, padding: "10px 12px", fontSize: "14px", color: "#111" }} aria-label="Phone">{profileData?.phoneNo || "—"}</div>
        </div>
        {profileData?.roles ? (
          <div style={{ marginBottom: "16px" }}>
            <div style={{ margin: 0, fontSize: "12px", color: "#444", fontWeight: 600 }}>Roles</div>
            <div style={{ display: "flex", flexWrap: "wrap", gap: "8px", marginTop: "8px" }}>
              {(Array.isArray(profileData.roles) ? profileData.roles : [profileData.roles]).map((role) => (
                <span
                  key={getRoleLabel(role)}
                  style={{
                    padding: "6px 10px",
                    borderRadius: "999px",
                    backgroundColor: "#f3f4f6",
                    color: "#111",
                    fontSize: "12px",
                    fontWeight: "600",
                  }}
                >
                  {getRoleLabel(role)}
                </span>
              ))}
            </div>
          </div>
        ) : null}
      </>
    );
  }

  useEffect(() => {
    function handleClickOutside(event) {
      if (
        profileOpen &&
        panelRef.current &&
        !panelRef.current.contains(event.target) &&
        !event.target.closest(".profile-toggle-btn")
      ) {
        setProfileOpen(false);
      }
    }

    globalThis.addEventListener("mousedown", handleClickOutside);
    return () => globalThis.removeEventListener("mousedown", handleClickOutside);
  }, [profileOpen]);
 
  return (
    <>
      <nav style={{
      position: "fixed", top: 0, left: 0, right: 0,
      height: "60px",
      background: "#111111",
      display: "flex", alignItems: "center",
      padding: "0 28px", zIndex: 200,
      boxShadow: "0 4px 12px rgba(0,0,0,0.2)",
      fontFamily: "'Segoe UI', sans-serif",
    }}>
      <div style={{
        width: "100%", display: "flex",
        alignItems: "center", justifyContent: "space-between",
      }}>

        <div style={{
  display: "flex", alignItems: "center",
  gap: "10px",
}}>
  <div style={{
    width: "34px", height: "34px", borderRadius: "8px",
    background: "rgba(255,255,255,0.1)",
    border: "1px solid rgba(255,255,255,0.25)",
    display: "flex", alignItems: "center",
    justifyContent: "center", fontSize: "17px",
    transition: "all 0.2s ease",
  }}>🛒</div>
  <div>
    <div style={{
      fontSize: "16px", fontWeight: "700",
      color: "#fff", letterSpacing: "0.3px", lineHeight: 1.1,
    }}>RetailPOS</div>
    <div style={{
      fontSize: "9px", color: "rgba(135,158,198,0.85)",
      letterSpacing: "1.5px", textTransform: "uppercase",
    }}>Management System</div>
  </div>
</div>
 
        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          {isLoggedIn ? (
            <button
              type="button"
              className="profile-toggle-btn"
              onClick={toggleProfilePanel}
              style={{
                display: "flex", alignItems: "center", gap: "7px",
                padding: "6px 10px", borderRadius: "6px",
                background: "transparent",
                border: "none",
                fontSize: "13px", fontWeight: "500",
                color: "rgba(255,255,255,0.95)",
                cursor: "pointer",
                transition: "all 0.2s ease",
              }}
            >
              <span style={{
                display: "inline-flex",
                alignItems: "center",
                justifyContent: "center",
                width: "36px",
                height: "36px",
                borderRadius: "50%",
                backgroundColor: "rgba(255,255,255,0.25)",
                border: "2px solid rgba(255,255,255,0.5)",
                fontSize: "14px", fontWeight: 500,
                color: "#fff",
                lineHeight: 1,
              }}>{initialLetter}</span>
            </button>
          ) : (
            <>
              <Link href="/login" style={{
                padding: "6px 14px", borderRadius: "6px",
                fontSize: "13px", color: "#dcdcdc",
                textDecoration: "none",
              }}>
                Login
              </Link>
              <Link href="/register" style={{
                padding: "6px 16px", borderRadius: "6px",
                backgroundColor: "#ffffff", color: "#000000",
                fontSize: "13px", fontWeight: "700",
                textDecoration: "none",
                transition: "all 0.2s ease",
              }}>
                Register
              </Link>
            </>
          )}
        </div>
 
      </div>
    </nav>

      {profileOpen && (
        <div
          ref={panelRef}
          style={{
            position: "fixed",
            top: "70px",
            right: "20px",
            width: "320px",
            backgroundColor: "#ffffff",
            borderRadius: "14px",
            boxShadow: "0 16px 40px rgba(0,0,0,0.16)",
            border: "1px solid rgba(0,0,0,0.08)",
            padding: "20px",
            zIndex: 210,
            animation: "slideInRight 0.22s ease",
          }}
        >
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "16px" }}>
            <div>
              <p style={{ margin: 0, fontSize: "16px", fontWeight: "700", color: "#111" }}>My Profile</p>
              <p style={{ margin: "6px 0 0", fontSize: "12px", color: "#666" }}>Editable profile panel</p>
            </div>
            <button
              type="button"
              onClick={() => setProfileOpen(false)}
              style={{
                border: "none",
                background: "transparent",
                color: "#666",
                fontSize: "18px",
                cursor: "pointer",
              }}
            >×</button>
          </div>

          {profileContent}
        </div>
      )}
    </>
  );
}