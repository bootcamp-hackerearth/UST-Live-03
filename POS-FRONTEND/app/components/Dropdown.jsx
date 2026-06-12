"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import api from "../api";

const MultiDropdown = ({
  name,
  value,
  onChange,
  apiUrl,
  disabled = false,
}) => {
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!apiUrl) return;

    api
      .post(apiUrl, { page: 0, sizePerPage: 10 })
      .then((res) => {
        setOptions(res.data.dtoList || res.data || []);
      })
      .catch(() => {
        console.error("Failed to load dropdown:", name);
      });
  }, [apiUrl, name]);

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
      {options.length === 0 ? (
        <option disabled>No options available</option>
      ) : (
        options.map((opt) => (
          <option
            key={opt.id || opt.identifier}
            value={opt.identifier}
          >
            {opt.identifier}
          </option>
        ))
      )}
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
}) => {
  const [options, setOptions] = useState([]);

  useEffect(() => {
    if (!apiUrl) return;

    api
      .post(apiUrl, { page: 0, sizePerPage: 10 })
      .then((res) => {
        setOptions(res.data.content || res.data || []);
      })
      .catch(() => {
        console.error("Failed to load dropdown:", name);
      });
  }, [apiUrl, name]);

  const handleChange = (e) => {
    if (disabled) return;
    onChange(e);
  };

  return (
    <select
      name={name}
      value={value || ""}
      onChange={handleChange}
      disabled={disabled}
      className={`w-full p-3 border rounded ${
        disabled ? "bg-gray-200 cursor-not-allowed" : ""
      }`}
    >
      <option value="">
        {placeholder || "-- Select --"}
      </option>

      {options.map((opt) => (
        <option
          key={opt.id || opt.identifier}
          value={opt.identifier}
        >
          {opt.identifier}
        </option>
      ))}
    </select>
  );
};

MultiDropdown.propTypes = {
  name: PropTypes.string.isRequired,
  value: PropTypes.array,
  onChange: PropTypes.func.isRequired,
  apiUrl: PropTypes.string,
  disabled: PropTypes.bool,
};

SingleDropdown.propTypes = {
  name: PropTypes.string.isRequired,
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
  apiUrl: PropTypes.string,
  placeholder: PropTypes.string,
  disabled: PropTypes.bool,
};

export { MultiDropdown, SingleDropdown };