"use client";

import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";

import InputField from "./InputField";
import SingleSelect from "./SingleSelect";
import MultiSelect from "./MultiSelect";
import axiosInstance from "../../services/axiosInstance";

function SectionForm({
  title,
  submitUrl,
  onSuccess,
  sections = [],
  initialValues = {},
  backUrl = "/",
  existingData = [],
  uniqueFields = [],
  buildCustomPayload = null,
}) {
  const router = useRouter();

  const [formData, setFormData] = useState({});
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const allFields = sections.flatMap((section) => section.fields || []);

  const buildInitialState = () => {
    const state = { ...initialValues };

    sections.forEach((section) => {
      (section.fields || []).forEach((field) => {
        if (!(field.key in state)) {
          state[field.key] =
            field.type === "multiselect" ? [] : field.defaultValue ?? "";
        }
      });
    });

    return state;
  };

  useEffect(() => {
    setFormData(buildInitialState());
  }, [initialValues, sections]);

  const validateRequired = (field, value) => {
    if (
      field.required &&
      (!value || (Array.isArray(value) && value.length === 0))
    ) {
      return `${field.label} is required`;
    }

    return "";
  };

  const validatePhone = (value) => {
    const phone = String(value).replaceAll(/\s/g, "");

    if (!/^\d+$/.test(phone)) {
      return "Phone number must contain only digits";
    }

    if (phone.length !== 10) {
      return "Phone number must be 10 digits";
    }

    return "";
  };

  const validateEmail = (value) => {
  if (!value || value.length > 254) {
    return "Please enter a valid email address";
  }

  const atIndex = value.indexOf("@");
  const dotIndex = value.lastIndexOf(".");

  const isValid =
    atIndex > 0 &&
    dotIndex > atIndex + 1 &&
    dotIndex < value.length - 1 &&
    !value.includes(" ") &&
    !value.includes("\t") &&
    !value.includes("\n");

  return isValid ? "" : "Please enter a valid email address";
};

  const validatePassword = (value) => {
    if (value.length < 6) {
      return "Password must be at least 6 characters";
    }

    if (!/[A-Z]/.test(value)) {
      return "Password must contain at least one uppercase letter";
    }

    if (!/[!@#$%^&*(),.?":{}|<>]/.test(value)) {
      return "Password must contain at least one special character";
    }

    return "";
  };

  const validateUnique = (field, value) => {
    const isDuplicate = existingData.some(
      (item) =>
        String(item[field.key]).toLowerCase() ===
          String(value).toLowerCase() &&
        item.identifier !== initialValues.identifier
    );

    return isDuplicate ? `${field.label} already exists` : "";
  };

  const applyValidation = (validator, value) => {
    return value ? validator(value) : "";
  };

  const validateField = (field, value) => {
    let error = validateRequired(field, value);
    if (error) return error;

    if (field.isPhone) {
      error = applyValidation(validatePhone, value);
      if (error) return error;
    }

    if (field.type === "email") {
      error = applyValidation(validateEmail, value);
      if (error) return error;
    }

    if (field.type === "password") {
      error = applyValidation(validatePassword, value);
      if (error) return error;
    }

    if (uniqueFields.includes(field.key) && value) {
      error = validateUnique(field, value);
      if (error) return error;
    }

    return "";
  };

  const handleChange = (key, value) => {
    setFormData((prev) => ({
      ...prev,
      [key]: value,
    }));

    const field = allFields.find((item) => item.key === key);
    if (!field) return;

    const error = validateField(field, value);

    setErrors((prev) => ({
      ...prev,
      [key]: error,
    }));
  };

  const validate = () => {
    const newErrors = {};

    allFields.forEach((field) => {
      const value = formData[field.key];
      const error = validateField(field, value);

      if (error) {
        newErrors[field.key] = error;
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
    const payload = {
      id: initialValues.id,
      identifier: initialValues.identifier,
    };

    allFields.forEach((field) => {
      if (field.disabled) return;

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

    return payload;
  };

  const handleSubmit = async () => {
    if (!validate()) return;

    try {
      setLoading(true);

      const payload = buildCustomPayload
        ? buildCustomPayload(formData)
        : buildPayload();

      console.log("PAYLOAD:", JSON.stringify(payload, null, 2));

      const response = await axiosInstance.post(submitUrl, payload);

      alert("Saved Successfully");

      if (onSuccess) {
        onSuccess(response.data);
      } else {
        router.push(backUrl);
      }
    } catch (err) {
      console.log(err);

      if (err.response?.status === 409) {
        uniqueFields.forEach((key) => {
          setErrors((prev) => ({
            ...prev,
            [key]: "This value already exists",
          }));
        });

        alert("Duplicate entry — this record already exists.");
      } else if (err.response?.status === 403) {
        alert("Access denied — check role permission for this API.");
      } else {
        alert("Something went wrong");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCopyFrom = (targetSection) => {
    const sourceSection = sections.find(
      (section) => section.key === targetSection.copyFrom
    );

    if (!sourceSection) return;

    const updates = {};
    const errorUpdates = {};

    sourceSection.fields.forEach((sourceField) => {
      const sourcePrefix = sourceSection.key;
      const targetPrefix = targetSection.key;

      if (!sourceField.key.startsWith(sourcePrefix)) return;

      const targetKey =
        targetPrefix + sourceField.key.slice(sourcePrefix.length);

      const targetField = targetSection.fields.find(
        (field) => field.key === targetKey
      );

      if (!targetField || targetField.disabled) return;

      updates[targetKey] = formData[sourceField.key];
      errorUpdates[targetKey] = "";
    });

    setFormData((prev) => ({
      ...prev,
      ...updates,
    }));

    setErrors((prev) => ({
      ...prev,
      ...errorUpdates,
    }));
  };

  const renderField = (field, sectionIndex, fieldIndex) => {
    const fieldKey = `section-${sectionIndex}-field-${fieldIndex}-${
      field.key || "field"
    }`;

    return (
      <div
        key={fieldKey}
        className={`flex flex-col gap-1 ${
          field.fullWidth ? "md:col-span-2" : ""
        }`}
      >
        {(field.type === "text" ||
          field.type === "number" ||
          field.type === "email" ||
          field.type === "password") && (
          <InputField
            label={field.label}
            type={field.type}
            value={formData[field.key] ?? field.defaultValue ?? ""}
            placeholder={field.placeholder || ""}
            required={field.required || false}
            disabled={field.disabled || false}
            error={errors[field.key] || ""}
            onChange={(value) => handleChange(field.key, value)}
          />
        )}

        {field.type === "select" && (
          <React.Fragment key={`${fieldKey}-select`}>
            <SingleSelect
              label={field.label}
              value={formData[field.key]}
              onChange={(value) => handleChange(field.key, value)}
              options={field.options || []}
              optionLabel={field.optionLabel || "identifier"}
              optionValue={field.optionValue || "identifier"}
              required={field.required || false}
            />

            {errors[field.key] && (
              <span className="text-red-500 text-xs mt-1">
                {errors[field.key]}
              </span>
            )}
          </React.Fragment>
        )}

        {field.type === "multiselect" && (
          <React.Fragment key={`${fieldKey}-multiselect`}>
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
              optionLabel={field.optionLabel || "identifier"}
              optionValue={field.optionValue || "identifier"}
            />

            {errors[field.key] && (
              <span className="text-red-500 text-xs mt-1">
                {errors[field.key]}
              </span>
            )}
          </React.Fragment>
        )}
      </div>
    );
  };

  return (
    <div className="flex flex-col gap-5">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-gray-800">{title}</h1>

        <div className="flex items-center gap-3">
          <button
            type="button"
            onClick={() => router.push(backUrl)}
            className="px-5 py-2 rounded-lg text-sm font-medium border border-gray-300 text-gray-600 hover:bg-gray-50 transition-all"
          >
            Cancel
          </button>

          <button
            type="button"
            onClick={handleSubmit}
            disabled={loading}
            className="px-5 py-2 rounded-lg text-sm font-medium bg-red-600 hover:bg-red-700 text-white transition-all disabled:opacity-50"
          >
            {loading ? "Saving..." : "Save & Publish"}
          </button>
        </div>
      </div>

      {sections.map((section, sectionIndex) => {
        let gridColsClass = "grid-cols-1 md:grid-cols-2";

        if (section.columns === 1) {
          gridColsClass = "grid-cols-1";
        }

        if (section.columns === 3) {
          gridColsClass = "grid-cols-1 md:grid-cols-3";
        }

        return (
          <div
            key={`section-${sectionIndex}-${section.key || "section"}`}
            className="bg-white rounded-xl shadow-sm"
          >
            {section.title && (
              <div className="px-6 py-4 border-b flex items-center justify-between">
                <div>
                  <h2 className="text-base font-semibold text-gray-800">
                    {section.title}
                  </h2>

                  {section.subtitle && (
                    <p className="text-sm text-gray-500 mt-0.5">
                      {section.subtitle}
                    </p>
                  )}
                </div>

                {section.copyFrom && (
                  <button
                    type="button"
                    onClick={() => handleCopyFrom(section)}
                    className="text-xs text-red-600 border border-red-300 px-3 py-1.5 rounded-lg hover:bg-red-50 transition-all"
                  >
                    Same as{" "}
                    {sections.find((item) => item.key === section.copyFrom)
                      ?.title || "Billing"}
                  </button>
                )}
              </div>
            )}

            <div className={`p-6 grid gap-6 ${gridColsClass}`}>
              {(section.fields || []).map((field, fieldIndex) =>
                renderField(field, sectionIndex, fieldIndex)
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
}

SectionForm.propTypes = {
  title: PropTypes.string.isRequired,
  submitUrl: PropTypes.string,
  onSuccess: PropTypes.func,
  sections: PropTypes.array,
  initialValues: PropTypes.object,
  backUrl: PropTypes.string,
  existingData: PropTypes.array,
  uniqueFields: PropTypes.array,
  buildCustomPayload: PropTypes.func,
};

export default SectionForm;