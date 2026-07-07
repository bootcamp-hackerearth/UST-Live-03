"use client";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import { useEffect, useState, useCallback } from "react";
import axios from "axios";

const styles = {
  wrapper: {
    display: "flex",
    minHeight: "100vh",
    width: "100%",
    background: "#f8fafc",
  },

  sidebar: {
    width: "260px",
    background: "linear-gradient(180deg, #111827, #1f2937)",
    color: "#fff",
    padding: "24px 18px",
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-between",
    height: "100vh",
    overflowY: "auto",
  },

  logoSection: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    marginBottom: "30px",
    cursor: "pointer",
  },

  logoCircle: {
    width: "45px",
    height: "45px",
    borderRadius: "12px",
    background: "linear-gradient(135deg, #6366f1, #8b5cf6)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontWeight: "bold",
    fontSize: "20px",
  },

  brand: {
    fontSize: "18px",
    fontWeight: "700",
    margin: 0,
  },

  brandSub: {
    fontSize: "12px",
    color: "#9ca3af",
    margin: 0,
  },

  menuContainer: {
    flex: 1,
  },

  menuLabel: {
    fontSize: "11px",
    color: "#9ca3af",
    marginBottom: "12px",
    letterSpacing: "1px",
  },

  menuList: {
    listStyle: "none",
    padding: 0,
    margin: 0,
  },

  menuItem: {
    width: "100%",
    padding: "13px 14px",
    border: "none",
    borderRadius: "12px",
    background: "transparent",
    color: "#f9fafb",
    cursor: "pointer",
    marginBottom: "10px",
    transition: "0.2s",
    fontSize: "14px",
    fontWeight: "500",
    textAlign: "left",
  },

  loadingBox: {
    padding: "12px",
    color: "#d1d5db",
    fontSize: "14px",
  },

  logoutBtn: {
    width: "100%",
    padding: "13px",
    borderRadius: "12px",
    border: "1px solid rgba(255, 255, 255, 0.1)",
    background: "rgba(255, 255, 255, 0.06)",
    color: "#fff",
    cursor: "pointer",
    fontWeight: "600",
  },

  main: {
    flex: 1,
    padding: "30px",
    background: "#f8fafc",
    minHeight: "100vh",
    overflowX: "hidden",
  },
};

export default function MainLayout({ children }) {
  const router = useRouter();

  const [menu, setMenu] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchMenu = useCallback(async () => {
    try {
      const token = localStorage.getItem("token");

      if (!token) {
        router.push("/login");
        return;
      }

      const res = await axios.get("/api/node/roles", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      console.log("ROLE BASED MENU:", res.data);

      setMenu(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.error("MENU ERROR:", err);

      if (err.response?.status === 401) {
        localStorage.removeItem("token");
        localStorage.removeItem("username");
        router.push("/login");
      }

      setMenu([]);
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    fetchMenu();
  }, [fetchMenu]);

  const handleMenuClick = (path) => {
    if (!path) return;

    const normalizedPath = path.replace(/\/list$/, "");

    router.push(normalizedPath);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    router.push("/login");
  };

  let renderMenu = null;

  if (loading) {
    renderMenu = <div style={styles.loadingBox}>Loading menu...</div>;
  } else if (!menu || menu.length === 0) {
    renderMenu = (
      <div style={styles.loadingBox}>No accessible menu available</div>
    );
  } else {
    renderMenu = (
      <ul style={styles.menuList}>
        {menu.map((item, index) => (
          <li key={item.id ?? item.identifier ?? item.path ?? index}>
            <button
              style={styles.menuItem}
              onMouseEnter={(e) => {
                e.currentTarget.style.background = "rgba(255,255,255,0.08)";
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.background = "transparent";
              }}
              onClick={() => handleMenuClick(item.path)}
            >
              {item.displayName || item.name || item.identifier}
            </button>
          </li>
        ))}
      </ul>
    );
  }

  return (
    <div style={styles.wrapper}>
      <aside style={styles.sidebar}>
        <div>
          <button
            type="button"
            aria-label="Go to dashboard"
            onClick={() => router.push("/dashboard1")}
            style={{
              ...styles.logoSection,
              border: "none",
              background: "transparent",
              padding: 0,
            }}
          >
            <div style={styles.logoCircle}>P</div>

            <div>
              <h2 style={styles.brand}>POS System</h2>
              <p style={styles.brandSub}>Management Panel</p>
            </div>
          </button>

          <div style={styles.menuContainer}>
            <p style={styles.menuLabel}>MAIN MENU</p>

            {renderMenu}
          </div>
        </div>

        <button
          style={styles.logoutBtn}
          onMouseEnter={(e) => {
            e.currentTarget.style.background = "rgba(255,255,255,0.12)";
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.background = "rgba(255,255,255,0.06)";
          }}
          onClick={handleLogout}
        >
          Logout
        </button>
      </aside>

      <main style={styles.main}>{children}</main>
    </div>
  );
}

MainLayout.propTypes = {
  children: PropTypes.node,
};
