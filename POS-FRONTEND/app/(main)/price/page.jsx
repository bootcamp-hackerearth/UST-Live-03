"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList/CommonList";
import AccessGuard from "@/app/components/AccessGuard";

import {
  listItems,
  deleteItem,
  updateItem,
  addItem,
} from "@/services/api";

const PricePage = () => {
  const [prices, setPrices] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [page, setPage] =
    useState(0);

  const [totalPages, setTotalPages] =
    useState(1);

  const [searchTerm, setSearchTerm] =
    useState("");

  const sizePerPage = 5;

  const [newPrice, setNewPrice] =
    useState({
      identifier: "",
      costPrice: "",
      sellingPrice: "",
    });

  const [products, setProducts] =
    useState([]);

  const [editPrice, setEditPrice] =
    useState(null);

  const fetchPrices = async () => {
  try {
    if (prices.length === 0) {
      setLoading(true);
    }

    setError("");

    const res = await listItems(
      "price",
      {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      }
    );

    setPrices(
      res?.content || res || []
    );

    setTotalPages(
      res?.totalPages || 1
    );

  } catch (err) {
    console.error(err);

    setError(
      "Failed to load prices"
    );
  } finally {
    setLoading(false);
  }
};

  const fetchProducts = async () => {
    try {
      const res =
        await listItems("product");

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

  useEffect(() => {
    fetchPrices();
  }, [page, searchTerm]);

  useEffect(() => {
    fetchProducts();
  }, []);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddPrice = async () => {
    const response = await addItem(
      "price",
      newPrice
    );

    if (response?.success !== false) {
      setNewPrice({
        identifier: "",
        costPrice: "",
        sellingPrice: "",
      });

      fetchPrices();
    }

    return response;
  };

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

      fetchPrices();

    } catch (err) {
      console.error("Delete failed", err);
      alert("Delete failed");
    }
  };

  const openEdit = (price) => {
    setEditPrice(price);
  };

  const handleUpdate = async () => {
    try {
      await updateItem(
        "price",
        editPrice
      );

      setEditPrice(null);

      fetchPrices();

    } catch (err) {
      console.error("Update failed", err);
      alert("Update failed");
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
      label: "Product Name",
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
      render: (p) =>
        Number(
          p.sellingPrice || 0
        ) -
        Number(
          p.costPrice || 0
        ),
    },
  ];

  const actions = [
    {
      label: "✏️ Edit",
      onClick: openEdit,
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
    <AccessGuard requiredPath="/product">
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

        onAdd={() =>
          setNewPrice({
            identifier: "",
            costPrice: "",
            sellingPrice: "",
          })
        }
        addButtonText="+ Add Price"
        newItem={newPrice}
        setNewItem={setNewPrice}
        handleAdd={handleAddPrice}

        addFields={[
          {
            name: "identifier",
            label: "Product",
            type: "select",
            options: products.map(
              (product) => ({
                label:
                  product.identifier,
                value:
                  product.identifier,
              })
            ),
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
        ]}

        editItem={editPrice}
        setEditItem={setEditPrice}
        handleUpdate={handleUpdate}

        editFields={[
          {
            name: "identifier",
            label: "Product",
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
        ]}

        actions={actions}
        emptyMessage="No prices found"
      />
    </AccessGuard>  
  );
};

export default PricePage;