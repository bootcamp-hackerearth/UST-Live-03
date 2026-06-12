"use client";
import { useEffect, useRef, useState, useCallback } from "react";
import PropTypes from "prop-types";
import api from "@/api/axios";

export function useDropdownPosition(onClose) {
  const boxRef = useRef(null);
  const wrapRef = useRef(null);
  const [menuPos, setMenuPos] = useState({ top: 0, left: 0, width: 0 });

  useEffect(() => {
    const handleOutside = (e) => {
      if (wrapRef.current && !wrapRef.current.contains(e.target)) onClose();
    };
    document.addEventListener("mousedown", handleOutside);
    return () => document.removeEventListener("mousedown", handleOutside);
  }, [onClose]);

  const recalculate = useCallback(() => {
    if (boxRef.current) {
      const r = boxRef.current.getBoundingClientRect();
      setMenuPos({
        top: r.bottom + window.scrollY,
        left: r.left + window.scrollX,
        width: r.width,
      });
    }
  }, []);

  return { boxRef, wrapRef, menuPos, recalculate };
}

export function useDropdownOptions({ apiUrl, valueField, labelField, filterOut = null }) {
  const [options, setOptions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    const load = async () => {
      try {
        const res = await api.get(apiUrl);
        let mapped = res.data.map((item) => ({
          value: item[valueField],
          label: item[labelField],
        }));
        if (filterOut) mapped = mapped.filter((o) => o.value !== filterOut);
        if (!cancelled) setOptions(mapped);
      } catch (err) {
        if (process.env.NODE_ENV !== "production") console.error(err);
        const { showAlert } = await import("@/utils/browser");
        showAlert("Could not load list. Please refresh.");
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, [apiUrl, valueField, labelField, filterOut]);

  return { options, loading };
}

export function dropdownStyles(p) {
  return `
    .${p}-wrap { width:100%; position:relative; margin-bottom:14px; font-family:"Segoe UI",sans-serif; }
    .${p}-label { font-size:11px; font-weight:700; color:#4b5563; margin-bottom:5px; display:block; letter-spacing:0.4px; text-transform:uppercase; }
    .${p}-box {
      width:100%; padding:9px 12px;
      background:#fafafa; border:1.5px solid #E8E8E8;
      border-radius:7px; cursor:pointer;
      box-sizing:border-box; font-size:13px;
      color:#1e2235; transition:border-color 0.15s;
      display:flex; align-items:center; justify-content:space-between;
    }
    .${p}-box:hover { border-color:#879EC6; }
    .${p}-box.open  { border-color:#54668E; }
    .${p}-chevron { font-size:10px; color:#9ca3af; transition:transform 0.2s; }
    .${p}-chevron.open { transform:rotate(180deg); }
    .${p}-menu {
      position:fixed;
      background:#ffffff; border:1.5px solid #E8E8E8;
      border-radius:8px; margin-top:4px;
      max-height:200px; overflow-y:auto;
      box-shadow:0 8px 24px rgba(54,57,85,0.12);
      z-index:99999;
    }
    .${p}-item {
      width:100%; padding:9px 12px; display:flex;
      align-items:center; gap:9px;
      cursor:pointer; border:none; border-bottom:1px solid #f3f4f6;
      background:transparent;
      transition:background 0.1s; font-size:13px; color:#374151;
      text-align:left;
    }
    .${p}-item:last-child { border-bottom:none; }
    .${p}-item:hover    { background:#F5F6E6; }
    .${p}-item.selected { background:rgba(84,102,142,0.08); color:#363955; font-weight:500; }
  `;
}

export function DropdownMenu({ prefix, isOpen, menuPos, label, children }) {
  return (
    <>
      <span className={`${prefix}-chevron${isOpen ? " open" : ""}`}>▼</span>
      {isOpen && (
        <div
          className={`${prefix}-menu`}
          style={{ top: menuPos.top, left: menuPos.left, width: menuPos.width }}
          aria-label={`${label} options`}
        >
          {children}
        </div>
      )}
    </>
  );
}

DropdownMenu.propTypes = {
  prefix: PropTypes.string.isRequired,
  isOpen: PropTypes.bool.isRequired,
  menuPos: PropTypes.shape({
    top: PropTypes.number.isRequired,
    left: PropTypes.number.isRequired,
    width: PropTypes.number.isRequired,
  }).isRequired,
  label: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
};