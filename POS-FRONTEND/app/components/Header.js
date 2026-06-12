"use client";

import PropTypes from "prop-types";
import { Menu, User } from "lucide-react";
import { useRouter } from "next/navigation";

const Header = ({ sidebarOpen, setSidebarOpen, username }) => {
  const router = useRouter();

  return (
    <header className="h-[75px] bg-white border-b border-slate-200 flex items-center justify-between px-6 shadow-sm">

      <button
        onClick={() => setSidebarOpen(!sidebarOpen)}
        className="w-10 h-10 bg-slate-900 hover:bg-slate-800 text-white rounded-lg flex items-center justify-center transition"
      >
        <Menu size={20} />
      </button>

      <button
        onClick={() => router.push("/profile")}
        className="flex items-center gap-2 cursor-pointer hover:bg-slate-100 px-3 py-2 rounded-lg transition"
      > 
         
           <User size={20} className="text-slate-700" />
        <div className="text-sm text-slate-600">
          <span className="text-xs">Logged in as</span>
          <br />
          <span className="font-semibold text-slate-900">
            {username || "Guest"}
          </span>
        </div>
      </button>

    </header>
  );
};

Header.propTypes = {
  sidebarOpen: PropTypes.bool.isRequired,
  setSidebarOpen: PropTypes.func.isRequired,
  username: PropTypes.string,
};

export default Header;