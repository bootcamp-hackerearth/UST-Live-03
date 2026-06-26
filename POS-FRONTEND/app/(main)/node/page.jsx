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

  const [addError, setAddError] = useState("");

  const [searchTerm, setSearchTerm] = useState("");

  const [viewItem, setViewItem] = useState(null);

  const sizePerPage = 5;

  // ADD
  const [newNode, setNewNode] = useState({
    identifier: "",
    path: "",
    roles: [],
  });

  // EDIT
  const [editNode, setEditNode] = useState(null);

  // FETCH ROLES
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

  // FETCH NODES
  const fetchNodes = async () => {
    try {
      setLoading(true);
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
  }, [page, searchTerm]);

  // ADD NODE
  const handleAddNode = async () => {
    try {
      setAddError("");

      const response = await addItem(
        "node",
        newNode
      );

      if (response?.success === false) {
        setAddError(response.message);
        return false;
      }

      setNewNode({
        identifier: "",
        path: "",
        roles: [],
      });

      await fetchNodes();
      return true;
    } catch (err) {
      console.error(err);
      setAddError("Add failed");
    }
    return false;
  };

  // UPDATE
  const handleUpdate = async () => {
    try {
      await updateItem("node", editNode);
      await fetchNodes();
      setEditNode(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // DELETE
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

  // COLUMNS
  const columns = [
    {
      key: "serialNo",
      label: "S.No",
      render: (row, rowIndex) =>
        page * sizePerPage + rowIndex + 1,
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

  // ACTIONS
  const actions = [
    {
      label: "View 👁️",
      onClick: (row) => setViewItem(row),
    },
    {
      label: "Edit ✏️",
      onClick: (row) => setEditNode(row),
    },
    {
      label: "Delete 🗑",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  // ADD FIELDS
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      required: true,
    },
    {
      name: "path",
      label: "Path",
       required: true,
    },
    {
      name: "roles",
      label: "Roles",
      type: "multiselect",
      options: roles.map((r) => ({
        label: r.identifier,
        value: r.identifier,
      })),
    },
  ];

  // EDIT FIELDS
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
      type: "multiselect",
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

      addError={addError}

      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}

      onAdd={() => {
        setAddError("");
      }}

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
      viewItem={viewItem}
      setViewItem={setViewItem}
      emptyMessage="No nodes found"

      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
    />
  );
};

export default NodePage;