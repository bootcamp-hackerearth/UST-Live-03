"use client";

import { usePathname } from "next/navigation";
import Navbar from "@/components/Navbar";
import Sidebar from "@/components/Sidebar";
import PropTypes from "prop-types";
import "./globals.css";
 
export default function RootLayout({ children }) {
  const pathname = usePathname();
  const isAuthRoute = pathname === "/login" || pathname === "/register";

  return (
    <html lang="en">
      <head>
        <title>RetailPOS</title>
        <meta name="description" content="Point of Sale Management System" />
      </head>
      <body style={{ margin: 0, padding: 0 }}>
        {!isAuthRoute && <Navbar />}
        {!isAuthRoute && <Sidebar />}
        {children}
      </body>
    </html>
  );  
}
RootLayout.propTypes = {
  children: PropTypes.node,
};