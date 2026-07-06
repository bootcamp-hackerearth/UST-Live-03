"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";

const NAV_MODULES = [
  { id: "nodes", label: "Nodes", path: "/node/list", icon: "⬡", desc: "Manage access paths & permissions", roles: ["ADMIN"] },
  { id: "prices", label: "Prices", path: "/price/list", icon: "₹", desc: "Set MRP, selling & cost prices", roles: ["ADMIN", "MANAGER", "CASHIER", "AUDITOR"] },
  { id: "categories", label: "Categories", path: "/category/list", icon: "◈", desc: "Organise product categories", roles: ["ADMIN", "MANAGER"] },
  { id: "brands", label: "Brands", path: "/brand/list", icon: "◎", desc: "Register and manage brands", roles: ["ADMIN", "MANAGER"] },
  { id: "warehouses", label: "Warehouses", path: "/wareHouse/list", icon: "▦", desc: "Track warehouse locations", roles: ["ADMIN", "MANAGER", "INVENTORY_MANAGER"] },
  { id: "customers", label: "Customers", path: "/customer/list", icon: "◉", desc: "View and manage customers", roles: ["ADMIN", "MANAGER", "CASHIER"] },
  { id: "models", label: "Models", path: "/models/list", icon: "◇", desc: "Product model catalogue", roles: ["ADMIN", "MANAGER"] },
  { id: "roles", label: "Roles", path: "/role/list", icon: "⬟", desc: "Configure user roles & access", roles: ["ADMIN"] },
  { id: "products", label: "Products", path: "/product/list", icon: "⬡", desc: "Full product inventory listing", roles: ["ADMIN", "MANAGER", "CASHIER"] },
  { id: "orders", label: "Orders", path: "/orders", icon: "▣", desc: "View and process orders", roles: ["ADMIN", "MANAGER", "CASHIER", "AUDITOR"] },
  { id: "shelves", label: "Shelves", path: "/shelfs/list", icon: "☰", desc: "Configure shelves in racks", roles: ["ADMIN", "INVENTORY_MANAGER"] },
  { id: "stocks", label: "Stocks", path: "/stocks/list", icon: "◈", desc: "Monitor stock levels & movement", roles: ["ADMIN", "INVENTORY_MANAGER"] },
  { id: "users", label: "Users", path: "/user/list", icon: "◎", desc: "Manage system users", roles: ["ADMIN"] },
  { id: "sales", label: "Sales", path: "/sales", icon: "◉", desc: "Sales reports & transactions", roles: ["ADMIN", "MANAGER", "CASHIER", "AUDITOR"] },
  { id: "units", label: "Units", path: "/unit/list", icon: "◇", desc: "Define measurement units", roles: ["ADMIN", "MANAGER"] },
  { id: "racks", label: "Racks", path: "/racks/list", icon: "▦", desc: "Manage rack configurations", roles: ["ADMIN", "INVENTORY_MANAGER"] },
];

// unique accent per card cycling through brand palette shades
const ACCENTS = [
  "#363955", "#54668E", "#879EC6", "#3d4266", "#4a5a7a", "#6b82a8",
  "#363955", "#54668E", "#879EC6", "#3d4266", "#4a5a7a", "#6b82a8",
  "#363955", "#54668E", "#879EC6", "#3d4266",
];

export default function Home() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [userName, setUserName] = useState("User");
  const [currentDate, setCurrentDate] = useState("");
  const [hoveredId, setHoveredId] = useState(null);
  const router = useRouter();

  useEffect(() => {
    const handleToggle = (e) => setIsSidebarOpen(e.detail.isOpen);
    globalThis.addEventListener("sidebar-toggle", handleToggle);

    const raw = localStorage.getItem("name") || localStorage.getItem("username") || localStorage.getItem("email") || "User";
    const clean = raw.includes("@") ? raw.split("@")[0] : raw;
    setUserName(clean.charAt(0).toUpperCase() + clean.slice(1));

    setCurrentDate(new Date().toLocaleDateString("en-US", {
      weekday: "long", year: "numeric", month: "long", day: "numeric",
    }));

    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);

  return (
    <div style={{
      position: "fixed", top: "60px", right: 0, bottom: 0,
      left: isSidebarOpen ? "220px" : "55px",
      backgroundColor: "#f4f5f9",
      fontFamily: "'Segoe UI', 'Helvetica Neue', sans-serif",
      display: "flex", flexDirection: "column",
      overflow: "hidden", transition: "left 0.2s ease",
    }}>

      {/* ── Header banner ── */}
      <div style={{
        background: "linear-gradient(135deg, #1e2a45 0%, #363955 50%, #54668E 100%)",
        padding: "28px 40px 24px",
        flexShrink: 0,
        position: "relative",
        overflow: "hidden",
      }}>
        {/* decorative circles */}
        <div style={{
          position: "absolute", top: "-40px", right: "60px",
          width: "160px", height: "160px", borderRadius: "50%",
          background: "rgba(135,158,198,0.12)", pointerEvents: "none",
        }} />
        <div style={{
          position: "absolute", bottom: "-30px", right: "200px",
          width: "100px", height: "100px", borderRadius: "50%",
          background: "rgba(255,255,255,0.06)", pointerEvents: "none",
        }} />

        <p style={{ margin: "0 0 4px", fontSize: "12px", color: "rgba(180,200,230,0.7)", letterSpacing: "1.2px", textTransform: "uppercase", fontWeight: "500" }}>
          {currentDate}
        </p>
        <h1 style={{ margin: "0 0 6px", fontSize: "26px", fontWeight: "700", color: "#ffffff", letterSpacing: "-0.3px" }}>
          Welcome back, <span style={{ color: "#879EC6" }}>{userName}</span>
        </h1>
        <p style={{ margin: 0, fontSize: "13.5px", color: "rgba(200,215,235,0.75)", fontWeight: "400" }}>
          Select a module below to get started with your store operations.
        </p>
      </div>

      {/* ── Module grid ── */}
      <div style={{
        flex: 1, overflowY: "auto",
        padding: "28px 40px 36px",
      }}>
        <p style={{
          margin: "0 0 18px",
          fontSize: "11px", fontWeight: "700",
          color: "#879EC6", letterSpacing: "1.4px",
          textTransform: "uppercase",
        }}>
          All Modules
        </p>

        <div style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fill, minmax(210px, 1fr))",
          gap: "14px",
        }}>
          {NAV_MODULES.map((mod, i) => {
            const accent = ACCENTS[i];
            const hovered = hoveredId === mod.id;
            return (
              <a
                key={mod.id}
                href={mod.path}
                aria-label={mod.label}
                onClick={(e) => { e.preventDefault(); router.push(mod.path); }}
                onMouseEnter={() => setHoveredId(mod.id)}
                onMouseLeave={() => setHoveredId(null)}
                onFocus={() => setHoveredId(mod.id)}
                onBlur={() => setHoveredId(null)}
                style={{
                  display: "flex",
                  textDecoration: "none",
                  background: "#ffffff",
                  border: `1.5px solid ${hovered ? accent : "#e4e6ef"}`,
                  borderRadius: "12px",
                  padding: "20px 18px 18px",
                  cursor: "pointer",
                  transition: "all 0.15s ease",
                  boxShadow: hovered
                    ? `0 8px 24px rgba(54,57,85,0.13)`
                    : "0 1px 4px rgba(54,57,85,0.06)",
                  transform: hovered ? "translateY(-2px)" : "translateY(0)",
                  flexDirection: "column", gap: "10px",
                  position: "relative", overflow: "hidden",
                }}
              >
                {/* top accent bar */}
                <div style={{
                  position: "absolute", top: 0, left: 0, right: 0,
                  height: "3px",
                  background: hovered
                    ? `linear-gradient(90deg, ${accent}, #879EC6)`
                    : "transparent",
                  transition: "background 0.15s",
                  borderRadius: "12px 12px 0 0",
                }} />

                {/* icon badge */}
                <div style={{
                  width: "38px", height: "38px", borderRadius: "10px",
                  background: hovered ? accent : "#f0f2f8",
                  display: "flex", alignItems: "center", justifyContent: "center",
                  fontSize: "17px",
                  color: hovered ? "#ffffff" : accent,
                  transition: "all 0.15s",
                  flexShrink: 0,
                }}>
                  {mod.icon}
                </div>

                <div>
                  <p style={{
                    margin: "0 0 3px",
                    fontSize: "14px", fontWeight: "700",
                    color: hovered ? accent : "#363955",
                    letterSpacing: "0.1px",
                    transition: "color 0.15s",
                  }}>
                    {mod.label}
                  </p>
                  <p style={{
                    margin: 0,
                    fontSize: "11.5px",
                    color: "#9ca3af",
                    lineHeight: "1.5",
                  }}>
                    {mod.desc}
                  </p>
                </div>

                {/* arrow indicator */}
                <div style={{
                  position: "absolute", bottom: "14px", right: "14px",
                  fontSize: "14px",
                  color: hovered ? accent : "#d1d5db",
                  transition: "all 0.15s",
                  transform: hovered ? "translateX(2px)" : "translateX(0)",
                }}>
                  →
                </div>
              </a>
            );
          })}
        </div>
      </div>

      {/* ── Footer strip ── */}
      <div style={{
        background: "linear-gradient(135deg, #363955, #54668E)",
        padding: "10px 40px",
        flexShrink: 0,
        textAlign: "center",
      }}>
        <span style={{ color: "rgba(232,232,232,0.65)", fontSize: "11.5px", fontWeight: "500", letterSpacing: "0.3px" }}>
          StoreFlow · Retail Management System · v2.0
        </span>
      </div>
    </div>
  );
}