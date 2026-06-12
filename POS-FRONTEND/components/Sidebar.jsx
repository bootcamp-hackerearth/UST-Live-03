"use client";

import { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import PropTypes from "prop-types";
import {
  XMarkIcon,
  ArrowLeftStartOnRectangleIcon,
  CubeIcon,
  ShoppingCartIcon,
  UserGroupIcon,
  ChartBarIcon,
  ArchiveBoxIcon,
  Squares2X2Icon,
} from "@heroicons/react/24/outline";

export default function Sidebar({ sidebarOpen, setSidebarOpen }) {
  const [nodes, setNodes] = useState([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();
  const pathname = usePathname();

  const fetchNodes = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) return;

      const response = await fetch("http://localhost:8080/api/node/list", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          page: 0,
          sizePerPage: 100,
          sortDirection: "ASC",
          sortField: "identifier",
        }),
      });

      const data = await response.json();
      const rawNodesList = Array.isArray(data) ? data : data.dtoList || [];

      setNodes(rawNodesList);
    } catch (error) {
      console.error("Failed fetching nodes:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNodes();

    globalThis.addEventListener("nodeDataChanged", fetchNodes);
    return () => {
      globalThis.removeEventListener("nodeDataChanged", fetchNodes);
    };
  }, []);

  const handleNavigation = (path) => {
    const finalPath = path.startsWith("/") ? path : `/${path}`;
    router.push(finalPath);
  };

  const getIcon = (identifier) => {
    const key = identifier?.toLowerCase()?.trim();
    switch (key) {
      case "dashboard":
        return ChartBarIcon;
      case "product":
      case "products":
        return CubeIcon;
      case "category":
      case "categories":
        return Squares2X2Icon;
      case "user":
      case "users":
        return UserGroupIcon;
      case "node":
      case "nodes":
        return Squares2X2Icon;
      case "price":
      case "prices":
        return ShoppingCartIcon;
      case "role":
      case "roles":
        return ArchiveBoxIcon;
      default:
        return Squares2X2Icon;
    }
  };

  return (
    <>
      <style>{`
        .sidebar-overlay {
          position: fixed;
          inset: 0;
          background-color: rgba(0, 0, 0, 0.15);
          backdrop-filter: blur(4px);
          z-index: 40;
          transition: all 0.3s ease;
        }

        .sidebar-overlay.open {
          opacity: 1;
          visibility: visible;
        }

        .sidebar-overlay.closed {
          opacity: 0;
          visibility: hidden;
        }

        @media (min-width: 1024px) {
          .sidebar-overlay {
            display: none;
          }
        }

        .sidebar-aside {
          position: fixed;
          top: 0;
          left: 0;
          height: 100vh;
          background-color: #ffffff;
          border-right: 1px solid #ebebf5;
          z-index: 50;
          display: flex;
          flex-direction: column;
          transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          box-sizing: border-box;
        }

        .sidebar-aside.open {
          width: 260px;
        }

        .sidebar-aside.closed {
          width: 88px;
        }

        @media (max-width: 1023px) {
          .sidebar-aside.closed {
            transform: translateX(-100%);
          }
        }

        .sidebar-header {
          height: 88px;
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 0 20px;
          box-sizing: border-box;
        }

        .brand-trigger {
          display: flex;
          align-items: center;
          gap: 14px;
          background: none;
          border: none;
          padding: 0;
          cursor: pointer;
          text-align: left;
        }

        .brand-icon-box {
          width: 44px;
          height: 44px;
          border-radius: 10px;
          background: #6c63ff;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
        }

        .brand-icon-box svg {
          width: 22px;
          height: 22px;
          color: #ffffff;
        }

        .brand-title {
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
          font-size: 18px;
          font-weight: 700;
          color: #2d2d6e;
          letter-spacing: -0.5px;
        }

        .close-trigger {
          background: #f8f8fc;
          border: 1px solid #ebebf5;
          border-radius: 8px;
          width: 32px;
          height: 32px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #8888a0;
          cursor: pointer;
        }

        @media (min-width: 1024px) {
          .close-trigger {
            display: none;
          }
        }

        .sidebar-nav-container {
          flex: 1;
          padding: 20px 14px;
          overflow-y: auto;
          box-sizing: border-box;
        }

        .sidebar-nav-container::-webkit-scrollbar {
          display: none;
        }

        .nav-stack {
          display: flex;
          flex-direction: column;
          gap: 8px;
        }

        .nav-item {
          width: 100%;
          display: flex;
          align-items: center;
          gap: 14px;
          padding: 0 16px;
          height: 48px;
          border-radius: 10px;
          border: none;
          cursor: pointer;
          box-sizing: border-box;
          transition: all 0.2s ease;
          text-align: left;
          background: transparent;
        }

        .nav-item.active {
          background: #f4f5fa;
          color: #6c63ff;
        }

        .nav-item.inactive {
          color: #8888a0;
        }

        .nav-item.inactive:hover {
          background: #f8f8fc;
          color: #2d2d6e;
        }

        .nav-icon {
          width: 20px;
          height: 20px;
          flex-shrink: 0;
          transition: color 0.2s ease;
        }

        .nav-item.active .nav-icon {
          color: #6c63ff;
        }

        .nav-item.inactive .nav-icon {
          color: #b0b0c8;
        }

        .nav-item.inactive:hover .nav-icon {
          color: #6c63ff;
        }

        .nav-text {
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
          font-size: 14px;
          font-weight: 500;
          white-space: nowrap;
        }

        .sidebar-footer {
          padding: 16px 14px;
          border-top: 1px solid #ebebf5;
          box-sizing: border-box;
        }

        .logout-btn {
          width: 100%;
          display: flex;
          align-items: center;
          gap: 14px;
          padding: 0 16px;
          height: 48px;
          border-radius: 10px;
          border: none;
          background: transparent;
          color: #8888a0;
          cursor: pointer;
          box-sizing: border-box;
          transition: all 0.2s ease;
          text-align: left;
        }

        .logout-btn:hover {
          background: #fff2f2;
          color: #e55555;
        }

        .logout-icon {
          width: 20px;
          height: 20px;
          flex-shrink: 0;
          color: #b0b0c8;
          transition: color 0.2s ease;
        }

        .logout-btn:hover .logout-icon {
          color: #e55555;
        }

        .skeleton-item {
          width: 100%;
          height: 48px;
          border-radius: 10px;
          background: linear-gradient(90deg, #f4f5fa 25%, #ebebf5 50%, #f4f5fa 75%);
          background-size: 200% 100%;
          animation: loading-shimmer 1.5s infinite;
        }

        @keyframes loading-shimmer {
          0% { background-position: 200% 0; }
          100% { background-position: -200% 0; }
        }
      `}</style>

      <button
        type="button"
        onClick={() => setSidebarOpen(false)}
        onKeyDown={(e) => e.key === "Escape" && setSidebarOpen(false)}
        aria-label="Close sidebar overlay"
        className={`sidebar-overlay ${sidebarOpen ? "open" : "closed"}`}
      />

      <aside className={`sidebar-aside ${sidebarOpen ? "open" : "closed"}`}>
        <div className="sidebar-header">
          <button type="button" onClick={() => setSidebarOpen(!sidebarOpen)} className="brand-trigger">
            <div className="brand-icon-box">
              <Squares2X2Icon />
            </div>
            {sidebarOpen && <h1 className="brand-title">POSFlow</h1>}
          </button>

          {sidebarOpen && (
            <button type="button" onClick={() => setSidebarOpen(false)} className="close-trigger">
              <XMarkIcon className="w-4 h-4" />
            </button>
          )}
        </div>

        <div className="sidebar-nav-container">
          <nav className="nav-stack">
            {loading ? (
              [1, 2, 3, 4, 5].map((n) => (
                <div key={`skeleton-${n}`} className="skeleton-item" />
              ))
            ) : (
              nodes.map((node) => {
                const Icon = getIcon(node.identifier);
                const finalPath = node.path.startsWith("/") ? node.path : `/${node.path}`;
                const isActive = pathname === finalPath;

                return (
                  <button
                    type="button"
                    key={node.identifier || node.id}
                    onClick={() => handleNavigation(node.path)}
                    className={`nav-item ${isActive ? "active" : "inactive"}`}
                  >
                    <Icon className="nav-icon" />
                    {sidebarOpen && <span className="nav-text">{node.identifier}</span>}
                  </button>
                );
              })
            )}
          </nav>
        </div>

        <div className="sidebar-footer">
          <button
            type="button"
            onClick={() => {
              localStorage.clear();
              router.push("/login");
            }}
            className="logout-btn"
          >
            <ArrowLeftStartOnRectangleIcon className="logout-icon" />
            {sidebarOpen && <span className="nav-text">Logout</span>}
          </button>
        </div>
      </aside>
    </>
  );
}

Sidebar.propTypes = {
  sidebarOpen: PropTypes.bool.isRequired,
  setSidebarOpen: PropTypes.func.isRequired,
};