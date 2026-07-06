"use client";

import { useEffect, useState, useCallback } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
import { styled } from '@mui/material/styles';
import { Switch } from '@mui/material';
import { searchInputSt, listSharedStyles, deleteModalSt } from "@/components/listColors";
import PaginationBar from "@/components/PaginationBar";

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
  ...listSharedStyles,
  ...deleteModalSt,
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
};
 
const toCamelCase = (value) => value.replaceAll(/_([a-z])/g, (_, letter) => letter.toUpperCase());
 
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
      const payload = { ...pagination, keyword: searchQuery.trim() };
      const res = await api.post(apis.list, payload);

      if (Array.isArray(res.data)) {
        setData(res.data);
        setTotalPages(1);
      } else {
        setData(res.data.dtoList ?? []);
        setTotalPages(res.data.totalPages ?? 1);
      }
      setLoadError("");
    } catch (err) {
      const status = err.response?.status;
      if (status === 403) {
        setLoadError("You do not have permission to view this resource.");
      } else {
        setLoadError("Failed to load data. Please try again.");
      }
      setData([]);
      setTotalPages(0);
    }
  }, [apis.list, pagination, searchQuery]);
 
  useEffect(() => {
    loadList();
  }, [loadList]);
 
  async function confirmDelete() {
    if (!deleteTarget) return;
    const value = String(deleteTarget[paramKey] ?? "");

    try {
      if (deleteStyle === "param") {
        await api.delete(apis.delete, { params: { [paramKey]: value }, skipErrorRedirect: [403] });
      } else {
        await api.delete(`${apis.delete}/${encodeURIComponent(value)}`, { skipErrorRedirect: [403] });
      }
      setDeleteTarget(null);
      if (data.length === 1 && pagination.page > 0) {
        setPagination(prev => ({ ...prev, page: prev.page - 1 }));
      } else {
        loadList();
      }
    } catch (err) {
      const status = err.response?.status;
      if (status === 403) {
        alert(`Cannot delete ${value}. Permission denied.`);
      } else {
        alert(`Failed to delete ${value}. Please try again.`);
      }
    }
  }
 
  async function handleToggle(row) {
    const value = String(row[paramKey] ?? "");
    try {
      await api.post(apis.toggleStatus, null, { params: { [paramKey]: value }, skipErrorRedirect: [403] });
      loadList();
    } catch (err) {
      const status = err.response?.status;
      if (status === 403) {
        alert(`Cannot toggle status for ${value}. Permission denied.`);
      } else {
        alert(`Failed to toggle status for ${value}. Please try again.`);
      }
    }
  }
 
  function goToPage(pageIndex) {
    setPagination(prev => ({ ...prev, page: pageIndex }));
  }

  function handleSearchChange(e) {
    setSearchQuery(e.target.value);
    setPagination(prev => ({ ...prev, page: 0 }));
  }

  const currentPage = pagination.page;

  return (
    <div style={{ ...styles.page, left: isSidebarOpen ? "220px" : "55px" }}>
      <div style={styles.inner}>
        <div style={styles.topRow}>
          <button style={styles.backBtn} onClick={() => router.push("/home")}>⮜ Home</button>
          <h2 style={styles.title}>{title}</h2>
          <input
            style={searchInputSt}
            type="text"
            placeholder={`Search ${title}`}
            value={searchQuery}
            onChange={handleSearchChange}
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
 
          {!loadError && (
            <PaginationBar currentPage={currentPage} totalPages={totalPages} onPageChange={goToPage} />
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