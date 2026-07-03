"use client";

import { useRouter, useSearchParams } from "next/navigation";
import React, { useState, useEffect, useRef, Suspense } from "react";
import PropTypes from "prop-types";
import axiosInstance from "../../api/axiosInstance";
import Layout from "@/app/Components/Layout";
import {
  Search, Barcode, Plus, Minus, Trash2, ShoppingCart,
  UserPlus, CheckCircle2, AlertCircle, X, Printer, ArrowRight,
  QrCode, CreditCard, Banknote, ArrowLeft
} from "lucide-react";

function resolveProductCode(source) {
  if (!source) return null;
  if (typeof source === "string") return source;
  if (typeof source === "object") {
    return (
      source.productCode ||
      source.code ||
      source.identifier ||
      source.name ||
      null
    );
  }
  return String(source);
}

function resolveEntryPrices(entry) {
  const mrp = Number.parseFloat(entry.price) || 0;
  const rate = Number.parseFloat(entry.selling_price ?? entry.sellingPrice) || 0;
  return { mrp, rate };
}

function resolveLineTotal(entry, rate, qty) {
  return Number.parseFloat(entry.total_price ?? entry.totalPrice) || (rate * qty);
}

function describeServerError(err) {
  const data = err.response?.data;

  if (typeof data === "string" && data.trim()) {
   
    return data.replaceAll(/<[^<>]*>/g, " ").trim().slice(0, 200);
  }
  if (data && typeof data === "object" && Object.keys(data).length > 0) {
    return data.message || data.error || JSON.stringify(data);
  }
  return err.message || "Unknown server error (no response body).";
}

function logRequestFailure(endpoint, err, payload) {
  console.error(
    `[${endpoint}] request failed —`,
    "status:", err.response?.status,
    "statusText:", err.response?.statusText,
    "data:", err.response?.data,
    "message:", err.message,
    "payload sent:", payload,
  );
}

function safeNumber(value, fallback = 0) {
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : fallback;
}

function resolveEntryQuantity(entry, rate = 0) {
  const directQty =
    safeNumber(entry.quantity, Number.NaN) ||
    safeNumber(entry.qty, Number.NaN) ||
    safeNumber(entry.count, Number.NaN) ||
    safeNumber(entry.cartQty, Number.NaN);

  if (Number.isFinite(directQty) && directQty > 0) {
    return directQty;
  }

  const total =
    safeNumber(entry.totalPrice, Number.NaN) ||
    safeNumber(entry.total_price, Number.NaN) ||
    safeNumber(entry.lineTotal, Number.NaN) ||
    safeNumber(entry.amount, Number.NaN);

  if (rate > 0 && Number.isFinite(total) && total > 0) {
    return total / rate;
  }

  return 0;
}

function resolveCartEntries(cart) {
  return (
    cart?.entryDtoList ||
    cart?.entry_dto_list ||
    cart?.entryList ||
    cart?.entry_list ||
    cart?.cartEntryDtoList ||
    cart?.cart_entry_dto_list ||
    []
  );
}

function computeMrpSubtotal(entries) {
  return entries.reduce((sum, e) => {
    const { mrp, rate } = resolveEntryPrices(e);
    return sum + mrp * resolveEntryQuantity(e, rate);
  }, 0);
}

function computeSellingSubtotal(cart, entries) {
  const cartTotal = Number.parseFloat(cart?.total_price ?? cart?.totalPrice);
  if (cartTotal) return cartTotal;
  return entries.reduce((sum, e) => {
    const { rate } = resolveEntryPrices(e);
    return sum + rate * resolveEntryQuantity(e, rate);
  }, 0);
}

function computeItemDiscount(cart, mrpSubtotal, sellingSubtotal) {
  const cartTotalDiscount = Number.parseFloat(cart?.total_discount ?? cart?.totalDiscount) || 0;
  if (cartTotalDiscount > 0) return cartTotalDiscount;
  return Math.max(0, mrpSubtotal - sellingSubtotal);
}

function deriveBillingTotals(cart, vatRate, flatDiscount, shippingFee, receiveAmount) {
  const entries = resolveCartEntries(cart);
  const mrpSubtotal = computeMrpSubtotal(entries);
  const sellingSubtotal = computeSellingSubtotal(cart, entries);
  const itemDiscount = computeItemDiscount(cart, mrpSubtotal, sellingSubtotal);

  const parsedVatRate = Number.parseFloat(vatRate) || 0;
  const vatAmount = (sellingSubtotal * parsedVatRate) / 100;
  const parsedFlatDiscount = Number.parseFloat(flatDiscount) || 0;
  const parsedShippingFee = Number.parseFloat(shippingFee) || 0;
  const parsedReceiveAmount = Number.parseFloat(receiveAmount) || 0;

  const grandTotalAmount = Math.max(
    0,
    sellingSubtotal + vatAmount + parsedShippingFee - parsedFlatDiscount
  );
  const totalSavings = itemDiscount + parsedFlatDiscount;
  const cashDifference = parsedReceiveAmount - grandTotalAmount;
  const changeAmount = Math.max(0, cashDifference);
  const dueAmount = Math.max(0, -cashDifference);

  return {
    entries,
    mrpSubtotal,
    sellingSubtotal,
    itemDiscount,
    parsedVatRate,
    vatAmount,
    parsedFlatDiscount,
    parsedShippingFee,
    parsedReceiveAmount,
    grandTotalAmount,
    totalSavings,
    changeAmount,
    dueAmount,
  };
}

function filterProducts(productsCatalog, searchQuery) {
  const q = searchQuery.toLowerCase();
  return productsCatalog.filter((p) => {
    const label = (p.productName || p.name || "").toLowerCase();
    const code = (p.productCode || p.code || p.identifier || "").toLowerCase();
    return label.includes(q) || code.includes(q);
  });
}

function matchCatalogProduct(productsCatalog, rawInput) {
  const input = rawInput.trim().toLowerCase();
  return productsCatalog.find((p) => {
    const pCode = (p.productCode || p.code || p.identifier || "").toLowerCase();
    const pName = (p.productName || p.name || "").toLowerCase();
    return pCode === input || pName === input;
  });
}

function buildCompletedOrder(opts) {
  const {
    response, activeCustomer, cartId, entries,
    sellingSubtotal, vatAmount, parsedShippingFee, parsedFlatDiscount,
    grandTotalAmount, selectedPaymentMode, parsedReceiveAmount, changeAmount,
  } = opts;

  const isCash = selectedPaymentMode === "Cash";

  return {
    orderId: response.data.orderId || "N/A",
    customerName: activeCustomer ? (activeCustomer.name || activeCustomer.identifier) : "Walking Customer",
    customerPhone: cartId,
    itemsCount: entries.length,
    itemsSnapshot: [...entries],
    subtotal: sellingSubtotal,
    vat: vatAmount,
    shipping: parsedShippingFee,
    discount: parsedFlatDiscount,
    grandTotal: grandTotalAmount,
    tendered: isCash ? parsedReceiveAmount : grandTotalAmount,
    change: isCash ? changeAmount : 0,
    timestamp: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
    paymentModeUsed: selectedPaymentMode,
  };
}

function ProductCard({ prod, pricesList, onSelect }) {
  const label = prod.productName || prod.name || "Item Code";
  const itemSku = resolveProductCode(prod) || "";

  const matchedPrice = pricesList.find(
    (p) => p.productCode === itemSku || p.identifier === itemSku
  );
  const rateDisplay = Number.parseFloat(matchedPrice?.sellingPrice ?? prod.price ?? 0);

  const handleKeyDown = (e) => {
    if (e.key === "Enter" || e.key === " ") {
      e.preventDefault();
      onSelect(prod);
    }
  };

  return (
    <button
      type="button"
      onClick={() => onSelect(prod)}
      onKeyDown={handleKeyDown}
      className="bg-white hover:bg-indigo-50/30 rounded-xl border border-dashed border-slate-200 hover:border-indigo-500 p-2.5 flex flex-col justify-between items-center text-center cursor-pointer transition shadow-sm w-full"
    >
      <div className="w-8 h-8 bg-slate-50 rounded-lg flex items-center justify-center text-sm">📦</div>
      <div className="mt-2 space-y-0.5 w-full">
        <h3 className="text-[11px] font-black text-slate-700 truncate uppercase tracking-tight">{label}</h3>
        <p className="font-mono text-[9px] text-slate-400 truncate">{itemSku}</p>
        <p className="text-emerald-600 font-bold font-mono text-[10px] mt-1">₹{rateDisplay.toFixed(0)}</p>
      </div>
    </button>
  );
}

ProductCard.propTypes = {
  prod: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    productName: PropTypes.string,
    name: PropTypes.string,
    productCode: PropTypes.string,
    code: PropTypes.string,
    identifier: PropTypes.string,
    price: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    mrp: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  }).isRequired,
  pricesList: PropTypes.arrayOf(
    PropTypes.shape({
      productCode: PropTypes.string,
      identifier: PropTypes.string,
      sellingPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
      mrp: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    })
  ).isRequired,
  onSelect: PropTypes.func.isRequired,
};

function CartRow({ entry, idx, onIncrement, onDecrement, onDelete }) {
  const productCode = resolveProductCode(entry.product) || entry.productCode || "N/A";
  const { mrp, rate } = resolveEntryPrices(entry);
  const qty = resolveEntryQuantity(entry, rate);
  const lineTotal = resolveLineTotal(entry, rate, qty);

  return (
    <tr className="bg-white hover:bg-slate-50/60">
      <td className="p-2 text-center font-mono text-slate-400">{idx + 1}</td>
      <td className="p-2 font-mono font-bold text-slate-900 uppercase">{productCode}</td>
      <td className="p-2 text-right font-mono text-slate-400 line-through">₹{mrp.toFixed(2)}</td>
      <td className="p-2 text-right font-mono font-bold text-slate-700">₹{rate.toFixed(2)}</td>
      <td className="p-2 text-center">
        <div className="flex items-center gap-1 bg-slate-50 border border-slate-200 rounded-md w-max mx-auto px-1 py-0.5">
          <button
            type="button"
            onClick={() => (qty <= 1 ? onDelete(entry) : onDecrement(entry, productCode))}
            className="p-0.5 text-slate-400 hover:text-red-500"
          >
            <Minus className="w-2.5 h-2.5" />
          </button>
          <span className="w-6 text-center font-mono font-black text-indigo-600">{Math.floor(qty)}</span>
          <button
            type="button"
            onClick={() => onIncrement(entry, productCode)}
            className="p-0.5 text-slate-400 hover:text-emerald-600"
          >
            <Plus className="w-2.5 h-2.5" />
          </button>
        </div>
      </td>
      <td className="p-2 text-right font-mono font-bold text-slate-900">₹{lineTotal.toFixed(2)}</td>
      <td className="p-2 text-center">
        <button type="button" onClick={() => onDelete(entry)} className="text-slate-400 hover:text-red-600 p-1">
          <Trash2 className="w-3.5 h-3.5" />
        </button>
      </td>
    </tr>
  );
}

CartRow.propTypes = {
  entry: PropTypes.shape({
    id: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    product: PropTypes.oneOfType([
      PropTypes.string,
      PropTypes.shape({
        productCode: PropTypes.string,
        code: PropTypes.string,
        identifier: PropTypes.string,
        name: PropTypes.string,
      }),
    ]),
    productCode: PropTypes.string,
    price: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    selling_price: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    sellingPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    quantity: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    qty: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    count: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    cartQty: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    totalPrice: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    total_price: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  }).isRequired,
  idx: PropTypes.number.isRequired,
  onIncrement: PropTypes.func.isRequired,
  onDecrement: PropTypes.func.isRequired,
  onDelete: PropTypes.func.isRequired,
};

function PaymentMethodTile({ mode, icon, title, subtitle, selected, onSelect }) {
  return (
    <button
      type="button"
      onClick={() => onSelect(mode)}
      className={`border-2 rounded-xl p-4 flex flex-col items-center gap-3 cursor-pointer transition-all w-full ${
        selected
          ? "border-indigo-600 bg-indigo-50/60 shadow-sm"
          : "border-slate-200 bg-white hover:border-slate-300"
      }`}
    >
      <div className={`p-2.5 rounded-xl ${selected ? "bg-indigo-600 text-white" : "bg-slate-100 text-slate-600"}`}>
        {icon}
      </div>
      <div className="text-center">
        <p className="text-xs font-black text-slate-800">{title}</p>
        <p className="text-[10px] text-slate-400 mt-0.5">{subtitle}</p>
      </div>
    </button>
  );
}

PaymentMethodTile.propTypes = {
  mode: PropTypes.string.isRequired,
  icon: PropTypes.node.isRequired,
  title: PropTypes.string.isRequired,
  subtitle: PropTypes.string.isRequired,
  selected: PropTypes.bool.isRequired,
  onSelect: PropTypes.func.isRequired,
};

function PaymentSimulator({ mode, receiveAmount, setReceiveAmount, changeAmount, dueAmount }) {
  if (mode === "UPI") {
    return (
      <div className="text-center space-y-2">
        <div className="w-24 h-24 bg-white border border-slate-300 rounded-lg mx-auto flex items-center justify-center shadow-inner relative overflow-hidden p-1.5">
          <div className="w-full h-full bg-slate-800 rounded flex flex-wrap p-1 items-center justify-center text-[7px] text-white font-mono leading-none select-none opacity-80">
            [ DUMMY QR CODE TERMINAL EMULATOR ]
          </div>
        </div>
        <p className="text-xs font-bold text-slate-700">Scan QR Code via smartphone camera</p>
        <p className="text-[11px] text-slate-400 max-w-sm mx-auto">Waiting for webhook trigger from the banking switch API…</p>
      </div>
    );
  }

  if (mode === "Card") {
    return (
      <div className="space-y-3 text-xs max-w-sm mx-auto w-full">
        <div className="bg-gradient-to-br from-slate-800 to-slate-900 text-white p-4 rounded-xl shadow-md space-y-4 font-mono relative">
          <span className="absolute top-3 right-3 text-[10px] bg-white/20 px-2 py-0.5 rounded uppercase font-sans font-bold">EMV Chip</span>
          <p className="text-[11px] text-slate-400 font-sans tracking-wide">POS TERMINAL MOCK</p>
          <p className="text-sm font-black tracking-widest text-center py-1">••••  ••••  ••••  4242</p>
          <div className="flex justify-between text-[10px]">
            <span>EXP: 12/29</span>
            <span>HOLDER: MOCK MERCHANT</span>
          </div>
        </div>
        <p className="text-center text-[11px] text-slate-400 font-semibold">Insert card or tap contactless NFC sensor.</p>
      </div>
    );
  }

  return (
    <div className="space-y-2 text-xs max-w-sm mx-auto w-full">
      <div className="flex justify-between items-center bg-white p-2.5 rounded-lg border border-slate-200">
        <span className="text-slate-500 font-bold">Physical Cash Received</span>
        <div className="flex items-center gap-1 font-mono">
          <span className="text-slate-400 text-sm">₹</span>
          <input
            type="number"
            value={receiveAmount || ""}
            onChange={(e) => setReceiveAmount(Number.parseFloat(e.target.value) || 0)}
            className="w-24 font-black text-right text-slate-900 border-none outline-none p-0 focus:ring-0 text-sm"
          />
        </div>
      </div>
      <div className="flex justify-between items-center text-[11px] px-1 font-semibold text-slate-600">
        <span>Calculated Return Change:</span>
        <span className="font-mono font-bold text-emerald-600">₹{changeAmount.toFixed(2)}</span>
      </div>
      {dueAmount > 0 && (
        <div className="bg-red-50 border border-red-200 p-2 rounded text-[11px] text-red-700 font-bold text-center">
          ⚠️ Short cash! Remaining balance due: ₹{dueAmount.toFixed(2)}
        </div>
      )}
    </div>
  );
}

PaymentSimulator.propTypes = {
  mode: PropTypes.oneOf(["UPI", "Card", "Cash"]).isRequired,
  receiveAmount: PropTypes.oneOfType([PropTypes.string, PropTypes.number]).isRequired,
  setReceiveAmount: PropTypes.func.isRequired,
  changeAmount: PropTypes.number.isRequired,
  dueAmount: PropTypes.number.isRequired,
};

function SupermarketPosBillingScreen({ initialPhone = null }) {
  const router = useRouter();

  const [cartId, setCartId] = useState("");
  const [cart, setCart] = useState(null);
  const [productsCatalog, setProductsCatalog] = useState([]);
  const [pricesList, setPricesList] = useState([]);
  const [loading, setLoading] = useState(false);

  const [searchQuery, setSearchQuery] = useState("");
  const [barcodeInput, setBarcodeInput] = useState("");

  const [customerSearchPhone, setCustomerSearchPhone] = useState("");
  const [searchStatus, setSearchStatus] = useState("idle");
  const [errorMessage, setErrorMessage] = useState("");
  const [activeCustomer, setActiveCustomer] = useState(null);

  const [receiveAmount, setReceiveAmount] = useState(0);
  const [vatRate, setVatRate] = useState(0);
  const [flatDiscount, setFlatDiscount] = useState(0);
  const [shippingFee, setShippingFee] = useState(0);

  const [currentStage, setCurrentStage] = useState("cart");
  const [selectedPaymentMode, setSelectedPaymentMode] = useState("UPI");

  const [showInvoiceModal, setShowInvoiceModal] = useState(false);
  const [completedOrderDetails, setCompletedOrderDetails] = useState(null);

  const barcodeRef = useRef(null);

  const fetchCartDetails = async (targetCartId) => {
    if (!targetCartId) return;
    setLoading(true);
    try {
      const response = await axiosInstance.post("/cart/getCart", {
        identifier: targetCartId,
      });

      if (response.data && Object.keys(response.data).length > 0) {
        setCart(structuredClone(response.data));
        return;
      }

      await createFreshCart(targetCartId);
    } catch (err) {
      await handleFetchCartError(err, targetCartId);
    } finally {
      setLoading(false);
    }
  };

  const createFreshCart = async (targetCartId) => {
    try {
      const created = await axiosInstance.post("/cart/add", {
        customerIdentifier: targetCartId,
        identifier: targetCartId,
        status: 1,
        totalDiscount: 0,
        totalPrice: 0,
        entryDtoList: [],
      });

      const createdCartId =
        created?.data?.identifier ||
        created?.data?.cart?.identifier ||
        created?.data?.cart ||
        targetCartId;

      setCart(created.data ? structuredClone(created.data) : null);
      setCartId(createdCartId);
      return createdCartId;
    } catch (error_) {
      console.error(
        "Could not create a new cart for this customer.",
        "Status:", error_.response?.status,
        "Data:", JSON.stringify(error_.response?.data),
        "Message:", error_.message
      );
      setCart(null);
      setErrorMessage(
        error_.response?.data?.message ||
        `Failed to create a cart for "${targetCartId}". Please check the backend logs.`
      );
      return null;
    }
  };

  const handleFetchCartError = async (err, targetCartId) => {
    const status = err.response?.status;
    const hasEmptyBody =
      !err.response?.data || Object.keys(err.response.data).length === 0;

    console.error(
      "fetchCartDetails failed —",
      "status:", status,
      "data:", JSON.stringify(err.response?.data),
      "message:", err.message,
      "url:", err.config?.url,
    );

    const treatAsNoCart = status === 404 || (status >= 400 && status < 500 && hasEmptyBody);

    if (treatAsNoCart) {
      await createFreshCart(targetCartId);
      return;
    }

    setCart(null);
    setErrorMessage(
      err.response?.data?.message ||
      err.message ||
      "Could not load the cart. Please check your connection and try again."
    );
  };

  const fetchMasterCatalogData = async () => {
    setLoading(true);
    try {
      const prodResponse = await axiosInstance.post("/product/findActiveStatus");
      if (Array.isArray(prodResponse.data)) setProductsCatalog(prodResponse.data);

      const priceResponse = await axiosInstance.post("/price/findActiveStatus");
      if (Array.isArray(priceResponse.data)) setPricesList(priceResponse.data);
    } catch (err) {
      console.error("Master catalog init failed:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchMasterCatalogData(); }, []);

  useEffect(() => {
    if (initialPhone) {
      void performCustomerSearch(initialPhone);
    }
  }, [initialPhone]);

  const resolveSearchFailure = (error) => {
    const statusCode = error.response?.status;
    const serverMessage = error.response?.data?.message || error.message || "";
    const messageSaysNotFound = serverMessage.toLowerCase().includes("not found");

    const isGenuinelyNotFound = statusCode === 404 || messageSaysNotFound;

    if (isGenuinelyNotFound) {
      setSearchStatus("not_found");
      setErrorMessage(serverMessage || "No customer record exists for this phone number.");
      return;
    }

    setSearchStatus("error");
    setErrorMessage(serverMessage || "Could not complete the customer search. Please try again.");
  };

  const performCustomerSearch = async (targetPhone) => {
    const normalizedPhone = targetPhone?.trim();
    if (!normalizedPhone) return;

    setCustomerSearchPhone(normalizedPhone);
    setSearchStatus("searching");
    setErrorMessage("");
    setLoading(true);

    try {
      const response = await axiosInstance.get(`/customer/get?identifier=${encodeURIComponent(normalizedPhone)}`);

      if (response.data?.identifier) {
        setActiveCustomer(response.data);
        setSearchStatus("found");
        setFlatDiscount(0);

        const createdCartId = await createFreshCart(normalizedPhone);
        if (createdCartId) {
          setCartId(createdCartId);
          await fetchCartDetails(createdCartId);
        } else {
          setCartId(normalizedPhone);
        }
      } else {
        setSearchStatus("not_found");
        setErrorMessage("No customer record exists for this phone number.");
      }
    } catch (error) {
      resolveSearchFailure(error);
    } finally {
      setLoading(false);
      setTimeout(() => barcodeRef.current?.focus(), 50);
    }
  };

  const handleCustomerSearch = async (e) => {
    if (e) e.preventDefault();
    const targetPhone = customerSearchPhone.trim();
    if (!targetPhone) return;
    await performCustomerSearch(targetPhone);
  };

  const navigateToCreateCustomerPage = () => {
    const targetPhone = customerSearchPhone.trim();
    router.push(targetPhone ? `/cart/add?phone=${encodeURIComponent(targetPhone)}` : "/cart/add");
  };

  const handleMutateEntryStream = async (productCode, quantityChange, forcedCartId = null) => {
    const activeCartTarget = forcedCartId || cartId;

    if (!activeCartTarget) {
      alert("Please search a customer phone number before adding items.");
      return;
    }

    const resolvedCode = resolveProductCode(productCode);
    if (!resolvedCode) {
      alert(`Invalid product reference: "${productCode}". Could not resolve to a product code string.`);
      return;
    }

    setLoading(true);

    try {
  await axiosInstance.post("/cartentry/addEntry", {
    product: resolvedCode,
    cart: activeCartTarget,
    quantity: quantityChange,
  });
} catch (err) {
  logRequestFailure("cartentry/addEntry", err, {
    product: resolvedCode,
    cart: activeCartTarget,
    quantity: quantityChange,
  });

  alert(
    `Failed to add "${resolvedCode}" to the cart.\n` +
    `Server (${err.response?.status || "no status"}): ${describeServerError(err)}`
  );

  setLoading(false);
  return;
}

    try {
      await axiosInstance.post("/cart/addToCart", { cart: activeCartTarget });
    } catch (err) {
      logRequestFailure("cart/addToCart", err, { cart: activeCartTarget });
 
      await fetchCartDetails(activeCartTarget);
      alert(
        `"${resolvedCode}" was added, but the cart totals could not be recalculated.\n` +
        `Server (${err.response?.status || "no status"}): ${describeServerError(err)}\n` +
        `Totals shown may be out of date — try refreshing.`
      );
      setLoading(false);
      return;
    }

    try {
      await fetchCartDetails(activeCartTarget);
      setBarcodeInput("");
      setSearchQuery("");
      setTimeout(() => barcodeRef.current?.focus(), 100);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteEntry = async (entry) => {
    if (!cartId) return;

    const rawProductCode = resolveProductCode(entry.product) || entry.productCode || null;
    if (!rawProductCode) return;
    if (!confirm(`Remove "${rawProductCode}" from the basket?`)) return;

    setLoading(true);
    try {
      await axiosInstance.delete("/cart/deleteEntry", {
        data: { product: rawProductCode, cart: cartId },
      });
      await fetchCartDetails(cartId);
    } catch (err) {
      logRequestFailure("cart/deleteEntry", err, { product: rawProductCode, cart: cartId });
      alert(
        `Failed to remove "${rawProductCode}".\n` +
        `Server (${err.response?.status || "no status"}): ${describeServerError(err)}`
      );
    } finally {
      setLoading(false);
    }
  };

  const resetCartScreenState = () => {
    setCart(null);
    setCartId("");
    setActiveCustomer(null);
    setCustomerSearchPhone("");
    setSearchStatus("idle");
    setErrorMessage("");
    setReceiveAmount(0);
    setFlatDiscount(0);
    setVatRate(0);
    setShippingFee(0);
  };


  const resetCartOnly = () => {
    setCart(null);
    setReceiveAmount(0);
    setFlatDiscount(0);
    setVatRate(0);
    setShippingFee(0);
  };

  const handleClearWholeCart = async () => {
    if (!cartId) return;
    if (!confirm("⚠️ This will delete the cart and all items for this customer. The customer record stays, and you can start a new cart for them right away. Continue?")) return;

    setLoading(true);
    try {
      const response = await axiosInstance.delete("/cart/deleteCart", {
        data: { identifier: cartId },
      });
      if (response.data === true) {
        resetCartOnly();
      }
    } catch (err) {
      logRequestFailure("cart/deleteCart", err, { identifier: cartId });
      alert(
        `Failed to delete the cart.\n` +
        `Server (${err.response?.status || "no status"}): ${describeServerError(err)}`
      );
    } finally {
      setLoading(false);
    }
  };

  const handleBarcodeSubmit = (e) => {
    e.preventDefault();
    if (!barcodeInput.trim()) return;

    const matched = matchCatalogProduct(productsCatalog, barcodeInput);
    const productCode = matched
      ? (matched.productCode || matched.code || matched.identifier)
      : barcodeInput.trim();

    handleMutateEntryStream(productCode, 1);
  };

  const handleProceedToCheckoutGateway = () => {
    if (!cartId || entries.length === 0) {
      alert("Cannot process checkout on an empty customer session.");
      return;
    }
    if (receiveAmount === 0) {
      setReceiveAmount(grandTotalAmount);
    }
    setCurrentStage("checkout_gateway");
  };

  const handleFinalizePayment = async () => {
    setLoading(true);
    try {
      const response = await axiosInstance.post("/orders/create", {
        identifier: cartId.trim(),
        paymentMode: selectedPaymentMode,
        totalPrice: grandTotalAmount,
        totalDiscount: totalSavings,
      });

      if (response.data) {
        setCompletedOrderDetails(buildCompletedOrder({
          response, activeCustomer, cartId, entries,
          sellingSubtotal, vatAmount, parsedShippingFee, parsedFlatDiscount,
          grandTotalAmount, selectedPaymentMode, parsedReceiveAmount, changeAmount,
        }));
        setCurrentStage("cart");
        setShowInvoiceModal(true);
      }
    } catch (err) {
      logRequestFailure("orders/create", err, {
        identifier: cartId,
        paymentMode: selectedPaymentMode,
        totalPrice: grandTotalAmount,
        totalDiscount: totalSavings,
      });
      alert(
        `Checkout failed.\n` +
        `Server (${err.response?.status || "no status"}): ${describeServerError(err)}`
      );
    } finally {
      setLoading(false);
    }
  };

  const handleCloseInvoiceModalAndRoute = () => {
    setShowInvoiceModal(false);
    setCompletedOrderDetails(null);
    resetCartScreenState();
    router.push(`/cart/list`);
  };

  const handleProductCardClick = (prod) => {
    const productCode = prod.productCode || prod.code || prod.identifier || prod.name;
    if (!productCode) return;
    handleMutateEntryStream(productCode, 1);
  };

  const {
    entries,
    mrpSubtotal,
    sellingSubtotal,
    itemDiscount,
    vatAmount,
    parsedFlatDiscount,
    parsedShippingFee,
    parsedReceiveAmount,
    grandTotalAmount,
    totalSavings,
    changeAmount,
    dueAmount,
  } = deriveBillingTotals(cart, vatRate, flatDiscount, shippingFee, receiveAmount);

  const filteredProducts = filterProducts(productsCatalog, searchQuery);

  return (
    <Layout>
      <div className="flex flex-col h-screen bg-slate-100 text-slate-800 font-sans overflow-hidden select-none">

        <header className="flex justify-between items-center bg-white px-6 py-2 shadow-sm border-b border-slate-200">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-emerald-600 rounded-lg flex items-center justify-center font-black text-white text-base">POS</div>
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">Terminal Desk</span>
          </div>
          <div className="flex items-center gap-6 text-xs text-slate-600 font-medium">
            <div className="relative">
              <span className="absolute -top-2 -right-1.5 bg-red-500 text-white text-[9px] font-bold px-1 rounded-full">{entries.length}</span>
              <ShoppingCart className="w-4 h-4 text-slate-500" />
            </div>
            <div className="flex items-center gap-2">
              <div className="w-7 h-7 bg-indigo-600 rounded-full flex items-center justify-center text-white text-xs font-bold">👤</div>
              <div>
                <p className="text-[10px] text-slate-400 font-bold uppercase tracking-tight">Active Customer</p>
                <p className="font-bold text-slate-800 -mt-0.5 truncate max-w-[150px]">
                  {activeCustomer ? activeCustomer.name || activeCustomer.identifier : "None Assigned"}
                </p>
              </div>
            </div>
      
            <button
              type="button"
              onClick={navigateToCreateCustomerPage}
              className="flex items-center gap-1.5 bg-indigo-600 hover:bg-indigo-700 text-white font-bold px-3 py-1.5 rounded-lg text-[11px] uppercase tracking-wide transition shadow-sm"
            >
              <UserPlus className="w-3.5 h-3.5" />
              <span>New Customer</span>
            </button>
          </div>
        </header>

        <main className="flex flex-1 overflow-hidden p-4 gap-4">

          {currentStage === "cart" ? (
            <>
              <section className="w-7/12 bg-white rounded-xl shadow-sm border border-slate-200 flex flex-col overflow-hidden p-4">
                <div className="bg-slate-50 p-3 rounded-xl border border-slate-200 mb-3 text-xs">
                  <form onSubmit={handleCustomerSearch} className="flex gap-2 items-center">
                    <div className="relative flex-1">
                      <Search className="absolute left-2.5 top-2.5 w-3.5 h-3.5 text-slate-400" />
                      <input
                        type="tel"
                        value={customerSearchPhone}
                        onChange={(e) => {
                          setCustomerSearchPhone(e.target.value);
                          if (["error", "found", "not_found"].includes(searchStatus)) {
                            setSearchStatus("idle");
                            setErrorMessage("");
                          }
                        }}
                        placeholder="Enter customer phone to activate terminal session…"
                        className="w-full bg-white border border-slate-300 rounded-lg pl-8 pr-3 py-2 text-slate-800 font-mono font-bold focus:outline-none focus:border-indigo-500"
                      />
                    </div>
                    <button
                      type="submit"
                      disabled={loading}
                      className="bg-slate-800 text-white font-bold px-4 py-2 rounded-lg hover:bg-slate-900 transition disabled:opacity-50 shadow-sm whitespace-nowrap"
                    >
                      {searchStatus === "searching" ? "Searching…" : "Search / Open"}
                    </button>
                  </form>

                  {searchStatus === "found" && !errorMessage && (
                    <div className="mt-2 flex items-center gap-1.5 text-emerald-600 font-bold bg-emerald-50 border border-emerald-200 px-2.5 py-1.5 rounded-md">
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      Terminal active — Live Basket "{cartId}" Loaded.
                    </div>
                  )}

                  {searchStatus === "found" && errorMessage && (
                    <div className="mt-2 flex items-center justify-between gap-2 bg-amber-50 border border-amber-200 px-2.5 py-1.5 rounded-md text-amber-800 font-bold text-xs">
                      <div className="flex items-center gap-1.5">
                        <AlertCircle className="w-3.5 h-3.5 flex-shrink-0" />
                        <span>Cart could not be loaded: {errorMessage}</span>
                      </div>
                    
                    </div>
                  )}

             
                  {searchStatus === "not_found" && (
                    <div className="mt-3 bg-red-50 border border-red-200 p-4 rounded-xl flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs text-red-800 font-bold shadow-sm">
                      <div className="flex items-center gap-2">
                        <AlertCircle className="w-4 h-4 text-red-600 flex-shrink-0" />
                        <div>
                          <p className="text-red-950 font-black text-sm">Customer not found</p>
                          <p className="text-red-600 font-mono text-[11px] mt-0.5 font-bold">
                            {errorMessage || `No record exists for "${customerSearchPhone}".`}
                          </p>
                        </div>
                      </div>
                      <button
                        type="button"
                        onClick={navigateToCreateCustomerPage}
                        className="flex items-center justify-center gap-2 bg-indigo-600 hover:bg-indigo-700 text-white font-black px-5 py-2.5 rounded-lg shadow-md transition-all transform active:scale-95 whitespace-nowrap text-xs uppercase tracking-wider"
                      >
                        <UserPlus className="w-4 h-4" />
                        <span>Create New Customer</span>
                      </button>
                    </div>
                  )}

                  {searchStatus === "error" && (
                    <div className="mt-2 flex items-center justify-between gap-2 bg-red-50 border border-red-200 px-2.5 py-1.5 rounded-md text-red-700 font-bold text-xs">
                      <div className="flex items-center gap-1.5">
                        <AlertCircle className="w-3.5 h-3.5 flex-shrink-0" />
                        <span>{"Customer not found."}</span>
                      </div>
                     
                    </div>
                  )}
                </div>

                <div className="bg-slate-50 border border-slate-200 px-3 py-1.5 rounded-lg font-mono text-slate-600 flex justify-between items-center text-xs mb-3">
                  <div className="flex items-center gap-1">
                    <span className="text-[10px] uppercase font-sans text-slate-400 font-bold">Active Cart:</span>
                    <span className="font-bold text-indigo-600">{cartId ? cartId.toUpperCase() : "AWAITING PHONE VERIFICATION"}</span>
                  </div>
                  {cartId && (
                    <button
                      type="button"
                      onClick={handleClearWholeCart}
                      disabled={loading}
                      className="bg-red-50 hover:bg-red-100 text-red-600 border border-red-200 font-sans font-bold px-2.5 py-1 rounded-md transition-colors flex items-center gap-1 text-[11px] disabled:opacity-50"
                    >
                      <Trash2 className="w-3 h-3" />
                      <span>Delete Cart</span>
                    </button>
                  )}
                </div>

                <div className="flex-1 overflow-auto border border-slate-200 rounded-xl mb-3 bg-slate-50/30">
                  <table className="w-full text-left border-collapse">
                    <thead>
                      <tr className="bg-slate-50 border-b border-slate-200 text-slate-500 text-[11px] font-bold uppercase sticky top-0 z-10">
                        <th className="p-2 text-center">#</th>
                        <th className="p-2">SKU Reference</th>
                        <th className="p-2 text-right">MRP</th>
                        <th className="p-2 text-right">Rate</th>
                        <th className="p-2 text-center">Qty</th>
                        <th className="p-2 text-right">Total</th>
                        <th className="p-2 text-center">Del</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100 text-xs font-medium text-slate-700">
                      {entries.length === 0 ? (
                        <tr>
                          <td colSpan={7} className="text-center py-20 text-slate-400">
                            {cartId ? "Basket empty. Select products or scan barcodes." : "Search phone identifier to initialize terminal stream."}
                          </td>
                        </tr>
                      ) : (
                        entries.map((entry, idx) => (
                          <CartRow
                            key={entry.id || `${resolveProductCode(entry.product)}-${idx}`}
                            entry={entry}
                            idx={idx}
                            onIncrement={(e, code) => handleMutateEntryStream(code, 1)}
                            onDecrement={(e, code) => handleMutateEntryStream(code, -1)}
                            onDelete={handleDeleteEntry}
                          />
                        ))
                      )}
                    </tbody>
                  </table>
                </div>

                <div className="grid grid-cols-2 gap-4 border-t border-slate-100 pt-3 text-xs">
                  <div className="space-y-1.5 bg-slate-50/50 p-2.5 rounded-xl border border-slate-200">
                    <div className="flex justify-between items-center text-slate-400">
                      <span>Gross MRP Subtotal</span>
                      <span className="font-mono line-through">₹{mrpSubtotal.toFixed(2)}</span>
                    </div>
                    {itemDiscount > 0 && (
                      <div className="flex justify-between items-center text-emerald-600 font-semibold">
                        <span>Catalog Savings</span>
                        <span className="font-mono">−₹{itemDiscount.toFixed(2)}</span>
                      </div>
                    )}
                    <div className="flex justify-between items-center">
                      <span className="text-slate-500 font-semibold">VAT (%)</span>
                      <input
                        type="number" min="0" max="100"
                        value={vatRate || ""}
                        onChange={(e) => setVatRate(Number.parseFloat(e.target.value) || 0)}
                        placeholder="0"
                        className="w-16 bg-white border border-slate-300 rounded px-1.5 py-0.5 text-right font-mono font-bold"
                      />
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-slate-500 font-semibold">Shipping (₹)</span>
                      <input
                        type="number" min="0"
                        value={shippingFee || ""}
                        onChange={(e) => setShippingFee(Number.parseFloat(e.target.value) || 0)}
                        placeholder="0"
                        className="w-16 bg-white border border-slate-300 rounded px-1.5 py-0.5 text-right font-mono font-bold"
                      />
                    </div>
                  </div>

                  <div className="space-y-2 bg-slate-50/50 p-2.5 rounded-xl border border-slate-200 flex flex-col justify-between">
                    <div>
                      <div className="flex justify-between items-center border-b border-slate-200 pb-1">
                        <span className="text-slate-500 font-bold uppercase tracking-tight text-[10px]">Net Value</span>
                        <span className="font-mono text-sm font-black text-slate-900">₹{sellingSubtotal.toFixed(2)}</span>
                      </div>
                      <div className="flex justify-between items-center mt-1">
                        <span className="text-slate-500">Ad-hoc Coupon</span>
                        <input
                          type="number" min="0" max={sellingSubtotal}
                          value={flatDiscount || ""}
                          onChange={(e) => setFlatDiscount(Number.parseFloat(e.target.value) || 0)}
                          placeholder="0.00"
                          className="w-20 border rounded px-1.5 py-0.5 text-right font-mono text-red-500 bg-white border-slate-300"
                        />
                      </div>
                    </div>

                    <div>
                      <div className="flex justify-between items-center border-t border-slate-200 pt-1.5 font-black text-slate-900 mb-2">
                        <span className="text-indigo-600 uppercase tracking-wider text-[11px]">Grand Total Due</span>
                        <span className="font-mono text-base text-emerald-600">₹{grandTotalAmount.toFixed(2)}</span>
                      </div>
                      <button
                        type="button"
                        onClick={handleProceedToCheckoutGateway}
                        disabled={entries.length === 0 || loading}
                        className="w-full bg-indigo-600 hover:bg-indigo-700 disabled:opacity-40 text-white font-black py-2 rounded-lg shadow-sm transition text-xs uppercase tracking-wider flex items-center justify-center gap-1"
                      >
                        <span>Commit Checkout</span>
                        <ArrowRight className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </div>
              </section>

              <section className="w-5/12 bg-white rounded-xl shadow-sm border border-slate-200 flex flex-col overflow-hidden p-4">
                <div className="space-y-2 mb-4">
                  <div className="relative">
                    <Search className="absolute left-3 top-2.5 w-4 h-4 text-slate-400" />
                    <input
                      type="text"
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                      placeholder="Filter by description or code…"
                      className="w-full bg-slate-50 border border-slate-200 rounded-lg pl-9 pr-4 py-2 text-xs font-medium focus:outline-none focus:bg-white"
                    />
                  </div>

                  <form onSubmit={handleBarcodeSubmit} className="relative text-xs">
                    <Barcode className="absolute left-3 top-2.5 w-4 h-4 text-emerald-500/80" />
                    <input
                      ref={barcodeRef}
                      type="text"
                      value={barcodeInput}
                      onChange={(e) => {
                        setBarcodeInput(e.target.value);
                        setSearchQuery(e.target.value);
                      }}
                      placeholder="Scan SKU barcode stream directly…"
                      className="w-full bg-slate-900 border border-slate-800 rounded-xl pl-9 pr-12 py-2 font-mono text-emerald-400 focus:outline-none placeholder-slate-600"
                    />
                    <button type="submit" className="absolute right-2 top-1.5 bg-red-600 text-white px-2 py-0.5 rounded shadow-sm text-xs">↵</button>
                  </form>
                </div>

                <div className="flex-1 overflow-y-auto grid grid-cols-3 gap-2.5 content-start pr-1">
                  {filteredProducts.map((prod) => (
                    <ProductCard
                      key={prod.id || resolveProductCode(prod)}
                      prod={prod}
                      pricesList={pricesList}
                      onSelect={handleProductCardClick}
                    />
                  ))}
                </div>
              </section>
            </>
          ) : (
            <section className="w-full bg-white rounded-xl shadow-md border border-slate-200 p-6 flex flex-col max-w-4xl mx-auto overflow-hidden">

              <div className="flex items-center justify-between border-b border-slate-200 pb-4 mb-6">
                <button
                  type="button"
                  onClick={() => setCurrentStage("cart")}
                  className="flex items-center gap-2 text-slate-600 hover:text-slate-900 transition font-bold text-xs uppercase bg-slate-100 px-3 py-2 rounded-lg"
                >
                  <ArrowLeft className="w-4 h-4" />
                  <span>Modify Basket / Back</span>
                </button>
                <div className="text-right">
                  <p className="text-[10px] text-slate-400 font-bold uppercase">Terminal Gateway Session</p>
                  <p className="text-xs font-mono font-bold text-slate-700">Client Block Ref: <span className="text-indigo-600">{cartId}</span></p>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-12 gap-6 flex-1 overflow-y-auto">

                <div className="md:col-span-7 space-y-4">
                  <h2 className="text-sm font-black text-slate-800 uppercase tracking-wider">Select Electronic Payment Engine</h2>

                  <div className="grid grid-cols-3 gap-3">
                    <PaymentMethodTile
                      mode="UPI"
                      icon={<QrCode className="w-6 h-6" />}
                      title="UPI / QR Code"
                      subtitle="Instant Digital Wire"
                      selected={selectedPaymentMode === "UPI"}
                      onSelect={setSelectedPaymentMode}
                    />
                    <PaymentMethodTile
                      mode="Card"
                      icon={<CreditCard className="w-6 h-6" />}
                      title="Bank Card"
                      subtitle="POS Chip / Swipe"
                      selected={selectedPaymentMode === "Card"}
                      onSelect={setSelectedPaymentMode}
                    />
                    <PaymentMethodTile
                      mode="Cash"
                      icon={<Banknote className="w-6 h-6" />}
                      title="Physical Cash"
                      subtitle="Manual Tender"
                      selected={selectedPaymentMode === "Cash"}
                      onSelect={setSelectedPaymentMode}
                    />
                  </div>

                  <div className="bg-slate-50 border border-slate-200 rounded-xl p-4 min-h-[180px] flex flex-col justify-center">
                    <PaymentSimulator
                      mode={selectedPaymentMode}
                      receiveAmount={receiveAmount}
                      setReceiveAmount={setReceiveAmount}
                      changeAmount={changeAmount}
                      dueAmount={dueAmount}
                    />
                  </div>
                </div>

                <div className="md:col-span-5 bg-slate-50 border border-slate-200 rounded-xl p-4 flex flex-col justify-between">
                  <div>
                    <h3 className="text-xs font-black uppercase tracking-wider text-slate-400 mb-3">Order Summary Snapshot</h3>
                    <div className="space-y-2 border-b border-slate-200 pb-3 max-h-[140px] overflow-y-auto pr-1">
                      {entries.map((item, idx) => {
                        const code = resolveProductCode(item.product) || "Item";
                        const { rate } = resolveEntryPrices(item);
                        const qty = resolveEntryQuantity(item, rate);
                        return (
                          <div key={item.id || `${code}-${idx}`} className="flex justify-between text-xs text-slate-600 font-mono">
                            <span className="truncate max-w-[130px] uppercase font-bold">{code} (×{Math.floor(qty)})</span>
                            <span>₹{(rate * qty).toFixed(2)}</span>
                          </div>
                        );
                      })}
                    </div>

                    <div className="space-y-1.5 text-xs pt-3 font-mono font-medium">
                      <div className="flex justify-between text-slate-500">
                        <span>Cart Subtotal</span>
                        <span>₹{sellingSubtotal.toFixed(2)}</span>
                      </div>
                      {vatAmount > 0 && (
                        <div className="flex justify-between text-slate-500">
                          <span>VAT Surcharge</span>
                          <span>₹{vatAmount.toFixed(2)}</span>
                        </div>
                      )}
                      {parsedShippingFee > 0 && (
                        <div className="flex justify-between text-slate-500">
                          <span>Logistics/Shipping</span>
                          <span>₹{parsedShippingFee.toFixed(2)}</span>
                        </div>
                      )}
                      {parsedFlatDiscount > 0 && (
                        <div className="flex justify-between text-red-600 font-bold">
                          <span>Coupon Reductions</span>
                          <span>−₹{parsedFlatDiscount.toFixed(2)}</span>
                        </div>
                      )}
                    </div>
                  </div>

                  <div className="pt-4 border-t border-slate-200">
                    <div className="flex justify-between items-baseline mb-4">
                      <span className="text-xs font-black text-slate-800 uppercase tracking-tight">Grand Total</span>
                      <span className="text-xl font-mono font-black text-emerald-600">₹{grandTotalAmount.toFixed(2)}</span>
                    </div>

                    <button
                      type="button"
                      onClick={handleFinalizePayment}
                      disabled={loading || (selectedPaymentMode === "Cash" && dueAmount > 0)}
                      className="w-full bg-emerald-600 hover:bg-emerald-700 disabled:opacity-40 text-white font-black py-3 rounded-xl text-xs uppercase tracking-wider shadow transition flex items-center justify-center gap-2"
                    >
                      {loading ? "Processing Secure Core..." : "Finalize Payment & Print Receipt"}
                    </button>
                  </div>
                </div>

              </div>
            </section>
          )}
        </main>

        {showInvoiceModal && completedOrderDetails && (
          <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center z-50 p-4 transition-all">
            <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 w-full max-w-md overflow-hidden flex flex-col">

              <div className="bg-emerald-600 text-white p-5 text-center relative">
                <button
                  onClick={handleCloseInvoiceModalAndRoute}
                  className="absolute right-4 top-4 text-emerald-100 hover:text-white p-1 rounded-full hover:bg-emerald-700/50 transition"
                  type="button"
                >
                  <X className="w-5 h-5" />
                </button>
                <div className="w-12 h-12 bg-white/20 rounded-full flex items-center justify-center mx-auto mb-2 shadow-inner">
                  <CheckCircle2 className="w-7 h-7 text-white" />
                </div>
                <h2 className="text-lg font-black uppercase tracking-wide">Transaction Successful</h2>
                <p className="text-xs text-emerald-100 font-mono mt-0.5">ID: {completedOrderDetails.orderId}</p>
              </div>

              <div className="p-5 flex-1 overflow-y-auto space-y-4 max-h-[60vh] font-sans">
                <div className="grid grid-cols-2 text-xs border-b border-dashed border-slate-200 pb-3 font-medium text-slate-500 font-mono">
                  <div>
                    <span className="block text-[10px] uppercase font-sans font-bold text-slate-400">Customer</span>
                    <span className="text-slate-800 font-bold">{completedOrderDetails.customerName}</span>
                  </div>
                  <div className="text-right">
                    <span className="block text-[10px] uppercase font-sans font-bold text-slate-400">Time / Terminal</span>
                    <span className="text-slate-800 font-bold">{completedOrderDetails.timestamp} | Desk-01</span>
                  </div>
                </div>

                <div>
                  <span className="text-[10px] uppercase font-bold tracking-wider text-slate-400 block mb-2">Itemized Basket</span>
                  <div className="space-y-2 max-h-[160px] overflow-y-auto pr-1">
                    {completedOrderDetails.itemsSnapshot.map((item, index) => {
                      const code = resolveProductCode(item.product) || item.productCode || "Item";
                      const { rate } = resolveEntryPrices(item);
                      const qty = resolveEntryQuantity(item, rate);
                      const total = resolveLineTotal(item, rate, qty);

                      return (
                        <div key={item.id || `${code}-${index}`} className="flex justify-between items-start text-xs bg-slate-50 p-2 rounded-lg border border-slate-100">
                          <div>
                            <p className="font-mono font-bold text-slate-800 uppercase">{code}</p>
                            <p className="text-[10px] text-slate-400 font-semibold">₹{rate.toFixed(2)} × {Math.floor(qty)}</p>
                          </div>
                          <span className="font-mono font-bold text-slate-900">₹{total.toFixed(2)}</span>
                        </div>
                      );
                    })}
                  </div>
                </div>

                <div className="bg-slate-50 rounded-xl p-3 border border-slate-200 text-xs space-y-1.5 font-medium font-mono">
                  <div className="flex justify-between text-slate-500">
                    <span>Basket Subtotal</span>
                    <span>₹{completedOrderDetails.subtotal.toFixed(2)}</span>
                  </div>
                  {completedOrderDetails.vat > 0 && (
                    <div className="flex justify-between text-slate-500">
                      <span>Value Added Tax</span>
                      <span>₹{completedOrderDetails.vat.toFixed(2)}</span>
                    </div>
                  )}
                  {completedOrderDetails.shipping > 0 && (
                    <div className="flex justify-between text-slate-500">
                      <span>Logistics / Shipping</span>
                      <span>₹{completedOrderDetails.shipping.toFixed(2)}</span>
                    </div>
                  )}
                  {completedOrderDetails.discount > 0 && (
                    <div className="flex justify-between text-emerald-600 font-bold">
                      <span>Ad-hoc Reductions</span>
                      <span>−₹{completedOrderDetails.discount.toFixed(2)}</span>
                    </div>
                  )}
                  <div className="flex justify-between text-slate-500">
                    <span>Payment Mode</span>
                    <span className="font-bold text-slate-900 uppercase tracking-wider">{completedOrderDetails.paymentModeUsed}</span>
                  </div>
                  <div className="flex justify-between border-t border-slate-200 pt-2 font-black text-slate-900 text-sm font-sans">
                    <span className="text-indigo-600 uppercase tracking-tight text-xs">Total Amount Paid</span>
                    <span className="font-mono text-emerald-600">₹{completedOrderDetails.grandTotal.toFixed(2)}</span>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3 text-center">
                  <div className="bg-slate-100 p-2.5 rounded-xl border border-slate-200">
                    <span className="block text-[9px] uppercase font-bold text-slate-400 mb-0.5">
                      {completedOrderDetails.paymentModeUsed === "Cash" ? "Tendered Cash" : "Authorized Amt"}
                    </span>
                    <span className="font-mono font-black text-slate-800 text-sm">₹{completedOrderDetails.tendered.toFixed(2)}</span>
                  </div>
                  <div className="bg-indigo-50 border border-indigo-100 p-2.5 rounded-xl">
                    <span className="block text-[9px] uppercase font-bold text-indigo-400 mb-0.5">Return Change</span>
                    <span className="font-mono font-black text-indigo-600 text-sm">₹{completedOrderDetails.change.toFixed(2)}</span>
                  </div>
                </div>
              </div>

              <div className="bg-slate-50 p-4 border-t border-slate-200 flex gap-3 text-xs">
                <button
                  type="button"
                  onClick={() => globalThis.print()}
                  className="flex-1 border border-slate-300 hover:border-slate-400 bg-white text-slate-700 font-bold py-2.5 px-4 rounded-xl shadow-sm transition flex items-center justify-center gap-2"
                >
                  <Printer className="w-4 h-4" />
                  <span>Print Receipt</span>
                </button>
                <button
                  type="button"
                  onClick={handleCloseInvoiceModalAndRoute}
                  className="flex-1 bg-slate-900 hover:bg-slate-800 text-white font-black py-2.5 px-4 rounded-xl shadow-md transition flex items-center justify-center gap-2 uppercase tracking-wide"
                >
                  <span>Next Order</span>
                  <ArrowRight className="w-4 h-4" />
                </button>
              </div>

            </div>
          </div>
        )}

      </div>
    </Layout>
  );
}

SupermarketPosBillingScreen.propTypes = {
  initialPhone: PropTypes.string,
};

function LoadingFallback() {
  return (
    <Layout>
      <div className="flex flex-col h-screen bg-slate-100 text-slate-800 font-sans overflow-hidden select-none items-center justify-center">
        <div className="text-center">
          <div className="w-12 h-12 bg-indigo-600 rounded-lg flex items-center justify-center font-black text-white text-base mx-auto mb-4 animate-pulse">POS</div>
          <p className="text-sm font-semibold text-slate-600">Initializing terminal…</p>
        </div>
      </div>
    </Layout>
  );
}

function SearchParamsWrapper() {
  const searchParams = useSearchParams();
  const phoneFromQuery = searchParams.get("customerPhone") || searchParams.get("phone");

  return <SupermarketPosBillingScreen initialPhone={phoneFromQuery} />;
}

export default function Page() {
  return (
    <Suspense fallback={<LoadingFallback />}>
      <SearchParamsWrapper />
    </Suspense>
  );
}