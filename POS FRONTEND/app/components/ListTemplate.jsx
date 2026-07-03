"use client";
import PropTypes from "prop-types";
import { useEffect, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import axiosInstance from "../api/axiosInstance";

function renderCell(col, item, showStatus, toggleStatus, statusKey) {
  if (col.field === "status" && showStatus) {
    return (
      <button
        onClick={() => toggleStatus(item[statusKey], !item.status)}
        className={`inline-flex rounded-full px-3 py-1 text-xs font-semibold text-white ${
          item.status ? "bg-emerald-500" : "bg-rose-500"
        }`}
      >
        {item.status ? "Active" : "Inactive"}
      </button>
    );
  }
  if (col.render) return col.render(item);
  return item[col.field] || "-";
}

const ListTemplate = ({
  title,
  columns,
  urlName,
  showStatus,
  editKey,
  deleteKey,
  deleteParam,
  rowKey,
  statusKey,
  addButtonLabel,
  showAddButton,
  pageSize,
  sortField,
  onPreDelete,
}) => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [rowsPerPage] = useState(pageSize);
  const [totalPages, setTotalPages] = useState(0);
  const [totalRecords, setTotalRecords] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [deleteError, setDeleteError] = useState("");
  const debounceRef = useRef(null);
  const router = useRouter();

  useEffect(() => {
    fetchData(page);
  }, [page, rowsPerPage]);

  useEffect(() => {
    clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      setPage(0);
      fetchData(0, searchTerm);
    }, 400);
    return () => clearTimeout(debounceRef.current);
  }, [searchTerm]);

  const fetchData = async (currentPage = 0, keyword = searchTerm) => {
    setLoading(true);
    try {
      const res = await axiosInstance.post(`/${urlName}/list`, {
        page: currentPage,
        sizePerPage: rowsPerPage,
        sortDirection: "ASC",
        sortField,
        ...(keyword ? { keyword } : {}),
      });

      const responseData = res.data || {};
      const list =
        responseData.dtoList ?? responseData.content ?? responseData.data ?? responseData ?? [];
      setData(Array.isArray(list) ? list : []);
      setTotalPages(responseData.totalPages ?? 0);
      setTotalRecords(
        responseData.totalRecords ?? (Array.isArray(list) ? list.length : 0)
      );
    } catch (err) {
      console.error("Fetch Error:", err);
      const status = err?.response?.status;
      if (status === 500) {
        // redirect to custom 500 page
        router.push('/500');
        return;
      }
    } finally {
      setLoading(false);
    }
  };

  const deleteItem = async (id) => {
    if (!globalThis.confirm("Delete this item?")) return;
    setDeleteError("");

    if (onPreDelete) {
      const errorMessage = onPreDelete(id);
      if (errorMessage) {
        setDeleteError(errorMessage);
        return;
      }
    }

    try {
      const res = await axiosInstance.delete(`/${urlName}/delete?${deleteParam}=${id}`);
      if (res.data?.success === false) {
        setDeleteError(res.data.message || "Cannot delete this record.");
        return;
      }
      fetchData(page);
    } catch (err) {
      console.error("Delete Error:", err);
      const status = err?.response?.status;
      if (status === 500) {
        router.push('/500');
        return;
      }
    }
  };

  const toggleStatus = async (recordValue, status) => {
    try {
      await axiosInstance.post(`/${urlName}/changeStatus`, {
        [statusKey]: recordValue,
        status,
      });
      fetchData(page);
    } catch (err) {
      console.error("Toggle Error:", err);
      const status = err?.response?.status;
      if (status === 500) {
        router.push('/500');
        return;
      }
    }
  };

  const cleanTitle = (title || "Record").replaceAll(" Management", "").trim();
  const finalButtonLabel = addButtonLabel || `Add ${cleanTitle}`;

  return (
    <div className="w-full max-w-full">
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">{title || "Management"}</h2>
          <p className="text-sm text-slate-500 mt-1">
            Browse and manage records for your POS system.
          </p>
        </div>
        {showAddButton !== false && (
          <button
            onClick={() => router.push(`/${urlName}/add`)}
          className="inline-flex items-center justify-center rounded-xl bg-white border-2 border-slate-800 px-4 py-2 text-xs font-bold text-slate-800 transition hover:bg-slate-800 hover:text-white shadow-sm"
          >
            + Add {finalButtonLabel}
          </button>
        )}
      </div>

      {deleteError && (
        <div className="mb-4 flex items-center justify-between rounded-xl bg-rose-50 border border-rose-200 px-4 py-3 text-sm font-medium text-rose-600">
          <span>{deleteError}</span>
          <button
            onClick={() => setDeleteError("")}
            className="ml-4 text-rose-400 hover:text-rose-600 font-bold"
          >
            ✕
          </button>
        </div>
      )}

      <div className="flex items-center gap-2 mb-4">
        <input
          type="text"
          placeholder="Search..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="flex-1 border border-slate-300 rounded-xl px-4 py-2 text-sm text-slate-800 bg-white outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition"
        />
        {searchTerm && (
          <button
            onClick={() => setSearchTerm("")}
            className="px-4 py-2 rounded-xl bg-slate-100 text-sm font-semibold text-slate-600 hover:bg-slate-200 transition"
          >
            Clear
          </button>
        )}
      </div>

      {loading && (
        <div className="rounded-3xl border border-slate-200 bg-white p-8 text-center text-slate-500 shadow-sm">
          Loading records...
        </div>
      )}

      {!loading && data.length === 0 && (
        <div className="rounded-3xl border border-slate-200 bg-white p-8 text-center text-slate-500 shadow-sm">
          {searchTerm ? `No results for "${searchTerm}".` : "No records available."}
        </div>
      )}

      {!loading && data.length > 0 && (
        <div className="overflow-x-auto rounded-3xl border border-slate-200 bg-white shadow-sm">
          <table className="min-w-full text-left text-sm text-slate-700">
            <thead className="bg-slate-900 text-white">
              <tr>
                {columns.map((col) => (
                  <th
                    key={col.label}
                    className="px-4 py-3 font-semibold uppercase tracking-[0.08em]"
                  >
                    {col.label}
                  </th>
                ))}
                <th className="px-4 py-3 text-right font-semibold uppercase tracking-[0.08em]">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody>
              {data.map((item) => (
                <tr
                  key={item[rowKey] ?? item.identifier ?? item.id}
                  className="border-t border-slate-200 hover:bg-slate-50"
                >
                  {columns.map((col) => (
                    <td key={col.label} className="px-4 py-4 align-top">
                      {renderCell(col, item, showStatus, toggleStatus, statusKey)}
                    </td>
                  ))}
                  <td className="px-4 py-4 align-top text-right">
                    <div className="inline-flex items-center gap-1">
                      <button
                        onClick={() => router.push(`/${urlName}/edit/${item[editKey]}`)}
                        title="Edit"
                        className="p-2 rounded-lg text-slate-400 hover:text-blue-600 hover:bg-blue-50 transition"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M15.232 5.232l3.536 3.536M9 13l6.586-6.586a2 2 0 012.828 2.828L11.828 15.828a2 2 0 01-1.414.586H8v-2.414a2 2 0 01.586-1.414z" />
                        </svg>
                      </button>
                      <button
                        onClick={() => deleteItem(item[deleteKey])}
                        title="Delete"
                        className="p-2 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6M9 7V4h6v3M4 7h16" />
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {!loading && data.length > 0 && (
        <div className="mt-6 flex flex-col gap-3 rounded-2xl border border-slate-200 bg-white px-4 py-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
          <p className="text-sm text-slate-600">
            Page {Math.min(page + 1, Math.max(totalPages, 1))} of{" "}
            {Math.max(totalPages, 1)}
            {typeof totalRecords === "number" ? ` — ${totalRecords} total records` : ""}
          </p>
          <div className="flex items-center gap-2">
            <button
              type="button"
              disabled={page === 0}
              onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
              className="rounded-xl bg-slate-100 px-4 py-2 text-sm font-semibold text-slate-700 transition hover:bg-slate-200 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Previous
            </button>
            <button
              type="button"
              disabled={page + 1 >= Math.max(totalPages, 1)}
              onClick={() => setPage((prev) => prev + 1)}
              className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

ListTemplate.propTypes = {
  title: PropTypes.string.isRequired,
  columns: PropTypes.arrayOf(PropTypes.object).isRequired,
  urlName: PropTypes.string.isRequired,
  showStatus: PropTypes.bool,
  editKey: PropTypes.string,
  deleteKey: PropTypes.string,
  deleteParam: PropTypes.string,
  rowKey: PropTypes.string,
  statusKey: PropTypes.string,
  addButtonLabel: PropTypes.string,
  showAddButton: PropTypes.bool,
  pageSize: PropTypes.number,
  sortField: PropTypes.string,
  onPreDelete: PropTypes.func,
};

ListTemplate.defaultProps = {
  showStatus: false,
  editKey: "identifier",
  deleteKey: "id",
  deleteParam: "id",
  rowKey: "id",
  statusKey: "identifier",
  addButtonLabel: null,
  showAddButton: true,
  pageSize: 10,
  sortField: "identifier",
  onPreDelete: null,
};

export default ListTemplate;