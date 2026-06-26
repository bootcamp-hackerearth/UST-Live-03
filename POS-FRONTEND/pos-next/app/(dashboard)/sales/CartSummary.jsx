"use client";

import PropTypes from "prop-types";
import React, { useState } from "react";
import PaymentModal from "./components/PaymentModal";  

export default function CartSummary({
    cart,
    onRemoveItem,
    refreshCart,
    loading,
    selectedCustomer, 
    onOrderComplete
}) {
    const [isPaymentOpen, setIsPaymentOpen] = useState(false);
    const [isSaving, setIsSaving] = useState(false);

    const cartEntries = cart?.cartEntryDtoList || [];
    const totalAmount = Number(cart?.totalPrice ?? cart?.total_price ?? 0);

    const handleOpenPayment = () => {
        if (!selectedCustomer) {
            alert("Please select a customer profile first!");
            return;
        }
        if (cartEntries.length === 0) {
            alert("Your active cart is empty!");
            return;
        }
        setIsPaymentOpen(true);
    };

    const handleRemove = async (entry) => {
        try {
            await onRemoveItem(entry);
            await refreshCart();
        } catch (error) {
            console.error("Failed to remove item:", error);
        }
    };

    const val = (entry, camel, snake) =>
        entry[camel] ?? entry[snake] ?? 0;

    const handleFinalCheckout = async (paymentDetails) => {
        setIsSaving(true);
        try {
            const response = await fetch("/api/checkout", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    payload: {
                        cartId: cart?.identifier,
                        customerIdentifier: selectedCustomer.identifier,
                        paymentMethod: paymentDetails.paymentMethod,
                        receivedAmount: paymentDetails.receivedAmount,
                    }
                })
            });

            const data = await response.json();
            if (!response.ok) throw new Error(data.message || "Failed to process order.");
            
            alert(`Order placed successfully!\nReference ID: ${data.identifier}`);
            setIsPaymentOpen(false);
            
            if (onOrderComplete) {
                onOrderComplete(selectedCustomer.identifier, data);
            }
        } catch (err) {
            alert(err.message);
        } finally {
            setIsSaving(false);
        }
    };

    return (
        <div className="h-full flex flex-col bg-[#faf8f6]">

            <div className="flex-1 overflow-auto p-4">
                {cartEntries.length === 0 ? (
                    <div className="h-full flex flex-col items-center justify-center text-slate-500">
                        <div className="w-20 h-20 rounded-full bg-orange-50 flex items-center justify-center mb-4">
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                fill="none"
                                viewBox="0 0 24 24"
                                strokeWidth={1.8}
                                stroke="currentColor"
                                className="w-10 h-10 text-orange-500"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    d="M2.25 3h1.386c.51 0 .955.343 1.087.835l.383 1.437M7.5 14.25a3 3 0 0 0-3 3h15.75m-12.75-3h11.218c1.121-2.3 2.1-4.684 2.924-7.138a60.114 60.114 0 0 0-16.536-1.84M7.5 14.25 5.106 5.272"
                                />
                            </svg>
                        </div>
                        <p className="font-semibold text-slate-700">No Products Added</p>
                        <p className="text-sm text-slate-400 mt-1">Start adding items from the catalog</p>
                    </div>
                ) : (
                    <div className="bg-white rounded-3xl border border-slate-200 overflow-hidden shadow-sm">
                        <table className="w-full text-sm">
                            <thead className="bg-[#f8f6f3] border-b border-slate-200">
                                <tr>
                                    <th className="px-4 py-4 text-left text-[11px] uppercase tracking-wider text-slate-500 font-bold">Item</th>
                                    <th className="px-4 py-4 text-right text-[11px] uppercase tracking-wider text-slate-500 font-bold">Unit Price</th>
                                    <th className="px-4 py-4 text-center text-[11px] uppercase tracking-wider text-slate-500 font-bold">Discount</th>
                                    <th className="px-4 py-4 text-center text-[11px] uppercase tracking-wider text-slate-500 font-bold">Qty</th>
                                    <th className="px-4 py-4 text-right text-[11px] uppercase tracking-wider text-slate-500 font-bold">Total</th>
                                    <th className="px-4 py-4 text-center text-[11px] uppercase tracking-wider bg-red-600 text-white font-bold">Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                {cartEntries.map((entry) => {
                                    const unitPrice = val(entry, "originalPrice", "original_price");
                                    const totalPrice = val(entry, "totalPrice", "total_price");
                                    const unitDiscount = val(entry, "discount", "discount");

                                    return (
                                        <tr
                                            key={entry.identifier}
                                            className="border-b border-slate-100 hover:bg-orange-50/40 transition"
                                        >
                                            <td className="px-4 py-4">
                                                <div>
                                                    <p className="font-semibold text-slate-800">{entry.product}</p>
                                                    <p className="text-xs text-slate-400">Product</p>
                                                </div>
                                            </td>
                                            <td className="px-4 py-4 text-right font-semibold text-slate-700">
                                                ₹{Number(unitPrice).toFixed(2)}
                                            </td>
                                            <td className="px-4 py-4 text-center">
                                                <span className="inline-flex items-center rounded-full bg-green-100 text-green-700 px-3 py-1 text-xs font-semibold">
                                                    ₹{Number(unitDiscount).toFixed(2)}
                                                </span>
                                            </td>
                                            <td className="px-4 py-4 text-center">
                                                <div className="inline-flex items-center justify-center w-8 h-8 rounded-full bg-orange-100 text-orange-700 font-bold">
                                                    {entry.quantity}
                                                </div>
                                            </td>
                                            <td className="px-4 py-4 text-right font-bold text-slate-900">
                                                ₹{Number(totalPrice).toFixed(2)}
                                            </td>
                                            <td className="px-4 py-4 text-center">
                                                <button
                                                    onClick={() => handleRemove(entry)}
                                                    disabled={loading || isSaving}
                                                    className="w-8 h-8 rounded-full bg-red-600 hover:bg-red-700 text-white font-bold transition disabled:opacity-40"
                                                >
                                                    ×
                                                </button>
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            <div className="border-t border-slate-200 bg-white p-5">
                <div className="bg-[#faf8f6] rounded-3xl border border-slate-200 p-5">
                    <div className="space-y-3">
                        <div className="flex justify-between text-sm">
                            <span className="text-slate-500">Sub Total</span>
                            <span className="font-semibold text-slate-800">
                                ₹{Number(cart?.originalPrice ?? cart?.original_price ?? 0).toFixed(2)}
                            </span>
                        </div>

                        <div className="flex justify-between text-sm">
                            <span className="text-slate-500">Discount</span>
                            <span className={`font-semibold ${Number(cart?.discount || 0) > 0 ? "text-green-500" : "text-slate-500"}`}>
                                - ₹{Number(cart?.discount || 0).toFixed(2)}
                            </span>
                        </div>

                        <div className="border-t border-slate-200 pt-4">
                            <div className="flex justify-between items-center">
                                <span className="font-bold text-slate-900 text-lg">Order Total</span>
                                <span className="text-3xl font-bold text-slate-900">
                                    ₹{totalAmount.toFixed(2)}
                                </span>
                            </div>
                        </div>
                    </div>

                    <button
                        onClick={handleOpenPayment}
                        disabled={cartEntries.length === 0 || loading || isSaving}
                        className="w-full mt-5 rounded-full bg-[#d85b1a] hover:bg-[#c24f14] text-white py-4 font-bold shadow-sm transition disabled:opacity-40 disabled:pointer-events-none"
                    >
                        {isSaving ? "Processing Order..." : "Complete & Finalize Sale"}
                    </button>
                </div>
            </div>

            <PaymentModal 
                isOpen={isPaymentOpen}
                onClose={() => setIsPaymentOpen(false)}
                totalAmount={totalAmount}
                onConfirm={handleFinalCheckout}
                loading={isSaving}
            />
        </div>
    );
}

CartSummary.propTypes = {
    cart: PropTypes.object,
    onRemoveItem: PropTypes.func.isRequired,
    refreshCart: PropTypes.func.isRequired,
    loading: PropTypes.bool,
    selectedCustomer: PropTypes.object,
    onOrderComplete: PropTypes.func
};