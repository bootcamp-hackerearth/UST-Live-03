"use client";

import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

function List({
  title,
  apiPath,
  columns = [],
  addPath,
  editPath,
  showStatusToggle = false,
  identifierKey = "identifier",
}) {
  const router = useRouter();
  const [dataRows, setDataRows] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");

  useEffect(() => {
    setCurrentPage(0);
  }, [searchQuery]);

  useEffect(() => {
    const timer = setTimeout(() => {
      fetchListData(currentPage, searchQuery);
    }, 400);
    return () => clearTimeout(timer);
  }, [currentPage, searchQuery]);

  const handleAuthError = (status) => {
    if (status === 401) {
      router.push("/login");
      return true;
    }
    if (status === 403) {
      router.push("/error?status=403");
      return true;
    }
    return false;
  };

  const fetchListData = async (page, keyword) => {
    const token = localStorage.getItem("token");
    const payload = {
      page,
      sizePerPage: 4,
      sortField: "identifier",
      sortDirection: "ASC",
      keyword: keyword ? keyword.trim() : "",
    };

    console.log("[List] fetching", apiPath, payload);

    try {
      const response = await fetch(
        `/api/${apiPath}/list`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify(payload),
        },
      );

      if (handleAuthError(response.status)) return;

      const result = await response.json();

      console.log("[List] response", apiPath, result);

      setDataRows(result.dtoList || []);
      setTotalPages(result.totalPages || 0);
    } catch (err) {
      console.error("[List] fetch failed", err);
      setErrorMessage("Unable to load data");
    }
  };

  const handleDelete = async (id) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `/api/${apiPath}/delete?identifier=${id}`,
        {
          method: "DELETE",
          headers: { Authorization: "Bearer " + token },
        },
      );

      if (handleAuthError(response.status)) return;
      if (!response.ok) throw new Error("Delete request failed");

      setDeleteTarget(null);
      setSuccessMessage(`"${id}" removed successfully`);
      setTimeout(() => setSuccessMessage(""), 2000);
      fetchListData(currentPage, searchQuery);
    } catch {
      setErrorMessage("Delete failed");
    }
  };

  const handleStatusToggle = async (id, currentStatus) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `/api/${apiPath}/toggleStatus`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify({ identifier: id }),
        },
      );

      if (handleAuthError(response.status)) return;
      if (!response.ok) throw new Error("Status toggle request failed");

      setDataRows((prev) =>
        prev.map((item) =>
          item[identifierKey] === id
            ? { ...item, status: !currentStatus }
            : item,
        ),
      );
    } catch {
      console.log("Status toggle failed");
    }
  };

  const pageNumbers = Array.from({ length: totalPages }, (_, i) => i + 1);

  const headerStyle = {
    background: "#111",
    color: "#fff",
    padding: "12px",
    textAlign: "center",
  };

  const cellStyle = {
    padding: "10px",
    border: "1px solid #e5e7eb",
    textAlign: "center",
  };

  return (
    <div style={{ padding: 20, background: "#f5f5f5" }}>
      {deleteTarget && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            background: "rgba(0,0,0,0.5)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 1000,
          }}
        >
          <div style={{ background: "#fff", padding: 20, borderRadius: 8 }}>
            <h3>Confirm Delete</h3>
            <p>
              Delete <b>{deleteTarget}</b>?
            </p>
            <div style={{ display: "flex", gap: 10 }}>
              <button
                onClick={() => setDeleteTarget(null)}
                style={{
                  padding: "6px 12px",
                  borderRadius: 6,
                  cursor: "pointer",
                }}
              >
                Cancel
              </button>
              <button
                onClick={() => handleDelete(deleteTarget)}
                style={{
                  background: "#dc2626",
                  color: "#fff",
                  border: "none",
                  padding: "6px 12px",
                  borderRadius: 6,
                  cursor: "pointer",
                }}
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}

      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          marginBottom: 20,
        }}
      >
        <h2>{title}</h2>

        <input
          type="text"
          placeholder="Search..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          style={{
            padding: "10px 16px",
            border: "1px solid #d1d5db",
            borderRadius: "8px",
            fontSize: "14px",
            width: "250px",
            outline: "none",
          }}
        />

        <button
          onClick={() => router.push(addPath)}
          style={{
            width: 60,
            height: 60,
            borderRadius: 16,
            background: "#4b5563",
            color: "#fff",
            border: "none",
            fontSize: 28,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            cursor: "pointer",
          }}
        >
          +
        </button>
      </div>

      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
      {successMessage && <p style={{ color: "green" }}>{successMessage}</p>}

      <table
        width="100%"
        style={{ background: "#fff", borderCollapse: "collapse" }}
      >
        <thead>
          <tr>
            {columns.map((col) => (
              <th key={col.key} style={headerStyle}>
                {col.label}
              </th>
            ))}
            {showStatusToggle && <th style={headerStyle}>STATUS</th>}
            <th style={headerStyle}>ACTIONS</th>
          </tr>
        </thead>
        <tbody>
          {dataRows.map((row, index) => (
            <tr key={row[identifierKey] || index}>
              {columns.map((col) => {
                let cellContent;
                if (col.key === "roles") {
                  if (Array.isArray(row.roles)) {
                    cellContent = row.roles
                      .map((role) =>
                        typeof role === "object"
                          ? role.name || role.identifier
                          : role,
                      )
                      .join(", ");
                  } else {
                    cellContent = row.roles || "-";
                  }
                } else if (Array.isArray(row[col.key])) {
                  cellContent = row[col.key].join(", ");
                } else {
                  cellContent = row[col.key];
                }

                return (
                  <td key={col.key} style={cellStyle}>
                    {cellContent}
                  </td>
                );
              })}

              {showStatusToggle && (
                <td style={cellStyle}>
                  <button
                    type="button"
                    onClick={() =>
                      handleStatusToggle(row[identifierKey], row.status)
                    }
                    aria-label="Toggle status"
                    style={{
                      width: 40,
                      height: 20,
                      borderRadius: 20,
                      background: row.status ? "green" : "#ccc",
                      cursor: "pointer",
                      margin: "0 auto",
                      border: "none",
                      padding: 0,
                    }}
                  />
                </td>
              )}

              <td style={cellStyle}>
                <div
                  style={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    gap: 10,
                  }}
                >
                  <button
                    onClick={() =>
                      router.push(`${editPath}/${row[identifierKey]}`)
                    }
                    style={{
                      background: "#000",
                      borderRadius: "50%",
                      width: 34,
                      height: 34,
                      border: "none",
                      cursor: "pointer",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      color: "#fff",
                    }}
                  >
                    ✏️
                  </button>
                  <button
                    onClick={() => setDeleteTarget(row[identifierKey])}
                    style={{
                      background: "#000",
                      borderRadius: "50%",
                      width: 34,
                      height: 34,
                      border: "none",
                      cursor: "pointer",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      color: "#fff",
                    }}
                  >
                    🗑️
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div
        style={{
          display: "flex",
          justifyContent: "center",
          marginTop: 20,
          gap: 8,
        }}
      >
        {pageNumbers.map((p) => (
          <button
            key={p}
            onClick={() => setCurrentPage(p - 1)}
            style={{
              padding: "8px 12px",
              borderRadius: 6,
              border: "1px solid #111",
              background: currentPage === p - 1 ? "#111" : "#fff",
              color: currentPage === p - 1 ? "#fff" : "#111",
              cursor: "pointer",
            }}
          >
            {p}
          </button>
        ))}
      </div>
    </div>
  );
}

List.propTypes = {
  title: PropTypes.string.isRequired,
  apiPath: PropTypes.string.isRequired,
  columns: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
    }),
  ),
  addPath: PropTypes.string.isRequired,
  editPath: PropTypes.string.isRequired,
  showStatusToggle: PropTypes.bool,
  identifierKey: PropTypes.string,
};

export default List;