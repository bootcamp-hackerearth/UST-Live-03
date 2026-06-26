"use client";

import PropTypes from "../lib/propTypes";
import { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";

export default function SideBar({
  sidebarOpen,
  setSidebarOpen,
}) {
  const [nodes, setNodes] =
    useState([]);

  const router =
    useRouter();

  const pathname =
    usePathname();

  useEffect(() => {
    if (sidebarOpen) {
      fetchNodes();
    }
  }, [sidebarOpen]);

  const fetchNodes =
    async () => {
      try {
        const response =
          await fetch(
            "http://localhost:8080/api/node/getnodesforroles",
            {
              method: "POST",
              headers: {
                "Content-Type":
                  "application/json",
                Authorization:
                  "Bearer " +
                  localStorage.getItem(
                    "token"
                  ),
              },
            }
          );

        const data =
          await response.json();

        setNodes(
          data || []
        );
      } catch (error) {
        console.log(
          "Sidebar Error:",
          error
        );
      }
    };

  const handleNavigation = (
    path
  ) => {
    const finalPath =
      path.startsWith("/")
        ? path
        : `/${path}`;

    router.push(
      finalPath
    );
  };

  return (
    <div
      className={`bg-black text-white h-screen transition-all duration-300 ease-in-out flex flex-col shadow-2xl ${sidebarOpen
          ? "w-[220px]"
          : "w-0"
        } overflow-hidden`}
    >
      <div className="h-[68px] flex items-center justify-between px-5 border-b border-white/10 shrink-0">
        <h2 className="text-2xl font-bold whitespace-nowrap">
          POS
        </h2>

        <button
          onClick={() =>
            setSidebarOpen(
              false
            )
          }
          className="text-white text-xl hover:text-gray-300 transition-all"
        >
          ←
        </button>
      </div>

      <div
        className="
          flex-1
          overflow-y-auto
          overflow-x-hidden
          p-3
        "
        style={{
          scrollbarWidth:
            "thin",
        }}
      >
        {nodes.length ===
          0 ? (
          <div className="text-gray-400 text-sm text-center mt-4">
            No Menu Items
          </div>
        ) : (
          nodes.map(
            (
              node
            ) => {
              const finalPath =
                node.path.startsWith(
                  "/"
                )
                  ? node.path
                  : `/${node.path}`;

              return (
                <button
                  key={finalPath}
                  onClick={() =>
                    handleNavigation(
                      node.path
                    )
                  }
                  className={`w-full text-left px-4 py-3 rounded-xl mb-2 transition-all duration-200 text-sm ${pathname ===
                      finalPath
                      ? "bg-white text-black font-semibold"
                      : "text-gray-300 hover:bg-white hover:text-black"
                    }`}
                >
                  {
                    node.identifier
                  }
                </button>
              );
            }
          )
        )}
      </div>
    </div>
  );
}

SideBar.propTypes = {
  sidebarOpen: PropTypes.bool.isRequired,
  setSidebarOpen: PropTypes.func.isRequired,
};