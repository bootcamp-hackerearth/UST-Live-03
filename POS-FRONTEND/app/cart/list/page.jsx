"use client";

import { useEffect, useState } from "react";
import {
  Plus, Trash2, RefreshCw, ShoppingCart, X, Tag,
  Receipt, User, Search, CreditCard, QrCode, CheckCircle,
} from "lucide-react";
import api from "@/app/services/api";
import Layout from "@/components/Layout";
import SearchableDropdown from "@/components/SearchableDropdown";
import SearchSelector from "@/components/SearchSelector";
import AddCustomer from "../../customer/add/page";
import PropTypes from "prop-types";

const currency = (val) =>
  `₹${Number(val || 0).toLocaleString("en-IN", { minimumFractionDigits: 2 })}`;

function CartTableBody({ loading, entries, getProductDetails, updateQty, handleRemove }) {
  if (loading) {
    return (
      <tr>
        <td colSpan={7} className="text-center py-14 text-slate-400 text-sm">
          <RefreshCw size={18} className="animate-spin mx-auto mb-2 text-slate-300" />
          Loading cart…
        </td>
      </tr>
    );
  }

  if (entries.length === 0) {
    return (
      <tr>
        <td colSpan={7} className="text-center py-14">
          <p className="text-sm font-medium text-slate-500">Cart is empty</p>
          <p className="text-xs text-slate-400 mt-1">Search a product above to add items</p>
        </td>
      </tr>
    );
  }

  return entries.map((e, i) => {
    const prod = getProductDetails(e.productId);
    return (
      <tr
        key={e.identifier}
        className="border-b border-slate-100 last:border-0 hover:bg-slate-50 transition-colors"
      >
        <td className="py-3 px-4">
          <div className="flex items-center gap-3">
            <div className="h-9 w-9 rounded-lg bg-slate-100 border border-slate-200 flex items-center justify-center font-semibold text-slate-600 text-sm shrink-0">
              {prod?.name?.charAt(0)?.toUpperCase() ?? "?"}
            </div>
            <div>
              <p className="font-medium text-slate-800 text-sm leading-tight">
                {prod?.name ?? "—"}
              </p>
              <p className="text-xs text-slate-400 mt-0.5">
                {prod?.brand}{prod?.brand && prod?.category ? " · " : ""}{prod?.category}
              </p>
            </div>
          </div>
        </td>
        <td className="py-3 px-4 text-right font-mono text-xs text-slate-400 line-through">
          {currency(e.mrp)}
        </td>
        <td className="py-3 px-4 text-right font-mono text-sm text-slate-700">
          {currency(e.sellingPrice)}
        </td>
        <td className="py-3 px-4 text-right font-mono text-xs text-emerald-600 font-medium">
          {currency(e.discount)}
        </td>
        <td className="py-3 px-4">
          <div className="flex items-center justify-center border border-slate-200 rounded-lg overflow-hidden w-fit mx-auto">
            <button
              onClick={() => updateQty(i, e.quantity - 1)}
              disabled={e.quantity <= 1}
              className="h-7 w-7 bg-slate-50 hover:bg-slate-100 disabled:opacity-40 text-slate-600 text-base transition-colors flex items-center justify-center"
            >
              −
            </button>
            <span className="w-9 text-center text-sm font-semibold text-slate-800">
              {e.quantity}
            </span>
            <button
              onClick={() => updateQty(i, e.quantity + 1)}
              className="h-7 w-7 bg-slate-50 hover:bg-slate-100 text-slate-600 text-base transition-colors flex items-center justify-center"
            >
              +
            </button>
          </div>
        </td>
        <td className="py-3 px-4 text-right font-mono text-sm font-semibold text-slate-800">
          {currency(e.totalPrice)}
        </td>
        <td className="py-3 px-4 text-center">
          <button
            onClick={() => handleRemove(i)}
            className="h-7 w-7 inline-flex items-center justify-center rounded-lg text-slate-300 hover:bg-red-50 hover:text-red-500 transition-colors"
          >
            <Trash2 size={14} />
          </button>
        </td>
      </tr>
    );
  });
}

export default function CartPage() {
  const [customers, setCustomers] = useState([]);
  const [customerSearchResults, setCustomerSearchResults] = useState([]);
  const [products, setProducts] = useState([]);
  const [customer, setCustomer] = useState("");
  const [selectedProduct, setSelectedProduct] = useState("");
  const [cartData, setCartData] = useState(null);
  const [entries, setEntries] = useState([]);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("CASH");
  const [receivedAmount, setReceivedAmount] = useState("");
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [checkoutLoading] = useState(false);
  const [toast, setToast] = useState("");
  const [showAddCustomer, setShowAddCustomer] = useState(false);

  const today = new Date().toLocaleDateString("en-IN", {
    day: "2-digit", month: "short", year: "numeric",
  });

  const totalQty = entries.reduce((sum, item) => sum + Number(item.quantity || 0), 0);

  const selectedCustomerObj =
    customers.find((c) => String(c.identifier ?? c.id) === String(customer)) ||
    customerSearchResults.find((c) => String(c.identifier ?? c.id) === String(customer));

  const getProductDetails = (productId) =>
    products.find((p) => String(p.identifier ?? p.id) === String(productId));

  const getList = (res) => {
    const data = res?.data;
    if (Array.isArray(data)) return data;
    if (Array.isArray(data?.dtoList)) return data.dtoList;
    return [];
  };

  const searchCustomers = async (query) => {
  if (!query.trim()) {
    setCustomerSearchResults([]);
    return;
  }

  try {
    const res = await api.post("/customer/list", {
      page: 0,
      sizePerPage: 20,
      keyword: query,
    });

    setCustomerSearchResults(getList(res));
  } catch (error) {
    console.error(error);
    setCustomerSearchResults([]);
  }
};

  useEffect(() => {
    api.post("/product/list", { page: 0, sizePerPage: 500 })
      .then((res) => setProducts(getList(res)))
      .catch(console.error);
    api.post("/customer/list", { page: 0, sizePerPage: 500 })
      .then((res) => setCustomers(getList(res)))
      .catch(console.error);
  }, []);

  const showToast = (msg) => {
    setToast(msg);
    setTimeout(() => setToast(""), 2200);
  };

  const fetchCart = async (customerId) => {
    if (!customerId) { setCartData(null); setEntries([]); return; }
    setLoading(true);
    try {
      const cartRes = await api.get(`/cart/get?identifier=${customerId}`);
      setCartData(cartRes.data || null);
      const entryRes = await api.post("/cartEntry/list", { page: 0, sizePerPage: 500 });
      const allEntries = getList(entryRes);
      setEntries(allEntries.filter((e) => e.cartId === customerId));
    } catch (err) {
      console.error(err);
      showToast("Failed to load cart");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchCart(customer); }, [customer]);

  const handleAdd = async () => {
    if (!customer || !selectedProduct) return;
    setSaving(true);
    try {
      await api.post("/cartEntry/add", { productId: selectedProduct, cartId: customer, quantity: 1 });
      setSelectedProduct("");
      fetchCart(customer);
      showToast("Item added to cart");
    } catch {
      showToast("Add failed");
    } finally {
      setSaving(false);
    }
  };

  const updateQty = async (index, newQty) => {
    if (newQty < 1) return;
    const entry = entries[index];
    const updated = [...entries];
    updated[index] = { ...updated[index], quantity: newQty, totalPrice: updated[index].sellingPrice * newQty };
    setEntries(updated);
    try {
      await api.put("/cartEntry/update", { identifier: entry.identifier, quantity: newQty });
      const res = await api.post(`/cart/recalculate?identifier=${customer}`);
      setCartData(res.data || null);
    } catch {
      showToast("Update failed");
    }
  };

  const handleRemove = async (index) => {
    const entry = entries[index];
    try {
      await api.post("/cartEntry/delete", { identifier: entry.identifier });
      fetchCart(customer);
      showToast("Item removed");
    } catch {
      showToast("Delete failed");
    }
  };

  const handleCheckout = async () => {
    try {
      const response = await api.post("/order/checkout", {
        customerIdentifier: customer,
        paymentMethod,
        receivedAmount: paymentMethod === "UPI" ? totalAmount : Number(receivedAmount),
      });
      if (response.data?.success) {
        setShowPaymentModal(false);
        setEntries([]);
        setCartData(null);
        setSelectedProduct("");
        await fetchCart(customer);
        showToast("Payment successful");
      }
    } catch {
      showToast("Checkout failed");
    }
  };

  const handleClearCart = async () => {
    if (!customer) return;
    const confirmClear = globalThis.confirm("Clear entire cart?");
    if (!confirmClear) return;
    try {
      await api.post("/cart/delete", { identifier: customer });
      setEntries([]);
      setCartData(null);
      showToast("Cart cleared");
    } catch {
      showToast("Clear failed");
    }
  };

  const totalAmount = Number(cartData?.totalPrice || 0);
  const changeAmount = paymentMethod === "CASH"
    ? Math.max(0, Number(receivedAmount || 0) - totalAmount)
    : 0;

  return (
    <Layout>
      <div className="min-h-screen bg-[#F4F5F7]">
        <div className="max-w-[1440px] mx-auto px-6 py-6 flex flex-col gap-5">

          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="h-10 w-10 rounded-xl bg-[#1F2430] flex items-center justify-center shrink-0">
                <ShoppingCart size={18} className="text-white" />
              </div>
              <div>
                <h1 className="text-[18px] font-semibold text-slate-800 leading-tight">Billing</h1>
                <p className="text-xs text-slate-400">Build and finalise a customer order</p>
              </div>
            </div>
            <div className="flex items-center gap-3">
              <div className={`flex items-center gap-2 text-xs px-3 py-1.5 rounded-full border font-medium ${customer ? "bg-emerald-50 border-emerald-200 text-emerald-700" : "bg-slate-100 border-slate-200 text-slate-500"}`}>
                <span className={`h-1.5 w-1.5 rounded-full ${customer ? "bg-emerald-500" : "bg-slate-400"}`} />
                {customer ? "Cart in progress" : "No active cart"}
              </div>
              <span className="text-xs text-slate-400 font-mono">{today}</span>
            </div>
          </div>

          <div className="bg-white border border-slate-200 rounded-xl p-5">
            <div className="grid grid-cols-12 gap-4 items-end">
              <div className="col-span-12 lg:col-span-5">
                <label className="flex items-center gap-1.5 text-[10px] font-semibold uppercase tracking-widest text-slate-400 mb-2">
                  <User size={11} /> Customer
                </label>
                <div className="flex items-center gap-2">
                  <div className="flex-1 min-w-0">
                    <SearchSelector
                      value={customer}
                      options={customerSearchResults}
                      onChange={setCustomer}
                      onSearch={searchCustomers}
                      placeholder="Search by name or phone…"
                      className="w-full h-10 bg-[#F9FAFB] border border-slate-200 rounded-lg px-3 text-sm text-slate-700 focus:outline-none focus:border-slate-400 placeholder:text-slate-300"
                    />
                  </div>
                  <button
                    type="button"
                    onClick={() => setShowAddCustomer(true)}
                    title="Add new customer"
                    className="h-10 w-10 shrink-0 rounded-lg bg-[#1F2430] hover:bg-[#2A2D3A] text-white flex items-center justify-center transition-colors"
                  >
                    <Plus size={16} />
                  </button>
                </div>
              </div>

              <div className="col-span-12 lg:col-span-5">
                <label className="flex items-center gap-1.5 text-[10px] font-semibold uppercase tracking-widest text-slate-400 mb-2">
                  <Search size={11} /> Product
                </label>
                <SearchableDropdown
                  name="product"
                  value={selectedProduct}
                  options={products}
                  onChange={(e) => setSelectedProduct(e.target.value)}
                  placeholder={customer ? "Search by name, code, brand…" : "Select a customer first"}
                  disabled={!customer}
                  className="w-full h-10 bg-[#F9FAFB] border border-slate-200 rounded-lg px-3 text-sm text-slate-700 focus:outline-none focus:border-slate-400 placeholder:text-slate-300 disabled:opacity-50 disabled:cursor-not-allowed"
                />
              </div>

              <div className="col-span-12 lg:col-span-2">
                <button
                  onClick={handleAdd}
                  disabled={saving || !customer || !selectedProduct}
                  className="h-10 w-full bg-[#1F2430] hover:bg-[#2A2D3A] disabled:bg-slate-100 disabled:text-slate-400 text-white rounded-lg text-sm font-medium transition-colors flex items-center justify-center gap-2"
                >
                  {saving ? <RefreshCw size={14} className="animate-spin" /> : <Plus size={14} />}
                  {saving ? "Adding…" : "Add to cart"}
                </button>
              </div>
            </div>

            {customer && (
              <div className="flex items-center gap-3 mt-4 pt-4 border-t border-slate-100 text-xs">
                <span className="text-slate-400">Customer</span>
                <span className="font-semibold text-slate-700">{selectedCustomerObj?.name || "—"}</span>
                <span className="text-slate-300">|</span>
                <span className="text-slate-400">Cart ID</span>
                <span className="font-mono text-slate-500 bg-slate-50 border border-slate-200 px-2 py-0.5 rounded">
                  {customer}
                </span>
              </div>
            )}
          </div>

          {!customer && (
            <div className="bg-white border border-dashed border-slate-300 rounded-xl py-20 text-center">
              <div className="h-12 w-12 rounded-xl bg-slate-100 flex items-center justify-center mx-auto mb-3">
                <ShoppingCart size={20} className="text-slate-400" />
              </div>
              <p className="text-sm font-medium text-slate-600">No customer selected</p>
              <p className="text-xs text-slate-400 mt-1">Choose a customer above to start a new order</p>
            </div>
          )}

          {customer && (
            <div className="grid grid-cols-2 lg:grid-cols-4 gap-3">
              {[
                { label: "Line items", value: entries.length, mono: false },
                { label: "Total units", value: totalQty, mono: false },
                { label: "Total savings", value: currency(cartData?.discount), mono: true, green: true },
              ].map((s) => (
                <div key={s.label} className="bg-white border border-slate-200 rounded-xl p-4">
                  <p className="text-[10px] font-semibold uppercase tracking-widest text-slate-400 mb-1">{s.label}</p>
                  <p className={`text-2xl font-semibold ${s.green ? "text-emerald-600" : "text-slate-800"} ${s.mono ? "font-mono" : ""}`}>
                    {s.value}
                  </p>
                </div>
              ))}
              <div className="bg-[#1F2430] border border-[#1F2430] rounded-xl p-4">
                <p className="text-[10px] font-semibold uppercase tracking-widest text-slate-500 mb-1">Cart value</p>
                <p className="text-2xl font-semibold text-white font-mono">{currency(cartData?.totalPrice)}</p>
              </div>
            </div>
          )}

          {customer && (
            <div className="grid grid-cols-12 gap-5">
              <div className="col-span-12 lg:col-span-8">
                <div className="bg-white border border-slate-200 rounded-xl overflow-hidden">
                  <table className="w-full text-sm">
                    <thead>
                      <tr className="bg-[#F9FAFB] border-b border-slate-200">
                        <th className="py-3 px-4 text-left text-[10px] font-semibold uppercase tracking-widest text-slate-400">Item</th>
                        <th className="py-3 px-4 text-right text-[10px] font-semibold uppercase tracking-widest text-slate-400">MRP</th>
                        <th className="py-3 px-4 text-right text-[10px] font-semibold uppercase tracking-widest text-slate-400">Unit price</th>
                        <th className="py-3 px-4 text-right text-[10px] font-semibold uppercase tracking-widest text-slate-400">Discount</th>
                        <th className="py-3 px-4 text-center text-[10px] font-semibold uppercase tracking-widest text-slate-400">Qty</th>
                        <th className="py-3 px-4 text-right text-[10px] font-semibold uppercase tracking-widest text-slate-400">Total</th>
                        <th className="py-3 px-4 w-12" />
                      </tr>
                    </thead>
                    <tbody>
                      <CartTableBody
                        loading={loading}
                        entries={entries}
                        getProductDetails={getProductDetails}
                        updateQty={updateQty}
                        handleRemove={handleRemove}
                      />
                    </tbody>
                  </table>
                </div>
              </div>

              <div className="col-span-12 lg:col-span-4">
                <div className="lg:sticky lg:top-6 bg-white border border-slate-200 rounded-xl overflow-hidden">
                  <div className="px-5 py-4 border-b border-slate-100 flex items-center gap-2">
                    <Receipt size={14} className="text-slate-400" />
                    <h3 className="text-[10px] font-semibold uppercase tracking-widest text-slate-400">Order summary</h3>
                  </div>
                  <div className="px-5 py-4 flex flex-col gap-3">
                    <div className="flex justify-between text-sm">
                      <span className="text-slate-500">Original price</span>
                      <span className="font-mono text-slate-700">{currency(cartData?.originalPrice)}</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span className="text-slate-500">Discount</span>
                      <span className="font-mono text-red-500">− {currency(cartData?.discount)}</span>
                    </div>
                    {cartData?.coupon && (
                      <div className="flex justify-between items-center text-sm">
                        <span className="text-slate-500">Coupon</span>
                        <span className="inline-flex items-center gap-1 px-2 py-1 text-xs font-medium bg-amber-50 text-amber-700 rounded border border-amber-200">
                          <Tag size={10} />{cartData.coupon}
                        </span>
                      </div>
                    )}
                    <div className="flex justify-between text-sm">
                      <span className="text-slate-500">Tax</span>
                      <span className="font-mono text-slate-700">₹0.00</span>
                    </div>
                    <div className="border-t border-dashed border-slate-200 pt-3 flex justify-between items-baseline">
                      <span className="text-sm font-semibold text-slate-700">Net payable</span>
                      <span className="text-2xl font-semibold text-slate-900 font-mono">{currency(cartData?.totalPrice)}</span>
                    </div>
                  </div>
                  <div className="px-5 pb-5 flex flex-col gap-2">
                    <button
                      onClick={() => setShowPaymentModal(true)}
                      disabled={checkoutLoading || entries.length === 0}
                      className="w-full h-10 bg-[#1F2430] hover:bg-[#2A2D3A] disabled:bg-slate-100 disabled:text-slate-400 text-white rounded-lg text-sm font-medium transition-colors flex items-center justify-center gap-2"
                    >
                      {checkoutLoading ? <RefreshCw size={14} className="animate-spin" /> : <Receipt size={14} />}
                      {checkoutLoading ? "Processing…" : "Process payment"}
                    </button>
                    <button
                      onClick={handleClearCart}
                      disabled={entries.length === 0}
                      className="w-full h-9 border border-slate-200 text-slate-400 hover:border-red-200 hover:text-red-500 hover:bg-red-50 disabled:opacity-40 rounded-lg text-sm transition-colors"
                    >
                      Clear cart
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {toast && (
        <div className="fixed bottom-6 left-1/2 -translate-x-1/2 bg-[#1F2430] text-white text-xs px-4 py-2.5 rounded-lg shadow-lg z-50 flex items-center gap-2">
          <CheckCircle size={13} className="text-emerald-400" />
          {toast}
        </div>
      )}

      {showAddCustomer && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="w-full max-w-4xl bg-white rounded-xl border border-slate-200 shadow-2xl overflow-hidden relative">
            <button
              onClick={() => setShowAddCustomer(false)}
              className="absolute top-4 right-4 h-8 w-8 rounded-lg hover:bg-slate-100 flex items-center justify-center z-10 transition-colors"
            >
              <X size={16} className="text-slate-500" />
            </button>
            <AddCustomer
              closeModal={() => setShowAddCustomer(false)}
              refreshData={async () => {
                const res = await api.post("/customer/list", { page: 0, sizePerPage: 500 });
                setCustomers(getList(res));
              }}
            />
          </div>
        </div>
      )}

      {showPaymentModal && (
        <div className="fixed inset-0 z-[100] bg-slate-900/40 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="w-full max-w-3xl bg-white rounded-xl border border-slate-200 shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">

            <div className="flex items-center justify-between px-6 py-4 border-b border-slate-100 shrink-0">
              <div>
                <h2 className="text-[15px] font-semibold text-slate-800">Review &amp; Pay</h2>
                <p className="text-xs text-slate-400 mt-0.5">
                  {selectedCustomerObj?.name || "Customer"}
                  <span className="mx-1.5 text-slate-300">·</span>
                  {entries.length} item{entries.length === 1 ? "" : "s"}
                  <span className="mx-1.5 text-slate-300">·</span>
                  {customer}
                </p>
              </div>
              <button
                onClick={() => setShowPaymentModal(false)}
                className="h-8 w-8 rounded-lg bg-slate-100 hover:bg-slate-200 flex items-center justify-center transition-colors"
              >
                <X size={15} className="text-slate-500" />
              </button>
            </div>

            <div className="flex flex-1 min-h-0 divide-x divide-slate-100">

              <div className="flex flex-col w-[55%] shrink-0 min-h-0">
                <div className="px-5 py-3 border-b border-slate-100 shrink-0">
                  <p className="text-[10px] font-semibold uppercase tracking-widest text-slate-400">
                    Cart items — remove any before confirming
                  </p>
                </div>
                <div className="flex-1 overflow-y-auto divide-y divide-slate-100">
                  {entries.map((e, i) => {
                    const prod = getProductDetails(e.productId);
                    return (
                      <div key={e.identifier} className="flex items-center gap-3 px-5 py-3 hover:bg-slate-50 transition-colors group">
                        <div className="h-8 w-8 rounded-lg bg-slate-100 border border-slate-200 flex items-center justify-center text-xs font-semibold text-slate-600 shrink-0">
                          {prod?.name?.charAt(0)?.toUpperCase() ?? "?"}
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-slate-800 truncate leading-tight">{prod?.name ?? "—"}</p>
                          <p className="text-[10px] text-slate-400 mt-0.5">
                            {prod?.brand}{prod?.brand && prod?.category ? " · " : ""}{prod?.category}
                          </p>
                        </div>
                        <span className="inline-flex items-center gap-1 text-xs text-slate-500 bg-slate-100 border border-slate-200 rounded px-2 py-0.5 font-mono shrink-0">
                          ×{e.quantity}
                        </span>
                        <div className="shrink-0 text-right min-w-[80px]">
                          <p className="text-sm font-semibold font-mono text-slate-800 leading-tight">{currency(e.totalPrice)}</p>
                          {Number(e.discount) > 0 && (
                            <p className="text-[10px] font-mono text-emerald-600 mt-0.5">−{currency(e.discount)} off</p>
                          )}
                        </div>
                        <button
                          onClick={() => handleRemove(i)}
                          className="h-7 w-7 shrink-0 rounded-lg flex items-center justify-center text-slate-300 hover:bg-red-50 hover:text-red-500 transition-colors opacity-0 group-hover:opacity-100"
                        >
                          <Trash2 size={13} />
                        </button>
                      </div>
                    );
                  })}
                  {entries.length === 0 && (
                    <div className="py-12 text-center">
                      <p className="text-sm text-slate-400">Cart is empty</p>
                    </div>
                  )}
                </div>
                <div className="border-t border-slate-100 px-5 py-3 shrink-0 bg-slate-50">
                  <div className="flex justify-between items-center text-xs text-slate-500 mb-1">
                    <span>Original price</span>
                    <span className="font-mono">{currency(cartData?.originalPrice)}</span>
                  </div>
                  <div className="flex justify-between items-center text-xs text-emerald-600 mb-2">
                    <span>Total discount</span>
                    <span className="font-mono">− {currency(cartData?.discount)}</span>
                  </div>
                  {cartData?.coupon && (
                    <div className="flex justify-between items-center text-xs mb-2">
                      <span className="text-slate-500">Coupon</span>
                      <span className="inline-flex items-center gap-1 px-2 py-0.5 text-[10px] font-medium bg-amber-50 text-amber-700 rounded border border-amber-200">
                        <Tag size={9} />{cartData.coupon}
                      </span>
                    </div>
                  )}
                  <div className="flex justify-between items-baseline pt-2 border-t border-slate-200 mt-1">
                    <span className="text-xs font-semibold text-slate-600">Net payable</span>
                    <span className="text-lg font-semibold font-mono text-slate-900">{currency(totalAmount)}</span>
                  </div>
                </div>
              </div>

              <div className="flex flex-col flex-1 min-h-0">
                <div className="flex-1 overflow-y-auto p-5 flex flex-col gap-4">
                  <div className="bg-[#F9FAFB] border border-slate-200 rounded-xl p-4">
                    <p className="text-[10px] font-semibold uppercase tracking-widest text-slate-400 mb-1">Amount due</p>
                    <p className="text-3xl font-semibold text-slate-900 font-mono">{currency(totalAmount)}</p>
                  </div>
                  <div>
                    <p className="text-[10px] font-semibold uppercase tracking-widest text-slate-400 mb-2">Payment method</p>
                    <div className="grid grid-cols-2 gap-2">
                      {[
                        { id: "CASH", label: "Cash", icon: <CreditCard size={14} /> },
                        { id: "UPI", label: "UPI / QR", icon: <QrCode size={14} /> },
                      ].map(({ id, label, icon }) => (
                        <button
                          key={id}
                          onClick={() => setPaymentMethod(id)}
                          className={`h-10 rounded-lg border text-sm font-medium flex items-center justify-center gap-2 transition-colors ${
                            paymentMethod === id
                              ? "bg-[#1F2430] border-[#1F2430] text-white"
                              : "bg-white border-slate-200 text-slate-600 hover:border-slate-300"
                          }`}
                        >
                          {icon} {label}
                        </button>
                      ))}
                    </div>
                  </div>
                  {paymentMethod === "CASH" && (
                    <div className="flex flex-col gap-3">
                      <div>
                        <p className="text-[10px] font-semibold uppercase tracking-widest text-slate-400 mb-2">Amount received (₹)</p>
                        <input
                          type="number"
                          value={receivedAmount}
                          onChange={(e) => setReceivedAmount(e.target.value)}
                          placeholder="0.00"
                          className="w-full h-10 bg-[#F9FAFB] border border-slate-200 rounded-lg px-3 font-mono text-sm text-slate-800 focus:outline-none focus:border-slate-400 placeholder:text-slate-300"
                        />
                      </div>
                      <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-3 flex items-baseline justify-between">
                        <span className="text-xs text-emerald-700 font-medium">Change to return</span>
                        <span className="text-xl font-semibold font-mono text-emerald-700">{currency(changeAmount)}</span>
                      </div>
                    </div>
                  )}
                  {paymentMethod === "UPI" && (
                    <div className="bg-slate-50 border border-slate-200 rounded-xl p-5 text-center">
                      <QrCode size={36} className="mx-auto text-slate-300 mb-2" />
                      <p className="text-xs text-slate-400">
                        Customer scans store QR code and pays{" "}
                        <span className="font-mono font-medium text-slate-600">{currency(totalAmount)}</span>{" "}directly.
                      </p>
                    </div>
                  )}
                </div>
                <div className="px-5 pb-5 pt-3 border-t border-slate-100 shrink-0">
                  <button
                    onClick={handleCheckout}
                    disabled={
                      entries.length === 0 ||
                      (paymentMethod === "CASH" && Number(receivedAmount) < totalAmount)
                    }
                    className="w-full h-10 bg-[#1F2430] hover:bg-[#2A2D3A] disabled:bg-slate-100 disabled:text-slate-400 text-white rounded-lg text-sm font-medium transition-colors flex items-center justify-center gap-2"
                  >
                    <CheckCircle size={14} />
                    Confirm &amp; complete sale
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
}
CartTableBody.propTypes = {
  loading: PropTypes.bool.isRequired,
  entries: PropTypes.arrayOf(PropTypes.object).isRequired,
  getProductDetails: PropTypes.func.isRequired,
  updateQty: PropTypes.func.isRequired,
  handleRemove: PropTypes.func.isRequired,
};