"use client";

import { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
import { useSidebarOpen } from "@/components/useDropdownOptions";
import { C, searchInputSt, listSharedStyles } from "@/components/listColors";
import PaginationBar from "@/components/PaginationBar";

const inputSt = {
  width: "100%",
  height: "42px",
  padding: "10px 14px",
  border: `1.2px solid ${C.gray}`,
  borderRadius: "8px",
  fontSize: "13px",
  outline: "none",
  boxSizing: "border-box",
  backgroundColor: "#ffffff",
  color: C.text,
};

const labelSt = {
  fontSize: "11px",
  fontWeight: "700",
  color: C.muted,
  textTransform: "uppercase",
  letterSpacing: "0.6px",
};

const styles = {
  ...listSharedStyles,
  actionBtn: {
    padding: "6px 12px",
    borderRadius: "6px",
    fontSize: "11px",
    fontWeight: "600",
    cursor: "pointer",
    border: "none",
    transition: "all 0.2s ease",
  },
  actionView: {
    background: C.primary,
    color: "#fff",
    marginRight: "6px",
  },
  actionPrint: {
    background: C.white,
    color: C.primary,
    border: `1px solid ${C.primary}`,
  },
  emptyRow: {
    textAlign: "center",
    padding: "48px",
    color: C.muted,
    fontSize: "13px",
  },
  statsBar: {
    display: "flex",
    gap: "30px",
    padding: "12px 14px",
    borderTop: `1px solid ${C.gray}`,
    borderBottom: `1px solid ${C.gray}`,
    backgroundColor: C.white,
    flexShrink: 0,
  },
  statItem: {
    display: "flex",
    flexDirection: "column",
  },
  modal: {
    position: "fixed",
    top: 0,
    right: 0,
    bottom: 0,
    left: 0,
    background: "rgba(0,0,0,0.55)",
    zIndex: 10000,
    display: "flex",
    alignItems: "flex-start",
    justifyContent: "center",
    paddingTop: "80px",
    overflow: "auto",
  },
  modalContent: {
    width: "680px",
    maxHeight: "85vh",
    background: C.white,
    borderRadius: "12px",
    overflow: "hidden",
    display: "flex",
    flexDirection: "column",
    boxShadow: "0 8px 32px rgba(0,0,0,0.18)",
    marginBottom: "40px",
  },
  modalHeader: {
    padding: "18px 24px",
    borderBottom: `1px solid ${C.gray}`,
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    flexShrink: 0,
  },
  modalBody: {
    flex: 1,
    overflowY: "auto",
    padding: "24px",
    display: "flex",
    flexDirection: "column",
    gap: "20px",
  },
  modalFooter: {
    padding: "16px 24px",
    borderTop: `1px solid ${C.gray}`,
    display: "flex",
    justifyContent: "flex-end",
    gap: "10px",
    alignItems: "center",
    flexShrink: 0,
  },
};

export default function OrdersPage() {
  const router = useRouter();
  const isSidebarOpen = useSidebarOpen();

  const [orders, setOrders] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [ordersLoading, setOrdersLoading] = useState(false);
  const [ordersError, setOrdersError] = useState("");
  const [orderSearch, setOrderSearch] = useState("");
  const [pagination, setPagination] = useState({
    page: 0, sizePerPage: 5, sortDirection: "DESC", sortField: "id",
  });
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [orderDetailLoading, setOrderDetailLoading] = useState(false);
  const [showOrderDetail, setShowOrderDetail] = useState(false);
  const [products, setProducts] = useState([]);

  const today = new Date().toLocaleDateString("en-GB");

  const loadOrders = useCallback(async () => {
    setOrdersLoading(true);
    setOrdersError("");
    try {
      if (orderSearch.trim() === "") {
        const res = await api.post("/order/list", pagination, { skipErrorRedirect: [403] });
        const data = res.data;
        if (Array.isArray(data)) {
          setOrders(data);
          setTotalPages(1);
        } else {
          setOrders(data.dtoList ?? []);
          setTotalPages(data.totalPages ?? 1);
        }
      } else {
        const res = await api.post("/order/list", { ...pagination, page: 0, sizePerPage: 1000 }, { skipErrorRedirect: [403] });
        const allData = Array.isArray(res.data) ? res.data : (res.data.dtoList ?? []);
        const q = orderSearch.toLowerCase();
        setOrders(allData.filter(o =>
          (o.orderId || "").toLowerCase().includes(q) ||
          (o.identifier || "").toLowerCase().includes(q) ||
          (o.paymentMode || "").toLowerCase().includes(q)
        ));
        setTotalPages(1);
      }
    } catch (err) {
      const status = err.response?.status;
      if (status === 401) {
        setOrdersError("Session expired. Please log in again.");
        localStorage.removeItem("token");
        globalThis.location.href = "/login";
      } else if (status === 403) {
        setOrdersError("Access denied. Your account cannot access order data.");
      } else {
        setOrdersError("Failed to load orders. Please check your connection and try again.");
      }
      setOrders([]);
      setTotalPages(0);
    } finally {
      setOrdersLoading(false);
    }
  }, [pagination, orderSearch]);

  useEffect(() => {
    loadOrders();
  }, [loadOrders]);

  useEffect(() => {
    api.get("/product/findByStatus").then(res => setProducts(Array.isArray(res.data) ? res.data : (res.data?.dtoList ?? []))).catch(() => {});
  }, []);

  const fetchOrderDetail = async (orderId) => {
    setOrderDetailLoading(true);
    try {
      const res = await api.post("/order/getOrder", { orderId }, { skipErrorRedirect: [403] });
      setSelectedOrder(res.data);
      setShowOrderDetail(true);
    } catch (err) {
      const status = err.response?.status;
      if (status === 401) {
        setOrdersError("Session expired. Please log in again.");
        localStorage.removeItem("token");
        globalThis.location.href = "/login";
      } else if (status === 403) {
        setOrdersError("Access denied. You cannot view this order details.");
      } else {
        setOrdersError("Failed to load order details. Please try again.");
      }
    } finally {
      setOrderDetailLoading(false);
    }
  };

  const handleInvoicePrint = (order) => {
    const orderId = order.orderId || "—";
    const orderTotal = Number(order.totalPrice || 0).toFixed(2);
    const orderMode = order.paymentMode || "—";
    const orderTime = order.orderDate ? new Date(order.orderDate).toLocaleString("en-GB") : today;
    const itemsHtml = (order.entryDtoList || []).map(entry => {
      const prod = products.find(p => p.identifier === entry.product);
      const prodName = prod?.name || entry.product;
      const qty = entry.quantity || 1;
      const price = Number(entry.sellingPrice ?? 0).toFixed(2);
      const total = Number(entry.totalPrice ?? 0).toFixed(2);
      return `<div style="margin-bottom:8px;"><div style="font-weight:600;">${prodName}</div><div class="flex-space"><span>${qty} x Rs. ${price}</span><span>Rs. ${total}</span></div></div>`;
    }).join("");
    const winPrint = window.open("", "", "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0");
    if (!winPrint) return;
    const htmlContent = `
      <!DOCTYPE html>
      <html>
        <head>
          <title>Print Order Invoice</title>
          <style>
            body { margin: 20px; font-family: Courier, monospace; color: #000; background: #fff; }
            .receipt-wrap { width: 100%; max-width: 360px; margin: 0 auto; }
            .flex-space { display: flex; justify-content: space-between; margin-bottom: 5px; }
            .dashed-line { border-bottom: 1px dashed #000; margin: 12px 0; }
            .center-text { text-align: center; font-weight: 700; }
          </style>
        </head>
        <body>
          <div class="receipt-wrap">
            <div class="center-text" style="font-size: 22px; margin-bottom: 4px;">RECEIPT</div>
            <div class="center-text" style="font-size: 12px; margin-bottom: 16px;">${orderTime}</div>
            <div class="dashed-line"></div>
            <div class="flex-space"><span>Order No:</span><strong>${orderId}</strong></div>
            <div class="flex-space"><span>Customer:</span><span>${order.identifier || "—"}</span></div>
            <div class="flex-space"><span>Payment:</span><span>${orderMode}</span></div>
            <div class="dashed-line"></div>
            <div style="font-weight:700; margin-bottom:8px;">ITEMS</div>
            ${itemsHtml}
            <div class="dashed-line"></div>
            <div class="flex-space" style="font-size: 15px; font-weight: 700;"><span>Amount Paid:</span><span>Rs. ${orderTotal}</span></div>
            <div class="dashed-line"></div>
            <div class="center-text" style="font-style: italic; margin-top: 16px;">Thank you for your purchase!</div>
          </div>
        </body>
      </html>
    `;
    winPrint.document.open();
    winPrint.document.close();
    winPrint.document.documentElement.innerHTML = htmlContent;
    setTimeout(() => {
      winPrint.focus();
      winPrint.print();
      winPrint.close();
    }, 250);
  };

  function goToPage(pageIndex) {
    setPagination(prev => ({ ...prev, page: pageIndex }));
  }

  const currentPage = pagination.page;

  return (
    <div style={{ ...styles.page, left: isSidebarOpen ? "220px" : "55px" }}>
      <div style={styles.inner}>
        <div style={styles.topRow}>
          <button style={styles.backBtn} onClick={() => router.push("/home")}>⮜ Home</button>
          <h2 style={styles.title}>Orders</h2>
          <input
            style={searchInputSt}
            type="text"
            placeholder="Search by Order ID, Customer, Payment..."
            value={orderSearch}
            onChange={(e) => setOrderSearch(e.target.value)}
          />
        </div>

        <div style={styles.card}>
          {orders.length > 0 && (
            <div style={styles.statsBar}>
              <div style={styles.statItem}>
                <div style={labelSt}>Total Orders</div>
                <div style={{ fontSize: "18px", fontWeight: "700", color: C.text }}>
                  {orders.length}
                </div>
              </div>
              <div style={styles.statItem}>
                <div style={labelSt}>Total Revenue</div>
                <div style={{ fontSize: "18px", fontWeight: "700", color: C.text }}>
                  ₹{orders.reduce((sum, o) => sum + Number(o.totalPrice ?? 0), 0).toFixed(2)}
                </div>
              </div>
            </div>
          )}

          <div style={styles.tableWrap}>
            {(() => {
              if (ordersLoading) return <div style={styles.emptyRow}>Loading orders...</div>;
              if (ordersError) return <div style={{ ...styles.emptyRow, color: C.error }}>{ordersError}</div>;
              if (orders.length === 0) return <div style={styles.emptyRow}>No order records found.</div>;
              return (
                <table style={styles.table}>
                  <thead>
                    <tr>
                      <th style={styles.th}>Order ID</th>
                      <th style={styles.th}>Customer</th>
                      <th style={styles.th}>Date</th>
                      <th style={styles.th}>Method</th>
                      <th style={{ ...styles.th, textAlign: "center" }}>Items</th>
                      <th style={{ ...styles.th, textAlign: "right" }}>Amount</th>
                      <th style={{ ...styles.th, textAlign: "center" }}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                  {orders.map((order, idx) => (
                    <tr key={`${order.orderId || order.identifier || "order"}-${idx}`} style={styles.tr}>
                      <td style={styles.td}>{order.orderId}</td>
                      <td style={styles.td}>{order.identifier}</td>
                      <td style={styles.td}>
                        {order.orderDate ? new Date(order.orderDate).toLocaleDateString("en-GB") : "—"}
                      </td>
                      <td style={styles.td}>
                        <span style={{ padding: "2px 8px", background: "#f1f5f9", borderRadius: "4px", fontSize: "11px", fontWeight: "600" }}>
                          {order.paymentMode || "—"}
                        </span>
                      </td>
                      <td style={{ ...styles.td, textAlign: "center" }}>
                        {(order.entryDtoList || []).length}
                      </td>
                      <td style={{ ...styles.td, textAlign: "right", fontWeight: "700" }}>
                        ₹{Number(order.totalPrice ?? 0).toFixed(2)}
                      </td>
                      <td style={{ ...styles.td, textAlign: "center", display: "flex", justifyContent: "center", gap: "8px" }}>
                        <button
                          type="button"
                          onClick={() => fetchOrderDetail(order.orderId)}
                          disabled={orderDetailLoading}
                          style={{ ...styles.actionBtn, ...styles.actionView }}
                        >
                          View
                        </button>
                        <button
                          type="button"
                          onClick={() => handleInvoicePrint(order)}
                          style={{ ...styles.actionBtn, ...styles.actionPrint }}
                        >
                          Print
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              );
            })()}
          </div>

          {!ordersError && orderSearch.trim() === "" && (
            <PaginationBar currentPage={currentPage} totalPages={totalPages} onPageChange={goToPage} />
          )}
        </div>
      </div>

      {showOrderDetail && selectedOrder && (
        <div style={styles.modal}>
          <div style={styles.modalContent}>
            <div style={styles.modalHeader}>
              <div>
                <h2 style={{ margin: 0, fontSize: "16px", color: C.text, fontWeight: "700" }}>
                  Order Details — {selectedOrder.orderId}
                </h2>
                <span style={{ fontSize: "12px", color: C.muted }}>
                  Customer ID: {selectedOrder.identifier}
                </span>
              </div>
              <button
                onClick={() => {
                  setShowOrderDetail(false);
                  setSelectedOrder(null);
                }}
                style={{ background: "none", border: "none", cursor: "pointer", color: C.muted, fontSize: "20px", lineHeight: 1 }}
              >
                ✕
              </button>
            </div>
            <div style={styles.modalBody}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "12px" }}>
                {[
                  { label: "Order ID", value: selectedOrder.orderId },
                  { label: "Customer Phone", value: selectedOrder.identifier },
                  { label: "Payment Mode", value: selectedOrder.paymentMode || "—" },
                  { label: "Order Date", value: selectedOrder.orderDate ? new Date(selectedOrder.orderDate).toLocaleString("en-GB") : "—" },
                  { label: "Total Discount", value: `₹ ${Number(selectedOrder.totalDiscount ?? 0).toFixed(2)}` },
                  { label: "Grand Total", value: `₹ ${Number(selectedOrder.totalPrice ?? 0).toFixed(2)}` },
                ].map((item) => (
                  <div key={item.label} style={{ background: "#f8fafc", borderRadius: "8px", padding: "12px" }}>
                    <div style={{ fontSize: "11px", color: C.muted, fontWeight: "600", marginBottom: "4px" }}>
                      {item.label}
                    </div>
                    <div style={{ fontSize: "13px", color: C.text, fontWeight: "700" }}>{item.value}</div>
                  </div>
                ))}
              </div>
              <div>
                <div style={{ fontSize: "13px", fontWeight: "700", color: C.text, marginBottom: "10px" }}>
                  Order Items ({(selectedOrder.entryDtoList || []).length})
                </div>
                {(selectedOrder.entryDtoList || []).length === 0 ? (
                  <div style={{ padding: "20px", textAlign: "center", color: C.muted, fontSize: "13px" }}>
                    No items linked to this order.
                  </div>
                ) : (
                  <table style={styles.table}>
                    <thead>
                      <tr style={{ borderBottom: `1px solid ${C.gray}`, color: C.muted, textAlign: "left" }}>
                        <th style={{ padding: "8px 6px", fontSize: "11px", fontWeight: "600" }}>Product</th>
                        <th style={{ padding: "8px 6px", textAlign: "center", fontSize: "11px", fontWeight: "600" }}>Qty</th>
                        <th style={{ padding: "8px 6px", textAlign: "right", fontSize: "11px", fontWeight: "600" }}>Price</th>
                        <th style={{ padding: "8px 6px", textAlign: "right", fontSize: "11px", fontWeight: "600" }}>Discount</th>
                        <th style={{ padding: "8px 6px", textAlign: "right", fontSize: "11px", fontWeight: "600" }}>Total</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(selectedOrder.entryDtoList || []).map((entry, idx) => {
                        const entryProd = products.find(p => p.identifier === entry.product);
                        return (
                          <tr key={entry.identifier || idx} style={{ borderBottom: `1px solid ${C.gray}`, color: C.text }}>
                            <td style={{ padding: "10px 6px" }}>
                              <div style={{ fontWeight: "600" }}>{entryProd?.name || entry.product}</div>
                              <div style={{ fontSize: "11px", color: C.muted }}>ID: {entry.product}</div>
                            </td>
                            <td style={{ padding: "10px 6px", textAlign: "center", fontWeight: "600" }}>
                              {entry.quantity}
                            </td>
                            <td style={{ padding: "10px 6px", textAlign: "right" }}>
                              ₹{Number(entry.sellingPrice ?? 0).toFixed(2)}
                            </td>
                            <td style={{ padding: "10px 6px", textAlign: "right", color: C.error }}>
                              -₹{Math.abs(Number(entry.discount ?? 0)).toFixed(2)}
                            </td>
                            <td style={{ padding: "10px 6px", textAlign: "right", fontWeight: "700" }}>
                              ₹{Number(entry.totalPrice ?? 0).toFixed(2)}
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                    <tfoot>
                      <tr style={{ borderTop: `1px solid ${C.gray}` }}>
                        <td colSpan={4} style={{ ...styles.td, textAlign: "right", fontWeight: "700", color: C.text }}>
                          Grand Total
                        </td>
                        <td style={{ ...styles.td, textAlign: "right", fontWeight: "700", fontSize: "15px", color: C.text }}>
                          ₹{Number(selectedOrder.totalPrice ?? 0).toFixed(2)}
                        </td>
                      </tr>
                    </tfoot>
                  </table>
                )}
              </div>
            </div>
            <div style={styles.modalFooter}>
              <button
                type="button"
                onClick={() => handleInvoicePrint(selectedOrder)}
                style={{ ...styles.actionBtn, ...styles.actionView, background: C.secondary }}
              >
                🖨 Print Invoice
              </button>
              <button
                type="button"
                onClick={() => {
                  setShowOrderDetail(false);
                  setSelectedOrder(null);
                }}
                style={{ ...styles.actionBtn, ...styles.actionView }}
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
