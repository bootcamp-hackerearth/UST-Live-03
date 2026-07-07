"use client";

import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import api from "../services/api";
import OrderReceiptModal from "./OrderReceiptModal";

export default function OrderHistory() {
  const [orders, setOrders] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [search, setSearch] = useState("");

  useEffect(() => {
    loadOrderHistory();
  }, []);

  const loadOrderHistory = async () => {
    try {
      const res = await api.post("/api/order/list", {
        page: 0,
        sizePerPage: 50,
        sortField: "id",
        sortDirection: "DESC",
      });

      setOrders(res.data.dtoList || []);
    } catch (err) {
      console.error("HISTORY LOOKUP ERROR =>", err);
    }
  };

  const handleViewOrderDetails = async (identifier) => {
    try {
      const res = await api.get(
        `/api/order/get?identifier=${identifier}`
      );

      setSelectedOrder(res.data);
    } catch (err) {
      console.error(err);
      alert("Failed to pull individual order entries.");
    }
  };

  const filteredOrders = orders.filter((o) => {
    const keyword = search.toLowerCase().trim();

    if (!keyword) return true;

    return o.identifier?.toLowerCase().includes(keyword);
  });

  return (
    <Layout>
      <div
        style={{
          width: "100%",
          minHeight: "100vh",
          backgroundColor: "#f8fafc",
          padding: "24px",
          boxSizing: "border-box",
          fontFamily: "system-ui, sans-serif",
        }}
      >
        <div
          style={{
            maxWidth: "1600px",
            margin: "0 auto",
          }}
        >
          <div
            style={{
              backgroundColor: "#ffffff",
              padding: "24px",
              borderRadius: "16px",
              border: "1px solid #e2e8f0",
            }}
          >
            <h3 style={{ margin: "0 0 16px 0" }}>
              Historical Settled Order Receipts
            </h3>

            <div style={{ marginBottom: "16px" }}>
              <input
                type="text"
                placeholder="Search by Order ID"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                style={{
                  width: "100%",
                  maxWidth: "400px",
                  padding: "10px 12px",
                  border: "1px solid #cbd5e1",
                  borderRadius: "8px",
                  outline: "none",
                  fontSize: "14px",
                }}
              />
            </div>

            {filteredOrders.length === 0 ? (
              <div
                style={{
                  color: "#94a3b8",
                  textAlign: "center",
                  padding: "20px",
                }}
              >
                No documented orders found in backend database archive.
              </div>
            ) : (
              <div
                style={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "8px",
                }}
              >
                {filteredOrders.map((o) => (
                  <div
                    key={o.identifier}
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      padding: "12px",
                      border: "1px solid #e2e8f0",
                      borderRadius: "8px",
                    }}
                  >
                    <div>
                      <span
                        style={{
                          fontWeight: "700",
                          color: "#2563eb",
                        }}
                      >
                        {o.identifier}
                      </span>

                      <div
                        style={{
                          fontSize: "11px",
                          color: "#64748b",
                        }}
                      >
                        Method: {o.paymentMethod} | Target:{" "}
                        {o.customerIdentifier}
                      </div>
                    </div>

                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "12px",
                      }}
                    >
                      <span style={{ fontWeight: "800" }}>
                        ₹{o.totalPrice}
                      </span>

                      <button
                        onClick={() =>
                          handleViewOrderDetails(o.identifier)
                        }
                        style={{
                          padding: "4px 10px",
                          cursor: "pointer",
                        }}
                      >
                        View Summary
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <OrderReceiptModal
            order={selectedOrder}
            onClose={() => setSelectedOrder(null)}
          />
        </div>
      </div>
    </Layout>
  );
}