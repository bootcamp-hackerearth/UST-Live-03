"use client";
import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";

function MultiDropdown({ value, onChange, label, apiPath, required = false }) {
  const [options, setOptions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch(
          `http://localhost:8080/api/${apiPath}/list`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
              page: 0,
              sizePerPage: 100,
              sortDirection: "ASC",
              sortField: "identifier",
            }),
          },
        );

        if (!response.ok) throw new Error(`API Failed: ${response.status}`);
        const data = await response.json();
        setOptions(Array.isArray(data) ? data : data.dtoList || []);
      } catch (err) {
        console.error(`Failed to fetch ${label}:`, err);
      } finally {
        setLoading(false);
      }
    };

    fetchOptions();
  }, [apiPath]);

  if (loading) {
    return <p className="text-sm text-gray-400">Loading {label}...</p>;
  }
  if (options.length === 0) {
    return <p className="text-sm text-red-400">No {label} options found.</p>;
  }

  return (
    <div style={{ width: "100%" }}>
      <label
        style={{
          display: "block",
          fontWeight: "bold",
          marginBottom: "6px",
          fontSize: "14px",
        }}
      >
        {label}
      </label>

      <select
        multiple
        value={value}
        onChange={(e) => {
          const selected = Array.from(
            e.target.selectedOptions,
            (option) => option.value,
          );
          onChange(selected);
        }}
        required={required}
        size={4}
        style={{
          width: "100%",
          border: "1px solid #d1d5db",
          borderRadius: "6px",
          padding: "4px",
          fontSize: "14px",
          boxSizing: "border-box",
          height: "100px",
          maxHeight: "100px",
          overflowY: "auto",
          backgroundColor: "#fff",
          display: "block",
        }}
      >
        {options.map((item) => (
          <option key={item.identifier} value={item.identifier}>
            {item.identifier}
          </option>
        ))}
      </select>

      <p style={{ fontSize: "12px", color: "#6b7280", marginTop: "6px" }}>
        Hold Ctrl (Windows) or Cmd (Mac) to select multiple
      </p>
    </div>
  );
}

MultiDropdown.propTypes = {
  value: PropTypes.array,
  onChange: PropTypes.func.isRequired,
  label: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  required: PropTypes.bool,
};
export default MultiDropdown;
