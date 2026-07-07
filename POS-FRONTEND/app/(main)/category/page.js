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
      setError("");

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
          Math.ceil(
            (res?.totalElements || data.length) /
              sizePerPage
          ) ||
          1
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load categories");
    } finally {
      setLoading(false);
    }
  };

  const fetchCategoryOptions = async () => {
  try {
    const res = await getListItems("category");

    const data = Array.isArray(res)
      ? res
      : res?.content || [];

    setCategoryOptions(data);
  } catch (err) {
    console.error(err);
  }
};

      useEffect(() => {
        fetchCategories();
      }, [page,searchTerm]);

      useEffect(() => {
        fetchCategoryOptions();
      }, []);

  const handleAddCategory = async () => {
  const exists = categoryOptions.some(
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
  } catch (err) {
    console.error(err);
    alert("Add failed");
  }
};

  const handleUpdate = async () => {
    try {
      await updateItem("category", editCategory);

      setCategories((prev) =>
        prev.map((c) =>
          c.identifier === editCategory.identifier
            ? editCategory
            : c
        )
      );

      setEditCategory(null);
      fetchCategories();
      fetchCategoryOptions();
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  const handleToggleStatus = async (category) => {
    try {
      const updatedCategory = {
        ...category,
        status:
          Number(category.status) === 1
            ? 0
            : 1,
      };

      await updateItem(
        "category",
        updatedCategory
      );

      fetchCategories();
    } catch (err) {
      console.error(err);
      alert("Status update failed");
    }
  };

  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(
      `Delete ${identifier}?`
    );

    if (!confirmDelete) return;

    try {
      await deleteItem(
        "category",
        identifier
      );

      setCategories((prev) =>
        prev.filter(
          (c) =>
            c.identifier !== identifier
        )
      );
      fetchCategoryOptions();
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
      label: "Category",
      key: "identifier",
    },

    {
      label: "Super Category",
      render: (row) =>
        row.superCategory || "-",
    },

    {
      label: "Status",
      render: (row) => (
        <label className="switch" aria-label="Toggle status">
          <input
            type="checkbox"
            checked={
              Number(row.status) === 1
            }
            onChange={() =>
              handleToggleStatus(row)
            }
          />
          <span className="slider"></span>
        </label>
      ),
    },
  ];

  const actions = [
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditCategory(row),
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
      label: "Category",
    },

    {
      name: "superCategory",
      label: "Super Category",
      type: "select",

      options: [
        {
          label: "None",
          value: "",
        },

        ...categoryOptions
          .filter(
            (c) =>
              c.identifier !==
              newCategory.identifier
          )
          .map((c) => ({
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
        {
          label: "None",
          value: "",
        },

        ...categoryOptions
          .filter(
            (c) =>
              c.identifier !==
              editCategory?.identifier
          )
          .map((c) => ({
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
      searchPlaceholder="Search Categories..."
      onAdd={() => {}}
      addButtonText="+ Add Category"
      newItem={newCategory}
      setNewItem={setNewCategory}
      handleAdd={handleAddCategory}
      addFields={addFields}
      editItem={editCategory}
      setEditItem={setEditCategory}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      emptyMessage="No categories found"
    />
  );
};

export default CategoryPage;