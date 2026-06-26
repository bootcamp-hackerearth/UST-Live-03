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
  getActiveShelves,
} from "@/services/api";

const RacksPage = () => {
  const [racks, setRacks] = useState([]);
  const [shelves, setShelves] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const sizePerPage = 5;
  const [searchTerm, setSearchTerm] = useState("");

  const [newRack, setNewRack] = useState({
    identifier: "",
    shelfs: [],
  });

  const [editRack, setEditRack] = useState(null);
  const [viewRack, setViewRack] = useState(null);

  const fetchRacks = async () => {
    try {
      if (racks.length === 0) setLoading(true);

      setError("");

      const res = await listItems("racks", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      setRacks(res?.content || []);
      setTotalPages(res?.totalPages || 1);
    } catch (err) {
      console.error(err);

      setError("Failed to load racks");
    } finally {
      setLoading(false);
    }
  };

  const fetchShelves = async () => {
    try {
      const res = await getActiveShelves();
      setShelves(res || []);
    } catch (err) {
      console.error("Failed to load shelves", err);
    }
  };

  useEffect(() => {
    fetchRacks();
    fetchShelves();
  }, [page, searchTerm]);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddRack = async () => {
    const response = await addItem("racks", newRack);

    if (response?.success === false) {
      throw new Error(response.message);
    }

    setNewRack({ identifier: "", shelfs: [] });
    fetchRacks();
    return true;
  };

  const handleUpdate = async () => {
    const response = await updateItem("racks", editRack);

    console.log("EDIT DATA:", editRack);

    if (response?.success === false) {
      throw new Error(response.message);
    }

    console.log("UPDATE RESPONSE:", response);

    fetchRacks();
    return true;
  };

  const handleDelete = async (identifier) => {
    if (!globalThis.confirm(`Delete ${identifier}?`)) return;

    try {
      await deleteItem("racks", identifier);
      fetchRacks();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleStatusChange = async (rack) => {
    try {
      await toggleItem("racks", rack.identifier);

      fetchRacks();
    } catch (err) {
      console.error("Status update failed", err);

      alert("Status update failed");
    }
  };

  const columns = [
    {
      label: "SL NO",
      render: (_, index) => page * sizePerPage + index + 1,
    },
    { label: "Rack ID", key: "identifier" },
    {
      label: "Shelves",
      render: (row) => row.shelfs?.join(", ") || "-",
    },
    {
      label: "Status",
      render: (row) => (
        <label
          className="switch"
          aria-label={`Toggle status for rack ${row.identifier}`}
        >
          <input
            type="checkbox"
            checked={row.status}
            onChange={() => handleStatusChange(row)}
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
        setViewRack(row),
    },

    {
      label: "✏️ Edit",
      onClick: (row) => setEditRack(row),
    },

    {
      label: "🗑 Delete",
      type: "delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  const shelfOptions = shelves.map((shelf) => ({
    label: shelf.identifier,
    value: shelf.identifier,
  }));

  return (
    <AccessGuard requiredPath="/rack">
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
        actions={actions}
        emptyMessage="No racks found"

        onAdd={() =>
          setNewRack({
            identifier: "",
            shelfs: [],
          })
        }
        addButtonText="+ Add Rack"
        newItem={newRack}
        setNewItem={setNewRack}
        handleAdd={handleAddRack}
        addFields={[
          { name: "identifier", label: "Rack ID" },
          {
            name: "shelfs",
            label: "Shelves",
            type: "select",
            multiple: true,
            options: shelfOptions,
          },
        ]}

        editItem={editRack}
        setEditItem={setEditRack}
        handleUpdate={handleUpdate}
        
        viewItem={viewRack}
        setViewItem={setViewRack}
        editFields={[
          {
            name: "identifier",
            label: "Rack ID",
            disabled: true,
          },
          {
            name: "shelfs",
            label: "Shelves",
            type: "select",
            multiple: true,
            options: shelfOptions,
          },
        ]}
      />
    </AccessGuard>
  );
};

export default RacksPage;