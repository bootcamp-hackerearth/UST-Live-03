"use client";

import { Suspense, useEffect, useState, useRef } from "react";
import api from "../api";
import { useSearchParams, useRouter } from "next/navigation";

 function OrderContent() {
  const searchParams = useSearchParams();
  const router = useRouter();

  const [orders, setOrders] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [isPlacingOrder, setIsPlacingOrder] = useState(false);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const orderPlacedRef = useRef(false);

  const cartId = searchParams.get("cartId");
  const customerIdentifier = searchParams.get("customerIdentifier");
  const paymentMethod = searchParams.get("paymentMethod");

  useEffect(() => {
    const handleOrderAndLoad = async () => {
      if (cartId && customerIdentifier && paymentMethod && !orderPlacedRef.current) {
        orderPlacedRef.current = true;
        setIsPlacingOrder(true);

        try {
          await api.post("/order/add", {
            cartIdentifier: cartId,
            customerIdentifier,
            paymentMethod,
          });

          alert("Order placed successfully!");
          router.replace("/order");
        } catch (err) {
          console.error(err);
          alert("Failed to place order.");
        } finally {
          setIsPlacingOrder(false);
        }
      }

      loadOrders();
    };

    handleOrderAndLoad();
  }, [cartId, customerIdentifier, paymentMethod, router, page]);

  const loadOrders = async () => {
    try {
      const res = await api.post("/order/list", {
        page: page,
        sizePerPage: 10,
      });

      setOrders(res.data.dtoList || []);
      setTotalPages(res.data.totalPages || 0);
    } catch (err) {
      console.error(err);
    }
  };

  const loadOrderDetails = async (identifier) => {
    try {
      const res = await api.get("/order/get", {
        params: { identifier },
      });

      setSelectedOrder(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div
      style={{
        padding: 24,
        background: "#f5f7fb",
        minHeight: "100vh",
      }}
    >

      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          marginBottom: 30,
        }}
      >
        <div>
          <h1 style={{ fontSize: 36, fontWeight: 700 }}>
            Orders
          </h1>

          <p style={{ color: "#6b7280" }}>
            {isPlacingOrder
              ? "Processing your new order..."
              : "View all placed orders"}
          </p>
        </div>

        <div
          style={{
            background: "#fff",
            padding: 20,
            borderRadius: 16,
            border: "1px solid #e5e7eb",
            width: 150,
            textAlign: "center",
          }}
        >
          <div style={{ color: "#6b7280" }}>
            Total Orders
          </div>

          <div style={{ fontSize: 30, fontWeight: 700 }}>
            {orders.length}
          </div>
        </div>
      </div>

      <div
        style={{
          background: "#fff",
          borderRadius: 20,
          overflow: "hidden",
          border: "1px solid #e5e7eb",
        }}
      >
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead style={{ background: "#f9fafb" }}>
            <tr>
              <th style={{ padding: 16 }}>Order No</th>
              <th style={{ padding: 16 }}>Customer</th>
              <th style={{ padding: 16 }}>Payment</th>
              <th style={{ padding: 16 }}>Original</th>
              <th style={{ padding: 16 }}>Discount</th>
              <th style={{ padding: 16 }}>Total</th>
              <th style={{ padding: 16 }}>Date</th>
            </tr>
          </thead>

          <tbody>
            {orders.map((order) => (
              <tr key={order.identifier} style={{ borderTop: "1px solid #eee" }}>
                <td style={{ padding: 16 }}>{order.identifier}</td>

                <td style={{ padding: 16 }}>
                  <button
                    onClick={() => loadOrderDetails(order.identifier)}
                    style={{
                      border: "none",
                      background: "transparent",
                      color: "#2563eb",
                      cursor: "pointer",
                    }}
                  >
                    {order.customerEmail ||
                      `Customer #${order.customerIdentifier}`}
                  </button>
                </td>

                <td style={{ padding: 16 }}>{order.paymentMethod}</td>
                <td style={{ padding: 16 }}>₹{order.originalPrice}</td>
                <td style={{ padding: 16, color: "green" }}>
                  ₹{order.discount}
                </td>
                <td style={{ padding: 16, fontWeight: 700 }}>
                  ₹{order.totalPrice}
                </td>
                <td style={{ padding: 16 }}>
                  {order.orderPlacedTime
                    ? new Date(order.orderPlacedTime).toLocaleString()
                    : ""}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div
        style={{
          display: "flex",
          justifyContent: "center",
          gap: 10,
          padding: 20,
        }}
      >
        <button
  onClick={() => setPage((p) => Math.max(p - 1, 0))}
  disabled={page === 0}
  style={{
    background: page === 0 ? "#9ca3af" : "#2563eb",
    color: "#fff",
    padding: "8px 16px",
    borderRadius: 6,
    cursor: page === 0 ? "not-allowed" : "pointer",
    border: "none",
  }}
>
  Prev
</button>

<span>
  Page {page + 1} of {totalPages}
</span>

<button
  onClick={() =>
    setPage((p) => Math.min(p + 1, totalPages - 1))
  }
  disabled={page >= totalPages - 1}
  style={{
    background: page >= totalPages - 1 ? "#9ca3af" : "#16a34a",
    color: "#fff",
    padding: "8px 16px",
    borderRadius: 6,
    cursor: page >= totalPages - 1 ? "not-allowed" : "pointer",
    border: "none",
  }}
>
  Next
</button>
      </div>

      {selectedOrder && (
        <div
          style={{
            position: "fixed",
            inset: 0,
            background: "rgba(0,0,0,0.4)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 999,
          }}
        >
          <div
            id="print-area"
            style={{
              width: "900px",
              maxHeight: "90vh",
              overflowY: "auto",
              background: "#fff",
              borderRadius: 20,
              padding: 24,
            }}
          >
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 20 }}>
              <h2>Order Details</h2>

              <button onClick={() => setSelectedOrder(null)}>
                ✕
              </button>
            </div>

            <table
              style={{
                width: "100%",
                borderCollapse: "collapse",
              }}
            >
              <thead>
                <tr>
                  <th style={thStyle}>Product</th>
                  <th style={thStyle}>Qty</th>
                  <th style={thStyle}>MRP</th>
                  <th style={thStyle}>Price</th>
                  <th style={thStyle}>Discount</th>
                  <th style={thStyle}>Total</th>
                </tr>
              </thead>

              <tbody>
                {selectedOrder.entryList?.map((item) => (
  <tr
    key={
      item.identifier ||
      `${item.productIdentifier}-${item.quantity}-${item.unitPrice}`
    }
  >
                    <td style={tdStyle}>{item.productIdentifier}</td>
                    <td style={tdStyle}>{item.quantity}</td>
                    <td style={tdStyle}>₹{item.originalPrice}</td>
                    <td style={tdStyle}>₹{item.unitPrice}</td>
                    <td style={tdStyle}>₹{item.discount}</td>
                    <td style={tdStyle}>₹{item.totalPrice}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div style={{ textAlign: "right", marginTop: 24 }}>
              <p>Original Price: ₹{selectedOrder.originalPrice}</p>
              <p>Discount: ₹{selectedOrder.discount}</p>
              <h2>Total: ₹{selectedOrder.totalPrice}</h2>
            </div>

            <div
              style={{
                marginTop: 20,
                display: "flex",
                justifyContent: "flex-end",
                gap: 10,
              }}
            >
              <button
               onClick={() => globalThis.print()}
                style={{
                  background: "#111827",
                  color: "#fff",
                  padding: "10px 20px",
                  borderRadius: 8,
                }}
              >
                🖨 Print
              </button>

              <button
                onClick={() => setSelectedOrder(null)}
                style={{
                  padding: "10px 20px",
                  borderRadius: 8,
                }}
              >
                Close
              </button>
            </div>

          </div>
        </div>
      )}

    </div>
  );
}

const thStyle = {
  padding: "12px",
  textAlign: "left",
  borderBottom: "1px solid #ccc",
};

const tdStyle = {
  padding: "12px",
  textAlign: "left",
  borderBottom: "1px solid #eee",
};

export default function OrderPage() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <OrderContent />
    </Suspense>
  );
}
