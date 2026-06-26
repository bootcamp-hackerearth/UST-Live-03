"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import { listItems, deleteItem, updateItem, addItem } from "@/services/api";

const WarehouseList = () => {
  const [warehouses, setWarehouses] = useState([]);

  const [search, setSearch] = useState("");

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editWarehouse, setEditWarehouse] = useState(null);

  const [auditWarehouse, setAuditWarehouse] = useState(null);

  const sizePerPage = 5;

  const fetchWarehouses = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("warehouse", {
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

      setWarehouses(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
      );
    } catch (err) {
      console.error(err);

      setError("Failed to load warehouses");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWarehouses();
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(`Delete warehouse ${identifier}?`);

    if (!confirmDelete) {
      return;
    }

    try {
      await deleteItem("warehouse", identifier, "identifier");

      fetchWarehouses();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editWarehouse,
        pincode: Number(editWarehouse.pincode),
      };

      delete payload.createdBy;
      delete payload.createdOn;
      delete payload.updatedBy;
      delete payload.updatedOn;
      delete payload.modifiedBy;
      delete payload.modifiedOn;

      if (editWarehouse.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("warehouse", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("warehouse", payload);
      }

      await fetchWarehouses();

      setEditWarehouse(null);
    } catch (err) {
      console.error(err);

      alert(
        editWarehouse?.isNew
          ? "Failed to add warehouse"
          : "Failed to update warehouse",
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
      label: "Country",
      key: "country",
    },
    {
      label: "Address",
      key: "address",
    },
    {
      label: "Pincode",
      key: "pincode",
    },
  ];

  const actions = [
    {
      label: "📋 Audit Details",
      onClick: (row) => setAuditWarehouse(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditWarehouse({
          ...row,
          isNew: false,
          formTitle: "Edit Warehouse",
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
      disabled: !editWarehouse?.isNew,
    },
    {
      name: "country",
      label: "Country",
    },
    {
      name: "address",
      label: "Address",
    },
    {
      name: "pincode",
      label: "Pincode",
      type: "number",
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
            setEditWarehouse({
              id: "",
              identifier: "",
              country: "",
              address: "",
              pincode: "",
              isNew: true,
              formTitle: "Add Warehouse",
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
          + Add Warehouse
        </button>
      </div>

      <CommonList
        title="Warehouses"
        data={warehouses}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        sizePerPage={sizePerPage}
        columns={columns}
        actions={actions}
        editItem={editWarehouse}
        setEditItem={setEditWarehouse}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editWarehouse?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No warehouses found"
      />
      {auditWarehouse && (
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
                <strong>Warehouse:</strong> {auditWarehouse.identifier}
              </div>

              <div>
                <strong>Created By:</strong> {auditWarehouse.createdBy || "N/A"}
              </div>

              <div>
                <strong>Created On:</strong>{" "}
                {auditWarehouse.createdOn
                  ? new Date(auditWarehouse.createdOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Modified By:</strong>{" "}
                {auditWarehouse.modifiedBy || auditWarehouse.updatedBy || "N/A"}
              </div>

              <div>
                <strong>Modified On:</strong>{" "}
                {auditWarehouse.modifiedOn || auditWarehouse.updatedOn
                  ? new Date(
                      auditWarehouse.modifiedOn || auditWarehouse.updatedOn,
                    ).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Country:</strong> {auditWarehouse.country || "N/A"}
              </div>

              <div>
                <strong>Address:</strong> {auditWarehouse.address || "N/A"}
              </div>

              <div>
                <strong>Pincode:</strong> {auditWarehouse.pincode || "N/A"}
              </div>
            </div>

            <div
              className="modalActions"
              style={{
                marginTop: "20px",
              }}
            >
              <button onClick={() => setAuditWarehouse(null)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default WarehouseList;
