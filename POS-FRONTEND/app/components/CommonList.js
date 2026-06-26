"use client";

import { useState } from "react";
import PropTypes from "prop-types";

const CommonList = ({
  title,
  data = [],
  columns = [],
  loading = false,
  error = "",
  page = 0,
  setPage,
  totalPages = 0,
  searchTerm = "",
  setSearchTerm,

  onAdd,
  addFields = [],
  newItem = {},
  setNewItem,
  handleAdd,

  viewItem,
  setViewItem,
  editItem,
  setEditItem,
  handleUpdate,
  editFields = [],

  actions = [],
  emptyMessage = "No data found",

  // NEW (optional)
  onToggleStatus,
}) => {
  const safeData = Array.isArray(data) ? data : [];

  const billingFields = addFields.filter((f) =>
    f.name.startsWith("billing.")
  );

  const shippingFields = addFields.filter((f) =>
    f.name.startsWith("shipping.")
  );

  const normalAddFields = addFields.filter(
    (f) =>
      !f.name.startsWith("billing.") &&
      !f.name.startsWith("shipping.")
  );

  const billingEditFields = editFields.filter((f) =>
    f.name.startsWith("billing.")
  );

  const shippingEditFields = editFields.filter((f) =>
    f.name.startsWith("shipping.")
  );

  const normalEditFields = editFields.filter(
    (f) =>
      !f.name.startsWith("billing.") &&
      !f.name.startsWith("shipping.")
  );

  const [openMenu, setOpenMenu] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [duplicateError, setDuplicateError] = useState("");

  const validateField = (name, value) => {
    // Email validation
    if (
      name.toLowerCase().includes("email") &&
      value &&
      !/^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(value)
    ) {
      alert("Enter a valid email address");
      return false;
    }

    // Phone validation
    if (
      (name.toLowerCase().includes("phone") ||
        name.toLowerCase().includes("phoneno")) &&
      value &&
      !/^\d{10}$/.test(value)
    ) {
      alert("Phone number must contain exactly 10 digits");
      return false;
    }

    return true;
  };

  const toggleMenu = (id) => {
    setOpenMenu((prev) => (prev === id ? null : id));
  };

  const renderCellContent = (col, row, rowIndex) => {
    if (col.key === "status") {
      return (
        <button
          onClick={() => onToggleStatus?.(row)}
          className={`
          relative w-14 h-7 flex items-center rounded-full transition-colors duration-300
          ${row.status ? "bg-teal-500" : "bg-slate-300"}
        `}
        >
          <span
            className={`
            w-6 h-6 bg-white rounded-full shadow-md transform transition-transform duration-300
            ${row.status ? "translate-x-7" : "translate-x-1"}
          `}
          />
        </button>
      );
    }

    if (col.render) {
      return col.render(row, rowIndex);
    }

    return row?.[col.key] ?? "-";
  };

  if (error) {
    return (
      <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-red-600">
        {error}
      </div>
    );
  }

  const renderField = (field, item, setItem) => {
    if (field.type === "select") {
      return (
        <select
          key={field.name}
          multiple={field.multiple}
          value={field.multiple ? item?.[field.name] || [] : item?.[field.name] || ""}
          onChange={(e) => {
            const values = Array.from(
              e.target.selectedOptions,
              (option) => option.value
            );

            setItem({
              ...item,
              [field.name]: field.multiple ? values : e.target.value,
            });
          }}
          className="w-full mb-3 p-3 rounded-lg border border-slate-300"
          style={field.multiple ? { height: "120px" } : {}}
        >
          {!field.multiple && (
            <option value="">
              Select {field.label}
            </option>
          )}

          {(field.options || []).map((option, index) => (
  <option
    key={option.id ?? `${option.value}-${index}`}
    value={option.value}
  >
    {option.label}
  </option>
))}
        </select>
      );
    }

    return (
      <input
        key={field.name}
        placeholder={field.label}
        value={item?.[field.name] || ""}
        readOnly={field.disabled}
        onChange={(e) =>
          setItem({
            ...item,
            [field.name]: e.target.value,
          })
        }
        className={`w-full mb-3 p-3 rounded-lg border text-slate-900 ${field.disabled
          ? "bg-slate-100 cursor-not-allowed"
          : "border-slate-300"
          }`}
      />
    );
  };

  return (
    <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">

      {/* HEADER */}
      <div className="px-8 py-6 border-b border-slate-100 flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">

        <div>
          <h2 className="text-xl font-semibold text-slate-800">{title}</h2>
          <p className="text-sm text-slate-500">
            Manage your {title.toLowerCase()}
          </p>
        </div>

        <div className="flex flex-col md:flex-row gap-4 items-center">

          {/* SEARCH (FIXED DARKER) */}
          <div className="relative">
            <span className="absolute left-3 top-2.5 text-slate-500">
              🔍
            </span>

            <input
              type="text"
              placeholder={`Search ${title}...`}
              value={searchTerm}
              onChange={(e) => {
                setSearchTerm(e.target.value);
                if (setPage) setPage(0);
              }}
              className="
                w-80
                h-11
                pl-11
                pr-4
                rounded-xl
                border
                border-slate-300
                bg-white
                text-slate-900
                placeholder-slate-600
                focus:border-teal-600
                focus:ring-2
                focus:ring-teal-100
                outline-none
                transition
              "
            />
          </div>

          {onAdd && (
            <button
              onClick={() => setShowAddModal(true)}
              className="
                flex items-center gap-2
                bg-teal-600 hover:bg-teal-700
                text-white
                px-5 py-2.5
                rounded-xl
                font-medium
                shadow-sm hover:shadow-md
                transition-all
              "
            >
              <span className="text-lg">＋</span>
              Add {title}
            </button>
          )}
        </div>
      </div>

      {/* LOADING */}
      {loading ? (
        <div className="text-center py-10 text-slate-500">Loading...</div>
      ) : (
        <>
          {/* TABLE */}
          <div className="overflow-hidden rounded-xl border border-slate-200">
            <div className="overflow-x-auto">
              <table className="w-full">

                <thead className="bg-slate-100">
                  <tr>
                    {columns.map((col) => (
                      <th
                        key={col.label}
                        className="px-4 py-3 text-left font-semibold text-slate-900"
                      >
                        {col.label}
                      </th>
                    ))}

                    {actions.length > 0 && (
                      <th className="px-6 py-4 text-left font-semibold text-slate-900">
                        Actions
                      </th>
                    )}
                  </tr>
                </thead>

                <tbody>
                  {safeData.length > 0 ? (
                    safeData.map((row, rowIndex) => {
                      const rowId =
                        row.identifier ||
                        row.id ||
                        row._id;

                      return (
                        <tr
                          key={rowId}
                          className="border-t border-slate-100 hover:bg-teal-50/40 transition"
                        >
                          {columns.map((col) => (
                            <td
                              key={col.label}
                              className="px-6 py-4 text-sm text-slate-900 font-medium"
                            >
                              {renderCellContent(col, row, rowIndex)}
                            </td>
                          ))}

                          {actions.length > 0 && (
                            <td className="px-4 py-3">
                              <div className="relative inline-block">

                                <button
                                  onClick={() => toggleMenu(rowId)}
                                  className="w-9 h-9 rounded-lg hover:bg-slate-200 flex items-center justify-center font-bold text-black"
                                >
                                  ⋮
                                </button>

                                {openMenu === rowId && (
                                  <div className="absolute right-0 mt-2 w-40 bg-white border border-slate-200 rounded-xl shadow-xl z-50">
                                    {actions.map((action) => (
                                      <button
                                        key={action.label}
                                        onClick={() => {
                                          action.onClick(row);

                                          if (action.label.includes("Edit")) {
                                            setShowEditModal(true);
                                          }

                                          setOpenMenu(null);
                                        }}
                                        className="w-full text-left px-4 py-2.5 hover:bg-slate-100 text-slate-900 font-medium"
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
                        className="py-10 text-center text-slate-500"
                      >
                        {emptyMessage}
                      </td>
                    </tr>
                  )}
                </tbody>

              </table>
            </div>
          </div>

          {/* PAGINATION (FIXED DARKER + CLEAR ACTIVE) */}
          {setPage && totalPages > 0 && (
            <div className="flex justify-center gap-2 mt-6 flex-wrap">

              <button
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
                className="px-4 py-2 rounded-lg border border-slate-300 bg-white hover:bg-slate-200 disabled:opacity-40 font-medium text-slate-800"
              >
                Prev
              </button>

              {Array.from({ length: totalPages }, (_, i) => i).map((pageNum) => (
                <button
                  key={pageNum}
                  onClick={() => setPage(pageNum)}
                  className={`
                    px-4 py-2 rounded-lg border font-medium
                    ${page === pageNum
                      ? "bg-slate-900 text-white border-slate-900"
                      : "bg-white hover:bg-slate-100 text-slate-800 border-slate-300"
                    }
                  `}
                >
                  {pageNum + 1}
                </button>
              ))}

              <button
                disabled={page === totalPages - 1}
                onClick={() => setPage(page + 1)}
                className="px-4 py-2 rounded-lg border border-slate-300 bg-white hover:bg-slate-200 disabled:opacity-40 font-medium text-slate-800"
              >
                Next
              </button>

            </div>
          )}

        </>
      )}



      {/* ================= ADD MODAL ================= */}
      {showAddModal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-xl w-[500px] max-h-[90vh] overflow-y-auto">

            <h2 className="text-xl font-bold text-slate-900 mb-4">
              Add {title}
            </h2>
            {duplicateError && (
              <div className="mb-3 p-3 rounded-lg bg-red-100 border border-red-300 text-red-700">
                {duplicateError}
              </div>
            )}

            {title === "Customers" ? (
              <>
                {normalAddFields.map((f) =>
                  f.type === "select" ? (
                    <select
                      key={f.name}
                      value={newItem?.[f.name] || ""}
                      onChange={(e) => {
                        setDuplicateError("");

                        setNewItem({
                          ...newItem,
                          [f.name]: e.target.value,
                        });
                      }}
                      className="w-full mb-3 p-3 rounded-lg border border-slate-300 text-slate-700"
                    >
                      <option value="">Select {f.label}</option>

                      {f.options?.map((opt) => (
                        <option key={opt.value} value={opt.value}>
                          {opt.label}
                        </option>
                      ))}
                    </select>
                  ) : (
                    <input
                      key={f.name}
                      placeholder={f.label}
                      value={newItem?.[f.name] || ""}
                      onChange={(e) =>
                        setNewItem({
                          ...newItem,
                          [f.name]: e.target.value,
                        })
                      }
                      className="w-full mb-3 p-3 rounded-lg border border-slate-300 text-slate-900 placeholder:text-slate-500"
                    />
                  )
                )}

                <details className="mb-3 border rounded-lg p-3">
                  <summary className="font-semibold cursor-pointer">
                    Billing Address
                  </summary>

                  <div className="mt-3">
                    {billingFields.map((f) => (
                      <input
                        key={f.name}
                        placeholder={f.label}
                        value={newItem?.[f.name] || ""}
                        onChange={(e) =>
                          setNewItem({
                            ...newItem,
                            [f.name]: e.target.value,
                          })
                        }
                        className="w-full mb-2 p-3 rounded-lg border border-slate-300 text-slate-900 placeholder:text-slate-500"
                      />
                    ))}
                  </div>
                </details>

                <details className="mb-3 border rounded-lg p-3">
                  <summary className="font-semibold cursor-pointer">
                    Shipping Address
                  </summary>

                  <div className="mt-3">
                    {shippingFields.map((f) => (
                      <input
                        key={f.name}
                        placeholder={f.label}
                        value={newItem?.[f.name] || ""}
                        onChange={(e) =>
                          setNewItem({
                            ...newItem,
                            [f.name]: e.target.value,
                          })
                        }
                        className="w-full mb-2 p-3 rounded-lg border border-slate-300 text-slate-900 placeholder:text-slate-500"
                      />
                    ))}
                  </div>
                </details>
              </>
            ) : (
              addFields.map((f) =>
                renderField(f, newItem, setNewItem)
              )
            )}

            <div className="flex justify-end gap-2 mt-4">
              <button
                onClick={() => setShowAddModal(false)}
                className="bg-slate-900 text-white px-4 py-2 rounded"
              >
                Cancel
              </button>

              <button
                onClick={async () => {
                  for (const key in newItem) {
                    if (!validateField(key, newItem[key])) {
                      return;
                    }
                  }

                  try {
                    setDuplicateError("");

                    await handleAdd();

                    setShowAddModal(false);
                  } catch (err) {
                    console.error("Add failed:", err);

                    setDuplicateError(
                      `${newItem.identifier} already exists`
                    );
                  }
                }}
                className="bg-teal-600 text-white px-4 py-2 rounded"
              >
                Save
              </button>
            </div>

          </div>
        </div>
      )}
      {/* ================= VIEW MODAL ================= */}
      {viewItem && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-xl w-[500px]">

            <h2 className="text-2xl font-bold text-slate-900 mb-6">
              View {title}
            </h2>

            <div className="space-y-4">

              <div>
                <p className="font-semibold text-slate-900">
                  {title === "Users" ? "Username" : "Identifier"}
                </p>

                <p className="text-slate-700">
                  {title === "Users"
                    ? viewItem.username
                    : viewItem.identifier}
                </p>
              </div>
              <hr className="border-slate-300" />

              <h3 className="font-bold text-slate-900 text-lg">
                Audit Information
              </h3>

              <div>
                <p className="font-semibold text-slate-900">
                  Created By
                </p>
                <p className="text-slate-700">
                  {viewItem.createdBy || "-"}
                </p>
              </div>

              <div>
                <p className="font-semibold text-slate-900">
                  Created On
                </p>
                <p className="text-slate-700">
                  {viewItem.createdOn || "-"}
                </p>
              </div>

              <div>
                <p className="font-semibold text-slate-900">
                  Modified By
                </p>
                <p className="text-slate-700">
                  {viewItem.modifiedBy || "-"}
                </p>
              </div>

              <div>
                <p className="font-semibold text-slate-900">
                  Modified On
                </p>
                <p className="text-slate-700">
                  {viewItem.modifiedOn || "-"}
                </p>
              </div>

            </div>

            <div className="flex justify-end mt-5">
              <button
                onClick={() => setViewItem(null)}
                className="bg-slate-900 text-white px-4 py-2 rounded"
              >
                Close
              </button>
            </div>

          </div>
        </div>
      )}

      {/* ================= EDIT MODAL ================= */}
      {showEditModal && editItem && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-xl w-[500px] max-h-[90vh] overflow-y-auto">

            <h2 className="text-xl font-bold text-slate-900 mb-4">
              Edit {title}
            </h2>

            {title === "Customers" ? (
              <>
                {normalEditFields.map((f) =>
                  f.type === "select" ? (
                    <select
                      key={f.name}
                      value={editItem?.[f.name] || ""}
                      onChange={(e) => {
                        let value = e.target.value;

                        if (
                          f.name.toLowerCase().includes("phone") ||
                          f.name.toLowerCase().includes("phoneno")
                        ) {
                          value = value.replaceAll(/\D/g, "").slice(0, 10);
                        }

                        setEditItem({
                          ...editItem,
                          [f.name]: value,
                        });
                      }}
                      className="w-full mb-3 p-3 rounded-lg border border-slate-300 text-slate-700"
                    >
                      <option value="">Select {f.label}</option>

                      {(f.options || []).map((opt) => (
                        <option key={opt.value} value={opt.value}>
                          {opt.label}
                        </option>
                      ))}
                    </select>
                  ) : (
                    <input
                      key={f.name}
                      value={editItem?.[f.name] || ""}
                      readOnly={f.disabled}
                      onChange={(e) =>
                        setEditItem({
                          ...editItem,
                          [f.name]: e.target.value,
                        })
                      }
                      className={`w-full mb-3 p-3 rounded-lg border text-slate-900 ${f.disabled
                        ? "bg-slate-100 cursor-not-allowed"
                        : "border-slate-300"
                        }`}
                    />
                  )
                )}

                <details className="mb-3 border rounded-lg p-3">
                  <summary className="font-semibold cursor-pointer">
                    Billing Address
                  </summary>

                  <div className="mt-3">
                    {billingEditFields.map((f) => (
                      <input
                        key={f.name}
                        value={editItem?.[f.name] || ""}
                        onChange={(e) =>
                          setEditItem({
                            ...editItem,
                            [f.name]: e.target.value,
                          })
                        }
                        className="w-full mb-2 p-3 rounded-lg border border-slate-300"
                      />
                    ))}
                  </div>
                </details>

                <details className="mb-3 border rounded-lg p-3">
                  <summary className="font-semibold cursor-pointer">
                    Shipping Address
                  </summary>

                  <div className="mt-3">
                    {shippingEditFields.map((f) => (
                      <input
                        key={f.name}
                        value={editItem?.[f.name] || ""}
                        onChange={(e) =>
                          setEditItem({
                            ...editItem,
                            [f.name]: e.target.value,
                          })
                        }
                        className="w-full mb-2 p-3 rounded-lg border border-slate-300"
                      />
                    ))}
                  </div>
                </details>
              </>
            ) : (
              editFields.map((f) =>
                renderField(f, editItem, setEditItem)
              )
            )}

            <div className="flex justify-end gap-2 mt-4">
              <button
                onClick={() => setShowEditModal(false)}
                className="bg-slate-900 text-white px-4 py-2 rounded"
              >
                Cancel
              </button>

              <button
                onClick={() => {
                  for (const key in editItem) {
                    if (!validateField(key, editItem[key])) {
                      return;
                    }
                  }

                  handleUpdate();
                  setShowEditModal(false);
                }}
                className="bg-teal-600 text-white px-4 py-2 rounded"
              >
                Update
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
  setPage: PropTypes.func,
  totalPages: PropTypes.number,
  searchTerm: PropTypes.string,
  setSearchTerm: PropTypes.func,

  onAdd: PropTypes.func,
  addFields: PropTypes.array,
  newItem: PropTypes.object,
  setNewItem: PropTypes.func,
  handleAdd: PropTypes.func,

  // ADD THESE
  viewItem: PropTypes.shape({
    identifier: PropTypes.string,
    username: PropTypes.string,
    description: PropTypes.string,
    createdBy: PropTypes.string,
    createdOn: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.object,
    ]),
    modifiedBy: PropTypes.string,
    modifiedOn: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.object,
    ]),
  }),
  setViewItem: PropTypes.func,

  editItem: PropTypes.object,
  setEditItem: PropTypes.func,
  handleUpdate: PropTypes.func,
  editFields: PropTypes.array,

  actions: PropTypes.array,
  emptyMessage: PropTypes.string,

  onToggleStatus: PropTypes.func,
};

export default CommonList;