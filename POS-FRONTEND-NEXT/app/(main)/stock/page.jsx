"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  addItem,
  getListItems,
} from "@/services/api";

const StockList = () => {
  const [stocks, setStocks] = useState([]);
  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);

  const [search, setSearch] = useState("");

  const [debouncedSearch, setDebouncedSearch] = useState("");

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editStock, setEditStock] = useState(null);

  const [auditStock, setAuditStock] = useState(null);

  const sizePerPage = 5;

  // ================= SEARCH DEBOUNCE =================

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 500);

    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    setPage(0);
  }, [debouncedSearch]);

  // ================= MASTER DATA =================

  const fetchProducts = async () => {
    try {
      const data = await getListItems("product");

      setProducts(Array.isArray(data) ? data : data?.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchWarehouses = async () => {
    try {
      const data = await getListItems("warehouse");

      setWarehouses(Array.isArray(data) ? data : data?.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchProducts();
    fetchWarehouses();
  }, []);

  // ================= STOCK LIST =================

  const fetchStocks = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("stock", {
        page,
        sizePerPage,
        sortField: "identifier",
        search: debouncedSearch,
      });

      let data = [];

      if (Array.isArray(res)) {
        data = res;
      } else if (Array.isArray(res?.content)) {
        data = res.content;
      }

      setStocks(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
      );
    } catch (err) {
      console.error(err);

      setError("Failed to load stock");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStocks();
  }, [page, debouncedSearch]);

  // ================= DELETE =================

  const handleDelete = async (identifier) => {
    const confirmed = globalThis.confirm(`Delete stock ${identifier}?`);

    if (!confirmed) {
      return;
    }

    try {
      await deleteItem("stock", identifier, "identifier");

      fetchStocks();
    } catch (err) {
      console.error(err);

      alert("Delete failed");
    }
  };

  // ================= UPDATE =================

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editStock,
        quantity: Number(editStock.quantity),
      };

      delete payload.createdBy;
      delete payload.createdOn;
      delete payload.updatedBy;
      delete payload.updatedOn;

      if (editStock.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("stock", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("stock", payload);
      }

      await fetchStocks();

      setEditStock(null);
    } catch (err) {
      console.error(err);

      alert(
        editStock?.isNew ? "Failed to add stock" : "Failed to update stock",
      );
    }
  };

  const columns = [
    {
      label: "ID",
      key: "id",
    },
    {
      label: "Product",
      key: "identifier",
    },
    {
      label: "Quantity",
      key: "quantity",
    },
    {
      label: "Status",
      key: "stockStatus",
    },
    {
      label: "Warehouse",
      key: "warehouseName",
    },
  ];

  const actions = [
    {
      label: "📋 Audit Details",
      onClick: (row) => setAuditStock(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditStock({
          ...row,
          isNew: false,
          formTitle: "Edit Stock",
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
      label: "Product",
      type: "select",
      disabled: !editStock?.isNew,
      options: products.map((product) => ({
        label: product.identifier,
        value: product.identifier,
      })),
    },
    {
      name: "quantity",
      label: "Quantity",
      type: "number",
    },
    {
      name: "stockStatus",
      label: "Stock Status",
      type: "select",
      options: [
        {
          label: "IN_STOCK",
          value: "IN_STOCK",
        },
        {
          label: "OUT_OF_STOCK",
          value: "OUT_OF_STOCK",
        },
      ],
    },
    {
      name: "warehouseName",
      label: "Warehouse",
      type: "select",
      options: warehouses.map((warehouse) => ({
        label: warehouse.identifier,
        value: warehouse.identifier,
      })),
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
            setEditStock({
              id: "",
              identifier: "",
              quantity: "",
              stockStatus: "IN_STOCK",
              warehouseName: "",
              isNew: true,
              formTitle: "Add Stock",
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
          + Add Stock
        </button>
      </div>

      <CommonList
        title="Stock"
        data={stocks}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        sizePerPage={sizePerPage}
        columns={columns}
        actions={actions}
        editItem={editStock}
        setEditItem={setEditStock}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editStock?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No stock found"
      />

      {auditStock && (
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
                <strong>Product:</strong> {auditStock.identifier}
              </div>

              <div>
                <strong>Created By:</strong> {auditStock.createdBy || "N/A"}
              </div>

              <div>
                <strong>Created On:</strong>{" "}
                {auditStock.createdOn
                  ? new Date(auditStock.createdOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Modified By:</strong>{" "}
                {auditStock.modifiedBy || auditStock.updatedBy || "N/A"}
              </div>

              <div>
                <strong>Modified On:</strong>{" "}
                {auditStock.modifiedOn || auditStock.updatedOn
                  ? new Date(
                      auditStock.modifiedOn || auditStock.updatedOn,
                    ).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Stock Status:</strong> {auditStock.stockStatus}
              </div>
            </div>

            <div
              className="modalActions"
              style={{
                marginTop: "20px",
              }}
            >
              <button onClick={() => setAuditStock(null)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default StockList;
