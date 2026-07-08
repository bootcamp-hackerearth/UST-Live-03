"use client";

import React, { useEffect, useState } from "react";
import {
    ArrowPathIcon,
    XMarkIcon,
    ShoppingBagIcon,
    UserIcon,
    PhoneIcon,
    CreditCardIcon,
    TagIcon,
    CalendarIcon,
} from "@heroicons/react/24/outline";

export default function OrdersPage() {
    const [orders, setOrders] = useState([]);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [search, setSearch] = useState("");
    const [loading, setLoading] = useState(false);

    const getToken = () => localStorage.getItem("token");

    const loadOrders = async () => {
        try {
            setLoading(true);
            const response = await fetch(process.env.NEXT_PUBLIC_BASE_URL+"/orders/list", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${getToken()}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    page: 0,
                    sizePerPage: 100,
                    sortDirection: "DESC",
                    sortField: "id",
                }),
            });

            const text = await response.text();
            const data = text ? JSON.parse(text) : {};
            setOrders(Array.isArray(data) ? data : data.dtoList || data.content || []);
        } catch (error) {
            console.error("Failed loading order historical registries:", error);
        } finally {
            setLoading(false);
        }
    };

    const viewOrder = async (identifier) => {
        try {
            const orderResponse = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/orders/get`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${getToken()}`,
                    "Content-Type": "text/plain",
                },
                body: String(identifier),
            });
            const orderText = await orderResponse.text();
            const orderData = orderText ? JSON.parse(orderText) : {};

            const entryResponse = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/ordersentry/findbyordersid`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${getToken()}`,
                    "Content-Type": "text/plain",
                },
                body: String(identifier),
            });
            const entryText = await entryResponse.text();
            const entryData = entryText ? JSON.parse(entryText) : [];

            setSelectedOrder({
                ...orderData,
                ordersEntryDtoList: Array.isArray(entryData) ? entryData : entryData.dtoList || [],
            });
        } catch (error) {
            console.error("Failed to inspect row node entry mappings:", error);
        }
    };

    useEffect(() => {
        loadOrders();
    }, []);

    const filteredOrders = orders.filter((order) => {
        const orderId = String(order?.identifier || order?.id || "").toLowerCase();
        const customer = String(order?.customerName || order?.name || "").toLowerCase();
        const phone = String(order?.customerPhone || order?.phoneNo || order?.phone || "");
        const query = search.toLowerCase();

        return orderId.includes(query) || customer.includes(query) || phone.includes(query);
    });

    const getOrderStatusStyle = (status) => {
        switch (String(status).toLowerCase()) {
            case "completed":
            case "paid":
            case "success":
                return "bg-emerald-50 text-[#10b981] border-emerald-200";
            case "pending":
            case "processing":
                return "bg-amber-50 text-amber-600 border-amber-200";
            case "cancelled":
            case "failed":
            case "voided":
                return "bg-[#fff2f2] text-[#e55555] border-[#ffd6d6]";
            default:
                return "bg-zinc-50 text-zinc-600 border-zinc-200";
        }
    };

    const orderBody = (() => {
        if (loading) {
            return (
                <tr>
                    <td colSpan={6} className="text-center py-24 text-sm text-zinc-400">
                        <div className="flex items-center justify-center gap-2">
                            <ArrowPathIcon className="w-5 h-5 animate-spin text-[#6c63ff]" />
                            Querying database registry stream...
                        </div>
                    </td>
                </tr>
            );
        }

        if (filteredOrders.length === 0) {
            return (
                <tr>
                    <td colSpan={6} className="text-center py-24 text-sm text-[#8888a0]">
                        No Orders Indexed. Ingest new checkout payloads to generate records.
                    </td>
                </tr>
            );
        }

        return filteredOrders.map((order) => {
            const orderId = order?.identifier || order?.id || "—";
            const customerName = order?.customerName || order?.name || "Walk-in Guest";
            const customerPhone = order?.customerPhone || order?.phoneNo || order?.phone || "—";
            const netPayable = Number.parseFloat(order?.totalPrice || order?.amount || 0);
            const isSelected = selectedOrder && String(selectedOrder?.identifier || selectedOrder?.id) === String(orderId);

            return (
                <tr key={orderId} className="border-b border-[#ebebf5] group">
                    <td className="text-sm font-mono font-semibold text-[#2d2d6e] px-6 py-5 bg-transparent transition-colors group-hover:bg-[#ebebf5]/30">
                        {orderId}
                    </td>
                    <td className="text-sm text-[#2d2d6e] px-6 py-5 capitalize bg-transparent transition-colors group-hover:bg-[#ebebf5]/30">
                        {customerName}
                    </td>
                    <td className="text-sm font-mono text-[#4b4b75] px-6 py-5 bg-transparent transition-colors group-hover:bg-[#ebebf5]/30">
                        {customerPhone}
                    </td>
                    <td className="text-sm px-6 py-5 bg-transparent transition-colors group-hover:bg-[#ebebf5]/30">
                        <span className={`px-2 py-0.5 rounded-md border text-[10px] font-bold uppercase tracking-wide ${getOrderStatusStyle(order.status || "paid")}`}>
                            {order.status || "PAID"}
                        </span>
                    </td>
                    <td className="text-sm font-mono font-bold text-right text-[#2d2d6e] px-6 py-5 bg-transparent transition-colors group-hover:bg-[#ebebf5]/30">
                        ₹ {netPayable.toFixed(2)}
                    </td>
                    <td className="px-6 py-5 bg-transparent transition-colors group-hover:bg-[#ebebf5]/30 text-center">
                        <button
                            type="button"
                            onClick={() => viewOrder(orderId)}
                            className={`px-4 h-8 rounded-lg text-xs font-medium border transition-all cursor-pointer ${isSelected
                                    ? "bg-[#6c63ff] border-[#6c63ff] text-white"
                                    : "bg-white text-[#4b4b75] border-[#ebebf5] hover:border-[#6c63ff] hover:text-[#6c63ff]"
                                }`}
                        >
                            Inspect
                        </button>
                    </td>
                </tr>
            );
        });
    })();

    return (
        <div className="min-h-screen bg-[#f4f5fa] p-10 font-sans antialiased text-slate-900">

            <div className="bg-white border border-zinc-200 rounded-2xl p-8 mb-6 shadow-sm flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-bold tracking-tight text-[#2d2d6e] mb-1">Order History Workspace</h1>
                    <p className="text-sm text-zinc-400">Search dynamic archive receipts, trace client nodes, and inspect billing ledgers.</p>
                </div>
                <button
                    type="button"
                    onClick={loadOrders}
                    disabled={loading}
                    className="h-11 px-5 bg-[#6c63ff] hover:bg-[#5850ec] text-white rounded-xl text-sm font-medium flex items-center gap-2 transition-all disabled:opacity-60"
                >
                    <ArrowPathIcon className={`w-5 h-5 ${loading ? "animate-spin" : ""}`} />
                    Refresh Sync
                </button>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start">

                <div className={`${selectedOrder ? "lg:col-span-2" : "lg:col-span-3"} flex flex-col gap-4 transition-all duration-300`}>
                    <div className="bg-white border border-[#ebebf5] rounded-2xl p-8 min-h-[400px] shadow-sm">

                        <div className="mb-6 max-w-md">
                            <label htmlFor="order-filter-input" className="text-xs font-semibold tracking-wide text-[#4b4b75] block mb-1.5">Filter Archives</label>
                            <input
                                id="order-filter-input"
                                type="text"
                                placeholder="Search orders..."
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                                className="w-full h-11 bg-[#f8f8fc] border border-[#ebebf5] rounded-xl px-4 text-sm text-[#2d2d6e] outline-none focus:border-[#6c63ff] focus:bg-white transition-all placeholder-[#b0b0c8]"
                            />
                        </div>

                        <div className="w-full overflow-x-auto">
                            <table className="w-full border-collapse text-left">
                                <thead>
                                    <tr className="border-b-2 border-[#ebebf5]">
                                        <th className="text-xs font-semibold text-[#8888a0] uppercase tracking-wider px-6 py-4">Order ID</th>
                                        <th className="text-xs font-semibold text-[#8888a0] uppercase tracking-wider px-6 py-4">Customer</th>
                                        <th className="text-xs font-semibold text-[#8888a0] uppercase tracking-wider px-6 py-4">Phone</th>
                                        <th className="text-xs font-semibold text-[#8888a0] uppercase tracking-wider px-6 py-4">Status</th>
                                        <th className="text-xs font-semibold text-[#8888a0] uppercase tracking-wider px-6 py-4 text-right">Total</th>
                                        <th className="text-xs font-semibold text-[#8888a0] uppercase tracking-wider px-6 py-4 text-center w-[120px]">Action</th>
                                    </tr>
                                </thead>
                                <tbody>{orderBody}</tbody>
                            </table>
                        </div>
                    </div>
                </div>

                {selectedOrder && (
                    <div className="bg-white border border-[#ebebf5] rounded-2xl p-6 shadow-sm flex flex-col lg:sticky lg:top-28 animate-in fade-in zoom-in-95 duration-150">
                        <div className="flex justify-between items-center pb-4 border-b border-[#ebebf5] mb-4">
                            <div>
                                <h2 className="text-base font-semibold text-[#2d2d6e]">Receipt Manifest</h2>
                                <p className="text-xs font-mono text-zinc-400 mt-0.5">ID: {selectedOrder.identifier || selectedOrder.id}</p>
                            </div>
                            <button
                                type="button"
                                onClick={() => setSelectedOrder(null)}
                                className="text-zinc-400 hover:text-zinc-600 transition-all cursor-pointer"
                            >
                                <XMarkIcon className="w-5 h-5" />
                            </button>
                        </div>

                        <div className="bg-[#f8f8fc] border border-[#ebebf5] rounded-xl p-4 grid grid-cols-2 gap-y-3 gap-x-4 text-xs mb-4">
                            <div className="flex gap-2 items-start">
                                <UserIcon className="w-4 h-4 text-[#b0b0c8] shrink-0 mt-0.5" />
                                <div>
                                    <span className="block text-[10px] font-semibold text-[#8888a0] uppercase tracking-wider">Purchaser Profile</span>
                                    <span className="font-semibold text-[#2d2d6e] capitalize block mt-0.5">{selectedOrder.customerName || "Walk-in Guest"}</span>
                                </div>
                            </div>
                            <div className="flex gap-2 items-start">
                                <PhoneIcon className="w-4 h-4 text-[#b0b0c8] shrink-0 mt-0.5" />
                                <div>
                                    <span className="block text-[10px] font-semibold text-[#8888a0] uppercase tracking-wider">Phone Matrix</span>
                                    <span className="font-mono text-[#4b4b75] block mt-0.5">{selectedOrder.customerPhone || "—"}</span>
                                </div>
                            </div>
                            <div className="flex gap-2 items-start">
                                <CreditCardIcon className="w-4 h-4 text-[#b0b0c8] shrink-0 mt-0.5" />
                                <div>
                                    <span className="block text-[10px] font-semibold text-[#8888a0] uppercase tracking-wider">Payment</span>
                                    <span className="font-semibold text-[#2d2d6e] block mt-0.5">{selectedOrder.paymentType || selectedOrder.paymentMethod || "CASH"}</span>
                                </div>
                            </div>
                            <div className="flex gap-2 items-start">
                                <ShoppingBagIcon className="w-4 h-4 text-[#b0b0c8] shrink-0 mt-0.5" />
                                <div>
                                    <span className="block text-[10px] font-semibold text-[#8888a0] uppercase tracking-wider">Status</span>
                                    <span className={`inline-block text-[10px] font-bold border px-2 py-0.5 rounded-md uppercase tracking-wide mt-0.5 ${getOrderStatusStyle(selectedOrder.status || "completed")}`}>
                                        {selectedOrder.status || "COMPLETED"}
                                    </span>
                                </div>
                            </div>
                            <div className="flex gap-2 items-start">
                                <TagIcon className="w-4 h-4 text-[#b0b0c8] shrink-0 mt-0.5" />
                                <div>
                                    <span className="block text-[10px] font-semibold text-[#8888a0] uppercase tracking-wider">Coupon</span>
                                    <span className="font-semibold text-[#2d2d6e] block mt-0.5">{selectedOrder.couponCode || selectedOrder.coupon || "—"}</span>
                                </div>
                            </div>
                            <div className="flex gap-2 items-start">
                                <CalendarIcon className="w-4 h-4 text-[#b0b0c8] shrink-0 mt-0.5" />
                                <div>
                                    <span className="block text-[10px] font-semibold text-[#8888a0] uppercase tracking-wider">Order Date</span>
                                    <span className="font-semibold text-[#2d2d6e] block mt-0.5">
                                        {selectedOrder.orderDate
                                            ? new Date(selectedOrder.orderDate).toLocaleString("en-IN", {
                                                day: "numeric",
                                                month: "numeric",
                                                year: "numeric",
                                                hour: "numeric",
                                                minute: "2-digit",
                                                second: "2-digit",
                                                hour12: true,
                                            })
                                            : "-"}
                                    </span>
                                </div>
                            </div>
                        </div>

                        <div className="divide-y divide-[#ebebf5] overflow-y-auto max-h-60 mb-6 pr-1 no-scrollbar">
                            {selectedOrder.ordersEntryDtoList?.length > 0 ? (
                                selectedOrder.ordersEntryDtoList.map((item, idx) => {
                                    const currentQty = Number.parseInt(item?.quantity || 1, 10);
                                    const unitPrice = Number.parseFloat(item?.unitPrice || item?.unit_price || 0);
                                    const itemTotalPrice = Number.parseFloat(item?.totalPrice || item?.total_price || 0);
                                    const discount = Number.parseFloat(item?.discount || 0);

                                    return (
                                        <div key={item.identifier || idx} className="py-3 flex justify-between items-start first:pt-0 last:pb-0">
                                            <div className="flex-1 min-w-0">
                                                <p className="font-semibold text-sm text-[#2d2d6e] truncate capitalize">{item.productName || `Product (${item.product})`}</p>
                                                <div className="flex items-center gap-x-3 text-xs text-zinc-400 mt-0.5">
                                                    <span>Qty: <span className="text-[#2d2d6e] font-semibold font-mono">{currentQty}</span></span>
                                                    <span>Rate: ₹{unitPrice.toFixed(2)}</span>
                                                    {discount > 0 && <span className="text-[#10b981] font-medium">Saved: ₹{discount.toFixed(2)}</span>}
                                                </div>
                                            </div>
                                            <div className="text-right pl-2 font-mono">
                                                <p className="font-semibold text-sm text-[#2d2d6e]">₹ {itemTotalPrice.toFixed(2)}</p>
                                            </div>
                                        </div>
                                    );
                                })
                            ) : (
                                <div className="py-12 text-center text-sm text-[#8888a0]">No Items Bound to Manifest</div>
                            )}
                        </div>

                        <div className="bg-[#f8f8fc] border border-[#ebebf5] rounded-xl p-5 space-y-3 mt-auto">
                            <div className="flex justify-between text-xs text-[#4b4b75]">
                                <span>Gross Catalog Value</span>
                                <span className="font-mono">₹ {Number.parseFloat(selectedOrder.totalOriginalPrice || 0).toFixed(2)}</span>
                            </div>
                            <div className="flex justify-between text-xs text-[#10b981]">
                                <span>Adjustments Discount</span>
                                <span className="font-mono font-semibold">- ₹ {Number.parseFloat(selectedOrder.discount || 0).toFixed(2)}</span>
                            </div>
                            <div className="border-t border-dashed border-[#ebebf5] pt-3 flex justify-between items-center text-sm font-semibold text-[#2d2d6e]">
                                <span>Grand Ledger Total</span>
                                <span className="text-xl font-bold font-mono">₹ {Number.parseFloat(selectedOrder.totalPrice || 0).toFixed(2)}</span>
                            </div>
                        </div>

                    </div>
                )}
            </div>
        </div>
    );
}