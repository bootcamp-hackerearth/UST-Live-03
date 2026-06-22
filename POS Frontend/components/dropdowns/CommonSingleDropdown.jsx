"use client";

import React, { useState, useCallback, useEffect, useRef } from "react";
import PropTypes from "prop-types";
import { useDropdownOptions } from "@/components/useDropdownOptions";

export default function SingleDropdown({
  label, apiUrl,
  selectedValue, onChange,
  valueField = "identifier", labelField = "identifier",
  filterOut = null,
  options: staticOptions = null,
}) {
  const [isOpen, setIsOpen] = useState(false);
  const [menuPos, setMenuPos] = useState({ top: 0, left: 0, width: 0 });
  const boxRef = useRef(null);
  const wrapRef = useRef(null);
  const { options: fetchedOptions, loading } = useDropdownOptions(apiUrl, valueField, labelField);

  const options = staticOptions || fetchedOptions;

  const filteredOptions = filterOut ? options.filter(o => o.value !== filterOut) : options;

  const closeDropdown = useCallback(() => setIsOpen(false), []);

  useEffect(() => {
    const handleOutside = (e) => {
      if (wrapRef.current && !wrapRef.current.contains(e.target)) closeDropdown();
    };
    document.addEventListener("mousedown", handleOutside);
    return () => document.removeEventListener("mousedown", handleOutside);
  }, [closeDropdown]);

  const recalculate = useCallback(() => {
    if (boxRef.current) {
      const r = boxRef.current.getBoundingClientRect();
      const estimatedHeight = Math.min(filteredOptions.length * 38, 114);
      const spaceBelow = window.innerHeight - r.bottom;
      const topPos = spaceBelow < estimatedHeight ? r.top - estimatedHeight : r.bottom;
      setMenuPos({ top: topPos, left: r.left, width: r.width });
    }
  }, [filteredOptions.length]);

  const handleToggle = () => {
    if (!isOpen) recalculate();
    setIsOpen((prev) => !prev);
  };

  const handleSelect = (value) => {
    onChange(value === selectedValue ? "" : value);
    setIsOpen(false);
  };

  if (!staticOptions && loading) return <p style={{ fontSize: "12px", color: "#666666", marginBottom: "6px" }}>Loading {label}…</p>;

  return (
    <>
      <style>{`
        .sd2-wrap { width: 100%; position: relative; font-family: "Segoe UI", sans-serif; }
        .sd2-label { font-size: 12px; font-weight: 600; color: #374151; margin-bottom: 0; margin-top: 0; display: block; letter-spacing: 0.2px; }
        .sd2-box {
          width: 100%; padding: 9px 12px;
          background: #fafafa; border: 1.5px solid #e8e8e8;
          border-radius: 7px; cursor: pointer;
          box-sizing: border-box; font-size: 13px;
          color: #1a1a1a; transition: border-color 0.15s;
          display: flex; align-items: center; justify-content: space-between;
        }
        .sd2-box:hover { border-color: #000000; }
        .sd2-box.open { border-color: #1a1a1a; }
        .sd2-chevron { font-size: 10px; color: #999999; transition: transform 0.2s; }
        .sd2-chevron.open { transform: rotate(180deg); }
        .sd2-menu {
          position: fixed;
          background: #ffffff; border: 1.5px solid #e8e8e8;
          border-radius: 8px; margin-top: 4px;
          max-height: 114px; overflow-y: auto;
          box-shadow: 0 8px 24px rgba(0,0,0,0.08);
          z-index: 99999;
        }
        .sd2-item {
          width: 100%; padding: 9px 12px; display: flex;
          align-items: center; gap: 9px;
          cursor: pointer; border: none; border-bottom: 1px solid #f3f4f6;
          background: transparent;
          transition: background 0.1s; font-size: 13px; color: #374151;
          text-align: left;
        }
        .sd2-item:last-child { border-bottom: none; }
        .sd2-item:hover { background: #f5f5f5; }
        .sd2-item.selected { background: rgba(0,0,0,0.06); color: #000000; font-weight: 500; }
        .sd2-radio { transform: scale(1.05); cursor: pointer; accent-color: #000000; }
      `}</style>

      <div className="sd2-wrap" ref={wrapRef}>
        <label className="sd2-label">{label}</label>
        <button
          ref={boxRef}
          type="button"
          className={`sd2-box ${isOpen ? "open" : ""}`}
          onClick={handleToggle}
          aria-expanded={isOpen}
          aria-haspopup="listbox"
        >
          <span>
            {selectedValue
              ? filteredOptions.find(o => o.value === selectedValue)?.label ?? selectedValue
              : <span style={{ color: "#9ca3af" }}>Select…</span>}
          </span>
          <span className={`sd2-chevron ${isOpen ? "open" : ""}`}>▼</span>
        </button>
        {isOpen && (
          <div
            className="sd2-menu"
            style={{ top: menuPos.top, left: menuPos.left, width: menuPos.width }}
            aria-label={`${label} options`}
          >
            {filteredOptions.length === 0
              ? <div className="sd2-item">No items found</div>
              : filteredOptions.map(opt => (
                <button key={opt.value}
                  type="button"
                  className={`sd2-item ${selectedValue === opt.value ? "selected" : ""}`}
                  onClick={() => handleSelect(opt.value)}
                >
                  <span aria-hidden="true" style={{ width: 18, display: "inline-block", textAlign: "center" }}>
                    {selectedValue === opt.value ? "✓" : ""}
                  </span>
                  {opt.label}
                </button>
              ))
            }
          </div>
        )}
      </div>
    </>
  );
}

SingleDropdown.propTypes = {
  label: PropTypes.string.isRequired,
  apiUrl: PropTypes.string,
  selectedValue: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  valueField: PropTypes.string,
  labelField: PropTypes.string,
  filterOut: PropTypes.any,
  options: PropTypes.arrayOf(PropTypes.shape({
    value: PropTypes.string,
    label: PropTypes.string,
  })),
};