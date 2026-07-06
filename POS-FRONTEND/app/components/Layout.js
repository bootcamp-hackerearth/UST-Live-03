"use client";
import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import Sidebar from "./Sidebar";
import Header from "./Header";
import Footer from "./Footer";

const Layout = ({ children, username, onLogout }) => {
  const router = useRouter();
  const [nodes, setNodes] = useState([]);
  const [mounted, setMounted] = useState(false);
  const [authorized, setAuthorized] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      router.replace("/login");
      return;
    }

    setAuthorized(true);
    setMounted(true);
    fetchNodes(token);
  }, []);

  const fetchNodes = async (token) => {
    try {
      const response = await fetch("/api/node/getAll", {
        method: "GET",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (response.status === 401 || response.status === 403) {
        // Token invalid/expired on the server side — force logout
        localStorage.removeItem("token");
        router.replace("/login");
        return;
      }

      const data = await response.json();
      setNodes(data || []);
    } catch (err) {
      console.error("Node fetch error:", err);
    }
  };

  // Don't render any protected content until we've confirmed a token exists
  if (!mounted || !authorized) return null;

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

Layout.propTypes = {
  children: PropTypes.node.isRequired,
  username: PropTypes.string,
  onLogout: PropTypes.func.isRequired,
};

export default Layout;
