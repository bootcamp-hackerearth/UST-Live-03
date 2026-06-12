"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList/CommonList";
import AccessGuard from "@/app/components/AccessGuard";

import {
  getAllItems,
  listItems,
  deleteItem,
  updateItem,
  addItem,
} from "@/services/api";

const CategoryPage = () => {
  const [categories, setCategories] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [page, setPage] =
    useState(0);

  const [totalPages, setTotalPages] =
    useState(1);

  const [searchTerm, setSearchTerm] =
   useState("");  

  const sizePerPage = 5;

  const [newCategory, setNewCategory] =
    useState({
      identifier: "",
      superCategory: "",
    });

  const [editCategory, setEditCategory] =
    useState(null);

  const [
    addCategoryOptions,
    setAddCategoryOptions,
  ] = useState([]);

  const [
    editCategoryOptions,
    setEditCategoryOptions,
  ] = useState([]);

  const fetchCategories = async () => {
    try {
      if (categories.length === 0) {
        setLoading(true);
      }

      setError("");

      const res = await listItems(
        "category",
        {
          page,
          sizePerPage,
          sortField: "id",
          search: searchTerm,
        }
      );

      console.log(
        "CATEGORY RESPONSE:",
        res
      );

      setCategories(
        res?.content || []
      );

      setTotalPages(
        res?.totalPages || 1
      );

    } catch (err) {
      console.error(err);

      setError(
        "Failed to load categories"
      );

    } finally {
      setLoading(false);
    }
  };

  const fetchAddCategories =
    async () => {
      try {
        const res =
          await getAllItems(
            "category"
          );

        setAddCategoryOptions(
          res || []
        );

      } catch (err) {
        console.error(
          "Failed to load category list",
          err
        );
      }
    };

  useEffect(() => {
    fetchCategories();
  }, [page, searchTerm]);

  useEffect(() => {
    fetchAddCategories();
  }, []);

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddCategory = async () => {
    const response = await addItem(
      "category",
      newCategory
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    setNewCategory({
      identifier: "",
      superCategory: "",
    });

    fetchCategories();

    return true;
  };

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

      fetchCategories();

    } catch (err) {
      console.error(err);

      alert("Delete failed");
    }
  };

  const openEdit = async (
    category
  ) => {
    try {
      const res =
        await getAllItems(
          "category"
        );

      const filtered =
        (res || []).filter(
          (cat) =>
            cat.identifier !==
            category.identifier
        );

      setEditCategoryOptions(
        filtered
      );

      setEditCategory(category);

    } catch (err) {
      console.error(err);
    }
  };

  const handleUpdate = async () => {
    const response = await updateItem(
      "category",
      editCategory
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    fetchCategories();

    return true;
  };

  const addOptions =
    addCategoryOptions.map(
      (cat) => ({
        value: cat.identifier,
        label: cat.identifier,
      })
    );

  const editOptions =
    editCategoryOptions.map(
      (cat) => ({
        value: cat.identifier,
        label: cat.identifier,
      })
    );

  const columns = [
    {
      label: "SL NO",

      render: (
        row,
        index
      ) =>
        page *
          sizePerPage +
        index +
        1,
    },

    {
      label: "Category",
      key: "identifier",
    },

    {
      label: "Super Category",
      key: "superCategory",
    },
  ];

  const actions = [
    {
      label: "✏️ Edit",
      onClick: openEdit,
    },

    {
      label: "🗑 Delete",
      type: "delete",

      onClick: (row) =>
        handleDelete(
          row.identifier
        ),
    },
  ];

  return (
    <AccessGuard requiredPath="/category">
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
        actions={actions}
        emptyMessage="No categories found"

        onAdd={() =>
          setNewCategory({
            identifier: "",
            superCategory: "",
          })
        }

        addButtonText="+ Add Category"

        addFields={[
          {
            name: "identifier",
            label: "Category Name",
          },

          {
            name: "superCategory",
            label: "Super Category",
            type: "select",
            options: addOptions,
            required: false,
          },
        ]}

        newItem={newCategory}

        setNewItem={
          setNewCategory
        }

        handleAdd={
          handleAddCategory
        }

        editItem={
          editCategory
        }

        setEditItem={
          setEditCategory
        }

        handleUpdate={
          handleUpdate
        }

        editFields={[
          {
            name: "identifier",
            label: "Category Name",
            disabled: true,
          },

          {
            name: "superCategory",
            label: "Super Category",
            type: "select",
            options: editOptions,
            required: false,
          },
        ]}
      />
    </AccessGuard>
  );
};

export default CategoryPage;