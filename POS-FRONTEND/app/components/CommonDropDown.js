"use client";

import { useEffect, useRef, useState, useMemo } from "react";
import PropTypes from "prop-types";
import api from "../services/api";
import { ChevronDown, Check } from "lucide-react";

export default function CommonDropDown({
  name,
  value,
  onChange,
  api: apiUrl,
  payload = {},
  optionLabel = "identifier",
  optionValue = "identifier",
  placeholder = "Select option",
  multiple = false,
  options: staticOptions,
  includeNoneOption = false,
}) {
  const [options, setOptions] = useState([]);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const ref = useRef(null);

  useEffect(() => {
    const fetchOptions = async () => {
      try {
        setLoading(true);

        if (staticOptions) {
          setOptions(
            includeNoneOption
              ? [{ [optionValue]: "", [optionLabel]: "None (Root Category)" }, ...staticOptions]
              : staticOptions
          );
          return;
        }

        if (apiUrl) {
          let res;

          if (apiUrl.includes("findallactive")) {
            res = await api.get(apiUrl);
          } else {
            res = await api.post(apiUrl, payload);
          }

          const data =
            res?.data?.data ||
            res?.data?.dtoList ||
            res?.data ||
            [];

          if (includeNoneOption) {
            setOptions([
              { [optionValue]: "", [optionLabel]: "None (Root Category)" },
              ...data,
            ]);
          } else {
            setOptions(data);
          }
        }
      } catch (err) {
        console.error(" Dropdown load error:", err);
        setOptions([]);
      } finally {
        setLoading(false);
      }
    };

    fetchOptions();
  }, [apiUrl, staticOptions, payload, includeNoneOption, optionLabel, optionValue]);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (ref.current && !ref.current.contains(e.target)) {
        setOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const selectedValues = useMemo(() => {
    if (!options.length) return [];
    if (multiple) return Array.isArray(value) ? value : [];
    return value !== undefined && value !== null ? [value] : [];
  }, [value, multiple, options]);

  const isSelected = (optionVal) =>
    selectedValues.some(
      (v) => String(v).toLowerCase() === String(optionVal).toLowerCase()
    );

  const handleSingleSelect = (val) => {
    onChange({ target: { name, value: val } });
    setOpen(false);
  };

  const handleMultiSelect = (val) => {
    const exists = isSelected(val);

    const updated = exists
      ? selectedValues.filter(
          (v) => String(v).toLowerCase() !== String(val).toLowerCase()
        )
      : [...selectedValues, val];

    onChange({ target: { name, value: updated } });
  };

  const handleSelect = multiple ? handleMultiSelect : handleSingleSelect;

  const selectedLabels = options
    .filter((o) => isSelected(o[optionValue]))
    .map((o) => o[optionLabel]);

  return (
    <div ref={ref} className="relative">
      <button
        type="button"
        onClick={() => setOpen(!open)}
        className="w-full border p-2 flex justify-between items-center"
      >
        <span>
          {selectedLabels.length
            ? selectedLabels.join(", ")
            : placeholder}
        </span>
        <ChevronDown size={16} />
      </button>

      {open && (
        <div className="absolute w-full border bg-white z-50 max-h-60 overflow-y-auto">
          {loading && <div className="p-2 text-gray-500">Loading...</div>}

          {!loading && options.length === 0 && (
            <div className="p-2 text-gray-500">No options found</div>
          )}

          {!loading &&
            options.map((item) => {
              const val = item[optionValue];
              const checked = isSelected(val);

              return (
                <button
                  key={`${optionValue}-${val}`}
                  onClick={() => handleSelect(val)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter" || e.key === " ") {
                      e.preventDefault();
                      handleSelect(val);
                    }
                  }}
                  className="w-full text-left p-2 flex justify-between hover:bg-gray-100 cursor-pointer focus:outline-none focus:bg-gray-200"
                  type="button"
                >
                  {item[optionLabel]}
                  {checked && <Check size={14} />}
                </button>
              );
            })}
        </div>
      )}
    </div>
  );
}

CommonDropDown.propTypes = {
  name: PropTypes.string.isRequired,
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
    PropTypes.array,
  ]),
  onChange: PropTypes.func.isRequired,
  api: PropTypes.string,
  payload: PropTypes.object,
  optionLabel: PropTypes.string,
  optionValue: PropTypes.string,
  placeholder: PropTypes.string,
  multiple: PropTypes.bool,
  options: PropTypes.array,
  includeNoneOption: PropTypes.bool,
};