"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";
import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
  toggleItem,
} from "@/services/api";

const ShelfList = () => {
  const [shelves, setShelves] = useState([]);
  const [message, setMessage] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [viewShelf, setviewShelf] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const sizePerPage = 5;

  // ================= ADD =================
  const [newShelf, setNewShelf] = useState({
    identifier: "",
    status: true,
  });

  // ================= EDIT =================
  const [editShelf, setEditShelf] = useState(null);

  // ================= FETCH =================
  const fetchShelves = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("shelf", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = Array.isArray(res)
        ? res
        : res?.content || [];

      const normalized = data.map((shelf) => ({
        ...shelf,
        status:
          shelf.status === true ||
          shelf.status === 1 ||
          shelf.status === "1",
      }));

      setShelves(normalized);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalElements ||
              normalized.length) /
              sizePerPage
          ) ||
          1
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load shelves");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchShelves();
  }, [page, searchTerm]);

  // ================= ADD =================
  const handleAddShelf = async () => {
    try {
      const res = await addItem(
        "shelf",
        newShelf
      );

      if (res?.success === false) {
        setMessage(res.message);
        return false;
      }

      setMessage(
        "Shelf added successfully"
      );

      setNewShelf({
        identifier: "",
        status: true,
      });

      await fetchShelves();

      return true;
    } catch (err) {
      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Add failed"
      );

      return false;
    }
  };

  // ================= UPDATE =================
  const handleUpdate = async () => {
    try {
      await updateItem("shelf", editShelf);
 
      fetchShelves();
      setEditShelf(null);
      setMessage("");
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
  const handleDelete = async (
    identifier
  ) => {
    const confirmDelete =
      globalThis.confirm(
        `Delete ${identifier}?`
      );

    if (!confirmDelete) return;

    try {
      await deleteItem(
        "shelf",
        identifier
      );

      setShelves((prev) =>
        prev.filter(
          (s) =>
            s.identifier !==
            identifier
        )
      );
    } catch (err) {
      console.error(err);

      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Delete failed"
      );
    }
  };

  // ================= STATUS =================
  const handleToggleStatus = async (
    identifier,
    currentStatus
  ) => {
    const newStatus =
      !currentStatus;

    setShelves((prev) =>
      prev.map((s) =>
        s.identifier ===
        identifier
          ? {
              ...s,
              status: newStatus,
            }
          : s
      )
    );

    try {
      await toggleItem(
        "shelf",
        identifier,
        newStatus
      );
      await fetchShelves();
    } catch (err) {
      console.error(err);

      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Toggle failed"
      );

      fetchShelves();
    }
  };

  // ================= COLUMNS =================
  const columns = [
    {
      label: "ID",
      render: (row, index) =>
        page * sizePerPage +
        index +
        1,
    },

    {
      label: "Identifier",
      key: "identifier",
    },

    {
      label: "Status",
      render: (shelf) => (
        <label
          htmlFor={`status-${shelf.identifier}`}
          className="switch"
          aria-label={`Toggle status for ${shelf.identifier}`}
        >
          <input
            id={`status-${shelf.identifier}`}
            type="checkbox"
            checked={!!shelf.status}
            onChange={() =>
              handleToggleStatus(
                shelf.identifier,
                shelf.status
              )
            }
          />
          <span className="slider"></span>
        </label>
      ),
    },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "👁️",
      onClick: (row) => setviewShelf(row),
    },
    {
      label: "✏️",
      onClick: (row) =>
        setEditShelf({
          ...row,
        }),
    },

    {
      label: "🗑",
      onClick: (row) =>
        handleDelete(
          row.identifier
        ),
    },
  ];

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      required: true,
    },
  ];

  // ================= EDIT FIELDS =================
  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      disabled: true,
    },
  ];

  return (
    <>
    <CommonList
      title="Shelves"
      data={shelves}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      message={message}
      setMessage={setMessage}

      onAdd={() => {}}
      addButtonText="+ Add Shelf"
      newItem={newShelf}
      setNewItem={setNewShelf}
      handleAdd={handleAddShelf}
      addFields={addFields}

      editItem={editShelf}
      setEditItem={setEditShelf}
      handleUpdate={handleUpdate}
      editFields={editFields}

      actions={actions}

      emptyMessage="No shelves found"
    />
    {viewShelf && (
      <div className="modalOverlay">
        <div className="viewModal">
          <div className="modalHeader">
            <h3>Shelf Details</h3>

            <button
              className="closeBtn"
              onClick={() =>
                setviewShelf(null)
              }
            >
              ✕
            </button>
          </div>

          <div className="viewContent">
            <div className="viewRow">
              <span>Name</span>
              <strong>
                {viewShelf.identifier}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created By</span>
              <strong>
                {viewShelf.createdBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created On</span>
              <strong>
                {viewShelf.createdOn ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified By</span>
              <strong>
                {viewShelf.modifiedBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified On</span>
              <strong>
                {viewShelf.modifiedOn ||
                  "-"}
              </strong>
            </div>
          </div>
        </div>
      </div>
    )}
    </>
  );
};

export default ShelfList;