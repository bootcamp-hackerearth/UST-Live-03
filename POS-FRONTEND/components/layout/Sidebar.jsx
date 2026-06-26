"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { removeToken } from "@/utils/auth";
import { useAuth } from "@/context/AuthContext";

export default function Sidebar() {

  const pathname = usePathname();
  const router = useRouter();
  const { nodes } = useAuth();

  const handleLogout = () => {
    removeToken();
    router.push("/login");
  };

  return (
    <aside className="w-70 bg-black border-r border-white/10 flex flex-col h-screen">

      <style>{`
        .sidebar-nav::-webkit-scrollbar {
          width: 4px;
        }
        .sidebar-nav::-webkit-scrollbar-track {
          background: transparent;
        }
        .sidebar-nav::-webkit-scrollbar-thumb {
          background: transparent;
          border-radius: 999px;
        }
        .sidebar-nav:hover::-webkit-scrollbar-thumb {
          background: rgba(255,255,255,0.15);
        }
        .sidebar-nav::-webkit-scrollbar-thumb:hover {
          background: rgba(255,255,255,0.3);
        }
      `}</style>

      <Link
        href="/dashboard"
        className={`px-8 py-10 border-b border-white/10 shrink-0 block transition-all duration-200
          ${pathname === "/dashboard"
            ? "bg-white/10"
            : "hover:bg-white/5"
          }`}
      >
        <h1 className="text-4xl font-bold text-white">POS</h1>
        <p className="text-gray-500 mt-2 text-sm">Retail Management System</p>
      </Link>

      <div className="sidebar-nav flex-1 overflow-y-auto p-4 space-y-2">
        {nodes.map((node) => (
          <Link
            key={node.identifier}
            href={node.path}
            className={`block px-5 py-4 rounded-2xl font-medium transition-all border
              ${pathname === node.path
                ? "bg-blue-600 text-white border-blue-400 shadow-lg shadow-blue-500/20"
                : "text-gray-300 border-transparent hover:bg-white/5 hover:border-white/10"
              }`}
          >
            {node.identifier}
          </Link>
        ))}
      </div>

      <div className="p-4 border-t border-white/10 shrink-0">
        <button
          type="button"
          onClick={handleLogout}
          className="w-full bg-red-500 hover:bg-red-600 py-4 rounded-2xl font-medium text-white transition-all"
        >
          Logout
        </button>
      </div>

    </aside>
  );
}