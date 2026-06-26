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


const ShelfPage = () => {
  const [shelves, setShelves] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [newShelf, setNewShelf] = useState({
    identifier: "",
    status: true,
  });

  const [editShelf, setEditShelf] = useState(null);
  const [viewShelf, setViewShelf] = useState(null);

  const fetchShelves = async () => {
    try {
      if (shelves.length === 0) {
        setLoading(true);
      }

      setError("");

      const res = await listItems(
        "shelf",
        {
          page,
          sizePerPage,
          sortField: "id",
          search: searchTerm,
        }
      );

      setShelves(res?.content || []);
      setTotalPages(res?.totalPages || 1);

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

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddShelf = async () => {
    const response = await addItem(
      "shelf",
      newShelf
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    setNewShelf({
      identifier: "",
      status: true,
    });

    fetchShelves();

    return true;
  };

  const handleUpdate = async () => {
    const response = await updateItem(
      "shelf",
      editShelf
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    fetchShelves();

    return true;
  };

  const handleDelete = async (
    identifier
  ) => {
    if (
      !globalThis.confirm(
        `Delete ${identifier}?`
      )
    ) {
      return;
    }

    try {
      await deleteItem(
        "shelf",
        identifier
      );

      fetchShelves();

    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleStatusChange = async (
    shelf
  ) => {
    try {
      await toggleItem(
        "shelf",
        shelf.identifier
      );

      fetchShelves();

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
      render: (_, index) =>
        page * sizePerPage +
        index +
        1,
    },

    {
      label: "Shelf ID",
      key: "identifier",
    },

    {
      label: "Status",
      render: (row) => (
        <label
          className="switch"
          aria-label={`Toggle status for shelf ${row.identifier}`}
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
        setViewShelf(row),
    },

    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditShelf(row),
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
    <AccessGuard requiredPath="/shelf">
      <CommonList
        title="Shelves"
        data={shelves}
        columns={columns}
        loading={loading}
        error={error}

        page={page}
        setPage={setPage}
        totalPages={totalPages}

        actions={actions}
        emptyMessage="No shelves found"

        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}

        onAdd={() => { }}
        addButtonText="+ Add Shelf"

        newItem={newShelf}
        setNewItem={setNewShelf}
        handleAdd={handleAddShelf}

        addFields={[
          {
            name: "identifier",
            label: "Shelf ID",
          },
          {
            name: "status",
            label: "Status",
            type: "select",
            options: [
              {
                label: "Active",
                value: true,
              },
              {
                label: "Inactive",
                value: false,
              },
            ],
          },
        ]}

        editItem={editShelf}
        setEditItem={setEditShelf}
        handleUpdate={handleUpdate}
        viewItem={viewShelf}
        setViewItem={setViewShelf}

        editFields={[
          {
            name: "identifier",
            label: "Shelf ID",
            disabled: true,
          },
          {
            name: "status",
            label: "Status",
            type: "select",
            options: [
              {
                label: "Active",
                value: true,
              },
              {
                label: "Inactive",
                value: false,
              },
            ],
          },
        ]}
      />
    </AccessGuard>
  );
};

export default ShelfPage;