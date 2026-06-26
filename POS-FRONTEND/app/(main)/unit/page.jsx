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

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [addError, setAddError] = useState("");

  const [viewItem, setViewItem] = useState(null);

  // ADD
  const [newUnit, setNewUnit] = useState({
    identifier: "",
    description: "",
  });

  // EDIT
  const [editUnit, setEditUnit] = useState(null);

  // FETCH
  const fetchUnits = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("unit", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = res?.content || [];

      const normalized = data.map((u) => ({
        ...u,
        status:
          u.status === true ||
          u.status === 1 ||
          u.status === "1",
      }));

      setUnits(normalized);

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
      setError("Failed to load units");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUnits();
  }, [page, searchTerm]);

  // ADD
  const handleAddUnit = async () => {
    try {
      setAddError("");

      const response = await addItem(
        "unit",
        newUnit
      );

      if (response?.success === false) {
        setAddError(
          response.message || "Unit already exists"
        );
        return false;
      }

      setNewUnit({
        identifier: "",
        description: "",
      });

      await fetchUnits();
      return true;
    } catch (err) {
      console.error(err);
      setAddError("Add failed");
      return false;
    }
  };

  // UPDATE
  const handleUpdate = async () => {
    try {
      await updateItem("unit", editUnit);
      await fetchUnits();
      setEditUnit(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // DELETE
  const handleDelete = async (identifier) => {
    const ok = globalThis.confirm(
      `Delete ${identifier}?`
    );

    if (!ok) return;

    try {
      await deleteItem("unit", identifier);

      setUnits((prev) =>
        prev.filter(
          (u) => u.identifier !== identifier
        )
      );
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // TABLE COLUMNS
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

  // ACTIONS
  const actions = [
    {
    label: "View 👁️",
    onClick: (row) => setViewItem(row),
    },
    {
      label: "Edit ✏️",
      onClick: (row) =>
        setEditUnit(row),
    },
    {
      label: "Delete 🗑",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  // ADD FIELDS
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      required: true,
    },
    {
      name: "description",
      label: "Description",
       required: true,
    },
  ];

  // EDIT FIELDS
  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled: true,
    },
    {
      name: "description",
      label: "Description",
    },
  ];

  return (
    <CommonList
      title="Units"
      data={units}
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
      addButtonText="+ Add Unit"
      newItem={newUnit}
      setNewItem={setNewUnit}
      handleAdd={handleAddUnit}
      addFields={addFields}
      editItem={editUnit}
      setEditItem={setEditUnit}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      viewItem={viewItem}
      setViewItem={setViewItem}
      emptyMessage="No units found"
    />
  );
};

export default UnitPage;