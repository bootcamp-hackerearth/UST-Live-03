"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  addItem,
} from "@/services/api";

const PriceList = () => {
  const [prices, setPrices] = useState([]);
  const [products, setProducts] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [message , setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const sizePerPage = 5;

  // ================= ADD =================
  const [newPrice, setNewPrice] = useState({
    identifier: "",
    costPrice: "",
    sellingPrice: "",
  });

  // ================= EDIT =================
  const [editPrice, setEditPrice] =
    useState(null);

  // ================= FETCH PRODUCTS =================
  const fetchProducts = async () => {
    try {
      const res = await listItems(
        "product",
        {
          page: 0,
          sizePerPage: 100,
          sortField: "id",
        }
      );

      setProducts(res?.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  // ================= FETCH PRICES =================
  const fetchPrices = async () => {
    try {
      setLoading(true);

      setError("");

      const res = await listItems("price", {
        page,
        sizePerPage,
        sortField: "id",
        search:searchTerm,
      });

      const data = res?.content || [];

      setPrices(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalElements ||
              data.length) / sizePerPage
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
  }, [page ,searchTerm]);

  // ================= ADD =================
  const handleAddPrice = async () => {
    try {
      const res = await addItem("price", newPrice);
      if(res?.success === false){
        setMessage(res.message);
        return false;
      }
      setMessage("Price Added Successfully");
      setNewPrice({
        identifier: "",
        costPrice: "",
        sellingPrice: "",
      });

      fetchPrices();
    } catch (err) {
      err?.success?.data?.message ||
      err?.message ||
      setMessage("Add failed");
    }
  };

  // ================= UPDATE =================
  const handleUpdate = async () => {
    try {
      await updateItem("price", editPrice);

      setPrices((prev) =>
        prev.map((p) =>
          p.identifier ===
          editPrice.identifier
            ? editPrice
            : p
        )
      );

      setEditPrice(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
  const handleDelete = async (
    identifier
  ) => {
    const confirmDelete =
      globalThis.confirm(
        `Delete ${identifier}?`
      );

    if (!confirmDelete) return;

    try {
      await deleteItem(
        "price",
        identifier
      );

      setPrices((prev) =>
        prev.filter(
          (p) =>
            p.identifier !== identifier
        )
      );
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= COLUMNS =================
  const columns = [
    {
      label: "ID",
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
      label: "Difference",
      key: "difference",
    },

    {
      label: "Selling Price",
      key: "sellingPrice",
    },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "✏️",

      onClick: (row) =>
        setEditPrice(row),
    },

    {
      label: "🗑",

      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  // ================= ADD FIELDS =================
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

  // ================= EDIT FIELDS =================
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

      // ADD
      onAdd={() => {}}
      addButtonText="+ Add Price"
      newItem={newPrice}
      setNewItem={setNewPrice}
      handleAdd={handleAddPrice}
      addFields={addFields}
      message={message}
      setMessage={setMessage}
      // EDIT
      editItem={editPrice}
      setEditItem={setEditPrice}
      handleUpdate={handleUpdate}
      editFields={editFields}

      // ACTIONS
      actions={actions}

      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}

      emptyMessage="No prices found"
    />
  );
};

export default PriceList;