"use client";

import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { FaEdit, FaTrash } from "react-icons/fa";

const CommonList = ({
  keys,
  routeName,
  editField = "identifier",
  FormComponent,
}) => {
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [open, setOpen] = useState(false);
  const [mode, setMode] = useState("add");
  const [editData, setEditData] = useState(null);

  const [search, setSearch] = useState("");

  useEffect(() => {
    fetchData();
  }, [routeName, page]);

  const fetchData = async () => {

    try {
      const res = await fetch(`http://localhost:8080/api/${routeName}/list`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ page, sizePerPage: 10 }),
      });

      if (!res.ok) {
        throw new Error("Request failed");
      }

      const text = await res.text();
      const result = text ? JSON.parse(text) : {};

      setData(result.content || []);
      setTotalPages(result.totalPages || 1);
    }
    catch (error) {
      console.error("Fetch data failed:", error);

      localStorage.removeItem("token");
      localStorage.removeItem("username");
      globalThis.location.href = "/login";
    }
  };

  const deleteData = async (identifier) => {
    const confirmDelete = globalThis.confirm("Delete this item?");
    if (!confirmDelete) return;

    const res = await fetch(`http://localhost:8080/api/${routeName}/delete`, {
      method: "POST",
      headers: {
        "Content-Type": "text/plain",
      },
      credentials: "include",
      body: identifier,
    });

    let result = null;
    try {
      result = await res.json();
    } catch {
      result = null;
    }

    if (result === true || result?.success === true) {
      fetchData();
    } else {
      alert(result?.message || "Delete failed");
    }
  };

  const openAdd = () => {
    setMode("add");
    setEditData(null);
    setOpen(true);
  };

  const openEdit = (item) => {
    setMode("edit");
    setEditData(item);
    setOpen(true);
  };

  const filteredData = data.filter((item) =>
    keys.some((key) =>
      String(item[key] ?? "")
        .toLowerCase()
        .includes(search.toLowerCase())
    )
  );

  const headers = [...keys, "Edit", "Delete"];

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
      if (routeName === "user") {
        url = "http://localhost:8080/api/user/register";
      } else {
        url = `http://localhost:8080/api/${routeName}/add`;
      }
    } else {
      url = `http://localhost:8080/api/${routeName}/update`;
    }
    const res = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      credentials: "include",
      body: JSON.stringify(formData),
    });

    let result = null;
    let rawText = null;
    const contentType = res.headers.get("content-type") || "";

    try {
      if (contentType.includes("application/json")) {
        result = await res.json();
      } else {
        rawText = await res.text().catch(() => null);
      }
    }
    catch (err) {
      console.error("Response parsing failed:", err);

      try {
        rawText = await res.text();
      } catch (error_) {
        console.error("Reading raw text failed:", error_);
        rawText = null;
      }
    }
    const message =
      getMessageFromResult(result) ||
      (typeof rawText === "string" ? rawText : null) ||
      "Save failed. Please try again.";

    const normalized = message.toString().toLowerCase();

    const failedResponse =
      !res.ok ||
      result === false ||
      result?.success === false ||
      result?.success === null ||
      result?.status === false;

    const duplicateResponse =
      normalized.includes("already exists") ||
      normalized.includes("already exist") ||
      normalized.includes("duplicate");

    if (failedResponse || duplicateResponse) {
      alert(message);
      return;
    }

    setOpen(false);
    fetchData();
  };

  return (
    <div className="bg-gray-100 shadow-lg rounded-xl w-full p-4">
      <h2 className="text-lg font-semibold text-center mb-3">
        {routeName.toUpperCase()} LIST
      </h2>

      <div className="flex justify-end mb-3">
        <input
          type="text"
          placeholder="Search..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-64 px-3 py-1.5 border rounded-md text-sm"
        />
      </div>

      <div className="overflow-x-auto">
        <table className="w-full border text-center text-sm">
          <thead className="bg-gray-200">
            <tr>
              {headers.map((h) => (
                <th key={h} className="border px-3 py-2">
                  {h}
                </th>
              ))}
            </tr>
          </thead>

          <tbody>
            {filteredData.map((item) => {
              const rowKey =
                item[editField] ??
                keys.map((k) => String(item[k] ?? "")).join("|") ??
                JSON.stringify(item);

              return (
                <tr key={rowKey} className="hover:bg-gray-50">
                  {keys.map((k) => (
                    <td key={k} className="border px-3 py-2">
                      {Array.isArray(item[k]) ? item[k].join(", ") : item[k]}
                    </td>
                  ))}

                  <td className="border px-3 py-2">
                    <button
                      onClick={() => openEdit(item)}
                      className="bg-blue-500 text-white p-1.5 rounded"
                    >
                      <FaEdit size={14} />
                    </button>
                  </td>

                  <td className="border px-3 py-2">
                    <button
                      onClick={() => deleteData(item[editField])}
                      className="bg-red-500 text-white p-1.5 rounded"
                    >
                      <FaTrash size={14} />
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <div className="flex justify-center gap-2 mt-4">
        <button
          onClick={() => setPage((p) => Math.max(p - 1, 0))}
          disabled={page === 0}
          className="bg-gray-400 text-white px-3 py-1 rounded"
        >
          Prev
        </button>

        <span>
          Page {page + 1} / {totalPages}
        </span>

        <button
          onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
          disabled={page >= totalPages - 1}
          className="bg-blue-500 text-white px-3 py-1 rounded"
        >
          Next
        </button>
      </div>

      <div className="flex justify-center mt-4">
        <button
          onClick={openAdd}
          className="bg-blue-600 text-white px-4 py-2 rounded"
        >
          + Add {routeName}
        </button>
      </div>

      {open && FormComponent && (
        <FormComponent
          title={routeName}
          mode={mode}
          data={editData}
          onClose={() => setOpen(false)}
          onSubmit={handleSubmit}
        />
      )}
    </div>
  );
};

CommonList.propTypes = {
  keys: PropTypes.array.isRequired,
  routeName: PropTypes.string.isRequired,
  editField: PropTypes.string,
  FormComponent: PropTypes.oneOfType([PropTypes.func, PropTypes.object]),
};

export default CommonList;