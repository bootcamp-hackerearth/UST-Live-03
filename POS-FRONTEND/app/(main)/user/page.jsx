"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList/CommonList";
import AccessGuard from "@/app/components/AccessGuard";

import {
  getAllItems,
  listItems,
  deleteItem,
  updateItem,
} from "@/services/api";

const UserPage = () => {
  const [users, setUsers] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [page, setPage] =
    useState(0);

  const [totalPages, setTotalPages] =
    useState(1);

  const [rolesList, setRolesList] = useState([]);

  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] =
   useState("");

  const [editUser, setEditUser] =
    useState(null);

  const fetchUsers = async () => {
    try {
      if (users.length === 0) {
        setLoading(true);
      }

      setError("");

      const res = await listItems(
        "user",
        {
          page,
          sizePerPage,
          sortField: "id",
          search: searchTerm,
        }
      );

      console.log(
        "USER RESPONSE:",
        res
      );

      setUsers(res?.content || []);

      setTotalPages(
        res?.totalPages || 1
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

  useEffect(() => {
    fetchUsers();
  }, [page, searchTerm]);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const fetchRoles = async () => {
    try {
      const res = await getAllItems("role");

      setRolesList(res?.content || res || []);
    } catch (err) {
      console.error("Failed to load roles", err);
    }
  };

  useEffect(() => {
    fetchRoles();
  }, []);

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

    } catch (err) {
      console.error(err);

      alert("Delete failed");
    }
  };

  const openEdit = (user) => {
    setEditUser(user);
  };

  const handleUpdate = async () => {
    try {
      await updateItem(
        "user",
        editUser
      );

      setEditUser(null);

      fetchUsers();

    } catch (err) {
      console.error("Update failed:", err);
      alert("Update failed");
    }
  };

  return (
    <AccessGuard requiredPath="/user">
      <div>

        <CommonList
          title="Users"
          data={users}
          loading={loading}
          error={error}
          page={page}
          setPage={setPage}
          sizePerPage={sizePerPage}
          totalPages={totalPages}

          searchTerm={searchTerm}
          setSearchTerm={setSearchTerm}

          columns={[
            {
              label: "SL NO",
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
                (row.roles || []).join(", ") || "-",
            },
          ]}

          editItem={editUser}
          setEditItem={setEditUser}
          handleUpdate={handleUpdate}

          editFields={[
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
              options: rolesList.map((role) => ({
                value: role.identifier,
                label: role.identifier,
              })),
            },
          ]}

          actions={[
            {
              label: "✏️ Edit",
              onClick: openEdit,
            },
            {
              label: "🗑 Delete",
              type: "delete",
              onClick: (row) =>
                handleDelete(row.username),
            },
          ]}
        />

      </div>
    </AccessGuard> 
  );
};

export default UserPage;