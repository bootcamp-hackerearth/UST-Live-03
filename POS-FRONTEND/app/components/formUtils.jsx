"use client";
import PropTypes from "prop-types";
import SingleSelectDropdown from "./Dropdown/SingleSelectDropdown";
import MultiSelectDropdown from "./Dropdown/MultiSelectDropdown";

export const INPUT_CLS =
  "rounded-lg border border-slate-200 bg-white px-4 py-2.5 text-sm font-medium text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100";

export const FieldWrapper = ({ label, children }) => (
  <div className="flex flex-col gap-1.5">
    <label className="text-sm font-bold text-slate-700">{label}</label>
    {children}
  </div>
);

FieldWrapper.propTypes = {
  label: PropTypes.string.isRequired,
  children: PropTypes.node.isRequired,
};

export const extraFieldShape = PropTypes.shape({
  key: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  type: PropTypes.string,
  placeholder: PropTypes.string,
  required: PropTypes.bool,
  apiPath: PropTypes.string,
  apiEndpoint: PropTypes.string,
  options: PropTypes.array,
  asArray: PropTypes.bool,
  valueType: PropTypes.string,
  labelKey: PropTypes.string,
  valueKey: PropTypes.string,
  pattern: PropTypes.string,
  maxLength: PropTypes.number,
  title: PropTypes.string,
  component: PropTypes.node,
});

export function renderField(field, values, handleChange) {
  if (field.type === "custom") return field.component;

  if (field.type === "select") {
    return (
      <SingleSelectDropdown
        label={field.label}
        name={field.key}
        apiPath={field.apiPath}
        apiEndpoint={field.apiEndpoint}
        options={field.options}
        labelKey={field.labelKey}
        valueKey={field.valueKey}
        value={values[field.key] || ""}
        placeholder={field.placeholder || `Select ${field.label}`}
        required={field.required}
        onChange={handleChange}
      />
    );
  }

  if (field.type === "multiselect") {
    return (
      <MultiSelectDropdown
        label={field.label}
        name={field.key}
        apiPath={field.apiPath}
        apiEndpoint={field.apiEndpoint}
        options={field.options}
        value={values[field.key] || []}
        required={field.required}
        helperText="Hold Ctrl to select multiple"
        onChange={handleChange}
      />
    );
  }

  return (
    <FieldWrapper label={field.label}>
      <input
        type={field.type || "text"}
        placeholder={field.placeholder || `Enter ${field.label}`}
        value={values[field.key] || ""}
        onChange={(e) => handleChange(field.key, e.target.value)}
        required={field.required}
        pattern={field.pattern}
        maxLength={field.maxLength}
        title={field.title}
        className={`placeholder:text-slate-400 ${INPUT_CLS}`}
      />
    </FieldWrapper>
  );
}

export function buildPayload({ id, identifierKey, identifier, showDescription, description, values, extraFields, extraData }) {
  const payload = {
    ...(id !== undefined && id !== null ? { id } : {}),
    [identifierKey]: identifier,
    ...(showDescription ? { description } : {}),
    ...values,
    ...extraData,
  };

  extraFields.forEach((field) => {
    if (field.asArray) {
      const current = payload[field.key];
      if (Array.isArray(current)) {
        payload[field.key] = current;
      } else if (current) {
        payload[field.key] = [current];
      } else {
        payload[field.key] = [];
      }
    }
    if (field.valueType === "boolean") {
      payload[field.key] =
        payload[field.key] === true || payload[field.key] === "true";
    }
  });

  return payload;
}

export const ErrorBanner = ({ message }) => (
  <div className="mb-5 rounded-lg border border-rose-100 bg-rose-50 px-4 py-3 text-sm font-bold text-rose-600">
    {message}
  </div>
);

ErrorBanner.propTypes = { message: PropTypes.string.isRequired };

export const DescriptionField = ({ value, onChange, placeholder }) => (
  <div className="flex flex-col gap-1.5 md:col-span-2">
    <label htmlFor="description" className="text-sm font-bold text-slate-700">
      Description
    </label>
    <textarea
      id="description"
      placeholder={placeholder || "Enter description"}
      value={value}
      onChange={onChange}
      rows={3}
      className={`resize-none ${INPUT_CLS}`}
    />
  </div>
);

DescriptionField.propTypes = {
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
};

export const ExtraFieldsList = ({ extraFields, values, handleChange }) =>
  extraFields.map((field) => (
    <div key={field.key}>{renderField(field, values, handleChange)}</div>
  ));

ExtraFieldsList.propTypes = {
  extraFields: PropTypes.arrayOf(extraFieldShape).isRequired,
  values: PropTypes.object.isRequired,
  handleChange: PropTypes.func.isRequired,
};

export const FormActions = ({ onCancel, loading, submitLabel }) => (
  <div className="mt-8 flex flex-col-reverse gap-3 sm:flex-row sm:justify-end">
    <button
      type="button"
      onClick={onCancel}
      className="inline-flex items-center justify-center rounded-lg bg-slate-100 px-5 py-2.5 text-sm font-bold text-slate-700 transition hover:bg-slate-200"
    >
      Cancel
    </button>
    <button
      type="submit"
      disabled={loading}
      className="inline-flex items-center justify-center rounded-lg bg-cyan-600 px-5 py-2.5 text-sm font-bold text-white shadow-lg shadow-cyan-200/70 transition hover:bg-cyan-700 disabled:cursor-not-allowed disabled:opacity-60"
    >
      {loading ? "Saving..." : submitLabel}
    </button>
  </div>
);

FormActions.propTypes = {
  onCancel: PropTypes.func.isRequired,
  loading: PropTypes.bool.isRequired,
  submitLabel: PropTypes.string.isRequired,
};
