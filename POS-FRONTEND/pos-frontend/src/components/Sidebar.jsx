import React from "react";
import { Link } from "react-router-dom";

function Sidebar({ sidebarOpen, nodes = [] }) {

  const sidebarWidth = sidebarOpen ? "w-[220px]" : "w-16";

  return (
    <aside
      className={`
        fixed top-0 left-0 h-screen bg-[#13151e] flex flex-col z-50
        border-r border-[#1e2233] transition-all duration-300
        ${sidebarWidth}
      `}
    >

      {/* Logo */}
      <div className="flex items-center gap-3 px-4 py-4 border-b border-[#1e2233] min-h-[60px]">
        <div className="flex items-center justify-center w-8 h-8 rounded-lg bg-gradient-to-br from-blue-500 to-green-400 shrink-0">
          <span className="text-white font-bold">N</span>
        </div>

        {sidebarOpen && (
          <span className="font-bold text-white text-xl tracking-wide">
            POS
          </span>
        )}
      </div>

      {/* Menu */}
      <nav className="flex-1 overflow-y-auto py-2">

        {/* Dashboard */}
        <Link
          to="/home"
          className="flex items-center gap-3 px-4 py-2.5 bg-red-600 text-white"
        >
          {sidebarOpen && (
            <span className="text-sm font-medium">Dashboard</span>
          )}
        </Link>

        {/* Dynamic Nodes */}
        {nodes.length > 0 && (
          <>
            {sidebarOpen && (
              <div className="text-[10px] text-[#4a5060] uppercase tracking-widest px-4 pt-3 pb-1">
                Modules
              </div>
            )}

            {nodes.map((node, idx) => {

              const frontendPath =
                "/" + node.path.split("/")[1] + "s";

              return (
                <Link
                  key={idx}
                  to={frontendPath}
                  className="flex items-center gap-3 px-4 py-2.5 text-[#9ca3b0]
                  hover:text-white hover:bg-[#1e2233] transition-colors"
                >
                  {sidebarOpen && (
                    <span className="text-sm font-medium">
                      {node.identifier}
                    </span>
                  )}
                </Link>
              );
            })}
          </>
        )}
      </nav>

      {/* Bottom */}
      <div
        className="flex items-center gap-3 px-4 py-3 text-[#9ca3b0]
        border-t border-[#1e2233]"
      >
        {sidebarOpen && (
          <span className="text-sm font-medium">
            Pos System
          </span>
        )}
      </div>

    </aside>
  );
}

export default Sidebar;