"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import PropTypes from "prop-types";

const styles = {
  overlay: {
    position: "fixed",
    inset: 0,
    background: "rgba(0,0,0,0.5)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 100,
  },
  modal: {
    background: "#fff",
    borderRadius: 16,
    padding: 28,
    width: 440,
    maxHeight: "85vh",
    overflowY: "auto",
    boxShadow: "0 20px 60px rgba(0,0,0,0.2)",
  },
  modalHeader: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 16,
  },
  modalTitle: { fontWeight: 700, fontSize: 18, margin: 0 },
  closeBtn: {
    background: "none",
    border: "none",
    cursor: "pointer",
    fontSize: 20,
    color: "#6b7280",
  },
  customerLine: { fontSize: 13, color: "#6b7280", marginBottom: 14 },
  itemsBox: {
    border: "1px solid #f3f4f6",
    borderRadius: 10,
    marginBottom: 14,
    overflow: "hidden",
  },
  itemsTable: { width: "100%", borderCollapse: "collapse" },
  itemsTh: {
    padding: "8px 10px",
    textAlign: "left",
    fontSize: 11,
    color: "#9ca3af",
    textTransform: "uppercase",
    letterSpacing: 0.3,
    background: "#f9fafb",
  },
  itemsTd: {
    padding: "8px 10px",
    fontSize: 13,
    borderTop: "1px solid #f9fafb",
  },
  itemsEmpty: {
    padding: 16,
    textAlign: "center",
    color: "#9ca3af",
    fontSize: 13,
  },
  summaryRow: {
    display: "flex",
    justifyContent: "space-between",
    fontSize: 13,
    marginBottom: 6,
  },
  summaryTotalRow: {
    display: "flex",
    justifyContent: "space-between",
    marginTop: 10,
    paddingTop: 10,
    borderTop: "1px solid #f3f4f6",
  },
  totalLabel: { fontSize: 12, color: "#6b7280", marginBottom: 4 },
  totalAmount: { fontSize: 32, fontWeight: 700, marginBottom: 20 },
  methodRow: { display: "flex", gap: 10, marginBottom: 20 },
  methodBtn: (active) => ({
    flex: 1,
    padding: "10px 0",
    borderRadius: 8,
    fontWeight: 600,
    fontSize: 14,
    border: active ? "2px solid #000" : "2px solid #e5e7eb",
    background: active ? "#000" : "#fff",
    color: active ? "#fff" : "#111",
    cursor: "pointer",
    transition: "all 0.15s",
  }),
  inputLabel: { fontSize: 13, color: "#6b7280", marginBottom: 6 },
  input: {
    width: "100%",
    border: "1px solid #d1d5db",
    borderRadius: 8,
    padding: "10px 14px",
    fontSize: 15,
    outline: "none",
    boxSizing: "border-box",
    marginBottom: 16,
  },
  confirmBtn: (disabled) => ({
    width: "100%",
    padding: "14px 0",
    borderRadius: 10,
    fontWeight: 700,
    fontSize: 15,
    border: "none",
    background: disabled ? "#d1d5db" : "#ef4444",
    color: "#fff",
    cursor: disabled ? "not-allowed" : "pointer",
  }),
  successOverlay: {
    position: "fixed",
    inset: 0,
    background: "rgba(0,0,0,0.5)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 200,
  },
  successBox: {
    background: "#fff",
    borderRadius: 20,
    padding: 36,
    width: 380,
    textAlign: "center",
    boxShadow: "0 20px 60px rgba(0,0,0,0.2)",
  },
  successTitle: {
    color: "#16a34a",
    fontWeight: 700,
    fontSize: 17,
    marginBottom: 20,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
  },
  infoRow: {
    display: "flex",
    justifyContent: "space-between",
    marginBottom: 10,
    fontSize: 14,
  },
  infoLabel: { color: "#6b7280", fontWeight: 500 },
  infoValue: { fontWeight: 600, color: "#111" },
  divider: { borderTop: "1px solid #f3f4f6", margin: "16px 0" },
  btnRow: { display: "flex", gap: 12, marginTop: 12 },
  homeBtn: {
    flex: 1,
    padding: "12px 0",
    borderRadius: 10,
    fontWeight: 700,
    fontSize: 14,
    border: "none",
    background: "#111",
    color: "#fff",
    cursor: "pointer",
  },
  viewOrderBtn: {
    flex: 1,
    padding: "12px 0",
    borderRadius: 10,
    fontWeight: 700,
    fontSize: 14,
    border: "none",
    background: "#10b981",
    color: "#fff",
    cursor: "pointer",
  },
  printBtn: {
    width: "100%",
    padding: "12px 0",
    borderRadius: 10,
    fontWeight: 700,
    fontSize: 14,
    border: "1px solid #e5e7eb",
    background: "#fff",
    color: "#111",
    cursor: "pointer",
    marginTop: 20,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: 8,
  },
};

const fmt = (n) =>
  Number(n || 0).toLocaleString("en-IN", { minimumFractionDigits: 2 });
const METHODS = ["Cash", "Card", "UPI"];

export default function PaymentModal({
  totalPayable,
  totalDiscount,
  customerName,
  onConfirm,
  onClose,
  placing,
  entries = [],
  products = [],
}) {
  const router = useRouter();
  const [method, setMethod] = useState("Cash");
  const [upiId, setUpiId] = useState("");
  const [orderResult, setOrderResult] = useState(null);

  const getProductName = (productId) =>
    products.find((p) => p.identifier === productId)?.productName || productId;

  const handlePrint = () => {
    if (!orderResult) return;
    const dateStr = new Date().toLocaleString("en-IN");

    const itemsHtml =
      entries && entries.length > 0
        ? `
        <hr/>
        <table style="width:100%; border-collapse:collapse; font-size:11px;">
          <thead>
            <tr>
              <td style="font-weight:bold; padding:2px 0;">Item</td>
              <td style="font-weight:bold; padding:2px 0; text-align:center;">Qty</td>
              <td style="font-weight:bold; padding:2px 0; text-align:right;">Amount</td>
            </tr>
          </thead>
          <tbody>
            ${entries
              .map(
                (e) => `
              <tr>
                <td style="padding:2px 0;">${getProductName(e.product)}</td>
                <td style="padding:2px 0; text-align:center;">${e.quantity}</td>
                <td style="padding:2px 0; text-align:right;">${fmt(e.totalPrice)}</td>
              </tr>
            `,
              )
              .join("")}
          </tbody>
        </table>
      `
        : "";

    const receiptHtml = `
      <html>
        <head>
          <title>Receipt - ${orderResult.orderId}</title>
          <style>
            * { box-sizing: border-box; }
            body { font-family: 'Courier New', monospace; font-size: 12px; width: 280px; margin: 0 auto; padding: 16px; color: #111; }
            h2 { text-align: center; margin: 0 0 2px; letter-spacing: 1px; }
            .sub { text-align: center; font-size: 11px; color: #555; margin: 0 0 12px; }
            .row { display: flex; justify-content: space-between; margin: 4px 0; }
            hr { border: none; border-top: 1px dashed #000; margin: 10px 0; }
            .total { font-weight: bold; font-size: 14px; }
            .center { text-align: center; }
            @media print { body { padding: 0; } }
          </style>
        </head>
        <body>
          <h2>RECEIPT</h2>
          <p class="sub">${dateStr}</p>
          <hr/>
          <div class="row"><span>Order No:</span><span>${orderResult.orderId}</span></div>
          <div class="row"><span>Customer:</span><span>${customerName || "-"}</span></div>
          <div class="row"><span>Payment:</span><span>${orderResult.method}</span></div>
          ${itemsHtml}
          <hr/>
          <div class="row total"><span>Amount Paid:</span><span>Rs. ${fmt(orderResult.amount)}</span></div>
          <hr/>
          <p class="center">Thank you for your purchase!</p>
        </body>
      </html>
    `;

    const printWindow = window.open(
      "",
      "PRINT_RECEIPT",
      "height=600,width=380",
    );
    if (!printWindow) return;
    printWindow.document.open();
    printWindow.document.documentElement.innerHTML = receiptHtml;
    printWindow.document.close();
    printWindow.focus();
    printWindow.onafterprint = () => printWindow.close();
    setTimeout(() => {
      printWindow.print();
    }, 200);
  };

  const isDisabled = () => {
    if (placing) return true;
    if (method === "UPI" && !upiId.trim()) return true;
    return false;
  };

  const handleConfirm = async () => {
    await onConfirm(method, (orderId) => {
      setOrderResult({ orderId, method, amount: totalPayable });
    });
  };

  if (orderResult) {
    return (
      <div style={styles.successOverlay}>
        <div style={styles.successBox}>
          <p style={styles.successTitle}>✅ Order Placed Successfully</p>
          <div style={styles.infoRow}>
            <span style={styles.infoLabel}>Order No:</span>
            <span style={styles.infoValue}>{orderResult.orderId}</span>
          </div>
          <div style={styles.infoRow}>
            <span style={styles.infoLabel}>Customer:</span>
            <span style={styles.infoValue}>{customerName}</span>
          </div>
          <div style={styles.infoRow}>
            <span style={styles.infoLabel}>Payment Mode:</span>
            <span style={styles.infoValue}>{orderResult.method}</span>
          </div>
          <div style={styles.infoRow}>
            <span style={styles.infoLabel}>Amount Paid:</span>
            <span style={styles.infoValue}>₹{fmt(orderResult.amount)}</span>
          </div>
          <div style={styles.divider} />
          <div style={styles.btnRow}>
            <button type="button" style={styles.homeBtn} onClick={onClose}>
              Home
            </button>
            <button
              type="button"
              style={styles.viewOrderBtn}
              onClick={() => {
                onClose();
                router.push(`/orders/list?orderId=${orderResult.orderId}`);
              }}
            >
              View Order
            </button>
          </div>
          <button type="button" style={styles.printBtn} onClick={handlePrint}>
            🖨 Print Receipt
          </button>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.overlay}>
      <div style={styles.modal}>
        <div style={styles.modalHeader}>
          <h3 style={styles.modalTitle}>Payment</h3>
          <button type="button" style={styles.closeBtn} onClick={onClose}>
            ✕
          </button>
        </div>

        {customerName && (
          <p style={styles.customerLine}>
            Customer: <strong>{customerName}</strong>
          </p>
        )}

        <div style={styles.itemsBox}>
          <table style={styles.itemsTable}>
            <thead>
              <tr>
                <th style={styles.itemsTh}>Item</th>
                <th style={{ ...styles.itemsTh, textAlign: "center" }}>Qty</th>
                <th style={{ ...styles.itemsTh, textAlign: "right" }}>
                  Subtotal
                </th>
              </tr>
            </thead>
            <tbody>
              {entries.length === 0 ? (
                <tr>
                  <td colSpan={3} style={styles.itemsEmpty}>
                    No items in this order.
                  </td>
                </tr>
              ) : (
                entries.map((e, i) => (
                  <tr key={e.identifier ?? i}>
                    <td style={styles.itemsTd}>{getProductName(e.product)}</td>
                    <td style={{ ...styles.itemsTd, textAlign: "center" }}>
                      {e.quantity}
                    </td>
                    <td
                      style={{
                        ...styles.itemsTd,
                        textAlign: "right",
                        fontWeight: 600,
                      }}
                    >
                      ₹{fmt(e.totalPrice)}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div style={styles.summaryRow}>
          <span style={{ color: "#6b7280" }}>Discount</span>
          <span style={{ color: "#16a34a" }}>- ₹{fmt(totalDiscount)}</span>
        </div>

        <p style={styles.totalLabel}>Total Amount</p>
        <p style={styles.totalAmount}>₹{fmt(totalPayable)}</p>

        <div style={styles.methodRow}>
          {METHODS.map((m) => (
            <button
              key={m}
              type="button"
              style={styles.methodBtn(method === m)}
              onClick={() => setMethod(m)}
            >
              {m}
            </button>
          ))}
        </div>

        {method === "Cash" && (
          <p style={{ color: "#6b7280", fontSize: 14, marginBottom: 20 }}>
            Collect cash payment from the customer.
          </p>
        )}

        {method === "UPI" && (
          <>
            <p style={styles.inputLabel}>UPI ID</p>
            <input
              type="text"
              placeholder="Enter UPI ID (e.g. name@upi)"
              value={upiId}
              onChange={(e) => setUpiId(e.target.value)}
              style={styles.input}
            />
          </>
        )}

        {method === "Card" && (
          <p style={{ color: "#6b7280", fontSize: 14, marginBottom: 20 }}>
            Swipe or tap card on the card machine to proceed.
          </p>
        )}

        <button
          type="button"
          style={styles.confirmBtn(isDisabled())}
          disabled={isDisabled()}
          onClick={handleConfirm}
        >
          {placing ? "Processing..." : "Confirm Payment"}
        </button>
      </div>
    </div>
  );
}

PaymentModal.propTypes = {
  totalPayable: PropTypes.number,
  totalDiscount: PropTypes.number,
  customerName: PropTypes.string,
  onConfirm: PropTypes.func.isRequired,
  onClose: PropTypes.func.isRequired,
  placing: PropTypes.bool,
  entries: PropTypes.array,
  products: PropTypes.array,
};
