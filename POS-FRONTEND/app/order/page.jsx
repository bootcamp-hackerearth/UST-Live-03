"use client";

import { FetchList } from "@/apicalls/fetch/FetchList";
import { useEffect, useState, useMemo } from "react";
import {
  History, Search, X,
  ArrowLeft, Loader2, Phone
} from "lucide-react";
import Link from "next/link";

export default function OrderHistoryPage() {
  const [allGlobalOrders, setAllGlobalOrders] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [selectedOrderEntries, setSelectedOrderEntries] = useState([]);
  const [orderSearchTerm, setOrderSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);
  const [showHistoryModal, setShowHistoryModal] = useState(false);

  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "/api";
  const getListContent = (res) => Array.isArray(res) ? res : res?.content || [];

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        const [ordersRes, customersRes, productsRes] = await Promise.all([
          FetchList(`${baseUrl}/order/list`, 0, 2000),
          FetchList(`${baseUrl}/customer/list`, 0, 1000),
          FetchList(`${baseUrl}/product/list`, 0, 1000)
        ]);
        setAllGlobalOrders(getListContent(ordersRes));
        setCustomers(getListContent(customersRes));
        setProducts(getListContent(productsRes));
      } catch (err) {
        console.error("Error loading order ledger history layers:", err);
      } finally {
        setLoading(false);
      }
    };
    loadData();
  }, []);

  const customerMap = useMemo(() => new Map(customers.map(c => [c.identifier, c])), [customers]);
  const productMap = useMemo(() => new Map(products.map(p => [p.identifier || p.id, p])), [products]);

  const filteredOrdersList = useMemo(() => {
    return allGlobalOrders.filter(o => {
      const normalizedSearch = orderSearchTerm.toLowerCase();
      const orderId = o.identifier || "";
      const custId = o.customerId || "";
      const paymentMode = o.paymentType || "";
      return orderId.toLowerCase().includes(normalizedSearch) ||
        custId.toLowerCase().includes(normalizedSearch) ||
        paymentMode.toLowerCase().includes(normalizedSearch);
    });
  }, [allGlobalOrders, orderSearchTerm]);

  const handleOpenOrderDetails = async (order) => {
    setSelectedOrder(order);
    setShowHistoryModal(true);
    try {
      const entriesRes = await FetchList(`${baseUrl}/orderentry/list`, 0, 5000);
      const filtered = getListContent(entriesRes).filter(e => e.orderId === order.identifier);
      setSelectedOrderEntries(filtered);
    } catch (err) {
      console.error("Error fetching order subline entries:", err);
    }
  };

  const renderPaymentBadge = (type) => {
    const base = "px-2.5 py-1 text-[10px] font-bold rounded-full tracking-wide inline-flex items-center gap-1 uppercase";
    let badge;
    switch (type?.toUpperCase()) {
      case "CASH":
        badge = <span className={`${base} bg-emerald-50 text-emerald-700 border border-emerald-200`}>● Cash</span>;
        break;
      case "CARD":
        badge = <span className={`${base} bg-blue-50 text-blue-700 border border-blue-200`}>■ Card</span>;
        break;
      case "UPI":
        badge = <span className={`${base} bg-purple-50 text-purple-700 border border-purple-200`}>▲ UPI</span>;
        break;
      default:
        badge = <span className={`${base} bg-orange-50 text-orange-700 border border-orange-200`}>◆ Online</span>;
    }
    return badge;
  };

  return (
    <div className="min-h-screen bg-zinc-50 text-slate-800 antialiased p-6 max-w-(screen-2xl) mx-auto">

      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6 bg-white p-6 rounded-xl border border-zinc-200 shadow-xs">
        <div className="flex items-center gap-3">
          <Link href="/" className="p-2 hover:bg-zinc-100 rounded-lg text-zinc-500 transition">
            <ArrowLeft size={16} />
          </Link>
          <div>
            <h1 className="text-lg font-bold text-zinc-900 tracking-tight flex items-center gap-2">
              <History className="text-zinc-500" size={20} /> Order Ledger History
            </h1>
            <p className="text-xs text-zinc-400">Review generated order data sequences below.</p>
          </div>
        </div>

        <div className="relative w-full sm:w-72">
          <Search className="absolute left-3 top-2.5 h-4 w-4 text-zinc-400" />
          <input
            type="text"
            placeholder="Search ID, Customer, Payment Mode..."
            value={orderSearchTerm}
            onChange={(e) => setOrderSearchTerm(e.target.value)}
            className="w-full pl-9 pr-4 py-2 border border-zinc-200 rounded-xl text-xs font-medium focus:outline-none focus:ring-1 focus:ring-zinc-400 bg-white"
          />
        </div>
      </div>

      <div className="bg-white rounded-xl border border-zinc-200 shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse text-xs font-medium text-zinc-600">
            <thead>
              <tr className="bg-zinc-50 border-b border-zinc-200 text-zinc-400 font-bold uppercase tracking-wider text-[10px]">
                <th className="py-3.5 px-5">Order ID</th>
                <th className="py-3.5 px-4">Customer Reference ID</th>
                <th className="py-3.5 px-4">Customer Profile</th>
                <th className="py-3.5 px-4 text-center">Order Date</th>
                <th className="py-3.5 px-4 text-center">Payment Type</th>
                <th className="py-3.5 px-4 text-right">Discount</th>
                <th className="py-3.5 px-4 text-right">Grand Total</th>
                <th className="py-3.5 px-5 text-center">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-100">
              {(() => {
                if (loading) {
                  return [
                    <tr key="loading">
                      <td colSpan={8} className="text-center py-16 text-zinc-400">
                        <Loader2 className="mx-auto animate-spin mb-2 text-zinc-500" size={20} />
                        Loading system database ledger lines...
                      </td>
                    </tr>
                  ];
                }
                if (filteredOrdersList.length === 0) {
                  return [
                    <tr key="empty">
                      <td colSpan={8} className="text-center py-12 text-zinc-400">No generated database records found.</td>
                    </tr>
                  ];
                }
                return filteredOrdersList.map((order) => {
                  const profile = customerMap.get(order.customerId);
                  return (
                    <tr key={order.identifier} className="hover:bg-zinc-50/60 transition">
                      <td className="py-4 px-5 font-mono font-bold text-zinc-900">{order.identifier}</td>
                      <td className="py-4 px-4 font-mono text-zinc-500">{order.customerId || "Walk-In"}</td>
                      <td className="py-4 px-4 font-bold text-zinc-900">
                        {profile?.name || "Walk-In Customer"}
                        {profile?.phoneNo && <span className="block text-[10px] text-zinc-400 font-normal mt-0.5"><Phone size={14} />{profile.phoneNo}</span>}
                      </td>
                      <td className="py-4 px-4 text-center font-mono text-zinc-500">
                        {order.orderDate
                          ? new Date(order.orderDate).toLocaleString()
                          : "-"}
                      </td>
                      <td className="py-4 px-4 text-center">{renderPaymentBadge(order.paymentType)}</td>
                      <td className="py-4 px-4 text-right font-mono text-zinc-500">₹{order.discount || 0}</td>
                      <td className="py-4 px-4 text-right font-mono font-black text-zinc-900 text-sm">₹{order.totalPrice || 0}</td>
                      <td className="py-4 px-5 text-center">
                        <button
                          onClick={() => handleOpenOrderDetails(order)}
                          className="px-3 py-1.5 bg-zinc-900 hover:bg-zinc-800 text-white rounded-lg text-[11px] font-bold flex items-center gap-1 mx-auto transition cursor-pointer shadow-xs"
                        >
                          View Details
                        </button>
                      </td>
                    </tr>
                  );
                });
              })()}
            </tbody>
          </table>
        </div>
      </div>

      {showHistoryModal && selectedOrder && (
        <div className="fixed inset-0 bg-zinc-900/60 backdrop-blur-xs flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-3xl w-full border border-zinc-100 overflow-hidden flex flex-col max-h-[85vh] scale-up">
            <div className="px-6 py-4 border-b border-zinc-100 flex justify-between items-center bg-zinc-50/80 sticky top-0 z-10">
              <div>
                <h3 className="text-sm font-black uppercase tracking-wider text-zinc-900">Transaction Details Terminal</h3>
                <p className="text-[11px] font-mono font-medium text-zinc-400 mt-0.5">Document Node: {selectedOrder.identifier}</p>
              </div>
              <button onClick={() => setShowHistoryModal(false)} className="p-1.5 text-zinc-400 hover:text-zinc-900 rounded-xl hover:bg-zinc-100 transition cursor-pointer">
                <X size={18} />
              </button>
            </div>

            <div className="p-6 space-y-6 overflow-y-auto">
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <div className="bg-zinc-50 border border-zinc-200 rounded-xl p-3 text-left">
                  <span className="text-[10px] text-zinc-400 font-bold uppercase tracking-wider block">Invoice ID</span>
                  <span className="text-xs font-mono font-bold text-zinc-900 block mt-1 truncate">{selectedOrder.identifier}</span>
                </div>
                <div className="bg-zinc-50 border border-zinc-200 rounded-xl p-3 text-left">
                  <span className="text-[10px] text-zinc-400 font-bold uppercase tracking-wider block">Customer Reference</span>
                  <span className="text-xs font-mono font-bold text-zinc-900 block mt-1 truncate">{selectedOrder.customerId || "Walk-In"}</span>
                </div>
                <div className="bg-zinc-50 border border-zinc-200 rounded-xl p-3 text-left">
                  <span className="text-[10px] text-zinc-400 font-bold uppercase tracking-wider block">Customer Name</span>
                  <span className="text-xs font-bold text-zinc-900 block mt-1 truncate">{customerMap.get(selectedOrder.customerId)?.name || "Walk-In Client"}</span>
                </div>
                <div className="bg-zinc-50 border border-zinc-200 rounded-xl p-3 text-left">
                  <span className="text-[10px] text-zinc-400 font-bold uppercase tracking-wider block">Customer Phone</span>
                  <span className="text-xs font-mono font-bold text-zinc-900 block mt-1 truncate">{customerMap.get(selectedOrder.customerId)?.phoneNo || "N/A"}</span>
                </div>
              </div>

              <div>
                <h4 className="text-xs font-black tracking-wider uppercase text-zinc-400 mb-3">Itemized Checkout Lines Log</h4>
                <div className="border border-zinc-200 rounded-xl overflow-hidden shadow-2xs">
                  <table className="w-full text-left border-collapse text-xs font-medium text-zinc-600">
                    <thead>
                      <tr className="bg-zinc-50 border-b border-zinc-200 text-zinc-400 font-bold uppercase tracking-wider text-[10px]">
                        <th className="py-2.5 px-4">Product Specification</th>
                        <th className="py-2.5 px-4 text-center">Quantity</th>
                        <th className="py-2.5 px-4 text-right">Standard Rate</th>
                        <th className="py-2.5 px-4 text-right">Markdown</th>
                        <th className="py-2.5 px-4 text-right">Net Price</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-zinc-100 bg-white">
                      {selectedOrderEntries.map((entry) => {
                        const resolvedProduct = productMap.get(entry.product);
                        return (
                          <tr key={entry.product} className="hover:bg-zinc-50/50 transition">
                            <td className="py-3 px-4">
                              <span className="font-bold text-zinc-900 block">{resolvedProduct?.name || "Inventory Master Item"}</span>
                              <span className="text-[10px] font-mono text-zinc-400 block mt-0.5">ID: {entry.product}</span>
                            </td>
                            <td className="py-3 px-4 text-center font-bold text-zinc-800 font-mono">{entry.quantity}</td>
                            <td className="py-3 px-4 text-right font-mono">₹{(entry.unitPrice || 0).toFixed(2)}</td>
                            <td className="py-3 px-4 text-right font-mono text-zinc-500">-₹{(entry.discount || 0).toFixed(2)}</td>
                            <td className="py-3 px-4 text-right font-mono font-bold text-zinc-900">₹{(entry.totalPrice || 0).toFixed(2)}</td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>

              <div className="flex justify-end">
                <div className="w-full sm:w-80 space-y-2 text-xs font-medium border border-zinc-200 rounded-xl p-4 bg-zinc-50/50">
                  <div className="flex justify-between text-zinc-500">
                    <span>Valuation Subtotal</span>
                    <span className="font-mono text-zinc-800">₹{(selectedOrder.totalOriginalPrice || 0).toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between text-zinc-500">
                    <span>Applied Discounts</span>
                    <span className="font-mono text-zinc-600">-₹{(selectedOrder.discount || 0).toFixed(2)}</span>
                  </div>
                  <div className="border-t border-zinc-200 pt-2 flex justify-between items-baseline font-black text-zinc-900 text-sm">
                    <span>Net Total Balance</span>
                    <span className="font-mono text-base text-zinc-900">₹{(selectedOrder.totalPrice || 0).toFixed(2)}</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="px-6 py-4 bg-zinc-50 border-t border-zinc-100 flex justify-end text-xs font-bold">
              <button onClick={() => setShowHistoryModal(false)} className="px-4 py-2 bg-white border border-zinc-200 text-zinc-600 rounded-xl hover:bg-zinc-100 transition cursor-pointer">Close View</button>
            </div>
          </div>
        </div>
      )}

      <style>{`
        @keyframes scaleUp {
          from { transform: scale(0.98); opacity: 0; }
          to { transform: scale(1); opacity: 1; }
        }
        .scale-up { animation: scaleUp 0.15s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
      `}</style>
    </div>
  );
}