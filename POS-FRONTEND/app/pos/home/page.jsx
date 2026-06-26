// app/pos/home/page.jsx

"use client";

import "../../globals.css";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import { ShoppingCart, Package, Layers, Users, Tag, BarChart2, UserCog, ArrowRight, Clock, AlertCircle, ClipboardList } from "lucide-react";

const MODULE_REGISTRY = [
  {
    key: "pos",
    title: "Sales Terminal",
    description: "Open the POS register, scan products, apply discounts, and process customer orders in real time.",
    path: "/pos/sales/create",
    icon: ShoppingCart,
    actionText: "Open terminal",
    badge: "Live",
    badgeColor: "bg-[#006E74]/10 text-[#006E74] border-[#006E74]/20",
    roles: ["ADMIN", "MANAGER", "CASHIER"],
  },
  {
    key: "orders",
    title: "Orders",
    description: "View and manage all placed orders, payment modes, line items, and order history by customer.",
    path: "/pos/orders",
    icon: ClipboardList,
    actionText: "View orders",
    roles: ["ADMIN", "MANAGER", "CASHIER"],
  },
  {
    key: "customers",
    title: "Customers",
    description: "Browse and manage customer profiles, contact details, and purchase history.",
    path: "/pos/customers",
    icon: Users,
    actionText: "Manage customers",
    roles: ["ADMIN", "MANAGER", "CASHIER"],
  },
  {
    key: "products",
    title: "Products",
    description: "Add, edit, and manage the master product catalogue, SKUs, brands, and stock status.",
    path: "/pos/products",
    icon: Package,
    actionText: "Manage products",
    roles: ["ADMIN", "MANAGER"],
  },
  {
    key: "categories",
    title: "Categories",
    description: "Organise product categories, manage hierarchy, and control category active status.",
    path: "/pos/categories",
    icon: Layers,
    actionText: "Manage categories",
    roles: ["ADMIN", "MANAGER"],
  },
  {
    key: "prices",
    title: "Prices",
    description: "Configure MRP, selling price, cost price, and effective date for each product.",
    path: "/pos/prices",
    icon: Tag,
    actionText: "Manage prices",
    roles: ["ADMIN", "MANAGER"],
  },
  {
    key: "reports",
    title: "Reports",
    description: "View sales summaries, revenue breakdowns, discount reports, and daily transaction logs.",
    path: "/pos/upcoming",
    icon: BarChart2,
    actionText: "View reports",
    badge: "Soon",
    badgeColor: "bg-amber-50 text-amber-600 border-amber-200",
    roles: ["ADMIN", "MANAGER"],
  },
  {
    key: "users",
    title: "User Management",
    description: "Create and manage operator accounts, assign roles, and control system access permissions.",
    path: "/pos/users",
    icon: UserCog,
    actionText: "Manage users",
    badge: "Admin only",
    badgeColor: "bg-red-50 text-red-500 border-red-200",
    roles: ["ADMIN"],
  },
];

const ROLE_STYLES = {
  ADMIN: "bg-red-50 text-red-600 border border-red-200",
  MANAGER: "bg-amber-50 text-amber-600 border border-amber-200",
  CASHIER: "bg-[#006E74]/8 text-[#006E74] border border-[#006E74]/20",
};
const defaultRoleStyle = "bg-gray-100 text-gray-600 border border-gray-200";

function getGreeting() {
  const h = new Date().getHours();
  if (h < 12) return "Good morning";
  if (h < 17) return "Good afternoon";
  return "Good evening";
}

function ModuleTile({ title, description, badge, badgeColor, path, icon: Icon, actionText }) {
  const router = useRouter();
  return (
    <div className="bg-white border border-gray-100 rounded-2xl p-5 flex flex-col justify-between transition-all hover:border-[#006E74]/40 hover:shadow-sm group cursor-default">
      <div>
        <div className="flex justify-between items-start mb-4">
          <div className="w-10 h-10 rounded-xl bg-[#006E74]/8 flex items-center justify-center text-[#006E74] group-hover:bg-[#006E74]/15 transition-colors">
            <Icon size={19} />
          </div>
          {badge && (
            <span className={`text-[9px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full border ${badgeColor || "bg-gray-100 text-gray-500 border-gray-200"}`}>
              {badge}
            </span>
          )}
        </div>
        <h3 className="text-sm font-bold text-[#231F20] group-hover:text-[#006E74] transition-colors">
          {title}
        </h3>
        <p className="text-xs text-gray-400 mt-1.5 leading-relaxed">
          {description}
        </p>
      </div>

      <button
        type="button"
        onClick={() => router.push(path)}
        className="w-full mt-5 flex items-center justify-between text-xs font-bold text-[#006E74] bg-gray-50 group-hover:bg-[#006E74] group-hover:text-white px-3 py-2 rounded-xl transition-all border-none cursor-pointer"
      >
        <span>{actionText}</span>
        <ArrowRight size={13} />
      </button>
    </div>
  );
}

ModuleTile.propTypes = {
  title: PropTypes.string.isRequired,
  description: PropTypes.string.isRequired,
  badge: PropTypes.string,
  badgeColor: PropTypes.string,
  path: PropTypes.string.isRequired,
  icon: PropTypes.elementType.isRequired,
  actionText: PropTypes.string.isRequired,
};

export default function HomePage() {
  const router = useRouter();
  const [name, setName] = useState("");
  const [roles, setRoles] = useState([]);   // string[]
  const [sessionTime, setSessionTime] = useState("");

  useEffect(() => {
    setName(localStorage.getItem("name") || "Operator");

    try {
      const raw = localStorage.getItem("roles");
      if (!raw) { setRoles([]); return; }
      const parsed = JSON.parse(raw);
      if (Array.isArray(parsed)) {
        setRoles(parsed.map((r) => String(r).toUpperCase()));
      } else {
        setRoles([String(parsed).toUpperCase()]);
      }
    } catch {
      const raw = localStorage.getItem("roles") || "";
      setRoles(raw ? [raw.toUpperCase()] : []);
    }

    const now = new Date();
    setSessionTime(
      now.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }) +
      " · " +
      now.toLocaleDateString("en-IN", { dateStyle: "medium" })
    );
  }, []);

  const visibleModules = MODULE_REGISTRY.filter((mod) =>
    roles.some((r) => mod.roles.includes(r))
  );

  const isAdmin = roles.includes("ADMIN");
  const isCashier = roles.includes("CASHIER") && !isAdmin;

  return (
    <div className="bg-[#f4f6f8] p-6 sm:p-8 max-w-7xl mx-auto space-y-7 font-sans text-[#231F20]">

      <div className="bg-[#006E74] rounded-3xl px-7 py-6 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 text-white shadow-lg shadow-[#006E74]/20">
        <div>
          <p className="text-[10px] font-bold text-white/50 uppercase tracking-widest">
            Retail POS · Workspace
          </p>
          <h1 className="text-2xl font-black mt-1">
            {getGreeting()}, {name} 👋
          </h1>
          <div className="flex flex-wrap gap-1.5 mt-2">
            {roles.length > 0 ? (
              roles.map((role) => (
                <span
                  key={role}
                  className={`text-[9px] font-bold uppercase tracking-wider px-2.5 py-1 rounded-full ${ROLE_STYLES[role] || defaultRoleStyle}`}
                >
                  {role}
                </span>
              ))
            ) : (
              <span className="text-[10px] text-white/40">No roles assigned</span>
            )}
          </div>
        </div>

        <div className="flex items-center gap-2 text-[11px] bg-white/10 border border-white/20 px-4 py-2.5 rounded-2xl font-semibold text-white/80 self-start sm:self-auto shrink-0">
          <Clock size={13} className="text-white/60" />
          <span>{sessionTime || "Loading…"}</span>
        </div>
      </div>

      {visibleModules.length === 0 && (
        <div className="bg-white border border-amber-200 rounded-2xl px-6 py-8 flex flex-col items-center gap-3 text-center">
          <AlertCircle size={28} className="text-amber-400" />
          <p className="text-md font-bold text-[#231F20]">No Quick Access modules</p>
          <p className="text-xs text-gray-400">
            You can still access the modules from the sidebar if assigned. 
          </p>
          <p className="text-xs font-bold text-[#231F20]">
            Contact your administrator if needed.
          </p>
        </div>
      )}

      {isCashier && (
        <button
          type="button"
          onClick={() => router.push("/pos/sales/create")}
          className="bg-white border-2 border-[#006E74] rounded-2xl px-6 py-4 flex items-center justify-between cursor-pointer hover:bg-[#006E74]/4 transition-colors group"
        >
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-xl bg-[#006E74] flex items-center justify-center text-white shrink-0">
              <ShoppingCart size={22} />
            </div>
            <div>
              <p className="text-[10px] font-bold text-[#006E74] uppercase tracking-widest">Quick start</p>
              <p className="text-sm font-black text-[#231F20] mt-0.5">Open POS Terminal</p>
              <p className="text-xs text-gray-400 mt-0.5">Start a new sale session immediately</p>
            </div>
          </div>
          <ArrowRight size={18} className="text-[#006E74] group-hover:translate-x-1 transition-transform shrink-0" />
        </button>
      )}

      {visibleModules.length > 0 && (
        <div>
          <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-4 pl-1">
            Your modules · {visibleModules.length} accessible
          </p>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            {visibleModules.map(({ key, ...rest }) => (
              <ModuleTile key={key} {...rest} />
            ))}
          </div>
        </div>
      )}

      <div className="bg-white border border-gray-100 rounded-2xl px-6 py-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div className="flex flex-wrap gap-4">
          {[
            { label: "API", status: "Connected", dot: "bg-emerald-500 animate-pulse" },
            { label: "Session", status: "Active", dot: "bg-[#006E74]" },
            { label: "Auth", status: "Valid", dot: "bg-green-500" },
          ].map((s) => (
            <div key={s.label} className="flex items-center gap-2">
              <div className={`w-2 h-2 rounded-full shrink-0 ${s.dot}`} />
              <span className="text-[10px] font-bold text-gray-400 uppercase tracking-wider">{s.label}</span>
              <span className="text-[10px] font-semibold text-[#231F20]">{s.status}</span>
            </div>
          ))}
        </div>

        <button
          type="button"
          onClick={() => { if (typeof globalThis !== "undefined") globalThis.location?.reload(); }}
          className="text-[10px] font-bold uppercase tracking-wider text-[#006E74] hover:text-[#0097AC] whitespace-nowrap cursor-pointer bg-transparent border-none"
        >
          ↻ Refresh session
        </button>
      </div>

    </div>
  );
}