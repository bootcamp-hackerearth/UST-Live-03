"use client";
import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import axiosInstance from "../api/axiosInstance";
 
const Dropdown = ({
  label,
  name,
  options = [],
  apiPath,
  value,
  onChange,
  placeholder = "Select an option",
  multiple = false,
  required = false,
  helperText,
  valueField,
  labelField,
  labelFields,
}) => {
  const [apiOptions, setApiOptions] = useState([]);
  const [loading, setLoading] = useState(Boolean(apiPath));
  const [error, setError] = useState("");
 
  const getOptionValue = (item) =>
    valueField
      ? item[valueField]
      : item.identifier ?? item.id ?? item.name;

  const getOptionLabel = (item) => {
    if (Array.isArray(labelFields) && labelFields.length > 0) {
      return labelFields.map((field) => item[field]).filter(Boolean).join(" - ");
    }

    if (labelField && item[labelField]) {
      return item[labelField];
    }

    if (item.productName) {
      return `${item.identifier ?? item.id} - ${item.productName}`;
    }

    if (item.name) {
      return `${item.identifier ?? item.id} - ${item.name}`;
    }

    return item.identifier ?? item.id;
  };

  const handleSingleChange = (event) => {
    onChange(name, event.target.value);
  };

  const handleMultipleChange = (event) => {
    const selected = Array.from(event.target.selectedOptions, (option) => option.value);
    onChange(name, selected);
  };

  const onControlChange = multiple ? handleMultipleChange : handleSingleChange;

  useEffect(() => {
    if (!apiPath) return;
 
    const fetchOptions = async () => {
      setLoading(true);
      setError("");
 
      try {
        const response = await axiosInstance.post(`/${apiPath}/list`, {
          page: 0,
          sizePerPage: 100,
          sortDirection: "ASC",
          sortField: "identifier",
        });
 
        const list =
          response.data.dtoList ??
          response.data.content ??
          (Array.isArray(response.data) ? response.data : []);

        setApiOptions(
          list
            .filter((item) => item.status !== false)
            .map((item) => ({
              value: getOptionValue(item),
              label: getOptionLabel(item),
            }))
        );
      } catch (error) {
        const status = error?.response?.status;
        const rawMessage =
          error?.response?.data?.message ||
          error?.message ||
          `Failed to fetch ${label}`;
        const endpoint = `/${apiPath}/list`;
        const message =
          status === 403
            ? `Access denied while loading ${label}. You may not have permission to view this list.`
            : rawMessage;
        setError(status ? `${message} (HTTP ${status} on ${endpoint})` : message);
        console.error(`Failed to fetch ${label}:`, error);
      } finally {
        setLoading(false);
      }
    };
 
    fetchOptions();
  }, [apiPath, label]);
 
  const dropdownOptions = apiPath ? apiOptions : options;
 
  if (loading) {
    return <p className="text-sm text-gray-400">Loading {label}...</p>;
  }

  if (error) {
    return <p className="text-sm text-rose-600">{error}</p>;
  }
 
  return (
    <div className="flex flex-col gap-1">
      <label className="text-sm font-semibold text-black">{label}</label>
      <select
        name={name}
        value={value ?? (multiple ? [] : "")}
        onChange={onControlChange}
        multiple={multiple}
        required={required}
        className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-black outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition"
      >
        {!multiple && <option value="">{placeholder}</option>}
        {dropdownOptions.map((option) => (
          <option key={option.value ?? option.label} value={option.value ?? option.label}>
            {option.label ?? option.value}
          </option>
        ))}
      </select>
      {helperText && <p className="text-xs text-black">{helperText}</p>}
    </div>
  );
};
 
Dropdown.propTypes = {
  label: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  options: PropTypes.arrayOf(
    PropTypes.shape({
      value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      label: PropTypes.string,
    })
  ),
  apiPath: PropTypes.string,
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
    PropTypes.arrayOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
  ]),
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  multiple: PropTypes.bool,
  required: PropTypes.bool,
  helperText: PropTypes.string,
  valueField: PropTypes.string,
  labelField: PropTypes.string,
  labelFields: PropTypes.arrayOf(PropTypes.string),
};

export default Dropdown;
