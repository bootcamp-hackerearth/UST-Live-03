"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { usePathname, useRouter } from "next/navigation";
import Navbar from "@/components/Navbar";
import Sidebar from "@/components/Sidebar";
import "./globals.css";

const PUBLIC_ROUTES = new Set(["/login", "/register"]);

export default function RootLayout({ children }) {
  const pathname = usePathname();
  const router = useRouter();
  const [checked, setChecked] = useState(false);

  const isAuthPage = PUBLIC_ROUTES.has(pathname);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token && !isAuthPage) {
      router.replace("/login");
      return;
    }
    setChecked(true);
  }, [pathname, isAuthPage, router]);

  if (process.env.NODE_ENV !== "production") {
    console.log(`Rendering RootLayout | Path: ${pathname} | Hide Navigation: ${isAuthPage}`);
  }
  if (!checked && !isAuthPage) {
    return (
      <html lang="en">
        <head>
          <title>RetailPOS</title>
          <meta name="description" content="Point of Sale Management System" />
        </head>
        <body style={{ margin: 0, padding: 0 }} />
      </html>
    );
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