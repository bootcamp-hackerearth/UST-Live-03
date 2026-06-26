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
  const [allRows, setAllRows] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [errorMessage, setErrorMessage] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");

  useEffect(() => {
    fetchAllData();
  }, []);

  useEffect(() => {
    if (!searchQuery.trim()) {
      fetchListData();
    }
  }, [currentPage]);

  const fetchListData = async () => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8080/api/${apiPath}/list`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify({
            page: currentPage,
            sizePerPage: 4,
            sortField: "identifier",
            sortDirection: "ASC",
          }),
        },
      );

      const result = await response.json();
      setDataRows(result.dtoList || []);
      setTotalPages(result.totalPages || 0);
    } catch {
      setErrorMessage("Unable to load data");
    }
  };

  const fetchAllData = async () => {
    const token = localStorage.getItem("token");
    try {
      const firstResponse = await fetch(
        `http://localhost:8080/api/${apiPath}/list`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify({
            page: 0,
            sizePerPage: 4,
            sortField: "identifier",
            sortDirection: "ASC",
          }),
        },
      );

      const firstResult = await firstResponse.json();
      const total = firstResult.totalPages || 1;
      setTotalPages(total);
      setDataRows(firstResult.dtoList || []);

      const requests = [];
      for (let i = 1; i < total; i++) {
        requests.push(
          fetch(`http://localhost:8080/api/${apiPath}/list`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + token,
            },
            body: JSON.stringify({
              page: i,
              sizePerPage: 4,
              sortField: "identifier",
              sortDirection: "ASC",
            }),
          }).then((r) => {
            return r.json();
          }),
        );
      }

      const results = await Promise.all(requests);
      const combined = [
        ...(firstResult.dtoList || []),
        ...results.flatMap((r) => r.dtoList || []),
      ];
      setAllRows(combined);
    } catch {
      setErrorMessage("Unable to load data");
    }
  };

  const filteredRows = searchQuery.trim()
    ? allRows.filter((row) => {
        const query = searchQuery.toLowerCase();
        return columns.some((col) => {
          const value = row[col.key];
          if (col.key === "roles" && Array.isArray(value)) {
            return value.some((v) =>
              (typeof v === "object" ? v.name || v.identifier || "" : String(v))
                .toLowerCase()
                .includes(query),
            );
          }
          if (Array.isArray(value)) {
            return value.some((v) => String(v).toLowerCase().includes(query));
          }
          return String(value ?? "")
            .toLowerCase()
            .includes(query);
        });
      })
    : dataRows;

  const handleDelete = async (id) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8080/api/${apiPath}/delete?identifier=${id}`,
        {
          method: "DELETE",
          headers: { Authorization: "Bearer " + token },
        },
      );

      if (!response.ok) throw new Error("Delete request failed");
      setDeleteTarget(null);
      setSuccessMessage(`"${id}" removed successfully`);
      setTimeout(() => setSuccessMessage(""), 2000);
      fetchAllData();
    } catch {
      setErrorMessage("Delete failed");
    }
  };

  const handleStatusToggle = async (id, currentStatus) => {
    const token = localStorage.getItem("token");
    try {
      const response = await fetch(
        `http://localhost:8080/api/${apiPath}/toggleStatus`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: "Bearer " + token,
          },
          body: JSON.stringify({ identifier: id }),
        },
      );

      if (!response.ok) throw new Error("Status toggle request failed");
      setDataRows((prev) =>
        prev.map((item) =>
          item[identifierKey] === id
            ? { ...item, status: !currentStatus }
            : item,
        ),
      );
      setAllRows((prev) =>
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
          onChange={(e) => {
            setSearchQuery(e.target.value);
            setCurrentPage(0);
          }}
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
          {filteredRows.map((row, index) => (
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

      {!searchQuery.trim() && (
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
      )}
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
