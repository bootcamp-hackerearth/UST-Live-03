"use client";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import api from "@/api/axios";
import { useSidebarOpen } from "@/components/ListingShared";

const C = {
    navy: "#363955",
    mid: "#54668E",
    light: "#879EC6",
    text: "#1e2235",
    muted: "#6b7280",
    red: "#dc2626",
    green: "#166534",
    greenBg: "#f0fdf4",
};

const inputSt = {
    width: "100%",
    height: "38px",
    padding: "0 10px",
    border: "1.5px solid #dcdfe6",
    borderRadius: "7px",
    fontSize: "13px",
    outline: "none",
    boxSizing: "border-box",
};

const PAGE_SIZE = 10;

export default function OrdersPage() {
    const router = useRouter();
    const isSidebarOpen = useSidebarOpen();

    const [orders, setOrders] = useState([]);
    const [ordersLoading, setOrdersLoading] = useState(false);
    const [ordersError, setOrdersError] = useState("");
    const [orderSearch, setOrderSearch] = useState("");
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [showOrderDetail, setShowOrderDetail] = useState(false);
    const [products, setProducts] = useState([]);

    const [page, setPage] = useState(0); 
    const [totalPages, setTotalPages] = useState(1);
    const [totalRecords, setTotalRecords] = useState(0);

    const today = new Date().toLocaleDateString("en-GB");

    const fetchOrders = async (pageToLoad = 0, searchVal = "") => {
        setOrdersLoading(true);
        setOrdersError("");
        try {
            const token = globalThis.window ? localStorage.getItem("token") : null;
            const config = token ? { headers: { "Authorization": token.startsWith("Bearer ") ? token : `Bearer ${token}` } } : {};

            if (searchVal.trim()) {
                const res = await api.post("/order/list", {
                    page: 0,
                    sizePerPage: 1000,
                    sortDirection: "DESC",
                    sortField: "orderDate",
                }, config);
                const all = Array.isArray(res.data?.dtoList) ? res.data.dtoList : [];
                const lower = searchVal.toLowerCase();
                const filtered = all.filter(o =>
                    (o.orderId || "").toLowerCase().includes(lower) ||
                    (o.identifier || "").toLowerCase().includes(lower) ||
                    (o.paymentMode || "").toLowerCase().includes(lower)
                );
                setOrders(filtered);
                setTotalPages(1);
                setTotalRecords(filtered.length);
                setPage(0);
            } else {
                const res = await api.post("/order/list", {
                    page: pageToLoad,
                    sizePerPage: PAGE_SIZE,
                    sortDirection: "DESC",
                    sortField: "orderDate",
                }, config);
                const data = res.data;
                setOrders(Array.isArray(data?.dtoList) ? data.dtoList : []);
                setTotalPages(data?.totalPages ?? 1);
                setTotalRecords(data?.totalRecords ?? 0);
                setPage(data?.page ?? pageToLoad);
            }
        } catch (err) {
            console.error(err);
            setOrdersError("Failed to pull database parameters. Verify authentication keys.");
        } finally {
            setOrdersLoading(false);
        }
    };

    useEffect(() => {
        fetchOrders(0, orderSearch);
    }, [orderSearch]);

    useEffect(() => {
        const token = globalThis.window ? localStorage.getItem("token") : null;
        const config = token ? { headers: { "Authorization": token.startsWith("Bearer ") ? token : `Bearer ${token}` } } : {};
        api.get("/product/findByStatus", config)
            .then(res => {
                const data = res.data;
                setProducts(Array.isArray(data) ? data : (data?.dtoList ?? []));
            })
            .catch(() => {});
    }, []);

    const goToPage = (newPage) => {
        if (newPage < 0 || newPage >= totalPages || newPage === page) return;
        fetchOrders(newPage, orderSearch);
    };

    const fetchOrderDetail = async (orderId) => {
        try {
            const token = globalThis.window ? localStorage.getItem("token") : null;
            const config = token ? { headers: { "Authorization": token.startsWith("Bearer ") ? token : `Bearer ${token}` } } : {};
            const res = await api.post("/order/getOrder", { orderId }, config);
            const order = res.data;
            const enrichedEntries = (order?.entryDtoList || []).map(e => {
                const matched = products.find(p => p.identifier === e.product);
                return {
                    ...e,
                    productName: matched?.name || e.productName || e.product,
                    sku: matched?.sku || matched?.identifier || e.product,
                };
            });
            setSelectedOrder({ ...order, entryDtoList: enrichedEntries });
            setShowOrderDetail(true);
        } catch (err) {
            console.error(err);
            alert("Failed to load matching order index details.");
        }
    };

    const handleInvoicePrint = (order) => {
        const orderId = order.orderId || "—";
        const orderTotal = Number(order.totalPrice || 0).toFixed(2);
        const orderDiscount = Number(order.totalDiscount || 0).toFixed(2);
        const orderMode = order.paymentMode || "—";
        const orderTime = order.orderDate ? new Date(order.orderDate).toLocaleString("en-GB") : today;
        const items = (order.entryDtoList || []).map(e => {
            const matched = products.find(p => p.identifier === e.product);
            return {
                ...e,
                productName: matched?.name || e.productName || e.product,
                sku: matched?.sku || matched?.identifier || e.product,
            };
        });
        const subtotal = items.reduce((s, e) => s + Number(e.sellingPrice ?? 0) * Number(e.quantity ?? 0), 0).toFixed(2);

        const itemsHtml = items.length === 0
            ? `<div style="text-align:center; padding:10px 0; font-size:12px;">No items found</div>`
            : items.map(entry => {
                const qty = Number(entry.quantity ?? 0);
                const price = Number(entry.sellingPrice ?? 0);
                const disc = Number(entry.discount ?? 0);
                const lineTotal = Number(entry.totalPrice ?? (price * qty - disc)).toFixed(2);
                return `
            <div class="item-row">
                <div class="item-name">${entry.productName || entry.product || entry.identifier || "Item"}</div>
                <div class="item-sku">SKU: ${entry.sku || entry.product || "—"}</div>
                <div class="item-meta">
                    <span>${qty} x Rs. ${price.toFixed(2)}</span>
                    <span>Rs. ${lineTotal}</span>
                </div>
            </div>
        `;
            }).join("");

        const printHtml = `
    <html>
        <head>
            <title>Print Order Invoice</title>
            <style>
                body { margin: 20px; font-family: Courier, monospace; color: #000; background: #fff; }
                .receipt-wrap { width: 100%; max-width: 360px; margin: 0 auto; }
                .flex-space { display: flex; justify-content: space-between; margin-bottom: 5px; }
                .dashed-line { border-bottom: 1px dashed #000; margin: 12px 0; }
                .center-text { text-align: center; font-weight: 700; }
                .item-row { margin-bottom: 8px; }
                .item-name { font-weight: 700; font-size: 13px; }
                .item-sku { font-size: 11px; color: #555; margin-bottom: 2px; }
                .item-meta { display: flex; justify-content: space-between; font-size: 12px; }
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
                <div class="center-text" style="margin-bottom: 10px; font-size: 13px;">ITEMS</div>
                ${itemsHtml}
                <div class="dashed-line"></div>
                <div class="flex-space"><span>Subtotal:</span><span>Rs. ${subtotal}</span></div>
                <div class="flex-space"><span>Discount:</span><span>- Rs. ${orderDiscount}</span></div>
                <div class="dashed-line"></div>
                <div class="flex-space" style="font-size: 15px; font-weight: 700;"><span>Amount Paid:</span><span>Rs. ${orderTotal}</span></div>
                <div class="dashed-line"></div>
                <div class="center-text" style="font-style: italic; margin-top: 16px;">Thank you for your purchase!</div>
            </div>
            <script>
                window.onload = function () {
                    window.focus();
                    window.print();
                };
            </script>
        </body>
    </html>
`;
        if (typeof globalThis !== "undefined" && globalThis.window) {
            const winPrint = globalThis.window.open("", "", "left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0");

            if (winPrint) {
                const blob = new Blob([printHtml], { type: "text/html" });
                const blobUrl = URL.createObjectURL(blob);
                winPrint.location.href = blobUrl;
                setTimeout(() => URL.revokeObjectURL(blobUrl), 60000);
            } else {
                alert("Please allow pop-ups in your browser to print receipts.");
            }
        }
    };

    const pageNumbers = Array.from({ length: totalPages }, (_, i) => i);

    const getTableSectionContent = () => {
        if (ordersLoading) {
            return <div style={{ padding: "60px", textAlign: "center", color: C.muted, fontSize: "14px" }}>Loading orders...</div>;
        }

        if (ordersError) {
            return <div style={{ padding: "40px", textAlign: "center", color: C.red, fontSize: "14px" }}>{ordersError}</div>;
        }

        if (orders.length === 0) {
            return <div style={{ padding: "60px", textAlign: "center", color: C.muted, fontSize: "14px" }}>No orders found.</div>;
        }

        return (
            <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "13px", background: "#fff", borderRadius: "10px", overflow: "hidden", boxShadow: "0 1px 4px rgba(0,0,0,0.06)" }}>
                <thead>
                    <tr style={{ background: "#f8fafc", borderBottom: "1.5px solid #e8eaf0", color: C.mid, textAlign: "left" }}>
                        <th style={{ padding: "12px 14px" }}>Order ID</th>
                        <th style={{ padding: "12px 14px" }}>Customer Phone</th>
                        <th style={{ padding: "12px 14px" }}>Date</th>
                        <th style={{ padding: "12px 14px" }}>Payment</th>
                        <th style={{ padding: "12px 14px", textAlign: "center" }}>Items</th>
                        <th style={{ padding: "12px 14px", textAlign: "right" }}>Discount</th>
                        <th style={{ padding: "12px 14px", textAlign: "right" }}>Total</th>
                        <th style={{ padding: "12px 14px", textAlign: "center" }}>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {orders.map((order, idx) => (
                        <tr key={`${order.orderId || order.identifier || "order"}-${idx}`} style={{ borderBottom: "1px solid #f0f1f6", color: C.text, transition: "background 0.1s" }} onMouseEnter={e => e.currentTarget.style.background = "#f8fafc"} onMouseLeave={e => e.currentTarget.style.background = "#fff"}>
                            <td style={{ padding: "12px 14px" }}><div style={{ fontWeight: "700", color: C.navy }}>{order.orderId}</div></td>
                            <td style={{ padding: "12px 14px", fontWeight: "600" }}>{order.identifier}</td>
                            <td style={{ padding: "12px 14px", color: C.muted, fontSize: "12px" }}>{order.orderDate ? new Date(order.orderDate).toLocaleString("en-GB") : "—"}</td>
                            <td style={{ padding: "12px 14px" }}><span style={{ padding: "2px 8px", background: "#f1f5f9", borderRadius: "4px", fontSize: "11px", fontWeight: "600" }}>{order.paymentMode || "—"}</span></td>
                            <td style={{ padding: "12px 14px", textAlign: "center", fontWeight: "600" }}>{(order.entryDtoList || []).length}</td>
                            <td style={{ padding: "12px 14px", textAlign: "right", color: C.red }}>-₹{Number(order.totalDiscount ?? 0).toFixed(2)}</td>
                            <td style={{ padding: "12px 14px", textAlign: "right", fontWeight: "700", color: C.navy }}>₹{Number(order.totalPrice ?? 0).toFixed(2)}</td>
                            <td style={{ padding: "12px 14px", textAlign: "center" }}>
                                <div style={{ display: "flex", gap: "6px", justifyContent: "center" }}>
                                    <button type="button" onClick={() => fetchOrderDetail(order.orderId)} style={{ padding: "4px 12px", background: C.navy, color: "#fff", border: "none", borderRadius: "5px", fontSize: "12px", fontWeight: "600", cursor: "pointer" }}>View</button>
                                    <button type="button" onClick={() => handleInvoicePrint(order)} style={{ padding: "4px 12px", background: "#fff", border: `1px solid ${C.mid}`, color: C.mid, borderRadius: "5px", fontSize: "12px", fontWeight: "600", cursor: "pointer" }}>Print</button>
                                </div>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        );
    };

    return (
        <div style={{ position: "fixed", top: "60px", right: 0, bottom: 0, left: isSidebarOpen ? "220px" : "55px", backgroundColor: "#f4f5f9", fontFamily: "'Segoe UI', sans-serif", display: "flex", flexDirection: "column", overflow: "hidden", transition: "left 0.2s ease" }}>
            <div style={{ background: "#fff", padding: "14px 20px", borderBottom: "1.5px solid #e8eaf0", display: "flex", alignItems: "center", justifyContent: "space-between", flexShrink: 0 }}>
                <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                    <button type="button" onClick={() => router.push("/home")} style={{ background: "#fff", border: `1.5px solid ${C.mid}`, color: C.mid, borderRadius: "7px", padding: "5px 14px", fontSize: "12px", fontWeight: "600", cursor: "pointer" }}>← Home</button>
                    <span style={{ fontSize: "16px", fontWeight: "700", color: C.navy }}>Orders</span>
                </div>
                <input readOnly value={today} style={{ ...inputSt, width: "140px", background: "#f7f8fc", color: C.text }} />
            </div>

            {showOrderDetail && selectedOrder && (
                <div style={{ position: "fixed", top: 0, right: 0, bottom: 0, left: 0, background: "rgba(0,0,0,0.55)", zIndex: 10000, display: "flex", alignItems: "center", justifyContent: "center" }}>
                    <div style={{ width: "680px", maxHeight: "85vh", background: "#fff", borderRadius: "12px", overflow: "hidden", display: "flex", flexDirection: "column", boxShadow: "0 8px 32px rgba(0,0,0,0.18)" }}>
                        <div style={{ padding: "18px 24px", borderBottom: "1px solid #e8eaf0", display: "flex", alignItems: "center", justifyContent: "space-between", flexShrink: 0 }}>
                            <div>
                                <h2 style={{ margin: 0, fontSize: "16px", color: C.navy, fontWeight: "700" }}>Order Details — {selectedOrder.orderId}</h2>
                                <span style={{ fontSize: "12px", color: C.muted }}>Customer ID: {selectedOrder.identifier}</span>
                            </div>
                            <button onClick={() => { setShowOrderDetail(false); setSelectedOrder(null); }} style={{ background: "none", border: "none", cursor: "pointer", color: C.muted, fontSize: "20px", lineHeight: 1 }}>✕</button>
                        </div>

                        <div style={{ flex: 1, overflowY: "auto", padding: "24px", display: "flex", flexDirection: "column", gap: "20px" }}>
                            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "12px" }}>
                                {[
                                    { label: "Order ID", value: selectedOrder.orderId },
                                    { label: "Customer Phone", value: selectedOrder.identifier },
                                    { label: "Payment Mode", value: selectedOrder.paymentMode || "—" },
                                    { label: "Order Date", value: selectedOrder.orderDate ? new Date(selectedOrder.orderDate).toLocaleString("en-GB") : "—" },
                                    { label: "Total Discount", value: `₹${Number(selectedOrder.totalDiscount ?? 0).toFixed(2)}` },
                                    { label: "Grand Total", value: `₹${Number(selectedOrder.totalPrice ?? 0).toFixed(2)}` },
                                ].map(item => (
                                    <div key={item.label} style={{ background: "#f8fafc", borderRadius: "8px", padding: "12px" }}>
                                        <div style={{ fontSize: "11px", color: C.muted, fontWeight: "600", marginBottom: "4px" }}>{item.label}</div>
                                        <div style={{ fontSize: "13px", color: C.text, fontWeight: "700" }}>{item.value}</div>
                                    </div>
                                ))}
                            </div>

                            <div>
                                <div style={{ fontSize: "13px", fontWeight: "700", color: C.navy, marginBottom: "10px" }}>Order Items ({(selectedOrder.entryDtoList || []).length})</div>
                                {(selectedOrder.entryDtoList || []).length === 0 ? (
                                    <div style={{ padding: "20px", textAlign: "center", color: C.muted, fontSize: "13px" }}>No items linked to this order.</div>
                                ) : (
                                    <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "13px" }}>
                                        <thead><tr style={{ borderBottom: "1.5px solid #e8eaf0", color: C.mid, textAlign: "left" }}><th style={{ padding: "8px 6px" }}>Product</th><th style={{ padding: "8px 6px", textAlign: "center" }}>Qty</th><th style={{ padding: "8px 6px", textAlign: "right" }}>Selling Price</th><th style={{ padding: "8px 6px", textAlign: "right" }}>Discount</th><th style={{ padding: "8px 6px", textAlign: "right" }}>Total</th></tr></thead>
                                        <tbody>
                                            {(selectedOrder.entryDtoList || []).map((entry, idx) => (
                                                <tr key={entry.identifier || idx} style={{ borderBottom: "1px solid #f0f1f6", color: C.text }}>
                                                    <td style={{ padding: "10px 6px" }}><div style={{ fontWeight: "600" }}>{entry.productName || entry.product}</div><div style={{ fontSize: "11px", color: C.muted }}>SKU: {entry.sku || entry.product}</div></td>
                                                    <td style={{ padding: "10px 6px", textAlign: "center", fontWeight: "600" }}>{entry.quantity}</td>
                                                    <td style={{ padding: "10px 6px", textAlign: "right" }}>₹{Number(entry.sellingPrice ?? 0).toFixed(2)}</td>
                                                    <td style={{ padding: "10px 6px", textAlign: "right", color: C.red }}>-₹{Number(entry.discount ?? 0).toFixed(2)}</td>
                                                    <td style={{ padding: "10px 6px", textAlign: "right", fontWeight: "700" }}>₹{Number(entry.totalPrice ?? 0).toFixed(2)}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                        <tfoot><tr style={{ borderTop: "1.5px solid #e8eaf0" }}><td colSpan="4" style={{ padding: "10px 6px", textAlign: "right", fontWeight: "700", color: C.navy }}>Grand Total</td><td style={{ padding: "10px 6px", textAlign: "right", fontWeight: "700", fontSize: "15px", color: C.navy }}>₹{Number(selectedOrder.totalPrice ?? 0).toFixed(2)}</td></tr></tfoot>
                                    </table>
                                )}
                            </div>
                        </div>

                        <div style={{ padding: "16px 24px", borderTop: "1px solid #e8eaf0", display: "flex", justifyContent: "flex-end", gap: "10px", alignItems: "center", flexShrink: 0 }}>
                            <button type="button" onClick={() => handleInvoicePrint(selectedOrder)} style={{ padding: "8px 16px", background: C.mid, color: "#fff", border: "none", borderRadius: "7px", fontWeight: "600", cursor: "pointer", fontSize: "13px" }}>🖨 Print Invoice</button>
                            <button type="button" onClick={() => { setShowOrderDetail(false); setSelectedOrder(null); }} style={{ padding: "8px 20px", background: C.navy, color: "#fff", border: "none", borderRadius: "7px", fontWeight: "600", cursor: "pointer", fontSize: "13px" }}>Close</button>
                        </div>
                    </div>
                </div>
            )}

            <div style={{ flex: 1, display: "flex", flexDirection: "column", overflow: "hidden" }}>
                <div style={{ background: "#fff", padding: "12px 20px", borderBottom: "1px solid #e8eaf0", display: "flex", gap: "10px", alignItems: "center", flexShrink: 0 }}>
                    <input type="text" placeholder="Search by Order ID, Customer Phone, Payment Mode..." value={orderSearch} onChange={e => setOrderSearch(e.target.value)} style={{ ...inputSt, flex: 2 }} />
                    <button type="button" onClick={() => fetchOrders(page, orderSearch)} style={{ height: "38px", padding: "0 18px", background: C.mid, color: "#fff", border: "none", borderRadius: "7px", fontWeight: "600", fontSize: "13px", cursor: "pointer", whiteSpace: "nowrap" }}>🔄 Refresh</button>
                </div>

                <div style={{ background: "#f8fafc", padding: "10px 20px", borderBottom: "1px solid #e8eaf0", display: "flex", gap: "20px", flexShrink: 0 }}>
                    {[
                        { label: "Total Records", value: totalRecords, color: C.navy },
                        { label: "Page Revenue", value: `₹${orders.reduce((s, o) => s + Number(o.totalPrice ?? 0), 0).toFixed(2)}`, color: C.green },
                    ].map(stat => (
                        <div key={stat.label} style={{ display: "flex", flexDirection: "column", gap: "2px" }}>
                            <span style={{ fontSize: "11px", color: C.muted, fontWeight: "600" }}>{stat.label}</span>
                            <span style={{ fontSize: "16px", fontWeight: "700", color: stat.color }}>{stat.value}</span>
                        </div>
                    ))}
                </div>

                <div style={{ flex: 1, overflowY: "auto", padding: "16px 20px" }}>
                    {getTableSectionContent()}
                </div>

                {!ordersLoading && !ordersError && orders.length > 0 && (
                    <div style={{
                        background: "#fff",
                        borderTop: "1px solid #e8eaf0",
                        padding: "12px 20px",
                        display: "flex",
                        justifyContent: "space-between",
                        alignItems: "center",
                        gap: "10px",
                        flexShrink: 0,
                    }}>
                        <span style={{ fontSize: "13px", color: C.muted }}>
                            Showing <strong style={{ color: C.text }}>{page * PAGE_SIZE + 1}</strong> – <strong style={{ color: C.text }}>{page * PAGE_SIZE + orders.length}</strong> of <strong style={{ color: C.text }}>{totalRecords}</strong> entries
                        </span>
                        {totalPages > 1 && <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>

                        <button
                            type="button"
                            onClick={() => goToPage(page - 1)}
                            disabled={page === 0}
                            style={{
                                width: "32px", height: "32px", borderRadius: "7px",
                                border: "1.5px solid #dcdfe6", background: "#fff",
                                color: page === 0 ? "#cbd5e1" : C.text,
                                cursor: page === 0 ? "not-allowed" : "pointer",
                                fontSize: "14px",
                            }}
                        >‹</button>

                        {pageNumbers.map((p) => (
                            <button
                                key={p}
                                type="button"
                                onClick={() => goToPage(p)}
                                style={{
                                    minWidth: "32px", height: "32px", padding: "0 8px", borderRadius: "7px",
                                    border: p === page ? "none" : "1.5px solid #dcdfe6",
                                    background: p === page ? C.navy : "#fff",
                                    color: p === page ? "#fff" : C.text,
                                    fontWeight: "600", fontSize: "13px", cursor: "pointer",
                                }}
                            >
                                {p + 1}
                            </button>
                        ))}

                        <button
                            type="button"
                            onClick={() => goToPage(page + 1)}
                            disabled={page >= totalPages - 1}
                            style={{
                                width: "32px", height: "32px", borderRadius: "7px",
                                border: "1.5px solid #dcdfe6", background: "#fff",
                                color: page >= totalPages - 1 ? "#cbd5e1" : C.text,
                                cursor: page >= totalPages - 1 ? "not-allowed" : "pointer",
                                fontSize: "14px",
                            }}
                        >›</button>
                        </div>}
                    </div>
                )}
            </div>
        </div>
    );
}