"use client";

import React from "react";
import PropTypes from "prop-types";

function SingleSelect({
  label,
  value,
  onChange,
  options = [],
  optionLabel = "name",
  optionValue = "identifier",
  placeholder = "Select",
  required = false,
  disabled = false,
  hasError = false,
}) {
  return (
    <div className="flex flex-col gap-2">
      <label className="text-sm font-medium text-gray-700">
        {label}
        {required && <span className="text-red-500 ml-1">*</span>}
      </label>

      <select
        value={value || ""}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
        className={`
          w-full
          border
          rounded-lg
          px-4 py-3
          bg-white
          text-gray-700
          focus:outline-none
          focus:ring-2
          focus:ring-red-500
          disabled:opacity-50
          disabled:cursor-not-allowed
          ${hasError ? "border-red-500" : "border-gray-300"}
        `}
      >
        <option value="" className="text-gray-400">
          {placeholder}
        </option>

        {options.map((option) => (
          <option
            key={option[optionValue]}
            value={option[optionValue]}
            className="text-gray-700"
          >
            {option[optionLabel]}
          </option>
        ))}
      </select>
    </div>
  );
}

export default SingleSelect;

SingleSelect.propTypes = {
  label: PropTypes.string,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  options: PropTypes.array,
  optionLabel: PropTypes.string,
  optionValue: PropTypes.string,
  placeholder: PropTypes.string,
  required: PropTypes.bool,
  disabled: PropTypes.bool,
  hasError: PropTypes.bool,
};