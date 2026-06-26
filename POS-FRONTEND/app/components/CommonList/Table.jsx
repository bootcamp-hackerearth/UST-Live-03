"use client";

import PropTypes from "prop-types";

const Table = ({
  data,
  columns,
  actions = [],
  emptyMessage,
  searchTerm,
}) => {
  const getActionClass = (type) => {
    switch (type) {
      case "delete":
        return "deleteBtn";

      case "cart":
        return "cartBtn";

      case "view":
        return "viewBtn";

      default:
        return "editBtn";
    }
  };

  const getActionIcon = (type) => {
    switch (type) {
      case "delete":
        return "🗑";

      case "cart":
        return "🛒";

      case "view":
        return "👁️";

      default:
        return "✏️";
    }
  };

  return (
    <table className="productTable">
      <thead>
        <tr>
          {columns.map((col) => (
            <th key={col.key || col.label}>
              {col.label}
            </th>
          ))}

          {actions.length > 0 && (
            <th>Actions</th>
          )}
        </tr>
      </thead>

      <tbody>
        {data.length > 0 ? (
          data.map((row, rowIndex) => (
            <tr
              key={
                row.identifier ||
                row.id ||
                row._id ||
                `row-${rowIndex}`
              }
            >
              {columns.map((col) => (
                <td key={col.key || col.label}>
                  {col.render
                    ? col.render(row, rowIndex)
                    : row?.[col.key] ?? "-"}
                </td>
              ))}

              {actions.length > 0 && (
                <td>
                  <div className="actionButtons">
                    {actions.map((action) => (
                      <button
                        key={action.key || action.label}
                        className={`tableActionBtn ${getActionClass(
                          action.type
                        )}`}
                        onClick={() => action.onClick(row)}
                        title={action.label}
                      >
                        {getActionIcon(action.type)}
                      </button>
                    ))}
                  </div>
                </td>
              )}
            </tr>
          ))
        ) : (
          <tr>
            <td
              colSpan={
                columns.length +
                (actions.length > 0 ? 1 : 0)
              }
              className="emptyRow"
            >
              {searchTerm
                ? "No matching records found"
                : emptyMessage}
            </td>
          </tr>
        )}
      </tbody>
    </table>
  );
};

Table.propTypes = {
  data: PropTypes.arrayOf(
    PropTypes.object
  ).isRequired,

  columns: PropTypes.arrayOf(
    PropTypes.shape({
      label: PropTypes.string.isRequired,
      key: PropTypes.string,
      render: PropTypes.func,
    })
  ).isRequired,

  actions: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string,
      label: PropTypes.string.isRequired,
      type: PropTypes.oneOf([
        "view",
        "edit",
        "delete",
        "cart",
      ]).isRequired,
      onClick: PropTypes.func.isRequired,
    })
  ),

  emptyMessage: PropTypes.string,

  searchTerm: PropTypes.string,
};

export default Table;