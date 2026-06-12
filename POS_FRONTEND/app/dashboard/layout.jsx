"use client";

import PropTypes from "prop-types";
import Sidebar from "@/components/Sidebar";
import Navbar from "@/components/Navbar";
import useAuth from "@/hooks/useAuth";
export default function DashboardLayout({ children }) {
  const authorized = useAuth();

  if (!authorized) {
    return null;
  }
  return (
    <div className="flex h-screen overflow-hidden">

      <Sidebar />

      <div className="flex flex-col flex-1 bg-[#F7F8FA]">

        <Navbar />

        <main className="flex-1 p-5 overflow-y-auto">
          {children}
        </main>

      </div>

    </div>
  );
}

DashboardLayout.propTypes = {
  children: PropTypes.node,
};