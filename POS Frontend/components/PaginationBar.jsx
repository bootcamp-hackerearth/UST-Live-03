"use client";
import PropTypes from "prop-types";
import { C } from "./listColors";

const ps = {
  paginationBar: {
    display: "flex", alignItems: "center",
    justifyContent: "center", gap: "5px",
    padding: "10px 16px",
    borderTop: `1px solid ${C.gray}`,
    backgroundColor: C.offWhite,
    flexShrink: 0,
  },
  pageBtn: {
    minWidth: "34px", height: "34px", padding: "0 9px",
    borderRadius: "7px",
    borderWidth: "1.5px", borderStyle: "solid", borderColor: C.gray,
    background: C.white, color: "#374151",
    fontSize: "12px", fontWeight: "600", cursor: "pointer",
    display: "flex", alignItems: "center", justifyContent: "center",
  },
  pageBtnActive: {
    background: "#000000",
    borderColor: "#000000", color: "#fff",
  },
  pageArrow: {
    minWidth: "34px", height: "34px", padding: "0 10px",
    borderRadius: "7px",
    borderWidth: "1.5px", borderStyle: "solid", borderColor: C.gray,
    background: C.white, color: C.text,
    fontSize: "15px", fontWeight: "700", cursor: "pointer",
    display: "flex", alignItems: "center", justifyContent: "center",
    transition: "all 0.2s ease",
  },
  pageArrowDisabled: { color: "#d1d5db", borderColor: C.gray, cursor: "not-allowed" },
  pageInfo: { fontSize: "12px", color: C.muted, padding: "0 8px", whiteSpace: "nowrap" },
};

export default function PaginationBar({ currentPage, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  let start = currentPage - 1;
  if (start < 0) start = 0;
  if (start + 3 > totalPages) start = Math.max(0, totalPages - 3);
  const visiblePages = Array.from({ length: Math.min(3, totalPages) }, (_, i) => start + i);

  if (visiblePages.length === 0) return null;

  return (
    <div style={ps.paginationBar}>
      <button
        style={{ ...ps.pageArrow, ...(currentPage === 0 ? ps.pageArrowDisabled : {}) }}
        onClick={() => onPageChange(currentPage - 1)} disabled={currentPage === 0}>
        ‹
      </button>
      {visiblePages.map(pageIndex => (
        <button
          key={`page-${pageIndex}`}
          style={{ ...ps.pageBtn, ...(currentPage === pageIndex ? ps.pageBtnActive : {}) }}
          onClick={() => onPageChange(pageIndex)}>
          {pageIndex + 1}
        </button>
      ))}
      <button
        style={{ ...ps.pageArrow, ...(currentPage === totalPages - 1 ? ps.pageArrowDisabled : {}) }}
        onClick={() => onPageChange(currentPage + 1)} disabled={currentPage === totalPages - 1}>
        ›
      </button>
      <span style={ps.pageInfo}>Page {currentPage + 1} of {totalPages}</span>
    </div>
  );
}

PaginationBar.propTypes = {
  currentPage: PropTypes.number.isRequired,
  totalPages: PropTypes.number.isRequired,
  onPageChange: PropTypes.func.isRequired,
};
