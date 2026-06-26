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

const RackPage = () => {
  const [racks, setRacks] = useState([]);
  const [shelfs, setShelfs] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewRack, setViewRack] = useState(null);

  const sizePerPage = 5;

  const [newRack, setNewRack] = useState({
    identifier: "",
    shelfs: [],
    status: true,
  });

  const [editRack, setEditRack] = useState(null);

  useEffect(() => {
    fetchRacks();
    fetchShelfs();
  }, [page, searchTerm]);

  const fetchRacks = async () => {
    try {
      setLoading(true);

      const res = await listItems("racks", {
        page,
        sizePerPage,
        sortField: "identifier",
        search: searchTerm,
      });

      const data = res?.content || [];

      setRacks(data);
      setTotalPages(res?.totalPages || 1);
    } catch (err) {
      console.error("Failed to load racks:", err);
      setError("Failed to load racks");
    } finally {
      setLoading(false);
    }
  };

  const fetchShelfs = async () => {
    try {
      const res = await listItems("shelf", {
        page: 0,
        sizePerPage: 100,
        sortField: "identifier",
      });

      const activeShelfs = (res?.content || []).filter(
        (s) =>
          s.status === true ||
          s.status === 1 ||
          s.status === "1"
      );

      setShelfs(activeShelfs);
    } catch (err) {
      console.log(err);
    }
  };

  const handleAdd = async () => {
    if (!newRack.identifier?.trim()) {
      alert("Rack Name is required");
      return;
    }

    const exists = racks.some(
      (rack) =>
        rack.identifier?.trim().toLowerCase() ===
        newRack.identifier?.trim().toLowerCase()
    );

    if (exists) {
      alert(`${newRack.identifier} already exists`);
      return;
    }

    try {
      await addItem("racks", newRack);

      setNewRack({
        identifier: "",
        shelfs: [],
        status: true,
      });

      fetchRacks();
    } catch (err) {
      console.error(err);
      alert("Add failed");
    }
  };

  const handleUpdate = async () => {
    await updateItem("racks", editRack);

    setEditRack(null);

    fetchRacks();
  };

  const handleDelete = async (identifier) => {
    await deleteItem("racks", identifier);

    setRacks((prev) =>
      prev.filter((r) => r.identifier !== identifier)
    );
  };

  const handleToggleStatus = async (row) => {
    const identifier = row.identifier;

    setRacks((prev) =>
      prev.map((r) =>
        r.identifier === identifier
          ? {
            ...r,
            status: !r.status,
          }
          : r
      )
    );

    try {
      await toggleItem("racks", identifier);
    } catch {
      fetchRacks();
    }
  };

  const columns = [
    {
      label: "Sl No",
      render: (row, index) =>
        page * sizePerPage + index + 1,
    },

    {
      label: "Rack Name",
      key: "identifier",
    },

    {
      label: "Shelf Name",
      render: (row) =>
        Array.isArray(row.shelfs)
          ? row.shelfs.join(", ")
          : row.shelfs || "-",
    },

    {
      label: "Status",
      key: "status",
    },
  ];

  const actions = [
    {
      label: "👁 View",
      onClick: (row) => setViewRack(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) => setEditRack(row),
    },
    {
      label: "🗑 Delete",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  const addFields = [
    {
      name: "identifier",
      label: "Rack Name",
    },

    {
      name: "shelfs",
      label: "Shelf Name",
      type: "select",
      multiple: true,
      options: shelfs.map((s) => ({
        label: s.identifier,
        value: s.identifier,
      })),
    },
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Rack Name",
      disabled:true,
    },

    {
      name: "shelfs",
      label: "Shelf Name",
      type: "select",
      multiple: true,
      options: shelfs.map((s) => ({
        label: s.identifier,
        value: s.identifier,
      })),
    },
  ];

  return (
    <CommonList
      title="Racks"
      data={racks}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      newItem={newRack}
      setNewItem={setNewRack}
      onAdd={handleAdd}
      handleAdd={handleAdd}
      addFields={addFields}
      editItem={editRack}
      setEditItem={setEditRack}
      handleUpdate={handleUpdate}
      editFields={editFields}
      viewItem={viewRack}
      setViewItem={setViewRack}
      actions={actions}
      emptyMessage="No racks found"
      onToggleStatus={handleToggleStatus}
    />
  );
};

export default RackPage;