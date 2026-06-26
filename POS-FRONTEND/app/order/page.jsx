"use client";

import { useEffect, useState } from "react";
import {useSearchParams,useRouter} from "next/navigation";
import PropTypes from "prop-types";
import {
  Eye,
  Receipt,
  X,
  Package,
  ChevronLeft,
  ChevronRight,
  RefreshCw,
  ArrowLeft,
  Printer
} from "lucide-react";

import Sidebar from "../../components/layout/Sidebar";
import api from "../../services/api";

const money = (v) =>
  `₹${Number(v || 0).toLocaleString("en-IN", {
    minimumFractionDigits: 2,
  })}`;

export default function OrderPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [orders, setOrders] = useState([]);
  const [selected, setSelected] = useState(null);
  const [customerName, setCustomerName] = useState("");
  const [loading, setLoading] = useState(false);
  
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(4);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const headers = () => ({
    Authorization: `Bearer ${localStorage.getItem("token")}`,
  });

  useEffect(() => {
    loadOrders();
  }, [currentPage, pageSize]);

  const loadOrders = async () => {
    setLoading(true);
    try {
      const res = await api.post(
        "/order/list",
        {
          page: currentPage,
          sizePerPage: pageSize,
        },
        {
          headers: headers(),
        }
      );

      setOrders(res.data.dtoList || []);
      setTotalPages(res.data.totalPages || 1);
      setTotalElements(res.data.totalElements || 0);
    } catch (err) {
      console.error("Error loading orders:", err);
    } finally {
      setLoading(false);
    }
  };
useEffect(() => {
  const orderId = searchParams.get("open");

  if (orderId && !selected) {
    openOrder(orderId);
  }
}, [searchParams, selected]);

  const openOrder = async (identifier) => {
    try {
      const res = await api.get(`/order/get?identifier=${identifier}`, {
        headers: headers(),
      });

      setSelected(res.data);
      setCustomerName("");

      if (res.data?.customerIdentifier) {
        try {
          const customer = await api.get(
            `/customer/get?identifier=${res.data.customerIdentifier}`,
            {
              headers: headers(),
            }
          );

          setCustomerName(customer.data?.name || "Unknown Customer");
        } catch {
          setCustomerName(res.data.customerIdentifier);
        }
      }
    } catch (err) {
      console.log(err);
    }
  };

  const getPaymentBadge = (method) => {
    const m = String(method || "").toUpperCase();
    if (m.includes("CASH")) return "bg-emerald-50 text-emerald-700 border-emerald-200";
    if (m.includes("UPI") || m.includes("CARD")) return "bg-purple-50 text-purple-700 border-purple-200";
    return "bg-gray-50 text-gray-700 border-gray-200";
  };

  const printInvoice = () => {
    if (!selected) return;

    const rows = selected.entryList
      ?.map(
        (x) => `
          <tr>
            <td style="font-family: monospace; font-weight: 600; color: #0f172a;">${x.productIdentifier}</td>
            <td style="text-align: center; font-weight: 700;">${x.quantity}</td>
            <td style="color: #64748b;">${money(x.mrp)}</td>
            <td style="color: #16a34a;">-${money(x.unitDiscount)}</td>
            <td style="font-medium: 500;">${money(x.unitPrice)}</td>
            <td style="text-align: right; font-weight: 700; color: #1e40af;">${money(x.totalPrice)}</td>
          </tr>
        `
      )
      .join("");

    const invoice = `
      <html>
      <head>
        <title>Invoice - ${selected.identifier}</title>
        <style>
          body { font-family: system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif; padding: 40px; color: #334155; background: #ffffff; line-height: 1.5; }
          .invoice-box { max-width: 850px; margin: auto; padding: 36px; border: 1px solid #e2e8f0; border-radius: 20px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02); }
          .header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 28px; border-bottom: 2px solid #f1f5f9; padding-bottom: 24px; }
          .title { font-size: 26px; font-weight: 900; color: #2563eb; letter-spacing: -0.03em; text-transform: uppercase; }
          .invoice-id { font-size: 14px; color: #64748b; margin-top: 4px; font-family: monospace; }
          .date-container { text-align: right; font-size: 14px; color: #475569; }
          .meta-grid { background: #f8fafc; border: 1px solid #f1f5f9; padding: 20px; border-radius: 14px; margin-bottom: 28px; display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
          .meta-item { font-size: 13px; color: #64748b; flex-direction: column; }
          .meta-item b { color: #1e293b; display: block; font-size: 11px; text-transform: uppercase; letter-spacing: 0.05em; margin-bottom: 4px; }
          .meta-value { font-size: 14px; font-weight: 600; color: #334155; }
          .meta-mono { font-family: monospace; }
          table { width: 100%; border-collapse: collapse; margin-top: 20px; text-align: left; }
          th { background: #f8fafc; padding: 12px 14px; font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; color: #64748b; border-bottom: 2px solid #e2e8f0; }
          td { padding: 14px; font-size: 14px; border-bottom: 1px solid #f1f5f9; color: #475569; }
          .summary-wrapper { width: 100%; display: flex; justify-content: flex-end; margin-top: 30px; }
          .summary { width: 340px; background: #fafafa; border: 1px solid #f1f5f9; border-radius: 14px; padding: 20px; }
          .summary p { display: flex; justify-content: space-between; margin: 0 0 10px 0; font-size: 14px; color: #64748b; }
          .summary p span:last-child { font-weight: 600; color: #334155; }
          .summary h2 { display: flex; justify-content: space-between; border-top: 2px dashed #e2e8f0; padding-top: 14px; margin: 14px 0 14px 0; font-size: 18px; font-weight: 800; color: #0f172a; }
          .summary h2 span:last-child { color: #2563eb; }
          @media print { body { padding: 0; } .invoice-box { border: none; box-shadow: none; padding: 0; } }
        </style>
      </head>
      <body>
        <div class="invoice-box">
          <div class="header">
            <div>
              <div class="title">POS Invoice</div>
              <div class="invoice-id">No: ${selected.invoiceCode || selected.identifier}</div>
            </div>
            <div class="date-container">
              <span style="font-size: 11px; text-transform: uppercase; tracking-wider: 0.05em; color: #94a3b8; display: block; font-weight:700;">Issue Date</span>
              <span style="font-weight: 600;">${selected.createdOn ? new Date(selected.createdOn).toLocaleString("en-IN") : "-"}</span>
            </div>
          </div>
          <div class="meta-grid">
            <div class="meta-item"><b>Order ID</b><span class="meta-value meta-mono">${selected.identifier}</span></div>
           <div class="meta-item">
  <b>Customer</b><span class="meta-value">${customerName || "Walk-in Customer"}</span>
                ${
                  selected.customerIdentifier
                    ? `
                    <div style="
                      font-size:12px;
                      color:#64748b;
                      margin-top:4px;
                      font-family:monospace;
                    ">
                      ID: ${selected.customerIdentifier}
                    </div>
                  `
                    : ""
                }
              </div>
            <div class="meta-item"><b>Payment Via</b><span class="meta-value" style="text-transform: uppercase;">${selected.paymentMethod}</span></div>
          </div>
          <table>
            <thead>
              <tr>
                <th>Product</th>
                <th style="text-align: center;">Qty</th>
                <th>MRP</th>
                <th>Discount</th>
                <th>Net Price</th>
                <th style="text-align: right;">Total</th>
              </tr>
            </thead>
            <tbody>
              ${rows}
            </tbody>
          </table>
          <div class="summary-wrapper">
            <div class="summary">
              <p><span>Original Price</span><span>${money(selected.originalPrice)}</span></p>
              <p style="color: #16a34a;"><span style="color: #16a34a;">Discount Saved</span><span>-${money(selected.discount)}</span></p>
              <h2><span>Grand Total</span><span>${money(selected.totalPrice)}</span></h2>
              <p style="font-size: 13px;"><span>Amount Received</span><span>${money(selected.receivedAmount)}</span></p>
              <p style="font-size: 13px; margin-bottom: 0;"><span>Change Returned</span><span>${money(selected.changeAmount)}</span></p>
            </div>
          </div>
        </div>
      </body>
      </html>
    `;
const win = window.open("", "_blank");

if (win) {
  win.document.documentElement.innerHTML = invoice;

  setTimeout(() => {
    win.focus();
    win.print();
  }, 300);
}
  };
  const closeOrderModal = () => {
  const openedFromCheckout = searchParams.get("open");

  setSelected(null);

  if (openedFromCheckout) {
    router.push("/cart");
  }
};
const renderOrders = () => {
  if (loading) {
    return [
      <tr key="loading">
        <td
          colSpan="5"
          className="text-center py-20 text-slate-400 font-medium"
        >
          <div className="flex justify-center items-center gap-2">
            <span
              className="w-5 h-5 border-2 border-blue-600 border-t-transparent rounded-full animate-spin"
            />
            {" "}
            Loading orders...
          </div>
        </td>
      </tr>,
    ];
  }

  if (orders.length === 0) {
    return [
      <tr key="empty">
        <td
          colSpan="5"
          className="text-center py-20 text-slate-400 font-medium"
        >
          No Orders Found
        </td>
      </tr>,
    ];
  }

  return orders.map((order) => (
    <tr
      key={order.identifier}
      className="hover:bg-slate-50/80 transition-colors group"
    >
      <td className="p-4 font-mono font-bold text-slate-800 text-sm pl-6">
        {order.identifier}
      </td>

      <td className="p-4 text-slate-600 text-sm font-medium">
        {order.customerIdentifier || "Walk-in Customer"}
      </td>

      <td className="p-4">
        <span
          className={`px-2.5 py-1 text-xs font-semibold rounded-lg border uppercase tracking-wide ${getPaymentBadge(order.paymentMethod)}`}
        >
          {order.paymentMethod || "N/A"}
        </span>
      </td>

      <td className="p-4 font-semibold text-slate-900 text-sm">
        {money(order.totalPrice)}
      </td>

      <td className="p-4 text-right pr-6">
        <button
          onClick={() => openOrder(order.identifier)}
          className="inline-flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold rounded-xl bg-blue-50 text-blue-600 hover:bg-blue-600 hover:text-white transition-all shadow-sm"
        >
          <Eye size={14} />
          View Details
        </button>
      </td>
    </tr>
  ));
};
  return (
    <Sidebar>
      <div className="min-h-screen bg-slate-50/50 p-6 md:p-8">
        <div className="max-w-7xl mx-auto space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-xl bg-blue-600 flex items-center justify-center shadow-lg shadow-blue-100">
                <Receipt className="text-white w-6 h-6" />
              </div>
              <div>
                <div className="flex items-center gap-3">
                  <button
                    onClick={() => router.push("/home")}
                    className="inline-flex items-center justify-center p-1.5 rounded-lg text-slate-500 hover:text-slate-900 hover:bg-slate-100 transition-colors mr-1"
                    title="Back to Home"
                  >
                <ArrowLeft size={18} />
                  </button>
                  <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Sales History</h1>
                </div>
                <p className="text-sm text-slate-500 font-medium mt-1">
                  Manage and monitor completed system orders ({totalElements} total)
                </p>
              </div>
            </div>
            
            <button 
              onClick={loadOrders}
              disabled={loading}
              className="self-start sm:self-auto flex items-center gap-2 px-4 py-2 text-sm font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200/80 rounded-xl transition-all disabled:opacity-50"
            >
              <RefreshCw size={15} className={loading ? "animate-spin" : ""} />
              Refresh
            </button>
          </div>
          <div className="bg-white rounded-2xl border border-slate-100 shadow-sm overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-slate-50/70 border-b border-slate-100">
                    <th className="p-4 text-xs font-bold uppercase tracking-wider text-slate-500 pl-6">Order ID</th>
                    <th className="p-4 text-xs font-bold uppercase tracking-wider text-slate-500">Customer ID</th>
                    <th className="p-4 text-xs font-bold uppercase tracking-wider text-slate-500">Payment</th>
                    <th className="p-4 text-xs font-bold uppercase tracking-wider text-slate-500">Total Price</th>
                    <th className="p-4 text-xs font-bold uppercase tracking-wider text-slate-500 text-right pr-6">Action</th>
                  </tr>
                </thead>
                  <tbody className="divide-y divide-slate-100">
                    {renderOrders()}
                  </tbody>
              </table>
            </div>
            <div className="bg-white border-t border-slate-100 px-6 py-4 flex flex-col sm:flex-row items-center justify-between gap-4">
              <div className="flex items-center gap-4 text-sm text-slate-500 font-medium">
                <div className="flex items-center gap-2">
                  <span>Rows per page:</span>
                  <select
                    value={pageSize}
                    onChange={(e) => {
                      setPageSize(Number(e.target.value));
                      setCurrentPage(0);
                    }}
                    className="border border-slate-200 rounded-lg p-1 bg-transparent font-semibold text-slate-700 outline-none focus:border-blue-500"
                  >
                    {[5, 10, 25, 50, 100].map((size) => (
                      <option key={size} value={size}>{size}</option>
                    ))}
                  </select>
                </div>
                <span>
                  Showing {orders.length > 0 ? currentPage * pageSize + 1 : 0} - {Math.min((currentPage + 1) * pageSize, totalElements)} of {totalElements}
                </span>
              </div>

              <div className="flex items-center gap-2">
                <button
                  onClick={() => setCurrentPage((prev) => Math.max(0, prev - 1))}
                  disabled={currentPage === 0 || loading}
                  className="p-2 border border-slate-200 rounded-xl hover:bg-slate-50 disabled:opacity-40 disabled:hover:bg-transparent transition-all"
                >
                  <ChevronLeft size={16} />
                </button>
                <span className="text-sm font-semibold text-slate-700 px-2">
                  Page {currentPage + 1} of {totalPages || 1}
                </span>
                <button
                  onClick={() => setCurrentPage((prev) => Math.min(totalPages - 1, prev + 1))}
                  disabled={currentPage >= totalPages - 1 || loading}
                  className="p-2 border border-slate-200 rounded-xl hover:bg-slate-50 disabled:opacity-40 disabled:hover:bg-transparent transition-all"
                >
                  <ChevronRight size={16} />
                </button>
              </div>
            </div>
          </div>

        </div>
        {selected && (
          <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex justify-center items-center z-[99999] p-4 animate-fade-in">
            <div className="bg-white rounded-2xl w-full max-w-5xl max-h-[85vh] overflow-hidden flex flex-col shadow-2xl border border-slate-100">
              <div className="p-6 border-b border-slate-100 flex justify-between items-center bg-slate-50/50">
                <div>
                  <h2 className="text-xl font-bold text-slate-900">Order Deep-Dive</h2>
                  <div className="text-xs font-mono text-slate-500 mt-0.5 bg-slate-200/60 px-2 py-0.5 rounded-md inline-block">
                    ID: {selected.identifier}
                  </div>
                </div>
                  <div className="flex items-center gap-3">
                    <button
                      onClick={printInvoice}
                      className="px-4 py-2 rounded-xl bg-green-600 text-white flex items-center gap-2 hover:bg-green-700 font-semibold text-sm transition-colors shadow-sm"
                    >
                      <Printer size={16} />
                      Print Invoice
                    </button>

                    <button
                      onClick={closeOrderModal}
                      className="w-9 h-9 rounded-xl hover:bg-slate-200/70 text-slate-500 hover:text-slate-800 flex justify-center items-center transition-all"
                    >
                      <X size={18} />
                    </button>
                  </div>
              </div>
              <div className="p-6 overflow-y-auto space-y-6">
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                  <Info label="Customer" value={customerName ? `${customerName} (${selected.customerIdentifier})` : selected.customerIdentifier}/>
                  <Info label="Payment Method" value={selected.paymentMethod} highlight />
                  <Info 
                    label="Transaction Date" 
                    value={selected.createdOn ? new Date(selected.createdOn).toLocaleString("en-IN") : "—"} 
                  />
                  <Info label="Core Reference" value={selected.identifier} isMono />
                </div>
                <div className="grid grid-cols-2 md:grid-cols-5 gap-4 bg-slate-50 p-4 rounded-2xl border border-slate-100">
                  <Amount title="Original Price" value={selected.originalPrice} />
                  <Amount title="Discount Applied" value={selected.discount} isDiscount />
                  <Amount title="Net Total" value={selected.totalPrice} isTotal />
                  <Amount title="Cash/Amount Tendered" value={selected.receivedAmount} />
                  <Amount title="Change Returned" value={selected.changeAmount} isChange />
                </div>
                <div className="border border-slate-200/80 rounded-xl overflow-hidden">
                  <div className="p-4 bg-slate-50 border-b border-slate-200/80 flex items-center gap-2">
                    <Package size={16} className="text-slate-500" />
                    <h3 className="font-bold text-sm text-slate-700">Line Items Summary</h3>
                  </div>
                  <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left border-collapse">
                      <thead>
                        <tr className="bg-slate-100/50 text-slate-500 text-xs font-bold uppercase tracking-wider border-b border-slate-200/60">
                          <th className="p-3 pl-4">Product ID</th>
                          <th className="p-3 text-center">Qty</th>
                          <th className="p-3">MRP</th>
                          <th className="p-3 text-green-600">Discount</th>
                          <th className="p-3">Unit Net</th>
                          <th className="p-3 text-right pr-4">Line Subtotal</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-slate-100 text-slate-700">
                            {selected.entryList?.map((item) => (
                              <tr
                                key={item.productIdentifier}
                                className="hover:bg-slate-50/40 transition-colors"
                              >
                            <td className="p-3 font-semibold text-slate-900 pl-4">{item.productIdentifier}</td>
                            <td className="p-3 text-center font-bold text-slate-800">{item.quantity}</td>
                            <td className="p-3 text-slate-500">{money(item.mrp)}</td>
                            <td className="p-3 text-green-600">-{money(item.unitDiscount)}</td>
                            <td className="p-3 font-medium">{money(item.unitPrice)}</td>
                            <td className="p-3 text-right font-bold text-blue-600 pr-4">{money(item.totalPrice)}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>

              </div>
            </div>
          </div>
        )}
      </div>
    </Sidebar>
  );
}

function Info({ label, value, highlight, isMono }) {
  return (
    <div className="bg-slate-50 border border-slate-100 rounded-xl p-4">
      <div className="text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-1">{label}</div>
      <div className={`text-sm font-bold truncate text-slate-800 ${highlight ? "text-blue-600 uppercase" : ""} ${isMono ? "font-mono text-xs" : ""}`}>
        {value || "—"}
      </div>
    </div>
  );
}
Info.propTypes = {
  label: PropTypes.string.isRequired,
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  highlight: PropTypes.bool,
  isMono: PropTypes.bool,
};
function Amount({ title, value, isTotal, isDiscount, isChange }) {
  let colorTheme = "text-slate-700 bg-white";
  if (isTotal) colorTheme = "text-blue-700 bg-blue-50/50 border-blue-200";
  if (isDiscount && value > 0) colorTheme = "text-emerald-700 bg-emerald-50/40 border-emerald-100";
  if (isChange && value > 0) colorTheme = "text-amber-700 bg-amber-50/40 border-amber-100";

  return (
    <div className={`p-3.5 rounded-xl border border-slate-100 shadow-2xs flex flex-col justify-between ${colorTheme}`}>
      <div className="text-[10px] font-bold uppercase tracking-wider opacity-75">{title}</div>
      <div className="text-base font-black tracking-tight mt-1.5">{money(value)}</div>
    </div>
  );
}
Amount.propTypes = {
  title: PropTypes.string.isRequired,
  value: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  isTotal: PropTypes.bool,
  isDiscount: PropTypes.bool,
  isChange: PropTypes.bool,
};