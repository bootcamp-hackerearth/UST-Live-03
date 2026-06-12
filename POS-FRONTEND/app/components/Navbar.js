"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";

export default function Navbar() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const pathname = usePathname();
  const router = useRouter();

  useEffect(() => {
    setIsLoggedIn(!!localStorage.getItem("token"));
  }, [pathname]);

  function handleLogout() {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    router.push("/login");
  }

  return (
    <nav className="fixed top-0 left-0 right-0 h-[60px] bg-brand flex items-center px-6 z-[200] shadow-[0_2px_8px_rgba(0,0,0,0.15)] font-sans">
      <div className="w-full flex items-center justify-between">
        <Link href="/home" className="flex items-center gap-2.5 no-underline">
          <span className="text-[22px]">🛒</span>
          <span className="text-hash text-lg font-bold text-white">
            RetailPOS
          </span>
        </Link>

        <div className="flex items-center gap-2">
          {isLoggedIn ? (
            <>
              <Link
                href="/profile"
                className="px-3.5 py-1.75 rounded-lg text-sm font-medium text-white/85 no-underline transition-colors hover:text-white"
              >
                Profile
              </Link>
              <button
                onClick={handleLogout}
                className="px-3.5 py-1.75 rounded-lg bg-white/12 text-white border border-solid border-white/30 text-sm font-semibold cursor-pointer transition-colors hover:bg-white/20"
              >
                &larr; Logout
              </button>
            </>
          ) : (
            <>
              <Link
                href="/"
                className="px-3.5 py-1.75 rounded-lg text-sm text-white/85 no-underline transition-colors hover:text-white"
              >
                Login
              </Link>
              <Link
                href="/register"
                className="px-4 py-1.75 rounded-lg bg-white text-brand text-sm font-bold no-underline transition-colors hover:bg-gray-100"
              >
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}