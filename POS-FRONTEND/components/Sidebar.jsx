"use client";

import PropTypes from "prop-types";
import { useEffect, useState, useRef, useCallback } from "react";
import { useRouter } from "next/navigation";

function Sidebar({ onToggle }) {
  const [nodes, setNodes] = useState([]);
  const router = useRouter();

  useEffect(() => {
    fetchNodes();
  }, []);

  const fetchNodes = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(
        `/api/node/getNodesForRoles`,
        {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        },
      );

      if (res.status === 401) {
        window.location.href = "/login";
        return;
      }

      if (res.status === 403) {
        setNodes([]);
        return;
      }

      if (!res.ok) {
        console.log("Failed to fetch nodes:", res.status);
        return;
      }

      const data = await res.json();
      setNodes(data);
    } catch (error) {
      console.log("Error:", error);
    }
  };

  const [showSidebar, setShowSidebar] = useState(false);
  const sidebarRef = useRef();

  const toggleSidebar = useCallback((val) => {
    setShowSidebar(val);
  }, []);

  useEffect(() => {
    onToggle?.(showSidebar);
  }, [showSidebar, onToggle]);

  useEffect(() => {
    const handler = () => {
      setShowSidebar((prev) => !prev);
    };
    globalThis.addEventListener("toggle-sidebar", handler);
    return () => globalThis.removeEventListener("toggle-sidebar", handler);
  }, []);

  return (
    <div
      ref={sidebarRef}
      style={{
        position: "fixed",
        top: "70px",
        left: showSidebar ? "0px" : "-260px",
        transition: "left 0.3s",
        zIndex: 999,
        width: "250px",
        height: "calc(100vh - 70px)",
        backgroundColor: "#111827",
        color: "white",
        padding: "20px",
        borderRight: "1px solid #374151",
        overflowY: "auto",
        boxSizing: "border-box",
      }}
    >
      <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
        {nodes.map((item) => (
          <li
            key={item.path || item.identifier}
            style={{ marginBottom: "12px" }}
          >
            <button
              type="button"
              onClick={() => {
                router.push(item.path);
                toggleSidebar(false);
              }}
              style={{
                width: "100%",
                padding: "14px",
                backgroundColor: "black",
                borderRadius: "8px",
                cursor: "pointer",
                textAlign: "center",
                fontWeight: "500",
                transition: "0.3s",
                border: "1px solid #374151",
                color: "white",
              }}
            >
              {item.identifier}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

Sidebar.propTypes = {
  onToggle: PropTypes.func,
};

export default Sidebar;