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

const StockList = () => {
  const [stocks, setStocks] = useState([]);
  const [message, setMessage] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [products, setProducts] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [warehouses, setWarehouses] = useState([]);

  const [viewStock, setviewStock] = useState(null);

  const sizePerPage = 5;

  // ================= ADD =================
  const [newStock, setNewStock] = useState({
    identifier: "",
    quantity: 0,
    stockStatus: "",
    status: true,
  });

  // ================= EDIT =================
  const [editStock, setEditStock] = useState(null);

  // ================= FETCH =================
  const fetchWarehouses = async () => {
  try {
    const res = await listItems("warehouse", {
      page: 0,
      sizePerPage: 1000,
      sortField: "id",
    });

    const data = Array.isArray(res)
      ? res
      : res?.content || [];

    setWarehouses(data);
  } catch (err) {
    console.error(err);
  }
};

const fetchProducts = async () => {
  try {
    const res = await listItems("product", {
      page: 0,
      sizePerPage: 1000,
      sortField: "id",
    });

    const data = Array.isArray(res)
      ? res
      : res?.content || [];

    setProducts(data);
  } catch (err) {
    console.error(err);
  }
};

// Load products once
useEffect(() => {
  fetchProducts();
}, []);

const productOptions = products.map((p) => ({
  label: p.identifier,
  value: p.identifier,
}));

const warehouseOptions = warehouses.map((w) => ({
  label: w.identifier,
  value: w.identifier,
}));



  const fetchStocks = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("stock", {
        page,
        sizePerPage,
        sortField: "id",
        search: searchTerm,
      });

      const data = Array.isArray(res)
        ? res
        : res?.content || [];

      const normalized = data.map((stock) => ({
        ...stock,
        status:
          stock.status === true ||
          stock.status === 1 ||
          stock.status === "1",
      }));

      setStocks(normalized);

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
      setError("Failed to load stocks");
    } finally {
      setLoading(false);
    }
  };

 useEffect(() => {
  fetchProducts();
  fetchWarehouses();
}, []);

useEffect(() => {
  fetchStocks();
}, [page, searchTerm]);

  // ================= ADD =================
  const handleAddStock = async () => {
    try {
      const res = await addItem(
        "stock",
        newStock
      );

      if (res?.success === false) {
        setMessage(res.message);
        return false;
      }

      setMessage(
        "Stock added successfully"
      );

      setNewStock({
        identifier: "",
        quantity: 0,
        stockStatus: "",
        status: true,
      });

      await fetchStocks();

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
        "stock",
        editStock
      );

      setStocks((prev) =>
        prev.map((s) =>
          s.identifier ===
          editStock.identifier
            ? editStock
            : s
        )
      );

      setEditStock(null);
      setMessage("");
      await fetchStocks();
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
        "stock",
        identifier
      );

      setStocks((prev) =>
        prev.filter(
          (s) =>
            s.identifier !==
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

    setStocks((prev) =>
      prev.map((s) =>
        s.identifier ===
        identifier
          ? {
              ...s,
              status: newStatus,
            }
          : s
      )
    );

    try {
      await toggleItem(
        "stock",
        identifier,
        newStatus
      );
      await fetchStocks();
    } catch (err) {
      console.error(err);

      setMessage(
        err?.response?.data?.message ||
          err?.message ||
          "Toggle failed"
      );

      fetchStocks();
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
    label: "Product",
    key: "identifier",
  },
  {
    label: "Warehouse",
    key: "warehouseName",
  },
  {
    label: "Quantity",
    key: "quantity",
  },
  {
    label: "Stock Status",
    key: "stockStatus",
  },
  {
    label: "Status",
      render: (stock) => (
      <label className="switch">
        <input
          type="checkbox"
          aria-label={`Toggle status for ${stock?.identifier ?? 'item'}`}
          checked={!!stock.status}
          onChange={() =>
            handleToggleStatus(
              stock.identifier,
              stock.status
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
      onClick: (row) => setviewStock(row),
    },
    {
      label: "✏️",
      onClick: (row) =>
        setEditStock({
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
    label: "Product",
    type: "select",
    required: true,
    options: productOptions,
  },

  {
    name: "warehouseName",
    label: "Warehouse",
    type: "select",
    required: true,
    options: warehouseOptions,
  },

  {
    name: "quantity",
    label: "Quantity",
    type: "number",
    required: true,
  },
];

  // ================= EDIT FIELDS =================
 const editFields = [
  {
    name: "identifier",
    label: "Product",
    type: "text",
    disabled: true,
  },

  {
    name: "warehouseName",
    label: "Warehouse",
    type: "select",
    options: warehouseOptions,
  },

  {
    name: "quantity",
    label: "Quantity",
    type: "number",
  },
];

  return (
    <>
    <CommonList
      title="Stocks"
      data={stocks}
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
      addButtonText="+ Add Stock"
      newItem={newStock}
      setNewItem={setNewStock}
      handleAdd={handleAddStock}
      addFields={addFields}

      // EDIT
      editItem={editStock}
      setEditItem={setEditStock}
      handleUpdate={handleUpdate}
      editFields={editFields}

      // ACTIONS
      actions={actions}

      emptyMessage="No stocks found"
    />
    {viewStock && (
      <div className="modalOverlay">
        <div className="viewModal">
          <div className="modalHeader">
            <h3>Stock Details</h3>

            <button
              className="closeBtn"
              onClick={() =>
                setviewStock(null)
              }
            >
              ✕
            </button>
          </div>

          <div className="viewContent">
            <div className="viewRow">
              <span>Name</span>
              <strong>
                {viewStock.identifier}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created By</span>
              <strong>
                {viewStock.createdBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Created On</span>
              <strong>
                {viewStock.createdOn ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified By</span>
              <strong>
                {viewStock.modifiedBy ||
                  "-"}
              </strong>
            </div>

            <div className="viewRow">
              <span>Modified On</span>
              <strong>
                {viewStock.modifiedOn ||
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

export default StockList;