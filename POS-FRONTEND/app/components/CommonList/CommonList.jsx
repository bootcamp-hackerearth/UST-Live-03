"use client";

import { useState, useEffect, useRef } from "react";
import "./CommonList.css";
import PropTypes from "prop-types";

import Table from "./Table";
import Pagination from "./Pagination";
import FormModal from "./FormModal";

const CommonList = ({
  title,
  data = [],
  columns = [],
  loading = false,
  error = "",
  page = 0,
  setPage,
  totalPages = 1,
  onAdd,
  addButtonText = "+ Add",
  addFields = [],
  newItem = {},
  setNewItem,
  handleAdd,
  editItem,
  setEditItem,
  handleUpdate,
  editFields = [],
  viewItem,
  setViewItem,
  actions = [],
  emptyMessage = "No data found",
  searchTerm = "",
  setSearchTerm,
}) => {

  const safeData = Array.isArray(data) ? data : [];

  useEffect(() => {
    if (
      safeData.length === 0 &&
      page > 0
    ) {
      setPage(page - 1);
    }
  }, [safeData, page, setPage]);

  const [showAddModal, setShowAddModal] = useState(false);
  const [localSearch, setLocalSearch] = useState(searchTerm || "");

  const searchInputRef = useRef(null);

  useEffect(() => {
    searchInputRef.current?.focus();
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      setSearchTerm?.(localSearch);
    }, 300);

    return () => clearTimeout(timer);
  }, [localSearch]);

  if (loading && safeData.length === 0) {
    return <div>Loading...</div>;
  }

  if (error) {
    return (
      <div style={{ color: "red" }}>
        {error}
      </div>
    );
  }


  return (
    <div className="section">

      <div className="tableHeader">

        <h2 className="sectionTitle">
          {title}
        </h2>

        <div className="headerActions">

          <input
            ref={searchInputRef}
            className="searchInput"
            placeholder="Search..."
            value={localSearch}
            onChange={(e) =>
              setLocalSearch(e.target.value)
            }
          />

          {onAdd && (
            <button
              className="actionBtn"
              onClick={() => {
                setNewItem?.({});
                setShowAddModal(true);
                onAdd();
              }}
            >
              {addButtonText}
            </button>
          )}

        </div>

      </div>


      <div className="tableWrapper">

        <Table
          data={safeData}
          columns={columns}
          actions={actions}
          emptyMessage={emptyMessage}
          searchTerm={searchTerm}
        />

      </div>


      {setPage && (
        <Pagination
          page={page}
          setPage={setPage}
          totalPages={totalPages}
        />
      )}

      {viewItem && (
        <FormModal
          mode="view"
          title={title}
          fields={editFields}
          item={viewItem}
          closeModal={() => setViewItem(null)}
        />
      )}

      {showAddModal && (
        <FormModal
          mode="add"
          title={title}
          fields={addFields}
          item={newItem}
          setItem={setNewItem}
          handleSubmit={handleAdd}
          closeModal={() => setShowAddModal(false)}
        />
      )}


      {editItem && (
        <FormModal
          mode="edit"
          title={title}
          fields={editFields}
          item={editItem}
          setItem={setEditItem}
          handleSubmit={handleUpdate}
          closeModal={() => setEditItem(null)}
        />
      )}

    </div>
  );
};

export default CommonList;

CommonList.propTypes = {
  title: PropTypes.string.isRequired,

  data: PropTypes.array,
  columns: PropTypes.array,
  loading: PropTypes.bool,
  error: PropTypes.string,

  page: PropTypes.number,
  setPage: PropTypes.func,
  totalPages: PropTypes.number,

  onAdd: PropTypes.func,
  addButtonText: PropTypes.string,

  addFields: PropTypes.array,
  newItem: PropTypes.object,
  setNewItem: PropTypes.func,
  handleAdd: PropTypes.func,

  editItem: PropTypes.object,
  setEditItem: PropTypes.func,
  handleUpdate: PropTypes.func,
  editFields: PropTypes.array,

  viewItem: PropTypes.object,
  setViewItem: PropTypes.func,

  actions: PropTypes.array,

  emptyMessage: PropTypes.string,

  searchTerm: PropTypes.string,
  setSearchTerm: PropTypes.func,
};