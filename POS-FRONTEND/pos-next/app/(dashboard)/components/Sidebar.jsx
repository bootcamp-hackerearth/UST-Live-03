import Link from "next/link";
import PropTypes from "prop-types";

export default function Sidebar({ nodes = [] }) {
  return (
    <aside className="fixed top-0 left-0 h-screen w-16 hover:w-55 bg-[#13151e] flex flex-col z-50 border-r
     border-[#1e2233] transition-all duration-300 overflow-hidden group">
      <div className="flex items-center gap-3 px-4 py-4 border-b border-[#1e2233] min-h-15">
        <div
          className="flex items-center justify-center w-8 h-8 rounded-lg bg-linear-to-br from-blue-500 to-green-400"
        >
          <span className="text-white font-bold">
            {nodes?.length > 0
              ? nodes[0]?.identifier?.charAt(0)?.toUpperCase()
              : "P"}
          </span>
        </div>
          <span
            className="font-bold text-white text-xl tracking-wide opacity-0 group-hover:opacity-100 
                       transition-opacity duration-200 whitespace-nowrap"
          >
          POS
          </span>
      </div>

      <nav className="flex-1 overflow-y-auto hide-scrollbar py-2 px-1">
        <Link
          href="/home"
          className=" flex items-center gap-3 px-4 py-2. text-white"
        >
          <span
          className=" opacity-0 group-hover:opacity-100 transition-opacity whitespace-nowrap"
          >
            Dashboard
          </span>
        </Link>

        {nodes.length > 0 && (
          <>
            <div className="text-[10px] text-[#4a5060] uppercase tracking-widest px-4 pt-3 pb-1">
              Modules
            </div>

            {nodes.map((node) => {
              const frontendPath ="/" + node.path.split("/")[1];
              return (
                <Link
                  key={node.path}
                  href={frontendPath}
                  className="flex items-center gap-3 px-4 py-2.5 text-[#9ca3b0] hover:text-white hover:bg-[#1e2233] transition-colors"
                >
                <span 
                className=" text-sm font-medium whitespace-nowrap opacity-0 group-hover:opacity-100
                            transition-opacity duration-200"
                >
                  {node.identifier}
                </span>
                </Link>);
            })}
          </>
        )}
      </nav>

      <div className="flex items-center gap-3 px-4 py-3 text-[#9ca3b0] border-t border-[#1e2233]">
        <span className="text-sm font-medium">
          Pos System
        </span>
      </div>
    </aside>
  );
}

Sidebar.propTypes = {
  nodes: PropTypes.arrayOf(
    PropTypes.shape({
      identifier: PropTypes.string,
      path: PropTypes.string,
    })),
};