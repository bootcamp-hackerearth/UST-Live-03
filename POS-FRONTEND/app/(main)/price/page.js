"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  addItem,
} from "@/services/api";

const PriceList = () => {
  const [prices, setPrices] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewPrice, setViewPrice] = useState(null);
  const sizePerPage = 5;

  const [newPrice, setNewPrice] = useState({
    identifier: "",
    costPrice: "",
    sellingPrice: "",
  });

  const [editPrice, setEditPrice] = useState(null);

  const fetchProducts = async () => {
    try {
      const res = await listItems("product", {
        page: 0,
        sizePerPage: 100,
        sortField: "id",
        search: searchTerm,
      });

      setProducts(res?.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchPrices = async () => {
    try {
      setError("");

      const res = await listItems("price", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = res?.content || [];

      setPrices(data);

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
      setError("Failed to load prices");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPrices();
  }, [page, searchTerm]);

  const handleAddPrice = async () => {
    if (!newPrice.identifier) {
      alert("Product is required");
      return;
    }

    if (
      newPrice.costPrice === "" ||
      Number(newPrice.costPrice) <= 0
    ) {
      alert("Cost Price must be greater than 0");
      return;
    }

    if (
      newPrice.sellingPrice === "" ||
      Number(newPrice.sellingPrice) <= 0
    ) {
      alert("Selling Price must be greater than 0");
      return;
    }

    const exists = prices.some(
      (price) =>
        price.identifier?.trim().toLowerCase() ===
        newPrice.identifier?.trim().toLowerCase()
    );

    if (exists) {
      alert(`${newPrice.identifier} already exists`);
      return;
    }

    try {
      await addItem("price", newPrice);

      setNewPrice({
        identifier: "",
        costPrice: "",
        sellingPrice: "",
      });

      fetchPrices();
    } catch {
      alert("Add failed");
    }
  };

  const handleUpdate = async () => {
    try {
      await updateItem("price", editPrice);

      setPrices((prev) =>
        prev.map((p) =>
          p.identifier === editPrice.identifier
            ? editPrice
            : p
        )
      );

      setEditPrice(null);
    } catch {
      alert("Update failed");
    }
  };

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(
      `Delete ${identifier}?`
    );

    if (!confirmDelete) return;

    try {
      await deleteItem("price", identifier);

      setPrices((prev) =>
        prev.filter(
          (p) => p.identifier !== identifier
        )
      );
    } catch {
      alert("Delete failed");
    }
  };

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
      label: "Cost Price",
      key: "costPrice",
    },

    {
      label: "Selling Price",
      key: "sellingPrice",
    },

    {
      label: "Difference",
      key: "difference",
    },
  ];

  const actions = [
    {
      label: "👁 View",
      onClick: (row) => setViewPrice(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) => setEditPrice(row),
    },

    {
      label: "🗑 Delete",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  const addFields = [
    {
      name: "identifier",
      label: "Product",
      type: "select",

      options: products.map((p) => ({
        label: p.identifier,
        value: p.identifier,
      })),
    },

    {
      name: "costPrice",
      label: "Cost Price",
      type: "number",
    },

    {
      name: "sellingPrice",
      label: "Selling Price",
      type: "number",
    },
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled: true,
    },

    {
      name: "costPrice",
      label: "Cost Price",
      type: "number",
    },

    {
      name: "sellingPrice",
      label: "Selling Price",
      type: "number",
    },
  ];

  return (
    <CommonList
      title="Prices"
      data={prices}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      onAdd={() => { }}
      addButtonText="+ Add Price"
      newItem={newPrice}
      setNewItem={setNewPrice}
      handleAdd={handleAddPrice}
      addFields={addFields}
      editItem={editPrice}
      setEditItem={setEditPrice}
      handleUpdate={handleUpdate}
      editFields={editFields}
      viewItem={viewPrice}
      setViewItem={setViewPrice}
      actions={actions}
      emptyMessage="No prices found"
    />
  );
};

export default PriceList;