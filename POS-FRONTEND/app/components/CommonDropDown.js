import React from "react";
import PropTypes from "prop-types";

const CommonDropdown = ({
  name,
  label,
  options = [],
  value,
  onChange,
  multiple = false,
  optionLabel = "name",
  optionValue = "identifier", 
}) => {

  const getValue = (opt) => {
    return opt?.[optionValue] ?? opt?.username ?? opt?.id ?? "";
  };

  const getLabel = (opt) => {
    return opt?.[optionLabel] ?? opt?.name ?? opt?.identifier ?? "";
  };

  return (
    <div>
      <label className="text-black block mb-1">{label}</label>

      <select
        name={name}
        value={value}
        onChange={onChange}
        multiple={multiple}
        className="w-full border p-2 bg-white text-black"
      >
        {!multiple && <option value="">Select {label}</option>}

        {options.map((opt, index) => {
          const val = getValue(opt);
          const labelText = getLabel(opt);

          return (
            <option
              key={val || index}   
              value={String(val)}
              className="text-black"
            >
              {labelText}
            </option>
          );
        })}
      </select>
    </div>
  );
};

CommonDropdown.propTypes = {
  name: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  options: PropTypes.array.isRequired,   
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.array]),
  onChange: PropTypes.func.isRequired,
  multiple: PropTypes.bool,
  optionLabel: PropTypes.string,
  optionValue: PropTypes.string,
};

export default CommonDropdown;