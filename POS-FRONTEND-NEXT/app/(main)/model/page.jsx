"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import { listItems, deleteItem, updateItem, addItem } from "@/services/api";

const ModelList = () => {
  const [models, setModels] = useState([]);

  const [search, setSearch] = useState("");

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editModel, setEditModel] = useState(null);

  const sizePerPage = 5;

  const fetchModels = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("model", {
        page,
        sizePerPage,
        sortField: "identifier",
        search,
      });

      let data = [];

      if (Array.isArray(res)) {
        data = res;
      } else if (Array.isArray(res?.content)) {
        data = res.content;
      }

      setModels(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
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
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmed = globalThis.confirm(`Delete model ${identifier}?`);

    if (!confirmed) {
      return;
    }

    try {
      await deleteItem("model", identifier, "identifier");

      fetchModels();
    } catch (err) {
      console.error(err);

      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editModel,
      };

      if (editModel.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("model", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("model", payload);
      }

      await fetchModels();

      setEditModel(null);
    } catch (err) {
      console.error(err);

      alert(
        editModel?.isNew ? "Failed to add model" : "Failed to update model",
      );
    }
  };

  const columns = [
    {
      label: "ID",
      key: "id",
    },
    {
      label: "Identifier",
      key: "identifier",
    },
    {
      label: "Status",
      render: (row) => (
        <label className="switch" aria-label="Status">
          <input type="checkbox" checked={row.status} readOnly />
          <span className="slider round"></span>
        </label>
      ),
    },
  ];

  const actions = [
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditModel({
          ...row,
          isNew: false,
          formTitle: "Edit Model",
        }),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  const editFields = [
    {
      name: "id",
      label: "ID",
      disabled: true,
    },
    {
      name: "identifier",
      label: "Identifier",
      disabled: !editModel?.isNew,
    },
    {
      name: "status",
      label: "Status",
      type: "checkbox",
    },
  ];

  return (
    <>
      <div
        style={{
          display: "flex",
          justifyContent: "flex-end",
          marginBottom: "16px",
        }}
      >
        <button
          onClick={() =>
            setEditModel({
              id: "",
              identifier: "",
              status: true,
              isNew: true,
              formTitle: "Add Model",
            })
          }
          style={{
            background: "#1976d2",
            color: "#fff",
            border: "none",
            borderRadius: "6px",
            padding: "10px 18px",
            cursor: "pointer",
            fontWeight: "600",
          }}
        >
          + Add Model
        </button>
      </div>

      <CommonList
        title="Models"
        data={models}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        columns={columns}
        actions={actions}
        editItem={editModel}
        setEditItem={setEditModel}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editModel?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No models found"
      />
    </>
  );
};

export default ModelList;
