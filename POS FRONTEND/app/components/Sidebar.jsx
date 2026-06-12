"use client";
import { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import axiosInstance from "../api/axiosInstance";

const ICON_MAP = {
  product: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10" />
    </svg>
  ),
  brand: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A2 2 0 013 12V7a4 4 0 014-4z" />
    </svg>
  ),
  category: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zm10 0a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zm10 0a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
    </svg>
  ),
  price: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
    </svg>
  ),
  user: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
    </svg>
  ),
  role: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
    </svg>
  ),
  node: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M13.828 10.172a4 4 0 00-5.656 0l-4 4a4 4 0 105.656 5.656l1.102-1.101m-.758-4.899a4 4 0 005.656 0l4-4a4 4 0 00-5.656-5.656l-1.1 1.1" />
    </svg>
  ),
  shelf: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
    </svg>
  ),
  rack: (
    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-7-4h.01M12 16h.01" />
    </svg>
  ),
};

const DEFAULT_ICON = (
  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
  </svg>
);

function getIcon(node) {
  const key = `${node.identifier} ${node.path}`.toLowerCase();
  const match = Object.keys(ICON_MAP).find((k) => key.includes(k));
  return match ? ICON_MAP[match] : DEFAULT_ICON;
}

function Sidebar() {
  const [nodes, setNodes] = useState([]);
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    const fetchNodes = async () => {
      try {
        const response = await axiosInstance.get("/node/nodeForRole");
        setNodes(response.data);
      } catch (error) {
        console.error("Failed to fetch nodes", error);
      }
    };
    fetchNodes();
  }, []);

  const isActive = (nodePath) => {
    const normalized = nodePath.startsWith("/") ? nodePath : `/${nodePath}`;
    return pathname === normalized || pathname.startsWith(`${normalized}/`);
  };

  return (
    <aside style={{ width: "260px", minHeight: "100vh", flexShrink: 0 }} className="flex flex-col bg-slate-900 shadow-xl">

      {/* Logo */}
      <div className="flex items-center gap-3 px-5 py-6 border-b border-slate-700">
        <div className="flex items-center justify-center rounded-xl bg-blue-600 text-white text-xs font-black shadow-lg" style={{ width: "40px", height: "40px", flexShrink: 0 }}>
          POS
        </div>
        <div>
          <p className="text-sm font-bold text-white">RetailPOS</p>
          <p className="text-xs text-slate-400">Management Panel</p>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 py-4 px-3">
        <p className="px-2 mb-2 text-xs font-semibold uppercase tracking-widest text-slate-500">
          Main Menu
        </p>

        {nodes.map((node) => {
          const active = isActive(node.path);
          return (
            <button
              key={node.identifier}
              onClick={() => router.push(node.path.startsWith("/") ? node.path : `/${node.path}`)}
              style={{ display: "flex", alignItems: "center", width: "100%", gap: "12px", padding: "10px 12px", borderRadius: "10px", marginBottom: "2px", textAlign: "left", fontSize: "14px", fontWeight: 500, transition: "all 0.15s", background: active ? "#2563eb" : "transparent", color: active ? "#ffffff" : "#cbd5e1", cursor: "pointer", border: "none" }}
              onMouseEnter={(e) => { if (!active) { e.currentTarget.style.background = "#1e293b"; e.currentTarget.style.color = "#ffffff"; } }}
              onMouseLeave={(e) => { if (!active) { e.currentTarget.style.background = "transparent"; e.currentTarget.style.color = "#cbd5e1"; } }}
            >
              <span style={{ flexShrink: 0, display: "flex", alignItems: "center" }}>
                {getIcon(node)}
              </span>
              <span style={{ flex: 1, overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                {node.identifier}
              </span>
              {active && (
                <span style={{ flexShrink: 0, width: "6px", height: "6px", borderRadius: "50%", background: "rgba(255,255,255,0.8)" }} />
              )}
            </button>
          );
        })}
      </nav>

      {/* Footer */}
      <div className="border-t border-slate-700 px-5 py-4">
        <p className="text-xs text-slate-500">© {new Date().getFullYear()} RetailPOS</p>
      </div>

    </aside>
  );
}

export default Sidebar;