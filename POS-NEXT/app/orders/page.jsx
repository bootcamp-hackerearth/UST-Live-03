"use client"

import { FetchList } from "@/apicalls/fetch/FetchList";
import Modal from "@/components/Modal";
import { getListContent } from "@/utils/CartHelpers";
import { useEffect, useRef, useState } from "react"
import BillModal from "../cart/BillModal";
import { FetchEntity } from "@/apicalls/fetch/FetchEntity";
import { useReactToPrint } from "react-to-print";
import PrintableReceipt from "../cart/PrintableReceipt";
import { Search } from "lucide-react";

export default function Orders() {

    const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
    const [orders, setOrders] = useState([]);
    const [showReceiptModal, setShowReceiptModal] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [orderEntries, setOrderEntries] = useState([]);
    const [products, setProducts] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const printRef = useRef(null);


    useEffect(() => {

        const fetchData = async () => {
            const orderRes = await FetchList(
                `${baseUrl}/orders/list`,
                0,
                "",
                200
            );
            const productRes = await FetchList(
                `${baseUrl}/product/list`,
                0,
                "",
                200
            );
            setOrders(getListContent(orderRes.content));
            setProducts(getListContent(productRes));
        };
        fetchData();
    }, []);

    const getProductName = (productIdentifier) => {

        const product = products.find(
            (p) =>
                p.identifier === productIdentifier ||
                p.id === productIdentifier
        );
        return product?.name || productIdentifier;
    };

    const paymentStyles = {
        CASH: "bg-green-100 text-green-700",
        CARD: "bg-blue-100 text-blue-700",
        UPI: "bg-violet-100 text-violet-700",
    };

    const handleOpenOrder = async (order) => {

        try {
            setSelectedOrder(order);

            const entries = await FetchEntity(
                `${baseUrl}/orderentry/get`,
                "POST",
                order.identifier,
                "text/plain"
            );
            setOrderEntries(entries);
            setShowReceiptModal(true);
        } catch (err) {
            console.error(err);
        }
    };

    const filteredOrders = orders.filter((order) => {
        const search = searchTerm.toLowerCase();

        return (
            order.identifier?.toLowerCase().includes(search) ||
            order.customerId?.toLowerCase().includes(search) ||
            order.paymentType?.toLowerCase().includes(search)
        );
    });

    const handlePrint = useReactToPrint({
        contentRef: printRef,
    });

    return (

        <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
            <div className="flex items-center justify-between border-b border-slate-200 bg-white px-4 py-3">

                <div>
                    <h2 className="text-sm font-semibold text-slate-800">
                        Orders
                    </h2>
                    <p className="text-[11px] text-slate-500">
                        {filteredOrders.length} records
                    </p>
                </div>

                <div className="relative">
                    <Search
                        size={14}
                        className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"/>

                    <input
                        type="text"
                        placeholder="Search Order ID, Customer, Payment..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="h-8 w-72 rounded-xl border border-slate-200 bg-slate-50 pl-9 pr-3 text-[11px] outline-none focus:border-violet-400 focus:bg-white"/>
                </div>
            </div>
            <table className="w-full p-2">
                <thead className="bg-violet-50 border-b border-violet-100">
                    <tr>
                        <th className="px-3 py-2 text-left text-[11px] font-semibold uppercase tracking-wide text-violet-700">Order</th>
                        <th className="px-3 py-2 text-left text-[11px] font-semibold uppercase tracking-wide text-violet-700">Customer</th>
                        <th className="px-3 py-2 text-left text-[11px] font-semibold uppercase tracking-wide text-violet-700">Payment</th>
                        <th className="px-3 py-2 text-left text-[11px] font-semibold uppercase tracking-wide text-violet-700">Amount</th>
                        <th className="px-3 py-2 text-left text-[11px] font-semibold uppercase tracking-wide text-violet-700">Discount</th>
                        <th className="px-3 py-2 text-right"></th>
                    </tr>
                </thead>

                <tbody>
                    {filteredOrders.map((order) => (
                        <tr
                            key={order.identifier}
                            onClick={() => handleOpenOrder(order)}
                            className="border-b border-slate-100 hover:bg-violet-50 hover:border-violet-200 cursor-pointer transition-all">
                            <td className="px-3 py-3">
                                <div>
                                    <div className="font-semibold text-slate-900 text-xs">
                                        {order.identifier}
                                    </div>

                                    <div className="text-[10px] text-slate-400">
                                        {new Date(order.orderDate).toLocaleDateString()}
                                    </div>
                                </div>
                            </td>

                            <td className="px-3 py-3">
                                <div className="text-xs font-medium text-slate-700">
                                    {order.customerId}
                                </div>
                            </td>

                            <td className="px-3 py-3">
                                <span
                                    className={`px-2 py-1 rounded-md text-[10px] font-semibold ${paymentStyles[order.paymentType] ||
                                        "bg-slate-100 text-slate-700"
                                        }`}>
                                    {order.paymentType}
                                </span>
                            </td>

                            <td className="px-3 py-3">
                                <div className="flex flex-col">
                                    <span className="text-[10px] text-slate-400 line-through">
                                        ₹{order.originalPrice}
                                    </span>

                                    <span className="text-sm font-bold text-slate-900">
                                        ₹{order.totalPrice}
                                    </span>
                                </div>
                            </td>

                            <td className="px-3 py-3">
                                {order.discount > 0 ? (
                                    <span className="text-[11px] font-medium text-green-600">
                                        Saved ₹{order.discount}
                                    </span>
                                ) : (
                                    <span className="text-[11px] text-slate-400">—</span>
                                )}
                            </td>

                            <td className="px-5 py-3 text-right">
                                <span className="text-[10px] text-blue-600 font-medium">
                                    View →
                                </span>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <Modal
                open={showReceiptModal}
                onClose={() => setShowReceiptModal(false)}
                title="Receipt">
                <BillModal
                    order={selectedOrder}
                    cartEntries={orderEntries}
                    paymentType={selectedOrder?.paymentType}
                    selectedCustomer={{ name: selectedOrder?.customerId }}
                    getProductName={getProductName}
                    onClose={() => setShowReceiptModal(false)}
                    onPrint={handlePrint} />
            </Modal>
            <div className="hidden">
                <PrintableReceipt
                    ref={printRef}
                    order={selectedOrder}
                    cartEntries={orderEntries}
                    selectedCustomer={{ name: selectedOrder?.customerId }} 
                    paymentType={selectedOrder?.paymentType}
                    getProductName={getProductName} />
            </div>
        </div>
    )
}