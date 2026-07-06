"use client";

import { useRouter, usePathname } from "next/navigation";
import { useEffect, useState, useCallback } from "react";
import PropTypes from "prop-types";
import axios from "axios";
import styles from "./MainLayout.module.css";

const MainLayout = ({ children }) => {
  const router = useRouter();
  const pathname = usePathname();

  const [menu, setMenu] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchMenu = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await axios.get(
        "/api/node/menu",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      console.log("MENU DATA:", res.data);
      setMenu(res.data || []);

    } catch (err) {
      console.error("MENU ERROR:", err);
      setMenu([]);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchMenu();

    const refreshMenu = () => {
      fetchMenu();
    };

    globalThis.addEventListener(
      "menuUpdated",
      refreshMenu
    );

    return () => {
      globalThis.removeEventListener(
        "menuUpdated",
        refreshMenu
      );
    };

  }, [fetchMenu]);

  const menuContent = (() => {
    if (loading) {
      return (
        <div className={styles.loadingBox}>
          Loading...
        </div>
      );
    }

    if (menu.length === 0) {
      return (
        <div className={styles.loadingBox}>
          No menu available
        </div>
      );
    }

    return (
      <ul className={styles.menuList}>
        {menu.map((item) => (
          <li key={item.path || item.identifier}>
            <button
              className={`${styles.menuItem} ${
                pathname === item.path ? styles.activeMenuItem : ""
              }`}
              onClick={() => router.push(item.path)}
            >
              {item.identifier}
            </button>
          </li>
        ))}
      </ul>
    );
  })();

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");

    router.push("/login");
  };

  return (
    <div className={styles.wrapper}>
      {/* SIDEBAR */}
      <aside className={styles.sidebar}>
        {/* TOP SECTION */}
        <div className={styles.topSection}>
          {/* LOGO */}
          <button
            type="button"
            className={styles.logoSection}
            onClick={() => router.push("/dashboard1")}
          >
            <div className={styles.logoCircle}>P</div>

            <div>
              <h2 className={styles.brand}>
                POS System
              </h2>

              <p className={styles.brandSub}>
                Management Panel
              </p>
            </div>
          </button>

          {/* MENU */}
          <div className={styles.menuContainer}>
            <p className={styles.menuLabel}>
              MAIN MENU
            </p>

            {menuContent}
          </div>
        </div>

        {/* LOGOUT BUTTON */}
        <button
          className={styles.logoutBtn}
          onClick={handleLogout}
        >
          Logout
        </button>
      </aside>

      {/* MAIN CONTENT */}
      <main className={styles.main}>
        {children}
      </main>
    </div>
  );
};

export default MainLayout;

MainLayout.propTypes = {
  children: PropTypes.node,
};
