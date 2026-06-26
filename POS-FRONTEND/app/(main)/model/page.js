"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
} from "@/services/api";

const ModelPage = () => {
  const [models, setModels] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewModel, setViewModel] = useState(null);
  const sizePerPage = 5;

  const [newModel, setNewModel] = useState({
    identifier: "",
    description: "",
  });

  const [editModel, setEditModel] = useState(null);

  useEffect(() => {
    fetchModels();
  }, [page, searchTerm]);

  const fetchModels = async () => {
    try {
      setLoading(true);

      const res = await listItems("modelProduct", {
        page,
        sizePerPage,
        sortField: "identifier",
        search: searchTerm,
      });

      setModels(res?.content || []);
      setTotalPages(res?.totalPages || 1);
    } catch (err) {
      console.error(err);
      setError("Failed to load models");
    } finally {
      setLoading(false);
    }
  };

  const handleAdd = async () => {
    if (!newModel.identifier?.trim()) {
      alert("Identifier is required");
      return;
    }

    const exists = models.some(
      (model) =>
        model.identifier?.trim().toLowerCase() ===
        newModel.identifier?.trim().toLowerCase()
    );

    if (exists) {
      alert(`${newModel.identifier} already exists`);
      return;
    }

    try {
      await addItem("modelProduct", newModel);

      setNewModel({
        identifier: "",
        description: "",
      });

      fetchModels();
    } catch (err) {
      console.error(err);
      alert("Add failed");
    }
  };

  const handleUpdate = async () => {
    try {
      await updateItem("modelProduct", editModel);

      setEditModel(null);
      fetchModels();
    } catch (err) {
      console.error(err);
    }
  };

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(
      `Delete ${identifier}?`
    );

    if (!confirmDelete) return;

    try {
      await deleteItem("modelProduct", identifier);

      setModels((prev) =>
        prev.filter(
          (item) => item.identifier !== identifier
        )
      );
    } catch (err) {
      console.error(err);
    }
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
      onClick: (row) => setViewModel(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) => setEditModel(row),
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
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      newItem={newModel}
      setNewItem={setNewModel}
      onAdd={handleAdd}
      handleAdd={handleAdd}
      addFields={addFields}
      editItem={editModel}
      setEditItem={setEditModel}
      handleUpdate={handleUpdate}
      editFields={editFields}
      viewItem={viewModel}
      setViewItem={setViewModel}
      actions={actions}
      emptyMessage="No models found"
    />
  );
};

export default ModelPage;