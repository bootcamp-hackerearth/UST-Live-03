'use client';

import { useEffect, useState, useMemo } from 'react';
import { useRouter } from 'next/navigation';
import Layout from '../components/Layout';

const safeFetch = async (url, options = {}) => {
  try {
    const token = (globalThis.window == "undefined") ? null : localStorage.getItem("token");
    const res = await fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
        Authorization: token ? `Bearer ${token}` : ''
      }
    });

    if (!res.ok) {
      console.warn(`Server responded with status: ${res.status}`);
      return null;
    }

    const text = await res.text();
    if (!text) return null;
    return JSON.parse(text);
  } catch (err) {
    console.error("Network or parsing error encountered:", err);
    return null;
  }
};

export default function POSPage() {
  const router = useRouter();
  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [warehouses, setWarehouses] = useState([]);
  const [selectedWarehouse, setSelectedWarehouse] = useState('');
  const [cart, setCart] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState('');
  const [username, setUsername] = useState('');
  const [customerSearchInput, setCustomerSearchInput] = useState('');
  const [search, setSearch] = useState('');
  const [currentTime, setCurrentTime] = useState('');
  const [showNewCustomerModal, setShowNewCustomerModal] = useState(false);
  const [newCustomerName, setNewCustomerName] = useState('');
  const [newCustomerPhone, setNewCustomerPhone] = useState('');
  const [receivedAmount, setReceivedAmount] = useState('0.00');
  const [paymentType, setPaymentType] = useState('Cash');
  const [note, setNote] = useState('');
  const [toast, setToast] = useState({ show: false, message: '' });
  const [lastSavedOrder, setLastSavedOrder] = useState(null);
  const [isProcessingPayment, setIsProcessingPayment] = useState(false);
  const triggerToast = (msg) => {
    setToast({ show: true, message: msg });
    setTimeout(() => setToast({ show: false, message: '' }), 4000);
  };

  useEffect(() => {
    const storedUsername = localStorage.getItem('username') || '';
    setUsername(storedUsername);
  }, []);

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      setCurrentTime(now.toLocaleString());
    };
    updateTime();
    const interval = setInterval(updateTime, 1000);
    return () => clearInterval(interval);
  }, []);

  const generatedInvoiceCode = useMemo(() => {
    if (!selectedCustomer) return "Pending Assignment";
    const cleanId = selectedCustomer.replaceAll(/[^a-zA-Z0-9]/g, '');
    return `INV-${cleanId}-${Date.now().toString().slice(-4)}`;
  }, [selectedCustomer]);

  const enrichCartEntries = (entries, currentProducts) => {
    if (!entries) return [];
    return entries.map(entry => {
      const matchedProd = currentProducts.find(
        p => p.name === entry.product || (p.identifier || p.productIdentifier) === entry.product
      );
      return {
        ...entry,
        productName: matchedProd ? matchedProd.name : entry.product,
        basePrice: matchedProd?.price || entry.price || 0,
        sellingPrice: entry.sellingPrice || matchedProd?.sellingPrice || entry.price || 0
      };
    });
  };

  const fetchCurrentCart = async (customerId, currentProductsList = products) => {
    if (!customerId) return;

    const res = await safeFetch('http://localhost:8080/api/cart/list', {
      method: 'POST',
      body: JSON.stringify({ identifier: customerId })
    });

    setCart(enrichCartEntries(res?.entryList, currentProductsList));
  };

  const displayOriginalPrice = useMemo(() => {
    return cart.reduce((sum, item) => sum + (Number(item.basePrice || item.price || 0) * Number(item.quantity || 0)), 0);
  }, [cart]);

  const displayTotalPrice = useMemo(() => {
    return Math.max(0, cart.reduce((sum, item) => sum + (Number(item.sellingPrice || 0) * Number(item.quantity || 0)), 0));
  }, [cart]);

  const displayTotalDiscount = useMemo(() => {
    return Math.max(0, displayOriginalPrice - displayTotalPrice);
  }, [displayOriginalPrice, displayTotalPrice]);

  useEffect(() => {
    safeFetch('http://localhost:8080/api/warehouse/findByStatus')
      .then(data => {
        if (data && data.length > 0) {
          setWarehouses(data);
          setSelectedWarehouse(data[0].identifier || data[0].id);
        }
      });

    safeFetch('http://localhost:8080/api/product/findByStatus')
      .then(async (baseProducts) => {
        if (!baseProducts || baseProducts.length === 0) return;

        const decoratedProducts = await Promise.all(
          baseProducts.map(async (product) => {
            const priceLookupKey = product.name || product.identifier;
            const priceRes = await safeFetch(
              `http://localhost:8080/api/price/findByProduct?productIdentifier=${encodeURIComponent(priceLookupKey)}`
            );
            const verifiedPrice = priceRes?.selling_price || priceRes?.sellingPrice || 0;
            return { ...product, sellingPrice: verifiedPrice };
          })
        );
        setProducts(decoratedProducts);
      });
  }, []);

  useEffect(() => {
    if (!customerSearchInput.trim() || showNewCustomerModal) {
      setCustomers([]);
      return;
    }

    const delayDebounceFn = setTimeout(async () => {
      const isNumeric = /^\d+$/.test(customerSearchInput.trim());
      const searchCriteria = {
        customerName: isNumeric ? "" : customerSearchInput,
        phoneNo: isNumeric ? customerSearchInput : ""
      };

      const filteredResults = await safeFetch('http://localhost:8080/api/customer/search', {
        method: 'POST',
        body: JSON.stringify(searchCriteria)
      });

      const results = filteredResults || [];
      setCustomers(results);

      if (results.length === 1) {
        const targetId = results[0].phoneNo || results[0].identifier || results[0].id;
        if (targetId !== selectedCustomer) {
          setSelectedCustomer(targetId);
          fetchCurrentCart(targetId);
        }
      }
    }, 350);

    return () => clearTimeout(delayDebounceFn);
  }, [customerSearchInput, showNewCustomerModal]);

  const handleCustomerChange = async (customerId) => {
    setSelectedCustomer(customerId);
    setSearch('');
    if (!customerId) {
      setCart([]);
      return;
    }
    await fetchCurrentCart(customerId);
  };

  const handleCreateCustomer = async (e) => {
    e.preventDefault();

    const cleanPhone = newCustomerPhone.trim();
    if (!newCustomerName.trim() || !cleanPhone) {
      alert("Please fill in both fields.");
      return;
    }

    if (cleanPhone.length !== 10) {
      alert(`Phone number must be exactly 10 digits long. Current length: ${cleanPhone.length}`);
      return;
    }

    const payload = {
      customerName: newCustomerName.trim(),
      phoneNo: cleanPhone
    };

    try {
      const res = await fetch('http://localhost:8080/api/customer/add', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': localStorage.getItem('token') ? `Bearer ${localStorage.getItem('token')}` : ''
        },
        body: JSON.stringify(payload)
      });

      if (!res.ok) {
        const errorText = await res.text();
        console.error(`DB Save Rejected (${res.status}):`, errorText);
        alert(`Server error code encountered: ${res.status}`);
        return;
      }

      const responseData = await res.json();

      if (responseData?.success === false) {
        alert(`Backend Validation Rejected: ${responseData.message}`);
        return;
      }

      triggerToast(`Customer "${payload.customerName}" registered successfully!`);
      setCustomerSearchInput(payload.customerName);
      setSelectedCustomer(payload.phoneNo);
      setCart([]);
      setNewCustomerName('');
      setNewCustomerPhone('');
      setShowNewCustomerModal(false);
    } catch (error) {
      console.error("Save Connection processing error:", error);
      alert("Could not connect to the backend server initialization runtime.");
    }
  };

  const adjustQuantity = async (productId, currentItem, delta) => {
    const nextQuantity = currentItem.quantity + delta;
    if (nextQuantity <= 0) {
      await deleteItem(currentItem);
      return;
    }

    const targetProduct = products.find(p => (p.identifier || p.productIdentifier) === productId || p.name === productId);
    const databaseLookupName = targetProduct?.name || productId;

    const entryPayload = {
      product: databaseLookupName,
      cart: selectedCustomer,
      quantity: delta,
      price: currentItem.sellingPrice || currentItem.price || 0
    };

    await safeFetch('http://localhost:8080/api/cartEntry/addEntry', {
      method: 'POST',
      body: JSON.stringify(entryPayload)
    });
    await fetchCurrentCart(selectedCustomer);
  };

  const addProductFromGrid = async (productId) => {
    if (!selectedCustomer) {
      alert('Please select an active customer profile first.');
      return;
    }

    const targetProduct = products.find(p => (p.identifier || p.productIdentifier) === productId);
    const databaseLookupName = targetProduct?.name || productId;
    const existingItem = cart.find(item => item.product === databaseLookupName || item.product === productId);
    
    if (existingItem) {
      await adjustQuantity(databaseLookupName, existingItem, 1);
      return;
    }

    const databasePrice = targetProduct?.sellingPrice || 0;
    const entryPayload = {
      product: databaseLookupName,
      cart: selectedCustomer,
      quantity: 1,
      price: databasePrice
    };

    await safeFetch('http://localhost:8080/api/cartEntry/addEntry', {
      method: 'POST',
      body: JSON.stringify(entryPayload)
    });

    await fetchCurrentCart(selectedCustomer);
    triggerToast(`Added ${databaseLookupName} to cart`);
  };

  const deleteItem = async (item) => {
    await safeFetch('http://localhost:8080/api/cart/deleteEntry', {
      method: 'POST',
      body: JSON.stringify({ product: item.product, cart: selectedCustomer })
    });
    await fetchCurrentCart(selectedCustomer);
    triggerToast("Item removed.");
  };

  const dueAmount = useMemo(() => {
    const received = Number.parseFloat(receivedAmount) || 0;
    return Math.max(0, displayTotalPrice - received);
  }, [displayTotalPrice, receivedAmount]);

  const changeAmount = useMemo(() => {
    const received = Number.parseFloat(receivedAmount) || 0;
    return received > displayTotalPrice ? (received - displayTotalPrice).toFixed(2) : '0.00';
  }, [displayTotalPrice, receivedAmount]);

  const finalizeOrderCommitToDB = async () => {
    if (!selectedCustomer || cart.length === 0) return;

    if (paymentType === 'Cash' && Number.parseFloat(receivedAmount) < displayTotalPrice) {
      alert(`Received cash ($${receivedAmount}) must cover Total Payable amount.`);
      return;
    }

    const calculatedChange = paymentType === 'Cash' ? changeAmount : '0.00';
    const computedReceived = paymentType === 'Cash' ? receivedAmount : displayTotalPrice.toFixed(2);
    const computedDue = paymentType === 'Cash' ? dueAmount : 0;

    const orderPayload = {
      invoiceCode: generatedInvoiceCode,
      customerIdentifier: selectedCustomer,
      warehouseIdentifier: selectedWarehouse,
      originalPrice: Number.parseFloat(displayOriginalPrice || 0),
      totalDiscount: Number.parseFloat(displayTotalDiscount || 0),
      totalPrice: Number.parseFloat(displayTotalPrice || 0),
      amountReceived: Number.parseFloat(computedReceived),
      changeAmount: Number.parseFloat(calculatedChange),
      dueAmount: Number.parseFloat(computedDue),
      paymentMethod: paymentType,
      transactionNotes: note || "POS Sale",
      couponCode: "POS_SALE",
      timestamp: currentTime,
      entryList: cart.map(item => ({
        product: item.product,
        quantity: item.quantity,
        price: Number.parseFloat(item.basePrice || item.price || 0),
        sellingPrice: Number.parseFloat(item.sellingPrice || 0),
        discount: Number.parseFloat(item.basePrice || 0) - Number.parseFloat(item.sellingPrice || 0),
        totalPrice: Number.parseFloat(item.sellingPrice) * item.quantity,
        couponCode: "NONE"
      }))
    };

    try {
      await safeFetch('http://localhost:8080/api/orders/checkout', {
        method: 'POST',
        body: JSON.stringify(orderPayload)
      });

      await safeFetch('http://localhost:8080/api/cart/deleteCart', {
        method: 'POST',
        body: JSON.stringify({ identifier: selectedCustomer })
      });

      triggerToast('Order processed successfully!');
      setLastSavedOrder(orderPayload);

      setTimeout(() => {
        handlePrintReceipt(orderPayload);
        resetTerminalState();
      }, 500);
    } catch (error) {
      console.error("Error committing checkout order:", error);
      alert("Failed to write invoice data.");
    }
  };

  const handlePrintReceipt = (orderData) => {
    const targetOrder = orderData || lastSavedOrder;
    if (!targetOrder) return;

    const win = window.open('', '_blank');
    if (!win) return;

    const doc = win.document;
    doc.title = `Receipt - ${targetOrder.invoiceCode}`;

    const style = doc.createElement('style');
    style.textContent = `
      body { font-family: 'Courier New', Courier, monospace; padding: 20px; color: #000; font-size: 13px; max-width: 320px; margin: 0 auto; line-height: 1.4; }
      .center { text-align: center; }
      .right { text-align: right; }
      .bold { font-weight: bold; }
      .divider { border-top: 1px dashed #000; margin: 12px 0; }
      table { width: 100%; border-collapse: collapse; }
      td, th { padding: 4px 0; text-align: left; font-size: 13px; }
    `;
    doc.head.appendChild(style);

    const bodyContent = `
      <div class="center">
        <h3 style="margin:0 0 4px 0; letter-spacing:1px;">STORE RECEIPT</h3>
        <p style="margin:0; font-size:11px;">Invoice: ${targetOrder.invoiceCode}</p>
        <p style="margin:2px 0; font-size:11px;">${targetOrder.timestamp}</p>
      </div>
      <div class="divider"></div>
      <p style="margin:3px 0;"><strong>Customer:</strong> ${targetOrder.customerIdentifier}</p>
      <p style="margin:3px 0;"><strong>Warehouse:</strong> ${targetOrder.warehouseIdentifier}</p>
      <div class="divider"></div>
      <table>
        <thead>
          <tr class="bold">
            <th>Item</th>
            <th class="center">Qty</th>
            <th class="right">Total</th>
          </tr>
        </thead>
        <tbody>
          ${targetOrder.entryList.map(item => `
            <tr>
              <td>${item.product}</td>
              <td class="center">${item.quantity}</td>
              <td class="right">$${Number(item.totalPrice).toFixed(2)}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
      <div class="divider"></div>
      <table>
        <tr><td>Original Subtotal:</td><td class="right">$${Number(targetOrder.originalPrice).toFixed(2)}</td></tr>
        <tr><td>Total Discount:</td><td class="right">-$${Number(targetOrder.totalDiscount).toFixed(2)}</td></tr>
        <tr class="bold"><td>Total Payable:</td><td class="right">$${Number(targetOrder.totalPrice).toFixed(2)}</td></tr>
        <tr style="height: 8px;"><td></td><td></td></tr>
        <tr><td>Received:</td><td class="right">$${Number(targetOrder.amountReceived).toFixed(2)}</td></tr>
        <tr><td>Change:</td><td class="right">$${Number(targetOrder.changeAmount).toFixed(2)}</td></tr>
        <tr class="bold"><td>Balance Due:</td><td class="right">$${Number(targetOrder.dueAmount).toFixed(2)}</td></tr>
      </table>
      <div class="divider" style="margin-top:24px;"></div>
      <p class="center" style="font-size:11px;">Thank you for your business!</p>
    `;

    if (doc.body) {
      doc.body.innerHTML = bodyContent;
    } else {
      const body = doc.createElement('body');
      body.innerHTML = bodyContent;
      doc.documentElement.appendChild(body);
    }

    win.focus();
    win.print();
    setTimeout(() => win.close(), 400);
  };

  const resetTerminalState = () => {
    setCart([]);
    setReceivedAmount('0.00');
    setNote('');
    setSelectedCustomer('');
    setCustomerSearchInput('');
    setCustomers([]);
    setLastSavedOrder(null);
    setShowNewCustomerModal(false);
  };

  return (
    <Layout username={username}>
      <div className="min-h-screen bg-slate-50 text-slate-800 p-6 relative">
        {toast.show && (
          <div className="fixed top-6 right-6 z-50 bg-emerald-600 text-white text-sm font-semibold px-5 py-3 rounded-lg shadow-2xl flex items-center gap-2 transition-all animate-bounce">
            <span className="w-2 h-2 rounded-full bg-white animate-ping" /> {toast.message}
          </div>
        )}

        <div className="mb-6 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-white p-4 rounded-xl shadow-sm border border-slate-200">
          <div className="flex items-center gap-3">
            <div className="h-3 w-3 rounded-full bg-blue-600 animate-pulse" />
            <h1 className="text-lg font-bold tracking-tight text-slate-900">Point of Sale Terminal</h1>
          </div>
          <div className="flex gap-3">
            <button
              onClick={() => router.push('/orders')}
              className="bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold px-4 py-2 rounded-lg shadow-sm transition-all"
            >
              Order Management Registry
            </button>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
          <div className="lg:col-span-7 space-y-6">
            <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="flex flex-col gap-1">
                <label htmlFor="invoice-code" className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Invoice Code</label>
                <input id="invoice-code" type="text" readOnly value={generatedInvoiceCode} className="bg-slate-50 border border-slate-200 rounded-lg p-2 text-xs font-medium text-slate-500 focus:outline-none" />
              </div>

              <div className="flex flex-col gap-1">
                <label htmlFor="session-time" className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Session Time</label>
                <input id="session-time" type="text" readOnly value={currentTime} className="bg-slate-50 border border-slate-200 rounded-lg p-2 text-xs font-medium text-slate-500 focus:outline-none" />
              </div>

              <div className="flex flex-col gap-1">
                <label htmlFor="active-warehouse" className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Active Warehouse</label>
                <select
                  id="active-warehouse"
                  value={selectedWarehouse}
                  onChange={(e) => setSelectedWarehouse(e.target.value)}
                  className="border border-slate-200 rounded-lg p-2 text-xs bg-white font-medium text-slate-700 focus:border-blue-500 focus:outline-none"
                >
                  {warehouses.map((w) => (
                    <option key={w.identifier || w.id} value={w.identifier || w.id}>
                      {w.name || w.identifier}
                    </option>
                  ))}
                </select>
              </div>
              <div className="md:col-span-3 flex gap-2 items-center relative pt-1">
                <div className="flex-1 flex flex-col gap-1">
                  <div className="flex justify-between items-center">
                    <label htmlFor="customer-search" className="text-[11px] font-bold text-slate-500 uppercase tracking-wider">Link Customer Target</label>
                    <div className="flex items-center gap-2">
                      {selectedCustomer && (
                        <span className="text-[11px] font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded-md">
                          Linked Phone: {selectedCustomer}
                        </span>
                      )}
                      <button
                        type="button"
                        onClick={() => {
                          setShowNewCustomerModal(!showNewCustomerModal);
                          setCustomerSearchInput('');
                          setCustomers([]);
                        }}
                        className="text-[11px] font-bold text-blue-600 hover:text-blue-800 transition-colors underline"
                      >
                        {showNewCustomerModal ? "Back to Search" : "+ Add New Customer"}
                      </button>
                    </div>
                  </div>

                  {showNewCustomerModal ? (
                    <form onSubmit={handleCreateCustomer} className="mt-1 p-3 bg-slate-50 rounded-lg border border-slate-200 grid grid-cols-1 sm:grid-cols-12 gap-3 items-end">
                      <div className="sm:col-span-5 flex flex-col gap-1">
                        <label htmlFor="customer-name" className="text-[10px] font-bold text-slate-500 uppercase">Customer Name</label>
                        <input
                          id="customer-name"
                          type="text"
                          required
                          placeholder="e.g. John Doe"
                          value={newCustomerName}
                          onChange={(e) => setNewCustomerName(e.target.value)}
                          className="border border-slate-200 rounded px-2 py-1.5 text-xs bg-white text-slate-800 focus:outline-none focus:border-blue-500 font-medium"
                        />
                      </div>
                      <div className="sm:col-span-5 flex flex-col gap-1">
                        <label htmlFor="customer-phone" className="text-[10px] font-bold text-slate-500 uppercase">Phone Number (10 digits)</label>
                        <input
                          id="customer-phone"
                          type="text"
                          required
                          maxLength={10}
                          placeholder="e.g. 9876543210"
                          value={newCustomerPhone}
                          onChange={(e) => setNewCustomerPhone(e.target.value.replaceAll(/\D/g, ''))}
                          className="border border-slate-200 rounded px-2 py-1.5 text-xs bg-white text-slate-800 focus:outline-none focus:border-blue-500 font-medium"
                        />
                      </div>
                      <div className="sm:col-span-2">
                        <button
                          type="submit"
                          className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs py-2 rounded shadow transition-colors"
                        >
                          Save
                        </button>
                      </div>
                    </form>
                  ) : (
                    <input
                      id="customer-search"
                      type="text"
                      placeholder="Search client accounts by name or phone digits..."
                      className={`w-full border rounded-lg p-2.5 text-xs font-medium focus:outline-none transition-all ${selectedCustomer ? 'border-emerald-500 bg-emerald-50/30' : 'border-slate-200 bg-white focus:border-blue-500'}`}
                      value={customerSearchInput}
                      onChange={(e) => {
                        setCustomerSearchInput(e.target.value);
                        if (selectedCustomer) {
                          setSelectedCustomer('');
                          setCart([]);
                        }
                      }}
                    />
                  )}

                  {customerSearchInput.trim() && customers.length > 0 && !selectedCustomer && !showNewCustomerModal && (
                    <div className="absolute top-[100%] left-0 right-0 z-30 mt-1 bg-white rounded-lg border border-slate-200 shadow-xl max-h-48 overflow-y-auto divide-y divide-slate-100">
                      {customers.map((c) => {
                        const uniqueId = c.phoneNo || c.identifier || c.id;
                        const labelName = c.customerName || c.name || uniqueId;
                        return (
                          <button
                            key={uniqueId}
                            type="button"
                            onClick={() => {
                              setCustomerSearchInput(labelName);
                              handleCustomerChange(uniqueId);
                            }}
                            className="w-full text-left text-xs hover:bg-slate-50 text-slate-700 font-medium px-4 py-2.5 transition-colors"
                          >
                            👤 {labelName} <span className="text-slate-400 text-[11px]">({uniqueId})</span>
                          </button>
                        );
                      })}
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
              <div className="max-h-[340px] overflow-y-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="bg-slate-50 border-b border-slate-200 text-[10px] font-bold text-slate-500 uppercase tracking-wider">
                      <th className="py-3 px-4">Product Details</th>
                      <th className="py-3 px-3 text-right">Original Price</th>
                      <th className="py-3 px-3 text-right">Selling Price</th>
                      <th className="py-3 px-4 text-center">Quantity</th>
                      <th className="py-3 px-3 text-right">Subtotal</th>
                      <th className="py-3 px-3 text-center">Action</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-100 text-xs font-medium text-slate-700">
                    {cart.map((item) => {
                      const qty = Number(item.quantity || 0);
                      const base = Number(item.basePrice || item.price || 0);
                      const selling = Number(item.sellingPrice || 0);
                      const grossSubtotal = base * qty;

                      return (
                        <tr key={item.product} className="hover:bg-slate-50/80 transition-colors">
                          <td className="py-3 px-4 max-w-[180px]">
                            <span className="font-semibold text-slate-900 block truncate">{item.productName}</span>
                            <span className="text-[10px] text-slate-400 block truncate font-medium">SKU: {item.product}</span>
                          </td>
                          <td className="py-3 px-3 text-right text-slate-400 line-through">
                            ${base.toFixed(2)}
                          </td>
                          <td className="py-3 px-3 text-right font-semibold text-slate-800">
                            ${selling.toFixed(2)}
                          </td>
                          <td className="py-3 px-4">
                            <div className="flex items-center justify-center bg-slate-100 p-0.5 rounded border border-slate-200 max-w-[84px] mx-auto">
                              <button
                                type="button"
                                onClick={() => adjustQuantity(item.product, item, -1)}
                                className="w-5 h-5 flex items-center justify-center bg-white rounded shadow-sm text-slate-600 hover:bg-slate-200 font-bold text-xs"
                              >
                                -
                              </button>
                              <span className="w-6 text-center font-bold text-slate-800 text-[11px]">
                                {qty}
                              </span>
                              <button
                                type="button"
                                onClick={() => adjustQuantity(item.product, item, 1)}
                                className="w-5 h-5 flex items-center justify-center bg-white rounded shadow-sm text-slate-600 hover:bg-slate-200 font-bold text-xs"
                              >
                                +
                              </button>
                            </div>
                          </td>
                          <td className="py-3 px-3 text-right font-bold text-slate-900">
                            ${grossSubtotal.toFixed(2)}
                          </td>
                          <td className="py-3 px-3 text-center">
                            <button
                              type="button"
                              onClick={() => deleteItem(item)}
                              className="text-slate-400 hover:text-rose-600 transition-colors p-1"
                            >
                              ✕
                            </button>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>

              {cart.length === 0 && (
                <div className="py-12 text-center text-slate-400 text-xs font-medium border-t border-slate-100">
                  Active customer item configuration payload empty.
                </div>
              )}
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm space-y-4">
                <div className="flex items-center justify-between gap-4">
                  <label htmlFor="paymentMethod" className="text-xs font-semibold text-slate-600 uppercase tracking-wider">Method</label>
                  <select
                    id="paymentMethod"
                    value={paymentType}
                    onChange={(e) => setPaymentType(e.target.value)}
                    className="border border-slate-200 rounded-lg p-2 text-xs font-medium text-slate-700 bg-white focus:outline-none"
                  >
                    <option value="Cash">Cash Ledger</option>
                    <option value="Card">Card / Swipe</option>
                    <option value="UPI">UPI Router</option>
                  </select>
                </div>
                {paymentType === 'Cash' && (
                  <div className="space-y-3 pt-2 border-t border-slate-100">
                    <div className="flex items-center justify-between gap-4">
                      <label htmlFor="receivedAmount" className="text-xs font-semibold text-slate-600 uppercase tracking-wider">Amount Received</label>
                      <input
                        id="receivedAmount"
                        type="number"
                        step="0.01"
                        className="w-32 bg-slate-50 border border-slate-200 rounded-lg p-2 text-right font-semibold text-xs text-slate-800 focus:outline-none"
                        value={receivedAmount}
                        onChange={(e) => setReceivedAmount(e.target.value)}
                      />
                    </div>
                    <div className="flex justify-between text-xs font-medium text-slate-500">
                      <span>Change:</span>
                      <span className="text-slate-900 font-semibold">${changeAmount}</span>
                    </div>
                    <div className="flex justify-between text-xs font-medium text-slate-500">
                      <span>Balance Due:</span>
                      <span className="text-rose-600 font-bold">${dueAmount.toFixed(2)}</span>
                    </div>
                  </div>
                )}
              </div>

              <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm flex flex-col justify-between">
                <div className="space-y-2 text-xs font-medium text-slate-600">
                  <div className="flex justify-between">
                    <span>Subtotal Original:</span>
                    <span className="text-slate-500 line-through">${displayOriginalPrice.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span>Discount applied:</span>
                    <span className="text-emerald-600 font-semibold">-${displayTotalDiscount.toFixed(2)}</span>
                  </div>
                  <div className="flex justify-between border-t border-slate-100 pt-2 text-sm font-bold text-slate-900">
                    <span>Total Payable:</span>
                    <span>${displayTotalPrice.toFixed(2)}</span>
                  </div>
                </div>
                
                <button
                  onClick={() => {
                    if (paymentType === 'Card' || paymentType === 'UPI') {
                      setIsProcessingPayment(true);
                    } else {
                      finalizeOrderCommitToDB();
                    }
                  }}
                  disabled={cart.length === 0 || !selectedCustomer}
                  className="mt-4 w-full bg-emerald-600 hover:bg-emerald-700 disabled:bg-slate-200 disabled:text-slate-400 text-white font-bold py-2.5 rounded-lg text-xs shadow transition-colors uppercase tracking-wider"
                >
                  {paymentType === 'Cash' ? "Checkout and Print" : `Proceed via ${paymentType}`}
                </button>
              </div>
            </div>
          </div>

          <div className="lg:col-span-5 bg-white p-5 rounded-xl border border-slate-200 shadow-sm space-y-4">
            <div className="flex justify-between items-center border-b border-slate-100 pb-3">
              <h2 className="text-xs font-bold text-slate-500 uppercase tracking-wider">Product Inventory Catalog</h2>
              <input 
                type="text" 
                placeholder="Quick filter..." 
                value={search} 
                onChange={(e) => setSearch(e.target.value)}
                className="border border-slate-200 rounded-md p-1.5 text-xs font-medium max-w-[150px] focus:outline-none focus:border-blue-500" 
              />
            </div>
            
            <div className="grid grid-cols-2 gap-3 max-h-[580px] overflow-y-auto pr-1">
              {products
                .filter(p => p.name?.toLowerCase().includes(search.toLowerCase()))
                .map((product) => (
                  <button
                    key={product.identifier || product.id}
                    onClick={() => addProductFromGrid(product.identifier || product.productIdentifier || product.id)}
                    className="text-left border border-slate-200 rounded-lg p-3 hover:border-blue-500 hover:bg-blue-50/20 transition-all space-y-1 group relative"
                  >
                    <span className="text-xs font-semibold text-slate-800 block truncate group-hover:text-blue-700">{product.name}</span>
                    <div className="text-right text-xs pt-1">
                      <span className="text-slate-900 font-bold">${Number(product.sellingPrice || 0).toFixed(2)}</span>
                    </div>
                  </button>
                ))}
            </div>
          </div>
        </div>
      </div>
      {isProcessingPayment && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-xl shadow-2xl border border-slate-200 max-w-sm w-full p-6 text-center space-y-6 animate-in fade-in zoom-in-95 duration-150">
            <div>
              <h3 className="text-sm font-bold text-slate-900 uppercase tracking-wider mb-1">Electronic Gateway Terminal</h3>
              <p className="text-xs text-slate-500">Invoice Transaction Amount: <span className="font-bold text-slate-800">${displayTotalPrice.toFixed(2)}</span></p>
            </div>

            {paymentType === 'Card' ? (
              <div className="py-4 bg-slate-50 rounded-lg border border-slate-100 flex flex-col items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 animate-pulse text-lg">💳</div>
                <p className="text-xs font-semibold text-slate-700">Awaiting External Swipe/Insert...</p>
              </div>
            ) : (
              <div className="py-4 flex flex-col items-center justify-center gap-2">
                <img 
                  src={`https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=${encodeURIComponent('upi://pay?pa=merchant@upi&pn=StorePOS&am=' + displayTotalPrice.toFixed(2) + '&cu=USD')}`}
                  alt="UPI Payment QR Code Router"
                  className="w-40 h-40 border-2 border-slate-100 p-2 rounded-lg shadow-sm"
                />
                <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mt-1">Scan QR to Complete Router Link</p>
              </div>
            )}

            <div className="flex gap-3">
              <button
                onClick={() => setIsProcessingPayment(false)}
                className="flex-1 py-2 text-xs font-semibold text-slate-500 hover:bg-slate-100 rounded-lg transition-colors border border-slate-200"
              >
                Cancel
              </button>
              <button
                onClick={() => {
                  setIsProcessingPayment(false);
                  finalizeOrderCommitToDB();
                  setTimeout(() => {
                    globalThis.window.location.reload();
                  }, 1200);
                }}
                className="flex-1 py-2 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-700 rounded-lg shadow transition-colors uppercase tracking-wider"
              >
                Confirm & Print
              </button>
            </div>
          </div>
        </div>
      )}
    </Layout>
  );
}