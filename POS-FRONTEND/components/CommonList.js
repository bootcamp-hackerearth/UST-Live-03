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
  onRowClick,
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
const getNestedValue = (obj, path) => {
  return path.split(".").reduce((acc, part) => {
    return acc ? acc[part] : "";
  }, obj);
};

const setNestedValue = (obj, path, value) => {
  const keys = path.split(".");
  const lastKey = keys.pop();

  const newObj = { ...obj };

  let current = newObj;

  keys.forEach((key) => {
    if (current[key] && typeof current[key] === "object") {
      current[key] = { ...current[key] };
    } else {
      current[key] = {};
    }

    current = current[key];
  });

  current[lastKey] = value;

  return newObj;
};
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
        setMessage(""); 
        setValidationErrors({});
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
      <tr
  key={row.id || row._id || rowIndex}
  onClick={() => onRowClick?.(row)}
  style={{
    cursor: onRowClick
      ? "pointer"
      : "default",
  }}
>
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
                  onClick={(e) => {
  e.stopPropagation();
  action.onClick(row);
}}
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
{(() => {
  const defaultFields = [];
  const billingFields = [];
  const shippingFields = [];

  addFields.forEach((field) => {
    if (field.name?.startsWith("billing.")) {
      billingFields.push(field);
    } else if (field.name?.startsWith("shipping.")) {
      shippingFields.push(field);
    } else {
      defaultFields.push(field);
    }
  });

  const clearFieldError = (fieldName) => {
    if (validationErrors[fieldName]) {
      setValidationErrors((prev) => ({
        ...prev,
        [fieldName]: "",
      }));
    }
  };

  const handleSelectChange = (field, selected) => {
    const value = field.multiple
      ? selected?.map((item) => item.value) || []
      : selected?.value || "";

    setNewItem({
      ...newItem,
      [field.name]: value,
    });

    clearFieldError(field.name);
  };

  const handleInputChange = (field, value) => {
    setNewItem(setNestedValue(newItem, field.name, value));
    clearFieldError(field.name);
  };

  const getSelectValue = (field) => {
    return field.multiple
      ? (field.options || []).filter((opt) =>
          (newItem?.[field.name] || []).includes(opt.value)
        )
      : (field.options || []).find(
          (opt) => opt.value === newItem?.[field.name]
        ) || null;
  };

  const renderField = (field) => (
    <div key={field.name || field.label}>
      {field.type === "select" ? (
        <>
          <Select
            options={field.options || []}
            isMulti={field.multiple}
            isSearchable
            placeholder={`Select ${field.label}`}
            value={getSelectValue(field)}
            onChange={(selected) => handleSelectChange(field, selected)}
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
            value={getNestedValue(newItem, field.name) || ""}
            onChange={(e) => handleInputChange(field, e.target.value)}
          />

          {validationErrors[field.name] && (
            <div className="fieldError">
              {validationErrors[field.name]}
            </div>
          )}
        </>
      )}
    </div>
  );

  return (
    <>
      {/* DEFAULT FIELDS */}
      {defaultFields.map(renderField)}

      {/* BILLING SECTION */}
      {billingFields.length > 0 && (
        <details>
          <summary>Billing Address</summary>
          {billingFields.map(renderField)}
        </details>
      )}

      {/* SHIPPING SECTION */}
      {shippingFields.length > 0 && (
        <details>
          <summary>Shipping Address</summary>
          {shippingFields.map(renderField)}
        </details>
      )}
    </>
  );
})()}

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
          setMessage("");
          setShowAddModal(false);
        }}
      >
        Cancel
      </button>
    </div>
  </dialog>
)}
      {/* EDIT MODAL */}
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

    {message && (
      <div className="formMessage">
        {message}
      </div>
    )}

    {(() => {
  const defaultFields = [];
  const billingFields = [];
  const shippingFields = [];

  editFields.forEach((field) => {
    if (field.name?.startsWith("billing.")) {
      billingFields.push(field);
    } else if (field.name?.startsWith("shipping.")) {
      shippingFields.push(field);
    } else {
      defaultFields.push(field);
    }
  });

  const getSelectValue = (field, item) => {
    if (field.multiple) {
      return (field.options || []).filter((opt) =>
        (getNestedValue(item, field.name) || []).includes(opt.value)
      );
    }

    return (
      (field.options || []).find((opt) => opt.value === getNestedValue(item, field.name)) || null
    );
  };

  const handleSelectChange = (selected, field) => {
    const value = field.multiple
      ? selected?.map((item) => item.value) || []
      : selected?.value || "";

    setEditItem(
      setNestedValue(editItem, field.name, value)
    );
  };

  const renderField = (field) => (
    <div key={field.name || field.label}>
      <label>{field.label}</label>

      {field.type === "select" ? (
        <Select
          options={field.options || []}
          isMulti={field.multiple}
          isSearchable
          placeholder={`Select ${field.label}`}
          value={getSelectValue(field, editItem)}
          onChange={(selected) => handleSelectChange(selected, field)}
        />
      ) : (
        <input
          type={field.type || "text"}
          placeholder={field.label}
          disabled={field.disabled}
          value={getNestedValue(editItem, field.name) || ""}
          onChange={(e) =>
            setEditItem(
              setNestedValue(editItem, field.name, e.target.value)
            )
          }
        />
      )}
    </div>
  );

  return (
    <>
      {/* DEFAULT FIELDS */}
      {defaultFields.map(renderField)}

      {/* BILLING */}
      {billingFields.length > 0 && (
        <details>
          <summary>Billing Address</summary>
          {billingFields.map(renderField)}
        </details>
      )}

      {/* SHIPPING */}
      {shippingFields.length > 0 && (
        <details>
          <summary>Shipping Address</summary>
          {shippingFields.map(renderField)}
        </details>
      )}
    </>
  );
})()}

    <div className="modalActions">
      <button
        type="button"
        onClick={
          handleUpdate
        }
        
      >
        Update
      </button>

      <button
        type="button"
        onClick={() =>
          setEditItem(null)
        }
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
  onRowClick: PropTypes.func,
};

export default CommonList;