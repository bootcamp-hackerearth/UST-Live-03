"use client";

import PropTypes from "prop-types";
import { useEffect, useState } from "react";

export default function CommonForm({
  title = "Item",
  mode,
  fields,
  data,
  onSubmit,
  onClose,
  validate,
}) {
  const [formData, setFormData] = useState({});
  const [options, setOptions] = useState({});
  const [errors, setErrors] = useState({});

  const isEdit = mode === "edit";

  useEffect(() => {
    if (!fields) return;

    const init = {};

    if (data?.id != null) {
      init.id = data.id;
    }

    fields.forEach((f) => {
      if (f.hidden) return;

      if (f.type === "multiselect") {
        init[f.name] = Array.isArray(data?.[f.name])
          ? data[f.name].map((x) =>
            typeof x === "object" ? x.identifier : x
          )
          : [];
      } else if (f.type === "select") {
        init[f.name] =
          typeof data?.[f.name] === "object"
            ? data[f.name]?.identifier
            : data?.[f.name] ?? "";
      } else {
        init[f.name] = data?.[f.name] ?? "";
      }
    });

    setFormData(init);
  }, [data, fields]);

  useEffect(() => {
    const loadOptions = async () => {
      for (const field of fields || []) {
        if (field.type === "select" || field.type === "multiselect") {

          if (field.options) continue;

          try {
            const res = await fetch(field.apiUrl, {
              method: "POST",
              headers: {
                "Content-Type": "application/json",
              },
              credentials: "include",
              body: JSON.stringify({ page: 0, sizePerPage: 100 }),
            });

            if (!res.ok) {
              throw new Error(`Server returned HTTP status ${res.status} for URL: "${field.apiUrl}"`);
            }

            const contentType = res.headers.get("content-type");
            if (!contentType?.includes("application/json")) {

              throw new Error(
                `Expected JSON from "${field.apiUrl}", but received content-type "${contentType}". The API is likely returning an HTML error page or routing fallback.`
              );
            }

            const result = await res.json();

            const list =
              result.content ||
              result.dtoList ||
              result.data ||
              result ||
              [];

            setOptions((prev) => ({
              ...prev,
              [field.name]: Array.isArray(list) ? list : [],
            }));
          } catch (error) {
            console.error(`[CommonForm] Error loading select options for field "${field.name}":`, error.message);
          }
        }
      }
    };

    loadOptions();
  }, [fields]);

  const handleChange = (e) => {
    const { name, value, multiple, options: htmlOptions } = e.target;

    if (multiple) {
      const selected = Array.from(htmlOptions)
        .filter((o) => o.selected)
        .map((o) => o.value);

      setFormData((prev) => ({ ...prev, [name]: selected }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }

    setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const submit = async (e) => {
    e.preventDefault();
    if (validate) {
      const err = validate(formData);
      setErrors(err || {});
      if (err && Object.keys(err).length > 0) return;
    }

    await onSubmit(formData);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60">
      <div className="w-105 bg-white rounded-xl relative">

        <div className="bg-black px-5 py-3 text-white text-center">
          {mode === "add" ? `Add ${title}` : `Edit ${title}`}
        </div>

        <form onSubmit={submit} className="p-4 space-y-3">

          {fields.map((f) => {
            if (f.hidden) return null;

            const disabled = isEdit && (f.disableOnEdit || f.name === "identifier");
            const inputClass = "w-full h-9 px-3 text-xs border rounded bg-gray-100";

            if (f.type === "select") {
              const selectOptions = f.options || options[f.name] || [];
              const fieldId = `common-form-${f.name}`;

              return (
                <div key={f.name}>
                  <label htmlFor={fieldId} className="text-xs font-semibold">
                    {f.placeholder}
                  </label>

                  <select
                    id={fieldId}
                    name={f.name}
                    value={formData[f.name] || ""}
                    onChange={handleChange}
                    disabled={disabled}
                    className={
                      inputClass +
                      (disabled ? " bg-gray-200 cursor-not-allowed" : "")
                    }
                  >
                    <option value="">Select</option>

                    {selectOptions.map((opt) => {
                      const optionValue = opt.value ?? opt.identifier ?? opt;
                      return (
                        <option key={optionValue} value={optionValue}>
                          {opt.label ?? opt.identifier ?? opt}
                        </option>
                      );
                    })}
                  </select>

                  {errors[f.name] && (
                    <p className="text-red-500 text-[10px]">
                      {errors[f.name]}
                    </p>
                  )}
                </div>
              );
            }

            if (f.type === "multiselect") {
              const fieldId = `common-form-${f.name}`;

              return (
                <div key={f.name}>
                  <label htmlFor={fieldId} className="text-xs font-semibold">
                    {f.placeholder}
                  </label>

                  <select
                    id={fieldId}
                    multiple
                    name={f.name}
                    value={formData[f.name] || []}
                    onChange={handleChange}
                    disabled={disabled}
                    className={
                      inputClass +
                      " h-24" +
                      (disabled ? " bg-gray-200 cursor-not-allowed" : "")
                    }
                  >
                    {(options[f.name] || []).map((opt) => {
                      const optionValue = opt.identifier ?? opt;
                      return (
                        <option key={optionValue} value={optionValue}>
                          {opt.identifier ?? opt}
                        </option>
                      );
                    })}
                  </select>

                  {errors[f.name] && (
                    <p className="text-red-500 text-[10px]">
                      {errors[f.name]}
                    </p>
                  )}
                </div>
              );
            }

            return (
              <div key={f.name}>
                <label htmlFor={`common-form-${f.name}`} className="text-xs font-semibold">
                  {f.placeholder}
                </label>

                <input
                  id={`common-form-${f.name}`}
                  type="text"
                  name={f.name}
                  value={formData[f.name] || ""}
                  onChange={handleChange}
                  disabled={disabled}
                  className={
                    inputClass +
                    (disabled ? " bg-gray-200 cursor-not-allowed" : "")
                  }
                />

                {errors[f.name] && (
                  <p className="text-red-500 text-[10px]">
                    {errors[f.name]}
                  </p>
                )}
              </div>
            );
          })}

          <div className="flex justify-end gap-2 pt-2">
            <button type="button" onClick={onClose} className="text-xs px-3 py-1 border rounded">
              Cancel
            </button>

            <button type="submit" className="bg-black text-white text-xs px-3 py-1 rounded">
              {mode === "add" ? "Save" : "Update"}
            </button>
          </div>

        </form>
      </div>
    </div>
  );
}

CommonForm.propTypes = {
  title: PropTypes.string,
  mode: PropTypes.string.isRequired,
  fields: PropTypes.arrayOf(
    PropTypes.shape({
      name: PropTypes.string.isRequired,
      placeholder: PropTypes.string,
      type: PropTypes.string,
      hidden: PropTypes.bool,
      disableOnEdit: PropTypes.bool,
      options: PropTypes.array,
      apiUrl: PropTypes.string,
    })
  ).isRequired,
  data: PropTypes.object,
  onSubmit: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
  validate: PropTypes.func,
};