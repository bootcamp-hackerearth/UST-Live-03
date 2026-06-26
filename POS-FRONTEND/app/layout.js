"use client";

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import PropTypes from "prop-types";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Navbar from "@/components/Navbar";
import Sidebar from "@/components/Sidebar";
import RouteGuard from "@/components/RouteGuard";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export default function RootLayout({ children }) {
  const pathname = usePathname();
  const router = useRouter();
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [authorized, setAuthorized] = useState(false);

  const hideLayout =
    pathname === "/" || pathname === "/login" || pathname === "/register";

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      if (!hideLayout) {
        setAuthorized(false);
        router.replace("/login");
      }
      return;
    }

    setAuthorized(true);
  }, [pathname, hideLayout, router]);

  if (!authorized && !hideLayout) {
    return (
      <html lang="en" className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}>
        <body className="min-h-full flex items-center justify-center bg-[#f4f5fa]">
          <style>{`
            .g-spinner {
              width: 36px;
              height: 36px;
              border: 3px solid #ebebf5;
              border-top-color: #6c63ff;
              border-radius: 50%;
              animation: g-spin 0.8s linear infinite;
            }
            @keyframes g-spin {
              to { transform: rotate(360deg); }
            }
          `}</style>
          <div className="g-spinner" />
        </body>
      </html>
    );
  }

  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="min-h-full flex flex-col">
        {!hideLayout && (
          <>
            <Sidebar
              sidebarOpen={sidebarOpen}
              setSidebarOpen={setSidebarOpen}
            />

            <div
              className={`transition-all duration-300 ${sidebarOpen ? "ml-[260px]" : "ml-[88px]"
                }`}
            >
              <Navbar
                sidebarOpen={sidebarOpen}
                setSidebarOpen={setSidebarOpen}
              />

              <RouteGuard>
                {children}
              </RouteGuard>
            </div>
          </>
        )}

        {hideLayout && children}
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children: PropTypes.node.isRequired,
};