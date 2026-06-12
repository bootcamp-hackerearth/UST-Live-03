"use client";

import React, { useState } from "react";
import PropTypes from "prop-types";
import { useDropdownOptions } from "@/components/useDropdownOptions"; 

export default function SingleDropdown({
  label, apiUrl,
  selectedValue, onChange,
  valueField = "identifier", labelField = "identifier",
}) {
  const [isOpen, setIsOpen] = useState(false);
  const { options, loading } = useDropdownOptions(apiUrl, valueField, labelField);

  const handleSelect = (value) => {
    onChange(value === selectedValue ? "" : value);
    setIsOpen(false);
  };

  if (loading) return <p style={{ fontSize: "12px", color: "#666666", marginBottom: "6px" }}>Loading {label}…</p>;

  return (
    <>
      <style>{`
        .sd2-wrap { width: 100%; position: relative; margin-bottom: 14px; font-family: "Segoe UI", sans-serif; }
        .sd2-label { font-size: 12px; font-weight: 600; color: #374151; margin-bottom: 5px; display: block; letter-spacing: 0.2px; }
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
          width: 100%; position: absolute;
          background: #ffffff; border: 1.5px solid #e8e8e8;
          border-radius: 8px; margin-top: 4px;
          max-height: 200px; overflow-y: auto;
          box-shadow: 0 8px 24px rgba(0,0,0,0.08);
          z-index: 50;
        }
        .sd2-item {
          padding: 9px 12px; display: flex;
          align-items: center; gap: 9px;
          cursor: pointer; border-bottom: 1px solid #f3f4f6;
          transition: background 0.1s; font-size: 13px; color: #374151;
        }
        .sd2-item:last-child { border-bottom: none; }
        .sd2-item:hover { background: #f5f5f5; }
        .sd2-item.selected { background: rgba(0,0,0,0.06); color: #000000; font-weight: 500; }
        .sd2-radio { transform: scale(1.05); cursor: pointer; accent-color: #000000; }
      `}</style>

      <div className="sd2-wrap">
        <label className="sd2-label">{label}</label>
        <button
          type="button"
          className={`sd2-box ${isOpen ? "open" : ""}`}
          onClick={() => setIsOpen(!isOpen)}
        >
          <span>
            {selectedValue
              ? options.find(o => o.value === selectedValue)?.label ?? selectedValue
              : <span style={{ color: "#9ca3af" }}>Select…</span>}
          </span>
          <span className={`sd2-chevron ${isOpen ? "open" : ""}`}>▼</span>
        </button>
        {isOpen && (
          <div className="sd2-menu">
            {options.length === 0
              ? <div className="sd2-item">No items found</div>
              : options.map(opt => (
                <button key={opt.value}
                  type="button"
                  className={`sd2-item ${selectedValue === opt.value ? "selected" : ""}`}
                  onClick={() => handleSelect(opt.value)}>
                  <input type="radio" className="sd2-radio" readOnly
                    checked={selectedValue === opt.value} />
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
  apiUrl: PropTypes.string.isRequired,
  selectedValue: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  valueField: PropTypes.string,
  labelField: PropTypes.string,
};