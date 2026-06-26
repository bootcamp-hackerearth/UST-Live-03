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
  payload = undefined,
  optionLabel = "identifier",
  optionValue = "identifier",
  placeholder = "Select option",
  multiple = false,
  options: staticOptions,
}) {
  const [options, setOptions] = useState([]);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const ref = useRef(null);

  useEffect(() => {
    const fetchOptions = async () => {
      try {
        setLoading(true);

        if (staticOptions?.length) {
          setOptions(staticOptions);
          return;
        }

        if (!apiUrl) return;

        const res = payload
          ? await api.post(apiUrl, payload)
          : await api.get(apiUrl);

        const data = res?.data?.data || res?.data?.dtoList || res?.data || [];
        setOptions(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error("Dropdown load error:", err);
        setOptions([]);
      } finally {
        setLoading(false);
      }
    };

    fetchOptions();
  }, [apiUrl, JSON.stringify(payload), JSON.stringify(staticOptions)]);


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

  const isSelected = (optionVal) => {
    const normalizedOption = String(optionVal ?? "").toLowerCase().trim();
    return selectedValues.some(
      (v) => String(v ?? "").toLowerCase().trim() === normalizedOption
    );
  };

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
    .filter((o) => isSelected(o?.[optionValue]))
    .map((o) => o?.[optionLabel] || "N/A");

  return (
    <div ref={ref} className="relative">
      <button
        type="button"
        onClick={() => setOpen(!open)}
        className="w-full px-3 py-2.5 rounded-lg text-sm bg-white border border-gray-300 text-gray-800 flex justify-between items-center focus:outline-none focus:ring-2 focus:ring-blue-600"
      >
        <span className="truncate text-left">
          {selectedLabels.length ? (
            selectedLabels.join(", ")
          ) : (
            <span className="text-gray-400">{placeholder}</span>
          )}
        </span>
        <ChevronDown size={16} className={`transition ${open ? "rotate-180" : ""}`} />
      </button>

      {open && (
        <div className="absolute left-0 top-full mt-1 w-full z-50 bg-white border border-gray-200 rounded-lg shadow-lg max-h-60 overflow-y-auto">
          {loading && <div className="p-3 text-sm text-gray-500">Loading...</div>}

          {!loading && options.length === 0 && (
            <div className="p-3 text-sm text-gray-500">No options found</div>
          )}

          {!loading &&
            options.map((item, index) => {
              const val = item?.[optionValue] ?? item?.value ?? item?.identifier ?? "";
              const label = item?.[optionLabel] ?? item?.label ?? item?.identifier ?? "N/A";

              if (!val) return null;

              const checked = isSelected(val);

              return (
                <button
                  key={`${val}-${index}`}
                  onClick={() => handleSelect(val)}
                  className={`w-full text-left px-3 py-2 text-sm flex justify-between items-center ${
                    checked ? "bg-blue-600 text-white" : "hover:bg-blue-50 text-gray-700"
                  }`}
                  type="button"
                >
                  {label}
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
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number, PropTypes.array]),
  onChange: PropTypes.func.isRequired,
  api: PropTypes.string,
  payload: PropTypes.object,
  optionLabel: PropTypes.string,
  optionValue: PropTypes.string,
  placeholder: PropTypes.string,
  multiple: PropTypes.bool,
  options: PropTypes.array,
};