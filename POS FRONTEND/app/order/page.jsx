"use client";

import { useEffect, useMemo, useState } from "react";
import axiosInstance from "../api/axiosInstance";
import { formatCurrency, formatDateTime, printReceipt } from "../lib/receiptUtils";

function normalizeOrdersResponse(data) {
  if (Array.isArray(data)) return data;
  return data?.dtoList ?? data?.content ?? data?.data ?? [];
}

export default function OrderPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [searchTerm, setSearchTerm] = useState("");

  const fetchOrders = async () => {
    setLoading(true);
    setError("");
    try {
      const res = await axiosInstance.get("/orders/list");
      const list = normalizeOrdersResponse(res.data);
      setOrders(Array.isArray(list) ? list : []);
    } catch (err) {
      setError(err?.response?.data?.message || "Failed to load orders.");
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const filteredOrders = useMemo(() => {
    const term = searchTerm.trim().toLowerCase();
    if (!term) return orders;
    return orders.filter((order) => {
      const haystack = [
        order.orderId,
        order.identifier,
        order.paymentMode,
        order.couponCode,
        order.orderDate,
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();
      return haystack.includes(term);
    });
  }, [orders, searchTerm]);

  const metrics = useMemo(() => {
    let totalSales = 0;
    let totalDiscount = 0;
    orders.forEach((order) => {
      const entries = order.orderEntryDtoList || [];
      const subtotal = entries.reduce((sum, entry) => sum + (Number(entry.totalPrice) || 0), 0) || Number(order.totalPrice) || 0;
      const discount = Number(order.totalDiscount) || 0;
      totalSales += Math.max(0, subtotal - discount);
      totalDiscount += discount;
    });
    return {
      count: orders.length,
      sales: totalSales,
      discount: totalDiscount,
    };
  }, [orders]);

  const handlePrint = (order) => {
    const entries = order.orderEntryDtoList || [];
    const subtotal = entries.reduce((sum, entry) => sum + (Number(entry.totalPrice) || 0), 0);
    const discount = Number(order.totalDiscount) || 0;
    printReceipt({
      order,
      entries,
      subtotal,
      discount,
      total: Math.max(0, subtotal - discount),
      payment: null,
    });
  };

  return (
    <div className="w-full space-y-6">
      <section className="relative overflow-hidden rounded-[2rem] border border-slate-200 bg-gradient-to-br from-slate-950 via-slate-900 to-blue-950 px-8 py-10 text-white shadow-sm lg:px-10">
        <div className="absolute inset-0 opacity-25">
          <div className="absolute -left-20 top-10 h-52 w-52 rounded-full bg-blue-500 blur-3xl" />
          <div className="absolute bottom-0 right-4 h-40 w-40 rounded-full bg-amber-400 blur-3xl" />
        </div>

        <div className="relative flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
          <div className="space-y-6">
            <div className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-4 py-2 text-xs font-semibold uppercase tracking-[0.2em] text-blue-100">
              Order Desk
            </div>

            <div className="max-w-2xl space-y-4">
              <h2 className="text-3xl font-black tracking-tight sm:text-4xl">
                Recent orders
              </h2>
              <p className="text-sm leading-6 text-slate-200 sm:text-base">
                Orders are placed and paid directly from the cart. Browse, search, and reprint receipts here.
              </p>
            </div>
          </div>

          
          <div className="grid w-full gap-4 sm:grid-cols-3 lg:w-auto lg:min-w-[480px]">
            <div className="flex flex-col justify-between rounded-2xl border border-white/10 bg-white/5 p-4 min-w-0">
              <p className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400 truncate">Orders</p>
              <p className="mt-2 text-xl font-black text-white sm:text-2xl break-words">{metrics.count}</p>
            </div>

            <div className="flex flex-col justify-between rounded-2xl border border-white/10 bg-white/5 p-4 min-w-0">
              <p className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400 truncate">Revenue</p>
              <p className="mt-2 text-xl font-black text-white sm:text-2xl break-all leading-tight" title={formatCurrency(metrics.sales)}>
                {formatCurrency(metrics.sales)}
              </p>
            </div>

            <div className="flex flex-col justify-between rounded-2xl border border-white/10 bg-white/5 p-4 min-w-0">
              <p className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400 truncate">Discount</p>
              <p className="mt-2 text-xl font-black text-white sm:text-2xl break-all leading-tight" title={formatCurrency(metrics.discount)}>
                {formatCurrency(metrics.discount)}
              </p>
            </div>
          </div>
        </div>
      </section>

      <section className="rounded-[1.5rem] border border-slate-200 bg-white p-6 shadow-sm">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-600">Order History</p>
            <h3 className="mt-2 text-2xl font-bold text-slate-900">Recent orders</h3>
          </div>
          <div className="flex w-full gap-2 sm:w-auto">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search by order id, payment mode, coupon..."
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-blue-500 focus:ring-2 focus:ring-blue-100 sm:max-w-sm"
            />
            <button
              type="button"
              onClick={fetchOrders}
              title="Refresh orders"
              className="shrink-0 rounded-xl border-2 border-slate-200 bg-white px-4 py-3 text-sm font-bold text-slate-700 transition hover:border-blue-300 hover:text-blue-700"
            >
              ↻
            </button>
          </div>
        </div>

        {error && (
          <div className="mt-4 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm font-medium text-rose-600">
            {error}
          </div>
        )}

        <div className="mt-6 overflow-hidden rounded-2xl border border-slate-200">
          {renderOrdersTable()}
        </div>
      </section>
    </div>
  );

  function renderOrdersTable() {
    if (loading) {
      return <div className="p-8 text-center text-sm text-slate-500">Loading orders...</div>;
    }

    if (filteredOrders.length === 0) {
      return (
        <div className="p-8 text-center text-sm text-slate-500">
          {searchTerm ? `No orders found for "${searchTerm}".` : "No orders available yet."}
        </div>
      );
    }

    return (
      <div className="overflow-x-auto">
        <table className="min-w-full text-left text-sm">
          <thead className="bg-slate-950 text-white">
            <tr>
              <th className="px-4 py-3 font-semibold uppercase tracking-[0.08em]">Order</th>
              <th className="px-4 py-3 font-semibold uppercase tracking-[0.08em]">Payment</th>
              <th className="px-4 py-3 font-semibold uppercase tracking-[0.08em]">Date</th>
              <th className="px-4 py-3 font-semibold uppercase tracking-[0.08em]">Items</th>
              <th className="px-4 py-3 font-semibold uppercase tracking-[0.08em]">Total</th>
              <th className="px-4 py-3 text-right font-semibold uppercase tracking-[0.08em]">Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredOrders.map((order) => {
              const key = order.orderId || order.identifier;
              const orderEntries = order.orderEntryDtoList || [];
              const itemCount = orderEntries.length;
              const orderSubtotal = orderEntries.reduce((sum, e) => sum + (Number(e.totalPrice) || 0), 0) || Number(order.totalPrice) || 0;
              const orderTotal = Math.max(0, orderSubtotal - (Number(order.totalDiscount) || 0));

              return (
                <tr key={key} className="border-t border-slate-200 transition hover:bg-slate-50">
                  <td className="px-4 py-4">
                    <div className="font-semibold text-slate-900">{order.orderId || "-"}</div>
                    <div className="text-xs text-slate-500">{order.identifier || "Walk-in"}</div>
                  </td>
                  <td className="px-4 py-4 text-slate-700">{order.paymentMode || "-"}</td>
                  <td className="px-4 py-4 text-slate-700">{formatDateTime(order.orderDate)}</td>
                  <td className="px-4 py-4 text-slate-700">{itemCount}</td>
                  <td className="px-4 py-4 font-semibold text-blue-700">
                    {formatCurrency(orderTotal)}
                  </td>
                  <td className="px-4 py-4 text-right">
                    <button
                      type="button"
                      onClick={() => handlePrint(order)}
                      title="Print receipt"
                      className="rounded-lg p-2 text-slate-400 transition hover:bg-blue-50 hover:text-blue-600"
                    >
                      🖨
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    );
  }
}