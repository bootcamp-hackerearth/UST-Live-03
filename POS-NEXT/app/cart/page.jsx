"use client";

import { FetchEntity } from "@/apicalls/fetch/FetchEntity";
import { FetchList } from "@/apicalls/fetch/FetchList";
import { useEffect, useState } from "react";
import dynamic from "next/dynamic";
const Select = dynamic(() => import("react-select"), {
  ssr: false,
});
 
export default function Cart() {

    const [customers, setCustomers] = useState([]);
    const [products, setProducts] = useState([]);
    const [prices, setPrices] = useState([]);
    const [cartEntries, setCartEntries] = useState([]);
    const [selectedCustomerId, setSelectedCustomerId] = useState("");
    const [selectedCart, setSelectedCart] = useState(null);
    const [message, setMessage] = useState("");
    const [loadingCart, setLoadingCart] = useState(false);
    const [addingProductId, setAddingProductId] = useState("");

    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";

    const getListContent = (response) => {
        if (Array.isArray(response)) {
            return response;
        }

        return response?.content || [];
    };

    const getProductIdentifier = (item) => item?.identifier || item?.id || "";

    const priceSummaryByProduct = prices.reduce((summary, priceItem) => {
        const productKey = priceItem?.product;
        if (!productKey) {
            return summary;
        }

        if (!summary[productKey]) {
            summary[productKey] = {};
        }

        summary[productKey][priceItem.priceType] = priceItem.priceAmount;
        return summary;
    }, {});

    const getPriceInfo = (productIdentifier) => {
        const productPrices = priceSummaryByProduct[productIdentifier] || {};

        const mrp = productPrices["MRP"] ?? null;
        const sellingPrice = productPrices["Selling Price"] ?? null;

        return {
            mrp,
            sellingPrice,
            hasPricing: mrp !== null && sellingPrice !== null,
        };
    };
    const getActiveCartIdentifier = () => selectedCart?.identifier || selectedCustomerId;

    const getItemLabel = (item) => {
        if (!item) return "-";

        return item.name || item.title || item.identifier || item.username || item.phoneNo || item.id || "-";
    };

    const getDisplayValue = (value) => {
        if (value === null || value === undefined || value === "") {
            return "-";
        }

        if (Array.isArray(value)) {
            return value.length ? value.join(", ") : "-";
        }

        if (typeof value === "object") {
            return value.name || value.title || value.identifier || value.id || JSON.stringify(value);
        }

        return value;
    };


    useEffect(() => {
        const fetchData = async () => {
            try {
                const customerRes = await FetchList(`${baseUrl}/customer/list`, 0, 200);
                const productRes = await FetchList(`${baseUrl}/product/list`, 0, 200);
                const priceRes = await FetchList(`${baseUrl}/price/list`, 0, 200);

                setCustomers(getListContent(customerRes));
                setProducts(getListContent(productRes));
                setPrices(getListContent(priceRes));

            } catch (error) {
                console.error("Error fetching data:", error);
            }
        };

        fetchData();
    }, []);

    useEffect(() => {
        if (!selectedCustomerId) return;

        const fetchCartEntries = async () => {
            setLoadingCart(true);
            setMessage("");

            try {
                const res = await FetchEntity(
                    `/api/cart/get`,
                    selectedCustomerId,
                    "text/plain"
                );

                setSelectedCart(res || null);
                const cartIdentifier = res?.identifier || selectedCustomerId;
                const cartEntryRes = await FetchList(`${baseUrl}/cartEntry/list`, 0, 200);
                const cartEntryItems = getListContent(cartEntryRes);

                setCartEntries(
                    cartEntryItems.filter((entry) => entry?.cartId === cartIdentifier)
                );

            } catch (err) {
                console.log(err);
                setSelectedCart(null);
                setCartEntries([]);
            } finally {
                setLoadingCart(false);
            }
        };

        fetchCartEntries();
    }, [selectedCustomerId]);

    const refreshCartEntries = async (cartIdentifier) => {
        const activeCartIdentifier = cartIdentifier || getActiveCartIdentifier();
        if (!activeCartIdentifier) {
            setCartEntries([]);
            return;
        }

        const cartEntryRes = await FetchList(`${baseUrl}/cartEntry/list`, 0, 200);
        const cartEntryItems = getListContent(cartEntryRes);

        setCartEntries(
            cartEntryItems.filter((entry) => entry?.cartId === activeCartIdentifier)
        );
    };

    const handleAddProduct = async (product) => {
        const cartId = selectedCart?.identifier || selectedCart?.id || selectedCustomerId;
        if (!cartId) {
            setMessage("Select a customer first.");
            return;
        }

        const productIdentifier = product?.identifier || product?.id;
        if (!productIdentifier) {
            setMessage("Selected product is missing an identifier.");
            return;
        }

        const priceInfo = getPriceInfo(productIdentifier);

        if (!priceInfo.hasPricing) {
            setMessage(`Missing pricing for ${getItemLabel(product)}.`);
            return;
        }

        setAddingProductId(productIdentifier);
        setMessage("");

        const unitPrice = Number(priceInfo.sellingPrice ?? 0);

        const payload = {
            cartId,
            product: productIdentifier,
            quantity: 1,
            discount: 0,
            orginalPrice: unitPrice,
            totalPrice: unitPrice,
            customerIdentifier: selectedCustomerId,
        };
        try {
            const response = await FetchEntity(`/api/cartEntry/add`, payload, "application/json");

            if (response) {
                const updatedCart = await FetchEntity(
                    `/api/cart/get`,
                    selectedCustomerId,
                    "text/plain"
                );

                setSelectedCart(updatedCart || null);
                await refreshCartEntries(updatedCart?.identifier || cartId);
                setMessage("Product added to cart.");
            } else {
                setMessage("Unable to add product to cart.");
            }
        } catch (error) {
            console.error("Error adding product:", error);
            setMessage("Unable to add product to cart.");
        } finally {
            setAddingProductId("");
        }
    };

    const handleCustomerSelect = (identifier) => {
        setSelectedCustomerId(identifier);
    };

    const customerOptions = customers.map((customer) => ({
        value: customer.identifier,
        label: `${customer.name} (${customer.phoneNo})`,
    }));


    const filterCustomerOption = (option, inputValue) => {
        const search = inputValue.toLowerCase();

        return (
            option.label.toLowerCase().includes(search) ||
            option.value.toLowerCase().includes(search)
        );
    };

    const handleUpdateQuantity = async (entry, delta) => {
    try {
        if (delta === 1) {
            const payload = {
                cartId: entry.cartId,
                product: entry.product,
                quantity: 1,
                customerIdentifier: selectedCustomerId,
            };

            await FetchEntity(`${baseUrl}/cartEntry/add`, payload, "application/json");

        } else if (delta === -1) {
            const newQty = Number(entry.quantity) - 1;

            if (newQty < 1) return;

            const payload = {
                ...entry,
                quantity: newQty,
            };

            await FetchEntity(`${baseUrl}/cartEntry/updatequantity`, payload, "application/json");
        }

        const updatedCart = await FetchEntity(
            `${baseUrl}/cart/get`,
            selectedCustomerId,
            "text/plain"
        );

        setSelectedCart(updatedCart || null);
        await refreshCartEntries(updatedCart?.identifier);

    } catch (err) {
        console.error("Error updating quantity", err);
    }
};

    return (
        <div className="min-h-screen bg-linear-to-br from-slate-50 via-white to-slate-100 p-6 md:p-8">
            <div className="mx-auto max-w-7xl space-y-6">

                <div className="flex flex-col gap-2">
                    <h1 className="text-3xl font-semibold text-slate-900">Cart</h1>
                </div>

                <div className="grid gap-6 lg:grid-cols-4">

                    <aside className="lg:col-span-3 rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
                        <div className="space-y-4">

                            <div>
                                <h2 className="text-lg font-semibold text-slate-900">Customers</h2>
                                <p className="text-sm text-slate-500">
                                    Search and select a customer to load their cart.
                                </p>
                            </div>
                            <Select
                                options={customerOptions}
                                placeholder="Search and select customer..."
                                value={customerOptions.find(opt => opt.value === selectedCustomerId) || null}
                                onChange={(selectedOption) => {
                                    handleCustomerSelect(selectedOption?.value || "");
                                }}
                                isSearchable
                                className="text-sm"
                                filterOption={filterCustomerOption}
                                styles={{
                                    control: (base) => ({
                                        ...base,
                                        borderRadius: "12px",
                                        padding: "2px",
                                        borderColor: "#cbd5f5",
                                    }),
                                    menu: (base) => ({
                                        ...base,
                                        zIndex: 9999,
                                    }),
                                }}
                            />

                            <div className="rounded-2xl bg-slate-50 p-4 text-sm text-slate-600">
                                <div className="font-medium text-slate-900">Selected customer</div>
                                <div>{selectedCustomerId || "None"}</div>
                                {message && <div className="mt-2 text-emerald-700">{message}</div>}
                            </div>

                            <div>
                                <div className="mb-3 flex justify-between">
                                    <h3 className="text-sm font-semibold uppercase text-slate-500">Cart Entries</h3>
                                    {loadingCart && <span className="text-xs text-slate-400">Loading...</span>}
                                </div>

                                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 max-h-[55vh] overflow-y-auto">
                                    {cartEntries.length === 0 ? (
                                        <div className="rounded-2xl border border-dashed border-slate-200 bg-slate-50 p-4 text-sm text-slate-400">
                                            No records found
                                        </div>
                                    ) : (
                                        cartEntries.map((item) => (
                                            <div key={item.identifier ?? item.id} className="rounded-2xl border border-slate-200 p-4 text-sm">
                                                <div className="font-medium text-slate-900">
                                                    {getDisplayValue(item.product)}
                                                </div>

                                                <div className="mt-1 text-slate-500">
                                                    Qty: {getDisplayValue(item.quantity)}
                                                </div>
                                                <div className="mt-2 flex items-center justify-between">

                                                    <button
                                                        onClick={() => handleUpdateQuantity(item, -1)}
                                                        className="w-7 h-7 rounded-full border border-slate-300 text-sm hover:bg-slate-100"
                                                    >
                                                        -
                                                    </button>

                                                    <span className="text-sm font-semibold text-slate-900">
                                                        {getDisplayValue(item.quantity)}
                                                    </span>

                                                    <button
                                                        onClick={() => handleUpdateQuantity(item, +1)}
                                                        className="w-7 h-7 rounded-full border border-slate-300 text-sm hover:bg-slate-100"
                                                    >
                                                        +
                                                    </button>

                                                </div>

                                                <div className="mt-1 text-slate-500">
                                                    Total: {getDisplayValue(item.totalPrice)}
                                                </div>
                                            </div>
                                        ))
                                    )}
                                </div>
                            </div>

                        </div>

                        <div className="mt-4 rounded-2xl border border-slate-200 bg-slate-50 p-4">
                            <h3 className="text-sm font-semibold uppercase text-slate-500 mb-3">
                                Summary
                            </h3>

                            <div className="space-y-2 text-sm">

                                <div className="flex justify-between text-slate-600">
                                    <span>Original Price</span>
                                    <span className="font-medium text-slate-900">
                                        ₹{selectedCart?.originalPrice ?? 0}
                                    </span>
                                </div>

                                <div className="flex justify-between text-slate-600">
                                    <span>Discount</span>
                                    <span className="font-medium text-red-500">
                                        -₹{selectedCart?.discount ?? 0}
                                    </span>
                                </div>

                                <div className="border-t pt-2 flex justify-between text-base font-semibold text-slate-900">
                                    <span>Total Price</span>
                                    <span>
                                        ₹{selectedCart?.totalPrice ?? 0}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </aside>

                    <section className="lg:col-span-1 rounded-3xl border border-slate-200 bg-white p-5 shadow-sm max-h-[75vh] overflow-y-auto">

                        <div className="mb-4">
                            <h2 className="text-lg font-semibold text-slate-900">Products</h2>
                            <p className="text-sm text-slate-500">
                                Click to add
                            </p>
                        </div>

                        <div className="grid gap-3">

                            {products.length === 0 ? (
                                <div className="rounded-2xl border border-dashed border-slate-200 bg-slate-50 p-4 text-sm text-slate-400">
                                    No products available.
                                </div>
                            ) : (
                                products.map((product) => {
                                    const productIdentifier = getProductIdentifier(product);
                                    const priceInfo = getPriceInfo(productIdentifier);
                                    const isAdding = addingProductId === productIdentifier;

                                    return (
                                        <button
                                            key={productIdentifier}
                                            onClick={() => handleAddProduct(product)}
                                            disabled={!selectedCustomerId || isAdding || !priceInfo.hasPricing}
                                            className="rounded-2xl border border-slate-200 bg-slate-50 p-3 text-left hover:bg-white hover:border-slate-300 transition disabled:opacity-60"
                                        >
                                            <div className="flex justify-between items-start">
                                                <div className="text-sm font-semibold text-slate-900">
                                                    {getItemLabel(product)}
                                                </div>
                                                <span className="text-xs bg-slate-900 text-white px-2 py-1 rounded-full">
                                                    {isAdding ? "..." : "+"}
                                                </span>
                                            </div>

                                            <div className="text-xs text-slate-500 mt-1">
                                                {getDisplayValue(product.category)}
                                            </div>

                                            <div className="text-xs mt-2 text-slate-700">
                                                {priceInfo.hasPricing
                                                    ? `₹${priceInfo.sellingPrice}`
                                                    : "No price"}
                                            </div>
                                        </button>
                                    );
                                })
                            )}
                        </div>
                    </section>
                </div>
            </div>
        </div>
    );
}