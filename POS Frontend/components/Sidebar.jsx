"use client";

import { useEffect, useState } from "react";
import { useRouter, usePathname } from "next/navigation";
import PropTypes from "prop-types";

const PATH_MAP = {
  "/brand/list": "/brands",
  "/category/list": "/categories",
  "/models/list": "/models",
  "/unit/list": "/units",
  "/price/list": "/prices",
  "/product/list": "/products",
  "/node/list": "/nodes",
  "/role/list": "/roles",
  "/user/list": "/users",
};

export default function Sidebar({ menuOpen, setMenuOpen }) {
  const [nodes, setNodes] = useState([]);
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (menuOpen) {
      const token = globalThis.window?.localStorage.getItem("token") ?? null;
      fetch("http://localhost:8080/api/nodes/getNodesForRoles", {
        headers: { Authorization: `Bearer ${token}` },
      })
        .then((r) => {
          if (r.status === 401) {
            globalThis.window.location.href = "/login";
            return null;
          }
          return r.json();
        })
        .then((data) => {
          if (data !== null && data !== undefined) setNodes(data);
        })
        .catch(() => {});
    }
  }, [menuOpen]);

  const handleClose = () => setMenuOpen(false);

  if (menuOpen) {
    return (
      <>
        <style>{`
          @import url('https://fonts.googleapis.com/css2?family=Barlow:wght@400;500;600;700;800;900&display=swap');
          .sb-ov-btn{position:fixed;inset:0;background:rgba(0,0,0,0.4);z-index:100;border:none;cursor:default;padding:0;animation:sbf .15s ease}
          @keyframes sbf{from{opacity:0}to{opacity:1}}
          .sb-panel{position:fixed;top:0;left:0;height:100%;width:248px;background:#fff;box-shadow:4px 0 20px rgba(0,0,0,0.12);z-index:101;display:flex;flex-direction:column;font-family:'Barlow',sans-serif;animation:sbs .2s cubic-bezier(.16,1,.3,1)}
          @keyframes sbs{from{transform:translateX(-100%)}to{transform:translateX(0)}}
          .sb-head{height:52px;background:#005dab;display:flex;align-items:center;justify-content:space-between;padding:0 14px;flex-shrink:0}
          .sb-brand{display:flex;align-items:center;gap:8px;font-size:12px;font-weight:800;color:#fff;text-transform:uppercase;letter-spacing:0.05em}
          .sb-dot{width:24px;height:24px;border-radius:4px;background:#e31837;display:flex;align-items:center;justify-content:center;font-size:11px;color:#fff;font-weight:900}
          .sb-x{width:26px;height:26px;border-radius:5px;border:none;background:rgba(255,255,255,0.2);cursor:pointer;font-size:11px;color:#fff;display:flex;align-items:center;justify-content:center;font-weight:700;transition:background .13s}
          .sb-x:hover{background:rgba(255,255,255,0.35)}
          .sb-nav{flex:1;overflow-y:auto;padding:10px 8px}
          .sb-sec{font-size:10px;font-weight:800;letter-spacing:0.1em;text-transform:uppercase;color:#aaa;padding:10px 10px 7px}
          .sb-item{display:flex;align-items:center;gap:9px;padding:8px 11px;border-radius:6px;cursor:pointer;font-size:13px;font-weight:600;color:#444;transition:all .12s;margin-bottom:2px;text-transform:capitalize;background:none;border:none;width:100%;text-align:left;font-family:'Barlow',sans-serif}
          .sb-item:hover{background:#e8f0fa;color:#005dab}
          .sb-item.active{background:#005dab;color:#fff;font-weight:700}
          .sb-item-dot{width:6px;height:6px;border-radius:50%;background:currentColor;flex-shrink:0;opacity:0.45}
          .sb-item.active .sb-item-dot{opacity:1;background:#fff}
          .sb-loading{padding:14px 10px;font-size:12px;color:#aaa;font-weight:500}
          .sb-foot{padding:12px 14px;border-top:1px solid #f0f0f0;font-size:10px;color:#aaa;font-weight:600;text-transform:uppercase;letter-spacing:0.06em}
        `}</style>
        <button
          type="button"
          className="sb-ov-btn"
          onClick={handleClose}
          aria-label="Close navigation overlay"
        />
        <nav className="sb-panel" aria-label="Main navigation">
          <div className="sb-head">
            <div className="sb-brand">
              <div className="sb-dot">P</div>
              POS Enterprise
            </div>
            <button
              className="sb-x"
              onClick={handleClose}
              aria-label="Close menu"
              type="button"
            >
              ✕
            </button>
          </div>
          <div className="sb-nav">
            <div className="sb-sec">Navigation</div>
            {nodes.length === 0 ? (
              <div className="sb-loading">Loading menu…</div>
            ) : (
              nodes.map((node) => {
                const mapped = PATH_MAP[node.path] ?? node.path;
                const isActive = pathname === mapped;
                const handleNav = () => {
                  handleClose();
                  router.push(mapped);
                };
                return (
                  <button
                    key={node.path}
                    type="button"
                    className={`sb-item${isActive ? " active" : ""}`}
                    onClick={handleNav}
                  >
                    <span className="sb-item-dot" />
                    {node.identifier}
                  </button>
                );
              })
            )}
          </div>
          <div className="sb-foot">POS Enterprise © 2026</div>
        </nav>
      </>
    );
  }

  return null;
}

Sidebar.propTypes = {
  menuOpen: PropTypes.bool.isRequired,
  setMenuOpen: PropTypes.func.isRequired,
};
