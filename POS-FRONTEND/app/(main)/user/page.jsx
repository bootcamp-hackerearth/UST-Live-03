"use client";

import { useEffect, useState, useMemo, useCallback } from "react";
import CommonList from "@/app/components/CommonList";
import {
  listItems,
  deleteItem,
  updateItem,
  getListItems,
} from "@/services/api";

// ================= OUTSIDE COMPONENT (Sonar fix) =================
const getColumns = (page, sizePerPage) => [
  {
    label: "S.No",
    render: (_, index) => page * sizePerPage + index + 1,
  },
  { label: "Name", key: "name" },
  { label: "Phone No", key: "phoneNo" },
  { label: "Username", key: "username" },
  {
    label: "Roles",
    render: (row) => (row.roles || []).join(", ") || "-",
  },
];

const getActions = (setViewItem,setEditUser, handleDelete) => [
  {
    label: "View 👁️",
    onClick: (row) => setViewItem(row),
    },
  {
    label: "Edit ✏️",
    onClick: (row) => setEditUser(row),
  },
  {
    label: "Delete 🗑",
    onClick: (row) => handleDelete(row.username),
  },
];

const getEditFields = (roles) => [
  {
    name: "username",
    label: "Username",
    disabled: true,
  },
  {
    name: "name",
    label: "Name",
  },
  {
    name: "phoneNo",
    label: "Phone Number",
  },
{
  name: "roles",
  label: "Roles",
  type: "multiselect",
  options: roles.map((role) => ({
    value: role.identifier,
    label: role.identifier,
  })),
},
];

// ================= COMPONENT =================

const UserList = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const sizePerPage = 5;
  const [searchTerm, setSearchTerm] = useState("");
  const [editUser, setEditUser] = useState(null);
  const [viewItem, setViewItem] = useState(null);

  // ================= FETCH USERS =================
  const fetchUsers = useCallback(async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("user", {
        page,
        sizePerPage,
        sortField: "name",
        search: searchTerm,
      });

      let data = [];

      if (Array.isArray(res)) {
        data = res;
      } else if (Array.isArray(res?.content)) {
        data = res.content;
      }

      setUsers(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalRecords || data.length) / sizePerPage
          ) ||
          1
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load users");
    } finally {
      setLoading(false);
    }
  }, [page, searchTerm]);

const [roles, setRoles] = useState([]);

useEffect(() => {
  fetchUsers();
}, [fetchUsers]);

useEffect(() => {
  const fetchRoles = async () => {
    try {
      const response = await getListItems("role");
      setRoles(response || []);
    } catch (err) {
      console.error(err);
    }
  };

  fetchRoles();
}, []);

  // ================= DELETE =================
  const handleDelete = useCallback(async (username) => {
    const confirmDelete = globalThis.confirm(
      `Delete ${username}?`
    );

    if (!confirmDelete) return;

    try {
      await deleteItem("user", username, "username");
      fetchUsers();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  }, [fetchUsers]);

  // ================= UPDATE =================
  const handleUpdate = useCallback(async () => {
    try {
      await updateItem("user", editUser);
      await fetchUsers();
      setEditUser(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  }, [editUser, fetchUsers]);

  // ================= MEMOIZED CONFIGS =================
  const columns = useMemo(
    () => getColumns(page, sizePerPage),
    [page, sizePerPage]
  );

  const actions = useMemo(
    () => getActions(setViewItem, setEditUser, handleDelete),
    [handleDelete]
  );

  const editFields = useMemo(
  () => getEditFields(roles),
  [roles]
);

  // ================= RENDER =================
  return (
    <CommonList
      title="Users"
      data={users}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      sizePerPage={sizePerPage}
      columns={columns}
      actions={actions}
      viewItem={viewItem}
      setViewItem={setViewItem}
      editItem={editUser}
      setEditItem={setEditUser}
      handleUpdate={handleUpdate}
      editFields={editFields}
      emptyMessage="No users found"
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
    />
  );
};

export default UserList;