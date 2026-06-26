"use client";

import { useState, useEffect, useCallback, useMemo, Fragment } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation"; // 1. IMPORT ROUTER
import axiosInstance from "../../api/axiosInstance";
import Layout from "@/app/Components/Layout";

const safeNum = (v, fallback = 0) => {
  const n = Number(v);
  return Number.isFinite(n) ? n : fallback;
};

const formatCurrency = (v) =>
  `₹${safeNum(v).toLocaleString("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

const formatDate = (iso) => {
  if (!iso) return "—";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return "—";
  return d.toLocaleString("en-IN", {
    day: "2-digit", month: "short", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
};

function getSortValues(order, key) {
  if (key === "totalPrice") {
    return safeNum(order.totalPrice);
  }
  if (key === "totalDiscount") {
    return safeNum(order.totalDiscount);
  }
  return new Date(order.orderDate).getTime() || 0;
}

function pluralizeOrderCount(orderCount) {
  const suffix = orderCount === 1 ? "" : "s";
  return `${orderCount} order${suffix} found`;
}

function getEmptyStateMessage(totalOrderCount) {
  if (totalOrderCount === 0) {
    return {
      title: "No orders yet",
      subtitle: "Orders placed through checkout will appear here.",
    };
  }
  return {
    title: "No orders match your filters",
    subtitle: "Try clearing the search or payment filter.",
  };
}

function applyOrderFilters(orders, searchTerm, paymentFilter, sortKey, sortDir) {
  let list = [...orders];

  const q = searchTerm.trim().toLowerCase();
  if (q) {
    list = list.filter((o) => {
      const orderId = (o.orderId || o.identifier || "").toLowerCase();
      const couponCode = (o.couponCode || "").toLowerCase();
      return orderId.includes(q) || couponCode.includes(q);
    });
  }

  if (paymentFilter) {
    list = list.filter((o) => o.paymentMode === paymentFilter);
  }

  list.sort((a, b) => {
    const av = getSortValues(a, sortKey);
    const bv = getSortValues(b, sortKey);
    return sortDir === "asc" ? av - bv : bv - av;
  });

  return list;
}

const PAYMENT_BADGE_COLORS = {
  cash: { bg: "#f0fdf4", border: "#bbf7d0", text: "#15803d" },
  card: { bg: "#eff6ff", border: "#bfdbfe", text: "#1d4ed8" },
  upi: { bg: "#faf5ff", border: "#e9d5ff", text: "#7e22ce" },
};

const DEFAULT_BADGE_COLOR = { bg: "#f8fafc", border: "#e2e8f0", text: "#64748b" };

const TOKEN_SHAPE = PropTypes.shape({
  bg: PropTypes.string,
  surface: PropTypes.string,
  border: PropTypes.string,
  muted: PropTypes.string,
  body: PropTypes.string,
  strong: PropTypes.string,
  indigo: PropTypes.string,
  emerald: PropTypes.string,
  red: PropTypes.string,
});

const ORDER_ENTRY_SHAPE = PropTypes.shape({
  identifier: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  product: PropTypes.string,
  price: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  sellingPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  quantity: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  discount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
});

function PaymentBadge({ mode }) {
  const key = (mode || "").toLowerCase();
  const c = PAYMENT_BADGE_COLORS[key] || DEFAULT_BADGE_COLOR;
  return (
    <span style={{
      display: "inline-flex", alignItems: "center", padding: "3px 10px",
      borderRadius: "20px", background: c.bg, border: `1px solid ${c.border}`,
      color: c.text, fontSize: "11px", fontWeight: 700, whiteSpace: "nowrap",
    }}>
      {mode || "Unspecified"}
    </span>
  );
}

PaymentBadge.propTypes = {
  mode: PropTypes.string,
};
PaymentBadge.defaultProps = {
  mode: "",
};

function Th({ label, sortKey, activeKey, dir, onClick, align }) {
  const sortable = Boolean(sortKey);
  const active = sortable && activeKey === sortKey;

  const handleClick = sortable ? () => onClick(sortKey) : undefined;

  return (
    <th
      onClick={handleClick}
      style={{
        padding: "10px 14px", textAlign: align, fontWeight: 700,
        color: active ? "#4f46e5" : "#94a3b8", fontSize: "10px",
        textTransform: "uppercase", letterSpacing: "0.5px",
        cursor: sortable ? "pointer" : "default", userSelect: "none", whiteSpace: "nowrap",
      }}
    >
      {label}
      {active && <span style={{ marginLeft: "4px" }}>{dir === "asc" ? "↑" : "↓"}</span>}
    </th>
  );
}

Th.propTypes = {
  label: PropTypes.string.isRequired,
  sortKey: PropTypes.string,
  activeKey: PropTypes.string,
  dir: PropTypes.oneOf(["asc", "desc"]),
  onClick: PropTypes.func,
  align: PropTypes.oneOf(["left", "right", "center"]),
};
Th.defaultProps = {
  sortKey: "",
  activeKey: "",
  dir: "desc",
  onClick: undefined,
  align: "left",
};

function PageButton({ onClick, disabled, children }) {
  return (
    <button
      type="button"
      onClick={onClick}
      disabled={disabled}
      style={{
        padding: "5px 12px", borderRadius: "6px", border: "1px solid #e2e8f0",
        background: disabled ? "#f8fafc" : "#fff", color: disabled ? "#cbd5e1" : "#475569",
        fontSize: "12px", fontWeight: 600, cursor: disabled ? "not-allowed" : "pointer",
      }}
    >
      {children}
    </button>
  );
}

PageButton.propTypes = {
  onClick: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
  children: PropTypes.node.isRequired,
};
PageButton.defaultProps = {
  disabled: false,
};

function OrderEntryDetail({ entries, tokens }) {
  if (!entries || entries.length === 0) {
    return <p style={{ fontSize: "12px", color: tokens.muted, padding: "10px 0" }}>No line items recorded for this order.</p>;
  }

  return (
    <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "12px", background: tokens.surface, borderRadius: "8px", border: `1px solid ${tokens.border}`, overflow: "hidden" }}>
      <thead>
        <tr style={{ background: "#f1f5f9" }}>
          {["Product", "MRP", "Rate", "Qty", "Discount", "Line Total"].map((h, i) => (
            <th key={h} style={{ padding: "7px 12px", textAlign: i === 0 ? "left" : "right", fontWeight: 700, color: tokens.muted, fontSize: "10px", textTransform: "uppercase" }}>
              {h}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {entries.map((e, idx) => (
          <tr key={e.identifier || idx} style={{ borderTop: "1px solid #f1f5f9" }}>
            <td style={{ padding: "7px 12px", fontFamily: "ui-monospace, monospace", fontWeight: 700, color: tokens.strong }}>{e.product}</td>
            <td style={{ padding: "7px 12px", textAlign: "right", color: tokens.muted, textDecoration: "line-through", fontFamily: "ui-monospace, monospace" }}>{formatCurrency(e.price)}</td>
            <td style={{ padding: "7px 12px", textAlign: "right", color: tokens.body, fontFamily: "ui-monospace, monospace" }}>{formatCurrency(e.sellingPrice)}</td>
            <td style={{ padding: "7px 12px", textAlign: "right", color: tokens.body }}>{safeNum(e.quantity)}</td>
            <td style={{ padding: "7px 12px", textAlign: "right", color: tokens.emerald, fontFamily: "ui-monospace, monospace" }}>
              {safeNum(e.discount) > 0 ? `−${formatCurrency(e.discount)}` : "—"}
            </td>
            <td style={{ padding: "7px 12px", textAlign: "right", fontWeight: 800, color: tokens.strong, fontFamily: "ui-monospace, monospace" }}>{formatCurrency(e.totalPrice)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

OrderEntryDetail.propTypes = {
  entries: PropTypes.arrayOf(ORDER_ENTRY_SHAPE),
  tokens: TOKEN_SHAPE.isRequired,
};
OrderEntryDetail.defaultProps = {
  entries: [],
};

// 2. MODIFIED HEADERS TO BRING IN BACK BUTTON
function OrdersHeader({ loading, refreshing, orderCount, onRefresh, navigate, tokens }) {
  return (
    <div style={{ display: "flex", alignItems: "flex-end", justifyContent: "space-between", marginBottom: "20px", flexWrap: "wrap", gap: "12px" }}>
      <div>
        <h1 style={{ fontSize: "22px", fontWeight: 800, color: tokens.strong, margin: 0, letterSpacing: "-0.3px" }}>Orders</h1>
        <p style={{ fontSize: "13px", color: tokens.muted, margin: "4px 0 0" }}>
          {loading ? "Loading…" : pluralizeOrderCount(orderCount)}
        </p>
      </div>
      
      <div style={{ display: "flex", gap: "10px" }}>
        {/* NEW: Back to Dashboard Button */}
        <button
          type="button"
          onClick={() => navigate.push("/Dashboard")}
          style={{
            display: "flex", alignItems: "center", gap: "6px",
            padding: "9px 16px", borderRadius: "8px",
            background: tokens.surface, border: `1px solid ${tokens.border}`,
            color: tokens.body, fontWeight: 700, fontSize: "13px",
            cursor: "pointer",
          }}
        >
          ← Back to Dashboard
        </button>

        <button
          type="button"
          onClick={onRefresh}
          disabled={loading || refreshing}
          style={{
            display: "flex", alignItems: "center", gap: "6px",
            padding: "9px 16px", borderRadius: "8px",
            background: tokens.surface, border: `1px solid ${tokens.border}`,
            color: tokens.body, fontWeight: 700, fontSize: "13px",
            cursor: loading || refreshing ? "not-allowed" : "pointer",
            opacity: loading || refreshing ? 0.6 : 1,
          }}
        >
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"
            style={{ transform: refreshing ? "rotate(180deg)" : "none", transition: "transform 0.4s" }}>
            <polyline points="23 4 23 10 17 10" /><polyline points="1 20 1 14 7 14" />
            <path d="M3.51 9a9 9 0 0114.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0020.49 15" />
          </svg>
          {refreshing ? "Refreshing…" : "Refresh"}
        </button>
      </div>
    </div>
  );
}

OrdersHeader.propTypes = {
  loading: PropTypes.bool.isRequired,
  refreshing: PropTypes.bool.isRequired,
  orderCount: PropTypes.number.isRequired,
  onRefresh: PropTypes.func.isRequired,
  navigate: PropTypes.object.isRequired, // Added prop validation
  tokens: TOKEN_SHAPE.isRequired,
};

function OrderFilters({ searchTerm, onSearchChange, paymentFilter, onPaymentFilterChange, paymentModes, tokens }) {
  return (
    <div style={{ display: "flex", gap: "10px", marginBottom: "16px", flexWrap: "wrap" }}>
      <div style={{ position: "relative", flex: "1 1 240px", minWidth: "220px" }}>
        <svg style={{ position: "absolute", left: "10px", top: "50%", transform: "translateY(-50%)" }} width="14" height="14" viewBox="0 0 24 24" fill="none" stroke={tokens.muted} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="11" cy="11" r="8" /><line x1="21" y1="21" x2="16.65" y2="16.65" /></svg>
        <input
          type="text"
          value={searchTerm}
          onChange={(e) => onSearchChange(e.target.value)}
          placeholder="Search by order ID or coupon code…"
          style={{ width: "100%", boxSizing: "border-box", border: `1px solid ${tokens.border}`, borderRadius: "8px", padding: "9px 12px 9px 32px", fontSize: "13px", color: tokens.strong, background: tokens.surface, outline: "none" }}
        />
      </div>

      <select
        value={paymentFilter}
        onChange={(e) => onPaymentFilterChange(e.target.value)}
        style={{ border: `1px solid ${tokens.border}`, borderRadius: "8px", padding: "9px 12px", fontSize: "13px", color: tokens.body, background: tokens.surface, outline: "none", minWidth: "160px" }}
      >
        <option value="">All payment modes</option>
        {paymentModes.map((m) => (
          <option key={m} value={m}>{m}</option>
        ))}
      </select>
    </div>
  );
}

OrderFilters.propTypes = {
  searchTerm: PropTypes.string.isRequired,
  onSearchChange: PropTypes.func.isRequired,
  paymentFilter: PropTypes.string.isRequired,
  onPaymentFilterChange: PropTypes.func.isRequired,
  paymentModes: PropTypes.arrayOf(PropTypes.string).isRequired,
  tokens: TOKEN_SHAPE.isRequired,
};

function ErrorBanner({ message, onRetry, tokens }) {
  if (!message) return null;
  return (
    <div style={{ display: "flex", alignItems: "center", gap: "10px", padding: "12px 16px", background: "#fef2f2", border: "1px solid #fecaca", borderRadius: "10px", color: tokens.red, fontSize: "13px", fontWeight: 600, marginBottom: "16px" }}>
      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="10" /><line x1="12" y1="8" x2="12" y2="12" /><line x1="12" y1="16" x2="12.01" y2="16" /></svg>
      <span style={{ flex: 1 }}>{message}</span>
      <button
        type="button"
        onClick={onRetry}
        style={{ background: "none", border: "1px solid #fecaca", borderRadius: "6px", padding: "4px 10px", color: tokens.red, fontWeight: 700, fontSize: "12px", cursor: "pointer" }}
      >Retry</button>
    </div>
  );
}

ErrorBanner.propTypes = {
  message: PropTypes.string,
  onRetry: PropTypes.func.isRequired,
  tokens: TOKEN_SHAPE.isRequired,
};
ErrorBanner.defaultProps = {
  message: "",
};

function EmptyState({ totalOrderCount }) {
  const { title, subtitle } = getEmptyStateMessage(totalOrderCount);
  return (
    <div style={{ padding: "64px 24px", textAlign: "center", color: "#94a3b8" }}>
      <div style={{ fontSize: "32px", marginBottom: "8px" }}>📦</div>
      <p style={{ fontWeight: 700, color: "#1e293b", margin: "0 0 4px", fontSize: "14px" }}>{title}</p>
      <p style={{ fontSize: "12px", margin: 0 }}>{subtitle}</p>
    </div>
  );
}

EmptyState.propTypes = {
  totalOrderCount: PropTypes.number.isRequired,
};

function LoadingState({ tokens }) {
  return (
    <div style={{ padding: "64px", textAlign: "center", color: tokens.muted, fontSize: "13px" }}>
      <div style={{ width: "28px", height: "28px", border: `3px solid ${tokens.border}`, borderTopColor: tokens.indigo, borderRadius: "50%", margin: "0 auto 12px", animation: "spin 0.8s linear infinite" }} />
      Loading orders…
      <style>{"@keyframes spin { to { transform: rotate(360deg); } }"}</style>
    </div>
  );
}

LoadingState.propTypes = {
  tokens: TOKEN_SHAPE.isRequired,
};

function OrderRow({ order, expanded, onToggle, tokens }) {
  const orderKey = order.orderId || order.identifier;
  const itemCount = order.orderEntryDtoList?.length || 0;

  return (
    <Fragment>
      <tr
        style={{ borderBottom: "1px solid #f1f5f9", cursor: "pointer" }}
        onClick={() => onToggle(orderKey)}
        onMouseEnter={(e) => { e.currentTarget.style.background = "#fafafa"; }}
        onMouseLeave={(e) => { e.currentTarget.style.background = "transparent"; }}
      >
        <td style={{ padding: "11px 14px", fontFamily: "ui-monospace, monospace", fontWeight: 700, color: tokens.strong, fontSize: "12px" }}>
          {orderKey || "—"}
        </td>
        <td style={{ padding: "11px 14px", color: tokens.body }}>{formatDate(order.orderDate)}</td>
        <td style={{ padding: "11px 14px" }}><PaymentBadge mode={order.paymentMode} /></td>
        <td style={{ padding: "11px 14px", textAlign: "center", color: tokens.body }}>{itemCount}</td>
        <td style={{ padding: "11px 14px", textAlign: "right", color: tokens.emerald, fontFamily: "ui-monospace, monospace" }}>
          {safeNum(order.totalDiscount) > 0 ? `−${formatCurrency(order.totalDiscount)}` : "—"}
        </td>
        <td style={{ padding: "11px 14px", textAlign: "right", fontWeight: 800, color: tokens.strong, fontFamily: "ui-monospace, monospace" }}>
          {formatCurrency(order.totalPrice)}
        </td>
        <td style={{ padding: "11px 14px", textAlign: "center", color: tokens.muted }}>
          <span style={{ display: "inline-block", transform: expanded ? "rotate(180deg)" : "none", transition: "transform 0.15s" }}>▾</span>
        </td>
      </tr>

      {expanded && (
        <tr style={{ background: "#f8fafc" }}>
          <td colSpan={7} style={{ padding: "0 14px 14px 14px" }}>
            <OrderEntryDetail entries={order.orderEntryDtoList} tokens={tokens} />
          </td>
        </tr>
      )}
    </Fragment>
  );
}

OrderRow.propTypes = {
  order: PropTypes.shape({
    orderId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    identifier: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    orderDate: PropTypes.string,
    paymentMode: PropTypes.string,
    totalDiscount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    orderEntryDtoList: PropTypes.arrayOf(ORDER_ENTRY_SHAPE),
  }).isRequired,
  expanded: PropTypes.bool.isRequired,
  onToggle: PropTypes.func.isRequired,
  tokens: TOKEN_SHAPE.isRequired,
};

function OrdersTable({
  pagedOrders, sortKey, sortDir, onToggleSort, expandedId, onToggleExpand,
  tokens, pageSafe, totalPages, onPrevPage, onNextPage,
}) {
  return (
    <>
      <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "13px" }}>
        <thead>
          <tr style={{ background: "#f8fafc", borderBottom: `1px solid ${tokens.border}` }}>
            <Th label="Order ID" align="left" />
            <Th label="Date" sortKey="orderDate" activeKey={sortKey} dir={sortDir} onClick={onToggleSort} align="left" />
            <Th label="Payment" align="left" />
            <Th label="Items" align="center" />
            <Th label="Discount" sortKey="totalDiscount" activeKey={sortKey} dir={sortDir} onClick={onToggleSort} align="right" />
            <Th label="Total" sortKey="totalPrice" activeKey={sortKey} dir={sortDir} onClick={onToggleSort} align="right" />
            <Th label="" align="center" />
          </tr>
        </thead>
        <tbody>
          {pagedOrders.map((order) => {
            const orderKey = order.orderId || order.identifier;
            return (
              <OrderRow
                key={orderKey}
                order={order}
                expanded={expandedId === orderKey}
                onToggle={onToggleExpand}
                tokens={tokens}
              />
            );
          })}
        </tbody>
      </table>

      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "12px 16px", borderTop: `1px solid ${tokens.border}`, fontSize: "12px", color: tokens.muted }}>
        <span>Page {pageSafe} of {totalPages}</span>
        <div style={{ display: "flex", gap: "6px" }}>
          <PageButton onClick={onPrevPage} disabled={pageSafe === 1}>← Prev</PageButton>
          <PageButton onClick={onNextPage} disabled={pageSafe === totalPages}>Next →</PageButton>
        </div>
      </div>
    </>
  );
}

OrdersTable.propTypes = {
  pagedOrders: PropTypes.arrayOf(PropTypes.object).isRequired,
  sortKey: PropTypes.string.isRequired,
  sortDir: PropTypes.oneOf(["asc", "desc"]).isRequired,
  onToggleSort: PropTypes.func.isRequired,
  expandedId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onToggleExpand: PropTypes.func.isRequired,
  tokens: TOKEN_SHAPE.isRequired,
  pageSafe: PropTypes.number.isRequired,
  totalPages: PropTypes.number.isRequired,
  onPrevPage: PropTypes.func.isRequired,
  onNextPage: PropTypes.func.isRequired,
};
OrdersTable.defaultProps = {
  expandedId: null,
};

const PAGE_SIZE = 10;

const TOKENS = {
  bg: "#f8fafc", surface: "#ffffff", border: "#e2e8f0",
  muted: "#94a3b8", body: "#475569", strong: "#1e293b",
  indigo: "#4f46e5", emerald: "#059669", red: "#dc2626",
};

export default function OrderListPage() {
  const navigate = useRouter(); // 3. HOOK INSTANTIATION
  
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [refreshing, setRefreshing] = useState(false);

  const [searchTerm, setSearchTerm] = useState("");
  const [paymentFilter, setPaymentFilter] = useState("");
  const [sortKey, setSortKey] = useState("orderDate");
  const [sortDir, setSortDir] = useState("desc");
  const [page, setPage] = useState(1);
  const [expandedId, setExpandedId] = useState(null);

  const resolveFetchError = (err) => {
    const status = err.response?.status;
    const message = err.response?.data?.message || err.message || "Unknown error";

    if (status === 401 || status === 403) {
      return "You are not authorized to view orders. Please sign in again.";
    }
    if (status >= 500) {
      return "The server had a problem loading orders. Please try again shortly.";
    }
    return `Could not load orders: ${message}`;
  };

  const fetchOrders = useCallback(async (isRefresh = false) => {
    if (isRefresh) setRefreshing(true); else setLoading(true);
    setError("");
    try {
      const res = await axiosInstance.get("/orders/list");
      setOrders(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      setError(resolveFetchError(err));
      setOrders([]);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => { fetchOrders(); }, [fetchOrders]);

  const paymentModes = useMemo(() => {
    const set = new Set(orders.map((o) => o.paymentMode).filter(Boolean));
    return Array.from(set);
  }, [orders]);

  const processedOrders = useMemo(
    () => applyOrderFilters(orders, searchTerm, paymentFilter, sortKey, sortDir),
    [orders, searchTerm, paymentFilter, sortKey, sortDir]
  );

  const totalPages = Math.max(1, Math.ceil(processedOrders.length / PAGE_SIZE));
  const pageSafe = Math.min(page, totalPages);
  const pagedOrders = processedOrders.slice((pageSafe - 1) * PAGE_SIZE, pageSafe * PAGE_SIZE);

  useEffect(() => { setPage(1); }, [searchTerm, paymentFilter, sortKey, sortDir]);

  const toggleSort = (key) => {
    if (sortKey === key) {
      setSortDir((d) => (d === "asc" ? "desc" : "asc"));
    } else {
      setSortKey(key);
      setSortDir("desc");
    }
  };

  const toggleExpand = (orderKey) => {
    setExpandedId((current) => (current === orderKey ? null : orderKey));
  };

  return (
    <Layout>
      <div style={{ minHeight: "100vh", background: TOKENS.bg, fontFamily: "'Inter', system-ui, sans-serif", padding: "28px" }}>
        <div style={{ maxWidth: "1180px", margin: "0 auto" }}>

          {/* 4. PASSED NAVIGATE PROPERTY */}
          <OrdersHeader
            loading={loading}
            refreshing={refreshing}
            orderCount={processedOrders.length}
            onRefresh={() => fetchOrders(true)}
            navigate={navigate} 
            tokens={TOKENS}
          />

          <OrderFilters
            searchTerm={searchTerm}
            onSearchChange={setSearchTerm}
            paymentFilter={paymentFilter}
            onPaymentFilterChange={setPaymentFilter}
            paymentModes={paymentModes}
            tokens={TOKENS}
          />

          <ErrorBanner message={error} onRetry={() => fetchOrders()} tokens={TOKENS} />

          <div style={{ background: TOKENS.surface, border: `1px solid ${TOKENS.border}`, borderRadius: "12px", overflow: "hidden", boxShadow: "0 1px 3px rgba(0,0,0,0.05)" }}>
            {loading && <LoadingState tokens={TOKENS} />}

            {!loading && processedOrders.length === 0 && (
              <EmptyState totalOrderCount={orders.length} />
            )}

            {!loading && processedOrders.length > 0 && (
              <OrdersTable
                pagedOrders={pagedOrders}
                sortKey={sortKey}
                sortDir={sortDir}
                onToggleSort={toggleSort}
                expandedId={expandedId}
                onToggleExpand={toggleExpand}
                tokens={TOKENS}
                pageSafe={pageSafe}
                totalPages={totalPages}
                onPrevPage={() => setPage((p) => Math.max(1, p - 1))}
                onNextPage={() => setPage((p) => Math.min(totalPages, p + 1))}
              />
            )}
          </div>
        </div>
      </div>
    </Layout>
  );
}