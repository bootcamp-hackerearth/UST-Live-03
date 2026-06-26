"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList/CommonList";
import AccessGuard from "@/app/components/AccessGuard";

import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
} from "@/services/api";

const WarehousePage = () => {
  const [warehouses, setWarehouses] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [newWarehouse, setNewWarehouse] = useState({
    identifier: "",
    country: "",
    pincode: "",
    address: "",
  });

  const [editWarehouse, setEditWarehouse] = useState(null);
  const [viewWarehouse, setViewWarehouse] = useState(null);

  const fetchWarehouses = async () => {
    try {
      if (warehouses.length === 0) {
        setLoading(true);
      }

      setError("");

      const res = await listItems(
        "warehouse",
        {
          page,
          sizePerPage,
          sortField: "id",
          search: searchTerm,
        }
      );

      setWarehouses(res?.content || []);
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

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddWarehouse =
    async () => {
      const response = await addItem(
        "warehouse",
        newWarehouse
      );

      if (response?.success === false) {
        throw new Error(response.message);
      }

      setNewWarehouse({
        identifier: "",
        country: "",
        pincode: "",
        address: "",
      });

      fetchWarehouses();

      return true;
    };

  const handleUpdate = async () => {
    const response = await updateItem(
      "warehouse",
      editWarehouse
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    fetchWarehouses();

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
        "warehouse",
        identifier
      );

      fetchWarehouses();

    } catch (err) {
      console.error(err);
      alert("Delete failed");
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
      label: "Warehouse ID",
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

  const actions = [
    {
      label: "👁 View",
      type: "view",
      onClick: (row) =>
        setViewWarehouse(row),
    },

    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditWarehouse(row),
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
    <AccessGuard requiredPath="/warehouse">
      <CommonList
        title="Warehouses"
        data={warehouses}
        columns={columns}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        actions={actions}
        emptyMessage="No warehouses found"

        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}

        onAdd={() => { }}
        addButtonText="+ Add Warehouse"

        newItem={newWarehouse}
        setNewItem={setNewWarehouse}
        handleAdd={handleAddWarehouse}

        addFields={[
          {
            name: "identifier",
            label: "Warehouse ID",
          },

          {
            name: "country",
            label: "Country",
          },

          {
            name: "pincode",
            label: "Pincode",
            pattern: /^\d{6}$/,
            errorMessage: "Enter a valid 6 digit pincode",
          },

          {
            name: "address",
            label: "Address",
          },
        ]}

        editItem={editWarehouse}
        setEditItem={setEditWarehouse}
        handleUpdate={handleUpdate}

        viewItem={viewWarehouse}
        setViewItem={setViewWarehouse}

        editFields={[
          {
            name: "identifier",
            label: "Warehouse ID",
            disabled: true,
          },

          {
            name: "country",
            label: "Country",
          },

          {
            name: "pincode",
            label: "Pincode",
            pattern: /^\d{6}$/,
            errorMessage: "Enter a valid 6 digit pincode",
          },

          {
            name: "address",
            label: "Address",
          },
        ]}
      />
    </AccessGuard>
  );
};

export default WarehousePage;