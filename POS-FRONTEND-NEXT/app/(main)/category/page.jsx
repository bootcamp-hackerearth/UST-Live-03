"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  addItem,
  getListItems,
} from "@/services/api";

const CategoryList = () => {
  const [categories, setCategories] = useState([]);

  const [search, setSearch] = useState("");

  const [allCategories, setAllCategories] = useState([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [page, setPage] = useState(0);

  const [totalPages, setTotalPages] = useState(1);

  const [editCategory, setEditCategory] = useState(null);

  const [auditCategory, setAuditCategory] = useState(null);

  const sizePerPage = 5;

  const fetchCategories = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("category", {
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

      setCategories(data);

      setTotalPages(
        res?.totalPages ||
        Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
        1,
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load categories");
    } finally {
      setLoading(false);
    }
  };

  const fetchAllCategories = async () => {
    try {
      const response = await getListItems("category");

      const data = Array.isArray(response) ? response : response?.content || [];

      setAllCategories(data);
    } catch (err) {
      console.error(err);
      setAllCategories([]);
    }
  };

  useEffect(() => {
    fetchAllCategories();
  }, []);

  useEffect(() => {
    fetchCategories();
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(`Delete category ${identifier}?`);

    if (!confirmDelete) return;

    try {
      await deleteItem("category", identifier, "identifier");

      fetchCategories();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editCategory,
      };

      delete payload.isNew;
      delete payload.formTitle;

      if (editCategory.isNew) {
        delete payload.id;

        await addItem("category", payload);
      } else {
        await updateItem("category", payload);
      }

      await fetchCategories();
      setEditCategory(null);
    } catch (err) {
      console.error(err);

      alert(
        editCategory?.isNew
          ? "Failed to add category"
          : "Failed to update category",
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
      label: "Super Category",
      key: "superCategory",
    },
  ];
  const actions = [
    {
      label: "📋 Audit Details",
      onClick: (row) => setAuditCategory(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditCategory({
          ...row,
          isNew: false,
          formTitle: "Edit Category",
        }),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.identifier),
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
      disabled: !editCategory?.isNew,
    },
    {
      name: "superCategory",
      label: "Super Category",
      type: "select",
      options: allCategories.map((cat) => ({
        label: cat.identifier,
        value: cat.identifier,
      })),
    },
    {
      name: "status",
      label: "Status",
      type: "checkbox",
    },
  ];

  return (
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
            setEditCategory({
              id: "",
              identifier: "",
              superCategory: "",
              status: true,
              isNew: true,
              formTitle: "Add Category",
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
          + Add Category
        </button>
      </div>

      <CommonList
        title="Categories"
        data={categories}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        sizePerPage={sizePerPage}
        columns={columns}
        actions={actions}
        editItem={editCategory}
        setEditItem={setEditCategory}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editCategory?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No categories found"
      />

      {auditCategory && (
        <div className="modalOverlay">
          <div
            className="modal"
            style={{
              width: "500px",
              padding: "20px",
            }}
          >
            <h2>Audit Details</h2>

            <div
              style={{
                display: "grid",
                gap: "12px",
                marginTop: "16px",
              }}
            >
              <div>
                <strong>Category:</strong> {auditCategory.identifier}
              </div>

              <div>
                <strong>Created By:</strong> {auditCategory.createdBy || "N/A"}
              </div>

              <div>
                <strong>Created On:</strong>{" "}
                {auditCategory.createdOn
                  ? new Date(auditCategory.createdOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Modified By:</strong>{" "}
                {auditCategory.modifiedBy || "N/A"}
              </div>

              <div>
                <strong>Modified On:</strong>{" "}
                {auditCategory.modifiedOn
                  ? new Date(auditCategory.modifiedOn).toLocaleString()
                  : "N/A"}
              </div>

              <div>
                <strong>Status:</strong>{" "}
                {auditCategory.status ? "Active" : "Inactive"}
              </div>

              <div>
                <strong>Super Category:</strong>{" "}
                {auditCategory.superCategory || "N/A"}
              </div>
            </div>

            <div
              className="modalActions"
              style={{
                marginTop: "20px",
              }}
            >
              <button onClick={() => setAuditCategory(null)}>Close</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default CategoryList;
