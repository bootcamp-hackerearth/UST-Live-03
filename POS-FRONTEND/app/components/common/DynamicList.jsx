"use client";

import React, { useEffect, useState, useRef, useCallback } from "react";
import PropTypes from "prop-types";
import { MoreVertical, Search, Plus, X } from "lucide-react";
import { useRouter } from "next/navigation";

import DynamicForm from "./DynamicForm";
import commonApi from "../../services/commonApi";

function DynamicList({
  title,
  routeName,
  columns = [],
  editUrl,
  addUrl,
  formFields = [],
  formTitle = "",
  uniqueFields = [],
  showAddToCart = false,
}) {
  const router = useRouter();

  const [allData, setAllData] = useState([]);
  const [data, setData] = useState([]);
  const [openMenu, setOpenMenu] = useState(null);
  const [menuPosition, setMenuPosition] = useState({ top: 0, left: 0 });
  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");

  const [page, setPage] = useState(0);
  const [sizePerPage, setSizePerPage] = useState(20);
  const [totalPages, setTotalPages] = useState(0);
  const [totalRecords, setTotalRecords] = useState(0);

  const [showModal, setShowModal] = useState(false);
  const [editIdentifier, setEditIdentifier] = useState(null);
  const [editInitialValues, setEditInitialValues] = useState({});

  const menuRef = useRef(null);

  // Debounce: wait 400ms after user stops typing before updating debouncedSearch
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 400);

    return () => clearTimeout(timer);
  }, [search]);

  const fetchData = useCallback(() => {
    if (!routeName) return;

   commonApi
  .list(routeName, {
    page,
    sizePerPage,
    sortDirection: "ASC",
    sortField: "id",
    keyword: debouncedSearch.trim(),
  })
      .then((res) => {
        const list = res.data.dtoList || [];

        setData(list);
        setAllData(list);
        setTotalRecords(res.data.totalRecords || 0);
        setTotalPages(res.data.totalPage || 0);
      })
      .catch((err) => console.log(err));
  }, [routeName, page, sizePerPage, debouncedSearch]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  useEffect(() => {
    const handleOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpenMenu(null);
      }
    };

    const handleScroll = () => setOpenMenu(null);

    if (openMenu !== null) {
      document.addEventListener("mousedown", handleOutside);
      document.addEventListener("scroll", handleScroll, true);
    }

    return () => {
      document.removeEventListener("mousedown", handleOutside);
      document.removeEventListener("scroll", handleScroll, true);
    };
  }, [openMenu]);

  const handleMenuToggle = useCallback(
    (e, rowIndex) => {
      if (openMenu === rowIndex) {
        setOpenMenu(null);
        return;
      }

      const rect = e.currentTarget.getBoundingClientRect();
      const menuWidth = 160;
      const menuHeight = 80;

      const spaceBelow = globalThis.innerHeight - rect.bottom;
      const spaceRight = globalThis.innerWidth - rect.right;

      const top =
        spaceBelow < menuHeight + 8
          ? rect.top - menuHeight - 4
          : rect.bottom + 4;

      const left =
        spaceRight < menuWidth ? rect.right - menuWidth : rect.left;

      setMenuPosition({ top, left });
      setOpenMenu(rowIndex);
    },
    [openMenu]
  );

  const handleDelete = async (identifier) => {
    if (!globalThis.confirm("Delete this record?")) return;

    try {
      await commonApi.delete(routeName, "identifier", identifier);

      const loggedInIdentifier = localStorage.getItem("username");

      if (routeName === "user" && loggedInIdentifier === identifier) {
        localStorage.removeItem("token");
        localStorage.removeItem("username");
        router.push("/login");
        return;
      }

      fetchData();
    } catch (err) {
      console.log(err);
    }
  };

  const handleToggle = async (identifier) => {
    try {
      await commonApi.toggle(routeName, identifier);
      fetchData();
    } catch (err) {
      console.log(err);
    }
  };

  const handleAddToCart = async (item) => {
    try {
      let cartIdentifier = localStorage.getItem("cartIdentifier");

      if (!cartIdentifier) {
        cartIdentifier = `CART-${Date.now()}`;

        await commonApi.add("cart", {
          identifier: cartIdentifier,
          originalPrice: 0,
          discount: 0,
          totalPrice: 0,
        });

        localStorage.setItem("cartIdentifier", cartIdentifier);
      }

      await commonApi.add("cartentry", {
        cartIdentifier,
        productIdentifier: item.identifier,
        quantity: 1,
      });

      alert("Product added to cart");
    } catch (err) {
      console.log(err);
      alert("Failed to add product");
    }
  };

  const handleAddClick = () => {
    if (formFields.length > 0) {
      setEditIdentifier(null);
      setEditInitialValues({});
      setShowModal(true);
      return;
    }

    if (addUrl) {
      router.push(addUrl);
    }
  };

  const handleEditClick = (item) => {
    if (formFields.length > 0) {
      setEditIdentifier(item.identifier);
      setEditInitialValues(item);
      setOpenMenu(null);
      setShowModal(true);
      return;
    }

    if (editUrl) {
      router.push(`${editUrl}/${encodeURIComponent(item.identifier)}`);
    }
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditIdentifier(null);
    setEditInitialValues({});
  };

  const isActive = (value) => value === true || value === "true";

  const getStockStatus = (item) => {
    const quantity = Number(item.quantity);
    const minimumStock = Number(item.minimumStock);

    if (quantity === 0) {
      return { label: "Out Of Stock", className: "bg-red-100 text-red-700" };
    }

    if (quantity < minimumStock) {
      return { label: "Low Stock", className: "bg-yellow-100 text-yellow-700" };
    }

    return { label: "In Stock", className: "bg-green-100 text-green-700" };
  };

  return (
    <>
      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <div className="flex items-center justify-between p-5 border-b">
          <h2 className="text-2xl font-semibold text-gray-800">{title}</h2>

          <button
            onClick={handleAddClick}
            className="flex items-center gap-2 bg-red-600 hover:bg-red-700 text-white px-5 py-2 rounded-lg transition-all"
          >
            <Plus size={18} />
            Add New
          </button>
        </div>

        <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4 p-5">
          <div className="flex items-center gap-2">
            <span className="text-sm text-gray-600">Show</span>

            <select
              value={sizePerPage}
              onChange={(e) => {
                setSizePerPage(Number(e.target.value));
                setPage(0);
              }}
              className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none bg-white text-gray-700"
            >
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>

            <span className="text-sm text-gray-600">entries</span>
          </div>

          <div className="relative w-full md:w-72">
            <input
              type="text"
              placeholder="Search..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
              className="w-full border border-gray-300 rounded-lg px-4 py-2 pr-10 focus:outline-none bg-white text-gray-700"
            />

            <Search
              size={18}
              className="absolute right-3 top-3 text-gray-400"
            />
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full border-collapse">
            <thead className="bg-gray-100">
              <tr>
                <th className="min-w-[70px] px-6 py-4 text-center text-sm font-semibold text-gray-700">
                  SL
                </th>

                {columns.map((column, index) => (
                  <th
                    key={column.key || index}
                    className={`px-6 py-4 text-sm font-semibold text-gray-700 ${
                      column.type === "toggle" ||
                      column.type === "stockStatus"
                        ? "text-center min-w-[120px]"
                        : "text-left min-w-[220px]"
                    }`}
                  >
                    {column.label}
                  </th>
                ))}

                <th className="min-w-[160px] px-6 py-4 text-center text-sm font-semibold text-gray-700">
                  Action
                </th>
              </tr>
            </thead>

            <tbody>
              {data.length > 0 ? (
                data.map((item, rowIndex) => (
                  <tr
                    key={
                      item && (item.identifier || item.id)
                        ? item.identifier || item.id
                        : `row-${rowIndex}`
                    }
                    className="border-t hover:bg-gray-50 transition-colors"
                  >
                    <td className="min-w-[70px] px-6 py-5 text-center text-sm text-gray-700 align-top">
                      {page * sizePerPage + rowIndex + 1}
                    </td>

                    {columns.map((column, colIndex) => {
                      const stockStatus = getStockStatus(item);

                      return (
                        <td
                          key={column.key || colIndex}
                          className={`px-6 py-5 text-sm text-gray-700 align-top ${
                            column.type === "toggle" ||
                            column.type === "stockStatus"
                              ? "text-center min-w-[120px]"
                              : "text-left min-w-[220px]"
                          }`}
                        >
                          {(!column.type || column.type === "text") && (
                            <div className="whitespace-normal break-words leading-6">
                              {item[column.key] || "-"}
                            </div>
                          )}

                          {column.type === "list" && (
                            <div className="flex flex-wrap gap-2">
                              {Array.isArray(item[column.key]) &&
                              item[column.key].length > 0 ? (
                                item[column.key].map((value, i) => (
                                  <span
                                    key={
                                      value && (value.identifier || value.id)
                                        ? value.identifier || value.id
                                        : `val-${i}`
                                    }
                                    className="px-2 py-1 bg-red-50 text-red-600 rounded-md text-xs border border-red-100"
                                  >
                                    {typeof value === "object"
                                      ? value[column.displayKey || "identifier"]
                                      : value}
                                  </span>
                                ))
                              ) : (
                                <span className="text-gray-400">-</span>
                              )}
                            </div>
                          )}

                          {column.type === "stockStatus" && (
                            <span
                              className={`px-3 py-1 rounded-full text-xs font-semibold ${stockStatus.className}`}
                            >
                              {stockStatus.label}
                            </span>
                          )}

                          {column.type === "toggle" && (
                            <div className="flex justify-center items-center">
                              <button
                                onClick={() => handleToggle(item.identifier)}
                                className={`relative inline-flex items-center w-11 h-6 rounded-full overflow-hidden transition-colors duration-300 ${
                                  isActive(item[column.key])
                                    ? "bg-red-600"
                                    : "bg-gray-300"
                                }`}
                              >
                                <span
                                  className={`absolute left-0.5 top-0.5 w-5 h-5 rounded-full bg-white shadow-md transition-transform duration-300 ${
                                    isActive(item[column.key])
                                      ? "translate-x-5"
                                      : "translate-x-0"
                                  }`}
                                />
                              </button>
                            </div>
                          )}
                        </td>
                      );
                    })}

                    <td className="min-w-[160px] px-6 py-5 text-center align-top">
                      <div className="flex items-center justify-center gap-2">
                        {showAddToCart && (
                          <button
                            onClick={() => handleAddToCart(item)}
                            className="bg-green-600 hover:bg-green-700 text-white px-3 py-2 rounded-lg text-sm"
                          >
                            Add To Cart
                          </button>
                        )}

                        <button
                          onClick={(e) => handleMenuToggle(e, rowIndex)}
                          className="p-2 hover:bg-gray-100 rounded-full text-gray-600"
                        >
                          <MoreVertical size={18} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td
                    colSpan={columns.length + 2}
                    className="text-center py-10 text-gray-500"
                  >
                    No Data Found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="flex items-center justify-between p-5 border-t">
          <div className="text-sm text-gray-500">
            Showing Page {page + 1} of {totalPages || 1}
            <span className="ml-3">Total Records: {totalRecords}</span>
          </div>

          <div className="flex items-center gap-2">
            <button
              disabled={page === 0}
              onClick={() => setPage((prev) => prev - 1)}
              className="px-4 py-2 border rounded-lg text-sm hover:bg-gray-50 disabled:opacity-50 text-gray-700"
            >
              Previous
            </button>

            <button
              disabled={page + 1 >= totalPages}
              onClick={() => setPage((prev) => prev + 1)}
              className="px-4 py-2 border rounded-lg text-sm hover:bg-gray-50 disabled:opacity-50 text-gray-700"
            >
              Next
            </button>
          </div>
        </div>
      </div>

      {openMenu !== null && data[openMenu] && (
        <div
          ref={menuRef}
          style={{
            position: "fixed",
            top: menuPosition.top,
            left: menuPosition.left,
            zIndex: 9999,
          }}
          className="w-40 bg-white border rounded-lg shadow-xl overflow-hidden"
        >
          <button
            onClick={() => handleEditClick(data[openMenu])}
            className="w-full text-left px-4 py-2 text-sm text-blue-600 hover:bg-blue-50"
          >
            Edit
          </button>

          <button
            onClick={() => {
              setOpenMenu(null);
              handleDelete(data[openMenu].identifier);
            }}
            className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
          >
            Delete
          </button>
        </div>
      )}

      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg mx-4">
            <div className="flex items-center justify-between px-6 py-5 border-b">
              <h2 className="text-xl font-semibold text-gray-800">
                {editIdentifier
                  ? `Edit ${formTitle || title}`
                  : `Add New ${formTitle || title}`}
              </h2>

              <button
                onClick={handleCloseModal}
                className="p-2 hover:bg-gray-100 rounded-lg text-gray-600"
              >
                <X size={20} />
              </button>
            </div>

            <DynamicForm
              fields={formFields}
              routeName={routeName}
              initialValues={editInitialValues}
              isModal={true}
              existingData={allData}
              uniqueFields={uniqueFields}
              onSuccess={() => {
                handleCloseModal();
                fetchData();
              }}
              onCancel={handleCloseModal}
            />
          </div>
        </div>
      )}
    </>
  );
}

DynamicList.propTypes = {
  title: PropTypes.string.isRequired,
  routeName: PropTypes.string.isRequired,
  columns: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      type: PropTypes.string,
      displayKey: PropTypes.string,
    })
  ),
  editUrl: PropTypes.string,
  addUrl: PropTypes.string,
  formFields: PropTypes.array,
  formTitle: PropTypes.string,
  uniqueFields: PropTypes.arrayOf(PropTypes.string),
  showAddToCart: PropTypes.bool,
};

export default DynamicList;