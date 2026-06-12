"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import {
  LayoutDashboard,
  Users,
  Package,
  Settings,
  LogOut,
} from "lucide-react";
import { useRouter, usePathname } from "next/navigation";
import api from "../services/api";

const Sidebar = ({ sidebarOpen }) => {
  const router = useRouter();
  const pathname = usePathname();

  const [menu, setMenu] = useState([]);
  const [loading, setLoading] = useState(true);

  const iconMap = {
    dashboard: <LayoutDashboard size={20} />,
    users: <Users size={20} />,
    products: <Package size={20} />,
    settings: <Settings size={20} />,
  };

  const fetchNodes = async () => {
    try {
      setLoading(true);
      const response = await api.get("/api/node/getnodesforroles");
      setMenu(response.data || []);
    } catch (error) {
      console.error(
        " Sidebar load error:",
        error.response?.data || error.message
      );
      router.replace("/login");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNodes();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    router.replace("/login");
  };

  let menuContent;

  if (loading) {
    menuContent = (
      <p className="text-blue-300 text-sm">Loading menu...</p>
    );
  } else if (menu.length === 0) {
    menuContent = (
      <p className="text-blue-300 text-sm">No menu available</p>
    );
  } else {
    menuContent = menu.map((item) => {
      const isActive = pathname.startsWith(item.path);

      const key =
        item.identifier || item.path || JSON.stringify(item);

      return (
        <button
          key={key}
          onClick={() => router.push(item.path || "/")}
          className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 ${
            isActive
              ? "bg-white text-blue-900 shadow-md"
              : "bg-white/5 text-blue-100 hover:bg-white/20 hover:text-white"
          }`}
        >
          {iconMap[item.identifier?.toLowerCase()] || (
            <LayoutDashboard size={20} />
          )}

          {sidebarOpen && (
            <span className="text-sm font-medium capitalize">
              {item.identifier?.replaceAll("_", " ")}
            </span>
          )}
        </button>
      );
    });
  }

  return (
    <aside
      className={`fixed top-0 left-0 h-screen flex flex-col border-r border-white/10 transition-all duration-300
      bg-gradient-to-br from-[#020617] via-[#020c2f] to-[#0a1f66] text-white ${
        sidebarOpen ? "w-[260px]" : "w-[80px]"
      }`}
    >
      <div className="h-[75px] flex items-center justify-center border-b border-white/10 shrink-0">
        {sidebarOpen ? (
          <h1 className="text-xl font-bold tracking-wide">
            Dashboard
          </h1>
        ) : (
          <LayoutDashboard size={24} />
        )}
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-3">
        {menuContent}
      </div>

      <div className="p-4 border-t border-white/10 shrink-0">
        <button
          onClick={handleLogout}
          className="w-full flex items-center justify-center gap-2
          bg-white text-blue-900 py-3 rounded-xl font-semibold 
          hover:bg-blue-100 transition"
        >
          <LogOut size={18} />
          {sidebarOpen && "Logout"}
        </button>
      </div>
    </aside>
  );
};

Sidebar.propTypes = {
  sidebarOpen: PropTypes.bool.isRequired,
};

export default Sidebar;