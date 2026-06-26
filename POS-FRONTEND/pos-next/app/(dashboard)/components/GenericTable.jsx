"use client";

import PropTypes from "prop-types";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import EditModal from "./EditModal";
import SearchBar from "./SearchBar";
import TablePagination from "./TablePagination";
import axios from "axios";

export default function GenericTable({
  columns,
  rows,
  toggleEndpoint,
  config,
  showActions = true,
}) {
  const router = useRouter();

  const currentUserEmail = globalThis.window === undefined ? null : localStorage.getItem("userEmail");
  const [tableRows, setTableRows] = useState(rows);
  const [editingRow, setEditingRow] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(5);

  useEffect(() => { setTableRows(rows); }, [rows]);
  useEffect(() => { setCurrentPage(1); }, [searchTerm]);

  const handleDelete = async (identifier) => {
    const confirmed = globalThis.confirm("Delete this record?");
    if (!confirmed) return;

    try {
      const response = await axios.delete("/api/delete-entity", {
        data: {
          endpoint: config.deleteEndpoint,
          identifier,
          paramName: config.deleteParam || "identifier",
        },
      });

      if (!response.data.success) {
        alert("Failed to delete record from system");
        return;
      }

      setTableRows((prev) => prev.filter((item) => item[config.deleteParam || "identifier"] !== identifier));

      const isUserEntity = config.deleteEndpoint === "/api/user/delete";

      if (isUserEntity && identifier === currentUserEmail) {
        await axios.post("/api/logout");
        localStorage.removeItem("userEmail");
        globalThis.location.replace("/login");
      }

    } catch (error) {
      console.error(error);
      alert("Failed to delete record");
    }
  };

  const handleEdit = async (row) => {
    try {
      const response = await axios.post("/api/get-entity",
        {
          endpoint: config.getEndpoint,
          identifier: row.identifier || row.username,
          paramName: config.getParam || "identifier",
        });

      setEditingRow(response.data.data);

    } catch (error) {
      console.error(error);
      alert("Failed to load record");
    }
  };

  const handleToggle = async (row) => {
    try {
      await axios.post("/api/toggle-status", {
        endpoint: toggleEndpoint,
        identifier: row.identifier
      });

      setTableRows((prev) => prev.map((item) => {
          if (item.id !== row.id) return item;

          const nextStatus = Number(item.status) === 1 ? 0 : 1;
          return { ...item, status: nextStatus };
        }));

    } catch (error) {
      console.error("Failed to update status", error);
      alert("Failed to update status");
    }
  };

  const filteredRows = tableRows.filter((row) =>
    Object.values(row).some((value) =>
      String(value).toLowerCase().includes(searchTerm.toLowerCase()))
  );

  const totalPages = Math.ceil(filteredRows.length / pageSize);
  const paginatedRows = filteredRows.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  return (
    <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-lg w-full">

      <div className="border-b border-slate-200 bg-slate-50 px-6 py-4">
        <SearchBar
          searchTerm={searchTerm}
          setSearchTerm={setSearchTerm}
        />
      </div>

      <div className="overflow-x-auto">
        <table className="w-full">

          <thead className="bg-linear-to-r from-slate-800 to-slate-700 text-white">
            <tr>
              {columns.map((column) => (
                <th
                  key={column.field}
                  className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider"
                >
                  {column.label}
                </th>
              ))}
              {showActions && (
                <th className="px-6 py-4 text-left text-xs font-semibold uppercase tracking-wider">
                  Actions
                </th>
              )}
            </tr>
          </thead>

          <tbody>
            {paginatedRows.length > 0 ? (
              paginatedRows.map((row, index) => {
                const serialNumber = (currentPage - 1) * pageSize + index + 1;

                return (
                  <tr
                    key={row.id || row.identifier || index}
                    className={`border-b border-slate-200 transition-all duration-200 hover:bg-blue-50
                    ${index % 2 === 0 ? "bg-white" : "bg-slate-50/40"}`}
                  >
                    {columns.map((column) => {
                      const value = column.field === "serialNumber" ? serialNumber : row[column.field];

                      return (
                        <td key={column.field} className="px-6 py-4 text-sm text-slate-700">
                          {column.field === "status" ? (
                            <button
                              type="button"
                              onClick={() => handleToggle(row)}
                              className={`relative h-6 w-12 rounded-full transition-all duration-300
                              ${Number(value) === 1 ? "bg-green-500" : "bg-red-500"}`}
                            >
                              <div
                                className={`absolute top-1 h-4 w-4 rounded-full bg-white transition-all duration-300
                                ${Number(value) === 1 ? "left-7" : "left-1"}`}
                              />
                            </button>
                          ) : (
                            <span className="font-medium">
                              {String(value ?? "")}
                            </span>
                          )}
                        </td>
                      );
                    })}

                    {showActions && (
                      <td className="px-6 py-4">
                        <div className="flex gap-2">
                          <button
                            type="button"
                            onClick={() => handleEdit(row)}
                            className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white"
                          >
                            Edit
                          </button>

                          <button
                            type="button"
                            onClick={() =>
                              handleDelete(
                                row[config.deleteParam || "identifier"]
                              )
                            }
                            className="rounded-lg bg-red-600 px-4 py-2 text-sm font-medium text-white"
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    )}
                  </tr>
                );
              })
            ) : (
              <tr>
                <td
                  colSpan={showActions ? columns.length + 1 : columns.length}
                  className="py-10 text-center text-slate-500"
                >
                  No records found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="border-t border-slate-200 bg-slate-50 px-6 py-4">
        <TablePagination
          currentPage={currentPage}
          totalPages={totalPages}
          pageSize={pageSize}
          setPageSize={setPageSize}
          setCurrentPage={setCurrentPage}
        />
      </div>

      {editingRow && (
        <EditModal
          row={editingRow}
          config={config}
          onClose={() => setEditingRow(null)}
          onSuccess={() => {
            setEditingRow(null);
            router.refresh();
          }}
        />
      )}
    </div>
  );
}

GenericTable.propTypes = {
  columns: PropTypes.arrayOf(
    PropTypes.shape({
      field: PropTypes.string.isRequired,
      label: PropTypes.string,
    })
  ).isRequired,
  rows: PropTypes.arrayOf(PropTypes.object).isRequired,
  toggleEndpoint: PropTypes.string,
  showActions: PropTypes.bool,
  config: PropTypes.shape({
    deleteEndpoint: PropTypes.string,
    deleteParam: PropTypes.string,
    getEndpoint: PropTypes.string,
    getParam: PropTypes.string,
  }).isRequired,
};