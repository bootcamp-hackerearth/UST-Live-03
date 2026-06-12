import React, { useEffect, useState } from "react";
import {
  MoreVertical,
  Search,
  Plus,
  X,
} from "lucide-react";
import { useNavigate } from "react-router-dom";

import DynamicForm from "./DynamicForm";
import commonApi from "../../components/api/commonApi";

function DynamicList({
  title,
  routeName,
  columns = [],
  editUrl,
  addUrl,
  formFields = [],
  formTitle = "",
  uniqueFields = [],
}) {

  const navigate = useNavigate();

  const [data, setData] = useState([]);
  const [openMenu, setOpenMenu] = useState(null);
  const [search, setSearch] = useState("");

  const [page, setPage] = useState(0);
  const [sizePerPage, setSizePerPage] = useState(10);

  const [totalPages, setTotalPages] = useState(0);
  const [totalRecords, setTotalRecords] = useState(0);

  const [showModal, setShowModal] = useState(false);
  const [editIdentifier, setEditIdentifier] = useState(null);
  const [editInitialValues, setEditInitialValues] = useState({});

  // =========================
  // FETCH DATA
  // =========================

  const fetchData = async () => {

    if (!routeName) {
      console.error("routeName is undefined");
      return;
    }

    try {

      const payload = {
        page,
        sizePerPage,
        sortDirection: "ASC",
        sortField: "id",
        search,
      };

      const res = await commonApi.list(routeName, payload);

      const responseData = res.data || {};

      setData(responseData.dtoList || []);

      setTotalPages(responseData.totalPage || 0);

      setTotalRecords(responseData.totalRecords || 0);

    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    fetchData();
  }, [page, sizePerPage, search]);

  // =========================
  // DELETE
  // =========================

  const handleDelete = async (identifier) => {

    if (!window.confirm("Delete this record?")) return;

    try {

      await commonApi.delete(
        routeName,
        "identifier",
        identifier
      );

      fetchData();

    } catch (err) {
      console.log(err);
    }
  };

  // =========================
  // TOGGLE
  // =========================

  const handleToggle = async (identifier) => {

    try {

      const response = await commonApi.toggle(
        routeName,
        identifier
      );

      const updatedItem = response.data;

      setData((prev) =>
        prev.map((row) =>
          row.identifier === identifier
            ? updatedItem
            : row
        )
      );

    } catch (err) {
      console.log(err);
    }
  };

  // =========================
  // ADD
  // =========================

  const handleAddClick = () => {

    if (formFields.length > 0) {

      setEditIdentifier(null);
      setEditInitialValues({});
      setShowModal(true);

    } else {

      navigate(addUrl);

    }
  };

  // =========================
  // EDIT
  // =========================

  const handleEditClick = (item) => {

    if (formFields.length > 0) {

      setEditIdentifier(item.identifier);
      setEditInitialValues(item);
      setOpenMenu(null);
      setShowModal(true);

    } else {

      navigate(`${editUrl}/${item.identifier}`);

    }
  };

  // =========================
  // CLOSE MODAL
  // =========================

  const handleCloseModal = () => {

    setShowModal(false);
    setEditIdentifier(null);
    setEditInitialValues({});
  };

  // =========================
  // STATUS
  // =========================

  const isActive = (value) => {
    return value === true || value === "true";
  };

  const getStockStatus = (item) => {

    const quantity = Number(item.quantity || 0);

    const minimumStock = Number(
      item.minimumStock || 0
    );

    if (quantity === 0) {
      return {
        label: "Out Of Stock",
        className: "bg-red-100 text-red-700",
      };
    }

    if (quantity < minimumStock) {
      return {
        label: "Low Stock",
        className: "bg-yellow-100 text-yellow-700",
      };
    }

    return {
      label: "In Stock",
      className: "bg-green-100 text-green-700",
    };
  };

  // =========================
  // PAGINATION
  // =========================

  const hasNextPage = page < totalPages - 1;

  const startRecord =
    totalRecords === 0
      ? 0
      : page * sizePerPage + 1;

  const endRecord = Math.min(
    (page + 1) * sizePerPage,
    totalRecords
  );

  return (
    <>
      <div className="bg-white rounded-xl shadow-sm overflow-hidden">

        {/* HEADER */}

        <div className="flex items-center justify-between p-5 border-b">

          <h2 className="text-2xl font-semibold text-gray-800">
            {title}
          </h2>

          <button
            onClick={handleAddClick}
            className="flex items-center gap-2 bg-red-600 hover:bg-red-700 text-white px-5 py-2 rounded-lg transition-all"
          >
            <Plus size={18} />
            Add New
          </button>
        </div>

        {/* FILTER */}

        <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-4 p-5">

          <div className="flex items-center gap-2">

            <span className="text-sm text-gray-600">
              Show
            </span>

            <select
              value={sizePerPage}
              onChange={(e) => {
                setSizePerPage(Number(e.target.value));
                setPage(0);
              }}
              className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none"
            >
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>

            <span className="text-sm text-gray-600">
              entries
            </span>
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
              className="w-full border border-gray-300 rounded-lg px-4 py-2 pr-10 focus:outline-none"
            />

            <Search
              size={18}
              className="absolute right-3 top-3 text-gray-400"
            />
          </div>
        </div>

        {/* TABLE */}

        <div className="overflow-x-auto">

          <table className="min-w-full border-collapse">

            <thead className="bg-gray-100">

              <tr>

                <th className="min-w-[70px] px-6 py-4 text-center text-sm font-semibold text-gray-700">
                  SL
                </th>

                {columns.map((column, index) => (

                  <th
                    key={index}
                    className={`
                      px-6 py-4 text-sm font-semibold text-gray-700
                      ${
                        column.type === "toggle" ||
                        column.type === "stockStatus"
                          ? "text-center min-w-[120px]"
                          : "text-left min-w-[220px]"
                      }
                    `}
                  >
                    {column.label}
                  </th>
                ))}

                <th className="min-w-[100px] px-6 py-4 text-center text-sm font-semibold text-gray-700">
                  Action
                </th>

              </tr>

            </thead>

            <tbody>

              {data.length > 0 ? (

                data.map((item, rowIndex) => (

                  <tr
                    key={rowIndex}
                    className="border-t hover:bg-gray-50 transition-colors"
                  >

                    <td className="min-w-[70px] px-6 py-5 text-center text-sm text-gray-700 align-top">
                      {page * sizePerPage + rowIndex + 1}
                    </td>

                    {columns.map((column, colIndex) => {

                      const stockStatus =
                        getStockStatus(item);

                      return (

                        <td
                          key={colIndex}
                          className={`
                            px-6 py-5 text-sm text-gray-700 align-top
                            ${
                              column.type === "toggle" ||
                              column.type === "stockStatus"
                                ? "text-center min-w-[120px]"
                                : "text-left min-w-[220px]"
                            }
                          `}
                        >

                          {/* TEXT */}

                          {(!column.type ||
                            column.type === "text") && (

                            <div className="whitespace-normal break-words leading-6">
                              {item[column.key]}
                            </div>
                          )}

                          {/* LIST */}

                          {column.type === "list" && (

                            <div className="flex flex-wrap gap-2">

                              {Array.isArray(item[column.key]) ? (

                                item[column.key].map(
                                  (value, i) => (

                                    <span
                                      key={i}
                                      className="px-2 py-1 bg-red-50 text-red-600 rounded-md text-xs border border-red-100"
                                    >
                                      {typeof value === "object"
                                        ? value[
                                            column.displayKey ||
                                              "identifier"
                                          ]
                                        : value}
                                    </span>
                                  )
                                )

                              ) : (

                                <span className="text-gray-400">
                                  -
                                </span>

                              )}
                            </div>
                          )}

                          {/* STOCK */}

                          {column.type === "stockStatus" && (

                            <span
                              className={`
                                px-3 py-1 rounded-full text-xs font-semibold
                                ${stockStatus.className}
                              `}
                            >
                              {stockStatus.label}
                            </span>
                          )}

                          {/* TOGGLE */}

                          {column.type === "toggle" && (

                            <div className="flex justify-center items-center">

                              <button
                                onClick={() =>
                                  handleToggle(
                                    item.identifier
                                  )
                                }
                                className={`
                                  relative inline-flex items-center
                                  w-11 h-6 rounded-full overflow-hidden
                                  transition-colors duration-300
                                  ${
                                    isActive(item[column.key])
                                      ? "bg-red-600"
                                      : "bg-gray-300"
                                  }
                                `}
                              >

                                <span
                                  className={`
                                    absolute left-0.5 top-0.5
                                    w-5 h-5 rounded-full bg-white shadow-md
                                    transition-transform duration-300
                                    ${
                                      isActive(item[column.key])
                                        ? "translate-x-5"
                                        : "translate-x-0"
                                    }
                                  `}
                                />
                              </button>
                            </div>
                          )}
                        </td>
                      );
                    })}

                    {/* ACTION */}

                    <td className="min-w-[100px] px-6 py-5 text-center relative align-top">

                      <button
                        onClick={() =>
                          setOpenMenu(
                            openMenu === rowIndex
                              ? null
                              : rowIndex
                          )
                        }
                        className="p-2 hover:bg-gray-100 rounded-full"
                      >
                        <MoreVertical size={18} />
                      </button>

                      {openMenu === rowIndex && (

                        <div
                          className={`
                            absolute right-5 w-40 bg-white border
                            rounded-lg shadow-xl z-50 overflow-hidden
                            ${
                              rowIndex >= data.length - 2
                                ? "bottom-full mb-2"
                                : "top-full mt-2"
                            }
                          `}
                        >

                          <button
                            onClick={() =>
                              handleEditClick(item)
                            }
                            className="w-full text-left px-4 py-2 text-sm text-blue-600 hover:bg-blue-50"
                          >
                            Edit
                          </button>

                          <button
                            onClick={() => {
                              setOpenMenu(null);
                              handleDelete(
                                item.identifier
                              );
                            }}
                            className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                          >
                            Delete
                          </button>
                        </div>
                      )}
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

        {/* FOOTER */}

        <div className="flex items-center justify-between p-5 border-t">

          <div className="text-sm text-gray-500">
            Showing {startRecord} to {endRecord} of{" "}
            {totalRecords} entries
          </div>

          <div className="flex items-center gap-2">

            <button
              disabled={page === 0}
              onClick={() => {
                if (page > 0) {
                  setPage((prev) => prev - 1);
                }
              }}
              className="px-4 py-2 border rounded-lg text-sm hover:bg-gray-50 disabled:opacity-50"
            >
              Previous
            </button>

            <button
              disabled={!hasNextPage}
              onClick={() => {
                if (hasNextPage) {
                  setPage((prev) => prev + 1);
                }
              }}
              className="px-4 py-2 border rounded-lg text-sm hover:bg-gray-50 disabled:opacity-50"
            >
              Next
            </button>

          </div>
        </div>
      </div>

      {/* MODAL */}

      {showModal && (

        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">

          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg mx-4">

            <div className="flex items-center justify-between px-6 py-5 border-b">

              <h2 className="text-xl font-semibold">
                {editIdentifier
                  ? `Edit ${formTitle || title}`
                  : `Add New ${formTitle || title}`}
              </h2>

              <button
                onClick={handleCloseModal}
                className="p-2 hover:bg-gray-100 rounded-lg"
              >
                <X size={20} />
              </button>
            </div>

            <DynamicForm
              fields={formFields}
              routeName={routeName}
              initialValues={editInitialValues}
              isModal={true}
              existingData={data}
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

export default DynamicList;