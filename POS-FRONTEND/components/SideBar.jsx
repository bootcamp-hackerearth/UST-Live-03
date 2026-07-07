"use client";

import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function Sidebar({
  menuOpen,
  setMenuOpen,
}) {
  const [nodes, setNodes] = useState([]);
  const router = useRouter();

  useEffect(() => {
    if (menuOpen) {
      fetchNodes();
    }
  }, [menuOpen]);

  const fetchNodes = async () => {
    try {
      const response = await fetch(
        process.env.NEXT_PUBLIC_BASE_URL+"/node/getnodesforroles",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          credentials: "include",
        }
      );

      const data = await response.json();
      setNodes(data);
    } catch (error) {
      console.error("Failed to load sidebar nodes:", error);
    }
  };

  const handleNavigation = (path) => {
    setMenuOpen(false);

    const module = path.split("/")[1];

    router.push(module ? `/${module}` : "/home");

    if (
      typeof globalThis !== "undefined" &&
      typeof globalThis.scrollTo === "function"
    ) {
      globalThis.scrollTo({
        top: 0,
        behavior: "smooth",
      });
    }
  };

  return (
    <div
      className={`bg-slate-800 text-white shadow-xl flex flex-col transition-all duration-300 overflow-hidden h-[calc(100vh-72px)] ${menuOpen ? "w-52" : "w-0"
        }`}
    >
      <div className="flex items-center justify-between px-4 py-3 border-b border-slate-600 min-w-52">
        <span className="text-lg font-semibold">Menu</span>

        <button
          type="button"
          onClick={() => setMenuOpen(false)}
          className="text-xl hover:text-gray-300"
          aria-label="Close sidebar"
        >
          {'\u2190'}
        </button>
      </div>

      <div
        className={`flex-1 min-w-52 ${menuOpen ? "overflow-y-auto" : "overflow-hidden"
          }`}
      >
        {nodes.map((node) => (
          <button
            key={node.identifier ?? node.path ?? node.id}
            type="button"
            onClick={() => handleNavigation(node.path)}
            className="
              w-full
              text-left
              px-5
              py-3
              text-sm
              tracking-wide
              hover:bg-white
              hover:text-black
              transition-all
              duration-200
            "
          >
            {node.identifier}
          </button>
        ))}
      </div>
    </div>
  );
}

Sidebar.propTypes = {
  menuOpen: PropTypes.bool.isRequired,
  setMenuOpen: PropTypes.func.isRequired,
};