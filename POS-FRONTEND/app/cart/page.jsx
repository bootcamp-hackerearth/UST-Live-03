"use client";

import React, { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import {
  PlusIcon,
  MinusIcon,
  TrashIcon,
  ShoppingCartIcon,
  PhoneIcon,
  XMarkIcon,
  UserPlusIcon,
  CreditCardIcon,
  CheckCircleIcon,
  PrinterIcon,
  ArrowRightIcon,
} from "@heroicons/react/24/outline";
import { CommonAddFetch } from "@/fetch/CommonAddFetch";

export default function CartPage() {
  const router = useRouter();
  const [customerPhone, setCustomerPhone] = useState("");
  const [selectedCart, setSelectedCart] = useState(null);
  const [productSearch, setProductSearch] = useState("");
  const [foundProducts, setFoundProducts] = useState([]);
  const [foundCustomers, setFoundCustomers] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [itemQuantity, setItemQuantity] = useState("1");
  const [actionLoading, setActionLoading] = useState(false);
  const [feedbackError, setFeedbackError] = useState("");
  const [feedbackSuccess, setFeedbackSuccess] = useState("");
  const [allProductsList, setAllProductsList] = useState([]);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [modalName, setModalName] = useState("");
  const [modalEmail, setModalEmail] = useState("");
  const [modalPhone, setModalPhone] = useState("");
  const [modalUserType, setModalUserType] = useState("customer");

  const [isPaymentModalOpen, setIsPaymentModalOpen] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("CASH");
  const [amountReceived, setAmountReceived] = useState("");
  const [paymentSuccess, setPaymentSuccess] = useState(false);
  const [completedOrderReceipt, setCompletedOrderReceipt] = useState(null);

  const loadBaseProductCatalog = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/product/list`, {
        method: "POST",
        headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
        body: JSON.stringify({ page: 0, sizePerPage: 200 }),
      });
      const text = await response.text();
      const data = text ? JSON.parse(text) : {};
      setAllProductsList(Array.isArray(data) ? data : data.dtoList || data.content || []);
    } catch (err) {
      console.error("Catalog fetch failed:", err);
    }
  };

  const refreshCartWorkspace = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/cart/list`, {
        method: "POST",
        headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
        body: JSON.stringify({ page: 0, sizePerPage: 100, sortDirection: "ASC", sortField: "identifier" }),
      });
      const text = await response.text();
      const data = text ? JSON.parse(text) : {};
      const rawCarts = Array.isArray(data) ? data : data.dtoList || data.content || [];

      if (selectedCart) {
        const updated = rawCarts.find((c) => String(c.identifier) === String(selectedCart.identifier));
        if (updated) setSelectedCart(updated);
      }
    } catch (err) {
      console.error("Cart workspace refresh failed:", err);
    }
  };

  useEffect(() => {
    loadBaseProductCatalog();
    refreshCartWorkspace();
  }, []);

  const syncCartTotalsToDatabase = async (cartIdentifier, calculatedEntries) => {
    try {
      const totalOriginal = calculatedEntries.reduce((acc, curr) => acc + Number.parseFloat(curr.totalOriginalPrice || curr.total_original_price || 0), 0);
      const totalDiscount = calculatedEntries.reduce((acc, curr) => acc + Number.parseFloat(curr.discount || 0), 0);
      const totalPriceNet = calculatedEntries.reduce((acc, curr) => acc + Number.parseFloat(curr.totalPrice || curr.total_price || 0), 0);

      const token = localStorage.getItem("token");
      await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/cart/update`, {
        method: "PUT",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          identifier: String(cartIdentifier),
          coupon: selectedCart?.coupon || null,
          discount: totalDiscount,
          totalPrice: totalPriceNet,
          totalOriginalPrice: totalOriginal,
        })
      });
    } catch (err) {
      console.error("Cart synchronization routine error:", err);
    }
  };

  const handleInitializeCart = async (e) => {
    if (e) e.preventDefault();
    const cleanPhone = String(customerPhone || "").replaceAll(/\D/g, "");
    if (cleanPhone.length !== 10) {
      setFeedbackError("Please enter a valid 10-digit customer phone number.");
      return;
    }
    try {
      setActionLoading(true); setFeedbackError(""); setFeedbackSuccess("");
      await CommonAddFetch(`${process.env.NEXT_PUBLIC_BASE_URL}/cart/add`, {
        identifier: cleanPhone, coupon: null, discount: 0, totalPrice: 0, totalOriginalPrice: 0,
      }, "application/json");

      const token = localStorage.getItem("token");
      const listResponse = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/cart/list`, {
        method: "POST",
        headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
        body: JSON.stringify({ page: 0, sizePerPage: 100 }),
      });
      const text = await listResponse.text();
      const data = text ? JSON.parse(text) : {};
      const rawCarts = Array.isArray(data) ? data : data.dtoList || data.content || [];
      const targetCart = rawCarts.find((c) => String(c.identifier) === String(cleanPhone));
      if (targetCart) {
        setSelectedCart(targetCart);
        setCustomerPhone(""); setFoundCustomers([]);
        setFeedbackSuccess("Workspace context assigned successfully.");
      }
    } catch (error) {
      console.error(error);
      setFeedbackError("Could not configure cart entity.");
    } finally {
      setActionLoading(false);
    }
  };

  useEffect(() => {
    const cleanPhone = String(customerPhone || "").replaceAll(/\D/g, "");
    if (!cleanPhone) { setFoundCustomers([]); return; }
    const delayDebounce = setTimeout(async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/customer/list`, {
          method: "POST",
          headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
          body: JSON.stringify({ page: 0, sizePerPage: 100, sortDirection: "ASC", sortField: "identifier" }),
        });
        const text = await response.text();
        const data = text ? JSON.parse(text) : {};
        const userList = Array.isArray(data) ? data : data.dtoList || data.content || [];
        setFoundCustomers(userList.filter((u) => String(u.phoneNo || u.phone || u.phone_no || "").includes(cleanPhone)));
      } catch (err) {
        console.error(err);
      }
    }, 200);
    return () => clearTimeout(delayDebounce);
  }, [customerPhone]);

  useEffect(() => {
    if (!productSearch.trim()) { setFoundProducts([]); return; }
    const delayDebounce = setTimeout(async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/product/list`, {
          method: "POST",
          headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
          body: JSON.stringify({ page: 0, sizePerPage: 20, sortDirection: "ASC", sortField: "identifier" }),
        });
        const text = await response.text();
        const data = text ? JSON.parse(text) : {};
        const productList = Array.isArray(data) ? data : data.dtoList || data.content || [];
        setFoundProducts(productList.filter((p) => p.identifier?.toLowerCase().includes(productSearch.toLowerCase()) || p.name?.toLowerCase().includes(productSearch.toLowerCase())));
      } catch (err) {
        console.error(err);
      }
    }, 200);
    return () => clearTimeout(delayDebounce);
  }, [productSearch]);

  const handleSelectCustomer = (customer) => {
    setSelectedCustomer(customer);
    setCustomerPhone(customer.phoneNo || "");
    setFoundCustomers([]);
    setTimeout(() => { handleInitializeCart(); }, 50);
  };

  const handleAddLineItem = async (productIdentifier, customQty = null) => {
    if (!selectedCart) { setFeedbackError("Select or load an active terminal customer context first."); return; }
    const targetQty = customQty == null ? Number.parseFloat(itemQuantity) : Number.parseFloat(customQty);
    if (Number.isNaN(targetQty) || targetQty === 0) { setFeedbackError("Specify a valid quantity count."); return; }
    try {
      setActionLoading(true); setFeedbackError(""); setFeedbackSuccess("");
      const resData = await CommonAddFetch(`${process.env.NEXT_PUBLIC_BASE_URL}/cartentry/add`, {
        cartId: String(selectedCart.identifier), product: String(productIdentifier), quantity: targetQty, discount: 0, totalPrice: 0, unitPrice: 0, totalOriginalPrice: 0,
      }, "application/json");

      if (!resData || resData === "Conflict" || resData === "Error" || resData?.success === false || resData?.status === 403) {
        setFeedbackError(resData?.message || "Operation rejected by backend schema validation patterns.");
        return;
      }
      setProductSearch(""); setFoundProducts([]); setItemQuantity("1");
      const token = localStorage.getItem("token");
      const listResponse = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/cart/list`, {
        method: "POST",
        headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
        body: JSON.stringify({ page: 0, sizePerPage: 100 }),
      });
      const text = await listResponse.text();
      const data = text ? JSON.parse(text) : {};
      const freshCarts = Array.isArray(data) ? data : data.dtoList || data.content || [];
      const updatedMatch = freshCarts.find((c) => String(c.identifier) === String(selectedCart.identifier));
      const transientEntries = updatedMatch?.cartEntryDtoList || updatedMatch?.cartEntryDtoLines || [];
      await syncCartTotalsToDatabase(selectedCart.identifier, transientEntries);
      await refreshCartWorkspace();
      setFeedbackSuccess("Product row synced successfully.");
    } catch (error) {
      console.error(error);
      setFeedbackError("Error adding item execution.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleRemoveLineItem = async (productName) => {
    if (!selectedCart) return;
    try {
      setActionLoading(true); setFeedbackError(""); setFeedbackSuccess("");
      const token = localStorage.getItem("token");

      const targetUrl = `${process.env.NEXT_PUBLIC_BASE_URL}/cartentry/delete?cartId=${encodeURIComponent(selectedCart.identifier)}&product=${encodeURIComponent(productName)}`;

      await fetch(targetUrl, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      const listResponse = await fetch(`${process.env.NEXT_PUBLIC_BASE_URL}/cart/list`, {
        method: "POST",
        headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
        body: JSON.stringify({ page: 0, sizePerPage: 100 }),
      });
      const text = await listResponse.text();
      const rawData = text ? JSON.parse(text) : {};
      const freshCarts = Array.isArray(rawData) ? rawData : rawData.dtoList || rawData.content || [];
      const updatedMatch = freshCarts.find((c) => String(c.identifier) === String(selectedCart.identifier));
      const transientEntries = updatedMatch?.cartEntryDtoList || updatedMatch?.cartEntryDtoLines || [];

      await syncCartTotalsToDatabase(selectedCart.identifier, transientEntries);
      await refreshCartWorkspace();
      setFeedbackSuccess("Removed entry row assignment.");
    } catch (err) {
      console.error("Delete line item execution error:", err);
      setFeedbackError("Failed to remove item from cart.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleClearEntireCart = async () => {
    if (!selectedCart) return;
    try {
      setActionLoading(true); setFeedbackError(""); setFeedbackSuccess("");
      const token = localStorage.getItem("token");

      const targetUrl = `${process.env.NEXT_PUBLIC_BASE_URL}/cartentry/clearCart?cartId=${encodeURIComponent(selectedCart.identifier)}`;

      const response = await fetch(targetUrl, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      if (response.ok) {
        await syncCartTotalsToDatabase(selectedCart.identifier, []);
        await refreshCartWorkspace();
        setFeedbackSuccess("Entire workspace entry queue cleared successfully.");
      } else {
        setFeedbackError("Failed clearing transaction row database registers.");
      }
    } catch (err) {
      console.error(err);
      setFeedbackError("Something went wrong while clearing the cart.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleAddCustomerSubmit = async (e) => {
    e.preventDefault();
    const cleanPhone = String(modalPhone || "").replaceAll(/\D/g, "");
    if (!modalName.trim() || cleanPhone.length !== 10) {
      setFeedbackError("Please provide a valid name and a 10-digit mobile number.");
      return;
    }
    try {
      setActionLoading(true); setFeedbackError(""); setFeedbackSuccess("");
      await CommonAddFetch(`${process.env.NEXT_PUBLIC_BASE_URL}/customer/add`, {
        identifier: modalEmail.trim() || cleanPhone, name: modalName.trim(), phoneNo: cleanPhone, userType: modalUserType, balance: 0, creditLimit: 0, status: true,
        shippingAddress: { phoneNo: cleanPhone, name: modalName.trim() }, billingAddress: { phoneNo: cleanPhone, name: modalName.trim() }
      }, "application/json");

      setCustomerPhone(cleanPhone); setIsModalOpen(false);
      setModalName(""); setModalEmail(""); setModalPhone(""); setModalUserType("customer");
      setFeedbackSuccess("Customer database listing registered successfully.");
      await refreshCartWorkspace();
      setTimeout(() => { handleInitializeCart(); }, 300);
    } catch (error) {
      console.error(error);
      setFeedbackError("Could not submit customer profile registry payload.");
    } finally {
      setActionLoading(false);
    }
  };

  const handleProceedToPayment = () => {
    if (currentEntries.length === 0) return;
    setAmountReceived(ledgerTotalPayable.toFixed(2));
    setPaymentSuccess(false);
    setCompletedOrderReceipt(null);
    setIsPaymentModalOpen(true);
  };

  const handleConfirmPaymentSubmit = async (e) => {
    e.preventDefault();
    try {
      setActionLoading(true);
      const token = localStorage.getItem("token");
      const orderId = `ORD-${Date.now()}`;

      const mappedOrderEntries = currentEntries.map((entry, idx) => ({
        identifier: `OE-${Date.now()}-${idx}`,
        ordersId: orderId,
        productId: entry.product,
        productName: allProductsList.find(p => String(p.identifier) === String(entry.product))?.name || entry.product,
        quantity: Number(entry.quantity || 1),
        unitPrice: Number(entry.unitPrice || 0),
        discount: Number(entry.discount || 0),
        totalPrice: Number(entry.totalPrice || 0)
      }));

      const localDate = new Date();
      const offsetMinutes = localDate.getTimezoneOffset();
      const adjustedDate = new Date(localDate.getTime() - (offsetMinutes * 60000));
      
      const orderPayload = {
        identifier: orderId,
        customerId: selectedCustomer?.identifier || selectedCart.identifier,
        customerName: selectedCustomer?.name || "Walk-In Guest",
        customerPhone: selectedCustomer?.phoneNo || selectedCart.identifier,
        totalOriginalPrice: ledgerOriginalSubtotal,
        discount: ledgerDiscount,
        totalPrice: ledgerTotalPayable,
        paymentType: paymentMethod,
        couponCode: null,
        orderDate: adjustedDate.toISOString().split('.')[0],
        status: "PAID",
        ordersEntryDtoList: mappedOrderEntries
      };

      await CommonAddFetch(`${process.env.NEXT_PUBLIC_BASE_URL}/orders/add`, orderPayload, "application/json");
      const clearCartUrl = `${process.env.NEXT_PUBLIC_BASE_URL}/cartentry/clearCart?cartId=${encodeURIComponent(selectedCart.identifier)}`;
      await fetch(clearCartUrl, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      await syncCartTotalsToDatabase(selectedCart.identifier, []);
      setCompletedOrderReceipt(orderPayload);
      setPaymentSuccess(true);
    } catch (err) {
      console.error(err);
      alert("Error logging dynamic order records.");
    } finally {
      setActionLoading(false);
    }
  };

  const handlePrintReceipt = () => {
    globalThis.print();
  };

  const handleClosePaymentFlow = () => {
    setIsPaymentModalOpen(false);
    setSelectedCart(null);
    setSelectedCustomer(null);
    refreshCartWorkspace();
    router.push("/orders");
  };

  const currentEntries = selectedCart?.cartEntryDtoList || selectedCart?.cartEntryDtoLines || [];
  const ledgerOriginalSubtotal = currentEntries.reduce((acc, curr) => acc + Number.parseFloat(curr.totalOriginalPrice || curr.total_original_price || 0), 0);
  const ledgerDiscount = currentEntries.reduce((acc, curr) => acc + Number.parseFloat(curr.discount || 0), 0);
  const ledgerTotalPayable = currentEntries.reduce((acc, curr) => acc + Number.parseFloat(curr.totalPrice || curr.total_price || 0), 0);
  const balanceChange = Number.parseFloat(amountReceived || 0) - ledgerTotalPayable;

  return (
    <div className="min-h-screen bg-[#f4f5fa] p-10 font-sans antialiased text-slate-900 print:p-0 print:bg-white">

      <style>{`
        @media print {
          body * { visibility: hidden; }
          .print-receipt-window, .print-receipt-window * { visibility: visible; }
          .print-receipt-window { position: absolute; left: 0; top: 0; width: 100%; max-width: 80mm; padding: 10mm; background: white; margin: 0 auto; box-shadow: none; border: none; }
          .print-no-render { display: none !important; }
        }
      `}</style>

      <div className="bg-white border border-zinc-200 rounded-2xl p-8 mb-6 shadow-sm print:hidden">
        <h1 className="text-2xl font-bold tracking-tight text-zinc-900 mb-1">Cart Operations Workspace</h1>
        <p className="text-sm text-zinc-400 mb-6">Search items, assign customers, and process bills instantly.</p>

        <form onSubmit={handleInitializeCart} className="flex gap-4 items-end relative">
          <div className="flex flex-col gap-1.5 flex-1">
            <label htmlFor="customer-phone-input" className="text-xs font-semibold tracking-wide text-[#4b4b75]">Customer Mobile Number</label>
            <div className="relative w-full">
              <PhoneIcon className="absolute left-4 top-1/2 -translate-y-1/2 w-[18px] h-[18px] text-[#b0b0c8]" />
              <input
                id="customer-phone-input"
                type="text"
                value={customerPhone || ""}
                onChange={(e) => setCustomerPhone(e.target.value)}
                placeholder="Enter 10-digit mobile number"
                className="w-full h-11 bg-[#f8f8fc] border border-[#ebebf5] rounded-xl pl-11 pr-4 text-sm text-[#2d2d6e] outline-none focus:border-[#6c63ff] focus:bg-white transition-all"
              />
              {foundCustomers.length > 0 && (
                <div className="absolute top-12 left-0 w-full bg-white border border-[#ebebf5] rounded-xl shadow-lg z-50 max-h-60 overflow-y-auto">
                  {foundCustomers.map((user) => (
                    <button
                      type="button"
                      key={user.id || user.username || user.identifier}
                      onClick={() => handleSelectCustomer(user)}
                      className="w-full text-left p-4 text-sm text-[#2d2d6e] hover:bg-[#f4f5fa] cursor-pointer flex justify-between items-center border-b border-[#ebebf5] last:border-none"
                    >
                      <div>
                        <div className="font-semibold">{user.name}</div>
                        <span className="text-xs text-zinc-400">Phone: {user.phoneNo || user.phone || user.phone_no} | Email: {user.identifier || user.username}</span>
                      </div>
                      <PlusIcon className="w-4 h-4 text-[#6c63ff]" />
                    </button>
                  ))}
                </div>
              )}
            </div>
          </div>
          <div className="flex gap-3">
            <button type="button" onClick={() => setIsModalOpen(true)} className="h-11 px-5 bg-[#111827] hover:bg-[#1f2937] text-white rounded-xl text-sm font-medium flex items-center gap-2 transition-all">
              <UserPlusIcon className="w-5 h-5" /> Add Customer
            </button>
            <button type="submit" disabled={actionLoading} className="h-11 px-5 bg-[#6c63ff] hover:bg-[#5850ec] text-white rounded-xl text-sm font-medium flex items-center gap-2 transition-all disabled:opacity-60 disabled:cursor-not-allowed">
              <ShoppingCartIcon className="w-5 h-5" /> Load / Create Checkout
            </button>
          </div>
        </form>
      </div>

      {feedbackError && <div className="p-3 bg-[#fff2f2] border border-[#ffd6d6] text-[#e55555] rounded-lg text-sm mb-5 text-center font-medium print:hidden">{feedbackError}</div>}
      {feedbackSuccess && <div className="p-3 bg-[#f2fdf5] border border-[#d3f9df] text-[#10b981] rounded-lg text-sm mb-5 text-center font-medium print:hidden">{feedbackSuccess}</div>}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start print:hidden">
        <div className="lg:col-span-2 flex flex-col gap-4">
          <div className="bg-white border border-[#ebebf5] rounded-2xl p-8 min-h-[400px] shadow-sm">
            <h2 className="text-lg font-semibold text-[#2d2d6e] mb-5">
              Active Terminal Entries {selectedCart && <span className="text-[#6c63ff] font-mono text-sm ml-1">({selectedCart.identifier})</span>}
            </h2>

            {currentEntries.length === 0 ? (
              <div className="text-center py-24 text-sm text-[#8888a0]">No products added to this transaction row yet.</div>
            ) : (
              <div className="space-y-4">
                {currentEntries.map((item) => {
                  const referenceMatch = allProductsList.find(p => String(p.identifier) === String(item.product));
                  const resolvedProductName = referenceMatch?.name || item.product;

                  return (
                    <div key={item.identifier || item.id} className="flex items-center justify-between p-5 border border-[#ebebf5] rounded-2xl bg-white shadow-sm hover:shadow-md transition-all">
                      <div className="flex items-center gap-5 flex-1">
                        <div className="w-20 h-20 rounded-xl bg-[#f8f8fc] flex items-center justify-center text-3xl shadow-inner">🛒</div>
                        <div className="flex-1">
                          <h3 className="text-base font-semibold text-[#2d2d6e]">{resolvedProductName}</h3>
                          {referenceMatch?.name && <div className="text-xs text-zinc-400 mt-0.5">Code: {item.product}</div>}
                          <div className="text-xs text-[#8888a0] mt-1">Unit Price: ₹ {Number.parseFloat(item.unitPrice || item.unit_price || 0).toFixed(2)}</div>
                          <div className="flex items-center gap-3 mt-3">
                            <button type="button" onClick={() => { if (Number(item.quantity) <= 1) { handleRemoveLineItem(item.product); } else { handleAddLineItem(item.product, -1); } }} className="w-7 h-7 rounded-md bg-[#f8f8fc] hover:bg-white border border-[#ebebf5] text-[#4b4b75] hover:border-[#6c63ff] hover:text-[#6c63ff] flex items-center justify-center transition-all">
                              <MinusIcon className="w-4 h-4" />
                            </button>
                            <span className="min-w-[24px] text-center font-semibold text-base text-[#2d2d6e]">{Number.parseInt(item.quantity || 0, 10)}</span>
                            <button type="button" onClick={() => handleAddLineItem(item.product, 1)} className="w-7 h-7 rounded-md bg-[#f8f8fc] hover:bg-white border border-[#ebebf5] text-[#4b4b75] hover:border-[#6c63ff] hover:text-[#6c63ff] flex items-center justify-center transition-all">
                              <PlusIcon className="w-4 h-4" />
                            </button>
                            <button type="button" onClick={() => handleRemoveLineItem(item.product)} className="text-sm text-[#8888a0] hover:text-[#e55555] bg-transparent border-none ml-5 flex items-center gap-1.5 transition-all">
                              <TrashIcon className="w-5 h-5" /> Remove
                            </button>
                          </div>
                        </div>
                      </div>
                      <div className="text-right">
                        <div className="text-sm text-[#10b981] mb-1.5">Discount ₹ {Number.parseFloat(item.discount || 0).toFixed(2)}</div>
                        <div className="text-2xl font-bold text-[#2d2d6e]">₹ {Number.parseFloat(item.totalPrice || item.total_price || 0).toFixed(2)}</div>
                        <div className="text-xs text-[#8888a0] mt-1">Original ₹ {Number.parseFloat(item.totalOriginalPrice || item.total_original_price || 0).toFixed(2)}</div>
                      </div>
                    </div>
                  );
                })}
                <div className="pt-4 border-t border-[#ebebf5] flex justify-start">
                  <button type="button" disabled={actionLoading || !selectedCart} onClick={handleClearEntireCart} className="h-11 px-6 bg-[#e55555] hover:bg-[#d44444] text-white rounded-lg text-sm font-medium flex items-center gap-2 transition-all disabled:opacity-60 disabled:cursor-not-allowed">
                    <XMarkIcon className="w-5 h-5" /> Clear Cart
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>

        <div className="space-y-6">
          <div className="bg-white border border-[#ebebf5] rounded-2xl p-6 shadow-sm">
            <h2 className="text-base font-semibold text-[#2d2d6e] mb-5">Transaction Ledger</h2>
            <div className="mb-6 relative">
              <div className="flex gap-2">
                <input type="text" className="flex-1 h-11 bg-[#f8f8fc] border border-[#ebebf5] rounded-xl px-4 text-sm text-[#2d2d6e] outline-none focus:border-[#6c63ff] focus:bg-white transition-all disabled:opacity-60" placeholder="Enter product ID" value={productSearch || ""} onChange={(e) => setProductSearch(e.target.value)} disabled={!selectedCart} />
                <button type="button" className="h-11 px-4 bg-[#6c63ff] hover:bg-[#5850ec] text-white rounded-xl text-sm font-medium transition-all disabled:opacity-60" onClick={() => { if (productSearch.trim()) { handleAddLineItem(productSearch.trim(), 1); } }} disabled={!selectedCart || actionLoading}>Apply</button>
              </div>
              {foundProducts.length > 0 && (
                <div className="absolute top-12 left-0 w-full bg-white border border-[#ebebf5] rounded-xl shadow-lg z-50 max-h-48 overflow-y-auto">
                  {foundProducts.map((prod) => (
                    <button
                      type="button"
                      key={prod.identifier || prod.id}
                      onClick={() => handleAddLineItem(prod.identifier)}
                      className="w-full text-left p-3 text-sm text-[#2d2d6e] hover:bg-[#f4f5fa] cursor-pointer flex justify-between items-center border-b border-[#ebebf5] last:border-none"
                    >
                      <div>
                        <div className="font-semibold">{prod.name || prod.identifier}</div>
                        <span className="text-xs text-zinc-400">Identifier: {prod.identifier}</span>
                      </div>
                      <PlusIcon className="w-4 h-4 text-[#6c63ff]" />
                    </button>
                  ))}
                </div>
              )}
            </div>

            <div className="bg-[#f8f8fc] border border-[#ebebf5] rounded-xl p-6 space-y-4 mb-6">
              <div className="flex justify-between text-sm text-[#4b4b75]"><span>Original Subtotal</span><span>₹ {ledgerOriginalSubtotal.toFixed(2)}</span></div>
              <div className="flex justify-between text-sm text-[#10b981]"><span>Campaign Discounts</span><span>- ₹ {ledgerDiscount.toFixed(2)}</span></div>
              <div className="flex justify-between text-sm text-[#4b4b75]"><span>Tax Allocation (0%)</span><span>₹ 0.00</span></div>
              <div className="flex justify-between items-center pt-3.5 border-t border-dashed border-[#ebebf5] text-lg font-semibold text-[#2d2d6e]"><span>Total Payable</span><span>₹ {ledgerTotalPayable.toFixed(2)}</span></div>
            </div>

            <button type="button" disabled={currentEntries.length === 0} onClick={handleProceedToPayment} className="w-full h-11 bg-[#111827] hover:bg-[#1f2937] text-white rounded-xl text-sm font-medium flex items-center justify-center gap-2 transition-all disabled:opacity-60 disabled:cursor-not-allowed shadow-sm">
              <CreditCardIcon className="w-5 h-5" /> Proceed to Payment
            </button>
          </div>
        </div>
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-[1000] print:hidden">
          <div className="bg-white border border-zinc-200 rounded-2xl w-full max-w-sm p-6 shadow-xl">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-base font-bold text-[#2d2d6e]">Add New Customer</h3>
              <button type="button" onClick={() => setIsModalOpen(false)} className="text-zinc-400 hover:text-zinc-600 transition-all"><XMarkIcon className="w-5 h-5" /></button>
            </div>
            <form onSubmit={handleAddCustomerSubmit} className="flex flex-col gap-3">
              <input type="text" placeholder="Full Name" value={modalName || ""} onChange={(e) => setModalName(e.target.value)} className="w-full h-[38px] border border-zinc-300 rounded-md px-3 text-sm text-zinc-900 outline-none focus:border-[#6c63ff]" required />
              <input type="email" placeholder="Email Address" value={modalEmail || ""} onChange={(e) => setModalEmail(e.target.value)} className="w-full h-[38px] border border-zinc-300 rounded-md px-3 text-sm text-zinc-900 outline-none focus:border-[#6c63ff]" />
              <input type="text" placeholder="10-Digit Phone Number" value={modalPhone || ""} onChange={(e) => setModalPhone(e.target.value)} className="w-full h-[38px] border border-zinc-300 rounded-md px-3 text-sm text-zinc-900 outline-none focus:border-[#6c63ff]" required />
              <select value={modalUserType || "customer"} onChange={(e) => setModalUserType(e.target.value)} className="w-full h-[38px] border border-zinc-300 rounded-md px-3 text-sm text-zinc-900 bg-white outline-none cursor-pointer focus:border-[#6c63ff]">
                <option value="customer">Customer</option>
                <option value="premium">Premium User</option>
                <option value="admin">Admin</option>
              </select>
              <button type="submit" disabled={actionLoading} className="w-full h-11 bg-[#6c63ff] hover:bg-[#5850ec] text-white rounded-lg text-sm font-semibold flex items-center justify-center transition-all mt-2 disabled:opacity-60">
                Save Profile
              </button>
            </form>
          </div>
        </div>
      )}

      {isPaymentModalOpen && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-xs flex items-center justify-center z-[2000]">
          <div className="bg-white border border-zinc-200 rounded-2xl w-full max-w-md p-6 shadow-2xl relative max-h-[90vh] overflow-y-auto no-scrollbar print-receipt-window">
            {paymentSuccess === false ? (
              <div className="print-no-render">
                <div className="flex justify-between items-center mb-5">
                  <h3 className="text-lg font-bold text-[#2d2d6e]">Process Cart Settlement</h3>
                  <button type="button" onClick={() => setIsPaymentModalOpen(false)} className="text-zinc-400 hover:text-zinc-600 transition-all"><XMarkIcon className="w-5 h-5" /></button>
                </div>

                <form onSubmit={handleConfirmPaymentSubmit} className="space-y-4">
                  <div className="bg-[#f8f8fc] border border-[#ebebf5] rounded-xl p-4 flex justify-between items-center">
                    <span className="text-sm font-semibold text-[#4b4b75]">Total Amount Due</span>
                    <span className="text-xl font-bold text-[#2d2d6e] font-mono">₹ {ledgerTotalPayable.toFixed(2)}</span>
                  </div>

                  <div className="flex flex-col gap-1.5">
                    <div className="text-xs font-semibold tracking-wide text-[#4b4b75]">Payment Channel</div>
                    <div className="grid grid-cols-3 gap-2">
                      {["CASH", "UPI", "CARD"].map((method) => (
                        <button
                          type="button"
                          key={method}
                          onClick={() => setPaymentMethod(method)}
                          className={`h-11 rounded-xl text-xs font-bold border transition-all cursor-pointer ${paymentMethod === method
                            ? "bg-[#6c63ff] border-[#6c63ff] text-white shadow-sm"
                            : "bg-[#f8f8fc] border-[#ebebf5] text-[#2d2d6e] hover:border-[#6c63ff]"
                            }`}
                        >
                          {method}
                        </button>
                      ))}
                    </div>
                  </div>

                  <div className="flex flex-col gap-1.5">
                    <label htmlFor="payment-amount-input" className="text-xs font-semibold tracking-wide text-[#4b4b75]">Amount Received</label>
                    <input
                      id="payment-amount-input"
                      type="number"
                      step="0.01"
                      value={amountReceived || ""}
                      onChange={(e) => setAmountReceived(e.target.value)}
                      className="w-full h-11 bg-[#f8f8fc] border border-[#ebebf5] rounded-xl px-4 text-sm text-[#2d2d6e] font-mono outline-none focus:border-[#6c63ff] focus:bg-white transition-all"
                      required
                    />
                  </div>

                  {paymentMethod === "CASH" && (
                    <div className="flex justify-between items-center text-xs px-1">
                      <span className="font-medium text-[#8888a0]">Cash Balance Change:</span>
                      <span className={`font-mono font-bold text-sm ${balanceChange >= 0 ? "text-emerald-600" : "text-[#e55555]"}`}>
                        ₹ {balanceChange.toFixed(2)}
                      </span>
                    </div>
                  )}

                  <button
                    type="submit"
                    disabled={actionLoading || balanceChange < 0}
                    className="w-full h-11 bg-[#111827] hover:bg-[#1f2937] text-white rounded-xl text-sm font-bold flex items-center justify-center gap-2 transition-all shadow-sm disabled:opacity-40 disabled:cursor-not-allowed pt-1"
                  >
                    Confirm Order Settlement
                  </button>
                </form>
              </div>
            ) : (
              <div className="flex flex-col gap-5 py-4">
                <div className="text-center space-y-2 print-no-render">
                  <CheckCircleIcon className="w-12 h-12 text-[#10b981] mx-auto animate-pulse" />
                  <h3 className="text-lg font-bold text-[#2d2d6e]">Payment Successful</h3>
                  <p className="text-xs text-zinc-400">Transaction logged into historical database archives.</p>
                </div>

                <div className="border border-[#ebebf5] bg-[#f8f8fc] rounded-2xl p-5 space-y-4 text-xs text-zinc-600 print:border-none print:bg-white print:p-0 print:text-black">
                  <div className="flex justify-between pb-2 border-b border-zinc-200/60 font-mono text-[11px] print:border-neutral-400">
                    <span>Receipt: {completedOrderReceipt?.identifier}</span>
                    <span>Method: {completedOrderReceipt?.paymentType}</span>
                  </div>

                  <div className="space-y-1">
                    <p><span className="font-semibold text-zinc-500 print:text-black">Customer:</span> <span className="text-zinc-900 font-medium print:text-black capitalize">{completedOrderReceipt?.customerName}</span></p>
                    <p><span className="font-semibold text-zinc-500 print:text-black">Phone:</span> <span className="text-zinc-900 font-mono print:text-black">{completedOrderReceipt?.customerPhone}</span></p>
                    <p><span className="font-semibold text-zinc-500 print:text-black">Date:</span> <span className="text-zinc-900 print:text-black">{completedOrderReceipt?.orderDate ? new Date(completedOrderReceipt.orderDate).toLocaleString() : ""}</span></p>
                  </div>

                  <div className="border-t border-b border-dashed border-zinc-200 py-3 space-y-2 print:border-neutral-400">
                    {completedOrderReceipt?.ordersEntryDtoList?.map((item) => (
                      <div key={item.identifier} className="flex justify-between text-zinc-800 print:text-black">
                        <span className="truncate max-w-[200px] capitalize print:max-w-none">{item.productName} (x{item.quantity})</span>
                        <span className="font-mono">₹{Number.parseFloat(item.totalPrice || 0).toFixed(2)}</span>
                      </div>
                    ))}
                  </div>

                  <div className="space-y-2 text-right">
                    <div className="flex justify-between text-[11px]"><span>Gross Subtotal:</span><span className="font-mono text-zinc-900 print:text-black">₹{Number.parseFloat(completedOrderReceipt?.totalOriginalPrice || 0).toFixed(2)}</span></div>
                    <div className="flex justify-between text-[11px] text-emerald-600 print:text-black"><span>Discounts:</span><span className="font-mono">-₹{Number.parseFloat(completedOrderReceipt?.discount || 0).toFixed(2)}</span></div>
                    <div className="flex justify-between items-center text-sm font-bold text-zinc-900 pt-2 border-t border-zinc-200/60 print:border-neutral-400 print:text-black"><span>Amount Settled:</span><span className="font-mono text-base font-extrabold text-[#2d2d6e] print:text-black">₹{Number.parseFloat(completedOrderReceipt?.totalPrice || 0).toFixed(2)}</span></div>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3 mt-1 print-no-render">
                  <button
                    type="button"
                    onClick={handlePrintReceipt}
                    className="h-11 border border-zinc-200 hover:bg-zinc-50 text-[#2d2d6e] rounded-xl text-xs font-bold flex items-center justify-center gap-2 transition-all cursor-pointer"
                  >
                    <PrinterIcon className="w-4 h-4" /> Print Receipt
                  </button>
                  <button
                    type="button"
                    onClick={handleClosePaymentFlow}
                    className="h-11 bg-[#6c63ff] hover:bg-[#5850ec] text-white rounded-xl text-xs font-bold flex items-center justify-center gap-2 transition-all cursor-pointer shadow-sm shadow-[#6c63ff]/10"
                  >
                    View Orders <ArrowRightIcon className="w-4 h-4" />
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}