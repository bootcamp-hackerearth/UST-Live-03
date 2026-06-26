"use client";

import { useState, useEffect, useCallback, useRef } from "react";
import { useRouter } from "next/navigation";
import PaymentModal from "../payments/PaymentsModal";
import api from "@/app/api/axios";

const CATEGORIES_ALL = "All";

export default function SalesTerminal() {
    const router = useRouter();

    const [customers, setCustomers] = useState([]);
    const [selectedCustomer, setSelectedCustomer] = useState("");
    const [customerSearchInput, setCustomerSearchInput] = useState("");
    const [showCustomerDropdown, setShowCustomerDropdown] = useState(false);
    const [products, setProducts] = useState([]);
    const [prices, setPrices] = useState([]);
    const [cart, setCart] = useState(null);
    const [quantities, setQuantities] = useState({});

    const [loading, setLoading] = useState(true);
    const [loadingAction, setLoadingAction] = useState(false);
    const [error, setError] = useState("");
    const [productSearchTerm, setProductSearchTerm] = useState("");
    const [activeCategory, setActiveCategory] = useState(CATEGORIES_ALL);
    const [showConfirm, setShowConfirm] = useState(false);

    const [showAddCustomerModal, setShowAddCustomerModal] = useState(false);
    const [newCustomer, setNewCustomer] = useState({ customerName: "", identifier: "", username: "" });

    const [showPayment, setShowPayment] = useState(false);

    const customerRef = useRef(null);

    useEffect(() => {
        async function bootstrapPOS() {
            try {
                setLoading(true);
                setError("");
                const [customerRes, productRes, priceRes] = await Promise.all([
                    api.get("/customer/getAllActive"),
                    api.get("/product/getAllActive"),
                    api.get("/price/getAllActive"),
                ]);
                setCustomers(customerRes.data || []);
                setProducts(productRes.data || []);
                setPrices(priceRes.data || []);
            } catch {
                setError("Failed to load terminal data.");
            } finally {
                setLoading(false);
            }
        }
        bootstrapPOS();
    }, []);

    useEffect(() => {
        function handleClickOutside(e) {
            if (customerRef.current && !customerRef.current.contains(e.target)) {
                setShowCustomerDropdown(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const updateCartState = useCallback((cartData) => {
        setCart(cartData);
        const qtyMap = {};
        (cartData?.cartEntryDtoList || []).forEach((entry) => {
            if (entry.product) qtyMap[entry.product] = Math.floor(Number(entry.quantity) || 0);
        });
        setQuantities(qtyMap);
    }, []);

    const refreshActiveCart = useCallback(async (customerIdentifier) => {
        if (!customerIdentifier) return;
        try {
            const updated = await api.post("/cart/getCart", { identifier: customerIdentifier });
            updateCartState(updated.data);
        } catch {
            setError("Failed to sync cart with server.");
        }
    }, [updateCartState]);

    async function handleSelectCustomer(customerIdentifier, customCustomerList = null) {
        setSelectedCustomer(customerIdentifier);
        const list = customCustomerList || customers;
        const match = list.find((c) => c.identifier === customerIdentifier);
        setCustomerSearchInput(match ? `${match.customerName || "Walk-In"} · ${match.identifier}` : customerIdentifier);
        setShowCustomerDropdown(false);
        setError("");
        if (!customerIdentifier) { setCart(null); setQuantities({}); return; }
        try {
            setLoadingAction(true);
            let res = await api.post("/cart/getCart", { identifier: customerIdentifier });
            if (!res?.data?.success) res = await api.post("/cart/add", { identifier: customerIdentifier });
            updateCartState(res.data);
        } catch {
            setError("Could not retrieve or create a cart for this customer.");
        } finally {
            setLoadingAction(false);
        }
    }

    function handleClearCustomer() {
        setSelectedCustomer("");
        setCustomerSearchInput("");
        setCart(null);
        setQuantities({});
        setError("");
    }

    async function handleQuickAddCustomer(e) {
        e.preventDefault();
        if (!newCustomer.customerName || !newCustomer.identifier || !newCustomer.username) {
            setError("Please fill out all fields.");
            return;
        }
        try {
            setLoadingAction(true);
            setError("");
            const res = await api.post("/customer/add", newCustomer);
            const saved = res.data?.customer || res.data || { ...newCustomer };
            const updatedCustomers = [...customers, saved];
            setCustomers(updatedCustomers);
            setShowAddCustomerModal(false);
            setNewCustomer({ customerName: "", identifier: "", username: "" });
            await handleSelectCustomer(saved.identifier, updatedCustomers);
        } catch {
            setError("Failed to create customer.");
        } finally {
            setLoadingAction(false);
        }
    }

    async function handleAddProduct(productIdentifier) {
        if (!selectedCustomer) { setError("Select a customer before adding items."); return; }
        setLoadingAction(true);
        setError("");
        const prev = { ...quantities };
        setQuantities((q) => ({ ...q, [productIdentifier]: (q[productIdentifier] || 0) + 1 }));
        try {
            await api.post("/cartEntry/addEntry", { product: productIdentifier, cart: selectedCustomer, quantity: 1 });
            await api.put("/cart/addToCart", { identifier: selectedCustomer });
            await refreshActiveCart(selectedCustomer);
        } catch {
            setQuantities(prev);
            setError("Failed to add product to cart.");
        } finally {
            setLoadingAction(false);
        }
    }

    async function handleReduceProduct(productIdentifier) {
        const currentQty = quantities[productIdentifier] || 0;
        if (currentQty === 0) return;
        setLoadingAction(true);
        setError("");
        const prev = { ...quantities };
        setQuantities((q) => {
            const updated = { ...q };
            if (currentQty === 1) delete updated[productIdentifier];
            else updated[productIdentifier] = currentQty - 1;
            return updated;
        });
        try {
            if (currentQty === 1) {
                await api.delete("/cart/deleteEntry", { data: { product: productIdentifier, cart: selectedCustomer } });
            } else {
                await api.post("/cartEntry/addEntry", { product: productIdentifier, cart: selectedCustomer, quantity: -1 });
                await api.put("/cart/addToCart", { identifier: selectedCustomer });
            }
            await refreshActiveCart(selectedCustomer);
        } catch {
            setQuantities(prev);
            setError("Failed to update item quantity.");
        } finally {
            setLoadingAction(false);
        }
    }

    async function confirmDeleteCart() {
        if (!selectedCustomer) return;
        setLoadingAction(true);
        setError("");
        setShowConfirm(false);
        try {
            await api.delete("/cart/deleteCart", { data: { identifier: selectedCustomer } });
            await api.post("/cart/add", { identifier: selectedCustomer });
            setCart(null);
            setQuantities({});
            handleClearCustomer();
        } catch {
            setError("Failed to void order.");
        } finally {
            setLoadingAction(false);
        }
    }

    const mappedProducts = products.map((product) => {
        const priceMatch = prices.find((p) => p.identifier === product.identifier);
        return {
            ...product,
            sellingPrice: priceMatch?.sellingPrice ?? null,
            mrp: priceMatch?.mrp ?? null,
        };
    });

    const categories = [CATEGORIES_ALL, ...new Set(mappedProducts.flatMap((p) => p.categories || []))];

    const filteredProducts = mappedProducts.filter((p) => {
        const matchCat = activeCategory === CATEGORIES_ALL || (p.categories || []).includes(activeCategory);
        const q = productSearchTerm.toLowerCase();
        const matchQ = !q || p.name?.toLowerCase().includes(q) || p.brand?.toLowerCase().includes(q) || p.identifier?.toLowerCase().includes(q);
        return matchCat && matchQ;
    });

    const filteredCustomers =
        customerSearchInput.trim() === "" || selectedCustomer
            ? []
            : customers.filter(
                (c) =>
                    c.identifier?.toLowerCase().includes(customerSearchInput.toLowerCase()) ||
                    c.customerName?.toLowerCase().includes(customerSearchInput.toLowerCase())
            );

    const activeCartEntries = cart?.cartEntryDtoList || [];
    const hasItems = activeCartEntries.length > 0;
    const displayTotal = Number(cart?.totalPrice || 0);
    const displayDiscount = Number(cart?.totalDiscount || 0);
    const displaySubtotal = displayTotal + displayDiscount;

    const totalQty = Object.values(quantities).reduce((s, q) => s + q, 0);

    const selectedCustomerObj = customers.find((c) => c.identifier === selectedCustomer);
    const custInitials = selectedCustomerObj
        ? (selectedCustomerObj.customerName || "WI")
            .split(" ")
            .map((w) => w[0])
            .join("")
            .slice(0, 2)
            .toUpperCase()
        : "";

    let cartBody = null;
    if (!selectedCustomer) {
        cartBody = (
            <div className="flex-1 flex flex-col items-center justify-center text-center px-6 gap-3">
                <div className="w-14 h-14 rounded-2xl bg-[#006E74]/8 flex items-center justify-center text-2xl">👤</div>
                <p className="text-sm font-semibold text-gray-500">No customer selected</p>
                <p className="text-xs text-gray-400 leading-relaxed">Search and select a customer to start building an order.</p>
            </div>
        );
    } else if (hasItems) {
        cartBody = (
            <>
                <div className="flex-1 overflow-y-auto px-4 py-3 flex flex-col gap-2">
                    {activeCartEntries.map((entry) => {
                        const product = mappedProducts.find((p) => p.identifier === entry.product);
                        return (
                            <div key={entry.identifier} className="flex items-center gap-3 bg-gray-50 rounded-xl p-3 border border-gray-100">
                                <div className="w-9 h-9 rounded-lg bg-[#006E74]/10 flex items-center justify-center shrink-0">
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#006E74" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
                                        <rect x="3" y="3" width="18" height="18" rx="2" /><circle cx="8.5" cy="8.5" r="1.5" /><polyline points="21 15 16 10 5 21" />
                                    </svg>
                                </div>
                                <div className="flex-1 min-w-0">
                                    <p className="text-xs font-semibold text-[#231F20] truncate">
                                        {product?.name || entry.product}
                                    </p>
                                    <p className="text-[10px] text-gray-400 mt-0.5">
                                        Qty {Math.floor(entry.quantity)}
                                        {Number(entry.discount) > 0 && (
                                            <span className="ml-2 text-[#0097AC] font-semibold">
                                                · saved ₹{Number(entry.discount).toFixed(2)}
                                            </span>
                                        )}
                                    </p>
                                </div>
                                <span className="text-xs font-bold text-[#231F20] shrink-0">
                                    ₹{Number(entry.totalPrice || 0).toFixed(2)}
                                </span>
                            </div>
                        );
                    })}
                </div>

                <div className="px-4 pb-4 pt-3 border-t border-gray-100 flex flex-col gap-2 shrink-0">
                    {displayDiscount > 0 && (
                        <>
                            <div className="flex justify-between text-xs text-gray-500">
                                <span>Subtotal (MRP)</span>
                                <span>₹{displaySubtotal.toFixed(2)}</span>
                            </div>
                            <div className="flex justify-between text-xs text-[#0097AC] font-semibold">
                                <span>Total savings</span>
                                <span>− ₹{displayDiscount.toFixed(2)}</span>
                            </div>
                        </>
                    )}
                    <div className="flex justify-between items-baseline pt-2 border-t border-gray-100">
                        <span className="text-sm font-bold text-[#231F20]">Total payable</span>
                        <span className="text-xl font-black text-[#006E74]">₹{displayTotal.toFixed(2)}</span>
                    </div>

                    <button
                        onClick={() => setShowPayment(true)}
                        className="w-full mt-3 py-3 bg-[#006E74] text-white font-bold rounded-xl border-none shadow-md cursor-pointer hover:bg-[#0097AC] transition-all text-sm"
                    >
                        💳 Proceed to payment
                    </button>

                    <button
                        onClick={() => setShowConfirm(true)}
                        disabled={loadingAction}
                        className="w-full py-2 bg-transparent text-gray-400 border border-gray-200 rounded-xl text-xs font-semibold hover:border-red-300 hover:text-red-500 transition-colors cursor-pointer disabled:opacity-50"
                    >
                        Cancel order
                    </button>
                </div>
            </>
        );
    } else {
        cartBody = (
            <div className="flex-1 flex flex-col items-center justify-center text-center px-6 gap-3">
                <div className="w-14 h-14 rounded-2xl bg-[#006E74]/8 flex items-center justify-center text-2xl">🛒</div>
                <p className="text-sm font-semibold text-gray-500">Cart is empty</p>
                <p className="text-xs text-gray-400 leading-relaxed">Add products from the catalog on the left.</p>
            </div>
        );
    }

    return (
        <div className="flex h-full w-full min-w-0 bg-[#f4f6f8] overflow-hidden">
            <div className="w-1/2 min-w-0 flex flex-col overflow-hidden">


                <div className="bg-white border-b border-gray-200 px-5 py-3 flex items-center gap-3 shrink-0">
                    <button
                        onClick={() => router.push("/pos/home")}
                        className="flex items-center gap-1.5 px-3 py-2 text-xs font-semibold text-[#006E74] border border-[#006E74] rounded-lg hover:bg-[#006E74]/5 transition-colors shrink-0"
                    >
                        ← Home
                    </button>

                    <div ref={customerRef} className="relative w-64">
                        <div className="relative flex items-center">
                            <span className="absolute left-3 text-gray-400 text-sm">👤</span>
                            <input
                                type="text"
                                placeholder="Search customer..."
                                value={customerSearchInput}
                                onChange={(e) => {
                                    setCustomerSearchInput(e.target.value);
                                    if (selectedCustomer) setSelectedCustomer("");
                                    setShowCustomerDropdown(true);
                                }}
                                onFocus={() => setShowCustomerDropdown(true)}
                                disabled={loadingAction}
                                className="w-full pl-8 pr-7 py-2 border border-gray-300 rounded-lg text-xs bg-gray-50 focus:outline-none focus:ring-2 focus:ring-[#0097AC] focus:bg-white disabled:opacity-60"
                            />
                            {customerSearchInput && (
                                <button onClick={handleClearCustomer} className="absolute right-2.5 text-gray-400 hover:text-gray-600 text-xs font-bold bg-transparent border-none cursor-pointer">✕</button>
                            )}
                        </div>

                        {showCustomerDropdown && filteredCustomers.length > 0 && (
                            <div className="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg max-h-48 overflow-y-auto z-50">
                                {filteredCustomers.map((c) => (
                                    <button
                                        key={c.identifier}
                                        type="button"
                                        onClick={() => handleSelectCustomer(c.identifier)}
                                        className="w-full text-left px-4 py-2.5 flex justify-between items-center text-xs hover:bg-gray-50 transition-colors"
                                    >
                                        <span className="font-semibold text-gray-800">{c.customerName || "Walk-In"}</span>
                                        <span className="font-mono text-gray-400 bg-gray-100 px-2 py-0.5 rounded">{c.identifier}</span>
                                    </button>
                                ))}
                            </div>
                        )}

                        {showCustomerDropdown && customerSearchInput.trim() !== "" && !selectedCustomer && filteredCustomers.length === 0 && (
                            <div className="absolute top-full left-0 right-0 mt-1 bg-white border border-gray-200 rounded-xl shadow-lg z-50 p-3 flex flex-col gap-2">
                                <span className="text-xs text-gray-400">No customer found.</span>
                                <button
                                    type="button"
                                    onClick={() => {
                                        const digits = customerSearchInput.replaceAll(/\D/g, "");
                                        setNewCustomer((prev) => ({ ...prev, identifier: digits }));
                                        setShowAddCustomerModal(true);
                                        setShowCustomerDropdown(false);
                                    }}
                                    className="py-1.5 bg-[#006E74]/10 text-[#006E74] font-semibold rounded-lg text-xs text-center hover:bg-[#006E74]/20 transition-colors border-none cursor-pointer"
                                >
                                    + Add new customer
                                </button>
                            </div>
                        )}
                    </div>

                    <div className="relative ml-auto w-52">
                        <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 text-sm">🔍</span>
                        <input
                            type="text"
                            placeholder="Search products..."
                            value={productSearchTerm}
                            onChange={(e) => setProductSearchTerm(e.target.value)}
                            className="w-full pl-8 pr-3 py-2 border border-gray-300 rounded-lg text-xs outline-none focus:border-[#006E74] bg-white"
                        />
                    </div>
                </div>

                <div className="bg-white border-b border-gray-200 px-5 py-2.5 flex flex-wrap gap-2 shrink-0">
                    {categories.map((cat) => (
                        <button
                            key={cat}
                            onClick={() => setActiveCategory(cat)}
                            className={`px-3.5 py-1 rounded-full text-xs font-semibold whitespace-nowrap border transition-all cursor-pointer ${
                                activeCategory === cat
                                    ? "bg-[#006E74] text-white border-[#006E74]"
                                    : "bg-white text-gray-500 border-gray-200 hover:border-[#0097AC] hover:text-[#006E74]"
                            }`}
                        >
                            {cat}
                        </button>
                    ))}
                </div>

                {error && (
                    <div className="mx-5 mt-3 bg-red-50 border border-red-200 text-red-700 rounded-xl px-4 py-2.5 text-xs font-medium shrink-0">
                        ⚠️ {error}
                    </div>
                )}

                {loading ? (
                    <div className="flex-1 flex items-center justify-center text-sm text-gray-400">Loading products...</div>
                ) : (
                    <div className="flex-1 overflow-y-auto p-5">
                        <div className="grid grid-cols-[repeat(auto-fill,minmax(160px,1fr))] gap-4">
                            {filteredProducts.map((product) => {
                                const qty = quantities[product.identifier] || 0;
                                const hasPrice = product.sellingPrice !== null && product.sellingPrice !== undefined;
                                const hasDiscount = hasPrice && product.mrp && Number(product.mrp) > Number(product.sellingPrice);
                                const discountPct = hasDiscount
                                    ? Math.round((1 - Number(product.sellingPrice) / Number(product.mrp)) * 100)
                                    : 0;

                                return (
                                    <div
                                        key={product.identifier}
                                        className={`bg-white rounded-2xl overflow-hidden flex flex-col transition-all duration-150 border ${qty > 0
                                                ? "border-[#006E74] ring-1 ring-[#006E74]/30 shadow-sm"
                                                : "border-gray-200 hover:border-[#0097AC]/60 hover:shadow-sm"
                                            }`}
                                    >
                                        <div className="relative w-full aspect-square bg-[#f0fafa] flex items-center justify-center overflow-hidden">
                                            <div className="flex flex-col items-center gap-1 opacity-40">
                                                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#006E74" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round">
                                                    <rect x="3" y="3" width="18" height="18" rx="2" /><circle cx="8.5" cy="8.5" r="1.5" /><polyline points="21 15 16 10 5 21" />
                                                </svg>
                                                <span className="text-[9px] font-mono text-[#006E74] tracking-wider">{product.identifier}</span>
                                            </div>

                                            {qty > 0 && (
                                                <span className="absolute top-2 left-2 bg-[#006E74] text-white text-[10px] font-bold px-2 py-0.5 rounded-full">
                                                    {qty} added
                                                </span>
                                            )}
                                            {discountPct > 0 && (
                                                <span className="absolute top-2 right-2 bg-[#0097AC]/15 text-[#006E74] text-[10px] font-bold px-2 py-0.5 rounded-full">
                                                    {discountPct}% off
                                                </span>
                                            )}
                                        </div>

                                        <div className="p-3 flex flex-col gap-1 flex-1">
                                            <div className="flex items-center justify-between gap-1">
                                                <span className="text-[10px] font-semibold text-gray-400 uppercase tracking-wide truncate">
                                                    {product.brand || "Generic"}
                                                </span>
                                                {product.categories?.[0] && (
                                                    <span className="text-[9px] font-semibold text-[#006E74] bg-[#006E74]/10 px-1.5 py-0.5 rounded-full whitespace-nowrap">
                                                        {product.categories[0]}
                                                    </span>
                                                )}
                                            </div>

                                            <h3 className="text-xs font-semibold text-[#231F20] leading-snug line-clamp-2 min-h-[32px]">
                                                {product.name?.trim() || product.identifier}
                                            </h3>

                                            <div className="flex items-baseline gap-1.5 mt-1">
                                                {hasPrice ? (
                                                    <>
                                                        <span className="text-sm font-bold text-[#231F20]">
                                                            ₹{Number(product.sellingPrice).toFixed(2)}
                                                        </span>
                                                        {hasDiscount && (
                                                            <span className="text-[10px] text-gray-400 line-through">
                                                                ₹{Number(product.mrp).toFixed(2)}
                                                            </span>
                                                        )}
                                                    </>
                                                ) : (
                                                    <span className="text-[10px] font-semibold text-amber-600 bg-amber-50 border border-amber-200 px-2 py-0.5 rounded-full">
                                                        Price not set
                                                    </span>
                                                )}
                                            </div>

                                            <div className="mt-2">
                                                {(() => {
                                                    if (!hasPrice) {
                                                        return (
                                                            <button disabled className="w-full py-1.5 bg-gray-100 text-gray-400 rounded-lg text-xs font-semibold cursor-not-allowed">
                                                                Unavailable
                                                            </button>
                                                        );
                                                    }
                                                    if (qty === 0) {
                                                        return (
                                                            <button
                                                                onClick={() => handleAddProduct(product.identifier)}
                                                                disabled={loadingAction}
                                                                className="w-full py-1.5 bg-[#006E74] text-white rounded-lg text-xs font-semibold border-none cursor-pointer hover:bg-[#0097AC] transition-colors disabled:bg-gray-300 disabled:cursor-not-allowed"
                                                            >
                                                                + Add to order
                                                            </button>
                                                        );
                                                    }
                                                    return (
                                                        <div className="flex items-center justify-between border border-[#006E74] rounded-lg overflow-hidden">
                                                            <button
                                                                onClick={() => handleReduceProduct(product.identifier)}
                                                                disabled={loadingAction}
                                                                className="flex-1 py-1.5 text-[#006E74] font-bold text-base hover:bg-[#006E74]/5 transition-colors border-none cursor-pointer disabled:opacity-50"
                                                            >
                                                                −
                                                            </button>
                                                            <span className="text-xs font-bold text-[#231F20] px-2">{qty}</span>
                                                            <button
                                                                onClick={() => handleAddProduct(product.identifier)}
                                                                disabled={loadingAction}
                                                                className="flex-1 py-1.5 text-[#006E74] font-bold text-base hover:bg-[#006E74]/5 transition-colors border-none cursor-pointer disabled:opacity-50"
                                                            >
                                                                +
                                                            </button>
                                                        </div>
                                                    );
                                                })()}
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                )}
            </div>

            <div className="w-1/2 min-w-0 flex-shrink-0 bg-white border-l border-gray-200 flex flex-col overflow-hidden">
                <div className="px-5 pt-4 pb-3 border-b border-gray-100 shrink-0">
                    <div className="flex items-center justify-between">
                        <span className="text-xs font-bold text-gray-500 uppercase tracking-widest">Current Order</span>
                        <span className="bg-[#006E74] text-white text-[10px] font-bold px-2.5 py-1 rounded-full">
                            {totalQty} {totalQty === 1 ? "item" : "items"}
                        </span>
                    </div>

                    {selectedCustomer && selectedCustomerObj && (
                        <div className="mt-3 flex items-center gap-2.5 bg-[#006E74]/8 rounded-xl px-3 py-2.5">
                            <div className="w-8 h-8 rounded-full bg-[#006E74] flex items-center justify-center text-white text-xs font-bold shrink-0">
                                {custInitials}
                            </div>
                            <div className="flex-1 min-w-0">
                                <p className="text-xs font-semibold text-[#006E74] truncate">
                                    {selectedCustomerObj.customerName || "Walk-In"}
                                </p>
                                <p className="text-[10px] text-[#0097AC] font-mono">{selectedCustomer}</p>
                            </div>
                            <button onClick={handleClearCustomer} className="text-[#006E74]/50 hover:text-[#006E74] text-xs font-bold bg-transparent border-none cursor-pointer shrink-0">✕</button>
                        </div>
                    )}
                </div>

                {cartBody}
            </div>

            {showAddCustomerModal && (
                <div className="fixed  inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-[10000]">
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-sm mx-4 overflow-hidden border border-gray-100">
                        <div className="px-6 pt-5 pb-4 border-b border-gray-100 flex items-center justify-between">
                            <h3 className="text-sm font-bold text-[#231F20] uppercase tracking-wide">⚡ New customer</h3>
                            <button
                                type="button"
                                onClick={() => setShowAddCustomerModal(false)}
                                className="text-gray-400 hover:text-gray-600 bg-transparent border-none cursor-pointer text-base font-bold"
                            >
                                ✕
                            </button>
                        </div>

                        <form onSubmit={handleQuickAddCustomer} className="px-6 py-5 flex flex-col gap-4">
                            <div>
                                <label htmlFor="newCustomerName" className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1.5">Full name</label>
                                <input
                                    id="newCustomerName"
                                    type="text"
                                    required
                                    placeholder="Customer full name"
                                    value={newCustomer.customerName}
                                    onChange={(e) => setNewCustomer({ ...newCustomer, customerName: e.target.value })}
                                    className="w-full px-3 py-2.5 border border-gray-300 rounded-xl text-sm bg-gray-50 focus:outline-none focus:ring-2 focus:ring-[#0097AC] focus:bg-white"
                                />
                            </div>
                            <div>
                                <label htmlFor="newCustomerIdentifier" className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1.5">Phone number</label>
                                <input
                                    id="newCustomerIdentifier"
                                    type="tel"
                                    required
                                    placeholder="10-digit mobile number"
                                    value={newCustomer.identifier}
                                    onChange={(e) => setNewCustomer({ ...newCustomer, identifier: e.target.value })}
                                    className="w-full px-3 py-2.5 border border-gray-300 rounded-xl text-sm bg-gray-50 focus:outline-none focus:ring-2 focus:ring-[#0097AC] focus:bg-white"
                                />
                            </div>
                            <div>
                                <label htmlFor="newCustomerUsername" className="block text-[10px] font-bold text-gray-400 uppercase tracking-wider mb-1.5">Email address</label>
                                <input
                                    id="newCustomerUsername"
                                    type="email"
                                    required
                                    placeholder="name@email.com"
                                    value={newCustomer.username}
                                    onChange={(e) => setNewCustomer({ ...newCustomer, username: e.target.value })}
                                    className="w-full px-3 py-2.5 border border-gray-300 rounded-xl text-sm bg-gray-50 focus:outline-none focus:ring-2 focus:ring-[#0097AC] focus:bg-white"
                                />
                            </div>

                            <div className="flex gap-3 pt-1">
                                <button
                                    type="button"
                                    onClick={() => setShowAddCustomerModal(false)}
                                    className="flex-1 py-2.5 bg-gray-100 text-gray-600 font-semibold rounded-xl text-sm border-none hover:bg-gray-200 cursor-pointer"
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    disabled={loadingAction}
                                    className="flex-1 py-2.5 bg-[#006E74] text-white font-semibold rounded-xl text-sm border-none hover:bg-[#0097AC] cursor-pointer disabled:bg-gray-300 transition-colors"
                                >
                                    {loadingAction ? "Saving..." : "Save & select"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {showConfirm && (
                <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-[9999]">
                    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-xs mx-4 text-center px-6 py-7">
                        <div className="w-12 h-12 rounded-full bg-red-50 flex items-center justify-center text-xl mx-auto mb-4">🗑️</div>
                        <h3 className="text-sm font-bold text-[#231F20] mb-2">Cancel this Sale?</h3>
                        <p className="text-xs text-gray-500 leading-relaxed mb-5">
                            All items for customer <span className="font-semibold text-gray-700">{selectedCustomer}</span> will be removed. This cannot be undone.
                        </p>
                        <div className="flex gap-3">
                            <button
                                onClick={() => setShowConfirm(false)}
                                className="flex-1 py-2.5 bg-gray-100 text-gray-600 font-semibold rounded-xl text-sm border-none hover:bg-gray-200 cursor-pointer"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={confirmDeleteCart}
                                className="flex-1 py-2.5 bg-red-600 text-white font-semibold rounded-xl text-sm border-none hover:bg-red-700 cursor-pointer"
                            >
                                Confirm Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {showPayment && (
                <PaymentModal
                    cart={cart}
                    customer={customers.find((c) => c.identifier === selectedCustomer) || { identifier: selectedCustomer, customerName: "Walk-In" }}
                    onClose={() => setShowPayment(false)}
                />
            )}
        </div>
    );
}