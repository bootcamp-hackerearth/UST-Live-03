"use client";

import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { fetchActiveItems } from "@/services/api";

export default function MultiCheckBox({
  label,
  model,
  values,
  onChange,
  required = false
}) {
  const [options, setOptions] =
    useState([]);

  const [loading, setLoading] =
    useState(false);

  const [error, setError] =
    useState("");

  const [touched, setTouched] =
    useState(false);

  useEffect(() => {
    loadOptions();
  }, []);

  const loadOptions = async () => {
  try {
    setLoading(true);
    setError("");

    const response = await fetchActiveItems(model);

    console.log(response);

    setOptions(
      Array.isArray(response)
        ? response
        : response?.items || []
    );
      } catch (error) {
        console.log(error);

    setError(`Failed to load ${label}`);
  } finally {
    setLoading(false);
  }
};

  const handleCheckboxChange = (
    optionValue
  ) => {
    setTouched(true);

    if (
      values.includes(optionValue)
    ) {
      onChange(
        values.filter(
          (item) =>
            item !== optionValue
        )
      );
    } else {
      onChange([
        ...values,
        optionValue
      ]);
    }
  };

  const hasError =
    required &&
    touched &&
    values.length === 0;

  return (
    <div>
      <p className="block mb-2 text-sm font-medium text-[#475467]">
        {label}
      </p>

      <div
        className={`bg-white border rounded-2xl p-4 space-y-3 transition-all ${
          hasError
            ? "border-red-500"
            : "border-[#d0d5dd]"
        }`}
      >
        {loading && (
          <p className="text-sm text-gray-500">
            Loading...
          </p>
        )}

        {!loading &&
          options.map((item) => (
            <label
              key={item.identifier}
              className="flex items-center gap-3 cursor-pointer"
            >
              <input
                type="checkbox"
                checked={values.includes(
                  item.identifier
                )}
                onChange={() =>
                  handleCheckboxChange(
                    item.identifier
                  )
                }
                className="w-4 h-4 border-gray-300 rounded accent-blue-600"
              />

              <span className="text-sm text-[#101828]">
                {item.identifier}
              </span>
            </label>
          ))}

        {!loading &&
          options.length === 0 && (
            <p className="text-sm text-gray-500">
              No options available
            </p>
          )}
      </div>

      {hasError && (
        <p className="mt-2 text-sm text-red-500">
          At least one option must be selected
        </p>
      )}

      {error && (
        <p className="mt-2 text-sm text-red-500">
          {error}
        </p>
      )}
    </div>
  );
}

MultiCheckBox.propTypes = {
  label: PropTypes.string.isRequired,
  model: PropTypes.string.isRequired,
  values: PropTypes.arrayOf(PropTypes.string).isRequired,
  onChange: PropTypes.func.isRequired,
  required: PropTypes.bool,
};