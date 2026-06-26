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

const NodeList = () => {
  const [nodes, setNodes] = useState([]);
  const [search, setSearch] = useState("");
  const [roles, setRoles] = useState([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editNode, setEditNode] = useState(null);

  const [auditNode, setAuditNode] = useState(null);

  const sizePerPage = 5;

  const fetchNodes = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("node", {
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

      setNodes(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load nodes");
    } finally {
      setLoading(false);
    }
  };

  const fetchRoles = async () => {
    try {
      const response = await getListItems("role");

      const data = Array.isArray(response) ? response : response?.content || [];

      setRoles(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  useEffect(() => {
    fetchNodes();
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(`Delete node ${identifier}?`);

    if (!confirmDelete) return;

    try {
      await deleteItem("node", identifier, "identifier");

      fetchNodes();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editNode,
      };

      delete payload.createdBy;
      delete payload.createdOn;
      delete payload.modifiedBy;
      delete payload.modifiedOn;

      if (editNode.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("node", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("node", payload);
      }

      await fetchNodes();
      setEditNode(null);
    } catch (err) {
      console.error(err);

      alert(editNode?.isNew ? "Failed to add node" : "Failed to update node");
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
      label: "Path",
      key: "path",
    },
    {
      label: "Roles",
      render: (row) =>
        Array.isArray(row.roles)
          ? row.roles.map((role) => role.identifier || role).join(", ")
          : "-",
    },
  ];
  const actions = [
    {
      label: "📋 Audit Details",
      onClick: (row) => setAuditNode(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditNode({
          ...row,
          roles: row.roles?.map((r) => r.identifier || r) || [],
          isNew: false,
          formTitle: "Edit Node",
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
      disabled: !editNode?.isNew,
    },
    {
      name: "path",
      label: "Path",
    },
    {
      name: "roles",
      label: "Roles",
      type: "select",
      multiple: true,
      options: roles.map((role) => ({
        label: role.identifier,
        value: role.identifier,
      })),
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
            setEditNode({
              id: "",
              identifier: "",
              path: "",
              roles: [],
              status: true,
              isNew: true,
              formTitle: "Add Node",
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
          + Add Node
        </button>
      </div>

      <CommonList
        title="Nodes"
        data={nodes}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        sizePerPage={sizePerPage}
        columns={columns}
        actions={actions}
        editItem={editNode}
        setEditItem={setEditNode}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editNode?.formTitle}
        emptyMessage="No nodes found"
        search={search}
        setSearch={setSearch}
      />

      {auditNode && (
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
                <strong>Identifier:</strong> {auditNode.identifier}
              </div>

              <div>
                <strong>Path:</strong> {auditNode.path || "N/A"}
              </div>

              <div>
                <strong>Roles:</strong>{" "}
                {Array.isArray(auditNode.roles)
                  ? auditNode.roles
                      .map((role) => role.identifier || role)
                      .join(", ")
                  : "N/A"}
              </div>

              <div>
                <strong>Created By:</strong> {auditNode.createdBy || "N/A"}
              </div>

              <div>
                <strong>Created On:</strong>{" "}
                {auditNode.createdOn
                  ? new Date(auditNode.createdOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Modified By:</strong> {auditNode.modifiedBy || "N/A"}
              </div>

              <div>
                <strong>Modified On:</strong>{" "}
                {auditNode.modifiedOn
                  ? new Date(auditNode.modifiedOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Status:</strong>{" "}
                {auditNode.status ? "Active" : "Inactive"}
              </div>
            </div>

            <div
              className="modalActions"
              style={{
                marginTop: "20px",
              }}
            >
              <button onClick={() => setAuditNode(null)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default NodeList;
