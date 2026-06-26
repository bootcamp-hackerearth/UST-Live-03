"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import axios from "@/config/axiosConfig";

import {
  Home,
  Users,
  Box,
  Layers,
  Grid3X3,
  Settings,
  Tag
} from "lucide-react";

export default function Sidebar() {
  const [nodes, setNodes] = useState([]);
  const [open, setOpen] = useState(false);
  const pathname = usePathname();

  useEffect(() => {
    axios.get("/node/getNodesForRoles")
      .then(res =>
        setNodes((res.data || []).filter(node => node.status === true))
      );
  }, []);

  const iconMap = {
    dashboard: Home,
    user: Users,
    users: Users,
    product: Box,
    products: Box,
    category: Grid3X3,
    categories: Grid3X3,
    role: Settings,
    roles: Settings,
    node: Layers,
    nodes: Layers
  };

  const getIcon = (identifier) => {
    const key = identifier?.toLowerCase();
    return iconMap[key] || Tag;
  };

  return (
    <aside
      onMouseEnter={() => setOpen(true)}
      onMouseLeave={() => setOpen(false)}
      className={`
        h-screen bg-[#111827] text-white
        transition-all duration-300
        ${open ? "w-64" : "w-16"}
        overflow-hidden
      `}
    >

      <div className="h-14 flex items-center px-4 border-b border-white/10">
        <div className="w-8 h-8 rounded-md bg-white/10"></div>

        {open && (
          <span className="ml-3 text-sm font-semibold tracking-wide">
            POS SYSTEM
          </span>
        )}
      </div>

      <nav className="mt-3 space-y-1">

        {nodes.map((node) => {
          const Icon = getIcon(node.identifier);
          const active = pathname === node.path;

          return (
            <Link
              key={node.path || node.identifier}
              href={node.path}
              className={`
                flex items-center gap-3 mx-2 px-3 py-2 rounded-lg
                text-sm transition

                ${active
                  ? "bg-white text-[#111827] font-medium"
                  : "text-slate-300 hover:bg-white/10 hover:text-white"
                }
              `}
            >

              <Icon size={18} />

              {open && (
                <span className="truncate">
                  {node.identifier}
                </span>
              )}

            </Link>
          );
        })}

      </nav>

    </aside>
  );
}