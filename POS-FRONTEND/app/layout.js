"use client";

import { useState, useEffect } from "react";
import {
  usePathname,
  useRouter,
} from "next/navigation";

import {
  Geist,
  Geist_Mono,
} from "next/font/google";

import PropTypes from "prop-types";

import "./globals.css";

import SideBar from "@/components/Sidebar";
import Navbar from "@/components/Navbar";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export default function RootLayout({
  children,
}) {
  const pathname =
    usePathname();

  const router =
    useRouter();

  const [sidebarOpen, setSidebarOpen] =
    useState(true);

  const [isAuthorized, setIsAuthorized] =
    useState(false);

  const publicRoutes = new Set([
    "/login",
    "/register",
  ]);

  useEffect(() => {
    if (
      publicRoutes.has(
        pathname
      )
    ) {
      setIsAuthorized(true);
      return;
    }

    const token =
      localStorage.getItem(
        "token"
      );

    if (!token) {
      router.replace(
        "/login"
      );
      return;
    }

    setIsAuthorized(true);
  }, [pathname, router]);

  const hideLayout =
    pathname === "/login" ||
    pathname === "/register";

  if (
    !isAuthorized &&
    !hideLayout
  ) {
    return (
      <html
        lang="en"
        className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
      >
        <body className="h-screen flex items-center justify-center bg-[#F4F4F4] overflow-hidden">
          <div className="text-lg font-medium text-gray-600">
            Loading...
          </div>
        </body>
      </html>
    );
  }

  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="h-screen overflow-hidden">
        {hideLayout ? (
          children
        ) : (
          <div className="h-screen bg-[#F4F4F4] flex overflow-hidden">
            <SideBar
              sidebarOpen={
                sidebarOpen
              }
              setSidebarOpen={
                setSidebarOpen
              }
            />

            <div className="flex-1 flex flex-col h-screen overflow-hidden">
              <Navbar
                sidebarOpen={
                  sidebarOpen
                }
                setSidebarOpen={
                  setSidebarOpen
                }
              />

              <main className="flex-1 overflow-y-auto overflow-x-hidden p-5">
                {children}
              </main>
            </div>
          </div>
        )}
      </body>
    </html>
  );
}

RootLayout.propTypes = {
  children:
    PropTypes.node
      .isRequired,
};