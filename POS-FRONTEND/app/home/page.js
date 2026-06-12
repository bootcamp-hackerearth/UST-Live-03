"use client";

import { useState, useEffect } from "react";

const styles = {
  page: {
    position: "fixed",
    top: "60px",
    right: 0,
    bottom: 0,
    backgroundColor: "#F4F5F8",
    fontFamily: "'Segoe UI', sans-serif",
    display: "flex",
    overflow: "hidden",
    transition: "left 0.2s ease", 
  },
  card: {
    flex: 1,
    backgroundColor: "#ffffff",
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
    overflow: "hidden",
    borderLeft: "1px solid #E8E8E8",
  },
  topSection: {
    flex: 1,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    padding: "48px 40px 24px",
    textAlign: "center",
    background: "linear-gradient(160deg, #F8FAFC 0%, #fff 60%)",
  },
  welcomeText: {
    fontSize: "32px",
    fontWeight: "700",
    color: "#363955",
    marginBottom: "12px",
    letterSpacing: "-0.5px",
  },
  brand: {
    color: "#54668E",
    borderBottom: "3px solid #879EC6",
    paddingBottom: "2px",
  },
  subtitle: {
    fontSize: "15px",
    color: "#6b7280",
    maxWidth: "480px",
    lineHeight: "1.6",
    margin: "0 auto 8px",
  },
  dateText: {
    fontSize: "13px",
    color: "#879EC6",
    fontWeight: "500",
  },
  bottomStrip: {
    background: "linear-gradient(135deg, #363955 0%, #54668E 100%)",
    padding: "14px 40px",
    textAlign: "center",
    flexShrink: 0,
  },
  bottomText: {
    color: "rgba(232,232,232,0.8)",
    fontSize: "12px",
    fontWeight: "500",
    letterSpacing: "0.3px",
  },
};

export default function Home() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [userName, setUserName] = useState("User");
  const [currentDate, setCurrentDate] = useState("");

  useEffect(() => {
    const handleToggle = (e) => setIsSidebarOpen(e.detail.isOpen);
    globalThis.addEventListener("sidebar-toggle", handleToggle);

    const storedName = localStorage.getItem("email") || localStorage.getItem("username") || "User";
    const cleanName = storedName.includes("@") ? storedName.split("@")[0] : storedName;
    setUserName(cleanName.charAt(0).toUpperCase() + cleanName.slice(1));

    const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    setCurrentDate(new Date().toLocaleDateString("en-US", options));

    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);

  return (
    <div style={{ ...styles.page, left: isSidebarOpen ? "220px" : "55px" }}>
      <div style={styles.card}>

        <div style={styles.topSection}>
          <h1 style={styles.welcomeText}>
            Welcome back, <span style={styles.brand}>{userName}</span>
          </h1>

          <p style={styles.subtitle}>
            Manage your store operations smoothly. Select a option from the navigation menu to get started.
          </p>
          
          <p style={styles.dateText}>
            {currentDate}
          </p>
        </div>

        <div style={styles.bottomStrip}>
          <span style={styles.bottomText}>
            RetailPOS · Built for modern retail teams · v2.0
          </span>
        </div>

      </div>
    </div>
  );
}