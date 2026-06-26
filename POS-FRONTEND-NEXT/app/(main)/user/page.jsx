"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  getListItems,
} from "@/services/api";

const UserList = () => {
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);

  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editUser, setEditUser] = useState(null);

  // ================= AUDIT DIALOG =================
  const [auditUser, setAuditUser] = useState(null);

  const sizePerPage = 5;

  // ================= DEBOUNCE SEARCH =================
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 400);

    return () => clearTimeout(timer);
  }, [search]);

  // ================= RESET PAGE ON SEARCH =================
  useEffect(() => {
    setPage(0);
  }, [debouncedSearch]);

  // ================= FETCH USERS =================
  const fetchUsers = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("user", {
        page,
        sizePerPage,
        sortField: "name",
        search: debouncedSearch,
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
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load users");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [page, debouncedSearch]);

  // ================= FETCH ROLES =================
  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const response = await getListItems("role");

        const roleData = Array.isArray(response)
          ? response
          : response?.content || [];

        setRoles(roleData);
      } catch (err) {
        console.error("Error fetching roles:", err);
        setRoles([]);
      }
    };

    fetchRoles();
  }, []);

  // ================= DELETE =================
  const handleDelete = async (username) => {
    const confirmDelete = globalThis.confirm(`Delete ${username}?`);

    if (!confirmDelete) return;

    try {
      await deleteItem("user", username, "username");

      fetchUsers();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= UPDATE =================
  const handleUpdate = async () => {
    try {
      await updateItem("user", editUser);

      fetchUsers();
      setEditUser(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= COLUMNS =================
  const columns = [
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
        (row.roles || [])
          .map((role) => (typeof role === "string" ? role : role.identifier))
          .join(", ") || "-",
    },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "📋 Audit",
      onClick: (row) => setAuditUser(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditUser({
          ...row,
          roles: (row.roles || []).map((role) =>
            typeof role === "string" ? role : role.identifier,
          ),
        }),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.username),
    },
  ];

  // ================= EDIT FIELDS =================
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
        label: role.identifier,
        value: role.identifier,
      })),
    },
  ];
  return (
    <>
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
        editItem={editUser}
        setEditItem={setEditUser}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle="Edit User"
        emptyMessage="No users found"
        search={search}
        setSearch={setSearch}
      />

      {auditUser && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            background: "rgba(0,0,0,0.5)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            zIndex: 9999,
          }}
        >
          <div
            style={{
              background: "#fff",
              padding: "24px",
              borderRadius: "12px",
              width: "500px",
              maxWidth: "95%",
              boxShadow: "0 8px 30px rgba(0,0,0,0.2)",
            }}
          >
            <h2
              style={{
                marginTop: 0,
                marginBottom: "20px",
              }}
            >
              Audit Information
            </h2>

            <div
              style={{
                display: "grid",
                gridTemplateColumns: "150px 1fr",
                gap: "12px",
              }}
            >
              <strong>Created By</strong>
              <span>{auditUser.createdBy || "-"}</span>

              <strong>Created On</strong>
              <span>
                {auditUser.createdOn
                  ? new Date(auditUser.createdOn).toLocaleString()
                  : "-"}
              </span>

              <strong>Modified By</strong>
              <span>{auditUser.modifiedBy || "-"}</span>

              <strong>Modified On</strong>
              <span>
                {auditUser.modifiedOn
                  ? new Date(auditUser.modifiedOn).toLocaleString()
                  : "-"}
              </span>
            </div>

            <div
              style={{
                display: "flex",
                justifyContent: "flex-end",
                marginTop: "24px",
              }}
            >
              <button
                onClick={() => setAuditUser(null)}
                style={{
                  background: "#1976d2",
                  color: "#fff",
                  border: "none",
                  padding: "10px 20px",
                  borderRadius: "6px",
                  cursor: "pointer",
                }}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default UserList;
