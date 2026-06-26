"use client";

import { useState, useEffect, useCallback } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axiosInstance from "../api/axiosInstance";
import { formatCurrency } from "../lib/receiptUtils";

export default function CartView() {
    const router = useRouter();
    const [phoneSearch, setPhoneSearch] = useState("");
    const [customerFound, setCustomerFound] = useState(null); 
    const [bannerDismissed, setBannerDismissed] = useState(false);
    const [searchingCustomer, setSearchingCustomer] = useState(false);
    const [cart, setCart] = useState(null);
    const [entries, setEntries] = useState([]);

    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [brands, setBrands] = useState([]);
    const [priceMap, setPriceMap] = useState({});
    const [mrpMap, setMrpMap] = useState({});
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedCategory, setSelectedCategory] = useState("");
    const [selectedBrand, setSelectedBrand] = useState("");

    const [receiveAmount, setReceiveAmount] = useState("");
    const [paymentType, setPaymentType] = useState("Cash");
    const [note, setNote] = useState("");
    const [actionLoading, setActionLoading] = useState(false);
    const [processingEntry, setProcessingEntry] = useState(null);
    const [error, setError] = useState("");
    const [orderReceipt, setOrderReceipt] = useState(null);
    const [receiptCustomerName, setReceiptCustomerName] = useState("");
    const [paymentInfo, setPaymentInfo] = useState(null);
    const [showPaymentForm, setShowPaymentForm] = useState(false);
    const [pendingSale, setPendingSale] = useState(null);
    const [receivedInput, setReceivedInput] = useState("");
    const [paymentProcessing, setPaymentProcessing] = useState(false);

    const today = new Date().toLocaleDateString("en-GB");

    const searchDisabled = searchingCustomer || phoneSearch.trim().length === 0 || customerFound !== null;
    const cartDisabled = actionLoading || cart == null;

    useEffect(() => {
        axiosInstance
            .get("/product/findAllActive")
            .then((res) => { setProducts(res.data || []); setFilteredProducts(res.data || []); })
            .catch(() => { });

        axiosInstance.get("/category/findActiveSubCategories").then((res) => setCategories(res.data || [])).catch(() => { });
        axiosInstance.get("/brand/findAllActive").then((res) => setBrands(res.data || [])).catch(() => { });

        axiosInstance.post("/price/list", {}).then((res) => {
            const list = res.data?.dtoList || [];
            const map = {};
            const mrp = {};
            list.forEach((p) => {
                if (p.identifier) {
                    map[p.identifier] = Number(p.sellingPrice) || 0;
                    mrp[p.identifier] = Number(p.mrp) || 0;
                }
            });
            setPriceMap(map);
            setMrpMap(mrp);
        }).catch(() => { });
    }, []);

    useEffect(() => {
        let list = [...products];
        if (searchTerm) {
            const s = searchTerm.toLowerCase();
            list = list.filter(
                (p) => p.productName?.toLowerCase().includes(s) || p.identifier?.toLowerCase().includes(s)
            );
        }
        if (selectedCategory) list = list.filter((p) => Array.isArray(p.category) && p.category.includes(selectedCategory));
        if (selectedBrand) list = list.filter((p) => p.brand === selectedBrand);
        setFilteredProducts(list);
    }, [searchTerm, selectedCategory, selectedBrand, products]);

    const refreshCart = useCallback(async (identifier) => {
        await axiosInstance.post("/cart/addToCart", { cart: identifier }).catch(() => { });
        const res = await axiosInstance.post("/cart/getCart", { identifier });
        const data = res.data;
        setCart(data);
        setEntries(data?.entryDtoList || []);
    }, []);

    const handlePhoneSearch = async () => {
        const phone = phoneSearch.trim();
        if (phone.length === 0) return;
        setSearchingCustomer(true);
        setCustomerFound(null);
        setBannerDismissed(false);
        setError("");
        setCart(null);
        setEntries([]);
        try {
            const res = await axiosInstance.get("/customer/update", { params: { identifier: phone } });
            const customer = res.data;
            setCustomerFound(customer);
            await handleCustomerSelect(customer.identifier || customer.phoneNum || phone);
        } catch {
            setCustomerFound(false);
        } finally {
            setSearchingCustomer(false);
        }
    };

    const handleClearCustomer = () => {
        setCustomerFound(null);
        setBannerDismissed(false);
        setPhoneSearch("");
        setCart(null);
        setEntries([]);
        setError("");
    };

    const handleCustomerSelect = async (value) => {
        setCart(null);
        setEntries([]);
        setError("");
        if (value == null || value === "") return;
        try {
            await refreshCart(value);
        } catch {
            try {
                await axiosInstance.post("/cart/add", { identifier: value, status: true });
                await refreshCart(value);
            } catch {
                setError("Failed to create cart for this customer.");
            }
        }
    };

    const searchParams = useSearchParams();
    useEffect(() => {
        const phoneFromQuery = searchParams?.get?.("phone") || "";
        if (phoneFromQuery) {
            setPhoneSearch(phoneFromQuery);
            handleCustomerSelect(phoneFromQuery);
        }
    }, [searchParams]);

    const handleAddProduct = async (product) => {
        if (cart == null) { setError("Select a customer first."); return; }
        if (actionLoading || processingEntry) return;
        setActionLoading(true);
        setError("");
        try {
            await axiosInstance.post("/cartentry/addEntry", {
                cart: cart.identifier,
                product: product.identifier,
                quantity: 1,
            });
            await refreshCart(cart.identifier);
        } catch {
            setError("Failed to add product.");
        } finally {
            setActionLoading(false);
        }
    };

    const handleQtyChange = async (entry, delta) => {
        if (processingEntry === entry.product) return;
        const newQty = Number(entry.quantity) + delta;
        setProcessingEntry(entry.product);
        setError("");
        const cartId = entry.cart || cart?.identifier;
        try {
            if (newQty <= 0) {
                await axiosInstance.post("/cart/deleteEntry", { product: entry.product, cart: cartId });
            } else {
                await axiosInstance.post("/cartentry/addEntry", {
                    cart: cartId,
                    product: entry.product,
                    quantity: delta,
                });
            }
            await refreshCart(cart.identifier);
        } catch {
            setError("Failed to update quantity.");
        } finally {
            setProcessingEntry(null);
        }
    };

    const handleDeleteEntry = async (entry) => {
        setActionLoading(true);
        setError("");
        const cartId = entry.cart || cart?.identifier;
        try {
            await axiosInstance.delete("/cart/deleteEntry", { product: entry.product, cart: cartId });
            await refreshCart(cart.identifier);
        } catch {
            setError("Failed to remove item.");
        } finally {
            setActionLoading(false);
        }
    };

    const handleCancel = async () => {
        if (cart == null) return;
        setActionLoading(true);
        try {
            await axiosInstance.delete("/cart/deleteCart", {
                data: {
                    identifier: cart.identifier
                }
            });
            setCart(null);
            setEntries([]);
            setPhoneSearch("");
            setCustomerFound(null);
            setReceiveAmount("");
            setNote("");
        } catch {
            setError("Failed to cancel cart.");
        } finally {
            setActionLoading(false);
        }
    };

    const handleSale = async () => {
        if (cart == null) {
            setError("Select a customer and build a cart first.");
            return;
        }
        if (entries.length === 0) {
            setError("Add at least one product before selling.");
            return;
        }
        setError("");
        setOrderReceipt(null);
        const createdSubtotal = entries.reduce((sum, e) => sum + (Number(e.totalPrice) || 0), 0) || 0;
        const totalDiscount = Number(cart?.totalDiscount) || 0;
        const orderTotal = Math.max(0, createdSubtotal - totalDiscount);
        setReceivedInput(orderTotal ? orderTotal.toFixed(2) : "");
        setPendingSale({ entries: [...entries], subtotal: createdSubtotal, totalDiscount, orderTotal, identifier: cart.identifier });
        setShowPaymentForm(true);
    };

    const confirmPayment = () => {
        const received = Number(receivedInput) || 0;

        if (orderReceipt) {
            const orderTotal = Math.max(0, (orderReceipt.orderEntryDtoList || []).reduce((s, e) => s + (Number(e.totalPrice) || 0), 0) - (Number(orderReceipt.totalDiscount) || 0));
            if (received < orderTotal) return;
            setPaymentProcessing(true);
            setTimeout(() => {
                setPaymentInfo({ paid: true, receivedAmount: received, changeAmount: received - orderTotal });
                setPaymentProcessing(false);
                setShowPaymentForm(false);
            }, 700);
            return;
        }

        if (pendingSale == null) return;
        const orderTotal = pendingSale.orderTotal || pendingSale.total || 0;
        if (received < orderTotal) return;

        (async () => {
            setPaymentProcessing(true);
            try {
                const res = await axiosInstance.post("/orders/create", {
                    identifier: pendingSale.identifier,
                    paymentMode: paymentType,
                    note,
                });
                const createdOrder = res.data;
                if (createdOrder?.success === false) {
                    setError(createdOrder.message || "Failed to place order.");
                    setPaymentProcessing(false);
                    return;
                }
                try {
                    await axiosInstance.delete("/cart/deleteCart", { data: { identifier: pendingSale.identifier } });
                } catch (e) {
                    console.warn("Failed to delete cart after order creation:", e);
                }

                setOrderReceipt(createdOrder);
                setReceiptCustomerName(customerFound?.username || "");
                setPaymentInfo({ paid: true, receivedAmount: received, changeAmount: received - orderTotal });
                setShowPaymentForm(false);

             
                setCart(null);
                setEntries([]);
                setPhoneSearch("");
                setCustomerFound(null);
                setBannerDismissed(false);
                setReceiveAmount("");
                setNote("");
                setPaymentType("Cash");
                setPendingSale(null);
            } catch (e) {
                console.error("Failed to create order", e);
                setError("Failed to create order." + (e?.message ? ` ${e.message}` : ""));
            } finally {
                setPaymentProcessing(false);
            }
        })();
    };

    const startNewSale = () => {
        setOrderReceipt(null);
        setReceiptCustomerName("");
        setPaymentInfo(null);
        setShowPaymentForm(false);
        setReceivedInput("");
        setPendingSale(null);
    };

    const subTotal = entries.reduce((sum, e) => sum + (Number(e.totalPrice) || 0), 0);
    const mrpSubtotal = entries.reduce((sum, e) => sum + ((mrpMap[e.product] ?? 0) * (Number(e.quantity) || 0)), 0);
    const receive = Number(receiveAmount) || 0;
    const changeAmount = Math.max(0, receive - subTotal);
    const dueAmount = Math.max(0, subTotal - receive);
    const totalAmount = cart?.totalPrice == null ? subTotal : Number(cart.totalPrice);

    const categoryList = categories.map((c) => (typeof c === "string" ? c : c.identifier));
    const renderReceipt = () => {
        const receiptEntries = orderReceipt.orderEntryDtoList || [];
        const receiptSubtotal = receiptEntries.reduce((sum, e) => sum + (Number(e.totalPrice) || 0), 0);
        const receiptDiscount = Number(orderReceipt?.totalDiscount) || 0;
        const receiptTotal = Math.max(0, receiptSubtotal - receiptDiscount);

        return (
            <div style={{ minHeight: "calc(100vh - 64px)", background: "#f8fafc", padding: "28px 32px" }}>
                <div style={{ maxWidth: "1200px", margin: "0 auto" }}>
                    <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", gap: "12px", marginBottom: "20px" }}>
                        <div>
                            <p style={{ fontSize: "11px", fontWeight: 700, letterSpacing: "0.15em", textTransform: "uppercase", color: "#2563eb", margin: 0 }}>Sale Completed</p>
                            <h2 style={{ fontSize: "26px", fontWeight: 800, margin: "6px 0 0", color: "#0f172a" }}>Order Receipt</h2>
                        </div>
                        <span style={{ borderRadius: "999px", padding: "6px 14px", fontSize: "11px", fontWeight: 700, textTransform: "uppercase", letterSpacing: "0.05em", background: paymentInfo?.paid ? "#dcfce7" : "#fee2e2", color: paymentInfo?.paid ? "#15803d" : "#b91c1c" }}>
                            {paymentInfo?.paid ? "Paid" : "Unpaid"}
                        </span>
                    </div>

                    <div style={{ display: "grid", gridTemplateColumns: "1.4fr 1fr", gap: "20px", alignItems: "start" }}>
                        <div style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
                            <div style={{ background: "#fff", borderRadius: "16px", border: "1px solid #e2e8f0", overflow: "hidden" }}>
                                <div style={{ padding: "14px 18px", borderBottom: "1px solid #e2e8f0", fontWeight: 700, fontSize: "14px", color: "#1e293b" }}>Items</div>
                                <table style={{ width: "100%", fontSize: "13px", borderCollapse: "collapse" }}>
                                    <thead>
                                        <tr style={{ background: "#f8fafc", color: "#64748b" }}>
                                            <th style={{ textAlign: "left", padding: "10px 18px" }}>Product</th>
                                            <th style={{ textAlign: "center", padding: "10px 18px" }}>Qty</th>
                                            <th style={{ textAlign: "right", padding: "10px 18px" }}>Price</th>
                                            <th style={{ textAlign: "right", padding: "10px 18px" }}>Total</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {receiptEntries.length === 0 ? (
                                            <tr><td colSpan={4} style={{ padding: "20px", textAlign: "center", color: "#94a3b8" }}>No items</td></tr>
                                        ) : (
                                            receiptEntries.map((entry, i) => (
                                                <tr key={entry.identifier || i} style={{ borderTop: "1px solid #f1f5f9" }}>
                                                    <td style={{ padding: "10px 18px", fontWeight: 600, color: "#1e293b" }}>{entry.product}</td>
                                                    <td style={{ padding: "10px 18px", textAlign: "center", color: "#475569" }}>{Number(entry.quantity) || 0}</td>
                                                    <td style={{ padding: "10px 18px", textAlign: "right", color: "#475569" }}>{formatCurrency(entry.sellingPrice ?? entry.price)}</td>
                                                    <td style={{ padding: "10px 18px", textAlign: "right", fontWeight: 700, color: "#2563eb" }}>{formatCurrency(entry.totalPrice)}</td>
                                                </tr>
                                            ))
                                        )}
                                    </tbody>
                                </table>
                            </div>

                            <div style={{ background: "#fff", borderRadius: "16px", border: "1px solid #e2e8f0", padding: "16px 18px", fontSize: "14px" }}>
                                <div style={{ display: "flex", justifyContent: "space-between" }}>
                                    <span style={{ color: "#64748b" }}>Subtotal</span>
                                    <span style={{ fontWeight: 600, color: "#1e293b" }}>{formatCurrency(receiptSubtotal)}</span>
                                </div>
                                <div style={{ display: "flex", justifyContent: "space-between", marginTop: "8px" }}>
                                    <span style={{ color: "#64748b" }}>Discount</span>
                                    <span style={{ fontWeight: 600, color: "#16a34a" }}>-{formatCurrency(receiptDiscount)}</span>
                                </div>
                                <div style={{ display: "flex", justifyContent: "space-between", marginTop: "10px", paddingTop: "10px", borderTop: "1px solid #e2e8f0" }}>
                                    <span style={{ fontWeight: 700, color: "#1e293b" }}>Total</span>
                                    <span style={{ fontWeight: 800, fontSize: "18px", color: "#2563eb" }}>{formatCurrency(receiptTotal)}</span>
                                </div>
                                {paymentInfo?.paid && (
                                    <>
                                        <div style={{ display: "flex", justifyContent: "space-between", marginTop: "10px" }}>
                                            <span style={{ color: "#64748b" }}>Received</span>
                                            <span style={{ fontWeight: 600, color: "#1e293b" }}>{formatCurrency(paymentInfo.receivedAmount)}</span>
                                        </div>
                                        <div style={{ display: "flex", justifyContent: "space-between", marginTop: "4px" }}>
                                            <span style={{ color: "#64748b" }}>Change</span>
                                            <span style={{ fontWeight: 600, color: "#16a34a" }}>{formatCurrency(paymentInfo.changeAmount)}</span>
                                        </div>
                                    </>
                                )}
                            </div>
                        </div>

                        <div style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
                            <div style={{ background: "#0f172a", color: "#fff", borderRadius: "16px", padding: "18px" }}>
                                <div style={{ marginBottom: "12px" }}>
                                    <p style={{ fontSize: "10px", color: "#94a3b8", margin: 0, textTransform: "uppercase", letterSpacing: "0.1em" }}>Order ID</p>
                                    <p style={{ fontSize: "14px", fontWeight: 700, margin: "4px 0 0", wordBreak: "break-all" }}>{orderReceipt.orderId || orderReceipt.identifier || "-"}</p>
                                </div>
                                <div style={{ fontSize: "12px", color: "#cbd5e1" }}>
                                    <div style={{ marginTop: 6 }}>Customer <strong style={{ color: "#fff" }}>{receiptCustomerName ? `${receiptCustomerName} (${orderReceipt.identifier || "-"})` : orderReceipt.identifier || "Walk-in"}</strong></div>
                                    <div style={{ marginTop: 8 }}>Date <strong style={{ color: "#fff" }}>{new Date(orderReceipt.createdAt || Date.now()).toLocaleString()}</strong></div>
                                    <div style={{ marginTop: 8 }}>Payment Mode <strong style={{ color: "#fff" }}>{orderReceipt.paymentMode || "-"}</strong></div>
                                </div>
                            </div>

                            {showPaymentForm && paymentInfo?.paid === false ? (
                                <div style={{ background: "#fff7ed", border: "1px solid #fde68a", borderRadius: "12px", padding: "14px" }}>
                                    <h4 style={{ margin: 0, marginBottom: 8 }}>Process Payment</h4>
                                    <div style={{ marginBottom: 8 }}>
                                        <label htmlFor="receivedAmount" style={{ display: "block", fontSize: 12, color: "#8c6d00", marginBottom: 6 }}>Received Amount</label>
                                        <input id="receivedAmount" value={receivedInput} onChange={(e) => setReceivedInput(e.target.value)} placeholder={String(receiptTotal.toFixed(2))} style={{ width: "100%", padding: "8px 10px", borderRadius: 8, border: "1px solid #fde68a" }} />
                                    </div>
                                    <div style={{ display: "flex", gap: 8 }}>
                                        <button onClick={confirmPayment} disabled={paymentProcessing} style={{ background: "#f59e0b", color: "#fff", padding: "10px 12px", borderRadius: 8, border: "none", cursor: "pointer" }}>{paymentProcessing ? "Processing..." : "Confirm Payment"}</button>
                                        <button onClick={startNewSale} style={{ background: "#fff", border: "1px solid #fddba1", padding: "10px 12px", borderRadius: 8, cursor: "pointer" }}>Cancel</button>
                                    </div>
                                </div>
                            ) : (
                                paymentInfo?.paid && (
                                    <div style={{ background: "#ecfdf5", border: "1px solid #bbf7d0", borderRadius: "12px", padding: "12px" }}>
                                        <div style={{ fontWeight: 700, color: "#065f46" }}>Payment received</div>
                                        <div style={{ marginTop: 6 }}>Received: {formatCurrency(paymentInfo.receivedAmount)}</div>
                                        <div>Change: {formatCurrency(paymentInfo.changeAmount)}</div>
                                    </div>
                                )
                            )}
                        </div>
                    </div>
                </div>
            </div>
        );
    };

    if (orderReceipt) return renderReceipt();

    const renderHeader = () => (
        <div style={{ padding: "12px 16px", borderBottom: "1px solid #e2e8f0", display: "flex", flexDirection: "column", gap: "8px" }}>

            {error && (
                <div style={{ background: "#fef2f2", border: "1px solid #fecaca", color: "#dc2626", borderRadius: "8px", padding: "8px 12px", fontSize: "12px" }}>
                    {error}
                </div>
            )}

            {orderReceipt && (
                <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: "12px", background: "#f0fdf4", border: "1px solid #bbf7d0", color: "#166534", borderRadius: "8px", padding: "8px 12px", fontSize: "12px" }}>
                    <div>
                        <strong>Sale completed.</strong> Order {orderReceipt.orderId || orderReceipt.identifier} was created successfully.
                    </div>
                    <button
                        onClick={() => router.push("/order")}
                        style={{ padding: "6px 10px", borderRadius: "6px", border: "none", background: "#16a34a", color: "#fff", fontSize: "12px", fontWeight: 700, cursor: "pointer", whiteSpace: "nowrap" }}
                    >
                        View Orders
                    </button>
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

            <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
                <div style={{ display: "flex", gap: "8px" }}>
                    <input
                        type="tel"
                        placeholder="Search customer by phone number..."
                        value={phoneSearch}
                        onChange={(e) => { setPhoneSearch(e.target.value); if (customerFound !== null) { setCustomerFound(null); setBannerDismissed(false); } }}
                        onKeyDown={(e) => e.key === "Enter" && handlePhoneSearch()}
                        disabled={customerFound !== null}
                        style={{ flex: 1, border: "1px solid #cbd5e1", borderRadius: "8px", padding: "8px 12px", fontSize: "13px", outline: "none", background: customerFound ? "#f8fafc" : "#fff", color: "#1e293b" }}
                    />
                    <button
                        onClick={handlePhoneSearch}
                        disabled={searchDisabled}
                        style={{ padding: "8px 16px", borderRadius: "8px", background: "#2563eb", color: "#fff", border: "none", fontWeight: 600, fontSize: "13px", cursor: searchDisabled ? "not-allowed" : "pointer", opacity: searchDisabled ? 0.5 : 1, whiteSpace: "nowrap" }}
                    >
                        {searchingCustomer ? "..." : "Search"}
                    </button>
                </div>

                {customerFound && bannerDismissed === false && (
                    <div style={{ display: "flex", alignItems: "center", gap: "8px", padding: "8px 12px", background: "#f0fdf4", border: "1px solid #bbf7d0", borderRadius: "8px" }}>
                        <span style={{ fontSize: "14px", color: "#16a34a", fontWeight: 700 }}>✓</span>
                        <div style={{ flex: 1, fontSize: "13px", color: "#166534" }}>
                            <strong>{customerFound.username || customerFound.identifier}</strong>
                            {customerFound.username && <span style={{ marginLeft: "8px", color: "#4ade80" }}>— {customerFound.identifier}</span>}
                        </div>
                        <button
                            onClick={() => setBannerDismissed(true)}
                            style={{ background: "none", cursor: "pointer", color: "#94a3b8", fontSize: "16px", lineHeight: 1, padding: "2px 4px", borderRadius: "4px" }}
                            onMouseEnter={(e) => (e.currentTarget.style.color = "#6b7280")}
                            onMouseLeave={(e) => (e.currentTarget.style.color = "#94a3b8")}
                        >✕</button>
                    </div>
                )}

                {customerFound && bannerDismissed && (
                    <div style={{ display: "flex", alignItems: "center", gap: "8px", padding: "6px 12px", background: "#f8fafc", border: "1px solid #e2e8f0", borderRadius: "8px" }}>
                        <span style={{ fontSize: "12px", color: "#16a34a", fontWeight: 700 }}>✓</span>
                        <span style={{ flex: 1, fontSize: "12px", color: "#475569" }}>
                            <strong>{customerFound.identifier}</strong>
                            {customerFound.username && <span style={{ marginLeft: "6px", color: "#94a3b8" }}>({customerFound.username})</span>}
                        </span>
                        <button
                            onClick={handleClearCustomer}
                            style={{ background: "none", cursor: "pointer", color: "#94a3b8", fontSize: "11px", fontWeight: 600, padding: "2px 6px", borderRadius: "4px", border: "1px solid #e2e8f0" }}
                            onMouseEnter={(e) => (e.currentTarget.style.color = "#ef4444")}
                            onMouseLeave={(e) => (e.currentTarget.style.color = "#94a3b8")}
                        >Change</button>
                    </div>
                )}

                {customerFound === false && (
                    <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "8px 12px", background: "#fef2f2", border: "1px solid #fecaca", borderRadius: "8px" }}>
                        <span style={{ fontSize: "13px", color: "#dc2626" }}>No customer found for &ldquo;{phoneSearch}&rdquo;</span>
                        <button
                            onClick={() => router.push(`/customer/add?phone=${encodeURIComponent(phoneSearch.trim())}`)}
                            style={{ padding: "6px 14px", background: "#2563eb", color: "#fff", border: "none", borderRadius: "6px", fontSize: "12px", fontWeight: 600, cursor: "pointer", whiteSpace: "nowrap" }}
                        >
                            + Add Customer
                        </button>
                    </div>
                )}

            </div>
        </div>
    );

    const renderEntriesTable = () => (
        <div style={{ flex: 1, overflowY: "auto" }}>
            <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "13px" }}>
                <thead>
                    <tr style={{ background: "#f1f5f9", position: "sticky", top: 0, zIndex: 1 }}>
                        {["Items", "Code", "Unit", "Sale Price", "Qty", "Sub Total", ""].map((h) => (
                            <th key={h} style={{ padding: "10px 12px", textAlign: "left", fontWeight: 600, color: "#475569", whiteSpace: "nowrap", borderBottom: "1px solid #e2e8f0" }}>
                                {h}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {entries.length === 0 ? (
                        <tr>
                            <td colSpan={7} style={{ padding: "40px", textAlign: "center", color: "#94a3b8", fontSize: "13px" }}>
                                {cart ? "No items yet — click a product on the right to add." : "Select a customer to start a cart."}
                            </td>
                        </tr>
                    ) : (
                        entries.map((entry, i) => (
                            <tr key={entry.identifier || i} style={{ borderBottom: "1px solid #f1f5f9" }}
                                onMouseEnter={(e) => (e.currentTarget.style.background = "#fafafa")}
                                onMouseLeave={(e) => (e.currentTarget.style.background = "transparent")}
                            >
                                <td style={{ padding: "10px 12px", color: "#1e293b", fontWeight: 500 }}>{entry.product}</td>
                                <td style={{ padding: "10px 12px", color: "#64748b", fontSize: "12px" }}>{entry.identifier}</td>
                                <td style={{ padding: "10px 12px", color: "#64748b" }}>—</td>
                                <td style={{ padding: "10px 12px", color: "#1e293b" }}>
                                    ${Number(entry.sellingPrice ?? entry.price ?? 0).toFixed(2)}
                                </td>
                                <td style={{ padding: "6px 12px" }}>
                                    <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                                        <button
                                            onClick={() => handleQtyChange(entry, -1)}
                                            disabled={processingEntry === entry.product}
                                            style={{ width: "26px", height: "26px", border: "1px solid #fecaca", borderRadius: "6px", background: "#fef2f2", cursor: processingEntry === entry.product ? "not-allowed" : "pointer", fontWeight: 700, color: "#ef4444", fontSize: "16px", lineHeight: 1, display: "flex", alignItems: "center", justifyContent: "center", opacity: processingEntry === entry.product ? 0.5 : 1 }}
                                        >−</button>
                                        <span style={{ minWidth: "24px", textAlign: "center", fontWeight: 700, fontSize: "14px" }}>
                                            {processingEntry === entry.product ? "…" : Number(entry.quantity)}
                                        </span>
                                        <button
                                            onClick={() => handleQtyChange(entry, 1)}
                                            disabled={processingEntry === entry.product}
                                            style={{ width: "26px", height: "26px", border: "1px solid #bbf7d0", borderRadius: "6px", background: "#f0fdf4", cursor: processingEntry === entry.product ? "not-allowed" : "pointer", fontWeight: 700, color: "#16a34a", fontSize: "16px", lineHeight: 1, display: "flex", alignItems: "center", justifyContent: "center", opacity: processingEntry === entry.product ? 0.5 : 1 }}
                                        >+</button>
                                    </div>
                                </td>
                                <td style={{ padding: "10px 12px", fontWeight: 700, color: "#2563eb" }}>
                                    ${Number(entry.totalPrice ?? 0).toFixed(2)}
                                </td>
                                <td style={{ padding: "10px 12px" }}>
                                    <button
                                        onClick={() => handleDeleteEntry(entry)}
                                        disabled={actionLoading}
                                        style={{ background: "none", border: "none", cursor: "pointer", color: "#94a3b8", fontSize: "18px", lineHeight: 1, padding: "2px 4px", borderRadius: "4px" }}
                                        onMouseEnter={(e) => (e.currentTarget.style.color = "#ef4444")}
                                        onMouseLeave={(e) => (e.currentTarget.style.color = "#94a3b8")}
                                    >✕</button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );

    const renderPaymentTotals = () => (
        <div style={{ borderTop: "2px solid #e2e8f0", padding: "12px 16px", display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>

            <div style={{ display: "flex", flexDirection: "column", gap: "7px" }}>
                {[
                    { label: "Receive Amount", value: receiveAmount, onChange: (v) => setReceiveAmount(v), editable: true, type: "number" },
                    { label: "Change Amount", value: changeAmount.toFixed(2), editable: false },
                    { label: "Due Amount", value: dueAmount.toFixed(2), editable: false },
                ].map(({ label, value, onChange, editable, type }) => (
                    <div key={label} style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                        <label style={{ width: "108px", fontSize: "11px", color: "#64748b", fontWeight: 600, flexShrink: 0 }}>{label}</label>
                        <input
                            type={type || "text"}
                            value={value}
                            onChange={editable ? (e) => onChange(e.target.value) : undefined}
                            readOnly={editable !== true}
                            placeholder="0"
                            style={{ flex: 1, border: "1px solid #e2e8f0", borderRadius: "6px", padding: "6px 10px", fontSize: "13px", background: editable ? "#fff" : "#f8fafc", color: editable ? "#1e293b" : "#64748b", outline: "none" }}
                        />
                    </div>
                ))}

                <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <label htmlFor="paymentType" style={{ width: "108px", fontSize: "11px", color: "#64748b", fontWeight: 600, flexShrink: 0 }}>Payment Type</label>
                    <select
                        id="paymentType"
                        value={paymentType}
                        onChange={(e) => setPaymentType(e.target.value)}
                        style={{ flex: 1, border: "1px solid #e2e8f0", borderRadius: "6px", padding: "6px 10px", fontSize: "13px", outline: "none" }}
                    >
                        <option>Cash</option>
                        <option>Card</option>
                        <option>UPI</option>
                    </select>
                </div>

                <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <label htmlFor="note" style={{ width: "108px", fontSize: "11px", color: "#64748b", fontWeight: 600, flexShrink: 0 }}>Note</label>
                    <input
                        id="note"
                        value={note}
                        onChange={(e) => setNote(e.target.value)}
                        placeholder="Type note..."
                        style={{ flex: 1, border: "1px solid #e2e8f0", borderRadius: "6px", padding: "6px 10px", fontSize: "13px", outline: "none" }}
                    />
                </div>
            </div>

            <div style={{ display: "flex", flexDirection: "column", justifyContent: "space-between" }}>
                <div style={{ display: "flex", flexDirection: "column", gap: "8px", fontSize: "13px" }}>
                    <div style={{ display: "flex", justifyContent: "space-between", color: "#64748b" }}>
                        <span>Sub Total</span>
                        <span style={{ fontWeight: 600, color: "#1e293b" }}>${subTotal.toFixed(2)}</span>
                    </div>
                    <div style={{ display: "flex", justifyContent: "space-between", color: "#64748b", fontSize: "12px", marginTop: "4px" }}>
                        <span>MRP Subtotal</span>
                        <span style={{ fontWeight: 600, color: "#475569" }}>{formatCurrency(mrpSubtotal)}</span>
                    </div>
                    <div style={{ display: "flex", justifyContent: "space-between", color: "#64748b" }}>
                        <span>Total Discount</span>
                        <span style={{ fontWeight: 600, color: "#16a34a" }}>-${Number(cart?.totalDiscount ?? 0).toFixed(2)}</span>
                    </div>
                    <div style={{ height: "1px", background: "#e2e8f0" }} />
                    <div style={{ display: "flex", justifyContent: "space-between" }}>
                        <span style={{ fontWeight: 700, fontSize: "15px", color: "#1e293b" }}>Total Amount</span>
                        <span style={{ fontWeight: 700, fontSize: "15px", color: "#2563eb" }}>${totalAmount.toFixed(2)}</span>
                    </div>
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginTop: "12px" }}>
                    {showPaymentForm && !orderReceipt && pendingSale ? (
                        <div style={{ gridColumn: "1/-1", marginBottom: 10 }}>
                            <div style={{ background: "#fff7ed", border: "1px solid #fde68a", borderRadius: "12px", padding: "14px" }}>
                                <h4 style={{ margin: 0, marginBottom: 8 }}>Process Payment</h4>
                                <div style={{ marginBottom: 8 }}>
                                    <label htmlFor="receivedAmountInline" style={{ display: "block", fontSize: 12, color: "#8c6d00", marginBottom: 6 }}>Received Amount</label>
                                    <input id="receivedAmountInline" value={receivedInput} onChange={(e) => setReceivedInput(e.target.value)} placeholder={String((pendingSale.orderTotal || 0).toFixed(2))} style={{ width: "100%", padding: "8px 10px", borderRadius: 8, border: "1px solid #fde68a" }} />
                                </div>
                                <div style={{ display: "flex", gap: 8 }}>
                                    <button onClick={confirmPayment} disabled={paymentProcessing} style={{ background: "#f59e0b", color: "#fff", padding: "10px 12px", borderRadius: 8, border: "none", cursor: "pointer" }}>{paymentProcessing ? "Processing..." : "Confirm Payment"}</button>
                                    <button onClick={startNewSale} style={{ background: "#fff", border: "1px solid #fddba1", padding: "10px 12px", borderRadius: 8, cursor: "pointer" }}>Cancel</button>
                                </div>
                            </div>
                        </div>
                    ) : null}
                    <button
                        onClick={handleCancel}
                        disabled={cartDisabled}
                        style={{ padding: "11px", borderRadius: "10px", border: "none", background: "#f59e0b", color: "#fff", fontWeight: 700, fontSize: "13px", cursor: cart == null ? "not-allowed" : "pointer", opacity: cart == null || actionLoading ? 0.55 : 1, transition: "opacity 0.15s" }}
                    >
                        Clear Cart
                    </button>
                    <button
                        onClick={handleSale}
                        disabled={entries.length === 0 || actionLoading}
                        style={{ padding: "11px", borderRadius: "10px", border: "none", background: "#dc2626", color: "#fff", fontWeight: 700, fontSize: "13px", cursor: entries.length === 0 || actionLoading ? "not-allowed" : "pointer", opacity: entries.length === 0 || actionLoading ? 0.55 : 1, transition: "opacity 0.15s" }}
                    >
                        {actionLoading ? "Processing..." : "Sale"}
                    </button>
                </div>
            </div>
        </div>
    );

    const renderRightPanel = () => (
        <>
            <div style={{ padding: "12px 16px", borderBottom: "1px solid #e2e8f0", background: "#fff", display: "flex", flexDirection: "column", gap: "8px" }}>
                <input
                    type="text"
                    placeholder="Search by name or code..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    style={{ border: "1px solid #e2e8f0", borderRadius: "8px", padding: "8px 12px", fontSize: "13px", outline: "none", width: "100%", boxSizing: "border-box" }}
                />
                <div style={{ display: "flex", gap: "6px", flexWrap: "wrap" }}>
                    <button
                        onClick={() => { setSelectedCategory(""); setSelectedBrand(""); }}
                        style={{ padding: "4px 12px", borderRadius: "20px", border: "1px solid #e2e8f0", background: selectedCategory === "" && selectedBrand === "" ? "#2563eb" : "#fff", color: selectedCategory === "" && selectedBrand === "" ? "#fff" : "#475569", fontSize: "11px", cursor: "pointer", fontWeight: 600 }}
                    >All</button>
                    {categoryList.slice(0, 4).map((cat) => (
                        <button
                            key={cat}
                            onClick={() => setSelectedCategory(selectedCategory === cat ? "" : cat)}
                            style={{ padding: "4px 12px", borderRadius: "20px", border: "1px solid #e2e8f0", background: selectedCategory === cat ? "#2563eb" : "#fff", color: selectedCategory === cat ? "#fff" : "#475569", fontSize: "11px", cursor: "pointer", fontWeight: 600 }}
                        >{cat}</button>
                    ))}
                    {brands.slice(0, 3).map((b) => (
                        <button
                            key={b.identifier}
                            onClick={() => setSelectedBrand(selectedBrand === b.identifier ? "" : b.identifier)}
                            style={{ padding: "4px 12px", borderRadius: "20px", border: "1px solid #e2e8f0", background: selectedBrand === b.identifier ? "#7c3aed" : "#fff", color: selectedBrand === b.identifier ? "#fff" : "#475569", fontSize: "11px", cursor: "pointer", fontWeight: 600 }}
                        >{b.identifier}</button>
                    ))}
                </div>
            </div>

            <div style={{ flex: 1, overflowY: "auto", padding: "12px", display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "10px", alignContent: "start" }}>
                {filteredProducts.map((product) => {
                    const inCart = entries.find((e) => e.product === product.identifier);
                    return (
                        <button
                            key={product.identifier}
                            onClick={() => handleAddProduct(product)}
                            disabled={cartDisabled}
                            style={{
                                background: inCart ? "#eff6ff" : "#fff",
                                border: `2px solid ${inCart ? "#2563eb" : "#e2e8f0"}`,
                                borderRadius: "12px",
                                padding: "12px 8px",
                                cursor: cart == null ? "not-allowed" : "pointer",
                                textAlign: "center",
                                display: "flex",
                                flexDirection: "column",
                                alignItems: "center",
                                gap: "6px",
                                opacity: cart == null ? 0.6 : 1,
                                position: "relative",
                                transition: "border-color 0.15s, background 0.15s",
                            }}
                            onMouseEnter={(e) => { if (cart) e.currentTarget.style.borderColor = "#2563eb"; }}
                            onMouseLeave={(e) => { if (inCart == null) e.currentTarget.style.borderColor = "#e2e8f0"; }}
                        >
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
                            <span style={{ fontSize: "11px", fontWeight: 600, color: "#1e293b", lineHeight: 1.3, wordBreak: "break-word", maxWidth: "100%" }}>{product.productName}</span>
                            <span style={{ fontSize: "10px", color: "#94a3b8" }}>{product.identifier}</span>
                            <span style={{ fontSize: "11px", fontWeight: 700, color: "#2563eb" }}>${(priceMap[product.identifier] ?? 0).toFixed(2)}</span>
                        </button>
                    );
                })}
                {filteredProducts.length === 0 && (
                    <div style={{ gridColumn: "1/-1", textAlign: "center", padding: "40px", color: "#94a3b8", fontSize: "13px" }}>
                        No products found
                    </div>
                )}
            </div>
        </>
    );

    return (
        <div style={{ display: "flex", height: "calc(100vh - 64px)", overflow: "hidden", background: "#f8fafc" }}>

            <div style={{ display: "flex", flexDirection: "column", width: "60%", background: "#fff", borderRight: "1px solid #e2e8f0", overflow: "hidden" }}>
                {renderHeader()}
                {renderEntriesTable()}
                {renderPaymentTotals()}
            </div>

            <div style={{ display: "flex", flexDirection: "column", width: "40%", background: "#f8fafc", overflow: "hidden" }}>
                {renderRightPanel()}
            </div>

        </div>
    );

}