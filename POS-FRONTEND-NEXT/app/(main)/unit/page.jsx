"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import { listItems, deleteItem, updateItem, addItem } from "@/services/api";

const UnitList = () => {
  const [units, setUnits] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [editUnit, setEditUnit] = useState(null);

  const [auditData, setAuditData] = useState(null);

  const sizePerPage = 5;

  const fetchUnits = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("unit", {
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

      setUnits(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load units");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUnits();
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(`Delete unit ${identifier}?`);

    if (!confirmDelete) {
      return;
    }

    try {
      await deleteItem("unit", identifier, "identifier");

      fetchUnits();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editUnit,
      };

      if (editUnit.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("unit", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("unit", payload);
      }

      await fetchUnits();
      setEditUnit(null);
    } catch (err) {
      console.error(err);

      alert(editUnit?.isNew ? "Failed to add unit" : "Failed to update unit");
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
  ];

  const actions = [
    {
      label: "📋 Audit",
      onClick: (row) => setAuditData(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditUnit({
          ...row,
          isNew: false,
          formTitle: "Edit Unit",
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
      disabled: !editUnit?.isNew,
    },
    {
      name: "description",
      label: "Description",
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
      <div
        style={{
          display: "flex",
          justifyContent: "flex-end",
          marginBottom: "16px",
        }}
      >
        <button
          onClick={() =>
            setEditUnit({
              id: "",
              identifier: "",
              description: "",
              createdBy: "",
              createdOn: "",
              modifiedBy: "",
              modifiedOn: "",
              isNew: true,
              formTitle: "Add Unit",
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
          + Add Unit
        </button>
      </div>

      <CommonList
        title="Units"
        data={units}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        sizePerPage={sizePerPage}
        columns={columns}
        actions={actions}
        editItem={editUnit}
        setEditItem={setEditUnit}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editUnit?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No units found"
      />

      {auditData && (
        <div
          style={{
            position: "fixed",
            top: 0,
            left: 0,
            width: "100%",
            height: "100%",
            background: "rgba(0,0,0,0.5)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 9999,
          }}
        >
          <div
            style={{
              background: "#fff",
              padding: "24px",
              borderRadius: "10px",
              minWidth: "450px",
              boxShadow: "0 4px 20px rgba(0,0,0,0.2)",
            }}
          >
            <h3
              style={{
                marginTop: 0,
                marginBottom: "20px",
              }}
            >
              Audit Details
            </h3>

            <div
              style={{
                display: "grid",
                gridTemplateColumns: "140px 1fr",
                rowGap: "12px",
              }}
            >
              <strong>Created By:</strong>
              <span>{auditData.createdBy || "-"}</span>

              <strong>Created On:</strong>
              <span>{auditData.createdOn || "-"}</span>

              <strong>Modified By:</strong>
              <span>{auditData.modifiedBy || "-"}</span>

              <strong>Modified On:</strong>
              <span>{auditData.modifiedOn || "-"}</span>
            </div>

            <div
              style={{
                marginTop: "20px",
                textAlign: "right",
              }}
            >
              <button
                onClick={() => setAuditData(null)}
                style={{
                  background: "#1976d2",
                  color: "#fff",
                  border: "none",
                  padding: "8px 16px",
                  borderRadius: "6px",
                  cursor: "pointer",
                }}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default UnitList;
