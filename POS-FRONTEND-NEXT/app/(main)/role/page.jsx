"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  addItem,
} from "@/services/api";

const RoleList = () => {
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [editRole, setEditRole] = useState(null);

  const sizePerPage = 5;

  const fetchRoles = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("role", {
        page,
        sizePerPage,
        sortField: "identifier",
        search,
      });

      let data = [];

      if (Array.isArray(res)) {
        data = res;
      } else if (Array.isArray(res?.content)) {
        data = res.content;
      }

      setRoles(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalRecords || data.length) /
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
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(
      `Delete role ${identifier}?`
    );

    if (!confirmDelete) return;

    try {
      await deleteItem(
        "role",
        identifier,
        "identifier"
      );

      fetchRoles();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      if (editRole?.isNew) {
        const allRolesResponse = await listItems(
          "role",
          {
            page: 0,
            sizePerPage: 10000,
          }
        );

        const allRoles = Array.isArray(
          allRolesResponse
        )
          ? allRolesResponse
          : allRolesResponse?.content || [];

        const duplicateExists = allRoles.some(
          (role) =>
            role.identifier
              ?.trim()
              .toLowerCase() ===
            editRole.identifier
              ?.trim()
              .toLowerCase()
        );

        if (duplicateExists) {
          alert(
            `Role with identifier "${editRole.identifier}" already exists`
          );
          return;
        }
      }

      const payload = {
        ...editRole,
      };

      if (editRole.isNew) {
        delete payload.id;
        delete payload.isNew;
        delete payload.formTitle;

        await addItem("role", payload);
      } else {
        delete payload.isNew;
        delete payload.formTitle;

        await updateItem("role", payload);
      }

      await fetchRoles();
      setEditRole(null);
    } catch (err) {
      console.error(err);

      alert(
        editRole?.isNew
          ? "Failed to add role"
          : "Failed to update role"
      );
    }
  };

  const columns = [
    {
      label: "ID",
      key: "id",
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
      onClick: (row) =>
        setEditRole({
          ...row,
          isNew: false,
          formTitle: "Edit Role",
        }),
    },
    {
      label: "🗑 Delete",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  const editFields = [
    {
      name: "id",
      label: "ID",
      disabled: true,
    },
    {
      name: "identifier",
      label: "Identifier",
      disabled: !editRole?.isNew,
    },
    {
      name: "description",
      label: "Description",
    },
  ];

  return (
  <>
    {/* LOADING OVERLAY */}
    {loading && (
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "200px",
          fontSize: "16px",
          fontWeight: "600",
        }}
      >
        Loading...
      </div>
    )}

    {/* ERROR STATE */}
    {!loading && error && (
      <div style={{ color: "red" }}>
        {error}
      </div>
    )}

    {/* MAIN CONTENT */}
    {!loading && !error && (
      <>
        <div
          style={{
            display: "flex",
            justifyContent: "flex-end",
            marginBottom: "16px",
          }}
        >
          <button
            onClick={() =>
              setEditRole({
                id: "",
                identifier: "",
                description: "",
                status: true,
                isNew: true,
                formTitle: "Add Role",
              })
            }
            style={{
              background: "#1976d2",
              color: "#fff",
              border: "none",
              borderRadius: "6px",
              padding: "10px 18px",
              cursor: "pointer",
              fontWeight: "600",
            }}
          >
            + Add Role
          </button>
        </div>

        <CommonList
          title="Roles"
          data={roles}
          loading={loading}
          error={error}
          page={page}
          setPage={setPage}
          totalPages={totalPages}
          search={search}
          setSearch={setSearch}
          columns={columns}
          actions={actions}
          editItem={editRole}
          setEditItem={setEditRole}
          handleUpdate={handleUpdate}
          editFields={editFields}
          popupTitle={editRole?.formTitle}
          emptyMessage="No roles found"
        />
      </>
    )}
  </>
);
}
export default RoleList;