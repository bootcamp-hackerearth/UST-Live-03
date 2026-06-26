"use client";
import PropTypes from "prop-types";
import { Suspense, useState, useEffect } from "react";
import { useSearchParams } from "next/navigation";
import {
  fmt,
  fetchProducts,
  fetchCustomers,
  fetchOrders,
  getProductName,
  getOrderEntries,
  makeShowError,
  badgeStyle,
  errorBannerStyle,
  EntryShape,
  ProductShape,
  CustomerShape,
  OrderShape,
} from "@/app/utils/orderUtils";

const styles = {
  page: { padding: 20, background: "#f5f5f5", minHeight: "100vh" },
  header: { display: "flex", alignItems: "center", gap: 12, marginBottom: 20 },
  searchBar: {
    width: "100%",
    maxWidth: 360,
    border: "1px solid #d1d5db",
    borderRadius: 8,
    padding: "10px 14px",
    fontSize: 14,
    outline: "none",
    background: "#fff",
    marginBottom: 20,
  },
  tableWrapper: {
    background: "#fff",
    borderRadius: 12,
    overflow: "hidden",
    border: "1px solid #e5e7eb",
  },
  tableHeader: {
    background: "#111",
    color: "#fff",
    padding: "12px",
    textAlign: "center",
  },
  tableCell: {
    padding: "10px",
    border: "1px solid #e5e7eb",
    textAlign: "center",
    fontSize: 14,
  },
  discountCell: {
    padding: "10px",
    border: "1px solid #e5e7eb",
    textAlign: "center",
    fontSize: 14,
    color: "green",
  },
  totalCell: {
    padding: "10px",
    border: "1px solid #e5e7eb",
    textAlign: "center",
    fontSize: 14,
    fontWeight: 700,
  },
  emptyCell: { padding: 40, color: "#9ca3af", textAlign: "center" },
  loadingState: { padding: 40, textAlign: "center", color: "#9ca3af" },
  expandBtn: {
    background: "none",
    border: "none",
    cursor: "pointer",
    color: "#2563eb",
    fontSize: 13,
    textDecoration: "underline",
    padding: 0,
  },
  entriesTable: { width: "100%", borderCollapse: "collapse", marginTop: 8 },
  entryHeader: {
    background: "#f3f4f6",
    padding: "8px 10px",
    textAlign: "center",
    fontSize: 12,
    color: "#6b7280",
  },
  entryCell: {
    padding: "8px 10px",
    border: "1px solid #f3f4f6",
    textAlign: "center",
    fontSize: 13,
  },
  entryMrpCell: {
    padding: "8px 10px",
    border: "1px solid #f3f4f6",
    textAlign: "center",
    fontSize: 13,
    color: "#9ca3af",
    textDecoration: "line-through",
  },
  entryDiscountCell: {
    padding: "8px 10px",
    border: "1px solid #f3f4f6",
    textAlign: "center",
    fontSize: 13,
    color: "green",
  },
  entrySubtotalCell: {
    padding: "8px 10px",
    border: "1px solid #f3f4f6",
    textAlign: "center",
    fontSize: 13,
    fontWeight: 600,
  },
};

const ENTRY_HEADERS = [
  "Product",
  "MRP",
  "Selling Price",
  "Qty",
  "Discount",
  "Subtotal",
];
const TABLE_COLUMNS = [
  "ORDER ID",
  "CUSTOMER",
  "PHONE",
  "DISCOUNT",
  "TOTAL",
  "STATUS",
  "ACTIONS",
];

function OrderEntriesRow({ entries, products }) {
  if (!entries || entries.length === 0)
    return <p style={{ color: "#9ca3af", fontSize: 13 }}>No entries</p>;

  return (
    <table style={styles.entriesTable}>
      <thead>
        <tr>
          {ENTRY_HEADERS.map((h) => (
            <th key={h} style={styles.entryHeader}>
              {h}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {entries.map((e, i) => (
          <tr key={e.identifier ?? i}>
            <td style={styles.entryCell}>
              {getProductName(products, e.product)}
            </td>
            <td style={styles.entryMrpCell}>₹{fmt(e.price)}</td>
            <td style={styles.entryCell}>₹{fmt(e.sellingPrice)}</td>
            <td style={styles.entryCell}>{e.quantity}</td>
            <td style={styles.entryDiscountCell}>₹{fmt(e.discount)}</td>
            <td style={styles.entrySubtotalCell}>₹{fmt(e.totalPrice)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

OrderEntriesRow.propTypes = {
  entries: PropTypes.arrayOf(EntryShape),
  products: PropTypes.arrayOf(ProductShape),
};

function OrderRow({ order, products, customers }) {
  const [expanded, setExpanded] = useState(false);
  const customer = customers.find((c) => c.identifier === order.customer);
  const customerName = customer?.name || order.customer;
  const customerPhone = customer?.phoneNo || order.customer || "-";

  return (
    <>
      <tr
        style={{
          background: order.orderStatus === "CANCELLED" ? "#fafafa" : "#fff",
        }}
      >
        <td style={styles.tableCell}>{order.identifier}</td>
        <td style={styles.tableCell}>{customerName}</td>
        <td style={styles.tableCell}>{customerPhone}</td>
        <td style={styles.discountCell}>- ₹{fmt(order.totalDiscount)}</td>
        <td style={styles.totalCell}>₹{fmt(order.totalPrice)}</td>
        <td style={styles.tableCell}>
          <span style={badgeStyle(order.orderStatus)}>
            {order.orderStatus || "PLACED"}
          </span>
        </td>
        <td style={styles.tableCell}>
          <button
            type="button"
            style={styles.expandBtn}
            onClick={() => setExpanded((p) => !p)}
          >
            {expanded ? "Hide Items" : "View Items"}
          </button>
        </td>
      </tr>
      {expanded && (
        <tr>
          <td
            colSpan={7}
            style={{
              padding: "12px 20px",
              background: "#fafafa",
              border: "1px solid #e5e7eb",
            }}
          >
            <OrderEntriesRow
              entries={getOrderEntries(order)}
              products={products}
            />
          </td>
        </tr>
      )}
    </>
  );
}

OrderRow.propTypes = {
  order: OrderShape.isRequired,
  products: PropTypes.arrayOf(ProductShape),
  customers: PropTypes.arrayOf(CustomerShape),
};

function OrdersPageContent() {
  const searchParams = useSearchParams();
  const customerParam = searchParams.get("customer") || "";

  const [search, setSearch] = useState("");
  const [allOrders, setAllOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const showError = makeShowError(setErrorMessage);

  useEffect(() => {
    const token = localStorage.getItem("token");

    const loadAll = async () => {
      setLoading(true);
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
        if (orderData?.status === 401) {
          showError("Session expired. Please log in again.");
          return;
        }
        setAllOrders(Array.isArray(orderData) ? orderData : []);
      } catch {
        showError("Failed to load orders");
      } finally {
        setLoading(false);
      }
    };

    loadAll();
  }, []);

  useEffect(() => {
    if (customerParam) setSearch(customerParam);
  }, [customerParam]);

  const filteredOrders = search.trim()
    ? allOrders.filter((o) => {
        const q = search.trim().toLowerCase();
        const customer = customers.find((c) => c.identifier === o.customer);
        const name = customer?.name || "";
        const phone = String(customer?.phoneNo || o.customer || "");
        return (
          (o.identifier || "").toLowerCase().includes(q) ||
          (o.customer || "").toLowerCase().includes(q) ||
          name.toLowerCase().includes(q) ||
          phone.toLowerCase().includes(q)
        );
      })
    : allOrders;

  return (
    <div style={styles.page}>
      <div style={styles.header}>
        <h2 style={{ margin: 0 }}>📦 ORDERS</h2>
      </div>
      {errorMessage && <p style={errorBannerStyle}>{errorMessage}</p>}
      <input
        type="search"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        placeholder="Search by customer name, phone or order ID..."
        style={styles.searchBar}
      />
      <div style={styles.tableWrapper}>
        {loading ? (
          <div style={styles.loadingState}>Loading orders...</div>
        ) : (
          <table width="100%" style={{ borderCollapse: "collapse" }}>
            <thead>
              <tr>
                {TABLE_COLUMNS.map((h) => (
                  <th key={h} style={styles.tableHeader}>
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {filteredOrders.length === 0 ? (
                <tr>
                  <td colSpan={7} style={styles.emptyCell}>
                    No orders found.
                  </td>
                </tr>
              ) : (
                filteredOrders.map((order, i) => (
                  <OrderRow
                    key={order.identifier ?? i}
                    order={order}
                    products={products}
                    customers={customers}
                  />
                ))
              )}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}

export default function OrdersPage() {
  return (
    <Suspense
      fallback={<div style={styles.loadingState}>Loading orders...</div>}
    >
      <OrdersPageContent />
    </Suspense>
  );
}
