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

const RackList = () => {
  const [racks, setRacks] = useState([]);
  const [shelfOptions, setShelfOptions] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");
  const [addError, setAddError] = useState("");

  const [viewItem, setViewItem] = useState(null);

  const [newRack, setNewRack] = useState({
    identifier: "",
    shelfs: [],
  });

  const [editRack, setEditRack] = useState(null);

  // ================= FETCH RACKS =================
  const fetchRacks = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("racks", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = res?.content || [];

      const normalized = data.map((r) => {
        let shelfs = [];
        if (Array.isArray(r.shelfs)) {
          shelfs = r.shelfs;
        } else if (r.shelfs) {
          shelfs = [r.shelfs];
        }

        return {
          ...r,
          status: r.status === true || r.status === 1 || r.status === "1",
          shelfs,
        };
      });

      setRacks(normalized);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalElements || data.length) / sizePerPage) ||
          1
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load racks");
    } finally {
      setLoading(false);
    }
  };

  // ================= FETCH SHELVES =================
  const fetchShelves = async () => {
    try {
      const res = await listItems("shelf", {
        page: 0,
        sizePerPage: 1000,
        search: "",
      });

      const raw = res?.content || res?.data || res || [];

      const active = raw
        .filter((s) => s.status === true || s.status === 1 || s.status === "1")
        .map((s) => ({
          label: s.description || s.identifier,
          value: s.identifier,
        }));

      setShelfOptions(active);
    } catch (err) {
      console.error("Shelf load failed", err);
      setShelfOptions([]);
    }
  };

  useEffect(() => {
    fetchRacks();
    fetchShelves();
  }, [page, searchTerm]);

  // ================= ADD =================
  const handleAddRack = async () => {
    try {
      setAddError("");

      const payload = {
        ...newRack,
        shelfs: newRack.shelfs || [],
      };

      const res = await addItem("racks", payload);

      if (res?.success === false) {
        setAddError(res.message || "Rack already exists");
        return false;
      }

      setNewRack({ identifier: "", shelfs: [] });
      await fetchRacks();

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
      await updateItem("racks", editRack);
      await fetchRacks();
      setEditRack(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
  const handleDelete = async (identifier) => {
    const ok = confirm(`Delete ${identifier}?`);
    if (!ok) return;

    try {
      await deleteItem("racks", identifier);

      setRacks((prev) =>
        prev.filter((r) => r.identifier !== identifier)
      );
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= TOGGLE =================
  const handleToggleStatus = async (identifier) => {
    setRacks((prev) =>
      prev.map((r) =>
        r.identifier === identifier
          ? { ...r, status: !r.status }
          : r
      )
    );

    try {
      await toggleItem("racks", identifier);
    } catch (err) {
      console.error(err);
      fetchRacks();
    }
  };

  // ================= COLUMNS =================
  const columns = [
    {
      label: "Sl No",
      render: (_, i) => page * sizePerPage + i + 1,
    },
    {
      label: "Identifier",
      key: "identifier",
    },
    {
      label: "Shelf",
      render: (row) =>
        Array.isArray(row.shelfs) ? row.shelfs.join(", ") : "-",
    },
    {
      label: "Status",
      render: (r) => (
        <label className="switch">
          <input
            type="checkbox"
            checked={r.status}
            onChange={() => handleToggleStatus(r.identifier)}
            aria-label={`Toggle status for rack ${r.identifier}`}
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
      onClick: (row) => {
        let shelfs;

        if (Array.isArray(row.shelfs)) {
          shelfs = row.shelfs;
        } else if (row.shelfs) {
          shelfs = [row.shelfs];
        } else {
          shelfs = [];
        }

        return setEditRack({ ...row, shelfs });
      },
    },
    {
      label: "Delete 🗑",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  // ================= FIELDS =================
  const addFields = [
    { name: "identifier", label: "Identifier" },
    {
      name: "shelfs",
      label: "Shelves",
      type: "multiselect",
      required: true,
      options: shelfOptions,
    },
  ];

  const editFields = [
    { name: "identifier", label: "Identifier", disabled: true },
    {
      name: "shelfs",
      label: "Shelves",
      type: "multiselect",
      options: shelfOptions,
    },
  ];

  return (
    <CommonList
      title="Racks"
      data={racks}
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
      addButtonText="+ Add Rack"
      newItem={newRack}
      setNewItem={setNewRack}
      handleAdd={handleAddRack}
      addFields={addFields}
      editItem={editRack}
      setEditItem={setEditRack}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      viewItem={viewItem}
      setViewItem={setViewItem}
      emptyMessage="No racks found"
    />
  );
};

export default RackList;
