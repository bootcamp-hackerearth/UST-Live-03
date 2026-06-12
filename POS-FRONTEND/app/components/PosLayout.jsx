"use client";

import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";

import Sidebar from "./Sidebar";
import Header from "./Header";
import Footer from "./Footer";

import axiosInstance from "../services/axiosInstance";

function POSLayout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [profileOpen, setProfileOpen] = useState(false);

  const [nodes, setNodes] = useState([]);
  const [user, setUser] = useState(null);

  const router = useRouter();

  useEffect(() => {
    const username = localStorage.getItem("username");

    axiosInstance
      .get("/home")
      .then((res) => {
        const data = res.data;

        if (Array.isArray(data)) {
          setNodes(data);
          setUser({ name: username });
        } else {
          setNodes(data.nodes || []);
          setUser(
            data.user || {
              name: username,
            }
          );
        }
      })
      .catch((err) => {
        console.log(err);
        localStorage.clear();
        router.push("/login");
      });
  }, [router]);

  const handleLogout = () => {
    localStorage.clear();
    router.push("/login");
  };

  const mainOffset = sidebarOpen ? "ml-[220px]" : "ml-16";

  return (
    <div className="min-h-screen bg-[#f0f2f8]">
      <Sidebar sidebarOpen={sidebarOpen} nodes={nodes} />

      <div
        className={`${mainOffset}
        transition-all duration-300
        flex flex-col min-h-screen`}
      >
        <Header
          sidebarOpen={sidebarOpen}
          setSidebarOpen={setSidebarOpen}
          user={user}
          profileOpen={profileOpen}
          setProfileOpen={setProfileOpen}
          handleLogout={handleLogout}
        />

        <main className="flex-1 mt-[60px] p-5">{children}</main>

        <Footer />
      </div>
    </div>
  );
}

POSLayout.propTypes = {
  children: PropTypes.node,
};

export default POSLayout;