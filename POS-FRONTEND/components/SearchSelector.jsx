"use client";

import { useState, useEffect } from "react";
import { Search } from "lucide-react";
import PropTypes from "prop-types";

const SearchSelector = ({
  label,
  value,
  options = [],
  onChange,
  onSearch,
  placeholder = "Search...",
}) => {
  const [search, setSearch] = useState("");
  const [open, setOpen] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(-1);

  const filteredOptions = options;

  useEffect(() => {
    const timer = setTimeout(() => {
      if (onSearch) {
        onSearch(search);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [search]);

  const handleSelect = (item) => {
    onChange(item.identifier);

    setSearch(
  item.phoneNo
    ? `${item.name} (${item.phoneNo})`
    : item.name
);

    setOpen(false);
  };

  const handleKeyDown = (e) => {
    if (!open || filteredOptions.length === 0) return;

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

      <div className="relative">
        <Search
          size={16}
          className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
        />

        <input
          type="text"
          value={search}
          onFocus={() => setOpen(true)}
          onChange={(e) => {
            setSearch(e.target.value);
            setOpen(true);
          }}
          placeholder={placeholder}
          onKeyDown={handleKeyDown}
          className="w-full border rounded-lg p-3 pl-10"
        />
      </div>

      {open && (
        <div className="absolute z-50 w-full mt-1 bg-white border rounded-lg shadow-lg max-h-60 overflow-y-auto">
          {search.trim() !== "" && filteredOptions.length > 0 ? (
            filteredOptions.map((item, index) => (
              <button
                type="button"
                key={item.identifier}
                onClick={() => handleSelect(item)}
                className={`p-3 cursor-pointer border-b ${
                  highlightedIndex === index
                    ? "bg-blue-100"
                    : "hover:bg-gray-100"
                }`}
              >
                <div className="font-medium">
                  {item.name}
                </div>

                {item.phoneNo && (
                  <div className="text-xs text-gray-500">
                    {item.phoneNo}
                  </div>
                )}
              </button>
            ))
          ) : (
            search.trim() !== "" && (
              <div className="p-3 text-gray-500">
                No results found
              </div>
            )
          )}
        </div>
      )}
    </div>
  );
};

SearchSelector.propTypes = {
  label: PropTypes.string,
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  options: PropTypes.arrayOf(
    PropTypes.shape({
      identifier: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number,
      ]),
      name: PropTypes.string,
      phoneNo: PropTypes.string,
    })
  ),
  onChange: PropTypes.func.isRequired,
  onSearch: PropTypes.func,
  placeholder: PropTypes.string,
};

export default SearchSelector;