"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import Header from "./Header";
import Sidebar from "./Sidebar";
import Footer from "./Footer";

export default function DashboardLayout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [username, setUsername] = useState("");
  const [isAuthorized, setIsAuthorized] = useState(false);
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      router.replace("/login");
      return;
    }

    const storedUsername = localStorage.getItem("username");
    if (storedUsername) {
      setUsername(storedUsername);
    }

    setIsAuthorized(true);
  }, []);
  if (!isAuthorized) return null;

  return (
    <div className="flex min-h-screen bg-slate-100">
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

        <main className="flex-1 p-6 overflow-auto">
          {children}
        </main>

        <Footer />
      </div>
    </div>
  );
}

DashboardLayout.propTypes = {
  children: PropTypes.node.isRequired,
};