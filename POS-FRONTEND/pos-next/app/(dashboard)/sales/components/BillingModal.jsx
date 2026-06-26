"use client";

import React from "react";
import PropTypes from "prop-types";

export default function BillingModal({ order, onClose }) {
    if (!order) return null;

    const handlePrint = () => { globalThis.print();};

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4 print:p-0 print:bg-white animate-fade-in">
            <div className="bg-white w-full max-w-md rounded-3xl shadow-xl border border-slate-200 overflow-hidden flex flex-col max-h-[90vh] print:max-h-full print:shadow-none print:border-none print:w-full">

                <div className="px-6 py-4 bg-slate-50 border-b border-slate-200 flex justify-between items-center print:hidden">
                    <h3 className="text-lg font-bold text-slate-900">Transaction Bill</h3>
                    <button
                        onClick={onClose}
                        className="text-slate-600 hover:text-red-600 transition text-sm p-1"
                    >
                        ✕ Close
                    </button>
                </div>

                <div className="p-6 overflow-y-auto flex-1 font-mono text-sm text-slate-800 space-y-4 print:overflow-visible">

                    <div className="text-center space-y-1 pb-4 border-b border-dashed border-slate-300">
                        <h1 className="text-xl font-black uppercase tracking-wider text-black">COSTCO</h1>
                        <p className="text-xs text-slate-500">Sales Invoice Receipt</p>
                    </div>

                    <div className="space-y-1 text-xs text-slate-600 pb-4 border-b border-dashed border-slate-300">
                        <div className="flex justify-between">
                            <span>Order ID:</span>
                            <span className="font-semibold text-slate-900">{order.identifier || order.id}</span>
                        </div>
                        <div className="flex justify-between">
                            <span>Customer ID:</span>
                            <span className="font-semibold text-slate-900">{order.customerIdentifier}</span>
                        </div>
                        <div className="flex justify-between">
                            <span>Date:</span>
                            <span className="font-semibold text-slate-900">
                                {order.createdOn ? new Date(order.createdOn).toLocaleString() : new Date().toLocaleString()}
                            </span>
                        </div>
                        <div className="flex justify-between">
                            <span>Payment Mode:</span>
                            <span className="font-semibold text-slate-900 uppercase">{order.paymentMethod || "CASH"}</span>
                        </div>
                    </div>

                    <div className="space-y-2 py-2 text-xs">
                        <div className="flex justify-between font-bold text-slate-900 uppercase border-b border-slate-200 pb-1">
                            <span>Item / Qty</span>
                            <span>Total</span>
                        </div>

                        {order.cartEntryDtoList?.map((entry, idx) => {
                            const qty = entry.quantity || 1;
                            const baseUnitPrice = (entry.originalPrice || 0) / qty;
                            const discountedUnitPrice = entry.unitPrice || 0;
                            const originalRowTotal = entry.originalPrice || 0;
                            const finalRowTotal = entry.totalPrice || 0;
                            const itemKey = entry.id || `order-entry-${entry.product || entry.productName}-${idx}`;

                            return (
                                <div key={itemKey} className="border-b border-slate-100 py-2 space-y-1">
                                    <div className="flex justify-between items-center font-medium text-slate-900">
                                        <span>{entry.product || entry.productName}</span>
                                        <span>${finalRowTotal.toFixed(2)}</span>
                                    </div>

                                    <div className="flex justify-between text-[11px] text-slate-500 font-mono">
                                        <div>
                                            <span>Base: ${baseUnitPrice.toFixed(2)} x {qty}</span>
                                            {entry.discount > 0 && (
                                                <span className="ml-2 text-emerald-600">
                                                    (Promo Unit: ${discountedUnitPrice.toFixed(2)})
                                                </span>
                                            )}
                                        </div>

                                        <div className="space-x-1.5">
                                            {entry.discount > 0 && (
                                                <span className="line-through text-slate-400">
                                                    ${originalRowTotal.toFixed(2)}
                                                </span>
                                            )}
                                            <span className="text-slate-700 font-semibold">
                                                Saved: -${(entry.discount || 0).toFixed(2)}
                                            </span>
                                        </div>
                                    </div>
                                </div>
                            );
                        })}

                        {(!order.cartEntryDtoList || order.cartEntryDtoList.length === 0) && (
                            <p className="text-slate-400 italic text-center py-2">No itemized lines present.</p>
                        )}
                    </div>

                    <div className="pt-4 border-t border-dashed border-slate-300 space-y-1.5 text-xs">
                        <div className="flex justify-between text-slate-600">
                            <span>Gross Subtotal:</span>
                            <span>${order.originalPrice?.toFixed(2) || "0.00"}</span>
                        </div>
                        {order.discount > 0 && (
                            <div className="flex justify-between text-emerald-600 font-medium">
                                <span>Applied Discount:</span>
                                <span>-${order.discount?.toFixed(2)}</span>
                            </div>
                        )}
                        <div className="flex justify-between text-base font-black text-slate-900 pt-1 border-t border-slate-200">
                            <span>GRAND TOTAL:</span>
                            <span>${((order.originalPrice || 0) - (order.discount || 0)).toFixed(2)}</span>
                        </div>

                        <div className="flex justify-between text-slate-600 pt-2">
                            <span>Amount Received:</span>
                            <span>${order.receivedAmount?.toFixed(2) || order.recievedAmount?.toFixed(2) || "0.00"}</span>
                        </div>
                        <div className="flex justify-between text-slate-600">
                            <span>Change/Balance Due:</span>
                            <span>${order.changeAmount?.toFixed(2) || "0.00"}</span>
                        </div>
                    </div>

                    <div className="text-center pt-6 text-[11px] text-slate-400 border-t border-dashed border-slate-300">
                        Thank you for shopping with us!
                    </div>

                </div>

                <div className="p-4 bg-slate-50 border-t border-slate-200 flex gap-3 print:hidden">
                    <button
                        onClick={onClose}
                        className="flex-1 px-4 py-2.5 rounded-xl border border-slate-200 text-slate-700 bg-white hover:bg-slate-50 font-semibold text-sm transition"
                    >
                        Done
                    </button>
                    <button
                        onClick={handlePrint}
                        className="flex-1 px-4 py-2.5 rounded-xl bg-orange-600 hover:bg-orange-700 text-white font-semibold text-sm shadow-sm hover:shadow transition flex items-center justify-center gap-2"
                    >
                        🖨️ Download Bill
                    </button>
                </div>

            </div>
        </div>
    );
}

BillingModal.propTypes = {
  onClose: PropTypes.func.isRequired,

  order: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    identifier: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    customerIdentifier: PropTypes.string,
    createdOn: PropTypes.oneOfType([PropTypes.string, PropTypes.number, PropTypes.instanceOf(Date)]),
    paymentMethod: PropTypes.string,

    originalPrice: PropTypes.number,
    discount: PropTypes.number,
    receivedAmount: PropTypes.number,
    recievedAmount: PropTypes.number,
    changeAmount: PropTypes.number,

    cartEntryDtoList: PropTypes.arrayOf(
      PropTypes.shape({
        product: PropTypes.string,
        productName: PropTypes.string,
        quantity: PropTypes.number,
        originalPrice: PropTypes.number,
        unitPrice: PropTypes.number,
        totalPrice: PropTypes.number,
        discount: PropTypes.number,
      })
    ),
  }),
};


