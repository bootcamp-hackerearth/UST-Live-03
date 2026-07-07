"use client";

import React, { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import axios from "axios";
import {
  PencilSquareIcon,
  TrashIcon,
  PlusIcon,
  MagnifyingGlassIcon,
  XMarkIcon,
} from "@heroicons/react/24/outline";
import CommonEdit from "@/components/CommonEdit";

const API_BASE = process.env.NEXT_PUBLIC_BASE_URL

function CommonList({
  title,
  subtitle,
  apiRoute,
  columns,
  searchKeys,
  fields,
  dropdownApis,
}) {
  const router = useRouter();
  const [allData, setAllData] = useState([]);
  const [search, setSearch] = useState("");
  const [token, setToken] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedIdentifier, setSelectedIdentifier] = useState("");
  const [toggleLoadingId, setToggleLoadingId] = useState(null);

  const sizePerPage = 5;

  const apiUrl = `${API_BASE}/${apiRoute}/list`;
  const deleteUrl = `${API_BASE}/${apiRoute}/delete`;

  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    setToken(storedToken || "");
  }, []);

  useEffect(() => {
    if (token) {
      fetchList();
    }
  }, [token, page, search]);

  const fetchList = async () => {
    try {
      const storedToken = localStorage.getItem("token") || token;

      const res = await axios.post(
        apiUrl,
        {
          page,
          sizePerPage,
          sortDirection: "ASC",
          sortField: "identifier",
          keyword: search,
        },
        {
          headers: {
            Authorization: `Bearer ${storedToken}`,
          },
          withCredentials: true,
        }
      );

      setAllData(res.data.dtoList || []);
      setTotalPages(res.data.totalPages || 1);
    } catch (err) {
      console.error("Fetch list error:", err);
    }
  };

  const handleStatusToggle = async (item) => {
    const toggleKey = item.identifier || item.username;
    const itemId = item.id || item.identifier;

    try {
      setToggleLoadingId(itemId);
      const storedToken = localStorage.getItem("token") || token;

      await axios.post(
        `${API_BASE}/${apiRoute}/toggle`,
        toggleKey,
        {
          headers: {
            Authorization: `Bearer ${storedToken}`,
            "Content-Type": "text/plain",
          },
          transformRequest: [(data) => data],
          withCredentials: true,
        }
      );

      setAllData((prevData) =>
        prevData.map((row) => {
          const isMatch =
            (item.id !== undefined && item.id !== null && row.id === item.id) ||
            (item.username && row.username === item.username) ||
            (item.identifier && row.identifier === item.identifier);

          return isMatch ? { ...row, status: !row.status } : row;
        })
      );

      globalThis.dispatchEvent(new Event("nodeDataChanged"));
    } catch (err) {
      console.error("Toggle status error:", err);
      alert("Failed to execute status path toggle endpoint.");
    } finally {
      setToggleLoadingId(null);
    }
  };

  const paginatedData = allData;

  const handleDelete = async (item) => {
    const confirmDelete = globalThis.confirm("Delete item?");
    if (!confirmDelete) return;

    const deleteKey = item.identifier || item.username;
    const currentLoggedInUser = localStorage.getItem("username");

    try {
      const response = await axios.delete(deleteUrl, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "text/plain",
        },
        data: deleteKey,
        transformRequest: [(data) => data],
        withCredentials: true,
      });

      if (response.data === true || response.status === 200 || response.status === 204) {
        globalThis.dispatchEvent(new Event("nodeDataChanged"));

        if (currentLoggedInUser && deleteKey === currentLoggedInUser) {
          localStorage.clear();
          router.push("/login");
        } else {
          fetchList();
        }
      } else {
        alert("Delete Failed");
      }
    } catch (err) {
      console.error("Delete Error:", err);
      alert("Delete Failed");
    }
  };

  const renderPagination = () => {
    const pages = [];
    const start = Math.max(0, page - 1);
    const end = Math.min(totalPages - 1, page + 1);

    if (start > 0) {
      pages.push(
        <button
          type="button"
          key="page-0"
          onClick={() => setPage(0)}
          className="w-9 h-9 bg-white border border-[#ebebf5] rounded-md text-[#4b4b75] text-sm font-medium cursor-pointer transition-all hover:border-[#6c63ff] hover:text-[#6c63ff]"
        >
          1
        </button>
      );
      if (start > 1) {
        pages.push(<span key="dots1" className="text-[#8888a0] text-sm px-1">...</span>);
      }
    }

    for (let i = start; i <= end; i++) {
      pages.push(
        <button
          type="button"
          key={`page-${i}`}
          onClick={() => setPage(i)}
          className={`w-9 h-9 border rounded-md text-sm font-medium cursor-pointer transition-all ${page === i
              ? "bg-[#6c63ff] border-[#6c63ff] text-white"
              : "bg-white border-[#ebebf5] text-[#4b4b75] hover:border-[#6c63ff] hover:text-[#6c63ff]"
            }`}
        >
          {i + 1}
        </button>
      );
    }

    if (end < totalPages - 2) {
      pages.push(<span key="dots2" className="text-[#8888a0] text-sm px-1">...</span>);
    }

    if (totalPages > 1 && end < totalPages - 1) {
      pages.push(
        <button
          type="button"
          key={`page-${totalPages - 1}`}
          onClick={() => setPage(totalPages - 1)}
          className="w-9 h-9 bg-white border border-[#ebebf5] rounded-md text-[#4b4b75] text-sm font-medium cursor-pointer transition-all hover:border-[#6c63ff] hover:text-[#6c63ff]"
        >
          {totalPages}
        </button>
      );
    }

    return pages;
  };

  return (
    <div className="min-h-screen bg-[#f4f5fa] p-10 font-sans box-border">

      <div className="flex items-start justify-between mb-3">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight text-[#2d2d6e]">{title}</h1>
          <p className="text-sm text-[#8888a0] mt-1">{subtitle}</p>
        </div>
      </div>

      <div className="flex items-center justify-between gap-4 mb-8">
        <div className="text-xs font-medium text-[#8888a0] bg-[#eef0f6] px-3 py-1 rounded-xl">
          {allData.length} active {title ? title.toLowerCase() : "items"}
        </div>

        <div className="flex items-center gap-4">
          <div className="relative w-[240px]">
            <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#b0b0c8]" />
            <input
              type="text"
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setPage(0);
              }}
              placeholder="Search..."
              className="w-full h-[38px] bg-white border border-[#ebebf5] rounded-lg pl-9 pr-3 text-xs text-[#2d2d6e] outline-none transition-all focus:border-[#6c63ff] placeholder-[#b0b0c8]"
            />
          </div>

          <button
            type="button"
            onClick={() => setShowAddModal(true)}
            className="h-[38px] px-4 bg-[#6c63ff] hover:bg-[#5850ec] border-none rounded-lg text-white text-xs font-medium flex items-center gap-1.5 cursor-pointer transition-all"
          >
            <PlusIcon className="w-4 h-4" />
            Add
          </button>
        </div>
      </div>

      <div className="w-full overflow-x-auto">
        <table className="w-full border-collapse text-left">
          <thead>
            <tr className="border-b-2 border-[#ebebf5]">
              {columns?.map((col) => (
                <th
                  key={col.key || col.label}
                  style={{ width: col.width || "auto" }}
                  className="text-xs font-semibold text-[#8888a0] uppercase tracking-wider px-6 py-4"
                >
                  {col.label}
                </th>
              ))}
              <th className="text-xs font-semibold text-[#8888a0] uppercase tracking-wider px-6 py-4 w-[120px]">
                Actions
              </th>
            </tr>
          </thead>
          <tbody>
            {paginatedData.length === 0 ? (
              <tr>
                <td
                  colSpan={(columns?.length || 0) + 1}
                  className="text-center py-12 text-sm text-[#8888a0]"
                >
                  No Data Found
                </td>
              </tr>
            ) : (
              paginatedData.map((item) => (
                <tr
                  key={item.identifier || item.username || `row-${item.id}`}
                  className="border-b border-[#ebebf5] group"
                >
                  {columns?.map((col) => {
                    if (col.key === "status") {
                      const isItemLoading = toggleLoadingId === (item.id || item.identifier);
                      return (
                        <td
                          key={`${item.identifier || item.username || item.id}-${col.key}`}
                          className="text-sm text-[#2d2d6e] px-6 py-5 bg-transparent transition-colors group-hover:bg-[#ebebf5]/30"
                        >
                          <div className="flex items-center">
                            <button
                              type="button"
                              disabled={isItemLoading}
                              onClick={() => handleStatusToggle(item)}
                              className={`relative inline-flex h-5 w-10 shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none disabled:opacity-50 ${item.status ? "bg-[#6c63ff]" : "bg-zinc-200"
                                }`}
                            >
                              <span
                                className={`pointer-events-none inline-block h-4 w-4 transform rounded-full bg-white shadow-sm ring-0 transition duration-200 ease-in-out ${item.status ? "translate-x-5" : "translate-x-0"
                                  }`}
                              />
                            </button>
                          </div>
                        </td>
                      );
                    }

                    return (
                      <td
                        key={`${item.identifier || item.username || item.id}-${col.key}`}
                        className="text-sm text-[#2d2d6e] px-6 py-5 bg-transparent transition-colors group-hover:bg-[#ebebf5]/30"
                      >
                        {col.render ? col.render(item) : item[col.key]}
                      </td>
                    );
                  })}
                  <td className="px-6 py-5 bg-transparent transition-colors group-hover:bg-[#ebebf5]/30">
                    <div className="flex gap-3">
                      <button
                        type="button"
                        onClick={() => {
                          setSelectedIdentifier(item.identifier || item.username);
                          setShowEditModal(true);
                        }}
                        className="inline-flex items-center justify-center p-1 bg-transparent border-none text-[#b0b0c8] hover:text-[#6c63ff] cursor-pointer transition-all"
                      >
                        <PencilSquareIcon className="w-[18px] h-[18px]" />
                      </button>
                      <button
                        type="button"
                        onClick={() => handleDelete(item)}
                        className="inline-flex items-center justify-center p-1 bg-transparent border-none text-[#b0b0c8] hover:text-[#e55555] cursor-pointer transition-all"
                      >
                        <TrashIcon className="w-[18px] h-[18px]" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-2 mt-8">
          <button
            type="button"
            onClick={() => setPage(page - 1)}
            disabled={page === 0}
            className="h-9 px-3.5 bg-white border border-[#ebebf5] rounded-md text-[#4b4b75] text-xs font-medium cursor-pointer transition-all hover:border-[#6c63ff] hover:text-[#6c63ff] disabled:opacity-40 disabled:cursor-not-allowed"
          >
            Prev
          </button>
          {renderPagination()}
          <button
            type="button"
            onClick={() => setPage(page + 1)}
            disabled={page === totalPages - 1}
            className="h-9 px-3.5 bg-white border border-[#ebebf5] rounded-md text-[#4b4b75] text-xs font-medium cursor-pointer transition-all hover:border-[#6c63ff] hover:text-[#6c63ff] disabled:opacity-40 disabled:cursor-not-allowed"
          >
            Next
          </button>
        </div>
      )}

      {(showAddModal || showEditModal) && (
        <div className="fixed inset-0 z-40 bg-[#f4f5fa]/70 backdrop-blur-xs overflow-y-auto px-6 py-10 box-border">
          <div className="flex justify-center w-full">
            <div className="relative w-full max-w-[920px]">
              <button
                type="button"
                onClick={() => {
                  setShowAddModal(false);
                  setShowEditModal(false);
                }}
                className="absolute top-6 right-6 z-50 w-9 h-9 bg-white border border-[#ebebf5] rounded-full flex items-center justify-center text-[#2d2d6e] shadow-xs cursor-pointer transition-all hover:border-[#6c63ff] hover:text-[#6c63ff]"
              >
                <XMarkIcon className="w-4 h-4" />
              </button>

              <CommonEdit
                title={title}
                fields={fields}
                apiRoute={apiRoute}
                dropdownApis={dropdownApis}
                method={showAddModal ? "add" : "update"}
                identifier={selectedIdentifier}
                closeModal={() => {
                  setShowAddModal(false);
                  setShowEditModal(false);
                  fetchList();
                }}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

CommonList.propTypes = {
  title: PropTypes.string,
  subtitle: PropTypes.string,
  apiRoute: PropTypes.string.isRequired,
  columns: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string,
      label: PropTypes.string,
      width: PropTypes.string,
      render: PropTypes.func,
    })
  ),
  searchKeys: PropTypes.arrayOf(PropTypes.string),
  fields: PropTypes.arrayOf(PropTypes.object),
  dropdownApis: PropTypes.object,
};

CommonList.defaultProps = {
  title: "",
  subtitle: "",
  columns: [],
  searchKeys: [],
  fields: [],
  dropdownApis: {},
};

export default CommonList;