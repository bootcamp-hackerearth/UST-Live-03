"use client";

import PropTypes from "prop-types";
import { useEffect, useState, useRef, useCallback, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import Layout from "@/components/Layout";
import api from "@/app/services/api";
import {
    Search,
    ShoppingBag,
    MoreVertical,
    Receipt,
    Trash2,
    X,
    Printer,
} from "lucide-react";


const currency = (value) =>
    `₹${Number(value || 0).toLocaleString("en-IN", {
        minimumFractionDigits: 2,
    })}`;

const formatDate = (value) => {
    if (!value) return "—";
    const d = new Date(value);
    if (Number.isNaN(d.getTime())) return value;
    return d.toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
    });
};

const formatTime = (value) => {
    if (!value) return "";
    const d = new Date(value);
    if (Number.isNaN(d.getTime())) return "";
    return d.toLocaleTimeString("en-IN", {
        hour: "2-digit",
        minute: "2-digit",
    });
};

const resolveOrderTimestamp = (order) => {
    const candidates = [
        order?.createdDate,
        order?.createdAt,
        order?.orderDate,
        order?.dateCreated,
        order?.creationDate,
        order?.createdOn,
        order?.created,
        order?.timestamp,
        order?.date,
    ];
    return candidates.find((v) => v != null) || null;
};

const getList = (res) => {
    const data = res?.data;
    if (Array.isArray(data)) return data;
    if (Array.isArray(data?.dtoList)) return data.dtoList;
    return [];
};


function OrderListContent() {
    const [orders, setOrders] = useState([]);
    const [customers, setCustomers] = useState([]);
    const [search, setSearch] = useState("");
    const [loading, setLoading] = useState(true);

    const [openMenuId, setOpenMenuId] = useState(null);
    const menuRef = useRef(null);

    const [invoiceOrder, setInvoiceOrder] = useState(null);
    const [invoiceLoading, setInvoiceLoading] = useState(false);

    const searchParams = useSearchParams();

    const loadData = useCallback(async () => {
        try {
            setLoading(true);
            const [orderRes, customerRes] = await Promise.all([
                api.post("/order/list", { page: 0, sizePerPage: 500 }),
                api.post("/customer/list", { page: 0, sizePerPage: 500 }),
            ]);
            setOrders(getList(orderRes));
            setCustomers(getList(customerRes));
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);


    useEffect(() => {
        const invoiceId = searchParams.get("invoice");
        if (!invoiceId || orders.length === 0) return;

        const target = orders.find((o) => o.identifier === invoiceId);
        if (target) {
            handleOpenInvoice(target);
            globalThis.history.replaceState(null, "", "/order");
        }
    }, [orders, searchParams]);

    useEffect(() => {
        const onClick = (e) => {
            if (menuRef.current && !menuRef.current.contains(e.target)) {
                setOpenMenuId(null);
            }
        };
        document.addEventListener("mousedown", onClick);
        return () => document.removeEventListener("mousedown", onClick);
    }, []);

    const getCustomer = (customerId) =>
        customers.find((c) => c.identifier === customerId) || null;

    const getCustomerName = (customerId) =>
        getCustomer(customerId)?.name || customerId || "Walk-in customer";


    const handleDelete = async (identifier) => {
        setOpenMenuId(null);
        const confirmDelete = globalThis.confirm(
            "Delete this order? This cannot be undone."
        );
        if (!confirmDelete) return;
        try {
            await api.get(`/order/delete?identifier=${identifier}`);
            loadData();
        } catch (error) {
            console.error(error);
        }
    };

    const handleOpenInvoice = async (order) => {
        setOpenMenuId(null);
        setInvoiceLoading(true);
        setInvoiceOrder({ ...order, entryList: order.entryList || [] });

        try {
            const res = await api.get(`/order/get?identifier=${order.identifier}`);
            const fullOrder = res?.data || order;

            const entries = fullOrder.entryList || [];
            const productIds = [...new Set(entries.map((e) => e.productIdentifier))];

            const productResults = await Promise.all(
                productIds.map((id) =>
                    api
                        .get(`/product/get?identifier=${id}`)
                        .then((r) => r.data)
                        .catch(() => null)
                )
            );

            const productMap = {};
            productIds.forEach((id, idx) => {
                productMap[id] = productResults[idx];
            });

            const hydratedEntries = entries.map((entry) => ({
                ...entry,
                productName:
                    productMap[entry.productIdentifier]?.name || entry.productIdentifier,
            }));

            let customerDetail = null;
            if (fullOrder.customerIdentifier) {
                try {
                    const cRes = await api.get(
                        `/customer/get?identifier=${fullOrder.customerIdentifier}`
                    );
                    customerDetail = cRes.data;
                } catch (e) {
                    console.error(e);
                }
            }

            setInvoiceOrder({
                ...fullOrder,
                entryList: hydratedEntries,
                customerDetail,
            });
        } catch (error) {
            console.error(error);
        } finally {
            setInvoiceLoading(false);
        }
    };

    const filteredOrders = orders.filter((order) => {
        const customerName = getCustomerName(order.customerIdentifier);
        const q = search.toLowerCase();
        return (
            order.identifier?.toLowerCase().includes(q) ||
            customerName.toLowerCase().includes(q)
        );
    });

    let tableContent;

    if (loading) {
        tableContent = (
            <tr>
                <td
                    colSpan={8}
                    className="text-center py-14 text-gray-400 text-sm"
                >
                    Loading orders...
                </td>
            </tr>
        );
    } else if (filteredOrders.length === 0) {
        tableContent = (
            <tr>
                <td
                    colSpan={8}
                    className="text-center py-14 text-gray-400 text-sm"
                >
                    No orders found
                </td>
            </tr>
        );
    } else {
        tableContent = filteredOrders.map((order, index) => (
            <tr
                key={order.identifier}
                className="border-b border-gray-100 hover:bg-cyan-50/40 transition-colors"
            >
                <td className="px-5 py-3.5 text-gray-400 text-xs">{index + 1}</td>
                <td className="px-5 py-3.5 font-medium text-gray-800 text-xs">{order.identifier}</td>
                <td className="px-5 py-3.5 text-gray-700">{getCustomerName(order.customerIdentifier)}</td>
                <td className="px-5 py-3.5 text-right font-mono text-gray-500 text-xs">{currency(order.originalPrice)}</td>
                <td className="px-5 py-3.5 text-right font-mono text-red-500 text-xs">{currency(order.discount)}</td>
                <td className="px-5 py-3.5 text-right font-mono font-bold text-gray-800 text-xs">{currency(order.totalPrice)}</td>
                <td className="px-5 py-3.5 text-center">
                    <span className="px-3 py-1 rounded-full bg-emerald-50 text-emerald-600 border border-emerald-200 text-xs font-medium">
                        {order.paymentMethod}
                    </span>
                </td>
                <td className="px-5 py-3.5">
                    <div className="flex justify-center relative">
                        <button
                            onClick={() => setOpenMenuId(openMenuId === order.identifier ? null : order.identifier)}
                            className="h-8 w-8 rounded-lg hover:bg-gray-100 flex items-center justify-center transition-colors"
                        >
                            <MoreVertical size={16} className="text-gray-500" />
                        </button>
                        {openMenuId === order.identifier && (
                            <div ref={menuRef} className="absolute right-0 top-9 z-20 w-44 bg-white rounded-xl border border-gray-200 shadow-lg py-1.5 text-left">
                                <button onClick={() => handleOpenInvoice(order)} className="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-gray-700 hover:bg-cyan-50">
                                    <Receipt size={15} className="text-[#0EA5C8]" /> Invoice
                                </button>

                                <div className="my-1 border-t border-gray-100" />
                                <button onClick={() => handleDelete(order.identifier)} className="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-red-600 hover:bg-red-50">
                                    <Trash2 size={15} /> Delete
                                </button>
                            </div>
                        )}
                    </div>
                </td>
            </tr>
        ));
    }

    return (
        <Layout>
            <div className="min-h-screen bg-gray-50 p-6">
                <div
                    className="rounded-2xl border-2 p-5 mb-5 flex items-center justify-between"
                    style={{ borderColor: "#0EA5C8" }}
                >
                    <div>
                        <div className="flex items-center gap-2 mb-0.5">
                            <ShoppingBag size={20} className="text-[#0EA5C8]" />
                            <h1 className="text-xl font-bold text-gray-800">Orders</h1>
                        </div>
                        <p className="text-sm text-gray-500 ml-7">Completed sales orders</p>
                    </div>

                    <div className="text-right text-sm text-gray-500">
                        <span className="font-semibold text-gray-700">Total orders: </span>
                        <span className="font-bold text-[#0EA5C8] text-base">{orders.length}</span>
                    </div>
                </div>

                <div className="bg-white rounded-2xl border border-gray-200 p-4 mb-5">
                    <div className="relative max-w-md">
                        <Search
                            size={16}
                            className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
                        />
                        <input
                            type="text"
                            placeholder="Search by order id or customer..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            className="w-full border border-gray-200 rounded-xl pl-9 pr-4 py-2.5 text-sm outline-none focus:ring-2 focus:ring-[#0EA5C8] transition-shadow"
                        />
                    </div>
                </div>

                <div className="bg-white rounded-2xl border border-gray-200 overflow-hidden">
                    <div className="overflow-x-auto">
                        <table className="w-full text-sm">
                            <thead>
                                <tr style={{ backgroundColor: "#0EA5C8" }} className="text-white">
                                    <th className="px-5 py-3.5 text-left font-semibold">SL</th>
                                    <th className="px-5 py-3.5 text-left font-semibold">Order ID</th>
                                    <th className="px-5 py-3.5 text-left font-semibold">Customer</th>
                                    <th className="px-5 py-3.5 text-right font-semibold">Original</th>
                                    <th className="px-5 py-3.5 text-right font-semibold">Discount</th>
                                    <th className="px-5 py-3.5 text-right font-semibold">Total</th>
                                    <th className="px-5 py-3.5 text-center font-semibold">Payment</th>
                                    <th className="px-5 py-3.5 text-center font-semibold">Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {tableContent}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            {invoiceOrder && (
                <InvoiceModal
                    order={invoiceOrder}
                    loading={invoiceLoading}
                    onClose={() => setInvoiceOrder(null)}
                />
            )}
        </Layout>
    );
}

export default function OrderListPage() {
    return (
        <Suspense fallback={<Layout><div className="p-6 text-sm text-gray-500">Loading component layout configuration...</div></Layout>}>
            <OrderListContent />
        </Suspense>
    );
}


function numberToWords(num) {
    const a = [
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
        "Seventeen", "Eighteen", "Nineteen",
    ];
    const b = [
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety",
    ];

    const inWords = (n) => {
        if (n < 20) return a[n];
        if (n < 100) return b[Math.floor(n / 10)] + (n % 10 ? " " + a[n % 10] : "");
        if (n < 1000) return a[Math.floor(n / 100)] + " Hundred" + (n % 100 ? " " + inWords(n % 100) : "");
        if (n < 100000) return inWords(Math.floor(n / 1000)) + " Thousand" + (n % 1000 ? " " + inWords(n % 1000) : "");
        if (n < 10000000) return inWords(Math.floor(n / 100000)) + " Lakh" + (n % 100000 ? " " + inWords(n % 100000) : "");
        return inWords(Math.floor(n / 10000000)) + " Crore" + (n % 10000000 ? " " + inWords(n % 10000000) : "");
    };

    const whole = Math.floor(Number(num) || 0);
    if (whole === 0) return "Zero Rupees";
    return `${inWords(whole)} Rupee${whole === 1 ? "" : "s"}`;
}

function InvoiceModal({ order, loading, onClose }) {
    const customer = order.customerDetail;
    const billing = customer?.billingAddress;

    const handlePrint = () => globalThis.print();

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 print:bg-white print:p-0">
            <div className="bg-white w-full max-w-2xl max-h-[90vh] rounded-2xl shadow-2xl overflow-hidden flex flex-col print:max-h-none print:rounded-none print:shadow-none">
                <div className="flex items-center justify-between px-6 py-4 print:hidden" style={{ backgroundColor: "#0EA5C8" }}>
                    <h2 className="font-bold text-white text-base tracking-wide">Sales Invoice</h2>
                    <div className="flex items-center gap-2">
                        <button onClick={handlePrint} className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-white text-[#0EA5C8] text-sm font-semibold hover:bg-cyan-50 transition-colors">
                            <Printer size={14} /> Print
                        </button>
                        <button onClick={onClose} className="h-8 w-8 rounded-lg bg-white/20 hover:bg-white/30 flex items-center justify-center transition-colors ml-1">
                            <X size={17} className="text-white" />
                        </button>
                    </div>
                </div>

                <div className="overflow-y-auto px-8 py-7">
                    {loading ? (
                        <div className="py-20 text-center text-gray-400 text-sm">Loading invoice details...</div>
                    ) : (
                        <>
                            <div className="flex items-start justify-between mb-6">
                                <div className="h-14 w-36 rounded-lg bg-cyan-50 border border-dashed border-cyan-300 flex items-center justify-center text-[10px] text-cyan-400">
                                    Your Logo
                                </div>
                                <div className="px-4 py-2 rounded-full" style={{ backgroundColor: "#0EA5C8" }}>
                                    <span className="text-sm font-bold tracking-widest text-white">INVOICE</span>
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-8 mb-7 text-sm">
                                <div className="space-y-1.5">
                                    <p><span className="font-semibold text-gray-800">Customer:</span> <span className="text-gray-600">{customer?.name || order.customerIdentifier || "Walk-in customer"}</span></p>
                                    <p><span className="font-semibold text-gray-800">Address:</span> <span className="text-gray-600">{billing ? [billing.addressLine1, billing.addressLine2, billing.city, billing.state, billing.pinCode].filter(Boolean).join(", ") || "N/A" : "N/A"}</span></p>
                                    <p><span className="font-semibold text-gray-800">Phone:</span> <span className="text-gray-600">{customer?.phoneNo || "N/A"}</span></p>
                                </div>
                                <div className="space-y-1.5 text-right">
                                    <p><span className="font-semibold text-gray-800">Invoice No:</span> <span className="text-gray-600">{order.identifier}</span></p>
                                    <p><span className="font-semibold text-gray-800">Date:</span> <span className="text-gray-600">{formatDate(resolveOrderTimestamp(order))}</span></p>
                                    <p><span className="font-semibold text-gray-800">Time:</span> <span className="text-gray-600">{formatTime(resolveOrderTimestamp(order))}</span></p>
                                </div>
                            </div>

                            <div className="rounded-xl overflow-hidden border border-gray-200 mb-6">
                                <table className="w-full text-sm">
                                    <thead>
                                        <tr style={{ backgroundColor: "#0EA5C8" }} className="text-white">
                                            <th className="px-4 py-3 text-left font-semibold w-10">SL</th>
                                            <th className="px-4 py-3 text-left font-semibold">Item</th>
                                            <th className="px-4 py-3 text-right font-semibold">Qty</th>
                                            <th className="px-4 py-3 text-right font-semibold">Unit Price</th>
                                            <th className="px-4 py-3 text-right font-semibold">Discount</th>
                                            <th className="px-4 py-3 text-right font-semibold">Total</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {(order.entryList || []).length === 0 ? (
                                            <tr><td colSpan={6} className="p-6 text-center text-gray-400">No items recorded for this order</td></tr>
                                        ) : (
                                            order.entryList.map((entry, idx) => (
                                                <tr key={entry.identifier || idx} className="border-b border-gray-100 last:border-0 hover:bg-cyan-50/30">
                                                    <td className="px-4 py-3 text-gray-400">{idx + 1}</td>
                                                    <td className="px-4 py-3 font-medium text-gray-800">{entry.productName}</td>
                                                    <td className="px-4 py-3 text-right text-gray-600">{entry.quantity}</td>
                                                    <td className="px-4 py-3 text-right font-mono text-gray-600">{currency(entry.unitPrice)}</td>
                                                    <td className="px-4 py-3 text-right font-mono text-red-500">{currency(entry.unitDiscount)}</td>
                                                    <td className="px-4 py-3 text-right font-mono font-semibold text-gray-800">{currency(entry.totalPrice)}</td>
                                                </tr>
                                            ))
                                        )}
                                    </tbody>
                                </table>
                            </div>

                            <div className="flex justify-between items-start gap-8">
                                <div className="text-sm text-gray-600 max-w-[55%]">
                                    <p className="font-medium text-gray-800 mb-1">{numberToWords(order.totalPrice)} only</p>
                                    <p className="mt-4"><span className="font-semibold text-gray-800">Payment Method:</span> {order.paymentMethod || "N/A"}</p>
                                    {order.receivedAmount != null && <p><span className="font-semibold text-gray-800">Received:</span> {currency(order.receivedAmount)}</p>}
                                    {order.changeAmount != null && <p><span className="font-semibold text-gray-800">Change:</span> {currency(order.changeAmount)}</p>}
                                </div>

                                <div className="w-64 text-sm space-y-2 bg-gray-50 rounded-xl p-4 border border-gray-100">
                                    <div className="flex justify-between"><span className="text-gray-500">Subtotal</span><span className="font-mono text-gray-700">{currency(order.originalPrice)}</span></div>
                                    <div className="flex justify-between"><span className="text-gray-500">Discount</span><span className="font-mono text-red-500">−{currency(order.discount)}</span></div>
                                    <div className="flex justify-between pt-2 border-t border-gray-200"><span className="font-semibold text-gray-800">Total Amount</span><span className="font-mono font-bold text-[#0EA5C8] text-base">{currency(order.totalPrice)}</span></div>
                                </div>
                            </div>
                            <p className="text-center text-xs text-gray-400 mt-8">Thank you for your business.</p>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

InvoiceModal.propTypes = {
    order: PropTypes.shape({
        identifier: PropTypes.string,
        customerIdentifier: PropTypes.string,
        originalPrice: PropTypes.number,
        discount: PropTypes.number,
        totalPrice: PropTypes.number,
        paymentMethod: PropTypes.string,
        receivedAmount: PropTypes.number,
        changeAmount: PropTypes.number,

        customerDetail: PropTypes.shape({
            name: PropTypes.string,
            phoneNo: PropTypes.string,
            billingAddress: PropTypes.shape({
                addressLine1: PropTypes.string,
                addressLine2: PropTypes.string,
                city: PropTypes.string,
                state: PropTypes.string,
                pinCode: PropTypes.string,
            }),
        }),

        entryList: PropTypes.arrayOf(
            PropTypes.shape({
                identifier: PropTypes.string,
                productName: PropTypes.string,
                quantity: PropTypes.number,
                unitPrice: PropTypes.number,
                unitDiscount: PropTypes.number,
                totalPrice: PropTypes.number,
            })
        ),
    }).isRequired,

    loading: PropTypes.bool.isRequired,
    onClose: PropTypes.func.isRequired,
};