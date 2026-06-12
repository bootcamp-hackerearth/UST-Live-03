// components/Sidebar.jsx

"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import * as Icons from "lucide-react";
import { Menu, PanelLeftClose, LogOut, Home } from "lucide-react";
import Image from "next/image";
import ustLogo from "../assets/logo/UST-White-logo.png";

export default function Sidebar({
  nodes,
  loading,
  onLogout,
  onCollapseChange,
}) {
  const pathname = usePathname();
  const [collapsed, setCollapsed] = useState(false);

  useEffect(() => {
    if (onCollapseChange) {
      onCollapseChange(collapsed);
    }
  }, [collapsed, onCollapseChange]);

  const iconMap = {
    Checkout: "ShoppingCart",
    Registers: "Monitor",
    Transactions: "Receipt",
    Products: "Package",
    Prices: "Tags",
    Categories: "FolderTree",
    Nodes: "Network",
    Roles: "ShieldAlert",
    Users: "Users",
    test: "Terminal",
  };

  const getIcon = (name) => {
    const key = Object.keys(iconMap).find(
      (k) => k.toLowerCase() === name?.toLowerCase()
    );
    return Icons[iconMap[key]] || Icons.Circle;
  };

  // FIXED: Explicitly grouped regex choices inside parentheses and converted .replace to .replaceAll
  const formatPath = (dbPath) => {
    if (!dbPath) return "";
    return dbPath.replaceAll(/(^\/)|(\/$)/g, "");
  };

  // FIXED: Explicitly grouped regex choices inside parentheses and converted .replace to .replaceAll
  const isActive = (path) => {
    const formattedPath = formatPath(path);
    const currentPath = pathname.replaceAll(/(^\/)|(\/$)/g, "");
    return currentPath === formattedPath;
  };

  return (
    <aside
      className={`${collapsed ? "w-20" : "w-64"
        } bg-[#231F20] text-white/80 flex flex-col border-r border-white/10 transition-all duration-300 fixed h-screen left-0 top-0 z-40`}
    >
      <div className="p-4 border-b border-white/10 flex items-center justify-between flex-shrink-0">
        <div
          className={`flex items-center ${collapsed ? "justify-center w-full" : "gap-3"
            }`}
        >
          <div className="bg-[#231F20] p-1 rounded-md">
            <Image
              src={ustLogo}
              alt="UST Logo"
              className="w-8 h-auto"
            />
          </div>

          {!collapsed && (
            <div className="leading-tight">
              <p className="text-white font-bold text-sm tracking-wide">
                RETAIL
              </p>
              <p className="text-xs text-[#0097AC] font-medium">POS</p>
            </div>
          )}
        </div>

        {!collapsed && (
          <button
            type="button"
            onClick={() => setCollapsed(true)}
            className="p-1.5 hover:bg-white/10 text-white/60 hover:text-white rounded transition-colors"
            title="Collapse sidebar"
          >
            <PanelLeftClose size={18} />
          </button>
        )}
      </div>

      {collapsed && (
        <div className="flex justify-center py-3 border-b border-white/10 flex-shrink-0">
          <button
            type="button"
            onClick={() => setCollapsed(false)}
            className="p-2 hover:bg-white/10 text-white/60 hover:text-white rounded transition-colors"
            title="Expand sidebar"
          >
            <Menu size={18} />
          </button>
        </div>
      )}

      <div className="flex-1 overflow-y-auto px-3 py-4 space-y-1">
        {!collapsed && (
          <p className="px-3 text-[10px] font-bold text-[#0097AC] uppercase tracking-widest mb-3">
            Navigation
          </p>
        )}

        <nav className="space-y-1">
          <Link
            href="/"
            className={`relative group flex items-center ${collapsed ? "justify-center" : "gap-3"
              } px-3 py-2.5 text-sm font-medium rounded-lg transition-all ${isActive("") ?
                "bg-[#006E74] text-white shadow-md font-semibold" :
                "text-white/70 hover:text-white hover:bg-white/5"
              }`}
            title={collapsed ? "Home" : undefined}
          >
            <Home
              size={18}
              className={`flex-shrink-0 ${isActive("") ? "text-white" : "text-[#0097AC]"
                }`}
            />
            {!collapsed && <span className="flex-1">Home</span>}
          </Link>

          {loading && (
            <div className="animate-pulse space-y-3 px-3 pt-2">
              {[1, 2, 3, 4].map((i) => (
                <div key={i} className="h-4 bg-white/10 rounded w-3/4" />
              ))}
            </div>
          )}

          {!loading && nodes && nodes.length > 0 && (
            <div className="pt-1 space-y-1">
              {nodes.map((node) => {
                const IconComponent = getIcon(node.identifier);
                const formattedPath = formatPath(node.path);
                const isNodeActive = isActive(node.path);

                return (
                  <Link
                    key={node.id || node.path}
                    href={`/${formattedPath}`}
                    className={`relative group flex items-center ${collapsed ? "justify-center" : "gap-3"
                      } px-3 py-2.5 text-sm font-medium rounded-lg transition-all ${isNodeActive
                        ? "bg-[#006E74] text-white shadow-md font-semibold"
                        : "text-white/70 hover:text-white hover:bg-white/5"
                      }`}
                    title={collapsed ? node.identifier : undefined}
                  >
                    <IconComponent
                      size={18}
                      className={`flex-shrink-0 ${isNodeActive ? "text-white" : "text-[#0097AC]"
                        }`}
                    />

                    {!collapsed && (
                      <span className="flex-1">
                        {node.identifier}
                      </span>
                    )}
                  </Link>
                );
              })}
            </div>
          )}

          {!loading && (!nodes || nodes.length === 0) && (
            <div className="px-3 py-4 text-xs text-white/40 italic">
              No additional modules
            </div>
          )}
        </nav>
      </div>

      <div className="p-3 border-t border-white/10 flex-shrink-0">
        <button
          type="button"
          onClick={onLogout}
          className={`w-full flex items-center justify-center ${collapsed ? "" : "gap-2"
            } px-4 py-2.5 text-xs font-bold text-red-400 hover:bg-red-600 hover:text-white rounded-lg transition-colors`}
        >
          <LogOut size={16} className="flex-shrink-0" />
          {!collapsed && "Logout"}
        </button>
      </div>
    </aside>
  );
}

Sidebar.propTypes = {
  nodes: PropTypes.array,
  loading: PropTypes.bool,
  onLogout: PropTypes.func.isRequired,
  onCollapseChange: PropTypes.func,
};