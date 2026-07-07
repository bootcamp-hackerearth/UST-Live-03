// /app/pos/orders/page.jsx

"use client";

import { useState, useEffect, useMemo, useCallback } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import api from "@/app/api/axios";
import { IndianRupee, Package, Tag, Eye } from "lucide-react";

const PAYMENT_METHODS = {
    UPI: { icon: "📲", label: "UPI" },
    CARD: { icon: "💳", label: "Card" },
    CASH: { icon: "💵", label: "Cash" },
};

const FILTER_OPTIONS = ["ALL", "UPI", "CARD", "CASH"];

const STAT_CONFIG = [
    { key: "revenue", label: "Total Revenue", icon: IndianRupee, color: "#006E74" },
    { key: "orders", label: "Total Orders", icon: Package, color: "#231F20" },
    { key: "discount", label: "Total Discounts", icon: Tag, color: "#0097AC" },
];

function formatCurrency(num) {
    return new Intl.NumberFormat("en-IN", {
        style: "currency",
        currency: "INR",
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    }).format(num);
}

function parseOrderDate(orderId) {
    const parts = (orderId ?? "").split("-");
    const ts = parts[parts.length - 1];
    if (ts?.length !== 14) return null;
    return new Date(
        `${ts.slice(0, 4)}-${ts.slice(4, 6)}-${ts.slice(6, 8)}T${ts.slice(8, 10)}:${ts.slice(10, 12)}:00`
    );
}

function formatTime(date) {
    return date?.toLocaleTimeString("en-IN", { timeStyle: "short" }) ?? "";
}

function groupByDate(orders) {
    return orders.reduce((groups, order) => {
        const date = parseOrderDate(order.orderId);
        const key = date
            ? date.toLocaleDateString("en-IN", { dateStyle: "long" })
            : "Unknown date";
        return { ...groups, [key]: [...(groups[key] ?? []), order] };
    }, {});
}

function sumOrders(orders, field) {
    return orders.reduce((sum, o) => sum + Number(o[field] ?? 0), 0);
}

function computeStats(orders) {
    return {
        revenue: sumOrders(orders, "totalPrice"),
        orders: orders.length,
        discount: sumOrders(orders, "totalDiscount"),
    };
}

function getPaymentIcon(mode) {
    return PAYMENT_METHODS[mode]?.icon ?? "🧾";
}

function getPaymentLabel(mode) {
    return PAYMENT_METHODS[mode]?.label ?? mode;
}

function LoadingSpinner() {
    return (
        <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f4f6f8] flex items-center justify-center">
            <div className="flex flex-col items-center gap-3">
                <div className="w-9 h-9 rounded-full border-4 border-[#006E74]/20 border-t-[#006E74] animate-spin" />
                <p className="text-xs text-gray-400 font-medium">Loading orders…</p>
            </div>
        </div>
    );
}

function StatCard({ stat, value }) {
    const Icon = stat.icon;
    const displayValue = stat.key === "orders" ? value : formatCurrency(value);

    return (
        <div className="bg-white rounded-2xl px-5 py-4 border border-gray-100 shadow-sm flex items-center gap-4">
            <div
                className="p-2.5 rounded-xl shrink-0"
                style={{ backgroundColor: `${stat.color}18`, color: stat.color }}
            >
                <Icon size={20} strokeWidth={2.5} />
            </div>
            <div className="min-w-0">
                <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest truncate">
                    {stat.label}
                </p>
                <p className="text-base font-black mt-0.5 truncate" style={{ color: stat.color }}>
                    {displayValue}
                </p>
            </div>
        </div>
    );
}

StatCard.propTypes = {
    stat: PropTypes.shape({
        key: PropTypes.string,
        label: PropTypes.string,
        icon: PropTypes.elementType,
        color: PropTypes.string,
    }).isRequired,
    value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
};

function FilterBar({ search, onSearchChange, methodFilter, onMethodChange }) {
    return (
        <div className="flex gap-3 items-center flex-wrap">
            <div className="relative flex-1 min-w-[300px] max-w-sm">
                <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-xs select-none">
                    🔍
                </span>
                <input
                    type="text"
                    placeholder="Search by order ID, customer, product…"
                    value={search}
                    onChange={onSearchChange}
                    className="w-full pl-8 pr-4 py-2.5 border border-gray-200 rounded-xl text-xs bg-white outline-none focus:border-[#006E74] transition-colors"
                />
            </div>

            <div className="flex gap-2 flex-wrap">
                {FILTER_OPTIONS.map((mode) => {
                    const isActive = methodFilter === mode;
                    const label = mode === "ALL"
                        ? "All"
                        : `${PAYMENT_METHODS[mode].icon} ${PAYMENT_METHODS[mode].label}`;
                    return (
                        <button
                            key={mode}
                            onClick={() => onMethodChange(mode)}
                            className={`px-3.5 py-2 rounded-xl text-xs font-bold border transition-all cursor-pointer ${isActive
                                    ? "bg-[#006E74] text-white border-[#006E74]"
                                    : "bg-white text-gray-500 border-gray-200 hover:border-[#006E74]/40"
                                }`}
                        >
                            {label}
                        </button>
                    );
                })}
            </div>
        </div>
    );
}

FilterBar.propTypes = {
    search: PropTypes.string.isRequired,
    onSearchChange: PropTypes.func.isRequired,
    methodFilter: PropTypes.string.isRequired,
    onMethodChange: PropTypes.func.isRequired,
};

function EmptyState() {
    return (
        <div className="flex flex-col items-center justify-center py-20 gap-3 text-center">
            <div className="w-14 h-14 rounded-2xl bg-gray-100 flex items-center justify-center text-2xl">
                📭
            </div>
            <p className="text-sm font-semibold text-gray-500">No orders found</p>
            <p className="text-xs text-gray-400">Try adjusting your search or filter.</p>
        </div>
    );
}

function OrderLineItem({ entry, index }) {
    const qty = Math.floor(Number(entry.quantity ?? 0));
    const lineTotal = Number(entry.totalPrice ?? 0);
    const lineSave = Number(entry.discount ?? 0);
    const unitPrice = Number(entry.sellingPrice ?? (qty > 0 ? lineTotal / qty : 0));

    return (
        <div className="px-5 py-3 flex items-center gap-3">
            <div className="w-7 h-7 rounded-lg bg-[#006E74]/8 border border-[#006E74]/10 flex items-center justify-center shrink-0">
                <svg width="11" height="11" viewBox="0 0 24 24" fill="none" stroke="#006E74" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="3" y="3" width="18" height="18" rx="2" />
                    <circle cx="8.5" cy="8.5" r="1.5" />
                    <polyline points="21 15 16 10 5 21" />
                </svg>
            </div>
            <div className="flex-1 min-w-0">
                <p className="text-xs font-semibold text-[#231F20] truncate">{entry.product}</p>
                <p className="text-[10px] text-gray-400 font-mono mt-0.5">
                    {qty} × ₹{unitPrice.toFixed(2)}
                    {lineSave > 0 && (
                        <span className="ml-2 text-[#0097AC] font-semibold">
                            · saved ₹{lineSave.toFixed(2)}
                        </span>
                    )}
                </p>
            </div>
            <span className="text-xs font-bold text-[#231F20] shrink-0">
                ₹{lineTotal.toFixed(2)}
            </span>
        </div>
    );
}

OrderLineItem.propTypes = {
    entry: PropTypes.shape({
        quantity: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        discount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        sellingPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        product: PropTypes.string,
    }).isRequired,
    index: PropTypes.number,
};

function OrderCardFooter({ total, discount }) {
    const mrpSubtotal = total + discount;
    return (
        <div className="px-5 py-3 bg-gray-50/80 border-t border-gray-100 flex items-center justify-between gap-4">
            {discount > 0 && (
                <div className="flex items-center gap-3 text-xs text-gray-400 flex-wrap">
                    <span>
                        MRP {" "}
                        <span className="text-gray-600 font-semibold">
                            ₹{mrpSubtotal.toFixed(2)}
                        </span>
                    </span>
                    <span className="text-[#0097AC] font-semibold">
                        Discount ₹{discount.toFixed(2)}
                    </span>
                </div>
            )}
            <div className="flex items-center gap-2 ml-auto shrink-0">
                <span className="text-xs text-gray-500 font-semibold">Total paid:</span>
                <span className="text-sm font-black text-[#006E74]">₹{total.toFixed(2)}</span>
            </div>
        </div>
    );
}

function OrderCard({ order, isExpanded, onToggle }) {
    const router = useRouter();
    const total = Number(order.totalPrice ?? 0);
    const discount = Number(order.totalDiscount ?? 0);
    const entries = order.orderEntryDtoList ?? [];
    const totalItems = entries.reduce((s, e) => s + Math.floor(Number(e.quantity ?? 0)), 0);
    const date = parseOrderDate(order.orderId);

    const handleViewSummary = useCallback((e) => {
        e.stopPropagation();
        router.push(`/pos/orders/summary?orderId=${order.orderId}&customer=${order.identifier || ""}`);
    }, [router, order.orderId, order.identifier]);

    return (
        <div
            className={`bg-white rounded-2xl border transition-all duration-200 overflow-hidden ${isExpanded
                    ? "border-[#006E74]/40 shadow-sm shadow-[#006E74]/8"
                    : "border-gray-100 hover:border-gray-200 hover:shadow-sm"
                }`}
        >
            <div className="px-5 py-4 flex items-center justify-between gap-4 select-none">
                <button
                    type="button"
                    onClick={onToggle}
                    aria-expanded={isExpanded}
                    className="flex items-center gap-4 flex-1 min-w-0 text-left"
                >
                    <div
                        className={`w-10 h-10 rounded-xl flex items-center justify-center text-base shrink-0 transition-colors ${isExpanded ? "bg-[#006E74]/10" : "bg-gray-50"
                            }`}
                    >
                        {getPaymentIcon(order.paymentMode)}
                    </div>

                    <div className="flex-1 min-w-0">
                        <div className="flex items-center gap-2 flex-wrap">
                            <p className="text-xs font-black text-[#231F20] font-mono truncate">
                                {order.orderId}
                            </p>
                            <span className="text-[9px] font-bold text-[#006E74] bg-[#006E74]/10 px-2 py-0.5 rounded-full shrink-0">
                                {getPaymentLabel(order.paymentMode)}
                            </span>
                        </div>
                        <div className="flex items-center gap-2 mt-1 text-[10px] text-gray-400 flex-wrap">
                            <span className="font-mono">📱 {order.identifier || "—"}</span>
                            <span className="text-gray-200">·</span>
                            <span>{totalItems} item{totalItems === 1 ? "" : "s"}</span>
                            {date && (
                                <>
                                    <span className="text-gray-200">·</span>
                                    <span>{formatTime(date)}</span>
                                </>
                            )}
                        </div>
                    </div>
                </button>

                <div className="flex items-center gap-4 shrink-0">
                    <div className="text-right">
                        <p className="text-sm font-black text-[#006E74]">₹{total.toFixed(2)}</p>
                        {discount > 0 && (
                            <p className="text-[10px] text-[#0097AC] font-semibold">
                                −₹{discount.toFixed(2)} off
                            </p>
                        )}
                    </div>

                    <button
                        type="button"
                        onClick={handleViewSummary}
                        title="View Order Summary"
                        className="p-2 border border-gray-200 text-gray-400 bg-white hover:text-[#006E74] hover:border-[#006E74]/30 hover:bg-[#006E74]/5 rounded-xl transition-all cursor-pointer flex items-center justify-center"
                    >
                        <Eye size={14} strokeWidth={2.5} />
                    </button>

                    <svg
                        width="14" height="14" viewBox="0 0 24 24" fill="none"
                        stroke="#9CA3AF" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"
                        className={`transition-transform duration-200 shrink-0 ${isExpanded ? "rotate-180" : ""}`}
                        aria-hidden="true"
                    >
                        <polyline points="6 9 12 15 18 9" />
                    </svg>
                </div>
            </div>

            {isExpanded && (
                <div className="border-t border-gray-100">
                    <div className="divide-y divide-gray-50">
                        {entries.map((entry, idx) => {
                            const key = entry.orderEntryId ?? entry.id ?? `${order.orderId}-${idx}`;
                            return <OrderLineItem key={key} entry={entry} index={idx} />;
                        })}
                    </div>
                    <OrderCardFooter total={total} discount={discount} />
                </div>
            )}
        </div>
    );
}

OrderCard.propTypes = {
    order: PropTypes.shape({
        totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        totalDiscount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        orderEntryDtoList: PropTypes.array,
        orderId: PropTypes.string,
        paymentMode: PropTypes.string,
        identifier: PropTypes.string,
    }).isRequired,
    isExpanded: PropTypes.bool,
    onToggle: PropTypes.func.isRequired,
};

function DateGroup({ dateLabel, orders, expandedId, onToggle }) {
    return (
        <div className="flex flex-col gap-3">
            <div className="flex items-center gap-3">
                <span className="text-[10px] font-bold text-gray-400 uppercase tracking-widest whitespace-nowrap">
                    {dateLabel}
                </span>
                <div className="flex-1 h-px bg-gray-100" />
                <span className="text-[10px] font-semibold text-gray-400 shrink-0">
                    {orders.length} order{orders.length === 1 ? "" : "s"}
                </span>
            </div>

            {orders.map((order) => (
                <OrderCard
                    key={order.orderId}
                    order={order}
                    isExpanded={expandedId === order.orderId}
                    onToggle={() => onToggle(order.orderId)}
                />
            ))}
        </div>
    );
}

DateGroup.propTypes = {
    dateLabel: PropTypes.string.isRequired,
    orders: PropTypes.array.isRequired,
    expandedId: PropTypes.string,
    onToggle: PropTypes.func.isRequired,
};

export default function OrdersListPage() {
    const router = useRouter();

    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [search, setSearch] = useState("");
    const [methodFilter, setMethodFilter] = useState("ALL");
    const [expandedId, setExpandedId] = useState(null);

    useEffect(() => {
        let cancelled = false;

        async function fetchOrders() {
            try {
                setLoading(true);
                const res = await api.get("/order/list");
                if (!cancelled) setOrders(res.data ?? []);
            } catch (err) {
                if (!cancelled) setError(err?.message ?? "Failed to load orders.");
            } finally {
                if (!cancelled) setLoading(false);
            }
        }

        fetchOrders();
        return () => { cancelled = true; };
    }, []);

    const filtered = useMemo(() => {
        const q = search.trim().toLowerCase();
        return orders.filter((order) => {
            const matchesSearch =
                !q ||
                order.orderId?.toLowerCase().includes(q) ||
                order.identifier?.toLowerCase().includes(q) ||
                (order.orderEntryDtoList ?? []).some((e) => e.product?.toLowerCase().includes(q));
            const matchesMethod =
                methodFilter === "ALL" || order.paymentMode === methodFilter;
            return matchesSearch && matchesMethod;
        });
    }, [orders, search, methodFilter]);

    const grouped = useMemo(() => groupByDate(filtered), [filtered]);
    const stats = useMemo(() => computeStats(orders), [orders]);

    const handleSearchChange = useCallback((e) => setSearch(e.target.value), []);
    const handleMethodChange = useCallback((mode) => setMethodFilter(mode), []);
    const handleToggleExpand = useCallback((id) => {
        setExpandedId((prev) => (prev === id ? null : id));
    }, []);
    const handleNewOrder = useCallback(() => router.push("/pos/sales/create"), [router]);

    if (loading) return <LoadingSpinner />;

    return (
        <div className="flex h-full w-full bg-[#f4f6f8] font-sans overflow-hidden text-[#231F20]">
            <div className="flex-1 overflow-y-auto px-6 py-8 flex flex-col gap-6">
                <div className="flex items-center justify-between">
                    <div>
                        <h1 className="text-xl font-black text-[#231F20]">Orders</h1>
                        <p className="text-xs text-gray-400 mt-0.5">{stats.orders} orders total</p>
                    </div>
                    <button
                        onClick={handleNewOrder}
                        className="px-4 py-2 bg-[#006E74] text-white text-xs font-bold rounded-xl cursor-pointer hover:bg-[#0097AC] transition-colors"
                    >
                        + New order
                    </button>
                </div>

                <div className="grid grid-cols-3 gap-4">
                    {STAT_CONFIG.map((stat) => (
                        <StatCard key={stat.key} stat={stat} value={stats[stat.key]} />
                    ))}
                </div>

                <FilterBar
                    search={search}
                    onSearchChange={handleSearchChange}
                    methodFilter={methodFilter}
                    onMethodChange={handleMethodChange}
                />

                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-600 rounded-xl px-4 py-3 text-xs font-semibold">
                        ⚠️ {error}
                    </div>
                )}

                {filtered.length === 0 ? (
                    <EmptyState />
                ) : (
                    Object.entries(grouped).map(([dateLabel, dayOrders]) => (
                        <DateGroup
                            key={dateLabel}
                            dateLabel={dateLabel}
                            orders={dayOrders}
                            expandedId={expandedId}
                            onToggle={handleToggleExpand}
                        />
                    ))
                )}
                <div className="pb-4" />
            </div>
        </div>
    );
}


OrderCardFooter.propTypes = {
    total: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
    discount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
};