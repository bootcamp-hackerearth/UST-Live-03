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
const [roles, setRoles] = useState([]);
  const [users, setUsers] = useState([]);
// message state removed; use `error` for user-facing errors
  const [loading, setLoading] =
    useState(true);

  const [viewUser, setviewUser] = useState(null);

  const [error, setError] =
    useState("");

  const [page, setPage] =
    useState(0);

  const [totalPages, setTotalPages] =
    useState(1);

  const sizePerPage = 5;

  // ================= EDIT =================
  const [editUser, setEditUser] =
    useState(null);
const fetchRoles = async () => {
  try {
    const res = await getListItems("role");

    const data = res?.content || res || [];

    setRoles(data);
  } catch (err) {
    console.error("Failed to fetch roles", err);
  }
};
  // ================= FETCH USERS =================
  const fetchUsers = async () => {
    try {

      setLoading(true);

      setError("");

      const res = await listItems(
        "user",
        {
          page,
          sizePerPage,
          sortField: "name",
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
useEffect(() => {
  fetchRoles();
}, []);
  useEffect(() => {
    fetchUsers();
  }, [page]);

  // ================= DELETE =================
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
    console.error("Delete failed:", err);

    setError(
      err?.response?.data?.message ||
      err?.message ||
      "Delete failed"
    );
  }
  };

  // ================= UPDATE =================
  const handleUpdate = async () => {

    try {

      await updateItem(
        "user",
        editUser
      );

      fetchUsers();

      setEditUser(null);
      setError("");

    } catch (err) {
  console.error("Update failed:", err);

  setError(
    err?.response?.data?.message ||
    err?.message ||
    "Update failed"
  );
}
  };

  // ================= COLUMNS =================
  const columns = [
    {
      label: "ID",
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

  // ================= ACTIONS =================
  const actions = [
    {
      label: "👁️",
      onClick: (row) => setviewUser(row),
    },
    {
      label: "✏️",

      onClick: (row) =>
        setEditUser(row),
    },

    {
      label: "🗑",

      onClick: (row) =>
        handleDelete(
          row.username
        ),
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
      label: role.name || role.identifier || role.role,
      value: role.name || role.identifier || role.role,
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

      // EDIT
      editItem={editUser}

      setEditItem={setEditUser}

      handleUpdate={handleUpdate}

      editFields={editFields}

      emptyMessage="No users found"
    />
    {viewUser && (
      <div className="modalOverlay">
        <div className="viewModal">
          <div className="modalHeader">
            <h3>User Details</h3>

            <button
              className="closeBtn"
              onClick={() =>
                setviewUser(null)
              }
            >
              ✕
            </button>
          </div>

          <div className="viewContent">
            <div className="viewRow">
              <span>Name</span>
              <strong>
                {viewUser.username}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created By</span>
              <strong>
                {viewUser.createdBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created On</span>
              <strong>
                {viewUser.createdOn ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified By</span>
              <strong>
                {viewUser.modifiedBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified On</span>
              <strong>
                {viewUser.modifiedOn ||
                  "-"}
              </strong>
            </div>
          </div>
        </div>
      </div>
    )}
    </>
  );
};

export default UserList;