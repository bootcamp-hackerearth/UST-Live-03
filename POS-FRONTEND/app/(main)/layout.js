"use client";

import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { getNodeRoles } from "@/services/api";
import Header from "../components/Header";
import ProtectedRoute from "../components/ProtectedRoute";

const MainLayout = ({ children }) => {
  const router = useRouter();

  const [menu, setMenu] = useState([]);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token || token === "null" || token === "undefined") {
      return;
    }

    loadMenu();
  }, []);

  const loadMenu = async () => {
    const token = localStorage.getItem("token");

    if (!token || token === "null" || token === "undefined") {
      return;
    }

    try {
      const res = await getNodeRoles();
      console.log("NODE ROLES:", res);
      setMenu(Array.isArray(res) ? res : []);
    } catch (err) {
      console.error(err);
    }
  };

  const logout = () => {
    localStorage.clear();
    router.push("/login");
  };

  const iconMap = {
    Role: "👤",
    User: "👥",
    Node: "☰",
    Product: "📦",
    Price: "₹",
    Warehouse: "🏬",
    Category: "📂",
    Customer: "🧑",
    Cart: "🛒",
    Order: "🧾",
    Racks: "🗄️",
    Shelf: "🪟",
    Unit: "📏",
    Brand: "🏷️",
    Model: "📱",
    Stock: "📊"
  };

  return (
    <ProtectedRoute>
      <div className="min-h-screen bg-slate-100">

        {/* SIDEBAR */}
        <aside
          className="
          group
          fixed
          left-0
          top-0
          h-screen
          bg-slate-950
          text-white
          z-50
          w-16
          hover:w-64
          transition-all
          duration-300
          flex
          flex-col
          overflow-hidden
        "
        >
          <div className="p-4 border-b border-slate-800">

            <div className="flex items-center gap-3">

              {/* LOGO */}
              <button
                onClick={() => router.push("/dashboard1")}
                className="
      h-10 w-10 rounded-xl
      bg-gradient-to-r from-teal-500 to-cyan-600
      flex items-center justify-center
      font-bold
      cursor-pointer
      shrink-0
    "
              >
                P
              </button>

              {/* TEXT (THIS WILL AUTO SHOW ON EXPAND) */}
              <div
                className="
      overflow-hidden
      whitespace-nowrap
      transition-all
      duration-300
      max-w-0
      hover:ml-2
      group-hover:max-w-[200px]
    "
              >
                <h2 className="font-bold">POS System</h2>
                <p className="text-xs text-slate-400">
                  Management Panel
                </p>
              </div>

            </div>

          </div>

          <div
            className="
            flex-1
            py-4
            overflow-y-auto
            scrollbar-hide
          "
          >
            <ul className="space-y-1 px-2">
              {menu.map((item) => (
                <li key={item.identifier}>
                  <button
                    onClick={() => router.push(item.path)}
                    className="
                  w-full
                  flex
                  items-center
                  justify-center
                  group-hover:justify-start
                  gap-4
                  px-4
                  py-3
                  rounded-xl
                  hover:bg-teal-500/10
                  hover:text-teal-400
                  transition-all
                "
                  >
                    <span className="text-lg">
                      {iconMap[item.identifier] || "📄"}
                    </span>

                    <span
                      className="
                      whitespace-nowrap
                      opacity-0
                      w-0
                      overflow-hidden
                      group-hover:w-auto
                      group-hover:opacity-100
                      transition-all
                    "
                    >
                      {item.identifier}
                    </span>
                  </button>
                </li>
              ))}
            </ul>
          </div>

          <div className="p-4 border-t border-slate-800">
            <button
              onClick={logout}
              className="
              w-full
              bg-red-500
              hover:bg-red-600
              py-3
              rounded-xl
              font-medium
            "
            >
              Logout
            </button>
          </div>

        </aside>

        {/* CONTENT */}
        <div className="ml-16 flex flex-col min-h-screen">
          <Header />

          <main className="p-6">
            {children}
          </main>
        </div>

      </div>

    </ProtectedRoute>);

};

MainLayout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default MainLayout;