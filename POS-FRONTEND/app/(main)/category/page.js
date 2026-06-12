"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";
import api, {
  deleteItem,
  updateItem,
  addItem,
  listItems,
} from "@/services/api";

const CategoryList = () => {
  const [categories, setCategories] = useState([]);
  const [addCategories, setAddCategories] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [message, setMessage] = useState("");

  const [editCategories, setEditCategories] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const sizePerPage = 5;

  const [newCategory, setNewCategory] = useState({
    identifier: "",
    superCategory: "",
  });

  const [editCategory, setEditCategory] = useState(null);

  // ================= FETCH TABLE =================
  const fetchCategories = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("category", {
        page,
        sizePerPage,
        sortField: "id",
        search : searchTerm,
      });

      const data = Array.isArray(res?.content)
        ? res.content
        : [];
      setCategories(data);

      const pages =
        res?.totalPages ??
        Math.max(
          1,
          Math.ceil(
            (res?.totalElements ?? data.length) /
              sizePerPage
          )
        );

      setTotalPages(pages);
    } catch (err) {
      console.error(err);
      setError("Failed to load categories");
    } finally {
      setLoading(false);
    }
  };

  // ================= ADD DROPDOWN =================
  const fetchCategoryWithNull = async () => {
    try {
      const response = await api.get(
        "/api/category/listWithNull"
      );

      setAddCategories(
        Array.isArray(response.data)
          ? response.data
          : []
      );
    } catch (err) {
      console.error(err);
      setAddCategories([]);
    }
  };

  // ================= EDIT DROPDOWN =================
  const fetchCategoryWithoutNull = async (
    identifier
  ) => {
    try {
      const response = await api.get(
        `/api/category/listWithoutNull?identifier=${identifier}`
      );

      setEditCategories(
        Array.isArray(response.data)
          ? response.data
          : []
      );
    } catch (err) {
      console.error(err);
      setEditCategories([]);
    }
  };

  // ================= LOAD =================
  useEffect(() => {
    fetchCategories();
  }, [page , searchTerm]);

  useEffect(() => {
    fetchCategoryWithNull();
  }, []);

  // ================= ADD =================
  const handleAddCategory = async () => {
    try {
      const res = await addItem("category", newCategory);
      if (res?.success === false) {
      setMessage(res.message);
      return false;
    }

    setMessage("Category added successfully");

      setNewCategory({
        identifier: "",
        superCategory: "",
      });

      await fetchCategories();
      await fetchCategoryWithNull();
    } catch (err) {
    setMessage(
      err?.response?.data?.message ||
      err?.message ||
      "Add failed"
    );
    }
  };

  // ================= UPDATE =================
  const handleUpdate = async () => {
    try {
      await updateItem(
        "category",
        editCategory
      );

      setEditCategory(null);

      await fetchCategories();
      await fetchCategoryWithNull();
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
  const handleDelete = async (
    identifier
  ) => {
    const confirmDelete =
  globalThis.confirm(
    `Delete ${identifier}?`
  );

    if (!confirmDelete) return;

    try {
      await deleteItem(
        "category",
        identifier
      );

      await fetchCategories();
      await fetchCategoryWithNull();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
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
      label: "Identifier",
      key: "identifier",
    },
    {
      label: "Super Category",
      render: (category) =>
        category.superCategory
          ? category.superCategory
              .identifier ||
            category.superCategory
          : "-",
    },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "✏️",

      onClick: async (row) => {
        await fetchCategoryWithoutNull(
          row.identifier
        );

        setEditCategory({
          ...row,
          superCategory:
            row.superCategory
              ?.identifier ||
            row.superCategory ||
            "",
        });
      },
    },
    {
      label: "🗑",

      onClick: (row) =>
        handleDelete(
          row.identifier
        ),
    },
  ];

  // ================= OPTIONS =================
  const addCategoryOptions =
    Array.isArray(addCategories)
      ? addCategories.map(
          (category) => ({
            label:
              category.identifier,
            value:
              category.identifier,
          })
        )
      : [];

  const editCategoryOptions =
    Array.isArray(editCategories)
      ? editCategories.map(
          (category) => ({
            label:
              category.identifier,
            value:
              category.identifier,
          })
        )
      : [];

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      name: "superCategory",
      label: "Super Category",
      type: "select",
      options: addCategoryOptions,
    },
  ];

  // ================= EDIT FIELDS =================
  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      disabled: true,
    },
    {
      name: "superCategory",
      label: "Super Category",
      type: "select",
      options: editCategoryOptions,
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
      totalPages={totalPages}
      onAdd={() => {}}
      addButtonText="+ Add Category"
      newItem={newCategory}
      setNewItem={setNewCategory}
      handleAdd={handleAddCategory}

      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      message={message}
      setMessage={setMessage}
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

export default CategoryList;