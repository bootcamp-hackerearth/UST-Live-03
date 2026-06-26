"use client";

import { useEffect, useState, useRef, useCallback } from "react";
import Layout from "@/components/Layout";
import api from "@/app/services/api";
import {
  Search,
  ShoppingBag,
  MoreVertical,
  Receipt,
  Trash2,
  X,
  Eye,
  Printer,
  User,
  Clock,
} from "lucide-react";
import PropTypes from "prop-types";


const currency = (value) =>
  `₹${Number(value || 0).toLocaleString("en-IN", {
    minimumFractionDigits: 2,
  })}`;

const formatDate = (value) => {
  if (!value) return "—";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return value;
  return d.toLocaleDateString("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
};

const formatTime = (value) => {
  if (!value) return "";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return "";
  return d.toLocaleTimeString("en-IN", {
    hour: "2-digit",
    minute: "2-digit",
  });
};

const resolveOrderTimestamp = (order) => {
  const candidates = [
    order?.createdDate,
    order?.createdAt,
    order?.orderDate,
    order?.dateCreated,
    order?.creationDate,
    order?.createdOn,
    order?.created,
    order?.timestamp,
    order?.date,
  ];
  return candidates.find((v) => v != null) || null;
};

const getList = (res) => {
  const data = res?.data;
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.dtoList)) return data.dtoList;
  return [];
};


export default function OrderListPage() {
  const [orders, setOrders] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  const [openMenuId, setOpenMenuId] = useState(null);
  const menuRef = useRef(null);

  const [invoiceOrder, setInvoiceOrder] = useState(null);
  const [invoiceLoading, setInvoiceLoading] = useState(false);
  const [viewOrder, setViewOrder] = useState(null);

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      const [orderRes, customerRes] = await Promise.all([
        api.post("/order/list", { page: 0, sizePerPage: 500 }),
        api.post("/customer/list", { page: 0, sizePerPage: 500 }),
      ]);
      setOrders(getList(orderRes));
      setCustomers(getList(customerRes));
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

  useEffect(() => {
    const onClick = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpenMenuId(null);
      }
    };
    document.addEventListener("mousedown", onClick);
    return () => document.removeEventListener("mousedown", onClick);
  }, []);

  const getCustomer = (customerId) =>
    customers.find((c) => c.identifier === customerId) || null;

  const getCustomerName = (customerId) =>
    getCustomer(customerId)?.name || customerId || "Walk-in customer";


  const handleDelete = async (identifier) => {
    setOpenMenuId(null);
    const confirmDelete = globalThis.confirm(
      "Delete this order? This cannot be undone."
    );
    if (!confirmDelete) return;

    try {
      await api.delete(`/order/delete?identifier=${identifier}`);
      loadData();
    } catch (error) {
      console.error(error);
    }
  };

  const handleView = async (order) => {
    setOpenMenuId(null);

    try {
      const res = await api.get(`/order/get?identifier=${order.identifier}`);
      setViewOrder(res.data || order);
    } catch (error) {
      console.error(error);
    }
  };

  const handleOpenInvoice = async (order) => {
    setOpenMenuId(null);
    setInvoiceLoading(true);
    setInvoiceOrder({ ...order, entryList: order.entryList || [] });

    try {
      const res = await api.get(`/order/get?identifier=${order.identifier}`);
      const fullOrder = res?.data || order;

      console.log("Raw order from /order/get:", fullOrder);

      const entries = fullOrder.entryList || [];
      const productIds = [...new Set(entries.map((e) => e.productIdentifier))];

      const productResults = await Promise.all(
        productIds.map((id) =>
          api
            .get(`/product/get?identifier=${id}`)
            .then((r) => r.data)
            .catch(() => null)
        )
      );

      const productMap = {};
      productIds.forEach((id, idx) => {
        productMap[id] = productResults[idx];
      });

      const hydratedEntries = entries.map((entry) => ({
        ...entry,
        productName:
          productMap[entry.productIdentifier]?.name || entry.productIdentifier,
      }));

      let customerDetail = null;
      if (fullOrder.customerIdentifier) {
        try {
          const cRes = await api.get(
            `/customer/get?identifier=${fullOrder.customerIdentifier}`
          );
          customerDetail = cRes.data;
        } catch (e) {
          console.error(e);
        }
      }

      setInvoiceOrder({
        ...fullOrder,
        entryList: hydratedEntries,
        customerDetail,
      });
    } catch (error) {
      console.error(error);
    } finally {
      setInvoiceLoading(false);
    }
  };


  const filteredOrders = orders.filter((order) => {
    const customerName = getCustomerName(order.customerIdentifier);
    const q = search.toLowerCase();
    return (
      order.identifier?.toLowerCase().includes(q) ||
      customerName.toLowerCase().includes(q)
    );
  });


  const renderTableBody = () => {
    if (loading) {
      return (
        <tr>
          <td colSpan={8} className="text-center p-10 text-slate-500">
            Loading orders...
          </td>
        </tr>
      );
    }

    if (filteredOrders.length === 0) {
      return (
        <tr>
          <td colSpan={8} className="text-center p-10 text-slate-500">
            No orders found
          </td>
        </tr>
      );
    }

    return filteredOrders.map((order, index) => (
      <tr
        key={order.identifier}
        className="border-b border-slate-100 hover:bg-slate-50 transition-colors"
      >
        <td className="p-4 text-slate-500">{index + 1}</td>

        <td className="p-4 font-medium text-[#14211E]">
          {order.identifier}
        </td>

        <td className="p-4">
          {getCustomerName(order.customerIdentifier)}
        </td>

        <td className="p-4 text-right font-mono text-slate-600">
          {currency(order.originalPrice)}
        </td>

        <td className="p-4 text-right font-mono text-red-500">
          {currency(order.discount)}
        </td>

        <td className="p-4 text-right font-mono font-semibold text-[#14211E]">
          {currency(order.totalPrice)}
        </td>

        <td className="p-4 text-center">
          <span className="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-emerald-50 border border-emerald-200 text-emerald-700">
            {order.paymentMethod}
          </span>
        </td>

        <td className="p-4">
          <div className="flex justify-center relative">
            <button
              onClick={() =>
                setOpenMenuId(
                  openMenuId === order.identifier ? null : order.identifier
                )
              }
              className="h-8 w-8 rounded-lg text-slate-400 hover:bg-slate-100 hover:text-slate-700 flex items-center justify-center transition-colors"
              aria-label="Order actions"
            >
              <MoreVertical size={17} className="text-slate-500" />
            </button>

            {openMenuId === order.identifier && (
              <div
                ref={menuRef}
                className="absolute right-0 top-9 z-20 w-44 bg-white rounded-xl border border-slate-200 shadow-lg py-1.5 text-left"
              >
                <button
                  onClick={() => handleView(order)}
                  className="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-slate-700 hover:bg-slate-50"
                >
                  <Eye size={15} className="text-slate-500" />
                  View Details
                </button>

                <button
                  onClick={() => handleOpenInvoice(order)}
                  className="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-slate-700 hover:bg-slate-50"
                >
                  <Receipt size={15} className="text-[#14211E]" />
                  Invoice
                </button>

                <div className="my-1 border-t border-slate-100" />

                <button
                  onClick={() => handleDelete(order.identifier)}
                  className="w-full flex items-center gap-2.5 px-3.5 py-2 text-sm text-red-600 hover:bg-red-50"
                >
                  <Trash2 size={15} />
                  Delete
                </button>
              </div>
            )}
          </div>
        </td>
      </tr>
    ));
  };


  return (
    <Layout>
      <div className="min-h-screen bg-[#F4F5F7]">
        <div className="max-w-[1440px] mx-auto px-6 py-6 flex flex-col gap-5">
        
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="h-10 w-10 rounded-xl bg-[#1F2430] flex items-center justify-center shrink-0">
                <ShoppingBag size={18} className="text-white" />
              </div>

              <div>
                <h1 className="text-[18px] font-semibold text-slate-800 leading-tight">
                  Orders
                </h1>
                <p className="text-xs text-slate-400">Completed sales history</p>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <div className="flex items-center gap-2 text-xs px-3 py-1.5 rounded-full border font-medium bg-emerald-50 border-emerald-200 text-emerald-700">
                <span className="h-1.5 w-1.5 rounded-full bg-emerald-500" />
                {orders.length} Orders
              </div>
            </div>
          </div>

          <div className="bg-white border border-slate-200 rounded-xl p-5">
            <div className="relative max-w-md">
              <Search
                size={16}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
              />

              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search by order id or customer..."
                className="w-full h-10 bg-[#F9FAFB] border border-slate-200 rounded-lg pl-10 pr-4 text-sm text-slate-700 focus:outline-none focus:border-slate-400"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
            <div className="bg-white border border-slate-200 rounded-xl p-4">
              <p className="text-[10px] font-semibold uppercase tracking-widest text-slate-400 mb-1">
                Total Orders
              </p>
              <p className="text-2xl font-semibold text-slate-800">
                {orders.length}
              </p>
            </div>

            <div className="bg-white border border-slate-200 rounded-xl p-4">
              <p className="text-[10px] font-semibold uppercase tracking-widest text-slate-400 mb-1">
                Visible Orders
              </p>
              <p className="text-2xl font-semibold text-slate-800">
                {filteredOrders.length}
              </p>
            </div>

            <div className="bg-[#1F2430] border border-[#1F2430] rounded-xl p-4">
              <p className="text-[10px] font-semibold uppercase tracking-widest text-slate-500 mb-1">
                Order History
              </p>
              <p className="text-2xl font-semibold text-white">Active</p>
            </div>
          </div>

          <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
            <div className="px-5 py-4 border-b border-slate-100 flex items-center gap-2">
              <Receipt size={14} className="text-slate-400" />
              <h3 className="text-[10px] font-semibold uppercase tracking-widest text-slate-400">
                Order history
              </h3>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="bg-[#F9FAFB] border-b border-slate-200">
                    <th className="py-3 px-4 text-left text-[10px] font-semibold uppercase tracking-widest text-slate-400">SL</th>
                    <th className="py-3 px-4 text-left text-[10px] font-semibold uppercase tracking-widest text-slate-400">Order ID</th>
                    <th className="py-3 px-4 text-left text-[10px] font-semibold uppercase tracking-widest text-slate-400">Customer</th>
                    <th className="py-3 px-4 text-right text-[10px] font-semibold uppercase tracking-widest text-slate-400">Original</th>
                    <th className="py-3 px-4 text-right text-[10px] font-semibold uppercase tracking-widest text-slate-400">Discount</th>
                    <th className="py-3 px-4 text-right text-[10px] font-semibold uppercase tracking-widest text-slate-400">Total</th>
                    <th className="py-3 px-4 text-right text-[10px] font-semibold uppercase tracking-widest text-slate-400">Payment</th>
                    <th className="py-3 px-4 text-right text-[10px] font-semibold uppercase tracking-widest text-slate-400">Actions</th>
                  </tr>
                </thead>

                <tbody>{renderTableBody()}</tbody>
              </table>
            </div>
          </div>
        </div>

        {invoiceOrder && (
          <InvoiceModal
            order={invoiceOrder}
            loading={invoiceLoading}
            onClose={() => setInvoiceOrder(null)}
          />
        )}
      </div>

      {viewOrder && (
        <OrderDetailsModal
          order={viewOrder}
          customers={customers}
          onClose={() => setViewOrder(null)}
        />
      )}
    </Layout>
  );
}


function OrderDetailsModal({ order, customers, onClose }) {
  const customer =
    customers.find((c) => c.identifier === order.customerIdentifier) || null;

  const timestamp = resolveOrderTimestamp(order);

  return (
    <div className="fixed inset-0 z-[100] bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="w-full max-w-2xl bg-white rounded-xl border border-slate-200 shadow-2xl overflow-hidden">
        <div className="flex items-center justify-between px-5 py-4 border-b border-slate-100">
          <div>
            <h2 className="text-[15px] font-semibold text-slate-800">
              Order Details
            </h2>
            <p className="text-xs text-slate-400">Audit Information</p>
          </div>

          <button
            onClick={onClose}
            className="h-8 w-8 rounded-lg hover:bg-slate-100 flex items-center justify-center"
          >
            <X size={15} />
          </button>
        </div>

        <div className="p-5 space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="bg-slate-50 border border-slate-200 rounded-xl p-4">
              <p className="text-[10px] uppercase tracking-widest text-slate-400 mb-2">
                Order ID
              </p>
              <p className="font-mono text-sm font-semibold">{order.identifier}</p>
            </div>

            <div className="bg-slate-50 border border-slate-200 rounded-xl p-4">
              <p className="text-[10px] uppercase tracking-widest text-slate-400 mb-2">
                Customer
              </p>
              <p className="text-sm font-medium">{customer?.name || "Walk-in"}</p>
            </div>
          </div>

          <div className="bg-white border border-slate-200 rounded-xl p-4">
            <div className="flex items-center gap-2 mb-4">
              <User size={15} />
              <span className="font-medium">Created By</span>
            </div>
            <p className="text-sm text-slate-700">
              {order.createdBy ||
                order.createdUser ||
                order.createdByName ||
                "Not Available"}
            </p>
          </div>

          <div className="bg-white border border-slate-200 rounded-xl p-4">
            <div className="flex items-center gap-2 mb-4">
              <Clock size={15} />
              <span className="font-medium">Created Time</span>
            </div>
            <p className="text-sm text-slate-700">{formatDate(timestamp)}</p>
            <p className="text-xs text-slate-400 mt-1">{formatTime(timestamp)}</p>
          </div>

          <div className="bg-[#1F2430] rounded-xl p-4">
            <p className="text-[10px] uppercase tracking-widest text-slate-400 mb-2">
              Order Value
            </p>
            <p className="text-2xl font-mono font-semibold text-white">
              {currency(order.totalPrice)}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}


function numberToWords(num) {
  const a = [
    "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
    "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
    "Seventeen", "Eighteen", "Nineteen",
  ];
  const b = [
    "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety",
  ];

  const inWords = (n) => {
    if (n < 20) return a[n];
    if (n < 100) return b[Math.floor(n / 10)] + (n % 10 ? " " + a[n % 10] : "");
    if (n < 1000)
      return a[Math.floor(n / 100)] + " Hundred" + (n % 100 ? " " + inWords(n % 100) : "");
    if (n < 100000)
      return inWords(Math.floor(n / 1000)) + " Thousand" + (n % 1000 ? " " + inWords(n % 1000) : "");
    if (n < 10000000)
      return inWords(Math.floor(n / 100000)) + " Lakh" + (n % 100000 ? " " + inWords(n % 100000) : "");
    return inWords(Math.floor(n / 10000000)) + " Crore" + (n % 10000000 ? " " + inWords(n % 10000000) : "");
  };

  const whole = Math.floor(Number(num) || 0);
  if (whole === 0) return "Zero Rupees";
  return `${inWords(whole)} Rupee${whole === 1 ? "" : "s"}`;
}


function InvoiceModal({ order, loading, onClose }) {
  const customer = order.customerDetail;
  const billing = customer?.billingAddress;

  const handlePrint = () => globalThis.print();

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4 print:bg-white print:p-0">
      <div className="bg-white w-full max-w-5xl max-h-[90vh] rounded-2xl shadow-2xl overflow-hidden flex flex-col print:max-h-none print:rounded-none print:shadow-none">
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 print:hidden">
          <h2 className="font-semibold text-[#14211E] text-lg">Sales Invoice</h2>

          <div className="flex items-center gap-2">
            <button
              onClick={handlePrint}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-[#14211E] text-white text-sm font-medium hover:bg-[#0e1714] transition-colors"
              title="Print invoice"
            >
              <Printer size={14} />
              Print
            </button>

            <button
              onClick={onClose}
              className="h-8 w-8 rounded-lg hover:bg-slate-100 flex items-center justify-center ml-1"
              aria-label="Close"
            >
              <X size={18} className="text-slate-500" />
            </button>
          </div>
        </div>

        <div className="overflow-y-auto px-6 py-6 bg-[#F9FAFB]">
          {loading ? (
            <div className="py-20 text-center text-slate-400 text-sm">
              Loading invoice details...
            </div>
          ) : (
            <>
              <div className="flex items-start justify-between mb-6">
                <div className="h-14 w-36 rounded-lg bg-[#14211E]/5 border border-dashed border-slate-300 flex items-center justify-center text-[10px] text-slate-400">
                  Your Logo
                </div>
                <div className="px-4 py-2 rounded-lg bg-[#1F2430]">
                  <span className="text-sm font-bold tracking-wide text-[#E8B95E]">
                    INVOICE
                  </span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-8 mb-7 text-sm">
                <div className="space-y-1.5">
                  <p>
                    <span className="font-semibold text-[#14211E]">Customer:</span>{" "}
                    {customer?.name || order.customerIdentifier || "Walk-in customer"}
                  </p>
                  <p>
                    <span className="font-semibold text-[#14211E]">Address:</span>{" "}
                    {billing
                      ? [billing.addressLine1, billing.addressLine2, billing.city, billing.state, billing.pinCode]
                          .filter(Boolean)
                          .join(", ") || "N/A"
                      : "N/A"}
                  </p>
                  <p>
                    <span className="font-semibold text-[#14211E]">Phone:</span>{" "}
                    {customer?.phoneNo || "N/A"}
                  </p>
                </div>

                <div className="space-y-1.5 text-right">
                  <p>
                    <span className="font-semibold text-[#14211E]">Invoice No:</span>{" "}
                    {order.identifier}
                  </p>
                  <p>
                    <span className="font-semibold text-[#14211E]">Date:</span>{" "}
                    {formatDate(resolveOrderTimestamp(order))}
                  </p>
                  <p>
                    <span className="font-semibold text-[#14211E]">Time:</span>{" "}
                    {formatTime(resolveOrderTimestamp(order))}
                  </p>
                </div>
              </div>

              <div className="rounded-xl border border-slate-200 overflow-hidden mb-6">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="bg-[#F9FAFB] border-b border-slate-200">
                      <th className="p-3 text-left font-medium w-10">SL</th>
                      <th className="p-3 text-left font-medium">Item</th>
                      <th className="p-3 text-right font-medium">Qty</th>
                      <th className="p-3 text-right font-medium">Unit Price</th>
                      <th className="p-3 text-right font-medium">Discount</th>
                      <th className="p-3 text-right font-medium">Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {(order.entryList || []).length === 0 ? (
                      <tr>
                        <td colSpan={6} className="p-6 text-center text-slate-400">
                          No items recorded for this order
                        </td>
                      </tr>
                    ) : (
                      order.entryList.map((entry, idx) => (
                        <tr key={entry.identifier || idx} className="border-b border-slate-100 last:border-0">
                          <td className="p-3 text-slate-500">{idx + 1}</td>
                          <td className="p-3 font-medium text-[#14211E]">
                            {entry.productName}
                          </td>
                          <td className="p-3 text-right">{entry.quantity}</td>
                          <td className="p-3 text-right font-mono">
                            {currency(entry.unitPrice)}
                          </td>
                          <td className="p-3 text-right font-mono text-red-500">
                            {currency(entry.unitDiscount)}
                          </td>
                          <td className="p-3 text-right font-mono font-semibold">
                            {currency(entry.totalPrice)}
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>

              <div className="flex justify-between items-start gap-8">
                <div className="text-sm text-slate-600 max-w-[55%]">
                  <p className="font-medium text-[#14211E] mb-1">
                    {numberToWords(order.totalPrice)} only
                  </p>
                  <p className="mt-4">
                    <span className="font-semibold text-[#14211E]">Payment Method:</span>{" "}
                    {order.paymentMethod || "N/A"}
                  </p>
                  {order.receivedAmount != null && (
                    <p>
                      <span className="font-semibold text-[#14211E]">Received:</span>{" "}
                      {currency(order.receivedAmount)}
                    </p>
                  )}
                  {order.changeAmount != null && (
                    <p>
                      <span className="font-semibold text-[#14211E]">Change:</span>{" "}
                      {currency(order.changeAmount)}
                    </p>
                  )}
                </div>

                <div className="w-64 text-sm space-y-2">
                  <div className="flex justify-between">
                    <span className="text-slate-500">Subtotal</span>
                    <span className="font-mono">{currency(order.originalPrice)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-slate-500">Discount</span>
                    <span className="font-mono text-red-500">
                      −{currency(order.discount)}
                    </span>
                  </div>
                  <div className="flex justify-between pt-2 border-t border-slate-200">
                    <span className="font-semibold text-[#14211E]">Total Amount</span>
                    <span className="font-mono font-bold text-[#14211E] text-base">
                      {currency(order.totalPrice)}
                    </span>
                  </div>
                </div>
              </div>

              <p className="text-center text-xs text-slate-400 mt-8">
                Thank you for your business.
              </p>
            </>
          )}
        </div>
      </div>
    </div>
  );
}


OrderDetailsModal.propTypes = {
  order: PropTypes.shape({
    identifier: PropTypes.string,
    customerIdentifier: PropTypes.string,
    totalPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    createdBy: PropTypes.string,
    createdUser: PropTypes.string,
    createdByName: PropTypes.string,
    createdDate: PropTypes.string,
    createdAt: PropTypes.string,
    orderDate: PropTypes.string,
    dateCreated: PropTypes.string,
    creationDate: PropTypes.string,
    createdOn: PropTypes.string,
    created: PropTypes.string,
    timestamp: PropTypes.string,
    date: PropTypes.string,
  }).isRequired,
  customers: PropTypes.arrayOf(
    PropTypes.shape({
      identifier: PropTypes.string,
      name: PropTypes.string,
    })
  ).isRequired,
  onClose: PropTypes.func.isRequired,
};

InvoiceModal.propTypes = {
  order: PropTypes.shape({
    identifier: PropTypes.string,
    customerIdentifier: PropTypes.string,
    originalPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    discount: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    totalPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    paymentMethod: PropTypes.string,
    receivedAmount: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    changeAmount: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    customerDetail: PropTypes.shape({
      name: PropTypes.string,
      phoneNo: PropTypes.string,
      billingAddress: PropTypes.shape({
        addressLine1: PropTypes.string,
        addressLine2: PropTypes.string,
        city: PropTypes.string,
        state: PropTypes.string,
        pinCode: PropTypes.string,
      }),
    }),
    entryList: PropTypes.arrayOf(
      PropTypes.shape({
        identifier: PropTypes.string,
        productIdentifier: PropTypes.string,
        productName: PropTypes.string,
        quantity: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        unitPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        unitDiscount: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        totalPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
      })
    ),
  }).isRequired,
  loading: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
};
