"use client";

import {
  useEffect,
  useState,
  useCallback,
} from "react";

import {
  useRouter,
  usePathname,
} from "next/navigation";

import axios from "axios";
import ProtectedRoute from "@/app/components/ProtectedRoute";
import PropTypes from "prop-types";
const MainLayout = ({ children }) => {
  const router = useRouter();
  const pathname = usePathname();
  const [menu, setMenu] = useState([]);
  const [loading, setLoading] = useState(true);

  /* ================= MENU API ================= */

  const fetchMenu = useCallback(async () => {
  const token = localStorage.getItem("token");

  if (!token) {
    setLoading(false);
    return;
  }

  try {
  const res = await axios.get(
    "/api/node/roles",
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

const menuData = res.data || [];

/**
 * Optional safety normalization (recommended)
 * - removes null identifiers
 * - ensures consistency
 */
const filteredMenu = menuData.filter(
  (item) => item?.identifier && item?.path
);

setMenu(filteredMenu);



  setMenu(filteredMenu);



} catch (err) {

  console.error("MENU ERROR:", err);

} finally {

    setLoading(false);

  }

}, []);



  useEffect(() => {

    fetchMenu();

  }, [fetchMenu]);



  /* ================= LOGOUT ================= */



  const handleLogout = () => {

    localStorage.removeItem("token");

    localStorage.removeItem("username");



    router.push("/login");

  };



  let menuContent;



if (loading) {

  menuContent = (

    <div style={styles.loadingBox}>

      Loading...

    </div>

  );

} else if (menu.length === 0) {

  menuContent = (

    <div style={styles.loadingBox}>

      No menu available

    </div>

  );

} else {

  menuContent = (

    <ul style={styles.menuList}>

      {menu.map((item) => {

        const isActive =

          pathname === item.path;



        return (

          <li key={item.identifier}>

            <button

              onClick={() =>

                router.push(item.path)

              }

              style={{

                ...styles.menuItem,

                ...(isActive

                  ? styles.activeMenu

                  : {}),

              }}

            >

              {item.identifier}

            </button>

          </li>

        );

      })}

    </ul>

  );

}



  return (

  <ProtectedRoute>

    <div style={styles.wrapper}>

      {/* SIDEBAR */}



      <aside style={styles.sidebar}>

        <div>

          {/* LOGO */}



<button

  type="button"

  onClick={() => router.push("/dashboard1")}

  style={{

    ...styles.logoSection,

    cursor: "pointer",

    background: "transparent",

    border: "none",

    padding: 0,

    color: "inherit",

  }}

>

  <div style={styles.logoCircle}>

    P

  </div>



  <div>

    <h2 style={styles.brand}>

      UST POS

    </h2>



    <p style={styles.brandSub}>

      Management Panel

    </p>

  </div>

</button>



          {/* MENU */}



          <div style={styles.menuContainer}>

            <p style={styles.menuLabel}>

              MAIN MENU

            </p>

              {menuContent}

          </div>

        </div>



        {/* LOGOUT BUTTON */}



        <button

          style={styles.logoutBtn}

          onClick={handleLogout}

        >

          Logout

        </button>

      </aside>



      {/* MAIN CONTENT */}



      <main style={styles.main}>

        {children}

      </main>

    </div>

  </ProtectedRoute>

);

};
MainLayout.propTypes = {
  children: PropTypes.node.isRequired,
};



export default MainLayout;



/* ================= STYLES ================= */



const SIDEBAR_WIDTH = "260px";



const styles = {

  /* WRAPPER */



  wrapper: {

    display: "flex",

    minHeight: "100vh",
    background: "#f8fafc",

    fontFamily: "Inter, sans-serif",

  },



  /* SIDEBAR */



  sidebar: {

    width: SIDEBAR_WIDTH,



    background:

      "linear-gradient(180deg, #111827, #1f2937)",




    color: "#fff",



    padding: "24px 18px",



    display: "flex",

    flexDirection: "column",

    justifyContent: "space-between",



    position: "fixed",

    top: 0,

    left: 0,



    height: "100vh",



    boxSizing: "border-box",



    boxShadow:

      "4px 0 20px rgba(78, 55, 208, 0.08)",



    overflowY: "auto",

  },



  /* LOGO */



  logoSection: {

    display: "flex",

    alignItems: "center",

    gap: "12px",

    marginBottom: "30px",

  },



  logoCircle: {

    width: "45px",

    height: "45px",



    borderRadius: "12px",



    background:

      "linear-gradient(135deg, #686bec, #8b5cf6)",



    display: "flex",

    alignItems: "center",

    justifyContent: "center",



    fontWeight: "bold",

    fontSize: "20px",

  },



  brand: {

    margin: 0,

    fontSize: "18px",

    fontWeight: "700",

  },



  brandSub: {

    margin: 0,

    fontSize: "12px",

    color: "#9ca3af",

  },



  /* MENU */



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



    display: "flex",

    alignItems: "center",

    gap: "10px",



    padding: "13px 14px",



    border: "none",

    borderRadius: "12px",



    background: "transparent",



    color: "#f9fafb",



    cursor: "pointer",



    marginBottom: "10px",



    transition: "all 0.3s ease",



    fontSize: "14px",

    fontWeight: "500",



    textAlign: "left",

  },



  activeMenu: {

    background: "rgba(255,255,255,0.12)",

  },



  loadingBox: {

    background: "rgba(255,255,255,0.08)",



    padding: "12px",



    borderRadius: "10px",



    textAlign: "center",



    color: "#d1d5db",



    fontSize: "14px",

  },



  /* LOGOUT */



  logoutBtn: {

    width: "100%",



    padding: "13px",



    borderRadius: "12px",



    border:

      "1px solid rgba(255,255,255,0.1)",



    background: "rgba(255,255,255,0.06)",



    color: "#fff",



    cursor: "pointer",



    fontWeight: "600",

  },



  /* MAIN CONTENT */



  main: {

    marginLeft: SIDEBAR_WIDTH,



    flex: 1,



    minHeight: "100vh",



    padding: "30px",



    boxSizing: "border-box",



    overflowX: "auto",

  },

};

