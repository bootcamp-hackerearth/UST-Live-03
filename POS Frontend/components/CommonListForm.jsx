"use client";
 
import { useEffect, useState, useCallback } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
import { styled } from '@mui/material/styles';
import { Switch } from '@mui/material'; 
 
const C = {
  primary: "#000000",
  secondary: "#3c3c3c",
  gray: "#d1d5db",
  offWhite: "#f4f4f4",
  text: "#1a1a1a",
  muted: "#666666",
  white: "#ffffff",
  error: "#ff4444",
};

const IOSSwitch = styled((props) => (
  <Switch focusVisibleClassName=".Mui-focusVisible" disableRipple {...props} />
))(({ theme }) => ({
  width: 42,
  height: 26,
  padding: 0,
  '& .MuiSwitch-switchBase': {
    padding: 0,
    margin: 2,
    transitionDuration: '300ms',
    '&.Mui-checked': {
      transform: 'translateX(16px)',
      color: '#fff',
      '& + .MuiSwitch-track': {
        backgroundColor: '#65C466',
        opacity: 1,
        border: 0,
        ...theme.applyStyles('dark', {
          backgroundColor: '#2ECA45',
        }),
      },
      '&.Mui-disabled + .MuiSwitch-track': {
        opacity: 0.5,
      },
    },
    '&.Mui-focusVisible .MuiSwitch-thumb': {
      color: '#33cf4d',
      border: '6px solid #fff',
    },
    '&.Mui-disabled .MuiSwitch-thumb': {
      color: theme.palette.grey[100],
      ...theme.applyStyles('dark', {
        color: theme.palette.grey[600],
      }),
    },
    '&.Mui-disabled + .MuiSwitch-track': {
      opacity: 0.7,
      ...theme.applyStyles('dark', {
        opacity: 0.3,
      }),
    },
  },
  '& .MuiSwitch-thumb': {
    boxSizing: 'border-box',
    width: 22,
    height: 22,
  },
  '& .MuiSwitch-track': {
    borderRadius: 26 / 2,
    backgroundColor: '#E9E9EA',
    opacity: 1,
    transition: theme.transitions.create(['background-color'], {
      duration: 500,
    }),
    ...theme.applyStyles('dark', {
      backgroundColor: '#39393D',
    }),
  },
}));
 
const styles = {
  page: {
    position: "fixed", top: "60px", right: 0, bottom: 0,
    backgroundColor: "#f4f4f4", fontFamily: "'Segoe UI', sans-serif",
    display: "flex", flexDirection: "column", overflow: "hidden",
    transition: "left 0.2s ease",
  },
  inner: {
    flex: 1, padding: "20px 24px",
    display: "flex", flexDirection: "column", overflow: "hidden",
  },
  topRow: {
    display: "flex", alignItems: "center",
    marginBottom: "16px", flexShrink: 0, position: "relative",
  },
  backBtn: {
    padding: "8px 16px", backgroundColor: "transparent",
    color: C.primary, border: `1px solid transparent`,
    borderRadius: "6px", fontSize: "13px",
    fontWeight: "600", cursor: "pointer", flexShrink: 0,
    transition: "all 0.2s ease",
    height: "42px",
  },
  title: {
    position: "absolute", left: "50%", transform: "translateX(-50%)",
    margin: 0, fontSize: "19px", fontWeight: "700",
    color: C.text, whiteSpace: "nowrap",
  },
  addBtn: {
    marginLeft: "auto",
    padding: "10px 18px",
    background: "#000000",
    color: "#fff", border: "none",
    borderRadius: "6px", cursor: "pointer",
    fontSize: "14px", fontWeight: "700", flexShrink: 0,
    letterSpacing: "0.2px",
    boxShadow: "0 4px 12px rgba(0,0,0,0.18)",
    transition: "all 0.2s ease",
  },
  card: {
    background: C.white, borderRadius: "12px",
    boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
    border: `1px solid ${C.gray}`,
    flex: 1, overflow: "hidden", display: "flex", flexDirection: "column",
  },
  tableWrap: { overflowY: "auto", flex: 1 },
  table: { width: "100%", borderCollapse: "collapse" },
  th: {
    textAlign: "left", padding: "11px 14px",
    borderBottom: `2px solid ${C.gray}`,
    fontSize: "11px", color: C.muted,
    fontWeight: "700", textTransform: "uppercase",
    letterSpacing: "0.6px",
    backgroundColor: "#ffffff",
    position: "sticky", top: 0,
  },
  tr: { borderBottom: `1px solid #e5e7eb`, transition: "background 0.1s" },
  td: { padding: "11px 14px", fontSize: "13px", color: C.text },
  actionEdit: {
    marginRight: "6px", padding: "6px 8px",
    borderRadius: "8px", backgroundColor: "#111111",
    color: "#ffffff", border: "none",
    cursor: "pointer", fontSize: "12px", fontWeight: "700",
    boxShadow: "0 3px 12px rgba(0,0,0,0.12)",
    transition: "all 0.2s ease",
    minWidth: "34px",
    minHeight: "34px",
  },
  actionDelete: {
    padding: "6px 8px", borderRadius: "8px",
    backgroundColor: "#dc2626",
    color: "#ffffff", border: "none",
    cursor: "pointer", fontSize: "12px", fontWeight: "700",
    boxShadow: "0 3px 12px rgba(0,0,0,0.12)",
    minWidth: "34px",
    minHeight: "34px",
  },
  emptyRow: {
    textAlign: "center", padding: "48px",
    color: C.light, fontSize: "13px",
  },
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
    background: C.white, color: C.navy,
    fontSize: "15px", fontWeight: "700", cursor: "pointer",
    display: "flex", alignItems: "center", justifyContent: "center",
    transition: "all 0.2s ease",
  },
  pageArrowDisabled: { color: "#d1d5db", borderColor: C.gray, cursor: "not-allowed" },
  searchInput: {
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
  pageInfo: { fontSize: "12px", color: C.muted, padding: "0 8px", whiteSpace: "nowrap" },
  modalOverlay: {
    position: "fixed", top: 0, left: 0, right: 0, bottom: 0,
    backgroundColor: "rgba(0,0,0,0.4)", display: "flex",
    alignItems: "center", justifyContent: "center", zIndex: 1000,
    fontFamily: "'Segoe UI', sans-serif",
  },
  modalBox: {
    backgroundColor: "#fff", padding: "24px", borderRadius: "12px",
    boxShadow: "0 4px 20px rgba(0,0,0,0.15)", width: "100%", maxWidth: "400px",
    textAlign: "center",
  },
  modalTitle: { margin: "0 0 10px 0", fontSize: "18px", fontWeight: "700", color: C.primary },
  modalText: { margin: "0 0 20px 0", fontSize: "14px", color: C.text, lineHeight: "1.5" },
  modalBtns: { display: "flex", gap: "12px", justifyContent: "center" },
  modalCancel: {
    padding: "9px 18px", backgroundColor: C.gray, color: C.text,
    border: "none", borderRadius: "6px", cursor: "pointer", fontSize: "13px", fontWeight: "600",
  },
  modalConfirm: {
    padding: "9px 18px", backgroundColor: "#dc2626", color: "#fff",
    border: "none", borderRadius: "6px", cursor: "pointer", fontSize: "13px", fontWeight: "600",
  },
};
 
const toCamelCase = (value) => value.replaceAll(/_([a-z])/g, (_, letter) => letter.toUpperCase());

const getFieldValue = (item, field) => {
  const value = item[field];
  const camelCaseKey = toCamelCase(field);
  const realValue = value ?? item[camelCaseKey] ?? "";

  return Array.isArray(realValue) ? realValue.join(" ") : String(realValue);
};

const matchesSearch = (item, regex, paramKey, fields) => {
  if (regex.test(String(item[paramKey] ?? ""))) {
    return true;
  }

  return fields.some((field) => regex.test(getFieldValue(item, field)));
};
 
export default function ListingSkeleton({
  title, fields, apis, addPath, editPathBase,
  paramKey = "identifier", identifierLabel = "Identifier", deleteStyle = "path",
}) {
  const [data, setData] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [searchQuery, setSearchQuery] = useState("");
  const [loadError, setLoadError] = useState("");
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const router = useRouter();
  const [pagination, setPagination] = useState({
    page: 0, sizePerPage: 5, sortDirection: "ASC", sortField: "id",
  });
 
  useEffect(() => {
    const handleToggle = (e) => setIsSidebarOpen(e.detail.isOpen);
    globalThis.addEventListener("sidebar-toggle", handleToggle);
    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);
 
  const loadList = useCallback(async () => {
    try {
      if (searchQuery.trim() === "") {
        const res = await api.post(apis.list, pagination);
        if (Array.isArray(res.data)) {
          setData(res.data);
          setTotalPages(1);
        } else {
          setData(res.data.dtoList ?? []);
          setTotalPages(res.data.totalPages ?? 1);
        }
        setLoadError("");
      } else {
        const res = await api.post(apis.list, { ...pagination, page: 0, sizePerPage: 1000 });
        const allData = Array.isArray(res.data) ? res.data : (res.data.dtoList ?? []);
          
        const escapedSearch = searchQuery.replaceAll(/[.*+?^${}()|[\]\\]/g, String.raw`\$&`);
        const regex = new RegExp(String.raw`\b` + escapedSearch, "i");
 
        const filtered = allData.filter((item) => matchesSearch(item, regex, paramKey, fields));
 
        setData(filtered);
        setTotalPages(1);
        setLoadError("");
      }
    } catch (err) {
      const status = err.response?.status;
      if (status === 403) {
        console.warn(`Permission denied accessing ${apis.list}:`, err.response?.data || err.message);
        setLoadError("You do not have permission to view this resource.");
      } else {
        console.error(`Failed to load ${apis.list}:`, err.response?.data || err.message);
        setLoadError("Failed to load data. Please try again.");
      }
      setData([]);
      setTotalPages(0);
    }
  }, [apis.list, pagination, searchQuery, fields, paramKey]);
 
  useEffect(() => {
    loadList();
  }, [loadList]);
 
  async function confirmDelete() {
    if (!deleteTarget) return;
    const value = String(deleteTarget[paramKey] ?? "");

    if (deleteStyle === "param") {
      await api.get(apis.delete, { params: { [paramKey]: value } });
    } else {
      await api.get(`${apis.delete}/${encodeURIComponent(value)}`);
    }
    setDeleteTarget(null);
    loadList();
  }
 
  async function handleToggle(row) {
    const value = String(row[paramKey] ?? "");
    await api.post(apis.toggleStatus, null, { params: { [paramKey]: value } });
    loadList();
  }
 
  function goToPage(pageIndex) {
    setPagination(prev => ({ ...prev, page: pageIndex }));
  }
 
  const currentPage = pagination.page;
 
  function getVisiblePages() {
    if (totalPages <= 0) return [];
    let start = currentPage - 1;
    if (start < 0) start = 0;
    if (start + 3 > totalPages) start = Math.max(0, totalPages - 3);
    return Array.from({ length: Math.min(3, totalPages) }, (_, i) => start + i);
  }
 
  return (
    <div style={{ ...styles.page, left: isSidebarOpen ? "220px" : "55px" }}>
      <div style={styles.inner}>
        <div style={styles.topRow}>
          <button style={styles.backBtn} onClick={() => router.push("/home")}>⮜ Home</button>
          <h2 style={styles.title}>{title}</h2>
          <input
            style={styles.searchInput}
            type="text"
            placeholder={`Search ${title}`}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button style={styles.addBtn} onClick={() => router.push(addPath)} disabled={!!loadError}>
            + Add {title}
          </button>
        </div>
 
        <div style={styles.card}>
          <div style={styles.tableWrap}>
            {loadError ? (
              <div style={{ color: "#7f1d1d", backgroundColor: "#fee2e2", padding: "48px 20px", textAlign: "center", fontSize: "14px", lineHeight: "1.5" }}>
                ⚠️ {loadError}
              </div>
            ) : (
            <table style={styles.table}>
              <thead>
                <tr>
                  <th style={styles.th}>{identifierLabel}</th>
                  {fields.map(f => (
                    <th key={f} style={styles.th}>{f.charAt(0).toUpperCase() + f.slice(1)}</th>
                  ))}
                  <th style={styles.th}>Status</th>
                  <th style={styles.th}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {data.length === 0 ? (
                  <tr>
                    <td colSpan={fields.length + 3} style={styles.emptyRow}>
                      No records found.
                    </td>
                  </tr>
                ) : (
                  data.map(row => (
                    <tr key={row[paramKey]} style={styles.tr}>
                      <td style={styles.td}>{row[paramKey]}</td>
                      {fields.map((f) => {
                        const value = row[f] ?? row[toCamelCase(f)] ?? "";
 
                        return (
                          <td style={styles.td} key={f}>
                            {Array.isArray(value) ? value.join(", ") : String(value)}
                          </td>
                        );
                      })}
                      <td style={styles.td}>
                        <IOSSwitch 
                          checked={!!row.status}
                          onChange={() => handleToggle(row)}
                        />
                      </td>
                      <td style={styles.td}>
                        <button
                          style={styles.actionEdit}
                          onClick={() => router.push(editPathBase + encodeURIComponent(String(row[paramKey] ?? "")))}
                          title="Edit"
                          aria-label={`Edit ${row[paramKey]}`}
                        >
                          ✎
                        </button>
                        <button
                          style={styles.actionDelete}
                          onClick={() => setDeleteTarget(row)}
                          title="Delete"
                          aria-label={`Delete ${row[paramKey]}`}
                        >
                          🗑
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
            )}
          </div>
 
          {!loadError && searchQuery.trim() === "" && totalPages > 1 && getVisiblePages().length > 0 && (
            <div style={styles.paginationBar}>
              <button
                style={{ ...styles.pageArrow, ...(currentPage === 0 ? styles.pageArrowDisabled : {}) }}
                onClick={() => goToPage(currentPage - 1)} disabled={currentPage === 0}>
                ‹
              </button>
              {getVisiblePages().map(pageIndex => (
                <button
                  key={`page-${pageIndex}`}
                  style={{ ...styles.pageBtn, ...(currentPage === pageIndex ? styles.pageBtnActive : {}) }}
                  onClick={() => goToPage(pageIndex)}>
                  {pageIndex + 1}
                </button>
              ))}
              <button
                style={{ ...styles.pageArrow, ...(currentPage === totalPages - 1 ? styles.pageArrowDisabled : {}) }}
                onClick={() => goToPage(currentPage + 1)} disabled={currentPage === totalPages - 1}>
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
              Are you sure you want to delete <strong>{deleteTarget[paramKey]}</strong>? This action cannot be undone.
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

ListingSkeleton.propTypes = {
  title: PropTypes.string.isRequired,
  fields: PropTypes.arrayOf(PropTypes.string).isRequired,
  apis: PropTypes.shape({
    list: PropTypes.string.isRequired,
    delete: PropTypes.string.isRequired,
    toggleStatus: PropTypes.string.isRequired,
  }).isRequired,
  addPath: PropTypes.string.isRequired,
  editPathBase: PropTypes.string.isRequired,
  paramKey: PropTypes.string,
  identifierLabel: PropTypes.string,
  deleteStyle: PropTypes.oneOf(["path", "param"]),
};