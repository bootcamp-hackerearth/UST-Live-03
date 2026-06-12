"use client";
import { useEffect, useState, useCallback } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
import {
  useSidebarOpen,
  usePageNavigation,
  thStyle, tdStyle, paginationBtn,
} from "@/components/ListingShared";

const C = {
  navy: "#363955", mid: "#54668E", light: "#879EC6",
  text: "#1e2235", muted: "#6b7280",
};

export default function ListingSkeleton({
  title, fields, apis, addPath, editPathBase,
  paramKey = "identifier", identifierLabel = "Identifier", deleteStyle = "path",
}) {
  const router = useRouter();
  const isSidebarOpen = useSidebarOpen(); 

  const [data, setData]             = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [pagination, setPagination] = useState({
    page: 0, sizePerPage: 5, sortDirection: "ASC", sortField: "id",
  });

  const { currentPage, goToPage, getVisiblePages } = usePageNavigation(pagination, setPagination);

  const loadList = useCallback(async () => {
    try {
      if (searchTerm.trim()) {
        const res = await api.post(apis.list, { ...pagination, page: 0, sizePerPage: 1000 });
        const allData = Array.isArray(res.data) ? res.data : (res.data.dtoList ?? []);
        const escapedSearch = searchTerm.replaceAll(/[.*+?^${}()|[\]\\]/g, String.raw`\$&`);
        const regex = new RegExp(String.raw`\b${escapedSearch}`, "i");
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

  useEffect(() => { loadList(); }, [loadList]);

  async function handleDelete(row) {
    const value = row[paramKey];
    const { showConfirm } = await import("@/utils/browser");
    if (!showConfirm(`Delete "${value}"? This cannot be undone.`)) return;
    const url = deleteStyle === "param"
      ? `${apis.delete}?${paramKey}=${value}`
      : `${apis.delete}/${value}`;
    await api.get(url);
    loadList();
  }

  async function handleToggle(row) {
    await api.post(`${apis.toggleStatus}?${paramKey}=${row[paramKey]}`);
    loadList();
  }

  const visiblePages = getVisiblePages(totalPages);

  return (
    <div style={{
      position: "fixed", top: "60px", right: 0, bottom: 0,
      left: isSidebarOpen ? "220px" : "55px",
      backgroundColor: "#ffffff", fontFamily: "'Segoe UI', sans-serif",
      display: "flex", flexDirection: "column",
      overflow: "hidden", transition: "left 0.2s ease",
    }}>
      {/* ── Toolbar ── */}
      <div style={{
        background: "#ffffff", padding: "16px 28px",
        display: "flex", alignItems: "center", justifyContent: "space-between",
        flexShrink: 0, borderBottom: "1.5px solid #e8eaf0",
      }}>
        <div style={{ display: "flex", alignItems: "center", gap: "14px" }}>
          <button
            onClick={() => router.push("/home")}
            style={{
              background: "#ffffff", border: `1.5px solid ${C.mid}`,
              color: C.mid, borderRadius: "7px",
              padding: "5px 14px", fontSize: "12px", fontWeight: "600", cursor: "pointer",
            }}
          >
            ← Home
          </button>
          <div style={{ width: "1px", height: "22px", background: "#e8eaf0" }} />
          <h2 style={{ margin: 0, fontSize: "18px", fontWeight: "700", color: C.navy, letterSpacing: "0.1px" }}>
            {title}
          </h2>
        </div>
        <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
          <input
            type="text"
            placeholder={`Search ${title}...`}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{
              padding: "7px 14px", background: "#f7f8fc",
              border: "1.5px solid #e8eaf0", borderRadius: "7px",
              fontSize: "13px", color: C.text, outline: "none", width: "240px",
            }}
          />
          <button
            onClick={() => router.push(addPath)}
            style={{
              padding: "7px 20px",
              background: `linear-gradient(135deg, ${C.navy}, ${C.mid})`,
              color: "#fff", border: "none", borderRadius: "7px",
              fontSize: "13px", fontWeight: "700", cursor: "pointer",
              boxShadow: "0 2px 8px rgba(54,57,85,0.20)",
            }}
          >
            + Add {title}
          </button>
        </div>
      </div>

      <div style={{ flex: 1, overflow: "auto", padding: "24px 32px", display: "flex", flexDirection: "column" }}>
        <div style={{
          flex: 1, display: "flex", flexDirection: "column",
          background: "#ffffff", borderRadius: "12px",
          boxShadow: "0 1px 12px rgba(54,57,85,0.07)",
          border: "1.5px solid #e8eaf0", overflow: "hidden",
        }}>
          <div style={{ flex: 1, overflowY: "auto" }}>
            <table style={{ width: "100%", borderCollapse: "collapse" }}>
              <thead>
                <tr style={{ background: "#f7f8fc" }}>
                  <th style={thStyle}>{identifierLabel}</th>
                  {fields.map((f) => (
                    <th key={f} style={thStyle}>{f.charAt(0).toUpperCase() + f.slice(1)}</th>
                  ))}
                  <th style={thStyle}>Status</th>
                  <th style={{ ...thStyle, textAlign: "right" }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {data.length === 0 ? (
                  <tr>
                    <td colSpan={fields.length + 3} style={{ textAlign: "center", padding: "64px", color: C.light, fontSize: "13px" }}>
                      No records found.
                    </td>
                  </tr>
                ) : (
                  data.map((row) => (
                    <tr
                      key={row[paramKey]}
                      style={{ borderBottom: "1px solid #f0f1f6", background: "#ffffff", transition: "background 0.12s" }}
                      onMouseEnter={(e) => { e.currentTarget.style.background = "#f7f8fc"; }}
                      onMouseLeave={(e) => { e.currentTarget.style.background = "#ffffff"; }}
                    >
                      <td style={tdStyle}>
                        <span style={{ fontWeight: "600", color: C.navy, fontSize: "13px" }}>{row[paramKey]}</span>
                      </td>
                      {fields.map((f) => (
                        <td key={f} style={tdStyle}>
                          {Array.isArray(row[f]) ? row[f].join(", ") : row[f]}
                        </td>
                      ))}
                      <td style={tdStyle}>
                        <button
                          onClick={() => handleToggle(row)}
                          style={{
                            padding: "4px 14px", borderRadius: "20px", border: "none",
                            fontSize: "11px", fontWeight: "700", cursor: "pointer",
                            background: row.status ? `linear-gradient(135deg, ${C.navy}, ${C.mid})` : "#e5e7eb",
                            color: row.status ? "#fff" : "#9ca3af",
                          }}
                        >
                          {row.status ? "Active" : "Inactive"}
                        </button>
                      </td>
                      <td style={{ ...tdStyle, textAlign: "right" }}>
                        <button
                          onClick={() => router.push(editPathBase + row[paramKey])}
                          style={{
                            marginRight: "8px", padding: "5px 16px", borderRadius: "6px",
                            background: "#ffffff", border: `1.5px solid ${C.mid}`,
                            color: C.mid, fontSize: "12px", fontWeight: "600", cursor: "pointer",
                          }}
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleDelete(row)}
                          style={{
                            padding: "5px 16px", borderRadius: "6px",
                            background: "#ffffff", border: "1.5px solid #dc2626",
                            color: "#dc2626", fontSize: "12px", fontWeight: "600", cursor: "pointer",
                          }}
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          {searchTerm.trim() === "" && totalPages > 1 && visiblePages.length > 0 && (
            <div style={{
              display: "flex", alignItems: "center", justifyContent: "flex-end",
              gap: "6px", padding: "12px 24px",
              borderTop: "1.5px solid #e8eaf0", background: "#ffffff", flexShrink: 0,
            }}>
              <span style={{ fontSize: "12px", color: C.muted, marginRight: "8px" }}>
                Page {currentPage + 1} of {totalPages}
              </span>
              <button onClick={() => goToPage(currentPage - 1)} disabled={currentPage === 0} style={paginationBtn(currentPage === 0, false)}>‹</button>
              {visiblePages.map((pageIndex) => (
                <button key={`page-${pageIndex}`} onClick={() => goToPage(pageIndex)} style={paginationBtn(false, currentPage === pageIndex)}>
                  {pageIndex + 1}
                </button>
              ))}
              <button onClick={() => goToPage(currentPage + 1)} disabled={currentPage === totalPages - 1} style={paginationBtn(currentPage === totalPages - 1, false)}>›</button>
            </div>
          )}
        </div>
      </div>
    </div>
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