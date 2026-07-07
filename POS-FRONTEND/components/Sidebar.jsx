"use client";

import React, { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import PropTypes from "prop-types";
import {
  XMarkIcon,
  ArrowLeftStartOnRectangleIcon,
  CubeIcon,
  TagIcon,
  CurrencyRupeeIcon,
  UserGroupIcon,
  ChartBarIcon,
  KeyIcon,
  Squares2X2Icon,
} from "@heroicons/react/24/outline";

export default function Sidebar({ sidebarOpen, setSidebarOpen }) {
  const [nodes, setNodes] = useState([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();
  const pathname = usePathname();

  const fetchNodes = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) return;

      const response = await fetch(process.env.NEXT_PUBLIC_BASE_URL+"/node/getnodesforroles", {
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

      const text = await response.text();
      const data = text ? JSON.parse(text) : {};
      const nodeList = Array.isArray(data) ? data : [];
      setNodes(nodeList);
    } catch (error) {
      console.error("Failed fetching nodes:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNodes();

    globalThis.addEventListener("nodeDataChanged", fetchNodes);
    return () => {
      globalThis.removeEventListener("nodeDataChanged", fetchNodes);
    };
  }, []);

  const handleNavigation = (path) => {
    const finalPath = path.startsWith("/") ? path : `/${path}`;
    router.push(finalPath);
  };

  const getIcon = (identifier) => {
    const key = identifier?.toLowerCase()?.trim();
    switch (key) {
      case "dashboard":
        return ChartBarIcon;
      case "cart":
        return Squares2X2Icon;
      case "product":
      case "products":
        return CubeIcon;
      case "category":
      case "categories":
        return TagIcon;
      case "user":
      case "users":
        return UserGroupIcon;
      case "node":
      case "nodes":
        return Squares2X2Icon;
      case "price":
      case "prices":
        return CurrencyRupeeIcon;
      case "role":
      case "roles":
        return KeyIcon;
      default:
        return Squares2X2Icon;
    }
  };

  return (
    <>
      <button
        type="button"
        onClick={() => setSidebarOpen(false)}
        onKeyDown={(e) => e.key === "Escape" && setSidebarOpen(false)}
        aria-label="Close sidebar overlay"
        className={`fixed inset-0 z-40 bg-black/15 backdrop-blur-xs transition-all duration-300 lg:hidden ${sidebarOpen ? "opacity-100 visible" : "opacity-0 invisible"
          }`}
      />

      <aside
        className={`fixed top-0 left-0 h-screen bg-white border-r border-[#ebebf5] z-50 flex flex-col box-border transition-all duration-300 cubic-bezier(0.4, 0, 0.2, 1) lg:translate-x-0 ${sidebarOpen ? "w-[260px]" : "w-[88px] max-lg:-translate-x-full"
          }`}
      >
        <div className="h-[88px] flex items-center justify-between px-5 box-border">
          <button
            type="button"
            onClick={() => setSidebarOpen(!sidebarOpen)}
            className="flex items-center gap-3.5 bg-transparent border-none p-0 cursor-pointer text-left"
          >
            <div className="w-11 h-11 rounded-xl bg-[#6c63ff] flex items-center justify-center shrink-0 shadow-sm shadow-[#6c63ff]/20">
              <Squares2X2Icon className="w-5 h-5 text-white" />
            </div>
            {sidebarOpen && (
              <h1 className="font-sans text-lg font-bold tracking-tight text-[#2d2d6e]">
                POSFlow
              </h1>
            )}
          </button>

          {sidebarOpen && (
            <button
              type="button"
              onClick={() => setSidebarOpen(false)}
              className="bg-[#f8f8fc] border border-[#ebebf5] rounded-lg w-8 h-8 flex items-center justify-center text-[#8888a0] hover:text-[#6c63ff] hover:border-[#6c63ff] transition-all lg:hidden"
            >
              <XMarkIcon className="w-4 h-4" />
            </button>
          )}
        </div>


        <div className="flex-1 px-3.5 py-5 overflow-y-auto box-border no-scrollbar">
          <nav className="flex flex-col gap-2">
            {loading ? (
              [1, 2, 3, 4, 5].map((n) => (
                <div
                  key={`skeleton-${n}`}
                  className="w-full h-12 rounded-xl bg-gradient-to-r from-[#f4f5fa] via-[#ebebf5] to-[#f4f5fa] bg-[length:200%_100%] animate-[pulse_1.5s_infinite]"
                />
              ))
            ) : (
              nodes.map((node) => {
                const Icon = getIcon(node.identifier);
                const finalPath = node.path.startsWith("/") ? node.path : `/${node.path}`;
                const isActive = pathname === finalPath;

                return (
                  <button
                    type="button"
                    key={node.identifier || node.id}
                    onClick={() => handleNavigation(node.path)}
                    className={`w-full h-12 rounded-xl px-4 flex items-center gap-3.5 border-none text-left bg-transparent cursor-pointer box-border transition-all ${isActive
                      ? "bg-[#f4f5fa] text-[#6c63ff] font-semibold"
                      : "text-[#8888a0] hover:bg-[#f8f8fc] hover:text-[#2d2d6e]"
                      }`}
                  >
                    <Icon
                      className={`w-5 h-5 shrink-0 transition-colors ${isActive ? "text-[#6c63ff]" : "text-[#b0b0c8] group-hover:text-[#6c63ff]"
                        }`}
                    />
                    {sidebarOpen && (
                      <span className="font-sans text-sm tracking-wide font-medium whitespace-nowrap">
                        {node.identifier}
                      </span>
                    )}
                  </button>
                );
              })
            )}
          </nav>
        </div>


        <div className="p-4 border-t border-[#ebebf5] box-border">
          <button
            type="button"
            onClick={() => {
              localStorage.clear();
              router.push("/login");
            }}
            className="w-full h-12 rounded-xl px-4 flex items-center gap-3.5 border-none bg-transparent text-[#8888a0] hover:bg-red-50/60 hover:text-[#e55555] cursor-pointer box-border transition-all group"
          >
            <ArrowLeftStartOnRectangleIcon className="w-5 h-5 shrink-0 text-[#b0b0c8] group-hover:text-[#e55555] transition-colors" />
            {sidebarOpen && (
              <span className="font-sans text-sm tracking-wide font-medium whitespace-nowrap">
                Logout
              </span>
            )}
          </button>
        </div>
      </aside>
    </>
  );
}

Sidebar.propTypes = {
  sidebarOpen: PropTypes.bool.isRequired,
  setSidebarOpen: PropTypes.func.isRequired,
};