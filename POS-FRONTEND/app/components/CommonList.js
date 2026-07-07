"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import "./List.css";

/**
 * CommonList Component
 * @param {string} title - Title of the list
 * @param {Array} data - Data to display in the list
 * @param {Array} columns - Column configuration
 * @param {boolean} loading - Loading state
 * @param {string} error - Error message
 * @param {number} page - Current page number
 * @param {Function} setPage - Function to set current page
 * @param {number} sizePerPage - Items per page
 * @param {number} totalPages - Total number of pages
 * @param {string} searchTerm - Current search term
 * @param {Function} setSearchTerm - Function to set search term
 * @param {Function} onAdd - Add button handler
 * @param {string} addButtonText - Text for add button
 * @param {Array} addFields - Fields for add form
 * @param {Object} newItem - New item object
 * @param {Function} setNewItem - Function to set new item
 * @param {Function} handleAdd - Handler for adding item
 * @param {Object} editItem - Item being edited
 * @param {Function} setEditItem - Function to set edit item
 * @param {Function} handleUpdate - Handler for updating item
 * @param {Array} editFields - Fields for edit form
 * @param {Array} actions - Action buttons configuration
 * @param {string} emptyMessage - Message when no data
 */
const CommonList = ({
  title,
  data = [],
  columns = [],
  loading = false,
  error = "",
  page = 0,
  setPage,
  sizePerPage = 5,
  totalPages = 0,
  searchTerm = "",
  setSearchTerm,

  // ADD
  onAdd,
  addButtonText = "+ Add",
  addFields = [],
  newItem = {},
  setNewItem,
  handleAdd,

  // EDIT
  editItem,
  setEditItem,
  handleUpdate,
  editFields = [],

  // ACTIONS
  actions = [],

  emptyMessage = "No data found",
}) => {
  const safeData = Array.isArray(data) ? data : [];

  const [openMenu, setOpenMenu] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);

  const toggleMenu = (id) => {
    setOpenMenu((prev) => (prev === id ? null : id));
  };

  if (error) return <div style={{ color: "red" }}>{error}</div>;

  return (
    <div className="section">
      {/* HEADER */}
      <div className="tableHeader">
        <h2 className="sectionTitle">{title}</h2>

        <div className="headerActions">
  <div className="searchContainer">
    <span className="searchIcon">🔍</span>

    <input
  type="text"
  className="searchInput"
  placeholder={`Search ${title}...`}
  value={searchTerm}
  onChange={(e) => {
    setSearchTerm(e.target.value);

    if (setPage) {
      setPage(0);
    }
  }}
/>
  </div>

  {onAdd && (
    <button
      className="actionBtn"
      onClick={() => {
        setShowAddModal(true);
        onAdd();
      }}
    >
      {addButtonText}
    </button>
  )}
</div>
      </div>

      {/* TABLE */}
      <div className="tableWrapper">
        <table className="productTable">
          <thead>
            <tr>
              {columns.map((col) => (
                <th key={col.label}>{col.label}</th>
              ))}
              {actions.length > 0 && <th>Actions</th>}
            </tr>
          </thead>

          <tbody>
            {safeData.length > 0 ? (
              safeData.map((row, rowIndex) => {
                const rowId = row.id || row._id || rowIndex;

                return (
                  <tr key={rowId}>
                    {columns.map((col) => (
                      <td key={col.label}>
                        {col.render
                          ? col.render(row, rowIndex)
                          : row?.[col.key] ?? "-"}
                      </td>
                    ))}

                    {actions.length > 0 && (
                      <td>
                        <div className="actionMenu">
                          <button
                            className="menuBtn"
                            onClick={() => toggleMenu(rowId)}
                          >
                            ⋮
                          </button>

                          {openMenu === rowId && (
                            <div className="dropdownMenu">
                              {actions.map((action) => (
                                <button
                                  key={action.label}
                                  className="dropdownItem"
                                  onClick={() => {
                                    action.onClick(row);
                                    setOpenMenu(null);
                                  }}
                                >
                                  {action.label}
                                </button>
                              ))}
                            </div>
                          )}
                        </div>
                      </td>
                    )}
                  </tr>
                );
              })
            ) : (
              <tr>
                <td
                  colSpan={columns.length + (actions.length ? 1 : 0)}
                  className="emptyRow"
                >
                  {emptyMessage}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* PAGINATION */}
      {setPage && totalPages > 0 && (
        <div className="pagination">
          <button
            className="pageBtn"
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
          >
            Prev
          </button>

          {Array.from({ length: totalPages }, (_, i) => i).map((pageNum) => (
            <button
              key={`page_${pageNum}`}
              className={`pageBtn ${page === pageNum ? "activePage" : ""}`}
              onClick={() => setPage(pageNum)}
            >
              {pageNum + 1}
            </button>
          ))}

          <button
            className="pageBtn"
            disabled={page === totalPages - 1}
            onClick={() => setPage(page + 1)}
          >
            Next
          </button>
        </div>
      )}

      {/* ADD MODAL */}
      {showAddModal && (
        <div className="modalOverlay">
          <div className="modal">
            <h2>Add {title}</h2>

            {addFields.map((field) => (
              <div key={field.name}>
                {field.type === "select" ? (
                  <select
                    multiple={field.multiple}
                    value={
                      field.multiple
                        ? newItem[field.name] || []
                        : newItem[field.name] || ""
                    }
                    onChange={(e) => {
                      const value = field.multiple
                        ? Array.from(
                            e.target.selectedOptions,
                            (option) => option.value
                          )
                        : e.target.value;

                      setNewItem({
                        ...newItem,
                        [field.name]: value,
                      });
                    }}
                  >
                    {!field.multiple && (
                      <option value="">
                        Select {field.label}
                      </option>
                    )}

                    {field.options?.map((opt) => (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    ))}
                  </select>
                ) : (
                  <input
                    type={field.type || "text"}
                    placeholder={field.label}
                    value={newItem[field.name] || ""}
                    onChange={(e) =>
                      setNewItem({
                        ...newItem,
                        [field.name]: e.target.value,
                      })
                    }
                  />
                )}
              </div>
            ))}

            <div className="modalActions">
              <button
                onClick={async () => {
                  await handleAdd();
                  setShowAddModal(false);
                }}
              >
                Add
              </button>

              <button onClick={() => setShowAddModal(false)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* EDIT MODAL */}
      {editItem && (
        <div className="modalOverlay">
          <div className="modal">
            <h2>Edit {title}</h2>

            {editFields.map((field) => (
              <div key={field.name}>
                {field.type === "select" ? (
                  <select
                    multiple={field.multiple}
                    value={
                      field.multiple
                        ? editItem[field.name] || []
                        : editItem[field.name] || ""
                    }
                    onChange={(e) => {
                      const value = field.multiple
                        ? Array.from(
                            e.target.selectedOptions,
                            (option) => option.value
                          )
                        : e.target.value;

                      setEditItem({
                        ...editItem,
                        [field.name]: value,
                      });
                    }}
                  >
                    {!field.multiple && (
                      <option value="">
                        Select {field.label}
                      </option>
                    )}

                    {field.options?.map((opt) => (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    ))}
                  </select>
                ) : (
                  <input
                    type={field.type || "text"}
                    disabled={field.disabled}
                    value={editItem[field.name] || ""}
                    onChange={(e) =>
                      setEditItem({
                        ...editItem,
                        [field.name]: e.target.value,
                      })
                    }
                  />
                )}
              </div>
            ))}

            <div className="modalActions">
              <button onClick={handleUpdate}>
                Update
              </button>

              <button onClick={() => setEditItem(null)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

CommonList.propTypes = {
  title: PropTypes.string.isRequired,
  data: PropTypes.array,
  columns: PropTypes.array,
  loading: PropTypes.bool,
  error: PropTypes.string,
  page: PropTypes.number,
  setPage: PropTypes.func.isRequired,
  sizePerPage: PropTypes.number,
  totalPages: PropTypes.number,
  searchTerm: PropTypes.string,
  setSearchTerm: PropTypes.func.isRequired,
  onAdd: PropTypes.func,
  addButtonText: PropTypes.string,
  addFields: PropTypes.array,
  newItem: PropTypes.object,
  setNewItem: PropTypes.func.isRequired,
  handleAdd: PropTypes.func.isRequired,
  editItem: PropTypes.object,
  setEditItem: PropTypes.func.isRequired,
  handleUpdate: PropTypes.func.isRequired,
  editFields: PropTypes.array,
  actions: PropTypes.array,
  emptyMessage: PropTypes.string,
};

export default CommonList;