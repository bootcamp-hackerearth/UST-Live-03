"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  fetchRoles,
} from "@/services/api";

const UserList = () => {

  const [users, setUsers] = useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [page, setPage] =
    useState(0);

  const [totalPages, setTotalPages] =
    useState(1);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewUser, setViewUser] = useState(null);
  const [roles, setRoles] = useState([]);
  const sizePerPage = 5;

  const [editUser, setEditUser] =
    useState(null);

  const fetchUsers = async () => {
    try {
      setError("");

      const res = await listItems(
        "user",
        {
          page,
          sizePerPage,
          sortField: "name",
          search: searchTerm,
        }
      );

      let data = [];

      if (Array.isArray(res)) {
        data = res;
      } else if (
        Array.isArray(res?.content)
      ) {
        data = res.content;
      }

      setUsers(data);

      setTotalPages(
        res?.totalPages ||
        Math.ceil(
          (res?.totalRecords ||
            data.length) /
          sizePerPage
        ) ||
        1
      );

    } catch (err) {

      console.error(err);

      setError(
        "Failed to load users"
      );

    } finally {

      setLoading(false);

    }
  };

  const loadRoles = async () => {
  try {
    const response = await fetchRoles();
    console.log(response);
    setRoles(response || []);
  } catch (err) {
    console.error("Failed to load roles", err);
  }
};

  useEffect(() => {
  fetchUsers();
  loadRoles();
}, [page, searchTerm]);

  const handleDelete = async (
    username
  ) => {

    const confirmDelete =
      globalThis.confirm(
        `Delete ${username}?`
      );

    if (!confirmDelete) return;

    try {

      await deleteItem(
        "user",
        username,
        "username"
      );

      fetchUsers();

    } catch {

      alert("Delete failed");

    }
  };

  const handleUpdate = async () => {

    try {

      await updateItem(
        "user",
        editUser
      );

      fetchUsers();

      setEditUser(null);

    } catch {

      alert("Update failed");

    }
  };

  const columns = [
    {
      label: "Sl No",
      render: (row, index) =>
        page * sizePerPage + index + 1,
    },
    {
      label: "Name",
      key: "name",
    },

    {
      label: "Phone No",
      key: "phoneNo",
    },

    {
      label: "Username",
      key: "username",
    },

    {
      label: "Roles",

      render: (row) =>
        (row.roles || []).join(
          ", "
        ) || "-",
    },
  ];

  const actions = [
    {
      label: "👁 View",
      onClick: (row) => setViewUser(row),
    },
   {
  label: "✏️ Edit",

  onClick: (row) => {
    let userRoles = [];

    if (Array.isArray(row.roles)) {
      userRoles = row.roles;
    } else if (row.roles) {
      userRoles = row.roles.split(",");
    }

    setEditUser({
      ...row,
      roles: userRoles,
    });
  },
},

    {
      label: "🗑 Delete",

      onClick: (row) =>
        handleDelete(
          row.username
        ),
    },
  ];

  const editFields = [
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
  type: "select",
  multiple: true,
  options: roles.map((role) => ({
    value: role.identifier,
    label: role.identifier,
  })),
},
  ];

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

      searchTerm={searchTerm}

      setSearchTerm={setSearchTerm}

      columns={columns}

      actions={actions}

      editItem={editUser}

      setEditItem={setEditUser}

      handleUpdate={handleUpdate}


      editFields={editFields}

      emptyMessage="No users found"
      viewItem={viewUser}
      setViewItem={setViewUser}
    />
  );
};

export default UserList;