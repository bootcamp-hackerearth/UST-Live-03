"use client";
import Link from "next/link";
import { useRouter } from "next/navigation";
 
const nodes = [
  { label: "Home", href: "/" },
  { label: "Category", href: "/category" },
  { label: "Product", href: "/product" },
  { label: "Price", href: "/price" },
  { label: "Node", href: "/node" },
  { label: "User", href: "/user" },
  { label: "Role", href: "/role" },
];
 
function Sidebar() {
  const router = useRouter();

 const handleLogout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("username");
  localStorage.removeItem("name");
  router.replace("/login");
};

  return (
    <aside className="flex min-h-screen w-64 shrink-0 flex-col border-r border-white/10 bg-[#111827] p-5 text-white shadow-2xl shadow-slate-300/40">
      <div className="mb-7">
        <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-cyan-500 text-base font-black tracking-wide text-slate-950 shadow-lg shadow-cyan-500/20">
          POS
        </div>
        <h2 className="mt-5 text-base font-bold tracking-wide">Point of Sale</h2>
        <p className="mt-1 text-xs font-medium uppercase text-slate-400">Workspace menu</p>
      </div>
 
      <nav className="flex flex-col gap-1.5">
        {nodes.map((node) => (
          <Link
            key={node.href}
            href={node.href}
            className="w-full rounded-lg px-3.5 py-2.5 text-left text-sm font-semibold text-slate-300 transition hover:bg-white/10 hover:text-white"
          >
            {node.label}
          </Link>
        ))}
      </nav>

      <div className="mt-auto pt-5">
        <button
          type="button"
          onClick={handleLogout}
          className="w-full rounded-lg border border-rose-400/20 bg-rose-500/95 px-4 py-2.5 text-sm font-bold text-white shadow-lg shadow-rose-950/20 transition hover:bg-rose-500"
        >
          Logout
        </button>
      </div>
    </aside>
  );
}
 
export default Sidebar;
