"use client";

import React, { useState, useId, useRef, useEffect } from "react";
import PropTypes from "prop-types";
import { ChevronDown, X } from "lucide-react";

function MultiSelect({
  label,
  selectedValues = [],
  onChange,
  options = [],
  optionLabel = "name",
  optionValue = "identifier",
  placeholder = "Select",
  required = false,
  disabled = false,
  hasError = false,
}) {
  const [open, setOpen] = useState(false);
  const listboxId = useId();
  const containerRef = useRef(null);

  useEffect(() => {
    if (!open) return;

    const handleOutsideClick = (e) => {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setOpen(false);
      }
    };

    document.addEventListener("mousedown", handleOutsideClick);
    return () => document.removeEventListener("mousedown", handleOutsideClick);
  }, [open]);

  const isSelected = (option) =>
    selectedValues.some((item) => item[optionValue] === option[optionValue]);

  const handleToggle = (option) => {
    if (disabled) return;

    const updated = isSelected(option)
      ? selectedValues.filter(
        (item) => item[optionValue] !== option[optionValue]
      )
      : [...selectedValues, option];

    onChange(updated);
  };

  const handleRemove = (e, option) => {
    e.stopPropagation();

    if (disabled) return;

    onChange(
      selectedValues.filter((item) => item[optionValue] !== option[optionValue])
    );
  };

  let triggerBorderClass;

  if (hasError) {
    triggerBorderClass = "border-red-500";
  } else if (open) {
    triggerBorderClass = "border-red-500 ring-2 ring-red-100";
  } else {
    triggerBorderClass = "border-gray-300";
  }

  return (
    <div ref={containerRef} className="flex flex-col gap-2 relative">
      {label && (
        <label className="text-sm font-medium text-gray-700">
          {label}
          {required && <span className="text-red-500 ml-1">*</span>}
        </label>
      )}

      <div
        className={`
          min-h-[50px] border rounded-lg px-3 py-2
          flex items-center flex-wrap gap-2 bg-white text-left w-full
          transition-colors
          ${disabled ? "opacity-50 cursor-not-allowed" : "hover:border-gray-400"}
          ${triggerBorderClass}
        `}
      >
        {selectedValues.length > 0 ? (
          selectedValues.map((item) => (
            <span
              key={item[optionValue]}
              className="flex items-center gap-1 bg-red-600 text-white px-2 py-1 rounded-md text-sm"
            >
              {item[optionLabel]}

              <button
                type="button"
                disabled={disabled}
                aria-label={`Remove ${item[optionLabel]}`}
                onClick={(e) => handleRemove(e, item)}
                className="hover:text-red-200 transition-colors disabled:cursor-not-allowed"
              >
                <X size={14} />
              </button>
            </span>
          ))
        ) : (
          <span className="text-gray-400 text-sm">{placeholder}</span>
        )}

        <button
          type="button"
          aria-haspopup="true"
          aria-expanded={open}
          aria-controls={`${listboxId}-menu`}
          disabled={disabled}
          onClick={() => setOpen((prev) => !prev)}
          onKeyDown={(e) => {
            if (e.key === "Escape") setOpen(false);
          }}
          className="ml-auto flex items-center justify-center text-gray-500 disabled:cursor-not-allowed"
        >
          <ChevronDown
            size={18}
            className={`flex-shrink-0 transition-transform duration-200 ${open ? "rotate-180" : ""
              }`}
          />
        </button>
      </div>

      {open && (
        <div
          id={`${listboxId}-menu`}
          role="menu"
          className="
            absolute top-full left-0 right-0 z-50
            mt-1 max-h-60 overflow-y-auto
            bg-white border border-gray-200 rounded-lg shadow-lg
          "
        >
          {options.length === 0 ? (
            <div role="menuitem" className="px-3 py-2 text-sm text-gray-400">
              No options available
            </div>
          ) : (
            options.map((option) => {
              const selected = isSelected(option);

              return (
                <div
                  key={option[optionValue]}
                  role="menuitemcheckbox"
                  aria-checked={selected}
                  tabIndex={0}
                  onClick={() => handleToggle(option)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                      e.preventDefault();
                      handleToggle(option);
                    }
                  }}
                  className={`
                    px-3 py-2 text-sm cursor-pointer flex items-center gap-2
                    transition-colors outline-none focus:bg-gray-100
                    ${selected
                      ? "bg-red-50 text-red-700 font-medium"
                      : "text-gray-700 hover:bg-gray-50"
                    }
                  `}
                >
                  <span
                    className={`
                      w-4 h-4 flex-shrink-0 rounded border flex items-center justify-center
                      ${selected
                        ? "bg-red-600 border-red-600"
                        : "border-gray-300"
                      }
                    `}
                  >
                    {selected && (
                      <svg
                        viewBox="0 0 10 8"
                        className="w-2.5 h-2.5 text-white fill-current"
                      >
                        <path
                          d="M1 4l2.5 2.5L9 1"
                          stroke="currentColor"
                          strokeWidth="1.5"
                          fill="none"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                        />
                      </svg>
                    )}
                  </span>

                  {option[optionLabel]}
                </div>
              );
            })
          )}
        </div>
      )}
    </div>
  );
}

MultiSelect.propTypes = {
  label: PropTypes.string,
  selectedValues: PropTypes.array,
  onChange: PropTypes.func.isRequired,
  options: PropTypes.array,
  optionLabel: PropTypes.string,
  optionValue: PropTypes.string,
  placeholder: PropTypes.string,
  required: PropTypes.bool,
  disabled: PropTypes.bool,
  hasError: PropTypes.bool,
};

export default MultiSelect;