"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import Header from "./Header";
import Sidebar from "./Sidebar";
import Footer from "./Footer";
import ErrorModal from "./ErrorModal"; // Import your ErrorModal component

export default function DashboardLayout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [username, setUsername] = useState("");
  
  // State to manage the error modal
  const [errorModal, setErrorModal] = useState({ 
    visible: false, 
    status: null, 
    message: "" 
  });

  useEffect(() => {
    const storedUsername = localStorage.getItem("username");
    if (storedUsername) {
      setUsername(storedUsername);
    }

    const handler = (e) => {
      setErrorModal({ 
        visible: true, 
        status: e.detail.status, 
        message: e.detail.message 
      });
    };

   globalThis.addEventListener("show-error", handler);

return () => globalThis.removeEventListener("show-error", handler);
  }, []);

  return (
    <div className="flex min-h-screen bg-gradient-to-br from-[#f8fafc] to-[#eef2ff]">
      
      {/* Global Error Modal Instance */}
      <ErrorModal 
        isOpen={errorModal.visible} 
        code={errorModal.status} 
        message={errorModal.message} 
        onClose={() => setErrorModal({ ...errorModal, visible: false })} 
      />

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