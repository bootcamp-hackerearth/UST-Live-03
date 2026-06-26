"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList/CommonList";
import AccessGuard from "@/app/components/AccessGuard";

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
  const [totalPages, setTotalPages] = useState(1);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [newBrand, setNewBrand] = useState({
    identifier: "",
    description: "",
    status: true,
  });

  const [editBrand, setEditBrand] = useState(null);
  const [viewBrand, setViewBrand] = useState(null);

  const fetchBrands = async () => {
    try {
      if (brands.length === 0) {
        setLoading(true);
      }

      setError("");

      const res = await listItems("brand", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const normalized =
        (res?.content || []).map((brand) => ({
          ...brand,
          status:
            brand.status === true ||
            brand.status === 1 ||
            brand.status === "1",
        }));

      setBrands(normalized);
      setTotalPages(res?.totalPages || 1);

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

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddBrand = async () => {
    const response = await addItem(
      "brand",
      newBrand
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    setNewBrand({
      identifier: "",
      description: "",
      status: true,
    });

    fetchBrands();
    return true;
  };

  const handleUpdate = async () => {
    const response = await updateItem(
      "brand",
      editBrand
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    fetchBrands();

    return true;
  };

  const handleToggleStatus = async (
    identifier
  ) => {
    try {
      await toggleItem(
        "brand",
        identifier
      );

      setBrands((prev) =>
        prev.map((brand) =>
          brand.identifier === identifier
            ? {
              ...brand,
              status: !brand.status,
            }
            : brand
        )
      );

    } catch (err) {
      console.error(
        "Failed to update status:",
        err
      );

      alert("Failed to update status");
    }
  };

  const handleDelete = async (
    identifier
  ) => {
    if (
      !globalThis.confirm(
        `Delete ${identifier}?`
      )
    ) {
      return;
    }

    try {
      await deleteItem(
        "brand",
        identifier
      );

      fetchBrands();

    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const columns = [
    {
      label: "SL NO",
      render: (row, index) =>
        page * sizePerPage +
        index +
        1,
    },

    {
      label: "Brand Name",
      key: "identifier",
    },

    {
      label: "Description",
      key: "description",
    },

    {
      label: "Status",
      render: (brand) => (
        <label className="switch">
          <input
            type="checkbox"
            checked={brand.status}
            aria-label={`Toggle status for ${brand.identifier}`}
            onChange={() =>
              handleToggleStatus(
                brand.identifier
              )
            }
          />

          <span className="slider"></span>
        </label>
      ),
    },
  ];

  const actions = [
    {
      label: "👁 View",
      type: "view",
      onClick: (row) =>
        setViewBrand(row),
    },

    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditBrand(row),
    },

    {
      label: "🗑 Delete",
      type: "delete",
      onClick: (row) =>
        handleDelete(row.identifier),
    },
  ];

  return (
    <AccessGuard requiredPath="/brand">
      <CommonList
        title="Brands"
        data={brands}
        columns={columns}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        actions={actions}
        emptyMessage="No brands found"

        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}

        onAdd={() => { }}
        addButtonText="+ Add Brand"

        newItem={newBrand}
        setNewItem={setNewBrand}
        handleAdd={handleAddBrand}

        addFields={[
          {
            name: "identifier",
            label: "Brand Name",
          },
          {
            name: "description",
            label: "Description",
          },
        ]}

        editItem={editBrand}
        setEditItem={setEditBrand}
        handleUpdate={handleUpdate}

        editFields={[
          {
            name: "identifier",
            label: "Brand Name",
            disabled: true,
          },
          {
            name: "description",
            label: "Description",
          },
        ]}
        viewItem={viewBrand}
        setViewItem={setViewBrand}
      />
    </AccessGuard>
  );
};

export default BrandPage;