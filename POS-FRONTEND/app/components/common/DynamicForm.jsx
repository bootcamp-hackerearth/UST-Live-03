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

  const auditKeys = new Set(["createdBy", "createdOn", "modifiedBy", "modifiedOn"]);

  const formFields = fields.filter((field) => !auditKeys.has(field.key));

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

    formFields.forEach((field) => {
      if (field.disabled) return;

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

  const formatDateTime = (value) => {
    if (!value) return "-";

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return date.toLocaleString();
  };

  const buildPayload = () => {
  const payload = {};

  formFields.forEach((field) => {
    const value = formData[field.key];

    if (field.disabled && !field.readOnlyOnEdit) return;

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

  if (isEdit) {
    payload.identifier = initialValues.identifier;
  }

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

    const result = response?.data || response;

    if (result?.success === false) {
      alert(result.message || "Operation failed");
      return;
    }

    alert(result?.message || (isEdit ? "Updated Successfully" : "Saved Successfully"));

    if (onSuccess) {
      onSuccess(result);
    }
  } catch (err) {
    console.log(err);

    if (err.response?.data?.message) {
      alert(err.response.data.message);
    } else if (err.response?.status === 409) {
      alert("Duplicate entry already exists");
    } else {
      alert("Something went wrong");
    }
  } finally {
    setLoading(false);
  }
};

  const renderAuditDetails = () => {
  if (!isEdit) return null;

  return (
    <div className="px-6 pt-5">
      <div className="grid grid-cols-2 gap-6 border rounded-xl bg-gray-50 p-3">
        <div>
          <h4 className="text-[10px] font-semibold uppercase tracking-wider text-gray-500 mb-2">
            Created Details
          </h4>

          <div className="space-y-1 text-[12px] text-gray-700">
            <p>
              <span className="font-semibold">Created By :</span>{" "}
              {formData.createdBy || "-"}
            </p>

            <p>
              <span className="font-semibold">Created On :</span>{" "}
              {formatDateTime(formData.createdOn)}
            </p>
          </div>
        </div>

        <div>
          <h4 className="text-[10px] font-semibold uppercase tracking-wider text-gray-500 mb-2 text-right">
            Modified Details
          </h4>

          <div className="space-y-1 text-[12px] text-gray-700 text-right">
            <p>
              <span className="font-semibold">Modified By :</span>{" "}
              {formData.modifiedBy || "-"}
            </p>

            <p>
              <span className="font-semibold">Modified On :</span>{" "}
              {formatDateTime(formData.modifiedOn)}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
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
        {renderAuditDetails()}

        <div className="px-6 py-6 flex flex-col gap-5">
          {formFields.map((field) => (
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
        {renderAuditDetails()}

        <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
          {formFields.map((field) => (
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