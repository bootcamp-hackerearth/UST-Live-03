"use client";
import PropTypes from "prop-types";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

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
  const [formData, setFormData] = useState(() => {
    const state = mode === "edit"
      ? { ...initialData }
      : {};

    config.formFields.forEach((field) => {
      if (mode === "edit") {
        state[field.name] = initialData[field.name];
      } else {
        state[field.name] = field.type === "multiselect"
          ? []
          : field.defaultValue ?? "";
      }
    });
    return state;
  });

  const [loading, setLoading] = useState(false);
  const [dropdownOptions, setDropdownOptions,] = useState({});

  useEffect(() => {
    if (mode === "edit") {
      const updatedState = { ...initialData, };
      config.formFields.forEach((field) => { updatedState[field.name] = initialData[field.name]; });
      setFormData(updatedState);
    }
  }, [initialData, mode, config]);

  useEffect(() => {
    const loadDropdowns = async () => {
      const selectFields = config.formFields.filter((field) => field.type === "select" || field.type === "multiselect");
      if (selectFields.length === 0) {
        return;
      }
      const results = {};

      for (const field of selectFields) {
        try {
          const response = await axios.post("/api/dropdown-options",
            {
              endpoint: field.optionsEndpoint,
              method: field.optionsMethod || "GET",
              payload: field.optionsPayload || {},
            }
          );
          results[field.name] = response.data.options || [];
        }
        catch (error) {
          console.error(`Failed loading ${field.name}`, error);
          results[field.name] = [];
        }
      }
      setDropdownOptions(results);
    };

    loadDropdowns();
  }, [config]);

  console.log("Dropdown Options:", dropdownOptions);

  const handleChange = (fieldName, value) => { setFormData((prev) => ({ ...prev, [fieldName]: value, })); };

  const isEmptyFieldValue = (value) => (
    value === "" ||
    value === null ||
    value === undefined ||
    (Array.isArray(value) && value.length === 0)
  );

  const validateFieldValue = (field, value) => {
    if (!value) return true;

    const MAX_INPUT_LENGTH = 254;
    if (value.length > MAX_INPUT_LENGTH) {
      alert("Input too long");
      return false;
    }

    if (field.type === "email") {
      const emailRegex = /^[^\s@]{1,64}@[^\s@]{1,255}$/;
      if (!emailRegex.test(value)) {
        alert("Invalid email address");
        return false;
      }
    }

    if (field.type === "password") {
      const hasMinLength = value.length >= 6;
      const hasLower = /[a-z]/.test(value);
      const hasUpper = /[A-Z]/.test(value);
      const hasDigit = /\d/.test(value);
      const hasSpecial = /[@$!%*?&]/.test(value);

      if (!(hasMinLength && hasLower && hasUpper && hasDigit && hasSpecial)) {
        alert("Password must be at least 6 characters and contain an uppercase letter, lowercase letter, number and special character");
        return false;
      }
    }

    if (field.type === "tel") {
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
      const value = formData[field.name];

      if (field.required && isEmptyFieldValue(value)) {
        alert(`${field.label} is required`);
        return false;
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
          endpoint: mode === "edit"
            ? config.updateEndpoint
            : config.addEndpoint,
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
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-3xl"
      >
        <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-xl">

          <div className="border-b border-slate-100 px-8 py-6">
            <h2 className="text-xl font-semibold capitalize text-slate-900">
              {config.displayName || entity}
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              {mode === "edit"
                ? `Edit ${entity} record`
                : `Create a new ${entity} record`}
            </p>
          </div>

          {errorMessage && (
            <div className="mx-6 mt-2 rounded-lg border border-red-300 bg-red-100 px-4 py-3 text-red-700">
              {errorMessage}
            </div>
          )}

          <div className="space-y-5 p-8">
            {config.formFields.filter((field) =>
              !(mode === "edit" && field.hideInEdit)
            ).map(
              (field) => (
                <div
                  key={field.name}
                  className="space-y-1.5"
                >
                  <label className="text-sm font-medium text-slate-700">
                    {field.label}

                    {field.required && (
                      <span className="ml-1 text-red-500">
                        *
                      </span>
                    )}
                  </label>

                  <input type="hidden" name="id" value="id" />
                  {["text", "email", "tel", "password",].includes(field.type) && (
                    <input
                      disabled={mode === "edit" && field.editable === false}
                      type={field.type}
                      value={formData[field.name] ?? ""}
                      onChange={(e) =>
                        handleChange(field.name, e.target.value)}
                      minLength={
                        field.type === "password"
                          ? 6
                          : undefined}

                      title={field.type === "password"
                        ? "Password must contain at least 6 characters, one uppercase letter, one lowercase letter, one number and one special character"
                        : undefined}
                      className="w-full rounded-lg border border-slate-300 bg-slate-50 px-4 py-2.5
                                 text-slate-900 transition-all duration-200 placeholder:text-slate-400
                                 hover:border-indigo-300 focus:bg-white focus:border-indigo-500
                                 focus:ring-4 focus:ring-indigo-100 outline-none"
                    />
                  )}

                  {field.type === "textarea" && (
                    <textarea
                      rows={4}
                      value={formData[field.name] ?? ""}
                      onChange={(e) =>
                        handleChange(field.name, e.target.value)}
                      disabled={mode === "edit" && field.editable === false}
                      className="w-full rounded-lg border border-slate-300 bg-slate-50 px-4 py-2.5
                                  text-slate-900 transition-all duration-200 hover:border-indigo-300
                                  focus:bg-white focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100
                                  outline-none resize-y"
                    />
                  )}

                  {field.type === "select" && (
                    <select
                      value={formData[field.name] ?? ""}
                      onChange={(e) =>
                        handleChange(field.name, e.target.value)}
                      disabled={mode === "edit" && field.editable === false}
                      className="w-full rounded-lg border border-slate-300 bg-slate-50 px-4 py-2.5
                                  text-slate-900 transition-all duration-200 hover:border-indigo-300
                                  focus:bg-white focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100
                                  outline-none cursor-pointer"
                    >
                      <option value="">
                        Select{" "}
                        {field.label}
                      </option>

                      {dropdownOptions[field.name]?.map(
                        (option) => (
                          <option
                            key={option.id}
                            value={option.identifier}
                          >
                            {option.identifier}
                          </option>)
                      )}
                    </select>
                  )}

                  {field.type === "multiselect" && (
                    <div>
                      <select
                        multiple
                        value={formData[field.name] || []}
                        onChange={(e) => {
                          const values = Array.from(
                            e.target.selectedOptions,
                            (option) => option.value
                          );

                          handleChange(
                            field.name, values);
                        }}
                        disabled={mode === "edit" && field.editable === false}
                        className="w-full h-40 rounded-lg border border-slate-300 bg-slate-50
                                   px-4 py-2.5 text-slate-900 transition-all duration-200 hover:border-indigo-300
                                   focus:bg-white focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100
                                   outline-none"
                      >
                        {(dropdownOptions[field.name] || []).map((option) => (
                          <option
                            key={option.id}
                            value={option.identifier}
                          >
                            {option.identifier}
                          </option>
                        )
                        )}
                      </select>

                      <p className="mt-2 text-xs text-slate-500">
                        Hold Ctrl (Windows) or Cmd (Mac) to select multiple options.
                      </p>
                    </div>
                  )}

                  {field.type ===
                    "switch" && (
                      <select
                        value={formData[field.name]}
                        onChange={(e) =>
                          handleChange(field.name,
                            Number(e.target.value))}
                        disabled={mode === "edit" && field.editable === false}
                        className="w-full rounded-lg border border-slate-300 bg-slate-50 px-4 py-2.5
                                   text-slate-900 transition-all duration-200 hover:border-indigo-300
                                   focus:bg-white focus:border-indigo-500 focus:ring-4 focus:ring-indigo-100
                                   outline-none cursor-pointer"
                      >
                        <option value={1}>
                          Active
                        </option>
                        <option value={0}>
                          Inactive
                        </option>
                      </select>
                    )}
                </div>
              )
            )}
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
      })
    ).isRequired,
  }).isRequired,
  mode: PropTypes.oneOf(["add", "edit"]),
  initialData: PropTypes.object,
  onSuccess: PropTypes.func,
  onCancel: PropTypes.func,
};