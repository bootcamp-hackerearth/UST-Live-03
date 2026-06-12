// app/pos/home/page.jsx

"use client";

import "../../globals.css";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import { Package, Layers, Settings, PlusCircle, ArrowRight, Clock} from "lucide-react";

const NavigationTile = ({ title, description, badge, path, icon: Icon, actionText = "Manage" }) => {
  const router = useRouter();
  
  return (
    <div className="bg-white border border-[#231F20]/10 rounded-xl p-5 flex flex-col justify-between transition-all hover:border-[#006E74] hover:shadow-sm group">
      <div>
        <div className="flex justify-between items-start mb-4">
          <div className="p-2.5 bg-slate-50 border border-[#231F20]/5 rounded-lg text-[#006E74] group-hover:bg-[#006E74]/10 transition-colors">
            <Icon size={20} />
          </div>
          {badge && (
            <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-[#0097AC]/10 text-[#0097AC] border border-[#0097AC]/20">
              {badge}
            </span>
          )}
        </div>
        <h3 className="text-sm font-bold text-[#231F20] tracking-tight group-hover:text-[#006E74] transition-colors">
          {title}
        </h3>
        <p className="text-xs text-[#231F20]/60 mt-1.5 leading-relaxed">
          {description}
        </p>
      </div>

      <button
        onClick={() => router.push(path)}
        className="w-full mt-5 flex items-center justify-between text-xs font-bold text-[#006E74] bg-slate-50 group-hover:bg-[#006E74] group-hover:text-white px-3 py-2 rounded-lg transition-all"
      >
        <span>{actionText}</span>
        <ArrowRight size={14} className="transform transition-transform group-hover:translate-x-0.5" />
      </button>
    </div>
  );
};

NavigationTile.propTypes = {
  title: PropTypes.string.isRequired,
  description: PropTypes.string.isRequired,
  badge: PropTypes.string,
  path: PropTypes.string.isRequired,
  icon: PropTypes.elementType.isRequired,
  actionText: PropTypes.string,
};

export default function HomePage() {
  const [name, setName] = useState("User");
  const [sessionTime, setSessionTime] = useState("");

  useEffect(() => {
    const userName = localStorage.getItem("name") || "Operator";
    setName(userName);
    
    const now = new Date();
    setSessionTime(
      now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + 
      " | " + 
      now.toLocaleDateString()
    );
  }, []);

  return (
    <div className="min-h-screen bg-slate-50 p-6 sm:p-8 max-w-7xl mx-auto space-y-8">
      
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-white border border-[#231F20]/10 rounded-xl p-6 shadow-sm">
        <div>
          <span className="text-[10px] font-bold text-[#006E74] uppercase tracking-widest bg-[#006E74]/10 px-2 py-0.5 rounded">
            Retail Workspace
          </span>
          <h1 className="text-2xl font-bold text-[#231F20] mt-2">
            Welcome back, {name}
          </h1>
          <p className="text-xs text-[#231F20]/60 mt-0.5">
            Navigate through the modules to explore and manage records.
          </p>
        </div>
        
        <div className="flex items-center gap-2 text-xs bg-slate-50 border border-[#231F20]/10 px-3 py-2 rounded-lg text-[#231F20]/70 font-semibold self-stretch sm:self-auto justify-center">
          <Clock size={14} className="text-[#006E74]" />
          <span>Logged in at : {sessionTime || "Establishing..."}</span>
        </div>
      </div>

      <div>
        <h2 className="text-xs font-bold text-[#231F20]/40 uppercase tracking-widest mb-4 pl-1">
          System Quick Access Modules
        </h2>
        
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
          <NavigationTile
            title="Products"
            description="Manage master products list, review layouts, pagination, database records, and control enable/disable status."
            path="/pos/products"
            icon={Package}
            actionText="Manage Products"
            badge="New"
          />

          <NavigationTile
            title="Add Product"
            description="Initialize product details, assign categories, generate SKU codes, and control product Status"
            path="/pos/products/add"
            icon={PlusCircle}
            actionText="Add Product"
          />

          <NavigationTile
            title="Category Framework"
            description="View and manage inventory categories, organize hierarchy, and control category status."
            path="/pos/categories"
            icon={Layers}
            actionText="Manage Categories"
          />

          <NavigationTile
            title="System Parameters"
            description="Manage global configuration settings and system parameters. (Coming soon)"
            path="#"
            icon={Settings}
            actionText="Coming Soon"
            badge="beta"
          />
        </div>
      </div>

      <div className="bg-white border border-[#231F20]/10 rounded-xl p-6 shadow-sm space-y-6">
        <div>
          <h3 className="text-sm font-bold text-[#231F20] tracking-tight">
            System Status Overview
          </h3>
          <p className="text-xs text-[#231F20]/60 mt-0.5">
            Displays the current connection, workspace mode, and authentication status of this session.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-2">
          <div className="bg-slate-50 border border-[#231F20]/5 rounded-lg p-4 flex items-center gap-3">
            <div className="w-2.5 h-2.5 rounded-full bg-emerald-500 animate-pulse flex-shrink-0" />
            <div>
              <p className="text-[11px] font-bold text-[#231F20]/50 uppercase tracking-wider">API Connectivity</p>
              <h4 className="text-xs font-bold text-[#231F20] mt-0.5">Connected • System Operational</h4>
            </div>
          </div>

          <div className="bg-slate-50 border border-[#231F20]/5 rounded-lg p-4 flex items-center gap-3">
            <div className="w-2.5 h-2.5 rounded-full bg-[#006E74] flex-shrink-0" />
            <div>
              <p className="text-[11px] font-bold text-[#231F20]/50 uppercase tracking-wider">Workspace Mode</p>
              <h4 className="text-xs font-bold text-[#231F20] mt-0.5">Backoffice Management</h4>
            </div>
          </div>

           <div className="bg-slate-50 border border-[#231F20]/5 rounded-lg p-4 flex items-center gap-3">
            <div className="w-2.5 h-2.5 rounded-full bg-green-500 flex-shrink-0" />
            <div>
              <p className="text-[11px] font-bold text-[#231F20]/50 uppercase tracking-wider">Authentication Status</p>
              <h4 className="text-xs font-bold text-[#231F20] mt-0.5">Session Valid</h4>
            </div>
          </div>
        </div>

        <div className="border-t border-[#231F20]/10 pt-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs">
          <p className="text-[#231F20]/60 leading-relaxed max-w-4xl">
            <strong>Note:</strong> Changes made to products, categories, or records are applied immediately across the system.          
          </p>
          <button 
            onClick={() => {
              if (typeof globalThis !== "undefined" && globalThis.location) {
                globalThis.location.reload();
              }
            }} 
            className="text-[11px] font-bold uppercase tracking-wider text-[#006E74] hover:text-[#0097AC] whitespace-nowrap self-start sm:self-center cursor-pointer"
          >
            Session Refresh
          </button>
        </div>
      </div>

    </div>
  );
}