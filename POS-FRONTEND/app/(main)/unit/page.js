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

const UnitList = () => {
  const [units, setUnits] = useState([]);
  const [message, setMessage] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [viewUnit, setviewUnit] = useState(null);

  const sizePerPage = 5;

  // ================= ADD =================
  const [newUnit, setNewUnit] = useState({
    identifier: "",
    description: "",
    status: true,
  });

  // ================= EDIT =================
  const [editUnit, setEditUnit] = useState(null);

  // ================= FETCH =================
  const fetchUnits = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("unit", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = Array.isArray(res)
        ? res
        : res?.content || [];

      const normalized = data.map((unit) => ({
        ...unit,
        status:
          unit.status === true ||
          unit.status === 1 ||
          unit.status === "1",
      }));

      setUnits(normalized);

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
      setError("Failed to load units");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUnits();
  }, [page, searchTerm]);

  // ================= ADD =================
  const handleAddUnit = async () => {
    try {
      const res = await addItem(
        "unit",
        newUnit
      );

      if (res?.success === false) {
        setMessage(res.message);
        return false;
      }

      setMessage(
        "Unit added successfully"
      );

      setNewUnit({
        identifier: "",
        description: "",
        status: true,
      });

      await fetchUnits();

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
        "unit",
        editUnit
      );

      setUnits((prev) =>
        prev.map((u) =>
          u.identifier ===
          editUnit.identifier
            ? editUnit
            : u
        )
      );

      setEditUnit(null);
      setMessage("");
      await fetchUnits();
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
        "unit",
        identifier
      );

      setUnits((prev) =>
        prev.filter(
          (u) =>
            u.identifier !==
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

    setUnits((prev) =>
      prev.map((u) =>
        u.identifier === identifier
          ? {
              ...u,
              status: newStatus,
            }
          : u
      )
    );

    try {
      await toggleItem(
        "unit",
        identifier,
        newStatus
      );
      await fetchUnits();
    } catch (err) {
      console.error(err);

      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Toggle failed"
      );

      fetchUnits();
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
      render: (unit) => (
        <label
          htmlFor={`status-${unit.identifier}`}
          className="switch"
        >
          <input
            id={`status-${unit.identifier}`}
            type="checkbox"
            checked={
              !!unit.status
            }
            aria-label="Toggle unit status"
            onChange={() =>
              handleToggleStatus(
                unit.identifier,
                unit.status
              )
            }
          />

          <span className="slider"></span>
        </label>
      ),
    },
  ];

  // ================= ACTIONS =================
  const actions = [
    {
      label: "👁️",
      onClick: (row) => setviewUnit(row),
    },
    {
      label: "✏️",
      onClick: (row) =>
        setEditUnit({
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
    {
      label: "👁️",
      onClick: (row) => setviewUnit(row),
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
      title="Units"
      data={units}
      columns={columns}
      loading={loading}
      error={error}
      page={page}
      setPage={setPage}
      sizePerPage={sizePerPage}
      totalPages={totalPages}
      searchTerm={searchTerm}
      setSearchTerm={setSearchTerm}
      message={message}
      setMessage={setMessage}

      // ADD
      onAdd={() => {}}
      addButtonText="+ Add Unit"
      newItem={newUnit}
      setNewItem={setNewUnit}
      handleAdd={handleAddUnit}
      addFields={addFields}

      // EDIT
      editItem={editUnit}
      setEditItem={setEditUnit}
      handleUpdate={handleUpdate}
      editFields={editFields}

      // ACTIONS
      actions={actions}

      emptyMessage="No units found"
    />
    {viewUnit && (
      <div className="modalOverlay">
        <div className="viewModal">
          <div className="modalHeader">
            <h3>Unit Details</h3>

            <button
              className="closeBtn"
              onClick={() =>
                setviewUnit(null)
              }
            >
              ✕
            </button>
          </div>

          <div className="viewContent">
            <div className="viewRow">
              <span>Name</span>
              <strong>
                {viewUnit.identifier}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created By</span>
              <strong>
                {viewUnit.createdBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created On</span>
              <strong>
                {viewUnit.createdOn ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified By</span>
              <strong>
                {viewUnit.modifiedBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified On</span>
              <strong>
                {viewUnit.modifiedOn ||
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

export default UnitList;