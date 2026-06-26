"use client";

import PropTypes from "prop-types";
import { useEffect, useState } from "react";

async function fetchFieldOptions(field) {
  const res = await fetch(field.apiUrl, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ page: 0, sizePerPage: 100 }),
  });

  if (!res.ok) {
    throw new Error(`Server returned HTTP status ${res.status} for URL: "${field.apiUrl}"`);
  }

  const contentType = res.headers.get("content-type");
  if (!contentType?.includes("application/json")) {
    throw new Error(
      `Expected JSON from "${field.apiUrl}", but received content-type "${contentType}".`
    );
  }

  const result = await res.json();

  const list = result.content || result.dtoList || result.data || result || [];

  return Array.isArray(list) ? list.filter((item) => item.status === true) : [];
}

function SelectField({ f, formData, options, handleChange, disabled, errors }) {
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
          "w-full h-9 px-3 text-xs border rounded bg-gray-100" +
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
        <p className="text-red-500 text-[10px]">{errors[f.name]}</p>
      )}
    </div>
  );
}

SelectField.propTypes = {
  f: PropTypes.shape({
    name: PropTypes.string,
    placeholder: PropTypes.string,
    options: PropTypes.array,
  }).isRequired,
  formData: PropTypes.object.isRequired,
  options: PropTypes.object,
  handleChange: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
  errors: PropTypes.object,
};

function MultiSelectField({ f, formData, options, handleChange, disabled, errors }) {
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
          "w-full h-24 px-3 text-xs border rounded bg-gray-100" +
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
        <p className="text-red-500 text-[10px]">{errors[f.name]}</p>
      )}
    </div>
  );
}

MultiSelectField.propTypes = {
  f: PropTypes.shape({
    name: PropTypes.string,
    placeholder: PropTypes.string,
    options: PropTypes.array,
  }).isRequired,
  formData: PropTypes.object.isRequired,
  options: PropTypes.object,
  handleChange: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
  errors: PropTypes.object,
};

function TextInputField({ f, formData, handleChange, disabled, errors }) {
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
          "w-full h-9 px-3 text-xs border rounded bg-gray-100" +
          (disabled ? " bg-gray-200 cursor-not-allowed" : "")
        }
      />

      {errors[f.name] && (
        <p className="text-red-500 text-[10px]">{errors[f.name]}</p>
      )}
    </div>
  );
}

TextInputField.propTypes = {
  f: PropTypes.shape({
    name: PropTypes.string,
    placeholder: PropTypes.string,
  }).isRequired,
  formData: PropTypes.object.isRequired,
  handleChange: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
  errors: PropTypes.object,
};

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
            const finalList = await fetchFieldOptions(field);
            setOptions((prev) => ({ ...prev, [field.name]: finalList }));
          } catch (error) {
            console.error(
              `[CommonForm] Error loading select options for field "${field.name}":`,
              error.message
            );
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

  const formatDate = (date) => {
    if (!date) return "-";
    return new Date(date).toLocaleDateString("en-GB");
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 p-4">
      <div className="w-105 bg-white rounded-xl relative flex flex-col max-h-[90vh]">
        <div className="bg-black px-5 py-3 text-white text-center rounded-t-xl flex-shrink-0">
          {mode === "add" ? `Add ${title}` : `Edit ${title}`}
        </div>

        <div className="overflow-y-auto flex-1 custom-scrollbar">
          {isEdit && (
            <div className="border-b border-gray-300 bg-gray-50 px-5 py-4 sticky top-0 z-10">
              <div className="grid grid-cols-2 gap-8 text-xs">
                <div>
                  <h4 className="font-semibold text-gray-700 mb-2">Created</h4>
                  <p><span className="font-medium">By :</span> {data?.createdBy ?? "-"}</p>
                  <p><span className="font-medium">On :</span> {formatDate(data?.createdOn)}</p>
                </div>
                <div>
                  <h4 className="font-semibold text-gray-700 mb-2">Modified</h4>
                  <p><span className="font-medium">By :</span> {data?.modifiedBy ?? "-"}</p>
                  <p><span className="font-medium">On :</span> {formatDate(data?.modifiedOn)}</p>
                </div>
              </div>
            </div>
          )}

          <form onSubmit={submit} className="p-4 space-y-3">
            {fields.map((f) => {
              if (f.hidden) return null;

              const disabled = isEdit && (f.disableOnEdit || f.name === "identifier");

              if (f.type === "select") {
                return (
                  <SelectField
                    key={f.name}
                    f={f}
                    formData={formData}
                    options={options}
                    handleChange={handleChange}
                    disabled={disabled}
                    errors={errors}
                  />
                );
              }

              if (f.type === "multiselect") {
                return (
                  <MultiSelectField
                    key={f.name}
                    f={f}
                    formData={formData}
                    options={options}
                    handleChange={handleChange}
                    disabled={disabled}
                    errors={errors}
                  />
                );
              }

              return (
                <TextInputField
                  key={f.name}
                  f={f}
                  formData={formData}
                  handleChange={handleChange}
                  disabled={disabled}
                  errors={errors}
                />
              );
            })}

            <div className="flex justify-end gap-2 pt-4 border-t border-gray-100 bg-white sticky bottom-0 z-10">
              <button type="button" onClick={onClose} className="text-xs px-3 py-1.5 border rounded hover:bg-gray-50 transition-colors">
                Cancel
              </button>

              <button type="submit" className="bg-black text-white text-xs px-4 py-1.5 rounded hover:bg-gray-800 transition-colors">
                {mode === "add" ? "Save" : "Update"}
              </button>
            </div>
          </form>
        </div>
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