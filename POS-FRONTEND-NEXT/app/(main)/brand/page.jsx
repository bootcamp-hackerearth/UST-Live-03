"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import { listItems, deleteItem, updateItem, addItem } from "@/services/api";

const BrandList = () => {
  const [brands, setBrands] = useState([]);

  const [search, setSearch] = useState("");

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editBrand, setEditBrand] = useState(null);

  const [auditBrand, setAuditBrand] = useState(null);

  const sizePerPage = 5;

  const fetchBrands = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("brand", {
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

      setBrands(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
      );
    } catch (err) {
      console.error(err);

      setError("Failed to load brands");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBrands();
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmed = globalThis.confirm(`Delete brand ${identifier}?`);

    if (!confirmed) {
      return;
    }

    try {
      await deleteItem("brand", identifier, "identifier");

      fetchBrands();
    } catch (err) {
      console.error(err);

      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editBrand,
      };

      if (editBrand.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("brand", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("brand", payload);
      }

      await fetchBrands();

      setEditBrand(null);
    } catch (err) {
      console.error(err);

      alert(
        editBrand?.isNew ? "Failed to add brand" : "Failed to update brand",
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
      label: "Description",
      key: "description",
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
      label: "📋 Audit Details",
      onClick: (row) => setAuditBrand(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditBrand({
          ...row,
          isNew: false,
          formTitle: "Edit Brand",
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
      disabled: !editBrand?.isNew,
    },
    {
      name: "description",
      label: "Description",
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
            setEditBrand({
              id: "",
              identifier: "",
              description: "",
              status: true,
              isNew: true,
              formTitle: "Add Brand",
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
          + Add Brand
        </button>
      </div>

      <CommonList
        title="Brands"
        data={brands}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        columns={columns}
        actions={actions}
        editItem={editBrand}
        setEditItem={setEditBrand}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editBrand?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No brands found"
      />

      {auditBrand && (
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
                <strong>Brand:</strong> {auditBrand.identifier}
              </div>

              <div>
                <strong>Created By:</strong> {auditBrand.createdBy || "N/A"}
              </div>

              <div>
                <strong>Created On:</strong>{" "}
                {auditBrand.createdOn
                  ? new Date(auditBrand.createdOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Modified By:</strong> {auditBrand.modifiedBy || "N/A"}
              </div>

              <div>
                <strong>Modified On:</strong>{" "}
                {auditBrand.modifiedOn
                  ? new Date(auditBrand.modifiedOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Status:</strong>{" "}
                {auditBrand.status ? "Active" : "Inactive"}
              </div>
            </div>

            <div
              className="modalActions"
              style={{
                marginTop: "20px",
              }}
            >
              <button onClick={() => setAuditBrand(null)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default BrandList;
