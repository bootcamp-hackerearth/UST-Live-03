"use client";

import { useState, useEffect } from "react";
import { usePathname, useRouter } from "next/navigation";
import api from "@/api/axios";
import { 
  SquareChevronLeft, 
  Home, 
  Package, 
  ShoppingCart, 
  BarChart3, 
  Users, 
  Settings, 
  Layers, 
  LogOut,
  Receipt,
  Truck,
  UserCheck,
  CreditCard,
  Tags,
  ShieldCheck,
  Warehouse,
  History,
  FileSpreadsheet,
  Boxes,
  Scale,     
  Columns4,   
  Grid,        
  DollarSign,  
  Network,
} from 'lucide-react';

export const SIDEBAR_WIDTH = "220px";

const getIconForNode = (identifier) => {
  const name = identifier?.toLowerCase() || "";
  const iconMap = [
    { match: n => n.includes("home"), icon: Home },
    { match: n => n.includes("price model"), icon: DollarSign },
    { match: n => n.includes("rack"), icon: Columns4 },
    { match: n => n.includes("shelf") || n.includes("shelves"), icon: Grid },
    { match: n => n.includes("unit"), icon: Scale },
    { match: n => n.includes("inventory") || n.includes("stock"), icon: Package },
    { match: n => n.includes("product") || n.includes("item"), icon: Boxes },
    { match: n => n.includes("warehouse") || n.includes("store"), icon: Warehouse },
    { match: n => n.includes("category") || n.includes("tag") || n.includes("brand"), icon: Tags },
    { match: n => n.includes("pos") || n.includes("billing") || n.includes("checkout"), icon: ShoppingCart },
    { match: n => n.includes("sale") || n.includes("order"), icon: Receipt },
    { match: n => n.includes("payment") || n.includes("transaction"), icon: CreditCard },
    { match: n => n.includes("history") || n.includes("log"), icon: History },
    { match: n => n.includes("customer"), icon: Users },
    { match: n => n.includes("employee") || n.includes("staff") || n.includes("user"), icon: UserCheck },
    { match: n => n.includes("supplier") || n.includes("vendor") || n.includes("shipping"), icon: Truck },
    { match: n => n.includes("report") || n.includes("insight") || n.includes("analytic"), icon: BarChart3 },
    { match: n => n.includes("tax") || n.includes("invoice") || n.includes("ledger"), icon: FileSpreadsheet },
    { match: n => n.includes("role") || n.includes("permission") || n.includes("auth"), icon: ShieldCheck },
    { match: n => n.includes("node"), icon: Network },
    { match: n => n.includes("setting") || n.includes("config"), icon: Settings },
  ]; 

  for (const entry of iconMap) {
    if (entry.match(name)) {
      const Icon = entry.icon;
      return <Icon size={18} />;
    }
  }

  return <Layers size={18} />;
};

const styles = {
  sidebar: {
    position: "fixed",
    top: "60px",
    left: 0,
    height: "calc(100vh - 60px)",
    backgroundColor: "#f8faf8",
    borderRight: "1px solid #d8e8d8",
    display: "flex",
    flexDirection: "column",
    fontFamily: "'Segoe UI', sans-serif",
    zIndex: 100,
    overflowY: "auto",
    overflowX: "hidden", 
    transition: "width 0.2s ease, border-color 0.2s ease",
  },
  header: {
    padding: "18px 16px 10px",
    fontSize: "11px",
    fontWeight: "700",
    color: "#999",
    textTransform: "uppercase",
    letterSpacing: "1px",
    borderBottom: "1px solid #e8ece8",
    marginBottom: "8px",
    display: "flex",
    alignItems: "center",
  },
  iconBtn: {
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    color: "#777",
    transition: "transform 0.2s ease, color 0.2s ease",
  },
  list: {
    display: "flex",
    flexDirection: "column",
    padding: "0 10px",
    gap: "2px",
    flex: 1,
  },
  item: {
    padding: "10px 14px",
    borderRadius: "8px",
    fontSize: "14px",
    fontWeight: "500",
    color: "#333",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    gap: "10px",
    transition: "0.2s ease",
  },
  itemActive: {
    backgroundColor: "#e8ecff",
    color: "#3b4a8a",
    fontWeight: "600",
  },
  itemHover: {
    backgroundColor: "#eef1ff",
    color: "#3b4a8a",
  },
  logoutWrap: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    padding: "0 10px",
    width: "100%",
    boxSizing: "border-box",
  },
  logoutBtn: {
    width: "100%",
    margin: "10px 0",
    borderRadius: "8px",
    border: "1px solid #f1b5b5",
    backgroundColor: "#fff5f5",
    color: "#c0392b",
    fontSize: "14px",
    fontWeight: "600",
    cursor: "pointer",
    transition: "0.2s ease",
    display: "flex",
    alignItems: "center",
    boxSizing: "border-box",
  },
  footer: {
    padding: "4px 16px 14px",
    fontSize: "11px",
    color: "#aaa",
    width: "100%",
    boxSizing: "border-box",
  },
};

function renderNavButton({ buttonKey, icon, label, isOpen, isActive, isHovered, onClick, onMouseEnter, onMouseLeave, title }) {
  return (
    <button
      key={buttonKey}
      type="button"
      onClick={onClick}
      onMouseEnter={onMouseEnter}
      onMouseLeave={onMouseLeave}
      title={title || undefined}
      aria-label={isOpen ? undefined : label || "Navigation Link"}
      style={{
        ...styles.item,
        justifyContent: isOpen ? "flex-start" : "center",
        padding: isOpen ? "10px 14px" : "10px 0",
        ...(isActive ? styles.itemActive : {}),
        ...(isHovered ? styles.itemHover : {}),
        border: "none",
        backgroundColor: "transparent", 
        width: "100%",
        textAlign: "left",
      }}
    >
      {icon}
      {isOpen && <span>{label}</span>}
    </button>
  );
}

export default function Sidebar() {
  const [nodes, setNodes] = useState([]);
  const [hovered, setHovered] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isOpen, setIsOpen] = useState(true); 
  const pathname = usePathname();
  const router = useRouter();

  useEffect(() => {
    const savedState = localStorage.getItem("sidebar-open");
    if (savedState !== null) {
      const parsedState = savedState === "true";
      setIsOpen(parsedState);
      globalThis.dispatchEvent(new CustomEvent("sidebar-toggle", { detail: { isOpen: parsedState } }));
    }
  }, []);

  useEffect(() => {
    const hasToken = !!localStorage.getItem("token");
    setIsLoggedIn(hasToken);
    if (!hasToken) setNodes([]);
  }, [pathname]);

  useEffect(() => {
  async function fetchNodes() {
    try {
      const res = await api.get("/node/getNodesForRoles");
      const data = res.data;
      setNodes(Array.isArray(data) ? data : data.data ?? []);
    } catch (err) {
      console.error("Failed to fetch nodes:", err);
      setNodes([]);
    }
  }

  if (isLoggedIn && pathname !== "/login" && pathname !== "/register") {
    fetchNodes();
  }
}, [isLoggedIn, pathname]);

  function handleLogout() {
    localStorage.removeItem("token");
    router.push("/login");
  }

  const toggleSidebar = () => {
    const nextState = !isOpen;
    setIsOpen(nextState);
    localStorage.setItem("sidebar-open", String(nextState));
    
    const event = new CustomEvent("sidebar-toggle", {
      detail: { isOpen: nextState }
    });
    globalThis.dispatchEvent(event);
  };

  const collapsed = !isOpen;

  return (
    <aside 
      style={{
        ...styles.sidebar,
        width: isOpen ? SIDEBAR_WIDTH : "55px", 
      }}
    >
      <div 
        style={{ 
          ...styles.header, 
          justifyContent: isOpen ? "space-between" : "center",
          padding: isOpen ? "18px 16px 10px" : "18px 0 10px",
          borderBottom: isOpen ? "1px solid #e8ece8" : "1px solid transparent",
        }}
      >
        {isOpen && <span>Navigation</span>}
        <button
          type="button"
          onClick={toggleSidebar}
          aria-expanded={isOpen}
          title={isOpen ? "Close Sidebar" : "Open Sidebar"}
          style={{
            ...styles.iconBtn,
            transform: isOpen ? "rotate(0deg)" : "rotate(180deg)",
            border: "none",
            background: "transparent",
            padding: 0,
          }}
        >
          <SquareChevronLeft size={22} />
        </button>
      </div>

      <div style={{ ...styles.list, padding: isOpen ? "0 10px" : "0 6px" }}>
        {renderNavButton({
          buttonKey: "home",
          icon: getIconForNode("home"),
          label: "Home",
          isOpen,
          isActive: pathname === "/home",
          isHovered: hovered === "home",
          onMouseEnter: () => setHovered("home"),
          onMouseLeave: () => setHovered(null),
          onClick: () => router.push("/home"),
          title: collapsed ? "Home" : "",
        })}

        {nodes.map((node, index) => renderNavButton({
          buttonKey: node.path || node.identifier || `node-${index}`,
          icon: getIconForNode(node.identifier),
          label: node.identifier,
          isOpen,
          isActive: pathname === node.path,
          isHovered: hovered === index,
          onMouseEnter: () => setHovered(index),
          onMouseLeave: () => setHovered(null),
          onClick: () => router.push(node.path || "/"),
          title: collapsed ? node.identifier : "",
        }))}
      </div>

      <div style={{ ...styles.logoutWrap, padding: isOpen ? "0 10px" : "0 4px" }}>
        <button 
          style={{ 
            ...styles.logoutBtn, 
            height: isOpen ? "auto" : "36px",
            width: isOpen ? "100%" : "36px",
            padding: isOpen ? "10px 14px" : "0",
            justifyContent: "center",
            gap: isOpen ? "10px" : "0px",
          }} 
          onClick={handleLogout}
          title={collapsed ? "Logout" : ""}
        >
          <LogOut size={18} style={{ flexShrink: 0 }} />
          {isOpen && <span style={{ width: "100%", textAlign: "left" }}>Logout</span>}
        </button>

        {isOpen && <div style={styles.footer}>RetailPOS © 2025</div>}
      </div>
    </aside>
  );
}