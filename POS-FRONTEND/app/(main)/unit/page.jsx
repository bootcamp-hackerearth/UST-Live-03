"use client";

import { useEffect, useState } from "react";
import CommonList from "@/app/components/CommonList/CommonList";
import AccessGuard from "@/app/components/AccessGuard";

import {
  listItems,
  addItem,
  updateItem,
  deleteItem,
} from "@/services/api";

const UnitPage = () => {
  const [units, setUnits] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const sizePerPage = 5;

  const [searchTerm, setSearchTerm] = useState("");

  const [newUnit, setNewUnit] = useState({
    identifier: "",
    description: "",
  });

  const [editUnit, setEditUnit] = useState(null);
  const [viewUnit, setViewUnit] = useState(null);

  const fetchUnits = async () => {
    try {
      if (units.length === 0) {
        setLoading(true);
      }

      setError("");

      const res = await listItems("unit", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      setUnits(res?.content || []);
      setTotalPages(res?.totalPages || 1);

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

  useEffect(() => {
    setPage(0);
  }, [searchTerm]);

  const handleAddUnit = async () => {
    const response = await addItem(
      "unit",
      newUnit
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    setNewUnit({
      identifier: "",
      description: "",
    });

    fetchUnits();

    return true;
  };

  const handleUpdate = async () => {
    const response = await updateItem(
      "unit",
      editUnit
    );

    if (response?.success === false) {
      throw new Error(response.message);
    }

    fetchUnits();

    return true;
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
        "unit",
        identifier
      );

      fetchUnits();

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
      label: "Unit",
      key: "identifier",
    },

    {
      label: "Description",
      key: "description",
    },
  ];

  const actions = [
    {
      label: "👁 View",
      type: "view",
      onClick: (row) =>
        setViewUnit(row),
    },

    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditUnit(row),
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
    <AccessGuard requiredPath="/unit">
      <CommonList
        title="Units"
        data={units}
        columns={columns}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        actions={actions}
        emptyMessage="No units found"

        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}

        onAdd={() => { }}
        addButtonText="+ Add Unit"

        newItem={newUnit}
        setNewItem={setNewUnit}
        handleAdd={handleAddUnit}

        addFields={[
          {
            name: "identifier",
            label: "Unit",
          },
          {
            name: "description",
            label: "Description",
          },
        ]}

        editItem={editUnit}
        setEditItem={setEditUnit}
        handleUpdate={handleUpdate}

        viewItem={viewUnit}
        setViewItem={setViewUnit}

        editFields={[
          {
            name: "identifier",
            label: "Unit",
            disabled: true,
          },
          {
            name: "description",
            label: "Description",
          },
        ]}
      />
    </AccessGuard>
  );
};

export default UnitPage;