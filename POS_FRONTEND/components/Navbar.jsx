"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { User, LogOut } from "lucide-react";

export default function Navbar() {

  const router = useRouter();

  const [username, setUsername] = useState("");
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const name = localStorage.getItem("username");
    setUsername(name || "User");
  }, []);

  const logout = () => {
    localStorage.clear();
    fetch("/auth/logout", { method: "POST", credentials: "include" });
    router.push("/login");
  };

  return (
    <header
      className="
      h-14 px-6
      bg-white border-b border-slate-200
      flex items-center justify-between
    "
    >

      <div className="text-sm font-medium text-slate-700">
        POS System
      </div>

      <div className="relative">

        <button
          onClick={() => setOpen(!open)}
          className="
            flex items-center gap-2
            px-3 py-1.5
            border border-slate-200
            rounded-full hover:bg-slate-50
          "
        >
          <User size={16} />

          <span className="text-xs text-slate-600">
            {username}
          </span>

        </button>

        {open && (
          <div
            className="
              absolute right-0 mt-2 w-40
              bg-white border border-slate-200
              rounded-lg shadow-md overflow-hidden
            "
          >

            <button
              onClick={() => {
                setOpen(false);
                router.push("/dashboard/profile");
              }}
              className="
                w-full px-3 py-2
                text-left text-sm
                hover:bg-slate-100
                flex items-center gap-2
              "
            >
              <User size={14} />
              Profile
            </button>

            <button
              onClick={logout}
              className="
                w-full px-3 py-2
                text-left text-sm
                hover:bg-slate-100
                flex items-center gap-2
                text-red-500
              "
            >
              <LogOut size={14} />
              Logout
            </button>

          </div>
        )}

      </div>

    </header>
  );
}