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

  const sizePerPage = 5;

  const [addError, setAddError] = useState("");

  const [searchTerm, setSearchTerm] = useState("");
  // ================= ADD =================
  const [newPrice, setNewPrice] = useState({
    identifier: "",
    costPrice: "",
    sellingPrice: "",
  });

  // ================= EDIT =================
  const [editPrice, setEditPrice] = useState(null);

  // ================= FETCH PRODUCTS =================
  const fetchProducts = async () => {
    try {
      const res = await listItems("product", {
        page: 0,
        sizePerPage: 100,
        sortField: "id",
      });

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

  // ================= ADD =================
const handleAddPrice = async () => {
  try {
    setAddError("");

    const response = await addItem(
      "price",
      newPrice
    );

    if (response?.success === false) {
      setAddError(
        response.message ||
        "Price already exists"
      );
      return false;
    }

    setNewPrice({
      identifier: "",
      costPrice: "",
      sellingPrice: "",
    });

    await fetchPrices();
    return true;
  } catch (err) {
    console.error(err);

    setAddError(
      err.response?.data?.message ||
      "Add failed"
    );
    return false;
  }
};

  // ================= UPDATE =================
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
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
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
    } catch (err) {
      console.error(err);
      alert("Delete failed");
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

  // ================= ACTIONS =================
  const actions = [
    {
      label: "✏️",
      onClick: (row) => setEditPrice(row),
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
      addError={addError}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      // ADD
      onAdd={() => {setAddError("");}}
      addButtonText="+ Add Price"
      newItem={newPrice}
      setNewItem={setNewPrice}
      handleAdd={handleAddPrice}
      addFields={addFields}
      // EDIT
      editItem={editPrice}
      setEditItem={setEditPrice}
      handleUpdate={handleUpdate}
      editFields={editFields}
      // ACTIONS
      actions={actions}
      emptyMessage="No prices found"

      searchTerm={searchTerm}
  setSearchTerm={setSearchTerm}
    />
  );
};

export default PriceList;