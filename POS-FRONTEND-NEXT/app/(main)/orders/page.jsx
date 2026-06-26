"use client";

import { useEffect, useState } from "react";
import CommonList from "@/components/CommonList";

import {
  listItems,
  deleteItem,
  updateItem,
  addItem,
  getItem,
} from "@/services/api";

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [editOrder, setEditOrder] = useState(null);

  const [selectedOrder, setSelectedOrder] = useState(null);

  const [showSummary, setShowSummary] = useState(false);

  const sizePerPage = 5;

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await listItems("order", {
        page,
        sizePerPage,
        sortField: "createdAt",
        search,
      });

      let data = [];

      if (Array.isArray(res)) {
        data = res;
      } else if (Array.isArray(res?.content)) {
        data = res.content;
      }

      setOrders(data);

      setTotalPages(
        res?.totalPages ||
          Math.ceil((res?.totalRecords || data.length) / sizePerPage) ||
          1,
      );
    } catch (err) {
      console.error(err);
      setError("Failed to load orders");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [page, search]);

  const handleDelete = async (identifier) => {
    const confirmed = globalThis.confirm(`Delete order ${identifier}?`);

    if (!confirmed) return;

    try {
      await deleteItem("order", identifier, "identifier");

      fetchOrders();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  const handleUpdate = async () => {
    try {
      const payload = {
        ...editOrder,
      };

      delete payload.isNew;
      delete payload.formTitle;

      if (editOrder.isNew) {
        await addItem("order", payload);
      } else {
        await updateItem("order", payload);
      }

      await fetchOrders();
      setEditOrder(null);
    } catch (err) {
      console.error(err);
      alert("Failed to save order");
    }
  };

  const handleViewSummary = async (identifier) => {
    try {
      const order = await getItem("order", identifier);

      setSelectedOrder(order);
      setShowSummary(true);
    } catch (err) {
      console.error(err);
      alert("Failed to load order summary");
    }
  };

  const columns = [
    {
      label: "Identifier",
      render: (row) => (
        <button
          style={{
            border: "none",
            background: "none",
            color: "#1976d2",
            cursor: "pointer",
            fontWeight: "600",
          }}
          onClick={() => handleViewSummary(row.identifier)}
        >
          {row.identifier}
        </button>
      ),
    },
    {
      label: "Customer",
      key: "customer",
    },
    {
      label: "Total",
      render: (row) => `₹${Number(row.totalPrice || 0).toLocaleString()}`,
    },
    {
      label: "Order Status",
      key: "orderStatus",
    },
    {
      label: "Payment Method",
      key: "paymentMethod",
    },
    {
      label: "Created At",
      render: (row) =>
        row.createdAt ? new Date(row.createdAt).toLocaleString() : "-",
    },
  ];

  const actions = [
    {
      label: "✏️ Edit",
      onClick: (row) =>
        setEditOrder({
          ...row,
          isNew: false,
          formTitle: "Edit Order",
        }),
    },
    {
      label: "🗑 Delete",
      onClick: (row) => handleDelete(row.identifier),
    },
  ];

  const editFields = [
    {
      name: "identifier",
      label: "Identifier",
      disabled: !editOrder?.isNew,
    },
    {
      name: "customer",
      label: "Customer",
      disabled: true,
    },
    {
      name: "orderStatus",
      label: "Order Status",
      type: "text",
    },
    {
      name: "paymentMethod",
      label: "Payment Method",
      type: "text",
    },
  ];

  return (
    <>
      <CommonList
        title="Orders"
        data={orders}
        loading={loading}
        error={error}
        page={page}
        setPage={setPage}
        totalPages={totalPages}
        sizePerPage={sizePerPage}
        columns={columns}
        actions={actions}
        editItem={editOrder}
        setEditItem={setEditOrder}
        handleUpdate={handleUpdate}
        editFields={editFields}
        popupTitle={editOrder?.formTitle}
        search={search}
        setSearch={setSearch}
        emptyMessage="No orders found"
      />

      {showSummary && selectedOrder && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            background: "rgba(0,0,0,0.5)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 9999,
          }}
        >
          <div
            style={{
              width: "700px",
              maxHeight: "85vh",
              overflowY: "auto",
              background: "#fff",
              borderRadius: "12px",
              padding: "24px",
            }}
          >
            <h2>Order Summary</h2>

            <p>
              <strong>Order:</strong> {selectedOrder.identifier}
            </p>

            <p>
              <strong>Customer:</strong> {selectedOrder.customer}
            </p>

            <p>
              <strong>Status:</strong> {selectedOrder.orderStatus}
            </p>
            <p>
              <strong>Payment:</strong> {selectedOrder.paymentMethod}
            </p>

            <p>
              <strong>Created At:</strong>{" "}
              {selectedOrder.createdAt
                ? new Date(selectedOrder.createdAt).toLocaleString()
                : "-"}
            </p>

            <hr />

            <h3>Ordered Items</h3>

            {selectedOrder.items?.length > 0 ? (
              selectedOrder.items.map((item) => (
                <div
                  key={item.identifier}
                  style={{
                    border: "1px solid #e5e7eb",
                    borderRadius: "8px",
                    padding: "12px",
                    marginBottom: "10px",
                  }}
                >
                  <div>
                    <strong>Product:</strong> {item.product}
                  </div>

                  <div>Qty: {item.quantity}</div>

                  <div>
                    Unit Price: ₹{Number(item.unitPrice || 0).toLocaleString()}
                  </div>

                  <div>
                    Discount: ₹{Number(item.discount || 0).toLocaleString()}
                  </div>

                  <div>
                    Total: ₹{Number(item.totalPrice || 0).toLocaleString()}
                  </div>
                </div>
              ))
            ) : (
              <div>No items found</div>
            )}

            <hr />

            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                marginTop: "12px",
              }}
            >
              <strong>Subtotal</strong>

              <strong>
                ₹{Number(selectedOrder.subtotal || 0).toLocaleString()}
              </strong>
            </div>

            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                marginTop: "8px",
              }}
            >
              <strong>Discount</strong>

              <strong>
                ₹{Number(selectedOrder.discount || 0).toLocaleString()}
              </strong>
            </div>

            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                marginTop: "12px",
                fontSize: "20px",
                fontWeight: "700",
              }}
            >
              <span>Grand Total</span>

              <span>
                ₹{Number(selectedOrder.totalPrice || 0).toLocaleString()}
              </span>
            </div>

            <button
              onClick={() => setShowSummary(false)}
              style={{
                marginTop: "20px",
                width: "100%",
                padding: "12px",
                border: "none",
                borderRadius: "8px",
                background: "#1976d2",
                color: "#fff",
                cursor: "pointer",
                fontWeight: "600",
              }}
            >
              Close
            </button>
          </div>
        </div>
      )}
    </>
  );
}
