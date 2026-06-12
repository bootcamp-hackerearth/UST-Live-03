"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import api from "@/api/axios";

const C = {
  navy:    "#363955",
  mid:     "#54668E",
  light:   "#879EC6",
  gray:    "#E8E8E8",
  text:    "#1e2235",
  muted:   "#6b7280",
  white:   "#ffffff",
  error:   "#c0392b",
  errorBg: "#fdf2f2",
};

function getInitials(name) {
  if (!name) return "?";
  return name.split(" ").map(w => w[0]).slice(0, 2).join("").toUpperCase();
}

export default function Profile() {
  const [user, setUser]       = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState("");
  const router                = useRouter();

  useEffect(() => {
    async function fetchProfile() {
      const token = localStorage.getItem("token");
      if (!token) { router.push("/"); return; }
      try {
        const res = await api.get("/user/profile");
        setUser(res.data);
      } catch (err) {
        if (err.response?.status === 401 || err.response?.status === 403) {
          localStorage.removeItem("token");
          router.push("/");
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
    router.push("/login");
  }

  return (
    <div style={{
      position: "fixed",
      top: "60px", left: "220px",
      right: 0, bottom: 0,
      backgroundColor: "#F0F1F5",
      fontFamily: "'Segoe UI', sans-serif",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      overflow: "hidden",
    }}>

      {loading && (
        <p style={{ color: C.muted, fontSize: "14px" }}>Loading your profile…</p>
      )}

      {!loading && error && (
        <div style={{
          backgroundColor: C.errorBg,
          border: "1px solid #f5c6c6",
          color: C.error, borderRadius: "10px",
          padding: "16px 20px", fontSize: "13px",
          maxWidth: "400px", textAlign: "center",
        }}>
          {error}
        </div>
      )}

      {!loading && !error && user && (
        <div style={{
          width: "100%",
          maxWidth: "420px",
          backgroundColor: C.white,
          borderRadius: "16px",
          border: `1px solid ${C.gray}`,
          boxShadow: "0 4px 24px rgba(54,57,85,0.10)",
          overflow: "hidden",
        }}>

          <div style={{
            background: `linear-gradient(135deg, ${C.navy} 0%, ${C.mid} 100%)`,
            padding: "18px 22px",
            display: "flex",
            alignItems: "center",
            gap: "14px",
          }}>
            <div style={{
              width: "46px", height: "46px",
              borderRadius: "12px",
              backgroundColor: "rgba(255,255,255,0.15)",
              border: "2px solid rgba(255,255,255,0.25)",
              display: "flex", alignItems: "center", justifyContent: "center",
              fontSize: "16px", fontWeight: "800",
              color: C.white, flexShrink: 0,
              letterSpacing: "-0.5px",
            }}>
              {getInitials(user.name)}
            </div>

            <div style={{ minWidth: 0 }}>
              <p style={{
                margin: "0 0 2px",
                fontSize: "15px", fontWeight: "700",
                color: C.white,
                overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap",
              }}>
                {user.name}
              </p>
              <p style={{ margin: 0, fontSize: "11px", color: "rgba(255,255,255,0.6)" }}>
                @{user.username}
              </p>
            </div>
          </div>

          <div style={{ padding: "18px 22px 22px" }}>

            <div style={{ display: "flex", gap: "10px", marginBottom: "10px" }}>
              <div style={{
                flex: 1, minWidth: 0,
                backgroundColor: "#f8f9fc",
                borderRadius: "10px",
                border: `1px solid ${C.gray}`,
                padding: "11px 13px",
              }}>
                <p style={{
                  margin: "0 0 4px", fontSize: "9px", fontWeight: "700",
                  color: C.light, textTransform: "uppercase", letterSpacing: "0.8px",
                }}>Username</p>
                <p style={{
                  margin: 0, fontSize: "13px", fontWeight: "600", color: C.text,
                  overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap",
                }}>
                  {user.username}
                </p>
              </div>

              <div style={{
                flex: 1, minWidth: 0,
                backgroundColor: "#f8f9fc",
                borderRadius: "10px",
                border: `1px solid ${C.gray}`,
                padding: "11px 13px",
              }}>
                <p style={{
                  margin: "0 0 4px", fontSize: "9px", fontWeight: "700",
                  color: C.light, textTransform: "uppercase", letterSpacing: "0.8px",
                }}>Phone</p>
                <p style={{ margin: 0, fontSize: "13px", fontWeight: "600", color: C.text }}>
                  {user.phoneNo || "—"}
                </p>
              </div>
            </div>

            <div style={{
              backgroundColor: "#f8f9fc",
              borderRadius: "10px",
              border: `1px solid ${C.gray}`,
              padding: "11px 13px",
              marginBottom: "10px",
            }}>
              <p style={{
                margin: "0 0 4px", fontSize: "9px", fontWeight: "700",
                color: C.light, textTransform: "uppercase", letterSpacing: "0.8px",
              }}>Full Name</p>
              <p style={{ margin: 0, fontSize: "13px", fontWeight: "600", color: C.text }}>
                {user.name}
              </p>
            </div>
            
            <div style={{
              backgroundColor: "#f8f9fc",
              borderRadius: "10px",
              border: `1px solid ${C.gray}`,
              padding: "11px 13px",
              marginBottom: "18px",
            }}>
              <p style={{
                margin: "0 0 8px", fontSize: "9px", fontWeight: "700",
                color: C.light, textTransform: "uppercase", letterSpacing: "0.8px",
              }}>Assigned Roles</p>
              {user.roles?.length > 0 ? (
                <div style={{ display: "flex", flexWrap: "wrap", gap: "6px" }}>
                  {user.roles.map((role) => (
                    <span key={role} style={{
                      padding: "3px 12px",
                      borderRadius: "20px",
                      backgroundColor: "rgba(84,102,142,0.09)",
                      color: C.mid,
                      fontSize: "11px", fontWeight: "600",
                      border: "1px solid rgba(84,102,142,0.2)",
                    }}>
                      {role}
                    </span>
                  ))}
                </div>
              ) : (
                <p style={{ margin: 0, fontSize: "13px", color: C.muted }}>No roles assigned</p>
              )}
            </div>

            <button
              onClick={handleLogout}
              style={{
                width: "100%",
                padding: "10px",
                backgroundColor: "#fff5f5",
                color: C.error,
                border: "1.5px solid #fecaca",
                borderRadius: "9px",
                fontSize: "13px", fontWeight: "600",
                cursor: "pointer",
              }}
            >
              Sign Out
            </button>
          </div>
        </div>
      )}
    </div>
  );
}