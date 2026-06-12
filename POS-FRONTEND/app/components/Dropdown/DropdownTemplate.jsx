"use client";
import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import axiosInstance from "../../api/axiosInstance";

const SELECT_CLS =
  "rounded-lg border border-slate-200 bg-white px-3 py-2.5 text-sm font-medium text-slate-900 shadow-sm outline-none transition focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100";

function useDropdownOptions({ apiPath, apiEndpoint, label }) {
  const [apiOptions, setApiOptions] = useState([]);
  const [loading, setLoading] = useState(Boolean(apiPath || apiEndpoint));

  useEffect(() => {
    if (!apiPath && !apiEndpoint) return;
    const fetchOptions = async () => {
      try {
        setLoading(true);
        const response = apiEndpoint
          ? await axiosInstance.get(apiEndpoint)
          : await axiosInstance.post(`/${apiPath}/list`, {
              page: 0, sizePerPage: 100, sortDirection: "ASC", sortField: "identifier",
            });
        const data = response.data;
        const list = data?.dtoList || data?.content || data?.data || data || [];
        setApiOptions(
          Array.isArray(list)
            ? list.map((item) => ({
                value: item.identifier || item.id || item.name,
                label: item.name || item.productname || item.identifier || item.id,
              }))
            : []
        );
      } catch (error) {
        console.error(`Failed to fetch ${label}:`, error);
      } finally {
        setLoading(false);
      }
    };
    fetchOptions();
  }, [apiPath, apiEndpoint, label]);

  return { apiOptions, loading };
}

function SingleDropdown({ name, options, value, onChange, placeholder, required, helperText }) {
  const normalizedValue = Array.isArray(value) ? (value[0] ?? "") : (value ?? "");
  return (
    <select
      id={name}
      name={name}
      value={normalizedValue}
      onChange={(e) => onChange(name, e.target.value)}
      required={required}
      className={SELECT_CLS}
    >
      <option value="">{placeholder}</option>
      {options.map((option) => (
        <option key={option.value} value={option.value}>{option.label}</option>
      ))}
      {helperText && <p className="text-xs font-medium text-slate-400">{helperText}</p>}
    </select>
  );
}

SingleDropdown.propTypes = {
  name: PropTypes.string.isRequired,
  options: PropTypes.array.isRequired,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number, PropTypes.array]),
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  required: PropTypes.bool,
  helperText: PropTypes.string,
};

function MultiDropdown({ name, options, value, onChange, required, helperText }) {
  const normalizedValue = value ?? [];
  return (
    <>
      <select
        id={name}
        name={name}
        value={normalizedValue}
        onChange={(e) => {
          const selected = Array.from(e.target.selectedOptions, (o) => o.value);
          onChange(name, selected);
        }}
        multiple
        required={required}
        className={SELECT_CLS}
      >
        {options.map((option) => (
          <option key={option.value} value={option.value}>{option.label}</option>
        ))}
      </select>
      {helperText && <p className="text-xs font-medium text-slate-400">{helperText}</p>}
    </>
  );
}

MultiDropdown.propTypes = {
  name: PropTypes.string.isRequired,
  options: PropTypes.array.isRequired,
  value: PropTypes.arrayOf(PropTypes.string),
  onChange: PropTypes.func.isRequired,
  required: PropTypes.bool,
  helperText: PropTypes.string,
};

export default function DropdownTemplate({
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
}) {
  const { apiOptions, loading } = useDropdownOptions({ apiPath, apiEndpoint, label });
  const dropdownOptions = apiPath || apiEndpoint ? apiOptions : options;

  if (loading) {
    return (
      <p className="rounded-lg border border-slate-200 bg-slate-50 px-4 py-2.5 text-sm font-semibold text-slate-500">
        Loading {label}...
      </p>
    );
  }

  return (
    <div className="flex flex-col gap-1.5">
      <label htmlFor={name} className="text-sm font-bold text-slate-700">{label}</label>
      {multiple ? (
        <MultiDropdown
          name={name}
          options={dropdownOptions}
          value={value}
          onChange={onChange}
          required={required}
          helperText={helperText}
        />
      ) : (
        <SingleDropdown
          name={name}
          options={dropdownOptions}
          value={value}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
          helperText={helperText}
        />
      )}
    </div>
  );
}

DropdownTemplate.propTypes = {
  label: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  options: PropTypes.array,
  apiPath: PropTypes.string,
  apiEndpoint: PropTypes.string,
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number, PropTypes.arrayOf(PropTypes.string)]),
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  multiple: PropTypes.bool,
  required: PropTypes.bool,
  helperText: PropTypes.string,
};