"use client";

import { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { Edit, Trash2 } from "lucide-react";

export default function CommonList({
  data = [],
  columns = [],
  onEdit,
  onDelete,
  onSearch,
}) {
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);

  const pageSize = 5;

  const totalPages = Math.ceil(data.length / pageSize);

  const paginatedData = data.slice(
    page * pageSize,
    page * pageSize + pageSize
  );

  useEffect(() => {
    setPage(0);
  }, [data]);

  return (
    <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
      <div className="flex justify-end items-center p-3">
        <input
          type="text"
          placeholder="Search..."
          value={search}
          onChange={(e) => {
            console.log("INPUT =", e.target.value);

            const keyword = e.target.value;
            setSearch(keyword);
            setPage(0);
            onSearch?.(keyword);
          }}
          className="border px-3 py-1 rounded-md text-sm"
        />
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b border-slate-200">
            <tr>
              {columns.map((col) => (
                <th
                  key={col.accessor || col.header}
                  className="px-4 py-3 text-left text-slate-600 font-medium"
                >
                  {col.header}
                </th>
              ))}

              {(onEdit || onDelete) && (
                <th className="px-4 py-3 text-right text-slate-600 font-medium">
                  Actions
                </th>
              )}
            </tr>
          </thead>

          <tbody>
            {paginatedData.length === 0 ? (
              <tr>
                <td
                  colSpan={columns.length + (onEdit || onDelete ? 1 : 0)}
                  className="text-center py-10 text-slate-400"
                >
                  No data found
                </td>
              </tr>
            ) : (
              paginatedData.map((row) => (
                <tr
                  key={
                    row.identifier ||
                    row.username ||
                    row.name ||
                    JSON.stringify(row)
                  }
                  className="border-b border-slate-100 hover:bg-slate-50 transition"
                >
                  {columns.map((col) => {
                    let value;

                    if (col.render) {
                      value = col.render(row);
                    } else if (col.cell) {
                      value = col.cell(row);
                    } else {
                      value = row[col.accessor];
                    }

                    return (
                      <td
                        key={col.accessor || col.header}
                        className="px-4 py-3 text-slate-700"
                      >
                        {value}
                      </td>
                    );
                  })}

                  {(onEdit || onDelete) && (
                    <td className="px-4 py-3">
                      <div className="flex justify-end gap-2">
                        {onEdit && (
                          <button
                            onClick={() => onEdit(row)}
                            className="p-2 rounded-md hover:bg-blue-50 text-blue-600"
                          >
                            <Edit size={16} />
                          </button>
                        )}

                        {onDelete && (
                          <button
                            onClick={() => onDelete(row)}
                            className="p-2 rounded-md hover:bg-red-50 text-red-600"
                          >
                            <Trash2 size={16} />
                          </button>
                        )}
                      </div>
                    </td>
                  )}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {data.length > 0 && (
        <div className="flex justify-center items-center gap-4 p-4">
          <button
            onClick={() => setPage((p) => Math.max(p - 1, 0))}
            disabled={page === 0}
            className="bg-gray-400 text-white px-3 py-1 rounded disabled:opacity-50"
          >
            Prev
          </button>

          <span className="text-sm text-slate-600">
            Page {page + 1} of {totalPages}
          </span>

          <button
            onClick={() =>
              setPage((p) => Math.min(p + 1, totalPages - 1))
            }
            disabled={page >= totalPages - 1}
            className="bg-blue-500 text-white px-3 py-1 rounded disabled:opacity-50"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}

CommonList.propTypes = {
  data: PropTypes.array,
  columns: PropTypes.arrayOf(
    PropTypes.shape({
      accessor: PropTypes.string,
      header: PropTypes.string,
      render: PropTypes.func,
      cell: PropTypes.func,
    })
  ),
  onEdit: PropTypes.func,
  onDelete: PropTypes.func,
  onSearch: PropTypes.func,
};