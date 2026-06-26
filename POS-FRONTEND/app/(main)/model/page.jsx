"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList/CommonList";
import AccessGuard from "@/app/components/AccessGuard";

import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
  toggleItem,
} from "@/services/api";

const ModelProductPage = () => {
  const [models, setModels] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [newModel, setNewModel] = useState({
    identifier: "",
    description: "",
  });

  const [editModel, setEditModel] = useState(null);
  const [viewModel, setViewModel] = useState(null);

  const fetchModels = async () => {
    try {
      if (models.length === 0) {
        setLoading(true);
      }

      setError("");

      const res = await listItems("model", {
        page,
        sizePerPage,
        sortField: "id",
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

  useEffect(() => {
    fetchModels();
  }, [page, searchTerm]);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddModel = async () => {
    const response = await addItem(
      "model",
      newModel
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    setNewModel({
      identifier: "",
      description: "",
    });

    fetchModels();

    return true;
  };

  const handleUpdate = async () => {
    const response = await updateItem(
      "model",
      editModel
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    fetchModels();

    return true;
  };

  const handleDelete = async (
    identifier
  ) => {
    if (
      !globalThis.confirm(`Delete ${identifier}?`)
    ) {
      return;
    }

    try {
      await deleteItem(
        "model",
        identifier
      );

      fetchModels();

    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleStatusChange = async (model) => {
    try {
      await toggleItem(
        "model",
        model.identifier
      );

      fetchModels();
    } catch (err) {
      console.error(
        "Status update failed",
        err
      );

      alert("Status update failed");
    }
  };

  const columns = [
    {
      label: "SL NO",
      render: (row, index) =>
        page * sizePerPage +
        index +
        1,
    },

    {
      label: "Model",
      key: "identifier",
    },

    {
      label: "Description",
      key: "description",
    },

    {
      label: "Status",
      render: (row) => (
        <label
          className="switch"
          aria-label={`Toggle ${row.identifier} status`}
        >
          <input
            type="checkbox"
            checked={row.status}
            onChange={() =>
              handleStatusChange(row)
            }
          />
          <span className="slider"></span>
        </label>
      ),
    },
  ];


  const actions = [
    {
      label: "👁 View",
      type: "view",
      onClick: (row) =>
        setViewModel(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditModel(row),
    },

    {
      label: "🗑 Delete",
      type: "delete",

      onClick: (row) =>
        handleDelete(
          row.identifier
        ),
    },
  ];


  return (
    <AccessGuard requiredPath="/model">
      <CommonList
        title="Models"
        data={models}
        columns={columns}
        loading={loading}
        error={error}

        page={page}
        setPage={setPage}
        totalPages={totalPages}

        actions={actions}
        emptyMessage="No models found"

        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}

        onAdd={() => { }}
        addButtonText="+ Add Model"

        newItem={newModel}
        setNewItem={setNewModel}
        handleAdd={handleAddModel}

        addFields={[
          {
            name: "identifier",
            label: "Model",
          },
          {
            name: "description",
            label: "Description",
          },
        ]}

        editItem={editModel}
        setEditItem={setEditModel}
        handleUpdate={handleUpdate}

        viewItem={viewModel}
        setViewItem={setViewModel}

        viewFields={[
          {
            name: "identifier",
            label: "Category Name",
          },
          {
            name: "superCategory",
            label: "Super Category",
          },
        ]}

        editFields={[
          {
            name: "identifier",
            label: "Model",
            disabled: true,
          },
          {
            name: "description",
            label: "Description",
          },
        ]}
      />
    </AccessGuard>
  );
};

export default ModelProductPage;