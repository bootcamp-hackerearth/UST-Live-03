"use client";

import { useEffect, useMemo, useState, Fragment } from "react";
import PropTypes from "prop-types";
import axiosInstance from "../api/axiosInstance";

function formatCurrency(value) {
    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2,
    }).format(Number(value) || 0);
}

function formatDateTime(value) {
    if (!value) return "-";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return String(value);
    return new Intl.DateTimeFormat("en-IN", {
        dateStyle: "medium",
        timeStyle: "short",
    }).format(date);
}

function normalizeOrdersResponse(data) {
    if (Array.isArray(data)) return data;
    return data?.dtoList ?? data?.content ?? data?.data ?? [];
}

function StatCard({ label, value }) {
    return (
        <div style={{
            borderRadius: "1rem",
            border: "1px solid rgba(255,255,255,0.12)",
            background: "rgba(255,255,255,0.07)",
            padding: "1rem 1.25rem",
            display: "flex", flexDirection: "column", gap: "0.5rem",
        }}>
            <p style={{ margin: 0, fontSize: "0.65rem", fontWeight: 700, letterSpacing: "0.18em", textTransform: "uppercase", color: "#94a3b8" }}>{label}</p>
            <p style={{ margin: 0, fontSize: "1.5rem", fontWeight: 900, color: "#ffffff", lineHeight: 1.1 }}>{value}</p>
        </div>
    );
}

StatCard.propTypes = {
    label: PropTypes.string.isRequired,
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
};

const inputStyle = {
    width: "100%", borderRadius: "0.75rem", border: "1.5px solid #d1d5db",
    background: "#ffffff", padding: "0.65rem 1rem", fontSize: "0.875rem",
    color: "#111827", outline: "none", boxSizing: "border-box",
    transition: "border-color 0.15s, box-shadow 0.15s",
};

function buildReceiptHTML(order, entries, subtotal, discount, total, productMap = {}) {
    const itemRows = entries.map((e) => `
        <tr style="border-bottom: 1px solid #f1f5f9;">
            <td style="padding: 10px 12px; font-size: 13px; color: #0f172a; font-weight: 600;">${productMap[e.product] || e.product || "-"}</td>
            <td style="padding: 10px 12px; font-size: 13px; color: #475569; text-align: center;">${Number(e.quantity) || 0}</td>
            <td style="padding: 10px 12px; font-size: 13px; color: #475569; text-align: right;">${formatCurrency(e.sellingPrice ?? e.price)}</td>
            <td style="padding: 10px 12px; font-size: 13px; color: #1d4ed8; font-weight: 700; text-align: right;">${formatCurrency(e.totalPrice)}</td>
        </tr>
    `).join("");

    return `<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Receipt - ${order.orderId || order.identifier}</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: 'Segoe UI', system-ui, sans-serif; background: #fff; color: #0f172a; padding: 40px; max-width: 680px; margin: 0 auto; }
    @media print { body { padding: 20px; } }
  </style>
</head>
<body>
  <div style="display:flex; justify-content:space-between; align-items:flex-start; padding-bottom:24px; border-bottom:2px solid #0f172a; margin-bottom:28px;">
    <div>
      <h1 style="font-size:28px; font-weight:900; color:#0f172a;">POS</h1>
      <p style="font-size:12px; color:#64748b; margin-top:4px; font-weight:600; letter-spacing:0.08em; text-transform:uppercase;">Point of Sale</p>
    </div>
    <div style="text-align:right;">
      <p style="font-size:11px; font-weight:700; letter-spacing:0.15em; text-transform:uppercase; color:#2563eb; margin-bottom:6px;">Receipt</p>
      <p style="font-size:14px; font-weight:700; color:#0f172a;">${order.orderId || "-"}</p>
      <p style="font-size:12px; color:#64748b; margin-top:4px;">${formatDateTime(order.orderDate)}</p>
    </div>
  </div>
  <div style="display:grid; grid-template-columns:1fr 1fr; gap:16px; margin-bottom:28px;">
    <div style="background:#f8fafc; border-radius:12px; padding:16px;">
      <p style="font-size:10px; font-weight:700; text-transform:uppercase; letter-spacing:0.15em; color:#94a3b8; margin-bottom:8px;">Customer</p>
      <p style="font-size:14px; font-weight:700; color:#0f172a;">${order.identifier || "Walk-in"}</p>
    </div>
    <div style="background:#f8fafc; border-radius:12px; padding:16px;">
      <p style="font-size:10px; font-weight:700; text-transform:uppercase; letter-spacing:0.15em; color:#94a3b8; margin-bottom:8px;">Payment Mode</p>
      <p style="font-size:14px; font-weight:700; color:#0f172a;">${order.paymentMode || "Cash"}</p>
    </div>
  </div>
  <div style="border-radius:12px; overflow:hidden; border:1px solid #e2e8f0; margin-bottom:24px;">
    <table style="width:100%; border-collapse:collapse;">
      <thead>
        <tr style="background:#0f172a;">
          <th style="padding:10px 12px; text-align:left; font-size:10px; font-weight:700; letter-spacing:0.1em; text-transform:uppercase; color:#94a3b8;">Product</th>
          <th style="padding:10px 12px; text-align:center; font-size:10px; font-weight:700; letter-spacing:0.1em; text-transform:uppercase; color:#94a3b8;">Qty</th>
          <th style="padding:10px 12px; text-align:right; font-size:10px; font-weight:700; letter-spacing:0.1em; text-transform:uppercase; color:#94a3b8;">Price</th>
          <th style="padding:10px 12px; text-align:right; font-size:10px; font-weight:700; letter-spacing:0.1em; text-transform:uppercase; color:#94a3b8;">Total</th>
        </tr>
      </thead>
      <tbody>${itemRows}</tbody>
    </table>
  </div>
  <div style="margin-left:auto; width:280px;">
    <div style="display:flex; justify-content:space-between; padding:8px 0; font-size:13px;">
      <span style="color:#64748b;">Subtotal</span><span style="font-weight:600;">${formatCurrency(subtotal)}</span>
    </div>
    <div style="display:flex; justify-content:space-between; padding:8px 0; font-size:13px; border-bottom:1px solid #e2e8f0;">
      <span style="color:#64748b;">Discount</span><span style="font-weight:600; color:#16a34a;">${formatCurrency(discount)}</span>
    </div>
    <div style="display:flex; justify-content:space-between; padding:14px 0; font-size:16px;">
      <span style="font-weight:900; color:#0f172a;">Total</span><span style="font-weight:900; color:#d97706;">${formatCurrency(total)}</span>
    </div>
  </div>
  <div style="margin-top:40px; padding-top:20px; border-top:1px solid #e2e8f0; text-align:center;">
    <p style="font-size:12px; color:#94a3b8; font-weight:600;">Thank you for your purchase!</p>
    <p style="font-size:11px; color:#cbd5e1; margin-top:4px;">Generated on ${new Date().toLocaleString("en-IN")}</p>
  </div>
  <script>window.onload = () => { window.print(); }</script>
</body>
</html>`;
}

const orderEntryShape = PropTypes.shape({
    identifier: PropTypes.string,
    product: PropTypes.string,
    quantity: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    sellingPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    price: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
});

const orderShape = PropTypes.shape({
    orderId: PropTypes.string,
    identifier: PropTypes.string,
    paymentMode: PropTypes.string,
    orderDate: PropTypes.string,
    totalDiscount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    orderEntryDtoList: PropTypes.arrayOf(orderEntryShape),
});

function OrderDetailRow({ order, onClose, onReceipt, generating, productMap }) {
    const entries = order?.orderEntryDtoList || [];
    const subtotal = entries.reduce((s, e) => s + (Number(e.totalPrice) || 0), 0);
    const discount = Number(order?.totalDiscount) || 0;
    const total = Number(order?.totalPrice) || subtotal;

    // FIX: Extracted nested ternary operation into a clean, independent conditional block
    let receiptButtonContent;
    if (generating) {
        receiptButtonContent = (
            <>
                <span style={{
                    width: "12px",
                    height: "12px",
                    borderRadius: "50%",
                    border: "2px solid rgba(255,255,255,0.3)",
                    borderTopColor: "#fff",
                    animation: "spin 0.6s linear infinite",
                    display: "inline-block"
                }} /> Generating…</>
        );
    } else {
        receiptButtonContent = <>🖨️ Generate Receipt</>;
    }

    return (
        <tr>
            <td colSpan={6} style={{ padding: 0, background: "#f8fafc", borderBottom: "2px solid #e2e8f0" }}>
                <div style={{ padding: "1.25rem 1.5rem", display: "flex", flexDirection: "column", gap: "1rem" }}>
                    <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: "0.75rem" }}>
                        {[
                            { label: "Order ID", value: order.orderId || "-" },
                            { label: "Customer", value: order.identifier || "Walk-in" },
                            { label: "Payment", value: order.paymentMode || "-" },
                            { label: "Date", value: formatDateTime(order.orderDate) },
                        ].map(({ label, value }) => (
                            <div key={label} style={{ background: "#fff", border: "1px solid #e2e8f0", borderRadius: "0.75rem", padding: "0.75rem 1rem" }}>
                                <p style={{ margin: 0, fontSize: "0.6rem", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.15em", color: "#94a3b8" }}>{label}</p>
                                <p style={{ margin: "0.3rem 0 0", fontSize: "0.85rem", fontWeight: 700, color: "#0f172a", wordBreak: "break-all" }}>{value}</p>
                            </div>
                        ))}
                    </div>

                    {entries.length > 0 && (
                        <div style={{ borderRadius: "0.75rem", border: "1px solid #e2e8f0", overflow: "hidden" }}>
                            <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "0.83rem" }}>
                                <thead>
                                    <tr style={{ background: "#0f172a" }}>
                                        {["Product", "Qty", "Unit Price", "Total"].map((h) => (
                                            <th key={h} style={{ padding: "0.6rem 1rem", textAlign: h === "Product" ? "left" : "right", fontSize: "0.62rem", fontWeight: 700, letterSpacing: "0.1em", textTransform: "uppercase", color: "#94a3b8" }}>{h}</th>
                                        ))}
                                    </tr>
                                </thead>
                                <tbody>
                                    {entries.map((e, i) => (
                                        <tr key={e.identifier || `entry-${i}`} style={{ borderTop: "1px solid #f1f5f9" }}>
                                            <td style={{ padding: "0.65rem 1rem", fontWeight: 600, color: "#0f172a" }}>
                                                {productMap?.[e.product] || e.product || "-"}
                                            </td>
                                            <td style={{ padding: "0.65rem 1rem", textAlign: "right", color: "#475569" }}>{Number(e.quantity) || 0}</td>
                                            <td style={{ padding: "0.65rem 1rem", textAlign: "right", color: "#475569" }}>{formatCurrency(e.sellingPrice ?? e.price)}</td>
                                            <td style={{ padding: "0.65rem 1rem", textAlign: "right", fontWeight: 700, color: "#1d4ed8" }}>{formatCurrency(e.totalPrice)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}

                    <div style={{ display: "flex", alignItems: "flex-end", justifyContent: "space-between", gap: "1rem", flexWrap: "wrap" }}>
                        <div style={{ display: "flex", gap: "1.5rem", fontSize: "0.85rem" }}>
                            <div><span style={{ color: "#94a3b8" }}>Subtotal </span><span style={{ fontWeight: 700, color: "#0f172a" }}>{formatCurrency(subtotal)}</span></div>
                            <div><span style={{ color: "#94a3b8" }}>Discount </span><span style={{ fontWeight: 700, color: "#16a34a" }}>{formatCurrency(discount)}</span></div>
                            <div><span style={{ color: "#94a3b8" }}>Total </span><span style={{ fontWeight: 800, fontSize: "1rem", color: "#d97706" }}>{formatCurrency(total)}</span></div>
                        </div>
                        <div style={{ display: "flex", gap: "0.75rem" }}>
                            <button onClick={onClose}
                                style={{ padding: "0.55rem 1.25rem", borderRadius: "0.625rem", border: "1px solid #e2e8f0", background: "#fff", fontSize: "0.82rem", fontWeight: 600, color: "#475569", cursor: "pointer" }}>
                                Close
                            </button>
                            <button onClick={onReceipt} disabled={generating}
                                style={{ padding: "0.55rem 1.25rem", borderRadius: "0.625rem", border: "none", background: generating ? "#1e3a5f" : "linear-gradient(135deg,#2563eb,#1d4ed8)", color: "#fff", fontSize: "0.82rem", fontWeight: 700, cursor: generating ? "wait" : "pointer", display: "flex", alignItems: "center", gap: "0.4rem" }}>
                                {receiptButtonContent}
                            </button>
                        </div>
                    </div>
                </div>
            </td>
        </tr>
    );
}

OrderDetailRow.propTypes = {
    order: orderShape.isRequired,
    onClose: PropTypes.func.isRequired,
    onReceipt: PropTypes.func.isRequired,
    generating: PropTypes.bool.isRequired,
    productMap: PropTypes.objectOf(PropTypes.string),
};

OrderDetailRow.defaultProps = {
    productMap: {},
};

export default function OrderPage() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [expandedOrderId, setExpandedOrderId] = useState(null);
    const [generating, setGenerating] = useState(false);

    const [productMap, setProductMap] = useState({});

    const fetchOrders = async () => {
        setLoading(true);
        setError("");
        try {
            const res = await axiosInstance.get("/order/list");
            const list = normalizeOrdersResponse(res.data);
            setOrders(Array.isArray(list) ? list : []);
        } catch (err) {
            setError(err?.response?.data?.message || "Failed to load orders.");
            setOrders([]);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchOrders();
        axiosInstance.get("/product/findByStatus")
            .then((res) => {
                const map = {};
                (res.data || []).forEach((p) => {
                    map[p.identifier] = p.productname || p.identifier;
                });
                setProductMap(map);
            })
            .catch(() => { });
    }, []);

    const filteredOrders = useMemo(() => {
        const term = searchTerm.trim().toLowerCase();
        if (!term) return orders;
        return orders.filter((order) => {
            const haystack = [order.orderId, order.identifier, order.paymentMode, order.couponCode, order.orderDate]
                .filter(Boolean).join(" ").toLowerCase();
            return haystack.includes(term);
        });
    }, [orders, searchTerm]);

    const metrics = useMemo(() => ({
        count: orders.length,
        sales: orders.reduce((s, o) => s + (Number(o.totalPrice) || 0), 0),
        discount: orders.reduce((s, o) => s + (Number(o.totalDiscount) || 0), 0),
    }), [orders]);

    const paymentBadge = { Cash: "#d1fae5", Card: "#dbeafe", UPI: "#fef9c3" };
    const paymentText = { Cash: "#065f46", Card: "#1e3a8a", UPI: "#713f12" };

    const handleToggleExpand = (key) => setExpandedOrderId((prev) => (prev === key ? null : key));

    const handleGenerateReceipt = (order, map = {}) => {
        if (!order) return;
        setGenerating(true);
        const entries = order.orderEntryDtoList || [];
        const subtotal = entries.reduce((s, e) => s + (Number(e.totalPrice) || 0), 0);
        const discount = Number(order.totalDiscount) || 0;
        const total = Number(order.totalPrice) || subtotal;
        const html = buildReceiptHTML(order, entries, subtotal, discount, total, map);
        const win = window.open("", "_blank");
        if (win) {
            win.document.documentElement.innerHTML = html;
            win.document.close();
        }
        setTimeout(() => setGenerating(false), 1000);
    };

    const renderOrdersContent = () => {
        if (loading) {
            return <div style={{ padding: "2.5rem", textAlign: "center", fontSize: "0.875rem", color: "#94a3b8" }}>Loading orders…</div>;
        }
        if (filteredOrders.length === 0) {
            return (
                <div style={{ padding: "2.5rem", textAlign: "center", fontSize: "0.875rem", color: "#94a3b8" }}>
                    {searchTerm ? `No orders found for "${searchTerm}".` : "No orders yet."}
                </div>
            );
        }
        return (
            <div style={{ overflowX: "auto" }}>
                <table style={{ minWidth: "100%", borderCollapse: "collapse", fontSize: "0.85rem" }}>
                    <thead>
                        <tr style={{ background: "#0f172a" }}>
                            {["Order ID", "Customer", "Payment", "Date", "Total", "Details"].map((h) => (
                                <th key={h} style={{ padding: "0.8rem 1rem", textAlign: h === "Total" || h === "Details" ? "right" : "left", fontSize: "0.65rem", fontWeight: 700, letterSpacing: "0.1em", textTransform: "uppercase", color: "#94a3b8", whiteSpace: "nowrap" }}>{h}</th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {filteredOrders.map((order) => {
                            const key = order.orderId || order.identifier;
                            const isExpanded = expandedOrderId === key;
                            const pm = order.paymentMode || "Cash";
                            return (
                                <Fragment key={key}>
                                    <tr
                                        style={{ borderTop: "1px solid #f1f5f9", background: isExpanded ? "#eff6ff" : "#ffffff", cursor: "pointer", transition: "background 0.12s" }}
                                        onMouseEnter={(e) => { if (!isExpanded) e.currentTarget.style.background = "#f8fafc"; }}
                                        onMouseLeave={(e) => { e.currentTarget.style.background = isExpanded ? "#eff6ff" : "#ffffff"; }}
                                        onClick={() => handleToggleExpand(key)}
                                    >
                                        <td style={{ padding: "0.9rem 1rem" }}>
                                            <div style={{ fontWeight: 700, color: "#0f172a", fontSize: "0.85rem" }}>{order.orderId || "-"}</div>
                                        </td>
                                        <td style={{ padding: "0.9rem 1rem", color: "#475569", fontSize: "0.82rem" }}>{order.identifier || "Walk-in"}</td>
                                        <td style={{ padding: "0.9rem 1rem" }}>
                                            <span style={{ borderRadius: "9999px", background: paymentBadge[pm] || "#f1f5f9", color: paymentText[pm] || "#374151", fontSize: "0.7rem", fontWeight: 700, padding: "0.25rem 0.65rem" }}>{pm}</span>
                                        </td>
                                        <td style={{ padding: "0.9rem 1rem", color: "#475569", fontSize: "0.82rem" }}>{formatDateTime(order.orderDate)}</td>
                                        <td style={{ padding: "0.9rem 1rem", fontWeight: 700, color: "#1d4ed8", fontSize: "0.85rem", textAlign: "right" }}>{formatCurrency(order.totalPrice)}</td>
                                        <td style={{ padding: "0.9rem 1rem", textAlign: "right" }}>
                                            <button
                                                onClick={(e) => { e.stopPropagation(); handleToggleExpand(key); }}
                                                style={{ padding: "0.35rem 0.9rem", borderRadius: "0.5rem", border: `1px solid ${isExpanded ? "#2563eb" : "#e2e8f0"}`, background: isExpanded ? "#eff6ff" : "#fff", color: isExpanded ? "#2563eb" : "#475569", fontSize: "0.75rem", fontWeight: 700, cursor: "pointer", whiteSpace: "nowrap", transition: "all 0.12s" }}>
                                                {isExpanded ? "▲ Hide" : "▼ View"}
                                            </button>
                                        </td>
                                    </tr>
                                    {isExpanded && (
                                        <OrderDetailRow
                                            order={order}
                                            productMap={productMap}
                                            onClose={() => setExpandedOrderId(null)}
                                            onReceipt={() => handleGenerateReceipt(order, productMap)}
                                            generating={generating}
                                        />
                                    )}
                                </Fragment>
                            );
                        })}
                    </tbody>
                </table>
            </div>
        );
    };

    return (
        <div style={{ width: "100%", display: "flex", flexDirection: "column", gap: "1.5rem", fontFamily: "'Inter', system-ui, sans-serif" }}>

            {/* HERO */}
            <section style={{ borderRadius: "1.5rem", overflow: "hidden", border: "1px solid #e2e8f0", boxShadow: "0 2px 16px rgba(0,0,0,0.06)", background: "linear-gradient(135deg, #0f172a 0%, #1e293b 60%, #1e3a5f 100%)", padding: "2.5rem", color: "#ffffff", position: "relative" }}>
                <div style={{ position: "absolute", inset: 0, pointerEvents: "none", overflow: "hidden" }}>
                    <div style={{ position: "absolute", top: "-3rem", left: "-4rem", width: "16rem", height: "16rem", borderRadius: "50%", background: "#3b82f6", opacity: 0.15, filter: "blur(60px)" }} />
                    <div style={{ position: "absolute", bottom: "-2rem", right: "2rem", width: "12rem", height: "12rem", borderRadius: "50%", background: "#f59e0b", opacity: 0.12, filter: "blur(50px)" }} />
                </div>
                <div style={{ position: "relative", display: "flex", flexDirection: "column", gap: "1.5rem" }}>
                    <span style={{ display: "inline-flex", alignItems: "center", borderRadius: "9999px", border: "1px solid rgba(255,255,255,0.15)", background: "rgba(255,255,255,0.08)", padding: "0.35rem 1rem", fontSize: "0.65rem", fontWeight: 700, letterSpacing: "0.2em", textTransform: "uppercase", color: "#bfdbfe", width: "fit-content" }}>Order Desk</span>
                    <div style={{ maxWidth: "38rem" }}>
                        <h2 style={{ margin: "0 0 0.75rem", fontSize: "2rem", fontWeight: 900, lineHeight: 1.15, color: "#ffffff", letterSpacing: "-0.02em" }}>Review customer orders</h2>
                        <p style={{ margin: 0, fontSize: "0.9rem", lineHeight: 1.7, color: "#cbd5e1" }}>Click any row to expand details and generate a print-ready PDF receipt.</p>
                    </div>
                    {error && <div style={{ borderRadius: "0.75rem", border: "1px solid #fecaca", background: "#fff1f2", padding: "0.75rem 1rem", fontSize: "0.8rem", fontWeight: 600, color: "#dc2626", maxWidth: "max-content" }}>{error}</div>}
                    <div style={{ display: "grid", gridTemplateColumns: "repeat(3, minmax(0, 240px))", gap: "0.75rem" }}>
                        <StatCard label="Orders" value={metrics.count} />
                        <StatCard label="Revenue" value={formatCurrency(metrics.sales)} />
                        <StatCard label="Discount" value={formatCurrency(metrics.discount)} />
                    </div>
                </div>
            </section>

            {/* TABLE */}
            <section style={{ borderRadius: "1.25rem", border: "1px solid #e2e8f0", background: "#ffffff", padding: "1.5rem", boxShadow: "0 1px 6px rgba(0,0,0,0.04)", display: "flex", flexDirection: "column", gap: "1.25rem" }}>
                <div style={{ display: "flex", alignItems: "center", justifycontent: "space-between", gap: "1rem", flexWrap: "wrap" }}>
                    <div>
                        <p style={{ margin: 0, fontSize: "0.65rem", fontWeight: 700, letterSpacing: "0.2em", textTransform: "uppercase", color: "#2563eb" }}>Order History</p>
                        <h3 style={{ margin: "0.35rem 0 0", fontSize: "1.35rem", fontWeight: 800, color: "#0f172a" }}>Recent orders</h3>
                    </div>
                    <input type="text" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)}
                        placeholder="Search by ID, payment, coupon…" style={{ ...inputStyle, maxWidth: "20rem" }}
                        onFocus={(e) => { e.target.style.borderColor = "#3b82f6"; e.target.style.boxShadow = "0 0 0 3px rgba(59,130,246,0.12)"; }}
                        onBlur={(e) => { e.target.style.borderColor = "#d1d5db"; e.target.style.boxShadow = "none"; }}
                    />
                </div>

                <div style={{ borderRadius: "1rem", border: "1px solid #e2e8f0", overflow: "hidden" }}>
                    {renderOrdersContent()}
                </div>
            </section>

            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        </div>
    );
}