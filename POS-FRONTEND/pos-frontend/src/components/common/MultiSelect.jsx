import React, { useState } from "react";
import { ChevronDown, X } from "lucide-react";

function MultiSelect({
  label,
  selectedValues = [],
  onChange,
  options = [],
  optionLabel = "name",
  optionValue = "identifier",
  placeholder = "Select",
}) {

  const [open, setOpen] = useState(false);

  // SELECT OPTION
  const handleSelect = (option) => {

    const exists = selectedValues.some(
      (item) =>
        item[optionValue] === option[optionValue]
    );

    if (exists) return;

    onChange([
      ...selectedValues,
      option,
    ]);

  };

  // REMOVE OPTION
  const handleRemove = (option) => {

    const updated =
      selectedValues.filter(
        (item) =>
          item[optionValue] !== option[optionValue]
      );

    onChange(updated);

  };

  return (

    <div className="flex flex-col gap-2 relative">

      {/* LABEL */}
      <label className="text-sm font-medium text-gray-700">
        {label}
      </label>

      {/* BOX */}
      <div
        onClick={() => setOpen(!open)}

        className="
          min-h-[50px]
          border border-gray-300
          rounded-lg
          px-3 py-2
          flex items-center flex-wrap gap-2
          cursor-pointer
          bg-white
        "
      >

        {/* SELECTED TAGS */}
        {selectedValues.length > 0 ? (

          selectedValues.map((item, index) => (

            <div
              key={index}

              className="
                flex items-center gap-1
                bg-red-600
                text-white
                px-2 py-1
                rounded-md
                text-sm
              "
            >

              {item[optionLabel]}

              <button
                type="button"

                onClick={(e) => {

                  e.stopPropagation();

                  handleRemove(item);

                }}
              >

                <X size={14} />

              </button>

            </div>

          ))

        ) : (

          <span className="text-gray-400 text-sm">
            {placeholder}
          </span>

        )}

        {/* ICON */}
        <div className="ml-auto">
          <ChevronDown size={18} />
        </div>

      </div>

      {/* DROPDOWN */}
      {open && (

        <div
          className="
            absolute top-full left-0
            w-full mt-1
            bg-white
            border border-gray-200
            rounded-lg
            shadow-lg
            max-h-52
            overflow-y-auto
            z-50
          "
        >

          {options.map((option, index) => {

            const selected =
              selectedValues.some(
                (item) =>
                  item[optionValue] ===
                  option[optionValue]
              );

            return (

              <div
                key={index}

                onClick={() =>
                  handleSelect(option)
                }

                className={`
                  px-4 py-2
                  cursor-pointer
                  hover:bg-red-50

                  ${
                    selected
                      ? "bg-red-100 text-red-600"
                      : ""
                  }
                `}
              >

                {option[optionLabel]}

              </div>

            );

          })}

        </div>

      )}

    </div>

  );

}

export default MultiSelect;