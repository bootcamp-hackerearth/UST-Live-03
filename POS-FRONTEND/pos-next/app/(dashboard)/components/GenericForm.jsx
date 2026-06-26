"use client";

import PropTypes from "prop-types";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import EntityAuditHistory from "./EntityAuditHistory";

const getNestedValue = (obj, path) => {
  if (!path) return undefined;
  return path.split('.').reduce((acc, part) => acc?.[part], obj);
};

const setNestedValue = (obj, path, value) => {
  const parts = path.split('.');
  const newObj = { ...obj };
  let current = newObj;

  for (let i = 0; i < parts.length - 1; i++) {
    const part = parts[i];
    current[part] = current[part] ? { ...current[part] } : {};
    current = current[part];
  }

  current[parts[parts.length - 1]] = value;
  return newObj;
};

export default function GenericForm({
  entity,
  config,
  mode = "add",
  initialData = {},
  onSuccess,
  onCancel,
}) {

  const router = useRouter();
  const [errorMessage, setErrorMessage] = useState("");
  
  const [formData, setFormData] = useState(() => { let state = mode === "edit" ? { ...initialData } : {};

    config.formFields.forEach((field) => {
      if (mode === "edit") {
        const existingValue = getNestedValue(initialData, field.name);
        if (existingValue !== undefined) {
          state = setNestedValue(state, field.name, existingValue);
        }
      } else {
        const fallbackValue = field.type === "multiselect" ? [] : field.defaultValue ?? "";
        state = setNestedValue(state, field.name, fallbackValue);
      }
    });
    return state;
  });

  const [loading, setLoading] = useState(false);
  const [dropdownOptions, setDropdownOptions,] = useState({});

  useEffect(() => {
    if (mode === "edit") {
      let updatedState = { ...initialData };
      config.formFields.forEach((field) => {
        const value = getNestedValue(initialData, field.name);
        if (value !== undefined) {
          updatedState = setNestedValue(updatedState, field.name, value);
        }
      });
      setFormData(updatedState);
    }
  }, [initialData, mode, config]);

  useEffect(() => {
    const loadDropdowns = async () => {
      const selectFields = config.formFields.filter((field) => (field.type === "select" || field.type === "multiselect") && field.optionsEndpoint);

      if (selectFields.length === 0) {
        return;
      }
      const results = {};

      for (const field of selectFields) {
        try {
          const response = await axios.post("/api/dropdown-options", {
            endpoint: field.optionsEndpoint,
            method: field.optionsMethod || "GET",
            payload: field.optionsPayload || {},
          });
          results[field.name] = response.data.options || [];
        } catch (error) {
          console.error(`Failed loading ${field.name}`, error);
          results[field.name] = [];
        }
      }

      setDropdownOptions((prev) => ({ ...prev, ...results }));
    };

    loadDropdowns();
  }, [config]);

  const handleChange = (fieldName, value) => { setFormData((prev) => setNestedValue(prev, fieldName, value)); };

  const isEmptyFieldValue = (value) => (
    value === "" ||
    value === null ||
    value === undefined ||
    (Array.isArray(value) && value.length === 0)
  );

  const validateFieldValue = (field, value) => {
    const MAX_INPUT_LENGTH = 254;

    if (value && value.length > MAX_INPUT_LENGTH) {
      alert("Input too long");
      return false;
    }

    if (field.type === "email" && value) {
      const emailRegex = /^[^\s@]{1,64}@[^\s@]{1,64}\.[a-zA-Z]{2,10}$/;
      if (!emailRegex.test(value)) {
        alert("Invalid email address");
        return false;
      }
    }

    if (field.type === "password" && value) {
      const hasMinLength = value.length >= 6;
      const hasLower = /[a-z]/.test(value);
      const hasUpper = /[A-Z]/.test(value);
      const hasDigit = /\d/.test(value);
      const hasSpecial = /[@$!%*?&]/.test(value);

      if (!(hasMinLength && hasLower && hasUpper && hasDigit && hasSpecial)) {
        alert("Password must be at least 6 characters and contain uppercase, lowercase, number and special character");
        return false;
      }
    }

    if (field.type === "tel" && value) {
      const phoneRegex = /^\d{10}$/;
      if (!phoneRegex.test(value)) {
        alert("Phone number must contain exactly 10 digits");
        return false;
      }
    }

    return true;
  };

  const validateForm = () => {
    for (const field of config.formFields) {
      const value = getNestedValue(formData, field.name);

      if (field.required && isEmptyFieldValue(value)) {
        alert(`${field.label} is required`);
        return false;
      }

      if (field.type === "number" && value !== "" && value !== null && value !== undefined) {
        if (Number.isNaN(Number.parseFloat(value))) {
          setErrorMessage(`${field.label} must be a valid number`);
          return false;
        }
      }

      if (!validateFieldValue(field, value)) {
        return false;
      }
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage("");

    if (!validateForm()) {
      return;
    }

    try {
      setLoading(true);
      const response = await axios.post("/api/add-entity",
        {
          endpoint: mode === "edit" ? config.updateEndpoint : config.addEndpoint,
          method: mode === "edit" ? "PUT" : "POST",
          payload: formData,
        });

      if (!response.data.success) {
        setErrorMessage(response.data.message);
        return;
      }

      if (mode === "edit") {
        onSuccess?.();
      } else {
        router.push(`/${entity}`);
        router.refresh();
      }

    } catch (error) {
      alert(error?.response?.data?.message || "Failed to save record");
    } finally {
      setLoading(false);
    }
  };

  let submitButtonLabel;
  if (loading) {
    submitButtonLabel = "Saving...";
  } else if (mode === "edit") {
    submitButtonLabel = "Update";
  } else {
    submitButtonLabel = `Save ${config.displayName || entity}`;
  }

  return (
    <div className="flex justify-center py-10">
      <form onSubmit={handleSubmit} className="w-full max-w-3xl">
        <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-xl">

          <div className="border-b border-slate-100 px-8 py-6">
            <h2 className="text-xl font-semibold capitalize text-slate-900">
              {config.displayName || entity}
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              {mode === "edit"
                ? `Edit record`
                : `Create a new ${entity} record`}
            </p>
          </div>

          {errorMessage && (
            <div className="mx-6 mt-2 rounded-lg border border-red-300 bg-red-100 px-4 py-3 text-red-700">
              {errorMessage}
            </div>
          )}

          <div className="space-y-5 p-8">
            <EntityAuditHistory activeItem={initialData} />
            {config.formFields
              .filter((field) => !(mode === "edit" && field.hideInEdit))
              .map((field) => {
                
                let inputPlaceholder = "";
                if (field.type === "number") {
                  inputPlaceholder = "Enter amount";
                } else if (field.type === "tel") {
                  inputPlaceholder = "Enter Phone No.";
                }

                let inputTitle = "";
                if (field.type === "password") {
                  inputTitle = "Password must contain at least 6 characters, one uppercase letter, one lowercase letter, one number and one special character";
                } else if (field.type === "tel") {
                  inputTitle = "Phone number must contain exactly 10 numeric digits";
                }

                const currentFieldValue = getNestedValue(formData, field.name);

                return (
                  <div key={field.name} className="space-y-1.5">

                    <label className="text-sm font-medium text-slate-700">
                      {field.label}
                      {field.required && <span className="ml-1 text-red-500">*</span>}
                    </label>

                    <input type="hidden" name="id" value={formData.id ?? ""} />

                    {["text", "email", "tel", "password", "number"].includes(field.type) && (
                      <input
                        disabled={mode === "edit" && field.editable === false}
                        type={field.type === "tel" ? "text" : field.type}
                        value={currentFieldValue ?? ""}

                        onChange={(e) => { let value = e.target.value;

                          if (field.type === "tel") {
                            value = value.replaceAll(/\D/g, "");
                            if (value.length > 10) {
                              value = value.slice(0, 10);
                            }
                          }

                          let parsedValue = value;
                          if (field.type === "number") {
                            parsedValue = value === "" ? "" : Number.parseFloat(value);
                          }

                          handleChange(field.name, parsedValue);
                        }}

                        maxLength={field.type === "tel" ? 10 : undefined}
                        minLength={field.type === "password" ? 6 : undefined}
                        step={field.step || (field.type === "number" ? "0.01" : undefined)}
                        min={field.min ?? (field.type === "number" ? "0" : undefined)}
                        placeholder={inputPlaceholder}
                        title={inputTitle}
                        className="w-full rounded-lg border border-slate-300 bg-slate-50 px-4 py-2.5
                        text-slate-900 transition-all duration-200 placeholder:text-slate-400
                        hover:border-indigo-300 focus:bg-white focus:border-indigo-500
                        focus:ring-4 focus:ring-indigo-100 outline-none"
                      />
                    )}

                    {field.type === "textarea" && (
                      <textarea
                        rows={4}
                        value={currentFieldValue ?? ""}
                        onChange={(e) => handleChange(field.name, e.target.value)}
                        disabled={mode === "edit" && field.editable === false}
                        className="w-full rounded-lg border border-slate-300 bg-slate-50 px-4 py-2.5
                      text-slate-900 transition-all duration-200 hover:border-indigo-300
                      focus:bg-white focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100
                      outline-none resize-y"
                      />
                    )}

                    {field.type === "select" && (
                      <select
                        value={currentFieldValue ?? ""}
                        onChange={(e) => handleChange(field.name, e.target.value)}
                        disabled={mode === "edit" && field.editable === false}
                        className="w-full rounded-lg border border-slate-300 bg-slate-50 px-4 py-2.5
                      text-slate-900 transition-all duration-200 hover:border-indigo-300
                      focus:bg-white focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100
                      outline-none cursor-pointer"
                      >
                        <option value="">Select {field.label}</option>
                        {(field.options || dropdownOptions[field.name] || []).map((option) => (
                          <option
                            key={option.value || option.id}
                            value={option.value || option.identifier}
                          >
                            {option.label || option.identifier}
                          </option>
                        ))}
                      </select>
                    )}

                    {field.type === "multiselect" && (
                      <div>
                        <select
                          multiple
                          value={currentFieldValue || []}
                          onChange={(e) => {
                            const values = Array.from(e.target.selectedOptions, (option) => option.value);
                            handleChange(field.name, values);
                          }}
                          disabled={mode === "edit" && field.editable === false}
                          className="w-full h-40 rounded-lg border border-slate-300 bg-slate-50 px-4 py-2.5 text-slate-900 transition-all duration-200 hover:border-indigo-300
                        focus:bg-white focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100 outline-none">
                          {(field.options || dropdownOptions[field.name] || []).map((option) => (
                            <option
                              key={option.id}
                              value={option.identifier}
                            >
                              {option.label || option.identifier}
                            </option>
                          ))}
                        </select>

                        <p className="mt-2 text-xs text-slate-500">
                          Hold Ctrl(Windows) or Cmd(Mac) to select multiple options.
                        </p>
                      </div>
                    )}

                    {field.type === "switch" && (
                      <select
                        value={currentFieldValue ?? 1}
                        onChange={(e) => handleChange(field.name, Number(e.target.value))}
                        disabled={mode === "edit" && field.editable === false}
                        className="w-full rounded-lg border border-slate-300 bg-slate-50 px-4 py-2.5 text-slate-900 transition-all duration-200 hover:border-indigo-300
                      focus:bg-white focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100 outline-none cursor-pointer">
                        <option value={1}>Active</option>
                        <option value={0}>Inactive</option>
                      </select>
                    )}
                  </div>
                );
              })}

          </div>

          <div className="flex justify-end gap-3 text-black border-t border-slate-100 bg-slate-50 px-8 py-5">
            <button
              type="button"
              onClick={() => {
                if (mode === "edit" && onCancel) {
                  onCancel();
                } else {
                  router.push(`/${entity}`);
                }
              }}
              className="px-4 py-2 border border-gray-300 rounded-md text-black hover:border-gray-500"
            >
              Cancel
            </button>

            <button
              type="submit"
              disabled={loading}
              className="rounded-lg bg-indigo-600 px-5 py-2.5 text-sm font-medium text-white transition-all hover:bg-indigo-700 hover:shadow-md disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {submitButtonLabel}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}

GenericForm.propTypes = {
  entity: PropTypes.string.isRequired,
  config: PropTypes.shape({
    displayName: PropTypes.string,
    addEndpoint: PropTypes.string,
    updateEndpoint: PropTypes.string,
    formFields: PropTypes.arrayOf(
      PropTypes.shape({
        name: PropTypes.string.isRequired,
        label: PropTypes.string,
        type: PropTypes.string,
        required: PropTypes.bool,
        defaultValue: PropTypes.any,
        optionsEndpoint: PropTypes.string,
        optionsMethod: PropTypes.string,
        optionsPayload: PropTypes.object,
        hideInEdit: PropTypes.bool,
        editable: PropTypes.bool,
        step: PropTypes.string,
        min: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
      })
    ).isRequired,
  }).isRequired,
  mode: PropTypes.oneOf(["add", "edit"]),
  initialData: PropTypes.object,
  onSuccess: PropTypes.func,
  onCancel: PropTypes.func,
};