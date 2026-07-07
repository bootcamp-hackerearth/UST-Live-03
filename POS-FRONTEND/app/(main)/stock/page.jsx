"use client";

import { useEffect, useState } from "react";

import CommonList from "@/app/components/CommonList/CommonList";
import AccessGuard from "@/app/components/AccessGuard";

import {
  getAllItems,
  listItems,
  addItem,
  updateItem,
  deleteItem,
  toggleItem,
} from "@/services/api";

const StockPage = () => {
  const [stocks, setStocks] = useState([]);
  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [searchTerm, setSearchTerm] = useState("");

  const sizePerPage = 5;

  const [newStock, setNewStock] = useState({
    identifier: "",
    warehouseName: "",
    productName: "",
    quantity: "",
  });

  const [editStock, setEditStock] = useState(null);
  const [viewStock, setViewStock] = useState(null);

  const fetchStocks = async () => {
    try {
      if (stocks.length === 0) {
        setLoading(true);
      }

      setError("");

      const res = await listItems(
        "stock",
        {
          page,
          sizePerPage,
          sortField: "id",
          search: searchTerm,
        }
      );

      setStocks(
        res?.content || res || []
      );

      setTotalPages(
        res?.totalPages || 1
      );

    } catch (err) {
      console.error(err);

      setError(
        "Failed to load stocks"
      );

    } finally {
      setLoading(false);
    }
  };

  const fetchProducts = async () => {
    try {
      const res =
        await getAllItems(
          "product"
        );

      setProducts(
        res?.content || res || []
      );

    } catch (err) {
      console.error(
        "Failed to load products",
        err
      );
    }
  };

  const fetchWarehouses = async () => {
    try {
      const res =
        await getAllItems(
          "warehouse"
        );

      setWarehouses(
        res?.content || res || []
      );

    } catch (err) {
      console.error(
        "Failed to load warehouses",
        err
      );
    }
  };

  useEffect(() => {
    fetchStocks();
  }, [page, searchTerm]);

  useEffect(() => {
    fetchProducts();
    fetchWarehouses();
  }, []);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddStock = async () => {
    const response = await addItem("stock", newStock);

    if (response?.success !== false) {
      setNewStock({
        identifier: "",
        warehouseName: "",
        productName: "",
        quantity: "",
      });

      fetchStocks();
    }

    return response;
  };

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(
      `Delete stock ${identifier}?`
    );

    if (!confirmDelete) return;

    try {
      await deleteItem("stock", identifier);

      fetchStocks();
    } catch (err) {
      console.error("Delete failed", err);

      alert("Delete failed");
    }
  };

  const handleStatusChange = async (stock) => {
    try {
      await toggleItem("stock", stock.identifier);

      fetchStocks();
    } catch (err) {
      console.error("Status update failed", err);

      alert("Status update failed");
    }
  };

  const handleUpdate = async () => {
    try {
      await updateItem("stock", editStock);

      setEditStock(null);
      fetchStocks();
    } catch (err) {
      console.error("Update failed", err);

      alert("Update failed");
    }
  };

  const columns = [
    {
      label: "SL NO",
      render: (row, index) =>
        page * sizePerPage + index + 1,
    },
    {
      label: "Stock ID",
      key: "identifier",
    },
    {
      label: "Status",
      render: (row) => (
        <label
          className="switch"
          aria-label={`Toggle status for stock ${row.identifier}`}
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
    {
      label: "Warehouse",
      key: "warehouseName",
    },
    {
      label: "Product",
      key: "productName",
    },
    {
      label: "Quantity",
      key: "quantity",
    },
  ];

  const actions = [
    {
      label: "👁 View",
      type: "view",
      onClick: (row) => setViewStock(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) => setEditStock(row),
    },
    {
      label: "🗑 Delete",
      type: "delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  const commonStockFields = [
    {
      name: "warehouseName",
      label: "Warehouse",
      type: "select",
      options: warehouses.map((warehouse) => ({
        label: warehouse.identifier,
        value: warehouse.identifier,
      })),
    },
    {
      name: "productName",
      label: "Product",
      type: "select",
      options: products.map((product) => ({
        label: product.identifier,
        value: product.identifier,
      })),
    },
    {
      name: "quantity",
      label: "Quantity",
      type: "number",
    },
  ];

  const addFields = [
    {
      name: "identifier",
      label: "Stock ID",
      type: "text",
    },
    ...commonStockFields,
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Stock ID",
      disabled: true,
    },
    ...commonStockFields,
  ];

  return (
    <AccessGuard requiredPath="/stock">
      <CommonList
        title="Stocks"
        data={stocks}
        columns={columns}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        onAdd={() =>
          setNewStock({
            identifier: "",
            warehouseName: "",
            productName: "",
            quantity: "",
          })
        }
        addButtonText="+ Add Stock"
        newItem={newStock}
        setNewItem={setNewStock}
        handleAdd={handleAddStock}
        addFields={addFields}
        editItem={editStock}
        setEditItem={setEditStock}
        handleUpdate={handleUpdate}
        viewItem={viewStock}
        setViewItem={setViewStock}
        editFields={editFields}
        actions={actions}
        emptyMessage="No stocks found"
      />
    </AccessGuard>
  );
};

export default StockPage;