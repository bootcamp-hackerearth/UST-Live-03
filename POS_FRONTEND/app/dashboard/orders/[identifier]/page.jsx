"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import { useParams } from "next/navigation";

export default function OrderDetailPage() {
  const { identifier } = useParams();
  const [order, setOrder] = useState(null);

  useEffect(() => {
    if (identifier) fetchOrder();
  }, [identifier]);

  const fetchOrder = async () => {
    const res = await axios.get(`/order/get?identifier=${identifier}`);
    setOrder(res.data);
  };

  const fmt = (n) =>
    Number(n).toLocaleString("en-IN", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

  if (!order) {
    return (
      <div className="min-h-screen bg-gray-100 flex items-center justify-center">
        <div className="w-6 h-6 border-2 border-gray-300 border-t-black rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div
      className="min-h-screen flex justify-center items-start py-10"
      style={{
        background: "linear-gradient(to bottom right, #f3f4f6, #e5e7eb)"
      }}
    >

      <div className="w-full max-w-md bg-white rounded-3xl shadow-2xl overflow-hidden">

        <div className="bg-black text-white px-6 py-6">
          <div className="flex justify-between items-center">
            <div>
              <h2 className="text-xl font-semibold">Order Receipt</h2>
              <p className="text-xs text-white/50 mt-1">POS System</p>
            </div>

            <span className="text-[11px] bg-green-500/20 px-3 py-1 rounded-full text-green-400">
              Completed
            </span>
          </div>

          <div className="mt-6 grid grid-cols-2 gap-4 text-xs">
            <div>
              <p className="text-white/50 mb-1 uppercase">Order ID</p>
              <p className="font-mono text-sm">{order.identifier}</p>
            </div>

            <div className="text-right">
              <p className="text-white/50 mb-1 uppercase">Date</p>
              <p className="font-mono text-sm">
                {order.orderDate
                  ? new Date(order.orderDate).toLocaleString("en-IN")
                  : "-"}
              </p>
            </div>
          </div>
        </div>

        <div className="px-6 py-5">

          <div className="flex justify-between text-xs text-gray-400 uppercase mb-3">
            <span>Item</span>
            <span>Qty</span>
            <span>Price</span>
          </div>

          <div className="space-y-3">
            {order.orderEntryDtoList?.map((item) => (
              <div
                key={item.identifier}
                className="flex justify-between items-center bg-gray-50 rounded-xl px-3 py-2"
              >
                <div className="text-sm text-gray-800 flex-1 truncate">
                  {item.product}
                </div>

                <div className="w-10 text-center text-sm font-mono text-gray-600">
                  {item.quantity}
                </div>

                <div className="w-20 text-right text-sm font-semibold text-gray-900 font-mono">
                  ₹{fmt(item.totalPrice)}
                </div>
              </div>
            ))}
          </div>

        </div>

        <div className="px-6 py-5 border-t space-y-2 bg-gray-50">

          <div className="flex justify-between text-sm text-gray-500">
            <span>Subtotal</span>
            <span className="font-mono">
              ₹{fmt(order.totalOriginalPrice)}
            </span>
          </div>

          <div className="flex justify-between text-sm text-red-500">
            <span>Discount</span>
            <span className="font-mono">
              - ₹{fmt(order.discount)}
            </span>
          </div>

          <div className="border-t pt-3 mt-2 flex justify-between items-center">
            <span className="text-base font-semibold text-gray-800">
              Total
            </span>
            <span className="text-xl font-bold text-black font-mono">
              ₹{fmt(order.totalPrice)}
            </span>
          </div>

        </div>

        <div className="flex gap-3 p-5">
          <button
            onClick={() => globalThis.print()}
            className="flex-1 bg-black text-white py-3 rounded-xl text-sm font-medium hover:bg-gray-800 transition"
          >
            Print
          </button>

          <button
            onClick={() => history.back()}
            className="flex-1 border border-gray-300 text-gray-700 py-3 rounded-xl text-sm hover:bg-gray-100 transition"
          >
            Back
          </button>
        </div>

      </div>

    </div>
  );
}