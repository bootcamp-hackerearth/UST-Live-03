  "use client";

  import { useEffect, useState } from "react";
  import CommonList from "@/app/components/CommonList/CommonList";
  import AccessGuard from "@/app/components/AccessGuard";

  import {
    listItems,
    deleteItem,
    toggleItem,
    updateItem,
    addItem,
    getSubCategories,
    getAllItems,
  } from "@/services/api";

  const ProductPage = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const sizePerPage = 5;

    const [newProduct, setNewProduct] =
      useState({
        identifier: "",
        supplierID: "",
        brand: "",
        unit: "",
        categories: [],
      });

    const [categoriesList, setCategoriesList] = useState([]);
    const [brands, setBrands] = useState([]);
    const [units, setUnits] = useState([]);

    const [searchTerm, setSearchTerm] = useState("");

    const [editProduct, setEditProduct] = useState(null);
    const [viewProduct, setViewProduct] = useState(null);

    const fetchProducts = async () => {
      try {
        if (products.length === 0) {
          setLoading(true);
        }

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

        const normalized =
          (res?.content || []).map(
            (p) => ({
              ...p,
              status:
                p.status === 1 ||
                p.status === true ||
                p.status === "1",
            })
          );

        setProducts(normalized);
        setTotalPages(res?.totalPages || 1);

      } catch (err) {

        console.error(err);
        setError("Failed to load products");

      } finally {
        setLoading(false);
      }
    };

    const fetchCategories = async () => {
      try {

        const res = await getSubCategories("category");
        console.log("CATEGORY RESPONSE:", res);
        setCategoriesList(res?.content || res || []);

      } catch (err) {
        console.error("Failed to load categories", err);
      }
    };

    const fetchBrands = async () => {
      try {
        const res = await getAllItems("brand");

        setBrands(res?.content || res || []);
      } catch (err) {
        console.error("Failed to load brands", err);
      }
    };

    const fetchUnits = async () => {
      try {
        const res = await getAllItems("unit");
        
        setUnits(res?.content || res || []);
      } catch (err) {
        console.error("Failed to load units", err);
      }
    };

    useEffect(() => {
      fetchProducts();
    }, [page, searchTerm]);

    useEffect(() => {
      fetchCategories();
      fetchBrands();
      fetchUnits();
    }, []);

    useEffect(() => {
      setPage(0);
    }, [searchTerm]);

    const handleAddProduct = async () => {
      const response = await addItem(
        "product",
        newProduct
      );

      console.log("Response:", response);

      if (response?.success === false) {
        throw new Error(response.message);
      }

      setNewProduct({
        identifier: "",
        supplierID: "",
        brand: "",
        unit: "",
        categories: [],
      });

      fetchProducts();

      return true;
    };

    const handleToggleStatus =
      async (identifier) => {
        try {
          await toggleItem(
            "product",
            identifier
          );

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

        } catch (err) {
          console.error("Failed to update status:", err);
          alert("Failed to update status");
        }
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
          "product",
          identifier
        );

        fetchProducts();

      } catch (err) {
        console.error("Delete failed", err);
        alert("Delete failed");
      }
    };

    const openEdit = (product) => {
      setEditProduct(product);
    };

    const handleUpdate = async () => {
      const response = await updateItem(
        "product",
        editProduct
      );

      if (response?.success === false) {
        throw new Error(response.message);
      }

      fetchProducts();

      return true;
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
        label: "Supplier ID",
        key: "supplierID",
      },

      {
        label: "Brand",
        key: "brand",
      },

      {
        label: "Unit",
        key: "unit",
      },

      {
        label: "Categories",
        render: (p) =>
          Array.isArray(p.categories)
            ? p.categories.join(", ")
            : "-",
      },

      {
        label: "Status",
        render: (p) => (
          <label className="switch">
            <input
              type="checkbox"
              aria-label={`Toggle status for ${p.identifier}`}
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
        label: "👁 View",
        type: "view",
        onClick: (row) =>
          setViewProduct(row),
      },

      {
        label: "✏️ Edit",
        onClick: openEdit,
      },

      {
        label: "🗑 Delete",
        type: "delete",
        onClick: (row) =>
          handleDelete(row.identifier),
      }
    ];

    const commonProductFields = [
      {
        name: "supplierID",
        label: "Supplier ID",
      },
      {
        name: "brand",
        label: "Brand",
        type: "select",
        options: brands.map((brand) => ({
          label: brand.identifier,
          value: brand.identifier,
        })),
      },
      {
        name: "unit",
        label: "Unit",
        type: "select",
        options: units.map((unit) => ({
          label: unit.identifier,
          value: unit.identifier,
        })),
      },
      {
        name: "categories",
        label: "Categories",
        type: "select",
        multiple: true,
        options: categoriesList.map((cat) => ({
          label: cat.identifier,
          value: cat.identifier,
        })),
      },
    ];

    const addFields = [
      {
        name: "identifier",
        label: "Product Name",
      },
      ...commonProductFields,
    ];

    const editFields = [
      {
        name: "identifier",
        label: "Product Name",
        disabled: true,
      },
      ...commonProductFields,
    ];

    return (
      <AccessGuard requiredPath="/product">
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

          onAdd={() =>
            setNewProduct({
            identifier: "",
            supplierID: "",
            brand: "",
            unit: "",
            categories: [],
          })
          }
          addButtonText="+ Add Product"
          newItem={newProduct}
          setNewItem={setNewProduct}
          handleAdd={handleAddProduct}
          addFields={addFields}

          editItem={editProduct}
          setEditItem={setEditProduct}
          handleUpdate={handleUpdate}
          editFields={editFields}

          viewItem={viewProduct}
          setViewItem={setViewProduct}

          actions={actions}
          emptyMessage="No products found"
        />
      </AccessGuard>  
    );
  };

  export default ProductPage;