"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";
import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
  toggleItem,
} from "@/services/api";

const BrandList = () => {
  const [brands, setBrands] = useState([]);
  const [message, setMessage] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
const [viewBrand, setViewBrand] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const sizePerPage = 5;

  // ================= ADD =================
  const [newBrand, setNewBrand] = useState({
    identifier: "",
    description: "",
    status: true,
  });

  // ================= EDIT =================
  const [editBrand, setEditBrand] = useState(null);

  // ================= FETCH =================
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

      const data = Array.isArray(res)
        ? res
        : res?.content || [];

      const normalized = data.map((brand) => ({
        ...brand,
        status:
          brand.status === true ||
          brand.status === 1 ||
          brand.status === "1",
      }));

      setBrands(normalized);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalElements ||
              normalized.length) / sizePerPage
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
      const res = await addItem(
        "brand",
        newBrand
      );

      if (res?.success === false) {
        setMessage(res.message);
        return false;
      }

      setMessage(
        "Brand added successfully"
      );

      setNewBrand({
        identifier: "",
        description: "",
        status: true,
      });

      await fetchBrands();

      return true;
    } catch (err) {
      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Add failed"
      );

      return false;
    }
  };

  // ================= UPDATE =================
  const handleUpdate = async () => {
    try {
      await updateItem(
        "brand",
        editBrand
      );

      setBrands((prev) =>
        prev.map((b) =>
          b.identifier ===
          editBrand.identifier
            ? editBrand
            : b
        )
      );

      setEditBrand(null);
      setMessage("");
      await fetchBrands();
    } catch (err) {
      console.error(err);

      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Update failed"
      );
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
        "brand",
        identifier
      );

      setBrands((prev) =>
        prev.filter(
          (b) =>
            b.identifier !==
            identifier
        )
      );
    } catch (err) {
      console.error(err);

      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Delete failed"
      );
    }
  };

  // ================= TOGGLE =================
  const handleToggleStatus = async (
    identifier,
    currentStatus
  ) => {
    const newStatus =
      !currentStatus;

    setBrands((prev) =>
      prev.map((b) =>
        b.identifier === identifier
          ? {
              ...b,
              status: newStatus,
            }
          : b
      )
    );

    try {
      await toggleItem(
        "brand",
        identifier,
        newStatus
      );
    } catch (err) {
      console.error(err);

      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Toggle failed"
      );

      fetchBrands();
    }
  };

  // ================= COLUMNS =================
  const columns = [
    {
      label: "ID",
      render: (row, index) =>
        page * sizePerPage +
        index +
        1,
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
      render: (brand) => (
        <label
          htmlFor={`status-${brand.identifier}`}
          className="switch"
          aria-label={`Toggle status for ${brand.identifier}`}
        >
          <input
            id={`status-${brand.identifier}`}
            type="checkbox"
            checked={
              !!brand.status
            }
            onChange={() =>
              handleToggleStatus(
                brand.identifier,
                brand.status
              )
            }
          />

          <span
            className="slider"
            aria-hidden="true"
          ></span>
        </label>
      ),
    },
  ];

  // ================= ACTIONS =================
  const actions = [
     {
    label: "👁️",
    onClick: (row) => setViewBrand(row),
  },
    {
      label: "✏️",
      onClick: (row) =>
        setEditBrand({
          ...row,
        }),
    },

    {
      label: "🗑",
      onClick: (row) =>
        handleDelete(
          row.identifier
        ),
    },
  ];

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      required: true,
    },

    {
      name: "description",
      label: "Description",
      type: "text",
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
      name: "description",
      label: "Description",
      type: "text",
    },
  ];

  return (
    <>
    <CommonList
      title="Brands"
      data={brands}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={
        setSearchTerm
      }
      message={message}
      setMessage={setMessage}

      // ADD
      onAdd={() => {}}
      addButtonText="+ Add Brand"
      newItem={newBrand}
      setNewItem={setNewBrand}
      handleAdd={handleAddBrand}
      addFields={addFields}

      // EDIT
      editItem={editBrand}
      setEditItem={setEditBrand}
      handleUpdate={handleUpdate}
      editFields={editFields}

      // ACTIONS
      actions={actions}

      emptyMessage="No brands found"
    />
     {viewBrand && (
      <div className="modalOverlay">
        <div className="viewModal">
          <div className="modalHeader">
            <h3>Brand Details</h3>

            <button
              className="closeBtn"
              onClick={() =>
                setViewBrand(null)
              }
            >
              ✕
            </button>
          </div>

          <div className="viewContent">
            <div className="viewRow">
              <span>Name</span>
              <strong>
                {viewBrand.identifier}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created By</span>
              <strong>
                {viewBrand.createdBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created On</span>
              <strong>
                {viewBrand.createdOn ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified By</span>
              <strong>
                {viewBrand.modifiedBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified On</span>
              <strong>
                {viewBrand.modifiedOn ||
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

export default BrandList;
