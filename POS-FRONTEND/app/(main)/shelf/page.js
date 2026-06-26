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

const ShelfPage = () => {
  const [shelves, setShelves] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [searchTerm, setSearchTerm] = useState("");
  const [viewShelf, setViewShelf] = useState(null);

  const sizePerPage = 5;

  const [newShelf, setNewShelf] = useState({
    identifier: "",
  });

  const [editShelf, setEditShelf] = useState(null);

  // ================= FETCH SHELVES =================
  const fetchShelves = async () => {
    try {
      setLoading(true);

      const res = await listItems("shelf", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        search: searchTerm,
      });

      console.log("SHELF RESPONSE:", res);

      const data =
        res?.content ||
        res?.data?.content ||
        [];

      setShelves(data);

      setTotalPages(
        res?.totalPages ||
        res?.data?.totalPages ||
        1
      );

      setError("");
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

  // ================= ADD SHELF =================
  const handleAdd = async () => {
    try {
      if (!newShelf.identifier?.trim()) {
        alert("Identifier is required");
        return;
      }

      await addItem("shelf", newShelf);

      setNewShelf({
        identifier: "",
      });

      fetchShelves();
    } catch (err) {
      console.error(err);
      alert("Failed to add shelf");
    }
  };

  // ================= UPDATE SHELF =================
  const handleUpdate = async () => {
    try {
      await updateItem("shelf", editShelf);

      setEditShelf(null);
      fetchShelves();
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE SHELF =================
  const handleDelete = async (identifier) => {
    try {
      await deleteItem("shelf", identifier);
      fetchShelves();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= TOGGLE STATUS =================
  const handleToggleStatus = async (row) => {
    const identifier = row.identifier;

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
      render: (row, index) => page * sizePerPage + index + 1,
    },
    {
      label: "Identifier",
      key: "identifier",
    },
    {
      label: "Status",
      key: "status",
    },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "👁 View",
      onClick: (row) => setViewShelf(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) => setEditShelf(row),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  // ================= FORM FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
    },
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled:true,
    },
  ];

  return (
    <CommonList
      title="Shelves"
      data={shelves}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      newItem={newShelf}
      setNewItem={setNewShelf}
      onAdd={handleAdd}
      handleAdd={handleAdd}
      addFields={addFields}
      editItem={editShelf}
      setEditItem={setEditShelf}
      handleUpdate={handleUpdate}
      editFields={editFields}
      viewItem={viewShelf}
      setViewItem={setViewShelf}
      actions={actions}
      emptyMessage="No shelves found"
      onToggleStatus={handleToggleStatus}
    />
  );
};

export default ShelfPage;