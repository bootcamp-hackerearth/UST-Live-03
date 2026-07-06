"use client";

import { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import api from "./Axios";

export default function ListingSkeleton({ title, fields, fieldLabels, apis, addPath, editPathBase }) {
  const [data, setData] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [deleteTarget, setDeleteTarget] = useState(null);
  const router = useRouter();

  const [pagination, setPagination] = useState({
    page: 0,
    sizePerPage: 4,
    sortDirection: "ASC",
    sortField: "id",
  });

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearch(searchTerm);
      setPagination((prev) => ({ ...prev, page: 0 }));
    }, 300);
    return () => clearTimeout(handler);
  }, [searchTerm]);

  useEffect(() => {
    async function loadList() {
      try {
        const payload = {
          ...pagination,
          keyword: debouncedSearch.trim() || undefined,
        };
        const res = await api.post(apis.list, payload);

        if (Array.isArray(res.data)) {
          setData(res.data);
          setTotalPages(1);
        } else {
          setData(res.data.dtoList ?? []);
          setTotalPages(res.data.totalPages ?? 1);
        }
      } catch { }
    }
    loadList();
  }, [pagination, debouncedSearch, apis.list, title, router]);

  async function confirmDelete() {
    if (!deleteTarget) return;
    try {
      await api.delete(`${apis.delete}?identifier=${encodeURIComponent(deleteTarget)}`);
      setDeleteTarget(null);
      setPagination((prev) => ({ ...prev }));
    } catch { }
  }

  async function handleToggle(identifier) {
    try {
      await api.post(`${apis.toggleStatus}?identifier=${encodeURIComponent(identifier)}`);
      setPagination((prev) => ({ ...prev }));
    } catch { }
  }

  function goToPage(pageIndex) {
    setPagination((prev) => ({ ...prev, page: pageIndex }));
  }

  const currentPage = pagination.page;

  function getVisiblePages() {
    let start = currentPage - 1;
    if (start < 0) start = 0;
    if (start + 3 > totalPages) start = totalPages - 3;
    if (start < 0) start = 0;
    return Array.from({ length: Math.min(3, totalPages) }, (_, i) => start + i);
  }

  function getFieldLabel(f) {
    return fieldLabels?.[f] ?? (f.charAt(0).toUpperCase() + f.slice(1));
  }

  const tableHeaderStyle = "text-left p-3.5 border-b-2 border-solid border-gray-200 text-[12px] text-gray-500 font-bold uppercase tracking-wider bg-gray-50 sticky top-0 whitespace-nowrap";
  const actionButtonStyle = "px-3 py-1.25 rounded-md text-white border-none cursor-pointer text-xs font-semibold transition-colors";
  const pgnButtonStyle = "min-w-9 h-9 px-3 rounded-lg border-[1.5px] border-solid bg-white font-bold text-lg flex items-center justify-center transition-all";
  const fallbackTableRowStyle = "border-b border-solid border-gray-100 hover:bg-gray-50/50 transition-colors";
  const basicTextCellClass = "p-3.5 text-sm text-gray-800 whitespace-nowrap";

  return (
    <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-gray-50 font-sans flex flex-col overflow-hidden">
      <div className="flex-1 p-5 md:p-6 flex flex-col overflow-hidden">
        <div className="flex items-center mb-4 shrink-0 relative gap-3">
          <button
            className="px-4 py-2 bg-transparent text-brand border-[1.5px] border-solid border-brand rounded-lg text-xs font-semibold cursor-pointer shrink-0 transition-colors hover:bg-brand/5"
            onClick={() => router.push("/home")}
          >
            &larr; Home
          </button>

          <h2 className="absolute left-1/2 -translate-x-1/2 m-0 text-xl font-bold text-brand whitespace-nowrap">
            {title}
          </h2>

          <input
            type="text"
            placeholder="Search..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="ml-auto px-3.5 py-2 border-[1.5px] border-solid border-gray-300 rounded-lg text-xs outline-none bg-white w-[220px] focus:border-brand"
          />

          <button
            className="px-4.5 py-2.5 bg-brand text-white border-none rounded-lg cursor-pointer text-sm font-semibold shrink-0 transition-colors hover:bg-brand-hover"
            onClick={() => router.push(addPath)}
          >
            + Add {title}
          </button>
        </div>

        <div className="bg-white rounded-xl shadow-[0_2px_10px_rgba(0,0,0,0.06)] flex-1 overflow-hidden flex flex-col">
          <div className="overflow-y-auto flex-1">
            <table className="w-full border-collapse">
              <thead>
                <tr>
                  <th className={tableHeaderStyle}>Identifier</th>
                  {fields.map((f) => (
                    <th key={f} className={tableHeaderStyle}>
                      {getFieldLabel(f)}
                    </th>
                  ))}
                  <th className={tableHeaderStyle}>Status</th>
                  <th className={tableHeaderStyle}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {data.length === 0 ? (
                  <tr>
                    <td colSpan={fields.length + 3} className="text-center p-10 text-gray-400 text-sm">
                      No records found.
                    </td>
                  </tr>
                ) : (
                  data.map((row) => (
                    <tr key={row.identifier} className={fallbackTableRowStyle}>
                      <td className={basicTextCellClass}>{row.identifier}</td>
                      {fields.map((f) => (
                        <td className={basicTextCellClass} key={f}>
                          {Array.isArray(row[f]) ? row[f].join(", ") : row[f]}
                        </td>
                      ))}
                      <td className={basicTextCellClass}>
                        <button
                          className={`w-12 py-1.25 text-center text-white rounded-full cursor-pointer text-[12px] font-semibold select-none border-none transition-colors ${row.status ? "bg-brand" : "bg-gray-300"
                            }`}
                          onClick={() => handleToggle(row.identifier)}
                        >
                          {row.status ? "ON" : "OFF"}
                        </button>
                      </td>
                      <td className={basicTextCellClass}>
                        <button
                          className={`${actionButtonStyle} mr-2 bg-[#1e6091] hover:bg-[#1a527c]`}
                          onClick={() => router.push(editPathBase + row.identifier)}
                        >
                          Edit
                        </button>
                        <button
                          className={`${actionButtonStyle} bg-[#d62828] hover:bg-[#ba2222]`}
                          onClick={() => setDeleteTarget(row.identifier)}
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

          {totalPages > 1 && (
            <div className="flex items-center justify-center gap-1.5 p-4 border-t border-solid border-gray-200 bg-white shrink-0">
              <button
                className={`${pgnButtonStyle} ${currentPage === 0
                  ? "text-gray-300 border-gray-100 cursor-not-allowed"
                  : "text-brand border-gray-200 cursor-pointer hover:bg-gray-50"
                  }`}
                onClick={() => goToPage(currentPage - 1)}
                disabled={currentPage === 0}
              >
                &lsaquo;
              </button>

              {getVisiblePages().map((pageIndex) => (
                <button
                  key={pageIndex}
                  className={`min-w-9 h-9 px-2.5 rounded-lg border-[1.5px] border-solid text-xs font-semibold flex items-center justify-center cursor-pointer transition-all ${currentPage === pageIndex
                    ? "bg-brand border-brand text-white"
                    : "bg-white border-gray-200 text-gray-700 hover:bg-gray-50"
                    }`}
                  onClick={() => goToPage(pageIndex)}
                >
                  {pageIndex + 1}
                </button>
              ))}

              <button
                className={`${pgnButtonStyle} ${currentPage === totalPages - 1
                  ? "text-gray-300 border-gray-100 cursor-not-allowed"
                  : "text-brand border-gray-200 cursor-pointer hover:bg-gray-50"
                  }`}
                onClick={() => goToPage(currentPage + 1)}
                disabled={currentPage === totalPages - 1}
              >
                &rsaquo;
              </button>
              <span className="text-xs text-gray-400 px-2 whitespace-nowrap">
                Page {currentPage + 1} of {totalPages}
              </span>
            </div>
          )}
        </div>
      </div>

      {deleteTarget && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 font-sans">
          <div className="bg-white p-6 rounded-xl shadow-[0_4px_20px_rgba(0,0,0,0.15)] w-full max-w-[400px] text-center">
            <h3 className="m-0 mb-2.5 text-lg font-bold text-gray-800">Confirm Deletion</h3>
            <p className="m-0 mb-5 text-sm text-gray-500 leading-relaxed">
              Are you sure you want to delete <strong>{deleteTarget}</strong>? This action cannot be undone.
            </p>
            <div className="flex gap-3 justify-center">
              <button
                className="px-4.5 py-2.25 bg-gray-100 text-gray-600 border-none rounded-md cursor-pointer text-xs font-semibold hover:bg-gray-200 transition-colors"
                onClick={() => setDeleteTarget(null)}
              >
                Cancel
              </button>
              <button
                className="px-4.5 py-2.25 bg-[#d62828] text-white border-none rounded-md cursor-pointer text-xs font-semibold hover:bg-[#ba2222] transition-colors"
                onClick={confirmDelete}
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

ListingSkeleton.propTypes = {
  title: PropTypes.string,
  fields: PropTypes.arrayOf(PropTypes.string),
  fieldLabels: PropTypes.objectOf(PropTypes.string),
  apis: PropTypes.shape({
    list: PropTypes.string,
    delete: PropTypes.string,
    toggleStatus: PropTypes.string,
  }),
  addPath: PropTypes.string.isRequired,
  editPathBase: PropTypes.string,
};

ListingSkeleton.defaultProps = {
  title: "",
  fields: [],
  fieldLabels: {},
  apis: {},
  editPathBase: "",
};