"use client";

import PropTypes from "prop-types";
import { useRouter, usePathname } from "next/navigation";
import {
  ShoppingCart,
  Package,
  Users,
  BarChart2,
  Settings,
  Tag,
  Truck,
  CreditCard,
  ClipboardList,
  Store,
  Layers,
  RotateCcw,
  AlertCircle,
  BookOpen,
  Grid,
  DollarSign,
  UserCheck,
  Warehouse,
  Percent,
  Receipt,
  LayoutDashboard,
} from "lucide-react";


const ICON_MAPPING = [
  {
    icon: LayoutDashboard,
    keywords: ["dashboard", "overview", "home"],
  },
  {
    icon: ShoppingCart,
    keywords: ["sale", "pos", "billing"],
  },
  {
    icon: Package,
    keywords: ["product", "item", "catalogue"],
  },
  {
    icon: Users,
    keywords: ["customer", "client"],
  },
  {
    icon: BarChart2,
    keywords: ["report", "analytic", "stat"],
  },
  {
    icon: Settings,
    keywords: ["setting", "config", "preference"],
  },
  {
    icon: Tag,
    keywords: ["categor", "tag", "label"],
  },
  {
    icon: Truck,
    keywords: ["supplier", "vendor", "purchase"],
  },
  {
    icon: CreditCard,
    keywords: ["payment", "transaction", "card"],
  },
  {
    icon: ClipboardList,
    keywords: ["order", "invoice"],
  },
  {
    icon: Store,
    keywords: ["store", "branch", "outlet"],
  },
  {
    icon: Layers,
    keywords: ["role", "permission", "access"],
  },
  {
    icon: RotateCcw,
    keywords: ["return", "refund", "exchange"],
  },
  {
    icon: Warehouse,
    keywords: ["stock", "inventory", "warehouse"],
  },
  {
    icon: Percent,
    keywords: ["discount", "promo", "coupon", "offer"],
  },
  {
    icon: Receipt,
    keywords: ["receipt", "voucher"],
  },
  {
    icon: UserCheck,
    keywords: ["user", "operator", "staff", "employee"],
  },
  {
    icon: DollarSign,
    keywords: ["price", "rate", "cost", "revenue"],
  },
  {
    icon: BookOpen,
    keywords: ["log", "audit", "history", "trail"],
  },
  {
    icon: Grid,
    keywords: ["master", "node", "module"],
  },
  {
    icon: AlertCircle,
    keywords: ["alert", "notification", "warning"],
  },
];

const getNodeIcon = (identifier = "") => {
  const name = identifier.toLowerCase();

  for (const mapping of ICON_MAPPING) {
    if (mapping.keywords.some((keyword) => name.includes(keyword))) {
      return mapping.icon;
    }
  }

  return Grid;
};

const Sidebar = ({ nodes = [], username, onLogout }) => {
  const router = useRouter();
  const pathname = usePathname();

  return (
    <aside className="w-60 min-w-[240px] bg-white text-slate-800 flex flex-col h-screen border-r border-slate-200 select-none">

      <div className="px-5 py-4 border-b border-slate-100 flex items-center gap-2.5 flex-shrink-0">
        <div className="w-8 h-8 bg-slate-900 rounded-lg flex items-center justify-center flex-shrink-0">
          <ShoppingCart size={15} className="text-white" strokeWidth={2.2} />
        </div>
        <div>
          <p className="text-sm font-bold text-slate-900 leading-none tracking-tight">RetailPOS</p>
          <p className="text-[10px] text-slate-400 font-medium mt-0.5 leading-none">Management Console</p>
        </div>
      </div>
      <div className="px-5 pt-4 pb-1.5 flex-shrink-0">
        <p className="text-[10px] font-bold tracking-widest uppercase text-slate-400">Modules</p>
      </div>
      <nav className="flex-1 px-3 pb-3 overflow-y-auto flex flex-col gap-0.5">
        {nodes.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-10 gap-2 text-slate-300">
            <Grid size={22} />
            <p className="text-xs font-medium text-slate-400">No modules assigned</p>
          </div>
        ) : (
          nodes.map((node) => {
            const isActive = pathname === node.path;
            const Icon = getNodeIcon(node.identifier);

            return (
              <button
                key={node.id}
                onClick={() => router.push(node.path)}
                className={`flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-150 text-left group ${
                  isActive
                    ? "bg-blue-50 text-blue-600"
                    : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                }`}
              >
                <div
                  className={`w-7 h-7 rounded-md flex items-center justify-center flex-shrink-0 transition-colors ${
                    isActive
                      ? "bg-blue-100 text-blue-600"
                      : "bg-slate-100 text-slate-500 group-hover:bg-slate-200 group-hover:text-slate-700"
                  }`}
                >
                  <Icon size={14} strokeWidth={2} />
                </div>

                <span className={`truncate ${isActive ? "font-semibold" : "font-medium"}`}>
                  {node.identifier}
                </span>
                {isActive && (
                  <span className="ml-auto w-1.5 h-1.5 rounded-full bg-blue-500 flex-shrink-0" />
                )}
              </button>
            );
          })
        )}
      </nav>
      <div className="p-3 border-t border-slate-100 flex flex-col gap-2 bg-slate-50/60 flex-shrink-0">

       
      </div>

    </aside>
  );
};

Sidebar.propTypes = {
  nodes: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string.isRequired,
      identifier: PropTypes.string.isRequired,
      path: PropTypes.string,
    })
  ).isRequired,
  username: PropTypes.string,
  onLogout: PropTypes.func.isRequired,
};

export default Sidebar;