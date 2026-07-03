"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Trash2, ShoppingCart, UserPlus, X, ChevronRight, Search, Package, CheckCircle } from "lucide-react";
import api from "../../services/api";
import PropTypes from "prop-types";
import Select from "react-select";
import Sidebar from "../../components/layout/Sidebar";

const today = () => {
  const d = new Date();
  return d.toLocaleDateString("en-GB").replaceAll("/", "-");
};

const currency = (val) =>
  `₹${Number(val || 0).toLocaleString("en-IN", { minimumFractionDigits: 2 })}`;

const customSelectStyles = {
  control: (base) => ({
    ...base,
    minHeight: "42px",
    height: "42px",
    border: "none",
    shadowBox: "none",
    background: "transparent",
    display: "flex",
    alignItems: "center",
    cursor: "text",
    width: "100%",
  }),
  valueContainer: (base) => ({
    ...base,
    height: "42px",
    padding: "0 8px",
    display: "flex",
    alignItems: "center",
  }),
  input: (base) => ({
    ...base,
    margin: 0,
    padding: 0,
    color: "#111827",
    fontSize: "14px",
  }),
  indicatorsContainer: (base) => ({ ...base, height: "42px" }),
  indicatorSeparator: () => ({ display: "none" }),
  dropdownIndicator: (base) => ({
    ...base,
    color: "#4B5563",
    padding: "0 8px",
    "&:hover": { color: "#111827" },
  }),
  placeholder: (base) => ({
    ...base,
    color: "#6B7280",
    fontSize: "14px",
  }),
  singleValue: (base) => ({
    ...base,
    color: "#111827",
    fontSize: "14px",
    fontWeight: "600",
  }),
  menu: (base) => ({
    ...base,
    borderRadius: "10px",
    boxShadow: "0 10px 25px rgba(0,0,0,0.15)",
    border: "1px solid #9CA3AF",
    overflow: "hidden",
    zIndex: 9999,
  }),
option: (base, state) => {
  let backgroundColor = "white";

  if (state.isSelected) {
    backgroundColor = "#2563EB";
  } else if (state.isFocused) {
    backgroundColor = "#F3F4F6";
  }

  return {
    ...base,
    backgroundColor,
    color: state.isSelected ? "white" : "#1F2937",
    fontSize: "14px",
    padding: "10px 14px",
    cursor: "pointer",
  };
},
};

const CustomerField = ({ value, onChange, customers, onSearch, }) => {
  const options = customers.map((c) => ({
    value: c.identifier,
    label: c.name ?? c.identifier,
  }));

  const selectedOption = options.find((o) => o.value === value) || null;

return (
  <div className="flex-1 min-w-[240px]">
    <label
      htmlFor="customer-select"
      className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5"
    >
      Customer
    </label>

    <div className="flex items-center bg-white border-2 border-gray-300 rounded-xl hover:border-gray-400 focus-within:border-blue-600 focus-within:ring-2 focus-within:ring-blue-100 transition-all px-2 shadow-sm">
<Select
  inputId="customer-select"
  options={options}
  value={selectedOption}
  onChange={(opt) => onChange(opt?.value || "")}
  onInputChange={(input) => {
    onSearch(input);
  }}
  placeholder="Search customer…"
  className="flex-1 text-sm"
  styles={customSelectStyles}
/>
    </div>
  </div>
);
};

const CartTable = ({ entries, onQtyChange, onRemove }) => (
  <div className="w-full space-y-3 mb-6">
    {entries.length === 0 ? (
      <div className="bg-white rounded-2xl border-2 border-gray-200 shadow-xs p-12 text-center">
        <div className="flex flex-col items-center justify-center max-w-sm mx-auto">
          <div className="w-12 h-12 rounded-full bg-gray-100 flex items-center justify-center mb-3">
            <ShoppingCart size={22} className="text-gray-400" />
          </div>
          <p className="text-base font-bold text-gray-800 mb-1">Cart is empty</p>
          <p className="text-sm text-gray-500 font-medium">Click on items from the left catalog to build your order</p>
        </div>
      </div>
    ) : (
      entries.map((entry, i) => (
        <div key={entry.identifier ?? i} className="bg-white rounded-2xl border-2 border-gray-200 shadow-xs hover:border-gray-300 p-4 transition-all">
          <div className="flex justify-between items-start border-b border-gray-100 pb-2.5 mb-3">
            <div>
              <h4 className="text-sm font-black text-gray-900">{entry.productIdentifier}</h4>
              <p className="text-[11px] font-mono font-bold text-gray-400 mt-0.5">SKU: {entry.identifier}</p>
            </div>
            <button
              onClick={() => onRemove(i)}
              className="w-7 h-7 flex items-center justify-center rounded-lg text-red-500 hover:text-red-700 hover:bg-red-50 transition-all"
            >
              <Trash2 size={15} />
            </button>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-5 gap-3 items-center text-xs">
            <div>
              <span className="block text-[10px] font-bold text-gray-400 uppercase mb-0.5">MRP</span>
              <span className="text-gray-400 line-through font-medium">
                {currency(entry.quantity > 0 ? entry.originalPrice / entry.quantity : 0)}
              </span>
            </div>

            <div>
              <span className="block text-[10px] font-bold text-gray-400 uppercase mb-0.5">Sale Price</span>
              <span className="text-gray-900 font-bold">{currency(entry.unitPrice)}</span>
            </div>

            <div>
              <span className="block text-[10px] font-bold text-gray-400 uppercase mb-1">Quantity</span>
              <div className="inline-flex items-center bg-gray-100 p-0.5 rounded-lg border border-gray-300">
                <button
                  onClick={() => {
                    const newQty = entry.quantity - 1;
                    if (newQty >= 1) onQtyChange(i, newQty);
                  }}
                  className="w-6 h-6 flex items-center justify-center rounded bg-white hover:bg-gray-50 text-gray-700 border border-gray-200 font-bold"
                >
                  −
                </button>
                <span className="w-7 text-center font-bold text-gray-900">{entry.quantity}</span>
                <button
                  onClick={() => onQtyChange(i, entry.quantity + 1)}
                  className="w-6 h-6 flex items-center justify-center rounded bg-white hover:bg-gray-50 text-gray-700 border border-gray-200 font-bold"
                >
                  +
                </button>
              </div>
            </div>

            <div>
              <span className="block text-[10px] font-bold text-gray-400 uppercase mb-0.5">Total MRP</span>
              <span className="text-gray-700 font-semibold">{currency(entry.originalPrice)}</span>
            </div>

            <div className="text-right sm:text-left">
              <span className="block text-[10px] font-bold text-blue-500 uppercase mb-0.5">Subtotal</span>
              <span className="text-sm font-black text-blue-700">{currency(entry.totalPrice)}</span>
            </div>
          </div>
        </div>
      ))
    )}
  </div>
);

const CartTotals = ({ cart }) => (
  <div className="w-full bg-white rounded-2xl border-2 border-gray-200 shadow-sm p-5 space-y-3.5">
    <h3 className="text-xs font-bold text-gray-800 uppercase tracking-wider pb-2 border-b border-gray-200">
      Order Summary
    </h3>
    <div className="flex justify-between items-center text-xs">
      <span className="text-gray-600 font-semibold">Original Price</span>
      <span className="text-gray-900 font-bold">{currency(cart?.totalOriginalPrice)}</span>
    </div>
    <div className="flex justify-between items-center text-xs">
      <span className="text-gray-600 font-semibold">Discount</span>
      <span className="text-emerald-600 font-bold">− {currency(cart?.totalDiscount)}</span>
    </div>
    <div className="pt-3 border-t-2 border-dashed border-gray-200 flex justify-between items-center">
      <span className="text-sm font-bold text-gray-900">Total Payable</span>
      <span className="text-base font-black text-blue-700 tracking-tight">
        {currency(cart?.totalPrice)}
      </span>
    </div>
  </div>
);

const CartPage = () => {
  const router = useRouter();

  const [customers, setCustomers] = useState([]);
  const [customer, setCustomer] = useState("");
  const [cartData, setCartData] = useState(null);
  const [entries, setEntries] = useState([]);
  const [products, setProducts] = useState([]);
  const [prices, setPrices] = useState([]);
  const [customerSearch, setCustomerSearch] = useState("");
  const [productSearch, setProductSearch] = useState("");
  const [savingId, setSavingId] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState("CASH");
  const [receivedAmount, setReceivedAmount] = useState("");
  const [checkingOut, setCheckingOut] = useState(false);
  const [message, setMessage] = useState("");
  const [showCustomerPopup, setShowCustomerPopup] = useState(false);
  const [showCheckoutPopup, setShowCheckoutPopup] = useState(false);
  const [newCustomer, setNewCustomer] = useState({ name: "", phoneNo: "", email: "", creditLimit: 0 });

  const getHeaders = () => {
    const token = globalThis.localStorage?.getItem("token");
    return { Authorization: `Bearer ${token}`, "Content-Type": "application/json" };
  };
  const loadCustomers = async (keyword = "") => {
  try {
    const res = await api.post(
      "/customer/list",
      {
        page: 0,
        sizePerPage: 500,
        keyword,
      },
      {
        headers: getHeaders(),
      }
    );

    setCustomers(res.data.dtoList ?? []);
  } catch (err) {
    console.error(err);
  }
};

const loadProducts = async (keyword = "") => {
  try {
    const res = await api.post(
      "/product/list",
      {
        page: 0,
        sizePerPage: 500,
        keyword,
      },
      {
        headers: getHeaders(),
      }
    );

    setProducts(res.data.dtoList ?? []);
  } catch (err) {
    console.error(err);
  }
};

useEffect(() => {
  const headers = getHeaders();

  loadProducts("");
  loadCustomers("");

  api.post(
    "/price/list",
    { page: 0, sizePerPage: 500 },
    { headers }
  )
    .then((res) => setPrices(res.data.dtoList ?? []))
    .catch(console.error);

}, []);

  const fetchCart = async (customerId) => {
    if (!customerId) { setCartData(null); setEntries([]); return; }
    try {
      const cartRes = await api.get(`/cart/get?identifier=${customerId}`, { headers: getHeaders() });
      setCartData(cartRes.data);
      const entriesRes = await api.post("/cartEntry/list", { page: 0, sizePerPage: 500 }, { headers: getHeaders() });
      setEntries((entriesRes.data ?? []).filter((entry) => entry.cartIdentifier === customerId));
    } catch {
      setCartData(null); setEntries([]);
    }
  };

  useEffect(() => { fetchCart(customer); }, [customer]);
  useEffect(() => {
  const timer = setTimeout(() => {
    loadCustomers(customerSearch);
  }, 300);

  return () => clearTimeout(timer);

}, [customerSearch]);
useEffect(() => {
  const timer = setTimeout(() => {
    loadProducts(productSearch);
  }, 300);

  return () => clearTimeout(timer);

}, [productSearch]);

  const handleSelectProduct = async (productIdentifier) => {
    if (!customer) { showMessage("Please select a customer first!"); return; }
    setSavingId(productIdentifier);
    try {
      await api.post("/cartEntry/add", { productIdentifier, cartIdentifier: customer, quantity: 1 }, { headers: getHeaders() });
      await fetchCart(customer);
      showMessage("Item added to cart");
    } catch {
      showMessage("Failed to add item");
    } finally {
      setSavingId(null);
    }
  };

  const handleQtyChange = async (index, qty) => {
    if (!qty || Number(qty) < 1) return;
    try {
      await api.put("/cartEntry/update", { ...entries[index], quantity: Number(qty) }, { headers: getHeaders() });
      await fetchCart(customer);
    } catch (err) { console.error(err); }
  };

  const handleRemove = async (index) => {
    try {
     await api.delete(
  `/cartEntry/delete?identifier=${entries[index].identifier}`,
  {
    headers: getHeaders(),
  }
);
      await fetchCart(customer);
      showMessage("Item removed");
    } catch (err) { console.error(err); }
  };

  const showMessage = (msg) => { setMessage(msg); setTimeout(() => setMessage(""), 3000); };

  const handleClearCart = async () => {
    if (!customer || !confirm("Are you sure you want to clear the cart?")) return;
    try {
     await api.delete("/cart/delete", {
      headers: getHeaders(),
      data: {
        identifier: customer,
      },
    });
      setEntries([]); setCartData(null); showMessage("Cart cleared");
    } catch { showMessage("Failed to clear cart"); }
  };

  const handleCheckoutOpen = () => {
    if (!customer) {
      showMessage("Select customer first");
      return;
    }
    if (entries.length === 0) {
      showMessage("Cart is empty");
      return;
    }
    setReceivedAmount(cartData?.totalPrice || "");
    setShowCheckoutPopup(true);
  };

  const handleCheckout = async () => {
    try {
      setCheckingOut(true);

      const res = await api.post(
        "/order/checkout",
        {
          customerIdentifier: customer,
          paymentMethod,
          receivedAmount: Number(receivedAmount || cartData?.totalPrice || 0),
        },
        {
          headers: getHeaders(),
        }
      );

   if (res.data.success) {
  setShowCheckoutPopup(false);

  const createdOrder =
    res.data.dto?.identifier ||
    res.data.identifier ||
    res.data.orderIdentifier ||
    res.data.data?.identifier;

  await fetchCart(customer);

  setCustomer("");
  setEntries([]);
  setCartData(null);

  if (createdOrder) {
    router.push(`/order?open=${createdOrder}`);
  } 
  else {
    router.push("/order");
  }
      } else {
        showMessage(res.data.message);
      }
    } catch (err) {
      console.log(err);
      showMessage("Checkout failed");
    } finally {
      setCheckingOut(false);
    }
  };

  const handleCustomerSave = async () => {
    try {
      if (!newCustomer.name.trim() || !/^[6-9]\d{9}$/.test(newCustomer.phoneNo)) {
        showMessage("Check fields details missing or invalid phone number"); 
        return;
      }
      if (customers.some((c) => c.phoneNo === newCustomer.phoneNo)) {
        showMessage("Phone number already exists"); 
        return;
      }

      const res = await api.post("/customer/add", {
        name: newCustomer.name, 
        phoneNo: newCustomer.phoneNo, 
        email: newCustomer.email,
        creditLimit: Number(newCustomer.creditLimit), 
        balance: 0, 
        balanceType: "CR", 
        partyType: "CUSTOMER",
        billingAddress: {}, 
        shippingAddress: {}
      }, { headers: getHeaders() });

      const savedCustomer = res.data?.dto || res.data?.data || res.data;
      const freshCustomersRes = await api.post("/customer/list", { page: 0, sizePerPage: 500 }, { headers: getHeaders() });
      const freshList = freshCustomersRes.data.dtoList ?? [];
      setCustomers(freshList);

      const targetIdentifier = savedCustomer?.identifier || freshList.find(c => c.phoneNo === newCustomer.phoneNo)?.identifier;

      if (targetIdentifier) {
        setCustomer(targetIdentifier);
      }

      setShowCustomerPopup(false);
      setNewCustomer({ name: "", phoneNo: "", email: "", creditLimit: 0 });
      showMessage("Customer Added Successfully!");
    } catch (err) { 
      console.error(err);
      showMessage("Save Failed"); 
    }
  };

  return (
    <Sidebar>
      <div className="min-h-screen bg-gray-50/50 p-4 md:p-6 lg:p-8">
        <div className="max-w-7xl mx-auto">
          <div className="flex items-center justify-between mb-6 border-b border-gray-200 pb-3">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-blue-600 flex items-center justify-center shadow-md shadow-blue-200">
                <ShoppingCart size={19} className="text-white" />
              </div>
              <div>
                <h1 className="text-2xl font-black text-gray-900 leading-tight">Cart Management</h1>
                <p className="text-xs font-bold text-gray-500 mt-0.5">{today()}</p>
              </div>
            </div>
            <div className="flex items-center gap-2 text-xs font-bold text-gray-400">
              <span>Home</span>
              <ChevronRight size={14} className="text-gray-300" />
              <span className="text-gray-700">POS Cart</span>
            </div>
          </div>

          {message && (
            <div className="fixed bottom-8 left-1/2 -translate-x-1/2 bg-gray-900 text-white px-6 py-3.5 rounded-xl text-sm font-bold shadow-2xl z-[10000] flex items-center gap-3 border border-gray-700 animate-bounce">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse"></span>
              {message}
            </div>
          )}
          <div className="bg-white rounded-2xl border-2 border-gray-200 shadow-sm p-4 mb-6">
            <div className="flex flex-wrap gap-4 items-end">
              <CustomerField
  value={customer}
  onChange={setCustomer}
  customers={customers}
  onSearch={setCustomerSearch}
/>
              <button
                onClick={() => setShowCustomerPopup(true)}
                className="flex items-center justify-center gap-2 h-[44px] px-4 rounded-xl bg-blue-50 text-blue-600 text-sm font-bold hover:bg-blue-100 border-2 border-blue-200 active:scale-98 transition-all"
              >
                <UserPlus size={16} />
                New Customer
              </button>
                <div className="flex-1 min-w-[160px]">
                  <p className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5">
              Customer ID
              </p>
                <div className="h-[44px] flex items-center px-4 bg-gray-50 border-2 border-gray-300 rounded-xl text-sm text-gray-900 font-mono font-bold shadow-inner">
                  {customer || <span className="text-gray-400">—</span>}
                </div>
              </div>
                </div>
              </div>
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
            <div className="lg:col-span-5 bg-white rounded-2xl border-2 border-gray-200 shadow-sm overflow-hidden flex flex-col max-h-[calc(100vh-240px)] lg:sticky lg:top-6">
              <div className="p-4 bg-gray-50/70 border-b border-gray-200 space-y-3">
                <div className="flex items-center justify-between">
                  <h2 className="text-sm font-black text-gray-900 uppercase tracking-wider flex items-center gap-2">
                    <Package size={16} className="text-blue-600" /> Items Catalog
                  </h2>
                  <span className="px-2.5 py-0.5 text-[11px] font-bold bg-gray-200 text-gray-700 rounded-full">
                    {products.length} items items
                  </span>
                </div>
                <div className="relative flex items-center">
                  <Search size={16} className="absolute left-3.5 text-gray-400 pointer-events-none" />
                  <input
                    type="text"
                    placeholder="Search by product name or code..."
                    value={productSearch}
onChange={(e) => setProductSearch(e.target.value)}
                    className="w-full h-10 pl-10 pr-4 bg-white border-2 border-gray-300 rounded-xl text-sm font-semibold placeholder-gray-400 focus:outline-none focus:border-blue-600 transition-all shadow-xs"
                  />
                </div>
              </div>
              <div className="flex-1 overflow-y-auto p-4 space-y-2.5 bg-gray-50/30">
                {products.length === 0 ? (
                  <div className="text-center py-12 text-sm font-medium text-gray-400">
                    No matching products found.
                  </div>
                ) : (
                 products.map((p) => {
                    const sPrice = prices.find((pr) => pr.product === p.identifier && pr.priceType === "SELLING PRICE")?.amount || 0;
                    const mrpPrice = prices.find((pr) => pr.product === p.identifier && pr.priceType === "MRP")?.amount || 0;

return (
          <button
            key={p.identifier}
            type="button"
            disabled={Boolean(savingId)}
            onClick={() => handleSelectProduct(p.identifier)}
            className={`w-full text-left bg-white border-2 rounded-xl p-3.5 flex items-center justify-between gap-4 transition-all shadow-xs cursor-pointer select-none group ${
              savingId === p.identifier
                ? "border-blue-300 bg-blue-50/30 opacity-60"
                : "border-gray-200 hover:border-blue-500 hover:shadow-md active:scale-[0.99]"
            }`}
          >
                        <div className="flex items-center gap-3 min-w-0">
                          <div className="w-9 h-9 rounded-lg bg-gray-100 flex items-center justify-center text-gray-500 border border-gray-200 group-hover:bg-blue-50 group-hover:text-blue-600 transition-colors shrink-0">
                            <Package size={18} />
                          </div>
                          <div className="min-w-0">
                            <h3 className="text-sm font-bold text-gray-900 group-hover:text-blue-600 transition-colors truncate">
                              {p.productName ?? p.identifier}
                            </h3>
                            <span className="text-[10px] font-mono text-gray-400 font-bold block mt-0.5">
                              ID: {p.identifier}
                            </span>
                          </div>
                        </div>

                        <div className="flex flex-col items-end shrink-0">
                          <span className="text-sm font-black text-gray-900">{currency(sPrice)}</span>
                          {mrpPrice > 0 && (
                            <span className="text-[10px] text-gray-400 line-through font-medium mt-0.5">
                              {currency(mrpPrice)}
                            </span>
                          )}
                        </div>
                      </button>
                    );
                  })
                )}
              </div>
            </div>
            <div className="lg:col-span-7 flex flex-col">
              <CartTable entries={entries} onQtyChange={handleQtyChange} onRemove={handleRemove} />
              <CartTotals cart={cartData} />
            </div>

          </div>
          <div className="flex flex-col sm:flex-row justify-between items-center gap-4 mt-6 pt-4 border-t border-gray-200">
            <button
              onClick={() => router.push("/home")}
              className="text-sm font-bold text-gray-600 hover:text-gray-900 flex items-center gap-2 transition-colors py-2"
            >
              ← Back to Home Dashboard
            </button>
            <div className="flex gap-3 w-full sm:w-auto">

              <button
                onClick={handleClearCart}
                disabled={!customer || entries.length === 0}
                className="px-5 py-2.5 rounded-xl border-2 border-red-200 text-red-600 text-sm font-bold hover:bg-red-50 disabled:opacity-40"
              >
                Clear Cart
              </button>

              <button
                onClick={() => showMessage("Cart saved successfully")}
                className="px-5 py-2.5 rounded-xl bg-gray-700 text-white text-sm font-bold"
              >
                Save Cart
              </button>

              <button
                onClick={handleCheckoutOpen}
                disabled={!customer || entries.length === 0 || checkingOut}
                className="px-8 py-2.5 rounded-xl bg-emerald-600 text-white text-sm font-bold hover:bg-emerald-700 disabled:opacity-50 flex items-center gap-2"
              >
                <ShoppingCart size={16} />
                Checkout
              </button>
            </div>
          </div>
        </div>
      </div>
      {showCustomerPopup && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-xs flex justify-center items-center z-[99999] p-4 animate-fade-in">
          <div className="bg-white w-full max-w-[440px] rounded-2xl shadow-2xl border border-gray-100 overflow-hidden transform scale-100 transition-all">
            <div className="flex items-center justify-between px-6 py-4 border-b-2 border-gray-200 bg-gray-50/50">
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-xl bg-blue-100 flex items-center justify-center">
                  <UserPlus size={16} className="text-blue-600" />
                </div>
                <h2 className="text-base font-black text-gray-900">Create New Customer</h2>
              </div>
              <button
                onClick={() => setShowCustomerPopup(false)}
                className="w-8 h-8 rounded-xl flex items-center justify-center text-gray-500 hover:text-gray-800 hover:bg-gray-200 transition-all border border-transparent"
              >
                <X size={16} />
              </button>
            </div>

            <div className="px-6 py-5 space-y-4">
              {[
                { label: "Full Name", key: "name", placeholder: "e.g. Ravi Kumar", type: "text" },
                { label: "Phone Number", key: "phoneNo", placeholder: "10-digit mobile number", type: "tel" },
                { label: "Email Address", key: "email", placeholder: "customer@email.com", type: "email" },
                { label: "Credit Limit (₹)", key: "creditLimit", placeholder: "0.00", type: "number" },
              ].map(({ label, key, placeholder, type }) => (
                <div key={key}>
                  <label
                    htmlFor={key}
                    className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5"
                  >
                    {label}
                  </label>

                  <input
                    id={key}
                    type={type}
                    placeholder={placeholder}
                    value={newCustomer[key]}
                    onChange={(e) => setNewCustomer({ ...newCustomer, [key]: e.target.value })}
                    className="w-full h-11 px-4 border-2 border-gray-300 rounded-xl text-sm font-semibold text-gray-900 placeholder-gray-400 focus:outline-none focus:border-blue-600 transition-all shadow-sm"
                  />
                </div>
              ))}
            </div>

            <div className="flex justify-end gap-3 px-6 py-4 bg-gray-50 border-t-2 border-gray-200">
              <button
                onClick={() => setShowCustomerPopup(false)}
                className="px-4 py-2.5 rounded-xl border-2 border-gray-300 text-sm font-bold text-gray-700 bg-white hover:bg-gray-100 transition-all"
              >
                Cancel
              </button>
              <button
                onClick={handleCustomerSave}
                className="px-6 py-2.5 rounded-xl bg-blue-600 text-white text-sm font-bold hover:bg-blue-700 shadow-md shadow-blue-100 transition-all active:scale-98"
              >
                Save Customer
              </button>
            </div>
          </div>
        </div>
      )}
      {showCheckoutPopup && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-xs flex justify-center items-center z-[99999] p-4 animate-fade-in">
          <div className="bg-white w-full max-w-[460px] rounded-2xl shadow-2xl border border-gray-100 overflow-hidden transform scale-100 transition-all">
            <div className="flex items-center justify-between px-6 py-4 border-b-2 border-gray-200 bg-gray-50/50">
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-xl bg-emerald-100 flex items-center justify-center">
                  <CheckCircle size={17} className="text-emerald-600" />
                </div>
                <h2 className="text-base font-black text-gray-900">Complete Payment</h2>
              </div>
              <button
                onClick={() => setShowCheckoutPopup(false)}
                className="w-8 h-8 rounded-xl flex items-center justify-center text-gray-500 hover:text-gray-800 hover:bg-gray-200 transition-all"
              >
                <X size={16} />
              </button>
            </div>

            <div className="px-6 py-5 space-y-4">
            <div>
              <span className="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-1">
                Total Payable
              </span>

              <div className="text-2xl font-black text-blue-700 tracking-tight">
                {currency(cartData?.totalPrice)}
              </div>
            </div>
              <div>
                <label
                  htmlFor="paymentMethod"
                  className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5"
                >
                  Payment Mode
                </label>

                <select
                  id="paymentMethod"
                  value={paymentMethod}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="w-full h-11 px-4 border-2 border-gray-300 rounded-xl text-sm font-semibold text-gray-900 bg-white focus:outline-none focus:border-blue-600 transition-all"
                >
                  <option value="CASH">Cash</option>
                  <option value="CARD">Card</option>
                  <option value="UPI">UPI</option>
                </select>
              </div>
              <div>
              <label
                htmlFor="receivedAmount"
                className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-1.5"
              >
                Amount Received (₹)
              </label>
              <input
                id="receivedAmount"
                  type="number"
                  placeholder="0.00"
                  value={receivedAmount}
                  onChange={(e) => setReceivedAmount(e.target.value)}
                  className="w-full h-11 px-4 border-2 border-gray-300 rounded-xl text-sm font-semibold text-gray-900 focus:outline-none focus:border-blue-600 transition-all shadow-sm"
                />
              </div>
            </div>

            <div className="flex justify-end gap-3 px-6 py-4 bg-gray-50 border-t-2 border-gray-200">
              <button
                onClick={() => setShowCheckoutPopup(false)}
                className="px-4 py-2.5 rounded-xl border-2 border-gray-300 text-sm font-bold text-gray-700 bg-white hover:bg-gray-100 transition-all"
              >
                Cancel
              </button>
              <button
                onClick={handleCheckout}
                disabled={checkingOut}
                className="px-6 py-2.5 rounded-xl bg-emerald-600 text-white text-sm font-bold hover:bg-emerald-700 shadow-md shadow-emerald-100 transition-all flex items-center gap-2"
              >
                {checkingOut ? "Processing..." : "Complete Order"}
              </button>
            </div>
          </div>
        </div>
      )}
    </Sidebar>
  );
};

CustomerField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
  customers: PropTypes.array.isRequired,
  onSearch: PropTypes.func.isRequired,
};

CartTable.propTypes = {
  entries: PropTypes.array.isRequired,
  onQtyChange: PropTypes.func.isRequired,
  onRemove: PropTypes.func.isRequired,
};

CartTotals.propTypes = {
  cart: PropTypes.shape({
    totalOriginalPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    totalDiscount: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
    totalPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  }),
};

export default CartPage;