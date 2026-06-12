"use client";

import { useEffect, useState } from "react";
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

function CommonList({
  title,
  subtitle,
  apiUrl,
  deleteUrl,
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

  const sizePerPage = 10;

  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    setToken(storedToken || "");
  }, []);

  useEffect(() => {
    if (token) {
      fetchList();
    }
  }, [token]);

  const fetchList = async () => {
    try {
      const storedToken = localStorage.getItem("token") || token;
      const res = await axios.post(
        apiUrl,
        {
          page: 0,
          sizePerPage: 100000,
          sortDirection: "ASC",
          sortField: "identifier",
        },
        {
          headers: {
            Authorization: `Bearer ${storedToken}`,
          },
          withCredentials: true,
        }
      );

      let responseData = [];
      if (Array.isArray(res.data)) {
        responseData = res.data;
      } else {
        responseData = res.data.dtoList || [];
      }
      setAllData(responseData);
    } catch (err) {
      console.error("Fetch list error:", err);
    }
  };

  const filteredData = allData.filter((item) =>
    searchKeys?.some((key) =>
      item[key]?.toString().toLowerCase().includes(search.toLowerCase())
    )
  );

  const paginatedData = filteredData.slice(
    page * sizePerPage,
    (page + 1) * sizePerPage
  );

  useEffect(() => {
    setTotalPages(Math.ceil(filteredData.length / sizePerPage) || 1);
  }, [filteredData]);

  const handleDelete = async (item) => {
    const confirmDelete = globalThis.confirm("Delete item?");
    if (!confirmDelete) return;

    const deleteKey = item.identifier || item.username;
    const currentLoggedInUser = localStorage.getItem("username");

    try {
      const response = await axios.post(deleteUrl, deleteKey, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "text/plain",
        },
        transformRequest: [(data) => data],
        withCredentials: true,
      });

      if (response.data === true) {
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
        <button type="button" key="page-0" onClick={() => setPage(0)} className="page-btn">
          1
        </button>
      );
      if (start > 1) {
        pages.push(<span key="dots1" className="page-dots">...</span>);
      }
    }

    for (let i = start; i <= end; i++) {
      pages.push(
        <button
          type="button"
          key={`page-${i}`}
          onClick={() => setPage(i)}
          className={`page-btn ${page === i ? "active" : ""}`}
        >
          {i + 1}
        </button>
      );
    }

    if (end < totalPages - 2) {
      pages.push(<span key="dots2" className="page-dots">...</span>);
    }

    if (totalPages > 1 && end < totalPages - 1) {
      pages.push(
        <button
          type="button"
          key={`page-${totalPages - 1}`}
          onClick={() => setPage(totalPages - 1)}
          className="page-btn"
        >
          {totalPages}
        </button>
      );
    }

    return pages;
  };

  return (
    <>
      <style>{`
        .list-wrapper {
          min-height: 100vh;
          background-color: #f4f5fa;
          padding: 40px;
          font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
          box-sizing: border-box;
        }

        .list-header {
          display: flex;
          align-items: flex-start;
          justify-content: space-between;
          margin-bottom: 12px;
        }

        .title-text {
          font-size: 26px;
          font-weight: 600;
          color: #2d2d6e;
          letter-spacing: -0.5px;
        }

        .subtitle-text {
          font-size: 14px;
          color: #8888a0;
          margin-top: 4px;
        }

        .meta-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 32px;
          gap: 16px;
        }

        .count-badge {
          font-size: 13px;
          color: #8888a0;
          background: #eef0f6;
          padding: 4px 12px;
          border-radius: 12px;
          font-weight: 500;
        }

        .action-controls {
          display: flex;
          align-items: center;
          gap: 16px;
        }

        .search-container {
          position: relative;
          width: 240px;
        }

        .search-icon {
          position: absolute;
          left: 12px;
          top: 50%;
          transform: translateY(-50%);
          width: 16px;
          height: 16px;
          color: #b0b0c8;
        }

        .search-input {
          width: 100%;
          height: 38px;
          background: #ffffff;
          border: 1.5px solid #ebebf5;
          border-radius: 8px;
          padding: 0 12px 0 36px;
          font-size: 13px;
          color: #2d2d6e;
          outline: none;
          box-sizing: border-box;
          transition: border-color 0.15s ease;
        }

        .search-input:focus {
          border-color: #6c63ff;
        }

        .search-input::placeholder {
          color: #b0b0c8;
        }

        .add-action-btn {
          height: 38px;
          padding: 0 16px;
          background: #6c63ff;
          border: none;
          border-radius: 8px;
          color: #ffffff;
          font-size: 13px;
          font-weight: 500;
          cursor: pointer;
          display: flex;
          align-items: center;
          gap: 6px;
          transition: background-color 0.15s ease;
        }

        .add-action-btn:hover {
          background-color: #5850ec;
        }

        .table-container {
          width: 100%;
          background: transparent;
          overflow-x: auto;
        }

        .minimal-table {
          width: 100%;
          border-collapse: collapse;
          text-align: left;
        }

        .minimal-table th {
          font-size: 13px;
          font-weight: 600;
          color: #8888a0;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          padding: 16px 24px;
          border-bottom: 1.5px solid #ebebf5;
        }

        .minimal-table td {
          font-size: 14px;
          color: #2d2d6e;
          padding: 20px 24px;
          border-bottom: 1px solid #ebebf5;
          background: transparent;
        }

        .minimal-table tr:hover td {
          background: rgba(235, 235, 245, 0.3);
        }

        .row-action-btn {
          background: transparent;
          border: none;
          padding: 4px;
          cursor: pointer;
          color: #b0b0c8;
          transition: color 0.15s ease;
          display: inline-flex;
          align-items: center;
          justify-content: center;
        }

        .row-action-btn:hover.edit {
          color: #6c63ff;
        }

        .row-action-btn:hover.delete {
          color: #e55555;
        }

        .row-action-btn svg {
          width: 18px;
          height: 18px;
        }

        .empty-state {
          text-align: center;
          padding: 48px 0;
          color: #8888a0;
          font-size: 14px;
        }

        .pagination-container {
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 8px;
          margin-top: 32px;
        }

        .page-nav-btn {
          height: 36px;
          padding: 0 14px;
          background: #ffffff;
          border: 1px solid #ebebf5;
          border-radius: 6px;
          color: #4b4b75;
          font-size: 13px;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.15s ease;
        }

        .page-nav-btn:hover:not(:disabled) {
          border-color: #6c63ff;
          color: #6c63ff;
        }

        .page-nav-btn:disabled {
          opacity: 0.4;
          cursor: not-allowed;
        }

        .page-btn {
          width: 36px;
          height: 36px;
          background: #ffffff;
          border: 1px solid #ebebf5;
          border-radius: 6px;
          color: #4b4b75;
          font-size: 13px;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.15s ease;
        }

        .page-btn:hover {
          border-color: #6c63ff;
          color: #6c63ff;
        }

        .page-btn.active {
          background: #6c63ff;
          border-color: #6c63ff;
          color: #ffffff;
        }

        .page-dots {
          color: #8888a0;
          font-size: 14px;
          padding: 0 4px;
        }

        .modal-blur-overlay {
          position: fixed;
          inset: 0;
          z-index: 40;
          background: rgba(244, 245, 250, 0.7);
          backdrop-filter: blur(4px);
          overflow-y: auto;
          padding: 40px 24px;
          box-sizing: border-box;
        }

        .modal-layout-container {
          display: flex;
          justify-content: center;
          width: 100%;
        }

        .modal-inner-wrapper {
          position: relative;
          width: 100%;
          max-width: 920px;
        }

        .modal-close-trigger {
          position: absolute;
          top: 24px;
          right: 24px;
          z-index: 100;
          width: 36px;
          height: 36px;
          border-radius: 50%;
          background: #ffffff;
          border: 1px solid #ebebf5;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #2d2d6e;
          cursor: pointer;
          box-shadow: 0 2px 8px rgba(0,0,0,0.05);
          transition: all 0.15s ease;
        }

        .modal-close-trigger:hover {
          border-color: #6c63ff;
          color: #6c63ff;
        }
      `}</style>

      <div className="list-wrapper">
        <div className="list-header">
          <div>
            <h1 className="title-text">{title}</h1>
            <p className="subtitle-text">{subtitle}</p>
          </div>
        </div>

        <div className="meta-row">
          <div className="count-badge">
            {filteredData.length} active {title ? title.toLowerCase() : "items"}
          </div>

          <div className="action-controls">
            <div className="search-container">
              <MagnifyingGlassIcon className="search-icon" />
              <input
                type="text"
                value={search}
                onChange={(e) => {
                  setSearch(e.target.value);
                  setPage(0);
                }}
                placeholder="Search..."
                className="search-input"
              />
            </div>

            <button
              type="button"
              onClick={() => setShowAddModal(true)}
              className="add-action-btn"
            >
              <PlusIcon className="w-4 h-4" />
              Add
            </button>
          </div>
        </div>

        <div className="table-container">
          <table className="minimal-table">
            <thead>
              <tr>
                {columns?.map((col) => (
                  <th key={col.key || col.label} style={{ width: col.width || "auto" }}>
                    {col.label}
                  </th>
                ))}
                <th style={{ width: "120px" }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {paginatedData.length === 0 ? (
                <tr>
                  <td colSpan={(columns?.length || 0) + 1} className="empty-state">
                    No Data Found
                  </td>
                </tr>
              ) : (
                paginatedData.map((item) => (
                  <tr key={item.identifier || item.username || `row-${item.id}`}>
                    {columns?.map((col) => (
                      <td key={`${item.identifier || item.username || item.id}-${col.key}`}>
                        {col.render ? col.render(item) : item[col.key]}
                      </td>
                    ))}
                    <td>
                      <div style={{ display: "flex", gap: "12px" }}>
                        <button
                          type="button"
                          onClick={() => {
                            setSelectedIdentifier(item.identifier || item.username);
                            setShowEditModal(true);
                          }}
                          className="row-action-btn edit"
                        >
                          <PencilSquareIcon />
                        </button>
                        <button
                          type="button"
                          onClick={() => handleDelete(item)}
                          className="row-action-btn delete"
                        >
                          <TrashIcon />
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
          <div className="pagination-container">
            <button
              type="button"
              onClick={() => setPage(page - 1)}
              disabled={page === 0}
              className="page-nav-btn"
            >
              Prev
            </button>
            {renderPagination()}
            <button
              type="button"
              onClick={() => setPage(page + 1)}
              disabled={page === totalPages - 1}
              className="page-nav-btn"
            >
              Next
            </button>
          </div>
        )}
      </div>

      {(showAddModal || showEditModal) && (
        <div className="modal-blur-overlay">
          <div className="modal-layout-container">
            <div className="modal-inner-wrapper">
              <button
                type="button"
                onClick={() => {
                  setShowAddModal(false);
                  setShowEditModal(false);
                }}
                className="modal-close-trigger"
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
    </>
  );
}

CommonList.propTypes = {
  title: PropTypes.string,
  subtitle: PropTypes.string,
  apiUrl: PropTypes.string.isRequired,
  deleteUrl: PropTypes.string.isRequired,
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