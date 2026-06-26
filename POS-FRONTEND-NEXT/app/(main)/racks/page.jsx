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

const RacksList = () => {
  const [racks, setRacks] = useState([]);
  const [shelves, setShelves] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editRack, setEditRack] = useState(null);

  const [auditRack, setAuditRack] = useState(null);

  const sizePerPage = 5;

  const formatAuditDate = (value) => {
    if (!value) return "-";

    try {
      return new Date(value).toLocaleString();
    } catch {
      return value;
    }
  };

  const fetchRacks = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("racks", {
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

      setRacks(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load racks");
    } finally {
      setLoading(false);
    }
  };

  const fetchShelves = async () => {
    try {
      const response = await getListItems("shelf");

      const data = Array.isArray(response) ? response : response?.content || [];

      setShelves(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchShelves();
  }, []);

  useEffect(() => {
    fetchRacks();
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmed = globalThis.confirm(`Delete rack ${identifier}?`);

    if (!confirmed) return;

    try {
      await deleteItem("racks", identifier, "identifier");

      fetchRacks();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editRack,
        shelves: editRack.shelves || [],
      };

      if (editRack.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("racks", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("racks", payload);
      }

      await fetchRacks();

      setEditRack(null);
    } catch (err) {
      console.error(err);

      alert(editRack?.isNew ? "Failed to add rack" : "Failed to update rack");
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
      label: "Shelves",
      render: (row) =>
        Array.isArray(row.shelves) ? row.shelves.join(", ") : "-",
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
      label: "📋 Audit",
      onClick: (row) => setAuditRack(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditRack({
          ...row,
          shelves: row.shelves || [],
          isNew: false,
          formTitle: "Edit Rack",
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
      disabled: !editRack?.isNew,
    },
    {
      name: "shelves",
      label: "Shelves",
      type: "select",
      multiple: true,
      options: shelves.map((shelf) => ({
        label: shelf.identifier,
        value: shelf.identifier,
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
            setEditRack({
              id: "",
              identifier: "",
              shelves: [],
              status: true,
              isNew: true,
              formTitle: "Add Rack",
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
          + Add Rack
        </button>
      </div>

      <CommonList
        title="Racks"
        data={racks}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        columns={columns}
        actions={actions}
        editItem={editRack}
        setEditItem={setEditRack}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editRack?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No racks found"
      />

      {auditRack && (
        <div
          style={{
            position: "fixed",
            inset: 0,
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
              maxWidth: "600px",
              boxShadow: "0 8px 25px rgba(0,0,0,0.2)",
            }}
          >
            <h2
              style={{
                marginTop: 0,
                marginBottom: "20px",
              }}
            >
              Rack Audit Details
            </h2>

            <div
              style={{
                display: "grid",
                gridTemplateColumns: "150px 1fr",
                rowGap: "12px",
              }}
            >
              <strong>Identifier</strong>
              <span>{auditRack.identifier}</span>

              <strong>Created By</strong>
              <span>{auditRack.createdBy || "-"}</span>

              <strong>Created On</strong>
              <span>{formatAuditDate(auditRack.createdOn)}</span>

              <strong>Modified By</strong>
              <span>{auditRack.modifiedBy || "-"}</span>

              <strong>Modified On</strong>
              <span>{formatAuditDate(auditRack.modifiedOn)}</span>

              <strong>Status</strong>
              <span>{auditRack.status ? "Active" : "Inactive"}</span>
            </div>

            <div
              style={{
                display: "flex",
                justifyContent: "flex-end",
                marginTop: "24px",
              }}
            >
              <button
                onClick={() => setAuditRack(null)}
                style={{
                  padding: "8px 16px",
                  border: "none",
                  borderRadius: "6px",
                  background: "#1976d2",
                  color: "#fff",
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

export default RacksList;
