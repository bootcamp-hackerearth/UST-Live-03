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

  const [searchTerm, setSearchTerm] = useState("");

  const [addError, setAddError] = useState("");
  const [viewItem, setViewItem] = useState(null);

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
        search :searchTerm,
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
  }, [page, searchTerm]);
 
 const handleAddRole = async () => {
  try {
    setAddError("");

    const response = await addItem(
      "role",
      newRole
    );

    if (response?.success === false) {
      setAddError(
        response.message || "Role already exists"
      );
      return false;
    }

    setNewRole({
      identifier: "",
      description: "",
    });

    await fetchRoles();
    return true;
  } catch (err) {
    console.error(err);

    setAddError(
      err.response?.data?.message ||
      "Add failed"
    );
    return false;
  }
};
 
  const handleUpdate = async () => {
    try {
      await updateItem("role", editRole);
 
      await fetchRoles();
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
      label: "Description",
      key: "description",
    },
  ];
 
  const actions = [
    {
    label: "View 👁️",
    onClick: (row) => setViewItem(row),
    },
  {
    label: "Edit ✏️",
    onClick: (row) => setEditRole(row),
  },
  {
    label: "Delete 🗑",
    onClick: (row) =>
      handleDelete(row.identifier),
  },
];
 
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      required: true,
    },
    {
      name: "description",
      label: "Description",
       required: true,
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
      addError={addError}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      onAdd={() => {setAddError("");}}
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
      viewItem={viewItem}
      setViewItem={setViewItem}
      emptyMessage="No roles found"
     searchTerm={searchTerm}
  setSearchTerm={setSearchTerm}
    />
  );
};
 
export default RolePage;