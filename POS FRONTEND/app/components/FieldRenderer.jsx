import PropTypes from "prop-types";
import SingleSelectDropdown from "./Dropdown/SingleSelectDropdown";
import MultiSelectDropdown from "./Dropdown/MultiSelectDropdown";

export default function FieldRenderer({ field, values, onChange }) {
  if (field.type === "custom") return field.component;

  if (field.type === "select") {
    return (
      <SingleSelectDropdown
        label={field.label}
        name={field.key}
        apiPath={field.apiPath}
        apiEndpoint={field.apiEndpoint}
        options={field.options}
        value={values[field.key] ?? ""}
        placeholder={field.placeholder || `Select ${field.label}`}
        required={field.required}
        onChange={onChange}
        optionValue={field.optionValue}
        optionLabel={field.optionLabel}
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
        onChange={onChange}
      />
    );
  }

  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={field.key} className="text-sm font-semibold text-gray-600">
        {field.label}
      </label>
      <input
        id={field.key}
        type={field.type || "text"}
        placeholder={field.placeholder || `Enter ${field.label}`}
        value={values[field.key] || ""}
        onChange={(event) => onChange(field.key, event.target.value)}
        required={field.required}
        className="border border-gray-300 rounded-xl px-4 py-3 text-sm text-gray-800 bg-white outline-none focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100 transition"
      />
    </div>
  );
}

FieldRenderer.propTypes = {
  field: PropTypes.shape({
    key: PropTypes.string.isRequired,
    type: PropTypes.string,
    label: PropTypes.string,
    apiPath: PropTypes.string,
    apiEndpoint: PropTypes.string,
    options: PropTypes.array,
    placeholder: PropTypes.string,
    required: PropTypes.bool,
    optionValue: PropTypes.string,
    optionLabel: PropTypes.string,
    component: PropTypes.node,
  }).isRequired,
  values: PropTypes.object.isRequired,
  onChange: PropTypes.func.isRequired,
};