"use client";
import { useEffect, useState, useCallback, useRef } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
import {
  useSidebarOpen,
  usePageNavigation,
} from "@/components/ListingShared";
import { HttpErrorPopup } from "@/components/sharedStyles";

const C = {
  navy: "#363955",
  mid: "#54668E",
  light: "#879EC6",
  text: "#1e2235",
  muted: "#6b7280",
  bg: "#f4f5f9",
  border: "#e4e6ef",
  rowHover: "#f8f9fc",
};

const th = {
  padding: "10px 14px",
  fontSize: "11px",
  fontWeight: "700",
  color: C.mid,
  textTransform: "uppercase",
  letterSpacing: "0.06em",
  textAlign: "left",
  whiteSpace: "nowrap",
  background: "#f4f5f9",
  borderBottom: `1.5px solid ${C.border}`,
};

const td = {
  padding: "10px 14px",
  fontSize: "13px",
  color: C.text,
  verticalAlign: "middle",
  borderBottom: `1px solid ${C.border}`,
  whiteSpace: "nowrap",
};

const getPgBtnColor = (disabled, active) => {
  if (active) return "#fff";
  if (disabled) return "#cbd5e1";
  return C.text;
};

const pgBtn = (disabled, active) => ({
  minWidth: "28px",
  height: "28px",
  padding: "0 6px",
  borderRadius: "5px",
  border: `1.5px solid ${active ? C.navy : C.border}`,
  background: active ? C.navy : "#fff",
  color: getPgBtnColor(disabled, active),
  fontSize: "12px",
  fontWeight: "600",
  cursor: disabled ? "not-allowed" : "pointer",
  opacity: disabled ? 0.5 : 1,
  transition: "all 0.12s",
});

function IconEdit() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
    </svg>
  );
}

function IconTrash() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="3 6 5 6 21 6" />
      <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
      <path d="M10 11v6M14 11v6" />
      <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" />
    </svg>
  );
}

function DeleteConfirmationModal({ isOpen, title, itemName, onConfirm, onCancel }) {
  const dialogRef = useRef(null);

  useEffect(() => {
    if (isOpen && dialogRef.current) {
      dialogRef.current.showModal();
    } else if (!isOpen && dialogRef.current) {
      dialogRef.current.close();
    }
  }, [isOpen]);

  const handleCancel = () => {
    dialogRef.current?.close();
    onCancel();
  };

  const handleConfirm = () => {
    dialogRef.current?.close();
    onConfirm();
  };

  return (
    <dialog
      ref={dialogRef}
      style={{
        borderRadius: "14px", border: `1.5px solid ${C.border}`,
        padding: "36px 40px", maxWidth: "420px", width: "90%",
        boxShadow: "0 12px 48px rgba(30,34,53,0.18)",
        animation: "slideUp 0.2s ease-out",
        textAlign: "center",
      }}
      onCancel={handleCancel}
    >
      <style>{`
        @keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
        @keyframes slideUp {
          from { opacity: 0; transform: translateY(16px) scale(0.98); }
          to   { opacity: 1; transform: translateY(0)    scale(1);    }
        }
        dialog::backdrop {
          background: rgba(30,34,53,0.5);
          backdrop-filter: blur(2px);
          animation: fadeIn 0.15s ease-out;
        }
      `}</style>

      <div style={{
        width: "58px", height: "58px", borderRadius: "50%",
        background: "#fef2f2", border: "1.5px solid #fecaca",
        display: "flex", alignItems: "center", justifyContent: "center",
        margin: "0 auto 20px",
      }}>
        <svg width="26" height="26" viewBox="0 0 24 24" fill="none"
          stroke="#dc2626" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
          <polyline points="3 6 5 6 21 6" />
          <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
          <path d="M10 11v6M14 11v6" />
          <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2" />
        </svg>
      </div>

      <h3 style={{ margin: "0 0 10px", fontSize: "17px", fontWeight: "700", color: C.navy }}>
        Delete {title}?
      </h3>

      <p style={{ margin: "0 0 28px", fontSize: "13.5px", color: C.muted, lineHeight: 1.65 }}>
        You are about to permanently delete{" "}
        <span style={{ fontWeight: "700", color: C.text }}>
          &ldquo;{itemName}&rdquo;
        </span>
        .<br />This action <span style={{ color: "#dc2626", fontWeight: "600" }}>cannot be undone</span>.
      </p>

      <div style={{ display: "flex", gap: "10px" }}>
        <button
          onClick={handleCancel}
          style={{
            flex: 1, padding: "9px 0", borderRadius: "8px",
            border: `1.5px solid ${C.border}`, background: "#ffffff",
            color: C.text, fontSize: "13px", fontWeight: "600", cursor: "pointer",
            transition: "background 0.12s",
          }}
          onMouseEnter={(e) => { e.currentTarget.style.background = "#f3f4f6"; }}
          onMouseLeave={(e) => { e.currentTarget.style.background = "#ffffff"; }}
        >
          Cancel
        </button>
        <button
          onClick={handleConfirm}
          style={{
            flex: 1, padding: "9px 0", borderRadius: "8px",
            border: "none", background: "#dc2626",
            color: "#ffffff", fontSize: "13px", fontWeight: "700", cursor: "pointer",
            transition: "background 0.12s",
          }}
          onMouseEnter={(e) => { e.currentTarget.style.background = "#b91c1c"; }}
          onMouseLeave={(e) => { e.currentTarget.style.background = "#dc2626"; }}
        >
          Yes, Delete
        </button>
      </div>
    </dialog>
  );
}

DeleteConfirmationModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  title: PropTypes.string.isRequired,
  itemName: PropTypes.string.isRequired,
  onConfirm: PropTypes.func.isRequired,
  onCancel: PropTypes.func.isRequired,
};

export default function ListingSkeleton({
  title,
  fields,
  apis,
  addPath,
  editPathBase,
  paramKey = "identifier",
  identifierLabel = "Identifier",
  deleteStyle = "path",
}) {
  const router = useRouter();
  const isSidebarOpen = useSidebarOpen();

  const [data, setData] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalRecords, setTotalRecords] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [pagination, setPagination] = useState({
    page: 0, sizePerPage: 5, sortDirection: "ASC", sortField: "id",
  });

  const [deleteModal, setDeleteModal] = useState({
    isOpen: false,
    itemName: "",
    itemData: null,
  });

  const [httpError, setHttpError] = useState(null);

  const { currentPage, goToPage, getVisiblePages } = usePageNavigation(pagination, setPagination);

  const loadList = useCallback(async () => {
    try {
      if (searchTerm.trim()) {
        const res = await api.post(apis.list, { ...pagination, page: 0, sizePerPage: 1000 });
        const allData = Array.isArray(res.data) ? res.data : res.data.dtoList ?? [];
        const escaped = searchTerm.replaceAll(/[.*+?^${}()|[\]\\]/g, String.raw`\$&`);
        const regex = new RegExp(String.raw`\b${escaped}`, "i");
        const filtered = allData.filter((item) => {
          const idMatch = regex.test(String(item[paramKey] ?? ""));
          const fieldMatch = fields.some((f) => {
            const v = item[f];
            return regex.test(Array.isArray(v) ? v.join(" ") : String(v ?? ""));
          });
          return idMatch || fieldMatch;
        });
        setData(filtered);
        setTotalPages(1);
        setTotalRecords(filtered.length);
      } else {
        const res = await api.post(apis.list, pagination);
        if (Array.isArray(res.data)) {
          setData(res.data);
          setTotalPages(1);
          setTotalRecords(res.data.length);
        } else {
          setData(res.data.dtoList ?? []);
          setTotalPages(res.data.totalPages ?? 1);
          setTotalRecords(
            res.data.totalElements ??
            res.data.totalRecords ??
            (res.data.totalPages ?? 1) * pagination.sizePerPage
          );
        }
      }
    } catch (err) {
      if (process.env.NODE_ENV !== "production") console.log(err);
      const status = err.response?.status;
      if ([400, 403, 404, 500].includes(status)) {
        setHttpError({ statusCode: status, message: err.response?.data?.message || null });
      }
    }
  }, [apis.list, pagination, searchTerm, fields, paramKey]);

  useEffect(() => { loadList(); }, [loadList]);

  function openDeleteModal(row) {
    const nameField = fields[0];
    const displayName =
      nameField && row[nameField]
        ? String(row[nameField])
        : String(row[paramKey]);
    setDeleteModal({ isOpen: true, itemName: displayName, itemData: row });
  }

  function closeDeleteModal() {
    setDeleteModal({ isOpen: false, itemName: "", itemData: null });
  }

  async function confirmDelete() {
    if (!deleteModal.itemData) return;
    try {
      const value = deleteModal.itemData[paramKey];
      const url =
        deleteStyle === "param"
          ? `${apis.delete}?${paramKey}=${value}`
          : `${apis.delete}/${value}`;
      await api.delete(url);
      closeDeleteModal();
      loadList();
    } catch (err) {
      if (process.env.NODE_ENV !== "production") console.log(err);
      closeDeleteModal();
      const status = err.response?.status;
      if ([400, 403, 404, 500].includes(status)) {
        setHttpError({ statusCode: status, message: err.response?.data?.message || null });
      }
    }
  }

  async function handleToggle(row) {
    try {
      await api.post(`${apis.toggleStatus}?${paramKey}=${row[paramKey]}`);
      loadList();
    } catch (err) {
      if (process.env.NODE_ENV !== "production") console.log(err);
      const status = err.response?.status;
      if ([400, 403, 404, 500].includes(status)) {
        setHttpError({ statusCode: status, message: err.response?.data?.message || null });
      }
    }
  }

  const visiblePages = getVisiblePages(totalPages);

  const rangeFrom = totalRecords === 0 ? 0 : currentPage * pagination.sizePerPage + 1;
  const rangeTo = Math.min((currentPage + 1) * pagination.sizePerPage, totalRecords);

  return (
    <>
      <DeleteConfirmationModal
        isOpen={deleteModal.isOpen}
        title={title}
        itemName={deleteModal.itemName}
        onConfirm={confirmDelete}
        onCancel={closeDeleteModal}
      />

      <HttpErrorPopup
        httpError={httpError}
        onClose={() => {
          setHttpError(null);
          loadList();
        }}
      />

      <div style={{
        position: "fixed", top: "60px", right: 0, bottom: 0,
        left: isSidebarOpen ? "220px" : "55px",
        backgroundColor: C.bg, fontFamily: "'Segoe UI', sans-serif",
        display: "flex", flexDirection: "column",
        overflow: "hidden", transition: "left 0.2s ease",
      }}>
        <div style={{
          background: "#ffffff", padding: "12px 24px",
          display: "flex", alignItems: "center", justifyContent: "space-between",
          flexShrink: 0, borderBottom: `1.5px solid ${C.border}`,
        }}>
          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <button
              onClick={() => router.push("/home")}
              style={{
                background: "#ffffff", border: `1.5px solid ${C.mid}`,
                color: C.mid, borderRadius: "6px",
                padding: "5px 12px", fontSize: "12px", fontWeight: "600", cursor: "pointer",
              }}
            >
              ← Home
            </button>
            <div style={{ width: "1px", height: "20px", background: C.border }} />
            <h2 style={{ margin: 0, fontSize: "16px", fontWeight: "700", color: C.navy }}>
              {title}
            </h2>
          </div>

          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
            <input
              type="text"
              placeholder={`Search ${title}...`}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{
                padding: "6px 12px", background: "#f7f8fc",
                border: `1.5px solid ${C.border}`, borderRadius: "6px",
                fontSize: "12px", color: C.text, outline: "none", width: "220px",
              }}
            />
            <button
              onClick={() => router.push(addPath)}
              style={{
                padding: "6px 16px",
                background: `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
                color: "#fff", border: "none", borderRadius: "6px",
                fontSize: "12px", fontWeight: "700", cursor: "pointer",
                boxShadow: "0 2px 6px rgba(54,57,85,0.20)", whiteSpace: "nowrap",
              }}
            >
              + Add {title}
            </button>
          </div>
        </div>

        <div style={{
          flex: 1, overflow: "auto", padding: "20px 24px", display: "flex", flexDirection: "column",
        }}>
          <div style={{
            background: "#ffffff", borderRadius: "10px",
            boxShadow: "0 1px 8px rgba(54,57,85,0.07)",
            border: `1.5px solid ${C.border}`,
            overflow: "hidden", display: "flex", flexDirection: "column", flex: 1,
          }}>
            <div style={{ overflowX: "auto", flex: 1 }}>
              <table style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead>
                  <tr>
                    <th style={th}>{identifierLabel}</th>
                    {fields.map((f) => (
                      <th key={f} style={th}>
                        {f.charAt(0).toUpperCase() + f.slice(1)}
                      </th>
                    ))}
                    <th style={th}>Status</th>
                    <th style={{ ...th, textAlign: "center" }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.length === 0 ? (
                    <tr>
                      <td colSpan={fields.length + 3} style={{
                        textAlign: "center", padding: "56px", color: C.light, fontSize: "13px",
                      }}>
                        No records found.
                      </td>
                    </tr>
                  ) : (
                    data.map((row) => (
                      <tr
                        key={row[paramKey]}
                        style={{ background: "#ffffff", transition: "background 0.1s" }}
                        onMouseEnter={(e) => { e.currentTarget.style.background = C.rowHover; }}
                        onMouseLeave={(e) => { e.currentTarget.style.background = "#ffffff"; }}
                      >
                        <td style={td}>
                          <span style={{ fontWeight: "700", color: C.navy, fontSize: "13px" }}>
                            {row[paramKey]}
                          </span>
                        </td>
                        {fields.map((f) => (
                          <td key={f} style={td}>
                            {Array.isArray(row[f]) ? row[f].join(", ") : row[f]}
                          </td>
                        ))}

                        <td style={td}>
                          <button
                            onClick={() => handleToggle(row)}
                            style={{
                              padding: "3px 12px", borderRadius: "20px", border: "none",
                              fontSize: "11px", fontWeight: "700", cursor: "pointer",
                              background: row.status
                                ? `linear-gradient(135deg, ${C.navy}, ${C.mid})`
                                : "#e5e7eb",
                              color: row.status ? "#fff" : "#9ca3af",
                            }}
                          >
                            {row.status ? "Active" : "Inactive"}
                          </button>
                        </td>

                        <td style={{ ...td, textAlign: "center" }}>
                          <div style={{
                            display: "inline-flex", alignItems: "center",
                            justifyContent: "center", gap: "6px",
                          }}>
                            <button
                              onClick={() => router.push(editPathBase + row[paramKey])}
                              title={`Edit ${title}`}
                              style={{
                                display: "inline-flex", alignItems: "center", gap: "5px",
                                padding: "5px 11px", borderRadius: "6px",
                                background: "#ffffff", border: `1.5px solid ${C.mid}`,
                                color: C.mid, fontSize: "12px", fontWeight: "600", cursor: "pointer",
                              }}
                            >
                              <IconEdit /> Edit
                            </button>

                            <button
                              onClick={() => openDeleteModal(row)}
                              title={`Delete ${title}`}
                              style={{
                                display: "inline-flex", alignItems: "center", justifyContent: "center",
                                width: "30px", height: "30px", borderRadius: "6px",
                                background: "#fff2f2", border: "1.5px solid #fca5a5",
                                color: "#dc2626", cursor: "pointer",
                                transition: "background 0.12s, border-color 0.12s",
                              }}
                              onMouseEnter={(e) => {
                                e.currentTarget.style.background = "#fee2e2";
                                e.currentTarget.style.borderColor = "#f87171";
                              }}
                              onMouseLeave={(e) => {
                                e.currentTarget.style.background = "#fff2f2";
                                e.currentTarget.style.borderColor = "#fca5a5";
                              }}
                            >
                              <IconTrash />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
            {totalRecords > 0 && (
              <div style={{
                display: "flex", alignItems: "center", justifyContent: "space-between",
                gap: "4px", padding: "10px 20px",
                borderTop: `1.5px solid ${C.border}`, background: "#ffffff", flexShrink: 0,
              }}>
                <span style={{ fontSize: "12px", color: C.muted }}>
                  Showing{" "}
                  <span style={{ fontWeight: "700", color: C.navy }}>{rangeFrom}</span>
                  {" "}–{" "}
                  <span style={{ fontWeight: "700", color: C.navy }}>{rangeTo}</span>
                  {" "}of{" "}
                  <span style={{ fontWeight: "700", color: C.navy }}>{totalRecords}</span>
                  {" "}entries
                </span>

                {searchTerm.trim() === "" && totalPages > 1 && visiblePages.length > 0 && (
                  <div style={{ display: "flex", alignItems: "center", gap: "4px" }}>
                    <button onClick={() => goToPage(currentPage - 1)} disabled={currentPage === 0}
                      style={pgBtn(currentPage === 0, false)}>‹</button>
                    {visiblePages.map((pageIndex) => (
                      <button key={`page-${pageIndex}`} onClick={() => goToPage(pageIndex)}
                        style={pgBtn(false, currentPage === pageIndex)}>
                        {pageIndex + 1}
                      </button>
                    ))}
                    <button onClick={() => goToPage(currentPage + 1)} disabled={currentPage === totalPages - 1}
                      style={pgBtn(currentPage === totalPages - 1, false)}>›</button>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </>
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
  deleteStyle: PropTypes.string,
};      