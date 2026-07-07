"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

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

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const sizePerPage = 5;

  const [newRole, setNewRole] = useState({
    identifier: "",
    description: "",
  });

  const [editRole, setEditRole] = useState(null);

  const fetchRoles = async () => {
    try {
      setLoading(true);

      const res = await listItems("role", {
        page,
        sizePerPage,
        sortField: "identifier",
      });

      const data = res?.content || [];

      setRoles(data);

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
      setError("Failed to load roles");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, [page]);

  const handleAddRole = async () => {
  const exists = roles.some(
    (role) =>
      role.identifier?.trim().toLowerCase() ===
      newRole.identifier?.trim().toLowerCase()
  );

  if (exists) {
    alert(`${newRole.identifier} already exists`);
    return;
  }

  try {
    await addItem("role", newRole);

    setNewRole({
      identifier: "",
      description: "",
    });

    fetchRoles();
  } catch (err) {
    console.error(err);
    alert("Add failed");
  }
};

  const handleUpdate = async () => {
    try {
      await updateItem("role", editRole);

      fetchRoles();
      setEditRole(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  const handleDelete = async (identifier) => {
    try {
      await deleteItem("role", identifier);

      fetchRoles();
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
      label: "Description",
      key: "description",
    },
  ];

  const actions = [
    {
      label: "✏️ Edit",
      onClick: (row) => setEditRole(row),
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
      name: "description",
      label: "Description",
    },
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled: true,
    },
    {
      name: "description",
      label: "Description",
    },
  ];

  return (
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
      onAdd={() => {}}
      addButtonText="+ Add Role"
      newItem={newRole}
      setNewItem={setNewRole}
      handleAdd={handleAddRole}
      addFields={addFields}
      editItem={editRole}
      setEditItem={setEditRole}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      emptyMessage="No roles found"
    />
  );
};

export default RolePage;