"use client";

import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";

export default function PaymentModal({ isOpen, onClose, totalAmount, onConfirm, loading }) {
  const [paymentMethod, setPaymentMethod] = useState("CASH");
  const [receivedAmount, setReceivedAmount] = useState("");
  const [change, setChange] = useState(0);

  useEffect(() => {
    const received = Number.parseFloat(receivedAmount) || 0;
    setChange(received > totalAmount ? received - totalAmount : 0);
  }, [receivedAmount, totalAmount]);

  if (!isOpen) return null;

  const handleSubmit = (e) => {
    e.preventDefault();
    onConfirm({ paymentMethod, receivedAmount: paymentMethod === "CASH" ? Number.parseFloat(receivedAmount) || 0 : totalAmount,});
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-sm p-4">
      <div className="w-full max-w-md bg-white rounded-xl shadow-2xl border p-6 space-y-4">
        <h3 className="text-lg font-black text-slate-900">Process Payment</h3>
        <p className="text-sm font-bold text-slate-600">Total Due: <span className="text-blue-600">${totalAmount.toFixed(2)}</span></p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="method" className="block text-xs font-black text-slate-700 uppercase mb-1">Method</label>
            <select 
              id="method"
              value={paymentMethod} 
              onChange={(e) => setPaymentMethod(e.target.value)}
              className="w-full border p-2.5 rounded-xl font-semibold outline-none"
            >
              <option value="CASH">Cash</option>
              <option value="CARD">Card / Terminal</option>
            </select>
          </div>

          {paymentMethod === "CASH" && (
            <>
              <div>
                <label htmlFor="recievedamt" className="block text-xs font-black text-slate-700 uppercase mb-1">Received Amount</label>
                <input 
                  id="recievedamt" 
                  type="number" 
                  step="0.01" 
                  required
                  placeholder="0.00"
                  value={receivedAmount}
                  onChange={(e) => setReceivedAmount(e.target.value)}
                  className="w-full border p-2.5 rounded-xl font-semibold outline-none"
                />
              </div>
              <div className="p-3 bg-slate-50 border rounded-xl text-sm font-bold text-slate-700 flex justify-between">
                <span>Change Return:</span>
                <span className="text-emerald-600">${change.toFixed(2)}</span>
              </div>
            </>
          )}

          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={onClose} disabled={loading} className="px-4 py-2 border rounded-xl font-bold text-sm text-slate-600 hover:bg-slate-50">Cancel</button>
            <button type="submit" disabled={loading} className="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm rounded-xl transition shadow">
              {loading ? "Processing..." : "Complete Checkout"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

PaymentModal.propTypes = {
  isOpen: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  totalAmount: PropTypes.number.isRequired,
  onConfirm: PropTypes.func.isRequired,
  loading: PropTypes.bool,
};
PaymentModal.defaultProps = {
  loading: false,
};