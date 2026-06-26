"use client";

import PropTypes from "prop-types";

export default function OrderReceiptModal({
  order,
  onClose,
  productMap = {},
  title = "Order Receipt Summary",
  showSuccessIcon = false,
}) {
  if (!order) return null;

  const handlePrintReceipt = () => {
    globalThis.print();
  };

  const orderDateTime =
    order.createdOn || order.createdDate || order.orderDate || order.orderDateTime || null;

  const formattedDateTime = orderDateTime
    ? new Date(orderDateTime).toLocaleString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      })
    : null;

  return (
    <>
      <style>{`
        @media print {
          body * { visibility: hidden; }
          #printable-invoice-modal, #printable-invoice-modal * { visibility: visible; }
          #printable-invoice-modal { position: absolute; left: 0; top: 0; width: 100%; max-width: 100%; box-shadow: none; padding: 0; margin: 0; }
          .no-print-close-btn { display: none !important; }
        }
      `}</style>

      <div style={{ position: "fixed", inset: 0, backgroundColor: "rgba(0,0,0,0.4)", display: "flex", alignItems: "center", justifyContent: "center", zIndex: 110, padding: "20px" }}>
        <div id="printable-invoice-modal" style={{ backgroundColor: "#fff", padding: "24px", borderRadius: "12px", width: "100%", maxWidth: "560px", maxHeight: "90vh", overflowY: "auto" }}>

          {showSuccessIcon ? (
            <div style={{ display: "flex", alignItems: "center", gap: "8px", marginBottom: "4px" }}>
              <span style={{ color: "#16a34a", fontSize: "20px" }}>✓</span>
              <h3 style={{ margin: 0 }}>{title}</h3>
            </div>
          ) : (
            <h3 style={{ margin: "0 0 4px 0" }}>{title}</h3>
          )}

          <p style={{ fontSize: "11px", fontFamily: "monospace", color: "#64748b", margin: "4px 0" }}>ID: {order.identifier}</p>

          {formattedDateTime && (
            <p style={{ fontSize: "11px", color: "#64748b", margin: "0 0 4px 0" }}>
              Date &amp; Time: {formattedDateTime}
            </p>
          )}

          {order.paymentMethod && (
  <p style={{ fontSize: "11px", color: "#64748b", margin: "0 0 8px 0" }}>
    Payment Method: {order.paymentMethod}
  </p>
)}

          <div style={{ border: "1px solid #e2e8f0", borderRadius: "8px", overflow: "hidden", margin: "12px 0" }}>
            <div style={{ backgroundColor: "#0f172a", color: "#fff", padding: "8px 10px", fontSize: "10px", fontWeight: "700", display: "flex", textTransform: "uppercase", letterSpacing: "0.03em" }}>
              <div style={{ flex: "3" }}>Product</div>
              <div style={{ flex: "1.3", textAlign: "center" }}>MRP</div>
              <div style={{ flex: "1.3", textAlign: "center" }}>Discount</div>
              <div style={{ flex: "1.3", textAlign: "center" }}>Unit Price</div>
              <div style={{ flex: "0.8", textAlign: "center" }}>Qty</div>
              <div style={{ flex: "1.5", textAlign: "right" }}>Sub Total</div>
            </div>

            {order.entryList?.map((item, i) => {
              const productInfo = productMap[item.productIdentifier];
              const displayName = productInfo
                ? `${productInfo.brand || ""} ${productInfo.productName || ""}`.trim() || item.productIdentifier
                : item.productIdentifier;

              return (
                <div
                  key={`${item.productIdentifier}-${i}`}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    padding: "8px 10px",
                    fontSize: "12px",
                    backgroundColor: i % 2 === 0 ? "#ffffff" : "#f8fafc",
                    borderTop: "1px solid #f1f5f9",
                  }}
                >
                  <div style={{ flex: "3" }}>
                    <div style={{ fontWeight: "600", color: "#0f172a" }}>{displayName}</div>
                    <div style={{ fontSize: "10px", color: "#94a3b8", fontFamily: "monospace" }}>ID: {item.productIdentifier}</div>
                  </div>
                  <div style={{ flex: "1.3", textAlign: "center", color: "#94a3b8", textDecoration: "line-through" }}>₹{item.originalPrice}</div>
                  <div style={{ flex: "1.3", textAlign: "center", color: "#16a34a" }}>-₹{item.discount || 0}</div>
                  <div style={{ flex: "1.3", textAlign: "center", fontWeight: "600", color: "#0f172a" }}>₹{item.unitPrice}</div>
                  <div style={{ flex: "0.8", textAlign: "center", fontWeight: "600" }}>{item.quantity}</div>
                  <div style={{ flex: "1.5", textAlign: "right", fontWeight: "700", color: "#0f172a" }}>₹{item.totalPrice}</div>
                </div>
              );
            })}
          </div>

          <div style={{ display: "flex", flexDirection: "column", gap: "4px", fontSize: "13px", padding: "10px 0", borderTop: "1px dashed #cbd5e1" }}>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <span>Gross Total (MRP):</span>
              <span style={{ textDecoration: "line-through", color: "#94a3b8" }}>₹{order.originalPrice}</span>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between", color: "#16a34a" }}>
              <span>Total Discount Savings:</span>
              <span>-₹{order.discount}</span>
            </div>
            <div style={{ display: "flex", justifyContent: "space-between", fontWeight: "800", fontSize: "16px", paddingTop: "6px", borderTop: "1px solid #e2e8f0" }}>
              <span>Net Amount Paid:</span>
              <span>₹{order.totalPrice}</span>
            </div>

            <div style={{ display: "flex", justifyContent: "space-between", color: "#64748b", marginTop: "6px" }}>
              <span>Payment Method:</span>
              <span style={{ fontWeight: "700" }}>{order.paymentMethod}</span>
            </div>

            {order.paymentMethod === "CASH" ? (
              <>
                <div style={{ display: "flex", justifyContent: "space-between", color: "#64748b" }}>
                  <span>Cash Received:</span>
                  <span>₹{order.receivedAmount}</span>
                </div>
                <div style={{ display: "flex", justifyContent: "space-between", color: "#64748b" }}>
                  <span>Change Returned:</span>
                  <span>₹{order.changeAmount}</span>
                </div>
              </>
            ) : (
              <div style={{ display: "flex", justifyContent: "space-between", color: "#64748b" }}>
                <span>Amount Paid:</span>
                <span>₹{order.receivedAmount ?? order.totalPrice}</span>
              </div>
            )}
          </div>

          <div className="no-print-close-btn" style={{ display: "flex", gap: "10px", marginTop: "20px" }}>
            <button onClick={handlePrintReceipt} style={{ flex: 1, padding: "10px", backgroundColor: "#2563eb", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer", fontWeight: "700" }}>🖨️ Print Receipt</button>
            <button onClick={onClose} style={{ flex: 1, padding: "10px", borderRadius: "6px", cursor: "pointer", border: "1px solid #cbd5e1", backgroundColor: "#fff" }}>Close</button>
          </div>

        </div>
      </div>
    </>
  );
}

OrderReceiptModal.propTypes = {
  order: PropTypes.shape({
    identifier: PropTypes.string,
    createdOn: PropTypes.string,
    createdDate: PropTypes.string,
    orderDate: PropTypes.string,
    orderDateTime: PropTypes.string,
    paymentMethod: PropTypes.string,
    entryList: PropTypes.arrayOf(PropTypes.object),
    originalPrice: PropTypes.number,
    discount: PropTypes.number,
    totalPrice: PropTypes.number,
    receivedAmount: PropTypes.number,
    changeAmount: PropTypes.number,
  }),
  onClose: PropTypes.func.isRequired,
  productMap: PropTypes.object,
  title: PropTypes.string,
  showSuccessIcon: PropTypes.bool,
};