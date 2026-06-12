"use client";

import React, { useState } from "react";
import PropTypes from "prop-types";
import { useDropdownOptions } from "@/components/useDropdownOptions"; 

export default function MultiDropDown({
  label, apiUrl,
  selectedValues, onChange,
  valueField = "identifier", labelField = "identifier",
}) {
  const [isOpen, setIsOpen] = useState(false);
  const { options, loading } = useDropdownOptions(apiUrl, valueField, labelField);

  const handleSelect = (value) => {
    const updated = selectedValues.includes(value)
      ? selectedValues.filter(v => v !== value)
      : [...selectedValues, value];
    onChange(updated);
  };

  if (loading) return <p style={{ fontSize: "12px", color: "#666666", marginBottom: "6px" }}>Loading {label}…</p>;

  return (
    <>
      <style>{`
        .md2-wrap { width: 100%; position: relative; margin-bottom: 14px; font-family: "Segoe UI", sans-serif; }
        .md2-label { font-size: 12px; font-weight: 600; color: #374151; margin-bottom: 5px; display: block; letter-spacing: 0.2px; }
        .md2-box {
          width: 100%; padding: 9px 12px;
          background: #fafafa; border: 1.5px solid #e8e8e8;
          border-radius: 7px; cursor: pointer;
          box-sizing: border-box; font-size: 13px;
          color: #1a1a1a; transition: border-color 0.15s;
          display: flex; align-items: center; justify-content: space-between;
        }
        .md2-box:hover { border-color: #000000; }
        .md2-box.open { border-color: #1a1a1a; }
        .md2-chevron { font-size: 10px; color: #999999; transition: transform 0.2s; }
        .md2-chevron.open { transform: rotate(180deg); }
        .md2-menu {
          width: 100%; position: absolute;
          background: #ffffff; border: 1.5px solid #e8e8e8;
          border-radius: 8px; margin-top: 4px;
          max-height: 200px; overflow-y: auto;
          box-shadow: 0 8px 24px rgba(0,0,0,0.08);
          z-index: 50;
        }
        .md2-item {
          padding: 9px 12px; display: flex;
          align-items: center; gap: 9px;
          cursor: pointer; border-bottom: 1px solid #f3f4f6;
          transition: background 0.1s; font-size: 13px; color: #374151;
        }
        .md2-item:last-child { border-bottom: none; }
        .md2-item:hover { background: #f5f5f5; }
        .md2-item.selected { background: rgba(0,0,0,0.06); color: #000000; font-weight: 500; }
        .md2-check { transform: scale(1.05); cursor: pointer; accent-color: #000000; }
      `}</style>

      <div className="md2-wrap">
        <label className="md2-label">{label}</label>
        <button
          type="button"
          className={`md2-box ${isOpen ? "open" : ""}`}
          onClick={() => setIsOpen(!isOpen)}
        >
          <span>
            {selectedValues.length > 0
              ? selectedValues.map(v => options.find(o => o.value === v)?.label).filter(Boolean).join(", ")
              : <span style={{ color: "#9ca3af" }}>Select…</span>}
          </span>
          <span className={`md2-chevron ${isOpen ? "open" : ""}`}>▼</span>
        </button>
        {isOpen && (
          <div className="md2-menu">
            {options.length === 0
              ? <div className="md2-item">No items found</div>
              : options.map(opt => (
                <button key={opt.value}
                  type="button"
                  className={`md2-item ${selectedValues.includes(opt.value) ? "selected" : ""}`}
                  onClick={() => handleSelect(opt.value)}>
                  <input type="checkbox" className="md2-check" readOnly
                    checked={selectedValues.includes(opt.value)} />
                  {opt.label}
                </button>
              ))}
          </div>
        )}
      </div>
    </>
  );
}

MultiDropDown.propTypes = {
  label: PropTypes.string.isRequired,
  apiUrl: PropTypes.string.isRequired,
  selectedValues: PropTypes.array.isRequired,
  onChange: PropTypes.func.isRequired,
  valueField: PropTypes.string,
  labelField: PropTypes.string,
};