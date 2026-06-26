// /app/pos/orders/summary/page.jsx

"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import api from "@/app/api/axios";
import { POSReceipt, PrintStyles } from "../../../../components/payments/posrecipt";

const METHOD_LABELS = { UPI: "UPI", CARD: "Card", CASH: "Cash" };
const METHOD_ICONS = { UPI: "📲", CARD: "💳", CASH: "💵" };

export default function OrderSummaryPage() {
    const router = useRouter();
    const searchParams = useSearchParams();

    const orderId = searchParams?.get("orderId");
    const customerIdentifier = searchParams?.get("customer");

    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        async function fetchOrder() {
            if (!orderId) { setError("No order ID found."); setLoading(false); return; }
            try {
                setLoading(true);
                const res = await api.get("/order/list");
                const found = (res.data || []).find((o) => o.orderId === orderId);
                if (!found) throw new Error("Order not found.");
                setOrder(found);
            } catch (err) {
                setError(err.message || "Could not load order details.");
            } finally {
                setLoading(false);
            }
        }
        fetchOrder();
    }, [orderId]);

    if (loading) {
        return (
            <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f4f6f8] flex items-center justify-center">
                <div className="flex flex-col items-center gap-4">
                    <div className="w-10 h-10 rounded-full border-4 border-[#006E74]/20 border-t-[#006E74] animate-spin" />
                    <p className="text-sm text-gray-400 font-medium">Loading order…</p>
                </div>
            </div>
        );
    }

    if (error || !order) {
        return (
            <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-[#f4f6f8] flex items-center justify-center">
                <div className="flex flex-col items-center gap-4 text-center px-8">
                    <span className="text-4xl">⚠️</span>
                    <p className="text-sm font-semibold text-gray-500">{error || "Order not found."}</p>
                    <button type="button" onClick={() => router.push("/pos")} className="px-5 py-2.5 bg-[#006E74] text-white text-sm font-semibold rounded-xl border-none cursor-pointer hover:bg-[#0097AC] transition-colors">
                        ← Back to terminal
                    </button>
                </div>
            </div>
        );
    }

    const totalAmount = Number(order.totalPrice || 0);
    const totalDiscount = Number(order.totalDiscount || 0);
    const subtotal = totalAmount + totalDiscount;
    const entries = order.orderEntryDtoList || [];
    const paymentMode = order.paymentMode || "—";
    const customerPhone = order.identifier || customerIdentifier || "—";

    const orderIdParts = order.orderId?.split("-") || [];
    const ts = orderIdParts[orderIdParts.length - 1] || "";
    let formattedDate = "";
    if (ts.length === 14) {
        formattedDate = `${ts.slice(6, 8)}/${ts.slice(4, 6)}/${ts.slice(0, 4)} at ${ts.slice(8, 10)}:${ts.slice(10, 12)}`;
    }

    return (
        <div className="flex-1 h-full w-full bg-[#f4f6f8] overflow-y-auto font-sans text-[#231F20]">

            <PrintStyles />

            <POSReceipt order={order} storeName="My Store" />

            <div className="max-w-xl mx-auto px-5 py-8 flex flex-col gap-5">

                <div className="flex items-center justify-between">
                    <button
                        type="button"
                        onClick={() => router.push("/pos/sales/create")}
                        className="flex items-center gap-1.5 text-xs font-semibold text-[#006E74] border border-[#006E74] px-3 py-2 rounded-lg hover:bg-[#006E74]/5 transition-colors cursor-pointer bg-white"
                    >
                        ← New order
                    </button>
                    <button
                        type="button"
                        onClick={() => globalThis.print?.()}
                        className="flex items-center gap-1.5 text-xs font-semibold text-gray-500 border border-gray-200 px-3 py-2 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer bg-white"
                    >
                        🖨️ Print receipt
                    </button>
                </div>

                <div className="bg-[#006E74] rounded-3xl px-6 py-6 flex items-center gap-4 text-white shadow-lg shadow-[#006E74]/25">
                    <div className="w-14 h-14 rounded-2xl bg-white/15 flex items-center justify-center shrink-0">
                        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                            <polyline points="20 6 9 17 4 12" />
                        </svg>
                    </div>
                    <div className="flex-1 min-w-0">
                        <p className="text-[10px] font-bold text-white/50 uppercase tracking-widest">Order confirmed</p>
                        <h1 className="text-base font-black mt-0.5 font-mono truncate">{order.orderId}</h1>
                        {formattedDate && <p className="text-[10px] text-white/50 mt-1">{formattedDate}</p>}
                    </div>
                    <div className="text-right shrink-0">
                        <p className="text-[10px] text-white/50">Total paid</p>
                        <p className="text-2xl font-black">₹{totalAmount.toFixed(2)}</p>
                    </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <div className="bg-white rounded-2xl px-5 py-4 border border-gray-100 flex flex-col gap-1.5">
                        <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Customer</p>
                        <div className="flex items-center gap-2 mt-0.5">
                            <div className="w-7 h-7 rounded-full bg-[#006E74] flex items-center justify-center text-white text-xs font-bold shrink-0">
                                {customerPhone.slice(0, 2)}
                            </div>
                            <p className="text-xs font-bold text-[#231F20] font-mono">{customerPhone}</p>
                        </div>
                    </div>
                    <div className="bg-white rounded-2xl px-5 py-4 border border-gray-100 flex flex-col gap-1.5">
                        <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Payment</p>
                        <div className="flex items-center gap-2 mt-0.5">
                            <span className="text-lg">{METHOD_ICONS[paymentMode] || "💳"}</span>
                            <span className="text-sm font-bold text-[#231F20]">{METHOD_LABELS[paymentMode] || paymentMode}</span>
                        </div>
                        <span className="inline-flex w-fit items-center gap-1 text-[10px] font-bold text-[#006E74] bg-[#006E74]/10 px-2 py-0.5 rounded-full">
                            ✓ Paid
                        </span>
                    </div>
                </div>

                <div className="bg-white rounded-2xl border border-gray-100 overflow-hidden">
                    <div className="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
                        <h2 className="text-xs font-bold text-gray-500 uppercase tracking-widest">Items ordered</h2>
                        <span className="text-xs font-semibold text-gray-400 bg-gray-100 px-2.5 py-1 rounded-full">
                            {entries.length} line{entries.length === 1 ? "" : "s"}
                        </span>
                    </div>
                    <div className="divide-y divide-gray-50">
                        {entries.map((entry, idx) => {
                            const lineTotal = Number(entry.totalPrice || 0);
                            const lineSave = Number(entry.discount || 0);
                            const qty = Math.floor(Number(entry.quantity || 0));
                            const unitPrice = Number(entry.sellingPrice || (qty > 0 ? lineTotal / qty : 0));
                            const key = entry.orderEntryId ?? entry.id ?? `${entry.product || 'item'}-${idx}`;
                            return (
                                <div key={key} className="px-5 py-4 flex items-center gap-3">
                                    <div className="w-10 h-10 rounded-xl bg-[#006E74]/8 border border-[#006E74]/10 flex items-center justify-center shrink-0">
                                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="#006E74" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                                            <rect x="3" y="3" width="18" height="18" rx="2" /><circle cx="8.5" cy="8.5" r="1.5" /><polyline points="21 15 16 10 5 21" />
                                        </svg>
                                    </div>
                                    <div className="flex-1 min-w-0">
                                        <p className="text-xs font-bold text-[#231F20] truncate">{entry.product}</p>
                                        <p className="text-[10px] text-gray-400 mt-0.5 font-mono">{qty} × ₹{unitPrice.toFixed(2)}</p>
                                        {lineSave > 0 && <p className="text-[10px] text-[#0097AC] font-semibold mt-0.5">Saved ₹{lineSave.toFixed(2)}</p>}
                                    </div>
                                    <span className="text-sm font-bold text-[#231F20] shrink-0">₹{lineTotal.toFixed(2)}</span>
                                </div>
                            );
                        })}
                    </div>
                    <div className="px-5 py-4 border-t border-gray-100 bg-gray-50 flex flex-col gap-2">
                        {totalDiscount > 0 && (
                            <>
                                <div className="flex justify-between text-xs text-gray-500"><span>Subtotal (MRP)</span><span>₹{subtotal.toFixed(2)}</span></div>
                                <div className="flex justify-between text-xs text-[#0097AC] font-semibold"><span>Total savings</span><span>− ₹{totalDiscount.toFixed(2)}</span></div>
                            </>
                        )}
                        <div className="flex justify-between items-baseline pt-2 border-t border-gray-200 mt-1">
                            <span className="text-sm font-bold text-[#231F20]">Total paid</span>
                            <span className="text-xl font-black text-[#006E74]">₹{totalAmount.toFixed(2)}</span>
                        </div>
                    </div>
                </div>

                <div className="flex gap-3 pb-6">
                    <button onClick={() => router.push("/pos/orders")} className="flex-1 py-3 bg-white text-[#006E74] border border-[#006E74] font-semibold rounded-2xl text-sm cursor-pointer hover:bg-[#006E74]/5 transition-colors">
                        All orders
                    </button>
                    <button onClick={() => router.push("/pos/sales/create")} className="flex-1 py-3 bg-[#006E74] text-white font-bold rounded-2xl text-sm border-none cursor-pointer hover:bg-[#0097AC] transition-colors">
                        New order →
                    </button>
                </div>
            </div>
        </div>
    );
}