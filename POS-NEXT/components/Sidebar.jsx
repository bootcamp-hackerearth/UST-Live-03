"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import Link from "next/link";
import { SidebarCloseIcon } from "lucide-react";

const Sidebar = ({ sidebarOpen, setSidebarOpen }) => {

  const [nodes, setNodes] = useState([]);
  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";

  useEffect(() => {
    const fetchNodes = async () => {
      try {
        const res = await fetch(
          `${baseUrl}/node/getnodesforroles`,
          {
            method: "POST",
            credentials: "include",
          }
        );

        const data = await res.json();
        setNodes(data);
      } catch (err) {
        console.error(err);
      }
    };

    fetchNodes();
  }, []);

  return (
    <>
      <div
        className={`fixed top-0 left-0 h-full w-60 bg-[#F4F5FB] border-r border-gray-200 shadow-sm transform ${sidebarOpen ? "translate-x-0" : "-translate-x-full"
          } transition-transform duration-300 z-50`}
      >
        <div className="flex justify-between items-center px-5 py-4">
          <h2 className="text-sm font-semibold text-gray-700 tracking-wide">
            Menu
          </h2>

          <button
            onClick={() => setSidebarOpen(false)}
            className="p-2 rounded-lg hover:bg-gray-200 transition"
          >
            <SidebarCloseIcon size={16} className="text-gray-600" />
          </button>
        </div>

        <ul className="px-4 mt-4 space-y-2 text-sm">
          {nodes.map((node) => (
            <li key={node.identifier}>
              <Link
                href={node.path}
                onClick={() => setSidebarOpen(false)}
                className="block px-3 py-2 rounded-xl text-gray-600 hover:bg-white hover:shadow-sm hover:text-gray-800 transition">
                {node.identifier}
              </Link>
            </li>
          ))}
        </ul>
      </div>

      {sidebarOpen && (
        <button
          type="button"
          aria-label="Close sidebar overlay"
          className="fixed inset-0 bg-black/20 backdrop-blur-sm z-40"
          onClick={() => setSidebarOpen(false)}/>
      )}
    </>
  );
};

Sidebar.propTypes = {
  sidebarOpen: PropTypes.bool.isRequired,
  setSidebarOpen: PropTypes.func.isRequired,
};

export default Sidebar;