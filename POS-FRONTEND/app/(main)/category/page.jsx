"use client";
import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

import {
  listItems,
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

  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [addError, setAddError] = useState("");

  const [viewItem, setViewItem] = useState(null);


  // ================= ADD =================
  const [newCategory, setNewCategory] = useState({
    identifier: "",
    superCategory: "",
  });

  // ================= EDIT =================
  const [editCategory, setEditCategory] = useState(null);

  // ================= FETCH =================
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

  useEffect(() => {
    fetchCategories();
  }, [page, searchTerm]);


  // ================= fetchCategoryOptions =================
  const fetchCategoryOptions = async () => {
    try {
      const res = await listItems("category", {
        page: 0,
        sizePerPage: 1000,
        sortField: "identifier",
      });

      setCategoryOptions(res?.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchCategoryOptions();
  }, []);
  // ================= ADD =================
  const handleAddCategory = async () => {
    try {
      setAddError("");

      const response = await addItem(
        "category",
        newCategory
      );

      if (response?.success === false) {
        setAddError(
          response.message ||
          "Category already exists"
        );
        return false;
      }

      setNewCategory({
        identifier: "",
        superCategory: "",
      });

      await fetchCategories();
      return true;
    } catch (err) {
      console.error(err);

      setAddError(
        err.response?.data?.message ||
        "Add failed"
      );
      return false;
    }
  };

  // ================= UPDATE =================
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
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
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

      fetchCategories();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= COLUMNS =================
  const columns = [
    {
      label: "Sl No",
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
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "View 👁️",
      onClick: (row) => setViewItem(row),
    },
    {
      label: "Edit ✏️",
      onClick: (row) => setEditCategory(row),
    },
    {
      label: "Delete 🗑",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Category",
      required: true,
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

        ...categoryOptions.map((c) => ({
          label: c.identifier,
          value: c.identifier,
        })),
      ],
    },
  ];

  // ================= EDIT FIELDS =================
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
      addError={addError}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      onAdd={() => { setAddError(""); }}
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
      viewItem={viewItem}
      setViewItem={setViewItem}
      emptyMessage="No categories found"
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
    />
  );
};

export default CategoryPage;