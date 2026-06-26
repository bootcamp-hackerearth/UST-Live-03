"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";
import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
  toggleItem,
} from "@/services/api";

const ShelfList = () => {
  const [shelves, setShelves] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [addError, setAddError] = useState("");

  const [viewItem, setViewItem] = useState(null);

  // ================= ADD =================
  const [newShelf, setNewShelf] = useState({
    identifier: "",
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

      const data = res?.content || [];

      const normalized = data.map((s) => ({
        ...s,
        status:
          s.status === true ||
          s.status === 1 ||
          s.status === "1",
      }));

      setShelves(normalized);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalElements || data.length) /
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
      setAddError("");

      const response = await addItem("shelf", newShelf);

      if (response?.success === false) {
        setAddError(response.message || "Shelf already exists");
        return false;
      }

      setNewShelf({
        identifier: "",
      });

      await fetchShelves();
      return true;
    } catch (err) {
      console.error(err);
      setAddError("Add failed");
      return false;
    }
  };

  // ================= UPDATE =================
  const handleUpdate = async () => {
    try {
      await updateItem("shelf", editShelf);
      await fetchShelves();
      setEditShelf(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
  const handleDelete = async (identifier) => {
    const ok = globalThis.confirm(`Delete ${identifier}?`);
    if (!ok) return;

    try {
      await deleteItem("shelf", identifier);

      setShelves((prev) =>
        prev.filter((s) => s.identifier !== identifier)
      );
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= TOGGLE =================
  const handleToggleStatus = async (identifier) => {
    setShelves((prev) =>
      prev.map((s) =>
        s.identifier === identifier
          ? { ...s, status: !s.status }
          : s
      )
    );

    try {
      await toggleItem("shelf", identifier);
    } catch (err) {
      console.error(err);
      alert("Toggle failed");
      fetchShelves();
    }
  };

  // ================= COLUMNS =================
  const columns = [
    {
      label: "Sl No",
      render: (row, index) =>
        page * sizePerPage + index + 1,
    },
    {
      label: "Identifier",
      key: "identifier",
    },
    {
      label: "Status",
      render: (s) => (
          <label
            className="switch"
            aria-label={`Toggle status for ${s.identifier}`}
          >
            <input
              type="checkbox"
              checked={s.status}
              onChange={() => handleToggleStatus(s.identifier)}
              aria-checked={s.status}
            />
            <span className="slider"></span>
          </label>
        ),
    },
  ];

  // ================= ACTIONS =================
  const actions = [
     {
    label: "View 👁️",
    onClick: (row) => setViewItem(row),
    },
    {
      label: "Edit ✏️",
      onClick: (row) => setEditShelf(row),
    },
    {
      label: "Delete 🗑",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      required: true,
    },
  ];

  // ================= EDIT FIELDS =================
  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled: true,
    },
  ];

  return (
    <CommonList
      title="Shelves"
      data={shelves}
      columns={columns}
      loading={loading}
      error={error}
      addError={addError}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      onAdd={() => setAddError("")}
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
      viewItem={viewItem}
      setViewItem={setViewItem}
      emptyMessage="No shelves found"
    />
  );
};

export default ShelfList;