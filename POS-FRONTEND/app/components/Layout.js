"use client";
import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import Sidebar from "./Sidebar";
import Header from "./Header";
import Footer from "./Footer";

const Layout = ({ children, username, onLogout }) => {
  const [nodes, setNodes] = useState([]);
  const [mounted, setMounted] = useState(false); 

  useEffect(() => {
    setMounted(true);
    fetchNodes();
  }, []);

  const fetchNodes = async () => {
    try {
      const token = localStorage.getItem("token");

      console.log("Fetching nodes with token:", token);

      const response = await fetch(
        "http://localhost:8080/api/node/getAll",
        {
          method: "GET",
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      const data = await response.json();
      setNodes(data || []);
    } catch (err) {
      console.error("Node fetch error:", err);
    }
  };


  if (!mounted) return null;

  return (
    <div className="flex h-screen w-screen overflow-hidden bg-slate-50 antialiased">
      
     
      <Sidebar nodes={nodes} username={username} onLogout={onLogout} />

      
      <div className="flex-1 flex flex-col overflow-hidden min-w-0">
        
        <Header username={username} />

    
        <main className="flex-1 overflow-y-auto p-6 bg-slate-50">
          {children}
        </main>

        <Footer />
      </div>
    </div>
  );
};
Layout .propTypes = {
  children: PropTypes.node.isRequired,
  username: PropTypes.string,
  onLogout: PropTypes.func.isRequired,
};

export default Layout;