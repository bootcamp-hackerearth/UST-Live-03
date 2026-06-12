import { useEffect, useState } from "react";

export function useSidebarOpen(initial = true) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(initial);
  useEffect(() => {
    const handler = (e) => setIsSidebarOpen(e.detail.isOpen);
    globalThis.addEventListener("sidebar-toggle", handler);
    return () => globalThis.removeEventListener("sidebar-toggle", handler);
  }, []);
  return isSidebarOpen;
}

export function usePageNavigation(pagination, setPagination) {
  const currentPage = pagination.page;

  function goToPage(pageIndex) {
    setPagination((prev) => ({ ...prev, page: pageIndex }));
  }

  function getVisiblePages(totalPages) {
    if (totalPages <= 0) return [];
    let start = currentPage - 1;
    if (start < 0) start = 0;
    if (start + 3 > totalPages) start = Math.max(0, totalPages - 3);
    return Array.from({ length: Math.min(3, totalPages) }, (_, i) => start + i);
  }

  return { currentPage, goToPage, getVisiblePages };
}

export const thStyle = {
  textAlign: "left", padding: "11px 20px",
  borderBottom: "2px solid #e8eaf0",
  fontSize: "11px", color: "#363955",
  fontWeight: "800", textTransform: "uppercase",
  letterSpacing: "0.8px",
  position: "sticky", top: 0, background: "#f7f8fc",
};

export const tdStyle = {
  padding: "12px 20px", fontSize: "13px", color: "#1e2235",
};

function getPaginationBackground(disabled, active) {
  if (active) return "linear-gradient(135deg, #363955, #54668E)";
  if (disabled) return "#f3f4f6";
  return "#fff";
}

function getPaginationColor(disabled, active) {
  if (active) return "#fff";
  if (disabled) return "#d1d5db";
  return "#374151";
}

export function paginationBtn(disabled, active) {
  const background = getPaginationBackground(disabled, active);
  const color = getPaginationColor(disabled, active);

  return {
    minWidth: "32px", height: "32px", borderRadius: "6px",
    border: active ? "none" : "1.5px solid #E8E8E8",
    background,
    color,
    fontSize: "13px", fontWeight: "600",
    cursor: disabled ? "not-allowed" : "pointer",
    display: "inline-flex", alignItems: "center", justifyContent: "center",
  };
}