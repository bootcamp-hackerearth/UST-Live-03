"use client"

import PropTypes from "prop-types";
import "./globals.css";
import Navbar from "@/components/Navbar";
import { usePathname } from "next/navigation";

export default function RootLayout({ children }) {

  const path = usePathname();
  const flag = ["/login", "/register"].includes(path);

  return (
    < html lang="en">
      <body className="min-h-full flex flex-col">
        {flag ? null : <Navbar />}
        {children}
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node.isRequired,
};
