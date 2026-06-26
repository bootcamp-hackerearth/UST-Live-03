"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";
import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
} from "@/services/api";

const WarehouseList = () => {
  const [warehouses, setWarehouses] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");

  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [viewWarehouse, setviewWarehouse] = useState(null);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const sizePerPage = 5;

  // ================= ADD =================
  const [newWarehouse, setNewWarehouse] = useState({
    identifier: "",
    address: "",
    country: "",
    pincode: "",
  });

  // ================= EDIT =================
  const [editWarehouse, setEditWarehouse] = useState(null);

  // ================= FETCH =================
  const fetchWarehouses = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("warehouse", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = res?.content || res || [];

      setWarehouses(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalElements || data.length) / sizePerPage
          ) ||
          1
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load warehouses");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWarehouses();
  }, [page, searchTerm]);

  // ================= ADD =================
  const handleAddWarehouse = async () => {
  try {
    const res = await addItem(
      "warehouse",
      newWarehouse
    );

    if (res?.success === false) {
      setMessage(res.message);
      return false;
    }

    setNewWarehouse({
      identifier: "",
      address: "",
      country: "",
      pincode: "",
    });

    await fetchWarehouses();

    setMessage("Warehouse added successfully");

    return true; 
  } catch (err) {
    console.error(err);

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
      const res = await updateItem("warehouse", editWarehouse);

      if (res?.success === false) {
        setMessage(res.message);
        return;
      }

      setWarehouses((prev) =>
        prev.map((w) =>
          w.identifier === editWarehouse.identifier
            ? editWarehouse
            : w
        )
      );

      setEditWarehouse(null);
      setMessage("");
      await fetchWarehouses();

      setMessage("Warehouse updated successfully");
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
  const handleDelete = async (identifier) => {
    const confirmDelete = globalThis.confirm(
      `Delete ${identifier}?`
    );

    if (!confirmDelete) return;

    try {
      await deleteItem("warehouse", identifier);

      setWarehouses((prev) =>
        prev.filter((w) => w.identifier !== identifier)
      );

      setMessage("Warehouse deleted successfully");
    } catch (err) {
      console.error(err);

      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Delete failed"
      );
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
      label: "Address",
      key: "address",
    },
    {
      label: "Country",
      key: "country",
    },
    {
      label: "Pincode",
      key: "pincode",
    },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "👁️",
      onClick: (row) => setviewWarehouse(row),
    },
    {
      label: "✏️",
      onClick: (row) => setEditWarehouse(row),
    },
    {
      label: "🗑",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
    },
    {
      name: "address",
      label: "Address",
    },
    {
      name: "country",
      label: "Country",
    },
    {
      name: "pincode",
      label: "Pincode",
      type: "number",
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
      name: "address",
      label: "Address",
    },
    {
      name: "country",
      label: "Country",
    },
    {
      name: "pincode",
      label: "Pincode",
      type: "number",
    },
  ];

  return (
    <>
    <CommonList
      title="Warehouses"
      data={warehouses}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      message={message}
      setMessage={setMessage}

      onAdd={() => {}}   // ✅ THIS FIXES YOUR ADD BUTTON ISSUE

      addButtonText="+ Add Warehouse"
      newItem={newWarehouse}
      setNewItem={setNewWarehouse}
      handleAdd={handleAddWarehouse}
      addFields={addFields}

      editItem={editWarehouse}
      setEditItem={setEditWarehouse}
      handleUpdate={handleUpdate}
      editFields={editFields}

      actions={actions}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      emptyMessage="No warehouses found"
    />
    {viewWarehouse && (
      <div className="modalOverlay">
        <div className="viewModal">
          <div className="modalHeader">
            <h3>Unit Details</h3>

            <button
              className="closeBtn"
              onClick={() =>
                setviewWarehouse(null)
              }
            >
              ✕
            </button>
          </div>

          <div className="viewContent">
            <div className="viewRow">
              <span>Name</span>
              <strong>
                {viewWarehouse.identifier}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created By</span>
              <strong>
                {viewWarehouse.createdBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created On</span>
              <strong>
                {viewWarehouse.createdOn ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified By</span>
              <strong>
                {viewWarehouse.modifiedBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified On</span>
              <strong>
                {viewWarehouse.modifiedOn ||
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

export default WarehouseList;