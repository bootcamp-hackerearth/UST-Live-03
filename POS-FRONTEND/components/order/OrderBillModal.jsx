"use client";

import { useRef } from "react";
import PropTypes from "prop-types";

const PAYMENT_BADGE = {
  CASH: "bg-green-100 text-green-700",
  CARD: "bg-blue-100 text-blue-700",
  ONLINE: "bg-purple-100 text-purple-700",
};

const formatDate = (dateString) => {
  if (!dateString) return "—";
  return new Date(dateString).toLocaleString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

export default function OrderBillModal({ order, onClose }) {
  const printRef = useRef();

  if (!order) return null;

const handlePrint = () => {
  if (!printRef.current) {
    return;
  }

  const printWindow = window.open("", "_blank");

  if (!printWindow) {
    return;
  }

  const style = printWindow.document.createElement("style");
  style.textContent = `
    body {
      font-family: 'Segoe UI', sans-serif;
      padding: 40px;
    }
  `;

  printWindow.document.head.appendChild(style);

  const content = printRef.current.cloneNode(true);

  printWindow.document.body.appendChild(content);

  printWindow.focus();

  setTimeout(() => {
    printWindow.print();
    printWindow.close();
  }, 300);
};

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      <button
        type="button"
        className="absolute inset-0 bg-black/40 backdrop-blur-sm"
        onClick={onClose}
        aria-label="Close modal"
      />

      <div className="relative bg-white rounded-3xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">

        <div className="sticky top-0 bg-white z-10 flex items-center justify-between px-8 pt-6 pb-4 border-b border-gray-100">
          <h2 className="text-xl font-bold text-gray-900">Order Bill</h2>
          <button type="button" onClick={onClose} aria-label="Close" className="text-gray-400 hover:text-gray-600 text-2xl font-light leading-none">×</button>
        </div>

        <div ref={printRef} className="px-8 py-6">

          <div className="bill-header text-center mb-8">
            <h1 className="text-3xl font-black tracking-widest text-gray-900">POS</h1>
            <p className="text-gray-400 text-sm mt-1">Retail Management System</p>
          </div>

          <hr className="border-dashed border-gray-300 my-5" />

          <div className="mb-5">
            <p className="text-xs font-semibold uppercase tracking-widest text-gray-400 mb-3">Order Info</p>
            <div className="grid grid-cols-2 gap-x-6 gap-y-3">
              <div className="flex flex-col">
                <span className="text-xs text-gray-400 uppercase tracking-wide">Order No.</span>
                <span className="text-sm font-bold text-gray-900 mt-0.5">#{order.id}</span>
              </div>
              <div className="flex flex-col">
                <span className="text-xs text-gray-400 uppercase tracking-wide">Payment</span>
                <span className={`mt-1 self-start px-3 py-0.5 rounded-xl text-xs font-semibold ${PAYMENT_BADGE[order.paymentMethod] || "bg-gray-100 text-gray-700"}`}>
                  {order.paymentMethod}
                </span>
              </div>
              <div className="flex flex-col">
                <span className="text-xs text-gray-400 uppercase tracking-wide">Customer</span>
                <span className="text-sm font-bold text-gray-900 mt-0.5">{order.identifier}</span>
              </div>
              <div className="flex flex-col">
                <span className="text-xs text-gray-400 uppercase tracking-wide">Date & Time</span>
                <span className="text-sm font-bold text-gray-900 mt-0.5">{formatDate(order.orderedAt)}</span>
              </div>
            </div>
          </div>

          <hr className="border-dashed border-gray-300 my-5" />

          <div className="mb-5">
            <p className="text-xs font-semibold uppercase tracking-widest text-gray-400 mb-3">Items Purchased</p>
            <table className="w-full">
              <thead>
                <tr className="border-b-2 border-gray-200">
                  <th className="text-left py-2 px-1 text-xs uppercase tracking-wide text-gray-400 font-semibold">Product</th>
                  <th className="text-right py-2 px-1 text-xs uppercase tracking-wide text-gray-400 font-semibold">Qty</th>
                  <th className="text-right py-2 px-1 text-xs uppercase tracking-wide text-gray-400 font-semibold">MRP</th>
                  <th className="text-right py-2 px-1 text-xs uppercase tracking-wide text-gray-400 font-semibold">Unit Price</th>
                  <th className="text-right py-2 px-1 text-xs uppercase tracking-wide text-gray-400 font-semibold">Discount</th>
                  <th className="text-right py-2 px-1 text-xs uppercase tracking-wide text-gray-400 font-semibold">Total</th>
                </tr>
              </thead>
              <tbody>
                {(order.entries || []).map((entry) => {
                    const key = entry.id || `${entry.product}-${entry.unitPrice}-${entry.quantity}`;

                    return (
                    <tr key={key} className="border-b border-gray-100">
                        <td className="py-3 px-1 text-sm font-medium text-gray-800">{entry.product}</td>
                        <td className="py-3 px-1 text-sm text-right text-gray-600">{entry.quantity}</td>
                        <td className="py-3 px-1 text-sm text-right text-gray-600">₹{entry.originalPrice}</td>
                        <td className="py-3 px-1 text-sm text-right text-gray-600">₹{entry.unitPrice}</td>
                        <td className="py-3 px-1 text-sm text-right text-green-600 font-medium">−₹{entry.discount}</td>
                        <td className="py-3 px-1 text-sm text-right font-bold text-gray-900">₹{entry.totalPrice}</td>
                    </tr>
                    );
                })}
                </tbody>
            </table>
          </div>

          <hr className="border-dashed border-gray-300 my-5" />

          <div className="space-y-2">
            <div className="flex justify-between text-sm text-gray-500">
              <span>Subtotal (MRP)</span>
              <span>₹{order.originalPrice}</span>
            </div>
            <div className="flex justify-between text-sm text-green-600 font-medium">
              <span>Total Discount</span>
              <span>−₹{order.discount}</span>
            </div>
            <div className="flex justify-between text-lg font-black text-gray-900 pt-3 mt-2 border-t-2 border-gray-900">
              <span>Grand Total</span>
              <span>₹{order.totalPrice}</span>
            </div>
          </div>

          <hr className="border-dashed border-gray-300 my-6" />

          <p className="text-center text-gray-400 text-xs tracking-wide">Thank you for shopping with us!</p>
        </div>

        <div className="sticky bottom-0 bg-white border-t border-gray-100 px-8 py-4 flex gap-3">
          <button
            type="button"
            onClick={onClose}
            className="flex-1 px-6 py-3 rounded-2xl border border-gray-200 text-gray-600 font-semibold hover:bg-gray-50 transition-colors"
          >
            Close
          </button>
          <button
            type="button"
            onClick={handlePrint}
            className="flex-1 px-6 py-3 rounded-2xl bg-[#2563eb] text-white font-semibold hover:bg-blue-700 transition-colors"
          >
            🖨️ Print Bill
          </button>
        </div>
      </div>
    </div>
  );
}

OrderBillModal.propTypes = {
  order: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    paymentMethod: PropTypes.string,
    identifier: PropTypes.string,
    orderedAt: PropTypes.oneOfType([PropTypes.string, PropTypes.instanceOf(Date)]),
    entries: PropTypes.arrayOf(
      PropTypes.shape({
        id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        product: PropTypes.string,
        quantity: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        originalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        unitPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        discount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
        totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      })
    ),
    originalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    discount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  }),
  onClose: PropTypes.func,
};