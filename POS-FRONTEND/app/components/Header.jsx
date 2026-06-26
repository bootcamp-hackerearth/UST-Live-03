"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axiosInstance from "../api/axiosInstance";
 
function Header() {
  const router = useRouter();
  const [displayName, setDisplayName] = useState("");
 
  useEffect(() => {
    const storedName = localStorage.getItem("name");
    const storedUsername = localStorage.getItem("username");

    if (storedName) {
      queueMicrotask(() => setDisplayName(storedName));
      return;
    }

    if (!storedUsername) {
      return;
    }

    const fetchName = async () => {
      try {
        const response = await axiosInstance.get("/user/identifier", {
          params: { username: storedUsername },
        });

        const name = response.data?.name;
        if (name) {
          setDisplayName(name);
          localStorage.setItem("name", name);
        }
      } catch (error) {
        console.error("Unable to fetch user name:", error);
      }
    };

    fetchName();
  }, [displayName]);

  const firstInitial = displayName
    ? displayName.trim().split(" ")[0]?.[0]?.toUpperCase()
    : "P";

  return (
    <header className="sticky top-0 z-10 flex items-center justify-between gap-4 border-b border-slate-200/80 bg-white/90 px-6 py-3 backdrop-blur">
      <div className="flex items-center gap-4">
        <div className="text-lg font-black tracking-wide text-slate-950">POS</div>
        <p className="hidden text-sm font-medium text-slate-500 sm:block">Point of Sale Dashboard</p>
      </div>
      <div className="flex items-center gap-3 text-sm text-slate-600">
        {displayName && (
          <span className="hidden font-semibold text-slate-700 sm:inline">
            {displayName}
          </span>
        )}
        <button
          type="button"
          onClick={() => router.push("/profile")}
          className="flex h-10 w-10 items-center justify-center rounded-full border border-slate-200 bg-slate-50 text-sm font-black text-slate-800 shadow-sm transition hover:border-cyan-300 hover:bg-cyan-50"
          aria-label="Open profile"
        >
          {firstInitial}
        </button>
      </div>
    </header>
  );
}
 
export default Header;
