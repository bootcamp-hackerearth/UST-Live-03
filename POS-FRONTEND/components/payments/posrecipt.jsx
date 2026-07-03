// components/payments/posrecipt.jsx

"use client";

import PropTypes from "prop-types";

export function PrintReceiptButton({ order }) {
    const handlePrint = () => {
        if (typeof globalThis !== "undefined" && typeof globalThis.print === "function") {
            globalThis.print();
        }
    };

    return (
        <button
            onClick={handlePrint}
            className="flex items-center gap-1.5 text-xs font-semibold text-gray-500 border border-gray-200 px-3 py-2 rounded-lg hover:bg-gray-50 transition-colors cursor-pointer bg-white"
        >
            🖨️ Print receipt
        </button>
    );
}

export function PrintStyles() {
    return (
        <style>{`
            @media print {
                /* Hide everything except the receipt */
                body * { visibility: hidden !important; }
                #pos-receipt, #pos-receipt * { visibility: visible !important; }
                #pos-receipt {
                    position: fixed !important;
                    inset: 0 !important;
                    display: flex !important;
                    align-items: flex-start !important;
                    justify-content: center !important;
                    background: white !important;
                    padding: 0 !important;
                    margin: 0 !important;
                }
                @page {
                    size: 80mm auto;
                    margin: 0;
                }
            }
        `}</style>
    );
}

export function POSReceipt({ order, storeName = "UST Retail" }) {
    const totalAmount   = Number(order?.totalPrice    || 0);
    const totalDiscount = Number(order?.totalDiscount || 0);
    const subtotal      = totalAmount + totalDiscount;
    const entries       = order?.orderEntryDtoList    || [];
    const paymentMode   = order?.paymentMode          || "—";
    const customerPhone = order?.identifier           || "—";
    const orderId       = order?.orderId              || "—";

    const parts = orderId.split("-");
    const ts    = parts[parts.length - 1] || "";
    let dateStr = "";
    if (ts.length === 14) {
        const d  = ts.slice(6,8),  mo = ts.slice(4,6),  y = ts.slice(0,4);
        const h  = ts.slice(8,10), mi = ts.slice(10,12), s = ts.slice(12,14);
        dateStr = `${d}/${mo}/${y}  ${h}:${mi}:${s}`;
    }

    const METHOD_LABELS = { UPI: "UPI / QR Code", CARD: "Card Swipe", CASH: "Cash" };

    return (
        <div id="pos-receipt" className="hidden print:block">
            <div style={{
                fontFamily: "'Courier New', Courier, monospace",
                fontSize:   "12px",
                width:      "72mm",
                padding:    "6mm 4mm",
                color:      "#000",
                background: "#fff",
                lineHeight: "1.5",
            }}>
                <div style={{ textAlign: "center", marginBottom: "4mm" }}>
                    <div style={{ fontSize: "18px", fontWeight: "900", letterSpacing: "2px", textTransform: "uppercase" }}>
                        {storeName}
                    </div>
                    <div style={{ fontSize: "10px", color: "#555" }}>
                        UST Campus, Technopark Phase II, Attipra Village, Kulathoor, Thiruvananthapuram, Kerala - 695583
                    </div>
                    <div style={{ fontSize: "10px", color: "#555" }}>
                        Email: customersupport.posretail@ust.com
                    </div>
                    <div style={{ fontSize: "10px", color: "#555" }}>
                        Contact No: +0471 98765100 | 98765101
                    </div>
                    <div style={{ fontSize: "10px", marginTop: "1mm", color: "#555" }}>
                        Customer Sale Invoice Receipt
                    </div>
                </div>

                <Divider dashed />

                <Row label="Invoice No" value={`INV${orderId.slice(10)}`}  mono/>
                <Row label="Order ID"  value={orderId}       mono />
                <Row label="Date"      value={dateStr}             />
                <Row label="Customer"  value={customerPhone}  mono />
                <Row label="Payment"   value={METHOD_LABELS[paymentMode] || paymentMode} />

                <Divider dashed />

                <div style={{ display: "flex", justifyContent: "space-between", fontSize: "10px", fontWeight: "bold", textTransform: "uppercase", color: "#555", marginBottom: "1mm" }}>
                    <span style={{ flex: 3 }}>Item</span>
                    <span style={{ flex: 1, textAlign: "center" }}>Qty</span>
                    <span style={{ flex: 1, textAlign: "right"  }}>Rate</span>
                    <span style={{ flex: 1, textAlign: "right"  }}>Amt</span>
                </div>

                <Divider />

                                {entries.map((entry) => {
                    const lineTotal = Number(entry.totalPrice  || 0);
                    const qty       = Math.floor(Number(entry.quantity || 0));
                    const unitPrice = Number(entry.sellingPrice || (qty > 0 ? lineTotal / qty : 0));
                    const discount  = Number(entry.discount    || 0);
                    return (
                        <div key={`${entry.product}-${lineTotal}`} style={{ marginBottom: "2mm" }}>
                            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start" }}>
                                <span style={{ flex: 3, wordBreak: "break-word", paddingRight: "2mm" }}>
                                    {entry.product}
                                </span>
                                <span style={{ flex: 1, textAlign: "center" }}>{qty}</span>
                                <span style={{ flex: 1, textAlign: "right"  }}>₹{unitPrice.toFixed(2)}</span>
                                <span style={{ flex: 1, textAlign: "right", fontWeight: "bold" }}>₹{lineTotal.toFixed(2)}</span>
                            </div>
                            {discount > 0 && (
                                <div style={{ fontSize: "10px", color: "#555", paddingLeft: "2mm" }}>
                                    Discount: -₹{discount.toFixed(2)}
                                </div>
                            )}
                        </div>
                    );
                })}

                <Divider />

                {totalDiscount > 0 && (
                    <>
                        <Row label="Subtotal (MRP)"  value={`₹${subtotal.toFixed(2)}`} />
                        <Row label="Total Savings"   value={`-₹${totalDiscount.toFixed(2)}`} />
                    </>
                )}

                <div style={{
                    display:        "flex",
                    justifyContent: "space-between",
                    fontWeight:     "900",
                    fontSize:       "15px",
                    marginTop:      "2mm",
                    paddingTop:     "2mm",
                    borderTop:      "2px solid #000",
                }}>
                    <span>TOTAL PAID</span>
                    <span>₹{totalAmount.toFixed(2)}</span>
                </div>

                <div style={{
                    display:        "flex",
                    justifyContent: "space-between",
                    fontSize:       "11px",
                    color:          "#555",
                    marginTop:      "1mm",
                }}>
                    <span>Payment mode</span>
                    <span>{METHOD_LABELS[paymentMode] || paymentMode}</span>
                </div>

                <Divider dashed />

                <div style={{ textAlign: "center", margin: "3mm 0 2mm" }}>
                    <div>NOTICE:Goods once sold will not be returned.</div>
                    <BarcodeStripes text={orderId} />
                    <div style={{ fontSize: "9px", color: "#555", marginTop: "1.5mm", letterSpacing: "1px" }}>
                        {orderId}
                    </div>
                </div>

                <Divider dashed />

                <div style={{ textAlign: "center", fontSize: "10px", color: "#555", marginTop: "2mm", lineHeight: "1.8" }}>
                    <div>Thank you for shopping with us!</div>
                    <div style={{ marginTop: "1mm", fontSize: "9px" }}>
                        — Visit Again —
                    </div>
                </div>

                <div style={{ marginTop: "8mm" }} />
            </div>
        </div>
    );
}

function Divider({ dashed }) {
    return (
        <div style={{
            borderTop:  dashed ? "1px dashed #aaa" : "1px solid #000",
            margin:     "2mm 0",
        }} />
    );
}

function Row({ label, value, mono }) {
    return (
        <div style={{ display: "flex", justifyContent: "space-between", fontSize: "11px", marginBottom: "0.5mm" }}>
            <span style={{ color: "#555" }}>{label}</span>
            <span style={{ fontWeight: "600", fontFamily: mono ? "monospace" : "inherit", textAlign: "right", maxWidth: "55%", wordBreak: "break-all" }}>
                {value}
            </span>
        </div>
    );
}

function BarcodeStripes({ text }) {
    const chars = (text || "").split("");
    return (
        <div style={{ display: "inline-flex", alignItems: "flex-end", gap: "1px", height: "14mm" }}>
            {chars.map((char, i) => {
                const codePoint = char.codePointAt(0) || 0;
                const height = 50 + ((codePoint * 7 + i * 13) % 50);
                const width  = (codePoint % 2 === 0) ? "1px" : "2px";
                return (
                    <div
                        key={`${text}-${i}-${codePoint}`}
                        style={{
                            width,
                            height:     `${height}%`,
                            background: "#000",
                            display:    "inline-block",
                        }}
                    />
                );
            })}
        </div>
    );
}

PrintReceiptButton.propTypes = {
    order: PropTypes.object,
};

POSReceipt.propTypes = {
    order: PropTypes.shape({
        totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        totalDiscount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        orderEntryDtoList: PropTypes.arrayOf(
            PropTypes.shape({
                product: PropTypes.string,
                quantity: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
                totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
                sellingPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
                discount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
            })
        ),
        paymentMode: PropTypes.string,
        identifier: PropTypes.string,
        orderId: PropTypes.string,
    }),
    storeName: PropTypes.string,
};

Divider.propTypes = {
    dashed: PropTypes.bool,
};

Row.propTypes = {
    label: PropTypes.string.isRequired,
    value: PropTypes.any,
    mono: PropTypes.bool,
};

BarcodeStripes.propTypes = {
    text: PropTypes.string,
};