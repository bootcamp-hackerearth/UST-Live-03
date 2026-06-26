"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
} from "@/services/api";

const UnitPage = () => {
  const [units, setUnits] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewUnit, setViewUnit] = useState(null);

  const sizePerPage = 5;

  const [newUnit, setNewUnit] = useState({
    identifier: "",
    description: "",
  });

  const [editUnit, setEditUnit] = useState(null);

  const fetchUnits = async () => {
    try {
      setLoading(true);

      const res = await listItems("unit", {
        page,
        sizePerPage,
        sortField: "identifier",
        search: searchTerm,
      });

      const data = res?.content || [];

      setUnits(data);
      setTotalPages(res?.totalPages || 0);
    } catch (err) {
      console.error("Failed to load units:", err);
      setError("Failed to load units");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUnits();
  }, [page, searchTerm]);

  const handleAdd = async () => {
    if (!newUnit.identifier?.trim()) {
      alert("Identifier is required");
      return;
    }

    const exists = units.some(
      (unit) =>
        unit.identifier?.trim().toLowerCase() ===
        newUnit.identifier?.trim().toLowerCase()
    );

    if (exists) {
      alert(`${newUnit.identifier} already exists`);
      return;
    }

    try {
      await addItem("unit", newUnit);

      setNewUnit({
        identifier: "",
        description: "",
      });

      fetchUnits();
    } catch (err) {
      console.error(err);
      alert("Add failed");
    }
  };

  const handleUpdate = async () => {
    await updateItem("unit", editUnit);

    setEditUnit(null);

    fetchUnits();
  };

  const handleDelete = async (identifier) => {
    await deleteItem("unit", identifier);

    fetchUnits();
  };


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
      label: "Description",
      key: "description",
    },
  ];

  const actions = [
    {
      label: "👁 View",
      onClick: (row) => setViewUnit(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) => setEditUnit(row),
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
      label: "Identifier",
    },
    {
      name: "description",
      label: "Description",
    },
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled:true
    },
    {
      name: "description",
      label: "Description",
    },
  ];

  return (
    <CommonList
      title="Unit"
      data={units}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      newItem={newUnit}
      setNewItem={setNewUnit}
      onAdd={handleAdd}
      handleAdd={handleAdd}
      addFields={addFields}
      editItem={editUnit}
      setEditItem={setEditUnit}
      handleUpdate={handleUpdate}
      editFields={editFields}
      viewItem={viewUnit}
      setViewItem={setViewUnit}
      actions={actions}
      emptyMessage="No units found"
    />
  );
};

export default UnitPage;