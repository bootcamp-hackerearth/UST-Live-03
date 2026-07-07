"use client";

import React, { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";

export default function OrderInvoiceDetailsView() {
  const { id } = useParams();
  const router = useRouter();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchInvoiceDetails = async () => {
      const token = localStorage.getItem("token");
      try {
        const res = await fetch(`/api/orders/${id}`, {
          headers: { Authorization: token ? `Bearer ${token}` : "" },
        });
        if (!res.ok) throw new Error(`Status ${res.status}`);
        const data = await res.json();
        setOrder(data);
      } catch (err) {
        console.error("Error fetching invoice:", err);
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchInvoiceDetails();
  }, [id]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 text-sm text-slate-500 font-medium">
        Loading invoice...
      </div>
    );
  }

  if (!order) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50 gap-4">
        <p className="text-sm text-slate-500">Invoice could not be found.</p>
        <button
          onClick={() => router.push("/orders")}
          className="text-xs bg-slate-800 text-white px-4 py-2 rounded-lg"
        >
          Back to Orders
        </button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-100 py-8 print:p-0 print:bg-white text-slate-800">
      <div className="max-w-md mx-auto mb-4 flex justify-between items-center px-4 print:hidden">
        <button
          onClick={() => router.push("/orders")}
          className="text-xs font-semibold text-slate-600 hover:text-slate-900 bg-white border px-3 py-2 rounded-lg shadow-sm transition-colors"
        >
          ← Return to Orders
        </button>
        <button
          onClick={() => globalThis.print()}
          className="text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 px-4 py-2 rounded-lg shadow-sm transition-colors"
        >
          🖨️ Print Receipt
        </button>
      </div>

      <div className="max-w-md mx-auto bg-white border border-slate-200 p-6 shadow-sm rounded-xl print:border-none print:shadow-none font-mono text-xs">
        <div className="text-center space-y-1 mb-4">
          <h2 className="text-base font-bold tracking-tight text-slate-900">
            POS RETAIL APP
          </h2>
          <p className="text-slate-500 text-[11px]">
            Warehouse: {order.warehouseIdentifier}
          </p>
          <div className="border-b border-dashed border-slate-300 my-2"></div>
        </div>

        <div className="space-y-1 mb-4 text-[11px] text-slate-600">
          <div className="flex justify-between">
            <span>INVOICE :</span>
            <span className="font-bold text-slate-900">
              {order.invoiceCode}
            </span>
          </div>
          <div className="flex justify-between">
            <span>CUSTOMER:</span>
            <span className="font-bold text-slate-900">
              {order.customerIdentifier}
            </span>
          </div>
          <div className="flex justify-between">
            <span>DATE :</span>
            <span>
              {order.createdOn
                ? new Date(order.createdOn).toLocaleString()
                : "N/A"}
            </span>
          </div>
        </div>

        <div className="border-b border-dashed border-slate-300 my-2"></div>

        <table className="w-full text-left my-3 border-collapse">
          <thead>
            <tr className="border-b border-slate-200 text-slate-400 font-semibold text-[11px]">
              <th className="pb-1 text-left">ITEM</th>
              <th className="pb-1 text-center w-12">QTY</th>
              <th className="pb-1 text-right w-16">PRICE</th>
              <th className="pb-1 text-right w-20">TOTAL</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {order.entryList?.map((entry, index) => (
              <tr key={entry.identifier || index} className="text-slate-700">
                <td className="py-2 font-semibold truncate max-w-[140px]">
                  {entry.product}
                </td>
                <td className="py-2 text-center">{entry.quantity}</td>
                <td className="py-2 text-right">
                  ${Number(entry.sellingPrice || entry.price || 0).toFixed(2)}
                </td>
                <td className="py-2 text-right font-bold">
                  ${Number(entry.totalPrice || 0).toFixed(2)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <div className="border-b border-dashed border-slate-300 my-2"></div>

        <div className="space-y-1.5 pt-1 text-[11px]">
          <div className="flex justify-between text-slate-500">
            <span>Subtotal (Original):</span>
            <span>${Number(order.originalPrice || 0).toFixed(2)}</span>
          </div>
          <div className="flex justify-between text-rose-600">
            <span>Discount:</span>
            <span>-${Number(order.totalDiscount || 0).toFixed(2)}</span>
          </div>
          <div className="flex justify-between text-base font-bold text-slate-900 pt-1 border-t border-dotted">
            <span>NET PAYABLE:</span>
            <span>${Number(order.totalPrice || 0).toFixed(2)}</span>
          </div>

          <div className="border-b border-dashed border-slate-300 my-2 pt-1"></div>

          <div className="flex justify-between text-slate-500">
            <span>Payment Method:</span>
            <span className="font-semibold text-slate-800">
              {order.paymentMethod}
            </span>
          </div>
          <div className="flex justify-between text-slate-500">
            <span>Amount Received:</span>
            <span>${Number(order.amountReceived || 0).toFixed(2)}</span>
          </div>
          <div className="flex justify-between text-slate-700 font-bold">
            <span>Change Returned:</span>
            <span>${Number(order.changeAmount || 0).toFixed(2)}</span>
          </div>
          <div className="flex justify-between text-slate-700 font-bold">
            <span>Balance Due:</span>
            <span className="text-rose-600">
              ${Number(order.dueAmount || 0).toFixed(2)}
            </span>
          </div>
        </div>

        {order.transactionNotes && (
          <div className="mt-4 p-2 bg-slate-50 border rounded text-[10px] text-slate-500 leading-relaxed italic">
            <strong>Notes:</strong> {order.transactionNotes}
          </div>
        )}

        <div className="text-center text-[10px] text-slate-400 mt-6 tracking-wide uppercase">
          Thank you for shopping with us!
        </div>
      </div>
    </div>
  );
}
