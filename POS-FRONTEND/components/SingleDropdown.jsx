"use client";

import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";

function SingleDropdown({
  value,
  onChange,
  label,
  apiPath,
  required = false,
  valueField = "identifier",
  displayField = "identifier",
  urlMethod,
}) {
  const [options, setOptions] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    const loadDropdownData = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        setErrorMsg("User not logged in");
        setIsLoading(false);
        return;
      }
      try {
        const fullUrl = `/api/${apiPath}`;
        const response = await fetch(fullUrl, {
          method: urlMethod === "get" ? "GET" : "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body:
            urlMethod === "get"
              ? undefined
              : JSON.stringify({ page: 0, sizePerPage: 100 }),
        });

        if (!response.ok) {
          console.error("Dropdown API error:", response.status);
          setErrorMsg(`Failed (${response.status})`);
          setIsLoading(false);
          return;
        }

        const result = await response.json();
        const listData = Array.isArray(result)
          ? result
          : (result?.dtoList ?? []);
        setOptions(listData);
      } catch (error) {
        console.error("Dropdown fetch error:", error);
        setErrorMsg("Unable to load data");
      } finally {
        setIsLoading(false);
      }
    };

    loadDropdownData();
  }, [apiPath]);

  const selectId = `single-${apiPath}`;

  return (
    <div style={{ width: "100%" }}>
      <label
        style={{
          display: "block",
          marginBottom: 6,
          fontWeight: "bold",
          fontSize: 14,
          color: "#111827",
        }}
        htmlFor={selectId}
      >
        {label}
        {required && <span style={{ color: "red", marginLeft: 4 }}>*</span>}
      </label>

      <select
        id={selectId}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        required={required}
        disabled={isLoading}
        style={{
          width: "100%",
          border: "1px solid #d1d5db",
          borderRadius: 8,
          padding: "10px",
          fontSize: 14,
          background: "#fff",
          whiteSpace: "pre-wrap",
        }}
      >
        {isLoading && <option value="">Loading {label}...</option>}

        {!isLoading && errorMsg && <option value="">{errorMsg}</option>}

        {!isLoading && !errorMsg && (
          <>
            <option value="">Select {label}</option>

            {options.map((item) => {
              const optionValue =
                typeof item === "string" ? item : item?.[valueField];
              let optionLabel;
              if (typeof displayField === "function") {
                optionLabel = displayField(item);
              } else if (typeof item === "string") {
                optionLabel = item;
              } else {
                optionLabel = item?.[displayField];
              }

              let keyVal;
              if (optionValue !== undefined && optionValue !== null) {
                keyVal = String(optionValue);
              } else if (item && typeof item === "object") {
                keyVal =
                  item?.id !== undefined && item?.id !== null
                    ? String(item.id)
                    : JSON.stringify(item);
              } else {
                keyVal = String(optionValue ?? "");
              }

              return (
                <option key={keyVal} value={optionValue}>
                  {optionLabel}
                </option>
              );
            })}
          </>
        )}
      </select>
    </div>
  );
}

SingleDropdown.propTypes = {
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
  label: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  required: PropTypes.bool,
  valueField: PropTypes.string,
  displayField: PropTypes.oneOfType([PropTypes.string, PropTypes.func]),
  urlMethod: PropTypes.string,
};
export default SingleDropdown;
