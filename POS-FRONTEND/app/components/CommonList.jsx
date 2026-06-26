import { useState, useEffect, useRef } from "react";
import "./List.css";
import PropTypes from "prop-types";
 
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

  // VIEW
  viewItem,
  setViewItem,

  // ACTIONS
  actions = [],
  emptyMessage = "No data found",
}) => {
  const safeData = Array.isArray(data) ? data : [];
  const [inputValue, setInputValue] = useState(searchTerm || "");
  const [openMenu, setOpenMenu] = useState(null);
  const menuRef = useRef(null);
  const [validationError, setValidationError] = useState("");
  const [addError, setAddError] = useState("");
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
  }, 500);

  return () => clearTimeout(timer);
}, [inputValue, searchTerm, setSearchTerm]);

useEffect(() => {
  const handleClickOutside = (event) => {
    if (
      menuRef.current &&
      !menuRef.current.contains(event.target)
    ) {
      setOpenMenu(null);
    }
  };

  document.addEventListener(
    "mousedown",
    handleClickOutside
  );

  return () => {
    document.removeEventListener(
      "mousedown",
      handleClickOutside
    );
  };
}, []);

useEffect(() => {
  if (showAddModal) {
    setValidationError("");
    setAddError("");
  }
}, [showAddModal]);

useEffect(() => {
  if (editItem) {
    setValidationError("");
  }
}, [editItem]);

const getNestedValue = (obj, path) =>
  path.split(".").reduce(
    (acc, key) => acc?.[key] ?? "",
    obj
  );

const setNestedValue = (obj, path, value) => {
  const keys = path.split(".");
  const copy = structuredClone(obj);

  let temp = copy;

  keys.forEach((key, index) => {
    if (index === keys.length - 1) {
      temp[key] = value;
    } else {
      temp[key] = temp[key] || {};
      temp = temp[key];
    }
  });

  return copy;
};
const isEmptyValue = (value) =>
  value === null ||
  value === undefined ||
  value === "" ||
  (Array.isArray(value) && value.length === 0);

const validateRequired = (field, value) => {
  if (field.required && isEmptyValue(value)) {
    return `${field.label} is required`;
  }
  return "";
};

const validateEmail = (field, value) => {
  if (field.type !== "email" || !value) {
    return "";
  }
  const email = String(value).trim();
  const hasAt = email.indexOf("@");
  const hasDot = email.lastIndexOf(".");

  return (
    hasAt > 0 &&
    hasDot > hasAt + 1 &&
    hasDot < email.length - 1
  )
    ? ""
    : "Enter a valid email address";
};

const validatePhone = (field, value) => {
  if (field.name !== "phoneno" || !value) {
    return "";
  }

  const phoneRegex = /^\d{10}$/;

  return phoneRegex.test(value)
    ? ""
    : "Phone number must be exactly 10 digits";
};

const validatePincode = (field, value) => {
  const pincodeFields = [
    "billing.pincode",
    "shipping.pincode",
    "pincode",
  ];

  if (
    !pincodeFields.includes(field.name) ||
    !value
  ) {
    return "";
  }

  const pinRegex = /^\d{6}$/;

  return pinRegex.test(value)
    ? ""
    : `${field.label} must be exactly 6 digits`;
};
const validateField = (field, value) => {
  return (
    validateRequired(field, value) ||
    validateEmail(field, value) ||
    validatePhone(field, value) ||
    validatePincode(field, value)
  );
};
const validateFields = (fields, data) =>
  fields
    .map((field) =>
      validateField(
        field,
        getNestedValue(data, field.name)
      )
    )
    .find(Boolean) || "";
const basicAddFields = addFields.filter(
  (field) => !field.section
);

const billingAddFields = addFields.filter(
  (field) => field.section === "Billing"
);

const shippingAddFields = addFields.filter(
  (field) => field.section === "Shipping"
);
const basicEditFields = editFields.filter(
  (field) => !field.section
);

const billingEditFields = editFields.filter(
  (field) => field.section === "Billing"
);

const shippingEditFields = editFields.filter(
  (field) => field.section === "Shipping"
);
const renderField = (field) => {
  const placeholder = field.label || field.name;

  return (
    <>
      {/* TEXT / NUMBER */}
      {(!field.type || ["text", "number", "email", "password", "pincode"].includes(field.type)) && (
        <input
          type={field.type || "text"}
          placeholder={placeholder}
          maxLength={field.maxLength}
          value={getNestedValue(newItem, field.name) || ""}
onChange={(e) =>
  setNewItem(
    setNestedValue(newItem, field.name, e.target.value)
  )
}
        />
      )}

      {/* SELECT */}
      {field.type === "select" && (
        <select
          value={getNestedValue(newItem, field.name) || ""}
          onChange={(e) =>
            setNewItem(
              setNestedValue(newItem, field.name, e.target.value)
            )
          }
        >
          <option value="">{placeholder}</option>

          {field.options?.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      )}

      {/* MULTISELECT */}
      {field.type === "multiselect" && (
        <select
          multiple
          value={newItem[field.name] || []}
          onChange={(e) => {
            const selected = Array.from(
              e.target.selectedOptions,
              (option) => option.value
            );

            setNewItem({
              ...newItem,
              [field.name]: selected,
            });
          }}
        >
          {field.options?.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      )}
    </>
  );
};

const renderSelectField = (
  field,
  item,
  setItem
) => (
  <select
    value={
      getNestedValue(item, field.name) || ""
    }
    onChange={(e) =>
      setItem(
        setNestedValue(
          item,
          field.name,
          e.target.value
        )
      )
    }
  >
    <option value="">
      {field.label}
    </option>

    {field.options?.map((opt) => (
      <option
        key={opt.value}
        value={opt.value}
      >
        {opt.label}
      </option>
    ))}
  </select>
);

  return (
    
    <div className="section">

    {loading && (
      <div>Loading...</div>
    )}

    {error && (
      <div style={{ color: "red" }}>
        {error}
      </div>
    )}
 
      {/* HEADER */}
  <div className="tableHeader">
  <h2 className="sectionTitle">{title}</h2>

  <div className="headerActions">

  {setSearchTerm && (
  <div className="searchContainer">
    <span className="searchIcon">🔍</span>

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
  </div>
)}

    {onAdd && (
      <button
        className="actionBtn"
        onClick={() => {
          setShowAddModal(true);
          setValidationError("");
          setAddError("");
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
              {columns.map((col, index) => (
  <th key={col.key ?? index}>{col.label}</th>
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
                    {columns.map((col, i) => (
                    <td key={col.key ?? i}>
                      {col.render
                      ? col.render(row, rowIndex)
                      : row?.[col.key] ?? "-"}
                       </td>
                      ))}
 
                    {actions.length > 0 && (
  <td>
  <div
  className="dropdownContainer"
  ref={openMenu === rowId ? menuRef : null}
>
    <button
      className="threeDotsBtn"
      onClick={() =>
        setOpenMenu(
          openMenu === rowId ? null : rowId
        )
      }
    >
      ⋮
    </button>

    {openMenu === rowId && (
      <div className="dropdownMenu">
        {actions.map((action, index) => (
          <button
            key={`${action.label}-${index}`}
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
 
          {Array.from({ length: totalPages }).map((_, i) => {
            const pageNumber = i + 1;

            return (
              <button
                key={`page-${pageNumber}`}
                className={`pageBtn ${page === i ? "activePage" : ""}`}
                onClick={() => setPage(i)}
              >
                {pageNumber}
              </button>
            );
          })}
 
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

        {validationError && (
          <div className="errorMessage">
            {validationError}
          </div>
        )}

        {addError && (
          <div className="errorMessage">
            {addError}
          </div>
        )}

      {/* BASIC FIELDS */}
{basicAddFields.map((field, index) => (
  <div key={field.name || index}>
    {renderField(field)}
  </div>
))}

{/* BILLING ADDRESS */}
{billingAddFields.length > 0 && (
  <div className="addressAccordion">
    <details>
      <summary>
        <span className="addressTitle">
          🧾 Billing Address
        </span>
      </summary>

      <div className="addressBody">
        {billingAddFields.map((field, index) => (
          <div key={field.name || index}>
            {renderField(field)}
          </div>
        ))}
      </div>
    </details>
  </div>
)}

{/* SHIPPING ADDRESS */}
{shippingAddFields.length > 0 && (
  <div className="addressAccordion">
    <details>
      <summary>
        <span className="addressTitle">
          🚚 Shipping Address
        </span>
      </summary>

      <div className="addressBody">
        {shippingAddFields.map((field, index) => (
          <div key={field.name || index}>
            {renderField(field)}
          </div>
        ))}
      </div>
    </details>
  </div>
)}
 
            <div className="modalActions">
<button
  onClick={async () => {

    const error = validateFields(
      addFields,
      newItem
    );

    if (error) {
      setValidationError(error);
      return;
    }

    setValidationError("");

    const success = await handleAdd();

    if (success) {
  setShowAddModal(false);
  setValidationError("");
  setAddError("");
}
  }}
>
  Add
</button>
 
              <button
  onClick={() => {
    setShowAddModal(false);
    setValidationError("");
    setAddError("");
  }}
>
  Cancel
</button>
            </div>
 
          </div>
        </div>
      )}
{/* VIEW MODAL */}
{viewItem && (
  <div className="modalOverlay">
    <div className="modal">

      <h2>View {title}</h2>

      <div className="viewContent">

        <div className="viewRow">
          <strong>Identifier</strong>
          <span>{viewItem.identifier || "-"}</span>
        </div>

        <div className="viewRow">
          <strong>Description</strong>
          <span>{viewItem.description || "-"}</span>
        </div>

        <hr />

        <h3>Audit Information</h3>

        <div className="viewRow">
          <strong>Created By</strong>
          <span>{viewItem.createdBy || "-"}</span>
        </div>

        <div className="viewRow">
          <strong>Created On</strong>
          <span>
            {viewItem.createdOn
              ? new Date(viewItem.createdOn).toLocaleString()
              : "-"}
          </span>
        </div>

        <div className="viewRow">
          <strong>Modified By</strong>
          <span>{viewItem.modifiedBy || "-"}</span>
        </div>

        <div className="viewRow">
          <strong>Modified On</strong>
          <span>
            {viewItem.modifiedOn
              ? new Date(viewItem.modifiedOn).toLocaleString()
              : "-"}
          </span>
        </div>

      </div>

      <div className="modalActions">
        <button onClick={() => setViewItem(null)}>
          Close
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
      {validationError && (
        <div className="errorMessage">
          {validationError}
        </div>
      )}

      {editFields.some(field => field.section) ? (

        <>
          {/* BASIC */}
          {basicEditFields.map((field, index) => (
            <div key={field.name || index}>
{field.type === "select" ? (
  renderSelectField(
    field,
    editItem,
    setEditItem
  )
) : (
  <input
    type={field.type || "text"}
    placeholder={field.label}
    maxLength={field.maxLength}
    disabled={field.disabled}
    value={
      getNestedValue(editItem, field.name) || ""
    }
    onChange={(e) =>
      setEditItem(
        setNestedValue(
          editItem,
          field.name,
          e.target.value
        )
      )
    }
  />
)}
            </div>
          ))}

          {/* BILLING */}
          {billingEditFields.length > 0 && (
            <div className="addressAccordion">
              <details>
                <summary>
                  <span className="addressTitle">
                    🧾 Billing Address
                  </span>
                </summary>

                <div className="addressBody">
                  {billingEditFields.map((field) => (
                    <input
                      key={field.name}
                      type="text"
                      placeholder={field.label}
                      value={
                        getNestedValue(editItem, field.name) || ""
                      }
                      onChange={(e) =>
                        setEditItem(
                          setNestedValue(
                            editItem,
                            field.name,
                            e.target.value
                          )
                        )
                      }
                    />
                  ))}
                </div>
              </details>
            </div>
          )}

          {/* SHIPPING */}
          {shippingEditFields.length > 0 && (
            <div className="addressAccordion">
              <details>
                <summary>
                  <span className="addressTitle">
                    🚚 Shipping Address
                  </span>
                </summary>

                <div className="addressBody">
                  {shippingEditFields.map((field) => (
                    <input
                      key={field.name}
                      type="text"
                      placeholder={field.label}
                      value={
                        getNestedValue(editItem, field.name) || ""
                      }
                      onChange={(e) =>
                        setEditItem(
                          setNestedValue(
                            editItem,
                            field.name,
                            e.target.value
                          )
                        )
                      }
                    />
                  ))}
                </div>
              </details>
            </div>
          )}
        </>

) : (

  <>
{editFields.map((field, index) => (
  <div key={field.name || index}>

    {field.type === "multiselect" && (
      <select
        multiple
        value={
          getNestedValue(editItem, field.name) || []
        }
        onChange={(e) => {
          const selected = Array.from(
            e.target.selectedOptions,
            (option) => option.value
          );

          setEditItem(
            setNestedValue(
              editItem,
              field.name,
              selected
            )
          );
        }}
      >
        {field.options?.map((opt) => (
          <option
            key={opt.value}
            value={opt.value}
          >
            {opt.label}
          </option>
        ))}
      </select>
    )}

    {field.type === "select" &&
  renderSelectField(
    field,
    editItem,
    setEditItem
  )}

    {field.type !== "multiselect" && field.type !== "select" && (
      <input
        type={field.type || "text"}
        placeholder={field.label}
        disabled={field.disabled}
        value={
          getNestedValue(editItem, field.name) || ""
        }
        onChange={(e) =>
          setEditItem(
            setNestedValue(
              editItem,
              field.name,
              e.target.value
            )
          )
        }
      />
    )}

  </div>
))}
  </>

)}

      <div className="modalActions">
        <button
  onClick={async () => {

    const error = validateFields(
      editFields,
      editItem
    );

    if (error) {
      setValidationError(error);
      return;
    }

    setValidationError("");

    await handleUpdate();
    setValidationError("");
  }}
>
  Update
</button>

        <button
  onClick={() => {
    setEditItem(null);
    setValidationError("");
  }}
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
  sizePerPage: PropTypes.number,
  totalPages: PropTypes.number,

  searchTerm: PropTypes.string,
  setSearchTerm: PropTypes.func,

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

  viewItem: PropTypes.object,
  setViewItem: PropTypes.func,
};
 
export default CommonList;