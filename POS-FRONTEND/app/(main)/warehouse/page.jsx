"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";
import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
} from "@/services/api";

const WarehousePage = () => {
  const [warehouses, setWarehouses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [searchTerm, setSearchTerm] = useState("");
  const [addError, setAddError] = useState("");

  const sizePerPage = 5;

  const [newWarehouse, setNewWarehouse] = useState({
    identifier: "",
    country: "",
    pincode: "",
    address: "",
  });

  const [editWarehouse, setEditWarehouse] = useState(null);

  const [viewItem, setViewItem] = useState(null);
  
  // ================= FETCH =================
  const fetchWarehouses = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("warehouse", {
        page,
        sizePerPage,
        sortField: "identifier",
        search: searchTerm?.trim() || "",
      });

      console.log("WAREHOUSE RESPONSE:", res);

      const data = res?.content || [];

      setWarehouses(data);
      setTotalPages(res?.totalPages || 1);
    } catch (err) {
      console.error(err);
      setError("Failed to load warehouses");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWarehouses();
  }, [page, searchTerm]);

  // ================= ADD =================
  const handleAddWarehouse = async () => {
    try {
      setAddError("");

      const res = await addItem("warehouse", newWarehouse);

      if (res?.success === false) {
        setAddError(res.message || "Add failed");
        return false;
      }

      setNewWarehouse({
        identifier: "",
        country: "",
        pincode: "",
        address: "",
      });

      await fetchWarehouses();
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
      await updateItem("warehouse", editWarehouse);

      await fetchWarehouses();

      setEditWarehouse(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
  const handleDelete = async (identifier) => {
    if (!confirm(`Delete ${identifier}?`)) return;

    try {
      await deleteItem("warehouse", identifier);

      setWarehouses((prev) =>
        prev.filter((w) => w.identifier !== identifier)
      );
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= TABLE COLUMNS =================
  const columns = [
    {
      label: "S.No",
      render: (_, index) => page * sizePerPage + index + 1,
    },
    {
      label: "Identifier",
      key: "identifier",
    },
    {
      label: "Country",
      key: "country",
    },
    {
      label: "Pincode",
      key: "pincode",
    },
    {
      label: "Address",
      key: "address",
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
      onClick: (row) => setEditWarehouse(row),
    },
    {
      label: "Delete 🗑",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  // ================= ADD FIELDS =================
  const addFields = [
    { name: "identifier", label: "Identifier", required: true,},
    { name: "country", label: "Country" ,  required: true},
 {
    name: "pincode",
    label: "Pincode",
    type: "text",
    maxLength: 6,
    pattern: /^\d*$/,
     required: true
  },    
  { name: "address", label: "Address",  required: true },
  ];

  // ================= EDIT FIELDS =================
  const editFields = [
    { name: "identifier", label: "Identifier", disabled: true },
    { name: "country", label: "Country" },
 {
    name: "pincode",
    label: "Pincode",
    type: "text",
    maxLength: 6,
    pattern: /^\d*$/,

  },    { name: "address", label: "Address" },
  ];

  return (
    <CommonList
      title="Warehouses"
      data={warehouses}
      columns={columns}
      loading={loading}
      error={error}
      addError={addError}
      emptyMessage="No warehouses found"

      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}

      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}

      onAdd={() => setAddError("")}
      addButtonText="+ Add Warehouse"
      newItem={newWarehouse}
      setNewItem={setNewWarehouse}
      handleAdd={handleAddWarehouse}
      addFields={addFields}

      editItem={editWarehouse}
      setEditItem={setEditWarehouse}
      handleUpdate={handleUpdate}
      editFields={editFields}

      actions={actions}
      viewItem={viewItem}
      setViewItem={setViewItem}
    />
  );
};

export default WarehousePage;