"use client"

import { useState , useEffect } from "react";
import PropTypes from "prop-types";
import "@/Styles/Layout.css";
import "@/Styles/Table.css";
import "@/Styles/Modal.css";
import "@/Styles/Form.css";
import "@/Styles/Pagination.css";
import "@/Styles/ReactSelect.css";
import Select from "react-select";


const CommonList = ({
  title,
  data = [],
  columns = [],
  error = "",
  page = 0,
  setPage,
  totalPages = 0,
  searchTerm = "",
  setSearchTerm,
  message = "",
  setMessage,
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
const [validationErrors, setValidationErrors] = useState({});
const [inputValue, setInputValue] = useState(searchTerm || "");
const [showAddModal, setShowAddModal] = useState(false);

useEffect(() => {
  setInputValue(searchTerm || "");
}, [searchTerm]);

useEffect(() => {
  const timer = setTimeout(() => {
    if (
      setSearchTerm &&
      inputValue !== searchTerm
    ) {
      setSearchTerm(inputValue);
    }
  }, 800);

  return () => clearTimeout(timer);
}, [inputValue, searchTerm, setSearchTerm]);

if (error) {
  return (
    <div style={{ color: "red" }}>
      {error}
    </div>
  );
}

 const validateForm = () => {
  const errors = {};

  addFields.forEach((field) => {
    if (
      field.required &&
      (
        newItem?.[field.name] === undefined ||
        newItem?.[field.name] === null ||
        newItem?.[field.name] === "" ||
        (Array.isArray(newItem?.[field.name]) &&
          newItem?.[field.name].length === 0)
      )
    ) {
      errors[field.name] = `${field.label} is required`;
    }
  });

  setValidationErrors(errors);

  return Object.keys(errors).length === 0;
};


  return (
    <div className="section">
      {/* HEADER */}
      <div className="tableHeader">
  <h2 className="sectionTitle">
    {title}
  </h2>

  <div className="headerActions">
    <input
  type="text"
  className="searchInput"
  placeholder={`Search ${title}...`}
  value={inputValue}
  onChange={(e) => {
    setInputValue(e.target.value);

    if (setPage) {
      setPage(0);
    }
  }}
/>

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
              {columns.map((col, i) => (
                <th key={col.key ?? col.label ?? i}>
                  {col.label}
                </th>
              ))}

              {actions.length > 0 && (
                <th>Actions</th>
              )}
            </tr>
          </thead>

          <tbody>
  {safeData.length > 0 ? (
    safeData.map((row, rowIndex) => (
      <tr key={row.id || row._id || rowIndex}>
        {columns.map((col, i) => (
          <td key={col.key ?? col.label ?? i}>
            {col.render
              ? col.render(row, rowIndex)
              : row?.[col.key] ?? "-"}
          </td>
        ))}

        {actions.length > 0 && (
          <td>
            <div className="rowActions">
              {actions.map((action, i) => (
                <button
                  key={action.label || action.name || `action-${i}`}
                  className={
                    action.label
                      .toLowerCase()
                      .includes("delete")
                      ? "deleteBtn"
                      : "editBtn"
                  }
                  onClick={() => action.onClick(row)}
                >
                  {action.label}
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
          (actions.length ? 1 : 0)
        }
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
            onClick={() =>
              setPage(page - 1)
            }
          >
            Prev
          </button>

          {Array.from({
            length: totalPages,
          }).map((_, i) => (
            <button
              key={i + 1}
              className={`pageBtn ${
                page === i
                  ? "activePage"
                  : ""
              }`}
              onClick={() => setPage(i)}
            >
              {i + 1}
            </button>
          ))}

          <button
            className="pageBtn"
            disabled={
              page === totalPages - 1
            }
            onClick={() =>
              setPage(page + 1)
            }
          >
            Next
          </button>
        </div>
      )}

      {/* ADD MODAL */}
     {showAddModal && (
  <dialog
    className="modal"
    open
    aria-labelledby="add-modal-title"
  >
    <h2 id="add-modal-title">Add {title}</h2>

    {message && (
      <div className="formMessage">
        {message}
      </div>
    )}

    {addFields.map((field, index) => (
      <div key={field.name || field.label || index}>
        {field.type === "select" ? (
          <>
            <Select
              options={field.options || []}
              isMulti={field.multiple}
              isSearchable
              placeholder={`Select ${field.label}`}
              value={
                field.multiple
                  ? (field.options || []).filter((opt) =>
                      (newItem?.[field.name] || []).includes(opt.value)
                    )
                  : (field.options || []).find(
                      (opt) => opt.value === newItem?.[field.name]
                    ) || null
              }
              onChange={(selected) => {
                const value = field.multiple
                  ? selected?.map((item) => item.value) || []
                  : selected?.value || "";

                setNewItem({
                  ...newItem,
                  [field.name]: value,
                });

                if (validationErrors[field.name]) {
                  setValidationErrors((prev) => ({
                    ...prev,
                    [field.name]: "",
                  }));
                }
              }}
            />

            {validationErrors[field.name] && (
              <div className="fieldError">
                {validationErrors[field.name]}
              </div>
            )}
          </>
        ) : (
          <>
            <input
              type={field.type || "text"}
              placeholder={field.label}
              value={newItem?.[field.name] || ""}
              onChange={(e) => {
                setNewItem({
                  ...newItem,
                  [field.name]: e.target.value,
                });

                if (validationErrors[field.name]) {
                  setValidationErrors((prev) => ({
                    ...prev,
                    [field.name]: "",
                  }));
                }
              }}
            />

            {validationErrors[field.name] && (
              <div className="fieldError">
                {validationErrors[field.name]}
              </div>
            )}
          </>
        )}
      </div>
    ))}

    <div className="modalActions">
      <button
        type="button"
        onClick={async () => {
          if (!validateForm()) {
            return;
          }

          const success = await handleAdd();

          if (success) {
            setValidationErrors({});
            setShowAddModal(false);
          }
        }}
      >
        Add
      </button>

      <button
        type="button"
        onClick={() => {
          setValidationErrors({});
          setShowAddModal(false);
        }}
      >
        Cancel
      </button>
    </div>
  </dialog>
)}
      {/* EDIT MODAL */}
     {editItem && (
  <dialog
    className="modal"
    open
    aria-labelledby="edit-modal-title"
  >
    <h2 id="edit-modal-title">
      Edit {title}
    </h2>

    {editFields.map((field, index) => (
      <div key={field.name || field.label || index}>
        {field.type === "select" ? (
          <Select
            options={field.options || []}
            isMulti={field.multiple}
            isSearchable
            placeholder={`Select ${field.label}`}
            value={
              field.multiple
                ? (field.options || []).filter((opt) =>
                    (editItem?.[field.name] || []).includes(
                      opt.value
                    )
                  )
                : (field.options || []).find(
                    (opt) =>
                      opt.value ===
                      editItem?.[field.name]
                  ) || null
            }
            onChange={(selected) => {
              const value = field.multiple
                ? selected?.map(
                    (item) => item.value
                  ) || []
                : selected?.value || "";

              setEditItem({
                ...editItem,
                [field.name]: value,
              });
            }}
          />
        ) : (
          <input
            type={field.type || "text"}
            disabled={field.disabled}
            value={
              editItem?.[field.name] || ""
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
        type="button"
        onClick={handleUpdate}
      >
        Update
      </button>

      <button
        type="button"
        onClick={() => {
          setEditItem(null);
        }}
      >
        Cancel
      </button>
    </div>
  </dialog>
)}
</div>
  );
};

CommonList.propTypes = {
  title: PropTypes.string.isRequired,
  data: PropTypes.array,
  columns: PropTypes.array,
  error: PropTypes.string,
  page: PropTypes.number,
  setPage: PropTypes.func,
  totalPages: PropTypes.number,
  searchTerm: PropTypes.string,
  setSearchTerm: PropTypes.func,
  message: PropTypes.string,
  setMessage: PropTypes.func,
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
};

export default CommonList;