"use client";

import { FetchEntity } from "@/apicalls/fetch/FetchEntity";
import { FetchList } from "@/apicalls/fetch/FetchList";
import { useEffect, useState, useRef } from "react";
import dynamic from "next/dynamic";
import { getListContent, getItemLabel, getDisplayValue, filterCustomerOption } from "@/utils/CartHelpers";
import Modal from "@/components/Modal";
import AddCustomerModal from "@/components/AddCustomerModal";
import CartEntries from "@/components/CartEntries";
import ProductList from "@/components/ProductList";
import useCart from "@/hooks/useCart";
import PaymentModal from "./PaymentModal";
import BillModal from "./BillModal";
import { useReactToPrint } from "react-to-print";
import PrintableReceipt from "./PrintableReceipt";
import { useRouter } from "next/navigation";

const Select = dynamic(() => import("react-select"), {
    ssr: false,
});

export default function Cart() {

    const [customers, setCustomers] = useState([]);
    const [products, setProducts] = useState([]);
    const [selectedCustomerId, setSelectedCustomerId] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [showCustomerModal, setShowCustomerModal] = useState(false);
    const [customerForm, setCustomerForm] = useState({
        name: "",
        identifier: "",
        phoneNo: "",
    });
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [showInvoiceModal, setShowInvoiceModal] = useState(false);
    const [paymentType, setPaymentType] = useState("");
    const [completedOrder, setCompletedOrder] = useState(null);
    const [invoiceItems, setInvoiceItems] = useState([]);
    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
    const printRef = useRef(null);
    const router = useRouter();
    const {
        cartEntries,
        selectedCart,
        loadingCart,
        addingProductId,
        handleAddProduct,
        handleUpdateQuantity,
        handleDelete,
        handleClearCart,
    } = useCart(selectedCustomerId, baseUrl);


     const fetchData = async () => {
            try {
                const customerRes = await FetchList(`${baseUrl}/customer/list`, 0, "", 200);
                const productRes = await FetchList(`${baseUrl}/product/cart-list`, 0, "", 200);

                setCustomers(getListContent(customerRes));
                setProducts(getListContent(productRes));

            } catch (error) {
                console.error("Error fetching data:", error);
            }   
            console.log("Customers",customers);         
        };

    useEffect(() => {
        fetchData();
    }, []);

    const customerOptions = customers.map((customer) => ({
        value: customer.identifier,
        label: `${customer.name} (${customer.phoneNo})`,
    }));

    const filteredProducts = products.filter((product) => {

        if ((product.stockQuantity || 0) <= 0) {
            return false;
        }
        const label = getItemLabel(product)?.toLowerCase() || "";
        const category = getDisplayValue(product.category)?.toLowerCase() || "";
        const term = searchTerm.toLowerCase();

        return label.includes(term) || category.includes(term);
    });

    const getProductName = (productIdentifier) => {
        const product = products.find(
            (p) => p.identifier === productIdentifier || p.id === productIdentifier
        );
        return product?.name || productIdentifier;
    };

    const handleAddCustomer = async () => {
        try {
            const response = await FetchEntity(
                `${baseUrl}/customer/add`,
                "POST",
                customerForm,
                "application/json"
            );
            if (!response) {
                return;
            }

            const customerRes = await FetchList(
                `${baseUrl}/customer/list`,
                0,
                200
            );

            const updatedCustomers = getListContent(customerRes);
            setCustomers(updatedCustomers);
            setShowCustomerModal(false);
            setCustomerForm({
                name: "",
                identifier: "",
                phoneNo: "",
            });

            const newCustomerId = response.identifier || response.id;
            setSelectedCustomerId(newCustomerId);

        } catch (error) {
            console.error(error);
        }
    };

    const selectedCustomer = customers.find(
        customer => customer.identifier === selectedCustomerId
    );

    const handlePaymentComplete = async () => {
        try {

            const orderPayload = {
                customerId: selectedCustomerId,
                paymentType: paymentType,
            };

            const orderResponse = await FetchEntity(
                `${baseUrl}/orders/add`,
                "POST",
                orderPayload,
                "application/json"
            );

            if (!orderResponse) {
                return;
            }

            setCompletedOrder(orderResponse);
            setInvoiceItems([...cartEntries]);

            await handleClearCart();
            await fetchData();

            setShowPaymentModal(false);
            setShowInvoiceModal(true);

        } catch (error) {
            console.error(error);
        }
    };

    const isCheckoutDisabled = !selectedCustomerId || cartEntries.length === 0;
    const handlePrint = useReactToPrint({
        contentRef: printRef,
    });
 

    return (
        <>
            <div className="min-h-screen bg-linear-to-br from-slate-50 via-white to-slate-100 p-3 md:p-4">
                <div className="mx-auto max-w-7xl space-y-1">

                    <div className="flex justify-between mb-2">
                        <h1 className="text-xl font-semibold text-slate-900 pl-2">
                            Cart
                        </h1>

                        <div className="flex gap-2">
                            <button
                                onClick={() => router.push("/orders")}
                                className="rounded-2xl border border-violet-200 bg-white px-4 py-1.5 text-[11px] font-bold text-violet-600 hover:bg-violet-200">
                                Orders
                            </button>

                            <button
                                onClick={() => setShowCustomerModal(true)}
                                className="rounded-2xl border border-violet-200 bg-white px-4 py-1.5 text-[11px] font-bold text-violet-600 hover:bg-violet-200"
                            >
                                Add Customer +
                            </button>
                        </div>
                    </div>

                    <div className="grid gap-3 lg:grid-cols-4 min-h-[calc(100vh-7rem)]">
                        <aside className="lg:col-span-3 rounded-3xl border border-slate-300 bg-white p-3 shadow-sm flex flex-col h-[calc(100vh-120px)]">                            <div className="flex flex-col gap-2.5 flex-1 min-h-0">
                            <div>
                                <h2 className="text-sm font-semibold text-slate-900">{selectedCustomer?.name || "Customers"}</h2>
                            </div>
                            <Select
                                options={customerOptions}
                                placeholder="Search and select customer..."
                                value={customerOptions.find(opt => opt.value === selectedCustomerId) || null}
                                onChange={(selectedOption) => {
                                    setSelectedCustomerId(selectedOption?.value || "");
                                }}
                                isSearchable
                                className="text-[11px] border border-slate-400"
                                filterOption={filterCustomerOption}
                                styles={{
                                    option: (base, state) => {
                                        let backgroundColor = "white";
                                        if (state.isSelected) {
                                            backgroundColor = "#8b5cf6";
                                        } else if (state.isFocused) {
                                            backgroundColor = "#ede9fe";
                                        }
                                        return {
                                            ...base,
                                            backgroundColor,
                                            color: state.isSelected ? "white" : "#0f172a",
                                            cursor: "pointer",
                                        };
                                    },
                                }} />
                            <div className="flex-1 overflow-hidden">
                                <CartEntries
                                    cartEntries={cartEntries}
                                    products={products}
                                    loading={loadingCart}
                                    getProductName={getProductName}
                                    getDisplayValue={getDisplayValue}
                                    onDelete={handleDelete}
                                    onUpdateQuantity={handleUpdateQuantity} />
                            </div>
                        </div>
                            <div className="mt-2.5 shrink-0">
                                <div className="mt-2.5 shrink-0 rounded-2xl border border-violet-300 bg-violet-50 p-2.5 text-[11px]">
                                    <div className="bg-violet-50 border-blue-200">
                                        <h3 className="mb-2 text-[11px] font-semibold uppercase tracking-wide text-slate-500">
                                            Summary
                                        </h3>
                                    </div>

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

                                    <div className="mt-1.5 flex justify-between border-t pt-1.5 text-[12px] font-semibold text-slate-900">
                                        <span>Total Price</span>
                                        <span>
                                            ₹{selectedCart?.totalPrice ?? 0}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </aside>
                        <div className="lg:col-span-1 flex flex-col h-full overflow-hidden">
                            <ProductList
                                products={filteredProducts}
                                cartEntries={cartEntries}
                                searchTerm={searchTerm}
                                setSearchTerm={setSearchTerm}
                                selectedCustomerId={selectedCustomerId}
                                addingProductId={addingProductId}
                                onAddProduct={handleAddProduct} />

                            <div className="mt-2.5 flex h-10 gap-2.5 shrink-0">
                                <button disabled={isCheckoutDisabled} onClick={() => setShowPaymentModal(true)} className="flex-1 rounded-xl bg-violet-500 font-bold disabled:bg-gray-200 disabled:text-black py-1.5 text-[11px] text-white transition hover:bg-violet-700">
                                    Checkout
                                </button>

                                <button onClick={handleClearCart} className="flex-1 rounded-xl bg-gray-200 font-bold py-1.5 text-[11px] text-slate-800 transition hover:bg-gray-300">
                                    Clear Cart
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <Modal
                open={showCustomerModal}
                onClose={() => setShowCustomerModal(false)}
                title="Add Customer">
                <AddCustomerModal
                    onClose={() => setShowCustomerModal(false)}
                    customerForm={customerForm}
                    setCustomerForm={setCustomerForm}
                    onSubmit={handleAddCustomer} />
            </Modal>
            <Modal
                open={showPaymentModal}
                onClose={() => setShowPaymentModal(false)}
                title="Payment">
                <PaymentModal
                    selectedCart={selectedCart}
                    paymentType={paymentType}
                    setPaymentType={setPaymentType}
                    onCompletePayment={handlePaymentComplete} />
            </Modal>
            <div className="hidden">
                <PrintableReceipt
                    ref={printRef}
                    order={completedOrder}
                    cartEntries={invoiceItems}
                    selectedCustomer={selectedCustomer}
                    paymentType={paymentType}
                    getProductName={getProductName}
                />
            </div>
            <Modal
                open={showInvoiceModal}
                onClose={() => setShowInvoiceModal(false)}
                title="Order Summary">
                <BillModal
                    order={completedOrder}
                    cartEntries={invoiceItems}
                    paymentType={paymentType}
                    selectedCustomer={selectedCustomer}
                    getProductName={getProductName}
                    onClose={() => setShowInvoiceModal(false)}
                    onPrint={handlePrint}
                />
            </Modal>
        </>
    );
}