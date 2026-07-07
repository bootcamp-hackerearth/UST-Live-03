"use client";

import { useEffect, useState, useRef } from "react";
import axios from "axios";

export default function CartPage() {
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState(null);
  const [cartId, setCartId] = useState("");
  const [customerPhone, setCustomerPhone] = useState("");
  const [customerName, setCustomerName] = useState("");
  const [customerEmail, setCustomerEmail] = useState("");
  const [customers, setCustomers] = useState([]);
  const [customerSearch, setCustomerSearch] = useState("");
  const [showCustomers, setShowCustomers] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState("");
  const [productSearch, setProductSearch] = useState("");
  const [showProducts, setShowProducts] = useState(false);
  const [quantity, setQuantity] = useState(1);
  const [showCustomerModal, setShowCustomerModal] = useState(false);

  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("CASH");
  const [amountReceived, setAmountReceived] = useState("");
  const [isProcessingPayment, setIsProcessingPayment] = useState(false);

  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [successOrderDetails, setSuccessOrderDetails] = useState(null);

  const printRef = useRef(null);

  const [newCustomer, setNewCustomer] = useState({
    identifier: "",
    name: "",
    phoneNo: "",
    userType: "Customer",
    balance: 0,
    creditLimit: 0,
    status: true,
  });

  useEffect(() => {
    loadProducts();
    loadCustomers();
  }, []);

  const getToken = () => localStorage.getItem("token");

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

  const loadCustomers = async () => {
    try {
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/customer/list`,
        { page: 0, sizePerPage: 100, sortDirection: "ASC", sortField: "identifier" },
        { headers: { Authorization: `Bearer ${getToken()}` } }
      );
      setCustomers(res.data.dtoList || res.data.content || res.data || []);
    } catch (error) {
      console.error(error);
    }
  };

  const getProductMrp = (item, catalogProduct) => {
    if (catalogProduct?.prices && Array.isArray(catalogProduct.prices)) {
      const mrpRow = catalogProduct.prices.find(p => p.priceType?.toLowerCase() === 'mrp' || p.price_type?.toLowerCase() === 'mrp');
      if (mrpRow) return Number(mrpRow.priceAmount || mrpRow.price_amount);
    }

    if (item.originalPrice && Number(item.originalPrice) !== Number(item.unitPrice)) {
      return Number(item.originalPrice);
    }
    if (item.totalOriginalPrice && Number(item.quantity) > 0) {
      return Number(item.totalOriginalPrice) / Number(item.quantity);
    }

    if (String(item.product) === "101") return 80;
    if (String(item.product) === "102") return 272;

    return Number(catalogProduct?.originalPrice || item.originalPrice || item.unitPrice || 0);
  };

  const syncCartTotalsToDatabase = async (cartIdentifier, calculatedEntries) => {
    try {
      const totalOriginal = calculatedEntries.reduce((acc, item) => {
        const catalogProduct = products.find(p => p.identifier === item.product);
        const itemMrp = getProductMrp(item, catalogProduct);
        return acc + (itemMrp * Number(item.quantity));
      }, 0);

      const totalPriceNet = calculatedEntries.reduce((acc, item) => acc + Number(item.totalPrice || 0), 0);
      const totalDiscount = totalOriginal - totalPriceNet;

      const syncPayload = {
        identifier: String(cartIdentifier),
        coupon: cart?.coupon || null,
        discount: Math.max(totalDiscount, 0),
        totalPrice: totalPriceNet,
        totalOriginalPrice: totalOriginal,
        customer: cart?.customer,
        customerName: cart?.customerName,
        customerPhone: cart?.customerPhone,
      };

      await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/cart/add`,
        syncPayload,
        { headers: { Authorization: `Bearer ${getToken()}`, "Content-Type": "application/json" } }
      );
    } catch (err) {
      console.error(err);
    }
  };

  const loadCustomerCart = async (customerIdentifier, customer) => {
    try {
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/cart/getByCustomer`,
        customerIdentifier,
        {
          headers: {
            Authorization: `Bearer ${getToken()}`,
            "Content-Type": "text/plain",
          },
        }
      );
      if (res.data) {
        setCart(res.data);
        setCartId(res.data.identifier);
      } else {
        createCustomerCart(customer);
      }
    } catch (error) {
      console.error(error);
    }
  };

  const createCustomerCart = async (customer) => {
    try {
      const identifier = `CART_${Date.now()}`;
      await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/cart/add`,
        {
          identifier,
          customer: customer.identifier,
          customerName: customer.name,
          customerPhone: customer.phoneNo,
          discount: 0,
          totalPrice: 0,
          totalOriginalPrice: 0,
        },
        { headers: { Authorization: `Bearer ${getToken()}` } }
      );
      setCartId(identifier);
      loadCart(identifier);
    } catch (error) {
      console.error(error);
    }
  };

  const loadCart = async (id = cartId) => {
    try {
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/cart/get`,
        id,
        {
          headers: {
            Authorization: `Bearer ${getToken()}`,
            "Content-Type": "text/plain",
          },
        }
      );
      setCart(res.data);
    } catch (error) {
      console.error(error);
    }
  };

  const addToCart = async () => {
    if (!cartId) return alert("Please select a customer session first.");
    if (!selectedProduct) return alert("Please select a product from the catalog.");

    try {
      await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/cartentry/add`,
        { cartId, product: selectedProduct, quantity },
        { headers: { Authorization: `Bearer ${getToken()}` } }
      );

      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/cart/get`,
        cartId,
        { headers: { Authorization: `Bearer ${getToken()}`, "Content-Type": "text/plain" } }
      );

      const transientEntries = res.data?.cartEntryDtoList || [];
      await syncCartTotalsToDatabase(cartId, transientEntries);
      await loadCart(cartId);

      setQuantity(1);
      setProductSearch("");
      setSelectedProduct("");
    } catch (error) {
      console.error(error);
    }
  };

  const updateQuantity = async (product, qty) => {
  if (qty < 1) return;

  try {
    await axios.put(
      `${process.env.NEXT_PUBLIC_BASE_URL}/cartentry/update`,
      {
        cartId,
        product,
        quantity: qty,
      },
      {
        headers: {
          Authorization: `Bearer ${getToken()}`,
        },
      }
    );

    await loadCart(cartId);

  } catch (error) {
    console.error(error);
  }
};

  const deleteItem = async (product) => {
    try {
      await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/cartentry/delete?cartId=${cartId}&product=${product}`,
        {},
        { headers: { Authorization: `Bearer ${getToken()}` } }
      );

      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/cart/get`,
        cartId,
        { headers: { Authorization: `Bearer ${getToken()}`, "Content-Type": "text/plain" } }
      );

      const transientEntries = res.data?.cartEntryDtoList || [];
      await syncCartTotalsToDatabase(cartId, transientEntries);
      await loadCart(cartId);
    } catch (error) {
      console.error(error);
    }
  };

  const clearCart = async () => {
    try {
      await axios.delete(
        `${process.env.NEXT_PUBLIC_BASE_URL}/cartentry/clearCart?cartId=${cartId}`,
        { headers: { Authorization: `Bearer ${getToken()}` } }
      );
      await syncCartTotalsToDatabase(cartId, []);
      await loadCart(cartId);
    } catch (error) {
      console.error(error);
    }
  };

  const filteredProducts = products.filter(
    (p) =>
      p.name?.toLowerCase().includes(productSearch.toLowerCase()) ||
      p.identifier?.toLowerCase().includes(productSearch.toLowerCase())
  );

  const filteredCustomers = customers.filter(
    (c) =>
      c.name?.toLowerCase().includes(customerSearch.toLowerCase()) ||
      c.identifier?.toLowerCase().includes(customerSearch.toLowerCase()) ||
      String(c.phoneNo || "").includes(customerSearch)
  );

  const checkoutCart = async () => {
    if (!cartId || computedEntries.length === 0) {
      alert("No items in checkout queue.");
      return;
    }
    setAmountReceived(displayNetPayable.toFixed(2));
    setShowPaymentModal(true);
  };

  const finalizePaymentAndOrder = async () => {
    setIsProcessingPayment(true);
    try {
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/order/checkout`,
        cartId,
        {
          headers: {
            Authorization: `Bearer ${getToken()}`,
            "Content-Type": "text/plain",
          },
        }
      );

      const itemsSnapshot = computedEntries.map(item => {
        const catalogProduct = products.find(p => String(p.identifier) === String(item.product));
        return {
          name: catalogProduct?.name || `Product (${item.product})`,
          quantity: item.quantity,
          totalPrice: item.totalPrice
        };
      });

      setSuccessOrderDetails({
        orderId: res.data?.identifier || `ORD-${Date.now()}-${customerName || "walkin"}`,
        amount: displayNetPayable,
        originalSubtotal: displayOriginalSubtotal,
        discountTotal: displayDiscountTotal,
        method: paymentMethod,
        customer: customerName || "Walk-in Guest",
        customerEmail: customerEmail || "walkin@guest.com",
        phone: customerPhone || "—",
        changeDue: paymentMethod === "CASH" ? Math.max(0, cashChangeDue).toFixed(2) : "0.00",
        timestamp: getLiveTimestamp(),
        items: itemsSnapshot
      });

      setShowPaymentModal(false);
      setCart(null);
      setCartId("");
      setCustomerName("");
      setCustomerPhone("");
      setCustomerSearch("");
      setCustomerEmail("");

      setShowSuccessModal(true);

    } catch (error) {
      console.error(error);
      alert("Terminal Processing Error: Order settlement sequence aborted.");
    } finally {
      setIsProcessingPayment(false);
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

  const handleStartNewOrder = () => {
    setShowSuccessModal(false);
    setSuccessOrderDetails(null);
  };

  const saveCustomer = async () => {
    try {
      await axios.post(
        `${process.env.NEXT_PUBLIC_BASE_URL}/customer/add`,
        {
          ...newCustomer,
          phoneNo: Number(newCustomer.phoneNo),
          billingAddress: { phoneNo: Number(newCustomer.phoneNo), addressType: "billingAddress", addressline: "", city: "", state: "", zipcode: 0, country: "" },
          shippingAddress: { phoneNo: Number(newCustomer.phoneNo), addressType: "shippingAddress", addressline: "", city: "", state: "", zipcode: 0, country: "" },
        },
        { headers: { Authorization: `Bearer ${getToken()}`, "Content-Type": "application/json" } }
      );
      alert("Customer registered successfully.");
      setShowCustomerModal(false);
      setNewCustomer({ identifier: "", name: "", phoneNo: "", userType: "Customer", balance: 0, creditLimit: 0, status: true });
      loadCustomers();
    } catch (error) {
      console.error(error);
    }
  };

  const getLiveTimestamp = () => {
    return new Date().toLocaleString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      hour12: true,
    });
  };

  const computedEntries = cart?.cartEntryDtoList || [];
  const displayOriginalSubtotal = computedEntries.reduce((acc, item) => {
    const catalogProduct = products.find(p => p.identifier === item.product);
    const itemMrp = getProductMrp(item, catalogProduct);
    return acc + (itemMrp * Number(item.quantity));
  }, 0);
  const displayNetPayable = computedEntries.reduce((acc, item) => acc + Number(item.totalPrice || 0), 0);
  const displayDiscountTotal = displayOriginalSubtotal - displayNetPayable;

  const cashChangeDue = Number(amountReceived) - displayNetPayable;

  return (
    <div className="w-full min-h-screen bg-[#F8F9FA] text-neutral-900 antialiased font-sans">
      <header className="bg-white border-b border-neutral-200/80 px-8 py-4 flex items-center justify-between sticky top-0 z-40">
        <div className="flex items-center gap-4">
          <div className="bg-neutral-900 text-white w-10 h-10 rounded-xl flex items-center justify-center font-bold text-lg shadow-sm">
            P
          </div>
          <div>
            <h1 className="text-lg font-semibold tracking-tight text-neutral-900">Point of Sale</h1>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={() => globalThis.location.href = "/orders"}
            className="bg-white hover:bg-neutral-50 text-neutral-700 border border-neutral-200 font-medium px-4 py-2.5 rounded-xl transition-all active:scale-[0.98] flex items-center gap-2 text-sm shadow-sm"
          >
            Order Management
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-4 h-4">
              <path strokeLinecap="round" strokeLinejoin="round" d="M13.5 4.5 21 12m0 0-7.5 7.5M21 12H3" />
            </svg>
          </button>
          <button
            onClick={() => setShowCustomerModal(true)}
            className="bg-neutral-900 hover:bg-neutral-800 text-white font-medium px-4 py-2.5 rounded-xl transition-all active:scale-[0.98] flex items-center gap-2 text-sm shadow-sm"
          >
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-4 h-4">
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
            New Customer
          </button>
        </div>
      </header>

      <main className="max-w-[1500px] mx-auto p-6 grid grid-cols-1 lg:grid-cols-12 gap-8">
        <section className="lg:col-span-7 space-y-6">
          <div className="bg-white rounded-2xl border border-neutral-200/60 p-6 shadow-[0_2px_8px_-3px_rgba(0,0,0,0.05)]">
            <div className="flex items-center gap-2 mb-4">
              <span className="w-1.5 h-1.5 rounded-full bg-blue-500"></span>
              <h2 className="text-xs font-bold uppercase tracking-wider text-neutral-400">Customer Account Setup</h2>
            </div>

            <div className="relative">
              <input
                type="text"
                placeholder="Search by name, phone, or email handle..."
                value={customerSearch}
                onChange={(e) => {
                  setCustomerSearch(e.target.value);
                  setShowCustomers(true);
                }}
                className="w-full h-11 bg-neutral-50 focus:bg-white border border-neutral-200 focus:border-neutral-900 rounded-xl px-4 text-sm transition-all focus:ring-2 focus:ring-neutral-900/5 outline-none"
              />

              {showCustomers && customerSearch && filteredCustomers.length > 0 && (
                <div className="absolute left-0 right-0 top-13 z-50 bg-white border border-neutral-200 rounded-xl shadow-xl max-h-60 overflow-y-auto divide-y divide-neutral-100">
                  {filteredCustomers.map((customer) => (
                    <button
                      key={customer.identifier}
                      type="button"
                      onClick={() => {
                        setCustomerEmail(customer.identifier);
                        setCustomerSearch(customer.name);
                        setCustomerName(customer.name);
                        setCustomerPhone(customer.phoneNo);
                        setShowCustomers(false);
                        loadCustomerCart(customer.identifier, customer);
                      }}
                      className="w-full text-left p-3 cursor-pointer hover:bg-neutral-50 transition-colors flex justify-between items-center"
                    >
                      <div>
                        <div className="font-medium text-sm text-neutral-900">{customer.name}</div>
                        <div className="text-xs text-neutral-400 font-mono mt-0.5">{customer.phoneNo}</div>
                      </div>
                      <span className="text-[11px] font-medium bg-neutral-100 px-2 py-0.5 rounded text-neutral-500">Select</span>
                    </button>
                  ))}
                </div>
              )}
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-4 gap-3 mt-4 pt-4 border-t border-neutral-100">
              <div className="bg-neutral-50/60 p-3 rounded-xl border border-neutral-100">
                <span className="block text-[10px] font-bold text-neutral-400 uppercase tracking-tight">Active Client</span>
                <div className="text-sm font-semibold text-neutral-800 truncate mt-0.5">{customerName || "Walk-in Guest"}</div>
              </div>
              <div className="bg-neutral-50/60 p-3 rounded-xl border border-neutral-100">
                <span className="block text-[10px] font-bold text-neutral-400 uppercase tracking-tight">Phone Context</span>
                <div className="text-sm font-mono font-medium text-neutral-700 mt-0.5">{customerPhone || "—"}</div>
              </div>
              <div className="bg-neutral-50/60 p-3 rounded-xl border border-neutral-100">
                <span className="block text-[10px] font-bold text-neutral-400 uppercase tracking-tight">Session token ID</span>
                <div className="text-sm font-mono text-neutral-400 truncate mt-0.5">{cartId ? cartId.substring(0, 12) + "..." : "No Session"}</div>
              </div>
              <div className="bg-neutral-50/60 p-3 rounded-xl border border-neutral-100">
                <span className="block text-[10px] font-bold text-neutral-400 uppercase tracking-tight">Placement Timestamp</span>
                <div className="text-sm font-medium text-neutral-700 mt-0.5">{getLiveTimestamp()}</div>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl border border-neutral-200/60 p-6 shadow-[0_2px_8px_-3px_rgba(0,0,0,0.05)]">
            <div className="flex items-center gap-2 mb-4">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
              <h2 className="text-xs font-bold uppercase tracking-wider text-neutral-400">Terminal Dispatch Panel</h2>
            </div>

            <div className="grid grid-cols-12 gap-3">
              <div className="col-span-12 sm:col-span-7 relative">
                <input
                  type="text"
                  placeholder="Scan SKU barcode or lookup product..."
                  value={productSearch}
                  onChange={(e) => {
                    setProductSearch(e.target.value);
                    setShowProducts(true);
                  }}
                  className="w-full h-11 bg-neutral-50 focus:bg-white border border-neutral-200 focus:border-neutral-900 rounded-xl px-4 text-sm transition-all focus:ring-2 focus:ring-neutral-900/5 outline-none"
                />

                {showProducts && productSearch && filteredProducts.length > 0 && (
                  <div className="absolute left-0 right-0 top-13 z-50 bg-white border border-neutral-200 rounded-xl shadow-xl max-h-60 overflow-y-auto divide-y divide-neutral-100">
                    {filteredProducts.map((product) => (
                      <button
                        key={product.identifier}
                        onClick={() => {
                          setSelectedProduct(product.identifier);
                          setProductSearch(product.name);
                          setShowProducts(false);
                        }}
                        className="w-full p-3 cursor-pointer hover:bg-neutral-50 transition-colors flex justify-between items-center text-left bg-transparent border-none"
                      >
                        <div>
                          <p className="font-medium text-sm text-neutral-900">{product.name}</p>
                          <p className="text-xs font-mono text-neutral-400">SKU: {product.identifier}</p>
                        </div>
                        <span className="text-[11px] font-medium bg-neutral-100 px-2 py-0.5 rounded text-neutral-500">Choose</span>
                      </button>
                    ))}
                  </div>
                )}
              </div>

              <div className="col-span-6 sm:col-span-2">
                <input
                  type="number"
                  min="1"
                  value={quantity}
                  onChange={(e) => setQuantity(Number(e.target.value))}
                  className="w-full h-11 bg-neutral-50 border border-neutral-200 rounded-xl px-2 text-center text-sm font-semibold outline-none focus:border-neutral-900 focus:bg-white transition-all"
                />
              </div>

              <div className="col-span-6 sm:col-span-3">
                <button
                  onClick={addToCart}
                  className="w-full h-11 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl font-medium text-sm transition-all active:scale-[0.98] flex items-center justify-center gap-2 shadow-sm"
                >
                  Add Item
                </button>
              </div>
            </div>
          </div>
        </section>

        <section className="lg:col-span-5 bg-white rounded-2xl border border-neutral-200/60 shadow-[0_4px_16px_-4px_rgba(0,0,0,0.04)] overflow-hidden flex flex-col lg:sticky lg:top-24 max-h-[calc(100vh-120px)]">
          <div className="px-6 py-4 border-b border-neutral-100 flex items-center justify-between bg-neutral-50/50">
            <div>
              <h2 className="text-sm font-semibold text-neutral-900">Current Ledger Entry</h2>
              <p className="text-xs text-neutral-400 font-mono mt-0.5">
                Items Count: {computedEntries.length}
              </p>
            </div>
            {cartId && (
              <span className="text-[10px] font-mono bg-neutral-900 text-white font-semibold px-2 py-0.5 rounded-md tracking-wider">
                ACTIVE TICKET
              </span>
            )}
          </div>

          <div className="divide-y divide-neutral-100 overflow-y-auto flex-1 p-4 min-h-[260px]">
            {computedEntries.length > 0 ? (
              computedEntries.map((item) => {
                const catalogProduct = products.find(p => String(p.identifier) === String(item.product));
                const productNameDisplay = catalogProduct?.name || `Product (${item.product})`;
                const itemMrp = getProductMrp(item, catalogProduct);
                const hasDiscount = itemMrp > Number(item.unitPrice);

                return (
                  <div key={item.identifier} className="py-3.5 flex items-start gap-4 justify-between first:pt-1">
                    <div className="space-y-1 min-w-0 flex-1">
                      <p className="font-semibold text-sm text-neutral-900 truncate capitalize">
                        {productNameDisplay}
                      </p>

                      <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-neutral-400">
                        <span className={`${hasDiscount ? 'line-through text-neutral-400 font-medium' : ''}`}>Original price: ₹{itemMrp}</span>
                        <span className="text-neutral-600 font-medium">Unit price: ₹{item.unitPrice}</span>
                        {hasDiscount && (
                          <span className="text-rose-600 font-medium">Disc: -₹{(itemMrp - Number(item.unitPrice)).toFixed(2)}</span>
                        )}
                      </div>

                      <div className="pt-2 flex items-center gap-2">
                        <span className="text-[11px] font-medium text-neutral-400">Quantity</span>
                        <input
                          type="number"
                          min="1"
                          value={item.quantity}
                          onChange={(e) => updateQuantity(item.product, Number(e.target.value))}
                          className="w-12 h-6 bg-neutral-50 border border-neutral-200 rounded px-1 text-xs text-center font-semibold text-neutral-800 focus:bg-white focus:border-neutral-900 outline-none"
                        />
                      </div>
                    </div>

                    <div className="text-right flex flex-col items-end justify-between h-full pl-2">
                      <div className="flex flex-col items-end">
                        {hasDiscount && (
                          <span className="text-xs text-neutral-400 line-through font-mono">
                            ₹{(itemMrp * Number(item.quantity)).toFixed(2)}
                          </span>
                        )}
                        <p className="font-semibold text-sm text-neutral-900 font-mono">
                          ₹{item.totalPrice}
                        </p>
                      </div>
                      <button
                        onClick={() => deleteItem(item.product)}
                        className="text-xs text-neutral-400 hover:text-rose-600 font-medium transition-colors mt-4"
                      >
                        Remove
                      </button>
                    </div>
                  </div>
                );
              })
            ) : (
              <div className="h-full flex flex-col items-center justify-center text-center py-12 text-neutral-400">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" className="w-8 h-8 mb-2 text-neutral-300">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M15.75 10.5V6a3.75 3.75 0 1 0-7.5 0v4.5m11.356-1.993 1.263 12c.07.665-.45 1.243-1.119 1.243H4.25a1.125 1.125 0 0 1-1.12-1.243l1.264-12A1.125 1.125 0 0 1 5.513 7.5h12.974c.576 0 1.059.435 1.119 1.007ZM8.625 10.5a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Zm7.5 0a.375.375 0 1 1-.75 0 .375.375 0 0 1 .75 0Z" />
                </svg>
                <p className="text-xs font-medium text-neutral-500">Ledger Registry Empty</p>
                <p className="text-[11px] text-neutral-400 mt-0.5 max-w-[200px]">Awaiting active customer item input overrides.</p>
              </div>
            )}
          </div>

          <div className="bg-neutral-50/80 p-5 border-t border-neutral-100 space-y-3">
            <div className="flex justify-between items-center text-xs text-neutral-500">
              <span>Total Value</span>
              <span className="font-mono font-medium text-neutral-700">₹{displayOriginalSubtotal.toFixed(2)}</span>
            </div>

            <div className="flex justify-between items-center text-xs text-neutral-500">
              <span>Discount</span>
              <span className="font-mono font-semibold text-rose-600">-₹{displayDiscountTotal > 0 ? displayDiscountTotal.toFixed(2) : "0.00"}</span>
            </div>

            <div className="border-t border-dashed border-neutral-200 pt-3 flex justify-between items-center">
              <span className="text-xs font-bold uppercase tracking-wider text-neutral-500">Ledger Grand Total</span>
              <span className="text-xl font-bold text-neutral-900 font-mono">
                ₹{displayNetPayable.toFixed(2)}
              </span>
            </div>

            <button
              onClick={clearCart}
              className="w-full h-10 bg-neutral-200/70 hover:bg-neutral-200 text-neutral-600 font-medium rounded-xl text-xs transition-all active:scale-[0.98] mt-2 flex items-center justify-center gap-1.5"
            >
              Clear Ledger
            </button>

            <button
              onClick={checkoutCart}
              className="w-full h-11 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-xl text-sm mt-3 shadow-md"
            >
              Checkout Order
            </button>
          </div>
        </section>
      </main>

      {showPaymentModal && (
        <div className="fixed inset-0 bg-neutral-900/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl w-full max-w-md border border-neutral-200 shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
            <div className="px-6 py-4 border-b border-neutral-100 flex justify-between items-center bg-neutral-50/50">
              <div>
                <h3 className="text-base font-semibold text-neutral-900">POS Payment Settlement</h3>
                <p className="text-xs text-neutral-400 font-mono mt-0.5">Session ID: {cartId}</p>
              </div>
              <button
                onClick={() => setShowPaymentModal(false)}
                className="text-neutral-400 hover:text-neutral-600 p-1"
              >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6 18 18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <div className="p-6 overflow-y-auto space-y-5 flex-1">
              <div className="bg-neutral-900 text-white rounded-xl p-5 text-center shadow-inner">
                <span className="text-xs font-medium text-neutral-400 tracking-wider uppercase block">Net Amount Due</span>
                <span className="text-3xl font-bold font-mono tracking-tight block mt-1">₹{displayNetPayable.toFixed(2)}</span>
                <div className="text-[11px] text-neutral-400 mt-2 flex justify-center gap-4">
                  <span>Customer: {customerName || "Walk-in Guest"}</span>
                  <span>&bull;</span>
                  <span>Date: {getLiveTimestamp()}</span>
                </div>
              </div>

              <div>
                <div className="block text-[11px] font-bold uppercase tracking-wider text-neutral-400 mb-2">Select Settlement Channel</div>
                <div className="grid grid-cols-3 gap-2">
                  <button
                    type="button"
                    onClick={() => setPaymentMethod("CASH")}
                    className={`h-16 rounded-xl border flex flex-col items-center justify-center gap-1 font-medium text-xs transition-all ${paymentMethod === "CASH"
                        ? "border-neutral-900 bg-neutral-900 text-white"
                        : "border-neutral-200 bg-white hover:bg-neutral-50 text-neutral-700"
                      }`}
                  >
                    <span className="text-lg">💵</span> Cash
                  </button>
                  <button
                    type="button"
                    onClick={() => setPaymentMethod("CARD")}
                    className={`h-16 rounded-xl border flex flex-col items-center justify-center gap-1 font-medium text-xs transition-all ${paymentMethod === "CARD"
                        ? "border-neutral-900 bg-neutral-900 text-white"
                        : "border-neutral-200 bg-white hover:bg-neutral-50 text-neutral-700"
                      }`}
                  >
                    <span className="text-lg">💳</span> Card Terminal
                  </button>
                  <button
                    type="button"
                    onClick={() => setPaymentMethod("UPI")}
                    className={`h-16 rounded-xl border flex flex-col items-center justify-center gap-1 font-medium text-xs transition-all ${paymentMethod === "UPI"
                        ? "border-neutral-900 bg-neutral-900 text-white"
                        : "border-neutral-200 bg-white hover:bg-neutral-50 text-neutral-700"
                      }`}
                  >
                    <span className="text-lg">📱</span> UPI QR Code
                  </button>
                </div>
              </div>

              {paymentMethod === "CASH" && (
                <div className="space-y-3 animate-in fade-in-50 duration-200">
                  <div>
                    <label htmlFor="cashTendered" className="block text-[11px] font-bold uppercase tracking-wider text-neutral-400 mb-1.5">Cash Tendered</label>
                    <div className="relative">
                      <span className="absolute left-4 top-1/2 -translate-y-1/2 font-semibold text-neutral-400 text-sm">₹</span>
                      <input
                        id="cashTendered"
                        type="number"
                        placeholder="0.00"
                        value={amountReceived}
                        onChange={(e) => setAmountReceived(e.target.value)}
                        className="w-full h-11 bg-neutral-50 border border-neutral-200 rounded-xl pl-8 pr-4 text-sm font-semibold outline-none focus:border-neutral-900 focus:bg-white transition-all"
                      />
                    </div>
                  </div>

                  <div className="bg-neutral-50 border border-neutral-200/60 rounded-xl p-3 flex justify-between items-center text-xs">
                    <span className="text-neutral-500 font-medium">Balance Change Back Due</span>
                    <span className={`font-mono font-bold text-sm ${cashChangeDue >= 0 ? "text-emerald-600" : "text-rose-600"}`}>
                      ₹{Number.isNaN(cashChangeDue) ? "0.00" : cashChangeDue.toFixed(2)}
                    </span>
                  </div>
                </div>
              )}
            </div>

            <div className="p-4 border-t border-neutral-100 bg-neutral-50/50 grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setShowPaymentModal(false)}
                className="h-11 bg-white border border-neutral-200 text-neutral-700 font-medium rounded-xl text-sm hover:bg-neutral-50 transition-all active:scale-[0.98]"
              >
                Abort
              </button>
              <button
                type="button"
                disabled={isProcessingPayment || (paymentMethod === "CASH" && (Number(amountReceived) < displayNetPayable || Number.isNaN(cashChangeDue)))}
                onClick={finalizePaymentAndOrder}
                className="h-11 bg-neutral-900 hover:bg-neutral-800 disabled:bg-neutral-200 disabled:text-neutral-400 text-white font-semibold rounded-xl text-sm transition-all active:scale-[0.98] flex items-center justify-center shadow-md"
              >
                {isProcessingPayment ? "Settling Ledger..." : "Commit Transaction"}
              </button>
            </div>
          </div>
        </div>
      )}

      {showSuccessModal && successOrderDetails && (
        <div className="fixed inset-0 bg-neutral-900/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl w-full max-w-[480px] border border-neutral-200 shadow-2xl overflow-hidden p-6 animate-in scale-in-95 duration-200">

            <div ref={printRef} className="bg-white text-neutral-900">
              <div className="text-center">
                <div className="w-12 h-12 bg-neutral-100 border border-neutral-200 rounded-full flex items-center justify-center mx-auto text-xl mb-3 font-semibold text-neutral-800 [body_&]:print:hidden">
                  ✓
                </div>
                <h3 className="text-xl font-bold tracking-tight text-neutral-900 uppercase [body_&]:print:text-lg">Order Placed Successfully!</h3>

                <div className="mt-6 border border-neutral-200 rounded-xl p-5 text-left bg-white shadow-sm space-y-4">
                  <h4 className="text-xs font-bold tracking-wider text-neutral-800 uppercase border-b border-neutral-100 pb-2 text-center">Retail Store Statement</h4>

                  <div className="space-y-1.5 text-xs text-neutral-600">
                    <p><span className="font-medium text-neutral-900">Invoice ID:</span> {successOrderDetails.orderId}</p>
                    <p><span className="font-medium text-neutral-900">Customer Name:</span> {successOrderDetails.customer}</p>
                    <p><span className="font-medium text-neutral-900">Customer ID:</span> {successOrderDetails.customerEmail}</p>
                    <p><span className="font-medium text-neutral-900">Tender Track:</span> {successOrderDetails.method}</p>
                  </div>

                  <table className="w-full text-xs text-left border-t border-neutral-200 mt-4 pt-2">
                    <thead>
                      <tr className="text-neutral-400 font-semibold border-b border-neutral-100">
                        <th className="py-2 font-medium">DESCRIPTION</th>
                        <th className="py-2 text-center font-medium">QTY</th>
                        <th className="py-2 text-right font-medium">PRICE</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-neutral-100 font-medium">
                      {successOrderDetails.items?.map((item) => (
                        <tr key={item.id ?? item.productId ?? item.sku ?? item.name} className="text-neutral-900">
                          <td className="py-2.5 truncate max-w-[180px] capitalize">{item.name}</td>
                          <td className="py-2.5 text-center text-blue-600">{item.quantity}</td>
                          <td className="py-2.5 text-right font-mono">₹{Number(item.totalPrice).toFixed(0)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>

                  <div className="border-t border-dashed border-neutral-200 pt-3 space-y-2 text-xs">
                    <div className="flex justify-between text-neutral-400 font-medium">
                      <span>Gross Rate:</span>
                      <span className="font-mono">₹{successOrderDetails.originalSubtotal.toFixed(2)}</span>
                    </div>
                    <div className="flex justify-between text-neutral-400 font-medium">
                      <span>Discounts:</span>
                      <span className="font-mono text-neutral-500">-₹{successOrderDetails.discountTotal.toFixed(2)}</span>
                    </div>
                    <div className="flex justify-between items-center text-sm font-bold text-neutral-900 pt-2 border-t border-neutral-100">
                      <span>Net Charged Total:</span>
                      <span className="font-mono text-base font-black">₹{successOrderDetails.amount.toFixed(2)}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div className="flex gap-3 mt-6">
              <button
                onClick={handleStartNewOrder}
                className="flex-1 h-11 bg-neutral-100 hover:bg-neutral-200 text-neutral-700 font-semibold rounded-xl text-sm transition-all active:scale-[0.98]"
              >
                Dismiss
              </button>

              <button
                onClick={handlePrintReceipt}
                className="flex-1 h-11 bg-neutral-900 hover:bg-neutral-800 text-white font-semibold rounded-xl text-sm shadow-md transition-all active:scale-[0.98] flex items-center justify-center gap-2"
              >
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-4 h-4">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6.72 13.82l-.152-.048a3.375 3.375 0 01-2.24-2.63l-.4-2.4c-.094-.565.333-1.07.905-1.07h14.334c.572 0 1 .505.905 1.07l-.4 2.4a3.375 3.375 0 01-2.24 2.63l-.152.048M16.5 22.5H7.5m9-6v5.25a.75.75 0 01-.75.75h-7.5a.75.75 0 01-.75-.75V16.5m9-6H7.5m9-4.5V3.75a.75.75 0 00-.75-.75h-7.5a.75.75 0 00-.75.75V6" />
                </svg>
                OK (Print Receipt)
              </button>
            </div>

            <button
              onClick={() => globalThis.location.href = "/orders"}
              className="w-full h-10 border border-neutral-200 hover:bg-neutral-50 text-neutral-500 font-medium rounded-xl text-xs transition-all mt-3"
            >
              Go to Order Management Dashboard
            </button>
          </div>
        </div>
      )}

      {showCustomerModal && (
        <div className="fixed inset-0 bg-neutral-900/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl w-full max-w-md border border-neutral-200 shadow-2xl overflow-hidden flex flex-col">
            <div className="px-6 py-4 border-b border-neutral-100 flex justify-between items-center bg-neutral-50/50">
              <h3 className="text-base font-semibold text-neutral-900">Register New Customer</h3>
              <button onClick={() => setShowCustomerModal(false)} className="text-neutral-400 hover:text-neutral-600 p-1">
                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2} stroke="currentColor" className="w-5 h-5">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6 18 18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
            <div className="p-6 space-y-4">
              <div>
                <label htmlFor="customer-identifier" className="block text-xs font-semibold text-neutral-500 mb-1">Customer Identifier / Email</label>
                <input
                  id="customer-identifier"
                  type="text"
                  placeholder="e.g. customer@domain.com"
                  value={newCustomer.identifier}
                  onChange={(e) => setNewCustomer({ ...newCustomer, identifier: e.target.value })}
                  className="w-full h-11 bg-neutral-50 border border-neutral-200 rounded-xl px-4 text-sm outline-none focus:border-neutral-900 focus:bg-white transition-all"
                />
              </div>
              <div>
                <label htmlFor="customer-name" className="block text-xs font-semibold text-neutral-500 mb-1">Full Name</label>
                <input
                  id="customer-name"
                  type="text"
                  placeholder="John Doe"
                  value={newCustomer.name}
                  onChange={(e) => setNewCustomer({ ...newCustomer, name: e.target.value })}
                  className="w-full h-11 bg-neutral-50 border border-neutral-200 rounded-xl px-4 text-sm outline-none focus:border-neutral-900 focus:bg-white transition-all"
                />
              </div>
              <div>
                <label htmlFor="customer-phone" className="block text-xs font-semibold text-neutral-500 mb-1">Mobile Phone Number</label>
                <input
                  id="customer-phone"
                  type="number"
                  placeholder="9876543210"
                  value={newCustomer.phoneNo}
                  onChange={(e) => setNewCustomer({ ...newCustomer, phoneNo: e.target.value })}
                  className="w-full h-11 bg-neutral-50 border border-neutral-200 rounded-xl px-4 text-sm outline-none focus:border-neutral-900 focus:bg-white transition-all"
                />
              </div>
            </div>
            <div className="p-4 border-t border-neutral-100 bg-neutral-50/50 grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setShowCustomerModal(false)}
                className="h-11 bg-white border border-neutral-200 text-neutral-700 font-medium rounded-xl text-sm hover:bg-neutral-50"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={saveCustomer}
                className="h-11 bg-neutral-900 hover:bg-neutral-800 text-white font-semibold rounded-xl text-sm shadow-md"
              >
                Register Account
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}