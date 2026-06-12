"use client";
import PropTypes from "prop-types";

export default function SearchBar({
  searchTerm,
  setSearchTerm,
}) {
  return (
    <div className="mb-4">
  <div className="relative w-1/3">
    
    <svg
      xmlns="http://www.w3.org/2000/svg"
      className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 text-slate-400"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
      strokeWidth={2}
    >
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M21 21l-4.35-4.35m1.85-5.15a7 7 0 11-14 0 7 7 0 0114 0z"
      />
    </svg>

    <input
      type="text"
      placeholder="Search..."
      value={searchTerm}
      onChange={(e) => setSearchTerm(e.target.value)}
      className="w-full rounded-xl border border-slate-300 bg-white py-2.5 pl-10 pr-4 text-sm text-slate-700 shadow-sm
                 transition-all duration-200 placeholder:text-slate-400 hover:border-slate-400 focus:border-blue-500
                 focus:ring-2 focus:ring-blue-200 focus:outline-none"
    />
  </div>
</div>
  );
}

SearchBar.propTypes = {
  searchTerm: PropTypes.string,
  setSearchTerm: PropTypes.func.isRequired,
};