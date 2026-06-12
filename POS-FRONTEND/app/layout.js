"use client";

import "./globals.css";
import Navbar from "../app/components/Navbar";
import Sidebar from "../app/components/Sidebar";
import { usePathname } from "next/navigation";
import PropTypes from "prop-types";

export default function RootLayout({ children }) {
  const pathname = usePathname();
  const showLayout = !["/login", "/", "/register"].includes(pathname);

  return (
    <html lang="en">
      <body className="min-h-full flex flex-col">
        {showLayout && (
          <>
            <Navbar />
            <Sidebar />
          </>
        )}
        <div style={showLayout ? { paddingLeft: "220px", paddingTop: "60px" } : {}}>
          {children}
        </div>
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node.isRequired,
};