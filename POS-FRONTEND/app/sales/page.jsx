"use client";
import { useState, useEffect, useCallback, useRef } from "react";
import PropTypes from "prop-types";
import { useRouter } from "next/navigation";
import axios from "axios";
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
    amber: "#92400e",
    amberBg: "#fffbeb",
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

function buildUpiPayload({ payeeVpa, payeeName, amount, note, txnRef }) {
    const params = new URLSearchParams({
        pa: payeeVpa,
        pn: payeeName,
        am: amount,
        cu: "INR",
        tn: note,
        tr: txnRef,
    });
    return `upi://pay?${params.toString()}`;
}

function getToken() {
    return globalThis.window === undefined ? null : globalThis.window.localStorage.getItem("token");
}

function getAuthConfig() {
    const token = getToken();
    return token ? { headers: { Authorization: token.startsWith("Bearer ") ? token : `Bearer ${token}` } } : {};
}

function getAuthParamsConfig(params = {}) {
    const token = getToken();
    const baseConfig = { params };
    return token ? { ...baseConfig, headers: { Authorization: token.startsWith("Bearer ") ? token : `Bearer ${token}` } } : baseConfig;
}

function mergePriceList(products, priceList) {
    return products.map(product => {
        const priceObj = priceList.find(pr => pr.identifier === product.identifier || pr.product === product.identifier);
        return priceObj ? { ...product, sellingPrice: priceObj.sellingPrice, mrp: priceObj.mrp } : product;
    });
}

function generateReceiptHTML({
    orderId,
    orderTimestamp,
    receiptCustomerName,
    paymentType,
    receiptEntries,
    receiptSubtotal,
    receiptDiscount,
    receiptTotal,
}) {
    const itemsHtml = receiptEntries.length === 0
        ? `<tr><td colspan="4" style="text-align:center; padding:15px 0; font-size:12px; color:#999;">No items found</td></tr>`
        : receiptEntries.map(entry => {
            const qty = Number(entry.quantity ?? 0);
            const price = Number(entry.sellingPrice ?? 0);
            const lineTotal = Number(entry.totalPrice ?? (price * qty)).toFixed(2);
            const displayName = entry.productName || entry.name || entry.product || entry.identifier || "Item";
            const skuCode = entry.sku || entry.product || entry.identifier || "—";
            return `
                <tr>
                    <td style="padding: 10px; border-bottom: 1px solid #f0f0f0;">${displayName}</td>
                    <td style="padding: 10px; border-bottom: 1px solid #f0f0f0; text-align: center;">${qty}</td>
                    <td style="padding: 10px; border-bottom: 1px solid #f0f0f0; text-align: right;">₹${price.toFixed(2)}</td>
                    <td style="padding: 10px; border-bottom: 1px solid #f0f0f0; text-align: right; font-weight: 600;">₹${lineTotal}</td>
                </tr>
                <tr>
                    <td colspan="4" style="padding: 2px 10px; font-size: 11px; color: #999;">SKU: ${skuCode}</td>
                </tr>
            `;
        }).join("");

    return `
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Receipt - ${orderId}</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Courier New', monospace;
            background: #f5f5f5;
            padding: 20px;
        }
        
        .receipt-container {
            max-width: 80mm;
            width: 100%;
            background: white;
            margin: 0 auto;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            page-break-after: always;
        }
        
        .receipt-header {
            text-align: center;
            margin-bottom: 20px;
            border-bottom: 2px solid #333;
            padding-bottom: 15px;
        }
        
        .receipt-title {
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 5px;
            letter-spacing: 2px;
        }
        
        .receipt-timestamp {
            font-size: 11px;
            color: #666;
            margin-bottom: 5px;
        }
        
        .receipt-divider {
            border-bottom: 1px dashed #333;
            margin: 12px 0;
        }
        
        .receipt-info {
            font-size: 12px;
            margin-bottom: 15px;
        }
        
        .receipt-info-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            padding: 3px 0;
        }
        
        .receipt-info-row strong {
            font-weight: bold;
        }
        
        .receipt-items {
            margin: 15px 0;
        }
        
        .items-header {
            text-align: center;
            font-weight: bold;
            margin-bottom: 10px;
            text-decoration: underline;
            font-size: 12px;
        }
        
        .receipt-items table {
            width: 100%;
            border-collapse: collapse;
            font-size: 11px;
            margin-bottom: 10px;
        }
        
        .receipt-items th {
            font-weight: bold;
            text-align: left;
            padding: 5px;
            border-bottom: 1px solid #333;
            font-size: 11px;
        }
        
        .receipt-items td {
            padding: 5px;
            word-break: break-word;
        }
        
        .receipt-summary {
            margin-top: 15px;
            font-size: 12px;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 5px;
            padding: 3px 0;
        }
        
        .summary-row.total {
            font-weight: bold;
            font-size: 14px;
            border-top: 1px dashed #333;
            border-bottom: 1px dashed #333;
            padding: 8px 0;
            margin-top: 10px;
        }
        
        .receipt-footer {
            text-align: center;
            margin-top: 20px;
            font-size: 11px;
            color: #666;
            font-style: italic;
            padding-top: 10px;
            border-top: 1px dashed #333;
        }
        
        @media print {
            body {
                background: white;
                padding: 0;
            }
            .receipt-container {
                box-shadow: none;
                max-width: 100%;
                width: 100%;
                padding: 10mm;
                margin: 0;
            }
        }
        
        @page {
            size: 80mm auto;
            margin: 0;
            padding: 0;
        }
    </style>
</head>
<body>
    <div class="receipt-container">
        <div class="receipt-header">
            <div class="receipt-title">RECEIPT</div>
            <div class="receipt-timestamp">${orderTimestamp}</div>
        </div>
        
        <div class="receipt-divider"></div>
        
        <div class="receipt-info">
            <div class="receipt-info-row">
                <span>Order No:</span>
                <strong>${orderId || "—"}</strong>
            </div>
            <div class="receipt-info-row">
                <span>Customer:</span>
                <span>${receiptCustomerName || "Walk-in"}</span>
            </div>
            <div class="receipt-info-row">
                <span>Payment:</span>
                <span>${paymentType}</span>
            </div>
        </div>
        
        <div class="receipt-divider"></div>
        
        <div class="receipt-items">
            <div class="items-header">ITEMS PURCHASED</div>
            <table>
                <thead>
                    <tr>
                        <th>Item</th>
                        <th style="text-align: center;">Qty</th>
                        <th style="text-align: right;">Rate</th>
                        <th style="text-align: right;">Total</th>
                    </tr>
                </thead>
                <tbody>
                    ${itemsHtml}
                </tbody>
            </table>
        </div>
        
        <div class="receipt-divider"></div>
        
        <div class="receipt-summary">
            <div class="summary-row">
                <span>Subtotal:</span>
                <span>₹${receiptSubtotal.toFixed(2)}</span>
            </div>
            <div class="summary-row">
                <span>Discount:</span>
                <span>-₹${receiptDiscount.toFixed(2)}</span>
            </div>
            <div class="summary-row total">
                <span>TOTAL AMOUNT PAID:</span>
                <span>₹${receiptTotal.toFixed(2)}</span>
            </div>
        </div>
        
        <div class="receipt-footer">
            Thank you for your purchase!<br>
            Please visit us again.
        </div>
    </div>
    
    <script>
        // Wait for content to load before printing
        window.addEventListener('load', function() {
            setTimeout(function() {
                window.print();
            }, 100);
        });
    </script>
</body>
</html>
    `;
}

function OrderConfirmedScreen({
    isSidebarOpen,
    paymentType,
    receiptTotal,
    handleReceiptPrint,
    startNewSale,
    showViewReceiptModal,
    setShowViewReceiptModal,
    orderTimestamp,
    orderReceipt,
    receiptCustomerName,
    receiptEntries,
    receiptSubtotal,
    receiptDiscount,
}) {
    return (
        <div style={{ position: "fixed", top: "60px", right: 0, bottom: 0, left: isSidebarOpen ? "220px" : "55px", backgroundColor: "#f4f5f9", display: "flex", alignItems: "center", justifyContent: "center", fontFamily: "'Segoe UI', sans-serif", transition: "left 0.2s ease" }}>
            <div style={{ background: "#fff", padding: "40px", borderRadius: "12px", width: "480px", textAlign: "center", boxShadow: "0 4px 20px rgba(0,0,0,0.08)" }}>
                <div style={{ width: "60px", height: "64px", background: C.greenBg, borderRadius: "50%", display: "flex", alignItems: "center", justifyContent: "center", margin: "0 auto 16px" }}>
                    <span style={{ fontSize: "28px", color: C.green }}>✓</span>
                </div>
                <h2 style={{ fontSize: "22px", fontWeight: "700", color: C.navy, margin: "0 0 8px" }}>Order Completed Successfully!</h2>
                <p style={{ fontSize: "13px", color: C.muted, margin: "0 0 8px" }}>Paid via {paymentType} · ₹{receiptTotal.toFixed(2)}</p>
                <p style={{ fontSize: "13px", color: C.muted, margin: "0 0 32px" }}>The records have been updated securely. Select options below to view or print the receipt.</p>

                <div style={{ display: "flex", gap: "12px" }}>
                    <button type="button" onClick={() => setShowViewReceiptModal(true)} style={{ flex: 1, height: "42px", border: `1.5px solid ${C.navy}`, background: "#fff", color: C.navy, borderRadius: "8px", fontWeight: "600", fontSize: "13px", cursor: "pointer" }}>
                        👁 View Receipt
                    </button>
                    <button type="button" onClick={handleReceiptPrint} style={{ flex: 1, height: "42px", background: C.mid, color: "#fff", border: "none", borderRadius: "8px", fontWeight: "600", fontSize: "13px", cursor: "pointer" }}>
                        🖨 Print Receipt
                    </button>
                </div>

                <button type="button" onClick={startNewSale} style={{ width: "100%", height: "42px", background: C.navy, color: "#fff", border: "none", borderRadius: "8px", fontWeight: "700", fontSize: "13px", marginTop: "12px", cursor: "pointer" }}>
                    Start Next Order Session ➔
                </button>
            </div>

            {showViewReceiptModal && (
                <div style={{ position: "fixed", top: 0, right: 0, bottom: 0, left: 0, background: "rgba(0,0,0,0.5)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 20000 }}>
                    <div style={{ background: "#fff", padding: "32px", borderRadius: "8px", width: "460px", maxHeight: "90vh", overflowY: "auto", boxShadow: "0 10px 25px rgba(0,0,0,0.2)" }}>
                        <div style={{ padding: "20px", fontFamily: "Courier, monospace", color: "#000", background: "#fff", border: "1px solid #e2e8f0" }}>
                            <div style={{ textAlign: "center", fontSize: "22px", fontWeight: "700", marginBottom: "4px" }}>RECEIPT</div>
                            <div style={{ textAlign: "center", fontSize: "12px", marginBottom: "16px" }}>{orderTimestamp}</div>
                            <div style={{ borderBottom: "1px dashed #000", marginBottom: "12px" }}></div>
                            <div style={{ display: "flex", fontSize: "13px", marginBottom: "4px", justifyContent: "space-between" }}>
                                <span>Order No:</span> <span style={{ fontWeight: "700" }}>{orderReceipt?.orderId || "—"}</span>
                            </div>
                            <div style={{ display: "flex", fontSize: "13px", marginBottom: "4px", justifyContent: "space-between" }}>
                                <span>Customer:</span> <span>{receiptCustomerName}</span>
                            </div>
                            <div style={{ display: "flex", fontSize: "13px", marginBottom: "12px", justifyContent: "space-between" }}>
                                <span>Payment:</span> <span>{paymentType}</span>
                            </div>
                            <div style={{ borderBottom: "1px dashed #000", marginBottom: "12px" }}></div>
                            <div style={{ textAlign: "center", fontSize: "13px", fontWeight: "700", marginBottom: "10px" }}>ITEMS</div>
                            {receiptEntries.length === 0 ? (
                                <div style={{ textAlign: "center", fontSize: "12px", padding: "8px 0" }}>No items found</div>
                            ) : (
                                receiptEntries.map((entry, i) => {
                                    const qty = Number(entry.quantity ?? 0);
                                    const price = Number(entry.sellingPrice ?? 0);
                                    const lineTotal = Number(entry.totalPrice ?? (price * qty)).toFixed(2);
                                    const displayName = entry.productName || entry.name || entry.product || entry.identifier || "Item";
                                    const skuCode = entry.sku || entry.product || entry.identifier || "—";
                                    return (
                                        <div key={entry.identifier || i} style={{ marginBottom: "10px" }}>
                                            <div style={{ fontWeight: "700", fontSize: "13px" }}>{displayName}</div>
                                            <div style={{ fontSize: "11px", color: "#555", marginBottom: "2px" }}>SKU: {skuCode}</div>
                                            <div style={{ display: "flex", justifyContent: "space-between", fontSize: "12px" }}>
                                                <span>{qty} x Rs. {price.toFixed(2)}</span>
                                                <span>Rs. {lineTotal}</span>
                                            </div>
                                        </div>
                                    );
                                })
                            )}
                            <div style={{ borderBottom: "1px dashed #000", marginBottom: "10px", marginTop: "6px" }}></div>
                            <div style={{ display: "flex", fontSize: "13px", marginBottom: "4px", justifyContent: "space-between" }}>
                                <span>Subtotal:</span> <span>Rs. {receiptSubtotal.toFixed(2)}</span>
                            </div>
                            <div style={{ display: "flex", fontSize: "13px", marginBottom: "8px", justifyContent: "space-between" }}>
                                <span>Discount:</span> <span>- Rs. {receiptDiscount.toFixed(2)}</span>
                            </div>
                            <div style={{ borderBottom: "1px dashed #000", marginBottom: "10px" }}></div>
                            <div style={{ display: "flex", fontSize: "15px", fontWeight: "700", justifyContent: "space-between" }}>
                                <span>Amount Paid:</span> <span>Rs. {receiptTotal.toFixed(2)}</span>
                            </div>
                            <div style={{ borderBottom: "1px dashed #000", marginTop: "12px", marginBottom: "16px" }}></div>
                            <div style={{ textAlign: "center", fontSize: "13px", fontStyle: "italic" }}>Thank you for your purchase!</div>
                        </div>

                        <div style={{ display: "flex", gap: "10px", marginTop: "20px" }}>
                            <button type="button" onClick={handleReceiptPrint} style={{ flex: 1, height: "36px", background: C.navy, color: "#fff", border: "none", borderRadius: "6px", fontSize: "13px", fontWeight: "600", cursor: "pointer" }}>Print Document</button>
                            <button type="button" onClick={() => setShowViewReceiptModal(false)} style={{ flex: 1, height: "36px", background: "#e2e8f0", color: C.text, border: "none", borderRadius: "6px", fontSize: "13px", fontWeight: "600", cursor: "pointer" }}>Dismiss Close</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

function OrderReceiptScreen({
    isSidebarOpen,
    today,
    orderReceipt,
    customerLabel,
    receiptEntries,
    receiptSubtotal,
    receiptDiscount,
    receiptTotal,
    showPaymentForm,
    paymentType,
    paymentError,
    paymentProcessing,
    upiStatus,
    upiPayload,
    handleReceiptPrint,
    confirmCashPayment,
    confirmCardPayment,
    startUpiPayment,
    cancelUpiPayment,
    setOrderReceipt,
    setShowPaymentForm,
    setPaymentType,
    setPaymentError,
    cardNumber,
    cardExpiry,
    cardCvv,
    cardName,
    setCardNumber,
    setCardExpiry,
    setCardCvv,
    setCardName,
    formatCardNumber,
    formatExpiry,
}) {
    return (
        <div style={{ position: "fixed", top: "60px", right: 0, bottom: 0, left: isSidebarOpen ? "220px" : "55px", backgroundColor: "#f4f5f9", fontFamily: "'Segoe UI', sans-serif", padding: "24px", overflowY: "auto", transition: "left 0.2s ease" }}>
            <div style={{ maxWidth: "1100px", margin: "0 auto" }}>
                <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: "20px" }}>
                    <div>
                        <span style={{ fontSize: "11px", fontWeight: 700, letterSpacing: "0.1em", textTransform: "uppercase", color: C.mid }}>Checkout Validation</span>
                        <h2 style={{ fontSize: "22px", fontWeight: 700, margin: "4px 0 0", color: C.navy }}>Order Receipt</h2>
                    </div>
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "1.4fr 1fr", gap: "20px", alignItems: "start" }}>
                    <div style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
                        <div style={{ background: "#fff", borderRadius: "8px", border: "1px solid #e2e8f0", overflow: "hidden" }}>
                            <div style={{ padding: "14px", borderBottom: "1px solid #e2e8f0", fontWeight: 700, fontSize: "14px", color: C.navy }}>Items</div>
                            <table style={{ width: "100%", fontSize: "13px", borderCollapse: "collapse" }}>
                                <thead>
                                    <tr style={{ background: "#f8fafc", color: C.mid, borderBottom: "1px solid #e2e8f0", textAlign: "left" }}>
                                        <th style={{ padding: "10px 14px" }}>Product</th>
                                        <th style={{ padding: "10px 14px", textAlign: "center" }}>Qty</th>
                                        <th style={{ padding: "10px 14px", textAlign: "right" }}>Price</th>
                                        <th style={{ padding: "10px 14px", textAlign: "right" }}>Total</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {receiptEntries.map((entry, i) => (
                                        <tr key={entry.identifier || i} style={{ borderBottom: "1px solid #f1f5f9", color: C.text }}>
                                            <td style={{ padding: "10px 14px" }}>
                                                <div style={{ fontWeight: "600" }}>{entry.productName || entry.product}</div>
                                                <div style={{ fontSize: "11px", color: C.muted }}>SKU: {entry.sku || entry.product}</div>
                                            </td>
                                            <td style={{ padding: "10px 14px", textAlign: "center" }}>{entry.quantity}</td>
                                            <td style={{ padding: "10px 14px", textAlign: "right" }}>₹{Number(entry.sellingPrice ?? 0).toFixed(2)}</td>
                                            <td style={{ padding: "10px 14px", textAlign: "right", fontWeight: "600" }}>₹{Number(entry.totalPrice ?? 0).toFixed(2)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        <div style={{ background: "#fff", borderRadius: "8px", border: "1px solid #e2e8f0", padding: "16px", fontSize: "14px" }}>
                            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "8px" }}>
                                <span style={{ color: C.muted }}>Subtotal</span>
                                <span style={{ fontWeight: "600" }}>₹{receiptSubtotal.toFixed(2)}</span>
                            </div>
                            <div style={{ display: "flex", justifyContent: "space-between", color: C.red, marginBottom: "8px" }}>
                                <span>Discount</span>
                                <span style={{ fontWeight: "600" }}>-₹{receiptDiscount.toFixed(2)}</span>
                            </div>
                            <div style={{ display: "flex", justifyContent: "space-between", fontWeight: "700", borderTop: "1px solid #e2e8f0", paddingTop: "10px", marginTop: "10px" }}>
                                <span style={{ color: C.navy }}>Total Due</span>
                                <span style={{ fontSize: "16px", color: C.navy }}>₹{receiptTotal.toFixed(2)}</span>
                            </div>
                        </div>
                    </div>

                    <div style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
                        <div style={{ background: C.navy, color: "#fff", borderRadius: "8px", padding: "18px", fontSize: "13px" }}>
                            <div style={{ marginBottom: "12px" }}>
                                <div style={{ color: C.light, fontSize: "11px", fontWeight: "600" }}>ORDER ID</div>
                                <div style={{ fontSize: "14px", fontWeight: "700", marginTop: "2px" }}>{orderReceipt.orderId || "—"}</div>
                            </div>
                            <div style={{ marginBottom: "12px" }}>
                                <div style={{ color: C.light, fontSize: "11px", fontWeight: "600" }}>CUSTOMER IDENTIFIER</div>
                                <div style={{ fontSize: "14px", fontWeight: "700", marginTop: "2px" }}>{customerLabel}</div>
                            </div>
                            <div style={{ display: "flex", justifyContent: "space-between", borderTop: "1px solid rgba(255,255,255,0.15)", paddingTop: "10px", marginTop: "10px" }}>
                                <span style={{ color: C.light }}>Timestamp</span>
                                <span>{orderReceipt.orderDate ? new Date(orderReceipt.orderDate).toLocaleString("en-GB") : today}</span>
                            </div>
                            <div style={{ display: "flex", justifyContent: "space-between", marginTop: "6px" }}>
                                <span style={{ color: C.light }}>Amount Due</span>
                                <span>₹{receiptTotal.toFixed(2)}</span>
                            </div>
                        </div>

                        {showPaymentForm && (
                            <div style={{ background: "#fff", border: `1.5px solid #cbd5e1`, borderRadius: "8px", padding: "16px" }}>
                                <p style={{ fontSize: "13px", fontWeight: "700", color: C.navy, margin: "0 0 12px" }}>Select Payment Method</p>

                                <div style={{ display: "flex", gap: "6px", marginBottom: "16px" }}>
                                    {[
                                        { key: "Cash", label: "💵 Cash" },
                                        { key: "Card", label: "💳 Card" },
                                        { key: "UPI", label: "📱 UPI" },
                                    ].map(opt => (
                                        <button
                                            key={opt.key}
                                            type="button"
                                            onClick={() => { setPaymentType(opt.key); setPaymentError(""); if (opt.key !== "UPI") cancelUpiPayment(); }}
                                            disabled={paymentProcessing || upiStatus === "waiting" || upiStatus === "scanned"}
                                            style={{
                                                flex: 1, height: "36px", borderRadius: "6px", fontSize: "12.5px", fontWeight: "700",
                                                border: paymentType === opt.key ? `1.5px solid ${C.navy}` : "1.5px solid #e2e8f0",
                                                background: paymentType === opt.key ? C.navy : "#fff",
                                                color: paymentType === opt.key ? "#fff" : C.text,
                                                cursor: "pointer",
                                            }}
                                        >
                                            {opt.label}
                                        </button>
                                    ))}
                                </div>

                                {paymentError && (
                                    <div style={{ background: "#fef2f2", border: "1px solid #fecaca", color: C.red, borderRadius: "6px", padding: "8px 10px", fontSize: "12px", marginBottom: "12px" }}>
                                        {paymentError}
                                    </div>
                                )}

                                {paymentType === "Cash" && (
                                    <div>
                                        <div style={{ background: "#f8fafc", border: "1px solid #e2e8f0", borderRadius: "6px", padding: "14px", marginBottom: "14px", textAlign: "center" }}>
                                            <div style={{ fontSize: "11px", color: C.muted, fontWeight: 600, marginBottom: "4px" }}>AMOUNT TO COLLECT</div>
                                            <div style={{ fontSize: "26px", fontWeight: "800", color: C.navy }}>₹{receiptTotal.toFixed(2)}</div>
                                            <div style={{ fontSize: "11.5px", color: C.muted, marginTop: "6px" }}>Confirm once cash has been received from the customer.</div>
                                        </div>
                                        <div style={{ display: "flex", gap: "8px" }}>
                                            <button type="button" onClick={confirmCashPayment} disabled={paymentProcessing} style={{ flex: 2, height: "40px", background: C.green, color: "#fff", border: "none", borderRadius: "6px", fontWeight: "700", fontSize: "13px", cursor: "pointer" }}>
                                                {paymentProcessing ? "Confirming..." : "Cash Received — Complete Sale"}
                                            </button>
                                            <button type="button" onClick={() => { setOrderReceipt(null); setShowPaymentForm(false); }} style={{ flex: 1, height: "40px", background: "#fff", border: "1px solid #cbd5e1", color: C.text, borderRadius: "6px", fontWeight: "600", fontSize: "13px", cursor: "pointer" }}>Dismiss</button>
                                        </div>
                                    </div>
                                )}

                                {paymentType === "Card" && (
                                    <div>
                                        <div style={{ display: "flex", flexDirection: "column", gap: "10px", marginBottom: "12px" }}>
                                            <div>
                                                <label htmlFor="cardNumber" style={{ fontSize: "11px", fontWeight: "600", color: C.mid, display: "block", marginBottom: "4px" }}>Card Number</label>
                                                <input
                                                    id="cardNumber"
                                                    type="text" inputMode="numeric" placeholder="1234 5678 9012 3456" maxLength={19}
                                                    value={cardNumber} onChange={e => setCardNumber(formatCardNumber(e.target.value))}
                                                    disabled={paymentProcessing}
                                                    style={{ ...inputSt, fontFamily: "monospace", letterSpacing: "0.5px" }}
                                                />
                                            </div>
                                            <div style={{ display: "flex", gap: "10px" }}>
                                                <div style={{ flex: 1 }}>
                                                    <label htmlFor="cardExpiry" style={{ fontSize: "11px", fontWeight: "600", color: C.mid, display: "block", marginBottom: "4px" }}>Expiry (MM/YY)</label>
                                                    <input
                                                        id="cardExpiry"
                                                        type="text" inputMode="numeric" placeholder="MM/YY" maxLength={5}
                                                        value={cardExpiry} onChange={e => setCardExpiry(formatExpiry(e.target.value))}
                                                        disabled={paymentProcessing}
                                                        style={inputSt}
                                                    />
                                                </div>
                                                <div style={{ flex: 1 }}>
                                                    <label htmlFor="cardCvv" style={{ fontSize: "11px", fontWeight: "600", color: C.mid, display: "block", marginBottom: "4px" }}>CVV</label>
                                                    <input
                                                        id="cardCvv"
                                                        type="password" inputMode="numeric" placeholder="•••" maxLength={4}
                                                        value={cardCvv} onChange={e => setCardCvv(e.target.value.replaceAll(/\D/g, "").slice(0, 4))}
                                                        disabled={paymentProcessing}
                                                        style={inputSt}
                                                    />
                                                </div>
                                            </div>
                                            <div>
                                                <label htmlFor="cardName" style={{ fontSize: "11px", fontWeight: "600", color: C.mid, display: "block", marginBottom: "4px" }}>Name on Card</label>
                                                <input
                                                    id="cardName"
                                                    type="text" placeholder="As printed on card"
                                                    value={cardName} onChange={e => setCardName(e.target.value)}
                                                    disabled={paymentProcessing}
                                                    style={inputSt}
                                                />
                                            </div>
                                        </div>
                                        <div style={{ display: "flex", justifyContent: "space-between", fontSize: "12px", color: C.muted, marginBottom: "12px" }}>
                                            <span>Charge amount</span>
                                            <span style={{ fontWeight: "700", color: C.navy }}>₹{receiptTotal.toFixed(2)}</span>
                                        </div>
                                        <div style={{ display: "flex", gap: "8px" }}>
                                            <button type="button" onClick={confirmCardPayment} disabled={paymentProcessing} style={{ flex: 2, height: "40px", background: C.navy, color: "#fff", border: "none", borderRadius: "6px", fontWeight: "700", fontSize: "13px", cursor: paymentProcessing ? "not-allowed" : "pointer", opacity: paymentProcessing ? 0.75 : 1 }}>
                                                {paymentProcessing ? "Authorizing with bank..." : "Charge Card"}
                                            </button>
                                            <button type="button" onClick={() => { setOrderReceipt(null); setShowPaymentForm(false); }} disabled={paymentProcessing} style={{ flex: 1, height: "40px", background: "#fff", border: "1px solid #cbd5e1", color: C.text, borderRadius: "6px", fontWeight: "600", fontSize: "13px", cursor: "pointer" }}>Dismiss</button>
                                        </div>
                                    </div>
                                )}

                                {paymentType === "UPI" && (
                                    <div>
                                        {upiStatus === "idle" && (
                                            <div style={{ textAlign: "center" }}>
                                                <div style={{ background: "#f8fafc", border: "1px solid #e2e8f0", borderRadius: "6px", padding: "14px", marginBottom: "14px" }}>
                                                    <div style={{ fontSize: "11px", color: C.muted, fontWeight: 600, marginBottom: "4px" }}>AMOUNT TO PAY</div>
                                                    <div style={{ fontSize: "26px", fontWeight: "800", color: C.navy }}>₹{receiptTotal.toFixed(2)}</div>
                                                </div>
                                                <button type="button" onClick={startUpiPayment} style={{ width: "100%", height: "40px", background: C.navy, color: "#fff", border: "none", borderRadius: "6px", fontWeight: "700", fontSize: "13px", cursor: "pointer", marginBottom: "8px" }}>
                                                    Generate QR Code
                                                </button>
                                                <button type="button" onClick={() => { setOrderReceipt(null); setShowPaymentForm(false); }} style={{ width: "100%", height: "36px", background: "#fff", border: "1px solid #cbd5e1", color: C.text, borderRadius: "6px", fontWeight: "600", fontSize: "13px", cursor: "pointer" }}>Dismiss</button>
                                            </div>
                                        )}

                                        {(upiStatus === "waiting" || upiStatus === "scanned") && (
                                            <div style={{ textAlign: "center" }}>
                                                <div style={{
                                                    display: "inline-block", padding: "12px", background: "#fff",
                                                    border: `2px solid ${upiStatus === "scanned" ? C.green : "#e2e8f0"}`,
                                                    borderRadius: "10px", marginBottom: "12px", transition: "border-color 0.3s",
                                                }}>
                                                    <UpiQrCode value={upiPayload} size={168} />
                                                </div>
                                                <div style={{ fontSize: "20px", fontWeight: "800", color: C.navy, marginBottom: "4px" }}>₹{receiptTotal.toFixed(2)}</div>
                                                <div style={{
                                                    display: "inline-flex", alignItems: "center", gap: "6px",
                                                    fontSize: "12.5px", fontWeight: "600",
                                                    color: upiStatus === "scanned" ? C.green : C.amber,
                                                    background: upiStatus === "scanned" ? C.greenBg : C.amberBg,
                                                    padding: "5px 12px", borderRadius: "20px", marginBottom: "14px",
                                                }}>
                                                    <span style={{
                                                        width: "7px", height: "7px", borderRadius: "50%",
                                                        background: upiStatus === "scanned" ? C.green : C.amber,
                                                        display: "inline-block", animation: "upi-pulse 1.2s ease-in-out infinite",
                                                    }} />
                                                    {upiStatus === "scanned" ? "QR scanned — confirming with bank…" : "Waiting for customer to scan…"}
                                                </div>
                                                <div style={{ fontSize: "11.5px", color: C.muted, marginBottom: "14px" }}>
                                                    Open any UPI app (GPay, PhonePe, Paytm) and scan this code to pay.
                                                </div>
                                                <button type="button" onClick={cancelUpiPayment} style={{ width: "100%", height: "36px", background: "#fff", border: "1px solid #cbd5e1", color: C.text, borderRadius: "6px", fontWeight: "600", fontSize: "13px", cursor: "pointer" }}>Cancel</button>
                                                <style>{`@keyframes upi-pulse {0%,100%{opacity:1;}50%{opacity:0.3;}}`}</style>
                                            </div>
                                        )}

                                        {upiStatus === "success" && (
                                            <div style={{ textAlign: "center", padding: "10px 0" }}>
                                                <div style={{ width: "48px", height: "48px", borderRadius: "50%", background: C.greenBg, display: "flex", alignItems: "center", justifyContent: "center", margin: "0 auto 10px" }}>
                                                    <span style={{ fontSize: "22px", color: C.green }}>✓</span>
                                                </div>
                                                <div style={{ fontSize: "14px", fontWeight: "700", color: C.green }}>Payment received</div>
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default function SalesPage() {
    const router = useRouter();
    const isSidebarOpen = useSidebarOpen();

    const [phoneSearch, setPhoneSearch] = useState("");
    const [searchingCustomer, setSearchingCustomer] = useState(false);
    const [customerFound, setCustomerFound] = useState(null);
    const [bannerDismissed, setBannerDismissed] = useState(false);
    const [allCustomersList, setAllCustomersList] = useState([]);
    const [filteredCustomers, setFilteredCustomers] = useState([]);
    const [showCustomerDropdown, setShowCustomerDropdown] = useState(false);
    const dropdownRef = useRef(null);
    const [showAddModal, setShowAddModal] = useState(false);
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [productSearch, setProductSearch] = useState("");
    const [newCustomer, setNewCustomer] = useState({ identifier: "", customerName: "", email: "" });
    const [addingCustomer, setAddingCustomer] = useState(false);
    const [addCustomerError, setAddCustomerError] = useState("");
    const [cart, setCart] = useState(null);
    const [entries, setEntries] = useState([]);
    const [paymentType, setPaymentType] = useState("Cash");
    const [actionLoading, setActionLoading] = useState(false);
    const [processingEntry, setProcessingEntry] = useState(null);
    const [error, setError] = useState("");
    const [toast, setToast] = useState(null);

    const [orderReceipt, setOrderReceipt] = useState(null);
    const [receiptCustomerName, setReceiptCustomerName] = useState("");
    const [showPaymentForm, setShowPaymentForm] = useState(false);
    const [paymentProcessing, setPaymentProcessing] = useState(false);
    const [isOrderConfirmed, setIsOrderConfirmed] = useState(false);
    const [showViewReceiptModal, setShowViewReceiptModal] = useState(false);
    const [paymentError, setPaymentError] = useState("");

    const [cardNumber, setCardNumber] = useState("");
    const [cardExpiry, setCardExpiry] = useState("");
    const [cardCvv, setCardCvv] = useState("");
    const [cardName, setCardName] = useState("");

    const [upiStatus, setUpiStatus] = useState("idle");
    const upiPollRef = useRef(null);
    const upiTxnRef = useRef(null);

    const today = new Date().toLocaleDateString("en-GB");

    const showToast = (msg, type = "success") => {
        setToast({ msg, type });
        setTimeout(() => setToast(null), 3000);
    };

    useEffect(() => {
        function handleClickOutside(event) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setShowCustomerDropdown(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    useEffect(() => {
        const config = getAuthConfig();

        api.get("/customer/findByStatus", config)
            .then(res => setAllCustomersList(Array.isArray(res.data) ? res.data : []))
            .catch(() => { });

        api.get("/product/findByStatus", config)
            .then(res => {
                const data = res.data;
                const list = Array.isArray(data) ? data : (data.dtoList ?? []);
                setProducts(list);
                setFilteredProducts(list);
            })
            .catch(() => { });

    }, []);

    useEffect(() => {
        if (products.length === 0) return;
        const config = getAuthConfig();

        api.get("/price/findByStatus", config)
            .then(res => {
                const priceList = Array.isArray(res.data) ? res.data : (res.data?.dtoList ?? []);
                setProducts(prev => mergePriceList(prev, priceList));
            })
            .catch(() => { });
    }, [products.length]);

    useEffect(() => {
        if (!phoneSearch.trim() || customerFound) {
            setFilteredCustomers([]);
            return;
        }
        const term = phoneSearch.toLowerCase();
        const matches = allCustomersList.filter(c => {
            const name = (c.customerName || c.name || "").toLowerCase();
            const phone = (c.phone || c.identifier || "").toLowerCase();
            return name.includes(term) || phone.includes(term);
        });
        setFilteredCustomers(matches.slice(0, 8));
    }, [phoneSearch, allCustomersList, customerFound]);

    useEffect(() => {
        let list = [...products];
        if (productSearch) {
            const s = productSearch.toLowerCase();
            list = list.filter(p =>
                p.name?.toLowerCase().includes(s) || p.identifier?.toLowerCase().includes(s)
            );
        }
        setFilteredProducts(list);
    }, [productSearch, products]);

    useEffect(() => {
        return () => { if (upiPollRef.current) clearInterval(upiPollRef.current); };
    }, []);

    const refreshCart = useCallback(async (identifier) => {
        if (!identifier) return null;
        try {
            const token = getToken();
            const baseURL = api.defaults.baseURL || "";
            const config = {
                headers: {
                    "Content-Type": "application/json",
                    ...(token ? { Authorization: token.startsWith("Bearer ") ? token : `Bearer ${token}` } : {})
                }
            };
            const res = await axios.post(`${baseURL}/cart/getCart`, { identifier }, config);
            const data = res.data;
            setCart(data);
            setEntries(data?.cartEntryDtoList ?? []);
            setError("");
            return data;
        } catch (err) {
            const status = err.response?.status;
            setError(`Failed to load cart structure (Status Code: ${status || 500}).`);
            throw err;
        }
    }, []);

    const handleCustomerSelect = async (identifier) => {
        setCart(null);
        setEntries([]);
        setError("");
        if (!identifier) return;
        try {
            await refreshCart(identifier);
        } catch {
            try {
                const config = getAuthConfig();
                const createRes = await api.post("/cart/add", { identifier, status: true }, config);
                if (createRes.data) {
                    setCart(createRes.data);
                    setEntries(createRes.data.cartEntryDtoList ?? []);
                }
                await refreshCart(identifier);
            } catch {
                setError("Failed to initialize cart session for this customer.");
            }
        }
    };

    const handlePhoneSearch = async (forcedPhone = null) => {
        const phone = (forcedPhone || phoneSearch).trim();
        if (!phone) return;
        setSearchingCustomer(true);
        setCustomerFound(null);
        setBannerDismissed(false);
        setError("");
        setCart(null);
        setEntries([]);
        setShowCustomerDropdown(false);
        try {
            const config = getAuthParamsConfig({ identifier: phone });
            const res = await api.get("/customer/findByStatus", config);
            const data = res.data;

            let customer = null;
            if (Array.isArray(data)) {
                customer = data.find(c => String(c.phone).trim() === phone || String(c.identifier).trim() === phone);
            } else if (data && (String(data.phone).trim() === phone || String(data.identifier).trim() === phone)) {
                customer = data;
            }

            if (!customer) {
                throw new Error("not found");
            }

            setCustomerFound(customer);
            setPhoneSearch(phone);
            await handleCustomerSelect(phone);
        } catch {
            setCustomerFound(null);
            setError("__NOT_FOUND__");
        }
        setSearchingCustomer(false);
    };

    const handleClearCustomer = () => {
        setCustomerFound(null);
        setBannerDismissed(false);
        setPhoneSearch("");
        setCart(null);
        setEntries([]);
        setError("");
    };

    const handleAddProduct = async (product) => {
        const explicitCartId = cart?.identifier || phoneSearch.trim();
        if (!explicitCartId) {
            setError("Please search or select a customer first.");
            return;
        }
        if (actionLoading || processingEntry) return;
        const price = Number(product.sellingPrice || product.mrp || product.price || 0);
        if (!price || price <= 0) {
            showToast("This product has no price set and cannot be added.", "error");
            return;
        }
        setActionLoading(true);
        setError("");
        try {
            const config = getAuthConfig();
            await api.post("/cartEntry/addEntry", {
                cart: explicitCartId,
                product: product.identifier,
                quantity: 1,
            }, config);
            await refreshCart(explicitCartId);
            showToast("Item added to workspace.");
        } catch {
            showToast("Failed to write product entry.", "error");
        } finally {
            setActionLoading(false);
        }
    };

    const handleQtyChange = async (entry, delta) => {
        if (processingEntry === entry.product) return;
        const newQty = Number(entry.quantity) + delta;
        setProcessingEntry(entry.product);
        setError("");
        const cartId = entry.cart || cart?.identifier || phoneSearch.trim();
        try {
            const config = getAuthConfig();
            if (newQty <= 0) {
                await api.put("/cart/deleteEntry", null, {
                    params: { identifier: entry.identifier, cart: cartId },
                    ...config
                });
            } else {
                await api.post("/cartEntry/addEntry", {
                    cart: cartId,
                    product: entry.product,
                    quantity: delta,
                }, config);
            }
            await refreshCart(cartId);
        } catch {
            showToast("Failed to adjust item count.", "error");
        } finally {
            setProcessingEntry(null);
        }
    };

    const handleDeleteEntry = async (entry) => {
        setProcessingEntry(entry.product);
        setError("");
        const cartId = entry.cart || cart?.identifier || phoneSearch.trim();
        try {
            const config = getAuthParamsConfig({ identifier: entry.identifier, cart: cartId });
            await api.put("/cart/deleteEntry", null, config);
            await refreshCart(cartId);
            showToast("Item removed successfully.");
        } catch {
            showToast("Failed to remove item.", "error");
        } finally {
            setProcessingEntry(null);
        }
    };

    const handleSale = async () => {
        const cartId = cart?.identifier || phoneSearch.trim();
        if (!cartId) { showToast("Select a customer first.", "error"); return; }
        if (entries.length === 0) { showToast("Cart workspace is empty.", "error"); return; }
        setActionLoading(true);
        setError("");
        setOrderReceipt(null);
        try {
            const config = getAuthConfig();
            const res = await api.post("/order/place", {
                identifier: cartId,
                paymentMode: paymentType,
            }, config);

            const createdOrder = res.data;
            if (createdOrder?.success === false) {
                setError(createdOrder.message || "Failed to transmit order entries.");
                return;
            }

            const enrichedEntries = (createdOrder?.entryDtoList || []).map(e => {
                const matchedProduct = products.find(p => p.identifier === e.product);
                return {
                    ...e,
                    productName: matchedProduct?.name || e.productName || e.product,
                    sku: matchedProduct?.sku || matchedProduct?.identifier || e.product,
                };
            });

            setOrderReceipt({ ...createdOrder, entryDtoList: enrichedEntries });
            setReceiptCustomerName(customerFound?.customerName || customerFound?.name || "Walk-in");
            setShowPaymentForm(true);
            setPaymentType("Cash");
            setPaymentError("");
            setCardNumber(""); setCardExpiry(""); setCardCvv(""); setCardName("");
            setUpiStatus("idle");

            setCart(null);
            setEntries([]);
            setPhoneSearch("");
            setCustomerFound(null);
            setBannerDismissed(false);
        } catch {
            showToast("Sale transmission failed. Please retry.", "error");
        } finally {
            setActionLoading(false);
        }
    };

    const finalizePayment = () => {
        setPaymentProcessing(true);
        setTimeout(() => {
            setPaymentProcessing(false);
            setShowPaymentForm(false);
            setIsOrderConfirmed(true);
        }, 500);
    };

    const confirmCashPayment = () => {
        setPaymentError("");
        finalizePayment();
    };

    const formatCardNumber = (val) => {
        const digits = val.replaceAll(/\D/g, "").slice(0, 16);
        return digits.replaceAll(/(.{4})/g, "$1 ").trim();
    };
    const formatExpiry = (val) => {
        const digits = val.replaceAll(/\D/g, "").slice(0, 4);
        if (digits.length <= 2) return digits;
        return `${digits.slice(0, 2)}/${digits.slice(2)}`;
    };
    const isCardValid = () => {
        const digits = cardNumber.replaceAll(/\s/g, "");
        const expiryOk = /^\d{2}\/\d{2}$/.test(cardExpiry) && (() => {
            const [mm, yy] = cardExpiry.split("/").map(Number);
            if (mm < 1 || mm > 12) return false;
            const expDate = new Date(2000 + yy, mm, 0);
            return expDate >= new Date();
        })();
        return digits.length >= 15 && digits.length <= 16 && expiryOk && cardCvv.length >= 3 && cardName.trim().length > 1;
    };
    const confirmCardPayment = () => {
        if (!isCardValid()) {
            setPaymentError("Check the card number, expiry, CVV and name on card.");
            return;
        }
        setPaymentError("");
        setPaymentProcessing(true);
        setTimeout(() => {
            setPaymentProcessing(false);
            setShowPaymentForm(false);
            setIsOrderConfirmed(true);
        }, 1400);
    };

    const startUpiPayment = () => {
        upiTxnRef.current = `TXN${Date.now()}`;
        setUpiStatus("waiting");
        setPaymentError("");

        let elapsed = 0;
        upiPollRef.current = setInterval(() => {
            elapsed += 1;
            if (elapsed === 3) {
                setUpiStatus("scanned");
            } else if (elapsed >= 5) {
                clearInterval(upiPollRef.current);
                setUpiStatus("success");
                setTimeout(() => {
                    setShowPaymentForm(false);
                    setIsOrderConfirmed(true);
                }, 700);
            }
        }, 1000);
    };

    const cancelUpiPayment = () => {
        if (upiPollRef.current) clearInterval(upiPollRef.current);
        setUpiStatus("idle");
    };

    const startNewSale = () => {
        setOrderReceipt(null);
        setReceiptCustomerName("");
        setShowPaymentForm(false);
        setIsOrderConfirmed(false);
        setShowViewReceiptModal(false);
        setPaymentType("Cash");
        setPaymentError("");
        setUpiStatus("idle");
        setCardNumber(""); setCardExpiry(""); setCardCvv(""); setCardName("");
        setError("");
    };

    const handleAddCustomer = async () => {
        const phoneRegex = /^\d{10}$/;
        const emailRegex = /^[^\s@]+@[^\s@.]+(?:\.[^\s@.]+)+$/;
        if (!newCustomer.identifier || !newCustomer.customerName) {
            setAddCustomerError("Phone number and name are required.");
            return;
        }
        if (!phoneRegex.test(newCustomer.identifier)) {
            setAddCustomerError("Phone number must be exactly 10 digits with no letters or special characters.");
            return;
        }
        if (newCustomer.email && !emailRegex.test(newCustomer.email)) {
            setAddCustomerError("Please enter a valid email address (e.g. user@example.com).");
            return;
        }
        setAddingCustomer(true);
        setAddCustomerError("");
        try {
            const config = getAuthConfig();
            const res = await api.post("/customer/add", {
                identifier: newCustomer.identifier,
                customerName: newCustomer.customerName,
                email: newCustomer.email || "",
                partyType: "Customer",
                billingAddress: { phoneNo: newCustomer.identifier, addressType: "Billing" },
                shippingAddress: { phoneNo: newCustomer.identifier, addressType: "Shipping" },
            }, config);
            if (res.data?.success === false) {
                setAddCustomerError(res.data.message || "Customer already exists with this phone number.");
                return;
            }
            if (res.data) {
                setCustomerFound(res.data);
                setPhoneSearch(res.data.identifier);
                setAllCustomersList(prev => [res.data, ...prev]);
                await handleCustomerSelect(res.data.identifier);
                setShowAddModal(false);
                setError("");
                setNewCustomer({ identifier: "", customerName: "", email: "" });
                showToast("Customer created successfully.");
            } else {
                setAddCustomerError("Failed to create customer.");
            }
        } catch {
            setAddCustomerError("Failed to create customer. Please try again.");
        } finally {
            setAddingCustomer(false);
        }
    };

    const handleCancel = async () => {
        const cartId = cart?.identifier || phoneSearch.trim();
        if (!cartId) return;
        setActionLoading(true);
        try {
            const config = getAuthConfig();
            await api.put("/cart/deleteCart", { identifier: cartId }, config);
        } catch { /* swallow */ }
        setCart(null);
        setEntries([]);
        setPhoneSearch("");
        setCustomerFound(null);
        setBannerDismissed(false);
        setError("");
        setActionLoading(false);
    };

    const handleReceiptPrint = () => {
        const receiptEntries = orderReceipt?.entryDtoList || [];
        const receiptSubtotal = receiptEntries.reduce((sum, e) => sum + (Number(e.totalPrice) || 0), 0);
        const receiptDiscount = Number(orderReceipt?.totalDiscount) || 0;
        const receiptTotal = Number(orderReceipt?.totalPrice ?? (receiptSubtotal - receiptDiscount));
        const orderTimestamp = orderReceipt?.orderDate
            ? new Date(orderReceipt.orderDate).toLocaleString("en-GB", { hour12: true }).toLowerCase()
            : new Date().toLocaleString("en-GB", { hour12: true }).toLowerCase();

        const htmlContent = generateReceiptHTML({
            orderId: orderReceipt?.orderId || "—",
            orderTimestamp,
            receiptCustomerName,
            paymentType,
            receiptEntries,
            receiptSubtotal,
            receiptDiscount,
            receiptTotal,
        });

        const newWindow = window.open("", "_blank", "width=800,height=900");
        if (newWindow) {
            const blob = new Blob([htmlContent], { type: "text/html" });
            const url = URL.createObjectURL(blob);
            newWindow.location.href = url;
            newWindow.addEventListener("load", () => URL.revokeObjectURL(url));
        } else {
            alert("Please allow pop-ups in your browser to print receipts.");
        }
    };

    const subTotal = entries.reduce((sum, e) => sum + (Number(e.totalPrice) || 0), 0);
    const totalDiscount = Number(cart?.totalDiscount ?? 0);
    const totalAmount = Number(cart?.totalPrice ?? (subTotal - totalDiscount));
    const receiptEntries = orderReceipt?.entryDtoList || [];
    const receiptSubtotal = receiptEntries.reduce((sum, e) => sum + (Number(e.totalPrice) || 0), 0);
    const receiptDiscount = Number(orderReceipt?.totalDiscount) || 0;
    const receiptTotal = Number(orderReceipt?.totalPrice ?? (receiptSubtotal - receiptDiscount));
    const orderTimestamp = orderReceipt?.orderDate
        ? new Date(orderReceipt.orderDate).toLocaleString("en-GB", { hour12: true }).toLowerCase()
        : new Date().toLocaleString("en-GB", { hour12: true }).toLowerCase();

    const upiPayload = orderReceipt ? buildUpiPayload({
        payeeVpa: "store@upi",
        payeeName: "POS Store",
        amount: receiptTotal.toFixed(2),
        note: `Order ${orderReceipt.orderId || ""}`,
        txnRef: upiTxnRef.current || "TXN",
    }) : "";

    if (isOrderConfirmed) {
        return (
            <OrderConfirmedScreen
                isSidebarOpen={isSidebarOpen}
                paymentType={paymentType}
                receiptTotal={receiptTotal}
                handleReceiptPrint={handleReceiptPrint}
                startNewSale={startNewSale}
                showViewReceiptModal={showViewReceiptModal}
                setShowViewReceiptModal={setShowViewReceiptModal}
                orderTimestamp={orderTimestamp}
                orderReceipt={orderReceipt}
                receiptCustomerName={receiptCustomerName}
                receiptEntries={receiptEntries}
                receiptSubtotal={receiptSubtotal}
                receiptDiscount={receiptDiscount}
            />
        );
    }

    if (orderReceipt) {
        return (
            <OrderReceiptScreen
                isSidebarOpen={isSidebarOpen}
                today={today}
                orderReceipt={orderReceipt}
                customerLabel={orderReceipt.identifier || "Walk-in"}
                receiptEntries={receiptEntries}
                receiptSubtotal={receiptSubtotal}
                receiptDiscount={receiptDiscount}
                receiptTotal={receiptTotal}
                showPaymentForm={showPaymentForm}
                paymentType={paymentType}
                paymentError={paymentError}
                paymentProcessing={paymentProcessing}
                upiStatus={upiStatus}
                upiPayload={upiPayload}
                handleReceiptPrint={handleReceiptPrint}
                confirmCashPayment={confirmCashPayment}
                confirmCardPayment={confirmCardPayment}
                startUpiPayment={startUpiPayment}
                cancelUpiPayment={cancelUpiPayment}
                setOrderReceipt={setOrderReceipt}
                setShowPaymentForm={setShowPaymentForm}
                setPaymentType={setPaymentType}
                setPaymentError={setPaymentError}
                cardNumber={cardNumber}
                cardExpiry={cardExpiry}
                cardCvv={cardCvv}
                cardName={cardName}
                setCardNumber={setCardNumber}
                setCardExpiry={setCardExpiry}
                setCardCvv={setCardCvv}
                setCardName={setCardName}
                formatCardNumber={formatCardNumber}
                formatExpiry={formatExpiry}
            />
        );
    }

    return (
        <SalesWorkspace
            isSidebarOpen={isSidebarOpen}
            today={today}
            navigateHome={() => router.push('/home')}
            showAddModal={showAddModal}
            setShowAddModal={setShowAddModal}
            addCustomerError={addCustomerError}
            setAddCustomerError={setAddCustomerError}
            newCustomer={newCustomer}
            setNewCustomer={setNewCustomer}
            handleAddCustomer={handleAddCustomer}
            addingCustomer={addingCustomer}
            toast={toast}
            setError={setError}
            error={error}
            customerFound={customerFound}
            bannerDismissed={bannerDismissed}
            setBannerDismissed={setBannerDismissed}
            showCustomerDropdown={showCustomerDropdown}
            setShowCustomerDropdown={setShowCustomerDropdown}
            filteredCustomers={filteredCustomers}
            dropdownRef={dropdownRef}
            phoneSearch={phoneSearch}
            setPhoneSearch={setPhoneSearch}
            searchingCustomer={searchingCustomer}
            handlePhoneSearch={handlePhoneSearch}
            handleClearCustomer={handleClearCustomer}
            entries={entries}
            cart={cart}
            productSearch={productSearch}
            setProductSearch={setProductSearch}
            filteredProducts={filteredProducts}
            handleAddProduct={handleAddProduct}
            handleQtyChange={handleQtyChange}
            handleDeleteEntry={handleDeleteEntry}
            handleCancel={handleCancel}
            handleSale={handleSale}
            actionLoading={actionLoading}
            subTotal={subTotal}
            totalDiscount={totalDiscount}
            totalAmount={totalAmount}
        />
    );
}
function UpiQrCode({ value, size = 168 }) {
    const [dataUrl, setDataUrl] = useState(null);

    useEffect(() => {
        const url = `https://api.qrserver.com/v1/create-qr-code/?size=${size * 2}x${size * 2}&data=${encodeURIComponent(value)}`;
        setDataUrl(url);
    }, [value, size]);

    if (!dataUrl) {
        return <div style={{ width: size, height: size, background: "#f1f5f9" }} />;
    }
    return (
        <img src={dataUrl} alt="UPI QR Code" width={size} height={size} style={{ display: "block" }} />
    );
}

UpiQrCode.propTypes = {
    value: PropTypes.string.isRequired,
    size: PropTypes.number,
};

function AddCustomerModal({
    showAddModal,
    addCustomerError,
    setShowAddModal,
    setAddCustomerError,
    newCustomer,
    setNewCustomer,
    handleAddCustomer,
    addingCustomer,
}) {
    if (!showAddModal) return null;
    return (
        <div style={{ position: "fixed", top: 0, right: 0, bottom: 0, left: 0, background: "rgba(0,0,0,0.55)", zIndex: 10000, display: "flex", alignItems: "center", justifyContent: "center" }}>
            <div style={{ width: "420px", background: "#fff", borderRadius: "12px", overflow: "hidden", position: "relative", boxShadow: "0 8px 32px rgba(0,0,0,0.18)" }}>
                <div style={{ padding: "18px 24px", borderBottom: "1px solid #e8eaf0", display: "flex", alignItems: "center", justifyContent: "space-between" }}>
                    <h2 style={{ margin: 0, fontSize: "16px", color: C.navy, fontWeight: "700" }}>Add New Customer</h2>
                    <button type="button" onClick={() => { setShowAddModal(false); setAddCustomerError(""); }} style={{ background: "none", border: "none", cursor: "pointer", color: C.muted, fontSize: "20px", lineHeight: 1 }}>✕</button>
                </div>
                <div style={{ padding: "24px", display: "flex", flexDirection: "column", gap: "14px" }}>
                    {addCustomerError && (
                        <div style={{ color: C.red, background: "#fef2f2", border: "1px solid #fecaca", padding: "10px 12px", borderRadius: "6px", fontSize: "13px" }}>{addCustomerError}</div>
                    )}
                    <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
                        <label htmlFor="newCustomer_identifier" style={{ fontSize: "11px", fontWeight: "600", color: C.mid }}>Phone Number <span style={{ color: C.red }}>*</span></label>
                        <input
                            id="newCustomer_identifier"
                            type="text"
                            inputMode="numeric"
                            placeholder="e.g. 9876543210"
                            maxLength={10}
                            value={newCustomer.identifier}
                            onChange={e => {
                                const digitsOnly = e.target.value.replaceAll(/\D/g, "").slice(0, 10);
                                setNewCustomer(prev => ({ ...prev, identifier: digitsOnly }));
                            }}
                            style={inputSt}
                        />
                    </div>
                    <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
                        <label htmlFor="newCustomer_name" style={{ fontSize: "11px", fontWeight: "600", color: C.mid }}>Customer Name <span style={{ color: C.red }}>*</span></label>
                        <input id="newCustomer_name" type="text" placeholder="Full name" value={newCustomer.customerName} onChange={e => setNewCustomer(prev => ({ ...prev, customerName: e.target.value }))} style={inputSt} />
                    </div>
                    <div style={{ display: "flex", flexDirection: "column", gap: "4px" }}>
                        <label htmlFor="newCustomer_email" style={{ fontSize: "11px", fontWeight: "600", color: C.mid }}>Email <span style={{ color: C.muted, fontWeight: 400 }}>(optional)</span></label>
                        <input id="newCustomer_email" type="email" placeholder="customer@email.com" value={newCustomer.email} onChange={e => setNewCustomer(prev => ({ ...prev, email: e.target.value }))} style={inputSt} />
                    </div>
                    <div style={{ display: "flex", gap: "10px", marginTop: "4px" }}>
                        <button type="button" onClick={() => { setShowAddModal(false); setAddCustomerError(""); }} style={{ flex: 1, height: "42px", background: "#fff", border: `1.5px solid ${C.muted}`, color: C.muted, borderRadius: "7px", fontWeight: "600", cursor: "pointer", fontSize: "13px" }}>Cancel</button>
                        <button type="button" onClick={handleAddCustomer} disabled={addingCustomer} style={{ flex: 2, height: "42px", background: C.navy, color: "#fff", border: "none", borderRadius: "7px", fontWeight: "600", cursor: addingCustomer ? "not-allowed" : "pointer", opacity: addingCustomer ? 0.7 : 1, fontSize: "13px" }}>{addingCustomer ? "Saving..." : "Save Customer"}</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

AddCustomerModal.propTypes = {
    showAddModal: PropTypes.bool.isRequired,
    addCustomerError: PropTypes.string,
    setShowAddModal: PropTypes.func.isRequired,
    setAddCustomerError: PropTypes.func.isRequired,
    newCustomer: PropTypes.shape({
        identifier: PropTypes.string,
        customerName: PropTypes.string,
        email: PropTypes.string,
    }).isRequired,
    setNewCustomer: PropTypes.func.isRequired,
    handleAddCustomer: PropTypes.func.isRequired,
    addingCustomer: PropTypes.bool.isRequired,
};

const salesWorkspaceViewPropTypes = {
    isSidebarOpen: PropTypes.bool.isRequired,
    today: PropTypes.string.isRequired,
    navigateHome: PropTypes.func.isRequired,
    showAddModal: PropTypes.bool.isRequired,
    setShowAddModal: PropTypes.func.isRequired,
    addCustomerError: PropTypes.string,
    setAddCustomerError: PropTypes.func.isRequired,
    newCustomer: PropTypes.shape({
        identifier: PropTypes.string,
        customerName: PropTypes.string,
        email: PropTypes.string,
    }).isRequired,
    setNewCustomer: PropTypes.func.isRequired,
    handleAddCustomer: PropTypes.func.isRequired,
    addingCustomer: PropTypes.bool.isRequired,
    toast: PropTypes.shape({
        msg: PropTypes.string,
        type: PropTypes.string,
    }),
    error: PropTypes.string,
    setError: PropTypes.func,
    customerFound: PropTypes.object,
    bannerDismissed: PropTypes.bool.isRequired,
    setBannerDismissed: PropTypes.func.isRequired,
    showCustomerDropdown: PropTypes.bool.isRequired,
    setShowCustomerDropdown: PropTypes.func.isRequired,
    filteredCustomers: PropTypes.arrayOf(PropTypes.object).isRequired,
    dropdownRef: PropTypes.shape({ current: PropTypes.any }),
    phoneSearch: PropTypes.string.isRequired,
    setPhoneSearch: PropTypes.func.isRequired,
    searchingCustomer: PropTypes.bool.isRequired,
    handlePhoneSearch: PropTypes.func.isRequired,
    handleClearCustomer: PropTypes.func.isRequired,
    entries: PropTypes.arrayOf(PropTypes.object).isRequired,
    cart: PropTypes.object,
    productSearch: PropTypes.string.isRequired,
    setProductSearch: PropTypes.func.isRequired,
    filteredProducts: PropTypes.arrayOf(PropTypes.object).isRequired,
    handleAddProduct: PropTypes.func.isRequired,
    handleQtyChange: PropTypes.func.isRequired,
    handleDeleteEntry: PropTypes.func.isRequired,
    handleCancel: PropTypes.func.isRequired,
    handleSale: PropTypes.func.isRequired,
    actionLoading: PropTypes.bool.isRequired,
    subTotal: PropTypes.number.isRequired,
    totalDiscount: PropTypes.number.isRequired,
    totalAmount: PropTypes.number.isRequired,
};

function SalesWorkspace(props) {
    return <SalesWorkspaceView {...props} />;
}

SalesWorkspace.propTypes = salesWorkspaceViewPropTypes;

function SalesWorkspaceView({
    isSidebarOpen,
    today,
    navigateHome,
    showAddModal,
    setShowAddModal,
    addCustomerError,
    setAddCustomerError,
    newCustomer,
    setNewCustomer,
    handleAddCustomer,
    addingCustomer,
    toast,
    error,
    setError,
    customerFound,
    bannerDismissed,
    setBannerDismissed,
    showCustomerDropdown,
    setShowCustomerDropdown,
    filteredCustomers,
    dropdownRef,
    phoneSearch,
    setPhoneSearch,
    searchingCustomer,
    handlePhoneSearch,
    handleClearCustomer,
    entries,
    cart,
    productSearch,
    setProductSearch,
    filteredProducts,
    handleAddProduct,
    handleQtyChange,
    handleDeleteEntry,
    handleCancel,
    handleSale,
    actionLoading,
    subTotal,
    totalDiscount,
    totalAmount,
}) {
    return (
        <div style={{ position: "fixed", top: "60px", right: 0, bottom: 0, left: isSidebarOpen ? "220px" : "55px", backgroundColor: "#f4f5f9", fontFamily: "'Segoe UI', sans-serif", display: "flex", flexDirection: "column", overflow: "hidden", transition: "left 0.2s ease" }}>

            <AddCustomerModal
                showAddModal={showAddModal}
                addCustomerError={addCustomerError}
                setShowAddModal={setShowAddModal}
                setAddCustomerError={setAddCustomerError}
                newCustomer={newCustomer}
                setNewCustomer={setNewCustomer}
                handleAddCustomer={handleAddCustomer}
                addingCustomer={addingCustomer}
            />

            {toast && (
                <div style={{ position: "absolute", top: "14px", right: "20px", zIndex: 999, padding: "10px 20px", borderRadius: "8px", background: toast.type === "error" ? "#fef2f2" : C.greenBg, border: `1px solid ${toast.type === "error" ? "#fca5a5" : "#86efac"}`, color: toast.type === "error" ? C.red : C.green, fontWeight: "600", fontSize: "13px", boxShadow: "0 2px 12px rgba(0,0,0,0.10)" }}>
                    {toast.msg}
                </div>
            )}

            <div style={{ background: "#fff", padding: "10px 20px", borderBottom: "1.5px solid #e8eaf0", display: "flex", alignItems: "center", justifyContent: "space-between", flexShrink: 0, gap: "12px" }}>
                <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
                    <button type="button" onClick={navigateHome} style={{ background: "#fff", border: `1.5px solid ${C.mid}`, color: C.mid, borderRadius: "7px", padding: "5px 14px", fontSize: "12px", fontWeight: "600", cursor: "pointer" }}>← Home</button>
                    <div style={{ display: "flex", border: `1.5px solid ${C.mid}`, borderRadius: "7px", overflow: "hidden" }}>
                        <span style={{ padding: "6px 20px", fontSize: "12px", fontWeight: "700", background: C.mid, color: "#fff", display: "inline-block" }}>🛒 POS CART WORKSPACE</span>
                    </div>
                </div>
                <div style={{ display: "flex", alignItems: "center", gap: "16px", fontSize: "13px", fontWeight: "600" }}>
                    <span style={{ color: C.mid, borderBottom: `2px solid ${C.mid}`, paddingBottom: "2px" }}>1. Add To Cart Workspace</span>
                    <span style={{ color: C.muted }}>➔</span>
                    <span style={{ color: C.muted }}>2. Checkout / Receipt Form Completion</span>
                </div>
                <input readOnly value={today} style={{ ...inputSt, width: "140px", background: "#f7f8fc", color: C.text }} />
            </div>

            <div style={{ flex: 1, display: "flex", overflow: "hidden" }}>
                <div style={{ width: "60%", display: "flex", flexDirection: "column", overflow: "hidden", background: "#fff", borderRight: "1.5px solid #e8eaf0" }}>
                    <div style={{ padding: "12px 16px 10px", borderBottom: "1px solid #f0f1f6", flexShrink: 0, display: "flex", flexDirection: "column", gap: "8px", position: "relative" }}>
                        {error && (
                            <div style={{ background: "#fef2f2", border: "1px solid #fecaca", color: C.red, borderRadius: "8px", padding: "8px 12px", fontSize: "12px", display: "flex", alignItems: "center", gap: "10px" }}>
                                {error === "__NOT_FOUND__" ? (
                                    <>
                                        <span>No customer found for identifier: <strong>{phoneSearch}</strong>. Register profile?</span>
                                        <button type="button" onClick={() => { setNewCustomer({ identifier: phoneSearch, customerName: "", email: "" }); setAddCustomerError(""); setShowAddModal(true); setError(""); }} style={{ padding: "4px 14px", background: C.navy, color: "#fff", border: "none", borderRadius: "6px", fontSize: "12px", fontWeight: "600", cursor: "pointer", whiteSpace: "nowrap" }}>+ Add Customer</button>
                                    </>
                                ) : <span>{error}</span>}
                            </div>
                        )}
                        <div style={{ display: "flex", gap: "10px", alignItems: "center" }}>
                            <div ref={dropdownRef} style={{ flex: 1, display: "flex", position: "relative" }}>
                                <input type="text" placeholder="Search customer by phone number..." value={phoneSearch}
                                    onFocus={() => setShowCustomerDropdown(true)}
                                    onChange={e => { setPhoneSearch(e.target.value); setShowCustomerDropdown(true); if (customerFound !== null) { setCustomerFound(null); setBannerDismissed(false); } if (error === "__NOT_FOUND__") setError(""); }}
                                    onKeyDown={e => e.key === "Enter" && handlePhoneSearch()}
                                    disabled={!!customerFound}
                                    style={{ width: "100%", height: "38px", padding: "0 10px", fontSize: "13px", outline: "none", boxSizing: "border-box", flex: 1, borderRadius: "7px 0 0 7px", borderTop: "1.5px solid #dcdfe6", borderBottom: "1.5px solid #dcdfe6", borderLeft: "1.5px solid #dcdfe6", borderRight: "none", background: customerFound ? "#f8fafc" : "#fff" }}
                                />
                                <button type="button" onClick={() => handlePhoneSearch()} disabled={searchingCustomer || !phoneSearch.trim() || !!customerFound} style={{ padding: "0 16px", background: C.mid, color: "#fff", border: "none", fontWeight: 600, fontSize: "13px", height: "38px", cursor: (!phoneSearch.trim() || !!customerFound) ? "not-allowed" : "pointer", opacity: (!phoneSearch.trim() || !!customerFound) ? 0.5 : 1, whiteSpace: "nowrap" }}>{searchingCustomer ? "..." : "Search"} </button>
                                <button type="button" title="Quick Add Customer" onClick={() => { setNewCustomer({ identifier: phoneSearch, customerName: "", email: "" }); setAddCustomerError(""); setShowAddModal(true); }} style={{ width: "38px", height: "38px", background: C.navy, border: "none", borderRadius: "0 7px 7px 0", color: "#fff", fontSize: "22px", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" }}>+</button>
                                {showCustomerDropdown && filteredCustomers.length > 0 && (
                                    <div style={{ position: "absolute", top: "40px", left: 0, right: 0, background: "#fff", border: "1px solid #cbd5e1", borderRadius: "8px", boxShadow: "0 4px 12px rgba(0,0,0,0.1)", zIndex: 2000, maxHeight: "220px", overflowY: "auto" }}>
                                        {filteredCustomers.map(c => {
                                            const cPhone = c.phone || c.identifier || "—";
                                            const cName = c.customerName || c.name || "—";
                                            return (
                                                <button key={c.id || c.identifier} type="button" onClick={() => handlePhoneSearch(cPhone)} style={{ padding: "10px 12px", borderBottom: "1px solid #f1f5f9", cursor: "pointer", display: "flex", flexDirection: "column", gap: "2px", background: "#fff", border: "none", width: "100%", textAlign: "left" }} onMouseEnter={e => e.currentTarget.style.backgroundColor = "#f8fafc"} onMouseLeave={e => e.currentTarget.style.backgroundColor = "#fff"}>
                                                    <span style={{ fontWeight: "700", fontSize: "14px", color: C.text }}>{cName}</span>
                                                    <span style={{ fontSize: "12px", color: C.muted }}>ID: {c.identifier} | Phone: {cPhone}</span>
                                                </button>
                                            );
                                        })}
                                    </div>
                                )}
                            </div>
                        </div>
                        {customerFound && !bannerDismissed && (
                            <div style={{ display: "flex", alignItems: "center", gap: "8px", padding: "8px 12px", background: C.greenBg, border: "1px solid #bbf7d0", borderRadius: "8px" }}>
                                <span style={{ fontSize: "14px", color: C.green, fontWeight: 700 }}>✓</span>
                                <div style={{ flex: 1, fontSize: "13px", color: C.green }}>
                                    <strong>{customerFound.customerName || customerFound.name || customerFound.identifier}</strong>
                                    <span style={{ marginLeft: "8px", fontWeight: 400, fontSize: "12px" }}>({customerFound.identifier})</span>
                                </div>
                                <button type="button" onClick={() => setBannerDismissed(true)} style={{ background: "none", border: "none", cursor: "pointer", color: "#94a3b8", fontSize: "16px" }}>✕</button>
                            </div>
                        )}
                        {customerFound && bannerDismissed && (
                            <div style={{ display: "flex", alignItems: "center", gap: "8px", padding: "6px 12px", background: "#f8fafc", border: "1px solid #e2e8f0", borderRadius: "8px" }}>
                                <span style={{ fontSize: "12px", color: C.green, fontWeight: 700 }}>✓</span>
                                <span style={{ flex: 1, fontSize: "12px", color: "#475569" }}>
                                    <strong>{customerFound.customerName || customerFound.name || customerFound.identifier}</strong>
                                    <span style={{ marginLeft: "6px", color: C.muted }}>({customerFound.identifier})</span>
                                </span>
                                <button type="button" onClick={handleClearCustomer} style={{ background: "none", border: "1px solid #e2e8f0", cursor: "pointer", color: "#94a3b8", fontSize: "11px", fontWeight: 600, padding: "2px 6px", borderRadius: "4px" }}>Change</button>
                            </div>
                        )}
                    </div>

                    <div style={{ flex: 1, overflowY: "auto", padding: "0 16px" }}>
                        {entries.length === 0 ? (
                            <div style={{ padding: "40px 0", textAlign: "center", color: C.muted, fontSize: "14px" }}>
                                {cart ? "Cart workspace is empty. Select products from the panel." : "Select a customer to start cart."}
                            </div>
                        ) : (
                            <table style={{ width: "100%", borderCollapse: "collapse", marginTop: "10px", fontSize: "13px" }}>
                                <thead>
                                    <tr style={{ borderBottom: "1.5px solid #e8eaf0", color: C.mid, textAlign: "left" }}>
                                        <th style={{ padding: "10px 6px" }}>Product Code</th>
                                        <th style={{ padding: "10px 6px" }}>Selling Price</th>
                                        <th style={{ padding: "10px 6px", textAlign: "center" }}>Qty</th>
                                        <th style={{ padding: "10px 6px", textAlign: "right" }}>Total</th>
                                        <th style={{ padding: "10px 6px", textAlign: "center" }}>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {entries.map(entry => (
                                        <tr key={entry.id || entry.identifier} style={{ borderBottom: "1px solid #f0f1f6", color: C.text }}>
                                            <td style={{ padding: "12px 6px" }}>
                                                <div style={{ fontWeight: "600" }}>{entry.product}</div>
                                                <div style={{ fontSize: "11px", color: C.muted }}>ID: {entry.identifier}</div>
                                            </td>
                                            <td style={{ padding: "12px 6px" }}>₹{Number(entry.sellingPrice).toFixed(2)}</td>
                                            <td style={{ padding: "12px 6px", textAlign: "center" }}>
                                                <div style={{ display: "inline-flex", alignItems: "center", border: "1px solid #dcdfe6", borderRadius: "5px" }}>
                                                    <button type="button" onClick={() => handleQtyChange(entry, -1)} disabled={false} style={{ border: "none", background: "none", width: "24px", height: "24px", cursor: "pointer", fontWeight: "700" }}>-</button>
                                                    <span style={{ minWidth: "24px", textAlign: "center", fontWeight: "600", fontSize: "12px" }}>{entry.quantity}</span>
                                                    <button type="button" onClick={() => handleQtyChange(entry, 1)} disabled={false} style={{ border: "none", background: "none", width: "24px", height: "24px", cursor: "pointer", fontWeight: "700" }}>+</button>
                                                </div>
                                            </td>
                                            <td style={{ padding: "12px 6px", textAlign: "right", fontWeight: "600" }}>₹{Number(entry.totalPrice).toFixed(2)}</td>
                                            <td style={{ padding: "12px 6px", textAlign: "center" }}>
                                                <button type="button" onClick={() => handleDeleteEntry(entry)} style={{ background: "none", border: "none", color: C.navy, cursor: "pointer", fontWeight: "600" }}>Delete</button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        )}
                    </div>

                    <div style={{ padding: "16px", background: "#f8fafc", borderTop: "1.5px solid #e8eaf0" }}>
                        <div style={{ display: "flex", flexDirection: "column", gap: "6px", fontSize: "14px", color: C.text }}>
                            <div style={{ display: "flex", justifyContent: "space-between" }}>
                                <span>Sub Total ({entries.reduce((sum, e) => sum + Number(e.quantity), 0)} items):</span>
                                <span>₹{subTotal.toFixed(2)}</span>
                            </div>
                            <div style={{ display: "flex", justifyContent: "space-between", color: C.mid }}>
                                <span>Discount Apply:</span>
                                <span>-₹{totalDiscount.toFixed(2)}</span>
                            </div>
                            <div style={{ display: "flex", justifyContent: "space-between", fontWeight: "700", fontSize: "16px", color: C.navy, borderTop: "1px solid #e2e8f0", paddingTop: "6px", marginTop: "4px" }}>
                                <span>Grand Total:</span>
                                <span>₹{totalAmount.toFixed(2)}</span>
                            </div>
                        </div>
                        <div style={{ display: "flex", gap: "10px", marginTop: "14px" }}>
                            <button type="button" onClick={handleCancel} style={{ flex: 1, height: "42px", background: "#fff", border: `1.5px solid ${C.navy}`, color: C.navy, borderRadius: "7px", fontWeight: "600", cursor: "pointer" }}>Clear Cart Workspace</button>
                            <button
                                type="button"
                                onClick={handleSale}
                                style={{ flex: 2, height: "42px", background: C.navy, border: "none", color: "#fff", borderRadius: "7px", fontWeight: "700", fontSize: "14px", cursor: "pointer" }}
                            >
                                Complete Sale
                            </button>
                        </div>
                    </div>
                </div>

                <div style={{ width: "40%", display: "flex", flexDirection: "column", overflow: "hidden", background: "#f8fafc" }}>
                    <div style={{ padding: "12px", display: "flex", flexDirection: "column", gap: "8px", borderBottom: "1px solid #e8eaf0", background: "#fff" }}>
                        <input type="text" placeholder="Filter products by name/SKU..." value={productSearch} onChange={e => setProductSearch(e.target.value)} style={inputSt} />
                    </div>
                    <div style={{ flex: 1, overflowY: "auto", padding: "12px" }}>
                        {filteredProducts.length === 0 ? (
                            <div style={{ padding: "20px", textAlign: "center", color: C.muted, fontSize: "13px" }}>No inventory parameters matching criteria.</div>
                        ) : (
                            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(130px, 1fr))", gap: "10px" }}>
                                {filteredProducts.map(p => {
                                    const matchInCart = entries.find(e => e.product === p.identifier);
                                    const price = Number(p.sellingPrice || p.mrp || p.price || 0);
                                    return (
                                        <button key={p.identifier} type="button" onClick={() => handleAddProduct(p)} disabled={!price || price <= 0} style={{ background: "#fff", border: matchInCart ? `1.5px solid ${C.mid}` : "1.5px solid #e2e8f0", borderRadius: "8px", padding: "10px", cursor: (!price || price <= 0) ? "not-allowed" : "pointer", opacity: (!price || price <= 0) ? 0.5 : 1, position: "relative", display: "flex", flexDirection: "column", justifyContent: "space-between", height: "110px", boxShadow: "0 1px 3px rgba(0,0,0,0.02)", textAlign: "left" }}>                                            {matchInCart && (
                                            <span style={{ position: "absolute", top: "-6px", right: "-6px", background: C.mid, color: "#fff", fontSize: "10px", fontWeight: "700", padding: "2px 6px", borderRadius: "10px" }}>{matchInCart.quantity}</span>
                                        )}
                                            <div>
                                                <div style={{ fontWeight: "600", fontSize: "12px", color: C.navy, WebkitLineClamp: 2, display: "-webkit-box", WebkitBoxOrient: "vertical", overflow: "hidden", lineHeight: 1.2 }}>{p.name || p.identifier}</div>
                                                <div style={{ fontSize: "10px", color: C.muted, marginTop: "2px" }}>SKU: {p.identifier}</div>
                                            </div>
                                            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline", marginTop: "6px" }}>
                                                <span style={{ fontSize: "10px", color: C.muted }}>Rate</span>
                                                <span style={{ fontWeight: "700", fontSize: "12px", color: (!price || price <= 0) ? C.red : C.text }}>{(!price || price <= 0) ? "No Price" : `₹${price.toFixed(2)}`}</span>                                            </div>
                                        </button>
                                    );
                                })}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

SalesWorkspaceView.propTypes = salesWorkspaceViewPropTypes;

OrderConfirmedScreen.propTypes = {
    isSidebarOpen: PropTypes.bool.isRequired,
    paymentType: PropTypes.string.isRequired,
    receiptTotal: PropTypes.number.isRequired,
    handleReceiptPrint: PropTypes.func.isRequired,
    startNewSale: PropTypes.func.isRequired,
    showViewReceiptModal: PropTypes.bool.isRequired,
    setShowViewReceiptModal: PropTypes.func.isRequired,
    orderTimestamp: PropTypes.string.isRequired,
    orderReceipt: PropTypes.shape({
        orderId: PropTypes.string,
        orderDate: PropTypes.string,
    }),
    receiptCustomerName: PropTypes.string,
    receiptEntries: PropTypes.arrayOf(
        PropTypes.shape({
            product: PropTypes.string,
            quantity: PropTypes.number,
            rate: PropTypes.number,
            total: PropTypes.number,
        }),
    ).isRequired,
    receiptSubtotal: PropTypes.number.isRequired,
    receiptDiscount: PropTypes.number.isRequired,
};

OrderReceiptScreen.propTypes = {
    isSidebarOpen: PropTypes.bool.isRequired,
    today: PropTypes.string.isRequired,
    orderReceipt: PropTypes.shape({
        orderId: PropTypes.string,
        orderDate: PropTypes.string,
        total: PropTypes.number,
    }),
    customerLabel: PropTypes.string.isRequired,
    receiptEntries: PropTypes.arrayOf(
        PropTypes.shape({
            product: PropTypes.string,
            quantity: PropTypes.number,
            rate: PropTypes.number,
            total: PropTypes.number,
        }),
    ).isRequired,
    receiptSubtotal: PropTypes.number.isRequired,
    receiptDiscount: PropTypes.number.isRequired,
    receiptTotal: PropTypes.number.isRequired,
    showPaymentForm: PropTypes.bool.isRequired,
    paymentType: PropTypes.string.isRequired,
    paymentError: PropTypes.string,
    paymentProcessing: PropTypes.bool.isRequired,
    upiStatus: PropTypes.string,
    upiPayload: PropTypes.string,
    handleReceiptPrint: PropTypes.func.isRequired,
    confirmCashPayment: PropTypes.func.isRequired,
    confirmCardPayment: PropTypes.func.isRequired,
    startUpiPayment: PropTypes.func.isRequired,
    cancelUpiPayment: PropTypes.func.isRequired,
    setOrderReceipt: PropTypes.func.isRequired,
    setShowPaymentForm: PropTypes.func.isRequired,
    setPaymentType: PropTypes.func.isRequired,
    setPaymentError: PropTypes.func.isRequired,
    cardNumber: PropTypes.string,
    cardExpiry: PropTypes.string,
    cardCvv: PropTypes.string,
    cardName: PropTypes.string,
    setCardNumber: PropTypes.func.isRequired,
    setCardExpiry: PropTypes.func.isRequired,
    setCardCvv: PropTypes.func.isRequired,
    setCardName: PropTypes.func.isRequired,
    formatCardNumber: PropTypes.func.isRequired,
    formatExpiry: PropTypes.func.isRequired,
};