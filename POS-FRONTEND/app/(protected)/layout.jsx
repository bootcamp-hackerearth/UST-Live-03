"use client";

import PropTypes from "prop-types";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { getToken } from "@/utils/auth";

import Sidebar from "@/components/layout/Sidebar";
import Navbar from "@/components/layout/Navbar";

import { AuthProvider } from "@/context/AuthContext";

export default function ProtectedLayout({
  children,
}) {

  const router = useRouter();

  useEffect(() => {

    const token = getToken();

    if (!token) {

      router.push("/login");

    }

  }, []);

  return (

    <AuthProvider>

      <div className="flex h-screen bg-[#f4f7fb] overflow-hidden">

        <Sidebar />

        <div className="flex-1 flex flex-col overflow-hidden">

          <Navbar />

          <main className="flex-1 overflow-y-auto bg-[#f4f7fb]">
            <div className="max-w-7xl mx-auto px-8 py-8">
              {children}
            </div>
          </main>

        </div>

      </div>

    </AuthProvider>

  );

}

ProtectedLayout.propTypes = {
  children: PropTypes.node.isRequired,
};