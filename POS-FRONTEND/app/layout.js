"use client";

import "./globals.css";
import Navbar from "@/components/NavBar";
import SideBar from "@/components/SideBar";
import { usePathname } from "next/navigation";
import { useState } from "react";
import PropTypes from "prop-types";

function LayoutContent({ children }) {
  const [menuOpen, setMenuOpen] = useState(false);
  const pathname = usePathname();

  const showLayout =
    pathname !== "/login" && pathname !== "/register";

  return (
    <>
      {showLayout && <Navbar setMenuOpen={setMenuOpen} />}

      <div className="flex min-h-screen">
        {showLayout && (
          <SideBar
            menuOpen={menuOpen}
            setMenuOpen={setMenuOpen}
          />
        )}

        <main className="flex-1 min-h-screen bg-gray-100">
          {children}
        </main>
      </div>
    </>
  );
}

LayoutContent.propTypes = {
  children: PropTypes.node.isRequired,
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className="min-h-screen bg-gray-100 overflow-x-hidden">
        <LayoutContent>{children}</LayoutContent>
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node.isRequired,
};