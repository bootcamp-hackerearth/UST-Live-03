"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  addItem,
  getListItems,
  addCartEntry,
} from "@/services/api";

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [editProduct, setEditProduct] = useState(null);

  const [auditProduct, setAuditProduct] = useState(null);

  const sizePerPage = 5;

  const fetchProducts = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("product", {
        page,
        sizePerPage,
        sortField: "identifier",
        search,
      });

      let data = [];

      if (Array.isArray(res)) {
        data = res;
      } else if (Array.isArray(res?.content)) {
        data = res.content;
      }

      setProducts(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load products");
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await getListItems("category");

      const data = Array.isArray(response) ? response : response?.content || [];

      setCategories(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(`Delete product ${identifier}?`);

    if (!confirmDelete) return;

    try {
      await deleteItem("product", identifier, "identifier");

      fetchProducts();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleAddToCart = async (product) => {
    try {
      const cartId = localStorage.getItem("cartId");

      if (!cartId) {
        alert("Please create a cart first.");

        return;
      }

      await addCartEntry({
        cartId,
        product: product.identifier,
        quantity: 1,
        discount: 0,
      });

      alert(`${product.identifier} added to cart`);
    } catch (err) {
      console.error(err);
      alert("Failed to add product to cart");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editProduct,
        supplierId: Number(editProduct.supplierId),
      };

      if (editProduct.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("product", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("product", payload);
      }

      await fetchProducts();
      setEditProduct(null);
    } catch (err) {
      console.error(err);

      alert(
        editProduct?.isNew
          ? "Failed to add product"
          : "Failed to update product",
      );
    }
  };

  const columns = [
    {
      label: "ID",
      key: "id",
    },
    {
      label: "Identifier",
      key: "identifier",
    },
    {
      label: "Supplier ID",
      key: "supplierId",
    },
    {
      label: "Category",
      key: "category",
    },
    {
      label: "Status",
      render: (row) => (
        <label className="switch" aria-label="Status">
          <input type="checkbox" checked={row.status} readOnly />
          <span className="slider round"></span>
        </label>
      ),
    },
  ];

  const actions = [
    {
      label: "🛒 Add To Cart",
      onClick: (row) => handleAddToCart(row),
    },
    {
      label: "📋 Audit Details",
      onClick: (row) => setAuditProduct(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditProduct({
          ...row,
          isNew: false,
          formTitle: "Edit Product",
        }),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];
  const editFields = [
    {
      name: "id",
      label: "ID",
      disabled: true,
    },
    {
      name: "identifier",
      label: "Identifier",
      disabled: !editProduct?.isNew,
    },
    {
      name: "supplierId",
      label: "Supplier ID",
      type: "number",
    },
    {
      name: "category",
      label: "Category",
      type: "select",
      options: categories.map((cat) => ({
        label: cat.identifier,
        value: cat.identifier,
      })),
    },
    {
      name: "status",
      label: "Status",
      type: "checkbox",
    },
  ];

  return (
    <>
      <div
        style={{
          display: "flex",
          justifyContent: "flex-end",
          marginBottom: "16px",
        }}
      >
        <button
          onClick={() =>
            setEditProduct({
              id: "",
              identifier: "",
              supplierId: "",
              category: "",
              status: true,
              isNew: true,
              formTitle: "Add Product",
            })
          }
          style={{
            background: "#1976d2",
            color: "#fff",
            border: "none",
            borderRadius: "6px",
            padding: "10px 18px",
            cursor: "pointer",
            fontWeight: "600",
          }}
        >
          + Add Product
        </button>
      </div>

      <CommonList
        title="Products"
        data={products}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        sizePerPage={sizePerPage}
        columns={columns}
        actions={actions}
        editItem={editProduct}
        setEditItem={setEditProduct}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editProduct?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No products found"
      />

      {auditProduct && (
        <div className="modalOverlay">
          <div
            className="modal"
            style={{
              width: "500px",
              padding: "20px",
            }}
          >
            <h2>Audit Details</h2>

            <div
              style={{
                display: "grid",
                gap: "12px",
                marginTop: "16px",
              }}
            >
              <div>
                <strong>Product:</strong> {auditProduct.identifier}
              </div>

              <div>
                <strong>Created By:</strong> {auditProduct.createdBy || "N/A"}
              </div>

              <div>
                <strong>Created On:</strong>{" "}
                {auditProduct.createdOn
                  ? new Date(auditProduct.createdOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Modified By:</strong> {auditProduct.modifiedBy || "N/A"}
              </div>

              <div>
                <strong>Modified On:</strong>{" "}
                {auditProduct.modifiedOn
                  ? new Date(auditProduct.modifiedOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Status:</strong>{" "}
                {auditProduct.status ? "Active" : "Inactive"}
              </div>
            </div>

            <div
              className="modalActions"
              style={{
                marginTop: "20px",
              }}
            >
              <button onClick={() => setAuditProduct(null)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default ProductList;
