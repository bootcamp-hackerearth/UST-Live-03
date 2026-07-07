"use client";

import { FetchEntity } from "@/apicalls/fetch/FetchEntity";
import { FetchList } from "@/apicalls/fetch/FetchList";
import { useEffect, useState } from "react";
import dynamic from "next/dynamic";
import Link from "next/link";
import {
  Trash2, ShoppingCart, Users, Package, Plus, Minus,
  Receipt, History, LayoutDashboard, X, FileText,
  Coins, CreditCard, QrCode, CheckCircle, AlertCircle
} from "lucide-react";

const Select = dynamic(() => import("react-select"), { ssr: false });

export default function Cart() {
  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [cartEntries, setCartEntries] = useState([]);
  const [selectedCustomerId, setSelectedCustomerId] = useState("");
  const [selectedCart, setSelectedCart] = useState(null);
  const [mounted, setMounted] = useState(false);
  const [showCustomerModal, setShowCustomerModal] = useState(false);

  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [paymentType, setPaymentType] = useState("Cash");
  const [lastCreatedOrder, setLastCreatedOrder] = useState(null);
  const [lastCreatedOrderEntries, setLastCreatedOrderEntries] = useState([]);

  const [toast, setToast] = useState({ show: false, message: "", type: "success" });

  const [newCustomer, setNewCustomer] = useState({ identifier: "", name: "", phoneNo: "" });

  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
  const getListContent = (res) => Array.isArray(res) ? res : res?.content || [];

  const triggerNotification = (message, type = "success") => {
    setToast({ show: true, message, type });
    setTimeout(() => setToast({ show: false, message: "", type: "success" }), 4000);
  };

  useEffect(() => {
    const fetchData = async () => {
      const [c, p] = await Promise.all([
        FetchList(`${baseUrl}/customer/list`, 0, 1000),
        FetchList(`${baseUrl}/product/list`, 0, 1000)
      ]);
      setCustomers(getListContent(c));
      setProducts(getListContent(p));
    };
    fetchData();
    setMounted(true);
  }, []);

  useEffect(() => {
    if (!selectedCustomerId) return;
    const loadCart = async () => {
      const cart = await FetchEntity(`${baseUrl}/cart/get`, selectedCustomerId, "text/plain");
      setSelectedCart(cart);
      const entries = await FetchList(`${baseUrl}/cartentry/list`, 0, 1000);
      setCartEntries(getListContent(entries).filter(e => e.cartId === (cart?.identifier || selectedCustomerId)));
    };
    loadCart();
  }, [selectedCustomerId]);

  const refreshCartEntries = async () => {
    const entries = await FetchList(`${baseUrl}/cartentry/list`, 0, 1000);
    setCartEntries(getListContent(entries).filter(e => e.cartId === (selectedCart?.identifier || selectedCustomerId)));
  };

  const handleAddCustomerSubmit = async (e) => {
    e.preventDefault();
    if (!newCustomer.identifier || !newCustomer.name) return;
    try {
      await FetchEntity(`${baseUrl}/customer/add`, newCustomer);
      const c = await FetchList(`${baseUrl}/customer/list`, 0, 1000);
      setCustomers(getListContent(c));
      setSelectedCustomerId(newCustomer.identifier);
      setNewCustomer({ identifier: "", name: "", phoneNo: "" });
      setShowCustomerModal(false);
      triggerNotification("Customer Account created successfully!");
    } catch (err) {
      console.error("Error creating customer:", err);
      triggerNotification("Failed to create customer account", "error");
    }
  };

  const handleProductSelect = async (option) => {
    if (!option) return;
    await FetchEntity(`${baseUrl}/cartentry/add`, {
      cartId: selectedCart?.identifier || selectedCustomerId,
      product: option.value,
      quantity: 1,
    });
    const updatedCart = await FetchEntity(`${baseUrl}/cart/get`, selectedCustomerId, "text/plain");
    setSelectedCart(updatedCart);
    await refreshCartEntries();
  };

  const handleUpdateQuantity = async (item, delta) => {
  if (delta === 1) {
    await FetchEntity(`${baseUrl}/cartentry/add`, {
      cartId: item.cartId,
      product: item.product,
      quantity: 1
    });
  } else if (item.quantity > 1) {

    await FetchEntity(
      `${baseUrl}/cartentry/updateQuantity`,
      {
        ...item,
        quantity: item.quantity - 1
      },
      "application/json",
      "PUT"
    );
  }

  const updatedCart = await FetchEntity(
    `${baseUrl}/cart/get`,
    selectedCustomerId,
    "text/plain"
  );

  setSelectedCart(updatedCart);
  await refreshCartEntries();
};

  const handleDelete = async (item) => {
   
await FetchEntity(
  `${baseUrl}/cartentry/delete`,
  {
    cartId: item.cartId,
    product: item.product
  },
  "application/json",
  "DELETE"
);

    const updatedCart = await FetchEntity(`${baseUrl}/cart/get`, selectedCustomerId, "text/plain");
    setSelectedCart(updatedCart);
    await refreshCartEntries();
  };

  const handleClearCart = async () => {
    await fetch(`${baseUrl}/cartentry/clearCart`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json"
      },
      credentials: "include",
      body: JSON.stringify({ cartId: selectedCustomerId })
    });
    setCartEntries([]);
    setSelectedCart(null);
    triggerNotification("Active customer cart container cleared.");
  };

  const handleCheckoutSubmit = async () => {
    if (!selectedCustomerId || cartEntries.length === 0) return;

    const currentCustomerIdBackup = selectedCustomerId;
    const currentPaymentTypeBackup = paymentType;
    const fallbackPricing = {
      totalOriginalPrice: selectedCart?.totalOriginalPrice || 0,
      discount: selectedCart?.discount || 0,
      totalPrice: selectedCart?.totalPrice || 0
    };

    const lineItems = cartEntries.map(item => ({
      product: item.product,
      quantity: item.quantity,
      totalPrice: item.totalPrice,
      totalOriginalPrice: item.totalOriginalPrice,
      discount: item.discount
    }));

    const orderPayload = {
      customerId: selectedCustomerId,
      paymentType: paymentType,
      orderDate: new Date().toISOString(),
      coupon: selectedCart?.coupon || "",
      couponCode: selectedCart?.coupon || "",
      discount: selectedCart?.discount || 0,
      totalPrice: selectedCart?.totalPrice || 0,
      totalOriginalPrice: selectedCart?.totalOriginalPrice || 0,
      entries: lineItems,
      items: lineItems
    };

    try {
      setShowPaymentModal(false);
      const createdOrder = await FetchEntity(`${baseUrl}/order/add`, orderPayload);

      setLastCreatedOrder({
        identifier: createdOrder?.identifier || `INV-${crypto.randomUUID().split("-")[0].toUpperCase()}`,
        customerId: createdOrder?.customerId || currentCustomerIdBackup,
        paymentType: createdOrder?.paymentType || currentPaymentTypeBackup,
        totalOriginalPrice: createdOrder?.totalOriginalPrice || fallbackPricing.totalOriginalPrice,
        discount: createdOrder?.discount || fallbackPricing.discount,
        totalPrice: createdOrder?.totalPrice || fallbackPricing.totalPrice
      });

      setLastCreatedOrderEntries([...cartEntries]);
      triggerNotification(`Transaction completed successfully via ${paymentType}!`);

      const clearOrderResponse = await fetch(`${baseUrl}/order/add`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify(orderPayload)
      });
      if (!clearOrderResponse.ok) {
        console.error("Failed to record order in secondary system:", clearOrderResponse.status);
      }

      const clearCartResponse = await fetch(`${baseUrl}/cartentry/clearCart`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({ cartId: selectedCart?.identifier || currentCustomerIdBackup })
      });
      if (!clearCartResponse.ok) {
        console.error("Failed to clear cart:", clearCartResponse.status);
      }

      setCartEntries([]);
      setSelectedCart(null);
      setSelectedCustomerId("");
      setShowSuccessModal(true);
    } catch (err) {
      console.error("Error during checkout:", err);
      triggerNotification("Failed to finalize database records.", "error");
    }
  };

  const customerOptions = customers.map(c => ({ value: c.identifier, label: `${c.phoneNo || "No Phone"} — (${c.identifier})` }));
  const productOptions = products.map(p => ({ value: p.identifier || p.id, label: `${p.identifier || p.id} - ${p.name}` }));

  return (
    <div className="min-h-screen bg-zinc-50 text-slate-800 antialiased print:bg-white print:p-0">

      {toast.show && (
        <div className="fixed top-5 left-1/2 -translate-x-1/2 z-50 animate-toastSlide font-sans pointer-events-none print:hidden">
          <div className={`flex items-center gap-2.5 px-4 py-2.5 rounded-xl shadow-lg border text-xs font-bold ${toast.type === "success" ? "bg-zinc-950 text-white border-zinc-800" : "bg-red-50 text-red-800 border-red-200"}`}>
            {toast.type === "success" ? <CheckCircle size={15} className="text-emerald-400 shrink-0" /> : <AlertCircle size={15} className="text-red-500 shrink-0" />}
            <span>{toast.message}</span>
          </div>
        </div>
      )}

      <header className="sticky top-0 z-40 bg-white border-b border-zinc-200 px-6 py-4 flex flex-col sm:flex-row justify-between items-center gap-4 print:hidden shadow-xs">
        <div className="flex items-center gap-3">
          <div className="bg-zinc-900 text-white p-2.5 rounded-xl"><ShoppingCart className="h-5 w-5" /></div>
          <div>
            <h1 className="text-lg font-bold text-zinc-900 tracking-tight">Enterprise POS Dashboard</h1>
            <p className="text-xs text-zinc-400">Store Counter Gateway</p>
          </div>
        </div>

        <div className="flex items-center gap-1.5 bg-zinc-100 p-1 rounded-xl w-full sm:w-auto">
          <button className="bg-white text-zinc-900 shadow-xs font-bold flex items-center gap-2 px-4 py-2 rounded-lg text-xs tracking-wide">
            <LayoutDashboard size={14} /> POS Counter
          </button>
          <Link href="/order" className="text-zinc-500 hover:text-zinc-900 flex items-center gap-2 px-4 py-2 rounded-lg text-xs font-semibold tracking-wide transition-all">
            <History size={14} /> View History Log
          </Link>
        </div>
      </header>

      <main className="p-6 max-w-(screen-2xl) mx-auto">
        <div className="grid lg:grid-cols-12 gap-6 items-start print:hidden">
          <div className="lg:col-span-8 space-y-6">
            <div className="grid md:grid-cols-2 gap-4">
              <div className="bg-white p-5 rounded-xl border border-zinc-200 shadow-xs">
                <div className="flex justify-between items-center mb-2.5">
                  <span className="flex items-center gap-2 font-semibold text-zinc-700 text-xs uppercase tracking-wider">
                    <Users size={14} className="text-zinc-400" /> Customer Lookup
                  </span>
                  <button onClick={() => setShowCustomerModal(true)} className="bg-zinc-900 text-white p-1.5 rounded-full hover:bg-zinc-800 transition cursor-pointer">
                    <Plus size={12} />
                  </button>
                </div>
                {mounted && (
                  <Select
                    options={customerOptions}
                    value={customerOptions.find(o => o.value === selectedCustomerId) || null}
                    onChange={(o) => setSelectedCustomerId(o?.value || "")}
                    placeholder="Search Phone or Identifier..."
                  />
                )}
              </div>

              <div className="bg-white p-5 rounded-xl border border-zinc-200 shadow-xs">
                <span className="flex items-center gap-2 font-semibold text-zinc-700 text-xs uppercase tracking-wider mb-2.5">
                  <Package size={14} className="text-zinc-400" /> Catalog Products
                </span>
                <Select options={productOptions} onChange={handleProductSelect} isDisabled={!selectedCustomerId} placeholder="Search Products..." />
              </div>
            </div>

            <div className="bg-white p-6 rounded-xl border border-zinc-200 shadow-xs">
              <span className="flex items-center gap-2 font-bold text-zinc-900 text-sm mb-4 border-b border-zinc-100 pb-3">
                <Receipt size={16} className="text-zinc-400" /> Counter Cart Allocations ({cartEntries.length})
              </span>

              {cartEntries.length === 0 ? (
                <div className="text-center py-16 text-zinc-400 border border-dashed border-zinc-200 rounded-xl bg-zinc-50/50 text-xs">
                  <ShoppingCart size={32} className="mx-auto mb-2 opacity-30 text-zinc-400" />
                  Cart is empty. Link a customer account and add items to begin checkout.
                </div>
              ) : (
                <div className="grid md:grid-cols-2 gap-4">
                  {cartEntries.map((item) => {
                    const product = products.find(p => p.identifier === item.product || p.id === item.product);
                    const unit = item.quantity > 0 ? item.totalPrice / item.quantity : 0;
                    return (
                      <div key={item.product} className="border border-zinc-200 rounded-xl p-4 bg-white flex flex-col justify-between">
                        <div>
                          <div className="flex justify-between items-start gap-2 mb-2">
                            <div className="font-bold text-zinc-900 text-xs sm:text-sm truncate">{product?.name || "Inventory Item"}</div>
                            <button onClick={() => handleDelete(item)} className="p-1 hover:bg-zinc-100 rounded-lg text-zinc-400 hover:text-red-500 transition cursor-pointer">
                              <Trash2 size={14} />
                            </button>
                          </div>
                          <div className="text-[10px] text-zinc-400 font-mono mb-2">ID: {item.product}</div>
                          <div className="text-[11px] space-y-1 mb-4 text-zinc-500 border-b border-zinc-100 pb-2">
                            <div className="flex justify-between"><span>Qty:</span> <span className="font-mono text-zinc-700">{item.quantity}</span></div>
                            <div className="flex justify-between"><span>Gross Total:</span> <span className="font-mono text-zinc-700">₹{item.totalOriginalPrice}</span></div>
                            <div className="flex justify-between"><span>Markdown:</span> <span className="font-mono text-zinc-700">₹{item.discount}</span></div>
                            <div className="flex justify-between"><span>Unit Rate:</span> <span className="font-mono text-zinc-700">₹{unit.toFixed(2)}</span></div>
                          </div>
                        </div>

                        <div className="flex justify-between items-center">
                          <div className="flex items-center gap-1 bg-zinc-50 border border-zinc-200 rounded-lg p-0.5">
                            <button onClick={() => handleUpdateQuantity(item, -1)} className="w-7 h-7 rounded-md hover:bg-white flex items-center justify-center text-zinc-600 transition cursor-pointer"><Minus size={11} /></button>
                            <span className="font-bold text-xs px-1.5 min-w-[20px] text-center text-zinc-900">{item.quantity}</span>
                            <button onClick={() => handleUpdateQuantity(item, 1)} className="w-7 h-7 rounded-md bg-zinc-900 text-white flex items-center justify-center transition cursor-pointer"><Plus size={11} /></button>
                          </div>
                          <span className="text-xs font-bold text-zinc-900 font-mono">₹{item.totalPrice}</span>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          </div>

          <div className="lg:col-span-4 bg-white border border-zinc-200 p-6 rounded-xl shadow-xs sticky top-24 space-y-6">
            <div>
              <h2 className="font-bold text-xs tracking-wider text-zinc-400 uppercase mb-4">Summary Ledger</h2>
              <div className="space-y-2.5 border-b border-zinc-100 pb-4 text-xs font-medium">
                <div className="flex justify-between text-zinc-500"><span>Gross Subtotal</span><span className="font-mono text-zinc-800">₹{selectedCart?.totalOriginalPrice || 0}</span></div>
                <div className="flex justify-between text-zinc-500"><span>Applied Discounts</span><span className="font-mono text-zinc-600">₹{selectedCart?.discount || 0}</span></div>
              </div>
              <div className="flex justify-between items-baseline pt-4">
                <span className="text-xs font-bold text-zinc-500 uppercase">Net Balance Due</span>
                <span className="text-2xl font-bold text-zinc-900 font-mono">₹{selectedCart?.totalPrice || 0}</span>
              </div>
            </div>

            <div className="space-y-2">
              <button onClick={() => setShowPaymentModal(true)} disabled={!selectedCustomerId || cartEntries.length === 0} className="w-full bg-zinc-900 hover:bg-zinc-800 disabled:bg-zinc-100 disabled:text-zinc-400 text-white font-bold text-xs uppercase py-3.5 rounded-xl tracking-wider transition cursor-pointer">Checkout</button>
              <button onClick={handleClearCart} disabled={!selectedCustomerId || cartEntries.length === 0} className="w-full bg-zinc-100 hover:bg-zinc-200 text-zinc-600 font-semibold text-xs py-2.5 rounded-xl transition cursor-pointer">Clear Container</button>
            </div>
          </div>
        </div>
      </main>

      {showCustomerModal && (
        <div className="fixed inset-0 bg-zinc-900/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl shadow-xl max-w-sm w-full border border-zinc-100 overflow-hidden">
            <div className="px-5 py-4 border-b border-zinc-100 flex justify-between items-center bg-zinc-50">
              <h3 className="text-xs font-bold uppercase tracking-wider text-zinc-700">Create Customer Profile</h3>
              <button onClick={() => setShowCustomerModal(false)} className="text-zinc-400 hover:text-zinc-600"><X size={16} /></button>
            </div>
            <form onSubmit={handleAddCustomerSubmit} className="p-5 space-y-4 text-xs">
              <div>
                <label htmlFor="customer-identifier" className="block text-zinc-500 mb-1">Customer Identifier / Email *</label>
                <input id="customer-identifier" type="email" required placeholder="customer@email.com" value={newCustomer.identifier} onChange={(e) => setNewCustomer({ ...newCustomer, identifier: e.target.value })} className="w-full px-3 py-2 border rounded-lg text-zinc-800 focus:outline-none focus:ring-1 focus:ring-zinc-950" />
              </div>
              <div>
                <label htmlFor="customer-name" className="block text-zinc-500 mb-1">Full Name *</label>
                <input id="customer-name" type="text" required placeholder="John Doe" value={newCustomer.name} onChange={(e) => setNewCustomer({ ...newCustomer, name: e.target.value })} className="w-full px-3 py-2 border rounded-lg text-zinc-800 focus:outline-none focus:ring-1 focus:ring-zinc-950" />
              </div>
              <div>
                <label htmlFor="customer-phone" className="block text-zinc-500 mb-1">Phone Number</label>
                <input id="customer-phone" type="text" placeholder="9876543210" value={newCustomer.phoneNo} onChange={(e) => setNewCustomer({ ...newCustomer, phoneNo: e.target.value })} className="w-full px-3 py-2 border rounded-lg text-zinc-800 focus:outline-none focus:ring-1 focus:ring-zinc-950" />
              </div>
              <div className="flex gap-2 pt-2 font-bold">
                <button type="button" onClick={() => setShowCustomerModal(false)} className="flex-1 bg-zinc-100 text-zinc-600 py-2.5 rounded-lg hover:bg-zinc-200">Cancel</button>
                <button type="submit" className="flex-1 bg-zinc-900 text-white py-2.5 rounded-lg hover:bg-zinc-800">Save Profile</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showPaymentModal && (
        <div className="fixed inset-0 bg-zinc-900/60 backdrop-blur-xs flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-xl max-w-sm w-full p-6 text-left scale-up">
            <h3 className="text-sm font-black text-zinc-900 uppercase tracking-wide mb-1">Select Tender Mode</h3>
            <p className="text-xs text-zinc-400 mb-5">Choose incoming settlement validation track.</p>
            <div className="grid grid-cols-1 gap-2.5 mb-6">
              {[
                { name: "Cash", icon: <Coins size={18} className="text-emerald-600" />, desc: "Physical hard tender logs" },
                { name: "Card", icon: <CreditCard size={18} className="text-blue-600" />, desc: "Visa, Mastercard, RuPay processing" },
                { name: "UPI", icon: <QrCode size={18} className="text-purple-600" />, desc: "Instant QR payment code" }
              ].map((m) => (
                <button key={m.name} type="button" onClick={() => setPaymentType(m.name)} className={`w-full flex items-center justify-between p-3.5 rounded-xl border text-left transition ${paymentType === m.name ? "border-zinc-900 bg-zinc-900 text-white" : "border-zinc-200 bg-white hover:bg-zinc-50"}`}>
                  <div className="flex items-center gap-3.5">
                    <div className={`p-2 rounded-lg ${paymentType === m.name ? 'bg-white/10 text-white' : 'bg-zinc-100'}`}>{m.icon}</div>
                    <div>
                      <span className="text-xs font-bold block">{m.name} Payment</span>
                      <span className={`text-[10px] block mt-0.5 ${paymentType === m.name ? 'text-zinc-300' : 'text-zinc-400'}`}>{m.desc}</span>
                    </div>
                  </div>
                  {paymentType === m.name && <CheckCircle size={14} className="text-white" />}
                </button>
              ))}
            </div>
            <div className="flex gap-2 font-bold text-xs">
              <button onClick={() => setShowPaymentModal(false)} className="flex-1 bg-zinc-100 text-zinc-600 py-3 rounded-xl hover:bg-zinc-200">Cancel</button>
              <button onClick={handleCheckoutSubmit} className="flex-1 bg-zinc-900 hover:bg-zinc-800 text-white py-3 rounded-xl shadow-xs">Proceed to Payment</button>
            </div>
          </div>
        </div>
      )}

      {showSuccessModal && (
        <div className="fixed inset-0 bg-zinc-900/60 backdrop-blur-xs flex items-center justify-center z-50 p-4 overflow-y-auto print:absolute print:inset-0 print:bg-white print:p-0">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 text-center print:p-0 border border-zinc-100 scale-up">
            <div className="print:hidden mb-4">
              <CheckCircle className="text-zinc-900 h-10 w-10 mx-auto mb-2" />
              <h3 className="text-sm font-black text-zinc-900 uppercase tracking-wide">Order Placed Successfully!</h3>
            </div>

            <div id="receipt-print-area" className="border border-zinc-200 rounded-xl p-5 text-left font-mono text-[11px] text-zinc-700 bg-zinc-50 print:border-none print:bg-white">
              <div className="text-center border-b border-dashed border-zinc-300 pb-3 mb-4">
                <h4 className="text-xs font-black tracking-widest text-zinc-900 uppercase">RETAIL STORE STATEMENT</h4>
              </div>
              <div className="space-y-0.5 mb-4 text-zinc-600 text-[10px]">
                <div><b>Invoice ID:</b> {lastCreatedOrder?.identifier || "Generating..."}</div>
                <div><b>Customer ID:</b> {lastCreatedOrder?.customerId || "N/A"}</div>
                <div><b>Tender Track:</b> {lastCreatedOrder?.paymentType || "N/A"}</div>
              </div>

              <div className="border-b border-dashed border-zinc-400 pb-1 mb-2 font-bold text-zinc-800 grid grid-cols-12 gap-1 uppercase text-[10px]">
                <div className="col-span-7">Description</div>
                <div className="col-span-2 text-center">Qty</div>
                <div className="col-span-3 text-right">Price</div>
              </div>
              <div className="divide-y divide-zinc-100 mb-4">
                {lastCreatedOrderEntries.map((item) => {
                  const prod = products.find(p => p.identifier === item.product || p.id === item.product);
                  return (
                    <div key={item.product} className="grid grid-cols-12 gap-1 py-1.5 text-zinc-600 items-start">
                      <div className="col-span-7 truncate">
                        <span className="font-bold text-zinc-800 block truncate">{prod?.name || "Asset Item"}</span>
                      </div>
                      <div className="col-span-2 text-center">{item.quantity}</div>
                      <div className="col-span-3 text-right font-bold text-zinc-800">₹{item.totalPrice}</div>
                    </div>
                  );
                })}
              </div>

              <div className="border-t border-dashed border-zinc-400 pt-3 space-y-1 text-right text-zinc-500 font-medium">
                <div className="flex justify-between">
                  <span>Gross Rate:</span>
                  <span>₹{Number(lastCreatedOrder?.totalOriginalPrice || 0).toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-zinc-500">
                  <span>Discounts:</span>
                  <span>-₹{Number(lastCreatedOrder?.discount || 0).toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-xs font-black border-t border-dashed border-zinc-400 pt-2 mt-1 text-zinc-900">
                  <span>Net Charged Total:</span>
                  <span className="text-zinc-950 text-sm font-black">₹{Number(lastCreatedOrder?.totalPrice || 0).toFixed(2)}</span>
                </div>
              </div>
            </div>

            <div className="flex gap-2 mt-5 print:hidden text-xs font-bold">
              <button onClick={() => setShowSuccessModal(false)} className="flex-1 bg-zinc-100 text-zinc-600 py-2.5 rounded-xl hover:bg-zinc-200">Dismiss</button>
              <button onClick={() => globalThis.print()} className="flex-1 bg-zinc-900 hover:bg-zinc-800 text-white py-2.5 rounded-xl flex items-center justify-center gap-1.5"><FileText size={13} /> OK (Print Receipt)</button>
            </div>
          </div>
        </div>
      )}

      <style>{`
        @media print {
          body * { visibility: hidden; }
          #receipt-print-area, #receipt-print-area * { visibility: visible; }
          #receipt-print-area { position: absolute; left: 0; top: 0; width: 100%; }
        }
        @keyframes toastSlide {
          from { transform: translate(-50%, -20px); opacity: 0; }
          to { transform: translate(-50%, 0); opacity: 1; }
        }
        .animate-toastSlide { animation: toastSlide 0.25s cubic-bezier(0.175, 0.885, 0.32, 1.275) forwards; }
      `}</style>
    </div>
  );
}