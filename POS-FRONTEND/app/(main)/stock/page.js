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

const StockList = () => {
  const [stocks, setStocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [warehouses, setWarehouses] = useState([]);
  const [products, setProducts] = useState([]);
  const [viewStock, setViewStock] = useState(null);

  const sizePerPage = 5;

  const [newStock, setNewStock] = useState({
    identifier: "",
    warehouseName: "",
    productName: "",
    noOfProducts: "",
  });

  const [editStock, setEditStock] = useState(null);

  // ================= FETCH STOCK =================
  const fetchStocks = async () => {
    try {
      setLoading(true);

      const res = await listItems("stock", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = res?.content || [];

      setStocks(data);

      setTotalPages(
        res?.totalPages ||
        Math.ceil((res?.totalElements || data.length) / sizePerPage) ||
        1
      );
    } catch (err) {
      console.error("Failed to load stocks:", err);
      setError("Failed to load stocks");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStocks();
  }, [page, searchTerm]);

  useEffect(() => {
    const fetchDropdowns = async () => {
      try {
        const whRes = await listItems("warehouse", {
          page: 0,
          sizePerPage: 100,
        });

        const prRes = await listItems("product", {
          page: 0,
          sizePerPage: 100,
        });

        setWarehouses(whRes?.content || []);
        setProducts(prRes?.content || []);

        console.log("WAREHOUSES", whRes?.content);
        console.log("PRODUCTS", prRes?.content);
      } catch (err) {
        console.log("Dropdown fetch error", err);
      }
    };

    fetchDropdowns();
  }, []);
  // ================= ADD STOCK =================
  const handleAdd = async () => {
    if (!newStock.identifier?.trim()) {
      alert("Stock Name is required");
      return;
    }

    if (!newStock.warehouseName?.trim()) {
      alert("Warehouse is required");
      return;
    }

    if (!newStock.productName?.trim()) {
      alert("Product is required");
      return;
    }

    if (
      !newStock.noOfProducts ||
      Number(newStock.noOfProducts) <= 0
    ) {
      alert("Quantity must be greater than 0");
      return;
    }

    await addItem("stock", newStock);

    setNewStock({
      identifier: "",
      warehouseName: "",
      productName: "",
      noOfProducts: "",
    });

    fetchStocks();
  };

  const handleUpdate = async () => {
    await updateItem("stock", editStock);
    setEditStock(null);
    fetchStocks();
  };

  // ================= DELETE =================
  const handleDelete = async (identifier) => {
    await deleteItem("stock", identifier);
    setStocks((prev) =>
      prev.filter((s) => s.identifier !== identifier)
    );
  };

  // ================= TOGGLE STATUS =================
  const handleToggleStatus = async (row) => {
    const identifier = row.identifier;

    setStocks((prev) =>
      prev.map((s) =>
        s.identifier === identifier
          ? { ...s, status: !s.status }
          : s
      )
    );

    try {
      await toggleItem("stock", identifier);
    } catch {
      alert("Toggle failed");
      fetchStocks();
    }
  };

  // ================= COLUMNS =================
  const columns = [
    {
      label: "Sl No",
      render: (row, index) =>
        page * sizePerPage + index + 1,
    },
    { label: "Stock Name", key: "identifier" },
    { label: "Warehouse", key: "warehouseName" },
    { label: "Product", key: "productName" },
    { label: "Quantity", key: "noOfProducts" },

    {
      label: "Status",
      key: "status",
    },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "👁 View",
      onClick: (row) => setViewStock(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) => setEditStock(row),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  // ================= ADD FIELDS =================
  const stockFields = [
    { name: "identifier", label: "Stock Name" },

    {
      name: "warehouseName",
      label: "Warehouse",
      type: "select",
      options: warehouses.map((w) => ({
        label: w.name || w.identifier,
        value: w.identifier,
      })),
    },

    {
      name: "productName",
      label: "Product",
      type: "select",
      options: products.map((p) => ({
        label: p.name || p.identifier,
        value: p.identifier,
      })),
    },

    { name: "noOfProducts", label: "Quantity", type: "number" },
  ];

  return (
    <CommonList
      title="Stock"
      data={stocks}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      newItem={newStock}
      setNewItem={setNewStock}
      onAdd={handleAdd}
      handleAdd={handleAdd}
      addFields={stockFields}
      editItem={editStock}
      setEditItem={setEditStock}
      handleUpdate={handleUpdate}
      editFields={stockFields}
      actions={actions}
      viewItem={viewStock}
      setViewItem={setViewStock}
      emptyMessage="No stock found"
      onToggleStatus={handleToggleStatus}
    />
  );
};

export default StockList;