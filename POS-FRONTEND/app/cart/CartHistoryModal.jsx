"use client";
import { useState, useEffect } from "react";
import PropTypes from "prop-types";

const fmt = (n) =>
  Number(n || 0).toLocaleString("en-IN", { minimumFractionDigits: 2 });

const getCustomerName = (customers, customerId) =>
  customers.find((c) => c.identifier === customerId)?.name || customerId;

const getProductName = (products, productId) =>
  products.find((p) => p.identifier === productId)?.productName || productId;

const apiFetch = async (url, token, options = {}) => {
  const { headers: customHeaders, ...restOptions } = options;

  const response = await fetch(url, {
    ...restOptions,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      ...customHeaders,
    },
  });

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }
  return response.json();
};

const styles = {
  overlay: {
    position: "fixed",
    inset: 0,
    background: "rgba(0,0,0,0.4)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 100,
  },
  modal: {
    background: "#fff",
    borderRadius: 16,
    padding: 28,
    width: 720,
    maxHeight: "80vh",
    overflowY: "auto",
    boxShadow: "0 20px 60px rgba(0,0,0,0.2)",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 16,
  },
  title: { fontWeight: 700, fontSize: 18 },
  closeBtn: {
    background: "none",
    border: "none",
    fontSize: 20,
    cursor: "pointer",
    color: "#6b7280",
  },
  searchInput: {
    width: "100%",
    border: "1px solid #e5e7eb",
    borderRadius: 8,
    padding: "9px 14px",
    fontSize: 14,
    outline: "none",
    marginBottom: 16,
  },
  loadingState: { padding: 40, textAlign: "center", color: "#9ca3af" },
  emptyState: { padding: 40, textAlign: "center", color: "#9ca3af" },
  orderCard: {
    border: "1px solid #e5e7eb",
    borderRadius: 12,
    marginBottom: 10,
    overflow: "hidden",
  },
  orderCardHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "12px 16px",
    background: "#f9fafb",
    cursor: "pointer",
    border: "none",
    textAlign: "left",
    width: "100%",
  },
  orderLeft: { display: "flex", flexDirection: "column", gap: 2 },
  orderIdText: { fontWeight: 600, fontSize: 14 },
  customerText: { fontSize: 12, color: "#6b7280" },
  orderMeta: { display: "flex", gap: 12, alignItems: "center" },
  totalText: { fontWeight: 700, fontSize: 14 },
  entriesTable: { width: "100%", borderCollapse: "collapse" },
  entryTh: {
    padding: "8px 16px",
    textAlign: "left",
    fontSize: 12,
    color: "#6b7280",
    borderBottom: "1px solid #f3f4f6",
    fontWeight: 600,
  },
  entryTd: {
    padding: "10px 16px",
    fontSize: 13,
    borderBottom: "1px solid #f9fafb",
  },
  discountTd: {
    padding: "10px 16px",
    fontSize: 13,
    borderBottom: "1px solid #f9fafb",
    color: "#16a34a",
  },
  subtotalTd: {
    padding: "10px 16px",
    fontSize: 13,
    borderBottom: "1px solid #f9fafb",
    fontWeight: 600,
  },
};

const STATUS_STYLES = {
  CANCELLED: { background: "#fee2e2", color: "#dc2626" },
  DEFAULT: { background: "#dcfce7", color: "#16a34a" },
};

const badgeStyle = (status) => ({
  display: "inline-block",
  padding: "2px 10px",
  borderRadius: 99,
  fontSize: 12,
  fontWeight: 600,
  ...(STATUS_STYLES[status] || STATUS_STYLES.DEFAULT),
});

const ORDER_TABLE_HEADERS = [
  "Product",
  "Qty",
  "Selling Price",
  "Discount",
  "Subtotal",
];

function StatusMessage({ type, message }) {
  return <div style={styles[type]}>{message}</div>;
}

StatusMessage.propTypes = {
  type: PropTypes.oneOf(["loadingState", "emptyState"]).isRequired,
  message: PropTypes.string.isRequired,
};

function OrderEntryRow({ entry, products }) {
  return (
    <tr>
      <td style={styles.entryTd}>{getProductName(products, entry.product)}</td>
      <td style={styles.entryTd}>{entry.quantity}</td>
      <td style={styles.entryTd}>₹{fmt(entry.sellingPrice)}</td>
      <td style={styles.discountTd}>₹{fmt(entry.discount)}</td>
      <td style={styles.subtotalTd}>₹{fmt(entry.totalPrice)}</td>
    </tr>
  );
}

OrderEntryRow.propTypes = {
  entry: PropTypes.object.isRequired,
  products: PropTypes.array.isRequired,
};

function OrderCard({ order, products, customers }) {
  const [expanded, setExpanded] = useState(false);
  const customerName = getCustomerName(customers, order.customer);

  return (
    <div style={styles.orderCard}>
      <button
        type="button"
        style={styles.orderCardHeader}
        onClick={() => setExpanded((prev) => !prev)}
        aria-pressed={expanded}
      >
        <div style={styles.orderLeft}>
          <span style={styles.orderIdText}>{order.identifier}</span>
          <span style={styles.customerText}>{customerName}</span>
        </div>
        <div style={styles.orderMeta}>
          <span style={badgeStyle(order.orderStatus)}>
            {order.orderStatus || "PLACED"}
          </span>
          <span style={styles.totalText}>₹{fmt(order.totalPrice)}</span>
          <span style={{ color: "#6b7280", fontSize: 13 }}>
            {expanded ? "▲" : "▼"}
          </span>
        </div>
      </button>

      {expanded && (
        <table style={styles.entriesTable}>
          <thead>
            <tr>
              {ORDER_TABLE_HEADERS.map((h) => (
                <th key={h} style={styles.entryTh}>
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {(order.orderEntryDtoList || []).map((entry, i) => (
              <OrderEntryRow
                key={entry.identifier ?? i}
                entry={entry}
                products={products}
              />
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

OrderCard.propTypes = {
  order: PropTypes.object.isRequired,
  products: PropTypes.array.isRequired,
  customers: PropTypes.array.isRequired,
};

export default function CartHistoryModal({ products, onClose }) {
  const [orders, setOrders] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");

    const loadData = async () => {
      try {
        const customerData = await apiFetch(
          "http://localhost:8080/api/customer/list",
          token,
          {
            method: "POST",
            body: JSON.stringify({ page: 0, sizePerPage: 500 }),
          },
        );
        setCustomers(
          Array.isArray(customerData)
            ? customerData
            : customerData.dtoList || [],
        );
      } catch {}

      try {
        const orderData = await apiFetch(
          "http://localhost:8080/api/order/list",
          token,
          { method: "GET" },
        );
        setOrders(Array.isArray(orderData) ? orderData : []);
      } catch {
        setOrders([]);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

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

  const renderContent = () => {
    if (loading)
      return <StatusMessage type="loadingState" message="Loading history..." />;
    if (!filtered.length)
      return <StatusMessage type="emptyState" message="No orders found." />;

    return filtered.map((order, i) => (
      <OrderCard
        key={order.identifier ?? i}
        order={order}
        products={products || []}
        customers={customers}
      />
    ));
  };

  return (
    <div style={styles.overlay}>
      <div style={styles.modal}>
        <div style={styles.header}>
          <p style={styles.title}>🕐 Order History</p>
          <button type="button" style={styles.closeBtn} onClick={onClose}>
            ✕
          </button>
        </div>
        <input
          type="search"
          placeholder="Search by order ID, customer name or phone..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={styles.searchInput}
        />
        {renderContent()}
      </div>
    </div>
  );
}

CartHistoryModal.propTypes = {
  products: PropTypes.array,
  onClose: PropTypes.func.isRequired,
};
