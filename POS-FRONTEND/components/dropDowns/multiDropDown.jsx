// components/dropDowns/multiDropDown.jsx

"use client";

import React, { useEffect, useRef, useState } from "react";
import PropTypes from "prop-types";
import api from "../../app/api/axios";

export default function MultiDropDown({
  label,
  entity,
  selectedValues = [],
  onChange,
  valueField = "identifier",
  labelField = "identifier",
}) {
  const [isOpen, setIsOpen] = useState(false);
  const [options, setOptions] = useState([]);
  const containerRef = useRef(null);

  useEffect(() => {
    async function loadOptions() {
      try {
        const res = await api.get(`/${entity}/getAllActive`);
        const list = Array.isArray(res.data)
          ? res.data
          : res.data?.dtoList || [];
        setOptions(
          list.map((item) => ({
            value: item[valueField],
            label: item[labelField],
          }))
        );
      } catch (err) {
        setOptions([]);
        console.error(`Failed to load ${entity} dropdown:`, err);
      }
    }
    loadOptions();
  }, [entity, valueField, labelField]);

  useEffect(() => {
    function handleOutside(e) {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    }
    document.addEventListener("mousedown", handleOutside);
    return () => document.removeEventListener("mousedown", handleOutside);
  }, []);

  const toggle = () => setIsOpen((prev) => !prev);
  const clearAll = () => onChange([]);
  const selectAll = () => onChange(options.map((o) => o.value));

  const handleSelect = (value) => {
    const updated = selectedValues.includes(value)
      ? selectedValues.filter((v) => v !== value)
      : [...selectedValues, value];
    onChange(updated);
  };

  return (
    <div ref={containerRef} className="w-full relative mb-5 text-left">
      <label className="block text-xs font-semibold text-[#006E74] uppercase mb-1">
        {label}
      </label>

      <button
        type="button"
        onClick={toggle}
        onKeyDown={(e) => e.key === "Enter" || e.key === " " ? toggle() : null}
        aria-haspopup="listbox"
        aria-expanded={isOpen}
        aria-label={`${label} selector`}
        className="w-full min-h-[40px] flex flex-wrap gap-1 items-center px-2 py-1 bg-white border border-[#006E74]/30 rounded-lg select-none cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-[#006E74]/20"
      >
        <span className="flex-1 min-w-[120px] pr-2 text-sm">
          {selectedValues.length === 0 ? (
            <span className="text-gray-400">Select {label.toLowerCase()}…</span>
          ) : (
            <span className="text-[#231F20]">{selectedValues.length} selected</span>
          )}
        </span>

        {selectedValues.map((val) => {
          const match = options.find((o) => o.value === val);
          const pillLabel = match ? match.label : val;
          return (
            <button
              key={val}
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                handleSelect(val);
              }}
              onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") {
                  e.stopPropagation();
                  handleSelect(val);
                }
              }}
              className="flex items-center gap-1 bg-[#006E74]/10 text-[#006E74] text-xs font-semibold px-2 py-1 rounded-full cursor-pointer hover:bg-red-50 focus:outline-none focus:ring-2 focus:ring-[#006E74]/20"
              aria-label={`Remove ${pillLabel}`}
            >
              {pillLabel}
              <span className="text-[#006E74]/60 hover:text-red-500 leading-none">✕</span>
            </button>
          );
        })}

        <span className="ml-auto shrink-0 p-1 pointer-events-none">
          <svg
            className={`transition-transform duration-200 ${isOpen ? "rotate-180" : ""}`}
            width="14" height="14" viewBox="0 0 24 24"
            fill="none" stroke="#006E74" strokeWidth="2.5"
            strokeLinecap="round" strokeLinejoin="round"
          >
            <polyline points="6 9 12 15 18 9" />
          </svg>
        </span>
      </button>

      {isOpen && (
        <div className="absolute w-full bg-white border border-[#006E74]/20 mt-1 rounded-xl shadow-lg z-50">

          <div className="flex justify-between px-3 py-2 text-xs border-b border-gray-100 bg-gray-50 rounded-t-xl">
            <button
              type="button"
              onClick={(e) => { e.stopPropagation(); selectAll(); }}
              className="text-[#006E74] font-semibold hover:underline"
            >
              Select all
            </button>
            <button
              type="button"
              onClick={(e) => { e.stopPropagation(); clearAll(); }}
              className="text-red-500 font-semibold hover:underline"
            >
              Clear
            </button>
          </div>

          {options.length === 0 ? (
            <p className="px-3 py-4 text-xs text-gray-400 text-center">No options found.</p>
          ) : (
            <ul className="max-h-60 overflow-y-auto rounded-b-xl">
              {options.map((opt) => {
                const isSelected = selectedValues.includes(opt.value);
                return (
                  <li key={opt.value}>
                    <button
                      type="button"
                      aria-pressed={isSelected}
                      onClick={(e) => {
                        e.stopPropagation();
                        handleSelect(opt.value);
                      }}
                      onKeyDown={(e) => {
                        if (e.key === "Enter" || e.key === " ") {
                          e.stopPropagation();
                          handleSelect(opt.value);
                        }
                      }}
                      className={`w-full flex items-center gap-2 px-3 py-2 text-sm cursor-pointer transition-colors text-left focus:outline-none focus:ring-2 focus:ring-inset focus:ring-[#006E74]/20
                        ${isSelected
                          ? "bg-[#006E74]/10 text-[#006E74] font-semibold"
                          : "text-[#231F20] hover:bg-gray-50"
                        }`}
                    >
                      <span className={`w-4 h-4 shrink-0 rounded border flex items-center justify-center text-[10px]
                        ${isSelected
                          ? "bg-[#006E74] border-[#006E74] text-white"
                          : "border-gray-300"
                        }`}
                      >
                        {isSelected && "✓"}
                      </span>
                      {opt.label}
                    </button>
                  </li>
                );
              })}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}

MultiDropDown.propTypes = {
  label: PropTypes.string.isRequired,
  entity: PropTypes.string.isRequired,
  selectedValues: PropTypes.arrayOf(PropTypes.any),
  onChange: PropTypes.func.isRequired,
  valueField: PropTypes.string,
  labelField: PropTypes.string,
};