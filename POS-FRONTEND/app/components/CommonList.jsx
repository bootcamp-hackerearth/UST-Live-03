 import { useState, useEffect} from "react";
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

  addError="",
  
 
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
  const [inputValue, setInputValue] = useState(searchTerm || "");

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
   const [showAddModal, setShowAddModal] = useState(false);
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
  <div className="actionButtons">
 {actions.map((action, label) => (
  <button
    key={`${action.label}-${label}`}
    className="iconBtn"
    onClick={() => action.onClick(row)}
  >
    {action.label}
  </button>
))}
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

      {addError && (
        <div className="errorMessage">
          {addError}
        </div>
      )}

      {addFields.map((field, index) => {
              const key = field.name || field.label || index;

              let fieldInput = null;

              if (field.type === "multiselect") {
                fieldInput = (
                  <>
                    <label>{field.label}</label>

                    <select
                      className="multiSelect"
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
                  </>
                );
              } else if (field.type === "select") {
                fieldInput = (
                  <select
                    value={newItem[field.name] || ""}
                    onChange={(e) =>
                      setNewItem({
                        ...newItem,
                        [field.name]: e.target.value,
                      })
                    }
                  >
                    <option value="">Select {field.label}</option>

                    {field.options?.map((opt) => (
                      <option key={opt.value} value={opt.value}>
                        {opt.label}
                      </option>
                    ))}
                  </select>
                );
              } else {
                fieldInput = (
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
                );
              }

              return (
                <div key={key}>
                  {fieldInput}
                </div>
              );
            })}
 
            <div className="modalActions">
<button
  onClick={async () => {
    const success = await handleAdd();

    if (success) {
      setShowAddModal(false);
    }
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
 
            {editFields.map((field, index) => {
              let fieldInput = (
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
              );

              if (field.type === "multiselect") {
                fieldInput = (
                  <>
                    <label>{field.label}</label>

                    <select
                      className="multiSelect"
                      multiple
                      value={editItem[field.name] || []}
                      onChange={(e) => {
                        const selected = Array.from(
                          e.target.selectedOptions,
                          (option) => option.value
                        );

                        setEditItem({
                          ...editItem,
                          [field.name]: selected,
                        });
                      }}
                    >
                      {field.options?.map((opt) => (
                        <option
                          key={opt.value ?? opt.label}
                          value={opt.value}
                        >
                          {opt.label}
                        </option>
                      ))}
                    </select>
                  </>
                );
              } else if (field.type === "select") {
                fieldInput = (
                  <select
                    value={editItem[field.name] || ""}
                    onChange={(e) =>
                      setEditItem({
                        ...editItem,
                        [field.name]: e.target.value,
                      })
                    }
                  >
                    <option value="">
                      Select {field.label}
                    </option>

                    {field.options?.map((opt) => (
                      <option
                        key={opt.value ?? opt.label}
                        value={opt.value}
                      >
                        {opt.label}
                      </option>
                    ))}
                  </select>
                );
              }

              return (
                <div key={field.name || field.label || index}>
                  {fieldInput}
                </div>
              );
            })}
 
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

  addError: PropTypes.string,

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