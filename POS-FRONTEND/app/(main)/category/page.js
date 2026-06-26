"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

import {
  listItems,
  getListItems,
  deleteItem,
  updateItem,
  addItem,
} from "@/services/api";

const CategoryPage = () => {
  const [categories, setCategories] = useState([]);
  const [categoryOptions, setCategoryOptions] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTerm, setSearchTerm] = useState("");
  const [viewCategory, setViewCategory] = useState(null);

  const sizePerPage = 5;

  const [newCategory, setNewCategory] = useState({
    identifier: "",
    superCategory: "",
    status: 1,
  });

  const [editCategory, setEditCategory] = useState(null);

  const fetchCategories = async () => {
    try {
      setLoading(true);

      const res = await listItems("category", {
        page,
        sizePerPage,
        sortField: "identifier",
        search: searchTerm,
      });

      const data = res?.content || [];
      setCategories(data);

      setTotalPages(
        res?.totalPages ||
        Math.ceil((res?.totalElements || data.length) / sizePerPage) ||
        1
      );
    } catch {
      setError("Failed to load categories");
    } finally {
      setLoading(false);
    }
  };

  const fetchCategoryOptions = async () => {
    try {
      const res = await getListItems("category");
      setCategoryOptions(Array.isArray(res) ? res : res?.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchCategories();
  }, [page, searchTerm]);

  useEffect(() => {
    fetchCategoryOptions();
  }, []);

  // ✅ TOGGLE FIX (IMPORTANT)
  const handleToggleStatus = async (row) => {
    const updated = {
      ...row,
      status: Number(row.status) === 1 ? 0 : 1,
    };

    // optimistic UI update
    setCategories((prev) =>
      prev.map((c) =>
        c.identifier === row.identifier ? updated : c
      )
    );

    try {
      await updateItem("category", updated);
    } catch {
      alert("Status update failed");
      fetchCategories();
    }
  };

  const handleAddCategory = async () => {
    if (!newCategory.identifier?.trim()) {
      alert("Category is required");
      return;
    }

    const exists = categories.some(
      (category) =>
        category.identifier?.trim().toLowerCase() ===
        newCategory.identifier?.trim().toLowerCase()
    );

    if (exists) {
      alert(`${newCategory.identifier} already exists`);
      return;
    }

    try {
      await addItem("category", newCategory);

      setNewCategory({
        identifier: "",
        superCategory: "",
        status: 1,
      });

      fetchCategories();
      fetchCategoryOptions();
    } catch {
      alert("Add failed");
    }
  };

  const handleUpdate = async () => {
    try {
      await updateItem("category", editCategory);

      setEditCategory(null);
      fetchCategories();
      fetchCategoryOptions();
    } catch {
      alert("Update failed");
    }
  };

  const handleDelete = async (identifier) => {
    if (!confirm(`Delete ${identifier}?`)) return;

    try {
      await deleteItem("category", identifier);
      fetchCategories();
      fetchCategoryOptions();
    } catch {
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
      label: "Category",
      key: "identifier",
    },

    {
      label: "Super Category",
      render: (row) => row.superCategory || "-",
    },

    // ✅ STATUS TOGGLE (NEW UI - NO CHECKBOX)
    {
      label: "Status",
      render: (row) => (
        <button
          onClick={() => handleToggleStatus(row)}
          className={`
            relative w-14 h-7 flex items-center rounded-full transition
            ${Number(row.status) === 1 ? "bg-teal-500" : "bg-slate-300"}
          `}
        >
          <span
            className={`
              w-6 h-6 bg-white rounded-full shadow-md transform transition
              ${Number(row.status) === 1 ? "translate-x-7" : "translate-x-1"}
            `}
          />
        </button>
      ),
    },
  ];

  const actions = [
    {
      label: "👁 View",
      onClick: (row) => setViewCategory(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) => setEditCategory(row),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  const addFields = [
    {
      name: "identifier",
      label: "Category",
    },
    {
      name: "superCategory",
      label: "Super Category",
      type: "select",
      options: [
        { label: "None", value: "" },
        ...categoryOptions.map((c) => ({
          label: c.identifier,
          value: c.identifier,
        })),
      ],
    },
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Category",
      disabled: true,
    },
    {
      name: "superCategory",
      label: "Super Category",
      type: "select",
      options: [
        { label: "None", value: "" },
        ...categoryOptions.map((c) => ({
          label: c.identifier,
          value: c.identifier,
        })),
      ],
    },
  ];

  return (
    <CommonList
      title="Categories"
      data={categories}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      onAdd={() => { }}
      addButtonText="+ Add Category"
      newItem={newCategory}
      setNewItem={setNewCategory}
      handleAdd={handleAddCategory}
      addFields={addFields}
      editItem={editCategory}
      setEditItem={setEditCategory}
      handleUpdate={handleUpdate}
      editFields={editFields}
      viewItem={viewCategory}
      setViewItem={setViewCategory}
      actions={actions}
      emptyMessage="No categories found"
    />
  );
};

export default CategoryPage;