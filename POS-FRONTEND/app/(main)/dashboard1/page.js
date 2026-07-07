"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

const Dashboard1 = () => {
  const router = useRouter();

  const [user, setUser] = useState({
    name: "Admin",
    role: "Administrator",
  });

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const token = localStorage.getItem("token");
        const username = localStorage.getItem("username");

        if (!token || !username) {
          router.push("/login");
          return;
        }

        const res = await axios.get(
          `http://localhost:8080/api/user/get?username=${username}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        setUser({
          name: res.data?.name || username,
          role: res.data?.role || "Administrator",
        });
      } catch (err) {
        console.error("USER FETCH ERROR:", err);

        const username = localStorage.getItem("username");

        setUser({
          name: username || "Admin",
          role: "Administrator",
        });
      }
    };

    fetchUser();
  }, [router]);

  return (
    <div style={styles.main}>
      {/* TOPBAR */}
      <div style={styles.topbar}>
        <div>
          <h1 style={styles.pageTitle}>Dashboard</h1>
          <p style={styles.pageSubtitle}>Welcome back</p>
        </div>

        {/* PROFILE */}
        <button
          style={styles.profile}
          onClick={() => router.push("/user/profile")}
          aria-label="Go to user profile"
        >
          <div style={styles.avatar}>
            {user.name?.charAt(0)?.toUpperCase() || "A"}
          </div>

          <div>
            <h4 style={styles.profileName}>
              {user.name}
            </h4>

            <p style={styles.profileRole}>
              {user.role}
            </p>
          </div>
        </button>
      </div>

      {/* HERO SECTION */}
      <div style={styles.heroCard}>
        <h2 style={styles.heroTitle}>
          Manage your POS business
        </h2>

        <p style={styles.heroText}>
          Track sales, orders and users in one place.
        </p>
      </div>
    </div>
  );
};

export default Dashboard1;

/* ================= STYLES ================= */

const styles = {
  main: {
    minHeight: "100vh",
    padding: "30px",
    background: "#f8fafc",
    fontFamily: "Inter, sans-serif",
  },

  topbar: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "28px",
    gap: "20px",
    flexWrap: "wrap",
  },

  pageTitle: {
    margin: 0,
    fontSize: "42px",
    color: "#111827",
    fontWeight: "800",
  },

  pageSubtitle: {
    marginTop: "6px",
    color: "#6b7280",
    fontSize: "16px",
  },

  profile: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    background: "#fff",
    padding: "12px 16px",
    borderRadius: "16px",
    boxShadow: "0 4px 18px rgba(0,0,0,0.05)",
    cursor: "pointer",
    border: "none",
  },

  avatar: {
    width: "50px",
    height: "50px",
    borderRadius: "50%",
    background: "linear-gradient(135deg, #00205b, #003380)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    color: "#fff",
    fontWeight: "700",
    fontSize: "20px",
  },

  profileName: {
    margin: 0,
    fontSize: "15px",
    color: "#111827",
  },

  profileRole: {
    margin: 0,
    fontSize: "13px",
    color: "#6b7280",
  },
heroCard: {
  background: "#111827",
  borderRadius: "24px",
  padding: "42px",
  color: "#fff",
  marginTop: "20px",
  boxShadow: "0 10px 24px rgba(0,0,0,0.15)",
},

heroTitle: {
  margin: 0,
  fontSize: "34px", // reduced
  fontWeight: "700",
  lineHeight: "1.2",
},

heroText: {
  marginTop: "10px",
  color: "rgba(255,255,255,0.8)",
  fontSize: "15px", // reduced
  lineHeight: "1.5",
},
};