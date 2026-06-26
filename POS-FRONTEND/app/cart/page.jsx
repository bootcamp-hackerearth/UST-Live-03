"use client";
import { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import axiosInstance from "../api/axiosInstance";

function formatCurrency(value) {
    return new Intl.NumberFormat("en-IN", {
        style: "currency", currency: "INR", maximumFractionDigits: 2,
    }).format(Number(value) || 0);
}

function formatDateTime(value) {
    if (!value) return "-";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return String(value);
    return new Intl.DateTimeFormat("en-IN", { dateStyle: "medium", timeStyle: "short" }).format(date);
}

function OrderConfirmedPopup({ order, entries, subtotal, discount, total, onViewOrder, onNewOrder, generatingReceipt }) {
    const orderId = order?.orderId || order?.identifier || "-";
    const customer = order?.identifier || "Walk-in";
    const paymentMode = (order?.paymentMode || "Cash").toUpperCase();
    const orderDate = formatDateTime(order?.orderDate || new Date().toISOString());

    const handlePrint = () => {
        const printContent = document.getElementById("receipt-bill-content");
        if (!printContent) return;
        const html = `<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8"/>
  <title>Receipt - ${orderId}</title>
  <style>
    * { margin:0; padding:0; box-sizing:border-box; }
    body { font-family:'Segoe UI',system-ui,sans-serif; background:#fff; color:#0f172a; padding:32px; max-width:600px; margin:0 auto; }
    @media print { body { padding:16px; } }
  </style>
</head>
<body>${printContent.innerHTML}</body>
<script>window.onload=()=>{window.print();}</script>
</html>`;
        const blob = new Blob([html], { type: "text/html" });
        const url = URL.createObjectURL(blob);
        const win = window.open(url, "_blank");
        if (win) win.addEventListener("load", () => URL.revokeObjectURL(url));
    };

    return (
        <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.55)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 9999, padding: "16px" }}>
            <div style={{ background: "#fff", width: "100%", maxWidth: "520px", borderRadius: "16px", overflow: "hidden", boxShadow: "0 24px 64px rgba(0,0,0,0.25)", maxHeight: "90vh", display: "flex", flexDirection: "column" }}>

                <div style={{ background: "linear-gradient(135deg,#1a3a6e,#1e4db7)", padding: "28px 24px 24px", textAlign: "center", color: "#fff", flexShrink: 0 }}>
                    <div style={{ width: "52px", height: "52px", borderRadius: "50%", border: "3px solid rgba(255,255,255,0.5)", display: "flex", alignItems: "center", justifyContent: "center", margin: "0 auto 14px", fontSize: "26px", fontWeight: 900 }}>✓</div>
                    <h2 style={{ margin: 0, fontSize: "20px", fontWeight: 800, letterSpacing: "-0.01em" }}>Order Confirmed</h2>
                    <p style={{ margin: "6px 0 0", fontSize: "13px", color: "rgba(255,255,255,0.75)" }}>Receipt #{orderId}</p>
                </div>

                <div style={{ overflowY: "auto", flex: 1 }}>
                    <div id="receipt-bill-content" style={{ padding: "20px 24px", display: "flex", flexDirection: "column", gap: "0" }}>

                        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", paddingBottom: "14px", borderBottom: "1px solid #e2e8f0" }}>
                            <div>
                                <p style={{ margin: 0, fontSize: "10px", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.12em", color: "#94a3b8" }}>Customer</p>
                                <p style={{ margin: "4px 0 0", fontSize: "14px", fontWeight: 700, color: "#0f172a" }}>{customer}</p>
                            </div>
                            <div style={{ textAlign: "right" }}>
                                <p style={{ margin: 0, fontSize: "10px", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.12em", color: "#94a3b8" }}>Date &amp; Time</p>
                                <p style={{ margin: "4px 0 0", fontSize: "13px", fontWeight: 600, color: "#0f172a" }}>{orderDate}</p>
                            </div>
                        </div>

                        <div style={{ paddingTop: "14px", paddingBottom: "14px", borderBottom: "1px solid #e2e8f0" }}>
                            <p style={{ margin: "0 0 10px", fontSize: "10px", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.12em", color: "#94a3b8" }}>Items Ordered</p>
                            <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
                                {entries.length === 0 ? (
                                    <p style={{ fontSize: "13px", color: "#94a3b8" }}>No items</p>
                                ) : entries.map((e, i) => {
                                    const name = e.productname || e.product || "-";
                                    const qty = Number(e.quantity) || 0;
                                    const price = Number(e.sellingPrice ?? e.sellingprice ?? e.price ?? 0);
                                    const lineTotal = Number(e.totalPrice) || qty * price;
                                    return (
                                        <div key={e.identifier || i} style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                                            <div>
                                                <p style={{ margin: 0, fontSize: "14px", fontWeight: 700, color: "#0f172a" }}>{name}</p>
                                                <p style={{ margin: "2px 0 0", fontSize: "12px", color: "#64748b" }}>
                                                    Qty: {qty} × {formatCurrency(price)} = {formatCurrency(qty * price)}
                                                </p>
                                            </div>
                                            <span style={{ fontSize: "15px", fontWeight: 700, color: "#2563eb", whiteSpace: "nowrap", marginLeft: "12px" }}>{formatCurrency(lineTotal)}</span>
                                        </div>
                                    );
                                })}
                            </div>
                        </div>

                        <div style={{ paddingTop: "14px", paddingBottom: "14px", borderBottom: "2px solid #1e4db7", display: "flex", flexDirection: "column", gap: "8px" }}>
                            {discount > 0 && (
                                <div style={{ display: "flex", justifyContent: "space-between", fontSize: "13px" }}>
                                    <span style={{ color: "#64748b" }}>Discount</span>
                                    <span style={{ fontWeight: 700, color: "#16a34a" }}>− {formatCurrency(discount)}</span>
                                </div>
                            )}
                            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                                <span style={{ fontSize: "16px", fontWeight: 800, color: "#1e4db7" }}>Total Amount</span>
                                <span style={{ fontSize: "18px", fontWeight: 900, color: "#1e4db7" }}>{formatCurrency(total)}</span>
                            </div>
                        </div>

                        <div style={{ paddingTop: "14px", paddingBottom: "14px", borderBottom: "1px solid #e2e8f0", display: "grid", gridTemplateColumns: "1fr 1fr", gap: "12px" }}>
                            {[
                                { label: "Payment Mode", value: paymentMode },
                                { label: "Order ID", value: orderId },
                            ].map(({ label, value }) => (
                                <div key={label} style={{ background: "#f8fafc", borderRadius: "8px", padding: "10px 12px" }}>
                                    <p style={{ margin: 0, fontSize: "10px", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.12em", color: "#94a3b8" }}>{label}</p>
                                    <p style={{ margin: "4px 0 0", fontSize: "14px", fontWeight: 800, color: label === "Order ID" ? "#2563eb" : "#0f172a" }}>{value}</p>
                                </div>
                            ))}
                        </div>

                        <div style={{ paddingTop: "14px", textAlign: "center" }}>
                            <p style={{ margin: 0, fontSize: "13px", color: "#64748b", fontWeight: 600 }}>Thank you for your purchase!</p>
                            <p style={{ margin: "4px 0 0", fontSize: "12px", color: "#94a3b8" }}>Please keep this receipt for your records</p>
                        </div>
                    </div>
                </div>

                <div style={{ padding: "16px 24px", borderTop: "1px solid #e2e8f0", display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "10px", flexShrink: 0 }}>
                    <button
                        type="button"
                        onClick={handlePrint}
                        disabled={generatingReceipt}
                        style={{ padding: "11px 8px", borderRadius: "8px", border: "none", background: "#1e4db7", color: "#fff", fontWeight: 700, fontSize: "12px", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center", gap: "5px", opacity: generatingReceipt ? 0.7 : 1 }}>
                        🖨️ Print Receipt
                    </button>
                    <button
                        type="button"
                        onClick={onViewOrder}
                        style={{ padding: "11px 8px", borderRadius: "8px", border: "1.5px solid #e2e8f0", background: "#fff", color: "#475569", fontWeight: 700, fontSize: "12px", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center", gap: "5px" }}>
                        🗒️ View Order
                    </button>
                    <button
                        type="button"
                        onClick={onNewOrder}
                        style={{ padding: "11px 8px", borderRadius: "8px", border: "none", background: "#dc2626", color: "#fff", fontWeight: 700, fontSize: "12px", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center", gap: "5px" }}>
                        + New Order
                    </button>
                </div>
            </div>
        </div>
    );
}

OrderConfirmedPopup.propTypes = {
    order: PropTypes.shape({
        orderId: PropTypes.string,
        identifier: PropTypes.string,
        paymentMode: PropTypes.string,
        orderDate: PropTypes.string,
    }),
    entries: PropTypes.arrayOf(PropTypes.shape({
        identifier: PropTypes.string,
        productname: PropTypes.string,
        product: PropTypes.string,
        quantity: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        sellingPrice: PropTypes.number,
        sellingprice: PropTypes.number,
        price: PropTypes.number,
        totalPrice: PropTypes.number,
    })).isRequired,
    subtotal: PropTypes.number.isRequired,
    discount: PropTypes.number.isRequired,
    total: PropTypes.number.isRequired,
    onViewOrder: PropTypes.func.isRequired,
    onNewOrder: PropTypes.func.isRequired,
    generatingReceipt: PropTypes.bool.isRequired,
};

OrderConfirmedPopup.defaultProps = {
    order: null,
};

async function fetchCartData(identifier) {
    const res = await axiosInstance.post("/cart/getCart", { identifier });
    if (res.data?.success === false) throw new Error(res.data.message);
    return res.data;
}

async function createCartForCustomer(customerId) {
    const response = await axiosInstance.post("/cart/add", { identifier: customerId, status: true });
    return response.data;
}

async function fetchOrCreateCart(value, refreshCart) {
    try {
        await refreshCart(value);
    } catch (err) {
        console.error("Cart fetch failed, attempting to create new cart", err);
        await createCartForCustomer(value);
        await refreshCart(value);
    }
}

async function loadProductsAndPrices({ setProducts, setFilteredProducts, setProductPrices }) {
    try {
        const productRes = await axiosInstance.get("/product/findByStatus");
        const productList = productRes.data || [];
        setProducts(productList);
        setFilteredProducts(productList);

        const priceEntries = await Promise.allSettled(
            productList.map((p) =>
                axiosInstance.get("/price/identifier", { params: { identifier: p.identifier } })
            )
        );

        const priceMap = {};
        priceEntries.forEach((result, idx) => {
            if (result.status === "fulfilled" && result.value?.data) {
                const pr = result.value.data;
                const key = productList[idx].identifier;
                priceMap[key] = pr.sellingprice ?? pr.sellingPrice ?? pr.mrpprice ?? pr.mrpPrice ?? pr.costprice ?? pr.costPrice ?? 0;
            }
        });
        setProductPrices(priceMap);
    } catch (err) {
        console.error("Failed to load products and prices", err);
    }
}

function getCardStyles(isInCart, isCartAvailable) {
    return {
        background: isInCart ? "#eff6ff" : "#fff",
        border: `2px solid ${isInCart ? "#2563eb" : "#e2e8f0"}`,
        borderRadius: "12px",
        padding: "12px 8px",
        cursor: isCartAvailable ? "pointer" : "not-allowed",
        textAlign: "center",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        gap: "6px",
        position: "relative",
        transition: "border-color 0.15s",
    };
}

function filterCustomerSuggestions(customers, query) {
    const lowerQuery = query.toLowerCase();
    return customers.filter((customer) =>
        customer.identifier?.toLowerCase().includes(lowerQuery) ||
        customer.username?.toLowerCase().includes(lowerQuery)
    );
}

function CustomerSuggestionList({ showSuggestions, existingCustomers, onSelect }) {
    if (!showSuggestions || existingCustomers.length === 0) {
        return null;
    }

    return (
        <div style={{ position: "absolute", top: "100%", left: 0, right: 0, background: "#ffffff", border: "1px solid #e2e8f0", borderRadius: "8px", boxShadow: "0 4px 12px rgba(0,0,0,0.1)", zIndex: 10, marginTop: "4px", maxHeight: "180px", overflowY: "auto" }}>
            {existingCustomers.map((cust) => (
                <button
                    key={cust.identifier}
                    type="button"
                    onClick={() => onSelect(cust)}
                    style={{
                        width: "100%",
                        padding: "10px 12px",
                        cursor: "pointer",
                        border: "none",
                        background: "#fff",
                        textAlign: "left",
                        fontSize: "13px",
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                    }}
                    onMouseEnter={(e) => { e.currentTarget.style.background = "#f8fafc"; }}
                    onMouseLeave={(e) => { e.currentTarget.style.background = "#fff"; }}
                >
                    <span style={{ fontWeight: 600, color: "#1e293b" }}>{cust.username || "Registered Customer"}</span>
                    <span style={{ color: "#2563eb" }}>{cust.identifier}</span>
                </button>
            ))}
        </div>
    );
}

CustomerSuggestionList.propTypes = {
    showSuggestions: PropTypes.bool.isRequired,
    existingCustomers: PropTypes.arrayOf(PropTypes.shape({
        identifier: PropTypes.string,
        username: PropTypes.string,
    })).isRequired,
    onSelect: PropTypes.func.isRequired,
};

function CustomerStatusBanner({ customerFound, bannerDismissed, phoneSearch, router, handleClearCustomer, setBannerDismissed }) {
    if (customerFound && !bannerDismissed) {
        return (
            <div style={{ display: "flex", alignItems: "center", gap: "8px", padding: "8px 12px", background: "#f0fdf4", border: "1px solid #bbf7d0", borderRadius: "8px" }}>
                <span style={{ fontSize: "14px", color: "#16a34a", fontWeight: 700 }}>✓</span>
                <div style={{ flex: 1, fontSize: "13px", color: "#166534" }}>
                    <strong>{customerFound.username || customerFound.identifier}</strong>
                    {customerFound.username && <span style={{ marginLeft: "8px", color: "#4ade80" }}>— {customerFound.identifier}</span>}
                </div>
                <button
                    type="button"
                    onClick={() => setBannerDismissed(true)}
                    style={{ background: "none", border: "none", cursor: "pointer", color: "#94a3b8", fontSize: "16px", lineHeight: 1, padding: "2px 4px" }}>
                    ✕
                </button>
            </div>
        );
    }

    if (customerFound && bannerDismissed) {
        return (
            <div style={{ display: "flex", alignItems: "center", gap: "8px", padding: "6px 12px", background: "#f8fafc", border: "1px solid #e2e8f0", borderRadius: "8px" }}>
                <span style={{ fontSize: "12px", color: "#16a34a", fontWeight: 700 }}>✓</span>
                <span style={{ flex: 1, fontSize: "12px", color: "#475569" }}>
                    <strong>{customerFound.identifier}</strong>
                    {customerFound.username && <span style={{ marginLeft: "6px", color: "#94a3b8" }}>({customerFound.username})</span>}
                </span>
                <button
                    type="button"
                    onClick={handleClearCustomer}
                    style={{ background: "none", border: "1px solid #e2e8f0", cursor: "pointer", color: "#94a3b8", fontSize: "11px", fontWeight: 600, padding: "2px 6px", borderRadius: "4px" }}
                    onMouseEnter={(e) => { e.currentTarget.style.color = "#ef4444"; }}
                    onMouseLeave={(e) => { e.currentTarget.style.color = "#94a3b8"; }}>
                    Change
                </button>
            </div>
        );
    }

    if (customerFound === false) {
        return (
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "8px 12px", background: "#fef2f2", border: "1px solid #fecaca", borderRadius: "8px" }}>
                <span style={{ fontSize: "13px", color: "#dc2626" }}>No customer found for &ldquo;{phoneSearch}&rdquo;</span>
                <button
                    type="button"
                    onClick={() => router.push("/customer/add?redirectTo=/cart")}
                    style={{ padding: "6px 14px", background: "#2563eb", color: "#fff", border: "none", borderRadius: "6px", fontSize: "12px", fontWeight: 600, cursor: "pointer", whiteSpace: "nowrap" }}>
                    + Add Customer
                </button>
            </div>
        );
    }

    return null;
}

CustomerStatusBanner.propTypes = {
    customerFound: PropTypes.oneOfType([PropTypes.object, PropTypes.bool]),
    bannerDismissed: PropTypes.bool.isRequired,
    phoneSearch: PropTypes.string.isRequired,
    router: PropTypes.object.isRequired,
    handleClearCustomer: PropTypes.func.isRequired,
    setBannerDismissed: PropTypes.func.isRequired,
};

CustomerStatusBanner.defaultProps = {
    customerFound: null,
};

function CartTableRows({ cart, entries, handleQtyChange, handleDeleteEntry, processingEntry, actionLoading }) {
    if (entries.length === 0) {
        return (
            <tr>
                <td colSpan={7} style={{ padding: "40px", textAlign: "center", color: "#94a3b8", fontSize: "13px" }}>
                    {cart ? "No items yet — click a product on the right to add." : "Select a customer to start a cart."}
                </td>
            </tr>
        );
    }

    return entries.map((entry, i) => {
        const unitPrice = entry.sellingPrice ?? entry.sellingprice ?? entry.price ?? 0;
        return (
            <tr
                key={entry.identifier || i}
                style={{ borderBottom: "1px solid #f1f5f9" }}
                onMouseEnter={(e) => { e.currentTarget.style.background = "#fafafa"; }}
                onMouseLeave={(e) => { e.currentTarget.style.background = "transparent"; }}
            >
                <td style={{ padding: "10px 12px", color: "#1e293b", fontWeight: 500 }}>{entry.productname || entry.product}</td>
                <td style={{ padding: "10px 12px", color: "#64748b", fontSize: "12px" }}>{entry.productCode || entry.identifier}</td>
                <td style={{ padding: "10px 12px", color: "#64748b" }}>—</td>
                <td style={{ padding: "10px 12px", color: "#1e293b" }}>{formatCurrency(unitPrice)}</td>
                <td style={{ padding: "6px 12px" }}>
                    <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                        <button
                            type="button"
                            onClick={() => handleQtyChange(entry, -1)}
                            disabled={processingEntry === entry.product}
                            style={{ width: "26px", height: "26px", border: "1px solid #fecaca", borderRadius: "6px", background: "#fef2f2", cursor: "pointer", fontWeight: 700, color: "#ef4444", fontSize: "16px", display: "flex", alignItems: "center", justifyContent: "center" }}>
                            −
                        </button>
                        <span style={{ minWidth: "24px", textAlign: "center", fontWeight: 700, fontSize: "14px" }}>
                            {processingEntry === entry.product ? "…" : Number(entry.quantity)}
                        </span>
                        <button
                            type="button"
                            onClick={() => handleQtyChange(entry, 1)}
                            disabled={processingEntry === entry.product}
                            style={{ width: "26px", height: "26px", border: "1px solid #bbf7d0", borderRadius: "6px", background: "#f0fdf4", cursor: "pointer", fontWeight: 700, color: "#16a34a", fontSize: "16px", display: "flex", alignItems: "center", justifyContent: "center" }}>
                            +
                        </button>
                    </div>
                </td>
                <td style={{ padding: "10px 12px", fontWeight: 700, color: "#2563eb" }}>{formatCurrency(entry.totalPrice ?? 0)}</td>
                <td style={{ padding: "10px 12px" }}>
                    <button
                        type="button"
                        onClick={() => handleDeleteEntry(entry)}
                        disabled={actionLoading}
                        style={{ background: "none", border: "none", cursor: "pointer", color: "#94a3b8", fontSize: "18px" }}
                        onMouseEnter={(e) => { e.currentTarget.style.color = "#ef4444"; }}
                        onMouseLeave={(e) => { e.currentTarget.style.color = "#94a3b8"; }}>
                        ✕
                    </button>
                </td>
            </tr>
        );
    });
}

CartTableRows.propTypes = {
    cart: PropTypes.object,
    entries: PropTypes.array.isRequired,
    handleQtyChange: PropTypes.func.isRequired,
    handleDeleteEntry: PropTypes.func.isRequired,
    processingEntry: PropTypes.string,
    actionLoading: PropTypes.bool.isRequired,
};

CartTableRows.defaultProps = {
    cart: null,
    processingEntry: null,
};

function FilterButtons({ selectedCategory, selectedBrand, categoryList, brands, setSelectedCategory, setSelectedBrand }) {
    return (
        <div style={{ display: "flex", gap: "6px", flexWrap: "wrap" }}>
            <button
                type="button"
                onClick={() => { setSelectedCategory(""); setSelectedBrand(""); }}
                style={{ padding: "4px 12px", borderRadius: "20px", border: "1px solid #e2e8f0", background: selectedCategory === "" && selectedBrand === "" ? "#2563eb" : "#fff", color: selectedCategory === "" && selectedBrand === "" ? "#fff" : "#475569", fontSize: "11px", cursor: "pointer", fontWeight: 600 }}>
                All
            </button>
            {categoryList.slice(0, 4).map((cat) => (
                <button
                    key={cat}
                    type="button"
                    onClick={() => setSelectedCategory(selectedCategory === cat ? "" : cat)}
                    style={{ padding: "4px 12px", borderRadius: "20px", border: "1px solid #e2e8f0", background: selectedCategory === cat ? "#2563eb" : "#fff", color: selectedCategory === cat ? "#fff" : "#475569", fontSize: "11px", cursor: "pointer", fontWeight: 600 }}>
                    {cat}
                </button>
            ))}
            {brands.slice(0, 3).map((b) => (
                <button
                    key={b.identifier}
                    type="button"
                    onClick={() => setSelectedBrand(selectedBrand === b.identifier ? "" : b.identifier)}
                    style={{ padding: "4px 12px", borderRadius: "20px", border: "1px solid #e2e8f0", background: selectedBrand === b.identifier ? "#7c3aed" : "#fff", color: selectedBrand === b.identifier ? "#fff" : "#475569", fontSize: "11px", cursor: "pointer", fontWeight: 600 }}>
                    {b.identifier}
                </button>
            ))}
        </div>
    );
}

FilterButtons.propTypes = {
    selectedCategory: PropTypes.string.isRequired,
    selectedBrand: PropTypes.string.isRequired,
    categoryList: PropTypes.array.isRequired,
    brands: PropTypes.array.isRequired,
    setSelectedCategory: PropTypes.func.isRequired,
    setSelectedBrand: PropTypes.func.isRequired,
};

function CartTotals({ subTotal, discount, totalAmount, handleCancel, handleSale, cancelDisabled, saleEnabled, actionLoading }) {
    return (
        <div style={{ display: "flex", flexDirection: "column", justifyContent: "space-between" }}>
            <div style={{ display: "flex", flexDirection: "column", gap: "8px", fontSize: "13px" }}>
                <div style={{ display: "flex", justifyContent: "space-between", color: "#64748b" }}>
                    <span>Sub Total</span>
                    <span style={{ fontWeight: 600, color: "#1e293b" }}>{formatCurrency(subTotal)}</span>
                </div>
                <div style={{ display: "flex", justifyContent: "space-between", color: "#64748b" }}>
                    <span>Total Discount</span>
                    <span style={{ fontWeight: 600, color: "#16a34a" }}>-{formatCurrency(discount)}</span>
                </div>
                <div style={{ height: "1px", background: "#e2e8f0" }} />
                <div style={{ display: "flex", justifyContent: "space-between" }}>
                    <span style={{ fontWeight: 700, fontSize: "15px", color: "#1e293b" }}>Total Amount</span>
                    <span style={{ fontWeight: 700, fontSize: "15px", color: "#2563eb" }}>{formatCurrency(totalAmount)}</span>
                </div>
            </div>
            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginTop: "12px" }}>
                <button
                    type="button"
                    onClick={handleCancel}
                    disabled={cancelDisabled}
                    style={{ padding: "11px", borderRadius: "10px", border: "none", background: "#f59e0b", color: "#fff", fontWeight: 700, fontSize: "13px", cursor: cancelDisabled ? "not-allowed" : "pointer", opacity: cancelDisabled ? 0.55 : 1 }}>
                    Cancel
                </button>
                <button
                    type="button"
                    onClick={handleSale}
                    disabled={!saleEnabled}
                    style={{ padding: "11px", borderRadius: "10px", border: "none", background: "#dc2626", color: "#fff", fontWeight: 700, fontSize: "13px", cursor: saleEnabled ? "pointer" : "not-allowed", opacity: saleEnabled ? 1 : 0.55 }}>
                    {actionLoading ? "Processing..." : "Sale"}
                </button>
            </div>
        </div>
    );
}

CartTotals.propTypes = {
    subTotal: PropTypes.number.isRequired,
    discount: PropTypes.number.isRequired,
    totalAmount: PropTypes.number.isRequired,
    handleCancel: PropTypes.func.isRequired,
    handleSale: PropTypes.func.isRequired,
    cancelDisabled: PropTypes.bool.isRequired,
    saleEnabled: PropTypes.bool.isRequired,
    actionLoading: PropTypes.bool.isRequired,
};

function computeCartState(phoneSearch, customerFound, entries, cart, actionLoading) {
    const hasSearchPhoneQuery = phoneSearch.trim().length > 0;
    const customerMissing = customerFound === null || customerFound === false;
    const searchEnabled = hasSearchPhoneQuery && customerMissing;
    const hasActiveEntries = entries.length > 0;
    const isActionIdle = !actionLoading;
    const saleEnabled = hasActiveEntries && isActionIdle;
    const cancelDisabled = cart === null || actionLoading;
    return { hasSearchPhoneQuery, customerMissing, searchEnabled, hasActiveEntries, isActionIdle, saleEnabled, cancelDisabled };
}

async function lookupCustomer(phone) {
    try {
        const res = await axiosInstance.get("/customer/identifier", { params: { identifier: phone } });
        return res.data || null;
    } catch (err) {
        console.error("Lookup customer error:", err);
        return null;
    }
}

async function submitOrder(cartId, paymentType) {
    const res = await axiosInstance.post("/order/create", {
        identifier: cartId,
        paymentMode: paymentType,
        status: true,
    });
    return res.data?.orderId || res.data?.identifier || res.data?.id || "Order";
}

function createOrderSnapshot(entries, cart, products) {
    const snapshotEntries = entries.map((entry) => {
        const productName = entry.productname ||
            products.find((p) => p.identifier === entry.product)?.productname ||
            entry.product ||
            "-";
        return { ...entry, productname: productName };
    });

    const snapshotSubtotal = entries.reduce(
        (sum, entry) => sum + (Number(entry.quantity) || 0) * (Number(entry.price ?? entry.sellingPrice) || 0),
        0,
    );
    const snapshotDiscount = Number(cart?.totalDiscount || 0);
    const snapshotTotal = Number(cart?.totalPrice ?? (snapshotSubtotal - snapshotDiscount)) || 0;

    return { snapshotEntries, snapshotSubtotal, snapshotDiscount, snapshotTotal };
}

function CartPageView() {
    const router = useRouter();
    const [phoneSearch, setPhoneSearch] = useState("");
    const [customerFound, setCustomerFound] = useState(null);
    const [bannerDismissed, setBannerDismissed] = useState(false);
    const [searchingCustomer, setSearchingCustomer] = useState(false);
    const [cart, setCart] = useState(null);
    const [entries, setEntries] = useState([]);
    const [existingCustomers, setExistingCustomers] = useState([]);
    const [showSuggestions, setShowSuggestions] = useState(false);
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [brands, setBrands] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("");
    const [selectedBrand, setSelectedBrand] = useState("");
    const [productPrices, setProductPrices] = useState({});
    const [receiveAmount, setReceiveAmount] = useState("");
    const [paymentType, setPaymentType] = useState("Cash");
    const [note, setNote] = useState("");
    const [actionLoading, setActionLoading] = useState(false);
    const [processingEntry, setProcessingEntry] = useState(null);
    const [error, setError] = useState("");
    const [showSuccessPopup, setShowSuccessPopup] = useState(false);
    const [completedOrder, setCompletedOrder] = useState(null);
    const [completedEntries, setCompletedEntries] = useState([]);
    const [completedSubtotal, setCompletedSubtotal] = useState(0);
    const [completedDiscount, setCompletedDiscount] = useState(0);
    const [completedTotal, setCompletedTotal] = useState(0);
    const [generatingReceipt] = useState(false);

    const today = new Date().toLocaleDateString("en-GB");

    useEffect(() => {
        const query = phoneSearch.trim();
        if (query.length < 2 || customerFound) {
            setExistingCustomers([]);
            return undefined;
        }

        const delayDebounce = setTimeout(() => {
            axiosInstance.post("/customer/list", {
                page: 0,
                sizePerPage: 5,
                sortDirection: "ASC",
                sortField: "identifier",
            })
                .then((res) => {
                    const data = res.data?.dtoList ?? res.data?.content ?? res.data ?? [];
                    setExistingCustomers(filterCustomerSuggestions(data, query));
                })
                .catch((err) => console.error("Auto customer lookup failed", err));
        }, 300);

        return () => clearTimeout(delayDebounce);
    }, [phoneSearch, customerFound]);

    useEffect(() => {
        loadProductsAndPrices({ setProducts, setFilteredProducts, setProductPrices });
        axiosInstance.get("/category/subcategory")
            .then((res) => setCategories(res.data || []))
            .catch((err) => console.error("Failed to load categories", err));
        axiosInstance.get("/brand/findByStatus")
            .then((res) => setBrands(res.data || []))
            .catch((err) => console.error("Failed to load brands", err));
    }, []);

    useEffect(() => {
        let list = [...products];
        if (searchTerm) {
            const s = searchTerm.toLowerCase();
            list = list.filter((p) => p.productname?.toLowerCase().includes(s) || p.identifier?.toLowerCase().includes(s));
        }
        if (selectedCategory) list = list.filter((p) => Array.isArray(p.category) && p.category.includes(selectedCategory));
        if (selectedBrand) list = list.filter((p) => p.brand === selectedBrand);
        setFilteredProducts(list);
    }, [searchTerm, selectedCategory, selectedBrand, products]);

    const refreshCart = useCallback(async (identifier) => {
        const cartData = await fetchCartData(identifier);
        setCart(cartData);
        const entryList = cartData.cartEntryDtoList || [];
        setEntries(entryList);
        setProductPrices((prev) => {
            const updated = { ...prev };
            entryList.forEach((e) => {
                const price = e.sellingPrice ?? e.sellingprice ?? e.price ?? null;
                if (e.product && price !== null) updated[e.product] = price;
            });
            return updated;
        });
    }, []);

    const handleCustomerSelect = useCallback(async (value) => {
        setCart(null);
        setEntries([]);
        setError("");
        if (!value) return;
        try {
            await fetchOrCreateCart(value, refreshCart);
        } catch (err) {
            console.error("Failed to load or create cart", err);
            setError("Failed to create cart for this customer.");
        }
    }, [refreshCart]);

    const handlePhoneSearch = async () => {
        const phone = phoneSearch.trim();
        if (!phone) return;

        setSearchingCustomer(true);
        setCustomerFound(null);
        setBannerDismissed(false);
        setError("");
        setCart(null);
        setEntries([]);
        setShowSuggestions(false);

        try {
            const customer = await lookupCustomer(phone);
            if (!customer) {
                setCustomerFound(false);
                return;
            }
            setCustomerFound(customer);
            await handleCustomerSelect(customer.identifier);
        } catch (err) {
            console.error("Customer search error", err);
            setCustomerFound(false);
        } finally {
            setSearchingCustomer(false);
        }
    };

    const handleSelectExisting = async (customer) => {
        setPhoneSearch(customer.identifier);
        setCustomerFound(customer);
        setBannerDismissed(false);
        setShowSuggestions(false);
        await handleCustomerSelect(customer.identifier);
    };

    const handleClearCustomer = () => {
        setCustomerFound(null);
        setBannerDismissed(false);
        setPhoneSearch("");
        setCart(null);
        setEntries([]);
        setError("");
        setExistingCustomers([]);
    };

    const handleAddProduct = async (product) => {
        const explicitCartId = cart?.identifier || phoneSearch.trim();
        if (!explicitCartId) return;
        try {
            await axiosInstance.post("/cartEntry/addEntry", { cart: explicitCartId, product: product.identifier, quantity: 1 });
            await refreshCart(explicitCartId);
        } catch (err) {
            console.error("Failed to add product", err);
            setError("Failed to add product.");
        }
    };

    const handleQtyChange = async (entry, delta) => {
        if (processingEntry === entry.product) return;

        const newQty = Number(entry.quantity || 0) + delta;
        const cartId = entry.cart || cart?.identifier;

        setProcessingEntry(entry.product);
        setError("");

        try {
            if (newQty <= 0) {
                await handleDeleteEntry(entry);
                return;
            }
            await axiosInstance.post("/cartEntry/addEntry", { cart: cartId, product: entry.product, quantity: delta });
            await refreshCart(cartId);
        } catch (err) {
            console.error("Failed to update quantity", err);
            setError(err.response?.data?.message || "Failed to update quantity.");
        } finally {
            setProcessingEntry(null);
        }
    };

    const handleDeleteEntry = async (entry) => {
        setActionLoading(true);
        setError("");
        const cartId = entry.cart || cart?.identifier;
        try {
            await axiosInstance.get("/cart/deleteEntry", { params: { identifier: entry.identifier, cart: cartId } });
            await refreshCart(cartId);
        } catch (err) {
            console.error("Failed to remove item", err);
            setError("Failed to remove item.");
        } finally {
            setActionLoading(false);
        }
    };

    const handleCancel = async () => {
        if (!cart) return;
        setActionLoading(true);
        try {
            await axiosInstance.post("/cart/deleteCart", { identifier: cart.identifier });
            setCart(null);
            setEntries([]);
            setPhoneSearch("");
            setCustomerFound(null);
            setReceiveAmount("");
            setNote("");
        } catch (err) {
            console.error("Failed to cancel cart", err);
            setError("Failed to cancel cart.");
        } finally {
            setActionLoading(false);
        }
    };

    const handleSale = async () => {
        if (!cart || entries.length === 0) {
            setError("Cart is empty.");
            return;
        }

        setActionLoading(true);
        setError("");

        try {
            const snapshot = createOrderSnapshot(entries, cart, products);
            const orderId = await submitOrder(cart.identifier, paymentType);

            setCompletedOrder({ orderId, identifier: cart.identifier, paymentMode: paymentType, orderDate: new Date().toISOString() });
            setCompletedEntries(snapshot.snapshotEntries);
            setCompletedSubtotal(snapshot.snapshotSubtotal);
            setCompletedDiscount(snapshot.snapshotDiscount);
            setCompletedTotal(snapshot.snapshotTotal);

            setCart(null);
            setEntries([]);
            setPhoneSearch("");
            setCustomerFound(null);
            setBannerDismissed(false);
            setReceiveAmount("");
            setNote("");
            setShowSuccessPopup(true);
        } catch (err) {
            console.error("Order submission error:", err);
            let errorMsg = "Failed to place order";
            if (err.response?.status === 403) {
                errorMsg = "Permission denied. Your role may not have access to create orders.";
            } else if (err.response?.data?.message) {
                errorMsg = err.response.data.message;
            } else if (err.message) {
                errorMsg = err.message;
            }
            setError(errorMsg);
        } finally {
            setActionLoading(false);
        }
    };

    const subTotal = entries.reduce((sum, e) => sum + Number(e.quantity || 0) * Number(e.price || 0), 0);
    const discount = Number(cart?.totalDiscount || 0);
    const totalAmount = Number(cart?.totalPrice || 0);
    const receive = Number(receiveAmount) || 0;
    const changeAmount = Math.max(0, receive - totalAmount);
    const dueAmount = Math.max(0, totalAmount - receive);
    const categoryList = categories.map((c) => (typeof c === "string" ? c : c.identifier));

    const { searchEnabled, saleEnabled, cancelDisabled } = computeCartState(phoneSearch, customerFound, entries, cart, actionLoading);

    return (
        <CartPageRender
            cart={cart}
            entries={entries}
            brands={brands}
            existingCustomers={existingCustomers}
            paymentType={paymentType}
            searchTerm={searchTerm}
            selectedCategory={selectedCategory}
            selectedBrand={selectedBrand}
            phoneSearch={phoneSearch}
            customerFound={customerFound}
            bannerDismissed={bannerDismissed}
            showSuggestions={showSuggestions}
            searchingCustomer={searchingCustomer}
            actionLoading={actionLoading}
            processingEntry={processingEntry}
            receiveAmount={receiveAmount}
            note={note}
            showSuccessPopup={showSuccessPopup}
            completedOrder={completedOrder}
            completedEntries={completedEntries}
            completedSubtotal={completedSubtotal}
            completedDiscount={completedDiscount}
            completedTotal={completedTotal}
            generatingReceipt={generatingReceipt}
            router={router}
            error={error}
            today={today}
            subTotal={subTotal}
            discount={discount}
            totalAmount={totalAmount}
            receive={receive}
            changeAmount={changeAmount}
            dueAmount={dueAmount}
            categoryList={categoryList}
            searchEnabled={searchEnabled}
            saleEnabled={saleEnabled}
            cancelDisabled={cancelDisabled}
            setPhoneSearch={setPhoneSearch}
            setShowSuggestions={setShowSuggestions}
            setCustomerFound={setCustomerFound}
            setBannerDismissed={setBannerDismissed}
            setReceiveAmount={setReceiveAmount}
            setNote={setNote}
            setPaymentType={setPaymentType}
            setSearchTerm={setSearchTerm}
            setSelectedCategory={setSelectedCategory}
            setSelectedBrand={setSelectedBrand}
            setShowSuccessPopup={setShowSuccessPopup}
            handlePhoneSearch={handlePhoneSearch}
            handleSelectExisting={handleSelectExisting}
            handleClearCustomer={handleClearCustomer}
            handleQtyChange={handleQtyChange}
            handleDeleteEntry={handleDeleteEntry}
            handleCancel={handleCancel}
            handleSale={handleSale}
            handleAddProduct={handleAddProduct}
            filteredProducts={filteredProducts}
            productPrices={productPrices}
        />
    );
}

function CartPageRender({
    cart,
    entries,
    brands,
    existingCustomers,
    paymentType,
    searchTerm,
    selectedCategory,
    selectedBrand,
    phoneSearch,
    customerFound,
    bannerDismissed,
    showSuggestions,
    searchingCustomer,
    actionLoading,
    processingEntry,
    receiveAmount,
    note,
    showSuccessPopup,
    completedOrder,
    completedEntries,
    completedSubtotal,
    completedDiscount,
    completedTotal,
    generatingReceipt,
    router,
    error,
    today,
    subTotal,
    discount,
    totalAmount,
    receive,
    changeAmount,
    dueAmount,
    categoryList,
    searchEnabled,
    saleEnabled,
    cancelDisabled,
    setPhoneSearch,
    setShowSuggestions,
    setCustomerFound,
    setBannerDismissed,
    setReceiveAmount,
    setNote,
    setPaymentType,
    setSearchTerm,
    setSelectedCategory,
    setSelectedBrand,
    setShowSuccessPopup,
    handlePhoneSearch,
    handleSelectExisting,
    handleClearCustomer,
    handleQtyChange,
    handleDeleteEntry,
    handleCancel,
    handleSale,
    handleAddProduct,
    filteredProducts,
    productPrices,
}) {
    return (
        <div style={{ display: "flex", height: "calc(100vh - 64px)", overflow: "hidden", background: "#f8fafc" }}>
            <div style={{ display: "flex", flexDirection: "column", width: "60%", background: "#fff", borderRight: "1px solid #e2e8f0", overflow: "hidden" }}>
                <div style={{ padding: "12px 16px", borderBottom: "1px solid #e2e8f0", display: "flex", flexDirection: "column", gap: "8px" }}>
                    {error && (
                        <div style={{ background: "#fef2f2", border: "1px solid #fecaca", color: "#dc2626", borderRadius: "8px", padding: "8px 12px", fontSize: "12px" }}>
                            {error}
                        </div>
                    )}
                    <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "8px" }}>
                        <input
                            value={cart?.identifier || ""}
                            readOnly
                            placeholder="Cart ID (auto)"
                            style={{ border: "1px solid #e2e8f0", borderRadius: "8px", padding: "8px 12px", fontSize: "13px", background: "#f8fafc", color: "#64748b", cursor: "not-allowed" }}
                        />
                        <input
                            value={today}
                            readOnly
                            style={{ border: "1px solid #e2e8f0", borderRadius: "8px", padding: "8px 12px", fontSize: "13px", background: "#f8fafc", color: "#64748b", cursor: "not-allowed" }}
                        />
                    </div>
                    <div style={{ display: "flex", flexDirection: "column", gap: "6px", position: "relative" }}>
                        <div style={{ display: "flex", gap: "8px" }}>
                            <input
                                type="tel"
                                placeholder="Search customer by phone or name..."
                                value={phoneSearch}
                                onChange={(e) => {
                                    setPhoneSearch(e.target.value);
                                    setShowSuggestions(true);
                                    if (customerFound !== null) {
                                        setCustomerFound(null);
                                        setBannerDismissed(false);
                                    }
                                }}
                                onFocus={() => setShowSuggestions(true)}
                                onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
                                onKeyDown={(e) => e.key === "Enter" && handlePhoneSearch()}
                                disabled={!!customerFound}
                                style={{ flex: 1, border: "1px solid #cbd5e1", borderRadius: "8px", padding: "8px 12px", fontSize: "13px", outline: "none", background: customerFound ? "#f8fafc" : "#fff", color: "#1e293b" }}
                            />
                            <button
                                type="button"
                                onClick={handlePhoneSearch}
                                disabled={!searchEnabled || searchingCustomer}
                                style={{ padding: "8px 16px", borderRadius: "8px", background: "#2563eb", color: "#fff", border: "none", fontWeight: 600, fontSize: "13px", cursor: searchEnabled ? "pointer" : "not-allowed", opacity: searchEnabled ? 1 : 0.5, whiteSpace: "nowrap" }}>
                                {searchingCustomer ? "..." : "Search"}
                            </button>
                        </div>

                        <CustomerSuggestionList
                            showSuggestions={showSuggestions}
                            existingCustomers={existingCustomers}
                            onSelect={handleSelectExisting}
                        />

                        <CustomerStatusBanner
                            customerFound={customerFound}
                            bannerDismissed={bannerDismissed}
                            phoneSearch={phoneSearch}
                            router={router}
                            handleClearCustomer={handleClearCustomer}
                            setBannerDismissed={setBannerDismissed}
                        />
                    </div>
                </div>

                <div style={{ flex: 1, overflowY: "auto" }}>
                    <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "13px" }}>
                        <thead>
                            <tr style={{ background: "#f1f5f9", position: "sticky", top: 0, zIndex: 1 }}>
                                {["Items", "Code", "Unit", "Sale Price", "Qty", "Sub Total", ""].map((h) => (
                                    <th key={h} style={{ padding: "10px 12px", textAlign: "left", fontWeight: 600, color: "#475569", whiteSpace: "nowrap", borderBottom: "1px solid #e2e8f0" }}>{h}</th>
                                ))}
                            </tr>
                        </thead>
                        <tbody>
                            <CartTableRows
                                cart={cart}
                                entries={entries}
                                handleQtyChange={handleQtyChange}
                                handleDeleteEntry={handleDeleteEntry}
                                processingEntry={processingEntry}
                                actionLoading={actionLoading}
                            />
                        </tbody>
                    </table>
                </div>

                <div style={{ borderTop: "2px solid #e2e8f0", padding: "12px 16px", display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
                    <div style={{ display: "flex", flexDirection: "column", gap: "7px" }}>
                        {[
                            { label: "Receive Amount", value: receiveAmount, onChange: (v) => setReceiveAmount(v), editable: true, type: "number" },
                            { label: "Change Amount", value: changeAmount.toFixed(2), editable: false },
                            { label: "Due Amount", value: dueAmount.toFixed(2), editable: false },
                        ].map(({ label, value, onChange, editable, type }) => (
                            <div key={label} style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                                <label htmlFor={`field-${label}`} style={{ width: "108px", fontSize: "11px", color: "#64748b", fontWeight: 600, flexShrink: 0 }}>{label}</label>
                                <input
                                    id={`field-${label}`}
                                    type={type || "text"}
                                    value={value}
                                    onChange={editable ? (e) => onChange(e.target.value) : undefined}
                                    readOnly={!editable}
                                    placeholder="0"
                                    style={{ flex: 1, border: "1px solid #e2e8f0", borderRadius: "6px", padding: "6px 10px", fontSize: "13px", background: editable ? "#fff" : "#f8fafc", color: editable ? "#1e293b" : "#64748b", outline: "none" }}
                                />
                            </div>
                        ))}
                        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                            <label htmlFor="field-payment-type" style={{ width: "108px", fontSize: "11px", color: "#64748b", fontWeight: 600, flexShrink: 0 }}>Payment Type</label>
                            <select
                                id="field-payment-type"
                                value={paymentType}
                                onChange={(e) => setPaymentType(e.target.value)}
                                style={{ flex: 1, border: "1px solid #e2e8f0", borderRadius: "6px", padding: "6px 10px", fontSize: "13px", outline: "none" }}>
                                <option>Cash</option>
                                <option>Card</option>
                                <option>UPI</option>
                            </select>
                        </div>
                        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                            <label htmlFor="field-note" style={{ width: "108px", fontSize: "11px", color: "#64748b", fontWeight: 600, flexShrink: 0 }}>Note</label>
                            <input
                                id="field-note"
                                value={note}
                                onChange={(e) => setNote(e.target.value)}
                                placeholder="Type note..."
                                style={{ flex: 1, border: "1px solid #e2e8f0", borderRadius: "6px", padding: "6px 10px", fontSize: "13px", outline: "none" }}
                            />
                        </div>
                    </div>
                    <CartTotals
                        subTotal={subTotal}
                        discount={discount}
                        totalAmount={totalAmount}
                        handleCancel={handleCancel}
                        handleSale={handleSale}
                        cancelDisabled={cancelDisabled}
                        saleEnabled={saleEnabled}
                        actionLoading={actionLoading}
                    />
                </div>
            </div>

            <div style={{ display: "flex", flexDirection: "column", width: "40%", background: "#f8fafc", overflow: "hidden" }}>
                <div style={{ padding: "12px 16px", borderBottom: "1px solid #e2e8f0", background: "#fff", display: "flex", flexDirection: "column", gap: "8px" }}>
                    <input
                        type="text"
                        placeholder="Search by name or code..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        style={{ border: "1px solid #e2e8f0", borderRadius: "8px", padding: "8px 12px", fontSize: "13px", outline: "none", width: "100%", boxSizing: "border-box" }}
                    />
                    <FilterButtons
                        selectedCategory={selectedCategory}
                        selectedBrand={selectedBrand}
                        categoryList={categoryList}
                        brands={brands}
                        setSelectedCategory={setSelectedCategory}
                        setSelectedBrand={setSelectedBrand}
                    />
                </div>
                <div style={{ flex: 1, overflowY: "auto", padding: "12px", display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "10px", alignContent: "start" }}>
                    {filteredProducts.map((product) => {
                        const inCart = entries.find((e) => e.product === product.identifier || e.productId === product.identifier);
                        const displayPrice = productPrices[product.identifier] ?? null;
                        const isInCart = Boolean(inCart);
                        const isCartAvailable = Boolean(cart);
                        const cardStyles = getCardStyles(isInCart, isCartAvailable);

                        return (
                            <button
                                key={product.identifier}
                                type="button"
                                onClick={() => handleAddProduct(product)}
                                disabled={!cart || actionLoading}
                                style={cardStyles}
                                onMouseEnter={(e) => { if (cart) e.currentTarget.style.borderColor = "#2563eb"; }}
                                onMouseLeave={(e) => { if (!inCart) e.currentTarget.style.borderColor = "#e2e8f0"; }}>
                                {inCart && (
                                    <span style={{ position: "absolute", top: "6px", right: "6px", background: "#2563eb", color: "#fff", borderRadius: "50%", width: "18px", height: "18px", fontSize: "10px", fontWeight: 700, display: "flex", alignItems: "center", justifyContent: "center" }}>
                                        {Number(inCart.quantity)}
                                    </span>
                                )}
                                <div style={{ width: "52px", height: "52px", background: "#f1f5f9", borderRadius: "8px", display: "flex", alignItems: "center", justifyContent: "center" }}>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="none" viewBox="0 0 24 24" stroke="#cbd5e1" strokeWidth="1.5">
                                        <path strokeLinecap="round" strokeLinejoin="round" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10" />
                                    </svg>
                                </div>
                                <span style={{ fontSize: "11px", fontWeight: 600, color: "#1e293b", lineHeight: 1.3, wordBreak: "break-word" }}>{product.productname}</span>
                                <span style={{ fontSize: "13px", fontWeight: 700, color: "#2563eb" }}>
                                    {displayPrice !== null && displayPrice !== undefined
                                        ? formatCurrency(displayPrice)
                                        : <span style={{ color: "#cbd5e1", fontSize: "11px" }}>No price</span>}
                                </span>
                            </button>
                        );
                    })}
                    {filteredProducts.length === 0 && (
                        <div style={{ gridColumn: "1/-1", textAlign: "center", padding: "40px", color: "#94a3b8", fontSize: "13px" }}>No products found</div>
                    )}
                </div>
            </div>

            {showSuccessPopup && (
                <OrderConfirmedPopup
                    order={completedOrder}
                    entries={completedEntries}
                    subtotal={completedSubtotal}
                    discount={completedDiscount}
                    total={completedTotal}
                    generatingReceipt={generatingReceipt}
                    onViewOrder={() => { setShowSuccessPopup(false); router.push("/order"); }}
                    onNewOrder={() => setShowSuccessPopup(false)}
                />
            )}

            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
        </div>
    );
}

CartPageRender.propTypes = {
    cart: PropTypes.shape({ identifier: PropTypes.string, totalDiscount: PropTypes.number, totalPrice: PropTypes.number, cartEntryDtoList: PropTypes.array }),
    entries: PropTypes.array.isRequired,
    brands: PropTypes.array.isRequired,
    existingCustomers: PropTypes.array.isRequired,
    paymentType: PropTypes.string.isRequired,
    searchTerm: PropTypes.string.isRequired,
    selectedCategory: PropTypes.string.isRequired,
    selectedBrand: PropTypes.string.isRequired,
    phoneSearch: PropTypes.string.isRequired,
    customerFound: PropTypes.oneOfType([PropTypes.object, PropTypes.bool]),
    bannerDismissed: PropTypes.bool.isRequired,
    showSuggestions: PropTypes.bool.isRequired,
    searchingCustomer: PropTypes.bool.isRequired,
    actionLoading: PropTypes.bool.isRequired,
    processingEntry: PropTypes.string,
    receiveAmount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    note: PropTypes.string.isRequired,
    showSuccessPopup: PropTypes.bool.isRequired,
    completedOrder: PropTypes.object,
    completedEntries: PropTypes.array.isRequired,
    completedSubtotal: PropTypes.number.isRequired,
    completedDiscount: PropTypes.number.isRequired,
    completedTotal: PropTypes.number.isRequired,
    generatingReceipt: PropTypes.bool.isRequired,
    router: PropTypes.object.isRequired,
    error: PropTypes.string.isRequired,
    today: PropTypes.string.isRequired,
    subTotal: PropTypes.number.isRequired,
    discount: PropTypes.number.isRequired,
    totalAmount: PropTypes.number.isRequired,
    receive: PropTypes.number.isRequired,
    changeAmount: PropTypes.number.isRequired,
    dueAmount: PropTypes.number.isRequired,
    categoryList: PropTypes.array.isRequired,
    searchEnabled: PropTypes.bool.isRequired,
    saleEnabled: PropTypes.bool.isRequired,
    cancelDisabled: PropTypes.bool.isRequired,
    setPhoneSearch: PropTypes.func.isRequired,
    setShowSuggestions: PropTypes.func.isRequired,
    setCustomerFound: PropTypes.func.isRequired,
    setBannerDismissed: PropTypes.func.isRequired,
    setReceiveAmount: PropTypes.func.isRequired,
    setNote: PropTypes.func.isRequired,
    setPaymentType: PropTypes.func.isRequired,
    setSearchTerm: PropTypes.func.isRequired,
    setSelectedCategory: PropTypes.func.isRequired,
    setSelectedBrand: PropTypes.func.isRequired,
    setShowSuccessPopup: PropTypes.func.isRequired,
    handlePhoneSearch: PropTypes.func.isRequired,
    handleSelectExisting: PropTypes.func.isRequired,
    handleClearCustomer: PropTypes.func.isRequired,
    handleQtyChange: PropTypes.func.isRequired,
    handleDeleteEntry: PropTypes.func.isRequired,
    handleCancel: PropTypes.func.isRequired,
    handleSale: PropTypes.func.isRequired,
    handleAddProduct: PropTypes.func.isRequired,
    filteredProducts: PropTypes.array.isRequired,
    productPrices: PropTypes.object.isRequired,
};

CartPageRender.defaultProps = {
    cart: null,
    customerFound: null,
    processingEntry: null,
    completedOrder: null,
};

export default CartPageView;