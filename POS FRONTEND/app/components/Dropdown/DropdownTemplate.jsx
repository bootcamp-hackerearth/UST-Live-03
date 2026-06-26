"use client";
import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import axiosInstance from "../../api/axiosInstance";

const DropdownTemplate = ({
  label,
  name,
  options = [],
  apiPath,
  apiEndpoint,
  value,
  onChange,
  placeholder = "Select an option",
  multiple = false,
  required = false,
  helperText,
  optionValue = null,
  optionLabel = null,
}) => {
  const [apiOptions, setApiOptions] = useState([]);
  const [loading, setLoading] = useState(Boolean(apiPath || apiEndpoint));

  useEffect(() => {
    if (!apiPath && !apiEndpoint) return;

    const fetchOptions = async () => {
      setLoading(true);
      try {
        const response = apiEndpoint
          ? await axiosInstance.get(apiEndpoint)
          : await axiosInstance.post(`/${apiPath}/list`, {
              page: 0,
              sizePerPage: 100,
              sortDirection: "ASC",
              sortField: "identifier",
            });

        const list =
          response.data.dtoList ?? response.data.content ?? response.data ?? [];
        setApiOptions(
          list.map((item) => ({
            value: optionValue ? item[optionValue] : (item.identifier ?? item.id ?? item.name),
            label: optionLabel ? item[optionLabel] : (item.name ?? item.identifier ?? item.id),
          }))
        );
      } catch (error) {
        console.error(`Failed to fetch ${label}:`, error);
      } finally {
        setLoading(false);
      }
    };

    fetchOptions();
  }, [apiPath, apiEndpoint, label, optionValue, optionLabel]);

  const handleSingleChange = (event) => {
    onChange(name, event.target.value);
  };

  const handleMultipleChange = (event) => {
    const selected = Array.from(
      event.target.selectedOptions,
      (option) => option.value
    );
    onChange(name, selected);
  };

  const dropdownOptions = apiPath || apiEndpoint ? apiOptions : options;
  const selectValue = multiple ? (value ?? []) : String(value ?? "");

  if (loading) {
    return <p className="text-sm text-gray-400">Loading {label}...</p>;
  }

  return (
    <div className="flex flex-col gap-1">
      <label className="text-sm font-semibold text-gray-600">{label}</label>
      <select
        name={name}
        value={selectValue}
        onChange={multiple ? handleMultipleChange : handleSingleChange}
        multiple={multiple}
        size={multiple ? 4 : 1}
        required={required}
        className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-800 bg-white outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition"
      >
        {!multiple && <option value="">{placeholder}</option>}
        {dropdownOptions.map((option) => (
          <option key={option.value ?? option.label} value={option.value ?? option.label}>
            {option.label ?? option.value}
          </option>
        ))}
      </select>
      {helperText && <p className="text-xs text-gray-400 mt-1">{helperText}</p>}
    </div>
  );
};

DropdownTemplate.propTypes = {
  label: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  options: PropTypes.arrayOf(
    PropTypes.shape({
      value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      label: PropTypes.string,
    })
  ),
  apiPath: PropTypes.string,
  apiEndpoint: PropTypes.string,
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.arrayOf(PropTypes.string),
  ]),
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  multiple: PropTypes.bool,
  required: PropTypes.bool,
  helperText: PropTypes.string,
  optionValue: PropTypes.string,
  optionLabel: PropTypes.string,
};

DropdownTemplate.defaultProps = {
  options: [],
  apiPath: null,
  apiEndpoint: null,
  value: undefined,
  placeholder: "Select an option",
  multiple: false,
  required: false,
  helperText: null,
  optionValue: null,
  optionLabel: null,
};

export default DropdownTemplate;