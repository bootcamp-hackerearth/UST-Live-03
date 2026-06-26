"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
import StatusToggle from "@/components/StatusToggle";
import { deleteModalSt } from "@/components/listColors";

const C = {
  navy: "#000000", mid: "#1a1a1a", light: "#333333",
  gray: "#e8e8e8", offWhite: "#f5f5f5", text: "#1a1a1a",
  muted: "#666666", white: "#ffffff",
};

const cellPad   = "16px 24px";
const fieldPad  = "14px 24px";

const styles = {
  page: {
    width: "100%",
    minHeight: "calc(100vh - 60px)",
    backgroundColor: "#ffffff",
    fontFamily: "'Segoe UI', sans-serif",
    display: "flex",
    flexDirection: "column",
    transition: "padding-left 0.2s ease",
    boxSizing: "border-box",
    marginTop: "60px",
  },
  inner: {
    flex: 1,
    padding: "40px 40px 32px",
    display: "flex",
    flexDirection: "column",
    overflow: "hidden",
  },
  topRow: {
    display: "flex",
    alignItems: "center",
    marginBottom: "24px",
    flexShrink: 0,
    position: "relative",
  },
  backBtn: {
    padding: "8px 16px",
    backgroundColor: "transparent",
    color: C.mid,
    border: "none",
    borderRadius: "8px",
    fontSize: "13px",
    fontWeight: "600",
    cursor: "pointer",
    flexShrink: 0,
    transition: "all 0.2s ease",
    height: "42px",
    display: "flex",
    alignItems: "center",
    gap: "6px",
  },
  searchBox: {
    padding: "10px 14px",
    border: `1.2px solid ${C.gray}`,
    borderRadius: "8px",
    backgroundColor: "#ffffff",
    color: C.text,
    fontSize: "13px",
    width: "260px",
    marginLeft: "16px",
    outline: "none",
    boxShadow: "none",
    height: "42px",
  },
  title: {
    position: "absolute",
    left: "50%",
    transform: "translateX(-50%)",
    margin: 0,
    fontSize: "22px",
    fontWeight: "700",
    color: C.navy,
    whiteSpace: "nowrap",
    letterSpacing: "-0.5px",
  },
  addBtn: {
    marginLeft: "auto",
    padding: "10px 18px",
    background: "linear-gradient(135deg, #000000, #1a1a1a)",
    color: "#fff",
    border: "none",
    borderRadius: "8px",
    cursor: "pointer",
    fontSize: "14px",
    fontWeight: "700",
    flexShrink: 0,
    letterSpacing: "0.2px",
    boxShadow: "0 3px 10px rgba(0,0,0,0.2)",
    transition: "all 0.2s ease",
    height: "42px",
    display: "flex",
    alignItems: "center",
  },
  card: {
    background: C.white,
    borderRadius: "12px",
    boxShadow: "0 4px 20px rgba(0,0,0,0.04)",
    border: `1px solid ${C.gray}`,
    flex: 1,
    overflow: "hidden",
    display: "flex",
    flexDirection: "column",
    padding: "8px 0 0 0",
  },
  tableWrap: { overflowY: "auto", flex: 1 },
  table: { width: "100%", borderCollapse: "collapse" },
  th: {
    textAlign: "left",
    padding: fieldPad,
    borderBottom: `1px solid ${C.gray}`,
    fontSize: "11px",
    color: "#4b5563",
    fontWeight: "700",
    textTransform: "uppercase",
    letterSpacing: "0.8px",
    backgroundColor: "#ffffff",
    position: "sticky",
    top: 0,
    zIndex: 10,
  },
  tr: { borderBottom: "1px solid #f3f4f6", transition: "background 0.1s" },
  td: { padding: cellPad, fontSize: "13px", color: C.text, fontWeight: "500" },
  actionEdit: {
    marginRight: "6px",
    padding: "6px 8px",
    borderRadius: "8px",
    backgroundColor: "#000000",
    color: "white",
    border: "none",
    cursor: "pointer",
    fontSize: "11px",
    fontWeight: "700",
    boxShadow: "0 3px 12px rgba(0,0,0,0.12)",
    minWidth: "34px",
    minHeight: "34px",
  },
  actionDelete: {
    padding: "6px 8px",
    borderRadius: "8px",
    backgroundColor: "#dc2626",
    color: "white",
    border: "none",
    cursor: "pointer",
    fontSize: "11px",
    fontWeight: "700",
    boxShadow: "0 3px 12px rgba(0,0,0,0.12)",
    minWidth: "34px",
    minHeight: "34px",
  },
  emptyRow: { textAlign: "center", padding: "64px", color: C.muted, fontSize: "14px" },
  paginationBar: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "6px",
    padding: "16px 24px",
    borderTop: `1px solid ${C.gray}`,
    backgroundColor: "#ffffff",
    flexShrink: 0,
  },
  pageBtn: {
    minWidth: "34px", height: "34px", padding: "0 9px",
    borderRadius: "6px", border: `1px solid ${C.gray}`,
    background: C.white, color: "#374151",
    fontSize: "12px", fontWeight: "600", cursor: "pointer",
    display: "flex", alignItems: "center", justifyContent: "center",
  },
  pageBtnActive: {
    background: "linear-gradient(135deg, #000000, #1a1a1a)",
    borderColor: "#000000",
    color: "#fff",
  },
  pageArrow: {
    minWidth: "34px", height: "34px", padding: "0 10px",
    borderRadius: "6px", border: `1px solid ${C.gray}`,
    background: C.white, color: C.navy,
    fontSize: "14px", fontWeight: "700", cursor: "pointer",
    display: "flex", alignItems: "center", justifyContent: "center",
    transition: "all 0.2s ease",
  },
  pageArrowDisabled: { color: "#d1d5db", borderColor: C.gray, cursor: "not-allowed" },
  pageInfo: { fontSize: "12px", color: C.muted, padding: "0 8px", whiteSpace: "nowrap" },
  ...deleteModalSt,
  modalCancel: { ...deleteModalSt.modalCancel, backgroundColor: C.gray },
};

function formatCurrency(value) {
  return value === null ? "—" : `₹${Number(value).toFixed(2)}`;
}

export default function ListPrice() {
  const [data, setData] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const router = useRouter();
  const [pagination, setPagination] = useState({
    page: 0, sizePerPage: 5, sortDirection: "ASC", sortField: "id",
  });

  useEffect(() => {
    const handleToggle = (e) => setSidebarOpen(e.detail.isOpen);
    globalThis.addEventListener("sidebar-toggle", handleToggle);
    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);

  async function loadList() {
    try {
      const res = await api.post("/price/list", pagination);
      if (Array.isArray(res.data)) {
        setData(res.data);
        setTotalPages(1);
      } else {
        setData(res.data?.dtoList || res.data?.content || []);
        setTotalPages(res.data?.totalPages || 1);
      }
    } catch (err) {
      console.error("Failed to populate price catalog list:", err);
    }
  }

  useEffect(() => { loadList(); }, [pagination]);

  async function confirmDelete() {
    if (!deleteTarget) return;
    await api.delete("/price/delete", { params: { identifier: deleteTarget } });
    setDeleteTarget(null);
    loadList();
  }

  async function handleToggle(identifier) {
    await api.post(`/price/toggle-status?identifier=${encodeURIComponent(identifier)}`);
    loadList();
  }

  function goToPage(pageIndex) {
    setPagination((prev) => ({ ...prev, page: pageIndex }));
  }

  const currentPage = pagination.page;

  const filteredData = data.filter(row => {
    const query = searchTerm.trim().toLowerCase();
    if (!query) return true;
    return String(row.identifier || "").toLowerCase().includes(query);
  });

  function getVisiblePages() {
    if (totalPages <= 0) return [];
    let start = currentPage - 1;
    if (start < 0) start = 0;
    if (start + 3 > totalPages) start = Math.max(0, totalPages - 3);
    return Array.from({ length: Math.min(3, totalPages) }, (_, i) => start + i);
  }

  const visiblePages = getVisiblePages();

  return (
    <div style={{ ...styles.page, paddingLeft: sidebarOpen ? "220px" : "55px" }}>
      <div style={styles.inner}>
        <div style={styles.topRow}>
          <button className="home-back-button" style={styles.backBtn} onClick={() => router.push("/home")}>
            ⮜ Home
          </button>
          <input
            type="text"
            placeholder="Search price profiles..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={styles.searchBox}
          />
          <h2 style={styles.title}>Prices</h2>
          <button style={styles.addBtn} onClick={() => router.push("/price/add")}>
            + Add Price
          </button>
        </div>

        <div style={styles.card}>
          <div style={styles.tableWrap}>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th style={styles.th}>Product Identifier</th>
                  <th style={styles.th}>MRP</th>
                  <th style={styles.th}>Selling Price</th>
                  <th style={styles.th}>Cost Price</th>
                  <th style={styles.th}>Effective From</th>
                  <th style={styles.th}>Status</th>
                  <th style={styles.th}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredData.length === 0 ? (
                  <tr>
                    <td colSpan={7} style={styles.emptyRow}>No records found.</td>
                  </tr>
                ) : (
                  filteredData.map((price) => (
                    <tr key={price.identifier} style={styles.tr}>
                      <td style={styles.td}>{price.identifier}</td>
                      <td style={styles.td}>{formatCurrency(price.mrp)}</td>
                      <td style={styles.td}>{formatCurrency(price.sellingPrice)}</td>
                      <td style={styles.td}>{formatCurrency(price.costPrice)}</td>
                      <td style={styles.td}>{price.effectiveFrom || "—"}</td>
                      <td style={styles.td}>
                        <StatusToggle
                          checked={!!price.status}
                          onChange={() => handleToggle(price.identifier)}
                        />
                      </td>
                      <td style={styles.td}>
                        <button
                          style={styles.actionEdit}
                          onClick={() => router.push(`/price/edit/${encodeURIComponent(price.identifier)}`)}
                        >
                          ✎
                        </button>
                        <button
                          style={styles.actionDelete}
                          onClick={() => setDeleteTarget(price.identifier)}
                        >
                          🗑
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {totalPages > 1 && visiblePages.length > 0 && (
            <div style={styles.paginationBar}>
              <button
                style={{ ...styles.pageArrow, ...(currentPage === 0 ? styles.pageArrowDisabled : {}) }}
                onClick={() => goToPage(currentPage - 1)}
                disabled={currentPage === 0}
              >
                ‹
              </button>
              {visiblePages.map((pageIndex) => (
                <button
                  key={`page-${pageIndex}`}
                  style={{ ...styles.pageBtn, ...(currentPage === pageIndex ? styles.pageBtnActive : {}) }}
                  onClick={() => goToPage(pageIndex)}
                >
                  {pageIndex + 1}
                </button>
              ))}
              <button
                style={{ ...styles.pageArrow, ...(currentPage === totalPages - 1 ? styles.pageArrowDisabled : {}) }}
                onClick={() => goToPage(currentPage + 1)}
                disabled={currentPage === totalPages - 1}
              >
                ›
              </button>
              <span style={styles.pageInfo}>Page {currentPage + 1} of {totalPages}</span>
            </div>
          )}
        </div>
      </div>

      {deleteTarget && (
        <div style={styles.modalOverlay}>
          <div style={styles.modalBox}>
            <h3 style={styles.modalTitle}>Confirm Deletion</h3>
            <p style={styles.modalText}>
              Are you sure you want to delete the price record for <strong>{deleteTarget}</strong>? This action cannot be undone.
            </p>
            <div style={styles.modalBtns}>
              <button style={styles.modalCancel} onClick={() => setDeleteTarget(null)}>Cancel</button>
              <button style={styles.modalConfirm} onClick={confirmDelete}>Delete</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}