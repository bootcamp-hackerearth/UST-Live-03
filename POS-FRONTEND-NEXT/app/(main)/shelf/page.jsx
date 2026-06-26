"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import { listItems, deleteItem, updateItem, addItem } from "@/services/api";

const ShelfList = () => {
  const [shelves, setShelves] = useState([]);

  const [search, setSearch] = useState("");

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editShelf, setEditShelf] = useState(null);

  const sizePerPage = 5;

  const fetchShelves = async () => {
    try {
      setLoading(true);

      const res = await listItems("shelf", {
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

      setShelves(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
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
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmed = globalThis.confirm(`Delete shelf ${identifier}?`);

    if (!confirmed) {
      return;
    }

    try {
      await deleteItem("shelf", identifier, "identifier");

      fetchShelves();
    } catch (err) {
      console.error(err);

      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editShelf,
      };

      if (editShelf.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("shelf", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("shelf", payload);
      }

      await fetchShelves();

      setEditShelf(null);
    } catch (err) {
      console.error(err);

      globalThis.alert(
        editShelf?.isNew ? "Failed to add shelf" : "Failed to update shelf",
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
        setEditShelf({
          ...row,
          isNew: false,
          formTitle: "Edit Shelf",
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
      disabled: !editShelf?.isNew,
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
            setEditShelf({
              id: "",
              identifier: "",
              status: true,
              isNew: true,
              formTitle: "Add Shelf",
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
          + Add Shelf
        </button>
      </div>

      <CommonList
        title="Shelf"
        data={shelves}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        sizePerPage={sizePerPage}
        columns={columns}
        actions={actions}
        editItem={editShelf}
        setEditItem={setEditShelf}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editShelf?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No shelves found"
      />
    </>
  );
};

export default ShelfList;
