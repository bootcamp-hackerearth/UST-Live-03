"use client";
import { useState, useEffect, useRef } from "react";
import PropTypes from 'prop-types';
import { useRouter } from "next/navigation";
import api from "../../components/Axios";

function formatReceiptDate(dateStr) {
    const d = dateStr ? new Date(dateStr) : new Date();
    return d.toLocaleString("en-IN", { day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" });
}

function ProductActionButtons({ cartId, selling, qty, loadingAction, onAdd, onReduce }) {
    const disabledBase = "disabled:border-gray-300 disabled:text-gray-300 disabled:cursor-not-allowed";

    if (!cartId) {
        return (
            <button disabled className="flex-1 py-2 bg-gray-100 text-gray-400 rounded-lg text-[12px] font-semibold border-none cursor-not-allowed">
                Select customer first
            </button>
        );
    }

    if (selling === null) {
        return (
            <button disabled className="flex-1 py-2 bg-gray-100 text-gray-400 rounded-lg text-[12px] font-semibold border-none cursor-not-allowed">
                Price not set
            </button>
        );
    }

    if (qty === 0) {
        return (
            <button
                onClick={onAdd}
                disabled={loadingAction}
                className="flex-1 py-2 bg-[#2d6a4f] text-white rounded-lg text-[13px] font-semibold border-none cursor-pointer disabled:bg-gray-400 disabled:cursor-not-allowed hover:bg-[#1b4332]"
            >
                + Add
            </button>
        );
    }

    const smallBtnClass = `w-8 h-8 rounded-lg border-[1.5px] border-[#2d6a4f] bg-white text-[#2d6a4f] text-lg font-bold cursor-pointer flex items-center justify-center p-0 ${disabledBase} hover:bg-[#f0faf5]`;

    return (
        <>
            <button onClick={onReduce} disabled={loadingAction} className={smallBtnClass}>−</button>
            <span className="text-[15px] font-bold text-gray-900 min-w-[20px] text-center">{qty}</span>
            <button onClick={onAdd} disabled={loadingAction} className={smallBtnClass}>+</button>
        </>
    );
}

ProductActionButtons.propTypes = {
    cartId: PropTypes.oneOfType([PropTypes.string, PropTypes.oneOf([null])]),
    selling: PropTypes.oneOfType([PropTypes.number, PropTypes.oneOf([null])]),
    qty: PropTypes.number,
    loadingAction: PropTypes.bool,
    onAdd: PropTypes.func,
    onReduce: PropTypes.func,
};

function CartEmptyState({ selectedCustomer }) {
    return (
        <div className="flex-1 flex flex-col items-center justify-center text-center px-6 gap-3">
            <span className="text-5xl">🛒</span>
            {selectedCustomer ? (
                <>
                    <p className="text-[14px] font-semibold text-gray-500 m-0">Cart is empty</p>
                    <p className="text-[12px] text-gray-400 m-0">Add products from the left.</p>
                </>
            ) : (
                <>
                    <p className="text-[14px] font-semibold text-gray-500 m-0">No customer selected</p>
                    <p className="text-[12px] text-gray-400 m-0">Enter a phone number above to associate a cart.</p>
                </>
            )}
        </div>
    );
}

CartEmptyState.propTypes = {
    selectedCustomer: PropTypes.oneOfType([PropTypes.object, PropTypes.bool, PropTypes.instanceOf(null)])
};

function ProductPriceDisplay({ selling, mrp, hasDiscount }) {
    if (selling === null) {
        return (
            <>
                <p className="text-xs text-transparent m-0 select-none">-</p>
                <p className="text-[13px] text-amber-600 font-semibold m-0">⚠ Price not set</p>
            </>
        );
    }
    if (hasDiscount) {
        return (
            <>
                <p className="text-xs text-gray-400 line-through m-0">₹{mrp.toFixed(2)}</p>
                <div className="flex items-center gap-2">
                    <p className="text-[15px] font-bold text-[#2d6a4f] m-0">₹{selling.toFixed(2)}</p>
                    <span className="text-[10px] font-semibold text-white bg-red-500 px-1.5 py-0.5 rounded-full">
                        -{Math.round(((mrp - selling) / mrp) * 100)}%
                    </span>
                </div>
            </>
        );
    }
    return (
        <>
            <p className="text-xs text-transparent m-0 select-none">-</p>
            <p className="text-[15px] font-bold text-gray-800 m-0">₹{selling.toFixed(2)}</p>
        </>
    );
}

ProductPriceDisplay.propTypes = {
    selling: PropTypes.oneOfType([PropTypes.number, PropTypes.oneOf([null])]),
    mrp: PropTypes.oneOfType([PropTypes.number, PropTypes.oneOf([null])]),
    hasDiscount: PropTypes.bool,
};

function ProductCard({ product, qty, price, cartId, loadingAction, onAdd, onReduce }) {
    const mrp = price ? Number(price.mrp) : null;
    const selling = price ? Number(price.sellingPrice) : null;
    const hasDiscount = mrp !== null && selling !== null && selling < mrp;
    const categoryLabel = Array.isArray(product.category) && product.category.length > 0
        ? product.category[0]
        : null;

    return (
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 flex flex-col overflow-hidden">
            <div className="w-full h-[120px] bg-gray-100 flex items-center justify-center shrink-0">
                <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#d1d5db" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                    <rect x="3" y="3" width="18" height="18" rx="2" />
                    <circle cx="8.5" cy="8.5" r="1.5" />
                    <polyline points="21 15 16 10 5 21" />
                </svg>
            </div>

            <div className="p-3 flex flex-col gap-1 flex-1">
                <div className="flex items-center gap-1.5 flex-wrap">
                    {categoryLabel && (
                        <span className="text-[10px] font-semibold text-[#2d6a4f] bg-[#f0faf5] border border-[#b7e4c7] px-1.5 py-0.5 rounded-full">
                            {categoryLabel}
                        </span>
                    )}
                    {product.brand && (
                        <span className="text-[10px] text-gray-400 font-medium">{product.brand}</span>
                    )}
                </div>

                <p className="text-[13px] font-bold text-gray-900 m-0 leading-snug min-h-[36px]">
                    {product.name || product.identifier}
                </p>

                <div className="flex items-center gap-2 flex-wrap">
                    {product.model && (
                        <span className="text-[11px] text-gray-400">{product.model}</span>
                    )}
                    {product.unit && (
                        <span className="text-[10px] text-gray-400 bg-gray-100 px-1.5 py-0.5 rounded">{product.unit}</span>
                    )}
                </div>

                <p className="text-[10px] text-gray-300 m-0 font-mono">SKU: {product.identifier}</p>

                <div className="flex flex-col gap-0.5 mt-1 min-h-[44px] justify-end">
                    <ProductPriceDisplay selling={selling} mrp={mrp} hasDiscount={hasDiscount} />
                </div>

                <div className="flex items-center gap-2 mt-3">
                    <ProductActionButtons
                        cartId={cartId}
                        selling={selling}
                        qty={qty}
                        loadingAction={loadingAction}
                        onAdd={onAdd}
                        onReduce={onReduce}
                    />
                </div>
            </div>
        </div>
    );
}

ProductCard.propTypes = {
    product: PropTypes.object.isRequired,
    qty: PropTypes.number,
    price: PropTypes.object,
    cartId: PropTypes.oneOfType([PropTypes.string, PropTypes.oneOf([null])]),
    loadingAction: PropTypes.bool,
    onAdd: PropTypes.func.isRequired,
    onReduce: PropTypes.func.isRequired,
};

function CartEntryRow({ entry, productMap, deletingEntry, onDelete }) {
    const prod = productMap[entry.product];
    const qty = Number.isNaN(Number(entry.quantity)) ? 0 : Math.floor(Number(entry.quantity));

    return (
        <div className="bg-gray-50 rounded-xl p-3 border border-gray-200">
            <div className="flex items-start justify-between gap-2 mb-1">
                <div className="flex flex-col gap-0.5 min-w-0">
                    <p className="text-[13px] font-bold text-gray-900 m-0 leading-snug truncate">
                        {prod?.name || entry.product}
                    </p>
                    <p className="text-[10px] text-gray-400 m-0 font-mono">SKU: {entry.product}</p>
                </div>
                <button
                    onClick={() => onDelete(entry)}
                    disabled={deletingEntry === entry.identifier}
                    className="shrink-0 w-6 h-6 flex items-center justify-center rounded-md text-red-400 hover:bg-red-50 hover:text-red-600 border border-transparent hover:border-red-200 transition-colors disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
                >
                    {deletingEntry === entry.identifier ? (
                        <span className="text-[10px]">…</span>
                    ) : (
                        <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                            <polyline points="3 6 5 6 21 6" /><path d="M19 6l-1 14H6L5 6" /><path d="M10 11v6M14 11v6" /><path d="M9 6V4h6v2" />
                        </svg>
                    )}
                </button>
            </div>
            <div className="flex justify-between items-center">
                <span className="text-xs text-gray-400">Qty: {qty}</span>
                <span className="text-[13px] font-bold text-[#2d6a4f]">₹{Number(entry.totalPrice || 0).toFixed(2)}</span>
            </div>
            {Number(entry.discount) > 0 && (
                <div className="text-[11px] text-[#2d6a4f] mt-1">You saved: ₹{Number(entry.discount).toFixed(2)}</div>
            )}
        </div>
    );
}

CartEntryRow.propTypes = {
    entry: PropTypes.object.isRequired,
    productMap: PropTypes.object.isRequired,
    deletingEntry: PropTypes.oneOfType([PropTypes.string, PropTypes.oneOf([null])]),
    onDelete: PropTypes.func.isRequired,
};

function PaymentMethodSelector({ paymentMode, onSelect }) {
    const options = [
        { key: "ONLINE", label: "📱 UPI" },
        { key: "CASH", label: "💵 Cash" },
        { key: "CARD", label: "💳 Card" },
    ];

    return (
        <div className="flex gap-2 mb-5">
            {options.map((opt) => (
                <button
                    key={opt.key}
                    onClick={() => onSelect(opt.key)}
                    className={`flex-1 py-3 rounded-xl border-2 text-[12px] font-semibold cursor-pointer transition-all ${paymentMode === opt.key ? "border-[#2d6a4f] bg-[#f0faf5] text-[#2d6a4f]" : "border-gray-200 text-gray-500 hover:border-gray-300"}`}
                >
                    {opt.label}
                </button>
            ))}
        </div>
    );
}

PaymentMethodSelector.propTypes = {
    paymentMode: PropTypes.string,
    onSelect: PropTypes.func.isRequired,
};

function OnlinePaymentPanel({ finalAmount }) {
    return (
        <div className="flex flex-col items-center gap-3 mb-5">
            <p className="text-[12px] text-gray-500 m-0">Scan to pay ₹{finalAmount.toFixed(2)}</p>
            <div className="w-[180px] h-[180px] bg-gray-100 rounded-xl flex items-center justify-center border-2 border-dashed border-gray-300">
                <div className="grid grid-cols-5 gap-1 p-3">
                    {Array.from({ length: 25 }).map((_, i) => {
                        const r = Math.floor(i / 5);
                        const c = i % 5;
                        return (
                            <div
                                key={`qr-${r}-${c}`}
                                className="w-6 h-6 rounded-sm"
                                style={{
                                    backgroundColor: (globalThis.crypto.getRandomValues(new Uint32Array(1))[0] / 4294967296) > 0.4
                                        ? "#1b4332"
                                        : "#f9fafb"
                                }}
                            />
                        );
                    })}
                </div>
            </div>
            <p className="text-[11px] text-gray-400 m-0">Use any UPI app to scan and pay</p>
        </div>
    );
}

OnlinePaymentPanel.propTypes = {
    finalAmount: PropTypes.number.isRequired,
};

function CashPaymentPanel({ cashReceived, onToggle }) {
    return (
        <div className="mb-5">
            <button
                onClick={onToggle}
                className={`w-full py-3 rounded-xl border-2 text-[13px] font-semibold cursor-pointer transition-all ${cashReceived ? "border-[#2d6a4f] bg-[#f0faf5] text-[#2d6a4f]" : "border-gray-200 text-gray-500 hover:border-gray-300"}`}
            >
                {cashReceived ? "✅ Cash Received" : "Cash Received?"}
            </button>
        </div>
    );
}

CashPaymentPanel.propTypes = {
    cashReceived: PropTypes.bool,
    onToggle: PropTypes.func.isRequired,
};

function CardPaymentPanel({ cardNumber, cardExpiry, cardCvv, cardErrors, onCardNumberChange, onExpiryChange, onCvvChange }) {
    return (
        <div className="mb-5 flex flex-col gap-3">
            <div>
                <label htmlFor="card-number" className="text-[12px] font-semibold text-gray-500 block mb-1">Card Number</label>
                <input
                    id="card-number"
                    type="text"
                    inputMode="numeric"
                    placeholder="1234 5678 9012 3456"
                    value={cardNumber}
                    onChange={(e) => onCardNumberChange(e.target.value)}
                    className={`w-full px-3 py-2 border-[1.5px] rounded-lg text-[13px] outline-none focus:border-[#2d6a4f] ${cardErrors.cardNumber ? "border-red-400" : "border-gray-300"}`}
                />
                {cardErrors.cardNumber && <p className="text-[11px] text-red-500 mt-1 mb-0">{cardErrors.cardNumber}</p>}
            </div>
            <div className="flex gap-3">
                <div className="flex-1">
                    <label htmlFor="card-expiry" className="text-[12px] font-semibold text-gray-500 block mb-1">Expiry (MM/YY)</label>
                    <input
                        id="card-expiry"
                        type="text"
                        inputMode="numeric"
                        placeholder="MM/YY"
                        value={cardExpiry}
                        onChange={(e) => onExpiryChange(e.target.value)}
                        className={`w-full px-3 py-2 border-[1.5px] rounded-lg text-[13px] outline-none focus:border-[#2d6a4f] ${cardErrors.cardExpiry ? "border-red-400" : "border-gray-300"}`}
                    />
                    {cardErrors.cardExpiry && <p className="text-[11px] text-red-500 mt-1 mb-0">{cardErrors.cardExpiry}</p>}
                </div>
                <div className="flex-1">
                    <label htmlFor="card-cvv" className="text-[12px] font-semibold text-gray-500 block mb-1">CVV</label>
                    <input
                        id="card-cvv"
                        type="password"
                        inputMode="numeric"
                        placeholder="123"
                        value={cardCvv}
                        onChange={(e) => onCvvChange(e.target.value)}
                        className={`w-full px-3 py-2 border-[1.5px] rounded-lg text-[13px] outline-none focus:border-[#2d6a4f] ${cardErrors.cardCvv ? "border-red-400" : "border-gray-300"}`}
                    />
                    {cardErrors.cardCvv && <p className="text-[11px] text-red-500 mt-1 mb-0">{cardErrors.cardCvv}</p>}
                </div>
            </div>
        </div>
    );
}

CardPaymentPanel.propTypes = {
    cardNumber: PropTypes.string,
    cardExpiry: PropTypes.string,
    cardCvv: PropTypes.string,
    cardErrors: PropTypes.object,
    onCardNumberChange: PropTypes.func.isRequired,
    onExpiryChange: PropTypes.func.isRequired,
    onCvvChange: PropTypes.func.isRequired,
};

function PaymentModePanel({ paymentMode, finalAmount, cashReceived, onToggleCash, cardFields }) {
    if (paymentMode === "ONLINE") return <OnlinePaymentPanel finalAmount={finalAmount} />;
    if (paymentMode === "CASH") return <CashPaymentPanel cashReceived={cashReceived} onToggle={onToggleCash} />;
    if (paymentMode === "CARD") return <CardPaymentPanel {...cardFields} />;
    return null;
}

PaymentModePanel.propTypes = {
    paymentMode: PropTypes.string,
    finalAmount: PropTypes.number.isRequired,
    cashReceived: PropTypes.bool,
    onToggleCash: PropTypes.func.isRequired,
    cardFields: PropTypes.object.isRequired,
};

function OrderSummaryItems({ entries, productMap }) {
    return (
        <div className="border-t border-gray-200 pt-4 mt-2 flex flex-col gap-2">
            {entries.map((entry) => (
                <div key={entry.identifier} className="flex justify-between text-[13px]">
                    <span className="text-gray-700">
                        {productMap[entry.product]?.name || entry.product}
                        <span className="text-gray-400 ml-1">× {Math.floor(Number(entry.quantity))}</span>
                    </span>
                    <span className="font-semibold text-gray-900">₹{Number(entry.totalPrice).toFixed(2)}</span>
                </div>
            ))}
        </div>
    );
}

OrderSummaryItems.propTypes = {
    entries: PropTypes.array.isRequired,
    productMap: PropTypes.object.isRequired,
};

function OrderSuccessCard({ placedOrder, productMap, finalAmount }) {
    return (
        <div className="w-full bg-gray-50 rounded-2xl border border-gray-200 p-5 mb-6">
            <div className="flex justify-between items-center mb-4">
                <span className="text-[12px] font-semibold text-gray-500">Order ID</span>
                <span className="text-[12px] font-mono text-gray-800">{placedOrder.orderId}</span>
            </div>
            <div className="flex justify-between items-center mb-4">
                <span className="text-[12px] font-semibold text-gray-500">Payment</span>
                <span className="text-[12px] text-gray-800">{placedOrder.paymentMode}</span>
            </div>
            {placedOrder.orderStatus && (
                <div className="flex justify-between items-center mb-4">
                    <span className="text-[12px] font-semibold text-gray-500">Status</span>
                    <span className="text-[11px] font-semibold text-[#2d6a4f] bg-[#f0faf5] border border-[#b7e4c7] px-2 py-0.5 rounded-full">{placedOrder.orderStatus}</span>
                </div>
            )}

            <OrderSummaryItems entries={placedOrder.entryDtoList || []} productMap={productMap} />

            <div className="border-t border-gray-200 mt-4 pt-4 flex flex-col gap-1">
                <div className="flex justify-between text-[13px] text-gray-500">
                    <span>Total</span>
                    <span>₹{Number(placedOrder.totalPrice).toFixed(2)}</span>
                </div>
                {Number(placedOrder.totalDiscount) > 0 && (
                    <div className="flex justify-between text-[13px] text-[#2d6a4f]">
                        <span>You Saved</span>
                        <span>₹{Number(placedOrder.totalDiscount).toFixed(2)}</span>
                    </div>
                )}
                <div className="flex justify-between text-[15px] font-bold text-gray-900 mt-1">
                    <span>Final Amount</span>
                    <span className="text-[#2d6a4f]">₹{finalAmount.toFixed(2)}</span>
                </div>
            </div>
        </div>
    );
}

OrderSuccessCard.propTypes = {
    placedOrder: PropTypes.object.isRequired,
    productMap: PropTypes.object.isRequired,
    finalAmount: PropTypes.number.isRequired,
};

function PrintableReceipt({ order, productMap, finalAmount }) {
    if (!order) return null;
    return (
        <div className="hidden print:block print:fixed print:inset-0 print:bg-white">
            <div className="max-w-[400px] mx-auto py-6 px-6 font-mono text-black">
                <div className="text-center mb-4">
                    <p className="text-[16px] font-bold m-0">Retail POS</p>
                    <p className="text-[10px] m-0 mt-1">1-38,Mallamgunta,Tirupati - 517507</p>
                    <p className="text-[10px] m-0">GSTIN: 1234567890</p>
                </div>
                <div className="border-t border-b border-black border-dashed py-2 mb-2 text-[11px]">
                    <div className="flex justify-between"><span>Bill No:</span><span>{order.orderId}</span></div>
                    <div className="flex justify-between"><span>Date:</span><span>{formatReceiptDate(order.orderDate)}</span></div>
                    <div className="flex justify-between"><span>Customer:</span><span>{order.identifier}</span></div>
                    <div className="flex justify-between"><span>Payment:</span><span>{order.paymentMode}</span></div>
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
                        {(order.entryDtoList || []).map((entry) => {
                            const prod = productMap[entry.product];
                            const qty = Math.floor(Number(entry.quantity));
                            const rate = Number(entry.sellingPrice || 0);
                            return (
                                <tr key={entry.identifier}>
                                    <td className="py-0.5">{prod?.name || entry.product}</td>
                                    <td className="text-center py-0.5">{qty}</td>
                                    <td className="text-right py-0.5">{rate.toFixed(2)}</td>
                                    <td className="text-right py-0.5">{Number(entry.totalPrice || 0).toFixed(2)}</td>
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
                <div className="border-t border-black border-dashed pt-2 text-[11px]">
                    <div className="flex justify-between"><span>Subtotal:</span><span>₹{Number(order.totalPrice || 0).toFixed(2)}</span></div>
                    {Number(order.totalDiscount) > 0 && (
                        <div className="flex justify-between"><span>You Saved:</span><span>₹{Number(order.totalDiscount).toFixed(2)}</span></div>
                    )}
                    <div className="flex justify-between text-[13px] font-bold border-t border-black mt-1 pt-1">
                        <span>TOTAL:</span><span>₹{finalAmount.toFixed(2)}</span>
                    </div>
                </div>
                <p className="text-center text-[10px] mt-4 mb-0">Thank you for shopping with us!</p>
            </div>
        </div>
    );
}

PrintableReceipt.propTypes = {
    order: PropTypes.object,
    productMap: PropTypes.object.isRequired,
    finalAmount: PropTypes.number.isRequired,
};

function CustomerSearchBar({ phoneInput, onPhoneChange, searchingCustomer, selectedCustomer, customerError, onClearCustomer, onAddCustomer }) {
    return (
        <div className="flex items-center gap-3 flex-1">
            <span className="text-[13px] font-semibold text-gray-500 shrink-0">Customer Phone</span>
            <div className="relative">
                <input
                    type="text"
                    inputMode="numeric"
                    placeholder="Enter phone number…"
                    value={phoneInput}
                    onChange={(e) => onPhoneChange(e.target.value)}
                    maxLength={10}
                    className="w-[200px] px-3 py-2 border-[1.5px] border-gray-300 rounded-lg text-[13px] outline-none bg-white focus:border-[#2d6a4f]"
                />
                {searchingCustomer && (
                    <span className="absolute right-2 top-1/2 -translate-y-1/2 text-[11px] text-gray-400">…</span>
                )}
            </div>

            {selectedCustomer && (
                <div className="flex items-center gap-2 bg-[#f0faf5] border border-[#b7e4c7] rounded-lg px-3 py-1.5">
                    <span className="text-[13px] font-bold text-[#2d6a4f]">{selectedCustomer.customerName}</span>
                    <span className="text-[11px] text-gray-400">{selectedCustomer.identifier}</span>
                    <button
                        onClick={onClearCustomer}
                        className="ml-1 text-gray-400 hover:text-red-500 text-[14px] leading-none cursor-pointer"
                    >×</button>
                </div>
            )}

            {customerError && selectedCustomer === null && (
                <div className="flex items-center gap-2">
                    <span className="text-[12px] text-red-500">{customerError}</span>
                    <button
                        onClick={onAddCustomer}
                        className="px-3 py-1.5 bg-[#2d6a4f] text-white text-[12px] font-semibold rounded-lg hover:bg-[#1b4332] cursor-pointer border-none"
                    >
                        + Add Customer
                    </button>
                </div>
            )}
        </div>
    );
}

CustomerSearchBar.propTypes = {
    phoneInput: PropTypes.string.isRequired,
    onPhoneChange: PropTypes.func.isRequired,
    searchingCustomer: PropTypes.bool,
    selectedCustomer: PropTypes.oneOfType([PropTypes.object, PropTypes.oneOf([null])]),
    customerError: PropTypes.string,
    onClearCustomer: PropTypes.func.isRequired,
    onAddCustomer: PropTypes.func.isRequired,
};

function AddCustomerModal({ newCustomer, onFieldChange, addCustomerError, addingCustomer, onCancel, onSubmit }) {
    return (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-[1000] print:hidden">
            <div className="bg-white p-6 rounded-xl shadow-xl w-full max-w-[420px]">
                <h3 className="text-lg font-bold text-gray-800 m-0 mb-1">New Customer</h3>
                <p className="text-[12px] text-gray-400 m-0 mb-5">Create a new customer account to continue.</p>
                <div className="flex flex-col gap-3">
                    <div>
                        <label htmlFor="customer-identifier" className="text-[12px] font-semibold text-gray-500 block mb-1">Phone Number (ID) *</label>
                        <input id="customer-identifier" type="text" value={newCustomer.identifier} onChange={(e) => onFieldChange("identifier", e.target.value)} placeholder="e.g. 9876543210" className="w-full px-3 py-2 border-[1.5px] border-gray-300 rounded-lg text-[13px] outline-none focus:border-[#2d6a4f]" />
                    </div>
                    <div>
                        <label htmlFor="customer-name" className="text-[12px] font-semibold text-gray-500 block mb-1">Customer Name *</label>
                        <input id="customer-name" type="text" value={newCustomer.customerName} onChange={(e) => onFieldChange("customerName", e.target.value)} placeholder="Full name" className="w-full px-3 py-2 border-[1.5px] border-gray-300 rounded-lg text-[13px] outline-none focus:border-[#2d6a4f]" />
                    </div>
                    <div>
                        <label htmlFor="customer-email" className="text-[12px] font-semibold text-gray-500 block mb-1">Email (optional)</label>
                        <input id="customer-email" type="email" value={newCustomer.email} onChange={(e) => onFieldChange("email", e.target.value)} placeholder="email@example.com" className="w-full px-3 py-2 border-[1.5px] border-gray-300 rounded-lg text-[13px] outline-none focus:border-[#2d6a4f]" />
                    </div>
                </div>
                {addCustomerError && <p className="text-[12px] text-red-500 mt-3 mb-0">{addCustomerError}</p>}
                <div className="flex gap-3 mt-5 justify-end">
                    <button onClick={onCancel} className="px-5 py-2 bg-gray-100 text-gray-600 rounded-lg text-[13px] font-semibold border-none cursor-pointer hover:bg-gray-200">Cancel</button>
                    <button onClick={onSubmit} disabled={addingCustomer} className="px-5 py-2 bg-[#2d6a4f] text-white rounded-lg text-[13px] font-semibold border-none cursor-pointer hover:bg-[#1b4332] disabled:bg-gray-400 disabled:cursor-not-allowed">
                        {addingCustomer ? "Creating…" : "Create Customer"}
                    </button>
                </div>
            </div>
        </div>
    );
}

AddCustomerModal.propTypes = {
    newCustomer: PropTypes.object.isRequired,
    onFieldChange: PropTypes.func.isRequired,
    addCustomerError: PropTypes.string,
    addingCustomer: PropTypes.bool,
    onCancel: PropTypes.func.isRequired,
    onSubmit: PropTypes.func.isRequired,
};

function ClearCartModal({ customerName, onCancel, onConfirm }) {
    return (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-[1000] print:hidden">
            <div className="bg-white p-6 rounded-xl shadow-xl w-full max-w-[400px] text-center">
                <h3 className="text-lg font-bold text-gray-800 m-0 mb-2">Clear Cart?</h3>
                <p className="text-sm text-gray-500 m-0 mb-5 leading-relaxed">
                    All items for <strong>{customerName}</strong> will be removed. This cannot be undone.
                </p>
                <div className="flex gap-3 justify-center">
                    <button onClick={onCancel} className="px-5 py-2 bg-gray-100 text-gray-600 rounded-lg text-[13px] font-semibold border-none cursor-pointer hover:bg-gray-200">Cancel</button>
                    <button onClick={onConfirm} className="px-5 py-2 bg-[#d62828] text-white rounded-lg text-[13px] font-semibold border-none cursor-pointer hover:bg-red-800">Yes, Clear</button>
                </div>
            </div>
        </div>
    );
}

ClearCartModal.propTypes = {
    customerName: PropTypes.string,
    onCancel: PropTypes.func.isRequired,
    onConfirm: PropTypes.func.isRequired,
};

function PaymentModal({ finalAmount, customerName, paymentMode, onSelectMode, cashReceived, onToggleCash, cardFields, placingOrder, onClose, onPlaceOrder }) {
    const placeDisabled =
        placingOrder ||
        !paymentMode ||
        (paymentMode === "CASH" && !cashReceived);

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[1000] print:hidden">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-[460px] overflow-hidden max-h-[90vh] overflow-y-auto">
                <div className="px-6 py-4 border-b border-gray-100 flex items-center justify-between sticky top-0 bg-white">
                    <div>
                        <h3 className="text-[16px] font-bold text-gray-900 m-0">Payment</h3>
                        <p className="text-[12px] text-gray-400 m-0 mt-0.5">{customerName} · ₹{finalAmount.toFixed(2)}</p>
                    </div>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 text-xl leading-none cursor-pointer">×</button>
                </div>

                <div className="px-6 py-5">
                    <p className="text-[12px] font-semibold text-gray-500 mb-3">Select payment method</p>
                    <PaymentMethodSelector paymentMode={paymentMode} onSelect={onSelectMode} />
                    <PaymentModePanel
                        paymentMode={paymentMode}
                        finalAmount={finalAmount}
                        cashReceived={cashReceived}
                        onToggleCash={onToggleCash}
                        cardFields={cardFields}
                    />
                    <button
                        onClick={onPlaceOrder}
                        disabled={placeDisabled}
                        className="w-full py-3 bg-[#2d6a4f] text-white rounded-xl text-[14px] font-bold border-none cursor-pointer disabled:bg-gray-300 disabled:cursor-not-allowed hover:bg-[#1b4332] transition-colors"
                    >
                        {placingOrder ? "Placing Order…" : "Place Order"}
                    </button>
                </div>
            </div>
        </div>
    );
}

PaymentModal.propTypes = {
    finalAmount: PropTypes.number.isRequired,
    customerName: PropTypes.string,
    paymentMode: PropTypes.string,
    onSelectMode: PropTypes.func.isRequired,
    cashReceived: PropTypes.bool,
    onToggleCash: PropTypes.func.isRequired,
    cardFields: PropTypes.object.isRequired,
    placingOrder: PropTypes.bool,
    onClose: PropTypes.func.isRequired,
    onPlaceOrder: PropTypes.func.isRequired,
};

function OrderSuccessOverlay({ placedOrder, productMap, finalAmount, customerName, onPrint, onNewSale, onViewOrders }) {
    return (
        <div className="fixed inset-0 bg-white z-[2000] overflow-y-auto print:hidden">
            <div className="min-h-full flex items-center justify-center py-10">
                <div className="w-full max-w-[520px] px-6">
                    <div className="flex flex-col items-center">
                        <div className="w-20 h-20 rounded-full bg-[#f0faf5] flex items-center justify-center mb-4" style={{ animation: "pop 0.4s ease" }}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#2d6a4f" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                <polyline points="20 6 9 17 4 12" />
                            </svg>
                        </div>

                        <h2 className="text-[22px] font-bold text-gray-900 m-0 mb-1">Order Placed!</h2>
                        <p className="text-[13px] text-gray-400 m-0 mb-6">Thank you, {customerName}</p>

                        <OrderSuccessCard placedOrder={placedOrder} productMap={productMap} finalAmount={finalAmount} />

                        <div className="flex gap-3 w-full mb-3">
                            <button
                                onClick={onPrint}
                                className="flex-1 py-3 bg-white text-gray-700 border-2 border-gray-300 rounded-xl text-[13px] font-bold cursor-pointer hover:bg-gray-50"
                            >
                                🖨️ Print Receipt
                            </button>
                        </div>

                        <div className="flex gap-3 w-full">
                            <button
                                onClick={onNewSale}
                                className="flex-1 py-3 bg-[#2d6a4f] text-white rounded-xl text-[13px] font-bold border-none cursor-pointer hover:bg-[#1b4332]"
                            >
                                New Sale
                            </button>
                            <button
                                onClick={onViewOrders}
                                className="flex-1 py-3 bg-white text-[#2d6a4f] border-2 border-[#2d6a4f] rounded-xl text-[13px] font-bold cursor-pointer hover:bg-[#f0faf5]"
                            >
                                View Orders
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <style>{`
                @keyframes pop {
                    0% { transform: scale(0.5); opacity: 0; }
                    70% { transform: scale(1.15); }
                    100% { transform: scale(1); opacity: 1; }
                }
            `}</style>
        </div>
    );
}

OrderSuccessOverlay.propTypes = {
    placedOrder: PropTypes.object.isRequired,
    productMap: PropTypes.object.isRequired,
    finalAmount: PropTypes.number.isRequired,
    customerName: PropTypes.string,
    onPrint: PropTypes.func.isRequired,
    onNewSale: PropTypes.func.isRequired,
    onViewOrders: PropTypes.func.isRequired,
};

function ProductGrid({ filteredProducts, quantities, prices, cartId, loadingAction, onAdd, onReduce }) {
    return (
        <div className="grid grid-cols-[repeat(auto-fill,minmax(200px,1fr))] gap-4">
            {filteredProducts.map((product) => (
                <ProductCard
                    key={product.identifier}
                    product={product}
                    qty={quantities[product.identifier] || 0}
                    price={prices[product.identifier]}
                    cartId={cartId}
                    loadingAction={loadingAction}
                    onAdd={() => onAdd(product)}
                    onReduce={() => onReduce(product)}
                />
            ))}
        </div>
    );
}

ProductGrid.propTypes = {
    filteredProducts: PropTypes.array.isRequired,
    quantities: PropTypes.object.isRequired,
    prices: PropTypes.object.isRequired,
    cartId: PropTypes.oneOfType([PropTypes.string, PropTypes.oneOf([null])]),
    loadingAction: PropTypes.bool,
    onAdd: PropTypes.func.isRequired,
    onReduce: PropTypes.func.isRequired,
};

function CartPanel({ selectedCustomer, cartInitialized, hasItems, cartEntries, productMap, deletingEntry, onDeleteEntry, cart, finalAmount, loadingAction, onCheckout, onClearCart }) {
    return (
        <div className="w-[340px] bg-white border-l border-gray-200 flex flex-col overflow-hidden shrink-0">
            <div className="px-5 py-4 border-b border-gray-200 flex items-center justify-between shrink-0">
                <div>
                    <span className="text-base font-bold text-gray-900">🛒 Cart</span>
                    {selectedCustomer ? (
                        <p className="text-[11px] text-gray-400 m-0 mt-0.5">{selectedCustomer.customerName} · {selectedCustomer.identifier}</p>
                    ) : (
                        <p className="text-[11px] text-gray-400 m-0 mt-0.5">No customer selected</p>
                    )}
                </div>
                <span className="text-[13px] text-gray-400 font-medium">
                    {cartEntries.length} item{cartEntries.length === 1 ? "" : "s"}
                </span>
            </div>

            {(!selectedCustomer || !cartInitialized || !hasItems) && (
                <CartEmptyState selectedCustomer={selectedCustomer} />
            )}

            {selectedCustomer && cartInitialized && hasItems && (
                <>
                    <div className="flex-1 overflow-y-auto px-4 py-3 flex flex-col gap-3">
                        {cartEntries.map((entry) => (
                            <CartEntryRow
                                key={entry.identifier}
                                entry={entry}
                                productMap={productMap}
                                deletingEntry={deletingEntry}
                                onDelete={onDeleteEntry}
                            />
                        ))}
                    </div>

                    <div className="px-4 py-3 border-t border-gray-200 flex flex-col gap-2 shrink-0">
                        <div className="flex justify-between text-[13px] text-gray-500">
                            <span>Total Price</span>
                            <span>₹{Number(cart?.totalPrice || 0).toFixed(2)}</span>
                        </div>
                        {Number(cart?.totalDiscount || 0) > 0 && (
                            <div className="flex justify-between text-[13px] text-[#2d6a4f]">
                                <span>You Saved</span>
                                <span>₹{Number(cart?.totalDiscount || 0).toFixed(2)}</span>
                            </div>
                        )}
                        <div className="flex justify-between text-[15px] font-bold text-gray-900 border-t border-gray-200 pt-3 mt-1">
                            <span>Final Amount</span>
                            <span className="text-[#2d6a4f]">₹{finalAmount.toFixed(2)}</span>
                        </div>
                    </div>

                    <button
                        onClick={onCheckout}
                        disabled={loadingAction}
                        className="mx-4 mb-2 py-3 bg-[#2d6a4f] text-white rounded-lg text-[13px] font-semibold border-none cursor-pointer shrink-0 disabled:bg-gray-400 disabled:cursor-not-allowed hover:bg-[#1b4332]"
                    >
                        ✅ Checkout
                    </button>

                    <button
                        onClick={onClearCart}
                        disabled={loadingAction}
                        className="mx-4 mb-4 py-3 bg-[#d62828] text-white rounded-lg text-[13px] font-semibold border-none cursor-pointer shrink-0 disabled:bg-gray-400 disabled:cursor-not-allowed hover:bg-red-800"
                    >
                        🗑️ Clear Cart
                    </button>
                </>
            )}
        </div>
    );
}

CartPanel.propTypes = {
    selectedCustomer: PropTypes.oneOfType([PropTypes.object, PropTypes.oneOf([null])]),
    cartInitialized: PropTypes.bool,
    hasItems: PropTypes.bool,
    cartEntries: PropTypes.array.isRequired,
    productMap: PropTypes.object.isRequired,
    deletingEntry: PropTypes.oneOfType([PropTypes.string, PropTypes.oneOf([null])]),
    onDeleteEntry: PropTypes.func.isRequired,
    cart: PropTypes.object,
    finalAmount: PropTypes.number.isRequired,
    loadingAction: PropTypes.bool,
    onCheckout: PropTypes.func.isRequired,
    onClearCart: PropTypes.func.isRequired,
};

function parseQty(val) {
    const n = Number(val);
    return Number.isNaN(n) ? 0 : Math.floor(n);
}

function buildPriceMap(productList, priceResults) {
    const priceMap = {};
    priceResults.forEach((result, i) => {
        if (result.status === "fulfilled" && result.value?.data?.identifier) {
            priceMap[productList[i].identifier] = result.value.data;
        }
    });
    return priceMap;
}

function buildQtyMap(entries) {
    const qtyMap = {};
    entries.forEach((e) => {
        qtyMap[e.product] = parseQty(e.quantity);
    });
    return qtyMap;
}

function validateCardFields(cardNumber, cardExpiry, cardCvv) {
    const errs = {};
    const rawNumber = cardNumber.replaceAll(/\s/g, "");
    if (rawNumber.length !== 16) errs.cardNumber = "Enter a valid 16-digit card number.";

    const expiryRegex = /^\d{2}\/\d{2}$/;
    if (expiryRegex.test(cardExpiry)) {
        const [mm, yy] = cardExpiry.split("/").map(Number);
        if (mm < 1 || mm > 12) {
            errs.cardExpiry = "Enter a valid month (01–12).";
        } else {
            const now = new Date();
            const currentYear = now.getFullYear() % 100;
            const currentMonth = now.getMonth() + 1;
            if (yy < currentYear || (yy === currentYear && mm < currentMonth)) {
                errs.cardExpiry = "Card has expired.";
            }
        }
    } else {
        errs.cardExpiry = "Enter expiry as MM/YY.";
    }

    if (!/^\d{3}$/.test(cardCvv)) errs.cardCvv = "Enter a valid 3-digit CVV.";
    return errs;
}

export default function CartPage() {
    const router = useRouter();

    const [phoneInput, setPhoneInput] = useState("");
    const [selectedCustomer, setSelectedCustomer] = useState(null);
    const [customerError, setCustomerError] = useState("");
    const [searchingCustomer, setSearchingCustomer] = useState(false);
    const [showAddCustomer, setShowAddCustomer] = useState(false);
    const [newCustomer, setNewCustomer] = useState({ customerName: "", email: "", identifier: "" });
    const [addingCustomer, setAddingCustomer] = useState(false);
    const [addCustomerError, setAddCustomerError] = useState("");
    const phoneDebounceRef = useRef(null);

    const [products, setProducts] = useState([]);
    const [prices, setPrices] = useState({});
    const [loadingProducts, setLoadingProducts] = useState(true);

    const [cart, setCart] = useState(null);
    const [quantities, setQuantities] = useState({});
    const [cartInitialized, setCartInitialized] = useState(false);
    const [loadingAction, setLoadingAction] = useState(false);
    const [deletingEntry, setDeletingEntry] = useState(null);
    const [showConfirm, setShowConfirm] = useState(false);

    const [showPayment, setShowPayment] = useState(false);
    const [paymentMode, setPaymentMode] = useState(null);
    const [cashReceived, setCashReceived] = useState(false);
    const [cardNumber, setCardNumber] = useState("");
    const [cardExpiry, setCardExpiry] = useState("");
    const [cardCvv, setCardCvv] = useState("");
    const [cardErrors, setCardErrors] = useState({});
    const [placingOrder, setPlacingOrder] = useState(false);

    const [placedOrder, setPlacedOrder] = useState(null);
    const [showSuccess, setShowSuccess] = useState(false);
    const [printingReceipt, setPrintingReceipt] = useState(false);

    const [error, setError] = useState("");
    const [searchTerm, setSearchTerm] = useState("");

    const CART_ID = selectedCustomer?.identifier || null;

    useEffect(() => { loadProducts(); }, []);

    useEffect(() => {
        if (!CART_ID) {
            setCart(null);
            setQuantities({});
            setCartInitialized(false);
            return;
        }
        initCart();
    }, [CART_ID]);

    useEffect(() => {
        if (!printingReceipt) return;
        const timer = setTimeout(() => {
            globalThis.print();
            setPrintingReceipt(false);
        }, 100);
        return () => clearTimeout(timer);
    }, [printingReceipt]);

    function handlePhoneChange(val) {
        const digitsOnly = val.replaceAll(/\D/g, "").slice(0, 10);
        setPhoneInput(digitsOnly);
        setCustomerError("");
        setSelectedCustomer(null);
        if (phoneDebounceRef.current) clearTimeout(phoneDebounceRef.current);
        if (digitsOnly.length < 10) return;
        phoneDebounceRef.current = setTimeout(() => lookupCustomer(digitsOnly), 400);
    }

    async function lookupCustomer(phone) {
        setSearchingCustomer(true);
        setCustomerError("");
        try {
            const token = localStorage.getItem("token");
            const res = await fetch(
                `http://localhost:8080/api/customer/findByIdentifierAndDeletedFalse?identifier=${encodeURIComponent(phone)}`,
                { headers: { Authorization: `Bearer ${token}`, "Content-Type": "application/json" } }
            );
            const data = res.ok ? await res.json() : null;
            if (data?.identifier) {
                setSelectedCustomer(data);
                setCustomerError("");
            } else {
                setCustomerError("No customer found. Use 'Add Customer' to create one.");
            }
        } catch {
            setCustomerError("No customer found. Use 'Add Customer' to create one.");
        } finally {
            setSearchingCustomer(false);
        }
    }

    function openAddCustomer() {
        setNewCustomer({ customerName: "", email: "", identifier: phoneInput.trim() });
        setAddCustomerError("");
        setShowAddCustomer(true);
    }

    function handleNewCustomerFieldChange(key, value) {
        setNewCustomer((p) => ({ ...p, [key]: value }));
    }

    async function handleAddCustomer() {
        if (!newCustomer.identifier || !newCustomer.customerName) {
            setAddCustomerError("Phone number and name are required.");
            return;
        }
        setAddingCustomer(true);
        setAddCustomerError("");
        try {
            const res = await api.post("/customer/add", {
                identifier: newCustomer.identifier,
                customerName: newCustomer.customerName,
                email: newCustomer.email || "",
                partyType: "Customer",
                billingAddress: { phoneNo: newCustomer.identifier, addressType: "Billing" },
                shippingAddress: { phoneNo: newCustomer.identifier, addressType: "Shipping" },
            });
            if (res.data && res.data.success !== false) {
                setSelectedCustomer(res.data);
                setPhoneInput(res.data.identifier);
                setCustomerError("");
                setShowAddCustomer(false);
            } else {
                setAddCustomerError(res.data?.message || "Failed to create customer.");
            }
        } catch {
            setAddCustomerError("Failed to create customer. Please try again.");
        } finally {
            setAddingCustomer(false);
        }
    }

    async function initCart() {
        setCartInitialized(false);
        setError("");
        try {
            const res = await api.post("/cart/add", { identifier: CART_ID });
            if (res.data.success === false) {
                const existing = await api.post("/cart/getCart", { identifier: CART_ID });
                setCart(existing.data);
                setQuantities(buildQtyMap(existing.data.entryDtoList || []));
            } else {
                setCart(res.data || { entryDtoList: [], totalPrice: 0, totalDiscount: 0 });
                setQuantities({});
            }
        } catch {
            setError("Could not initialize cart.");
        } finally {
            setCartInitialized(true);
        }
    }

    async function loadProducts() {
        try {
            const res = await api.post("/product/list", {
                page: 0, sizePerPage: 100, sortDirection: "ASC", sortField: "id",
            });
            const productList = res.data.dtoList || [];
            setProducts(productList);

            const priceResults = await Promise.allSettled(
                productList.map((p) =>
                    api.get(`/price/getByDeletedFalse?identifier=${encodeURIComponent(p.identifier)}`)
                )
            );
            setPrices(buildPriceMap(productList, priceResults));
        } catch {
            setError("Could not load products.");
        } finally {
            setLoadingProducts(false);
        }
    }

    async function refreshCart() {
        const updated = await api.post("/cart/getCart", { identifier: CART_ID });
        setCart(updated.data || { entryDtoList: [], totalPrice: 0, totalDiscount: 0 });
        setQuantities(buildQtyMap(updated.data?.entryDtoList || []));
    }

    async function handleAdd(product) {
        if (!CART_ID) return;
        setLoadingAction(true);
        setError("");
        try {
            await api.post("/cartEntry/add", { product: product.identifier, cart: CART_ID, quantity: 1 });
            await api.post("/cart/addToCart", { cart: CART_ID });
            await refreshCart();
        } catch {
            setError("Failed to add product.");
        } finally {
            setLoadingAction(false);
        }
    }

    async function handleReduce(product) {
        if (!CART_ID) return;
        const currentQty = quantities[product.identifier] || 0;
        if (currentQty === 0) return;
        setLoadingAction(true);
        setError("");
        try {
            if (currentQty === 1) {
                await api.delete("/cartEntry/deleteEntries", { data: { product: product.identifier, cart: CART_ID } });
            } else {
                await api.post("/cartEntry/add", { product: product.identifier, cart: CART_ID, quantity: -1 });
            }
            await api.post("/cart/addToCart", { cart: CART_ID });
            await refreshCart();
        } catch {
            setError("Failed to remove product.");
        } finally {
            setLoadingAction(false);
        }
    }

    async function handleDeleteEntry(entry) {
        if (!CART_ID) return;
        setDeletingEntry(entry.identifier);
        setError("");
        try {
            await api.delete("/cartEntry/deleteEntries", { data: { product: entry.product, cart: CART_ID } });
            await api.post("/cart/addToCart", { cart: CART_ID });
            await refreshCart();
        } catch {
            setError("Failed to delete entry.");
        } finally {
            setDeletingEntry(null);
        }
    }

    async function confirmDeleteCart() {
        if (!CART_ID) return;
        setLoadingAction(true);
        setError("");
        setShowConfirm(false);
        try {
            await api.delete("/cart/delete", { data: { identifier: CART_ID } });
            await api.post("/cart/add", { identifier: CART_ID });
            setCart({ entryDtoList: [], totalPrice: 0, totalDiscount: 0 });
            setQuantities({});
        } catch {
            setError("Failed to clear cart.");
        } finally {
            setLoadingAction(false);
        }
    }

    function openPaymentModal() {
        setPaymentMode(null);
        setCashReceived(false);
        setCardNumber("");
        setCardExpiry("");
        setCardCvv("");
        setCardErrors({});
        setShowPayment(true);
    }

    function handleSelectPaymentMode(mode) {
        setPaymentMode(mode);
        if (mode !== "CASH") setCashReceived(false);
    }

    function handleCardNumberChange(val) {
        const digits = val.replaceAll(/\D/g, "").slice(0, 16);
        const formatted = digits.replaceAll(/(\d{4})(?=\d)/g, "$1 ");
        setCardNumber(formatted);
        if (cardErrors.cardNumber) setCardErrors((p) => ({ ...p, cardNumber: "" }));
    }

    function handleExpiryChange(val) {
        let digits = val.replaceAll(/\D/g, "").slice(0, 4);
        if (digits.length >= 3) digits = `${digits.slice(0, 2)}/${digits.slice(2)}`;
        setCardExpiry(digits);
        if (cardErrors.cardExpiry) setCardErrors((p) => ({ ...p, cardExpiry: "" }));
    }

    function handleCvvChange(val) {
        const digits = val.replaceAll(/\D/g, "").slice(0, 3);
        setCardCvv(digits);
        if (cardErrors.cardCvv) setCardErrors((p) => ({ ...p, cardCvv: "" }));
    }

    function validateCard() {
        const errs = validateCardFields(cardNumber, cardExpiry, cardCvv);
        setCardErrors(errs);
        return Object.keys(errs).length === 0;
    }

    async function handlePlaceOrder() {
        if (!CART_ID) return;
        if (paymentMode === "CASH" && !cashReceived) return;
        if (paymentMode === "CARD" && !validateCard()) return;
        setPlacingOrder(true);
        try {
            const res = await api.post("/order/place", {
                identifier: CART_ID,
                paymentMode: paymentMode,
            });
            setShowPayment(false);
            setPlacedOrder(res.data);
            setShowSuccess(true);
            setCart({ entryDtoList: [], totalPrice: 0, totalDiscount: 0 });
            setQuantities({});
        } catch {
            setError("Failed to place order. Please try again.");
        } finally {
            setPlacingOrder(false);
        }
    }

    function handlePrintReceipt() {
        setPrintingReceipt(true);
    }

    function handleNewSale() {
        setShowSuccess(false);
        setPlacedOrder(null);
        setSelectedCustomer(null);
        setPhoneInput("");
    }

    const productMap = {};
    products.forEach((p) => { productMap[p.identifier] = p; });

    const filteredProducts = products.filter((p) =>
        p.identifier?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.brand?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.model?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const cartEntries = cart?.entryDtoList || [];
    const hasItems = cartEntries.length > 0;
    const finalAmount = Number(cart?.totalPrice || 0);
    const placedFinalAmount = placedOrder ? Number(placedOrder.totalPrice || 0) : 0;

    const cardFields = {
        cardNumber, cardExpiry, cardCvv, cardErrors,
        onCardNumberChange: handleCardNumberChange,
        onExpiryChange: handleExpiryChange,
        onCvvChange: handleCvvChange,
    };

    return (
        <div className="fixed top-[60px] left-[220px] right-0 bottom-0 bg-gray-50 font-sans flex flex-col overflow-hidden">

            <div className="bg-white border-b border-gray-200 px-6 py-3 flex items-center gap-4 shrink-0 print:hidden">
                <button
                    onClick={() => router.push("/home")}
                    className="px-4 py-2 text-[13px] font-semibold text-[#2d6a4f] border-[1.5px] border-[#2d6a4f] rounded-lg bg-transparent cursor-pointer shrink-0 hover:bg-[#f0faf5]"
                >
                    ← Home
                </button>

                <CustomerSearchBar
                    phoneInput={phoneInput}
                    onPhoneChange={handlePhoneChange}
                    searchingCustomer={searchingCustomer}
                    selectedCustomer={selectedCustomer}
                    customerError={customerError}
                    onClearCustomer={() => { setSelectedCustomer(null); setPhoneInput(""); setCustomerError(""); }}
                    onAddCustomer={openAddCustomer}
                />
            </div>

            <div className="flex flex-1 overflow-hidden print:hidden">

                <div className="flex-1 overflow-y-auto p-6 flex flex-col min-w-0">
                    <div className="mb-4 shrink-0">
                        <input
                            type="text"
                            placeholder="Search by name, brand, model or SKU…"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full max-w-[360px] px-4 py-2 border-[1.5px] border-gray-300 rounded-lg text-[13px] outline-none bg-white focus:border-[#2d6a4f]"
                        />
                    </div>

                    {error && (
                        <div className="bg-red-50 border border-red-300 text-red-700 rounded-lg px-4 py-3 text-[13px] text-center mb-4">
                            {error}
                        </div>
                    )}

                    {loadingProducts ? (
                        <p className="text-center text-gray-400 text-sm py-10">Loading products…</p>
                    ) : (
                        <ProductGrid
                            filteredProducts={filteredProducts}
                            quantities={quantities}
                            prices={prices}
                            cartId={CART_ID}
                            loadingAction={loadingAction}
                            onAdd={handleAdd}
                            onReduce={handleReduce}
                        />
                    )}
                </div>

                <CartPanel
                    selectedCustomer={selectedCustomer}
                    cartInitialized={cartInitialized}
                    hasItems={hasItems}
                    cartEntries={cartEntries}
                    productMap={productMap}
                    deletingEntry={deletingEntry}
                    onDeleteEntry={handleDeleteEntry}
                    cart={cart}
                    finalAmount={finalAmount}
                    loadingAction={loadingAction}
                    onCheckout={openPaymentModal}
                    onClearCart={() => setShowConfirm(true)}
                />
            </div>

            {showAddCustomer && (
                <AddCustomerModal
                    newCustomer={newCustomer}
                    onFieldChange={handleNewCustomerFieldChange}
                    addCustomerError={addCustomerError}
                    addingCustomer={addingCustomer}
                    onCancel={() => setShowAddCustomer(false)}
                    onSubmit={handleAddCustomer}
                />
            )}

            {showConfirm && (
                <ClearCartModal
                    customerName={selectedCustomer?.customerName}
                    onCancel={() => setShowConfirm(false)}
                    onConfirm={confirmDeleteCart}
                />
            )}

            {showPayment && (
                <PaymentModal
                    finalAmount={finalAmount}
                    customerName={selectedCustomer?.customerName}
                    paymentMode={paymentMode}
                    onSelectMode={handleSelectPaymentMode}
                    cashReceived={cashReceived}
                    onToggleCash={() => setCashReceived(!cashReceived)}
                    cardFields={cardFields}
                    placingOrder={placingOrder}
                    onClose={() => setShowPayment(false)}
                    onPlaceOrder={handlePlaceOrder}
                />
            )}

            {showSuccess && placedOrder && (
                <OrderSuccessOverlay
                    placedOrder={placedOrder}
                    productMap={productMap}
                    finalAmount={placedFinalAmount}
                    customerName={selectedCustomer?.customerName}
                    onPrint={handlePrintReceipt}
                    onNewSale={handleNewSale}
                    onViewOrders={() => router.push("/order/list")}
                />
            )}

            <PrintableReceipt order={placedOrder} productMap={productMap} finalAmount={placedFinalAmount} />

            <style>{`
                @media print {
                    body * { visibility: hidden; }
                    .print\\:block, .print\\:block * { visibility: visible; }
                }
            `}</style>
        </div>
    );
}