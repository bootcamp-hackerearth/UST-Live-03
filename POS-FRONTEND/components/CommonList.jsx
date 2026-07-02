"use client";

import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { PencilSquareIcon, TrashIcon, PlusIcon, MagnifyingGlassIcon } from "@heroicons/react/24/outline";

export default function CommonList({
  keys,
  routeName,
  editField = "identifier",
  FormComponent,
  headers,
}) {
  const [displayData, setDisplayData] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [open, setOpen] = useState(false);
  const [mode, setMode] = useState("add");
  const [editData, setEditData] = useState(null);
  const [search, setSearch] = useState("");
  const sizePerPage = 2;

  useEffect(() => {
    setPage(0);
    fetchData(0, "");
}, [routeName]);

  useEffect(() => {
    fetchData(page, search);
}, [page, search]);

  const fetchData = async (currentPage = page, keyword = search) => {
  try {
    const res = await fetch(
      `http://localhost:8080/api/${routeName}/list`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: JSON.stringify({
          page: currentPage,
          sizePerPage,
          sortDirection: "ASC",
          sortField: editField,
          keyword,
        }),
      }
    );

    if (!res.ok) {
  const errorText = await res.text();
  console.log("Status:", res.status);
  console.log("Error:", errorText);
  throw new Error(errorText);
}

    const result = await res.json();

    const responseData =
      result.content ||
      result.dtoList ||
      result.data ||
      [];

    setDisplayData(responseData);

    setTotalPages(
      result.totalPages ||
      result.paginationDto?.totalPages ||
      1
    );
  } catch (error) {
    console.error(error);
    setDisplayData([]);
    setTotalPages(1);
  }
};

  const deleteData = async (identifier) => {
    const confirmDelete = globalThis.confirm("Delete this item?");
    if (!confirmDelete) return;

    try {
      const res = await fetch(`http://localhost:8080/api/${routeName}/delete`, {
        method: "DELETE",
        headers: {
          "Content-Type": "text/plain",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: String(identifier),
      });

      const result = await res.json().catch(() => null);
      if (result === true || result?.success === true) {
        fetchData(page, search);
      } else {
        alert("Delete failed");
      }
    } catch (error) {
      console.error(error);
    }
  };

  const toggleStatus = async (item) => {
    try {
      const res = await fetch(`http://localhost:8080/api/${routeName}/toggle`, {
        method: "POST",
        headers: {
          "Content-Type": "text/plain",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: String(item[editField]),
      });

      if (res.ok) fetchData();
    } catch (error) {
      console.error(error);
    }
  };

  const getMessageFromResult = (value) => {
    if (!value) return null;
    if (typeof value === "string") return value;
    if (typeof value === "object") {
      return (
        value.message ||
        value.error ||
        value.errorMessage ||
        value.msg ||
        value.detail ||
        value.description ||
        Object.values(value).map(getMessageFromResult).find(Boolean) ||
        null
      );
    }
    return null;
  };

  const handleSubmit = async (formData) => {
    let url;
    if (mode === "add") {
      url = routeName === "user"
        ? "http://localhost:8080/api/user/register"
        : `http://localhost:8080/api/${routeName}/add`;
    } else {
      url = `http://localhost:8080/api/${routeName}/update`;
    }

    try {
      const res = await fetch(url, {
        method: mode === "add" ? "POST" : "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
        body: JSON.stringify(formData),
      });

      let result = null;
      let rawText = null;
      const contentType = res.headers.get("content-type") || "";

      if (contentType.includes("application/json")) {
        result = await res.json();
      } else {
        rawText = await res.text().catch(() => null);
      }

      const message = getMessageFromResult(result) || rawText || "Save failed. Please try again.";
      const normalized = message.toString().toLowerCase();

      if (!res.ok || result === false || result?.success === false || normalized.includes("exists") || normalized.includes("duplicate")) {
        alert(message);
        return;
      }

      setOpen(false);
      fetchData();
    } catch (error) {
      console.error(error);
    }
  };

  const formatValue = (value) => {
    if (value === null || value === undefined) return "-";
    if (Array.isArray(value)) return value.join(", ");
    if (typeof value === "string") return value.replaceAll(/([a-z])([A-Z])/g, "$1, $2");
    return value;
  };

  const buildRowKey = (item) => {
    const keyValue = item?.[editField];
    return keyValue !== undefined && keyValue !== null ? String(keyValue) : JSON.stringify(item);
  };

  const renderPageNumbers = () => {
    const pages = [];
    for (let i = 0; i < totalPages; i++) {
      pages.push(
        <button
          key={`page-${i}`}
          type="button"
          onClick={() => setPage(i)}
          className={`w-8 h-8 rounded-lg border text-xs font-semibold transition-all ${page === i
              ? "bg-neutral-900 text-white border-neutral-900 shadow-sm"
              : "bg-white border-neutral-200 text-neutral-600 hover:bg-neutral-50"
            }`}
        >
          {i + 1}
        </button>
      );
    }
    return pages;
  };

  return (
    <div className="w-full min-h-screen bg-[#F8F9FA] p-6 text-neutral-900 antialiased font-sans">
      <div className="max-w-[1500px] mx-auto bg-white rounded-2xl border border-neutral-200/60 shadow-[0_2px_8px_-3px_rgba(0,0,0,0.05)] overflow-hidden">
        <div className="px-8 py-5 border-b border-neutral-100 flex items-center justify-between bg-neutral-50/50">
          <div>
            <h1 className="text-xl font-bold tracking-tight text-neutral-900 capitalize">{routeName} Management</h1>
            <p className="text-xs text-neutral-400 font-medium mt-0.5">Configure operational metadata parameters</p>
          </div>
          <button
            type="button"
            onClick={() => {
              setMode("add");
              setEditData(null);
              setOpen(true);
            }}
            className="h-10 px-4 rounded-xl bg-neutral-900 text-white text-xs font-semibold flex items-center gap-2 hover:bg-neutral-800 transition-all active:scale-[0.98] shadow-sm"
          >
            <PlusIcon className="w-4 h-4 stroke-[2.5]" />
            Add {routeName?.toUpperCase()}
          </button>
        </div>

        <div className="p-6">
          <div className="flex justify-end mb-4">
            <div className="relative w-full sm:w-[320px]">
              <MagnifyingGlassIcon className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-neutral-400" />
              <input
                type="text"
                placeholder="Search..."
                value={search}
                onChange={(e) => {
    setSearch(e.target.value);
    setPage(0);
}}
                className="w-full h-10 rounded-xl border border-neutral-200 bg-neutral-50/50 pl-10 pr-4 text-xs text-neutral-800 outline-none focus:border-neutral-900 focus:bg-white transition-all focus:ring-2 focus:ring-neutral-900/5"
              />
            </div>
          </div>

          <div className="overflow-x-auto rounded-xl border border-neutral-200/60">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-neutral-50/70 border-b border-neutral-200/60 text-[11px] font-bold uppercase tracking-wider text-neutral-400">
                  {(headers || keys || []).map((headerLabel) => (
                    <th key={headerLabel} className="p-4 first:pl-6 capitalize">{headerLabel}</th>
                  ))}
                  <th className="p-4 pr-6 text-center w-[120px]">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-neutral-100 text-sm">
                {displayData.length === 0 ? (
                  <tr>
                    <td colSpan={(keys || []).length + 1} className="py-16 text-center text-neutral-400 text-xs">
                      No Data Found
                    </td>
                  </tr>
                ) : (
                  displayData.map((item) => {
                    return (
                      <tr key={buildRowKey(item)} className="hover:bg-neutral-50/40 transition-colors">
                        {(keys || []).map((k) => (
                          <td key={k} className="p-4 first:pl-6 text-neutral-800 font-medium">
                            {k === "status" ? (
                              <button
                                type="button"
                                onClick={() => toggleStatus(item)}
                                className={`relative inline-flex h-5 w-9 cursor-pointer items-center rounded-full transition-all duration-200 border outline-none ${item.status ? "bg-neutral-900 border-neutral-900" : "bg-neutral-200 border-neutral-200"
                                  }`}
                              >
                                <span
                                  className={`inline-block h-3.5 w-3.5 transform rounded-full bg-white shadow transition-all duration-200 ${item.status ? "translate-x-4.5" : "translate-x-0.5"
                                    }`}
                                />
                              </button>
                            ) : (
                              formatValue(item[k])
                            )}
                          </td>
                        ))}
                        <td className="p-4 pr-6">
                          <div className="flex items-center justify-center gap-1.5">
                            <button
                              type="button"
                              onClick={() => {
                                setMode("edit");
                                setEditData(item);
                                setOpen(true);
                              }}
                              className="w-8 h-8 rounded-lg bg-white border border-neutral-200 text-neutral-700 hover:border-neutral-900 hover:text-neutral-900 flex items-center justify-center transition-colors shadow-sm"
                            >
                              <PencilSquareIcon className="w-4 h-4" />
                            </button>
                            <button
                              type="button"
                              onClick={() => deleteData(item[editField])}
                              className="w-8 h-8 rounded-lg bg-white border border-neutral-200 text-neutral-400 hover:border-rose-600 hover:text-rose-600 flex items-center justify-center transition-colors shadow-sm"
                            >
                              <TrashIcon className="w-4 h-4" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>

          <div className="flex items-center justify-center gap-1.5 mt-6 flex-wrap">
            <button
              type="button"
              disabled={page === 0}
              onClick={() => setPage((p) => Math.max(p - 1, 0))}
              className="h-8 px-3 rounded-lg border border-neutral-200 text-neutral-600 hover:bg-neutral-50 disabled:opacity-40 transition-all flex items-center gap-1 text-xs font-medium"
            >
              Prev
            </button>

            {renderPageNumbers()}

            <button
              type="button"
              disabled={page >= totalPages - 1}
              onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
              className="h-8 px-3 rounded-lg border border-neutral-200 text-neutral-600 hover:bg-neutral-50 disabled:opacity-40 transition-all flex items-center gap-1 text-xs font-medium"
            >
              Next
            </button>
          </div>
        </div>
      </div>

      {open && FormComponent && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-neutral-900/40 backdrop-blur-sm">
          <div className="bg-white text-neutral-900 rounded-2xl p-6 w-[440px] max-w-full border border-neutral-100 shadow-2xl relative">
            <button
              type="button"
              onClick={() => setOpen(false)}
              className="absolute top-4 right-4 w-7 h-7 flex items-center justify-center rounded-lg border border-neutral-200 text-neutral-400 hover:text-neutral-600 text-lg"
            >
              &times;
            </button>

            <FormComponent
              title={routeName}
              mode={mode}
              data={editData}
              onClose={() => setOpen(false)}
              onSubmit={handleSubmit}
            />
          </div>
        </div>
      )}
    </div>
  );
}

CommonList.propTypes = {
  keys: PropTypes.array.isRequired,
  routeName: PropTypes.string.isRequired,
  editField: PropTypes.string,
  FormComponent: PropTypes.oneOfType([PropTypes.func, PropTypes.object]),
  headers: PropTypes.array,
};