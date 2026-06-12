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
    <aside className="w-70 bg-black border-r border-white/10 flex flex-col justify-between">

      <div>
        <div className="px-8 py-10 border-b border-white/10">

          <h1 className="text-4xl font-bold text-white">

            POS

          </h1>

          <p className="text-gray-500 mt-2 text-sm">

            Retail Management System

          </p>
        </div>

        <div className="p-4 space-y-2">

          <Link
            href="/dashboard"
            className={`block px-5 py-4 rounded-2xl font-medium transition-all border
              
              ${
                pathname === "/dashboard"
                  ? "bg-blue-600 text-white border-blue-400 shadow-lg shadow-blue-500/20"
                  : "text-gray-300 border-transparent hover:bg-white/5 hover:border-white/10"
              }
            `}
          >
            Dashboard

          </Link>

          {
            nodes.map((node) => (

              <Link
                key={node.identifier}
                href={node.path}
                className={`block px-5 py-4 rounded-2xl font-medium transition-all border
                  
                  ${
                    pathname === node.path
                      ? "bg-blue-600 text-white border-blue-400 shadow-lg shadow-blue-500/20"
                      : "text-gray-300 border-transparent hover:bg-white/5 hover:border-white/10"
                  }
                `}
              >

                {node.identifier}

              </Link>

            ))
          }
        </div>
      </div>

      <div className="p-4 border-t border-white/10">
        <button
          onClick={handleLogout}
          className="w-full bg-red-500 hover:bg-red-600 py-4 rounded-2xl font-medium text-white transition-all"
        >

          Logout

        </button>
      </div>
    </aside>
  );
}