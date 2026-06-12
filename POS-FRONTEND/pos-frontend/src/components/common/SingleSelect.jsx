import React from "react";

function SingleSelect({
  label,
  value,
  onChange,
  options = [],
  optionLabel = "name",
  optionValue = "identifier",
  placeholder = "Select",
}) {

  return (

    <div className="flex flex-col gap-2">

      {/* LABEL */}
      <label className="text-sm font-medium text-gray-700">
        {label}
      </label>

      {/* SELECT */}
      <select
        value={value || ""}

        onChange={(e) =>
          onChange(e.target.value)
        }

        className="
          w-full
          border border-gray-300
          rounded-lg
          px-4 py-3
          bg-white
          focus:outline-none
          focus:ring-2
          focus:ring-red-500
        "
      >

        <option value="">
          {placeholder}
        </option>

        {options.map((option, index) => (

          <option
            key={index}

            value={option[optionValue]}
          >
            {option[optionLabel]}
          </option>

        ))}

      </select>

    </div>

  );

}

export default SingleSelect;