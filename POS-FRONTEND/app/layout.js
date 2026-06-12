"use client";

import PropTypes from "prop-types";
import { usePathname } from "next/navigation";
import Navbar from "@/components/Navbar";
import Sidebar from "@/components/Sidebar";
import "./globals.css";

export default function RootLayout({ children }) {
  const pathname = usePathname();

  // Condition to check if the current route is either login or register
  const isAuthPage = pathname === "/login" || pathname === "/register";

  if (process.env.NODE_ENV !== "production") {
    console.log(`Rendering RootLayout | Path: ${pathname} | Hide Navigation: ${isAuthPage}`);
  }

  return (
    <html lang="en">
      <head>
        <title>RetailPOS</title>
        <meta name="description" content="Point of Sale Management System" />
      </head>
      <body style={{ margin: 0, padding: 0 }}>
        {!isAuthPage && <Navbar />}
        <div style={{ display: "flex", width: "100%" }}>
          {!isAuthPage && <Sidebar />}
          <main style={{ flex: 1, minWidth: 0 }}>
            {children}
          </main>
        </div>
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node.isRequired,
};