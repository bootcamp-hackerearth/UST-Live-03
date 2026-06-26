"use client"
import React, { useEffect, useState } from 'react'
import axios from "@/components/axiosConfig"
import Modal from '@/components/Modal'
import BillModal from '@/components/BillModal'

const PAYMENT_LABELS = {
    CASH: { label: "Cash"},
    UPI: { label: "UPI" },
    CARD: { label: "Card" },
}

const OrdersPage = () => {
    const [orders, setOrders] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [filter, setFilter] = useState("")
    const [selectedOrderId, setSelectedOrderId] = useState(null)

    useEffect(() => {
        fetchOrders()
    }, [])

    const fetchOrders = async () => {
        setLoading(true)
        setError(null)
        try {
            const res = await axios.get("/order/list")
            setOrders(res.data)
        } catch (err) {
            console.error("Failed to fetch orders", err)
            setError("Could not load orders. Please try again.")
        } finally {
            setLoading(false)
        }
    }

    const filteredOrders = orders.filter(order =>
        order.identifier?.toLowerCase().includes(filter.toLowerCase()) ||
        order.customerId?.toLowerCase().includes(filter.toLowerCase()) ||
        order.paymentType?.toLowerCase().includes(filter.toLowerCase())
    )

    const formatDate = (dateStr) => {
        if (!dateStr) return '—'
        return new Date(dateStr).toLocaleDateString('en-IN', {
            day: '2-digit', month: 'short', year: 'numeric'
        })
    }

    const formatTime = (dateStr) => {
        if (!dateStr) return ''
        return new Date(dateStr).toLocaleTimeString('en-IN', {
            hour: '2-digit', minute: '2-digit', hour12: true
        })
    }

    return (
        <div className="flex flex-col h-screen border border-zinc-200 rounded-xl overflow-hidden bg-white">

            <div className="flex items-center justify-between px-5 py-3.5 border-b border-zinc-100">
                <div>
                    <h1 className="text-sm font-semibold text-zinc-900">Orders</h1>
                    <p className="text-[11px] text-zinc-400 mt-0.5">{orders.length} total orders</p>
                </div>
                <button
                    onClick={fetchOrders}
                    className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium border border-zinc-200 rounded-lg bg-zinc-50 hover:bg-zinc-100 cursor-pointer transition-colors text-zinc-600"
                >
                    ↻ Refresh
                </button>
            </div>

            <div className="flex items-center gap-2 px-5 py-2 bg-zinc-50 border-b border-zinc-100">
                <span className="text-zinc-400 text-sm">⌕</span>
                <input
                    className="flex-1 text-xs bg-transparent border-none outline-none placeholder-zinc-400"
                    placeholder="Search by order ID, customer, or payment type…"
                    value={filter}
                    onChange={e => setFilter(e.target.value)}
                />
                {filter && (
                    <button onClick={() => setFilter("")} className="text-zinc-400 hover:text-zinc-600 text-xs">✕</button>
                )}
            </div>

            <div className="flex-1 overflow-y-auto">

                {loading && (
                    <div className="flex flex-col items-center justify-center h-full gap-3">
                        <svg className="animate-spin h-5 w-5 text-zinc-400" viewBox="0 0 24 24" fill="none">
                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z" />
                        </svg>
                        <p className="text-xs text-zinc-400">Loading orders…</p>
                    </div>
                )}

                {!loading && error && (
                    <div className="flex flex-col items-center justify-center h-full gap-3">
                        <p className="text-xs text-red-500">{error}</p>
                        <button
                            onClick={fetchOrders}
                            className="text-xs px-3 py-1.5 border border-zinc-200 rounded-lg hover:bg-zinc-50 cursor-pointer"
                        >
                            Try again
                        </button>
                    </div>
                )}

                {!loading && !error && filteredOrders.length === 0 && (
                    <div className="flex flex-col items-center justify-center h-full gap-2">
                        <div className="text-xs font-medium text-zinc-500">
                            {filter ? "No orders match your search" : "No orders yet"}
                        </div>
                        <p className="text-[10px] text-zinc-400">
                            {filter ? "Try a different search term" : "Orders will appear here once placed"}
                        </p>
                    </div>
                )}

                {!loading && !error && filteredOrders.length > 0 && (
                    <table className="w-full border-collapse">
                        <thead>
                            <tr className="border-b border-zinc-100 sticky top-0 bg-white z-10">
                                <th className="text-left text-[10px] font-semibold text-zinc-400 uppercase tracking-wide px-5 py-2.5">#</th>
                                <th className="text-left text-[10px] font-semibold text-zinc-400 uppercase tracking-wide px-3 py-2.5">Order ID</th>
                                <th className="text-left text-[10px] font-semibold text-zinc-400 uppercase tracking-wide px-3 py-2.5">Customer</th>
                                <th className="text-left text-[10px] font-semibold text-zinc-400 uppercase tracking-wide px-3 py-2.5">Payment</th>
                                <th className="text-left text-[10px] font-semibold text-zinc-400 uppercase tracking-wide px-3 py-2.5">Date</th>
                                <th className="text-right text-[10px] font-semibold text-zinc-400 uppercase tracking-wide px-5 py-2.5">Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredOrders.map((order, i) => {
                                const payment = PAYMENT_LABELS[order.paymentType] || { label: order.paymentType, icon: "💰" }
                                const statusStyle = "bg-zinc-50 text-zinc-500 border-zinc-200"
                                return (
                                    <tr
                                        key={order.identifier}
                                        onClick={() => setSelectedOrderId(order.identifier)}
                                        className="border-b border-zinc-50 hover:bg-zinc-50 cursor-pointer transition-colors group"
                                    >
                                        <td className="px-5 py-3 text-[11px] text-zinc-400 font-mono">{i + 1}</td>
                                        <td className="px-3 py-3">
                                            <span className="text-[12px] font-mono font-medium text-zinc-700 group-hover:text-zinc-900 transition-colors">
                                                #{order.identifier}
                                            </span>
                                        </td>
                                        <td className="px-3 py-3">
                                            <div className="flex items-center gap-2">
                                                <div className="w-6 h-6 rounded-full bg-blue-50 text-blue-700 text-[10px] font-semibold flex items-center justify-center shrink-0">
                                                    {order.customerId?.slice(0, 2).toUpperCase() || '?'}
                                                </div>
                                                <span className="text-[12px] text-zinc-700">{order.customerId || '—'}</span>
                                            </div>
                                        </td>
                                        <td className="px-3 py-3">
                                            <span className={`inline-flex items-center gap-1 text-[11px] px-2 py-0.5 rounded-md border ${statusStyle} font-medium`}>
                                                {payment.icon} {payment.label}
                                            </span>
                                        </td>
                                        <td className="px-3 py-3">
                                            <div className="text-[11px] text-zinc-600">{formatDate(order.orderDate)}</div>
                                            <div className="text-[10px] text-zinc-400">{formatTime(order.orderDate)}</div>
                                        </td>
                                        <td className="px-5 py-3 text-right">
                                            <span className="text-[13px] font-mono font-semibold text-zinc-800">
                                                ${order.totalPrice ?? '—'}
                                            </span>
                                        </td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </table>
                )}
            </div>

            <Modal isOpen={!!selectedOrderId} onClose={() => setSelectedOrderId(null)}>
                {selectedOrderId && (
                    <BillModal
                        orderId={selectedOrderId}
                        onClose={() => setSelectedOrderId(null)}
                    />
                )}
            </Modal>
        </div>
    )
}

export default OrdersPage