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

const ModelPage = () => {
  const [models, setModels] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [addError, setAddError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [newModel, setNewModel] = useState({
    identifier: "",
    description: "",
  });

  const [editModel, setEditModel] = useState(null);

  const [viewItem, setViewItem] = useState(null);

  // ================= FETCH =================
  const fetchModels = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("modelProduct", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = res?.content || [];

      const normalized = data.map((m) => ({
        ...m,
        status:
          m.status === true ||
          m.status === 1 ||
          m.status === "1",
      }));

      setModels(normalized);

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
      setError("Failed to load models");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchModels();
  }, [page, searchTerm]);

  // ================= ADD =================
  const handleAddModel = async () => {
    try {
      setAddError("");

      const response = await addItem(
        "modelProduct",
        newModel
      );

      if (response?.success === false) {
        setAddError(
          response.message || "Model already exists"
        );
        return false;
      }

      setNewModel({
        identifier: "",
        description: "",
      });

      await fetchModels();
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
      await updateItem(
        "modelProduct",
        editModel
      );
      await fetchModels();
      setEditModel(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
  const handleDelete = async (identifier) => {
    const ok = globalThis.confirm(
      `Delete ${identifier}?`
    );

    if (!ok) return;

    try {
      await deleteItem(
        "modelProduct",
        identifier
      );

      setModels((prev) =>
        prev.filter(
          (m) => m.identifier !== identifier
        )
      );
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= TOGGLE =================
  const handleToggleStatus = async (
    identifier
  ) => {
    setModels((prev) =>
      prev.map((m) =>
        m.identifier === identifier
          ? {
            ...m,
            status: !m.status,
          }
          : m
      )
    );

    try {
      await toggleItem(
        "modelProduct",
        identifier
      );
    } catch (err) {
      console.error(err);
      alert("Toggle failed");
      fetchModels();
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
      label: "Description",
      key: "description",
    },
    {
      label: "Status",
      render: (m) => (
        <label className="switch">
          <input
            type="checkbox"
            aria-label="Status"
            checked={m.status}
            onChange={() => handleToggleStatus(m.identifier)}
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
      onClick: (row) =>
        setEditModel(row),
    },
    {
      label: "Delete 🗑",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  // ================= ADD FIELDS =================
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

  // ================= EDIT FIELDS =================
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
      title="Models"
      data={models}
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
      addButtonText="+ Add Model"
      newItem={newModel}
      setNewItem={setNewModel}
      handleAdd={handleAddModel}
      addFields={addFields}
      editItem={editModel}
      setEditItem={setEditModel}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      viewItem={viewItem}
      setViewItem={setViewItem}
      emptyMessage="No models found"
    />
  );
};

export default ModelPage;