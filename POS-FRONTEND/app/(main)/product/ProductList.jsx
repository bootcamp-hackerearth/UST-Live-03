"use client";
import { useState, useEffect } from "react";
import { FiLayers } from "react-icons/fi";
import ProductRegistration from "./ProductRegistration";
import ProductEdit from "./ProductEdit.jsx";
import CommonList from "@/component/CommonList";
import api from "../api/axios";

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [sizePerPage] = useState(5);
  const [totalPage, setTotalPage] = useState(0);
  const [search, setSearch] = useState("");

  const productColumns = [
    { header: "Identifier", key: "identifier" },
    { header: "Category", key: "category" },
    { header: "Warehouse", key: "warehouseName" },
    { header: "Unit", key: "unit" },
    { header: "Supplier ID", key: "supplierId" },
    { header: "Status", key: "status" },
  ];

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const response = await api.post("/api/product/list", {
        page,
        sizePerPage,
        sortDirection: "ASC",
        sortField: "identifier",
        search,
      });

      const data = response.data.dtoList || [];
      setProducts(data);
      setTotalPage(response.data.totalPage);
    } catch (err) {
      console.error("Failed to fetch products", err);
      setProducts([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, [page, search]);

  const handleStatusToggle = async (item) => {
    const originalStatus = item.status;
    const newStatus = !originalStatus;

    setProducts((prev) =>
      prev.map((p) => (p.id === item.id ? { ...p, status: newStatus } : p)),
    );

    try {
      await api.put("/api/product/update", {
        ...item,
        status: newStatus,
      });
    } catch (err) {
      console.error(err);

      setProducts((prev) =>
        prev.map((p) =>
          p.id === item.id ? { ...p, status: originalStatus } : p,
        ),
      );
    }
  };
  const renderStatusCell = (item) => (
    <label className="relative inline-flex items-center cursor-pointer">
      <input
        type="checkbox"
        className="sr-only peer"
        checked={item.status}
        onChange={() => handleStatusToggle(item)}
      />

      <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600" />

      <span
        className={`ml-3 w-24 text-sm font-medium uppercase ${
          item.status ? "text-green-600" : "text-red-600"
        }`}
      >
        {item.status ? "IN-STOCK" : "OUT-STOCK"}
      </span>
    </label>
  );

  return (
    <div className="w-full">
      <CommonList
        title="Product Inventory"
        icon={FiLayers}
        columns={productColumns}
        data={products}
        loading={loading}
        searchTerm={search}
        setSearchTerm={(value) => {
          setPage(0);
          setSearch(value);
        }}
        renderCustomCell={(key, item) => {
          if (key === "status") {
            return renderStatusCell(item);
          }

          return item[key];
        }}
        AddComponent={ProductRegistration}
        EditComponent={ProductEdit}
        editPropName="product"
        deleteApi={(identifier) =>
          api.delete("/api/product/delete", {
            params: { identifier },
          })
        }
        deleteIdentifierField="identifier"
        pagination={{
          page,
          totalPage,
          setPage,
        }}
        refreshData={fetchProducts}
      />
    </div>
  );
};

export default ProductList;
