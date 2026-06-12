"use client";
import PropTypes from "prop-types";

export default function TablePagination({
  currentPage,
  totalPages,
  pageSize,
  setPageSize,
  setCurrentPage,
}) {
  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);}

  return (
    <div
      className="mt-4 flex items-center justify-between"
    >
      <div className="flex items-center gap-2">
        <span className="text-sm text-black">
          Rows:
        </span>
        <div className="rounded-2xl bg-white/60 backdrop-blur-md p-1 shadow-lg border border-white/40"></div>
        <select
          value={pageSize}
          onChange={(e) => {
            setPageSize(Number(e.target.value));
            setCurrentPage(1);
          }}
          className="px-3 py-2 rounded-xl border border-slate-200 bg-white/80 backdrop-blur-sm shadow-sm text-slate-700
                     font-medium transition-all duration-200 hover:border-indigo-300 hover:shadow-md focus:outline-none
                     focus:ring-4 focus:ring-indigo-100 focus:border-indigo-500 cursor-pointer"
        >
          <option value={3}>
            3
          </option>
          <option value={5}>
            5
          </option>
          <option value={10}>
            10
          </option>
          <option value={50}>
            50
          </option>
        </select>
      </div>

      <div className="flex items-center gap-2">

        <button
          disabled={currentPage === 1}
          onClick={() => setCurrentPage(currentPage - 1)}
          className="px-4 py-2 rounded-xl border border-slate-200 bg-white text-slate-700 shadow-sm transition-all duration-300 hover:-translate-y-0.5
                     hover:shadow-md hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed"
        >
          ←
        </button>

        {pageNumbers.map((page) => (
          <button
            key={page}
            onClick={() => setCurrentPage(page)}
            className={`min-w-19 h-10 rounded-xl font-medium transition-all duration-300 transform
        ${currentPage === page
                ? ` bg-indigo-600 text-white shadow-lg scale-110`
                : ` bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 hover:scale-105 hover:shadow-md`
              }`}
          >
            {page}
          </button>
        ))}
        <button
          disabled={currentPage === totalPages}
          onClick={() => setCurrentPage(currentPage + 1)}
          className="px-4 py-2 rounded-xl border border-slate-200 bg-white text-slate-700 shadow-sm transition-all duration-300
                     hover:-translate-y-0.5 hover:shadow-md hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed"
        >
          →
        </button>
      </div>
      </div>
      );
    }
      TablePagination.propTypes = {
        currentPage: PropTypes.number.isRequired,
      totalPages: PropTypes.number.isRequired,
      pageSize: PropTypes.number.isRequired,
      setPageSize: PropTypes.func.isRequired,
      setCurrentPage: PropTypes.func.isRequired,
};