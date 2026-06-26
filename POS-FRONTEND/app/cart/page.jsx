"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Plus, Trash2, RefreshCw, ShoppingCart, X } from "lucide-react";
import api from "@/app/services/api";
import Layout from "@/components/Layout";
import SearchableDropdown from "@/components/SearchableDropdown";
import SearchSelector from "@/components/SearchSelector";
import AddCustomer from "../customer/add/page";

const currency = (val) =>
    `₹${Number(val || 0).toLocaleString("en-IN", {
        minimumFractionDigits: 2,
    })}`;

export default function CartPage() {
    const router = useRouter();

    const [customers, setCustomers] = useState([]);
    const [products, setProducts] = useState([]);
    const [customer, setCustomer] = useState("");
    const [selectedProduct, setSelectedProduct] = useState("");
    const [cartData, setCartData] = useState(null);
    const [entries, setEntries] = useState([]);

  
    const [checkingOut, setCheckingOut] = useState(false);
    const [toast, setToast] = useState("");
    const [showAddCustomer, setShowAddCustomer] = useState(false);

    const [showPayment, setShowPayment] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState("CASH");
    const [receivedAmount, setReceivedAmount] = useState("");

    const today = new Date().toLocaleDateString("en-IN");

    const getList = (res) => {
        const data = res?.data;
        if (Array.isArray(data)) return data;
        if (Array.isArray(data?.dtoList)) return data.dtoList;
        return [];
    };

    useEffect(() => {
        api.post("/product/list", { page: 0, sizePerPage: 500 })
            .then(res => setProducts(getList(res)))
            .catch(console.error);

        api.post("/customer/list", { page: 0, sizePerPage: 500 })
            .then(res => setCustomers(getList(res)))
            .catch(console.error);
    }, []);

    const fetchCart = async (customerId) => {
        if (!customerId) {
            setCartData(null);
            setEntries([]);
            return;
        }

    
        try {
            const cartRes = await api.get(`/cart/get?identifier=${customerId}`);
            setCartData(cartRes.data || null);

            const entryRes = await api.post("/cartEntry/list", {
                page: 0,
                sizePerPage: 500,
            });

            const allEntries = getList(entryRes);
            setEntries(allEntries.filter(e => e.cartId === customerId));

        } catch (err) {
            console.error(err);
            showToast("Failed to load cart");
        } 
    };

    useEffect(() => {
        fetchCart(customer);
    }, [customer]);

    const handleAdd = async () => {
        if (!customer || !selectedProduct) return;

        try {
            await api.post("/cartEntry/add", {
                productId: selectedProduct,
                cartId: customer,
                quantity: 1,
            });

            setSelectedProduct("");
            fetchCart(customer);
            showToast("Added");
        } catch {
            showToast("Add failed");
        } 
    };

    const updateQty = async (index, newQty) => {
        if (newQty < 1) return;

        const entry = entries[index];

        const updated = [...entries];
        updated[index] = {
            ...updated[index],
            quantity: newQty,
            totalPrice: Number(updated[index].sellingPrice || 0) * newQty,
        };

        setEntries(updated);

        try {
            await api.put("/cartEntry/update", {
                identifier: entry.identifier,
                quantity: newQty,
            });

            const res = await api.post(`/cart/recalculate?identifier=${customer}`);
            setCartData(res.data || null);

        } catch {
            showToast("Update failed");
        }
    };

    const handleRemove = async (index) => {
        try {

            await api.delete("/cartEntry/delete", {
                data: { identifier: entries[index].identifier },
            })


            fetchCart(customer);
            showToast("Removed");
        } catch {
            showToast("Delete failed");
        }
    };


    const handleClearCart = async () => {
        if (!customer) return;

        const confirmClear = globalThis.confirm("Clear entire cart?");
        if (!confirmClear) return;

        try {
            await api.delete("/cart/delete", { data: { identifier: customer }, });

            setEntries([]);
            setCartData(null);

            showToast("Cart cleared");
        } catch {
            showToast("Clear failed");
        }
    };

    const showToast = (msg) => {
        setToast(msg);
        setTimeout(() => setToast(""), 2000);
    };

    const subtotal = Number(cartData?.originalPrice || 0);
    const discountValue = Number(cartData?.discount || 0);
    const finalTotal = Number(cartData?.totalPrice ?? Math.max(subtotal - discountValue, 0));

    const totalProducts = entries.length;

    const totalQuantity = entries.reduce(
        (sum, item) => sum + Number(item.quantity || 0),
        0
    );

    const totalSavings = entries.reduce(
        (sum, item) =>
            sum +
            ((Number(item.mrp || 0) - Number(item.sellingPrice || 0))
                * Number(item.quantity || 0)),
        0
    );

    const openPaymentModal = () => {
        if (!customer) {
            showToast("Select a customer first");
            return;
        }
        if (entries.length === 0) {
            showToast("Cart is empty");
            return;
        }
        setPaymentMethod("CASH");
        setReceivedAmount(finalTotal.toFixed(2));
        setShowPayment(true);
    };

    const handleCheckout = async () => {
        const received = Number(receivedAmount || 0);

        if (paymentMethod === "CASH" && received < finalTotal) {
            showToast("Received amount is less than total");
            return;
        }

        const finalReceived = paymentMethod === "CASH" ? received : finalTotal;
        const changeAmount = paymentMethod === "CASH" ? finalReceived - finalTotal : 0;

        const entryList = entries.map((e) => ({
            productIdentifier: e.productId,
            mrp: e.mrp,
            unitDiscount: Number(e.mrp || 0) - Number(e.sellingPrice || 0),
            unitPrice: e.sellingPrice,
            quantity: e.quantity,
            totalPrice: e.totalPrice,
        }));

        const orderDto = {
            customerIdentifier: customer,
            originalPrice: subtotal,
            discount: discountValue,
            totalPrice: finalTotal,
            entryList,
            paymentMethod,
            receivedAmount: finalReceived,
            changeAmount,
        };

        setCheckingOut(true);
        try {
            const res = await api.post("/order/checkout", orderDto);
            const order = res?.data;

            if (!order?.identifier) {
                showToast("Checkout failed");
                return;
            }

            try {
                await api.delete("/cart/delete",{data: { identifier: customer },});
            } catch {
            }

            setShowPayment(false);
            showToast("Order placed");
            router.push(`/order?invoice=${order.identifier}`);
        } catch (err) {
            console.error(err);
            showToast("Checkout failed");
        } finally {
            setCheckingOut(false);
        }
    };

    return (
        <Layout>
            <div className="max-w-6xl mx-auto mt-6 px-4">
                <div className="bg-white shadow-xl rounded-2xl p-6 border-t-4 border-cyan-500">

                    <div className="flex items-center gap-3 mb-6">
                        <ShoppingCart className="text-cyan-600" />
                        <h2 className="text-2xl font-semibold text-slate-800">Cart Management</h2>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 items-start">
                        <div>
                            <label 
                                htmlFor="customer"
                                className="block text-sm font-medium text-slate-600 mb-1.5">
                                Customer
                            </label>
                            <div className="flex items-center gap-2">
                                <div className="flex-1">
                                    <SearchSelector
                                        value={customer}
                                        options={customers}
                                        onChange={setCustomer}
                                        placeholder="Search customer..."
                                        displayField="name"
                                        searchFields={["name", "phoneNo"]}
                                    />
                                </div>
                                <button
                                    type="button"
                                    onClick={() => setShowAddCustomer(true)}
                                    className="h-10 w-10 shrink-0 rounded-lg bg-cyan-500 text-white flex items-center justify-center hover:bg-cyan-600 transition"
                                >
                                    <Plus size={18} />
                                </button>
                            </div>
                        </div>

                        <div>
                            <label 
                                htmlFor="customerid"
                                className="block text-sm font-medium text-slate-600 mb-1.5">
                                Customer ID
                            </label>
                            <div className="h-10 px-4 rounded-lg border border-slate-300 bg-slate-50 flex items-center text-sm text-slate-700">
                                {customer || "-"}
                            </div>
                        </div>

                        <div>
                            <label 
                                htmlFor="date"
                                className="block text-sm font-medium text-slate-600 mb-1.5">
                                Date
                            </label>
                            <div className="h-10 px-4 rounded-lg border border-slate-300 bg-slate-50 flex items-center text-sm text-slate-700">
                                {today}
                            </div>
                        </div>
                    </div>

                    {customer && (
                        <div className="flex gap-3 mt-5 items-end">
                            <div className="flex-1">
                                <SearchableDropdown
                                    label="Product"
                                    name="product"
                                    value={selectedProduct}
                                    options={products}
                                    onChange={(e) => setSelectedProduct(e.target.value)}
                                    placeholder="Select Product"
                                />
                            </div>
                            <button
                                onClick={handleAdd}
                                className="h-10 w-10 shrink-0 rounded-lg bg-cyan-500 text-white flex items-center justify-center hover:bg-cyan-600 transition"
                            >
                                <Plus size={18} />
                            </button>
                        </div>
                    )}

                    <div className="overflow-hidden rounded-xl border border-slate-200 mt-6">
                        <table className="w-full text-sm">
                            <thead>
                                <tr className="bg-cyan-500 text-white">
                                    {["Product", "Code", "MRP", "Selling", "Qty", "Total", "Action"].map(
                                        (h) => (
                                            <th key={h} className="p-3 text-left font-semibold">{h}</th>
                                        )
                                    )}
                                </tr>
                            </thead>
                            <tbody>
                                {entries.length === 0 ? (
                                    <tr>
                                        <td colSpan={7} className="text-center p-6 text-slate-500">
                                            No items
                                        </td>
                                    </tr>
                                ) : (
                                    entries.map((e, i) => (
                                        <tr key={e.identifier} className="border-b border-slate-200">
                                            <td className="p-3 text-slate-700">{e.productId}</td>
                                            <td className="p-3 text-slate-700">{e.identifier}</td>
                                            <td className="p-3 line-through text-slate-400">{currency(e.mrp)}</td>
                                            <td className="p-3 text-slate-700">{currency(e.sellingPrice)}</td>
                                            <td className="p-3">
                                                <div className="flex items-center gap-2">
                                                    <button
                                                        onClick={() => updateQty(i, e.quantity - 1)}
                                                        className="px-2 py-1 border border-slate-300 rounded font-bold text-slate-600 hover:bg-slate-100 disabled:opacity-40"
                                                        disabled={e.quantity <= 1}
                                                    >
                                                        −
                                                    </button>
                                                    <span className="w-10 text-center text-slate-700">
                                                        {e.quantity}
                                                    </span>
                                                    <button
                                                        onClick={() => updateQty(i, e.quantity + 1)}
                                                        className="px-2 py-1 border border-slate-300 rounded font-bold text-slate-600 hover:bg-slate-100"
                                                    >
                                                        +
                                                    </button>
                                                </div>
                                            </td>
                                            <td className="p-3 font-semibold text-slate-800">
                                                {currency(e.totalPrice)}
                                            </td>
                                            <td className="p-3">
                                                <button onClick={() => handleRemove(i)}>
                                                    <Trash2 size={16} className="text-red-500" />
                                                </button>
                                            </td>
                                        </tr>
                                    ))
                                )}
                            </tbody>
                        </table>
                    </div>

                    <div className="grid grid-cols-1 lg:grid-cols-12 gap-4 mt-6">
                        <div className="lg:col-span-8 border border-slate-200 rounded-xl p-5 bg-white shadow-sm">
                            <div className="flex items-center justify-between mb-4">
                                <h3 className="text-sm font-semibold text-slate-800">
                                    Cart Insights
                                </h3>
                                <span className="text-xs text-slate-400">
                                    Customer: {customer || "-"}
                                </span>
                                <button
                                    onClick={handleClearCart}
                                    className="px-4 py-2 text-sm border border-red-300 text-red-500 rounded-lg hover:bg-red-50 self-start"
                                >
                                    Clear Cart
                                </button>
                            </div>

                            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                                <div className="rounded-xl border border-cyan-100 bg-cyan-50 p-4">
                                    <p className="text-xs text-slate-500 mb-1">Products</p>
                                    <p className="text-3xl font-bold text-cyan-700">{totalProducts}</p>
                                </div>
                                <div className="rounded-xl border border-blue-100 bg-blue-50 p-4">
                                    <p className="text-xs text-slate-500 mb-1">Quantity</p>
                                    <p className="text-3xl font-bold text-blue-700">{totalQuantity}</p>
                                </div>
                                <div className="rounded-xl border border-green-100 bg-green-50 p-4">
                                    <p className="text-xs text-slate-500 mb-1">Savings</p>
                                    <p className="text-3xl font-bold text-green-700">{currency(totalSavings)}</p>
                                </div>
                            </div>
                        </div>

                        <div className="lg:col-span-4 border border-slate-200 rounded-xl p-4 bg-slate-50 shadow-sm">
                            <p className="text-sm font-semibold text-slate-800 mb-3">Order Summary</p>
                            <div className="space-y-1.5 mb-3 max-h-32 overflow-y-auto pr-1">
                                {entries.length === 0 ? (
                                    <p className="text-xs text-slate-400">No items</p>
                                ) : (
                                    entries.map((e) => (
                                        <div key={e.identifier} className="flex justify-between text-xs text-slate-600">
                                            <span className="truncate pr-2">{e.productId} × {e.quantity}</span>
                                            <span className="shrink-0 text-slate-700">{currency(e.totalPrice)}</span>
                                        </div>
                                    ))
                                )}
                            </div>

                            <div className="border-t border-slate-200 pt-3 space-y-2">
                                <div className="flex justify-between text-sm text-slate-600">
                                    <span>Subtotal</span>
                                    <span>{currency(subtotal)}</span>
                                </div>
                                <div className="flex justify-between items-center text-sm">
                                    <span className="text-slate-600">Discount</span>
                                    <span className="text-slate-700">{currency(discountValue)}</span>
                                </div>
                            </div>

                            <div className="border-t border-slate-200 mt-3 pt-3 flex justify-between text-slate-800">
                                <span className="font-semibold">Total</span>
                                <span className="font-semibold">{currency(finalTotal)}</span>
                            </div>

                            <div className="flex gap-2 mt-4">
                                {/* <button
                                    onClick={handleRecalculate}
                                    className="flex-1 bg-slate-200 text-slate-700 py-2 rounded-lg flex justify-center items-center gap-2 hover:bg-slate-300 transition font-medium text-sm"
                                >
                                    <RefreshCw size={16} className={recalculating ? "animate-spin" : ""} />
                                    Recalculate
                                </button> */}
                                <button
                                    onClick={openPaymentModal}
                                    disabled={entries.length === 0 || !customer}
                                    className="flex-1 bg-cyan-500 text-white py-2 rounded-lg flex justify-center items-center gap-2 hover:bg-cyan-600 transition font-medium text-sm disabled:opacity-40 disabled:cursor-not-allowed"
                                >
                                    Save Cart
                                </button>
                            </div>
                        </div>
                    </div>

                </div>
            </div>

            {toast && (
                <div className="fixed bottom-6 left-1/2 -translate-x-1/2 bg-white border border-slate-200 px-4 py-2 rounded-lg shadow-lg text-sm text-slate-700">
                    {toast}
                </div>
            )}

            {showAddCustomer && (
                <div className="fixed inset-0 bg-slate-100/60 backdrop-blur-sm flex items-center justify-center z-50 p-4">
                    <div className="w-full max-w-4xl bg-white rounded-2xl border border-[#163D4A] shadow-[0_8px_30px_rgba(22,61,74,0.15)] overflow-hidden">
                        <AddCustomer
                            closeModal={() => setShowAddCustomer(false)}
                            refreshData={async () => {
                                const res = await api.post("/customer/list", {
                                    page: 0,
                                    sizePerPage: 500,
                                });
                                setCustomers(getList(res));
                            }}
                        />
                    </div>
                </div>
            )}

            {showPayment && (
                <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
                    <div className="w-full max-w-md bg-white rounded-2xl shadow-2xl p-6 relative">
                        <button
                            onClick={() => setShowPayment(false)}
                            className="absolute top-4 right-4 text-slate-400 hover:text-slate-600"
                        >
                            <X size={20} />
                        </button>

                        <h3 className="text-lg font-semibold text-slate-800 mb-1">Checkout</h3>
                        <p className="text-sm text-slate-500 mb-5">
                            Total due: <span className="font-semibold text-slate-800">{currency(finalTotal)}</span>
                        </p>

                        <div className="mb-4">
                            <label 
                                htmlFor="paymentmethod"
                                className="block text-sm font-medium text-slate-600 mb-1.5">
                                Payment Method
                            </label>
                            <div className="grid grid-cols-3 gap-2">
                                {["CASH", "CARD", "UPI"].map((method) => (
                                    <button
                                        key={method}
                                        onClick={() => {
                                            setPaymentMethod(method);
                                            if (method !== "CASH") {
                                                setReceivedAmount(finalTotal.toFixed(2));
                                            }
                                        }}
                                        className={`py-2 rounded-lg text-sm font-medium border transition ${paymentMethod === method
                                                ? "bg-cyan-500 text-white border-cyan-500"
                                                : "bg-white text-slate-600 border-slate-300 hover:bg-slate-50"
                                            }`}
                                    >
                                        {method}
                                    </button>
                                ))}
                            </div>
                        </div>

                        {paymentMethod === "CASH" && (
                            <div className="mb-4">
                                <label 
                                    htmlFor="receivedamount"
                                    className="block text-sm font-medium text-slate-600 mb-1.5">
                                    Received Amount
                                </label>
                                <input
                                    type="number"
                                    value={receivedAmount}
                                    onChange={(e) => setReceivedAmount(e.target.value)}
                                    className="w-full h-10 px-3 rounded-lg border border-slate-300 text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-cyan-400"
                                />
                                <p className="text-xs text-slate-500 mt-1.5">
                                    Change due: {currency(Math.max(Number(receivedAmount || 0) - finalTotal, 0))}
                                </p>
                            </div>
                        )}

                        <button
                            onClick={handleCheckout}
                            disabled={checkingOut}
                            className="w-full bg-cyan-500 text-white py-2.5 rounded-lg font-medium hover:bg-cyan-600 transition disabled:opacity-50 flex justify-center items-center gap-2"
                        >
                            <RefreshCw size={16} className={checkingOut ? "animate-spin" : ""} />
                            Confirm & Checkout
                        </button>
                    </div>
                </div>
            )}
        </Layout>
    );
}