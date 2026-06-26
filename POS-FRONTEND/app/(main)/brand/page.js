"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList";

import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
  toggleItem,
} from "@/services/api";

const BrandPage = () => {
  const [brands, setBrands] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [viewBrand, setViewBrand] = useState(null);

  const [searchTerm, setSearchTerm] = useState("");

  const sizePerPage = 5;

  const [newBrand, setNewBrand] = useState({
    identifier: "",
    description: "",
  });

  const [editBrand, setEditBrand] = useState(null);

  // ================= FETCH BRANDS =================
  const fetchBrands = async () => {
    try {
      setLoading(true);

      const res = await listItems("brand", {
        page,
        sizePerPage: sizePerPage,
        sortField: "identifier",
        search: searchTerm,
      });

      console.log("BRAND API RESPONSE:", res);

      // ✅ FIX: handle all backend formats
      const data =
        res?.content ||
        res?.data?.content ||
        (Array.isArray(res) ? res : []);
      console.log("FIRST BRAND:", data[0]);

      setBrands(data);

      setTotalPages(
        res?.totalPages ||
        res?.data?.totalPages ||
        1
      );

      setError("");
    } catch (err) {
      console.error(err);
      setError("Failed to load brands");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBrands();
  }, [page, searchTerm]);

  // ================= ADD BRAND =================
  const handleAdd = async () => {
    if (!newBrand.identifier?.trim()) {
      alert("Identifier is required");
      return;
    }

    const exists = brands.some(
      (brand) =>
        brand.identifier?.trim().toLowerCase() ===
        newBrand.identifier?.trim().toLowerCase()
    );

    if (exists) {
      alert(`${newBrand.identifier} already exists`);
      return;
    }

    try {
      await addItem("brand", {
        identifier: newBrand.identifier,
        description: newBrand.description,
      });

      setNewBrand({
        identifier: "",
        description: "",
      });

      fetchBrands();
    } catch (err) {
      console.error(err);
      alert("Add failed");
    }
  };

  // ================= UPDATE BRAND =================
  const handleUpdate = async () => {
    try {
      await updateItem("brand", editBrand);

      setEditBrand(null);
      fetchBrands();
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE BRAND =================
  const handleDelete = async (identifier) => {
    try {
      await deleteItem("brand", identifier);
      fetchBrands();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= TOGGLE STATUS =================
  const handleToggleStatus = async (row) => {
    const identifier = row.identifier;

    // optimistic UI
    setBrands((prev) =>
      prev.map((b) =>
        b.identifier === identifier
          ? { ...b, status: !b.status }
          : b
      )
    );

    try {
      await toggleItem("brand", identifier);
    } catch (err) {
      console.error(err);
      alert("Toggle failed");
      fetchBrands();
    }
  };

  // ================= TABLE COLUMNS =================
  const columns = [
    {
      label: "Sl No",
      render: (row, index) => page * sizePerPage + index + 1,
    },
    {
      label: "Identifier",
      key: "identifier",
    },
    {
      label: "Description",
      key: "description",
    },
    {
      label: "Status",
      key: "status",
    },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "👁 View",
      onClick: (row) => setViewBrand(row),
    },
    {
      label: "✏️ Edit",
      onClick: (row) => setEditBrand(row),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  // ================= FORM FIELDS =================
  const addFields = [
    { name: "identifier", label: "Identifier" },
    { name: "description", label: "Description" },
  ];

  const editFields = [
    { name: "identifier", label: "Identifier",disabled:true },
    { name: "description", label: "Description" },
  ];

  return (
    <CommonList
      title="Brands"
      data={brands}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      newItem={newBrand}
      setNewItem={setNewBrand}
      onAdd={handleAdd}
      handleAdd={handleAdd}
      viewItem={viewBrand}
      setViewItem={setViewBrand}
      addFields={addFields}
      editItem={editBrand}
      setEditItem={setEditBrand}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      emptyMessage="No brands found"
      onToggleStatus={handleToggleStatus}
    />
  );
};

export default BrandPage;