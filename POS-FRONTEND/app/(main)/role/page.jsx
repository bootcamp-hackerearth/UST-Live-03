"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList/CommonList";
import AccessGuard from "@/app/components/AccessGuard";

import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
} from "@/services/api";

const RolePage = () => {
  const [roles, setRoles] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(1);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [newRole, setNewRole] =
    useState({
      identifier: "",
      description: "",
    });

  const [editRole, setEditRole] = useState(null);
  const [viewRole, setViewRole] = useState(null);

  const fetchRoles = async () => {
    try {
      if (roles.length === 0) {
        setLoading(true);
      }
      setError("");

      const res = await listItems(
        "role",
        {
          page,
          sizePerPage,
          sortField: "id",
          search: searchTerm,
        }
      );

      setRoles(res?.content || []);
      setTotalPages(res?.totalPages || 1);

    } catch (err) {

      console.error(err);
      setError("Failed to load roles");

    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, [page, searchTerm]);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddRole = async () => {
    const response = await addItem("role", newRole);

    if (response?.success === false) {
      throw new Error(response.message);
    }

    setNewRole({
      identifier: "",
      description: "",
    });

    fetchRoles();

    return true;
  };

  const handleDelete = async (
    identifier
  ) => {
    const confirmDelete =
      globalThis.confirm(
        `Delete ${identifier}?`
      );

    if (!confirmDelete) return;

    try {
      await deleteItem("role", identifier);
      fetchRoles();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    const response = await updateItem("role", editRole);

    if (response?.success === false) {
      throw new Error(response.message);
    }

    fetchRoles();
    return true;
  };

  const columns = [
    {
      label: "SL NO",
      render: (
        row,
        index
      ) =>
        page *
        sizePerPage +
        index +
        1,
    },

    {
      label: "Role",
      key: "identifier",
    },

    {
      label: "Description",
      key: "description",
    },
  ];

  const actions = [
    {
      label: "👁 View",
      type: "view",
      onClick: (role) =>
        setViewRole(role),
    },

    {
      label: "✏️ Edit",
      onClick: (role) =>
        setEditRole(role),
    },

    {
      label: "🗑 Delete",
      type: "delete",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  return (
    <AccessGuard requiredPath="/role">
      <CommonList
        title="Roles"
        data={roles}
        columns={columns}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        sizePerPage={sizePerPage}
        totalPages={totalPages}
        actions={actions}
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
        emptyMessage="No roles found"

        onAdd={() => { }}
        addButtonText="+ Add Role"

        addFields={[
          {
            name: "identifier",
            label: "Role Name",
          },

          {
            name: "description",
            label:
              "Role Description",
          },
        ]}

        newItem={newRole}
        setNewItem={setNewRole}
        handleAdd={handleAddRole}

        editItem={editRole}
        setEditItem={setEditRole}
        handleUpdate={handleUpdate}

        viewItem={viewRole}
        setViewItem={setViewRole}

        editFields={[
          {
            name: "identifier",
            label: "Role Name",
            disabled: true,
          },

          {
            name: "description",
            label:
              "Role Description",
          },
        ]}
      />
    </AccessGuard>
  );
};

export default RolePage;