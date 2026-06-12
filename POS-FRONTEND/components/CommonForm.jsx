"use client";

import PropTypes from "../lib/propTypes";
import { useEffect, useState } from "react";

export default function CommonForm({
  title,
  mode,
  fields,
  data,
  onSubmit,
  onClose,
  onSuccess,
}) {
  const [formData, setFormData] =
    useState({});

  const [options, setOptions] =
    useState({});

  useEffect(() => {
    const initialData = {
      id: data?.id || null,
    };

    fields.forEach((field) => {
      const value =
        data?.[field.name];

      if (
        field.type ===
        "multiselect"
      ) {
        initialData[field.name] =
          value || [];
      } else {
        initialData[field.name] =
          value || "";
      }
    });

    setFormData(initialData);
  }, [data, fields]);

  useEffect(() => {
    fields.forEach(async (field) => {
      if (
        field.type ===
          "multiselect" ||
        field.type === "select"
      ) {
        try {
          const response =
            await fetch(
              field.apiUrl,
              {
                method: "POST",
                headers: {
                  Authorization: `Bearer ${localStorage.getItem(
                    "token"
                  )}`,
                  "Content-Type":
                    "application/json",
                },
                body: JSON.stringify({
                  page: 0,
                  sizePerPage: 100,
                  sortDirection:
                    "ASC",
                  sortField:
                    "identifier",
                }),
              }
            );

          const result =
            await response.json();

          const list =
            Array.isArray(
              result
            )
              ? result
              : result.dtoList ||
                result.content ||
                [];

          setOptions(
            (prev) => ({
              ...prev,
              [field.name]:
                list,
            })
          );
        } catch (error) {
          console.log(error);
        }
      }
    });
  }, [fields]);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]:
        e.target.value,
    });
  };

  const handleMultiSelect =
    (e) => {
      const values =
        Array.from(
          e.target
            .selectedOptions
        ).map(
          (option) =>
            option.value
        );

      setFormData({
        ...formData,
        [e.target.name]:
          values,
      });
    };

  const handleSubmit = async (
    e
  ) => {
    e.preventDefault();

    const payload = {
      ...formData,
    };

    fields.forEach((field) => {
      if (
        field.type ===
          "multiselect" &&
        !payload[field.name]
      ) {
        payload[field.name] =
          [];
      }
    });

    console.log(
      "Submitting:",
      payload
    );

    try {
      const result = await onSubmit(payload);

      if (result && typeof onSuccess === "function") {
        onSuccess();
      }
    } catch (err) {
      // swallow - individual handlers handle logging
      console.log("CommonForm submit error:", err);
    }
  };

  const renderField = (field) => {
    if (field.type === "multiselect") {
      return (
        <select
          multiple
          name={field.name}
          value={formData[field.name] || []}
          onChange={handleMultiSelect}
          required={field.required}
          className="w-full h-[120px] rounded-xl border border-gray-300 px-4 text-black"
        >
          {(options[field.name] || []).map((option) => (
            <option
              key={option.identifier}
              value={option.identifier}
            >
              {option.identifier}
            </option>
          ))}
        </select>
      );
    }

    if (field.type === "staticSelect") {
      return (
        <select
          name={field.name}
          value={formData[field.name] || ""}
          onChange={handleChange}
          required={field.required}
          className="w-full h-14 rounded-xl border border-gray-300 px-4 text-black"
        >
          <option value="">
            Select {field.label}
          </option>

          {field.options?.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
      );
    }

    if (field.type === "select") {
      return (
        <select
          name={field.name}
          value={formData[field.name] || ""}
          onChange={handleChange}
          required={field.required}
          className="w-full h-14 rounded-xl border border-gray-300 px-4 text-black"
        >
          <option value="">
            Select {field.label}
          </option>

          {(options[field.name] || []).map((option) => (
            <option
              key={option.identifier}
              value={option.identifier}
            >
              {option.identifier}
            </option>
          ))}
        </select>
      );
    }

    return (
      <input
        type={field.type || "text"}
        name={field.name}
        value={formData[field.name] || ""}
        onChange={handleChange}
        placeholder={field.label}
        required={field.required}
        className="w-full h-14 rounded-xl border border-gray-300 px-4 text-black"
      />
    );
  };

  return (
    <div className="w-full">
      <h2 className="text-3xl font-bold text-black mb-6">
        {mode === "add"
          ? `Add ${title}`
          : `Edit ${title}`}
      </h2>

      <form
        onSubmit={handleSubmit}
        className="space-y-4"
      >
        {fields.map((field) => (
          <div key={field.name}>
            {renderField(field)}
          </div>
        ))}

        <div className="flex justify-end gap-3 pt-2">
          <button
            type="button"
            onClick={onClose}
            className="px-6 py-2 border rounded-xl text-black"
          >
            Cancel
          </button>

          <button
            type="submit"
            className="px-6 py-2 bg-black text-white rounded-xl"
          >
            {mode === "add"
              ? "Save"
              : "Update"}
          </button>
        </div>
      </form>
    </div>
  );
}

CommonForm.propTypes = {
  title: PropTypes.string.isRequired,
  mode: PropTypes.oneOf(["add", "edit"]).isRequired,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      type: PropTypes.string,
      label: PropTypes.string,
      required: PropTypes.bool,
      apiUrl: PropTypes.string,
      options: PropTypes.arrayOf(PropTypes.string),
    })
  ).isRequired,
  data: PropTypes.shape({
    id: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.number,
    ]),
  }),
  onSubmit: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
  onSuccess: PropTypes.func,
};