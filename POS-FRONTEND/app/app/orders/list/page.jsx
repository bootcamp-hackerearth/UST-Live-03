"use client";
import { useState, useEffect, Suspense } from "react";
import PropTypes from "prop-types";
import { useSearchParams, useRouter } from "next/navigation";
import {
  fmt,
  fetchProducts,
  fetchCustomers,
  fetchOrders,
  getCustomerName,
  getProductName,
  getOrderEntries,
  makeShowError,
  statusBadgeStyle,
  paymentBadgeStyle,
  errorBannerStyle,
  ProductShape,
  CustomerShape,
  OrderShape,
} from "@/app/utils/orderUtils";

const styles = {
  page: { padding: "24px 32px", background: "#f9fafb", minHeight: "100vh" },
  topLabel: {
    fontSize: 12,
    fontWeight: 600,
    color: "#ef4444",
    letterSpacing: 1,
    textTransform: "uppercase",
    marginBottom: 4,
  },
  title: { fontSize: 24, fontWeight: 700, color: "#111", marginBottom: 24 },
  searchInput: {
    border: "1px solid #e5e7eb",
    borderRadius: 8,
    padding: "9px 16px",
    fontSize: 14,
    outline: "none",
    width: 280,
    background: "#fff",
    marginBottom: 16,
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
    background: "#fff",
    borderRadius: 12,
    overflow: "hidden",
    boxShadow: "0 1px 4px rgba(0,0,0,0.06)",
  },
  th: {
    padding: "12px 16px",
    textAlign: "left",
    fontSize: 12,
    fontWeight: 600,
    color: "#6b7280",
    borderBottom: "1px solid #f3f4f6",
    background: "#fff",
  },
  td: {
    padding: "14px 16px",
    fontSize: 14,
    color: "#111",
    borderBottom: "1px solid #f9fafb",
  },
  tdBold: {
    padding: "14px 16px",
    fontSize: 14,
    color: "#111",
    borderBottom: "1px solid #f9fafb",
    fontWeight: 600,
  },
  viewBtn: {
    padding: "6px 16px",
    borderRadius: 6,
    border: "1px solid #e5e7eb",
    background: "#fff",
    fontSize: 13,
    cursor: "pointer",
    fontWeight: 500,
  },
  loadingState: { padding: 60, textAlign: "center", color: "#9ca3af" },
  emptyState: { padding: 60, textAlign: "center", color: "#9ca3af" },
  modal: {
    position: "fixed",
    inset: 0,
    background: "rgba(0,0,0,0.4)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 100,
  },
  modalBox: {
    background: "#fff",
    borderRadius: 16,
    padding: 28,
    width: 660,
    maxHeight: "80vh",
    overflowY: "auto",
    boxShadow: "0 20px 60px rgba(0,0,0,0.2)",
  },
  modalHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "flex-start",
    marginBottom: 20,
  },
  modalTitle: { fontWeight: 700, fontSize: 17 },
  modalSub: { color: "#6b7280", fontSize: 13, marginTop: 2 },
  closeBtn: {
    background: "none",
    border: "none",
    fontSize: 20,
    cursor: "pointer",
    color: "#6b7280",
  },
  infoGrid: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr 1fr",
    gap: 12,
    marginBottom: 20,
  },
  infoBox: { background: "#f9fafb", borderRadius: 10, padding: "12px 16px" },
  infoLabel: {
    fontSize: 11,
    color: "#9ca3af",
    marginBottom: 4,
    textTransform: "uppercase",
    letterSpacing: 0.5,
  },
  infoValue: { fontSize: 14, fontWeight: 600, color: "#111" },
  infoValueGreen: { fontSize: 14, fontWeight: 600, color: "#16a34a" },
  entriesTable: { width: "100%", borderCollapse: "collapse" },
  entryTh: {
    padding: "10px 12px",
    textAlign: "left",
    fontSize: 12,
    color: "#6b7280",
    borderBottom: "1px solid #f3f4f6",
    fontWeight: 600,
  },
  entryTd: {
    padding: "10px 12px",
    fontSize: 13,
    borderBottom: "1px solid #f9fafb",
  },
  entryTdBold: {
    padding: "10px 12px",
    fontSize: 13,
    borderBottom: "1px solid #f9fafb",
    fontWeight: 500,
  },
  entryTdMrp: {
    padding: "10px 12px",
    fontSize: 13,
    borderBottom: "1px solid #f9fafb",
    color: "#9ca3af",
    textDecoration: "line-through",
  },
  entryTdDiscount: {
    padding: "10px 12px",
    fontSize: 13,
    borderBottom: "1px solid #f9fafb",
    color: "#16a34a",
  },
  entryTdSubtotal: {
    padding: "10px 12px",
    fontSize: 13,
    borderBottom: "1px solid #f9fafb",
    fontWeight: 600,
  },
  totalRow: {
    display: "flex",
    justifyContent: "space-between",
    padding: "10px 0",
    borderTop: "1px solid #f3f4f6",
    marginTop: 8,
  },
  totalRowBold: {
    display: "flex",
    justifyContent: "space-between",
    padding: "10px 0",
    borderTop: "1px solid #f3f4f6",
    marginTop: 8,
    fontWeight: 700,
    fontSize: 16,
  },
};

const ENTRY_HEADERS = [
  "#",
  "Product",
  "MRP",
  "Selling Price",
  "Qty",
  "Discount",
  "Subtotal",
];
const TABLE_HEADERS = [
  "SL",
  "Order No",
  "Customer",
  "Payment",
  "Total",
  "Status",
  "Action",
];

const INFO_FIELDS = (order, customerName) => [
  { label: "Customer", value: customerName },
  { label: "Total", value: `₹${fmt(order.totalPrice)}` },
  { label: "Items", value: getOrderEntries(order).length },
  { label: "Order ID", value: order.identifier },
];

function OrderDetailModal({ order, products, customers, onClose }) {
  const customerName = getCustomerName(customers, order.customer);

  return (
    <div style={styles.modal}>
      <div style={styles.modalBox}>
        <div style={styles.modalHeader}>
          <div>
            <p style={styles.modalTitle}>{order.identifier}</p>
            <p style={styles.modalSub}>Order Details</p>
          </div>
          <button type="button" style={styles.closeBtn} onClick={onClose}>
            ✕
          </button>
        </div>

        <div style={styles.infoGrid}>
          {INFO_FIELDS(order, customerName).map(({ label, value }) => (
            <div key={label} style={styles.infoBox}>
              <p style={styles.infoLabel}>{label}</p>
              <p style={styles.infoValue}>{value}</p>
            </div>
          ))}
          <div style={styles.infoBox}>
            <p style={styles.infoLabel}>Status</p>
            <span style={statusBadgeStyle(order.orderStatus)}>
              {order.orderStatus || "PLACED"}
            </span>
          </div>
          <div style={styles.infoBox}>
            <p style={styles.infoLabel}>Discount</p>
            <p style={styles.infoValueGreen}>- ₹{fmt(order.totalDiscount)}</p>
          </div>
        </div>

        <table style={styles.entriesTable}>
          <thead>
            <tr>
              {ENTRY_HEADERS.map((h) => (
                <th key={h} style={styles.entryTh}>
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {getOrderEntries(order).map((e, i) => (
              <tr key={e.identifier ?? i}>
                <td style={styles.entryTd}>{i + 1}</td>
                <td style={styles.entryTdBold}>
                  {getProductName(products, e.product)}
                </td>
                <td style={styles.entryTdMrp}>₹{fmt(e.price)}</td>
                <td style={styles.entryTd}>₹{fmt(e.sellingPrice)}</td>
                <td style={styles.entryTd}>{e.quantity}</td>
                <td style={styles.entryTdDiscount}>₹{fmt(e.discount)}</td>
                <td style={styles.entryTdSubtotal}>₹{fmt(e.totalPrice)}</td>
              </tr>
            ))}
          </tbody>
        </table>

        <div style={styles.totalRow}>
          <span style={{ color: "#6b7280" }}>Total Discount</span>
          <span style={{ color: "#16a34a", fontWeight: 600 }}>
            - ₹{fmt(order.totalDiscount)}
          </span>
        </div>
        <div style={styles.totalRowBold}>
          <span>Total Payable</span>
          <span>₹{fmt(order.totalPrice)}</span>
        </div>
      </div>
    </div>
  );
}

OrderDetailModal.propTypes = {
  order: OrderShape.isRequired,
  products: PropTypes.arrayOf(ProductShape),
  customers: PropTypes.arrayOf(CustomerShape),
  onClose: PropTypes.func.isRequired,
};

function OrderListContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const orderIdParam = searchParams.get("orderId");
  const isDirectOrderView = Boolean(orderIdParam);

  const [orders, setOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [errorMessage, setErrorMessage] = useState("");

  const showError = makeShowError(setErrorMessage);

  useEffect(() => {
    const token = localStorage.getItem("token");

    const loadAll = async () => {
      try {
        const productData = await fetchProducts(token);
        setProducts(productData.dtoList || []);
      } catch {}

      try {
        const customerData = await fetchCustomers(token);
        setCustomers(
          Array.isArray(customerData)
            ? customerData
            : customerData.dtoList || [],
        );
      } catch {}

      try {
        const orderData = await fetchOrders(token);
        setOrders(Array.isArray(orderData) ? orderData : []);
      } catch {
        showError("Failed to load orders");
      } finally {
        setLoading(false);
      }
    };

    loadAll();
  }, []);

  useEffect(() => {
    if (!orderIdParam || orders.length === 0) return;
    const found = orders.find(
      (o) => String(o.identifier) === String(orderIdParam),
    );
    if (found) setSelectedOrder(found);
  }, [orderIdParam, orders]);

  const handleCloseDirectView = () => {
    setSelectedOrder(null);
    router.push("/orders/list");
  };

  const filtered = search.trim()
    ? orders.filter((o) => {
        const q = search.toLowerCase();
        const name = getCustomerName(customers, o.customer).toLowerCase();
        return (
          (o.identifier || "").toLowerCase().includes(q) ||
          (o.customer || "").toLowerCase().includes(q) ||
          name.includes(q)
        );
      })
    : orders;

  if (isDirectOrderView) {
    const notFound = !loading && orders.length > 0 && !selectedOrder;
    let content;
    if (notFound) {
      content = (
        <div style={styles.loadingState}>
          <p>Order &quot;{orderIdParam}&quot; not found.</p>
          <button
            type="button"
            style={{ ...styles.viewBtn, marginTop: 12 }}
            onClick={handleCloseDirectView}
          >
            Back to Orders
          </button>
        </div>
      );
    } else if (loading || !selectedOrder) {
      content = <div style={styles.loadingState}>Loading order...</div>;
    } else {
      content = (
        <OrderDetailModal
          order={selectedOrder}
          products={products}
          customers={customers}
          onClose={handleCloseDirectView}
        />
      );
    }
    return <div style={styles.page}>{content}</div>;
  }

  return (
    <div style={styles.page}>
      {selectedOrder && (
        <OrderDetailModal
          order={selectedOrder}
          products={products}
          customers={customers}
          onClose={() => setSelectedOrder(null)}
        />
      )}
      <p style={styles.topLabel}>Sales History</p>
      <p style={styles.title}>Orders</p>
      {errorMessage && <p style={errorBannerStyle}>{errorMessage}</p>}
      <input
        type="search"
        placeholder="Search order, customer..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={styles.searchInput}
      />
      {loading ? (
        <div style={styles.loadingState}>Loading orders...</div>
      ) : (
        <table style={styles.table}>
          <thead>
            <tr>
              {TABLE_HEADERS.map((h) => (
                <th key={h} style={styles.th}>
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan={7} style={styles.emptyState}>
                  No orders found.
                </td>
              </tr>
            ) : (
              filtered.map((order, i) => (
                <tr
                  key={order.identifier ?? i}
                  style={{ background: i % 2 === 0 ? "#fff" : "#fafafa" }}
                >
                  <td style={styles.td}>{i + 1}</td>
                  <td style={styles.tdBold}>{order.identifier}</td>
                  <td style={styles.td}>
                    {getCustomerName(customers, order.customer)}
                  </td>
                  <td style={styles.td}>
                    <span style={paymentBadgeStyle("CASH")}>CASH</span>
                  </td>
                  <td style={styles.tdBold}>₹{fmt(order.totalPrice)}</td>
                  <td style={styles.td}>
                    <span style={statusBadgeStyle(order.orderStatus)}>
                      {order.orderStatus || "PLACED"}
                    </span>
                  </td>
                  <td style={styles.td}>
                    <button
                      type="button"
                      style={styles.viewBtn}
                      onClick={() => setSelectedOrder(order)}
                    >
                      View
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default function OrderListPage() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <OrderListContent />
    </Suspense>
  );
}
