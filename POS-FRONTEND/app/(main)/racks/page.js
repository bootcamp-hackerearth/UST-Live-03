"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";
import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
  toggleItem,
  getListItems,
} from "@/services/api";

const RacksList = () => {
  const [racks, setRacks] = useState([]);
  const [shelves, setShelves] = useState([]);

  const [message, setMessage] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [viewRacks, setviewRacks] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const sizePerPage = 5;

  // ================= ADD =================
  const [newRack, setNewRack] = useState({
    identifier: "",
    shelfs: [],
    status: true,
  });

  // ================= EDIT =================
  const [editRack, setEditRack] = useState(null);

  // ================= FETCH SHELVES =================
  const fetchShelves = async () => {
    try {
      const res = await getListItems("shelf");

      const data = Array.isArray(res)
        ? res
        : res?.content || [];

      setShelves(data);
    } catch (err) {
      console.error(err);
    }
  };

  // ================= FETCH RACKS =================
  const fetchRacks = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("racks", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = Array.isArray(res)
        ? res
        : res?.content || [];

      const normalized = data.map((rack) => ({
        ...rack,
        status:
          rack.status === true ||
          rack.status === 1 ||
          rack.status === "1",
      }));

      setRacks(normalized);

      setTotalPages(
        res?.totalPages ||
          Math.ceil(
            (res?.totalElements ||
              normalized.length) /
              sizePerPage
          ) ||
          1
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load racks");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchShelves();
  }, []);

  useEffect(() => {
    fetchRacks();
  }, [page, searchTerm]);

  // ================= ADD =================
  const handleAddRack = async () => {
    try {
      const res = await addItem(
        "racks",
        newRack
      );

      if (res?.success === false) {
        setMessage(res.message);
        return false;
      }

      setMessage(
        "Rack added successfully"
      );

      setNewRack({
        identifier: "",
        shelfs: [],
        status: true,
      });

      await fetchRacks();

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
        "racks",
        editRack
      );

      setRacks((prev) =>
        prev.map((r) =>
          r.identifier ===
          editRack.identifier
            ? editRack
            : r
        )
      );

      setEditRack(null);
      setMessage("");
      await fetchRacks();
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
        "racks",
        identifier
      );

      setRacks((prev) =>
        prev.filter(
          (r) =>
            r.identifier !==
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

  // ================= TOGGLE STATUS =================
  const handleToggleStatus = async (
    identifier
  ) => {
    try {
      await toggleItem(
        "racks",
        identifier
      );

      fetchRacks();
    } catch (err) {
      console.error(err);

      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Toggle failed"
      );
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
      label: "Shelves",
      render: (row) =>
        row.shelfs?.join(", ") ||
        "-",
    },

    {
      label: "Status",
      render: (rack) => (
        <label
          className="switch"
          htmlFor={`status-${rack.identifier}`}
          aria-label={`Toggle status for ${rack.identifier}`}
        >
          <input
            id={`status-${rack.identifier}`}
            type="checkbox"
            checked={!!rack.status}
            onChange={() =>
              handleToggleStatus(
                rack.identifier
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
      onClick: (row) => setviewRacks(row),
    },
    {
      label: "✏️",
      onClick: (row) =>
        setEditRack({
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

  const shelfOptions =
    shelves.map((shelf) => ({
      label: shelf.identifier,
      value: shelf.identifier,
    }));

  // ================= ADD FIELDS =================
  const addFields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      required: true,
    },

    {
      name: "shelfs",
      label: "Shelves",
      type: "select",
      multiple: true,
      required: true,
      options: shelfOptions,
    },
  ];

  // EDIT FIELDS 
  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
      disabled: true,
    },

    {
      name: "shelfs",
      label: "Shelves",
      type: "select",
      multiple: true,
      options: shelfOptions,
    },
  ];

  return (
    <>
    <CommonList
      title="Racks"
      data={racks}
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
      addButtonText="+ Add Rack"
      newItem={newRack}
      setNewItem={setNewRack}
      handleAdd={handleAddRack}
      addFields={addFields}

      // EDIT
      editItem={editRack}
      setEditItem={setEditRack}
      handleUpdate={handleUpdate}
      editFields={editFields}

      // ACTIONS
      actions={actions}

      emptyMessage="No racks found"
    />
    {viewRacks && (
      <div className="modalOverlay">
        <div className="viewModal">
          <div className="modalHeader">
            <h3>Racks Details</h3>

            <button
              className="closeBtn"
              onClick={() =>
                setviewRacks(null)
              }
            >
              ✕
            </button>
          </div>

          <div className="viewContent">
            <div className="viewRow">
              <span>Name</span>
              <strong>
                {viewRacks.identifier}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created By</span>
              <strong>
                {viewRacks.createdBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created On</span>
              <strong>
                {viewRacks.createdOn ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified By</span>
              <strong>
                {viewRacks.modifiedBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified On</span>
              <strong>
                {viewRacks.modifiedOn ||
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

export default RacksList;