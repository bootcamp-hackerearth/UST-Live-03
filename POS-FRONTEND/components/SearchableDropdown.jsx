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
  const [highlightedIndex, setHighlightedIndex] = useState(-1);

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
    setHighlightedIndex(-1);
  };

  const handleKeyDown = (e) => {
    if (filteredOptions.length === 0) return;

    switch (e.key) {
      case "ArrowDown":
        e.preventDefault();
        setHighlightedIndex((prev) =>
          prev < filteredOptions.length - 1 ? prev + 1 : 0
        );
        break;

      case "ArrowUp":
        e.preventDefault();
        setHighlightedIndex((prev) =>
          prev > 0 ? prev - 1 : filteredOptions.length - 1
        );
        break;

      case "Enter":
        e.preventDefault();

        if (highlightedIndex >= 0) {
          handleSelect(filteredOptions[highlightedIndex]);
        }
        break;

      case "Escape":
        setOpen(false);
        setHighlightedIndex(-1);
        break;

      default:
        break;
    }
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
        onClick={() => {
          setOpen(!open);
          setHighlightedIndex(-1);
        }}
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
              autoFocus
              onChange={(e) => {
                setSearch(e.target.value);
                setHighlightedIndex(0);
              }}
              onKeyDown={handleKeyDown}
              className="w-full outline-none"
            />
          </div>

          <div className="max-h-60 overflow-y-auto">
            {filteredOptions.length > 0 ? (
              filteredOptions.map((option, index) => (
                <button
                  type="button"
                  key={option.identifier}
                  onClick={() => handleSelect(option)}
                  className={`w-full text-left p-3 ${
                    highlightedIndex === index
                      ? "bg-blue-100"
                      : "hover:bg-gray-100"
                  }`}
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
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  options: PropTypes.arrayOf(
    PropTypes.shape({
      identifier: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number,
      ]).isRequired,
      name: PropTypes.string,
    })
  ).isRequired,
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
};

export default SearchableDropdown;