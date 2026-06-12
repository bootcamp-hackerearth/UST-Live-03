import React, { useEffect, useState } from "react";
import axios from "axios";

import Sidebar from "./Sidebar";
import Header from "./Header";
import Footer from "./Footer";

function POSLayout({ children }) {

  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [profileOpen, setProfileOpen] = useState(false);

  const [nodes, setNodes] = useState([]);
  const [user, setUser] = useState(null);

  const token = localStorage.getItem("token");
  const username = localStorage.getItem("username");

  useEffect(() => {

    axios
      .get("http://localhost:8080/api/home", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then((res) => {

        const data = res.data;

        if (Array.isArray(data)) {

          setNodes(data);

        } else {

          setNodes(data.nodes || []);
          setUser(data.user || {
            name: username,
          });

        }

      })
      .catch((err) => {
        console.log(err);
      });

  }, []);

  const handleLogout = () => {

    localStorage.clear();
    window.location.href = "/login";

  };

  const mainOffset =
    sidebarOpen ? "ml-[220px]" : "ml-16";

  return (

    <div className="min-h-screen bg-[#f0f2f8]">

      {/* Sidebar */}
      <Sidebar
        sidebarOpen={sidebarOpen}
        nodes={nodes}
      />

      {/* Main */}
      <div
        className={`${mainOffset}
        transition-all duration-300
        flex flex-col min-h-screen`}
      >

        {/* Header */}
        <Header
          sidebarOpen={sidebarOpen}
          setSidebarOpen={setSidebarOpen}
          user={user}
          profileOpen={profileOpen}
          setProfileOpen={setProfileOpen}
          handleLogout={handleLogout}
        />

        {/* Content */}
        <main className="flex-1 mt-[60px] p-5">

          {children}

        </main>

        {/* Footer */}
        <Footer />

      </div>

    </div>
  );
}

export default POSLayout;