"use client";

import PropTypes from "prop-types";
import { useRouter, usePathname, useSearchParams } from "next/navigation";

export default function TablePagination({
  currentPage,   // 0-based (matches backend)
  totalPages,
  pageSize,
}) {
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  const navigate = (newPage, newSize = pageSize) => {
    const params = new URLSearchParams(searchParams.toString());
    params.set("page", String(newPage));
    params.set("size", String(newSize));
    router.push(`${pathname}?${params.toString()}`);
  };

  const pageNumbers = Array.from({ length: totalPages }, (_, i) => i);

  return (
    <div className="mt-4 flex items-center justify-between">
      <div className="flex items-center gap-2">
        <span className="text-sm text-black">Rows:</span>
        <select
          value={pageSize}
          onChange={(e) => navigate(0, Number(e.target.value))}
          className="px-3 py-2 rounded-xl border border-slate-200 bg-white/80 backdrop-blur-sm shadow-sm text-slate-700
                     font-medium transition-all duration-200 hover:border-indigo-300 hover:shadow-md focus:outline-none
                     focus:ring-4 focus:ring-indigo-100 focus:border-indigo-500 cursor-pointer"
        >
          <option value={5}>5</option>
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={50}>50</option>
        </select>
      </div>

      <div className="flex items-center gap-2">
        <button
          disabled={currentPage === 0}
          onClick={() => navigate(currentPage - 1)}
          className="px-4 py-2 rounded-xl border border-slate-200 bg-white text-slate-700 shadow-sm transition-all duration-300
                     hover:-translate-y-0.5 hover:shadow-md hover:bg-slate-50 disabled:opacity-40 disabled:cursor-not-allowed"
        >
          ←
        </button>

        {pageNumbers.map((p) => (
          <button
            key={p}
            onClick={() => navigate(p)}
            className={`min-w-10 h-10 rounded-xl font-medium transition-all duration-300 transform
              ${currentPage === p
                ? "bg-indigo-600 text-white shadow-lg scale-110"
                : "bg-white border border-slate-200 text-slate-700 hover:bg-slate-50 hover:scale-105 hover:shadow-md"
              }`}
          >
            {p + 1}
          </button>
        ))}

        <button
          disabled={currentPage >= totalPages - 1}
          onClick={() => navigate(currentPage + 1)}
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
  currentPage: PropTypes.number.isRequired,   // 0-based
  totalPages: PropTypes.number.isRequired,
  pageSize: PropTypes.number.isRequired,
};