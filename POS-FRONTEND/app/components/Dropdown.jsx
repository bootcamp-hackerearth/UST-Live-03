"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../api";

const extractOptions = (data) => {
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.dtoList)) return data.dtoList;
  if (Array.isArray(data?.content)) return data.content;
  return [];
};

const useDropdownOptions = (apiUrl, name) => {
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!apiUrl) return;

    api
      .post(apiUrl, { page: 0, sizePerPage: 10 })
      .then((res) => {
        setOptions(extractOptions(res.data));
      })
      .catch(() => {
        console.error("Failed to load dropdown:", name);
        setOptions([]);
      });
  }, [apiUrl, name]);

  return options;
};

const renderOptions = (options, displayKey) => {
  if (options.length === 0) {
    return <option disabled>No options available</option>;
  }

  return options.map((opt) => (
    <option key={opt.identifier} value={opt.identifier}>
      {opt[displayKey] || opt.identifier}
    </option>
  ));
};

const MultiDropdown = ({
  name,
  value,
  onChange,
  apiUrl,
  disabled = false,
  displayKey = "name",
}) => {
  const options = useDropdownOptions(apiUrl, name);

  const handleMultiChange = (e) => {
    if (disabled) return;

    const selectedValues = Array.from(e.target.options)
      .filter((option) => option.selected)
      .map((option) => option.value);

    onChange({
      target: {
        name,
        value: selectedValues,
      },
    });
  };

  return (
    <select
      name={name}
      multiple
      value={value || []}
      onChange={handleMultiChange}
      disabled={disabled}
      className={`w-full p-3 border rounded ${
        disabled ? "bg-gray-200 cursor-not-allowed" : ""
      }`}
    >
      {renderOptions(options, displayKey)}
    </select>
  );
};

const SingleDropdown = ({
  name,
  value,
  onChange,
  apiUrl,
  placeholder,
  disabled = false,
  displayKey = "name",
}) => {
  const options = useDropdownOptions(apiUrl, name);

  return (
    <select
      name={name}
      value={value || ""}
      onChange={onChange}
      disabled={disabled}
      className={`w-full p-3 border rounded ${
        disabled ? "bg-gray-200 cursor-not-allowed" : ""
      }`}
    >
      <option value="">
        {placeholder || "-- Select --"}
      </option>

      {renderOptions(options, displayKey)}
    </select>
  );
};

MultiDropdown.propTypes = {
  name: PropTypes.string.isRequired,
  value: PropTypes.array,
  onChange: PropTypes.func.isRequired,
  apiUrl: PropTypes.string,
  disabled: PropTypes.bool,
  displayKey: PropTypes.string,
};

SingleDropdown.propTypes = {
  name: PropTypes.string.isRequired,
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
  apiUrl: PropTypes.string,
  placeholder: PropTypes.string,
  disabled: PropTypes.bool,
  displayKey: PropTypes.string,
};

export { MultiDropdown, SingleDropdown };