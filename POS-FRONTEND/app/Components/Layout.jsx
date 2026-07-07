"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axiosInstance from "../api/axiosInstance";
import Header from "./Header";
import Sidebar from "./Sidebar";
import Footer from "./Footer";
import PropTypes from "prop-types";

function Layout({ children, user, nodes, logout }) {
  const router = useRouter();

  const [layoutUser, setLayoutUser] = useState(user || null);
  const [layoutNodes, setLayoutNodes] = useState(
    Array.isArray(nodes) ? nodes : []
  );

  const activeLogout =
    logout ||
    (() => {
      localStorage.clear();
      router.push("/Login");
    });

  useEffect(() => {
    if (user) {
      setLayoutUser(user);
    }
  }, [user]);

  useEffect(() => {
    if (Array.isArray(nodes)) {
      setLayoutNodes(nodes);
    } else if (Array.isArray(nodes?.dtoList)) {
      setLayoutNodes(nodes.dtoList);
    }
  }, [nodes]);

  useEffect(() => {
    if (user) return;

    const fetchProfile = async () => {
      try {
        const response = await axiosInstance.get("/user/Profile");
        setLayoutUser(response.data);
      } catch (error) {
        console.error("Profile fetch failed:", error);
      }
    };

    fetchProfile();
  }, [user]);

  useEffect(() => {
    if (nodes) return;

    const fetchNodes = async () => {
      try {
        const response = await axiosInstance.get("/node/getNodesForRoles");
        setLayoutNodes(response.data.dtoList || response.data || []);
      } catch (error) {
        console.error("Node fetch error:", error);

        if (
          error.response?.status === 401 ||
          error.response?.status === 403
        ) {
          activeLogout(); 
        }
      }
    };

    fetchNodes();
  }, [nodes, router]); 

  return (
    <div
      style={{
        display: "flex",
        height: "100vh",
        overflow: "hidden",
      }}
    >
      <Sidebar nodes={layoutNodes} user={layoutUser} />

      <div
        style={{
          flex: 1,
          display: "flex",
          flexDirection: "column",
          overflow: "hidden",
        }}
      >
        <Header
          user={layoutUser}
          logout={activeLogout}
          router={router}
        />

        <div
          style={{
            flex: 1,
            padding: "30px",
            overflowY: "auto",
            background: "#f8fafc",
          }}
        >
          {children}
        </div>

        {/* FOOTER */}
        <Footer />
      </div>
    </div>
  );
}

Layout.propTypes = {
  children: PropTypes.node.isRequired,
  user: PropTypes.object,
  nodes: PropTypes.oneOfType([
    PropTypes.array,
    PropTypes.object,
  ]),
  logout: PropTypes.func,
};

export default Layout;