"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import {
  deleteItem,
  updateItem,
  addItem,
  getListItems,
  listItems,
} from "@/services/api";

const NodeList = () => {
  const [nodes, setNodes] = useState([]);
  const [roles, setRoles] = useState([]);
  const [message, setMessage] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [viewNode, setViewNode] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const sizePerPage = 5;

  // ================= ADD =================
  const [newNode, setNewNode] = useState({
    identifier: "",
    path: "",
    roles: [],
  });

  // ================= EDIT =================
  const [editNode, setEditNode] = useState(null);

  // ================= FETCH NODES =================
  const fetchNodes = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("node", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = Array.isArray(res?.content)
        ? res.content
        : [];

      setNodes(data);

      const pages =
        res?.totalPages ??
        Math.max(
          1,
          Math.ceil(
            (res?.totalElements ?? data.length) /
              sizePerPage
          )
        );

      setTotalPages(pages);
    } catch (err) {
      console.error("FETCH NODES ERROR:", err);
      setError("Failed to load nodes");
    } finally {
      setLoading(false);
    }
  };

  // ================= FETCH ROLES =================
  const fetchRoles = async () => {
    try {
      const res = await getListItems("role");

      console.log("ROLES API:", res);

      const data = Array.isArray(res)
        ? res
        : [];

      setRoles(data);
    } catch (err) {
      console.error(err);
      setRoles([]);
    }
  };

  // ================= INITIAL LOAD =================
  useEffect(() => {
    console.log("fetchNodes effect");
    fetchNodes();
  }, [page , searchTerm]);

  useEffect(() => {
     console.log("fetchRoles effect");
    fetchRoles();
  }, []);

  // ================= ADD =================
  const handleAddNode = async () => {
  try {
    const res = await addItem("node", newNode);

    if (res?.success === false) {
      setMessage(res.message);
      return false;
    }

    setMessage("Node added successfully");

    setNewNode({
      identifier: "",
      path: "",
      roles: [],
    });

    await fetchNodes();

    return true;
  } catch (err) {
    setMessage(
      err?.response?.data?.message ||
      err?.message ||
      "Add failed"
    );

    return false;
  }
};

  // ================= UPDATE =================
  const handleUpdate = async () => {
    try {
      await updateItem("node", editNode);

      await fetchNodes();

      setEditNode(null);
      setMessage("");
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
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
        "node",
        identifier
      );

      await fetchNodes();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= COLUMNS =================
  const columns = [
   {
      label: "ID",
      render: (row, index) =>
        page * sizePerPage + index + 1,
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

      render: (node) => {
        if (!node.roles) return "-";

        if (
          Array.isArray(node.roles) &&
          typeof node.roles[0] ===
            "string"
        ) {
          return node.roles.join(", ");
        }

        if (
          Array.isArray(node.roles) &&
          typeof node.roles[0] ===
            "object"
        ) {
          return node.roles
            .map(
              (r) =>
                r.name ||
                r.identifier
            )
            .join(", ");
        }

        return "-";
      },
    },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "👁️",
      onClick: (row) => setViewNode(row),
    },
    {
      label: "✏️",

      onClick: (row) =>
        setEditNode({
          ...row,
          roles: row.roles || [],
        }),
    },

    {
      label: "🗑",

      onClick: (row) =>
        handleDelete(
          row.identifier
        ),
    },
  ];

  // ================= ROLE OPTIONS =================
  const roleOptions = roles.map(
    (role) => ({
      label: role.identifier,
      value: role.identifier,
    })
  );

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      required: true,
    },

    {
      name: "path",
      label: "Path",
      type: "text",
    },

    {
      name: "roles",
      label: "Roles",
      type: "select",
      multiple: true,
      options: roleOptions,
    },
  ];

  // ================= EDIT FIELDS =================
  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      disabled: true,
    },

    {
      name: "path",
      label: "Path",
      type: "text",
    },

    {
      name: "roles",
      label: "Roles",
      type: "select",
      multiple: true,
      options: roleOptions,
    },
  ];

  return (
    <>
    <CommonList
      title="Nodes"
      data={nodes}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
       message={message}
  setMessage={setMessage}
      // ADD
      onAdd={() => {}}
      addButtonText="+ Add Node"
      newItem={newNode}
      setNewItem={setNewNode}
      handleAdd={handleAddNode}
      addFields={addFields}

      // EDIT
      editItem={editNode}
      setEditItem={setEditNode}
      handleUpdate={handleUpdate}
      editFields={editFields}

      // ACTIONS
      actions={actions}

      emptyMessage="No nodes found"
    />
    {viewNode && (
      <div className="modalOverlay">
        <div className="viewModal">
          <div className="modalHeader">
            <h3>Node Details</h3>

            <button
              className="closeBtn"
              onClick={() =>
                setViewNode(null)
              }
            >
              ✕
            </button>
          </div>

          <div className="viewContent">
            <div className="viewRow">
              <span>Name</span>
              <strong>
                {viewNode.identifier}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created By</span>
              <strong>
                {viewNode.createdBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created On</span>
              <strong>
                {viewNode.createdOn ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified By</span>
              <strong>
                {viewNode.modifiedBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified On</span>
              <strong>
                {viewNode.modifiedOn ||
                  "-"}
              </strong>
            </div>
          </div>
        </div>
      </div>
    )}
    </>
  );
};

export default NodeList;