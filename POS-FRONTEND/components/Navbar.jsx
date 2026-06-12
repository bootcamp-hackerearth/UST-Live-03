"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { ShoppingBag } from "lucide-react";

export default function Navbar() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userInitial, setUserInitial] = useState("U");
  const [displayName, setDisplayName] = useState("User");
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const pathname = usePathname();

  useEffect(() => {
    const token = localStorage.getItem("token");
    setIsLoggedIn(!!token);

    if (token) {
      const storedName = localStorage.getItem("email") || localStorage.getItem("username") || "User";
      setUserInitial(storedName.charAt(0).toUpperCase());
      const cleanName = storedName.includes("@") ? storedName.split("@")[0] : storedName;
      setDisplayName(cleanName.charAt(0).toUpperCase() + cleanName.slice(1));
    }
  }, [pathname]);

  useEffect(() => {
    const handleToggle = (e) => setIsSidebarOpen(e.detail.isOpen);
    globalThis.addEventListener("sidebar-toggle", handleToggle);
    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);

  const handleCartClick = (e) => {
    if (isLoggedIn) {
      e.preventDefault();
      const nextState = !isSidebarOpen;
      setIsSidebarOpen(nextState);
      const event = new CustomEvent("sidebar-toggle", {
        detail: { isOpen: nextState },
      });
      globalThis.dispatchEvent(event);
    }
  };

  if (pathname === "/login" || pathname === "/register") return null;

  return (
    <nav style={{
      position: "fixed", top: 0, left: 0, right: 0,
      height: "58px",
      background: "linear-gradient(135deg, #1e2a45 0%, #2e4272 100%)",
      display: "flex", alignItems: "center",
      padding: "0 32px",
      zIndex: 200,
      boxShadow: "0 2px 10px rgba(0,0,0,0.25)",
      fontFamily: "'Segoe UI', 'Helvetica Neue', sans-serif",
    }}>
      <div style={{
        width: "100%", display: "flex",
        alignItems: "center", justifyContent: "space-between",
      }}>

        <Link
          href="/home"
          onClick={handleCartClick}
          style={{
            display: "flex", alignItems: "center",
            gap: "10px", textDecoration: "none",
            cursor: "pointer",
          }}
          title={isLoggedIn ? "Toggle Sidebar Menu" : "Go Home"}
        >
          <div style={{
            width: "34px", height: "34px", borderRadius: "8px",
            background: "rgba(255,255,255,0.12)",
            border: "1px solid rgba(255,255,255,0.18)",
            display: "flex", alignItems: "center", justifyContent: "center",
            flexShrink: 0,
          }}>
            <ShoppingBag size={17} color="#ffffff" strokeWidth={2} />
          </div>

          <div style={{ display: "flex", flexDirection: "column", gap: "1px" }}>
            <span style={{
              fontSize: "15px", fontWeight: "700",
              color: "#ffffff", letterSpacing: "0.2px", lineHeight: 1.2,
            }}>
              StoreFlow
            </span>
            <span style={{
              fontSize: "9px", color: "rgba(180,200,230,0.8)",
              letterSpacing: "1.4px", textTransform: "uppercase", fontWeight: "500",
            }}>
              Retail Management
            </span>
          </div>
        </Link>

        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          {isLoggedIn ? (
            <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
              {/* Welcome text */}
              <div style={{ textAlign: "right" }}>
                <div style={{ fontSize: "11px", color: "rgba(255,255,255,0.55)", fontWeight: "400" }}>
                  Welcome back,
                </div>
                <div style={{ fontSize: "13px", color: "#ffffff", fontWeight: "600", letterSpacing: "0.1px" }}>
                  {displayName}
                </div>
              </div>

              <Link href="/profile" style={{
                width: "34px", height: "34px", borderRadius: "50%",
                background: "rgba(255,255,255,0.15)",
                border: "2px solid rgba(255,255,255,0.35)",
                display: "flex", alignItems: "center", justifyContent: "center",
                fontSize: "13px", fontWeight: "700",
                color: "#ffffff", textDecoration: "none",
                flexShrink: 0,
              }} title="My Account">
                {userInitial}
              </Link>
            </div>
          ) : (
            <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
              <Link href="/login" style={{
                padding: "6px 16px", borderRadius: "6px",
                fontSize: "13px", fontWeight: "500",
                color: "rgba(255,255,255,0.85)", textDecoration: "none",
                border: "1px solid rgba(255,255,255,0.25)",
              }}>
                Login
              </Link>
              <Link href="/register" style={{
                padding: "6px 16px", borderRadius: "6px",
                fontSize: "13px", fontWeight: "600",
                color: "#1e2a45", textDecoration: "none",
                background: "#ffffff",
              }}>
                Register
              </Link>
            </div>
          )}
        </div>

      </div>
    </nav>
  );
}