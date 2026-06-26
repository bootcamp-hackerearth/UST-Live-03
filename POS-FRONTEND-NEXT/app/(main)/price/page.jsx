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

const PriceList = () => {
  const [prices, setPrices] = useState([]);

  const [products, setProducts] = useState([]);

  const [search, setSearch] = useState("");

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editPrice, setEditPrice] = useState(null);

  const [auditPrice, setAuditPrice] = useState(null);

  const sizePerPage = 5;

  const addButtonContainerStyle = {
    display: "flex",
    justifyContent: "flex-end",
    marginBottom: "16px",
  };

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
      mrp: "",
      difference: "",
      status: true,
      isNew: true,
      formTitle: "Add Price",
    });

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const res = await getListItems("product");

        const data = Array.isArray(res) ? res : res?.content || [];

        setProducts(data);
      } catch (err) {
        console.error("Failed to load products", err);

        setProducts([]);
      }
    };

    fetchProducts();
  }, []);

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

      const data = Array.isArray(res) ? res : res?.content || [];

      setPrices(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
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

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(`Delete price ${identifier}?`);

    if (!confirmDelete) return;

    try {
      await deleteItem("price", identifier, "identifier");

      fetchPrices();
    } catch (err) {
      console.error(err);

      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editPrice,
        costPrice: Number(editPrice.costPrice),
        sellingPrice: Number(editPrice.sellingPrice),
        mrp: Number(editPrice.mrp),
      };

      delete payload.isNew;
      delete payload.formTitle;

      delete payload.createdBy;
      delete payload.createdOn;
      delete payload.modifiedBy;
      delete payload.modifiedOn;

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
        editPrice?.isNew ? "Failed to add price" : "Failed to update price",
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
      label: "Cost Price",
      key: "costPrice",
    },
    {
      label: "Selling Price",
      key: "sellingPrice",
    },
    {
      label: "MRP",
      key: "mrp",
    },
    {
      label: "Difference",
      key: "difference",
    },
  ];

  const actions = [
    {
      label: "📋 Audit Details",
      onClick: (row) => setAuditPrice(row),
    },
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
      name: "mrp",
      label: "MRP",
      type: "number",
    },
    {
      name: "difference",
      label: "Difference",
      disabled: true,
    },
    {
      name: "createdBy",
      label: "Created By",
      disabled: true,
    },
    {
      name: "createdOn",
      label: "Created On",
      disabled: true,
    },
    {
      name: "modifiedBy",
      label: "Modified By",
      disabled: true,
    },
    {
      name: "modifiedOn",
      label: "Modified On",
      disabled: true,
    },
  ];

  return (
    <>
      <div style={addButtonContainerStyle}>
        <button onClick={handleAddPriceClick} style={addButtonStyle}>
          + Add Price
        </button>
      </div>

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

      {auditPrice && (
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
                <strong>Product:</strong> {auditPrice.identifier}
              </div>

              <div>
                <strong>Cost Price:</strong> {auditPrice.costPrice}
              </div>

              <div>
                <strong>Selling Price:</strong> {auditPrice.sellingPrice}
              </div>

              <div>
                <strong>MRP:</strong> {auditPrice.mrp}
              </div>

              <div>
                <strong>Difference:</strong> {auditPrice.difference}
              </div>

              <div>
                <strong>Created By:</strong> {auditPrice.createdBy || "N/A"}
              </div>

              <div>
                <strong>Created On:</strong>{" "}
                {auditPrice.createdOn
                  ? new Date(auditPrice.createdOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Modified By:</strong> {auditPrice.modifiedBy || "N/A"}
              </div>

              <div>
                <strong>Modified On:</strong>{" "}
                {auditPrice.modifiedOn
                  ? new Date(auditPrice.modifiedOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Status:</strong>{" "}
                {auditPrice.status ? "Active" : "Inactive"}
              </div>
            </div>

            <div
              className="modalActions"
              style={{
                marginTop: "20px",
              }}
            >
              <button onClick={() => setAuditPrice(null)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default PriceList;
