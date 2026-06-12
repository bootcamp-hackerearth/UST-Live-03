"use client";

import { useEffect, useState, useCallback } from "react";
import PropTypes from "prop-types";
import "./MainLayout.css";
import { getListItems } from "@/services/api";
import { useRouter } from "next/navigation";
import ProtectedRoute from "@/components/ProtectedRoute";

const MainLayout = ({ children }) => {
  const router = useRouter();

  const [menu, setMenu] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchMenu = useCallback(async () => {
    try {
      const res = await getListItems("node");
      setMenu(res || []);
    } catch (err) {
      console.error("MENU ERROR:", err);
      setMenu([]);
    } finally {
      setLoading(false);
    }
  }, []);

useEffect(() => {
  const token = localStorage.getItem("token");
  if (!token) {
    return;
  }
  fetchMenu();
}, [fetchMenu]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    router.push("/login");
  };

  let menuContent;

  if (loading) {
    menuContent = <div className="loadingBox">Loading...</div>;
  } else if (menu.length === 0) {
    menuContent = <div className="loadingBox">No menu available</div>;
  } else {
    menuContent = (
      <ul className="menuList">
        {menu.map((item) => (
          <li key={item.path ?? item.identifier}>
            <button
              className="menuItem"
              onClick={() => router.push(item.path)}
            >
              {item.identifier}
            </button>
          </li>
        ))}
      </ul>
    );
  }

  return (
    <ProtectedRoute>
      <div className="wrapper">
        {/* SIDEBAR */}
        <aside className="sidebar">

          <div className="sidebarContent">
            <button
              type="button"
              className="logoSection"
              onClick={() => router.push("/dashboard1")}
            >
              <div className="logoCircle">P</div>

              <div>
                <h2 className="brand">POS System</h2>
                <p className="brandSub">Management Panel</p>
              </div>
            </button>

            <div className="menuContainer">
              <p className="menuLabel">MAIN MENU</p>

              {menuContent}
            </div>
          </div>

          <button className="logoutBtn" onClick={handleLogout}>
            Logout
          </button>

        </aside>

        {/* MAIN CONTENT */}
        <main className="main">{children}</main>
      </div>
    </ProtectedRoute>
  );
};

MainLayout.propTypes = {
  children: PropTypes.node,
};

export default MainLayout;