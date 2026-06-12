"use client";

import React from "react";
import PropTypes from "prop-types";

function InputField({
  label,
  type = "text",
  value,
  onChange,
  placeholder,
  required = false,
  disabled = false,
  error = "",
}) {
  return (
    <div className="flex flex-col gap-2">
      <label className="text-sm font-medium text-gray-700">
        {label}
        {required && (
          <span className="text-red-500 ml-1">*</span>
        )}
      </label>

      <input
        type={type}
        value={value || ""}
        onChange={(e) => onChange(e.target.value)}
        placeholder={placeholder}
        disabled={disabled}
        className={`
          w-full border rounded-lg px-4 py-3
          transition-colors focus:outline-none focus:ring-2
          ${
            error
              ? "border-red-500 focus:ring-red-300 bg-red-50"
              : "border-gray-300 focus:ring-red-500 bg-white"
          }
          ${
            disabled
              ? "bg-gray-100 text-gray-400 cursor-not-allowed"
              : ""
          }
        `}
      />

      {error && (
        <span className="text-red-500 text-xs">
          {error}
        </span>
      )}
    </div>
  );
}

InputField.propTypes = {
  label: PropTypes.string.isRequired,
  type: PropTypes.string,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  required: PropTypes.bool,
  disabled: PropTypes.bool,
  error: PropTypes.string,
};

export default InputField;