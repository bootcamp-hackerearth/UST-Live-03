"use client";

import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { listItems, fetchActiveProducts } from "@/services/api";

export default function SingleDropDown({
  label,
  model,
  value,
  onChange,
  placeholder = "Select Option",
  required = false
}) {

  const [options, setOptions] =
    useState([]);

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  useEffect(() => {
    loadOptions();
  }, []);

  const loadOptions = async () => {

    try {
      setLoading(true);
      setError("");

      let response;

        if (model === "product") {
          response =
            await fetchActiveProducts();
        } else {
          response =
            await listItems(model);
        }  

      console.log(
        `${model} RESPONSE:`,
        response
      );

      setOptions(
        Array.isArray(response)
          ? response
          : response?.items || []
      );

    } catch (error) {
      console.log(error);

      setError(
        `Failed to load ${label}`
      );

    } finally {
      setLoading(false);
    }
  };

  return (
    <div>

      <label className="block mb-2 text-sm font-medium text-[#475467]">
        {label}
      </label>

      <select
        value={value || ""}
        onChange={(event) =>
          onChange(event.target.value)
        }
        required={required}
        disabled={loading}
        className="w-full h-12 px-4 bg-white border border-[#d0d5dd] rounded-2xl text-[#101828] outline-none transition-all focus:border-blue-500 focus:ring-4 focus:ring-blue-100 disabled:bg-gray-100 disabled:cursor-not-allowed"
      >

        <option value="">
          {loading
            ? "Loading..."
            : placeholder}
        </option>

        {options.map((item) => (

          <option
            key={item.identifier}
            value={item.identifier}
          >
            {item.identifier}
          </option>

        ))}

      </select>

      {error && (
        <p className="mt-2 text-sm text-red-600">
          {error}
        </p>
      )}

    </div>
  );
}

SingleDropDown.propTypes = {
  label: PropTypes.string.isRequired,
  model: PropTypes.string.isRequired,
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  required: PropTypes.bool,
};