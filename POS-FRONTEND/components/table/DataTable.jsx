"use client";

import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import StatusToggle from "./StatusToggle";

export default function DataTable({
  items,
  columns,
  showToggle,
  editPath,
  onToggleStatus,
  onDelete,
  identifierField = "identifier",
}) {
  const router = useRouter();

  return (
    <div className="bg-white rounded-[30px] border border-gray-200 overflow-hidden shadow-sm">
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead className="bg-[#f8fafc] border-b border-gray-200">
            <tr>
              {columns.map((column,index) => (
                <th
                  key={column.field || column.header || index}
                  className="text-left px-6 py-5 text-sm font-semibold text-gray-500"
                >
                  {column.header}
                </th>
              ))}

              {showToggle && (
                <th className="text-left px-6 py-5 text-sm font-semibold text-gray-500">
                  Status
                </th>
              )}

              <th className="text-center px-6 py-5 text-sm font-semibold text-gray-500">
                Actions
              </th>
            </tr>
          </thead>

          <tbody>
            {items.map((item,index) => (
              <tr
                key={item[identifierField] || index}
                className="border-b border-gray-100 hover:bg-[#fafcff]"
              >
                {columns.map((column,i) => (
                  <td
                    key={`${item[identifierField] || index}-${column.field || i}`}
                    className="px-6 py-5 text-gray-700"
                  >
                    {Array.isArray(item[column.field]) ? (
                      <div className="flex flex-wrap gap-2">
                        {item[column.field].map((value,index) => (
                          <span
                            key={`${item[identifierField]}-${index}`}
                            className="px-3 py-1 rounded-xl bg-blue-100 text-blue-700 text-sm"
                          >
                            {value}
                          </span>
                        ))}
                      </div>
                    ) : (
                      item[column.field]
                    )}
                  </td>
                ))}

                {showToggle && (
                  <td className="px-6 py-5">
                    <StatusToggle
                      checked={item.status}
                      onChange={() =>
                        onToggleStatus(
                          item[identifierField],
                          item.status
                        )
                      }
                    />
                  </td>
                )}

                <td className="px-6 py-5">
                  <div className="flex items-center justify-center gap-3">
                    <button
                      onClick={() =>
                        router.push(
                          `${editPath}/${encodeURIComponent(
                            item[identifierField]
                          )}`
                        )
                      }
                      className="h-10 w-10 rounded-xl border border-gray-200"
                    >
                      ✎
                    </button>

                    <button
                      onClick={() => onDelete(item)}
                      className="h-10 w-10 rounded-xl border border-gray-200"
                    >
                      🗑
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

DataTable.propTypes = {
  items: PropTypes.array.isRequired,
  columns: PropTypes.array.isRequired,
  showToggle: PropTypes.bool,
  editPath: PropTypes.string,
  onToggleStatus: PropTypes.func,
  onDelete: PropTypes.func,
  identifierField: PropTypes.string,
};