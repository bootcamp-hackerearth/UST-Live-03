"use client";
import React, { useEffect, useState, useCallback } from "react";
import PropTypes from "prop-types";
import axios from "axios";
import { useRouter } from "next/navigation";
import Layout from "./Layout";

const CommonList = ({
  title,
  columns,
  urlName,
  showStatus = false,
  editKey = "identifier",
}) => {
  const BASE_URL = "http://localhost:8080/api";

  const [token, setToken] = useState("");
  const [username, setUsername] = useState("");

  useEffect(() => {
    const storedToken = localStorage.getItem("token") || "";
    const storedUsername = localStorage.getItem("username") || "";

    setToken(storedToken);
    setUsername(storedUsername);
  }, []);

  const router = useRouter();

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [sizePerPage, setSizePerPage] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [totalRecords, setTotalRecords] = useState(0);

  const [search, setSearch] = useState("");
  const [sortField] = useState("id");
  const [sortDirection] = useState("ASC");

  const [toast, setToast] = useState({
    visible: false,
    message: "",
    type: "success",
  });

  const triggerToast = useCallback((message, type = "success") => {
    setToast({ visible: true, message, type });
    setTimeout(() => {
      setToast((prev) => ({ ...prev, visible: false }));
    }, 4000);
  }, []);

  const fetchData = useCallback(async () => {
    if (!token) return;

    setLoading(true);
    try {
      const res = await axios.post(
        `${BASE_URL}/${urlName}/list`,
        {
          page: page,
          sizePerPage: sizePerPage,
          sortDirection: sortDirection,
          sortField: sortField,
        },
        { headers: { Authorization: "Bearer " + token } },
      );

      if (res.status !== 200 && res.status !== 201) {
        if (res.status === 500) {
          router.push("/500");
          return;
        }
      }

      const responseData = res.data;
      if (responseData) {
        setData(responseData.dtoList || []);
        setTotalPages(responseData.totalPages || 1);
        setTotalRecords(responseData.totalRecords || 0);
      }
    } catch (err) {
      console.error("Fetch Error:", err);
      if (err.response.status === 403 || err.response.status === 500) {
        router.push("/500");
        return;
      }
      triggerToast("Failed to fetch records from server", "error");
    } finally {
      setLoading(false);
    }
  }, [
    BASE_URL,
    urlName,
    page,
    sizePerPage,
    sortDirection,
    sortField,
    token,
    triggerToast,
    router,
  ]);

  useEffect(() => {
    if (token) {
      fetchData();
    }
  }, [fetchData, token]);

  const filteredData = React.useMemo(() => {
    if (!search) return data;
    return data.filter((item) =>
      columns.some((col) => {
        const value = item[col.field];
        if (value === null || value === undefined) return false;
        if (typeof value === "object") {
          return JSON.stringify(value)
            .toLowerCase()
            .includes(search.toLowerCase());
        }
        return value.toString().toLowerCase().includes(search.toLowerCase());
      }),
    );
  }, [data, search, columns]);

  const deleteItem = async (identifier) => {
    if (!globalThis.confirm("Are you sure you want to delete this item?"))
      return;
    try {
      const res = await axios.delete(
        `${BASE_URL}/${urlName}/delete?identifier=${identifier}`,
        { headers: { Authorization: "Bearer " + token } },
      );
      if (res.data === true) {
        triggerToast("Record successfully deleted", "success");
        fetchData();
      } else {
        triggerToast(
          "Cannot delete this record (you may be deleting yourself)",
          "error",
        );
      }
    } catch (err) {
      console.error("Delete Error:", err);
      triggerToast("Could not complete delete operation", "error");
    }
  };

  const toggleStatus = async (identifier) => {
    try {
      await axios.post(
        `${BASE_URL}/${urlName}/toggle-status?identifier=${identifier}`,
        {},
        { headers: { Authorization: "Bearer " + token } },
      );
      triggerToast("Status updated successfully", "success");
      fetchData();
    } catch (err) {
      console.error("Toggle Error:", err);
      triggerToast("Failed to update status code", "error");
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    router.push("/");
  };

  const renderCellContent = (item, col) => {
    if (col.field === "status" && showStatus) {
      return (
        <button
          onClick={() => toggleStatus(item.identifier)}
          className={`inline-flex px-2.5 py-0.5 rounded-full text-xs font-semibold tracking-wide border transition-all duration-150 ${
            item.status
              ? "bg-green-50 text-green-700 border-green-200"
              : "bg-red-50 text-red-600 border-red-100"
          }`}
        >
          {item.status ? "Active" : "Inactive"}
        </button>
      );
    }

    if (col.render) {
      return col.render(item);
    }

    const isIdentifier = col.field === "id" || col.field === "identifier";
    return (
      <span
        className={
          isIdentifier
            ? "font-mono text-slate-500 font-medium"
            : "text-slate-800"
        }
      >
        {String(item[col.field] ?? "")}
      </span>
    );
  };

  return (
    <Layout nodes={[]} username={username} onLogout={handleLogout}>
      <div
        className={`fixed top-4 right-4 z-50 transform transition-all duration-300 pointer-events-none ${
          toast.visible
            ? "translate-y-0 opacity-100"
            : "-translate-y-4 opacity-0"
        }`}
      >
        {toast.visible && (
          <div
            className={`flex items-center gap-3 px-4 py-3 rounded-xl shadow-lg border text-sm font-medium pointer-events-auto min-w-[300px] ${
              toast.type === "success"
                ? "bg-emerald-50 border-emerald-200 text-emerald-800"
                : "bg-rose-50 border-rose-200 text-rose-800"
            }`}
          >
            {toast.type === "success" ? (
              <svg
                className="w-5 h-5 text-emerald-600 flex-shrink-0"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            ) : (
              <svg
                className="w-5 h-5 text-rose-600 flex-shrink-0"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
                />
              </svg>
            )}
            <span className="flex-1">{toast.message}</span>
            <button
              onClick={() => setToast((prev) => ({ ...prev, visible: false }))}
              className="text-slate-400 hover:text-slate-600 transition-colors pl-2"
            >
              ✕
            </button>
          </div>
        )}
      </div>

      <div className="flex flex-col h-full bg-slate-50 overflow-hidden text-left">
        <div className="flex justify-between items-center mb-6 flex-shrink-0">
          <div>
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">
              {title}
            </h1>
            <p className="text-sm text-slate-500 mt-0.5">
              Manage and update your records configuration below.
            </p>
          </div>
          <button
            onClick={() => router.push(`/${urlName}/add`)}
            className="bg-blue-600 hover:bg-blue-700 text-white font-medium text-sm px-4 py-2.5 rounded-lg shadow-sm transition-colors duration-150 flex items-center gap-2"
          >
            <span className="text-base font-semibold">+</span> Add New
          </button>
        </div>
        <div className="bg-white border border-slate-200 rounded-t-xl p-4 flex justify-between items-center gap-4 flex-shrink-0">
          <div className="relative flex-1 max-w-md">
            <span className="absolute inset-y-0 left-0 flex items-center pl-3 text-slate-400 pointer-events-none text-sm">
              🔍
            </span>
            <input
              type="text"
              placeholder="Search data records..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
              className="w-full bg-slate-50 border border-slate-200 rounded-lg pl-9 pr-4 py-2 text-sm text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
            />
          </div>

          <div className="flex items-center gap-2">
            <span className="text-xs font-medium text-slate-500 whitespace-nowrap">
              Rows per page:
            </span>
            <select
              value={sizePerPage}
              onChange={(e) => {
                setSizePerPage(Number(e.target.value));
                setPage(0);
              }}
              className="bg-white border border-slate-200 rounded-lg px-2.5 py-1.5 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 cursor-pointer"
            >
              <option value={5}>5 Rows</option>
              <option value={10}>10 Rows</option>
              <option value={25}>25 Rows</option>
            </select>
          </div>
        </div>
        <div className="flex-1 overflow-auto border-x border-slate-200 bg-white shadow-sm min-h-0 relative">
          {loading && (
            <div className="absolute inset-0 flex items-center justify-center bg-white/80 z-20">
              <div className="text-sm text-slate-500 font-medium flex items-center gap-2">
                <svg
                  className="animate-spin h-5 w-5 text-blue-600"
                  fill="none"
                  viewBox="0 0 24 24"
                >
                  <circle
                    className="opacity-25"
                    cx="12"
                    cy="12"
                    r="10"
                    stroke="currentColor"
                    strokeWidth="4"
                  />
                  <path
                    className="opacity-75"
                    fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                  />
                </svg>
                Fetching system data...
              </div>
            </div>
          )}
          {!loading && filteredData.length === 0 && (
            <div className="py-24 text-center text-sm text-slate-400">
              No matching system data found.
            </div>
          )}
          {!loading && filteredData.length > 0 && (
            <table className="w-full table-auto border-collapse">
              <thead className="sticky top-0 bg-slate-900 text-white text-xs font-semibold tracking-wider z-10">
                <tr>
                  {columns.map((col, i) => (
                    <th
                      key={col.field || `col-${i}`}
                      className="p-4 text-left whitespace-nowrap font-semibold"
                    >
                      {col.label}
                    </th>
                  ))}
                  <th className="p-4 text-center font-semibold w-32">
                    Actions
                  </th>
                </tr>
              </thead>

              <tbody className="divide-y divide-slate-100 text-sm text-slate-700">
                {filteredData.map((item, rowIdx) => (
                  <tr
                    key={item.identifier || item.username || `row-${rowIdx}`}
                    className="hover:bg-slate-50/80 transition-colors duration-100"
                  >
                    {columns.map((col, i) => (
                      <td
                        key={col.field || `cell-${i}`}
                        className="p-4 whitespace-nowrap max-w-xs truncate"
                      >
                        {renderCellContent(item, col)}
                      </td>
                    ))}

                    <td className="p-4 text-center whitespace-nowrap">
                      <div className="flex justify-center gap-1.5">
                        <button
                          onClick={() =>
                            router.push(`/${urlName}/edit/${item[editKey]}`)
                          }
                          title="Edit row item"
                          className="p-1.5 text-slate-500 hover:text-blue-600 hover:bg-slate-100 rounded-md transition-colors"
                        >
                          <svg
                            className="w-4 h-4"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"
                            />
                          </svg>
                        </button>

                        <button
                          onClick={() => deleteItem(item.identifier)}
                          title="Delete row item"
                          className="p-1.5 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-md transition-colors"
                        >
                          <svg
                            className="w-4 h-4"
                            fill="none"
                            stroke="currentColor"
                            strokeWidth="2"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                            />
                          </svg>
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        <div className="bg-white border border-slate-200 rounded-b-xl px-4 py-3.5 flex justify-between items-center flex-shrink-0 shadow-sm">
          <div className="text-xs text-slate-500 font-medium">
            Page{" "}
            <span className="text-slate-800 font-semibold">{page + 1}</span> of{" "}
            <span className="text-slate-800 font-semibold">{totalPages}</span>{" "}
            <span className="mx-2 text-slate-300">|</span> Total Records:{" "}
            <span className="text-slate-800 font-semibold">{totalRecords}</span>
          </div>

          <div className="flex items-center gap-1.5">
            <button
              disabled={page === 0}
              onClick={() => setPage(page - 1)}
              className="px-3 py-1.5 rounded-lg border border-slate-200 bg-white font-medium text-xs text-slate-600 hover:bg-slate-50 disabled:opacity-40 disabled:hover:bg-white transition-colors"
            >
              Previous
            </button>

            <div className="flex gap-1 max-w-[200px] sm:max-w-xs overflow-x-auto px-1">
              {[...new Array(totalPages).keys()].map((p) => (
                <button
                  key={`page-${p}`}
                  onClick={() => setPage(p)}
                  className={`px-3 py-1.5 text-xs font-semibold rounded-lg transition-all ${
                    page === p
                      ? "bg-blue-600 text-white shadow-sm shadow-blue-500/10"
                      : "text-slate-600 hover:bg-slate-100"
                  }`}
                >
                  {p + 1}
                </button>
              ))}
            </div>

            <button
              disabled={page === totalPages - 1}
              onClick={() => setPage(page + 1)}
              className="px-3 py-1.5 rounded-lg border border-slate-200 bg-white font-medium text-xs text-slate-600 hover:bg-slate-50 disabled:opacity-40 disabled:hover:bg-white transition-colors"
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </Layout>
  );
};

CommonList.propTypes = {
  title: PropTypes.string,
  columns: PropTypes.array.isRequired,
  urlName: PropTypes.string.isRequired,
  showStatus: PropTypes.bool,
  editKey: PropTypes.string,
};

export default CommonList;
