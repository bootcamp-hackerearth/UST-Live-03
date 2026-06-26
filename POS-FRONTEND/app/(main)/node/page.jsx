"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList/CommonList";
import AccessGuard from "@/app/components/AccessGuard";

import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
  getAllItems,
} from "@/services/api";

const NodePage = () => {
  const [nodes, setNodes] =
    useState([]);

  const [roles, setRoles] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [newNode, setNewNode] =
    useState({
      identifier: "",
      path: "",
      roles: [],
    });

  const [editNode, setEditNode] = useState(null);
  const [viewNode, setViewNode] = useState(null);

  const fetchNodes = async () => {
    try {
      if (nodes.length === 0) {
        setLoading(true);
      }

      const res = await listItems(
        "node",
        {
          page,
          sizePerPage,
          sortField: "id",
          search: searchTerm,
        }
      );

      setNodes(res?.content || []);
      setTotalPages(res?.totalPages || 1);

    } catch (err) {

      console.error(err);
      setError("Failed to load nodes");

    } finally {
      setLoading(false);
    }
  };

  const fetchRoles = async () => {
    try {
      const res =
        await getAllItems("role");

      setRoles(res || []);
    } catch (err) {
      console.error("Failed to load roles", err);
    }
  };

  useEffect(() => {
    fetchNodes();
  }, [page, searchTerm]);

  useEffect(() => {
    fetchRoles();
  }, []);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddNode = async () => {
    const response = await addItem(
      "node",
      newNode
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    setNewNode({
      identifier: "",
      path: "",
      roles: [],
    });

    fetchNodes();

    globalThis.dispatchEvent(
      new Event("menuUpdated")
    );

    return true;
  };

  const handleUpdate = async () => {
    const response = await updateItem(
      "node",
      editNode
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    fetchNodes();

    globalThis.dispatchEvent(
      new Event("menuUpdated")
    );

    return true;
  };

  const handleDelete =
    async (identifier) => {
      if (
        !globalThis.confirm(
          `Delete ${identifier}?`
        )
      )
        return;

      try {
        await deleteItem("node", identifier);

        fetchNodes();

        globalThis.dispatchEvent(
          new Event("menuUpdated")
        );
      } catch (err) {
        console.error(err);

        alert("Delete failed");
      }
    };

  const roleOptions =
    roles.map((role) => ({
      value: role.identifier,
      label: role.identifier,
    }));

  const columns = [
    {
      label: "SL NO",
      render: (row, index) =>
        page * sizePerPage +
        index +
        1,
    },

    {
      label: "Node",
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
      label: "👁 View",
      type: "view",
      onClick: (row) =>
        setViewNode(row),
    },

    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditNode(row),
    },

    {
      label: "🗑 Delete",
      type: "delete",

      onClick: (row) =>
        handleDelete(
          row.identifier
        ),
    },
  ];

  return (
    <AccessGuard requiredPath="/node">
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
        actions={actions}
        emptyMessage="No nodes found"

        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}

        onAdd={() => { }}
        addButtonText="+ Add Node"

        newItem={newNode}
        setNewItem={setNewNode}
        handleAdd={handleAddNode}

        addFields={[
          {
            name: "identifier",
            label: "Node Name",
          },

          {
            name: "path",
            label: "Node Path",
          },

          {
            name: "roles",
            label: "Roles",
            type: "select",
            multiple: true,
            options: roleOptions,
          },
        ]}

        editItem={editNode}
        setEditItem={setEditNode}
        handleUpdate={handleUpdate}

        viewItem={viewNode}
        setViewItem={setViewNode}

        viewFields={[
          {
            name: "identifier",
            label: "Node Name",
          },

          {
            name: "path",
            label: "Node Path",
          },

          {
            name: "roles",
            label: "Roles",
          },
        ]}

        editFields={[
          {
            name: "identifier",
            label: "Node Name",
            disabled: true,
          },

          {
            name: "path",
            label: "Node Path",
          },

          {
            name: "roles",
            label: "Roles",
            type: "select",
            multiple: true,
            options: roleOptions,
          },
        ]}
      />
    </AccessGuard>
  );
};

export default NodePage;