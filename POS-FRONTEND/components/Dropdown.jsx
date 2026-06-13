'use client';
 
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
          rounded-xl
          bg-slate-50
          text-slate-800
          focus:outline-none
          focus:ring-2
          focus:ring-cyan-500
          focus:border-cyan-500
          transition
        "
      >
 
        <option value=""
                className="text-slate-400 bg-white"
        >
          {placeholder}
        </option>
 
        {options.map((option, index) => (
 
        <option
          key={option.id || index}
          value={option.identifier}
          className="text-black bg-white"
        >
          { option.name || option.identifier}
        </option>

      ))}
 
      </select>
 
    </div>
 
  );
};
 
export default CommonDropdown;