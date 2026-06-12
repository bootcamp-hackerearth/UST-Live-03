"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  addItem,
  getListItems, // ✅ FIXED IMPORT
} from "@/services/api";

const PriceList = () => {
  const [prices, setPrices] = useState([]);
  const [products, setProducts] = useState([]);

  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [editPrice, setEditPrice] = useState(null);

  const sizePerPage = 5;

  const addButtonContainerStyle = { display: "flex", justifyContent: "flex-end", marginBottom: "16px" };

  const addButtonStyle = {
    background: "#1976d2",
    color: "#fff",
    border: "none",
    borderRadius: "6px",
    padding: "10px 18px",
    cursor: "pointer",
    fontWeight: "600",
  };

  const handleAddPriceClick = () =>
    setEditPrice({
      id: "",
      identifier: "",
      costPrice: "",
      sellingPrice: "",
      difference: "",
      isNew: true,
      formTitle: "Add Price",
    });

  // ================= FETCH PRODUCTS =================
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const res = await getListItems("product"); // ✅ FIXED

        const data = Array.isArray(res)
          ? res
          : res?.content || [];

        setProducts(data);
      } catch (err) {
        console.error("Failed to load products", err);
        setProducts([]); // safety fallback
      }
    };

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
        sortField: "identifier",
        search,
      });

      const data = Array.isArray(res)
        ? res
        : res?.content || [];

      setPrices(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalRecords || data.length) /
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
  }, [page, search]);

  // ================= DELETE =================
  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(
      `Delete price ${identifier}?`
    );

    if (!confirmDelete) return;

    try {
      await deleteItem("price", identifier, "identifier");
      fetchPrices();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= ADD / UPDATE =================
  const handleUpdate = async () => {
    try {
      const payload = {
        ...editPrice,
        costPrice: Number(editPrice.costPrice),
        sellingPrice: Number(editPrice.sellingPrice),
      };

      delete payload.isNew;
      delete payload.formTitle;

      if (editPrice.isNew) {
        delete payload.id;
        await addItem("price", payload);
      } else {
        await updateItem("price", payload);
      }

      await fetchPrices();
      setEditPrice(null);
    } catch (err) {
      console.error(err);

      alert(
        editPrice?.isNew
          ? "Failed to add price"
          : "Failed to update price"
      );
    }
  };

  // ================= COLUMNS =================
  const columns = [
    { label: "ID", key: "id" },
    { label: "Product", key: "identifier" },
    { label: "Cost Price", key: "costPrice" },
    { label: "Selling Price", key: "sellingPrice" },
    { label: "Difference", key: "difference" },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditPrice({
          ...row,
          isNew: false,
          formTitle: "Edit Price",
        }),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  // ================= EDIT FIELDS (FIXED DROPDOWN) =================
  const editFields = [
    {
      name: "id",
      label: "ID",
      disabled: true,
    },

    {
      name: "identifier",
      label: "Product",
      type: "select",
      options: products.map((p) => ({
        label: p.identifier,
        value: p.identifier,
      })),
      disabled: !editPrice?.isNew,
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
    {
      name: "difference",
      label: "Difference",
      disabled: true,
    },
  ];

  return (
    <>
      {/* ADD BUTTON */}
      <div style={addButtonContainerStyle}>
        <button onClick={handleAddPriceClick} style={addButtonStyle}>
          + Add Price
        </button>
      </div>

      {/* TABLE */}
      <CommonList
        title="Prices"
        data={prices}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        sizePerPage={sizePerPage}
        columns={columns}
        actions={actions}
        editItem={editPrice}
        setEditItem={setEditPrice}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editPrice?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No prices found"
      />
    </>
  );
};

export default PriceList;