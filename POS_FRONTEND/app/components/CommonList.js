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
  toggleMethod = "PATCH",
  sortField = "id",
  sortOrder = "ASC",
  itemsPerPage = 5,
}) {
  const router = useRouter();

  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState("");
  const [ready, setReady] = useState(false);

  const [fetchError, setFetchError] = useState(false);
  const [serverError, setServerError] = useState(null);

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
    setToast({
      visible: true,
      message: msg,
    });

    setTimeout(() => {
      setToast({
        visible: false,
        message: "",
      });
    }, 3000);
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) setReady(true);
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      setFetchError(false);
      setServerError(null);

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

      const list = res.data?.dtoList || [];

      const normalizedList = list.map((item) => ({
        ...item,
        status:
          item.status === true ||
          item.status === "ACTIVE" ||
          item.status === 1,
      }));

      setData(normalizedList);
      setTotalRecords(res.data?.totalRecords || 0);
      setTotalPages(res.data?.totalPage || 0);
    } catch (err) {
      const status = err.response?.status;

      const errorMessages = {
        403: "Access Denied: You do not have permission.",
        404: "No records found.",
        500: "Server error occurred.",
      };

      const message =
        errorMessages[status] || "Failed to load records.";

      triggerToast(message);
      setServerError(message);
      setFetchError(true);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (ready) {
      fetchData();
    }
  }, [pagination, ready, search]);

  const filteredData = useMemo(() => {
    return data.filter((item) =>
      columns.some((col) => {
        const val = item[col.field];
        if (!val) return false;

        return val
          .toString()
          .toLowerCase()
          .includes(search.toLowerCase());
      })
    );
  }, [data, search, columns]);

  const handleDelete = async (row) => {
    if (!confirm("Delete this record?")) return;

    try {
      await api.delete(deleteApi, {
        params: {
          [deleteParam]: row[deleteParam],
        },
      });

      triggerToast("Deleted successfully");
      fetchData();
    } catch (err) {
      const status = err.response?.status;

      const errorMessages = {
        403: "Access Denied.",
        404: "Record not found.",
        500: "Server error occurred.",
      };

      triggerToast(
        errorMessages[status] || "Delete failed."
      );
    }
  };

  const handleEdit = (row) => {
    let route = editRoute;

    const value = row[deleteParam];

    if (!value) {
      triggerToast("Unable to edit record.");
      return;
    }

    route = route.replace(/:\w+/, encodeURIComponent(value));

    router.push(route);
  };

  const handleToggle = async (row) => {
    const id = row[deleteParam];
    const newValue = !row[toggleField];

    setData((prev) =>
      prev.map((item) =>
        item[deleteParam] === id
          ? { ...item, [toggleField]: newValue }
          : item
      )
    );

    try {
      await api({
        method: toggleMethod.toLowerCase(),
        url: toggleApi,
        params: {
          [toggleParam]: id,
        },
      });

      triggerToast("Status updated");
    } catch (err) {
      const status = err.response?.status;

      const errorMessages = {
        403: "Access Denied.",
        404: "Record not found.",
        500: "Server error occurred.",
      };

      triggerToast(
        errorMessages[status] || "Status update failed."
      );

      fetchData();
    }
  };

  const numbers = Array.from(
    { length: totalPages },
    (_, i) => i + 1
  );

  if (loading && data.length === 0) {
    return (
      <Layout>
        <div className="flex justify-center py-20">
          Loading...
        </div>
      </Layout>
    );
  }

  if (fetchError) {
    return (
      <Layout>
        {toast.visible && (
          <div className="fixed top-4 right-4 z-50 bg-red-600 text-white px-6 py-3 rounded-lg shadow-xl">
            {toast.message}
          </div>
        )}

        <div className="flex flex-col items-center justify-center py-20">
          <p className="text-gray-600 mb-4">
            {serverError || "Failed to load records."}
          </p>

          <button
            onClick={fetchData}
            className="text-blue-600 underline"
          >
            Retry
          </button>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="space-y-6">
        {toast.visible && (
          <div className="fixed top-4 right-4 z-50 bg-red-600 text-white px-6 py-3 rounded-lg shadow-xl">
            {toast.message}
          </div>
        )}

        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-2xl font-bold text-blue-900">
              {title}
            </h2>
            <p className="text-sm text-gray-500">
              Manage your records
            </p>
          </div>

          {addRoute && (
            <button
              onClick={() => router.push(addRoute)}
              className="bg-gradient-to-r from-blue-700 to-blue-900 text-white px-4 py-2 rounded-lg flex items-center gap-2 shadow"
            >
              <Plus size={16} />
              Add
            </button>
          )}
        </div>

        <div className="bg-white border border-blue-50 p-4 rounded-xl shadow-sm flex justify-between items-center">
          <input
            className="border border-blue-100 rounded-lg px-3 py-2 text-sm w-full max-w-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Search..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

          <span className="text-sm text-gray-500 ml-4">
            {totalRecords} records
          </span>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-blue-50 overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-gradient-to-r from-[#0a1f66] to-[#1e3a8a] text-white">
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
              {filteredData.length === 0 ? (
                <tr>
                  <td
                    colSpan={columns.length + 1}
                    className="text-center p-6 text-gray-500"
                  >
                    No records found
                  </td>
                </tr>
              ) : (
                filteredData.map((row) => {
                  const rowKey =
                    row[deleteParam] ??
                    row.identifier ??
                    JSON.stringify(row);

                  return (
                    <tr
                      key={rowKey}
                      className="border-t hover:bg-blue-50"
                    >
                      {columns.map((c) => {
                        const value = row[c.field];
                        const isActive = value === true;

                        return (
                          <td
                            key={`${rowKey}-${c.field}`}
                            className="p-3"
                          >
                            {c.field === toggleField &&
                            showStatus ? (
                              <button
                                onClick={() =>
                                  handleToggle(row)
                                }
                                className={`w-11 h-6 flex items-center rounded-full p-1 ${
                                  isActive
                                    ? "bg-green-500"
                                    : "bg-gray-300"
                                }`}
                              >
                                <span
                                  className={`w-4 h-4 bg-white rounded-full shadow transform ${
                                    isActive
                                      ? "translate-x-5"
                                      : ""
                                  }`}
                                />
                              </button>
                            ) : (
                              value
                            )}
                          </td>
                        );
                      })}

                      <td className="p-3 text-center flex justify-center gap-3">
                        <button
                          onClick={() => handleEdit(row)}
                        >
                          <Pencil size={16} />
                        </button>

                        <button
                          onClick={() => handleDelete(row)}
                        >
                          <Trash2 size={16} />
                        </button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 0 && search.trim() === "" && (
          <div className="bg-white border border-blue-50 rounded-xl p-4 flex justify-between items-center shadow-sm">
            <div className="text-sm text-gray-600">
              Page <b>{pagination.page + 1}</b> of{" "}
              <b>{totalPages}</b>
            </div>

            <div className="flex gap-2">
              {numbers.map((num) => (
                <button
                  key={num}
                  onClick={() =>
                    setPagination((p) => ({
                      ...p,
                      page: num - 1,
                    }))
                  }
                  className={`px-3 py-1 rounded-md text-sm ${
                    pagination.page === num - 1
                      ? "bg-blue-800 text-white"
                      : "bg-gray-100 hover:bg-blue-100"
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
      field: PropTypes.string.isRequired,
      render: PropTypes.func,
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