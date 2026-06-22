"use client";

import { useEffect, useState, useCallback } from "react";
import { useRouter, usePathname } from "next/navigation";
import PropTypes from "prop-types";
import { BASE } from "@/lib/api";
import { STORAGE_KEYS, PATHS } from "@/config/constants";

const getStorageItem = (key) => {
  try {
    if (globalThis.window?.localStorage) {
      return globalThis.window.localStorage.getItem(key);
    }
  } catch {
    return null;
  }
  return null;
};

const PATH_MAP = {
  "/brand/list": PATHS.BRANDS,
  "/category/list": PATHS.CATEGORIES,
  "/models/list": PATHS.MODELS,
  "/unit/list": PATHS.UNITS,
  "/price/list": PATHS.PRICES,
  "/product/list": PATHS.PRODUCTS,
  "/node/list": PATHS.NODES,
  "/role/list": PATHS.ROLES,
  "/user/list": PATHS.USERS,
};

export default function Sidebar({ menuOpen, setMenuOpen }) {
  const [nodes, setNodes] = useState([]);
  const [loading, setLoading] = useState(false);
  const router = useRouter();
  const pathname = usePathname();

  const handleClose = useCallback(() => {
    setMenuOpen(false);
  }, [setMenuOpen]);

  const fetchNodes = useCallback(async () => {
    if (!menuOpen) return;
    
    setLoading(true);
    try {
      const token = getStorageItem(STORAGE_KEYS.TOKEN);
      if (!token) {
        return;
      }

      const response = await fetch(`${BASE}/api/nodes/getNodesForRoles`, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (response.status === 401) {
        if (globalThis.window !== undefined) {
          globalThis.window.location.href = PATHS.LOGIN;
        }
        return;
      }

      if (!response.ok) {
        return;
      }

      const data = await response.json();
      if (data && Array.isArray(data)) {
        setNodes(data);
      }
    } catch {
    } finally {
      setLoading(false);
    }
  }, [menuOpen]);

  useEffect(() => {
    if (globalThis.window !== undefined) {
      fetchNodes();
    }
  }, [menuOpen, fetchNodes]);

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
            {loading ? (
              <div className="sb-loading">Loading menu…</div>
            ) : null}
            {nodes.length === 0 && !loading ? (
              <div className="sb-loading">No menu items available</div>
            ) : null}
            {nodes.length > 0 && !loading && (
              nodes.map((node) => {
                if (!node?.path) {
                  return null;
                }
                const mapped = PATH_MAP[node.path] ?? node.path;
                const isActive = pathname === mapped;
                const handleNav = () => {
                  handleClose();
                  router.push(mapped);
                };
                return (
                  <button
                    key={`node-${node.path}`}
                    type="button"
                    className={`sb-item${isActive ? " active" : ""}`}
                    onClick={handleNav}
                    aria-current={isActive ? "page" : undefined}
                    aria-label={`Navigate to ${node.identifier}`}
                  >
                    <span className="sb-item-dot" aria-hidden="true" />
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
