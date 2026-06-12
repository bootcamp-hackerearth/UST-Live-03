// components/dropDowns/multiDropDown.jsx

"use client";

import React, { useEffect, useState } from "react";
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

  useEffect(() => {
  const loadOptions = async () => {
    try {
      const res = await api.get(
        `http://localhost:8080/api/${entity}/getAllActive`
      );

      const mapped = res.data.map((item) => ({
        value: item[valueField],
        label: item[labelField],
      }));

      setOptions(mapped);
    } catch (err) {
      setOptions([]);
      console.error(`Failed to load terminal ${entity} dropdown context:`, err);
    }
  };

  loadOptions();
}, [entity, valueField, labelField]);

const toggle = () => setIsOpen((prev) => !prev);

const handleSelect = (value) => {
  const updated = selectedValues.includes(value)
    ? selectedValues.filter((v) => v !== value)
    : [...selectedValues, value];
  onChange(updated);
};
  const clearAll = () => onChange([]);

  const selectAll = () => onChange(options.map((o) => o.value));

  let chosenPillsLayout = (
    <span className="text-slate-400 text-sm px-2">Select multiple...</span>
  );

  if (selectedValues.length > 0) {
    chosenPillsLayout = selectedValues.map((val) => {
      const matchOpt = options.find((o) => o.value === val);
      const displayPillLabel = matchOpt ? matchOpt.label : val;
      
      return (
        <span
          key={val}
          className="flex items-center bg-blue-100 text-blue-800 text-xs px-2 py-1 rounded-full"
        >
          {displayPillLabel}
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              handleSelect(val);
            }}
            className="ml-1 text-blue-600 hover:text-red-600 cursor-pointer"
          >
            ✕
          </button>
        </span>
      );
    });
  }

  let dropdownMenuOverlay = null;
  if (isOpen) {
    dropdownMenuOverlay = (
      <div className="absolute w-full bg-white border mt-1 rounded-lg shadow-lg z-50 max-h-60 overflow-y-auto">
        <div className="flex justify-between px-3 py-2 text-xs border-b bg-gray-50">
          <button type="button" onClick={selectAll} className="text-blue-600 hover:underline cursor-pointer">
            Select All
          </button>
          <button type="button" onClick={clearAll} className="text-red-600 hover:underline cursor-pointer">
            Clear
          </button>
        </div>

        {options.map((opt) => {
          const isSelected = selectedValues.includes(opt.value);
          const spanClass = isSelected ? "font-semibold" : "";

          return (
            <button
              key={opt.value}
              type="button"
              onClick={() => handleSelect(opt.value)}
              className="w-full flex items-center px-3 py-2 text-sm text-left hover:bg-gray-50 transition-colors cursor-pointer focus:outline-none"
            >
              <input
                type="checkbox"
                checked={isSelected}
                readOnly
                className="mr-2 cursor-pointer"
              />
              <span className={spanClass}>{opt.label}</span>
            </button>
          );
        })}
      </div>
    );
  }

  return (
    <div className="w-full relative mb-5 text-left">
      <label className="block text-xs font-bold text-slate-700 uppercase mb-2">
        {label}
      </label>

      <button
        type="button"
        onClick={toggle}
        className="w-full min-h-[40px] flex flex-wrap gap-1 items-center px-2 py-1 bg-white border rounded-lg text-left cursor-pointer focus:outline-none focus:ring-2 focus:ring-blue-500/20"
      >
        {chosenPillsLayout}
      </button>

      {dropdownMenuOverlay}
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