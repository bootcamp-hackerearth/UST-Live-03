"use client";
 
import { useState } from "react";
import { Search } from "lucide-react";
import PropTypes from 'prop-types';
 
const SearchSelector = ({
  label,
  value,
  options,
  onChange,
  placeholder = "Search...",
  searchFields = ["name"],
}) => {
  const [search, setSearch] = useState("");
  const [open, setOpen] = useState(false);
 
  const filteredOptions =
    search.trim() === ""
      ? []
      : options.filter((item) =>
          searchFields.some((field) =>
            String(item[field] || "")
              .toLowerCase()
              .includes(search.toLowerCase())
          )
        );
 
  const handleSelect = (item) => {
    onChange(item.identifier);
 
    setSearch(
      `${item.name}${
        item.phoneNo ? ` (${item.phoneNo})` : ""
      }`
    );
 
    setOpen(false);
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
          className="w-full border rounded-lg p-3 pl-10"
        />
      </div>
 
      {open && (
        <div className="absolute z-50 w-full mt-1 bg-white border rounded-lg shadow-lg max-h-60 overflow-y-auto">
          {search.trim() !== "" && filteredOptions.length > 0 ? (
            filteredOptions.map((item) => (
              <button
                key={item.identifier}
                type="button"
                onClick={() => handleSelect(item)}
                className="w-full text-left p-3 hover:bg-gray-100 border-b"
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
  value: PropTypes.string,
  options: PropTypes.arrayOf(
    PropTypes.shape({
      identifier: PropTypes.string,
      name: PropTypes.string,
      phoneNo: PropTypes.string
    })
  ).isRequired,
  onChange: PropTypes.func.isRequired,
  placeholder: PropTypes.string,
  searchFields: PropTypes.arrayOf(
    PropTypes.string
  )
};

export default SearchSelector;