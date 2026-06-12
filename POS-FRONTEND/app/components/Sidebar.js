"use client";

import { useState, useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import api from "./Axios";

const SIDEBAR_WIDTH = "220px";

export default function Sidebar() {
  const [nodes, setNodes] = useState([]);
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    async function fetchNodes() {
      try {
        const res = await api.get("/node/getNodesForRoles");
        setNodes(res.data || []);
      } catch (err) {
        console.error("Failed to fetch nodes:", err);
      }
    }
    fetchNodes();
  }, []);

  return (
    <aside className="fixed top-[60px] left-0 w-[220px] h-[calc(100vh-60px)] bg-[#f8faf8] border-r border-solid border-[#d8e8d8] flex flex-col font-sans z-100 overflow-y-auto">
      <div className="px-4 pt-4.5 pb-2.5 text-[11px] font-bold text-gray-400 uppercase tracking-wider border-b border-solid border-[#e8ece8] mb-2">
        Navigation
      </div>
      <div className="flex flex-col px-2.5 gap-0.5 flex-1">
        {nodes.map((node) => {
          const isActive = pathname === node.path;
          return (
            <button
              key={node.path || node.identifier}
              type="button"
              onClick={() => router.push(node.path)}
              className={`px-3.5 py-2.5 rounded-lg text-sm font-medium border-none cursor-pointer text-left w-full transition-colors flex items-center gap-2 ${
                isActive
                  ? "bg-[#e8f5e9] text-brand font-semibold"
                  : "bg-transparent text-gray-800 hover:bg-gray-100"
              }`}
            >
              {node.identifier}
            </button>
          );
        })}
      </div>
      <div className="px-4 py-3.5 border-t border-solid border-[#e8ece8] text-[11px] text-gray-400">
        RetailPOS &copy; 2025
      </div>
    </aside>
  );
}

export { SIDEBAR_WIDTH };