"use client";

import { useState } from "react";
import { Search } from "lucide-react";
import PropTypes from "prop-types";

const SearchableDropdown = ({
  label,
  name,
  value,
  options,
  onChange,
  placeholder = "-- Select --",
}) => {
  const [open, setOpen] = useState(false);
  const [search, setSearch] = useState("");

  const filteredOptions = options.filter((option) =>
    option.name?.toLowerCase().includes(search.toLowerCase())
  );

  const selectedOption = options.find(
    (option) => option.identifier === value
  );

  const handleSelect = (option) => {
    onChange({
      target: {
        name,
        value: option.identifier,
      },
    });

    setOpen(false);
    setSearch("");
  };

  return (
    <div className="relative">
      {label && (
        <label className="block mb-2 text-sm font-semibold text-slate-700">
          {label}
        </label>
      )}

        <button
          type="button"
          onClick={() => setOpen(!open)}
          className="
            w-full
            p-3
            border
            border-slate-300
            rounded-lg
            bg-white
            cursor-pointer
            flex
            justify-between
            items-center
          "
        >
          
        <span>
          {selectedOption ? selectedOption.name : placeholder}
        </span>

        <span>▼</span>
      </button>

      {open && (
        <div className="absolute z-50 w-full mt-1 bg-white border border-slate-300 rounded-lg shadow-lg">
          <div className="flex items-center gap-2 p-2 border-b">
            <Search size={16} />

            <input
              type="text"
              placeholder="Search..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full outline-none"
            />
          </div>

          <div className="max-h-60 overflow-y-auto">
            {filteredOptions.length > 0 ? (
              filteredOptions.map((option) => (
                <button
                  key={option.identifier}
                  type="button"
                  onClick={() => handleSelect(option)}
                  className="w-full text-left p-3 hover:bg-gray-100"
                >
                  {option.name}
                </button>
              ))
            ) : (
              <div className="p-3 text-gray-500">
                No results found
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

SearchableDropdown.propTypes = {
  label: PropTypes.string,
  name: PropTypes.string.isRequired,
  value: PropTypes.string,
  options: PropTypes.arrayOf(
    PropTypes.shape({
      identifier: PropTypes.string.isRequired,
      name: PropTypes.string.isRequired,
    })
  ).isRequired,
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
};

export default SearchableDropdown;