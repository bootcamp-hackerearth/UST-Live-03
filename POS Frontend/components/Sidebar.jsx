"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
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
  BadgeDollarSign,
  Workflow
} from 'lucide-react';

export const SIDEBAR_WIDTH = "220px";

const ICON_PATTERNS = [
  { patterns: ["price"], Icon: BadgeDollarSign },
  { patterns: ["model"], Icon: Workflow },
  { patterns: ["rack"], Icon: Columns4 },
  { patterns: ["shelf", "shelves"], Icon: Grid },
  { patterns: ["unit"], Icon: Scale },
  { patterns: ["inventory", "stock"], Icon: Package },
  { patterns: ["product", "item"], Icon: Boxes },
  { patterns: ["warehouse", "store"], Icon: Warehouse },
  { patterns: ["category", "tag", "brand"], Icon: Tags },
  { patterns: ["pos", "billing", "checkout"], Icon: ShoppingCart },
  { patterns: ["sale", "order"], Icon: Receipt },
  { patterns: ["payment", "transaction"], Icon: CreditCard },
  { patterns: ["history", "log"], Icon: History },
  { patterns: ["customer"], Icon: Users },
  { patterns: ["employee", "staff", "user"], Icon: UserCheck },
  { patterns: ["supplier", "vendor", "shipping"], Icon: Truck },
  { patterns: ["report", "insight", "analytic"], Icon: BarChart3 },
  { patterns: ["tax", "invoice", "ledger"], Icon: FileSpreadsheet },
  { patterns: ["role", "permission", "auth"], Icon: ShieldCheck },
  { patterns: ["setting", "config"], Icon: Settings },
];

const getIconForNode = (identifier) => {
  const name = identifier?.toLowerCase() || "";

  if (name.includes("home")) return <Home size={18} />;

  for (const { patterns, Icon } of ICON_PATTERNS) {
    if (patterns.some(p => name.includes(p))) {
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
    backgroundColor: "#f4f4f4",
    borderRight: "1px solid #d1d5db",
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
    borderBottom: "1px solid #d1d5db",
    marginBottom: "8px",
    display: "flex",
    alignItems: "center",
  },
  iconBtn: {
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    color: "#1a1a1a",
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
    transition: "all 0.2s ease",
  },
  itemActive: {
    backgroundColor: "#e5e7eb",
    color: "#000000",
    fontWeight: "600",
    borderLeftWidth: "3px",
    borderLeftStyle: "solid",
    borderLeftColor: "#000000",
  },
  itemHover: {
    backgroundColor: "#f5f5f5",
    color: "#000000",
  },
  footerSection: {
    display: "flex",
    flexDirection: "column",
    borderTop: "1px solid #d1d5db",
    backgroundColor: "#f4f4f4",
  },
  logoutBtn: {
    borderRadius: "8px",
    borderWidth: "1px",
    borderStyle: "solid",
    borderColor: "#f5c6c6",
    backgroundColor: "#ffffff",
    color: "#1a1a1a",
    fontSize: "14px",
    fontWeight: "600",
    cursor: "pointer",
    transition: "0.2s ease",
    display: "flex",
    alignItems: "center",
    gap: "10px",
  },
  logoutHoverStyle: {
    backgroundColor: "#fee2e2",
    borderColor: "#dc2626",
    color: "#b91c1c",
  },
  footer: {
    padding: "10px 16px 14px",
    fontSize: "11px",
    color: "#aaa",
    textAlign: "center"
  },
};

function MenuButton({ icon, label, path, identifier, isOpen, isActive, hovered, setHovered, onClick }) {
  let resolvedBgColor = "transparent";
  if (isActive) {
    resolvedBgColor = styles.itemActive.backgroundColor;
  } else if (hovered === identifier) {
    resolvedBgColor = styles.itemHover.backgroundColor;
  }

  const btnStyle = {
    ...styles.item,
    justifyContent: isOpen ? "flex-start" : "center",
    padding: isOpen ? "10px 14px" : "10px 0",
    ...(isActive ? styles.itemActive : {}),
    ...(hovered === identifier ? styles.itemHover : {}),
    borderTopWidth: "0px",
    borderTopStyle: "none",
    borderRightWidth: "0px",
    borderRightStyle: "none",
    borderBottomWidth: "0px",
    borderBottomStyle: "none",
    borderLeftWidth: isActive ? "3px" : "0px",
    borderLeftStyle: isActive ? "solid" : "none",
    borderLeftColor: isActive ? "#000000" : "transparent",
    backgroundColor: resolvedBgColor,
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" || e.key === " ") {
      e.preventDefault();
      onClick();
    }
  };

  return (
    <button
      type="button"
      style={btnStyle}
      onMouseEnter={() => setHovered(identifier)}
      onMouseLeave={() => setHovered(null)}
      onClick={onClick}
      onKeyDown={handleKeyDown}
      title={isOpen ? "" : label}
      aria-label={label}
    >
      {icon}
      {isOpen && <span>{label}</span>}
    </button>
  );
}

function LogoutButton({ isOpen, logoutHover, setLogoutHover, onClick }) {
  return (
    <button
      type="button"
      className="logout-button"
      style={{
        ...styles.logoutBtn,
        ...(logoutHover ? styles.logoutHoverStyle : {}),
        width: "100%",
        padding: isOpen ? "10px 14px" : "10px 0",
        justifyContent: isOpen ? "flex-start" : "center",
      }}
      onClick={onClick}
      onMouseEnter={() => setLogoutHover(true)}
      onMouseLeave={() => setLogoutHover(false)}
      onKeyDown={(e) => { if (e.key === "Enter" || e.key === " ") { e.preventDefault(); onClick(); } }}
      title={isOpen ? "" : "Logout"}
      aria-label="Logout"
    >
      <LogOut size={18} />
      {isOpen && <span>Logout</span>}
    </button>
  );
}

export default function Sidebar() {
  const [nodes, setNodes] = useState([]);
  const [hovered, setHovered] = useState(null);
  const [logoutHover, setLogoutHover] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isOpen, setIsOpen] = useState(true);
  const pathname = usePathname();
  const router = useRouter();

  useEffect(() => {
    setIsLoggedIn(!!localStorage.getItem("token"));
  }, [pathname]);

  useEffect(() => {
    async function fetchNodes() {
      try {
        const res = await api.get("/node/getNodesForRoles");
        const data = res.data;
        setNodes(Array.isArray(data) ? data : data.data ?? []);
      } catch (err) {
        console.error("Failed to fetch nodes:", err.response?.data || err.message);
        setNodes([]);
      }
    }

    if (isLoggedIn) {
      fetchNodes();
    }
  }, [isLoggedIn]);

  if (pathname === "/login" || pathname === "/register") {
    return null;
  }

  function handleLogout() {
    localStorage.removeItem("token");
    router.push("/login");
  }

  const toggleSidebar = () => {
    const nextState = !isOpen;
    setIsOpen(nextState);

    const event = new CustomEvent("sidebar-toggle", {
      detail: { isOpen: nextState }
    });
    globalThis.dispatchEvent(event);
  };

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
          style={{
            ...styles.iconBtn,
            transform: isOpen ? "rotate(0deg)" : "rotate(180deg)",
            border: "none",
            backgroundColor: "transparent",
            padding: "0",
          }}
          onClick={toggleSidebar}
          title={isOpen ? "Close Sidebar" : "Open Sidebar"}
          aria-label={isOpen ? "Close Sidebar" : "Open Sidebar"}
        >
          <SquareChevronLeft size={22} />
        </button>
      </div>

      <div style={{ ...styles.list, padding: isOpen ? "0 10px" : "0 6px" }}>
        <MenuButton
          icon={getIconForNode("home")}
          label="Home"
          identifier="home"
          isOpen={isOpen}
          isActive={pathname === "/home"}
          hovered={hovered}
          setHovered={setHovered}
          onClick={() => router.push("/home")}
        />

        <NodeList
          nodes={nodes}
          isOpen={isOpen}
          hovered={hovered}
          setHovered={setHovered}
          pathname={pathname}
          router={router}
        />
      </div>

      <div style={styles.footerSection}>
        <div style={{ padding: isOpen ? "12px 10px" : "12px 6px" }}>
          <LogoutButton
            isOpen={isOpen}
            logoutHover={logoutHover}
            setLogoutHover={setLogoutHover}
            onClick={handleLogout}
          />
        </div>

        {isOpen && <div style={styles.footer}>RetailPOS © 2026</div>}
      </div>
    </aside>
  );
}

function NodeList({ nodes, isOpen, hovered, setHovered, pathname, router }) {
  return (
    <>
      {nodes.map((node) => (
        <MenuButton
          key={node.identifier}
          icon={getIconForNode(node.identifier)}
          label={node.identifier}
          identifier={node.identifier}
          isOpen={isOpen}
          isActive={pathname === node.path}
          hovered={hovered}
          setHovered={setHovered}
          onClick={() => router.push(node.path)}
        />
      ))}
    </>
  );
}

NodeList.propTypes = {
  nodes: PropTypes.array,
  isOpen: PropTypes.bool,
  hovered: PropTypes.string,
  setHovered: PropTypes.func,
  pathname: PropTypes.string,
  router: PropTypes.object,
};

MenuButton.propTypes = {
  icon: PropTypes.node,
  label: PropTypes.string,
  path: PropTypes.string,
  identifier: PropTypes.string,
  isOpen: PropTypes.bool,
  isActive: PropTypes.bool,
  hovered: PropTypes.string,
  setHovered: PropTypes.func,
  onClick: PropTypes.func,
};

LogoutButton.propTypes = {
  isOpen: PropTypes.bool,
  logoutHover: PropTypes.bool,
  setLogoutHover: PropTypes.func,
  onClick: PropTypes.func,
};