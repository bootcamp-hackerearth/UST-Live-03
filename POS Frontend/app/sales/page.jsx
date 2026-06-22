"use client";

import { useState, useEffect, useCallback, useMemo, useRef } from "react";
import Image from "next/image";
import { useRouter, useSearchParams } from "next/navigation";
import api from "@/api/axios";

function useSidebarOpen() {
  const [isOpen, setIsOpen] = useState(true);
  useEffect(() => {
    const handleToggle = (e) => setIsOpen(e.detail?.isOpen ?? true);
    globalThis.addEventListener("sidebar-toggle", handleToggle);
    return () => globalThis.removeEventListener("sidebar-toggle", handleToggle);
  }, []);
  return isOpen;
}

/* ============================================================
   REDESIGNED COLOR PALETTE — Black & White Theme
   Premium aesthetic with high contrast and refined spacing
   ============================================================ */
const C = {
  black: "#000000",
  white: "#FFFFFF",
  darkGray: "#1a1a1a",
  mediumGray: "#3a3a3a",
  lightGray: "#6b7280",
  veryLightGray: "#e5e7eb",
  ultraLightGray: "#f5f5f5",
  panelBg: "#fafbfc",
  
  // Accent colors (minimal use)
  error: "#dc2626",
  success: "#16a34a",
  successBg: "#ecfdf5",
  errorBg: "#fef2f2",
  
  // Text hierarchy
  primary: "#000000",
  secondary: "#6b7280",
  tertiary: "#9ca3af",
  blue: "#2563eb",
};

const inputSt = {
  width: "100%",
  height: "42px",
  padding: "0 14px",
  border: `1px solid ${C.veryLightGray}`,
  borderRadius: "8px",
  fontSize: "14px",
  outline: "none",
  boxSizing: "border-box",
  backgroundColor: C.white,
  color: C.primary,
  transition: "all 0.2s ease",
  fontFamily: "'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif",
};

const selectSt = {
  ...inputSt,
  appearance: "none",
  WebkitAppearance: "none",
  MozAppearance: "none",
  backgroundImage: "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%236b7280' stroke-width='2'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E\")",
  backgroundRepeat: "no-repeat",
  backgroundPosition: "right 12px center",
  backgroundSize: "16px",
  paddingRight: "36px",
};

const labelSt = {
  fontSize: "12px",
  fontWeight: "600",
  color: C.secondary,
  letterSpacing: "0.02em",
  display: "block",
  marginBottom: "6px",
  textTransform: "uppercase",
};

const modalOverlaySt = {
  position: "fixed",
  inset: 0,
  background: "rgba(0,0,0,0.5)",
  zIndex: 10000,
  display: "flex",
  alignItems: "flex-start",
  justifyContent: "center",
  paddingTop: "70px",
};

function SalesSelect({ value, onChange, options, wrapperStyle = {}, selectStyle = {} }) {
  const [isOpen, setIsOpen] = useState(false);
  return (
    <div style={{ position: "relative", width: "100%", ...wrapperStyle }}>
      <select
        value={value}
        onChange={(e) => {
          onChange(e);
          setIsOpen(false);
        }}
        onMouseDown={() => setIsOpen(true)}
        onFocus={() => setIsOpen(true)}
        onBlur={() => setIsOpen(false)}
        style={{ ...selectSt, ...selectStyle }}
      >
        {options.map(opt => (
          <option key={opt.value ?? opt.label} value={opt.value}>{opt.label}</option>
        ))}
      </select>
      <span style={{ position: "absolute", right: "12px", top: "50%", transform: `translateY(-50%) rotate(${isOpen ? 180 : 0}deg)`, pointerEvents: "none", color: C.lightGray, fontSize: "13px", transition: "transform 0.2s ease" }}>▼</span>
    </div>
  );
}

function extractList(payload) {
  if (Array.isArray(payload)) return payload;
  if (!payload || typeof payload !== "object") return [];
  if (Array.isArray(payload.dtoList)) return payload.dtoList;
  if (Array.isArray(payload.data)) return payload.data;
  if (Array.isArray(payload.content)) return payload.content;
  if (Array.isArray(payload.list)) return payload.list;
  if (Array.isArray(payload.records)) return payload.records;
  return [];
}

function getEntityLabel(item) {
  if (typeof item === "string") return item;
  if (!item || typeof item !== "object") return "";
  return item.name || item.subCategoryName || item.categoryName || item.brandName || item.title || item.identifier || item.id || "";
}

function getEntityValue(item) {
  if (typeof item === "string") return item;
  if (!item || typeof item !== "object") return "";
  return item.identifier ?? item.id ?? getEntityLabel(item);
}

function ProductThumb({ url, size = 34 }) {
  if (url) {
    return <Image src={url} alt="" width={size} height={size} unoptimized style={{ width: size, height: size, objectFit: "cover", borderRadius: "6px", border: `1px solid ${C.veryLightGray}` }} />;
  }
  return (
    <div style={{ width: size + 16, height: size + 16, display: "flex", alignItems: "center", justifyContent: "center", border: `1px dashed ${C.veryLightGray}`, borderRadius: "8px", background: C.ultraLightGray }}>
      <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="#d1d5db" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round">
        <path d="M12 2 L21 7 L21 17 L12 22 L3 17 L3 7 Z" />
        <path d="M12 22 L12 12" />
        <path d="M21 7 L12 12 L3 7" />
        <path d="M16.5 4.5 L7.5 9.5" />
      </svg>
    </div>
  );
}

export default function SalesPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const isSidebarOpen = useSidebarOpen();

  const [pageTab, setPageTab] = useState(() => {
    const requested = searchParams?.get("tab");
    return requested === "orders" ? "orders" : "pos";
  });

  useEffect(() => {
    const requested = searchParams?.get("tab");
    setPageTab(requested === "orders" ? "orders" : "pos");
  }, [searchParams]);

  const [phoneSearch, setPhoneSearch] = useState("");
  const [searchingCustomer, setSearchingCustomer] = useState(false);
  const [customerFound, setCustomerFound] = useState(null);
  const [bannerDismissed, setBannerDismissed] = useState(false);
  const [allCustomersList, setAllCustomersList] = useState([]);
  const [filteredCustomers, setFilteredCustomers] = useState([]);
  const [showCustomerDropdown, setShowCustomerDropdown] = useState(false);
  const dropdownRef = useRef(null);

  const [showAddModal, setShowAddModal] = useState(false);
  const [newCustomer, setNewCustomer] = useState({ identifier: "", customerName: "", email: "" });
  const [addingCustomer, setAddingCustomer] = useState(false);
  const [addCustomerError, setAddCustomerError] = useState("");

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [productSearch, setProductSearch] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  const [selectedBrand, setSelectedBrand] = useState("");

  const [showCategoryDrawer, setShowCategoryDrawer] = useState(false);
  const [categorySearchTerm, setCategorySearchTerm] = useState("");
  const [showBrandDrawer, setShowBrandDrawer] = useState(false);
  const [brandSearchTerm, setBrandSearchTerm] = useState("");

  const [warehouses, setWarehouses] = useState([]);
  const [selectedWarehouse, setSelectedWarehouse] = useState("");

  const [cart, setCart] = useState(null);
  const [entries, setEntries] = useState([]);

  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [showOrderSuccess, setShowOrderSuccess] = useState(false);
  const [paymentType, setPaymentType] = useState("Cash");

  const [actionLoading, setActionLoading] = useState(false);
  const [processingEntry, setProcessingEntry] = useState(null);
  const [error, setError] = useState("");
  const [toast, setToast] = useState(null);

  const [orders, setOrders] = useState([]);
  const [ordersLoading, setOrdersLoading] = useState(false);
  const [ordersError, setOrdersError] = useState("");
  const [orderSearch, setOrderSearch] = useState("");
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [orderDetailLoading, setOrderDetailLoading] = useState(false);
  const [showOrderDetail, setShowOrderDetail] = useState(false);

  const [justPlacedOrderId, setJustPlacedOrderId] = useState(null);

  const [showCalculator, setShowCalculator] = useState(false);
  const [calcDisplay, setCalcDisplay] = useState("0");
  const [calcPrev, setCalcPrev] = useState(null);
  const [calcOp, setCalcOp] = useState(null);
  const [calcWaitingForNew, setCalcWaitingForNew] = useState(false);

  const today = new Date().toLocaleDateString("en-GB");

  const showToast = useCallback((msg, type = "success") => {
    setToast({ msg, type });
    setTimeout(() => setToast(null), 3000);
  }, []);

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowCustomerDropdown(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const fetchCustomers = useCallback(async () => {
    try {
      const res = await api.get("/customer/findByStatus");
      setAllCustomersList(Array.isArray(res.data) ? res.data : extractList(res.data));
    } catch { /* Fail silently */ }
  }, []);

  const fetchCategories = useCallback(async () => {
    try {
      const catRes = await api.get("/category/findActiveSubCategories");
      setCategories(extractList(catRes.data));
    } catch { /* Fail silently */ }
  }, []);

  const fetchBrands = useCallback(async () => {
    try {
      const brandRes = await api.get("/brand/findAllActive");
      setBrands(extractList(brandRes.data));
    } catch { /* Fail silently */ }
  }, []);

  const fetchAllOrders = async () => {
    setOrdersLoading(true);
    setOrdersError("");
    try {
      const res = await api.get("/order/findAll");
      setOrders(Array.isArray(res.data) ? res.data : []);
    } catch {
      setOrdersError("Failed to load orders. Please try again.");
    } finally {
      setOrdersLoading(false);
    }
  };

  const fetchOrderDetail = async (orderId) => {
    setOrderDetailLoading(true);
    try {
      const res = await api.post("/order/getOrder", { orderId });
      setSelectedOrder(res.data);
      setShowOrderDetail(true);
    } catch {
      showToast("Failed to load order details.", "error");
    } finally {
      setOrderDetailLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
    
    api.get("/product/findByStatus")
      .then(res => {
        const list = Array.isArray(res.data) ? res.data : (res.data?.dtoList ?? []);
        setProducts(list);
      })
      .catch(() => {});

    fetchCategories();
    fetchBrands();

    api.post("/wareHouse/list", { page: 0, sizePerPage: 100, sortDirection: "ASC", sortField: "id" })
      .then(res => {
        setWarehouses(Array.isArray(res.data) ? res.data : (res.data?.dtoList ?? res.data?.data ?? []));
      })
      .catch(() => {
        api.get("/wareHouse/findByStatus")
          .then(res2 => setWarehouses(Array.isArray(res2.data) ? res2.data : (res2.data?.dtoList ?? [])))
          .catch(() => setWarehouses([]));
      });
  }, [fetchCustomers, fetchCategories, fetchBrands]);

  useEffect(() => {
    if (products.length === 0) return;
    api.get("/price/findByStatus")
      .then(res => {
        const priceList = Array.isArray(res.data) ? res.data : (res.data?.dtoList ?? []);
        setProducts(prev => prev.map(p => {
          const priceObj = priceList.find(pr => pr.identifier === p.identifier || pr.product === p.identifier);
          return priceObj ? { ...p, sellingPrice: priceObj.sellingPrice, mrp: priceObj.mrp } : p;
        }));
      })
      .catch(() => {});
  }, [products.length]);

  useEffect(() => {
    if (!phoneSearch.trim() || customerFound) {
      setFilteredCustomers([]);
      return;
    }
    const term = phoneSearch.toLowerCase();
    const matches = allCustomersList.filter(c => {
      const name = (c.customerName || c.name || "").toLowerCase();
      const phone = (c.phone || c.identifier || "").toLowerCase();
      return name.includes(term) || phone.includes(term);
    });
    setFilteredCustomers(matches.slice(0, 8));
  }, [phoneSearch, allCustomersList, customerFound]);

  useEffect(() => {
    if (pageTab === "orders") {
      fetchAllOrders();
    }
  }, [pageTab]);

  const filteredProducts = useMemo(() => {
    let list = [...products];
    if (productSearch) {
      const s = productSearch.toLowerCase();
      list = list.filter(p => p.name?.toLowerCase().includes(s) || p.identifier?.toLowerCase().includes(s));
    }
    if (selectedCategory) {
      list = list.filter(p => {
        const catVal = p.category?.identifier || p.category || "";
        return Array.isArray(catVal) ? catVal.includes(selectedCategory) : catVal === selectedCategory;
      });
    }
    if (selectedBrand) {
      list = list.filter(p => (p.brand === selectedBrand || p.brandIdentifier === selectedBrand));
    }
    return list;
  }, [productSearch, selectedCategory, selectedBrand, products]);

  const filteredOrders = orders.filter(o => {
    return !orderSearch.trim() ||
      (o.orderId || "").toLowerCase().includes(orderSearch.toLowerCase()) ||
      (o.identifier || "").toLowerCase().includes(orderSearch.toLowerCase()) ||
      (o.paymentMode || "").toLowerCase().includes(orderSearch.toLowerCase());
  });

  const refreshCart = useCallback(async (identifier) => {
    if (!identifier) return null;
    try {
      const res = await api.post("/cart/getCart", { identifier });
      setCart(res.data);
      setEntries(res.data?.cartEntryDtoList ?? []);
      return res.data;
    } catch (err) {
      const status = err.response?.status;
      const serverMsg = err.response?.data?.message || err.response?.data?.error;
      let userMsg;
      if (!status) {
        userMsg = "Network error — please check your connection and try again.";
      } else if (status === 401 || status === 403) {
        userMsg = `Session expired or access denied (${status}). Please log in again.`;
      } else if (status === 404) {
        userMsg = "Cart not found for this customer.";
      } else {
        userMsg = serverMsg ? `Failed to load cart: ${serverMsg}` : `Failed to load cart (error ${status}). Please try again.`;
      }
      setError(userMsg);
      throw err;
    }
  }, []);

  const handleCustomerSelect = async (identifier) => {
    setCart(null);
    setEntries([]);
    setError("");
    if (!identifier) return;
    try {
      await refreshCart(identifier);
    } catch {
      try {
        const createRes = await api.post("/cart/add", { identifier, status: true });
        if (createRes.data) {
          setCart(createRes.data);
          setEntries(createRes.data.cartEntryDtoList ?? []);
        }
        await refreshCart(identifier);
      } catch {
        setError("Failed to initialize cart session for this customer.");
      }
    }
  };

  const handlePhoneSearch = async (forcedPhone = null) => {
    const phone = (forcedPhone || phoneSearch).trim();
    if (!phone) return;
    setSearchingCustomer(true);
    setCustomerFound(null);
    setBannerDismissed(false);
    setError("");
    setCart(null);
    setEntries([]);
    setShowCustomerDropdown(false);
    try {
      const res = await api.get("/customer/findByStatus", { params: { identifier: phone } });
      const data = res.data;
      const customer = Array.isArray(data) ? data[0] : data;
      if (!customer) throw new Error("not found");
      setCustomerFound(customer);
      setPhoneSearch(phone);
      await handleCustomerSelect(phone);
    } catch {
      setCustomerFound(null);
      setError("__NOT_FOUND__");
    } finally {
      setSearchingCustomer(false);
    }
  };

  const handleClearCustomer = () => {
    setCustomerFound(null);
    setBannerDismissed(false);
    setPhoneSearch("");
    setCart(null);
    setEntries([]);
    setError("");
  };

  const handleAddProduct = async (product) => {
    const explicitCartId = cart?.identifier || phoneSearch.trim();
    if (!explicitCartId) {
      setError("Please search or select a customer first.");
      return;
    }
    if (actionLoading || processingEntry) return;
    setActionLoading(true);
    setError("");
    try {
      await api.post("/cartEntry/addEntry", {
        cart: explicitCartId,
        product: product.identifier,
        quantity: 1,
        warehouse: selectedWarehouse || undefined
      });
      await refreshCart(explicitCartId);
      showToast("Item added to workspace.");
    } catch {
      showToast("Failed to write product entry.", "error");
    } finally {
      setActionLoading(false);
    }
  };

  const handleQtyChange = async (entry, delta) => {
    if (processingEntry === entry.product) return;
    const newQty = Number(entry.quantity) + delta;
    setProcessingEntry(entry.product);
    setError("");
    const cartId = entry.cart || cart?.identifier || phoneSearch.trim();
    try {
      if (newQty <= 0) {
        await api.get("/cart/deleteEntry", { params: { identifier: entry.identifier, cart: cartId } });
      } else {
        await api.post("/cartEntry/addEntry", { cart: cartId, product: entry.product, quantity: delta, warehouse: selectedWarehouse || undefined });
      }
      await refreshCart(cartId);
    } catch {
      showToast("Failed to adjust item count.", "error");
    } finally {
      setProcessingEntry(null);
    }
  };

  const handleDeleteEntry = async (entry) => {
    setProcessingEntry(entry.product);
    setError("");
    const cartId = entry.cart || cart?.identifier || phoneSearch.trim();
    try {
      await api.get("/cart/deleteEntry", { params: { identifier: entry.identifier, cart: cartId } });
      await refreshCart(cartId);
      showToast("Item removed successfully.");
    } catch {
      showToast("Failed to remove item.", "error");
    } finally {
      setProcessingEntry(null);
    }
  };

  const handleAddCustomer = async () => {
    if (!newCustomer.identifier || !newCustomer.customerName) {
      setAddCustomerError("Phone number and name are required.");
      return;
    }
    setAddingCustomer(true);
    setAddCustomerError("");
    try {
      const res = await api.post("/customer/add", {
        identifier: newCustomer.identifier,
        customerName: newCustomer.customerName,
        email: newCustomer.email || "",
        status: true,
        partyType: "Customer",
        billingAddress: { phoneNo: newCustomer.identifier, addressType: "Billing" },
        shippingAddress: { phoneNo: newCustomer.identifier, addressType: "Shipping" },
      });
      if (res.data?.success === false) {
        setAddCustomerError(res.data.message || "Customer already exists with this phone number.");
        return;
      }
      if (res.data) {
        setCustomerFound(res.data);
        setPhoneSearch(res.data.identifier);
        setAllCustomersList(prev => [res.data, ...prev]);
        await handleCustomerSelect(res.data.identifier);
        setShowAddModal(false);
        setError("");
        setNewCustomer({ identifier: "", customerName: "", email: "" });
        showToast("Customer created successfully.");
      } else {
        setAddCustomerError("Failed to create customer.");
      }
    } catch {
      setAddCustomerError("Failed to create customer. Please try again.");
    } finally {
      setAddingCustomer(false);
    }
  };

  const handleSale = async () => {
    const cartId = cart?.identifier || phoneSearch.trim();
    if (!cartId) { showToast("Select a customer first.", "error"); return; }
    if (entries.length === 0) { showToast("Cart workspace is empty.", "error"); return; }
    setActionLoading(true);
    try {
      const res = await api.post("/order/place", {
        identifier: cartId,
        paymentMode: paymentType,
        receivedAmount: 0,
        note: undefined
      });
      const placedOrderId = res?.data?.orderId || res?.data?.identifier || null;
      setJustPlacedOrderId(placedOrderId);
      setShowPaymentModal(false);
      setShowOrderSuccess(true);
      setActionLoading(false);
    } catch {
      showToast("Sale transmission failed. Please retry.", "error");
      setActionLoading(false);
    }
  };

  const handleOrderSuccessDone = () => {
    setShowOrderSuccess(false);
    setJustPlacedOrderId(null);
    setCart(null);
    setEntries([]);
    setPhoneSearch("");
    setCustomerFound(null);
    setBannerDismissed(false);
    setPaymentType("Cash");
    setError("");
    setPageTab("pos");
  };

  const handleViewJustPlacedOrder = async () => {
    if (!justPlacedOrderId) {
      showToast("Order id unavailable for viewing.", "error");
      return;
    }
    setShowOrderSuccess(false);
    await fetchOrderDetail(justPlacedOrderId);
  };

  const handleQuickPrintJustPlacedOrder = async () => {
    if (!justPlacedOrderId) {
      showToast("Order id unavailable for printing.", "error");
      return;
    }
    try {
      const res = await api.post("/order/getOrder", { orderId: justPlacedOrderId });
      setSelectedOrder(res.data);
      setTimeout(() => {
        window.print();
      }, 300);
    } catch {
      showToast("Failed to initialize print process.", "error");
    }
  };

  const handleCancel = async () => {
    const cartId = cart?.identifier || phoneSearch.trim();
    if (!cartId) return;
    setActionLoading(true);
    try {
      await api.post("/cart/deleteCart", { identifier: cartId });
    } catch { /* Fail silently */ }
    setCart(null);
    setEntries([]);
    setPhoneSearch("");
    setCustomerFound(null);
    setBannerDismissed(false);
    setPaymentType("Cash");
    setError("");
    setActionLoading(false);
  };

  const calcCompute = (a, b, op) => {
    switch (op) {
      case "+": return a + b;
      case "-": return a - b;
      case "×": return a * b;
      case "÷": return b === 0 ? 0 : a / b;
      default: return b;
    }
  };
  const calcInputDigit = (d) => {
    setCalcDisplay(prev => {
      if (calcWaitingForNew || prev === "0") { setCalcWaitingForNew(false); return d; }
      return prev + d;
    });
  };
  const calcInputDot = () => {
    setCalcDisplay(prev => {
      if (calcWaitingForNew) { setCalcWaitingForNew(false); return "0."; }
      return prev.includes(".") ? prev : prev + ".";
    });
  };
  const calcChooseOp = (op) => {
    const current = parseFloat(calcDisplay);
    if (calcOp && !calcWaitingForNew && calcPrev !== null) {
      const result = calcCompute(calcPrev, current, calcOp);
      setCalcPrev(result);
      setCalcDisplay(String(result));
    } else {
      setCalcPrev(current);
    }
    setCalcOp(op);
    setCalcWaitingForNew(true);
  };
  const calcEquals = () => {
    if (calcOp === null || calcPrev === null) return;
    const current = parseFloat(calcDisplay);
    const result = calcCompute(calcPrev, current, calcOp);
    setCalcDisplay(String(result));
    setCalcPrev(null);
    setCalcOp(null);
    setCalcWaitingForNew(true);
  };
  const calcClear = () => { setCalcDisplay("0"); setCalcPrev(null); setCalcOp(null); setCalcWaitingForNew(false); };
  const calcBackspace = () => { setCalcDisplay(prev => prev.length > 1 ? prev.slice(0, -1) : "0"); };

  const subTotal = entries.reduce((sum, e) => sum + (Number(e.totalPrice) || 0), 0);
  const totalDiscount = Number(cart?.totalDiscount ?? 0);
  const totalAmount = cart?.totalPrice != null ? Number(cart.totalPrice) : subTotal;

  const categoryDrawerList = categories.filter(c => {
    return getEntityLabel(c).toLowerCase().includes(categorySearchTerm.toLowerCase());
  });
  const brandDrawerList = brands.filter(b => {
    return getEntityLabel(b).toLowerCase().includes(brandSearchTerm.toLowerCase());
  });

  return (
    <div style={{ position: "fixed", top: "60px", right: 0, bottom: 0, left: isSidebarOpen ? "220px" : "55px", backgroundColor: C.ultraLightGray, fontFamily: "'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif", display: "flex", flexDirection: "column", overflow: "hidden", transition: "left 0.2s ease" }}>

      <style>{`
        input:focus, select:focus { border-color: ${C.black} !important; }
        input:hover, select:hover { border-color: ${C.lightGray} !important; }

        @media print {
          body * { visibility: hidden; }
          #printable-order-receipt, #printable-order-receipt * { visibility: visible; }
          #printable-order-receipt {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            box-shadow: none !important;
            border-radius: 0 !important;
            max-height: none !important;
          }
          #printable-order-receipt .no-print { display: none !important; }
        }
      `}</style>

      {/* Toast Notification */}
      {toast && (
        <div style={{ position: "absolute", top: "16px", right: "20px", zIndex: 9999, padding: "12px 18px", borderRadius: "8px", background: toast.type === "error" ? C.errorBg : C.successBg, border: `1px solid ${toast.type === "error" ? "#fca5a5" : "#d1fae5"}`, color: toast.type === "error" ? C.error : C.success, fontWeight: "600", fontSize: "14px" }}>
          {toast.msg}
        </div>
      )}

      {/* Add Customer Modal */}
      {showAddModal && (
        <div style={modalOverlaySt}>
          <div style={{ width: "420px", background: C.white, borderRadius: "12px", overflow: "hidden", boxShadow: "0 10px 40px rgba(0,0,0,0.15)" }}>
            <div style={{ padding: "20px 24px", borderBottom: `1px solid ${C.veryLightGray}`, display: "flex", alignItems: "center", justifyContent: "space-between" }}>
              <h2 style={{ margin: 0, fontSize: "16px", color: C.primary, fontWeight: "700" }}>Add New Customer</h2>
              <button onClick={() => { setShowAddModal(false); setAddCustomerError(""); }} style={{ background: "none", border: "none", cursor: "pointer", color: C.tertiary, fontSize: "20px", lineHeight: 1 }}>✕</button>
            </div>
            <div style={{ padding: "24px", display: "flex", flexDirection: "column", gap: "16px" }}>
              {addCustomerError && (
                <div style={{ color: C.error, background: C.errorBg, border: `1px solid #fca5a5`, padding: "10px 12px", borderRadius: "8px", fontSize: "13px" }}>{addCustomerError}</div>
              )}
              <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
                <label style={labelSt}>Phone Number *</label>
                <input type="text" placeholder="9876543210" value={newCustomer.identifier} onChange={e => setNewCustomer(prev => ({ ...prev, identifier: e.target.value }))} style={inputSt} />
              </div>
              <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
                <label style={labelSt}>Customer Name *</label>
                <input type="text" placeholder="Full name" value={newCustomer.customerName} onChange={e => setNewCustomer(prev => ({ ...prev, customerName: e.target.value }))} style={inputSt} />
              </div>
              <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
                <label style={labelSt}>Email (optional)</label>
                <input type="email" placeholder="customer@email.com" value={newCustomer.email} onChange={e => setNewCustomer(prev => ({ ...prev, email: e.target.value }))} style={inputSt} />
              </div>
              <div style={{ display: "flex", gap: "12px", marginTop: "8px" }}>
                <button type="button" onClick={() => { setShowAddModal(false); setAddCustomerError(""); }} style={{ flex: 1, height: "42px", background: C.white, border: `1px solid ${C.veryLightGray}`, color: C.secondary, borderRadius: "8px", fontWeight: "600", cursor: "pointer", fontSize: "13px" }}>Cancel</button>
                <button type="button" onClick={handleAddCustomer} disabled={addingCustomer} style={{ flex: 2, height: "42px", background: C.black, color: C.white, border: "none", borderRadius: "8px", fontWeight: "600", cursor: addingCustomer ? "not-allowed" : "pointer", opacity: addingCustomer ? 0.7 : 1, fontSize: "13px" }}>{addingCustomer ? "Saving..." : "Save Customer"}</button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Order Details Modal */}
      {showOrderDetail && selectedOrder && (
        <div style={modalOverlaySt}>
          <div id="printable-order-receipt" style={{ width: "700px", maxHeight: "calc(100vh - 100px)", background: C.white, borderRadius: "12px", overflow: "hidden", display: "flex", flexDirection: "column", boxShadow: "0 10px 40px rgba(0,0,0,0.15)" }}>
            <div className="no-print" style={{ padding: "20px 24px", borderBottom: `1px solid ${C.veryLightGray}`, display: "flex", alignItems: "center", justifyContent: "space-between", flexShrink: 0 }}>
              <div>
                <h2 style={{ margin: 0, fontSize: "16px", color: C.primary, fontWeight: "700" }}>Order {selectedOrder.orderId}</h2>
                <span style={{ fontSize: "12px", color: C.secondary }}>Customer: {selectedOrder.identifier}</span>
              </div>
              <button onClick={() => { setShowOrderDetail(false); setSelectedOrder(null); if (justPlacedOrderId) handleOrderSuccessDone(); }} style={{ background: "none", border: "none", cursor: "pointer", color: C.tertiary, fontSize: "20px", lineHeight: 1 }}>✕</button>
            </div>

            <div style={{ display: "none" }} className="print-only">
              <h2>StoreFlow Retail Management — Order {selectedOrder.orderId}</h2>
            </div>

            <div style={{ flex: 1, overflowY: "auto", padding: "24px", display: "flex", flexDirection: "column", gap: "24px" }}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr 1fr", gap: "12px" }}>
                {[
                  { label: "Order ID", value: selectedOrder.orderId },
                  { label: "Customer Phone", value: selectedOrder.identifier },
                  { label: "Payment Mode", value: selectedOrder.paymentMode || "—" },
                  { label: "Order Date", value: selectedOrder.orderDate ? new Date(selectedOrder.orderDate).toLocaleString("en-GB") : "—" },
                  { label: "Total Discount", value: `₹${Number(selectedOrder.totalDiscount ?? 0).toFixed(2)}` },
                  { label: "Grand Total", value: `₹${Number(selectedOrder.totalPrice ?? 0).toFixed(2)}` },
                ].map(item => (
                  <div key={item.label} style={{ background: C.ultraLightGray, borderRadius: "8px", padding: "12px", border: `1px solid ${C.veryLightGray}` }}>
                    <div style={labelSt}>{item.label}</div>
                    <div style={{ fontSize: "14px", color: C.primary, fontWeight: "700" }}>{item.value}</div>
                  </div>
                ))}
              </div>

              <div>
                <div style={{ fontSize: "14px", fontWeight: "700", color: C.primary, marginBottom: "12px" }}>Order Items ({(selectedOrder.entryDtoList || []).length})</div>
                {(selectedOrder.entryDtoList || []).length === 0 ? (
                  <div style={{ padding: "20px", textAlign: "center", color: C.secondary, fontSize: "13px" }}>No items in this order.</div>
                ) : (
                  <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "13px" }}>
                    <thead>
                      <tr style={{ borderBottom: `1px solid ${C.veryLightGray}`, color: C.secondary, textAlign: "left" }}>
                        <th style={{ padding: "10px 8px", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.02em" }}>Product</th>
                        <th style={{ padding: "10px 8px", textAlign: "center", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.02em" }}>Qty</th>
                        <th style={{ padding: "10px 8px", textAlign: "right", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.02em" }}>Price</th>
                        <th style={{ padding: "10px 8px", textAlign: "right", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.02em" }}>Discount</th>
                        <th style={{ padding: "10px 8px", textAlign: "right", fontSize: "12px", fontWeight: "600", textTransform: "uppercase", letterSpacing: "0.02em" }}>Total</th>
                      </tr>
                    </thead>
                    <tbody>
                      {(selectedOrder.entryDtoList || []).map((entry, idx) => (
                        <tr key={entry.identifier || idx} style={{ borderBottom: `1px solid ${C.veryLightGray}`, color: C.primary }}>
                          <td style={{ padding: "10px 8px" }}>
                            <div style={{ fontWeight: "600" }}>{entry.product}</div>
                            <div style={{ fontSize: "11px", color: C.secondary }}>ID: {entry.identifier}</div>
                          </td>
                          <td style={{ padding: "10px 8px", textAlign: "center", fontWeight: "600" }}>{entry.quantity}</td>
                          <td style={{ padding: "10px 8px", textAlign: "right", color: C.primary }}>₹{Number(entry.sellingPrice ?? 0).toFixed(2)}</td>
                          <td style={{ padding: "10px 8px", textAlign: "right", color: C.error }}>-₹{Number(entry.discount ?? 0).toFixed(2)}</td>
                          <td style={{ padding: "10px 8px", textAlign: "right", fontWeight: "700", color: C.primary }}>₹{Number(entry.totalPrice ?? 0).toFixed(2)}</td>
                        </tr>
                      ))}
                    </tbody>
                    <tfoot>
                      <tr style={{ borderTop: `1px solid ${C.veryLightGray}` }}>
                        <td colSpan="4" style={{ padding: "10px 8px", textAlign: "right", fontWeight: "700", color: C.primary }}>Grand Total</td>
                        <td style={{ padding: "10px 8px", textAlign: "right", fontWeight: "700", fontSize: "14px", color: C.primary }}>₹{Number(selectedOrder.totalPrice ?? 0).toFixed(2)}</td>
                      </tr>
                    </tfoot>
                  </table>
                )}
              </div>
            </div>

            <div className="no-print" style={{ padding: "16px 24px", borderTop: `1px solid ${C.veryLightGray}`, display: "flex", justifyContent: "flex-end", gap: "10px", flexShrink: 0 }}>
              <button type="button" onClick={() => window.print()} style={{ padding: "8px 20px", background: C.white, color: C.primary, border: `1px solid ${C.veryLightGray}`, borderRadius: "8px", fontWeight: "600", cursor: "pointer", fontSize: "13px", display: "flex", alignItems: "center", gap: "6px" }}>🖨️ Print</button>
              <button type="button" onClick={() => { setShowOrderDetail(false); setSelectedOrder(null); if (justPlacedOrderId) handleOrderSuccessDone(); }} style={{ padding: "8px 20px", background: C.black, color: C.white, border: "none", borderRadius: "8px", fontWeight: "600", cursor: "pointer", fontSize: "13px" }}>Close</button>
            </div>
          </div>
        </div>
      )}

      {/* Category Drawer */}
      {showCategoryDrawer && (
        <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.5)", zIndex: 10000 }} onClick={() => setShowCategoryDrawer(false)}>
          <div onClick={(e) => e.stopPropagation()} style={{ position: "absolute", top: "68px", right: 0, bottom: "20px", width: "480px", maxWidth: "86vw", background: C.white, boxShadow: "-8px 0 30px rgba(0,0,0,0.2)", display: "flex", flexDirection: "column", borderRadius: "12px" }}>
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "20px 24px 12px" }}>
              <h3 style={{ margin: 0, fontSize: "18px", fontWeight: "700", color: C.primary }}>Categories</h3>
              <button onClick={() => setShowCategoryDrawer(false)} style={{ background: "none", border: "none", fontSize: "18px", cursor: "pointer", color: C.tertiary }}>✕</button>
            </div>
            <div style={{ padding: "0 24px 16px" }}>
              <input type="text" placeholder="Search categories..." value={categorySearchTerm} onChange={e => setCategorySearchTerm(e.target.value)} style={inputSt} />
            </div>
            <div style={{ flex: 1, overflowY: "auto", padding: "0 24px 24px" }}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
                <div onClick={() => { setSelectedCategory(""); setShowCategoryDrawer(false); }} style={{ border: `1px solid ${!selectedCategory ? C.black : C.veryLightGray}`, borderRadius: "8px", padding: "18px 12px", textAlign: "center", cursor: "pointer", background: C.white, transition: "all 0.2s" }}>
                  <div style={{ fontSize: "13px", fontWeight: "700", color: C.primary }}>All</div>
                </div>
                {categoryDrawerList.map(c => {
                  const val = getEntityValue(c);
                  const label = getEntityLabel(c) || val;
                  return (
                    <div key={val} onClick={() => { setSelectedCategory(val); setShowCategoryDrawer(false); }} style={{ border: `1px solid ${selectedCategory === val ? C.black : C.veryLightGray}`, borderRadius: "8px", padding: "18px 12px", textAlign: "center", cursor: "pointer", background: C.white, transition: "all 0.2s" }}>
                      <div style={{ fontSize: "13px", fontWeight: "700", color: C.primary }}>{label}</div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Brand Drawer */}
      {showBrandDrawer && (
        <div style={{ position: "fixed", inset: 0, background: "rgba(0,0,0,0.5)", zIndex: 10000 }} onClick={() => setShowBrandDrawer(false)}>
          <div onClick={(e) => e.stopPropagation()} style={{ position: "absolute", top: "68px", right: 0, bottom: "20px", width: "480px", maxWidth: "86vw", background: C.white, boxShadow: "-8px 0 30px rgba(0,0,0,0.2)", display: "flex", flexDirection: "column", borderRadius: "12px" }}>
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", padding: "20px 24px 12px" }}>
              <h3 style={{ margin: 0, fontSize: "18px", fontWeight: "700", color: C.primary }}>Brands</h3>
              <button onClick={() => setShowBrandDrawer(false)} style={{ background: "none", border: "none", fontSize: "18px", cursor: "pointer", color: C.tertiary }}>✕</button>
            </div>
            <div style={{ padding: "0 24px 16px" }}>
              <input type="text" placeholder="Search brands..." value={brandSearchTerm} onChange={e => setBrandSearchTerm(e.target.value)} style={inputSt} />
            </div>
            <div style={{ flex: 1, overflowY: "auto", padding: "0 24px 24px" }}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
                <div onClick={() => { setSelectedBrand(""); setShowBrandDrawer(false); }} style={{ border: `1px solid ${!selectedBrand ? C.black : C.veryLightGray}`, borderRadius: "8px", padding: "18px 12px", textAlign: "center", cursor: "pointer", background: C.white, transition: "all 0.2s" }}>
                  <div style={{ fontSize: "13px", fontWeight: "700", color: C.primary }}>All</div>
                </div>
                {brandDrawerList.map(b => {
                  const val = getEntityValue(b);
                  const label = getEntityLabel(b) || val;
                  return (
                    <div key={val} onClick={() => { setSelectedBrand(val); setShowBrandDrawer(false); }} style={{ border: `1px solid ${selectedBrand === val ? C.black : C.veryLightGray}`, borderRadius: "8px", padding: "18px 12px", textAlign: "center", cursor: "pointer", background: C.white, transition: "all 0.2s" }}>
                      <div style={{ fontSize: "13px", fontWeight: "700", color: C.primary }}>{label}</div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Calculator Popup */}
      {showCalculator && (
        <div style={modalOverlaySt} onClick={() => setShowCalculator(false)}>
          <div onClick={(e) => e.stopPropagation()} style={{ width: "280px", background: C.white, borderRadius: "12px", padding: "16px", boxShadow: "0 10px 40px rgba(0,0,0,0.15)" }}>
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "12px" }}>
              <span style={{ fontWeight: "700", fontSize: "14px", color: C.primary }}>Calculator</span>
              <button onClick={() => setShowCalculator(false)} style={{ background: "none", border: "none", fontSize: "16px", cursor: "pointer", color: C.tertiary }}>✕</button>
            </div>
            <div style={{ background: C.ultraLightGray, borderRadius: "8px", padding: "12px", textAlign: "right", fontSize: "24px", fontWeight: "700", color: C.primary, marginBottom: "12px", overflowX: "auto" }}>{calcDisplay}</div>
            <div style={{ display: "grid", gridTemplateColumns: "repeat(4, 1fr)", gap: "6px" }}>
              {[
                { t: "C", fn: calcClear }, { t: "⌫", fn: calcBackspace }, { t: "÷", fn: () => calcChooseOp("÷") }, { t: "×", fn: () => calcChooseOp("×") },
                { t: "7", fn: () => calcInputDigit("7") }, { t: "8", fn: () => calcInputDigit("8") }, { t: "9", fn: () => calcInputDigit("9") }, { t: "-", fn: () => calcChooseOp("-") },
                { t: "4", fn: () => calcInputDigit("4") }, { t: "5", fn: () => calcInputDigit("5") }, { t: "6", fn: () => calcInputDigit("6") }, { t: "+", fn: () => calcChooseOp("+") },
                { t: "1", fn: () => calcInputDigit("1") }, { t: "2", fn: () => calcInputDigit("2") }, { t: "3", fn: () => calcInputDigit("3") }, { t: "=", fn: calcEquals, span: true },
                { t: "0", fn: () => calcInputDigit("0"), wide: true }, { t: ".", fn: calcInputDot },
              ].map((btn, i) => (
                <button key={i} onClick={btn.fn} style={{ gridColumn: btn.wide ? "span 2" : undefined, gridRow: btn.span ? "span 2" : undefined, height: "40px", borderRadius: "6px", border: `1px solid ${C.veryLightGray}`, background: ["C", "⌫", "÷", "×", "-", "+", "="].includes(btn.t) ? C.ultraLightGray : C.white, fontWeight: "700", fontSize: "14px", cursor: "pointer", color: C.primary, transition: "all 0.1s" }}>{btn.t}</button>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Payment Mode Modal */}
      {showPaymentModal && (
        <div style={modalOverlaySt}>
          <div style={{ width: "440px", background: C.white, borderRadius: "12px", overflow: "hidden", boxShadow: "0 10px 40px rgba(0,0,0,0.15)" }}>
            <div style={{ padding: "14px 18px", borderBottom: `1px solid ${C.veryLightGray}`, display: "flex", alignItems: "center", justifyContent: "space-between" }}>
              <h2 style={{ margin: 0, fontSize: "15px", color: C.primary, fontWeight: "700" }}>Select Payment Mode</h2>
              <button onClick={() => setShowPaymentModal(false)} style={{ background: "none", border: "none", cursor: "pointer", color: C.tertiary, fontSize: "20px", lineHeight: 1 }}>✕</button>
            </div>

            <div style={{ padding: "16px" }}>
              <div style={{ background: C.ultraLightGray, padding: "10px 14px", borderRadius: "8px", marginBottom: "16px", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                <span style={{ fontSize: "13px", fontWeight: "600", color: C.secondary }}>Order Total</span>
                <span style={{ fontSize: "16px", fontWeight: "700", color: C.primary }}>₹{totalAmount.toFixed(2)}</span>
              </div>

              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "16px" }}>
                {[
                  { value: "Cash", label: "Cash", icon: "💵" },
                  { value: "Card", label: "Card", icon: "💳" },
                  { value: "UPI", label: "UPI / Digital", icon: "📱" },
                  { value: "Online", label: "Online", icon: "🌐" }
                ].map(method => (
                  <div
                    key={method.value}
                    onClick={() => setPaymentType(method.value)}
                    style={{
                      padding: "14px",
                      border: `2px solid ${paymentType === method.value ? C.black : C.veryLightGray}`,
                      borderRadius: "10px",
                      cursor: "pointer",
                      textAlign: "center",
                      background: paymentType === method.value ? C.ultraLightGray : C.white,
                      transition: "all 0.2s"
                    }}
                  >
                    <div style={{ fontSize: "22px", marginBottom: "6px" }}>{method.icon}</div>
                    <div style={{ fontSize: "13px", fontWeight: "700", color: C.primary }}>{method.label}</div>
                  </div>
                ))}
              </div>

              <button
                type="button"
                onClick={handleSale}
                disabled={actionLoading}
                style={{
                  width: "100%",
                  height: "42px",
                  background: C.black,
                  color: C.white,
                  border: "none",
                  borderRadius: "10px",
                  fontWeight: "700",
                  fontSize: "13px",
                  cursor: actionLoading ? "not-allowed" : "pointer",
                  opacity: actionLoading ? 0.7 : 1
                }}
              >
                {actionLoading ? "Processing..." : "Confirm Order"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Order Success Modal */}
      {showOrderSuccess && (
        <div style={modalOverlaySt}>
          <div style={{ width: "450px", background: C.white, borderRadius: "12px", overflow: "hidden", boxShadow: "0 12px 42px rgba(0,0,0,0.12)", textAlign: "center", padding: "36px 24px" }}>
            <div style={{ fontSize: "36px", color: C.primary, marginBottom: "16px", display: "flex", alignItems: "center", justifyContent: "center", width: "72px", height: "72px", borderRadius: "50%", border: `2px solid ${C.black}`, background: C.white, margin: "0 auto 20px" }}>
              ✓
            </div>
            <h2 style={{ margin: 0, fontSize: "22px", color: C.primary, fontWeight: "700", marginBottom: "8px", letterSpacing: "-0.01em" }}>Order Completed Successfully!</h2>
            <p style={{ margin: 0, fontSize: "13px", color: C.secondary, marginBottom: "28px", lineHeight: "1.5" }}>The records have been logged. You can now pull the print layout view metrics.</p>
            
            <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
              <div style={{ display: "flex", gap: "10px" }}>
                <button
                  type="button"
                  onClick={handleViewJustPlacedOrder}
                  disabled={!justPlacedOrderId || orderDetailLoading}
                  style={{
                    flex: 1,
                    height: "44px",
                    background: C.white,
                    color: C.primary,
                    border: `1px solid ${C.black}`,
                    borderRadius: "8px",
                    fontWeight: "600",
                    fontSize: "13px",
                    cursor: (!justPlacedOrderId || orderDetailLoading) ? "not-allowed" : "pointer",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    gap: "6px"
                  }}
                >
                  👁 View Receipt Layout
                </button>
                <button
                  type="button"
                  onClick={handleQuickPrintJustPlacedOrder}
                  disabled={!justPlacedOrderId}
                  style={{
                    flex: 1,
                    height: "44px",
                    background: C.mediumGray,
                    color: C.white,
                    border: "none",
                    borderRadius: "8px",
                    fontWeight: "600",
                    fontSize: "13px",
                    cursor: !justPlacedOrderId ? "not-allowed" : "pointer",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    gap: "6px"
                  }}
                >
                  🖨 Direct Quick Print
                </button>
              </div>
              
              <button
                type="button"
                onClick={handleOrderSuccessDone}
                style={{
                  width: "100%",
                  height: "44px",
                  background: C.black,
                  color: C.white,
                  border: "none",
                  borderRadius: "8px",
                  fontWeight: "600",
                  fontSize: "13px",
                  cursor: "pointer",
                  marginTop: "4px"
                }}
              >
                Start Next Order Session ➜
              </button>
            </div>
          </div>
        </div>
      )}

      <div style={{ background: C.white, padding: "12px 20px", borderBottom: `1px solid ${C.veryLightGray}`, display: "flex", alignItems: "center", justifyContent: "space-between", flexShrink: 0, gap: "16px" }}>
        <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          {pageTab === "orders" ? (
            <button type="button" onClick={() => router.push("/sales")} style={{ background: "none", border: "none", color: C.primary, padding: "5px 6px", fontSize: "13px", fontWeight: "500", cursor: "pointer" }}>⮜ Back</button>
          ) : (
            <button type="button" onClick={() => router.push("/home")} style={{ background: "none", border: "none", color: C.primary, padding: "5px 6px", fontSize: "13px", fontWeight: "500", cursor: "pointer" }}>⮜ Home</button>
          )}
          {pageTab === "orders" && (
            <span style={{ fontSize: "16px", fontWeight: "700", color: C.primary }}>Order History</span>
          )}
        </div>
        <div style={{ display: "flex", gap: "10px", alignItems: "center" }}>
          <button type="button" onClick={() => setShowCalculator(true)} style={{ height: "36px", width: "36px", background: C.ultraLightGray, border: `1px solid ${C.veryLightGray}`, borderRadius: "8px", cursor: "pointer", fontSize: "16px" }}>🧮</button>
          <input readOnly value={today} style={{ ...inputSt, width: "130px", height: "36px", background: C.ultraLightGray, textAlign: "center" }} />
        </div>
      </div>

      {/* POS TAB */}
      {pageTab === "pos" && (
        <div style={{ flex: 1, display: "flex", overflow: "hidden" }}>
          {/* LEFT: Cart & Checkout */}
          <div style={{ width: "60%", display: "flex", minHeight: 0, flexDirection: "column", overflow: "hidden", background: C.white, borderRight: `1px solid ${C.veryLightGray}` }}>
            
            {/* Customer Search */}
            <div style={{ padding: "12px 16px 10px", borderBottom: `1px solid ${C.veryLightGray}`, flexShrink: 0, display: "flex", flexDirection: "column", gap: "8px" }}>
              {error && (
                <div style={{ background: C.errorBg, border: `1px solid #fca5a5`, color: C.error, borderRadius: "8px", padding: "10px 12px", fontSize: "12px", display: "flex", alignItems: "center", gap: "10px" }}>
                  {error === "__NOT_FOUND__" ? (
                    <>
                      <span>No customer found for <strong>{phoneSearch}</strong></span>
                      <button type="button" onClick={() => { setNewCustomer({ identifier: phoneSearch, customerName: "", email: "" }); setAddCustomerError(""); setShowAddModal(true); setError(""); }} style={{ padding: "4px 12px", background: C.black, color: C.white, border: "none", borderRadius: "6px", fontSize: "11px", fontWeight: "700", cursor: "pointer", whiteSpace: "nowrap" }}>+ Add</button>
                    </>
                  ) : <span>{error}</span>}
                </div>
              )}
              <div style={{ display: "flex", gap: "8px", alignItems: "center" }}>
                <div ref={dropdownRef} style={{ flex: 1, display: "flex", position: "relative" }}>
                  <input type="text" placeholder="Search customer by phone or name..." value={phoneSearch}
                    onFocus={() => setShowCustomerDropdown(true)}
                    onChange={e => { setPhoneSearch(e.target.value); setShowCustomerDropdown(true); if (customerFound !== null) { setCustomerFound(null); setBannerDismissed(false); } if (error === "__NOT_FOUND__") setError(""); }}
                    onKeyDown={e => e.key === "Enter" && handlePhoneSearch()}
                    disabled={!!customerFound}
                    style={{ ...inputSt, height: "36px", flex: 1, borderRadius: "8px 0 0 8px", borderRight: "none" }}
                  />
                  <button type="button" onClick={() => handlePhoneSearch()} disabled={searchingCustomer || !phoneSearch.trim() || !!customerFound} style={{ padding: "0 14px", background: C.black, color: C.white, border: "none", fontWeight: "600", fontSize: "12px", height: "36px", cursor: (!phoneSearch.trim() || !!customerFound) ? "not-allowed" : "pointer", opacity: (!phoneSearch.trim() || !!customerFound) ? 0.5 : 1 }}>{searchingCustomer ? "..." : "Search"}</button>
                  <button type="button" title="Quick Add Customer" onClick={() => { const phone = phoneSearch.trim(); setNewCustomer({ identifier: phone, customerName: "", email: "" }); setAddCustomerError(""); setShowAddModal(true); }} style={{ width: "36px", height: "36px", background: C.darkGray, border: "none", borderRadius: "0 8px 8px 0", color: C.white, fontSize: "20px", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" }}>+</button>
                  
                  {showCustomerDropdown && filteredCustomers.length > 0 && (
                    <div style={{ position: "absolute", top: "38px", left: 0, right: 0, background: C.white, border: `1px solid ${C.veryLightGray}`, borderRadius: "8px", boxShadow: "0 4px 12px rgba(0,0,0,0.1)", zIndex: 2000, maxHeight: "200px", overflowY: "auto" }}>
                      {filteredCustomers.map(c => {
                        const cName = c.customerName || c.name || "Unknown";
                        const cPhone = c.phone || c.identifier || "No Phone";
                        return (
                          <div key={c.id || c.identifier} onClick={() => handlePhoneSearch(cPhone)} style={{ padding: "10px 12px", borderBottom: `1px solid ${C.veryLightGray}`, cursor: "pointer" }} onMouseEnter={e => e.currentTarget.style.backgroundColor = C.ultraLightGray} onMouseLeave={e => e.currentTarget.style.backgroundColor = C.white}>
                            <div style={{ fontWeight: "600", fontSize: "13px", color: C.primary }}>{cName}</div>
                            <div style={{ fontSize: "11px", color: C.secondary }}>Phone: {cPhone}{c.email ? ` · ${c.email}` : ""}</div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
                <div style={{ flex: 1 }}>
                  <select className="themed-select" value={selectedWarehouse} onChange={e => setSelectedWarehouse(e.target.value)} style={{ ...selectSt, height: "36px" }}>
                    <option value="">Select Warehouse</option>
                    {warehouses.map(w => (<option key={w.identifier} value={w.identifier}>{w.name ?? w.identifier}</option>))}
                  </select>
                </div>
              </div>

              {customerFound && !bannerDismissed && (
                <div style={{ display: "flex", alignItems: "center", gap: "8px", padding: "8px 12px", background: C.successBg, border: `1px solid #d1fae5`, borderRadius: "8px" }}>
                  <span style={{ fontSize: "14px", color: C.success, fontWeight: "700" }}>✓</span>
                  <div style={{ flex: 1, fontSize: "13px", color: C.success }}>
                    <strong>{customerFound.customerName ?? customerFound.name ?? customerFound.identifier}</strong>
                    {customerFound.phone && <span style={{ marginLeft: "8px", opacity: 0.8 }}>— {customerFound.phone}</span>}
                  </div>
                  <button type="button" onClick={() => setBannerDismissed(true)} style={{ background: "none", border: "none", cursor: "pointer", color: "#10b981", fontSize: "16px" }}>✕</button>
                </div>
              )}

              {customerFound && bannerDismissed && (
                <div style={{ display: "flex", alignItems: "center", gap: "8px", padding: "6px 12px", background: C.ultraLightGray, border: `1px solid ${C.veryLightGray}`, borderRadius: "8px" }}>
                  <span style={{ fontSize: "12px", color: C.success, fontWeight: "700" }}>✓</span>
                  <span style={{ flex: 1, fontSize: "12px", color: C.primary }}>
                    <strong>{customerFound.customerName ?? customerFound.name ?? customerFound.identifier}</strong>
                    {customerFound.phone && <span style={{ marginLeft: "6px", color: C.secondary }}>({customerFound.phone})</span>}
                  </span>
                  <button type="button" onClick={handleClearCustomer} style={{ background: "none", border: `1px solid ${C.veryLightGray}`, cursor: "pointer", color: C.secondary, fontSize: "11px", fontWeight: "600", padding: "2px 8px", borderRadius: "6px" }}>Change</button>
                </div>
              )}
            </div>

            {/* Cart Content */}
            <div style={{ flex: 1, minHeight: 0, overflowY: "auto", display: "flex", flexDirection: "column" }}>
                {entries.length === 0 ? (
                  <div style={{ padding: "60px 20px", textAlign: "center", color: C.secondary, fontSize: "14px" }}>
                    Select a customer and add products from the right panel
                  </div>
                ) : (
                  <div style={{ overflowX: "auto" }}>
                    <table style={{ width: "100%", minWidth: "700px", borderCollapse: "collapse", fontSize: "12px" }}>
                      <thead>
                        <tr style={{ borderBottom: `1px solid ${C.veryLightGray}`, background: C.ultraLightGray }}>
                          <th style={{ padding: "10px 8px", fontWeight: "600", fontSize: "11px", color: C.secondary, textAlign: "left", textTransform: "uppercase", letterSpacing: "0.02em" }}>Product</th>
                          <th style={{ padding: "10px 8px", fontWeight: "600", fontSize: "11px", color: C.secondary, textAlign: "center", textTransform: "uppercase", letterSpacing: "0.02em" }}>Qty</th>
                          <th style={{ padding: "10px 8px", fontWeight: "600", fontSize: "11px", color: C.secondary, textAlign: "right", textTransform: "uppercase", letterSpacing: "0.02em" }}>Price</th>
                          <th style={{ padding: "10px 8px", fontWeight: "600", fontSize: "11px", color: C.secondary, textAlign: "right", textTransform: "uppercase", letterSpacing: "0.02em" }}>Total</th>
                          <th style={{ padding: "10px 8px", fontWeight: "600", fontSize: "11px", color: C.secondary, textAlign: "center", textTransform: "uppercase", letterSpacing: "0.02em" }}>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {entries.map((entry) => {
                          const prod = products.find(p => p.identifier === entry.product);
                          const imgUrl = prod?.image || prod?.imageUrl || prod?.thumbnail || null;
                          return (
                            <tr key={entry.id || entry.identifier} style={{ borderBottom: `1px solid ${C.veryLightGray}` }}>
                              <td style={{ padding: "10px 8px" }}>
                                <div style={{ display: "flex", gap: "8px", alignItems: "center" }}>
                                  <ProductThumb url={imgUrl} size={28} />
                                  <div>
                                    <div style={{ fontWeight: "600", color: C.primary, fontSize: "12px" }}>{prod?.name || entry.productName || entry.product}</div>
                                    <div style={{ fontSize: "11px", color: C.secondary }}>{prod?.sku || entry.product}</div>
                                  </div>
                                </div>
                              </td>
                              <td style={{ padding: "10px 8px", textAlign: "center" }}>
                                <div style={{ display: "inline-flex", alignItems: "center", border: `1px solid ${C.veryLightGray}`, borderRadius: "6px" }}>
                                  <button onClick={() => handleQtyChange(entry, -1)} disabled={processingEntry === entry.product} style={{ border: "none", background: "none", width: "24px", height: "24px", cursor: "pointer", color: C.primary, fontWeight: "600" }}>−</button>
                                  <span style={{ minWidth: "20px", textAlign: "center", fontWeight: "600", color: C.primary, fontSize: "12px" }}>{entry.quantity}</span>
                                  <button onClick={() => handleQtyChange(entry, 1)} disabled={processingEntry === entry.product} style={{ border: "none", background: "none", width: "24px", height: "24px", cursor: "pointer", color: C.primary, fontWeight: "600" }}>+</button>
                                </div>
                              </td>
                              <td style={{ padding: "10px 8px", textAlign: "right", color: C.primary }}>₹{Number(entry.sellingPrice).toFixed(2)}</td>
                              <td style={{ padding: "10px 8px", textAlign: "right", fontWeight: "700", color: C.primary }}>₹{Number(entry.totalPrice).toFixed(2)}</td>
                              <td style={{ padding: "10px 8px", textAlign: "center" }}>
                                <button onClick={() => handleDeleteEntry(entry)} disabled={processingEntry === entry.product} style={{ background: C.error, border: "none", color: C.white, cursor: "pointer", fontWeight: "600", fontSize: "11px", padding: "4px 10px", borderRadius: "6px" }}>Remove</button>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>

            {/* Summary Footer */}
            <div style={{ padding: "16px", background: C.ultraLightGray, borderTop: `1px solid ${C.veryLightGray}`, flexShrink: 0 }}>
              <div style={{ display: "flex", flexDirection: "column", gap: "8px", fontSize: "13px", color: C.primary, marginBottom: "12px" }}>
                <div style={{ display: "flex", justifyContent: "space-between" }}>
                  <span>Sub Total:</span>
                  <span>₹{subTotal.toFixed(2)}</span>
                </div>
                <div style={{ display: "flex", justifyContent: "space-between", color: C.error, fontSize: "12px" }}>
                  <span>Discount:</span>
                  <span>-₹{totalDiscount.toFixed(2)}</span>
                </div>
                <div style={{ display: "flex", justifyContent: "space-between", fontWeight: "700", fontSize: "14px", borderTop: `1px solid ${C.veryLightGray}`, paddingTop: "8px", marginTop: "4px" }}>
                  <span>Grand Total:</span>
                  <span>₹{totalAmount.toFixed(2)}</span>
                </div>
              </div>

              <div style={{ display: "flex", gap: "10px" }}>
                <button type="button" onClick={handleCancel} disabled={actionLoading || !cart} style={{ flex: 1, height: "40px", background: C.white, border: `1px solid ${C.veryLightGray}`, color: C.error, borderRadius: "8px", fontWeight: "600", cursor: "pointer" }}>Cancel Order</button>
                <button type="button" onClick={() => setShowPaymentModal(true)} disabled={entries.length === 0} style={{ flex: 2, height: "40px", background: C.black, border: "none", color: C.white, borderRadius: "8px", fontWeight: "600", cursor: entries.length === 0 ? "not-allowed" : "pointer", opacity: entries.length === 0 ? 0.6 : 1 }}>Checkout</button>
              </div>
            </div>
          </div>

          {/* RIGHT: Products Panel */}
          <div style={{ width: "40%", display: "flex", flexDirection: "column", minHeight: 0, overflow: "hidden", background: C.ultraLightGray }}>
            <div style={{ padding: "12px", display: "flex", flexDirection: "column", gap: "8px", borderBottom: `1px solid ${C.veryLightGray}`, background: C.white }}>
              <input type="text" placeholder="Search products..." value={productSearch} onChange={e => setProductSearch(e.target.value)} style={inputSt} />
              <div style={{ display: "flex", gap: "8px" }}>
                <button type="button" onClick={() => setShowCategoryDrawer(true)} style={{ ...inputSt, flex: 1, textAlign: "left", background: C.white, cursor: "pointer", fontSize: "13px" }}>
                  {selectedCategory ? `📁 ${selectedCategory}` : "All Categories"}
                </button>
                <button type="button" onClick={() => setShowBrandDrawer(true)} style={{ ...inputSt, flex: 1, textAlign: "left", background: C.white, cursor: "pointer", fontSize: "13px" }}>
                  {selectedBrand ? `🏷️ ${selectedBrand}` : "All Brands"}
                </button>
              </div>
            </div>

            <div style={{ flex: 1, minHeight: 0, overflowY: "auto", padding: "12px" }}>
              {filteredProducts.length === 0 ? (
                <div style={{ padding: "40px 20px", textAlign: "center", color: C.secondary, fontSize: "13px" }}>No products found</div>
              ) : (
                <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill, minmax(130px, 1fr))", gap: "12px" }}>
                  {filteredProducts.map(p => {
                    const matchInCart = entries.find(e => e.product === p.identifier);
                    const imgUrl = p.image || p.imageUrl || p.thumbnail || null;
                    return (
                      <div key={p.identifier} onClick={() => handleAddProduct(p)} style={{ background: C.white, border: `2px solid ${matchInCart ? C.black : C.veryLightGray}`, borderRadius: "10px", padding: "12px", cursor: "pointer", position: "relative", display: "flex", flexDirection: "column", alignItems: "center", gap: "8px", minHeight: "160px", transition: "all 0.2s" }}>
                        {matchInCart && (
                          <span style={{ position: "absolute", top: "8px", right: "8px", background: C.error, color: C.white, fontSize: "10px", fontWeight: "700", padding: "3px 6px", borderRadius: "10px" }}>{matchInCart.quantity}</span>
                        )}
                        <ProductThumb url={imgUrl} size={56} />
                        <div style={{ textAlign: "center", flex: 1, display: "flex", flexDirection: "column", justifyContent: "center" }}>
                          <div style={{ fontWeight: "600", fontSize: "12px", color: C.primary, lineHeight: "1.3", display: "-webkit-box", WebkitLineClamp: 2, WebkitBoxOrient: "vertical", overflow: "hidden" }}>{p.name || p.identifier}</div>
                        </div>
                        <div style={{ fontWeight: "700", fontSize: "14px", color: C.primary }}>₹{Number(p.sellingPrice || p.mrp || 0).toFixed(2)}</div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* ORDERS TAB */}
      {pageTab === "orders" && (
        <div style={{ flex: 1, display: "flex", flexDirection: "column", overflow: "hidden" }}>
          <div style={{ background: C.white, padding: "12px 20px", borderBottom: `1px solid ${C.veryLightGray}`, display: "flex", gap: "10px", alignItems: "center", flexShrink: 0 }}>
            <input type="text" placeholder="Search by order ID or customer..." value={orderSearch} onChange={e => setOrderSearch(e.target.value)} style={{ ...inputSt, flex: 2 }} />
            <button type="button" onClick={fetchAllOrders} title="Refresh" style={{ height: "36px", width: "36px", padding: 0, background: C.ultraLightGray, color: C.primary, border: `1px solid ${C.veryLightGray}`, borderRadius: "8px", fontSize: "14px", cursor: "pointer", display: "flex", alignItems: "center", justifyContent: "center" }}>🔄</button>
          </div>

          <div style={{ background: C.ultraLightGray, padding: "10px 20px", borderBottom: `1px solid ${C.veryLightGray}`, display: "flex", gap: "30px", flexShrink: 0 }}>
            <div>
              <div style={labelSt}>Total Orders</div>
              <div style={{ fontSize: "18px", fontWeight: "700", color: C.primary }}>{orders.length}</div>
            </div>
            <div>
              <div style={labelSt}>Total Revenue</div>
              <div style={{ fontSize: "18px", fontWeight: "700", color: C.primary }}>₹{orders.reduce((s, o) => s + Number(o.totalPrice ?? 0), 0).toFixed(2)}</div>
            </div>
          </div>

          <div style={{ flex: 1, overflowY: "auto" }}>
            {ordersLoading ? (
              <div style={{ padding: "60px 20px", textAlign: "center", color: C.secondary, fontSize: "13px" }}>Loading orders...</div>
            ) : ordersError ? (
              <div style={{ padding: "40px 20px", textAlign: "center", color: C.error, fontSize: "13px" }}>{ordersError}</div>
            ) : filteredOrders.length === 0 ? (
              <div style={{ padding: "60px 20px", textAlign: "center", color: C.secondary, fontSize: "13px" }}>No orders found</div>
            ) : (
              <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "12px", background: C.white }}>
                <thead>
                  <tr style={{ background: C.ultraLightGray, borderBottom: `1px solid ${C.veryLightGray}`, color: C.secondary }}>
                    <th style={{ padding: "12px 14px", fontSize: "11px", fontWeight: "600", textAlign: "left", textTransform: "uppercase", letterSpacing: "0.02em" }}>Order ID</th>
                    <th style={{ padding: "12px 14px", fontSize: "11px", fontWeight: "600", textAlign: "left", textTransform: "uppercase", letterSpacing: "0.02em" }}>Customer</th>
                    <th style={{ padding: "12px 14px", fontSize: "11px", fontWeight: "600", textAlign: "left", textTransform: "uppercase", letterSpacing: "0.02em" }}>Date</th>
                    <th style={{ padding: "12px 14px", fontSize: "11px", fontWeight: "600", textAlign: "left", textTransform: "uppercase", letterSpacing: "0.02em" }}>Method</th>
                    <th style={{ padding: "12px 14px", fontSize: "11px", fontWeight: "600", textAlign: "center", textTransform: "uppercase", letterSpacing: "0.02em" }}>Items</th>
                    <th style={{ padding: "12px 14px", fontSize: "11px", fontWeight: "600", textAlign: "right", textTransform: "uppercase", letterSpacing: "0.02em" }}>Amount</th>
                    <th style={{ padding: "12px 14px", fontSize: "11px", fontWeight: "600", textAlign: "center", textTransform: "uppercase", letterSpacing: "0.02em" }}>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredOrders.map((order, idx) => (
                    <tr key={`${order.orderId || order.identifier || "order"}-${idx}`} style={{ borderBottom: `1px solid ${C.veryLightGray}`, color: C.primary }} onMouseEnter={e => e.currentTarget.style.background = C.ultraLightGray} onMouseLeave={e => e.currentTarget.style.background = C.white}>
                      <td style={{ padding: "12px 14px", fontWeight: "600" }}>{order.orderId}</td>
                      <td style={{ padding: "12px 14px" }}>{order.identifier}</td>
                      <td style={{ padding: "12px 14px", color: C.secondary, fontSize: "11px" }}>{order.orderDate ? new Date(order.orderDate).toLocaleDateString("en-GB") : "—"}</td>
                      <td style={{ padding: "12px 14px" }}><span style={{ padding: "2px 8px", background: C.ultraLightGray, borderRadius: "4px", fontSize: "11px", fontWeight: "600" }}>{order.paymentMode || "—"}</span></td>
                      <td style={{ padding: "12px 14px", textAlign: "center", fontWeight: "600" }}>{(order.entryDtoList || []).length}</td>
                      <td style={{ padding: "12px 14px", textAlign: "right", fontWeight: "700", color: C.primary }}>₹{Number(order.totalPrice ?? 0).toFixed(2)}</td>
                      <td style={{ padding: "12px 14px", textAlign: "center" }}>
                        <button type="button" onClick={() => fetchOrderDetail(order.orderId)} disabled={orderDetailLoading} style={{ padding: "4px 12px", background: C.black, color: C.white, border: "none", borderRadius: "6px", fontSize: "11px", fontWeight: "600", cursor: "pointer" }}>View</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}
    </div>
  );
}