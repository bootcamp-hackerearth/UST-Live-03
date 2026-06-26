import PropTypes from "prop-types";
const FormFields = ({ field, register, errors, dropdownData }) => {

const baseInput = "w-full px-2 py-1 rounded-lg border border-gray-200 bg-[#F9FAFC] text-sm text-gray-700 focus:outline-none focus:ring-1 focus:ring-indigo-400";

  if (["text", "email", "number", "password"].includes(field.type)) {
    return (
      <div>
        <input
          type={field.type}
          {...register(field.name, {
            required: field.required ? `${field.placeholder} is required` : false,
            ...(field.minlength && {
              minLength: {
                value: field.minlength,
                message: field.patternMessage || `${field.placeholder} must be at least ${field.minlength} characters`
              }
            }),
            ...(field.pattern && {
              pattern: {
                value: new RegExp(field.pattern),
                message: field.patternMessage || `Invalid ${field.placeholder}`
              }
            })
          })}
          placeholder={field.placeholder}
          readOnly={field.readOnly || false}
          className={baseInput} />

        {errors[field.name] && (
          <p className="text-xs text-red-400 mt-1">
            {errors[field.name].message}
          </p>
        )}
      </div>
    );
  }

  if (field.type === "select") {
    const options = field.hardCoded
      ? field.hardCodedArray
      : dropdownData[field.dataKey] || [];

    return (
      <div>
        <select
          defaultValue={field.multiple ? undefined : ""}
          multiple={field.multiple || false}
          disabled={field.readOnly || false}
          {...register(field.name)}
          className={`${baseInput} ${field.readOnly ? "bg-gray-100" : ""}`}>
          {!field.multiple && (
    <option value="" disabled hidden>
        {field.placeholder}
    </option>
)}

          {options.map((item) => (
            <option
              key={field.hardCoded ? item : item.id}
              value={field.hardCoded ? item : item.identifier}>
              {field.hardCoded ? item : (item.identifier || item.name)}
            </option>
          ))}
        </select>

        {field.readOnly && (
          <input type="hidden" {...register(field.name)} />
        )}
        {errors[field.name] && (
          <p className="text-xs text-red-400 mt-1">
            {errors[field.name].message}
          </p>
        )}
      </div>
    );
  }
  return null;
};

FormFields.propTypes = {
  field: PropTypes.object.isRequired,
  register: PropTypes.func.isRequired,
  errors: PropTypes.object.isRequired,
  dropdownData: PropTypes.object,
};

export default FormFields;


