"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { Pencil, Trash2, Plus } from "lucide-react";
import api from "../services/api";
import Layout from "./Layout";
import PropTypes from "prop-types";

function CommonList({
  title = "List",
  apiUrl,
  method = "POST",
  payload = {},
  columns = [],
  deleteApi,
  deleteParam = "identifier",
  editRoute,
  addRoute,
  showStatus = false,
  toggleApi,
  toggleParam = "identifier",
  toggleField = "status",
  toggleMethod = "POST",
  sortField = "id",
  sortOrder = "ASC",
  itemsPerPage = 5,
}) {
  const router = useRouter();

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState("");
  const [ready, setReady] = useState(false);

  const [pagination, setPagination] = useState({
    page: 0,
    sizePerPage: itemsPerPage,
  });

  const [totalRecords, setTotalRecords] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [toast, setToast] = useState({
    visible: false,
    message: "",
  });

  const triggerToast = (msg) => {
    setToast({ visible: true, message: msg });
    setTimeout(() => {
      setToast({ visible: false, message: "" });
    }, 2500);
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) setReady(true);
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);

      const isSearching = search.trim().length > 0;

      const res = await api({
        method,
        url: apiUrl,
        data: {
          ...payload,
          page: isSearching ? 0 : pagination.page,
          sizePerPage: isSearching ? 1000 : pagination.sizePerPage,
          sortField,
          sortDirection: sortOrder,
        },
      });

      setData(res.data?.dtoList || []);
      setTotalRecords(res.data?.totalRecords || 0);
      setTotalPages(res.data?.totalPage || 0);
    } catch {
      triggerToast("Failed to fetch data");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (ready) fetchData();
  }, [pagination, ready, search]);

  const filteredData = useMemo(() => {
    return data.filter((item) =>
      columns.some((col) => {
        const val = item[col.field];
        if (!val) return false;
        return val.toString().toLowerCase().includes(search.toLowerCase());
      })
    );
  }, [data, search, columns]);

  const handleDelete = async (row) => {
    if (!confirm("Delete this record?")) return;

    try {
      await api.get(deleteApi, {
        params: { [deleteParam]: row[deleteParam] },
      });

      triggerToast("Deleted successfully");
      fetchData();
    } catch {
      triggerToast("Delete failed");
    }
  };

  const handleEdit = (row) => {
    const value = row[deleteParam] ?? row.identifier;
    let route = editRoute;

    if (route.includes(":username")) {
      route = route.replace(":username", encodeURIComponent(value));
    }

    if (route.includes(":identifier")) {
      route = route.replace(":identifier", encodeURIComponent(value));
    }

    router.push(route);
  };

  const handleToggle = async (row) => {
  const id = row[deleteParam] ?? row.identifier ?? row.id;

  if (!id) {
    console.error(" Missing identifier in row:", row);
    triggerToast("Invalid ID");
    return;
  }

  setData((prev) =>
    prev.map((item) =>
      (item[deleteParam] ?? item.identifier ?? item.id) === id
        ? {
            ...item,
            [toggleField]: !(
              item[toggleField] === true ||
              item[toggleField] === "ACTIVE" ||
              item[toggleField] === 1
            ),
          }
        : item
    )
  );

  try {
    await api({
      method: toggleMethod,
      url: toggleApi,
      params: { [toggleParam]: id },
    });

    triggerToast("Status updated");
  } catch (err) {
    console.error(" Toggle error:", err);
    triggerToast("Toggle failed");
    fetchData();
  }
};
  const numbers = Array.from({ length: totalPages }, (_, i) => i + 1);

  return (
    <Layout>
      <div className="p-6">

        {toast.visible && (
          <div className="fixed top-4 right-4 bg-slate-900 text-white px-4 py-2 rounded-lg shadow text-sm">
            {toast.message}
          </div>
        )}

        <div className="flex justify-between items-center mb-6">
          <div>
            <h2 className="text-2xl font-semibold text-[var(--text-h)]">
              {title}
            </h2>
            <p className="text-sm text-[var(--text)]">
              Manage your records
            </p>
          </div>

          {addRoute && (
            <button
              onClick={() => router.push(addRoute)}
              className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg flex items-center gap-2 shadow transition"
            >
              <Plus size={16} /> Add
            </button>
          )}
        </div>

        <div className="bg-white p-4 rounded-xl shadow mb-4 flex justify-between items-center">
          <input
            className="border rounded-lg px-3 py-2 text-sm w-full max-w-sm focus:ring-2 focus:ring-blue-200 outline-none"
            placeholder="Search..."
            onChange={(e) => setSearch(e.target.value)}
          />
          <span className="text-sm text-gray-500 ml-4">
            {totalRecords} records
          </span>
        </div>

        <div className="bg-white rounded-xl shadow overflow-hidden border">

          {loading && (
            <div className="p-4 text-center text-sm text-gray-500">
              Loading...
            </div>
          )}

          <table className="w-full text-sm">
            <thead className="bg-slate-900 text-white">
              <tr>
                {columns.map((c) => (
                  <th key={c.field} className="p-3 text-left">
                    {c.header}
                  </th>
                ))}
                <th className="p-3 text-center">Actions</th>
              </tr>
            </thead>

            <tbody>
              {filteredData.map((row) => {
                const rowKey =
                  row[deleteParam] ?? row.identifier ?? JSON.stringify(row);

                return (
                  <tr key={rowKey} className="border-t hover:bg-slate-50">

                    {columns.map((c) => (
                      <td key={`${rowKey}-${c.field}`} className="p-3">

                        {c.field === toggleField && showStatus ? (
                          <button
                            onClick={() => handleToggle(row)}
                            className={`w-11 h-6 flex items-center rounded-full p-1 ${
                              row[toggleField] === true ||
                              row[toggleField] === "ACTIVE" ||
                              row[toggleField] === 1
                                ? "bg-green-500"
                                : "bg-gray-300"
                            }`}
                          >
                            <span
                              className={`w-4 h-4 bg-white rounded-full shadow transform ${
                                row[toggleField] === true ||
                                row[toggleField] === "ACTIVE" ||
                                row[toggleField] === 1
                                  ? "translate-x-5"
                                  : ""
                              }`}
                            />
                          </button>
                        ) : (
                          row[c.field]
                        )}

                      </td>
                    ))}

                    <td className="p-3 text-center flex justify-center gap-2">

                      <button
                        onClick={() => handleEdit(row)}
                        className="p-2 rounded-lg bg-blue-100 text-blue-600 hover:bg-blue-200 transition"
                        title="Edit"
                      >
                        <Pencil size={16} />
                      </button>

                      <button
                        onClick={() => handleDelete(row)}
                        className="p-2 rounded-lg bg-red-100 text-red-600 hover:bg-red-200 transition"
                        title="Delete"
                      >
                        <Trash2 size={16} />
                      </button>

                    </td>

                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>

        {totalPages > 0 && search.trim() === "" && (
          <div className="mt-4 bg-white border rounded-xl p-4 flex justify-between items-center shadow">

            <div className="text-sm text-gray-600">
              Page <b>{pagination.page + 1}</b> of <b>{totalPages}</b>
            </div>

            <div className="flex gap-2">
              {numbers.map((num) => (
                <button
                  key={num}
                  onClick={() =>
                    setPagination((p) => ({ ...p, page: num - 1 }))
                  }
                  className={`px-3 py-1 border rounded ${
                    pagination.page === num - 1
                      ? "bg-slate-900 text-white"
                      : "hover:bg-gray-100"
                  }`}
                >
                  {num}
                </button>
              ))}
            </div>

          </div>
        )}

      </div>
    </Layout>
  );
}

CommonList.propTypes = {
  title: PropTypes.string,
  apiUrl: PropTypes.string.isRequired,
  method: PropTypes.string,
  payload: PropTypes.object,
  columns: PropTypes.arrayOf(
    PropTypes.shape({
      header: PropTypes.string,
      field: PropTypes.string,
    })
  ).isRequired,
  deleteApi: PropTypes.string,
  deleteParam: PropTypes.string,
  editRoute: PropTypes.string,
  addRoute: PropTypes.string,
  showStatus: PropTypes.bool,
  toggleApi: PropTypes.string,
  toggleParam: PropTypes.string,
  toggleField: PropTypes.string,
  toggleMethod: PropTypes.string,
  sortField: PropTypes.string,
  sortOrder: PropTypes.string,
  itemsPerPage: PropTypes.number,
};

export default CommonList;