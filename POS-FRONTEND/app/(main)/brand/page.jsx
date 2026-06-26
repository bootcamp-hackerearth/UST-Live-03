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

const BrandList = () => {
  const [brands, setBrands] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [addError, setAddError] = useState("");

  const [viewItem, setViewItem] = useState(null);


  // ================= ADD =================
  const [newBrand, setNewBrand] = useState({
    identifier: "",
    description: "",
  });

  // ================= EDIT =================
  const [editBrand, setEditBrand] = useState(null);

  // ================= FETCH BRANDS =================
  const fetchBrands = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("brand", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = res?.content || [];

      const normalized = data.map((b) => ({
        ...b,
        status:
          b.status === true ||
          b.status === 1 ||
          b.status === "1",
      }));

      setBrands(normalized);

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
      setError("Failed to load brands");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBrands();
  }, [page, searchTerm]);

  // ================= ADD =================
  const handleAddBrand = async () => {
    try {
      setAddError("");

      const response = await addItem("brand", newBrand);

      if (response?.success === false) {
        setAddError(response.message || "Brand already exists");
        return false;
      }

      setNewBrand({
        identifier: "",
        description: "",
      });

      await fetchBrands();
      return true;
    } catch (err) {
      console.error(err);
      setAddError("Add failed");
      return false;
    }
  };

  // ================= UPDATE =================
  const handleUpdate = async () => {
    try {
      await updateItem("brand", editBrand);

      setBrands((prev) =>
        prev.map((b) =>
          b.identifier === editBrand.identifier
            ? editBrand
            : b
        )
      );

      setEditBrand(null);
    } catch (err) {
      console.error(err);
      alert("Update failed");
    }
  };

  // ================= DELETE =================
  const handleDelete = async (identifier) => {
    const ok = globalThis.confirm(`Delete ${identifier}?`);
    if (!ok) return;

    try {
      await deleteItem("brand", identifier);

      setBrands((prev) =>
        prev.filter((b) => b.identifier !== identifier)
      );
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  // ================= TOGGLE =================
  const handleToggleStatus = async (identifier) => {
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

  // ================= COLUMNS (UPDATED) =================
  const columns = [
    {
      label: "Sl No",
      render: (row, index) =>
        page * sizePerPage + index + 1,
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
      render: (b) => (
        <label className="switch">
          <input
            type="checkbox"
            aria-label="Status"
            checked={b.status}
            onChange={() => handleToggleStatus(b.identifier)}
          />
          <span className="slider"></span>
        </label>
      ),
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
      onClick: (row) => setEditBrand(row),
    },
    {
      label: "Delete 🗑",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      required: true,
    },
    {
      name: "description",
      label: "Description",
       required: true,
    },
  ];

  // ================= EDIT FIELDS =================
  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled: true,
    },
    {
      name: "description",
      label: "Description",
    },
  ];

  return (
    <CommonList
      title="Brands"
      data={brands}
      columns={columns}
      loading={loading}
      error={error}
      addError={addError}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      onAdd={() => setAddError("")}
      addButtonText="+ Add Brand"
      newItem={newBrand}
      setNewItem={setNewBrand}
      handleAdd={handleAddBrand}
      addFields={addFields}
      editItem={editBrand}
      setEditItem={setEditBrand}
      handleUpdate={handleUpdate}
      editFields={editFields}
      actions={actions}
      viewItem={viewItem}
      setViewItem={setViewItem}
      emptyMessage="No brands found"
    />
  );
};

export default BrandList;