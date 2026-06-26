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
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewWarehouse, setViewWarehouse] = useState(null);

  const [newItem, setNewItem] = useState({
    identifier: "",
    country: "",
    pincode: "",
    address: "",
  });

  const [editItem, setEditItem] = useState(null);

  const fetchWarehouses = async () => {
    try {
      const response = await listItems("warehouse", {
        page,
        sizePerPage: 5,
        sortField: "identifier",
        search: searchTerm,
      });

      setData(response.content || []);
      setTotalPages(response.totalPages || 0);
    } catch (error) {
      console.error("Error loading warehouses", error);
    }
  };

  useEffect(() => {
    fetchWarehouses();
  }, [page, searchTerm]);

  const handleAdd = async () => {
    if (!newItem.identifier?.trim()) {
      alert("Warehouse Name is required");
      return;
    }

    if (!newItem.country?.trim()) {
      alert("Country is required");
      return;
    }

    if (!newItem.pincode?.toString().trim()) {
      alert("Pincode is required");
      return;
    }

    if (!newItem.address?.trim()) {
      alert("Address is required");
      return;
    }

    try {
      await addItem("warehouse", newItem);

      setNewItem({
        identifier: "",
        country: "",
        pincode: "",
        address: "",
      });

      fetchWarehouses();
    } catch (error) {
      console.error("Error adding warehouse", error);
    }
  };

  const handleUpdate = async () => {
    try {
      await updateItem("warehouse", editItem);

      setEditItem(null);
      fetchWarehouses();
    } catch (error) {
      console.error("Error updating warehouse", error);
    }
  };

  const handleDelete = async (warehouse) => {
    if (!globalThis.confirm(`Delete warehouse ${warehouse.identifier}?`)) {
      return;
    }

    try {
      await deleteItem("warehouse", warehouse.identifier);
      fetchWarehouses();
    } catch (error) {
      console.error("Error deleting warehouse", error);
    }
  };

  const columns = [
    {
      label: "Warehouse Name",
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

  const addFields = [
    {
      name: "identifier",
      label: "Warehouse Name",
    },
    {
      name: "country",
      label: "Country",
    },
    {
      name: "pincode",
      label: "Pincode",
      type: "number",
    },
    {
      name: "address",
      label: "Address",
    },
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Warehouse Name",
      disabled: true,
    },
    {
      name: "country",
      label: "Country",
    },
    {
      name: "pincode",
      label: "Pincode",
      type: "number",
    },
    {
      name: "address",
      label: "Address",
    },
  ];

  const actions = [
    {
      label: "👁 View",
      onClick: (row) => setViewWarehouse(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) => setEditItem({ ...row }),
    },
    {
      label: "🗑 Delete",
      onClick: handleDelete,
    },
  ];

  return (
    <CommonList
      title="Warehouse"
      data={data}
      columns={columns}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      addButtonText="+ Add Warehouse"
      onAdd={() => { }}
      addFields={addFields}
      newItem={newItem}
      setNewItem={setNewItem}
      handleAdd={handleAdd}
      editItem={editItem}
      setEditItem={setEditItem}
      handleUpdate={handleUpdate}
      editFields={editFields}
      viewItem={viewWarehouse}
      setViewItem={setViewWarehouse}
      actions={actions}
      emptyMessage="No warehouses found"
    />
  );
};

export default WarehousePage;