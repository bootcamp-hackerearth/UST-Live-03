"use client";

import PropTypes from "prop-types";
import { useState, useEffect, useMemo } from "react";
import "./List.css";

const CommonList = ({
  title,
  data = [],
  columns = [],
  loading = false,
  error = "",
  page = 0,
  setPage,
  totalPages = 0,

  // SEARCH
  search,
  setSearch,

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

  popupTitle,

  actions = [],

  emptyMessage = "No data found",
}) => {
  const safeData = Array.isArray(data) ? data : [];

  const [openMenu, setOpenMenu] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [addError, setAddError] = useState("");

  // SEARCH STATE
  const [internalSearch, setInternalSearch] = useState("");
  const [localSearch, setLocalSearch] = useState(search || "");

  const isControlledSearch =
    typeof setSearch === "function" && typeof search === "string";

  const searchValue = isControlledSearch ? localSearch : internalSearch;

  const pageNumbers = Array.from(
    { length: Math.max(0, totalPages) },
    (_, n) => n,
  );

  // Debounce parent updates
  useEffect(() => {
    if (!isControlledSearch) return;

    const timer = setTimeout(() => {
      if (search !== localSearch) {
        setSearch(localSearch);
        setPage?.(0);
      }
    }, 400);

    return () => clearTimeout(timer);
  }, [localSearch, search, setSearch, setPage, isControlledSearch]);

  const toggleMenu = (id) => {
    setOpenMenu((prev) => (prev === id ? null : id));
  };

  const filteredData = useMemo(() => {
    const term = searchValue.toLowerCase();

    return safeData.filter((row) =>
      Object.values(row || {}).some((value) =>
        String(value || "")
          .toLowerCase()
          .includes(term),
      ),
    );
  }, [safeData, searchValue]);

  if (loading) return <div>Loading...</div>;

  if (error) {
    return <div style={{ color: "red" }}>{error}</div>;
  }
  const formatAuditDate = (value) => {
    if (!value) return "-";

    try {
      return new Date(value).toLocaleString();
    } catch {
      return value;
    }
  };

  return (
    <div className="section">
      {/* HEADER */}
      <div className="tableHeader">
        <h2 className="sectionTitle">{title}</h2>

        <div className="headerActions">
          <input
            type="text"
            className="searchInput"
            placeholder={`Search ${title}...`}
            value={searchValue}
            onChange={(e) => {
              const value = e.target.value;

              if (isControlledSearch) {
                setLocalSearch(value);
              } else {
                setInternalSearch(value);
              }
            }}
          />

          {onAdd && (
            <button
              className="actionBtn"
              onClick={() => {
                setAddError("");
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
              {columns.map((col, i) => (
                <th key={col.key ?? col.label ?? `col-${i}`}>{col.label}</th>
              ))}
              {actions.length > 0 && <th>Actions</th>}
            </tr>
          </thead>

          <tbody>
            {filteredData.length > 0 ? (
              filteredData.map((row, rowIndex) => {
                const rowId =
                  row.id ??
                  row._id ??
                  row.identifier ??
                  row.username ??
                  `row-${rowIndex}`;

                return (
                  <tr key={rowId}>
                    {columns.map((col, i) => {
                      const cellKey = col.key ?? col.label ?? `col-${i}`;
                      let cellValue;

                      if (col.render) {
                        cellValue = col.render(row);
                      } else if (
                        ["createdOn", "modifiedOn"].includes(col.key)
                      ) {
                        cellValue = formatAuditDate(row?.[col.key]);
                      } else {
                        cellValue = row?.[col.key] ?? "-";
                      }

                      return <td key={cellKey}>{cellValue}</td>;
                    })}

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
                              {actions.map((action, idx) => (
                                <button
                                  key={action.label ?? `action-${idx}`}
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
                  colSpan={columns.length + (actions.length > 0 ? 1 : 0)}
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

          {pageNumbers.map((p) => (
            <button
              key={p}
              className={`pageBtn ${page === p ? "activePage" : ""}`}
              onClick={() => setPage(p)}
            >
              {p + 1}
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
          <div className="modal" style={{ width: "420px", padding: "18px" }}>
            <h2>{popupTitle || `Add ${title}`}</h2>

            {addError && (
              <div
                style={{
                  background: "#fee2e2",
                  color: "#dc2626",
                  padding: "10px",
                  borderRadius: "8px",
                  marginBottom: "12px",
                  fontSize: "14px",
                }}
              >
                {addError}
              </div>
            )}

            {addFields.map((field, index) => {
              const key = field.name ?? field.label ?? index;
              const addFieldValue = field.multiple
                ? newItem?.[field.name] || []
                : newItem?.[field.name] || "";

              let fieldContent;

              if (field.type === "select") {
                fieldContent = (
                  <select
                    multiple={field.multiple}
                    value={addFieldValue}
                    onChange={(e) => {
                      const value = field.multiple
                        ? [...e.target.selectedOptions].map((o) => o.value)
                        : e.target.value;

                      setNewItem({
                        ...newItem,
                        [field.name]: value,
                      });
                    }}
                  >
                    {!field.multiple && <option value="">{field.label}</option>}

                    {field.options?.map((opt, i) => (
                      <option
                        key={opt.value ?? opt.label ?? `opt-${i}`}
                        value={opt.value}
                      >
                        {opt.label}
                      </option>
                    ))}
                  </select>
                );
              } else if (field.type === "checkbox") {
                fieldContent = (
                  <div className="toggleContainer">
                    <span>{field.label}</span>

                    <label className="switch" aria-label={field.label}>
                      <input
                        type="checkbox"
                        checked={newItem?.[field.name] || false}
                        onChange={(e) =>
                          setNewItem({
                            ...newItem,
                            [field.name]: e.target.checked,
                          })
                        }
                      />
                      <span className="slider round"></span>
                    </label>
                  </div>
                );
              } else {
                fieldContent = (
                  <input
                    type={field.type || "text"}
                    placeholder={field.label}
                    value={newItem?.[field.name] || ""}
                    onChange={(e) =>
                      setNewItem({
                        ...newItem,
                        [field.name]: e.target.value,
                      })
                    }
                  />
                );
              }

              return (
                <div key={key} style={{ marginBottom: "10px" }}>
                  {fieldContent}
                </div>
              );
            })}

            <div className="modalActions">
              <button
                onClick={async () => {
                  setAddError("");

                  if (newItem?.identifier) {
                    const duplicateExists = safeData.some(
                      (item) =>
                        String(item?.identifier || "")
                          .trim()
                          .toLowerCase() ===
                        String(newItem.identifier).trim().toLowerCase(),
                    );

                    if (duplicateExists) {
                      setAddError(
                        `${title} with identifier "${newItem.identifier}" already exists`,
                      );
                      return;
                    }
                  }

                  await handleAdd();
                  setShowAddModal(false);
                }}
              >
                Add
              </button>

              <button
                onClick={() => {
                  setAddError("");
                  setShowAddModal(false);
                }}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* EDIT MODAL */}
      {editItem && (
        <div className="modalOverlay">
          <div className="modal" style={{ width: "420px", padding: "18px" }}>
            <h2>{editItem?.formTitle || `Edit ${title}`}</h2>

            {editFields.map((field, index) => {
              const key = field.name ?? field.label ?? index;
              const editFieldValue = field.multiple
                ? editItem?.[field.name] || []
                : editItem?.[field.name] || "";

              let fieldContent;

              if (field.type === "select") {
                fieldContent = (
                  <select
                    multiple={field.multiple}
                    value={editFieldValue}
                    onChange={(e) => {
                      const value = field.multiple
                        ? [...e.target.selectedOptions].map((o) => o.value)
                        : e.target.value;

                      setEditItem({
                        ...editItem,
                        [field.name]: value,
                      });
                    }}
                  >
                    {!field.multiple && <option value="">{field.label}</option>}

                    {field.options?.map((opt, i) => (
                      <option
                        key={opt.value ?? opt.label ?? `opt-${i}`}
                        value={opt.value}
                      >
                        {opt.label}
                      </option>
                    ))}
                  </select>
                );
              } else if (field.type === "checkbox") {
                fieldContent = (
                  <div className="toggleContainer">
                    <span>{field.label}</span>

                    <label className="switch" aria-label={field.label}>
                      <input
                        type="checkbox"
                        checked={editItem?.[field.name] || false}
                        onChange={(e) =>
                          setEditItem({
                            ...editItem,
                            [field.name]: e.target.checked,
                          })
                        }
                      />
                      <span className="slider round"></span>
                    </label>
                  </div>
                );
              } else {
                fieldContent = (
                  <input
                    type={field.type || "text"}
                    placeholder={field.label}
                    disabled={field.disabled}
                    value={editItem?.[field.name] || ""}
                    onChange={(e) =>
                      setEditItem({
                        ...editItem,
                        [field.name]: e.target.value,
                      })
                    }
                  />
                );
              }

              return (
                <div key={key} style={{ marginBottom: "10px" }}>
                  {fieldContent}
                </div>
              );
            })}

            <div className="modalActions">
              <button
                onClick={() => {
                  if (editItem?.isNew && editItem?.identifier) {
                    const duplicateExists = safeData.some(
                      (item) =>
                        String(item?.identifier || "")
                          .trim()
                          .toLowerCase() ===
                        String(editItem.identifier).trim().toLowerCase(),
                    );

                    if (duplicateExists) {
                      alert(
                        `${title} with identifier "${editItem.identifier}" already exists`,
                      );
                      return;
                    }
                  }

                  handleUpdate();
                }}
              >
                {editItem?.isNew ? "Add" : "Update"}
              </button>

              <button onClick={() => setEditItem(null)}>Cancel</button>
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
  columns: PropTypes.arrayOf(
    PropTypes.shape({
      label: PropTypes.node,
      key: PropTypes.string,
      render: PropTypes.func,
    }),
  ),
  loading: PropTypes.bool,
  error: PropTypes.string,
  page: PropTypes.number,
  setPage: PropTypes.func,
  totalPages: PropTypes.number,
  search: PropTypes.string,
  setSearch: PropTypes.func,
  onAdd: PropTypes.func,
  addButtonText: PropTypes.string,
  addFields: PropTypes.array,
  newItem: PropTypes.object,
  setNewItem: PropTypes.func,
  handleAdd: PropTypes.func,
  editItem: PropTypes.object,
  setEditItem: PropTypes.func,
  handleUpdate: PropTypes.func,
  editFields: PropTypes.array,
  popupTitle: PropTypes.string,
  actions: PropTypes.array,
  emptyMessage: PropTypes.string,
};

export default CommonList;
