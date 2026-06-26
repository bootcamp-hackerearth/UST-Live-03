"use client";
import { useState } from "react";
import PropTypes from "prop-types";

const PAYMENT_METHODS = [
  {
    id: "CASH",
    label: "Cash",
    description: "Pay with physical currency",
    icon: "💵",
  },
  {
    id: "CARD",
    label: "Card",
    description: "Debit or credit card",
    icon: "💳",
  },
  {
    id: "ONLINE",
    label: "Online",
    description: "UPI, net banking, wallet",
    icon: "📱",
  },
];

export default function PaymentModal({ open, total, onClose, onConfirm, loading }) {
  const [selectedMethod, setSelectedMethod] = useState("");

  const handleClose = () => {
    setSelectedMethod("");
    onClose();
  };

  const handleConfirm = () => {
    if (!selectedMethod) return;
    onConfirm(selectedMethod);
  };

  if (!open) return null;

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50">
      <div className="bg-white w-full max-w-md rounded-3xl shadow-2xl overflow-hidden">

        <div className="px-8 pt-8 pb-6 border-b border-[#f0f2f5]">
          <p className="text-xs font-semibold uppercase tracking-widest text-[#6b7a99] mb-1">Payment</p>
          <h2 className="text-2xl font-bold text-[#101828]">Select Payment Method</h2>
          <p className="text-[#6b7a99] mt-1 text-sm">
            Collecting <span className="font-semibold text-[#101828]">₹{total}</span> from customer
          </p>
        </div>

        <div className="px-8 py-6 space-y-3">
          {PAYMENT_METHODS.map((method) => (
            <button
              key={method.id}
              type="button"
              onClick={() => setSelectedMethod(method.id)}
              className={`w-full flex items-center gap-4 px-5 py-4 rounded-2xl border-2 transition-all text-left ${
                selectedMethod === method.id
                  ? "border-[#1570ef] bg-[#eff6ff]"
                  : "border-[#e3e8ef] bg-white hover:border-[#b0c4de] hover:bg-[#f8fafc]"
              }`}
            >
              <span className="text-2xl">{method.icon}</span>
              <div className="flex-1">
                <p className={`text-sm font-semibold ${selectedMethod === method.id ? "text-[#1570ef]" : "text-[#101828]"}`}>
                  {method.label}
                </p>
                <p className="text-xs text-[#6b7a99] mt-0.5">{method.description}</p>
              </div>
              <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center transition-all ${
                selectedMethod === method.id
                  ? "border-[#1570ef] bg-[#1570ef]"
                  : "border-[#d0d5dd]"
              }`}>
                {selectedMethod === method.id && (
                  <div className="w-2 h-2 rounded-full bg-white" />
                )}
              </div>
            </button>
          ))}
        </div>

        <div className="px-8 pb-8 flex gap-3">
          <button
            type="button"
            onClick={handleClose}
            disabled={loading}
            className="flex-1 h-12 rounded-2xl border border-[#d0d5dd] text-[#344054] font-medium hover:bg-[#f9fafb] transition-all disabled:opacity-50"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={handleConfirm}
            disabled={loading || !selectedMethod}
            className="flex-1 h-12 rounded-2xl bg-[#1570ef] hover:bg-[#1264d3] text-white font-semibold transition-all disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? "Processing..." : `Confirm ₹${total}`}
          </button>
        </div>

      </div>
    </div>
  );
}

PaymentModal.propTypes = {
  open: PropTypes.bool,
  total: PropTypes.number,
  onClose: PropTypes.func,
  onConfirm: PropTypes.func,
  loading: PropTypes.bool,
};