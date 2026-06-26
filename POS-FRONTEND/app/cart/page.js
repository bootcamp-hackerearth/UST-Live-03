"use client";

import { useEffect, useState } from "react";
import Layout from "../components/Layout";
import api from "../services/api";
import AddCustomerForm from "./AddCustomerForm";
import Payment from "./Payment";
import OrderReceiptModal from "../order/OrderReceiptModal";

export default function CartPage() {
  const [customers, setCustomers] = useState([]);
  const [products, setProducts] = useState([]);
  const [productMap, setProductMap] = useState({});
  const [priceMap, setPriceMap] = useState({});
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [phoneSearchQuery, setPhoneSearchQuery] = useState("");
  const [customerIdentifier, setCustomerIdentifier] = useState("");
  const [cartId, setCartId] = useState("");
  const [cart, setCart] = useState(null);
  const [showAddCustomer, setShowAddCustomer] = useState(false);
  const [step, setStep] = useState("cart");
  const [paymentMethod, setPaymentMethod] = useState("");
  const [receivedAmount, setReceivedAmount] = useState("");
  const [changeAmount, setChangeAmount] = useState(0);
  const [lastAddedProduct, setLastAddedProduct] = useState(null);
  const [addingProductId, setAddingProductId] = useState(null);
  const [completedOrder, setCompletedOrder] = useState(null);

  useEffect(() => {
    loadCustomers();
    loadProducts();
    loadPrices();
  }, []);

  useEffect(() => {
    if (paymentMethod === "CASH" && receivedAmount && cart) {
      const change = Number.parseFloat(receivedAmount) - (cart.totalPrice || 0);
      setChangeAmount(Math.max(change, 0));
    } else {
      setChangeAmount(0);
    }
  }, [receivedAmount, paymentMethod, cart]);

  const loadCustomers = async () => {
    try {
      const res = await api.post("/api/customer/list", {
        page: 0,
        sizePerPage: 100,
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
      activeProducts.forEach((p) => {
        map[p.identifier] = p;
      });
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
        map[price.identifier] = price.priceAmount;
      });

      setPriceMap(map);
    } catch (err) {
      console.error(err);
    }
  };

  const recalculateCartTotals = (items, currentCustomerField) => {
    let originalPrice = 0;
    let totalPrice = 0;
    let discount = 0;

    items.forEach((item) => {
      originalPrice += item.originalPrice * item.quantity;
      totalPrice += item.totalPrice;
    });

    discount = originalPrice - totalPrice;

    return {
      customerIdentifier: currentCustomerField,
      entryCart: items,
      originalPrice: originalPrice,
      totalPrice: totalPrice,
      discount: Math.max(discount, 0),
    };
  };

  const autoCreateCart = (targetCustomerIdentifier) => {
    if (!targetCustomerIdentifier) return;
    setCartId(targetCustomerIdentifier);
    setCart({
      customerIdentifier: targetCustomerIdentifier,
      entryCart: [],
      originalPrice: 0,
      totalPrice: 0,
      discount: 0,
    });
  };

  const handleCustomerChange = (e) => {
    const val = e.target.value;
    setCustomerIdentifier(val);
    if (val) {
      autoCreateCart(val);
    } else {
      setCart(null);
      setCartId("");
    }
  };

  const handlePhoneSearchVerify = () => {
    if (!phoneSearchQuery.trim()) {
      alert("Please enter a phone number to search.");
      return;
    }

    const match = customers.find((c) => c.phoneNo === phoneSearchQuery.trim());

    if (match) {
      const targetId = match.identifier || match.phoneNo;
      setCustomerIdentifier(targetId);
      setShowAddCustomer(false);
      autoCreateCart(targetId);
    } else {
      alert("Customer record not found. Opening the 'Add Customer' interface.");
      setShowAddCustomer(true);
    }
  };

  const handleCustomerSavedSuccessfully = async (newCustomerIdentifier) => {
    try {
      setShowAddCustomer(false);
      await loadCustomers();
      if (newCustomerIdentifier) {
        setCustomerIdentifier(newCustomerIdentifier);
        autoCreateCart(newCustomerIdentifier);
      }
    } catch (err) {
      console.error("POST-SAVE SYNC ERROR =>", err);
    }
  };

  const addToCart = (productIdentifier) => {
    if (!cartId) {
      alert("No active session! Please select or verify a customer profile first before appending products.");
      return;
    }

    const productInfo = productMap[productIdentifier];
    const nameToDisplay = productInfo ? `${productInfo.brand || ""} ${productInfo.productName || ""}`.trim() : productIdentifier;

    setAddingProductId(productIdentifier);
    setLastAddedProduct(nameToDisplay);

    setTimeout(() => {
      setAddingProductId(null);
      setLastAddedProduct(null);
    }, 1200);

    const mrpPrice = priceMap[`${productIdentifier}-MRP`] || 0;
    const sellingPrice = priceMap[`${productIdentifier}-SELLING`] || mrpPrice;
    const individualDiscount = Math.max(mrpPrice - sellingPrice, 0);

    const existingItems = cart?.entryCart ? [...cart.entryCart] : [];
    const existingItemIndex = existingItems.findIndex(
      (item) => item.productIdentifier === productIdentifier
    );

    if (existingItemIndex > -1) {
      const target = existingItems[existingItemIndex];
      const nextQty = target.quantity + 1;
      existingItems[existingItemIndex] = {
        ...target,
        quantity: nextQty,
        totalPrice: nextQty * sellingPrice,
        discount: nextQty * individualDiscount,
      };
    } else {
      existingItems.push({
        identifier: `${cartId}-${productIdentifier}`,
        productIdentifier,
        quantity: 1,
        unitPrice: sellingPrice,
        originalPrice: mrpPrice,
        discount: individualDiscount,
        totalPrice: sellingPrice,
      });
    }

    setCart(recalculateCartTotals(existingItems, customerIdentifier));
  };

  const updateQuantity = (productIdentifier, currentQty, change) => {
    if (currentQty + change < 1) {
      deleteCartItem(productIdentifier);
      return;
    }

    const mrpPrice = priceMap[`${productIdentifier}-MRP`] || 0;
    const sellingPrice = priceMap[`${productIdentifier}-SELLING`] || mrpPrice;
    const individualDiscount = Math.max(mrpPrice - sellingPrice, 0);

    const updatedItems = cart.entryCart.map((item) => {
      if (item.productIdentifier === productIdentifier) {
        const nextQty = currentQty + change;
        return {
          ...item,
          quantity: nextQty,
          totalPrice: nextQty * sellingPrice,
          discount: nextQty * individualDiscount,
        };
      }
      return item;
    });

    setCart(recalculateCartTotals(updatedItems, customerIdentifier));
  };

  const deleteCartItem = (productIdentifier) => {
    if (!confirm("Remove this item completely from your order?")) return;
    const filteredItems = cart.entryCart.filter((item) => item.productIdentifier !== productIdentifier);
    setCart(recalculateCartTotals(filteredItems, customerIdentifier));
  };

  const clearCart = () => {
    if (!cartId) return;
    if (!confirm("Are you sure you want to clear this cart session?")) return;
    setCart({
      customerIdentifier,
      entryCart: [],
      originalPrice: 0,
      totalPrice: 0,
      discount: 0,
    });
  };

  const handleConfirmOrder = () => {
    if (!cart?.entryCart?.length) {
      alert("Cannot check out an empty active terminal session.");
      return;
    }
    setStep("payment");
  };

  const handleCheckoutSubmit = async () => {
    if (!cart?.entryCart?.length) {
      alert("Cannot check out an empty active terminal session.");
      return;
    }

    if (!paymentMethod) {
      alert("Please select a dynamic gateway payment method.");
      return;
    }

    if (paymentMethod === "CASH" && (!receivedAmount || Number.parseFloat(receivedAmount) < cart.totalPrice)) {
      alert("Insufficient funds provided. Cash amount cannot fall under total net balance.");
      return;
    }

    const targetCustomer = cart.customerIdentifier || customerIdentifier;

    const payload = {
      cartIdentifier: cartId,
      customerIdentifier: targetCustomer,
      paymentMethod: paymentMethod.toUpperCase(),
      originalPrice: cart.originalPrice || 0,
      discount: cart.discount || 0,
      totalPrice: cart.totalPrice || 0,
      receivedAmount: paymentMethod === "CASH" ? Number.parseFloat(receivedAmount) : cart.totalPrice,
      changeAmount: changeAmount,
      entryList: cart.entryCart.map((item) => ({
        productIdentifier: item.productIdentifier,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        originalPrice: item.originalPrice,
        discount: item.discount,
        totalPrice: item.totalPrice,
      })),
    };

    try {
      const res = await api.post("/api/order/checkout", payload);

      if (res.data?.success === true) {
        const savedOrder = res.data.data || res.data.order || res.data;
        setCompletedOrder(savedOrder);
        setCart(null);
        setCartId("");
        setCustomerIdentifier("");
        setPhoneSearchQuery("");
        setPaymentMethod("");
        setReceivedAmount("");
        setChangeAmount(0);
        setStep("cart");
      } else {
        alert(`Server Rejected Order: ${res.data?.message || "Unknown Validation Error"}`);
      }
    } catch (err) {
      console.error("ERROR EXCEPTION Pipelines =>", err);
      alert(err.response?.data?.message || "Exception encountered throughout order finalization pipelines.");
    }
  };

  const uniqueCategories = Array.from(
    new Set(products.map((p) => p.category).filter(Boolean))
  );

  const filteredProducts = products.filter((product) => {
    const matchesSearch =
      !searchTerm ||
      (product.brand?.toLowerCase() || "").includes(searchTerm.toLowerCase()) ||
      (product.model?.toLowerCase() || "").includes(searchTerm.toLowerCase()) ||
      (product.productName?.toLowerCase() || "").includes(searchTerm.toLowerCase()) ||
      (product.category?.toLowerCase() || "").includes(searchTerm.toLowerCase());

    const matchesCategory =
      !selectedCategory || product.category === selectedCategory;

    const productPrice =
      priceMap[`${product.identifier}-SELLING`] ||
      priceMap[`${product.identifier}-MRP`] ||
      0;
    const matchesPrice = !maxPrice || productPrice <= Number.parseFloat(maxPrice);

    return matchesSearch && matchesCategory && matchesPrice;
  });

  const resetFilters = () => {
    setSearchTerm("");
    setSelectedCategory("");
    setMaxPrice("");
  };

  return (
    <Layout>
      <div style={{ width: "100%", minHeight: "100vh", backgroundColor: "#f8fafc", padding: "24px", boxSizing: "border-box", fontFamily: "system-ui, sans-serif", position: "relative" }}>
        {lastAddedProduct && (
          <div style={{
            position: "fixed",
            top: "24px",
            left: "50%",
            transform: "translateX(-50%)",
            backgroundColor: "#0f172a",
            color: "#ffffff",
            padding: "12px 24px",
            borderRadius: "8px",
            boxShadow: "0 10px 25px -5px rgba(0, 0, 0, 0.1)",
            zIndex: 9999,
            fontSize: "14px",
            fontWeight: "600",
            display: "flex",
            alignItems: "center",
            gap: "8px",
            border: "1px solid #334155"
          }}>
            <span style={{ color: "#22c55e" }}>⚡</span> Syncing items stream...
          </div>
        )}

        <div style={{ maxWidth: "1600px", margin: "0 auto" }}>
          <div style={{ backgroundColor: "#ffffff", borderRadius: "12px", border: "1px solid #e2e8f0", padding: "16px 20px", display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "24px", boxShadow: "0 1px 3px rgba(0,0,0,0.05)" }}>
            <div style={{ display: "flex", alignItems: "center", gap: "20px" }}>
              <span style={{ fontWeight: "800", fontSize: "16px", color: "#1e293b", letterSpacing: "-0.025em" }}>🏪 POS Terminal Workspace</span>
              <span style={{ padding: "6px 12px", backgroundColor: "#eff6ff", color: "#2563eb", borderRadius: "6px", fontSize: "12px", fontWeight: "700" }}>
                {step === "payment" ? "Checkout Gateway" : "Billing Register Window"}
              </span>
            </div>
            <div style={{ display: "flex", gap: "10px" }}>
              <button
                onClick={() => setShowAddCustomer(!showAddCustomer)}
                style={{ padding: "8px 16px", backgroundColor: showAddCustomer ? "#0f172a" : "#ffffff", color: showAddCustomer ? "#ffffff" : "#334155", border: "1px solid #cbd5e1", borderRadius: "6px", fontSize: "12px", fontWeight: "700", cursor: "pointer", transition: "all 0.15s" }}
              >
                {showAddCustomer ? "✕ Close Form" : "👤 + Add Customer"}
              </button>
              <button
                onClick={clearCart}
                disabled={!cartId}
                style={{ padding: "8px 14px", backgroundColor: "#fef2f2", color: "#dc2626", border: "1px solid #fca5a5", borderRadius: "6px", fontSize: "12px", fontWeight: "700", cursor: "pointer", opacity: cartId ? 1 : 0.5 }}
              >
                Clear Current Order
              </button>
            </div>
          </div>

          {showAddCustomer && (
            <AddCustomerForm
              onSaved={handleCustomerSavedSuccessfully}
              onCancel={() => setShowAddCustomer(false)}
            />
          )}

          {step === "cart" && (
            <div style={{ display: "flex", gap: "24px", alignItems: "flex-start" }}>
              <div style={{ flex: "3.5", backgroundColor: "#ffffff", borderRadius: "16px", border: "1px solid #e2e8f0", padding: "24px", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.02)" }}>
                <h2 style={{ fontSize: "11px", fontWeight: "800", color: "#94a3b8", textTransform: "uppercase", margin: "0 0 16px 0", letterSpacing: "0.05em" }}>Active Checkout Register</h2>

                <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: "16px", marginBottom: "20px" }}>
                  <div>
                    <label htmlFor="phoneSearchInput" style={{ display: "block", fontSize: "11px", fontWeight: "700", color: "#64748b", marginBottom: "6px" }}>SEARCH PHONE NUMBER</label>
                    <div style={{ display: "flex", gap: "6px" }}>
                      <input id="phoneSearchInput" type="text" placeholder="Enter Phone No..." value={phoneSearchQuery} onChange={(e) => setPhoneSearchQuery(e.target.value)} style={{ flex: "1", padding: "8px 12px", border: "1px solid #cbd5e1", borderRadius: "6px", fontSize: "13px" }} />
                      <button onClick={handlePhoneSearchVerify} style={{ padding: "8px 14px", backgroundColor: "#2563eb", border: "none", borderRadius: "6px", cursor: "pointer", fontSize: "12px", fontWeight: "700", color: "#fff" }}>Verify</button>
                    </div>
                  </div>

                  <div>
                    <label htmlFor="customerSelect" style={{ display: "block", fontSize: "11px", fontWeight: "700", color: "#64748b", marginBottom: "6px" }}>CUSTOMER SELECTION PROFILE</label>
                    <select id="customerSelect" value={customerIdentifier} onChange={handleCustomerChange} style={{ width: "100%", padding: "8px 12px", border: "1px solid #cbd5e1", borderRadius: "6px", fontSize: "13px", height: "37px", backgroundColor: "#fff" }}>
                      <option value="">Choose Target Profile (Triggers Auto-Cart)</option>
                      {customers.map((c, idx) => (
                        <option key={c.identifier || c.phoneNo || idx} value={c.identifier || c.phoneNo}>
                          {c.customerName} {c.phoneNo ? `(${c.phoneNo})` : ""}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div>
                    <label htmlFor="cartIdInput" style={{ display: "block", fontSize: "11px", fontWeight: "700", color: "#64748b", marginBottom: "6px" }}>ACTIVE TERMINAL REF (CART ID)</label>
                    <input id="cartIdInput" type="text" readOnly value={cartId} placeholder="Auto-Sync Context Active" style={{ width: "100%", padding: "8px 12px", backgroundColor: "#f8fafc", border: "1px solid #cbd5e1", borderRadius: "6px", fontSize: "13px", fontWeight: "700", height: "37px", boxSizing: "border-box" }} />
                  </div>
                </div>
                <div style={{ border: "1px solid #e2e8f0", borderRadius: "12px", overflow: "hidden" }}>
                  <div style={{ backgroundColor: "#0f172a", color: "#ffffff", padding: "12px 16px", fontSize: "11px", fontWeight: "700", display: "flex", textTransform: "uppercase", letterSpacing: "0.05em" }}>
                    <div style={{ flex: "3.5" }}>Product Details</div>
                    <div style={{ flex: "1.5", textAlign: "center" }}>MRP</div>
                    <div style={{ flex: "2", textAlign: "center" }}>Discount</div>
                    <div style={{ flex: "1.5", textAlign: "center" }}>Selling Price</div>
                    <div style={{ flex: "2", textAlign: "center" }}>Quantity</div>
                    <div style={{ flex: "2", textAlign: "right" }}>Sub Total</div>
                    <div style={{ width: "60px", textAlign: "center" }}>Discard</div>
                  </div>

                  <div style={{ maxHeight: "360px", overflowY: "auto", minHeight: "140px", backgroundColor: "#fafafa" }}>
                    {cart?.entryCart?.length > 0 ? (
                      cart.entryCart.map((item) => {
                        const productInfo = productMap[item.productIdentifier];
                        return (
                          <div key={item.productIdentifier} style={{ display: "flex", padding: "14px 16px", alignItems: "center", borderBottom: "1px solid #f1f5f9", backgroundColor: "#ffffff", fontSize: "13px" }}>
                            <div style={{ flex: "3.5" }}>
                              <span style={{ fontWeight: "700", color: "#1e293b", display: "block", fontSize: "14px" }}>{productInfo?.brand || "Product"}</span>
                              {productInfo?.productName && <span style={{ fontSize: "12px", color: "#475569", display: "block", marginTop: "2px" }}>{productInfo.productName}</span>}
                              <span style={{ fontSize: "11px", color: "#64748b", display: "block", marginTop: "4px", fontFamily: "monospace", backgroundColor: "#f1f5f9", width: "fit-content", padding: "1px 6px", borderRadius: "4px" }}>ID: {item.productIdentifier}</span>
                            </div>
                            <div style={{ flex: "1.5", textAlign: "center", textDecoration: "line-through", color: "#94a3b8" }}>₹{item.originalPrice}</div>
                            <div style={{ flex: "1.5", textAlign: "center",  color: "#94a3b8" }}>₹{item.discount}</div>
                            <div style={{ flex: "1.5", textAlign: "center", fontWeight: "700", color: "#0f172a" }}>₹{item.unitPrice}</div>

                            <div style={{ flex: "2", display: "flex", justifyContent: "center", alignItems: "center", gap: "8px" }}>
                              <button onClick={() => updateQuantity(item.productIdentifier, item.quantity, -1)} style={{ width: "26px", height: "26px", border: "1px solid #cbd5e1", borderRadius: "4px", backgroundColor: "#fff", cursor: "pointer", fontWeight: "700" }}>-</button>
                              <span style={{ fontWeight: "700", minWidth: "20px", textAlign: "center" }}>{item.quantity}</span>
                              <button onClick={() => updateQuantity(item.productIdentifier, item.quantity, 1)} style={{ width: "26px", height: "26px", backgroundColor: "#2563eb", color: "#fff", border: "none", borderRadius: "4px", cursor: "pointer", fontWeight: "700" }}>+</button>
                            </div>

                            <div style={{ flex: "2", textAlign: "right", fontWeight: "700", color: "#0f172a" }}>₹{item.totalPrice}</div>

                            <div style={{ width: "60px", textAlign: "center" }}>
                              <button
                                onClick={() => deleteCartItem(item.productIdentifier)}
                                style={{ border: "none", backgroundColor: "transparent", color: "#ef4444", cursor: "pointer", display: "inline-flex", padding: "6px", borderRadius: "6px", transition: "background-color 0.15s" }}
                                onMouseEnter={(e) => e.currentTarget.style.backgroundColor = "#fef2f2"}
                                onMouseLeave={(e) => e.currentTarget.style.backgroundColor = "transparent"}
                                title="Remove item"
                              >
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                  <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5Zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5Zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6Z"/>
                                  <path d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1ZM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118ZM2.5 3h11V2h-11v1Z"/>
                                </svg>
                              </button>
                            </div>
                          </div>
                        );
                      })
                    ) : (
                      <div style={{ textAlign: "center", padding: "50px 0", color: "#94a3b8", fontSize: "14px", fontWeight: "500" }}>Your active bill calculation stream is empty. Choose a product to add.</div>
                    )}
                  </div>
                </div>

                {cart && (
                  <div style={{ marginTop: "16px" }}>
                    <div style={{ padding: "16px", backgroundColor: "#f8fafc", borderRadius: "12px", display: "flex", justifyContent: "space-between", border: "1px solid #e2e8f0" }}>
                      <div>
                        <div style={{ fontSize: "12px", color: "#64748b", fontWeight: "500" }}>Total Original MRP: <span style={{ textDecoration: "line-through" }}>₹{cart.originalPrice}</span></div>
                        <div style={{ fontSize: "13px", color: "#16a34a", fontWeight: "700", marginTop: "4px" }}>Total Combined Savings: -₹{cart.discount}</div>
                      </div>
                      <div style={{ textAlign: "right" }}>
                        <span style={{ fontSize: "11px", color: "#94a3b8", fontWeight: "700", textTransform: "uppercase", letterSpacing: "0.05em" }}>NET PAYABLE AMOUNT</span>
                        <span style={{ display: "block", fontSize: "32px", fontWeight: "900", color: "#16a34a", marginTop: "2px", letterSpacing: "-0.03em" }}>₹{cart.totalPrice}</span>
                      </div>
                    </div>
                    <button onClick={handleConfirmOrder} style={{ marginTop: "16px", width: "100%", backgroundColor: "#16a34a", color: "#fff", border: "none", padding: "14px", fontWeight: "700", borderRadius: "8px", cursor: "pointer", fontSize: "14px", transition: "background-color 0.15s" }}>
                      ✓ Proceed to Checkout Payment Gateway
                    </button>
                  </div>
                )}
              </div>
              <div style={{ flex: "2", backgroundColor: "#ffffff", borderRadius: "16px", border: "1px solid #e2e8f0", padding: "24px", boxShadow: "0 4px 6px -1px rgba(0,0,0,0.02)" }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "16px" }}>
                  <h3 style={{ margin: 0, fontSize: "15px", fontWeight: "800", color: "#0f172a" }}>Items Catalog Collection</h3>
                  {(searchTerm || selectedCategory || maxPrice) && (
                    <button onClick={resetFilters} style={{ fontSize: "12px", cursor: "pointer", color: "#2563eb", background: "none", border: "none", fontWeight: "600" }}>Clear Filters</button>
                  )}
                </div>

                <div style={{ display: "flex", flexDirection: "column", gap: "10px", marginBottom: "16px" }}>
                  <input type="text" placeholder="🔍 Search Items..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} style={{ padding: "10px 12px", borderRadius: "8px", border: "1px solid #cbd5e1", fontSize: "13px" }} />
                  <select value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)} style={{ padding: "10px 12px", borderRadius: "8px", border: "1px solid #cbd5e1", fontSize: "13px", backgroundColor: "#fff" }}>
                    <option value="">All Categories</option>
                    {uniqueCategories.map((c) => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "12px", maxHeight: "470px", overflowY: "auto", paddingRight: "4px" }}>
                  {filteredProducts.map((p) => {
                    const mrpVal = priceMap[`${p.identifier}-MRP`] || 0;
                    const sellingVal = priceMap[`${p.identifier}-SELLING`] || mrpVal;
                    const discountAmt = Math.max(mrpVal - sellingVal, 0);
                    const isCurrentlyAdding = addingProductId === p.identifier;

                    return (
                      <button
                        key={p.identifier}
                        onClick={() => !isCurrentlyAdding && addToCart(p.identifier)}
                        onKeyDown={(e) => {
                          if (e.key === "Enter" || e.key === " ") {
                            e.preventDefault();
                            !isCurrentlyAdding && addToCart(p.identifier);
                          }
                        }}
                        disabled={isCurrentlyAdding}
                        style={{
                          padding: "14px",
                          border: isCurrentlyAdding ? "1px dashed #2563eb" : "1px solid #e2e8f0",
                          borderRadius: "10px",
                          cursor: isCurrentlyAdding ? "not-allowed" : "pointer",
                          backgroundColor: isCurrentlyAdding ? "#f0f9ff" : "#ffffff",
                          transition: "all 0.15s ease-in-out",
                          display: "flex",
                          flexDirection: "column",
                          justifyContent: "space-between",
                          position: "relative",
                          opacity: isCurrentlyAdding ? 0.85 : 1
                        }}
                        onMouseEnter={(e) => {
                          if (!isCurrentlyAdding) {
                            e.currentTarget.style.boxShadow = "0 4px 12px rgba(0,0,0,0.06)";
                            e.currentTarget.style.borderColor = "#cbd5e1";
                          }
                        }}
                        onMouseLeave={(e) => {
                          if (!isCurrentlyAdding) {
                            e.currentTarget.style.boxShadow = "none";
                            e.currentTarget.style.borderColor = "#e2e8f0";
                          }
                        }}
                      >
                        <div>
                          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: "6px" }}>
                            <span style={{ fontSize: "10px", color: "#2563eb", fontWeight: "800", textTransform: "uppercase", letterSpacing: "0.025em" }}>{p.brand}</span>
                            {discountAmt > 0 && !isCurrentlyAdding && (
                              <span style={{ fontSize: "10px", color: "#16a34a", backgroundColor: "#f0fdf4", padding: "2px 6px", borderRadius: "4px", fontWeight: "700", border: "1px solid #bbf7d0" }}>
                                Save ₹{discountAmt}
                              </span>
                            )}
                          </div>

                          <div style={{ fontSize: "13px", fontWeight: "700", color: "#0f172a", lineHeight: "1.4" }}>{p.identifier || "Unnamed Item"}</div>

                          <div style={{ fontSize: "11px", color: "#64748b", marginTop: "6px", fontFamily: "monospace", backgroundColor: isCurrentlyAdding ? "#e0f2fe" : "#f8fafc", padding: "2px 6px", borderRadius: "4px", width: "fit-content" }}>
                            model: {p.model}
                          </div>
                        </div>

                        {isCurrentlyAdding ? (
                          <div style={{
                            marginTop: "12px",
                            fontSize: "11px",
                            fontWeight: "700",
                            color: "#2563eb",
                            backgroundColor: "#e0f2fe",
                            padding: "6px 8px",
                            borderRadius: "6px",
                            textAlign: "center",
                            animation: "pulse 1.5s infinite"
                          }}>
                             Adding product...
                          </div>
                        ) : (
                          <div style={{ marginTop: "14px", display: "flex", alignItems: "baseline", gap: "6px", borderTop: "1px dashed #f1f5f9", paddingTop: "10px" }}>
                            <span style={{ fontSize: "16px", fontWeight: "800", color: "#0f172a" }}>₹{sellingVal}</span>
                            {discountAmt > 0 && (
                              <span style={{ fontSize: "12px", color: "#94a3b8", textDecoration: "line-through" }}>₹{mrpVal}</span>
                            )}
                          </div>
                        )}
                      </button>
                    );
                  })}
                </div>
              </div>
            </div>
          )}

          {step === "payment" && cart && (
            <Payment
              cart={cart}
              paymentMethod={paymentMethod}
              setPaymentMethod={setPaymentMethod}
              receivedAmount={receivedAmount}
              setReceivedAmount={setReceivedAmount}
              changeAmount={changeAmount}
              setStep={setStep}
              handleCheckoutSubmit={handleCheckoutSubmit}
            />
          )}

        </div>

        <OrderReceiptModal
          order={completedOrder}
          onClose={() => setCompletedOrder(null)}
          productMap={productMap}
          title="Order Placed Successfully"
          showSuccessIcon={true}
        />

      </div>
    </Layout>
  );
}