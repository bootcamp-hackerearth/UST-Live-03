"use client";

import { useState, useEffect, useCallback } from "react";
import AddCustomer from "./components/AddCustomer";
import { proxyPost, proxyGet } from "./proxyClient";
import ProductPicker from "./ProductPicker";
import CartSummary from "./CartSummary";
import BillingModal from "./components/BillingModal";
import { order } from "../lib/entities/order";

export default function SalesPage() {

    const [customers, setCustomers] = useState([]);
    const [selectedCustomer, setSelectedCustomer] = useState(null);
    const [carts, setCarts] = useState({});
    const [loading, setLoading] = useState(false);
    const [completedOrder, setCompletedOrder] = useState(null);

    const [isCustomerModalOpen, setIsCustomerModalOpen] = useState(false);

    const cart = selectedCustomer ? carts[selectedCustomer.identifier] || null : null;

    useEffect(() => {
        const loadCustomers = async () => {
            try {
                const response = await proxyPost("/api/customer/list", {
                    page: 0,
                    sizePerPage: 100,
                    sortField: "identifier",
                    sortDirection: "ASC",
                });
                setCustomers(response.dtoList || []);
            } catch (error) {
                console.error("Failed to load customers:", error);
            }
        };
        loadCustomers();
    }, []);

    const unwrapEntries = (response) => Array.isArray(response) ? response : response?.dtoList || [];

    const refreshCart = useCallback(async (cartId = cart?.identifier) => {
        if (!cartId || !selectedCustomer) return;

        try {
            const [cartResponse, entriesResponse] = await Promise.all([
                proxyGet(`/api/cart/get?identifier=${encodeURIComponent(cartId)}`),
                proxyGet(`/api/cartEntry/listByCartId?cartId=${encodeURIComponent(cartId)}`),
            ]);

            setCarts((prevCarts) => ({
                ...prevCarts,
                [selectedCustomer.identifier]: {
                    ...cartResponse,
                    cartEntryDtoList: unwrapEntries(entriesResponse),
                },
            }));
        } catch (error) {
            console.error("Failed to refresh cart:", error);
        }
    }, [cart?.identifier, selectedCustomer]);

    const initializeCart = async (targetCustomer = selectedCustomer) => {
        if (!targetCustomer) return null;

        try {
            setLoading(true);
            const cartId = targetCustomer.identifier;
            const response = await proxyPost("/api/cart/add", {
                identifier: cartId,
                coupon: null,
            });

            const newCart = {
                ...response,
                identifier: cartId,
                cartEntryDtoList: [],
            };

            setCarts((prevCarts) => ({ ...prevCarts, [cartId]: newCart }));
            return newCart;
        } catch (error) {
            console.error("Failed to create cart:", error);
            return null;
        } finally {
            setLoading(false);
        }
    };

    const handleCustomerAdded = async (newCustomer) => {
        setCustomers((prev) => [...prev, newCustomer]);
        setSelectedCustomer(newCustomer);

        try {
            setLoading(true);
            const cartId = newCustomer.identifier;
            const response = await proxyPost("/api/cart/add", {
                identifier: cartId,
                coupon: null,
            });

            const newCart = {
                ...response,
                identifier: cartId,
                cartEntryDtoList: [],
            };

            setCarts((prevCarts) => ({ ...prevCarts, [cartId]: newCart }));
        } catch (error) {
            console.error("Failed auto-initializing active ledger:", error);
        } finally {
            setLoading(false);
        }
    };

    const handleAddToCart = async (product, quantity) => {
        if (!selectedCustomer) {
            alert("Please select a customer first");
            return;
        }

        let activeCart = cart;
        if (!activeCart) {
            activeCart = await initializeCart();
            if (!activeCart) return;
        }

        try {
            setLoading(true);
            await proxyPost("/api/cartEntry/add", {
                cartId: activeCart.identifier,
                product: product.name,
                quantity,
            });
            await refreshCart(activeCart.identifier);
        } catch (error) {
    console.error("ADD TO CART FAILED");

    console.error("Status:", error?.response?.status);

    console.error("Response:", error?.response?.data);

    console.error(error);
} finally {
            setLoading(false);
        }
    };

    const handleRemoveItem = async (entry) => {
        try {
            setLoading(true);
            await proxyPost("/api/cartEntry/delete", entry);
            if (cart?.identifier) {
                await refreshCart(cart.identifier);
            }
        } catch (error) {
            console.error(error);
            throw error;
        } finally {
            setLoading(false);
        }
    };

    const handleCheckout = async () => {
        if (!cart || cart.cartEntryDtoList.length === 0) return;

        try {
            setLoading(true);
            await proxyPost("/api/order/create", {
                cartId: cart.identifier,
                customerId: selectedCustomer.identifier || selectedCustomer.id,
            });

            setCarts((prevCarts) => {
                const updated = { ...prevCarts };
                delete updated[selectedCustomer.identifier];
                return updated;
            });
            setSelectedCustomer(null);
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const handleOrderComplete = (customerIdentifier, fullOrderData) => {
        const currentCartEntries = carts[customerIdentifier]?.cartEntryDtoList || [];

        const orderWithItems = {
            ...fullOrderData,
            cartEntryDtoList: currentCartEntries
        };

        setCarts((prevCarts) => {
            const updated = { ...prevCarts };
            delete updated[customerIdentifier];
            return updated;
        });
        setSelectedCustomer(null);
        setCompletedOrder(orderWithItems);
    };

    const handleClearCart = async () => {
        if (!cart) {
            setSelectedCustomer(null);
            return;
        }

        try {
            setLoading(true);
            const entriesResponse = await proxyGet(
                `/api/cartEntry/listByCartId?cartId=${encodeURIComponent(cart.identifier)}`
            );
            const entries = unwrapEntries(entriesResponse);

            for (const entry of entries) {
                try {
                    await proxyPost("/api/cartEntry/delete", entry);
                } catch (err) {
                    console.error(err);
                }
            }

            await proxyPost("/api/cart/delete", { identifier: cart.identifier });

            setCarts((prevCarts) => {
                const updated = { ...prevCarts };
                delete updated[selectedCustomer.identifier];
                return updated;
            });
            setSelectedCustomer(null);
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="h-[calc(100vh-40px)] bg-[#f8f6f3] p-5 overflow-hidden">

            <div className="grid h-full grid-cols-1 lg:grid-cols-2 gap-5">

                <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden flex flex-col">

                    <div className="px-6 py-5 border-b border-slate-200 bg-white">
                        <div className="flex justify-between items-start">
                            <div>
                                <span className="text-[13px] uppercase tracking-[0.2em] font-bold text-black">
                                    Billing Area
                                </span>
                                <h2 className="text-2xl font-bold text-slate-900 mt-1">
                                    Current Sale
                                </h2>
                                <p className="text-sm text-slate-800 mt-1">
                                    {`Customer: ${selectedCustomer ? selectedCustomer.name : "No Customer Selected"}`}
                                </p>
                            </div>

                            <button
                                onClick={handleClearCart}
                                disabled={loading}
                                className="px-4 py-2 rounded-full bg-red-600 hover:bg-red-100 text-white text-sm font-semibold transition disabled:opacity-40"
                            >
                                Clear Session
                            </button>
                        </div>
                    </div>

                    <div className="p-5 border-b border-slate-200 bg-[#faf8f6]">
                        <div className="flex justify-between items-center mb-3">
                            <span className="text-[11px] uppercase tracking-[0.15em] font-bold text-slate-500">
                                Customer Selection
                            </span>

                            <button
                                type="button"
                                onClick={() => setIsCustomerModalOpen(true)}
                                className="rounded-full bg-orange-600 hover:bg-orange-700 text-white px-4 py-2 text-sm font-semibold transition"
                            >
                                + New Customer
                            </button>
                        </div>

                        <div className="space-y-3">
                            <select
                                value={selectedCustomer ? selectedCustomer.identifier : ""}
                                onChange={(e) => {
                                    const found = customers.find((c) => String(c.identifier) === String(e.target.value));
                                    setSelectedCustomer(found || null);
                                }}
                                className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-800 outline-none focus:border-orange-300 focus:ring-2 focus:ring-orange-100"
                            >
                                <option value="">
                                    Choose customer account...
                                </option>

                                {customers.map((customer, index) => (
                                    <option
                                        key={customer.identifier || `cust-${index}`}
                                        value={customer.identifier || ""}
                                    >
                                        {customer.name} [{customer.identifier}]
                                    </option>
                                ))}
                            </select>

                            {!cart && selectedCustomer && (
                                <button
                                    onClick={() => initializeCart(selectedCustomer)}
                                    disabled={loading}
                                    className="w-full rounded-full bg-orange-600 hover:bg-orange-700 text-white py-3 font-semibold transition disabled:opacity-40"
                                >
                                    {loading ? "Activating Session..." : "Initialize Customer Cart"}
                                </button>
                            )}
                        </div>
                    </div>

                    <div className="flex-1 overflow-hidden">
                        <CartSummary
                            cart={cart}
                            onRemoveItem={handleRemoveItem}
                            refreshCart={refreshCart}
                            onCheckout={handleCheckout}
                            loading={loading}
                            selectedCustomer={selectedCustomer}
                            onOrderComplete={handleOrderComplete}
                        />
                    </div>



                </div>

                <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden">
                    <ProductPicker
                        onAddToCart={handleAddToCart}
                        loading={loading}
                    />
                </div>
            </div>

            <AddCustomer
                isOpen={isCustomerModalOpen}
                onClose={() => setIsCustomerModalOpen(false)}
                onCustomerAdded={handleCustomerAdded}
            />

            {completedOrder && (
                <BillingModal
                    order={completedOrder}
                    onClose={() => setCompletedOrder(null)}
                />
            )}
        </div>
    );
}
