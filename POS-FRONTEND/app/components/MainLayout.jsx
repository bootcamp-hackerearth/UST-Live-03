"use client";

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "../api";

import Navbar from "./Navbar";
import Sidebar from "./Sidebar";

export default function MainLayout({ children }) {
  const [nodes, setNodes] = useState([]);
  const [collapsed, setCollapsed] = useState(false);

  const pathname = usePathname();
  const router = useRouter();

  const hideLayout =
    pathname === "/login" || pathname === "/register";

  useEffect(() => {
    if (!hideLayout) fetchNodes();
  }, [hideLayout]);

  const fetchNodes = async () => {
    try {
      const token = localStorage.getItem("token");

      if (!token) {
        router.push("/login");
        return;
      }

      const res = await api.get("/node/getNodesForRoles", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      setNodes(res.data || []);
    } catch (err) {
      console.log(err);
    }
  };

  const logout = () => {
    localStorage.clear();
    router.push("/login");
  };

  const toggleSidebar = () => setCollapsed(!collapsed);

  if (hideLayout) return <>{children}</>;

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        height: "100vh",
      }}
    >
 
      <Navbar
        toggleSidebar={toggleSidebar}
        logout={logout}
      />

      <div
        style={{
          display: "flex",
          flex: 1,
          overflow: "hidden",
        }}
      >
        <Sidebar
          nodes={nodes}
          pathname={pathname}
          collapsed={collapsed}
        />

        <div
          style={{
            flex: 1,
            padding: 24,
            background: "#f3f4f6",
            overflowY: "auto",
          }}
        >
          {children}
        </div>
      </div>
    </div>
  );
}

MainLayout.propTypes = {
  children: PropTypes.node.isRequired,
};