"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import Header from "./Header";
import Sidebar from "./Sidebar";
import Footer from "./Footer";

export default function DashboardLayout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [username, setUsername] = useState("");

  useEffect(() => {
    const storedUsername = localStorage.getItem("username");
    if (storedUsername) {
      setUsername(storedUsername);
    }
  }, []);

  return (
    <div className="flex min-h-screen bg-gradient-to-br from-[#f8fafc] to-[#eef2ff]">

      {/* SIDEBAR */}
      <Sidebar sidebarOpen={sidebarOpen} />

      {/* MAIN SECTION */}
      <div
        className={`flex-1 flex flex-col transition-all duration-300 ${
          sidebarOpen ? "ml-[260px]" : "ml-[80px]"
        }`}
      >
        {/* HEADER */}
        <Header
          sidebarOpen={sidebarOpen}
          setSidebarOpen={setSidebarOpen}
          username={username}
        />

        {/* PAGE CONTENT */}
        <main className="flex-1 p-6 overflow-y-auto">

          {/* ✅ Content Wrapper (REAL APP STYLE) */}
          <div className="bg-white rounded-2xl shadow-sm border border-blue-50 p-6 min-h-full">
            {children}
          </div>

        </main>

        {/* FOOTER */}
        <Footer />

      </div>
    </div>
  );
}

DashboardLayout.propTypes = {
  children: PropTypes.node.isRequired,
};