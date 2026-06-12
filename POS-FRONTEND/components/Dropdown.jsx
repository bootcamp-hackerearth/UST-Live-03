'use client';

import PropTypes from "prop-types";

const CommonDropdown = ({
  label,
  name,
  value,
  options,
  onChange,
  required = false,
  placeholder = '-- Select --'
}) => {

  return (

    <div>

      <label className="block mb-2 text-sm font-semibold text-slate-700">
        {label}
      </label>

      <select
        name={name}
        value={value}
        onChange={onChange}
        required={required}
        className="
          w-full
          p-3
          border
          border-slate-300
          rounded-lg
          focus:outline-none
          focus:ring-2
          focus:ring-slate-400
        "
      >

        <option value="">
          {placeholder}
        </option>

        {options.map((option, index) => (

        <option
          key={option.id || index}
          value={option.identifier}   
        >
          {option.name}               
        </option>

      ))}

      </select>

    </div>

  );
};

CommonDropdown.propTypes = {
  label: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  value: PropTypes.string,
  options: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      identifier: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
      name: PropTypes.string.isRequired,
    })
  ).isRequired,
  onChange: PropTypes.func.isRequired,
  required: PropTypes.bool,
  placeholder: PropTypes.string,
};


export default CommonDropdown;