"use client";

import PropTypes from "../lib/propTypes";
import { useEffect, useState } from "react";
import axios from "axios";

import {
  PencilSquareIcon,
  TrashIcon,
  PlusIcon,
  MagnifyingGlassIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
} from "@heroicons/react/24/outline";

export default function CommonList({
  title,
  subtitle,
  apiUrl,
  deleteUrl,
  columns,
  dataKey,
  addButtonText,
  onDelete,
  FormComponent,
  formComponentProps = {},
}) {
  const [allData, setAllData] = useState([]);
  const [displayData, setDisplayData] = useState([]);
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const sizePerPage = 5;
  const [openModal, setOpenModal] =
    useState(false);

  const [mode, setMode] =
    useState("add");

  const [selectedData, setSelectedData] =
    useState(null);

  useEffect(() => {
    fetchAllData();
  }, []);

  useEffect(() => {
    handleSearchAndPagination();
  }, [search, allData, page]);

  const fetchAllData = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await axios.post(
        apiUrl,
        {
          page: 0,
          sizePerPage: 100,
          sortDirection: "ASC",
          sortField: "identifier",
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      console.log("API Response:", res.data);

      let responseData = [];

      if (Array.isArray(res.data)) {
        responseData = res.data;
      } else if (Array.isArray(res.data.dtoList)) {
        responseData = res.data.dtoList;
      } else if (Array.isArray(res.data.content)) {
        responseData = res.data.content;
      } else if (Array.isArray(res.data.data)) {
        responseData = res.data.data;
      }

      setAllData(responseData);

      const total =
        Math.ceil(responseData.length / sizePerPage);

      setTotalPages(total || 1);
    } catch (err) {
      console.log("Fetch Error:", err);
      setAllData([]);
    }
  };

  const handleSearchAndPagination = () => {
    let filtered = allData;

    if (search.trim() !== "") {
      filtered = allData.filter((item) =>
        columns.some((column) => {
          const value = item[column.key];

          if (value === null || value === undefined) {
            return false;
          }

          if (Array.isArray(value)) {
            return value
              .join(", ")
              .toLowerCase()
              .includes(search.toLowerCase());
          }

          return value
            .toString()
            .toLowerCase()
            .includes(search.toLowerCase());
        })
      );
    }

    const total = Math.ceil(filtered.length / sizePerPage);

    setTotalPages(total || 1);

    const start = page * sizePerPage;
    const end = start + sizePerPage;

    setDisplayData(filtered.slice(start, end));
  };

  const handleDelete = async (
    identifier
  ) => {
    const confirmDelete =
      globalThis.confirm(
        "Delete this item?"
      );

    if (!confirmDelete)
      return;

    try {
      const token =
        localStorage.getItem(
          "token"
        );

      const response =
        await axios.post(
          deleteUrl,
          identifier,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type":
                "text/plain",
            },
          }
        );

      if (
        response.data === true
      ) {
        fetchAllData();
      } else {
        alert(
          "Delete Failed"
        );
      }
    } catch (error) {
      console.log(error);
      alert(
        "Delete Failed"
      );
    }
  };

  const formatValue = (value) => {
    if (value === null || value === undefined) {
      return "-";
    }

    if (Array.isArray(value)) {
      return value.join(", ");
    }

    if (typeof value === "string") {
      return value.replaceAll(
        /([a-z])([A-Z])/g,
        "$1, $2"
      );
    }

    return value;
  };

  const buildRowKey = (item) => {
    const keyValue = item?.[dataKey];
    return keyValue !== undefined && keyValue !== null
      ? String(keyValue)
      : JSON.stringify(item);
  };

  const renderPageNumbers = () => {
    const pages = [];

    for (let i = 0; i < totalPages; i++) {
      pages.push(
        <button
          key={`page-${i}`}
          onClick={() => setPage(i)}
          className={`w-10 h-10 rounded-xl border transition-all duration-300 text-sm font-medium ${page === i
            ? "bg-black text-white border-black"
            : "bg-white border-gray-300 text-gray-600 hover:bg-gray-100"
            }`}
        >
          {i + 1}
        </button>
      );
    }

    return pages;
  };

  return (
    <div className="w-full min-h-full relative">
      <div className="w-full bg-white rounded-[24px] shadow-[0_10px_40px_rgba(0,0,0,0.06)] overflow-hidden">
        <div className="px-7 py-6 border-b border-gray-200 flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-black">
              {title}
            </h1>

            <p className="text-gray-500 text-sm mt-1">
              {subtitle}
            </p>
          </div>

          <button
            onClick={() => {
              console.log("CommonList: Add clicked", { title });
              setMode("add");
              setSelectedData(null);
              setOpenModal(true);
            }}
            className="h-12 px-5 rounded-xl bg-black text-white text-sm font-medium flex items-center gap-2 hover:bg-[#1A1A1A] transition-all"
          >
            <PlusIcon className="w-5 h-5" />
            {addButtonText}
          </button>
        </div>

        <div className="p-6">
          <div className="flex justify-end mb-6">
            <div className="relative w-[320px]">
              <MagnifyingGlassIcon className="w-5 h-5 absolute left-4 top-1/2 -translate-y-1/2 text-gray-400" />

              <input
                type="text"
                value={search}
                onChange={(e) => {
                  setSearch(e.target.value);
                  setPage(0);
                }}
                placeholder="Search..."
                className="w-full h-12 rounded-xl border border-gray-300 bg-white pl-12 pr-4 text-sm text-black outline-none focus:border-black"
              />
            </div>
          </div>

          <div className="overflow-x-auto rounded-2xl border border-gray-200">
            <table className="w-full">
              <thead className="bg-black text-white">
                <tr>
                  {columns.map((column) => (
                    <th
                      key={column.key}
                      className="px-5 py-4 text-left text-sm font-semibold"
                    >
                      {column.label}
                    </th>
                  ))}

                  <th className="px-5 py-4 text-center text-sm font-semibold">
                    Actions
                  </th>
                </tr>
              </thead>

              <tbody>
                {displayData.length === 0 ? (
                  <tr>
                    <td
                      colSpan={columns.length + 1}
                      className="py-16 text-center text-gray-500 text-sm"
                    >
                      No Data Found
                    </td>
                  </tr>
                ) : (
                  displayData.map((item) => (
                    <tr
                      key={buildRowKey(item)}
                      className="border-b border-gray-200 hover:bg-gray-50 transition-all"
                    >
                      {columns.map((column) => (
                        <td
                          key={column.key}
                          className="px-5 py-4 text-sm text-gray-700"
                        >
                          {formatValue(
                            item[column.key]
                          )}
                        </td>
                      ))}

                      <td className="px-5 py-4">
                        <div className="flex items-center justify-center gap-2">
                          <button
                            onClick={() => {
                              console.log("CommonList: Edit clicked", { title, item });
                              setMode("edit");
                              setSelectedData(item);
                              setOpenModal(true);
                            }}
                            className="w-10 h-10 rounded-xl bg-black text-white hover:bg-[#1A1A1A] flex items-center justify-center transition-all"
                          >
                            <PencilSquareIcon className="w-5 h-5" />
                          </button>

                         <button
                            onClick={() =>
                              handleDelete(
                                item[dataKey]
                              )
                            }
                            className="w-10 h-10 rounded-xl border border-red-500 text-red-500 hover:bg-red-500 hover:text-white flex items-center justify-center transition-all"
                          >
                            <TrashIcon className="w-5 h-5" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          <div className="flex items-center justify-center gap-3 mt-8 flex-wrap">
            <button
              disabled={page === 0}
              onClick={() =>
                setPage(page - 1)
              }
              className="h-10 px-4 rounded-xl border border-gray-300 text-gray-600 hover:bg-gray-100 disabled:opacity-40 transition-all flex items-center gap-2"
            >
              <ChevronLeftIcon className="w-4 h-4" />
              Prev
            </button>

            {renderPageNumbers()}

            <button
              disabled={
                page === totalPages - 1
              }
              onClick={() =>
                setPage(page + 1)
              }
              className="h-10 px-4 rounded-xl border border-gray-300 text-gray-600 hover:bg-gray-100 disabled:opacity-40 transition-all flex items-center gap-2"
            >
              Next
              <ChevronRightIcon className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {openModal &&
        FormComponent && (console.log("CommonList: Opening modal", { mode, selectedData }),
          <div className="absolute inset-0 z-20 flex items-start justify-center pt-12 bg-black/20">
            <div className="bg-white text-black rounded-3xl p-8 w-[700px] max-w-[90%] shadow-2xl relative">
              <button
                onClick={() =>
                  setOpenModal(false)
                }
                className="absolute top-4 right-4 text-3xl text-gray-500 hover:text-black"
              >
                ×
              </button>

              <FormComponent
                mode={mode}
                data={selectedData}
                onClose={() =>
                  setOpenModal(false)
                }
                onSuccess={() => {
                  setOpenModal(false);
                  fetchAllData();
                }}
                {...formComponentProps}
              />
            </div>
          </div>
        )}

    </div>
  );
}

CommonList.propTypes = {
  title: PropTypes.string.isRequired,
  subtitle: PropTypes.string,
  apiUrl: PropTypes.string.isRequired,
  deleteUrl: PropTypes.string.isRequired,
  columns: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string.isRequired,
      label: PropTypes.string,
    })
  ).isRequired,
  dataKey: PropTypes.string.isRequired,
  addButtonText: PropTypes.string,
  onDelete: PropTypes.func,
  FormComponent: PropTypes.elementType,
  formComponentProps: PropTypes.object,
};