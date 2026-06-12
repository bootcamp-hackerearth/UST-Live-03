"use client";

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import PropTypes from "prop-types";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Navbar from "@/components/Navbar";
import Sidebar from "@/components/Sidebar";

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
  const [isPathAllowed, setIsPathAllowed] = useState(true);

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

    if (hideLayout) {
      setAuthorized(true);
      setIsPathAllowed(true);
      return;
    }

    const verifyAccess = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/node/list", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            page: 0,
            sizePerPage: 100,
            sortDirection: "ASC",
            sortField: "identifier",
          }),
        });

        const data = await response.json();
        const nodesList = Array.isArray(data) ? data : data.dtoList || [];

        const allowedPaths = [
          "/home",
          "/home/profile",
          ...nodesList.map((node) => {
            const path = node.path || "";
            return path.startsWith("/") ? path.toLowerCase() : `/${path}`.toLowerCase();
          }),
        ];

        const currentNormalizedPath = pathname.toLowerCase();
        const hasAccess = allowedPaths.some((allowedPath) => 
          currentNormalizedPath === allowedPath || currentNormalizedPath.startsWith(allowedPath + "/")
        );

        setIsPathAllowed(hasAccess);
        setAuthorized(true);
      } catch (error) {
        console.error("Authorization Error:", error);
        setAuthorized(true);
        setIsPathAllowed(false);
      }
    };

    verifyAccess();
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
              className={`transition-all duration-300 ${
                sidebarOpen ? "ml-[260px]" : "ml-[88px]"
              }`}
            >
              <Navbar
                sidebarOpen={sidebarOpen}
                setSidebarOpen={setSidebarOpen}
              />

              {isPathAllowed ? (
                children
              ) : (
                <>
                  <style>{`
                    .denied-container {
                      min-height: calc(100vh - 88px);
                      background-color: #f4f5fa;
                      display: flex;
                      flex-direction: column;
                      align-items: center;
                      justify-content: center;
                      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                      padding: 24px;
                      box-sizing: border-box;
                    }
                    .denied-box {
                      text-align: center;
                      max-width: 440px;
                    }
                    .denied-title {
                      font-size: 38px;
                      font-weight: 700;
                      color: #2d2d6e;
                      margin-bottom: 12px;
                      letter-spacing: -0.5px;
                    }
                    .denied-title span {
                      color: #e55555;
                    }
                    .denied-text {
                      font-size: 14px;
                      color: #8888a0;
                      line-height: 1.6;
                      margin-bottom: 24px;
                    }
                    .denied-btn {
                      height: 40px;
                      padding: 0 20px;
                      background: #6c63ff;
                      border: none;
                      border-radius: 8px;
                      color: #ffffff;
                      font-size: 13px;
                      font-weight: 500;
                      cursor: pointer;
                      transition: background-color 0.15s ease;
                    }
                    .denied-btn:hover {
                      background-color: #5850ec;
                    }
                  `}</style>
                  <div className="denied-container">
                    <div className="denied-box">
                      <h1 className="denied-title">Access <span>Denied</span></h1>
                      <p className="denied-text">
                        Your security profile does not have an active node allocation for this management module. Contact your primary administrator to request permission.
                      </p>
                      <button type="button" onClick={() => router.push("/home")} className="denied-btn">
                        Return Home
                      </button>
                    </div>
                  </div>
                </>
              )}
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