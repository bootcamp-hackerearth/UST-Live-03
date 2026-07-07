"use client";

import { useEffect, useState, useRef } from "react";
import axios from "axios";

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(false);

  const printRef = useRef(null);

  const getToken = () => localStorage.getItem("token");

  useEffect(() => {
    loadOrders();
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/product/list`,
        { page: 0, sizePerPage: 200, sortDirection: "ASC", sortField: "identifier" },
        { headers: { Authorization: `Bearer ${getToken()}` } }
      );
      setProducts(res.data.dtoList || res.data.content || res.data || []);
    } catch (error) {
      console.error(error);
    }
  };

  const loadOrders = async () => {
    try {
      setLoading(true);
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/order/list`,
        {
          page: 0,
          sizePerPage: 100,
          sortDirection: "DESC",
          sortField: "id",
        },
        {
          headers: {
            Authorization: `Bearer ${getToken()}`,
          },
        }
      );
      setOrders(res.data.dtoList || []);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const viewOrder = async (identifier) => {
    try {
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/order/get`,
        identifier,
        {
          headers: {
            Authorization: `Bearer ${getToken()}`,
            "Content-Type": "text/plain",
          },
        }
      );
      setSelectedOrder(res.data);
    } catch (error) {
      console.error(error);
    }
  };

  const handlePrintReceipt = () => {
    const printContent = printRef.current;
    if (!printContent) return;

    const originalContent = document.body.innerHTML;
    document.body.innerHTML = printContent.innerHTML;
    globalThis.print();
    document.body.innerHTML = originalContent;
    globalThis.location.reload();
  };

  const formatOrderDate = (dateString) => {
    if (!dateString) return "—";
    try {
      const date = new Date(dateString);
      if (Number.isNaN(date.getTime())) return dateString;

      return date.toLocaleString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
        hour12: true,
      });
    } catch (e) {
      console.error("Error formatting date:", e);
      return dateString;
    }
  };

  const filteredOrders = orders.filter(
    (order) =>
      order.identifier?.toLowerCase().includes(search.toLowerCase()) ||
      order.customerName?.toLowerCase().includes(search.toLowerCase()) ||
      String(order.customerPhone || "").includes(search)
  );

  const getOrderStatusStyle = (status) => {
    switch (status?.toLowerCase()) {
      case "completed":
      case "success":
      case "paid":
        return "bg-emerald-50 text-emerald-700 border-emerald-200";
      case "pending":
      case "processing":
        return "bg-amber-50 text-amber-700 border-amber-200";
      case "failed":
      case "voided":
        return "bg-rose-50 text-rose-700 border-rose-200";
      default:
        return "bg-neutral-50 text-neutral-700 border-neutral-200";
    }
  };

  let orderTableBody;
  if (loading) {
    orderTableBody = (
      <tr>
        <td colSpan={5} className="text-center py-20 text-neutral-400 font-medium">
          <div className="flex flex-col items-center justify-center gap-3">
            <div className="w-6 h-6 border-2 border-neutral-300 border-t-neutral-900 rounded-full animate-spin"></div>
            <span className="text-xs">Querying database registry stream...</span>
          </div>
        </td>
      </tr>
    );
  } else if (filteredOrders.length > 0) {
    orderTableBody = filteredOrders.map((order) => {
      const isSelected = selectedOrder?.identifier === order.identifier;
      return (
        <tr
          key={order.identifier}
          className={`hover:bg-neutral-50/60 transition-colors ${isSelected ? "bg-neutral-50 font-medium" : ""}`}
        >
          <td className="p-4 pl-6 font-mono text-xs font-semibold text-neutral-900">
            {order.identifier}
          </td>
          <td className="p-4 text-neutral-800 capitalize">
            {order.customerName || <span className="text-neutral-400">Walk-in Guest</span>}
          </td>
          <td className="p-4 font-mono text-xs text-neutral-500">
            {order.customerPhone || "—"}
          </td>
          <td className="p-4 text-right font-mono font-semibold text-neutral-900">
            ₹{Number(order.totalPrice || 0).toFixed(2)}
          </td>
          <td className="p-4 pr-6 text-center">
            <button
              type="button"
              onClick={() => viewOrder(order.identifier)}
              className={`px-3.5 py-1.5 rounded-lg text-xs font-medium border transition-all ${isSelected
                  ? "bg-neutral-900 text-white border-neutral-900"
                  : "bg-white text-neutral-700 border-neutral-200 hover:border-neutral-900 hover:text-neutral-900"
                }`}
            >
              Inspect
            </button>
          </td>
        </tr>
      );
    });
  } else {
    orderTableBody = (
      <tr>
        <td colSpan={5} className="text-center py-16 text-neutral-400">
          <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-8 h-8 mb-2 text-neutral-300 mx-auto">
            <path strokeLinecap="round" strokeLinejoin="round" d="M2.25 13.5h3.86a2.25 2.25 0 0 1 2.008 1.24l.885 1.77a2.25 2.25 0 0 0 2.007 1.24h1.98a2.25 2.25 0 0 0 2.007-1.24l.885-1.77a2.25 2.25 0 0 1 2.007-1.24h3.86m-18 0h18a2.25 2.25 0 0 1 2.25 2.25v4.25a2.25 2.25 0 0 1-2.25 2.25H2.25A2.25 2.25 0 0 1 0 20v-4.25A2.25 2.25 0 0 1 2.25 13.5Z" />
          </svg>
          <p className="text-xs font-medium text-neutral-500">No Orders Indexed</p>
          <p className="text-[11px] text-neutral-400 mt-0.5">Adjust filter query strings or ingest new checkout payloads.</p>
        </td>
      </tr>
    );
  }

  return (
    <div className="w-full min-h-screen bg-[#F8F9FA] text-neutral-900 antialiased font-sans">
      <header className="bg-white border-b border-neutral-200/80 px-8 py-4 flex items-center justify-between sticky top-0 z-40">
        <div className="flex items-center gap-4">
          <div className="bg-neutral-900 text-white w-10 h-10 rounded-xl flex items-center justify-center font-bold text-lg shadow-sm">
            O
          </div>
          <div>
            <h1 className="text-lg font-semibold tracking-tight text-neutral-900">Order Management</h1>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={loadOrders}
            className="bg-neutral-900 hover:bg-neutral-800 text-white font-medium px-4 py-2.5 rounded-xl transition-all active:scale-[0.98] flex items-center gap-2 text-sm shadow-sm"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className={`w-4 h-4 ${loading ? "animate-spin" : ""}`}>
              <path strokeLinecap="round" strokeLinejoin="round" d="M16.023 9.348h4.992v-.001M2.985 19.644v-4.992m0 0h4.992m-4.993 0 3.181 3.183a8.25 8.25 0 0 0 13.803-3.7M4.031 9.865a8.25 8.25 0 0 1 13.803-3.7l3.181 3.182m0-4.991v4.99" />
            </svg>
            Refresh Sync
          </button>
        </div>
      </header>

      <main className="max-w-[1500px] mx-auto p-6 grid grid-cols-1 lg:grid-cols-12 gap-8">
        <section className={`${selectedOrder ? "lg:col-span-7" : "lg:col-span-12"} space-y-6 transition-all duration-300`}>
          <div className="bg-white rounded-2xl border border-neutral-200/60 p-6 shadow-[0_2px_8px_-3px_rgba(0,0,0,0.05)]">
            <div className="flex items-center gap-2 mb-4">
              <span className="w-1.5 h-1.5 rounded-full bg-blue-500"></span>
              <h2 className="text-xs font-bold uppercase tracking-wider text-neutral-400">Filter Archives</h2>
            </div>

            <div className="relative">
              <input
                type="text"
                placeholder="Search orders by invoice token reference number or profile parameters..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="w-full h-11 bg-neutral-50 focus:bg-white border border-neutral-200 focus:border-neutral-900 rounded-xl px-4 text-sm transition-all focus:ring-2 focus:ring-neutral-900/5 outline-none"
              />
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-neutral-200/60 shadow-[0_4px_16px_-4px_rgba(0,0,0,0.04)] overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-neutral-50/70 border-b border-neutral-200/60 text-[11px] font-bold uppercase tracking-wider text-neutral-400">
                    <th className="p-4 pl-6">Order ID</th>
                    <th className="p-4">Customer Details</th>
                    <th className="p-4">Phone Matrix</th>
                    <th className="p-4 text-right">Net Payable</th>
                    <th className="p-4 pr-6 text-center">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-neutral-100 text-sm">
                  {orderTableBody}
                </tbody>
              </table>
            </div>
          </div>
        </section>

        {selectedOrder && (
          <section className="lg:col-span-5 bg-white rounded-2xl border border-neutral-200/60 shadow-[0_4px_16px_-4px_rgba(0,0,0,0.04)] overflow-hidden flex flex-col lg:sticky lg:top-24 max-h-[calc(100vh-120px)] animate-in slide-in-from-right-4 duration-200">
            <div ref={printRef} className="bg-white text-neutral-900 flex flex-col overflow-y-auto flex-1">
              <div className="px-6 py-4 border-b border-neutral-100 flex items-center justify-between bg-neutral-50/50 [body_&]:print:bg-transparent">
                <div>
                  <h2 className="text-sm font-semibold text-neutral-900 [body_&]:print:text-base">Receipt Manifest</h2>
                  <p className="text-xs text-neutral-400 font-mono mt-0.5">
                    ID: {selectedOrder.identifier}
                  </p>
                </div>
                <button
                  onClick={() => setSelectedOrder(null)}
                  className="text-neutral-400 hover:text-neutral-600 transition-colors p-1 [body_&]:print:hidden"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-4 h-4">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M6 18 18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              <div className="p-5 border-b border-neutral-100 bg-neutral-50/30 grid grid-cols-2 gap-y-3 gap-x-4 text-xs [body_&]:print:bg-transparent">
                <div>
                  <span className="block text-[10px] font-bold text-neutral-400 uppercase tracking-tight">Purchaser Profile</span>
                  <span className="font-semibold text-neutral-800 capitalize block mt-0.5">{selectedOrder.customerName || "Walk-in Guest"}</span>
                </div>
                <div>
                  <span className="block text-[10px] font-bold text-neutral-400 uppercase tracking-tight">Pipeline Status</span>
                  <span className={`inline-block text-[10px] font-bold uppercase tracking-wide border px-2 py-0.5 rounded-md mt-0.5 ${getOrderStatusStyle(selectedOrder.status)}`}>
                    {selectedOrder.status || "COMPLETED"}
                  </span>
                </div>
                <div>
                  <span className="block text-[10px] font-bold text-neutral-400 uppercase tracking-tight">Phone Context Handle</span>
                  <span className="font-mono text-neutral-700 font-medium block mt-0.5">{selectedOrder.customerPhone || "—"}</span>
                </div>
                <div>
                  <span className="block text-[10px] font-bold text-neutral-400 uppercase tracking-tight">Placement Timestamp</span>
                  <span className="font-medium text-neutral-700 block mt-0.5">
                    {formatOrderDate(selectedOrder.createdDate || selectedOrder.createdAt || selectedOrder.orderDate)}
                  </span>
                </div>
              </div>

              <div className="divide-y divide-neutral-100 p-4 flex-1">
                {selectedOrder.orderEntryDtoList?.map((item) => {
                  const catalogProduct = products.find(p => String(p.identifier) === String(item.product));
                  const productNameDisplay = catalogProduct?.name || `Product (${item.product})`;
                  const unitPriceNum = Number(item.unitPrice || 0);
                  const discountNum = Number(item.discount || 0);
                  const itemMrp = unitPriceNum + (discountNum / Number(item.quantity || 1));
                  const hasDiscount = discountNum > 0;

                  return (
                    <div key={item.identifier} className="py-3 flex items-start gap-4 justify-between first:pt-1">
                      <div className="space-y-0.5 min-w-0 flex-1">
                        <p className="font-semibold text-sm text-neutral-900 truncate capitalize">
                          {productNameDisplay}
                        </p>
                        <div className="flex flex-wrap items-center gap-x-3 text-xs text-neutral-400">
                          <span>Qty: <span className="text-neutral-700 font-semibold">{item.quantity}</span></span>
                          <span>Rate: ₹{unitPriceNum.toFixed(2)}</span>
                          {hasDiscount && (
                            <span className="text-rose-600 font-medium [body_&]:print:text-neutral-600">Saved: ₹{discountNum.toFixed(2)}</span>
                          )}
                        </div>
                      </div>
                      <div className="text-right pl-2">
                        <p className="font-semibold text-sm text-neutral-900 font-mono">
                          ₹{Number(item.totalPrice || 0).toFixed(2)}
                        </p>
                        {hasDiscount && (
                          <span className="text-[10px] text-neutral-400 line-through font-mono">
                            ₹{(itemMrp * Number(item.quantity)).toFixed(2)}
                          </span>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>

              <div className="bg-neutral-50/80 p-5 border-t border-neutral-100 space-y-2.5 [body_&]:print:bg-transparent">
                <div className="flex justify-between items-center text-xs text-neutral-500">
                  <span>Gross Catalog Value</span>
                  <span className="font-mono font-medium text-neutral-700">₹{Number(selectedOrder.totalOriginalPrice || 0).toFixed(2)}</span>
                </div>

                <div className="flex justify-between items-center text-xs text-neutral-500">
                  <span>Dynamic Adjustments Discount</span>
                  <span className="font-mono font-semibold text-rose-600">-₹{Number(selectedOrder.discount || 0).toFixed(2)}</span>
                </div>

                <div className="border-t border-dashed border-neutral-200 pt-3 flex justify-between items-center">
                  <span className="text-xs font-bold uppercase tracking-wider text-neutral-500">Grand Ledger Total</span>
                  <span className="text-xl font-bold text-neutral-900 font-mono">
                    ₹{Number(selectedOrder.totalPrice || 0).toFixed(2)}
                  </span>
                </div>
              </div>
            </div>

            <div className="p-4 border-t border-neutral-100 bg-neutral-50/50 [body_&]:print:hidden">
              <button
                type="button"
                onClick={handlePrintReceipt}
                className="w-full h-11 bg-neutral-900 hover:bg-neutral-800 text-white font-semibold rounded-xl text-sm transition-all active:scale-[0.98] flex items-center justify-center gap-2 shadow-md"
              >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-4 h-4">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6.72 13.82l-.152-.048a3.375 3.375 0 01-2.24-2.63l-.4-2.4c-.094-.565.333-1.07.905-1.07h14.334c.572 0 1 .505.905 1.07l-.4 2.4a3.375 3.375 0 01-2.24 2.63l-.152.048M16.5 22.5H7.5m9-6v5.25a.75.75 0 01-.75.75h-7.5a.75.75 0 01-.75-.75V16.5m9-6H7.5m9-4.5V3.75a.75.75 0 00-.75-.75h-7.5a.75.75 0 00-.75.75V6" />
                </svg>
                Print Receipt
              </button>
            </div>
          </section>
        )}
      </main>
    </div>
  );
}