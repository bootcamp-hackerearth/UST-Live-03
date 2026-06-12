"use client";
 
import { useEffect, useState, useCallback } from "react";
import PropTypes from "prop-types";
import "./MainLayout.css";
import { getListItems } from "@/services/api";
import { useRouter } from "next/navigation";

/**
 * MainLayout Component
 * @param {React.ReactNode} children - Child components to render
 */
const MainLayout = ({children}) => {
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
    fetchMenu();
  }, [fetchMenu]);
 
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    router.push("/login");
  };
 
  return (
    <div className="wrapper">
 
      {/* SIDEBAR */}
      <aside className="sidebar">
 
        <div>
          <button 
            className="logoSection"
            onClick={() => router.push("/dashboard1")}
            style={{cursor:"pointer", border: "none", background: "transparent", padding: 0}}
            aria-label="Go to dashboard"
            >
          <div className="logoCircle">
              P
          </div>
            <div>
              <h2 className="brand">POS System</h2>
              <p className="brandSub">Management Panel</p>
            </div>
          </button>
 
          <div className="menuContainer">
            <p className="menuLabel">MAIN MENU</p>
 
            {!loading && menu.length === 0 && (
              <div className="loadingBox">No menu available</div>
            )}

            {loading && (
              <div className="loadingBox">Loading...</div>
            )}

            {!loading && menu.length > 0 && (
              <ul className="menuList">
  {menu
    .filter((item) =>
      ["Role", "User", "Node", "Price", "Product", "Category"].includes(
        item.identifier
      )
    )
    .map((item) => {
      const iconMap = {
        Role: "👤",
        User: "👥",
        Node: "☰",
        Category: "▣",
        Product: "◻",
        Price: "₹",
      };

      return (
        <li key={item.identifier}>
          <button
            className="menuItem"
            onClick={() => router.push(item.path)}
          >
            <span className="menuIcon">
              {iconMap[item.identifier] || "📄"}
            </span>

            <span>{item.identifier}</span>
          </button>
        </li>
      );
    })}
</ul>
            )}
          </div>
        </div>
 
        <button className="logoutBtn" onClick={handleLogout}>
          Logout
        </button>
 
      </aside>
 
      {/* MAIN CONTENT */}
      <main className="main">
        {children}
      </main>
 
    </div>
  );
};

MainLayout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default MainLayout;