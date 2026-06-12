"use client";

import React, { useEffect, useState, useRef } from "react";
import PropTypes from "prop-types";
import api from "./Axios";

export default function MultiDropDown({
  label,
  apiUrl,
  selectedValues = [],
  onChange,
  valueField = "identifier",
  labelField = "identifier",
  isOpen: externalIsOpen,
  setOpen: externalSetOpen,
}) {
  const [internalIsOpen, setInternalIsOpen] = useState(false);
  const [options, setOptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const containerRef = useRef(null);

  const isControlled = externalIsOpen !== undefined && externalSetOpen !== undefined;
  const isOpen = isControlled ? externalIsOpen : internalIsOpen;
  const setIsOpen = isControlled ? externalSetOpen : setInternalIsOpen;

  useEffect(() => {
    let isMounted = true;
    const loadOptions = async () => {
      try {
        const res = await api.get(apiUrl);
        if (isMounted) {
          const mapped = res.data.map((item) => ({
            value: item[valueField],
            label: item[labelField],
          }));
          setOptions(mapped);
        }
      } catch (err) {
        console.error("Error loading dropdown:", err);
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };
    loadOptions();

    return () => {
      isMounted = false;
    };
  }, [apiUrl, valueField, labelField]);

  useEffect(() => {
    function handleClickOutside(e) {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [setIsOpen]);

  const handleSelect = (value) => {
    const updated = selectedValues.includes(value)
      ? selectedValues.filter((v) => v !== value)
      : [...selectedValues, value];
    onChange(updated);
  };

  const handleBoxKeyDown = (event) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      setIsOpen(!isOpen);
    }
  };

  const handleItemKeyDown = (event, value) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      handleSelect(value);
    }
  };

  if (loading) {
    return (
      <p className="text-xs text-gray-500 mb-1.5">
        Loading {label}&hellip;
      </p>
    );
  }

  const selectedDisplay = selectedValues
    .map((v) => options.find((o) => o.value === v)?.label)
    .filter(Boolean)
    .join(", ");

  const multiBoxStyle = "w-full px-3.5 py-2.5 bg-[#fafaf8] border-[1.5px] border-solid border-gray-300 rounded-lg cursor-pointer text-sm text-left flex items-center justify-start line-clamp-1 hover:border-gray-400 focus:outline-none focus:border-brand";
  const overlayMenuWrapper = "w-full absolute bg-white border border-solid border-gray-300 rounded-lg mt-1.5 max-h-[200px] overflow-y-auto shadow-[0_4px_20px_rgba(0,0,0,0.1)] z-50";
  const checkableRowStyle = "px-3.5 py-2.5 flex items-center gap-2.5 cursor-pointer border-b border-solid border-gray-50 last:border-b-0 transition-colors text-left text-sm";

  return (
    <div className="w-full relative mb-4.5 font-sans" ref={containerRef}>
      <label className="block text-xs font-semibold text-gray-700 mb-1.5">
        {label}
      </label>
      
      <button
        type="button"
        className={multiBoxStyle}
        aria-haspopup="listbox"
        aria-expanded={isOpen}
        onClick={() => setIsOpen(!isOpen)}
        onKeyDown={handleBoxKeyDown}
      >
        {selectedValues.length > 0 ? selectedDisplay : "Select\u2026"}
      </button>

      {isOpen && (
        <div className={overlayMenuWrapper} role="menu">
          {options.length === 0 ? (
            <div className="px-3.5 py-2.5 text-sm text-gray-400 text-left" role="menuitem" aria-disabled="true">
              No items found
            </div>
          ) : (
            options.map((opt) => {
              const isSelected = selectedValues.includes(opt.value);
              return (
                <div
                  key={opt.value}
                  className={`${checkableRowStyle} ${
                    isSelected ? "bg-[#e8f5e9] text-emerald-900" : "text-gray-800 hover:bg-gray-50"
                  }`}
                  role="menuitemcheckbox"
                  aria-checked={isSelected}
                  tabIndex={0}
                  onClick={() => handleSelect(opt.value)}
                  onKeyDown={(e) => handleItemKeyDown(e, opt.value)}
                >
                  <input
                    type="checkbox"
                    className="scale-110 cursor-pointer m-0 accent-brand"
                    readOnly
                    checked={isSelected}
                  />
                  <span>{opt.label}</span>
                </div>
              );
            })
          )}
        </div>
      )}
    </div>
  );
}

MultiDropDown.propTypes = {
  label: PropTypes.string.isRequired,
  apiUrl: PropTypes.string.isRequired,
  selectedValues: PropTypes.arrayOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
  onChange: PropTypes.func.isRequired,
  valueField: PropTypes.string,
  labelField: PropTypes.string,
  isOpen: PropTypes.bool,
  setOpen: PropTypes.func,
};