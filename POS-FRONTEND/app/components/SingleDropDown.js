"use client";
import React, { useEffect, useState, useRef, useMemo } from "react";
import PropTypes from "prop-types";
import api from "./Axios";

export default function SingleDropDown({
  label,
  apiUrl,
  selectedValue,
  onChange,
  valueField = "identifier",
  labelField = "identifier",
}) {

  const [isOpen, setIsOpen] = useState(false);
  const [options, setOptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const containerRef = useRef(null);

  useEffect(() => {
    let isMounted = true;
    (async () => {
      try {
        const res = await api.get(apiUrl);
        if (isMounted) {
          setOptions(
            res.data.map((item) => ({
              value: item[valueField],
              label: item[labelField],
            }))
          );
        }
      } catch {
      } finally {
        if (isMounted) setLoading(false);
      }
    })();

    return () => {
      isMounted = false;
    };
  }, [apiUrl, valueField, labelField]);

  useEffect(() => {
    const fn = (e) =>
      containerRef.current &&
      !containerRef.current.contains(e.target) &&
      setIsOpen(false);

    document.addEventListener("mousedown", fn);
    return () => document.removeEventListener("mousedown", fn);
  }, []);

  const selectedLabel = useMemo(
    () =>
      selectedValue
        ? options.find((o) => o.value === selectedValue)?.label ?? selectedValue
        : "Select\u2026",
    [options, selectedValue]
  );

  const handleSelect = (v) => {
    onChange(v === selectedValue ? "" : v);
    setIsOpen(false);
  };

  const handleKey = (e, v) => {
    if (e.key === "Enter" || e.key === " ") {
      e.preventDefault();
      handleSelect(v);
    }
  };

  const classes = useMemo(
    () => ({
      btn:
        "w-full px-3.5 py-2.5 bg-[#fafaf8] border-[1.5px] border-solid border-gray-300 rounded-lg cursor-pointer text-sm text-left flex items-center justify-start line-clamp-1 hover:border-gray-400 focus:outline-none focus:border-brand",
      box:
        "w-full absolute bg-white border border-solid border-gray-300 rounded-lg mt-1.5 max-h-[200px] overflow-y-auto shadow-[0_4px_20px_rgba(0,0,0,0.1)] z-50",
      row:
        "px-3.5 py-2.5 flex items-center gap-2.5 cursor-pointer border-b border-solid border-gray-50 last:border-b-0 transition-colors text-left text-sm",
      empty:
        "px-3.5 py-2.5 text-sm text-gray-400 text-left",
      selected:
        "bg-[#e8f5e9] text-emerald-900",
      normal:
        "text-gray-800 hover:bg-gray-50",
    }),
    []
  );

  if (loading) {
    return (
      <p className="text-xs text-gray-500 mb-1.5">
        Loading {label}…
      </p>
    );
  }

  const renderOption = (opt) => {
    const isSelected = selectedValue === opt.value;

    return (
      <div
        key={opt.value}
        className={`${classes.row} ${
          isSelected ? classes.selected : classes.normal
        }`}
        role="menuitemradio"
        aria-checked={isSelected}
        tabIndex={0}
        onClick={() => handleSelect(opt.value)}
        onKeyDown={(e) => handleKey(e, opt.value)}
      >
        <input
          type="radio"
          className="scale-110 cursor-pointer m-0 accent-brand"
          readOnly
          checked={isSelected}
        />
        <span>{opt.label}</span>
      </div>
    );
  };

  const renderMenu = () =>
    options.length === 0 ? (
      <div className={classes.empty}>No items found</div>
    ) : (
      options.map(renderOption)
    );

  return (
    <div className="w-full relative mb-4.5 font-sans" ref={containerRef}>
      <label className="block text-xs font-semibold text-gray-700 mb-1.5">
        {label}
      </label>

      <button
        type="button"
        className={classes.btn}
        aria-haspopup="menu"
        aria-expanded={isOpen}
        onClick={() => setIsOpen((p) => !p)}
      >
        {selectedLabel}
      </button>

      {isOpen && <div className={classes.box}>{renderMenu()}</div>}
    </div>
  );
}

SingleDropDown.propTypes = {
  label: PropTypes.string.isRequired,
  apiUrl: PropTypes.string.isRequired,
  selectedValue: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  valueField: PropTypes.string,
  labelField: PropTypes.string,
};