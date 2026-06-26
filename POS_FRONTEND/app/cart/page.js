"use client";

import { useEffect, useState, useMemo } from "react";
import Layout from "../components/Layout";
import api from "../services/api";
import AddCustomerForm from "./AddCustomerForm";

export default function CartPage() {
  const [viewMode, setViewMode] = useState("billing"); 

  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [productMap, setProductMap] = useState({});
  const [priceMap, setPriceMap] = useState({});
  
  const [orders, setOrders] = useState([]);
  const [ordersLoading, setOrdersLoading] = useState(false);
  
  const [searchTerm, setSearchTerm] = useState("");
  const [customerSearch, setCustomerSearch] = useState("");
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const [customerIdentifier, setCustomerIdentifier] = useState("");
  const [cartId, setCartId] = useState("");
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [isCheckoutModalOpen, setIsCheckoutModalOpen] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("CASH"); 
  const [amountReceived, setAmountReceived] = useState("");
  const [processingOrder, setProcessingOrder] = useState(false);

  const [selectedOrder, setSelectedOrder] = useState(null);
  const [isViewOrderModalOpen, setIsViewOrderModalOpen] = useState(false);
  const [singleOrderLoading, setSingleOrderLoading] = useState(false);

  useEffect(() => {
    loadCustomers();
    loadProducts();
    loadPrices();
  }, []);

  useEffect(() => {
    if (viewMode === "orders") {
      loadOrders();
    }
  }, [viewMode]);

  const filteredProducts = useMemo(() => {
    const query = searchTerm.toLowerCase().trim();
    if (!query) return products;
    return products.filter(
      (product) =>
        product.identifier?.toLowerCase().includes(query) ||
        product.model?.toLowerCase().includes(query) ||
        product.brand?.toLowerCase().includes(query) ||
        product.category?.toLowerCase().includes(query)
    );
  }, [products, searchTerm]);

  const filteredCustomers = useMemo(() => {
    const query = customerSearch.toLowerCase().trim();
    if (!query) return customers;
    return customers.filter(
      (c) =>
        c.customerName?.toLowerCase().includes(query) ||
        c.phoneNo?.includes(query)
    );
  }, [customers, customerSearch]);

  const loadCustomers = async () => {
    try {
      const res = await api.post("/api/customer/list", {
        page: 0,
        sizePerPage: 200,
        sortField: "id",
        sortDirection: "ASC",
      });
      setCustomers(res.data.dtoList || []);
    } catch (err) {
      console.error("CUSTOMER ERROR =>", err);
    }
  };

  const loadProducts = async () => {
    try {
      const res = await api.post("/api/product/list", {
        page: 0,
        sizePerPage: 100,
        sortField: "id",
        sortDirection: "ASC",
      });

      const activeProducts = (res.data?.dtoList || []).filter(
        (p) => p.status === true
      );
      setProducts(activeProducts);

      const map = {};
      activeProducts.forEach((p) => { map[p.identifier] = p; });
      setProductMap(map);
    } catch (err) {
      console.error(err);
    }
  };

  const loadPrices = async () => {
    try {
      const res = await api.post("/api/price/list", {
        page: 0,
        sizePerPage: 500,
        sortField: "id",
        sortDirection: "ASC",
      });

      const map = {};
      (res.data.dtoList || []).forEach((price) => {
        if (price.type === "SELLING") {
          map[price.product] = price.priceAmount;
        }
      });
      setPriceMap(map);
    } catch (err) {
      console.error(err);
    }
  };

  const loadOrders = async () => {
    try {
      setOrdersLoading(true);
      const res = await api.post("/api/order/list", {
        page: 0,
        sizePerPage: 50,
        sortField: "id",
        sortDirection: "DESC", 
      });
      setOrders(res.data.dtoList || []);
    } catch (err) {
      console.error("ORDERS FETCH FAILURE =>", err);
    } finally {
      setOrdersLoading(false);
    }
  };

  const createCart = async () => {
    if (!customerIdentifier) {
      alert("Please choose or register a client instance first.");
      return;
    }

    try {
      setLoading(true);
      const selectedCustomer = customers.find(
        (c) => c.phoneNo === customerIdentifier || c.identifier === customerIdentifier
      );

      if (!selectedCustomer) {
        alert("Selected target customer properties are invalid.");
        return;
      }

      const identifier =
        "CART-" + selectedCustomer.customerName.replaceAll(/\s+/g, "-").toUpperCase() + "-" + Date.now().toString().slice(-4);

      await api.post("/api/cart/add", {
        identifier,
        customerIdentifier,
      });

      setCartId(identifier);
      await loadCart(identifier);
    } catch (err) {
      console.error(err);
      alert("Failed to create operational cart layout.");
    } finally {
      setLoading(false);
    }
  };

  const loadCart = async (identifier) => {
    try {
      const res = await api.get("/api/cart/get", { params: { identifier } });
      setCart(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const addToCart = async (productIdentifier) => {
    if (!cartId) {
      alert("Please instantiate or build a customer account session sequence active cart first.");
      return;
    }
    try {
      await api.post("/api/cartentry/add", {
        cartIdentifier: cartId,
        productIdentifier,
        quantity: 1,
      });
      await loadCart(cartId);
    } catch (err) {
      console.error(err);
    }
  };

  const updateQuantity = async (productIdentifier, qty) => {
    try {
      await api.post("/api/cartentry/add", {
        cartIdentifier: cartId,
        productIdentifier,
        quantity: qty,
      });
      await loadCart(cartId);
    } catch (err) {
      console.error(err);
    }
  };

  const removeProductFromCart = async (productIdentifier) => {
    if (!confirm("Are you sure you want to remove this item?")) return;
    try {
      await api.post("/api/cartentry/add", {
        cartIdentifier: cartId,
        productIdentifier,
        quantity: -1000,
      });
      await loadCart(cartId);
    } catch (err) {
      console.error(err);
    }
  };

  const clearCart = async () => {
    if (!cartId || !confirm("Are you sure you want to empty the active cart items?")) return;
    try {
      await api.post("/api/cart/clear", null, { params: { cartIdentifier: cartId } });
      await loadCart(cartId);
    } catch (err) {
      console.error(err);
    }
  };

  const handleCustomerSavedSuccessfully = async (phoneNo) => {
    await loadCustomers();
    setCustomerIdentifier(phoneNo);
    const savedCustomer = customers.find(c => c.phoneNo === phoneNo);
    setCustomerSearch(savedCustomer ? `${savedCustomer.customerName} (${phoneNo})` : phoneNo);
    setIsModalOpen(false);
  };

  const handleFinalizeOrder = async () => {
    const totalPayable = cart?.totalPrice || 0;
    
    if (paymentMethod === "CASH" && Number(amountReceived) < totalPayable) {
      alert("Insufficient cash amount received to fulfill payable gross total.");
      return;
    }

    try {
      setProcessingOrder(true);
      
      const orderPayload = {
        customerIdentifier: cartId, 
        originalPrice: cart?.originalPrice || 0,
        discount: cart?.discount || 0,
        totalPrice: totalPayable,
        paymentMethod: paymentMethod,
        receivedAmount: paymentMethod === "CASH" ? Number(amountReceived) : totalPayable
      };

      const response = await api.post("/api/order/checkout", orderPayload);
      
     if (response?.data?.success) 
   {
  alert(
    `Transaction Completed Successfully! Order Code: ${response.data.identifier}`
  );

  setIsCheckoutModalOpen(false);

  const orderRes = await api.get("/api/order/get", {
    params: {
      identifier: response.data.identifier,
    },
  });

  if (orderRes.data) {
    setSelectedOrder(orderRes.data);
    setIsViewOrderModalOpen(true);
  }

} else {
        alert(`Terminal Validation Response Issue: ${response.data?.message || "Check log data registries"}`);
      }
    } catch (err) {
      console.error("ORDER PROCESSING FAILURE =>", err);
      alert("Pipeline failure. Check API network logs.");
    } finally {
      setProcessingOrder(false);
    }
  };

  const handleOpenOrderDetails = async (order) => {
    try {
      setSingleOrderLoading(true);
      setSelectedOrder(order);
      setIsViewOrderModalOpen(true);
      const res = await api.get("/api/order/get", { 
        params: { identifier: order.identifier } 
      });
      
      if (res.data) {
        setSelectedOrder(res.data);
      }
    } catch (err) {
      console.error("Error retrieving deep context order models =>", err);
    } finally {
      setSingleOrderLoading(false);
    }
  };

  const handlePrintReceipt = () => {
  globalThis.print();
};
  const clearCompletedSale = () => {
  setCart(null);
  setCartId("");
  setCustomerIdentifier("");
  setCustomerSearch("");
  setAmountReceived("");
  setPaymentMethod("CASH");

  loadCustomers();
};

  const computedChange = useMemo(() => {
    const payable = cart?.totalPrice || 0;
    const received = Number(amountReceived) || 0;
    return received > payable ? (received - payable).toFixed(2) : "0.00";
  }, [amountReceived, cart?.totalPrice]);

  return (
    <Layout>
      
      <div className="space-y-6 max-w-[1600px] mx-auto p-2 no-print">
        
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center bg-white p-4 rounded-xl shadow-sm border border-gray-100 gap-4">
          <h1 className="text-2xl font-bold text-gray-900 tracking-tight flex items-center gap-2">
            <div className="flex items-center gap-2">
              <span className="h-2 w-2 rounded-full bg-blue-600" aria-hidden="true"></span>
              <span className="text-sm font-medium text-gray-700">Payment Verified</span>
            </div>
            POS Management Console
          </h1>
          
          <div className="flex bg-gray-100 p-1 rounded-xl border border-gray-200">
            <button
              onClick={() => setViewMode("billing")}
              className={`px-4 py-2 text-xs font-bold rounded-lg transition-all ${
                viewMode === "billing"
                  ? "bg-white text-blue-600 shadow-xs"
                  : "text-gray-500 hover:text-gray-900"
              }`}
            >
              🛒 Live POS Billing
            </button>
            <button
              onClick={() => setViewMode("orders")}
              className={`px-4 py-2 text-xs font-bold rounded-lg transition-all ${
                viewMode === "orders"
                  ? "bg-white text-blue-600 shadow-xs"
                  : "text-gray-500 hover:text-gray-900"
              }`}
            >
              📋 Order Summary Registers
            </button>
          </div>
        </div>

        {viewMode === "billing" && (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
            <div className="lg:col-span-7 space-y-6 w-full">
              <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5 relative">
                <div className="flex justify-between items-center mb-4">
                  <h2 className="font-semibold text-gray-800 text-sm tracking-wide uppercase">Customer Channel Identification</h2>
                  <button
                    type="button"
                    onClick={() => setIsModalOpen(true)}
                    className="bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold px-3 py-2 rounded-lg transition-all flex items-center gap-1 shadow-sm"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M12 4v16m8-8H4" />
                    </svg>
                     Add New Customer
                  </button>
                </div>

                <div className="space-y-3">
                  <div className="relative">
                    <input
                      type="text"
                      placeholder="Type to search name or contact digits..."
                      value={customerSearch}
                      onFocus={() => setIsDropdownOpen(true)}
                      onChange={(e) => {
                        setCustomerSearch(e.target.value);
                        setCustomerIdentifier(""); 
                        setIsDropdownOpen(true);
                      }}
                      className="w-full border border-gray-200 rounded-lg p-3 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 text-sm text-gray-800 transition-all"
                    />
                    {isDropdownOpen && (
                      <div className="absolute left-0 right-0 mt-1 max-h-60 overflow-y-auto bg-white border border-gray-200 rounded-lg shadow-xl z-30 divide-y divide-gray-50">
                        {filteredCustomers.length === 0 ? (
                          <div className="p-3 text-xs text-gray-400 text-center">No record files found matching entry</div>
                        ) : (
                          filteredCustomers.map((c) => (
                            <button
  key={c.identifier || c.phoneNo}
  type="button"
  onClick={() => {
    setCustomerIdentifier(c.phoneNo);
    setCustomerSearch(`${c.customerName} (${c.phoneNo})`);
    setIsDropdownOpen(false);
  }}
  className="w-full text-left p-3 text-sm text-gray-700 hover:bg-blue-50 flex justify-between items-center transition-colors focus:outline-none focus:bg-blue-50 focus:ring-2 focus:ring-inset focus:ring-blue-500"
>
  <span className="font-medium text-gray-900">{c.customerName}</span>
  <span className="text-xs text-gray-400 font-mono">{c.phoneNo}</span>
</button>
                          ))
                        )}
                      </div>
                      
                    )}
                    {customerSearch && (
                      <button 
                        onClick={() => { setCustomerSearch(""); setCustomerIdentifier(""); }}
                        className="absolute right-3 top-3.5 text-gray-400 hover:text-gray-600 text-xs font-bold"
                      >
                        Clear
                      </button>
                    )}
                  </div>

                  <div className="grid grid-cols-2 gap-3">
                    <button
                      onClick={createCart}
                      disabled={loading || !customerIdentifier}
                      className="w-full bg-blue-800 hover:bg-blue-900 transition-all text-white rounded-lg px-4 py-3 text-sm font-medium disabled:opacity-40 shadow-sm"
                    >
                      {loading ? "Connecting Sequence..." : "Create Cart"}
                    </button>
                    <button
                      onClick={clearCart}
                      disabled={!cartId}
                      className="w-full bg-red-600 hover:bg-red-700 transition text-white rounded-lg px-4 py-3 font-medium disabled:opacity-50"
                    >
                     Clear Cart
                    </button>
                  </div>
                </div>
              </div>

              {cart && (
                <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
                  <div className="px-5 py-4 border-b border-gray-100 flex justify-between items-center">
                    <h2 className="font-semibold text-gray-800 text-sm tracking-wide uppercase">Selected Basket Items</h2>
                    <span className="text-xs bg-gray-100 px-2 py-1 rounded font-medium text-gray-600">
                      {cart.entryCart?.length || 0} Products Listed
                    </span>
                  </div>
                  <div className="max-h-[380px] overflow-y-auto">
                    <table className="min-w-full divide-y divide-gray-100 text-sm">
                      <thead className="bg-gray-50/70 text-gray-500 font-medium text-xs uppercase">
                        <tr>
                          <th className="p-3 text-left">Product Details</th>
                          <th className="p-3 text-center">Base Price</th>
                          <th className="p-3 text-center">Disc</th>
                          <th className="p-3 text-center">Qty Actions</th>
                          <th className="p-3 text-right">Extended Total</th>
                          <th className="p-3 text-center"></th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-gray-100 bg-white">
                        {cart.entryCart?.map((item) => {
                          const computedMrp = (Number(item.unitPrice || 0) + Number(item.discount || 0)).toFixed(2);
                          return (
                            <tr key={item.identifier} className="hover:bg-gray-50/80 transition-colors">
                              <td className="p-3">
                                <div className="font-semibold text-gray-900">{productMap[item.productIdentifier]?.brand}</div>
                                <div className="text-xs text-gray-400 font-mono">{productMap[item.productIdentifier]?.model}</div>
                              </td>
                              <td className="p-3 text-center text-gray-400 line-through text-xs">₹{computedMrp}</td>
                              <td className="p-3 text-center text-red-600 font-medium">₹{item.discount}</td>
                              <td className="p-3">
                                <div className="flex justify-center items-center gap-2">
                                  <button
                                    onClick={() => updateQuantity(item.productIdentifier, -1)}
                                    className="w-6 h-6 bg-gray-100 hover:bg-gray-200 rounded font-bold text-gray-600 flex items-center justify-center transition-all"
                                  >
                                    -
                                  </button>
                                  <span className="w-6 text-center font-bold text-gray-800 font-mono text-xs">{item.quantity}</span>
                                  <button
                                    onClick={() => updateQuantity(item.productIdentifier, 1)}
                                    className="w-6 h-6 bg-blue-600 hover:bg-blue-700 text-white rounded font-bold flex items-center justify-center transition-all"
                                  >
                                    +
                                  </button>
                                </div>
                              </td>
                              <td className="p-3 text-right font-bold text-gray-900 font-mono">₹{item.totalPrice}</td>
                              <td className="p-3 text-center">
                                <button
                                  onClick={() => removeProductFromCart(item.productIdentifier)}
                                  className="p-1.5 text-gray-300 hover:text-red-500 rounded-md hover:bg-red-50 transition-all"
                                >
                                  <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                  </svg>
                                </button>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                </div>
              )}

              {cart && (
                <div className="bg-white rounded-xl text-black shadow-sm p-5 border border-gray-100">
                  <h2 className="font-semibold text-xs tracking-wider uppercase text-gray-500 mb-4">Checkout Valuation Statement</h2>
                  <div className="space-y-4 text-sm font-mono">
                    <div className="flex justify-between text-gray-600">
                      <span>Original Price Total</span>
                      <span>₹{cart.originalPrice || 0}</span>
                    </div>
                    <div className="flex justify-between text-gray-500">
                      <span>Campaign Discount Deductions</span>
                      <span>-₹{cart.discount || 0}</span>
                    </div>
                    <div className="flex justify-between text-lg font-bold text-gray-900 border-t border-gray-100 pt-3">
                      <span className="font-sans">Payable Gross Total</span>
                      <span className="text-xl text-blue-600 font-mono">₹{cart.totalPrice || 0}</span>
                    </div>
                    
                    <div className="pt-2">
                      <button
                        type="button"
                        onClick={() => setIsCheckoutModalOpen(true)}
                        disabled={(cart.entryCart?.length || 0) === 0}
                        className="w-full bg-emerald-600 hover:bg-emerald-700 disabled:opacity-40 text-white rounded-xl py-3.5 font-sans font-bold text-base shadow-md transition-all flex items-center justify-center gap-2 tracking-wide"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2.5} d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
                        </svg>
                        Proceed to Payment (Pay ₹{cart.totalPrice || 0})
                      </button>
                    </div>
                  </div>
                </div>
              )}
            </div>

            <div className="lg:col-span-5">
              <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5">
                <div className="flex flex-col gap-3 mb-4">
                  <h2 className="font-semibold text-gray-800 text-sm tracking-wide uppercase">Inventory Selection</h2>
                  <input
                    type="text"
                    placeholder="Filter by attribute (Brand, Model, Type)..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="border border-gray-200 rounded-lg px-4 py-2.5 bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 text-sm text-gray-800 w-full transition-all"
                  />
                </div>

                <div className="max-h-[72vh] overflow-y-auto pr-1">
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    {filteredProducts.map((product) => (
                      <button
  key={product.identifier}
  type="button"
  onClick={() => addToCart(product.identifier)}
  className="w-full text-left bg-white border border-gray-100 hover:border-blue-500 rounded-xl p-4 transition-all flex flex-col justify-between group relative overflow-hidden focus:outline-none focus:ring-2 focus:ring-blue-500/50 hover:shadow-md"
>
                        <div>
                          <div className="flex justify-between items-start gap-1">
                            <h3 className="font-bold text-gray-900 group-hover:text-blue-600 transition-colors text-sm truncate max-w-[120px]">
                              {product.identifier}
                            </h3>
                            <span className="text-[10px] font-mono bg-gray-100 text-gray-500 px-1.5 py-0.5 rounded">
                              {product.category}
                            </span>
                          </div>
                          <p className="text-xs text-gray-700 font-semibold mt-1">{product.brand}</p>
                          <p className="text-xs text-gray-400 font-mono truncate">{product.model}</p>
                        </div>
                        <div className="mt-4 pt-2 border-t border-gray-50 flex items-center justify-between">
                          <span className="text-sm font-bold text-emerald-600 font-mono">
                            ₹{priceMap[product.identifier] || 0}
                          </span>
                          <div className="bg-blue-50 text-blue-600 group-hover:bg-blue-600 group-hover:text-white rounded-md px-2 py-1 text-[11px] font-bold transition-all">
                            + Add
                          </div>
                        </div>
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

{viewMode === "orders" && (() => {
  if (ordersLoading) {
    return (
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <div className="py-20 text-center font-medium text-gray-400 text-sm">
          Parsing terminal records database pipeline...
        </div>
      </div>
    );
  }

  if (orders.length === 0) {
    return (
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <div className="py-20 text-center font-medium text-gray-400 text-sm">
          No orders discovered inside current data context scope.
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-lg font-bold text-gray-900">Historical Sales Registers</h2>
          <p className="text-xs text-gray-400 font-medium">Audit logs displaying last 50 transactions records</p>
        </div>
        <button 
          onClick={loadOrders}
          className="text-xs border border-gray-200 bg-gray-50 hover:bg-gray-100 font-bold px-3 py-2 rounded-lg transition"
        >
          🔄 Refresh Logs
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-100 text-sm text-left">
          <thead className="bg-gray-50 text-gray-500 text-xs font-bold uppercase tracking-wider">
            <tr>
              <th className="p-4">Order Code / Identifier</th>
              <th className="p-4">Payment Method</th>
              <th className="p-4 text-center">Original Total</th>
              <th className="p-4 text-center">Discount Applied</th>
              <th className="p-4 text-right">Final Amount Paid</th>
              <th className="p-4 text-center">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100 bg-white text-gray-700">
  {orders.map((order) => {
    let paymentMethodClass;

    if (order.paymentMethod === "CASH") {
      paymentMethodClass =
        "bg-emerald-50 text-emerald-700 border border-emerald-100";
    } else if (order.paymentMethod === "UPI") {
      paymentMethodClass =
        "bg-purple-50 text-purple-700 border border-purple-100";
    } else {
      paymentMethodClass =
        "bg-blue-50 text-blue-700 border border-blue-100";
    }

    return (
      <tr
        key={order.identifier || order.id}
        className="hover:bg-gray-50/50 transition-colors font-mono"
      >
        <td className="p-4 font-bold text-gray-900 font-sans tracking-tight">
          {order.identifier || `ORD-REF-${order.id}`}
        </td>

        <td className="p-4">
          <span
            className={`px-2.5 py-1 text-xs font-bold rounded-md ${paymentMethodClass}`}
          >
            {order.paymentMethod}
          </span>
        </td>

        <td className="p-4 text-center text-gray-400 font-sans">
          ₹{order.originalPrice || 0}
        </td>

        <td className="p-4 text-center text-red-600 font-medium font-sans">
          -₹{order.discount || 0}
        </td>

        <td className="p-4 text-right font-black text-gray-900 text-base font-sans">
          ₹{order.totalPrice || 0}
        </td>

        <td className="p-4 text-center">
          <button
            type="button"
            onClick={() => handleOpenOrderDetails(order)}
            className="bg-blue-50 hover:bg-blue-600 text-blue-600 hover:text-white px-3 py-1.5 rounded-lg text-xs font-bold font-sans transition-all border border-blue-100 shadow-2xs"
          >
            👁️ View Details
          </button>
        </td>
      </tr>
    );
  })}
</tbody>
        </table>
      </div>
    </div>
  );
})()}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs transition-opacity no-print">
          <div className="bg-white rounded-xl shadow-2xl w-full max-w-md overflow-hidden border border-gray-100">
            <div className="flex justify-between items-center px-5 py-4 border-b border-gray-100 bg-gray-50">
              <h3 className="font-bold text-gray-900 text-base">Create New Customer Profile</h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-gray-400 hover:text-gray-600 text-2xl font-semibold leading-none focus:outline-none"
              >
                &times;
              </button>
            </div>
            <div className="p-5">
              <AddCustomerForm
                onSaved={handleCustomerSavedSuccessfully}
                onCancel={() => setIsModalOpen(false)}
              />
            </div>
          </div>
        </div>
      )}

      {isCheckoutModalOpen && cart && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs transition-opacity no-print">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden border border-gray-100 transform transition-all duration-300">
            <div className="flex justify-between items-center px-6 py-4 border-b border-gray-100 bg-gray-50">
              <div>
                <h3 className="font-bold text-gray-900 text-lg">POS Tender Processing</h3>
                <p className="text-xs font-mono text-gray-400 mt-0.5">Session: {cartId}</p>
              </div>
              <button
                onClick={() => setIsCheckoutModalOpen(false)}
                className="text-gray-400 hover:text-gray-600 text-2xl font-semibold focus:outline-none"
              >
                &times;
              </button>
            </div>

            <div className="p-6 space-y-6">
              <div className="bg-blue-50/70 border border-blue-100 rounded-xl p-4 flex justify-between items-center">
                <span className="text-sm font-semibold text-blue-900">Net Final Payable:</span>
                <span className="text-2xl font-black font-mono text-blue-700">₹{cart.totalPrice || 0}</span>
              </div>

             <fieldset className="space-y-2">
  <legend className="text-xs font-bold text-gray-500 uppercase tracking-wider">
    Payment Mode
  </legend>
  
  <div className="grid grid-cols-3 gap-3">
    {["CASH", "CARD", "UPI"].map((method) => {
      const isSelected = paymentMethod === method;
      
      return (
        <button
          key={method}
          type="button"
          onClick={() => setPaymentMethod(method)}
          aria-pressed={isSelected}
          className={`py-3 rounded-xl border text-sm font-bold transition-all flex flex-col items-center justify-center gap-1 ${
            isSelected
              ? "bg-blue-600 border-blue-600 text-white shadow-md shadow-blue-500/20"
              : "bg-white border-gray-200 text-gray-700 hover:bg-gray-50"
          }`}
        >
          {method === "CASH" && "💵 Cash"}
          {method === "CARD" && "💳 Card Swipe"}
          {method === "UPI" && "📱 UPI / QR"}
        </button>
      );
    })}
  </div>
</fieldset>

              {paymentMethod === "CASH" ? (
                <div className="space-y-4 bg-gray-50 p-4 rounded-xl border border-gray-200">
                  <div className="space-y-1.5">
  {/* Added htmlFor attribute */}
  <label 
    htmlFor="cash-amount" 
    className="text-xs font-bold text-gray-600 uppercase"
  >
    Cash Amount Received
  </label>
  
  <div className="relative">
    <span className="absolute left-3 top-3 text-gray-400 font-bold text-base">₹</span>
    <input
      id="cash-amount" 
      type="number"
      placeholder="Enter paid bills amount..."
      value={amountReceived}
      onChange={(e) => setAmountReceived(e.target.value)}
      className="w-full border border-gray-200 rounded-lg pl-8 pr-4 py-2.5 bg-white font-mono text-lg font-bold text-gray-800 focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all"
    />
  </div>
</div>

                  <div className="flex gap-2">
                    {[500, 1000].map((quickCash) => (
                      <button
                        key={quickCash}
                        type="button"
                        onClick={() => setAmountReceived(quickCash.toString())}
                        className="bg-white border border-gray-200 hover:border-gray-400 font-mono text-xs text-gray-700 px-3 py-1.5 rounded-md transition-all font-semibold"
                      >
                        +₹{quickCash}
                      </button>
                    ))}
                  </div>

                  <div className="flex justify-between items-center border-t border-gray-200 pt-3 mt-2">
                    <span className="text-xs font-bold text-gray-500 uppercase">Change to return:</span>
                    <span className="text-xl font-bold font-mono text-emerald-600">₹{computedChange}</span>
                  </div>
                </div>
              ) : (
                <div className="p-4 border border-dashed border-gray-200 bg-gray-50 rounded-xl text-center">
                  <p className="text-sm font-medium text-gray-600">
                    {paymentMethod === "UPI" 
                      ? "Generate dynamic display system checkout code screen on client verification app terminal."
                      : "Awaiting point-of-sale card machine clearance execution response hook."}
                  </p>
                </div>
              )}
            </div>

            <div className="px-6 py-4 bg-gray-50 border-t border-gray-100 flex justify-end gap-3">
              <button
                type="button"
                onClick={() => setIsCheckoutModalOpen(false)}
                className="bg-white border border-gray-200 hover:bg-gray-100 text-gray-700 font-medium rounded-xl px-5 py-2.5 text-sm transition"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={handleFinalizeOrder}
                disabled={processingOrder || (paymentMethod === "CASH" && !amountReceived)}
                className="bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white font-bold rounded-xl px-6 py-2.5 text-sm transition shadow-sm flex items-center gap-1.5"
              >
                {processingOrder ? "Finalizing Transaction..." : "Complete & Print Receipt"}
              </button>
            </div>
          </div>
        </div>
      )}

      {isViewOrderModalOpen && selectedOrder && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs transition-opacity animate-fade-in">
          <div 
            id="printable-order-receipt"
            className="bg-white rounded-xl shadow-2xl w-full max-w-2xl overflow-hidden border border-gray-100 animate-scale-up"
          >
            
            <div className="flex justify-between items-center px-6 py-4 border-b border-gray-100 bg-gray-50">
              <div>
                <h3 className="font-bold text-gray-900 text-base">Invoice Registry: {selectedOrder.identifier || `ORD-REF-${selectedOrder.id}`}</h3>
                <p className="text-xs text-gray-400 mt-0.5 font-sans font-medium">Detailed customer purchases summary breakdown</p>
              </div>
              <button
  onClick={() => {
    setIsViewOrderModalOpen(false);
    setSelectedOrder(null);

    clearCompletedSale();
  }}

                className="text-gray-400 hover:text-gray-600 text-2xl font-semibold leading-none focus:outline-none no-print"
              >
                &times;
              </button>
            </div>

            <div className="p-6 space-y-5 max-h-[70vh] overflow-y-auto">
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-4 bg-gray-50 border border-gray-100 rounded-xl p-4 text-xs">
                <div>
                  <span className="block font-semibold text-gray-400 uppercase tracking-wide mb-0.5">Customer Channel Identifier</span>
                  <span className="font-bold text-gray-800 text-sm truncate block">{selectedOrder.customerIdentifier || "Walk-In Profile"}</span>
                </div>
                <div>
                  <span className="block font-semibold text-gray-400 uppercase tracking-wide mb-0.5">Tender Method</span>
                  <span className="font-bold font-mono px-2 py-0.5 bg-gray-200/60 text-gray-800 rounded text-[11px] inline-block mt-0.5">{selectedOrder.paymentMethod}</span>
                </div>
                <div>
                  <span className="block font-semibold text-gray-400 uppercase tracking-wide mb-0.5">Cash Cleared</span>
                  <span className="font-bold text-gray-800 font-mono text-sm">₹{selectedOrder.receivedAmount || selectedOrder.totalPrice || 0}</span>
                </div>
              </div>

<div className="space-y-2">
  <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider">
    Purchased Products List
  </h4>

  {(() => {
    if (singleOrderLoading) {
      return (
        <div className="p-8 text-center text-xs text-gray-400 font-medium">
          Retrieving customer product manifest directly from target sequence database context...
        </div>
      );
    }

    if (!selectedOrder.entryList?.length) {
      return (
        <div className="p-6 bg-gray-50/50 rounded-xl text-center text-xs text-gray-400 border border-dashed border-gray-200">
          No matching itemization maps found in database records.
        </div>
      );
    }

    return (
      <div className="border border-gray-100 rounded-xl overflow-hidden shadow-sm">
        <table className="min-w-full divide-y divide-gray-100 text-xs text-left">
          <thead className="bg-gray-50/70 text-gray-500 font-bold uppercase">
            <tr>
              <th className="p-3">Product Name / Specs</th>
              <th className="p-3 text-center">Qty Bought</th>
              <th className="p-3 text-center">Unit Price</th>
              <th className="p-3 text-right">Extended Total</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100 bg-white text-gray-700">
            {selectedOrder.entryList.map((item) => (
              <tr key={item.id} className="hover:bg-gray-50/40">
                <td className="p-3">
                  <div className="font-bold text-gray-900">
                    {productMap[item.productIdentifier]?.brand || "Product ID:"} {item.productIdentifier}
                  </div>
                  <div className="text-[10px] font-mono text-gray-400">
                    {productMap[item.productIdentifier]?.model || "Item Manifest Ref"}
                  </div>
                </td>
                <td className="p-3 text-center font-bold font-mono text-blue-600 bg-blue-50/20">
                  {item.quantity}
                </td>
                <td className="p-3 text-center font-mono text-gray-500">
                  ₹{item.unitPrice || 0}
                </td>
                <td className="p-3 text-right font-black font-mono text-gray-900">
                  ₹{item.totalPrice || 0}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  })()}
</div>

              <div className="border-t border-gray-100 pt-4 space-y-2 text-xs font-mono">
                <div className="flex justify-between text-gray-500">
                  <span>Gross Valuation Accumulation</span>
                  <span>₹{selectedOrder.originalPrice || 0}</span>
                </div>
                <div className="flex justify-between text-red-500 font-medium">
                  <span>Campaign Markdown Deductions</span>
                  <span>-₹{selectedOrder.discount || 0}</span>
                </div>
                <div className="flex justify-between items-center text-sm font-bold text-gray-900 pt-2 border-t border-dashed border-gray-100 font-sans">
                  <span>Net Ledger Total Amount</span>
                  <span className="text-xl font-mono text-blue-600 font-black">₹{selectedOrder.totalPrice || 0}</span>
                </div>
              </div>
            </div>

            <div className="px-6 py-4 bg-gray-50 border-t border-gray-100 flex justify-end gap-3 no-print">
              <button
  type="button"
  onClick={() => {
    setIsViewOrderModalOpen(false);
    setSelectedOrder(null);

    clearCompletedSale();
  }}
>
  Close
</button>
              <button
                type="button"
                onClick={handlePrintReceipt}
                className="bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-lg px-5 py-2 text-xs transition shadow-sm flex items-center gap-1.5"
              >
                🖨️ Print Receipt
              </button>
            </div>

          </div>
        </div>
      )}
      </div>
    </Layout>
  );
}