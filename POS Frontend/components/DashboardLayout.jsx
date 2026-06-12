"use client";

import { useState, useEffect } from "react";
import { useRouter, usePathname } from "next/navigation";
import Link from "next/link";
import PropTypes from "prop-types";
import Sidebar from "./Sidebar";

const PUBLIC_PATHS = new Set(["/login", "/register"]);

export default function DashboardLayout({ children }) {
  const router = useRouter();
  const pathname = usePathname();
  const [username, setUsername] = useState("");
  const [menuOpen, setMenuOpen] = useState(false);
  const isPublic = PUBLIC_PATHS.has(pathname);

  useEffect(() => {
    if (isPublic) return;
    const token = globalThis.window?.localStorage.getItem("token") ?? null;
    if (token === null || token === undefined || token === "") {
      router.replace("/login");
      return;
    }
    setUsername(
      (globalThis.window.localStorage.getItem("username") || "").split("@")[0],
    );
  }, [pathname, isPublic, router]);

  if (isPublic) return <>{children}</>;

  const handleSignOut = () => {
    globalThis.window.localStorage.removeItem("token");
    globalThis.window.localStorage.removeItem("username");
    router.replace("/login");
  };

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');
        .dl-root{font-family:'Barlow',sans-serif;min-height:100vh;background:#f0f2f5;display:flex;flex-direction:column}
        .dl-root *{box-sizing:border-box}
        .dl-bar{height:52px;background:#005dab;border-bottom:2px solid #004a8f;display:flex;align-items:center;justify-content:space-between;padding:0 18px;position:sticky;top:0;z-index:40;box-shadow:0 2px 8px rgba(0,0,0,0.12)}
        .dl-left{display:flex;align-items:center;gap:10px}
        .dl-hbg{width:30px;height:30px;border-radius:5px;border:none;background:rgba(255,255,255,0.15);cursor:pointer;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:4px;transition:background .13s;flex-shrink:0}
        .dl-hbg:hover{background:rgba(255,255,255,0.25)}
        .dl-hbg span{display:block;width:14px;height:2px;background:#fff;border-radius:2px}
        .dl-brand{font-size:13px;font-weight:800;color:#fff;display:flex;align-items:center;gap:8px;text-decoration:none;text-transform:uppercase;letter-spacing:0.05em}
        .dl-brand:hover{opacity:.85}
        .dl-dot{width:24px;height:24px;border-radius:4px;background:#e31837;display:flex;align-items:center;justify-content:center;font-size:11px;color:#fff;font-weight:900}
        .dl-right{display:flex;align-items:center;gap:8px}
        .dl-profile{display:flex;align-items:center;gap:8px;color:inherit;text-decoration:none;cursor:pointer}
        .dl-profile:hover .dl-user{opacity:.85}
        .dl-user{font-size:12px;color:rgba(255,255,255,0.8);font-weight:600;text-transform:capitalize}
        .dl-avatar{width:28px;height:28px;border-radius:50%;background:#e31837;color:#fff;display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:800;border:2px solid rgba(255,255,255,0.3);flex-shrink:0}
        .dl-out{padding:4px 12px;border-radius:5px;border:1.5px solid rgba(255,255,255,0.3);background:transparent;color:#fff;font-size:11px;font-weight:700;cursor:pointer;font-family:'Barlow',sans-serif;transition:all .13s;text-transform:uppercase;letter-spacing:0.04em}
        .dl-out:hover{background:rgba(255,255,255,0.15);border-color:rgba(255,255,255,0.5)}
        .dl-main{flex:1}
      `}</style>
      <div className="dl-root">
        <Sidebar menuOpen={menuOpen} setMenuOpen={setMenuOpen} />
        <header className="dl-bar">
          <div className="dl-left">
            <button
              className="dl-hbg"
              onClick={() => setMenuOpen(true)}
              aria-label="Open menu"
              type="button"
            >
              <span />
              <span />
              <span />
            </button>
            <Link href="/home" className="dl-brand">
              <div className="dl-dot">P</div>
              POS Enterprise
            </Link>
          </div>
          <div className="dl-right">
            <Link
              href="/profile"
              className="dl-profile"
              aria-label="Open profile"
            >
              {username && <span className="dl-user">{username}</span>}
              <div className="dl-avatar">
                {(username || "U").charAt(0).toUpperCase()}
              </div>
            </Link>
            <button className="dl-out" onClick={handleSignOut} type="button">
              Sign out
            </button>
          </div>
        </header>
        <main className="dl-main">{children}</main>
      </div>
    </>
  );
}

DashboardLayout.propTypes = {
  children: PropTypes.node.isRequired,
};
