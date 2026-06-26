"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import api from "@/app/api/axios";
import Image from "next/image";
import PropTypes from "prop-types";
import KushalQRCode from "../../assets/images/Kushal_UPI_QR.png";

const PAYMENT_METHODS = [
    { id: "UPI", label: "UPI", icon: "📲", desc: "GPay, PhonePe, Paytm" },
    { id: "CARD", label: "Card", icon: "💳", desc: "Debit / Credit card" },
    { id: "CASH", label: "Cash", icon: "💵", desc: "Collect from customer" },
];

export default function PaymentModal({ cart, customer, onClose }) {
    const router = useRouter();

    const [method, setMethod] = useState("UPI");
    const [step, setStep] = useState("select");
    const [cashCollected, setCashCollected] = useState(false);
    const [cardSwiped, setCardSwiped] = useState(false);
    const [error, setError] = useState("");
    const [placedOrder, setPlacedOrder] = useState(null);

    const totalAmount = Number(cart?.totalPrice || 0);
    const totalDiscount = Number(cart?.totalDiscount || 0);
    const entries = cart?.cartEntryDtoList || [];

    useEffect(() => {
        if (step === "pay" && method === "CARD") {
            setCardSwiped(false);
            const t = setTimeout(() => setCardSwiped(true), 2200);
            return () => clearTimeout(t);
        }
    }, [step, method]);

    async function handleConfirmPayment() {
        setStep("processing");
        setError("");
        try {
            const res = await api.post("/order/create", {
                identifier: customer.identifier,
                paymentMode: method,
            });
            const order = res.data;
            setPlacedOrder(order);
            setStep("done");

            setTimeout(() => {
                router.push(`/pos/orders/summary?orderId=${order.orderId}&customer=${customer.identifier}`);
            }, 1400);
        } catch {
            setError("Order creation failed. Please try again.");
            setStep("pay");
        }
    }

    const canConfirm =
        (method === "CASH" && cashCollected) ||
        (method === "CARD" && cardSwiped) ||
        method === "UPI";

    if (step === "processing" || step === "done") {
        return (
            <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-[10000]">
                <div className="bg-white rounded-3xl shadow-2xl w-full max-w-xs mx-4 px-8 py-12 flex flex-col items-center gap-5 text-center">
                    {step === "processing" ? (
                        <>
                            <div className="w-16 h-16 rounded-full border-4 border-[#006E74]/20 border-t-[#006E74] animate-spin" />
                            <p className="text-sm font-bold text-[#231F20]">Processing payment…</p>
                            <p className="text-xs text-gray-400">Placing your order, please wait.</p>
                        </>
                    ) : (
                        <>
                            <div className="w-16 h-16 rounded-full bg-[#006E74] flex items-center justify-center shadow-lg shadow-[#006E74]/30">
                                <svg width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2.8" strokeLinecap="round" strokeLinejoin="round">
                                    <polyline points="20 6 9 17 4 12" />
                                </svg>
                            </div>
                            <div>
                                <p className="text-sm font-black text-[#006E74]">Payment successful!</p>
                                {placedOrder?.orderId && (
                                    <p className="text-[10px] font-mono text-gray-400 mt-1">{placedOrder.orderId}</p>
                                )}
                            </div>
                            <p className="text-xs text-gray-400">Redirecting to order summary…</p>
                        </>
                    )}
                </div>
            </div>
        );
    }

    return (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-[10000]">
            <div className="bg-white rounded-3xl shadow-2xl w-full max-w-md mx-4 overflow-hidden border border-gray-100 flex flex-col max-h-[92vh]">

                {/* Header */}
                <div className="px-6 pt-5 pb-4 border-b border-gray-100 flex items-start justify-between shrink-0">
                    <div>
                        <h2 className="text-xs font-bold text-gray-400 uppercase tracking-widest">Payment</h2>
                        <p className="text-sm font-bold text-[#231F20] mt-0.5">
                            {customer?.customerName || "Walk-In Customer"}
                        </p>
                        <p className="text-[10px] font-mono text-gray-400">{customer?.identifier}</p>
                    </div>
                    <button
                        onClick={onClose}
                        className="w-8 h-8 rounded-full bg-gray-100 hover:bg-gray-200 flex items-center justify-center text-gray-500 text-xs font-bold border-none cursor-pointer transition-colors mt-1"
                    >
                        ✕
                    </button>
                </div>

                <div className="flex-1 overflow-y-auto px-6 py-5 flex flex-col gap-5">

                    <div className="bg-[#006E74] rounded-2xl px-5 py-4 text-white flex items-center justify-between">
                        <div>
                            <p className="text-[10px] font-semibold text-white/60 uppercase tracking-widest">Total payable</p>
                            <p className="text-3xl font-black mt-0.5">₹{totalAmount.toFixed(2)}</p>
                            {totalDiscount > 0 && (
                                <p className="text-[10px] text-[#0097AC] mt-1 font-semibold">
                                    Savings: ₹{totalDiscount.toFixed(2)}
                                </p>
                            )}
                        </div>
                        <div className="text-right flex flex-col items-end gap-1 max-h-24 overflow-y-auto">
                            {entries.map((e) => (
                                <span key={`${e.product}-${e.quantity}`} className="text-[10px] text-white/50 whitespace-nowrap">
                                    {e.product} × {Math.floor(e.quantity)}
                                </span>
                            ))}
                        </div>
                    </div>

                    {step === "select" && (
                        <>
                            <div>
                                <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest mb-3">
                                    Select payment method
                                </p>
                                <div className="grid grid-cols-3 gap-3">
                                    {PAYMENT_METHODS.map((m) => (
                                        <button
                                            key={m.id}
                                            onClick={() => setMethod(m.id)}
                                            className={`flex flex-col items-center gap-2 py-5 rounded-2xl border-2 transition-all cursor-pointer font-sans ${method === m.id
                                                    ? "border-[#006E74] bg-[#006E74]/8 text-[#006E74]"
                                                    : "border-gray-200 bg-white text-gray-400 hover:border-[#0097AC]/40"
                                                }`}
                                        >
                                            <span className="text-2xl">{m.icon}</span>
                                            <span className="text-xs font-bold">{m.label}</span>
                                            <span className="text-[9px] text-gray-400 px-1 text-center leading-tight">{m.desc}</span>
                                        </button>
                                    ))}
                                </div>
                            </div>

                            <button
                                onClick={() => setStep("pay")}
                                className="w-full py-3.5 bg-[#006E74] text-white font-bold rounded-2xl border-none cursor-pointer hover:bg-[#0097AC] transition-colors text-sm"
                            >
                                Continue with {method} →
                            </button>
                        </>
                    )}

                    {step === "pay" && (
                        <>
                            {method === "UPI" && (
                                <div className="flex flex-col items-center gap-4">
                                    <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Scan & Pay</p>

                                    <div className="relative w-52 h-52 p-3 bg-white border border-gray-100 rounded-2xl shadow-sm overflow-hidden flex items-center justify-center">

                                        <Image
                                            src={KushalQRCode}
                                            alt="UPI Payment QR Code"
                                            className="w-full h-full object-contain"
                                            priority
                                        />

                                        <div className="absolute inset-3 overflow-hidden rounded-xl pointer-events-none">
                                            <div className="absolute left-0 right-0 h-0.5 bg-gradient-to-r from-transparent via-[#006E74] to-transparent opacity-60"
                                                style={{ animation: "qrscan 2s ease-in-out infinite" }} />
                                        </div>
                                    </div>

                                    <style>{`
                                        @keyframes qrscan {
                                            0%   { top: 0%;   }
                                            50%  { top: 100%; }
                                            100% { top: 0%;   }
                                        }
                                    `}</style>

                                    <div className="text-center">
                                        <p className="text-lg font-black text-[#006E74]">₹{totalAmount.toFixed(2)}</p>
                                        <p className="text-xs text-gray-400 mt-1">
                                            Ask customer to scan using GPay / PhonePe / Paytm.<br />
                                            Tap <span className="font-semibold text-gray-500">Confirm payment</span> once received.
                                        </p>
                                    </div>
                                </div>
                            )}

                            {method === "CARD" && (
                                <div className="flex flex-col items-center gap-5">
                                    <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Swipe / Tap card</p>

                                    <div className="relative w-full h-44 flex items-center justify-center select-none">
                                        <div className="relative z-10 w-28 h-44 bg-[#1a1a1a] rounded-2xl flex flex-col items-center justify-between py-5 shadow-2xl border border-gray-800">
                                            <div className="w-16 h-9 bg-[#006E74]/20 rounded-lg border border-[#006E74]/30 flex items-center justify-center">
                                                <span className={`text-[8px] font-bold transition-colors duration-700 ${cardSwiped ? "text-[#0097AC]" : "text-gray-600"}`}>
                                                    {cardSwiped ? "APPROVED ✓" : "READY"}
                                                </span>
                                            </div>
                                            <div className="w-20 h-1 bg-gray-700 rounded-full" />
                                            <div className={`w-3 h-3 rounded-full transition-colors duration-700 shadow-sm ${cardSwiped ? "bg-[#006E74] shadow-[#006E74]/50" : "bg-gray-600"}`} />
                                        </div>

                                        <div
                                            className="absolute z-0 transition-all duration-[1800ms] ease-in-out"
                                            style={{
                                                right: cardSwiped ? "calc(50% + 44px)" : "calc(100% + 8px)",
                                                top: "50%",
                                                transform: "translateY(-50%)",
                                            }}
                                        >
                                            <div className="w-24 h-14 rounded-xl shadow-lg bg-gradient-to-br from-[#006E74] to-[#004f56] flex flex-col justify-between p-2 border border-[#0097AC]/20">
                                                <div className="w-7 h-5 bg-amber-400/90 rounded-sm" />
                                                <div className="flex gap-1 items-center">
                                                    {[0, 1, 2, 3].map(i => (
                                                        <div key={i} className="flex gap-0.5">
                                                            {[0, 1, 2, 3].map(j => <div key={j} className="w-1 h-1 rounded-full bg-white/40" />)}
                                                        </div>
                                                    ))}
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="text-center">
                                        <p className="text-lg font-black text-[#006E74]">₹{totalAmount.toFixed(2)}</p>
                                        <p className={`text-xs mt-1 font-semibold transition-colors duration-500 ${cardSwiped ? "text-[#006E74]" : "text-gray-400"}`}>
                                            {cardSwiped ? "✓ Card read — confirm to complete" : "Swipe or tap card on terminal…"}
                                        </p>
                                    </div>
                                </div>
                            )}

                            {method === "CASH" && (
                                <div className="flex flex-col items-center gap-4">
                                    <p className="text-[10px] font-bold text-gray-400 uppercase tracking-widest">Cash payment</p>

                                    <div className="w-full bg-[#006E74]/5 border border-[#006E74]/15 rounded-2xl p-6 flex flex-col items-center gap-3">
                                        <span className="text-5xl">💵</span>
                                        <p className="text-2xl font-black text-[#006E74]">₹{totalAmount.toFixed(2)}</p>
                                        <p className="text-xs text-gray-400 text-center leading-relaxed">
                                            Collect cash from the customer,<br />then mark as collected below.
                                        </p>
                                    </div>

                                    <button
                                        onClick={() => setCashCollected(true)}
                                        disabled={cashCollected}
                                        className={`w-full py-3.5 rounded-2xl font-bold text-sm border-2 transition-all cursor-pointer ${cashCollected
                                                ? "bg-[#006E74] text-white border-[#006E74] cursor-default shadow-md shadow-[#006E74]/20"
                                                : "bg-white text-[#006E74] border-[#006E74] hover:bg-[#006E74]/5"
                                            }`}
                                    >
                                        {cashCollected ? "✓ Cash collected" : "Mark cash as collected"}
                                    </button>
                                </div>
                            )}

                            {error && (
                                <div className="bg-red-50 border border-red-200 text-red-600 rounded-xl px-4 py-2.5 text-xs font-semibold text-center">
                                    ⚠️ {error}
                                </div>
                            )}

                            <div className="flex gap-3 pt-1">
                                <button
                                    onClick={() => {
                                        setStep("select");
                                        setCashCollected(false);
                                        setCardSwiped(false);
                                        setError("");
                                    }}
                                    className="flex-1 py-3 bg-gray-100 text-gray-600 font-semibold rounded-2xl text-sm border-none cursor-pointer hover:bg-gray-200 transition-colors"
                                >
                                    ← Change method
                                </button>
                                <button
                                    onClick={handleConfirmPayment}
                                    disabled={!canConfirm}
                                    className={`flex-1 py-3 font-bold rounded-2xl text-sm border-none transition-all ${canConfirm
                                            ? "bg-[#006E74] text-white cursor-pointer hover:bg-[#0097AC]"
                                            : "bg-gray-200 text-gray-400 cursor-not-allowed"
                                        }`}
                                >
                                    Confirm payment
                                </button>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

PaymentModal.propTypes = {
    cart: PropTypes.shape({
        totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        totalDiscount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        cartEntryDtoList: PropTypes.arrayOf(
            PropTypes.shape({
                product: PropTypes.string,
                quantity: PropTypes.number,
            })
        ),
    }),
    customer: PropTypes.shape({
        identifier: PropTypes.string,
        customerName: PropTypes.string,
    }),
    onClose: PropTypes.func.isRequired,
};