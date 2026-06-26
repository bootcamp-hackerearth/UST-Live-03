"use client";
import { useState, useEffect, useMemo } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";
import api from "../../components/Axios";

function formatDate(dateStr) {
    if (!dateStr) return "—";
    const d = new Date(dateStr);
    return d.toLocaleString("en-IN", { day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" });
}

function calculateFinalAmount(order) {
    return Number(order?.totalPrice || 0);
}

function formatMoney(amount) {
    return `₹${Number(amount || 0).toFixed(2)}`;
}

function PageNavButton({ direction, disabled, onClick }) {
    return (
        <button
            type="button"
            className={`min-w-9 h-9 px-3 rounded-lg border-[1.5px] border-solid bg-white font-bold text-lg flex items-center justify-center transition-all ${disabled ? "text-gray-300 border-gray-100 cursor-not-allowed" : "text-[#2d6a4f] border-gray-200 cursor-pointer hover:bg-gray-50"}`}
            onClick={onClick}
            disabled={disabled}
        >
            {direction === "prev" ? <>&lsaquo;</> : <>&rsaquo;</>}
        </button>
    );
}

PageNavButton.propTypes = {
    direction: PropTypes.oneOf(["prev", "next"]).isRequired,
    disabled: PropTypes.bool.isRequired,
    onClick: PropTypes.func.isRequired,
};

function OrderEntryRow({ entry, productName }) {
    return (
        <div className="flex items-center justify-between py-2 border-b border-gray-100 last:border-0">
            <div>
                <p className="text-[13px] font-semibold text-gray-800 m-0">{productName}</p>
                <p className="text-[11px] text-gray-400 m-0 font-mono">SKU: {entry.product}</p>
            </div>
            <div className="text-right">
                <p className="text-[13px] font-bold text-gray-900 m-0">{formatMoney(entry.totalPrice)}</p>
                <p className="text-[11px] text-gray-400 m-0">Qty: {Math.floor(Number(entry.quantity))} × {formatMoney(entry.sellingPrice)}</p>
                {Number(entry.discount) > 0 && (
                    <p className="text-[11px] text-red-500 m-0">Saved: {formatMoney(entry.discount)}</p>
                )}
            </div>
        </div>
    );
}

OrderEntryRow.propTypes = {
    entry: PropTypes.shape({
        identifier: PropTypes.string,
        product: PropTypes.string,
        quantity: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        sellingPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        totalPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        discount: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    }).isRequired,
    productName: PropTypes.string.isRequired,
};

function OrderAmountSummary({ order, finalAmount }) {
    return (
        <div className="mt-3 pt-3 border-t border-gray-200 flex flex-col gap-1">
            <div className="flex justify-between text-[13px] text-gray-500">
                <span>Total Price</span>
                <span>{formatMoney(order.totalPrice)}</span>
            </div>
            {Number(order.totalDiscount) > 0 && (
                <div className="flex justify-between text-[13px] text-red-500">
                    <span>Total Discount</span>
                    <span>− {formatMoney(order.totalDiscount)}</span>
                </div>
            )}
            <div className="flex justify-between text-[15px] font-bold text-gray-900 mt-1">
                <span>Final Amount</span>
                <span className="text-[#2d6a4f]">{formatMoney(finalAmount)}</span>
            </div>
        </div>
    );
}

OrderAmountSummary.propTypes = {
    order: PropTypes.shape({
        totalPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        totalDiscount: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    }).isRequired,
    finalAmount: PropTypes.number.isRequired,
};

export default function OrdersPage() {
    const router = useRouter();
    const [orders, setOrders] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [expandedOrderId, setExpandedOrderId] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [debouncedSearch, setDebouncedSearch] = useState("");
    const [printOrder, setPrintOrder] = useState(null);

    const [pagination, setPagination] = useState({
        page: 0,
        sizePerPage: 6,
        sortDirection: "DESC",
        sortField: "orderDate",
    });

    useEffect(() => {
        loadProducts();
    }, []);

    useEffect(() => {
        const handler = setTimeout(() => {
            setDebouncedSearch(searchTerm);
            setPagination((prev) => ({ ...prev, page: 0 }));
        }, 300);
        return () => clearTimeout(handler);
    }, [searchTerm]);

    useEffect(() => {
        loadOrders();
    }, [pagination, debouncedSearch]);

    useEffect(() => {
        if (printOrder) {
            const timer = setTimeout(() => {
                globalThis.print();
                setPrintOrder(null);
            }, 100);
            return () => clearTimeout(timer);
        }
    }, [printOrder]);

    async function loadProducts() {
        try {
            const res = await api.post("/product/list", {
                page: 0, sizePerPage: 1000, sortDirection: "ASC", sortField: "id",
            });
            setProducts(res.data.dtoList || []);
        } catch { }
    }

    async function loadOrders() {
        setLoading(true);
        setError("");
        try {
            if (debouncedSearch.trim()) {
                const res = await api.post("/order/list", { ...pagination, sizePerPage: 1000 });
                const all = res.data.dtoList ?? [];
                const term = debouncedSearch.toLowerCase();
                const filtered = all.filter(
                    (o) =>
                        o.orderId?.toLowerCase().includes(term) ||
                        o.identifier?.toLowerCase().includes(term)
                );
                setOrders(filtered);
                setTotalPages(1);
            } else {
                const res = await api.post("/order/list", pagination);
                setOrders(res.data.dtoList ?? []);
                setTotalPages(res.data.totalPages ?? 1);
            }
        } catch {
            setError("Could not load orders.");
        } finally {
            setLoading(false);
        }
    }

    function toggleExpand(orderId) {
        setExpandedOrderId(expandedOrderId === orderId ? null : orderId);
    }

    function goToPage(pageIndex) {
        setPagination((prev) => ({ ...prev, page: pageIndex }));
    }

    function handlePrintOrder(order, e) {
        e.stopPropagation();
        setPrintOrder(order);
    }

    const productMap = useMemo(() => {
        const map = {};
        products.forEach((p) => { map[p.identifier] = p; });
        return map;
    }, [products]);

    const currentPage = pagination.page;

    function getVisiblePages() {
        let start = currentPage - 1;
        if (start < 0) start = 0;
        if (start + 3 > totalPages) start = totalPages - 3;
        if (start < 0) start = 0;
        return Array.from({ length: Math.min(3, totalPages) }, (_, i) => start + i);
    }

    const printFinalAmount = printOrder ? calculateFinalAmount(printOrder) : 0;

    return (
        <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-gray-50 font-sans flex flex-col overflow-hidden">

            <div className="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between shrink-0 print:hidden relative">
                <div className="z-10">
                    <button
                        type="button"
                        onClick={() => router.push("/home")}
                        className="px-4 py-2 text-[13px] font-semibold text-[#2d6a4f] border-[1.5px] border-[#2d6a4f] rounded-lg bg-transparent cursor-pointer shrink-0 hover:bg-[#f0faf5]"
                    >
                        ← Home
                    </button>
                </div>

                <h2 className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 text-[15px] font-bold text-gray-800 m-0 pointer-events-none">
                    Orders
                </h2>

                <div className="flex items-center gap-4 z-10">
                    <input
                        type="text"
                        placeholder="Search by Order ID or phone…"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-[260px] px-4 py-2 border-[1.5px] border-gray-300 rounded-lg text-[13px] outline-none bg-white focus:border-[#2d6a4f]"
                    />
                    <button
                        type="button"
                        onClick={loadOrders}
                        className="px-4 py-2 text-[13px] font-semibold text-gray-500 border-[1.5px] border-gray-300 rounded-lg bg-white cursor-pointer hover:bg-gray-50"
                    >
                        ↻ Refresh
                    </button>
                </div>
            </div>

            <div className="flex-1 overflow-y-auto p-6 flex flex-col print:hidden">
                {error && (
                    <div className="bg-red-50 border border-red-300 text-red-700 rounded-lg px-4 py-3 text-[13px] text-center mb-4">
                        {error}
                    </div>
                )}

                {(() => {
                    if (loading) {
                        return <p className="text-center text-gray-400 text-sm py-10">Loading orders…</p>;
                    }

                    if (orders.length === 0) {
                        return (
                            <div className="flex flex-col items-center justify-center py-20 text-center gap-3 text-gray-400">
                                <span className="text-5xl">📋</span>
                                <p className="text-[15px] font-semibold m-0">No orders found</p>
                                <p className="text-[12px] m-0">Orders will appear here after checkout.</p>
                            </div>
                        );
                    }

                    return (
                        <>
                            <div className="flex flex-col gap-3 max-w-[860px] mx-auto w-full flex-1">
                                {orders.map((order) => {
                                    const itemsList = order.entryDtoList || [];
                                    const isExpanded = expandedOrderId === order.orderId;
                                    const finalAmount = calculateFinalAmount(order);

                                    return (
                                        <div key={order.orderId} className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
                                            <div className="px-5 py-4 flex items-center gap-4 hover:bg-gray-50 transition-colors">
                                                <button
                                                    type="button"
                                                    onClick={() => toggleExpand(order.orderId)}
                                                    className="flex-1 min-w-0 flex items-center gap-4 text-left cursor-pointer"
                                                >
                                                    <div className="flex-1 min-w-0">
                                                        <div className="flex items-center gap-2 flex-wrap">
                                                            <span className="text-[13px] font-bold text-gray-900 font-mono">{order.orderId}</span>
                                                            <span className={`text-[10px] font-semibold px-2 py-0.5 rounded-full border ${order.orderStatus === "PLACED" ? "text-[#2d6a4f] bg-[#f0faf5] border-[#b7e4c7]" : "text-gray-500 bg-gray-100 border-gray-200"}`}>
                                                                {order.orderStatus || "—"}
                                                            </span>
                                                            <span className="text-[11px] text-gray-400 bg-gray-100 px-2 py-0.5 rounded-full">
                                                                {order.paymentMode}
                                                            </span>
                                                        </div>
                                                        <p className="text-[11px] text-gray-400 m-0 mt-1">
                                                            Customer: {order.identifier} · {formatDate(order.orderDate)}
                                                        </p>
                                                    </div>

                                                    <div className="text-right shrink-0">
                                                        <p className="text-[15px] font-bold text-[#2d6a4f] m-0">{formatMoney(finalAmount)}</p>
                                                        <p className="text-[11px] text-gray-400 m-0">{itemsList.length} item{itemsList.length === 1 ? "" : "s"}</p>
                                                    </div>

                                                    <span className="text-gray-400 text-[18px] shrink-0 ml-1">{isExpanded ? "▲" : "▼"}</span>
                                                </button>

                                                <button
                                                    type="button"
                                                    onClick={(e) => handlePrintOrder(order, e)}
                                                    className="shrink-0 px-3 py-1.5 text-[12px] font-semibold text-gray-600 border-[1.5px] border-gray-300 rounded-lg bg-white cursor-pointer hover:bg-gray-50"
                                                >
                                                    🖨️ Print
                                                </button>
                                            </div>

                                            {isExpanded && (
                                                <div className="border-t border-gray-100 px-5 py-4 bg-gray-50">
                                                    <div className="flex flex-col gap-2">
                                                        {itemsList.map((entry) => (
                                                            <OrderEntryRow
                                                                key={entry.identifier}
                                                                entry={entry}
                                                                productName={productMap[entry.product]?.name || entry.product}
                                                            />
                                                        ))}
                                                    </div>

                                                    <OrderAmountSummary order={order} finalAmount={finalAmount} />
                                                </div>
                                            )}
                                        </div>
                                    );
                                })}
                            </div>

                            {totalPages > 1 && (
                                <div className="flex items-center justify-center gap-1.5 py-4 shrink-0">
                                    <PageNavButton direction="prev" disabled={currentPage === 0} onClick={() => goToPage(currentPage - 1)} />

                                    {getVisiblePages().map((pageIndex) => (
                                        <button
                                            type="button"
                                            key={pageIndex}
                                            className={`min-w-9 h-9 px-2.5 rounded-lg border-[1.5px] border-solid text-xs font-semibold flex items-center justify-center cursor-pointer transition-all ${currentPage === pageIndex ? "bg-[#2d6a4f] border-[#2d6a4f] text-white" : "bg-white border-gray-200 text-gray-700 hover:bg-gray-50"}`}
                                            onClick={() => goToPage(pageIndex)}
                                        >
                                            {pageIndex + 1}
                                        </button>
                                    ))}

                                    <PageNavButton direction="next" disabled={currentPage === totalPages - 1} onClick={() => goToPage(currentPage + 1)} />
                                    <span className="text-xs text-gray-400 px-2 whitespace-nowrap">
                                        Page {currentPage + 1} of {totalPages}
                                    </span>
                                </div>
                            )}
                        </>
                    );
                })()}
            </div>

            {printOrder && (
                <div className="hidden print:block print:fixed print:inset-0 print:bg-white">
                    <div className="max-w-[400px] mx-auto py-6 px-6 font-mono text-black">
                        <div className="text-center mb-4">
                            <p className="text-[16px] font-bold m-0">Retail POS</p>
                            <p className="text-[10px] m-0 mt-1">1-38,,Mallamgunta,Andhra Pradesh - 517507</p>
                            <p className="text-[10px] m-0">GSTIN: 1234567890</p>
                        </div>
                        <div className="border-t border-b border-black border-dashed py-2 mb-2 text-[11px]">
                            <div className="flex justify-between"><span>Bill No:</span><span>{printOrder.orderId}</span></div>
                            <div className="flex justify-between"><span>Date:</span><span>{formatDate(printOrder.orderDate)}</span></div>
                            <div className="flex justify-between"><span>Customer:</span><span>{printOrder.identifier}</span></div>
                            <div className="flex justify-between"><span>Payment:</span><span>{printOrder.paymentMode}</span></div>
                        </div>
                        <table className="w-full text-[11px] mb-2">
                            <thead>
                                <tr className="border-b border-black">
                                    <th className="text-left py-1">Item</th>
                                    <th className="text-center py-1">Qty</th>
                                    <th className="text-right py-1">Rate</th>
                                    <th className="text-right py-1">Amt</th>
                                </tr>
                            </thead>
                            <tbody>
                                {(printOrder.entryDtoList || []).map((entry) => (
                                    <tr key={entry.identifier}>
                                        <td className="py-0.5">{productMap[entry.product]?.name || entry.product}</td>
                                        <td className="text-center py-0.5">{Math.floor(Number(entry.quantity))}</td>
                                        <td className="text-right py-0.5">{Number(entry.sellingPrice || 0).toFixed(2)}</td>
                                        <td className="text-right py-0.5">{Number(entry.totalPrice || 0).toFixed(2)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                        <div className="border-t border-black border-dashed pt-2 text-[11px]">
                            <div className="flex justify-between"><span>Subtotal:</span><span>{formatMoney(printOrder.totalPrice)}</span></div>
                            {Number(printOrder.totalDiscount) > 0 && (
                                <div className="flex justify-between"><span>Discount:</span><span>-{formatMoney(printOrder.totalDiscount)}</span></div>
                            )}
                            <div className="flex justify-between text-[13px] font-bold border-t border-black mt-1 pt-1">
                                <span>TOTAL:</span><span>{formatMoney(printFinalAmount)}</span>
                            </div>
                        </div>
                        <p className="text-center text-[10px] mt-4 mb-0">Thank you for shopping with us!</p>
                    </div>
                </div>
            )}

            <style>{`
                @media print {
                    body * { visibility: hidden; }
                    .print\\:block, .print\\:block * { visibility: visible; }
                }
            `}</style>
        </div>
    );
}