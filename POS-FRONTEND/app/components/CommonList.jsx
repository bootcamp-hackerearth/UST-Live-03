"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "../api";
import { Switch } from "@mui/material";

export default function List({ urlName, keys }) {
  const router = useRouter();

  const [data, setData] = useState([]);
  const [message, setMessage] = useState("");
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const [pagination, setPagination] = useState({
    page: 0,
    sizePerPage: 5,
  });

  const [searchTerm, setSearchTerm] = useState("");

  const numbers =
    totalPages > 0
      ? Array.from({ length: totalPages }, (_, i) => i + 1)
      : [];

  const displayKeys = keys.filter((k) => k !== "status");

  const getStockStatus = (quantity, minimumstock) => {
    if (quantity === 0) {
      return (
        <span className="text-red-600 font-semibold">
          Out of Stock
        </span>
      );
    } else if (quantity < minimumstock) {
      return (
        <span className="text-yellow-500 font-semibold">
          Low Stock
        </span>
      );
    } else {
      return (
        <span className="text-green-600 font-semibold">
          Available
        </span>
      );
    }
  };

  const fetchData = async () => {
    try {
      const token = localStorage.getItem("token");

      if (!token) {
        if (urlName !== "home") {
          router.push("/login");
        }
        return;
      }

      setLoading(true);

      const isSearching = searchTerm.trim().length > 0;

      const res = await api.post(`/${urlName}/list`, {
        ...pagination,
        page: isSearching ? 0 : pagination.page,
        sizePerPage: isSearching ? 1000 : pagination.sizePerPage,
      });

      setData(res.data.dtoList || []);
      setTotalPages(res.data.totalPages || 0);
    } catch {
      setMessage("Failed to load data");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [pagination, searchTerm]);

  const filteredData = data.filter((row) => {
    return Object.values(row || {})
      .join(" ")
      .toLowerCase()
      .includes(searchTerm.toLowerCase());
  });

  const updateLocalStatus = (row, newStatus) => {
    setData((prev) =>
      prev.map((p) =>
        (p.identifier || p.username) ===
        (row.identifier || row.username)
          ? { ...p, status: newStatus }
          : p
      )
    );
  };

  const handleToggle = async (row, newStatus) => {
    const value = row.identifier || row.username;

    try {
      await api.patch(`/${urlName}/toggle`, null, {
        params: {
          identifier: value,
          status: newStatus,
        },
      });

      setMessage("Status updated");
      setTimeout(() => setMessage(""), 3000);
    } catch {
      setMessage("Toggle failed");
    }
  };

  const handleDelete = async (row) => {
    const loggedInUsername =
      localStorage.getItem("username");

    const value = row.identifier || row.username;

    if (
      urlName === "category" &&
      (!row.superCategory || row.superCategory === "")
    ) {
      setMessage("Cannot delete root category (no super category)");
      setTimeout(() => setMessage(""), 3000);
      return;
    }

    const isSelfDelete =
      row.username === loggedInUsername ||
      row.identifier === loggedInUsername;

    const confirmMessage = isSelfDelete
      ? "⚠️ You are deleting your own account. Continue?"
      : "Are you sure you want to delete this user?";

    if (!confirm(confirmMessage)) return;

    try {
      await api.delete(`/${urlName}/delete`, {
        params: { identifier: value },
      });

      if (isSelfDelete) {
        localStorage.clear();
        router.push("/login");
        return;
      }

      setMessage("Deleted successfully");
      setTimeout(() => setMessage(""), 3000);

      fetchData();
    } catch {
      setMessage("Delete failed");
    }
  };

  let tableContent;

  if (loading) {
    tableContent = (
      <div className="text-center py-10">Loading...</div>
    );
  } else if (filteredData.length === 0) {
    tableContent = (
      <div className="text-center py-10">
        No data found
      </div>
    );
  } else {
    tableContent = (
      <table className="w-full border-collapse">
        <thead>
          <tr className="bg-gray-100 text-sm">
            <th className="p-3 border">Sl</th>

            {displayKeys.map((key) => (
              <th key={key} className="p-3 border">
                {key}
              </th>
            ))}

            {keys.includes("status") && (
              <th className="p-3 border">Status</th>
            )}

            {urlName === "stock" && (
              <th className="p-3 border">Status</th>
            )}

            <th className="p-3 border">Actions</th>
          </tr>
        </thead>

        <tbody>
          {filteredData.map((row, index) => {
            const rowKey =
              row.identifier || row.username;

            return (
              <tr
                key={rowKey}
                className="text-center border-b hover:bg-gray-50"
              >
                <td className="p-3 border">
                  {pagination.page *
                    pagination.sizePerPage +
                    index +
                    1}
                </td>

                {displayKeys.map((key) => (
                  <td
                    key={rowKey + key}
                    className="p-3 border"
                  >
                    {Array.isArray(row[key])
                      ? row[key]
                          .map((item) =>
                            typeof item === "object"
                              ? item.name ||
                                item.identifier
                              : item
                          )
                          .join(", ")
                      : row[key]}
                  </td>
                ))}

                {keys.includes("status") && (
                  <td className="p-3 border">
                    <Switch
                      checked={row.status === true}
                      onChange={(e) => {
                        const newStatus =
                          e.target.checked;

                        updateLocalStatus(
                          row,
                          newStatus
                        );
                        handleToggle(row, newStatus);
                      }}
                    />
                  </td>
                )}

                {urlName === "stock" && (
                  <td className="p-3 border">
                    {getStockStatus(
                      row.quantity,
                      row.minimumstock
                    )}
                  </td>
                )}

                <td className="p-3 border">
                  <button
                    onClick={() =>
                      router.push(
                        `/${urlName}/edit?identifier=${rowKey}`
                      )
                    }
                    className="mx-2 text-blue-600"
                  >
                    ✏️
                  </button>

                  <button
                    onClick={() =>
                      handleDelete(row)
                    }
                    className="mx-2 text-red-500"
                  >
                    🗑️
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 flex justify-center items-start p-6">
      <div className="bg-white w-full max-w-6xl p-8 rounded-2xl shadow-xl overflow-x-auto">

        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-gray-800">
            {urlName.toUpperCase()} LIST
          </h2>

          <div className="flex gap-3 items-center">
            <input
              type="text"
              placeholder="Search..."
              value={searchTerm}
              onChange={(e) =>
                setSearchTerm(e.target.value)
              }
              className="border p-2 rounded-lg text-sm"
            />

            <button
              onClick={() =>
                router.push(`/${urlName}/add`)
              }
              className="bg-green-600 text-white px-4 py-2 rounded-lg"
            >
              + Add
            </button>

            <button
              onClick={() => router.push("/dashboard1")}
              className="bg-blue-600 text-white px-4 py-2 rounded-lg"
            >
              Home
            </button>

          </div>
        </div>

        {message && (
          <div
            className={`mb-4 text-center text-sm ${
              message.includes("success")
                ? "text-green-600"
                : "text-red-500"
            }`}
          >
            {message}
          </div>
        )}

        {tableContent}

        {searchTerm.trim() === "" && (
          <div className="flex justify-center items-center gap-2 mt-8">
            <button
              disabled={pagination.page === 0}
              onClick={() =>
                setPagination((prev) => ({
                  ...prev,
                  page: prev.page - 1,
                }))
              }
              className="h-10 px-4 rounded-xl border"
            >
              ◀
            </button>

            {numbers.map((num) => (
              <button
                key={num}
                onClick={() =>
                  setPagination((prev) => ({
                    ...prev,
                    page: num - 1,
                  }))
                }
                className={`w-10 h-10 rounded-xl ${
                  pagination.page === num - 1
                    ? "bg-blue-600 text-white"
                    : "border"
                }`}
              >
                {num}
              </button>
            ))}

            <button
              disabled={
                pagination.page === totalPages - 1
              }
              onClick={() =>
                setPagination((prev) => ({
                  ...prev,
                  page: prev.page + 1,
                }))
              }
              className="h-10 px-4 rounded-xl border"
            >
              ▶
            </button>
          </div>
        )}

      </div>
    </div>
  );
}

List.propTypes = {
  urlName: PropTypes.string.isRequired,
  keys: PropTypes.arrayOf(PropTypes.string).isRequired,
};