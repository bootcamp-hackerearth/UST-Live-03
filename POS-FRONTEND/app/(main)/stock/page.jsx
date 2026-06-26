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

const StockPage = () => {
  const [stocks, setStocks] = useState([]);
  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [addError, setAddError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [viewItem, setViewItem] = useState(null);

  // ADD
  const [newStock, setNewStock] = useState({
    identifier: "",
    productName: "",
    warehouseName: "",
    noOfProducts: "",
  });

  // EDIT
  const [editStock, setEditStock] = useState(null);

  // ================= PRODUCTS =================
  const fetchProducts = async () => {
    try {
      const res = await listItems("product", {
        page: 0,
        sizePerPage: 1000,
        sortField: "id",
        search: "",
      });

      setProducts(res?.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  // ================= WAREHOUSES =================
  const fetchWarehouses = async () => {
    try {
      const res = await listItems("warehouse", {
        page: 0,
        sizePerPage: 1000,
        sortField: "id",
        search: "",
      });

      setWarehouses(res?.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  // ================= STOCKS =================
  const fetchStocks = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("stock", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = res?.content || [];

      const normalized = data.map((s) => ({
        ...s,
        status:
          s.status === true ||
          s.status === 1 ||
          s.status === "1",
      }));

      setStocks(normalized);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalElements || data.length) /
              sizePerPage
          ) ||
          1
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load stocks");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStocks();
  }, [page, searchTerm]);

  useEffect(() => {
    fetchProducts();
    fetchWarehouses();
  }, []);

  // ================= ADD =================
  const handleAddStock = async () => {
    try {
      setAddError("");

      const response = await addItem(
        "stock",
        newStock
      );

      if (response?.success === false) {
        setAddError(
          response.message || "Stock already exists"
        );
        return false;
      }

      setNewStock({
        identifier: "",
        productName: "",
        warehouseName: "",
        noOfProducts: "",
      });

      await fetchStocks();
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
      await updateItem("stock", editStock);
      await fetchStocks();
      setEditStock(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
  const handleDelete = async (identifier) => {
    const ok = globalThis.confirm(
      `Delete ${identifier}?`
    );

    if (!ok) return;

    try {
      await deleteItem("stock", identifier);

      setStocks((prev) =>
        prev.filter(
          (s) => s.identifier !== identifier
        )
      );
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= TOGGLE =================
  const handleToggleStatus = async (
    identifier
  ) => {
    setStocks((prev) =>
      prev.map((s) =>
        s.identifier === identifier
          ? {
              ...s,
              status: !s.status,
            }
          : s
      )
    );

    try {
      await toggleItem("stock", identifier);
    } catch (err) {
      console.error(err);
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
    {
      label: "Identifier",
      key: "identifier",
    },
    {
      label: "Product",
      key: "productName",
    },
    {
      label: "Warehouse",
      key: "warehouseName",
    },
    {
      label: "Quantity",
      key: "noOfProducts",
    },
    {
      label: "Status",
      render: (s) => (
        <label className="switch" aria-label={`Toggle status for ${s.productName}`}>
          <input
            type="checkbox"
            checked={s.status}
            onChange={() =>
              handleToggleStatus(
                s.identifier
              )
            }
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
      onClick: (row) =>
        setEditStock(row),
    },
    {
      label: "Delete 🗑",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      required: true,
    },
    {
      name: "productName",
      label: "Product",
      type: "select",
      options: products.map((p) => ({
        value: p.identifier,
        label: p.identifier,
      })),
       required: true,
    },
    {
      name: "warehouseName",
      label: "Warehouse",
      type: "select",
      options: warehouses.map((w) => ({
        value: w.identifier,
        label: w.identifier,
      })),
       required: true,
    },
    {
      name: "noOfProducts",
      label: "No Of Products",
      type: "number",
       required: true,
    },
  ];

  // ================= EDIT FIELDS =================
  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled: true,
    },
    {
      name: "productName",
      label: "Product",
      type: "select",
      options: products.map((p) => ({
        value: p.identifier,
        label: p.identifier,
      })),
    },
    {
      name: "warehouseName",
      label: "Warehouse",
      type: "select",
      options: warehouses.map((w) => ({
        value: w.identifier,
        label: w.identifier,
      })),
    },
    {
      name: "noOfProducts",
      label: "No Of Products",
      type: "number",
    },
  ];

  return (
    <CommonList
      title="Stocks"
      data={stocks}
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
      addButtonText="+ Add Stock"
      newItem={newStock}
      setNewItem={setNewStock}
      handleAdd={handleAddStock}
      addFields={addFields}
      editItem={editStock}
      setEditItem={setEditStock}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      viewItem={viewItem}
      setViewItem={setViewItem}
      emptyMessage="No stocks found"
    />
  );
};

export default StockPage;