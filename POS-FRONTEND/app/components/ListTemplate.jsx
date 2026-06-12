"use client";
import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import axiosInstance from "../api/axiosInstance";

const renderStatusCell = (item, rowKey, togglingIds, toggleStatus) => {
  const key = item[rowKey] ?? item.identifier ?? item.id;
  const isToggling = togglingIds.has(String(key));
  return (
    <button
      type="button"
      onClick={(e) => { e.preventDefault(); toggleStatus(key, !item.status); }}
      disabled={isToggling}
      className={`relative inline-flex h-7 w-16 items-center rounded-full transition ${
        item.status ? "bg-emerald-500" : "bg-slate-300"
      } ${isToggling ? "cursor-wait opacity-80" : "hover:ring-4 hover:ring-slate-200"}`}
    >
      <span
        className={`absolute left-1 h-5 w-5 rounded-full bg-white shadow transition-transform duration-200 ${
          item.status ? "translate-x-8" : "translate-x-0"
        } ${isToggling ? "animate-pulse" : ""}`}
      />
      <span className="pointer-events-none absolute inset-0 flex items-center justify-center text-[0.55rem] font-bold uppercase tracking-wide text-slate-900">
        {item.status ? "On" : "Off"}
      </span>
    </button>
  );
};

const renderCell = (col, item, rowKey, togglingIds, toggleStatus, showStatus) => {
  if (col.field === "status" && showStatus) {
    return renderStatusCell(item, rowKey, togglingIds, toggleStatus);
  }
  if (col.render) {
    return col.render(item);
  }
  return item[col.field] || "-";
};

const ListTemplate = ({
  title,
  columns,
  urlName,
  showStatus = false,
  editKey = "identifier",
  deleteKey = "id",
  deleteParam = "id",
  rowKey = "id",
  addButtonLabel,
  showAddButton = true,
  pageSize = 10,
  sortField = "identifier",
  editUseQuery = false,
}) => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [rowsPerPage] = useState(pageSize);
  const [totalPages, setTotalPages] = useState(0);
  const [totalRecords, setTotalRecords] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [togglingIds, setTogglingIds] = useState(new Set());
  const router = useRouter();

  const filteredData = data.filter((item) => {
    const term = searchTerm.trim().toLowerCase();
    if (!term) return true;
    return columns.some((col) => {
      const value = item[col.field];
      if (value == null) return false;
      if (Array.isArray(value)) return value.join(", ").toLowerCase().includes(term);
      return String(value).toLowerCase().includes(term);
    });
  });

  const fetchData = useCallback(async (currentPage = 0) => {
    await Promise.resolve();
    setLoading(true);
    try {
      const res = await axiosInstance.post(`/${urlName}/list`, {
        page: currentPage,
        sizePerPage: rowsPerPage,
        sortDirection: "ASC",
        sortField,
      });
      const responseData = res.data || {};
      const list =
        responseData.dtoList ?? responseData.content ?? responseData.data ?? responseData ?? [];
      const normalizedList = Array.isArray(list)
        ? list.map((it) => ({ ...it, status: it.status === true || it.status === "true" }))
        : [];
      setData(normalizedList);
      setTotalPages(responseData.totalPages ?? 0);
      setTotalRecords(responseData.totalRecords ?? normalizedList.length);
    } catch (err) {
      console.error("Fetch Error:", err);
    } finally {
      setLoading(false);
    }
  }, [rowsPerPage, sortField, urlName]);

  useEffect(() => {
    queueMicrotask(() => fetchData(page));
  }, [fetchData, page]);

 const deleteItem = async (item) => {
  const id = item[deleteKey];

  if (!globalThis.confirm("Delete this item?")) {
    return;
  }

  const loggedInUsername = localStorage.getItem("username");

  const deletingUsername =
    item.username || item.identifier || String(id);

  const params = new URLSearchParams();
  params.set(deleteParam, id);

  if (deleteParam !== "username") {
    params.set("username", item.username || id);
  }

  if (deleteParam !== "identifier") {
    params.set("identifier", item.identifier || id);
  }

  try {
    await axiosInstance.get(
      `/${urlName}/delete?${params.toString()}`
    );
    if (
      urlName === "user" &&
      loggedInUsername &&
      loggedInUsername === deletingUsername
    ) {
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      localStorage.removeItem("name");

      router.replace("/login");
      return;
    }

    fetchData(page);
  } catch (err) {
    console.error("Delete Error:", err);
  }
};

  const toggleStatus = async (identifier, newStatus) => {
    setTogglingIds((prev) => { const s = new Set(prev); s.add(String(identifier)); return s; });
    setData((prev) =>
      prev.map((it) => {
        const key = it.identifier ?? it.username ?? it.id;
        return String(key) === String(identifier) ? { ...it, status: newStatus } : it;
      })
    );
    try {
      if (["price", "product", "category"].includes(urlName)) {
        await axiosInstance.post(`/${urlName}/update`, { identifier, status: newStatus });
      } else {
        await axiosInstance.get(`/${urlName}/toggleStatus?identifier=${identifier}`);
      }
    } catch (err) {
      console.error("Toggle Error:", err);
      setData((prev) =>
        prev.map((it) => {
          const key = it.identifier ?? it.username ?? it.id;
          return String(key) === String(identifier) ? { ...it, status: !newStatus } : it;
        })
      );
    } finally {
      setTogglingIds((prev) => { const s = new Set(prev); s.delete(String(identifier)); return s; });
    }
  };

  return (
    <div className="w-full max-w-7xl">
      <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h2 className="text-2xl font-black tracking-tight text-slate-950">{title}</h2>
          <p className="mt-1 text-sm font-medium text-slate-500">
            Browse and manage records for your POS system.
          </p>
        </div>
        {showAddButton && (
          <button
            onClick={() => router.push(`/${urlName}/add`)}
            className="inline-flex items-center justify-center rounded-lg bg-cyan-600 px-5 py-2.5 text-sm font-bold text-white shadow-lg shadow-cyan-200/70 transition hover:bg-cyan-700"
          >
           + Add {addButtonLabel || (title.endsWith(" Management") ? title.slice(0, -" Management".length) : title)}
          </button>
        )}
      </div>

      <div className="mb-6">
        <input
          type="text"
          placeholder={`Search ${title}...`}
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full rounded-lg border border-slate-200 bg-white px-4 py-2.5 text-sm font-medium text-slate-800 shadow-sm outline-none transition placeholder:text-slate-400 focus:border-cyan-400 focus:ring-4 focus:ring-cyan-100 md:w-96"
        />
      </div>

      {loading && (
        <div className="rounded-lg border border-slate-200 bg-white p-8 text-center text-sm font-semibold text-slate-500 shadow-sm">
          Loading records...
        </div>
      )}
      {!loading && filteredData.length === 0 && (
        <div className="rounded-lg border border-slate-200 bg-white p-8 text-center text-sm font-semibold text-slate-500 shadow-sm">
          No records available.
        </div>
      )}
      {!loading && filteredData.length > 0 && (
        <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white shadow-sm">
          <table className="min-w-full text-left text-sm text-slate-700">
            <thead className="bg-slate-950 text-white">
              <tr>
                {columns.map((col) => (
                  <th key={col.field} className="px-4 py-3 text-xs font-bold uppercase tracking-wide">
                    {col.label}
                  </th>
                ))}
                <th className="px-4 py-3 text-right text-xs font-bold uppercase tracking-wide">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody>
              {filteredData.map((item) => (
                <tr
                  key={item[rowKey] ?? item.identifier ?? item.id}
                  className="border-t border-slate-100 hover:bg-cyan-50/40"
                >
                  {columns.map((col) => (
                    <td key={col.field} className="px-4 py-4 align-top font-medium">
                      {renderCell(col, item, rowKey, togglingIds, toggleStatus, showStatus)}
                    </td>
                  ))}
                  <td className="space-x-2 px-4 py-4 text-right align-top">
                    <button
                      type="button"
                      onClick={() => {
                        const editPath = editUseQuery
                          ? `/${urlName}/edit?${editKey}=${item[editKey]}`
                          : `/${urlName}/edit/${item[editKey]}`;
                        router.push(editPath);
                      }}
                      className="rounded-lg bg-amber-400 px-3 py-2 text-sm font-bold text-slate-950 transition hover:bg-amber-500"
                    >
                      Edit
                    </button>
                    <button
                      type="button"
                      onClick={() => deleteItem(item)}
                      className="rounded-lg bg-rose-500 px-3 py-2 text-sm font-bold text-white transition hover:bg-rose-600"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {!loading && filteredData.length > 0 && (
        <div className="mt-6 flex flex-col gap-3 rounded-lg border border-slate-200 bg-white px-4 py-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
          <p className="text-sm font-semibold text-slate-600">
            Page {Math.min(page + 1, Math.max(totalPages, 1))} of {Math.max(totalPages, 1)}
            {typeof totalRecords === "number" ? ` - ${totalRecords} total records` : ""}
          </p>
          <div className="flex items-center gap-2">
            <button
              type="button"
              disabled={page === 0}
              onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
              className="rounded-lg bg-slate-100 px-4 py-2 text-sm font-bold text-slate-700 transition hover:bg-slate-200 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Previous
            </button>
            <button
              type="button"
              disabled={page + 1 >= Math.max(totalPages, 1)}
              onClick={() => setPage((prev) => prev + 1)}
              className="rounded-lg bg-slate-950 px-4 py-2 text-sm font-bold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

const columnShape = PropTypes.shape({
  field: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  render: PropTypes.func,
});

ListTemplate.propTypes = {
  title: PropTypes.string.isRequired,
  columns: PropTypes.arrayOf(columnShape).isRequired,
  urlName: PropTypes.string.isRequired,
  showStatus: PropTypes.bool,
  editKey: PropTypes.string,
  deleteKey: PropTypes.string,
  deleteParam: PropTypes.string,
  rowKey: PropTypes.string,
  addButtonLabel: PropTypes.string,
  showAddButton: PropTypes.bool,
  pageSize: PropTypes.number,
  sortField: PropTypes.string,
  editUseQuery: PropTypes.bool,
};

export default ListTemplate;