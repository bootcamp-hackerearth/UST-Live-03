"use client";
import PropTypes from "prop-types";
import { DEFAULT_PAGINATION } from "@/services/api";

export default function TablePagination({

  currentPage = 0,
  totalPages = 0,
  totalRecords = 0,
  pageSize = DEFAULT_PAGINATION.sizePerPage,
  onPageChange,

}) {

  const safeCurrentPage =
    Number(currentPage) || 0;

  const safeTotalPages =
    Number(totalPages) || 0;

  const safeTotalRecords =
    Number(totalRecords) || 0;

  const safePageSize =
    Number(pageSize) || DEFAULT_PAGINATION.sizePerPage;

  const startRecord =
    safeTotalRecords === 0
      ? 0
      : safeCurrentPage *
          safePageSize +
        1;

  const endRecord = Math.min(
    (safeCurrentPage + 1) *
      safePageSize,
    safeTotalRecords
  );

  const pageNumbers = [];

  for (
    let i = 0;
    i < safeTotalPages;
    i++
  ) {
    pageNumbers.push(i);
  }

  return (

    <div className="flex items-center justify-between px-6 py-5 border-t border-gray-200 bg-white">

      <div className="text-sm text-gray-500 font-medium">
        Showing{' '}
        <span className="text-gray-900 font-semibold mx-1">{startRecord}</span>{' '}
        -{' '}
        <span className="text-gray-900 font-semibold mx-1">{endRecord}</span>{' '}
        of{' '}
        <span className="text-gray-900 font-semibold mx-1">{safeTotalRecords}</span>{' '}
        records
      </div>

      <div className="flex items-center gap-2">

        <button
          type="button"
          onClick={() =>
            onPageChange(
              safeCurrentPage - 1
            )
          }
          disabled={safeCurrentPage === 0}
          className={`h-10 px-4 rounded-xl border text-sm font-medium transition-all

          ${
            safeCurrentPage === 0
              ? "border-gray-200 text-gray-300 cursor-not-allowed bg-gray-50"
              : "border-gray-300 text-gray-700 hover:bg-gray-50"
          }`}
        >

          Previous

        </button>

        {
          pageNumbers.map((page) => (
            <button
              type="button"
              key={page}
              onClick={() =>
                onPageChange(page)
              }
              className={`h-10 w-10 rounded-xl text-sm font-semibold transition-all

              ${
                safeCurrentPage === page
                  ? "bg-blue-600 text-white shadow-lg shadow-blue-500/20"
                  : "border border-gray-300 text-gray-700 hover:bg-gray-50"
              }`}
            >

              {page + 1}

            </button>
          ))
        }

        <button
          type="button"
          onClick={() =>
            onPageChange(
              safeCurrentPage + 1
            )
          }
          disabled={
            safeCurrentPage >=
            safeTotalPages - 1
          }
          className={`h-10 px-4 rounded-xl border text-sm font-medium transition-all

          ${
            safeCurrentPage >=
            safeTotalPages - 1
              ? "border-gray-200 text-gray-300 cursor-not-allowed bg-gray-50"
              : "border-gray-300 text-gray-700 hover:bg-gray-50"
          }`}
        >

          Next
        </button>
      </div>
    </div>
  );
}

TablePagination.propTypes = {
  currentPage: PropTypes.number,
  totalPages: PropTypes.number,
  totalRecords: PropTypes.number,
  pageSize: PropTypes.number,
  onPageChange: PropTypes.func,
};