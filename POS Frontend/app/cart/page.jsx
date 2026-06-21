"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { useRouter } from "next/navigation";
import CartPage from "@/components/CartPage";
import logger from "@/lib/logger";
import { BASE } from "@/lib/api";
import { PATHS, ERROR_MESSAGES, STORAGE_KEYS } from "@/config/constants";

const getToken = () => logger.getStorageItem(STORAGE_KEYS.TOKEN) ?? null;
const authHeaders = () => ({
  Authorization: `Bearer ${getToken()}`,
  "Content-Type": "application/json",
});
async function apiFetch(url, opts = {}) {
  try {
    const method = opts.method || "GET";
    const res = await fetch(`${BASE}${url}`, {
      ...opts,
      headers: { ...authHeaders(), ...opts.headers },
    });

    if (res.status === 401) {
      logger.warn("Unauthorized - redirecting to login", "CartRoute");
      if (globalThis.window !== undefined) {
        globalThis.window.location.href = PATHS.LOGIN;
      }
      throw new Error(ERROR_MESSAGES.UNAUTHORIZED);
    }

    const ct = res.headers.get("content-type") || "";
    const body = ct.includes("application/json") ? await res.json() : null;

    if (!res.ok) {
  const errMsg = body?.message || `HTTP ${res.status}`;

  if (
    !(
      res.status === 400 &&
      errMsg.toLowerCase().includes("already exists")
    )
  ) {
    logger.apiError(url, method, res.status, errMsg);
  }

  throw Object.assign(new Error(errMsg), { body, status: res.status });
}

    logger.apiSuccess(url, method, res.status);
    return body;
  } catch (error) {
  const isDuplicateCustomer =
    error?.status === 400 &&
    error?.message?.toLowerCase().includes("already exists");

  if (!isDuplicateCustomer) {
    logger.error(
      `API fetch failed: ${opts.method || "GET"} ${url}`,
      error,
      "apiFetch"
    );
  }

  throw error;
}
}

const PB = JSON.stringify({
  page: 0,
  sizePerPage: 10000,
  sortDirection: "ASC",
  sortField: "id",
});

function resolveProductName(entry, productMap = {}) {
  return (
    entry?.product?.productName ||
    entry?.productName ||
    entry?.productDto?.productName ||
    entry?.name ||
    productMap[entry?.productIdentifier]?.productName ||
    productMap[entry?.productIdentifier]?.name ||
    entry?.productIdentifier ||
    "Unknown Product"
  );
}

export default function CartRoute() {
  const router = useRouter();
  const [phoneInput, setPhoneInput] = useState("");
  const [debouncedPhoneInput, setDebouncedPhoneInput] = useState("");
  const [allCustomers, setAllCustomers] = useState([]);
  const [custReady, setCustReady] = useState(false);
  const [ddShow, setDdShow] = useState(false);
  const [ddIdx, setDdIdx] = useState(-1);
  const [customer, setCustomer] = useState(null);
  const [showQF, setShowQF] = useState(false);
  const [qfPhone, setQfPhone] = useState("");
  const [qfName, setQfName] = useState("");
  const [qfEmail, setQfEmail] = useState("");
  const [qfParty, setQfParty] = useState("Customer");
  const [qfSaving, setQfSaving] = useState(false);
  const [qfErr, setQfErr] = useState("");
  const [cart, setCart] = useState(null);
  const [busy, setBusy] = useState(false);
  const [products, setProducts] = useState([]);
  // FIX: productMap is a { [identifier]: product } lookup built from the products list.
  // Used as a fallback when the backend doesn't embed product name inside cart entries.
  const [productMap, setProductMap] = useState({});
  const [prodSearch, setProdSearch] = useState("");
  const [debouncedProdSearch, setDebouncedProdSearch] = useState("");
  const [prodQtys, setProdQtys] = useState({});
  const [pageErr, setPageErr] = useState("");
  const [pageOk, setPageOk] = useState("");
  const [confirmClear, setConfirmClear] = useState(false);
  const [confirmCheckout, setConfirmCheckout] = useState(false);
  const [orderConfirm, setOrderConfirm] = useState(null);
  const ddRef = useRef(null);
  const phoneDebounceRef = useRef(null);
  const prodSearchDebounceRef = useRef(null);
  const receiptRef = useRef(null);

  // Fetch initial data
  useEffect(() => {
    const fetchData = async () => {
      try {
        const customersData = await apiFetch("/api/customers/list", {
          method: "POST",
          body: PB,
        });
        setAllCustomers(Array.isArray(customersData) ? customersData : (customersData?.dtoList ?? []));
      } catch (error) {
        logger.error("Failed to fetch customers", error, "CartRoute");
      } finally {
        setCustReady(true);
      }
    };

    const fetchProducts = async () => {
      // FIX: helper to build productMap whenever we get a products list
      const applyProductsList = (list) => {
        setProducts(list);
        const map = {};
        for (const p of list) {
          if (p.identifier) map[p.identifier] = p;
        }
        setProductMap(map);
      };

      try {
        const productsData = await apiFetch("/api/products/active", {
          method: "GET",
        });
        const productsList = Array.isArray(productsData) ? productsData : (productsData?.dtoList ?? []);
        if (productsList.length > 0) {
          applyProductsList(productsList);
        } else {
          throw new Error("No products available");
        }
      } catch (error) {
        logger.error("Failed to fetch active products, trying list endpoint", error, "CartRoute");
        try {
          const productsData = await apiFetch("/api/products/list", {
            method: "POST",
            body: PB,
          });
          const productsList = Array.isArray(productsData) ? productsData : (productsData?.dtoList ?? []);
          applyProductsList(productsList);
        } catch (fallbackError) {
          logger.error("Failed to fetch products from both endpoints", fallbackError, "CartRoute");
        }
      }
    };

    fetchData();
    fetchProducts();
  }, []);

  // Debounce phone input (300ms)
  useEffect(() => {
    if (phoneDebounceRef.current) clearTimeout(phoneDebounceRef.current);
    phoneDebounceRef.current = setTimeout(() => {
      setDebouncedPhoneInput(phoneInput);
    }, 300);
    return () => {
      if (phoneDebounceRef.current) clearTimeout(phoneDebounceRef.current);
    };
  }, [phoneInput]);

  // Debounce product search (300ms)
  useEffect(() => {
    if (prodSearchDebounceRef.current) clearTimeout(prodSearchDebounceRef.current);
    prodSearchDebounceRef.current = setTimeout(() => {
      setDebouncedProdSearch(prodSearch);
    }, 300);
    return () => {
      if (prodSearchDebounceRef.current) clearTimeout(prodSearchDebounceRef.current);
    };
  }, [prodSearch]);

  // Handle dropdown close
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (ddRef.current && !ddRef.current.contains(e.target)) {
        setDdShow(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Load order confirmation from localStorage on mount
  useEffect(() => {
    try {
      const stored = logger.getStorageItem("orderConfirm");
      if (stored) {
        const parsed = JSON.parse(stored);
        setOrderConfirm(parsed);
      }
    } catch (error) {
      logger.error("Failed to load order confirmation from storage", error, "CartRoute");
    }
  }, []);

  // Save order confirmation to localStorage whenever it changes
  useEffect(() => {
    if (orderConfirm) {
      try {
        logger.setStorageItem("orderConfirm", JSON.stringify(orderConfirm));
      } catch (error) {
        logger.error("Failed to save order confirmation to storage", error, "CartRoute");
      }
    } else {
      try {
        localStorage.removeItem("orderConfirm");
      } catch (error) {
        logger.error("Failed to clear order confirmation from storage", error, "CartRoute");
      }
    }
  }, [orderConfirm]);

  const q = debouncedPhoneInput.trim().toLowerCase();
  const suggestions = q.length === 0 ? [] : allCustomers.filter((c) =>
    c.identifier?.toLowerCase().includes(q) ||
    c.customerName?.toLowerCase().includes(q) ||
    c.email?.toLowerCase().includes(q)
  ).slice(0, 8);

  const ensureCart = useCallback(async (id) => {
    try {
      const cart = await apiFetch(`/api/cart/${id}`);
      if (cart) {
        setCart(cart);
        return cart;
      }
    } catch (error) {
      logger.error(`Failed to fetch cart for ${id}`, error, "ensureCart");
    }
    try {
      const newCart = await apiFetch("/api/cart/save", {
        method: "POST",
        body: JSON.stringify({ username: id, identifier: id }),
      });
      setCart(newCart);
      return newCart;
    } catch (error) {
      const errorMsg = error.message || ERROR_MESSAGES.SERVER_ERROR;
      setPageErr(errorMsg);
      logger.error("Could not create cart", error, "ensureCart");
      return null;
    }
  }, []);

  const openQF = useCallback(() => {
    setQfPhone(phoneInput.replace(/\D/g, ""));
    setQfName("");
    setQfEmail("");
    setQfParty("Customer");
    setQfErr("");
    setShowQF(true);
    setDdShow(false);
  }, [phoneInput]);

  const selectCustomer = useCallback(async (c) => {
    if (!c?.identifier) {
      logger.warn("Invalid customer selected", "selectCustomer");
      return;
    }
    setCustomer(c);
    setPhoneInput(c.identifier);
    setDdShow(false);
    setDdIdx(-1);
    setPageErr("");
    setPageOk("");
    setCart(null);
    setBusy(true);
    await ensureCart(c.identifier);
    setBusy(false);
  }, [ensureCart]);

  const handlePhoneChange = useCallback((val) => {
    setPhoneInput(val);
    setDdIdx(-1);
    setDdShow(val.length > 0);
  }, []);

  const handlePhoneKey = useCallback((e) => {
    if (!ddShow) return;
    const total = suggestions.length + 1;
    if (e.key === "ArrowDown") {
      e.preventDefault();
      setDdIdx((i) => Math.min(i + 1, total - 1));
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setDdIdx((i) => Math.max(i - 1, -1));
    } else if (e.key === "Enter") {
      e.preventDefault();
      if (ddIdx >= 0 && ddIdx < suggestions.length) {
        selectCustomer(suggestions[ddIdx]);
      } else if (ddIdx === suggestions.length) {
        openQF();
      }
    } else if (e.key === "Escape") {
      setDdShow(false);
    }
  }, [ddShow, suggestions, ddIdx, selectCustomer, openQF]);

  const handlePrintReceipt = () => {
    if (!receiptRef.current) return;

    const receiptHTML = receiptRef.current.innerHTML;

    const htmlContent = `
      <!DOCTYPE html>
      <html>
        <head>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>Receipt</title>
          <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { 
              font-family: 'Barlow', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
              background: #fff;
              padding: 20px;
            }
            .no-print { display: none !important; }
            @media print {
              body { padding: 0; }
            }
            p { margin: 0; }
          </style>
        </head>
        <body>
          <div style="max-width: 500px; margin: 0 auto; font-size: 14px;">
            ${receiptHTML}
          </div>
          <script>
            window.onload = function() {
              window.print();
            };
          </script>
        </body>
      </html>
    `;
    // Modern approach: use Blob URL instead of deprecated document.write
    const blob = new Blob([htmlContent], { type: 'text/html;charset=UTF-8' });
    const url = URL.createObjectURL(blob);
    const newWindow = window.open(url, '_blank');
    if (newWindow) {
      // Print when the document is loaded
      newWindow.addEventListener('load', () => {
        newWindow.print();
      }, { once: true });
    }
  };

  const handleViewOrder = () => {
    router.push(`/orders`);
  };

  const handleNewOrder = () => {
    setOrderConfirm(null);
    setCustomer(null);
    setPhoneInput("");
    setDebouncedPhoneInput("");
    setCart(null);
    setPageOk("");
    setPageErr("");
  };

  const handleQFSave = useCallback(async () => {
    if (!qfPhone.trim()) {
      setQfErr("Phone is required.");
      return;
    }
    if (!qfName.trim()) {
      setQfErr("Name is required.");
      return;
    }
    setQfSaving(true);
    setQfErr("");
    try {
      const newCustomer = await apiFetch("/api/customers/save", {
        method: "POST",
        body: JSON.stringify({
          identifier: qfPhone.trim(),
          customerName: qfName.trim(),
          email: qfEmail.trim() || "",
          partyType: qfParty,
          status: true,
          creditType: "",
          credit: 0,
          creditLimit: 0,
          billingAddress: {
            identifier: "",
            phoneNo: qfPhone.trim(),
            addressType: "Billing",
            city: "",
            zipCode: "",
            country: "",
            state: "",
            addressLine: "",
            status: true,
          },
          shippingAddress: {
            identifier: "",
            phoneNo: qfPhone.trim(),
            addressType: "Shipping",
            city: "",
            zipCode: "",
            country: "",
            state: "",
            addressLine: "",
            status: true,
          },
        }),
      });
      setShowQF(false);
      setPageOk("Customer saved — ready to bill!");
      const customerId = newCustomer?.identifier ?? qfPhone.trim();
      setCustomer(newCustomer ?? { identifier: customerId, customerName: qfName.trim() });
      setPhoneInput(customerId);
      setAllCustomers((prev) => [...prev, newCustomer ?? { identifier: customerId, customerName: qfName.trim() }]);
      setBusy(true);
      await ensureCart(customerId);
      setBusy(false);
      logger.info("Customer created successfully", { id: customerId });
    } catch (error) {
  const errorMsg = error.message || ERROR_MESSAGES.SERVER_ERROR;

  if (errorMsg.toLowerCase().includes("already exists")) {
    const existingCustomer = allCustomers.find(
      (c) => c.identifier === qfPhone.trim()
    );

    if (existingCustomer) {
      setShowQF(false);
      setQfErr("");
      setCustomer(existingCustomer);
      setPhoneInput(existingCustomer.identifier);
      setPageOk("Customer already exists. Loaded existing customer.");

      setBusy(true);
      await ensureCart(existingCustomer.identifier);
      setBusy(false);

      logger.info(
        "Existing customer loaded",
        { id: existingCustomer.identifier },
        "handleQFSave"
      );

      return;
    }
  }

  setQfErr(errorMsg);
  logger.error("Failed to save customer", error, "handleQFSave");
} finally {
      setQfSaving(false);
    }
}, [qfPhone, qfName, qfEmail, qfParty, ensureCart, allCustomers]);
  const handleChangeCustomer = () => {
    setCustomer(null); setCart(null); setPhoneInput("");
    setPageErr(""); setPageOk("");
  };

  const handleAddProduct = useCallback(async (product) => {
    if (!cart) { setPageErr("Cart not ready yet."); return; }
    const qty = Number.parseInt(prodQtys[product.identifier] || "1", 10);
    if (Number.isNaN(qty) || qty < 1) { setPageErr("Quantity must be at least 1."); return; }
    setBusy(true); setPageErr(""); setPageOk("");
    try {
      const updated = await apiFetch("/api/cart/save", { method: "POST", body: JSON.stringify({
        username: cart.username ?? cart.identifier, identifier: cart.identifier,
        cartEntries: [{ cartIdentifier: cart.identifier, productIdentifier: product.identifier, quantity: qty }],
      })});
      setCart(updated);
      setProdQtys((p) => ({ ...p, [product.identifier]: "" }));
    } catch (e) { setPageErr(e.message || "Failed to add product."); }
    finally { setBusy(false); }
  }, [cart, prodQtys]);

  const handleQtyChange = useCallback(async (entry, delta) => {
    const newQty = (entry.quantity ?? 1) + delta;
    if (newQty < 1) return;
    setBusy(true); setPageErr("");
    try {
      const updated = await apiFetch(`/api/cart/update/${cart.identifier}`, { method: "POST", body: JSON.stringify({
        identifier: cart.identifier, username: cart.username ?? cart.identifier,
        cartEntries: [{ identifier: entry.identifier, cartIdentifier: cart.identifier, productIdentifier: entry.productIdentifier, quantity: newQty }],
      })});
      setCart(updated);
    } catch (e) { setPageErr(e.message || "Failed to update quantity."); }
    finally { setBusy(false); }
  }, [cart]);

  const handleRemoveEntry = useCallback(async (entryId) => {
    setBusy(true); setPageErr("");
    try {
      await apiFetch(`/api/cart/delete-entry/${entryId}`, { method: "POST", body: JSON.stringify({}) });
      setCart(await apiFetch(`/api/cart/${cart.identifier}`) ?? null);
    } catch (e) { setPageErr(e.message || "Failed to remove item."); }
    finally { setBusy(false); }
  }, [cart]);

  const handleClearCart = async () => {
    setConfirmClear(false);
    if (!cart) return;
    setBusy(true); setPageErr("");
    try {
      await apiFetch(`/api/cart/delete/${cart.identifier}`, { method: "POST", body: JSON.stringify({}) });
      setCart(await apiFetch(`/api/cart/${cart.identifier}`) ?? null);
      setPageOk("Cart cleared.");
    } catch (e) { setPageErr(e.message || "Failed to clear cart."); }
    finally { setBusy(false); }
  };

  const handleCheckout = async () => {
    setConfirmCheckout(false);
    if (!cart) {
      setPageErr("Cart is empty. Please add items before checkout.");
      return;
    }
    if (!customer) {
      setPageErr("Please select a customer before checkout.");
      return;
    }
    if (entries.length === 0) {
      setPageErr("No items in cart. Please add products before checkout.");
      return;
    }
    if (totalPrice <= 0) {
      setPageErr("Cart total is invalid. Please check your items.");
      return;
    }
    setBusy(true);
    setPageErr("");
    try {
      const orderResponse = await apiFetch("/api/orders/place", {
        method: "POST",
        body: JSON.stringify({ cartIdentifier: cart.identifier, paymentMode: "CASH" }),
      });

      setOrderConfirm({
        orderId: orderResponse?.id || orderResponse?.identifier,
        customer: customer,
        // FIX: use resolveProductName so receipt always shows the real product name,
        // not the SKU. Also use sellingPrice (not mrpPrice) for the line price,
        // and prefer entry.totalPrice directly instead of recomputing it.
        items: entries.map((e) => ({
          productName: resolveProductName(e, productMap),
          quantity: e.quantity ?? 1,
          price: Number(e.sellingPrice ?? e.mrpPrice ?? e.price ?? 0),
          totalPrice: Number(e.totalPrice ?? (e.sellingPrice ?? e.mrpPrice ?? e.price ?? 0) * (e.quantity ?? 1)),
        })),
        totalPrice: totalPrice,
        totalDiscount: totalDiscount,
        timestamp: new Date().toLocaleString("en-IN"),
        paymentMode: "CASH",
      });

      setPageOk("Order placed successfully!");
    } catch (e) {
      setPageErr(e.message || "Failed to place order.");
      setBusy(false);
    }
  };

  const entries = cart?.cartEntries ?? [];
  const entrySkus = new Set(entries.map((e) => e.productIdentifier));
  const totalPrice = Number(cart?.totalPrice ?? 0);
  const totalDiscount = Number(cart?.discount ?? 0);
  const totalMrp = entries.reduce((s, e) => s + Number(e.mrpPrice ?? 0) * Number(e.quantity ?? 1), 0);
  const pq = debouncedProdSearch.trim().toLowerCase();
  const filteredProducts = pq
    ? products.filter((p) => p.productName?.toLowerCase().includes(pq) || p.identifier?.toLowerCase().includes(pq))
    : products;

  return (
    <CartPage
      phoneInput={phoneInput}
      handlePhoneChange={handlePhoneChange}
      suggestions={suggestions}
      custReady={custReady}
      ddShow={ddShow}
      setDdShow={setDdShow}
      ddIdx={ddIdx}
      ddRef={ddRef}
      handlePhoneKey={handlePhoneKey}
      selectCustomer={selectCustomer}
      onNewCustomer={openQF}
      customer={customer}
      onChangeCustomer={handleChangeCustomer}
      showQF={showQF}
      qfPhone={qfPhone} setQfPhone={setQfPhone}
      qfName={qfName} setQfName={setQfName}
      qfEmail={qfEmail} setQfEmail={setQfEmail}
      qfParty={qfParty} setQfParty={setQfParty}
      qfSaving={qfSaving} qfErr={qfErr}
      onQFSave={handleQFSave}
      onQFCancel={() => setShowQF(false)}
      cart={cart}
      busy={busy}
      entries={entries}
      entrySkus={entrySkus}
      totalPrice={totalPrice}
      totalDiscount={totalDiscount}
      totalMrp={totalMrp}
      products={products}
      productMap={productMap}
      filteredProducts={filteredProducts}
      prodSearch={prodSearch}
      setProdSearch={setProdSearch}
      prodQtys={prodQtys}
      setProdQtys={setProdQtys}
      onAddProduct={handleAddProduct}
      onQtyChange={handleQtyChange}
      onRemoveEntry={handleRemoveEntry}
      onClearCart={handleClearCart}
      onCheckout={handleCheckout}
      pageErr={pageErr}
      pageOk={pageOk}
      confirmClear={confirmClear}
      setConfirmClear={setConfirmClear}
      confirmCheckout={confirmCheckout}
      setConfirmCheckout={setConfirmCheckout}
      orderConfirm={orderConfirm}
      onPrintReceipt={handlePrintReceipt}
      onViewOrder={handleViewOrder}
      onNewOrder={handleNewOrder}
      receiptRef={receiptRef}
    />
  );
}