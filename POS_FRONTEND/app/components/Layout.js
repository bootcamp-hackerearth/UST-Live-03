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

      <Sidebar sidebarOpen={sidebarOpen} />

      <div
        className={`flex-1 flex flex-col transition-all duration-300 ${
          sidebarOpen ? "ml-[260px]" : "ml-[80px]"
        }`}
      >
        <Header
          sidebarOpen={sidebarOpen}
          setSidebarOpen={setSidebarOpen}
          username={username}
        />

        <main className="flex-1 p-6 overflow-y-auto">

          <div className="bg-white rounded-2xl shadow-sm border border-blue-50 p-6 min-h-full">
            {children}
          </div>

        </main>

        <Footer />

      </div>
    </div>
  );
}

DashboardLayout.propTypes = {
  children: PropTypes.node.isRequired,
};