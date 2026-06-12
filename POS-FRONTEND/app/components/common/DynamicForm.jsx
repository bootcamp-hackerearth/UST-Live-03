"use client";

import React, { useState } from "react";
import PropTypes from "prop-types";

import InputField from "./InputField";
import SingleSelect from "./SingleSelect";
import MultiSelect from "./MultiSelect";
import commonApi from "../../services/commonApi";

function DynamicForm({
  title,
  fields = [],
  routeName,
  initialValues = {},
  onSuccess,
  isModal = false,
  onCancel,
  existingData = [],
  uniqueFields = [],
}) {
  const [formData, setFormData] = useState(initialValues);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});

  const isEdit = !!initialValues?.identifier;

  let buttonLabel = "Save";
  if (loading) {
    buttonLabel = "Saving...";
  } else if (isEdit) {
    buttonLabel = "Update";
  }

  function handleChange(key, value) {
    setFormData((prev) => ({
      ...prev,
      [key]: value,
    }));

    setErrors((prev) => ({
      ...prev,
      [key]: "",
    }));
  }

  const validate = () => {
    const newErrors = {};

    fields.forEach((field) => {
      const value = formData[field.key];

      if (field.required !== false) {
        if (
          value === undefined ||
          value === null ||
          value === "" ||
          (Array.isArray(value) && value.length === 0)
        ) {
          newErrors[field.key] = `${field.label} is required`;
        }
      }

      if (uniqueFields.includes(field.key) && value) {
        const isDuplicate = existingData.some(
          (item) =>
            String(item[field.key]).toLowerCase() ===
              String(value).toLowerCase() &&
            item.identifier !== initialValues.identifier
        );

        if (isDuplicate) {
          newErrors[field.key] = `${field.label} already exists`;
        }
      }
    });

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const formatValue = (value) => {
    if (value === "true") return true;
    if (value === "false") return false;
    return value ?? "";
  };

  const buildPayload = () => {
    const payload = {};

    fields.forEach((field) => {
      const value = formData[field.key];

      if (field.type === "multiselect") {
        payload[field.key] = Array.isArray(value)
          ? value.map((item) =>
              typeof item === "object"
                ? item[field.optionValue || "identifier"]
                : item
            )
          : [];
      } else {
        payload[field.key] = formatValue(value);
      }
    });

    console.log("FINAL PAYLOAD:", payload);
    return payload;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validate()) return;

    try {
      setLoading(true);

      const payload = buildPayload();

      const response = isEdit
        ? await commonApi.update(routeName, payload)
        : await commonApi.add(routeName, payload);

      alert(isEdit ? "Updated Successfully" : "Saved Successfully");

      if (onSuccess) {
        onSuccess(response.data);
      }
    } catch (err) {
      console.log(err);

      if (err.response?.status === 409) {
        alert("Duplicate entry already exists");
      } else {
        alert("Something went wrong");
      }
    } finally {
      setLoading(false);
    }
  };

  const renderField = (field) => {
    const isReadOnly = field.disabled || (isEdit && field.readOnlyOnEdit);

    return (
      <div key={field.key} className="w-full flex flex-col gap-1">
        {(field.type === "text" ||
          field.type === "number" ||
          field.type === "email" ||
          field.type === "password") && (
          <InputField
            label={field.label}
            type={field.type}
            value={formData[field.key] || ""}
            placeholder={field.placeholder}
            required={field.required || false}
            disabled={isReadOnly}
            error={errors[field.key]}
            onChange={(value) => handleChange(field.key, value)}
          />
        )}

        {field.type === "select" && (
          <SingleSelect
            label={field.label}
            value={formData[field.key]}
            onChange={(value) => handleChange(field.key, value)}
            options={field.options || []}
            optionLabel={field.optionLabel}
            optionValue={field.optionValue}
            required={field.required || false}
            disabled={isReadOnly}
          />
        )}

        {field.type === "multiselect" && (
          <MultiSelect
            label={field.label}
            selectedValues={
              Array.isArray(formData[field.key])
                ? formData[field.key].map((item) =>
                    typeof item === "string"
                      ? {
                          [field.optionValue || "identifier"]: item,
                          [field.optionLabel || "identifier"]: item,
                        }
                      : item
                  )
                : []
            }
            onChange={(value) => handleChange(field.key, value)}
            options={field.options || []}
            optionLabel={field.optionLabel}
            optionValue={field.optionValue}
            disabled={isReadOnly}
          />
        )}

        {errors[field.key] && field.type !== "text" && (
          <span className="text-red-500 text-xs mt-1">
            {errors[field.key]}
          </span>
        )}
      </div>
    );
  };

  if (isModal) {
    return (
      <form onSubmit={handleSubmit} className="flex flex-col">
        <div className="px-6 py-6 flex flex-col gap-5">
          {fields.map((field) => (
            <div key={field.key}>{renderField(field)}</div>
          ))}
        </div>

        <div className="border-t px-6 py-5 flex items-center justify-center gap-4">
          <button
            type="button"
            onClick={onCancel}
            className="min-w-[140px] border border-red-500 text-red-600 hover:bg-red-50 px-6 py-3 rounded-xl font-medium"
          >
            Cancel
          </button>

          <button
            type="submit"
            disabled={loading}
            className="min-w-[140px] bg-red-600 hover:bg-red-700 text-white px-6 py-3 rounded-xl font-medium disabled:opacity-50"
          >
            {buttonLabel}
          </button>
        </div>
      </form>
    );
  }

  return (
    <div className="max-w-6xl mx-auto bg-white rounded-2xl shadow-sm">
      {title && (
        <div className="p-6 border-b">
          <h2 className="text-3xl font-semibold">{title}</h2>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
          {fields.map((field) => (
            <div
              key={field.key}
              className={field.fullWidth ? "md:col-span-2" : ""}
            >
              {renderField(field)}
            </div>
          ))}
        </div>

        <div className="p-6 border-t flex justify-end gap-4">
          {onCancel && (
            <button
              type="button"
              onClick={onCancel}
              className="px-6 py-3 border border-red-500 text-red-600 rounded-lg"
            >
              Cancel
            </button>
          )}

          <button
            type="submit"
            disabled={loading}
            className="px-6 py-3 bg-red-600 hover:bg-red-700 text-white rounded-lg disabled:opacity-50"
          >
            {buttonLabel}
          </button>
        </div>
      </form>
    </div>
  );
}

DynamicForm.propTypes = {
  title: PropTypes.string,
  fields: PropTypes.arrayOf(PropTypes.object),
  routeName: PropTypes.string.isRequired,
  initialValues: PropTypes.object,
  onSuccess: PropTypes.func,
  isModal: PropTypes.bool,
  onCancel: PropTypes.func,
  existingData: PropTypes.arrayOf(PropTypes.object),
  uniqueFields: PropTypes.arrayOf(PropTypes.string),
};

export default DynamicForm;
