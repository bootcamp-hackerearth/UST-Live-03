export function formatCurrency(value) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(Number(value) || 0);
}

export function formatDateTime(value) {
  if (!value) return "-";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return String(value);
  return new Intl.DateTimeFormat("en-IN", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(date);
}

export function printReceipt({ order, entries, subtotal, discount, total, payment }) {
  const win = globalThis.open("", "_blank", "width=420,height=600");
  if (!win) {
    alert("Popup blocker prevented printing. Please allow popups for this site.");
    return;
  }

  const rows = entries
    .map(
      (entry) => `
        <tr>
          <td>${entry.product || "-"}</td>
          <td style="text-align:center">${Number(entry.quantity) || 0}</td>
          <td style="text-align:right">${formatCurrency(entry.sellingPrice ?? entry.price)}</td>
          <td style="text-align:right">${formatCurrency(entry.totalPrice)}</td>
        </tr>`
    )
    .join("");

  const html = `
    <html>
      <head>
        <title>Receipt ${order.orderId || order.identifier || ""}</title>
        <style>
          * { box-sizing: border-box; }
          body { font-family: 'Courier New', monospace; padding: 16px; color: #111; max-width: 360px; margin: 0 auto; }
          h1 { font-size: 16px; text-align: center; margin: 0 0 4px; letter-spacing: 1px; }
          .sub { text-align: center; font-size: 11px; color: #555; margin-bottom: 12px; }
          .row { display: flex; justify-content: space-between; font-size: 12px; margin: 2px 0; }
          hr { border: none; border-top: 1px dashed #999; margin: 10px 0; }
          table { width: 100%; border-collapse: collapse; font-size: 11px; margin-top: 6px; }
          th { text-align: left; border-bottom: 1px solid #333; padding: 4px 2px; font-size: 10px; text-transform: uppercase; }
          td { padding: 4px 2px; vertical-align: top; }
          .totals .row { font-size: 12px; }
          .grand { font-size: 14px; font-weight: bold; }
          .footer { text-align: center; font-size: 11px; margin-top: 16px; color: #555; }
        </style>
      </head>
      <body>
        <h1>RetailPOS</h1>
        <p class="sub">Sales Receipt</p>
        <div class="row"><span>Order ID</span><span>${order.orderId || "-"}</span></div>
        <div class="row"><span>Customer</span><span>${order.identifier || "Walk-in"}</span></div>
        <div class="row"><span>Date</span><span>${formatDateTime(order.orderDate)}</span></div>
        <div class="row"><span>Payment</span><span>${order.paymentMode || "-"}</span></div>
        <hr/>
        <table>
          <thead><tr><th>Item</th><th style="text-align:center">Qty</th><th style="text-align:right">Price</th><th style="text-align:right">Total</th></tr></thead>
          <tbody>${rows || `<tr><td colspan="4" style="text-align:center;color:#888;">No items</td></tr>`}</tbody>
        </table>
        <hr/>
        <div class="totals">
          <div class="row"><span>Subtotal</span><span>${formatCurrency(subtotal)}</span></div>
          <div class="row"><span>Discount</span><span>-${formatCurrency(discount)}</span></div>
          <div class="row grand"><span>Total</span><span>${formatCurrency(total)}</span></div>
          ${payment?.paid ? `
          <div class="row"><span>Received</span><span>${formatCurrency(payment.receivedAmount)}</span></div>
          <div class="row"><span>Change</span><span>${formatCurrency(payment.changeAmount)}</span></div>` : ""}
        </div>
        <hr/>
        <p class="footer">Thank you for shopping with us!</p>
        
        <script>
          window.onload = function() {
            window.focus();
            setTimeout(function() {
              window.print();
              window.close();
            }, 300);
          };
        </script>
      </body>
    </html>
  `;

  const blob = new Blob([html], { type: "text/html" });
  const url = URL.createObjectURL(blob);
  win.location.href = url;
  win.addEventListener?.("unload", () => URL.revokeObjectURL(url));
}