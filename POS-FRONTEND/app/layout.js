"use client";

import Navbar from "@/components/Navbar";
import Sidebar from "@/components/Sidebar";
import PropTypes from "prop-types";
import { usePathname, useRouter } from "next/navigation";
import { useState, useEffect } from "react";

const PUBLIC_ROUTES = new Set(["/login", "/register"]);
const ERROR_ROUTES = new Set(["/error"]);

export default function RootLayout({ children }) {
  const path = usePathname();
  const router = useRouter();
  const showLayout = !PUBLIC_ROUTES.has(path) && !ERROR_ROUTES.has(path);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token && !PUBLIC_ROUTES.has(path) && !ERROR_ROUTES.has(path)) {
      router.replace("/login");
    }
  }, [path, router]);

  useEffect(() => {
    const originalFetch = globalThis.fetch;
    globalThis.fetch = async (...args) => {
      try {
        const response = await originalFetch(...args);
        const url =
          typeof args[0] === "string" ? args[0] : (args[0]?.url ?? "");
        const isAuthCall = url.includes("/api/authenticate");

        if (!isAuthCall) {
          if (response.status === 401) {
            localStorage.removeItem("token");
            router.replace("/login");
          } else if (response.status === 403) {
            router.replace("/error?status=403");
          }
        }

        return response;
      } catch (error) {
        console.error("Server Error:", error.message);
        throw error;
      }
    };
    return () => {
      globalThis.fetch = originalFetch;
    };
  }, [router]);

  return (
    <html lang="en">
      <body
        style={{
          margin: 0,
          padding: 0,
          overflow: "hidden",
          height: "100vh",
          backgroundColor: "#f5f5f5",
        }}
      >
        {showLayout && (
          <>
            <Navbar />
            <Sidebar onToggle={setSidebarOpen} />
          </>
        )}
        <div
          style={{
            position: "fixed",
            top: showLayout ? "70px" : "0px",
            left: 0,
            right: 0,
            bottom: 0,
            marginLeft: showLayout && sidebarOpen ? "250px" : "0px",
            transition: "margin-left 0.3s",
            overflowY: "auto",
            overflowX: "hidden",
            boxSizing: "border-box",
            backgroundColor: "#f5f5f5",
          }}
        >
          {children}
        </div>
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node.isRequired,
};