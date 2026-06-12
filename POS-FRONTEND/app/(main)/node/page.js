"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  addItem,
  getListItems,
} from "@/services/api";

const NodePage = () => {
  const [nodes, setNodes] = useState([]);
  const [roles, setRoles] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");

  const sizePerPage = 5;

  const [newNode, setNewNode] = useState({
    identifier: "",
    path: "",
    roles: [],
  });

  const [editNode, setEditNode] = useState(null);

  const fetchRoles = async () => {
    try {
      const res = await getListItems("role");
      setRoles(res || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, []);

  const fetchNodes = async () => {
    try {
      setError("");

      const res = await listItems("node", {
        page,
        sizePerPage,
        sortField: "identifier",
        search: searchTerm,
      });

      const data = res?.content || [];

      setNodes(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalElements || data.length) /
              sizePerPage
          ) ||
          1
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load nodes");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNodes();
  }, [page,searchTerm]);

 const handleAddNode = async () => {
  const exists = nodes.some(
    (node) =>
      node.identifier?.trim().toLowerCase() ===
      newNode.identifier?.trim().toLowerCase()
  );

  if (exists) {
    alert(`${newNode.identifier} already exists`);
    return;
  }

  try {
    await addItem("node", newNode);

    setNewNode({
      identifier: "",
      path: "",
      roles: [],
    });

    fetchNodes();
  } catch (err) {
    console.error(err);
    alert("Add failed");
  }
};

  const handleUpdate = async () => {
    try {
      await updateItem("node", editNode);

      setNodes((prev) =>
        prev.map((n) =>
          n.identifier === editNode.identifier
            ? editNode
            : n
        )
      );

      setEditNode(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(
      `Delete ${identifier}?`
    );

    if (!confirmDelete) return;

    try {
      await deleteItem("node", identifier);

      setNodes((prev) =>
        prev.filter(
          (n) => n.identifier !== identifier
        )
      );
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

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
      render: (row) =>
        Array.isArray(row.roles)
          ? row.roles.join(", ")
          : row.roles || "-",
    },
  ];

  const actions = [
    {
      label: "✏️ Edit",
      onClick: (row) => setEditNode(row),
    },
    {
      label: "🗑 Delete",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
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
      options: roles.map((r) => ({
        label: r.identifier,
        value: r.identifier,
      })),
    },
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled: true,
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
      options: roles.map((r) => ({
        label: r.identifier,
        value: r.identifier,
      })),
    },
  ];

  return (
    <CommonList
      title="Nodes"
      data={nodes}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      onAdd={() => {}}
      addButtonText="+ Add Node"
      newItem={newNode}
      setNewItem={setNewNode}
      handleAdd={handleAddNode}
      addFields={addFields}
      editItem={editNode}
      setEditItem={setEditNode}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      emptyMessage="No nodes found"
    />
  );
};

export default NodePage;