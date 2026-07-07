"use client";
 
import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";
 
import {
  listItems,
  deleteItem,
  toggleItem,
  updateItem,
  addItem,
  getListItems,
} from "@/services/api";
 
const ProductList = () => {

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
 
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
 
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
 
  const sizePerPage = 5;
 
  const [newProduct, setNewProduct] =
    useState({
      identifier: "",
      supplierId: "",
      category: "",
    });
 
  const [editProduct, setEditProduct] =
    useState(null);
 
  const fetchProducts = async () => {
 
    try {
 
      setLoading(true);
 
      setError("");
 
      const res = await listItems(
        "product",
        {
          page,
          sizePerPage,
          sortField: "id",
          search: searchTerm,
        }
      );
 
      const data =
        res?.content || [];
 
      const normalized =
        data.map((p) => ({
          ...p,
 
          status:
            p.status === true ||
            p.status === 1 ||
            p.status === "1",
        }));
 
      setProducts(normalized);
 
      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (
              res?.totalElements ||
              data.length
            ) / sizePerPage
          ) ||
          1
      );
 
    } catch (err) {
 
      console.error(err);
 
      setError(
        "Failed to load products"
      );
 
    } finally {
 
      setLoading(false);
 
    }
  };
 
  const fetchCategory = async () => {
    try {
      const response =
        await getListItems(
          "category"
        );
      setCategories(
        response || []
      );
    } catch (err) {
      console.log(err);
    }
  };
  useEffect(() => {
    fetchProducts();
  }, [page,searchTerm]);
 
  useEffect(() => {
    fetchCategory();
  }, []);
 
  const handleAddProduct = async () => {

  const exists = products.some(
    (product) =>
      product.identifier?.trim().toLowerCase() ===
      newProduct.identifier?.trim().toLowerCase()
  );

  if (exists) {
    alert(`${newProduct.identifier} already exists`);
    return;
  }

  try {

    await addItem(
      "product",
      newProduct
    );

    setNewProduct({
      identifier: "",
      supplierId: "",
      category: "",
    });

    fetchProducts();

  } catch {

    alert("Add failed");

  }
};
 
  const handleUpdate =
    async () => {
 
      try {
 
        await updateItem(
          "product",
          editProduct
        );
 
        setProducts((prev) =>
          prev.map((p) =>
            p.identifier ===
            editProduct.identifier
              ? editProduct
              : p
          )
        );
        setEditProduct(null);
 
      } catch {
 
        alert("Update failed");
 
      }
    };
 
  const handleDelete =
    async (identifier) => {
 
      const confirmDelete =
        globalThis.confirm(
          `Delete ${identifier}?`
        );
 
      if (!confirmDelete) return;
 
      try {
 
        await deleteItem(
          "product",
          identifier
        );
 
        setProducts((prev) =>
          prev.filter(
            (p) =>
              p.identifier !==
              identifier
          )
        );
 
      } catch {
 
        alert("Delete failed");
 
      }
    };
 
  const handleToggleStatus =
    async (identifier) => {
 
      // SMOOTH UI UPDATE
      setProducts((prev) =>
        prev.map((p) =>
          p.identifier ===
          identifier
            ? {
                ...p,
                status: !p.status,
              }
            : p
        )
      );
 
      try {
 
        await toggleItem(
          "product",
          identifier
        );
 
      } catch {
 
        alert("Toggle failed");
 
        // REFETCH IF FAILED
        fetchProducts();
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
      label: "Supplier",
      key: "supplierId",
    },
 
    {
      label: "Category",
      key: "category",
    },
 
    {
      label: "Status",
 
      render: (p) => (
        <label className="switch" aria-label="Toggle status">
 
          <input
            type="checkbox"
 
            checked={p.status}
 
            onChange={() =>
              handleToggleStatus(
                p.identifier
              )
            }
          />
 
          <span className="slider"></span>
 
        </label>
      ),
    },
  ];
 
  const actions = [
    {
      label: "✏️ Edit",
 
      onClick: (row) =>
        setEditProduct(row),
    },
 
    {
      label: "🗑 Delete",
 
      onClick: (row) =>
        handleDelete(
          row.identifier
        ),
    },
  ];
 
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
    },
 
    {
      name: "supplierId",
      label: "Supplier ID",
    },
 
    {
      name: "category",
      label: "Category",
 
      type: "select",
 
      options:
        categories.map((cat) => ({
          label:
            cat.identifier,
 
          value:
            cat.identifier,
        })),
    },
  ];
 
  const editFields = [
    {
      name: "identifier",
 
      label: "Identifier",
 
      disabled: true,
    },
 
    {
      name: "supplierId",
 
      label: "Supplier ID",
    },
 
    {
      name: "category",
 
      label: "Category",
 
      type: "select",
 
      options:
        categories.map((cat) => ({
          label:
            cat.identifier,
 
          value:
            cat.identifier,
        })),
    },
  ];
 
  return (
    <CommonList
      title="Products"
 
      data={products}
 
      columns={columns}
 
      loading={loading}
 
      error={error}
 
      page={page}
 
      setPage={setPage}
 
      sizePerPage={sizePerPage}
 
      totalPages={totalPages}

      searchTerm={searchTerm}

      setSearchTerm={setSearchTerm}
 
      onAdd={() => {}}
 
      addButtonText="+ Add Product"
 
      newItem={newProduct}
 
      setNewItem={
        setNewProduct
      }
 
      handleAdd={
        handleAddProduct
      }
 
      addFields={addFields}
 
      editItem={editProduct}
 
      setEditItem={
        setEditProduct
      }
 
      handleUpdate={
        handleUpdate
      }
 
      editFields={editFields}
 
      actions={actions}
 
      emptyMessage="No products found"
    />
  );
};
 
export default ProductList;