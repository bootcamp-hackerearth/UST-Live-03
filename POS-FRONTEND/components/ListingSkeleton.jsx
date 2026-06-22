"use client";
import { useEffect, useState, useCallback } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
import {
  useSidebarOpen,
  usePageNavigation,
} from "@/components/ListingShared";

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

/* ── Shared cell styles ── */
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

/* ── Action buttons ── */
const actionBtn = (variant) => ({
  display: "inline-flex",
  alignItems: "center",
  justifyContent: "center",
  gap: "4px",
  padding: variant === "delete" ? "6px 8px" : "6px 12px",
  borderRadius: "5px",
  fontSize: variant === "delete" ? "13px" : "12px",
  fontWeight: "600",
  cursor: "pointer",
  lineHeight: 1.4,
  transition: "all 0.12s",
  minWidth: variant === "delete" ? "auto" : "auto",
  ...(variant === "edit"
    ? {
      background: "#ffffff",
      border: `1.5px solid ${C.mid}`,
      color: C.mid,
    }
    : {
      background: "#ffffff",
      border: "1.5px solid #dc2626",
      color: "#dc2626",
      padding: "6px 8px",
    }),
});

/* ── Pagination button ── */
const pgBtn = (disabled, active) => ({
  minWidth: "28px",
  height: "28px",
  padding: "0 6px",
  borderRadius: "5px",
  border: `1.5px solid ${active ? C.navy : C.border}`,
  background: active ? C.navy : "#fff",
  color: active ? "#fff" : disabled ? "#cbd5e1" : C.text,
  fontSize: "12px",
  fontWeight: "600",
  cursor: disabled ? "not-allowed" : "pointer",
  opacity: disabled ? 0.5 : 1,
  transition: "all 0.12s",
});

/* ── Delete Confirmation Modal ── */
function DeleteConfirmationModal({ isOpen, itemName, onConfirm, onCancel }) {
  if (!isOpen) return null;

  return (
    <div
      style={{
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: "rgba(0, 0, 0, 0.5)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        zIndex: 9999,
        animation: "fadeIn 0.2s ease-out",
      }}
      onClick={onCancel}
    >
      <style>{`
        @keyframes fadeIn {
          from { opacity: 0; }
          to { opacity: 1; }
        }
        @keyframes slideUp {
          from {
            opacity: 0;
            transform: translateY(20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }
      `}</style>
      <div
        style={{
          background: "#ffffff",
          borderRadius: "12px",
          padding: "32px 36px",
          maxWidth: "420px",
          width: "90%",
          boxShadow: "0 10px 40px rgba(54, 57, 85, 0.15)",
          border: `1.5px solid ${C.border}`,
          animation: "slideUp 0.3s ease-out",
        }}
        onClick={(e) => e.stopPropagation()}
      >
        

        {/* Title */}
        <h3
          style={{
            margin: "0 0 8px 0",
            fontSize: "18px",
            fontWeight: "700",
            color: C.navy,
          }}
        >
          Delete Item
        </h3>

        {/* Message */}
        <p
          style={{
            margin: "0 0 24px 0",
            fontSize: "14px",
            color: C.muted,
            lineHeight: 1.6,
          }}
        >
          Are you sure you want to delete{" "}
          <span style={{ fontWeight: "700", color: C.text }}>"{itemName}"</span>
          ? This action cannot be undone.
        </p>

        {/* Buttons */}
        <div
          style={{
            display: "flex",
            gap: "10px",
            justifyContent: "flex-end",
          }}
        >
          <button
            onClick={onCancel}
            style={{
              padding: "8px 20px",
              background: "#f3f4f6",
              border: `1.5px solid ${C.border}`,
              borderRadius: "6px",
              fontSize: "13px",
              fontWeight: "600",
              color: C.text,
              cursor: "pointer",
              transition: "all 0.12s",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = "#e5e7eb";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = "#f3f4f6";
            }}
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            style={{
              padding: "8px 20px",
              background: "#dc2626",
              border: "1.5px solid #dc2626",
              borderRadius: "6px",
              fontSize: "13px",
              fontWeight: "600",
              color: "#ffffff",
              cursor: "pointer",
              transition: "all 0.12s",
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.background = "#b91c1c";
              e.currentTarget.style.borderColor = "#b91c1c";
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.background = "#dc2626";
              e.currentTarget.style.borderColor = "#dc2626";
            }}
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
}

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
  const [searchTerm, setSearchTerm] = useState("");
  const [pagination, setPagination] = useState({
    page: 0,
    sizePerPage: 5,
    sortDirection: "ASC",
    sortField: "id",
  });

  const [deleteModal, setDeleteModal] = useState({
    isOpen: false,
    itemName: "",
    itemData: null,
  });

  const { currentPage, goToPage, getVisiblePages } = usePageNavigation(
    pagination,
    setPagination
  );

  const loadList = useCallback(async () => {
    try {
      if (searchTerm.trim()) {
        const res = await api.post(apis.list, {
          ...pagination,
          page: 0,
          sizePerPage: 1000,
        });
        const allData = Array.isArray(res.data)
          ? res.data
          : res.data.dtoList ?? [];
        const escaped = searchTerm.replaceAll(
          /[.*+?^${}()|[\]\\]/g,
          String.raw`\$&`
        );
        const regex = new RegExp(String.raw`\b${escaped}`, "i");
        const filtered = allData.filter((item) => {
          const idMatch = regex.test(String(item[paramKey] ?? ""));
          const fieldMatch = fields.some((f) => {
            const v = item[f];
            return regex.test(
              Array.isArray(v) ? v.join(" ") : String(v ?? "")
            );
          });
          return idMatch || fieldMatch;
        });
        setData(filtered);
        setTotalPages(1);
      } else {
        const res = await api.post(apis.list, pagination);
        if (Array.isArray(res.data)) {
          setData(res.data);
          setTotalPages(1);
        } else {
          setData(res.data.dtoList ?? []);
          setTotalPages(res.data.totalPages ?? 1);
        }
      }
    } catch (err) {
      if (process.env.NODE_ENV !== "production") console.log(err);
    }
  }, [apis.list, pagination, searchTerm, fields, paramKey]);

  useEffect(() => {
    loadList();
  }, [loadList]);

  function openDeleteModal(row) {
    setDeleteModal({
      isOpen: true,
      itemName: row[paramKey],
      itemData: row,
    });
  }

  function closeDeleteModal() {
    setDeleteModal({
      isOpen: false,
      itemName: "",
      itemData: null,
    });
  }

  async function confirmDelete() {
    if (!deleteModal.itemData) return;

    try {
      const row = deleteModal.itemData;
      const value = row[paramKey];
      const url =
        deleteStyle === "param"
          ? `${apis.delete}?${paramKey}=${value}`
          : `${apis.delete}/${value}`;
      await api.get(url);
      closeDeleteModal();
      loadList();
    } catch (err) {
      if (process.env.NODE_ENV !== "production") console.log(err);
      closeDeleteModal();
    }
  }

  async function handleToggle(row) {
    await api.post(`${apis.toggleStatus}?${paramKey}=${row[paramKey]}`);
    loadList();
  }

  const visiblePages = getVisiblePages(totalPages);

  return (
    <>
      <div
        style={{
          position: "fixed",
          top: "60px",
          right: 0,
          bottom: 0,
          left: isSidebarOpen ? "220px" : "55px",
          backgroundColor: C.bg,
          fontFamily: "'Segoe UI', sans-serif",
          display: "flex",
          flexDirection: "column",
          overflow: "hidden",
          transition: "left 0.2s ease",
        }}
      >
        {/* ── Toolbar ── */}
        <div
          style={{
            background: "#ffffff",
            padding: "12px 24px",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            flexShrink: 0,
            borderBottom: `1.5px solid ${C.border}`,
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
            <button
              onClick={() => router.push("/home")}
              style={{
                background: "#ffffff",
                border: `1.5px solid ${C.mid}`,
                color: C.mid,
                borderRadius: "6px",
                padding: "5px 12px",
                fontSize: "12px",
                fontWeight: "600",
                cursor: "pointer",
              }}
            >
              ← Home
            </button>
            <div style={{ width: "1px", height: "20px", background: C.border }} />
            <h2
              style={{
                margin: 0,
                fontSize: "16px",
                fontWeight: "700",
                color: C.navy,
              }}
            >
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
                padding: "6px 12px",
                background: "#f7f8fc",
                border: `1.5px solid ${C.border}`,
                borderRadius: "6px",
                fontSize: "12px",
                color: C.text,
                outline: "none",
                width: "220px",
              }}
            />
            <button
              onClick={() => router.push(addPath)}
              style={{
                padding: "6px 16px",
                background: `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
                color: "#fff",
                border: "none",
                borderRadius: "6px",
                fontSize: "12px",
                fontWeight: "700",
                cursor: "pointer",
                boxShadow: "0 2px 6px rgba(54,57,85,0.20)",
                whiteSpace: "nowrap",
              }}
            >
              + Add {title}
            </button>
          </div>
        </div>

        {/* ── Content ── */}
        <div
          style={{
            flex: 1,
            overflow: "auto",
            padding: "20px 24px",
            display: "flex",
            flexDirection: "column",
          }}
        >
          <div
            style={{
              background: "#ffffff",
              borderRadius: "10px",
              boxShadow: "0 1px 8px rgba(54,57,85,0.07)",
              border: `1.5px solid ${C.border}`,
              overflow: "hidden",
              display: "flex",
              flexDirection: "column",
              flex: 1,
            }}
          >
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
                      <td
                        colSpan={fields.length + 3}
                        style={{
                          textAlign: "center",
                          padding: "56px",
                          color: C.light,
                          fontSize: "13px",
                        }}
                      >
                        No records found.
                      </td>
                    </tr>
                  ) : (
                    data.map((row) => (
                      <tr
                        key={row[paramKey]}
                        style={{ background: "#ffffff", transition: "background 0.1s" }}
                        onMouseEnter={(e) => {
                          e.currentTarget.style.background = C.rowHover;
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.background = "#ffffff";
                        }}
                      >
                        <td style={td}>
                          <span
                            style={{
                              fontWeight: "700",
                              color: C.navy,
                              fontSize: "13px",
                            }}
                          >
                            {row[paramKey]}
                          </span>
                        </td>
                        {fields.map((f) => (
                          <td key={f} style={td}>
                            {Array.isArray(row[f])
                              ? row[f].join(", ")
                              : row[f]}
                          </td>
                        ))}

                        {/* Status toggle */}
                        <td style={td}>
                          <button
                            onClick={() => handleToggle(row)}
                            style={{
                              padding: "3px 12px",
                              borderRadius: "20px",
                              border: "none",
                              fontSize: "11px",
                              fontWeight: "700",
                              cursor: "pointer",
                              background: row.status
                                ? `linear-gradient(135deg, ${C.navy}, ${C.mid})`
                                : "#e5e7eb",
                              color: row.status ? "#fff" : "#9ca3af",
                            }}
                          >
                            {row.status ? "Active" : "Inactive"}
                          </button>
                        </td>

                        {/* Actions — compact inline with improved alignment */}
                        <td style={{ ...td, textAlign: "center" }}>
                          <div
                            style={{
                              display: "inline-flex",
                              alignItems: "center",
                              justifyContent: "center",
                              gap: "8px",
                            }}
                          >
                            <button
                              onClick={() =>
                                router.push(editPathBase + row[paramKey])
                              }
                              style={actionBtn("edit")}
                              title="Edit"
                            >
                              ✎
                            </button>
                            <button
                              onClick={() => openDeleteModal(row)}
                              style={actionBtn("delete")}
                              title="Delete"
                            >
                              🗑️
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {searchTerm.trim() === "" &&
              totalPages > 1 &&
              visiblePages.length > 0 && (
                <div
                  style={{
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "flex-end",
                    gap: "4px",
                    padding: "10px 20px",
                    borderTop: `1.5px solid ${C.border}`,
                    background: "#ffffff",
                    flexShrink: 0,
                  }}
                >
                  <span
                    style={{
                      fontSize: "12px",
                      color: C.muted,
                      marginRight: "8px",
                    }}
                  >
                    Page {currentPage + 1} of {totalPages}
                  </span>
                  <button
                    onClick={() => goToPage(currentPage - 1)}
                    disabled={currentPage === 0}
                    style={pgBtn(currentPage === 0, false)}
                  >
                    ‹
                  </button>
                  {visiblePages.map((pageIndex) => (
                    <button
                      key={`page-${pageIndex}`}
                      onClick={() => goToPage(pageIndex)}
                      style={pgBtn(false, currentPage === pageIndex)}
                    >
                      {pageIndex + 1}
                    </button>
                  ))}
                  <button
                    onClick={() => goToPage(currentPage + 1)}
                    disabled={currentPage === totalPages - 1}
                    style={pgBtn(currentPage === totalPages - 1, false)}
                  >
                    ›
                  </button>
                </div>
              )}
          </div>
        </div>
      </div>

      {/* Delete Confirmation Modal */}
      <DeleteConfirmationModal
        isOpen={deleteModal.isOpen}
        itemName={deleteModal.itemName}
        onConfirm={confirmDelete}
        onCancel={closeDeleteModal}
      />
    </>
  );
}

ListingSkeleton.propTypes = {
  title: PropTypes.string.isRequired,
  fields: PropTypes.array.isRequired,
  apis: PropTypes.object.isRequired,
  addPath: PropTypes.string.isRequired,
  editPathBase: PropTypes.string.isRequired,
  paramKey: PropTypes.string,
  identifierLabel: PropTypes.string,
  deleteStyle: PropTypes.string,
};