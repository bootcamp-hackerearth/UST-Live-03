"use client";
 
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
 
const C = {
  navy: "#000000",
  mid: "#1a1a1a",
  light: "#333333",
  gray: "#e8e8e8",
  offWhite: "#f5f5f5",
  text: "#1a1a1a",
  muted: "#666666",
  white: "#ffffff",
  error: "#d32f2f",
  errorBg: "#ffebee",
};
 
const styles = {
  page: {
    position: "fixed",
    top: "60px",
    left: "220px",
    right: 0,
    bottom: 0,
    backgroundColor: "#ffffff",
    fontFamily: "'Segoe UI', sans-serif",
    display: "flex",
    flexDirection: "column",
    overflow: "hidden",
  },
 
  inner: {
    flex: 1,
    padding: "12px 18px",
    display: "flex",
    flexDirection: "column",
    overflow: "hidden",
  },
 
  topRow: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    marginBottom: "12px",
    flexShrink: 0,
    position: "relative",
  },
 
  backBtn: {
    padding: "7px 16px",
    backgroundColor: "transparent",
    color: C.mid,
    border: "none",
    borderRadius: "7px",
    fontSize: "12px",
    fontWeight: "600",
    cursor: "pointer",
    flexShrink: 0,
  },
 
  pageTitle: {
    position: "absolute",
    left: "50%",
    transform: "translateX(-50%)",
    margin: 0,
    fontSize: "18px",
    fontWeight: "700",
    color: C.navy,
    whiteSpace: "nowrap",
  },
 
  cardWrap: {
    flex: 1,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    overflow: "hidden",
  },
 
  card: {
    backgroundColor: C.white,
    borderRadius: "12px",
    padding: "20px 22px",
    width: "100%",
    maxWidth: "410px",
    boxShadow: "0 4px 20px rgba(54,57,85,0.1)",
    border: `1px solid ${C.gray}`,
    overflow: "hidden",
  },
 
  hero: {
    display: "flex",
    alignItems: "center",
    gap: "14px",
    marginBottom: "16px",
  },
 
  avatar: {
    width: "48px",
    height: "48px",
    borderRadius: "12px",
    background: `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
    color: "#fff",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "17px",
    fontWeight: "700",
    flexShrink: 0,
    boxShadow: `0 4px 12px rgba(54,57,85,0.3)`,
  },
 
  welcome: {
    fontSize: "17px",
    fontWeight: "700",
    color: C.navy,
    margin: "0 0 2px",
  },
 
  subtitle: {
    fontSize: "11px",
    color: C.muted,
    margin: 0,
  },
 
  divider: {
    height: "1px",
    backgroundColor: C.gray,
    margin: "0 0 14px",
  },
 
  infoRow: {
    display: "flex",
    alignItems: "flex-start",
    gap: "10px",
    marginBottom: "10px",
    padding: "8px 10px",
    borderRadius: "8px",
    backgroundColor: C.offWhite,
    border: `1px solid ${C.gray}`,
  },
 
  infoIcon: {
    fontSize: "14px",
    width: "20px",
    flexShrink: 0,
    paddingTop: "1px",
  },
 
  infoLabel: {
    fontSize: "9px",
    color: C.light,
    fontWeight: "700",
    textTransform: "uppercase",
    letterSpacing: "0.8px",
    marginBottom: "2px",
  },
 
  infoValue: {
    fontSize: "12px",
    color: C.text,
    fontWeight: "500",
    margin: 0,
  },
 
  rolesWrap: {
    display: "flex",
    flexWrap: "wrap",
    gap: "5px",
    marginTop: "3px",
  },
 
  roleChip: {
    padding: "3px 10px",
    borderRadius: "20px",
    backgroundColor: "rgba(84,102,142,0.1)",
    color: C.mid,
    fontSize: "10px",
    fontWeight: "600",
    border: `1px solid rgba(84,102,142,0.25)`,
  },
 
  logoutBtn: {
    width: "100%",
    padding: "9px",
    marginTop: "12px",
    backgroundColor: C.white,
    color: C.error,
    borderWidth: "1.5px",
    borderStyle: "solid",
    borderColor: "#f5c6c6",
    borderRadius: "8px",
    fontSize: "12px",
    fontWeight: "600",
    cursor: "pointer",
    letterSpacing: "0.2px",
  },
  logoutHover: {
    backgroundColor: "#ffe6e6",
    borderColor: "#f44336",
    color: "#b91c1c",
  },
 
  center: {
    textAlign: "center",
    padding: "20px 0",
    color: C.muted,
    fontSize: "14px",
  },
 
  errorBox: {
    backgroundColor: C.errorBg,
    border: "1px solid #f5c6c6",
    color: C.error,
    borderRadius: "8px",
    padding: "14px",
    textAlign: "center",
    fontSize: "14px",
  },
};

function getInitials(name) {
  if (!name) return "?";
  return name
    .split(" ")
    .map((w) => w[0])
    .slice(0, 2)
    .join("")
    .toUpperCase();
}

function getRoleLabel(role) {
  if (typeof role === "string") return role;
  if (!role) return "Unknown";
  return role.name || role.identifier || role.label || role.role || JSON.stringify(role);
}
 
export default function Profile() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [hoverSignout, setHoverSignout] = useState(false);
 
  const router = useRouter();
 
  useEffect(() => {
    async function fetchProfile() {
      const token = localStorage.getItem("token");
 
      if (!token) {
        router.push("/");
        return;
      }
 
      try {
        const res = await api.get("/user/profile");
        setUser(res.data);
      } catch (err) {
        if (
          err.response?.status === 401 ||
          err.response?.status === 403
        ) {
          localStorage.removeItem("token");
          router.push("/");
        } else {
          setError(
            "Could not load your profile. Please try again."
          );
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

  const rolesArray = (() => {
    if (!user) return [];
    if (Array.isArray(user.roles)) return user.roles;
    if (user.roles) return [user.roles];
    return [];
  })();

  return (
    <div style={styles.page}>
      <div style={styles.inner}>
        <div style={styles.topRow}>
          <button
            style={{ ...styles.backBtn, border: "none" }}
            onClick={() => router.push("/home")}
          >
            ⮜ Home
          </button>

          <h2 style={styles.pageTitle}>My Profile</h2>
        </div>

        <div style={styles.cardWrap}>
          <div style={styles.card}>
            {loading && (
              <p style={styles.center}>Loading your profile…</p>
            )}
 
            {!loading && error && (
              <div style={styles.errorBox}>
                {error}
              </div>
            )}
 
            {!loading && !error && user && (
              <>
                <div style={styles.hero}>
                  <div style={styles.avatar}>
                    {getInitials(user.name || user.username)}
                  </div>
 
                  <div>
                    <h1 style={styles.welcome}>
                      Hi, {user.name || user.username}
                    </h1>

                    <p style={styles.subtitle}>
                      Welcome back to your dashboard
                    </p>
                  </div>
                </div>
 
                <div style={styles.divider} />
 
                <div style={styles.infoRow}>
                  <div>
                    <p style={styles.infoLabel}>Username</p>
                    <p style={styles.infoValue}>{user.username}</p>
                  </div>
                </div>
 
                <div style={styles.infoRow}>
                  <div>
                    <p style={styles.infoLabel}>Full Name</p>
                    <p style={styles.infoValue}>{user.name}</p>
                  </div>
                </div>
 
                <div style={styles.infoRow}>
                  <div>
                    <p style={styles.infoLabel}>Phone</p>
                    <p style={styles.infoValue}>{user.phoneNo || "—"}</p>
                  </div>
                </div>
 
                <div style={styles.infoRow}>
                  <div>
                    <p style={styles.infoLabel}>Roles</p>

                    {rolesArray.length > 0 ? (
                      <div style={styles.rolesWrap}>
                        {rolesArray.map((role) => (
                          <span key={getRoleLabel(role)} style={styles.roleChip}>
                            {getRoleLabel(role)}
                          </span>
                        ))}
                      </div>
                    ) : (
                      <p style={styles.infoValue}>No roles assigned</p>
                    )}
                  </div>
                </div>
 
                <button
                  style={{ ...styles.logoutBtn, ...(hoverSignout ? styles.logoutHover : {}) }}
                  onClick={handleLogout}
                  onMouseEnter={() => setHoverSignout(true)}
                  onMouseLeave={() => setHoverSignout(false)}
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