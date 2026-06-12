
"use client";
import { useState, useEffect, useRef } from "react";
import Select from "react-select";
import "./CommonList.css";
import PropTypes from "prop-types";
const CommonList = ({
  title,
  data = [],
  columns = [],
  loading = false,
  error = "",
  page = 0,
  setPage,
  totalPages = 1,
  onAdd,
  addButtonText = "+ Add",
  addFields = [],
  newItem = {},
  setNewItem,
  handleAdd,
  editItem,
  setEditItem,
  handleUpdate,
  editFields = [],
  actions = [],
  emptyMessage = "No data found",
  searchTerm = "",
  setSearchTerm,
}) => {
  const selectStyles = {
    control: (provided, state) => ({
      ...provided,
      minHeight: "42px",
      borderRadius: "8px",
      borderColor: state.isFocused
        ? "#4f46e5"
        : "#d1d5db",
      boxShadow: "none",
      cursor: "pointer",
      "&:hover": {
        borderColor: "#4f46e5",
      },
    }),
    valueContainer: (provided) => ({
      ...provided,
      padding: "2px 10px",
    }),
    placeholder: (provided) => ({
      ...provided,
      color: "#9ca3af",
      fontSize: "14px",
    }),
    singleValue: (provided) => ({
      ...provided,
      color: "#111827",
      fontSize: "14px",
    }),
    menu: (provided) => ({
      ...provided,
      borderRadius: "8px",
      overflow: "hidden",
      zIndex: 9999,
    }),
    option: (provided, state) => {
      let backgroundColor = "#fff";

      if (state.isSelected) {
        backgroundColor = "#4f46e5";
      } else if (state.isFocused) {
        backgroundColor = "#eef2ff";
      }

      return {
        ...provided,
        backgroundColor,
        color: state.isSelected ? "#fff" : "#111827",
        cursor: "pointer",
        fontSize: "14px",
      };
    },
    multiValue: (provided) => ({
      ...provided,
      backgroundColor: "#eef2ff",
      borderRadius: "6px",
    }),
    multiValueLabel: (provided) => ({
      ...provided,
      color: "#4f46e5",
      fontWeight: "500",
    }),
    multiValueRemove: (provided) => ({
      ...provided,
      color: "#4f46e5",
      ":hover": {
        backgroundColor: "#4f46e5",
        color: "#fff",
      },
    }),
  };

  const safeData = Array.isArray(data)
    ? data
    : [];

  const [showAddModal, setShowAddModal] =
    useState(false);

  const [modalError, setModalError] =
    useState("");

  const [localSearch, setLocalSearch] =
    useState(searchTerm || "");

  const searchInputRef = useRef(null);

  useEffect(() => {
    searchInputRef.current?.focus();
  }, [searchTerm]);

  useEffect(() => {
    const timer = setTimeout(() => {
      setSearchTerm(localSearch);
    }, 300);

    return () => clearTimeout(timer);
  }, [localSearch]);

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return (
      <div style={{ color: "red" }}>
        {error}
      </div>
    );
  }
  return (
    <div className="section">
      {/* HEADER */}
      <div className="tableHeader">
        <h2 className="sectionTitle">
          {title}
        </h2>

        <div className="headerActions">
          <input
            ref={searchInputRef}
            type="text"
            className="searchInput"
            placeholder="Search..."
            value={localSearch}
            onChange={(e) =>
              setLocalSearch(e.target.value)
            }
          />

          {onAdd && (
            <button
              className="actionBtn"
              onClick={() => {
                setModalError("");
                if (setNewItem) {
                  setNewItem({});
                }
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
              {columns.map(
                (col) => (
                  <th key={col.label}>
                    {col.label}
                  </th>
                )
              )}
              {actions.length > 0 && (
                <th>Actions</th>
              )}
            </tr>
          </thead>
          <tbody>
            {safeData.length > 0 ? (
              safeData.map((row, rowIndex) => {
                  return (
                    <tr key={row.id || row._id || rowIndex}>
                      {/* DATA COLUMNS */}
                      {columns.map(
                        (
                          col,
                          colIndex
                        ) => (
                          <td
                            key={col.key ?? col.label ?? colIndex}
                          >
                            {col.render
                              ? col.render(
                                  row,
                                  rowIndex
                                )
                              : row?.[
                                  col.key
                                ] ?? "-"}
                          </td>
                        )
                      )}
                      {/* ACTIONS */}
                        {actions.length > 0 && (
                          <td>
                            <div className="actionButtons">
                              {actions.map(
                                (action, actionIndex) => (
                                  <button
                                    key={`${row.id ?? row._id ?? rowIndex}-${action.key ?? action.label ?? action.type}`}
                                    className={`tableActionBtn ${
                                      action.type === "delete"
                                        ? "deleteBtn"
                                        : "editBtn"
                                    }`}
                                    onClick={() =>
                                      action.onClick(row)
                                    }
                                    title={action.label}
                                  >
                                    {action.type === "delete"
                                      ? "🗑"
                                      : "✏️"}
                                  </button>
                                )
                              )}
                            </div>
                          </td>
                        )}
                    </tr>
                  );
                }
              )
            ) : (
              <tr>
                <td
                  colSpan={
                    columns.length +
                    (actions.length >
                    0
                      ? 1
                      : 0)
                  }
                  className="emptyRow"
                >
                  {searchTerm  ? "No matching records found" : emptyMessage}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      {/* PAGINATION */}
      {setPage &&
        totalPages > 0 && (
          <div className="pagination">
            {/* PREVIOUS */}
            <button
              className="pageBtn"
              disabled={page === 0}
              onClick={() =>
                setPage(page - 1)
              }
            >
              ←
            </button>
            {/* PAGE NUMBERS */}
            {Array.from({ length: totalPages }).map(
              (_, index) => (
                <button
                  key={`page-${index + 1}`}
                  className={`pageBtn ${
                    page === index
                      ? "activePage"
                      : ""
                  }`}
                  onClick={() =>
                    setPage(index)
                  }
                >
                  {index + 1}
                </button>
              )
            )}
            {/* NEXT */}
            <button
              className="pageBtn"
              disabled={
                page ===
                totalPages - 1
              }
              onClick={() =>
                setPage(page + 1)
              }
            >
              →
            </button>
          </div>
        )}
      {/* ADD MODAL */}
      {showAddModal && (
        <div className="modalOverlay">
          <div
            className="modalBox"
          >
            <h2 className="modalTitle">
              Add {title}
            </h2>
            {modalError && (
              <div className="errorMessage">
                {modalError}
              </div>
            )}
            {addFields.map((field) => (
              <div key={field.name}>
                {field.type ===
                "select" ? (
                  <Select
                    styles={selectStyles}
                    options={
                      field.options || []
                    }
                    isSearchable={true}
                    isMulti={
                      field.multiple
                    }
                    placeholder={`Select ${field.label}`}
                    value={
                      field.multiple
                        ? (
                            field.options ||
                            []
                          ).filter(
                            (opt) =>
                              (
                                newItem?.[
                                  field
                                    .name
                                ] || []
                              ).includes(
                                opt.value
                              )
                          )
                        : (
                            field.options ||
                            []
                          ).find(
                            (opt) =>
                              opt.value ===
                              newItem?.[
                                field
                                  .name
                              ]
                          ) || null
                    }
                    onChange={(
                      selected
                    ) => {
                      const value =
                        field.multiple
                          ? selected?.map(
                              (
                                item
                              ) =>
                                item.value
                            ) || []
                          : selected?.value ||
                            "";
                      setNewItem({
                        ...newItem,
                        [field.name]:
                          value,
                      });
                    }}
                  />
                ) : (
                  <input
                    className="inputField"
                    type={
                      field.type ||
                      "text"
                    }
                    placeholder={
                      field.label
                    }
                    value={
                      newItem?.[
                        field.name
                      ] || ""
                    }
                    onChange={(e) =>
                      setNewItem({
                        ...newItem,
                        [field.name]:
                          e.target.value,
                      })
                    }
                  />
                )}
              </div>
            ))}
            <div className="modalActions">
              <button
                className="updateBtn"
                onClick={async () => {
                  setModalError("");
                  const hasEmptyField = addFields.some((field) => {
                    if (field.required === false) {
                      return false;
                    }

                    const value = newItem?.[field.name];

                    return (
                      value === undefined ||
                      value === null ||
                      value === "" ||
                      (Array.isArray(value) && value.length === 0)
                    );
                  });
                  if (hasEmptyField) {
                    setModalError("Please fill all fields");
                    return;
                  }
                  try {
                    const response = await handleAdd();
                    if (response?.success === false) {
                      setModalError(response.message);
                      return;
                    }
                    setShowAddModal(false);
                  } catch (err) {
                    setModalError(
                      err.message || "Operation failed"
                    );
                  }
                }}
              >
                Add
              </button>
              <button
                className="cancelBtn"
                onClick={() =>
                  setShowAddModal(false)
                }
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
          <div
            className="modalBox"
          >
            <h2 className="modalTitle">
              Edit {title}
            </h2>
            {modalError && (
              <div className="errorMessage">
                {modalError}
              </div>
            )}
            {editFields.map((field) => (
              <div key={field.name}>
                {field.type ===
                "select" ? (
                  <Select
                    styles={selectStyles}
                    options={
                      field.options || []
                    }
                    isSearchable={true}
                    isMulti={
                      field.multiple
                    }
                    placeholder={`Select ${field.label}`}
                    value={
                      field.multiple
                        ? (
                            field.options ||
                            []
                          ).filter(
                            (opt) =>
                              (
                                editItem?.[
                                  field
                                    .name
                                ] || []
                              ).includes(
                                opt.value
                              )
                          )
                        : (
                            field.options ||
                            []
                          ).find(
                            (opt) =>
                              opt.value ===
                              editItem?.[
                                field
                                  .name
                              ]
                          ) || null
                    }
                    onChange={(
                      selected
                    ) => {
                      const value =
                        field.multiple
                          ? selected?.map(
                              (
                                item
                              ) =>
                                item.value
                            ) || []
                          : selected?.value ||
                            "";
                      setEditItem({
                        ...editItem,
                        [field.name]:
                          value,
                      });
                    }}
                  />
                ) : (
                  <input
                    className="inputField"
                    disabled={
                      field.disabled
                    }
                    value={
                      editItem?.[
                        field.name
                      ] || ""
                    }
                    onChange={(e) =>
                      setEditItem({
                        ...editItem,
                        [field.name]:
                          e.target.value,
                      })
                    }
                  />
                )}
              </div>
            ))}
            <div className="modalActions">
              <button
                className="updateBtn"
                onClick={async () => {
                  setModalError("");
                  const hasEmptyField = editFields.some((field) => {
                    if (field.required === false) {
                      return false;
                    }

                    const value = editItem?.[field.name];

                    return (
                      value === undefined ||
                      value === null ||
                      value === "" ||
                      (Array.isArray(value) && value.length === 0)
                    );
                  });
                  if (hasEmptyField) {
                    setModalError("Please fill all fields");
                    return;
                  }
                  try {
                    const response = await handleUpdate();
                    if (response?.success === false) {
                      setModalError(response.message);
                      return;
                    }
                    setEditItem(null);
                  } catch (err) {
                    setModalError(
                      err.message || "Operation failed"
                    );
                  }
                }}
              >
                Update
              </button>
              <button
                className="cancelBtn"
                onClick={() =>
                  setEditItem(null)
                }
              >
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
  title: PropTypes.string,
  data: PropTypes.array,
  columns: PropTypes.array,
  loading: PropTypes.bool,
  error: PropTypes.string,
  page: PropTypes.number,
  setPage: PropTypes.func,
  totalPages: PropTypes.number,
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
  actions: PropTypes.array,
  emptyMessage: PropTypes.string,
  searchTerm: PropTypes.string,
  setSearchTerm: PropTypes.func,
};

export default CommonList;