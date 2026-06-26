"use client"
import React, { useEffect, useState } from 'react'
import PropTypes from 'prop-types'
import axios from "@/components/axiosConfig"

const PAYMENT_LABELS = {
    CASH: { label: "Cash", },
    UPI: { label: "UPI",  },
    CARD: { label: "Card",  },
}

const BillModal = ({ orderId, onClose }) => {
    const [orderEntries, setOrderEntries] = useState([])
    const [orderInfo, setOrderInfo] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        if (!orderId) return
        const fetchOrderData = async () => {
            setLoading(true)
            setError(null)
            try {
                const [entriesRes, infoRes] = await Promise.all([
                    axios.get(`/orderEntry/findByOrderId?orderId=${orderId}`),
                    axios.get(`/order/get?identifier=${orderId}`),
                ])
                setOrderEntries(entriesRes.data)
                setOrderInfo(infoRes.data)
            } catch (err) {
                console.error("Failed to fetch order data", err)
                setError("Could not load order details.")
            } finally {
                setLoading(false)
            }
        }
        fetchOrderData()
    }, [orderId])

    const handlePrint = () => {
        if (!orderInfo || !orderEntries.length) return

        const payment = PAYMENT_LABELS[orderInfo.paymentType] || { label: orderInfo.paymentType, icon: "💰" }
        const now = new Date()
        const dateStr = now.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
        const timeStr = now.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true })

        const itemRows = orderEntries.map(item => `
            <tr>
                <td style="padding:5px 4px;border-bottom:1px dashed #e5e7eb;vertical-align:middle;">
                    <div style="font-weight:600;font-size:11px;color:#18181b;">${item.productName ||                        item.product}</div>
                    <div style="font-size:10px;color:#a1a1aa;font-family:'Courier New',monospace;">${item.product}</div>
                </td>
                <td style="padding:5px 4px;border-bottom:1px dashed #e5e7eb;text-align:right;font-size:11px;color:#52525b;vertical-align:middle;">${item.quantity}</td>
                <td style="padding:5px 4px;border-bottom:1px dashed #e5e7eb;text-align:right;font-size:11px;color:#71717a;font-family:'Courier New',monospace;vertical-align:middle;">$${item.unitPrice}</td>
                <td style="padding:5px 4px;border-bottom:1px dashed #e5e7eb;text-align:right;font-size:11px;font-weight:600;color:#18181b;font-family:'Courier New',monospace;vertical-align:middle;">$${item.totalPrice}</td>
            </tr>
        `).join('')

        const couponRow = orderInfo.coupon ? `
            <tr>
                <td style="padding:3px 0;font-size:11px;color:#71717a;">Coupon</td>
                <td style="padding:3px 0;text-align:right;font-size:11px;color:#15803d;font-weight:600;font-family:'Courier New',monospace;">-$${orderInfo.coupon}</td>
            </tr>` : ''

        const html = `<!DOCTYPE html>
<html>
<head>
    <title>Receipt #${orderId}</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: 'Courier New', monospace; background: #fff; color: #18181b; }
        .page { max-width: 380px; margin: 0 auto; padding: 24px 20px; }
    </style>
</head>
<body>
<div class="page">
    <div style="text-align:center;padding-bottom:12px;border-bottom:1px dashed #d4d4d8;margin-bottom:12px;">
        <div style="font-size:18px;font-weight:700;letter-spacing:4px;color:#18181b;">RECEIPT</div>
        <div style="font-size:10px;color:#a1a1aa;margin-top:2px;">Thank you for your purchase</div>
    </div>
    <table style="width:100%;border-collapse:collapse;margin-bottom:12px;padding-bottom:12px;border-bottom:1px dashed #d4d4d8;">
        <tbody>
            ${[
                ['Order ID', `#${orderId}`],
                ['Customer', orderInfo.customerId || orderInfo.identifier],
                ['Date', dateStr],
                ['Time', timeStr],
                ['Payment', ` ${payment.label}`],
            ].map(([k, v]) => `
            <tr>
                <td style="padding:2px 0;font-size:11px;color:#a1a1aa;">${k}</td>
                <td style="padding:2px 0;font-size:11px;font-weight:600;text-align:right;color:#3f3f46;">${v}</td>
            </tr>`).join('')}
        </tbody>
    </table>
    <table style="width:100%;border-collapse:collapse;margin-bottom:12px;padding-bottom:12px;border-bottom:1px dashed #d4d4d8;">
        <thead>
            <tr style="border-bottom:1px solid #18181b;">
                <th style="padding:0 4px 5px 4px;text-align:left;font-size:10px;text-transform:uppercase;letter-spacing:0.06em;color:#a1a1aa;font-weight:600;">Item</th>
                <th style="padding:0 4px 5px 4px;text-align:right;font-size:10px;text-transform:uppercase;letter-spacing:0.06em;color:#a1a1aa;font-weight:600;">Qty</th>
                <th style="padding:0 4px 5px 4px;text-align:right;font-size:10px;text-transform:uppercase;letter-spacing:0.06em;color:#a1a1aa;font-weight:600;">Unit</th>
                <th style="padding:0 4px 5px 4px;text-align:right;font-size:10px;text-transform:uppercase;letter-spacing:0.06em;color:#a1a1aa;font-weight:600;">Total</th>
            </tr>
        </thead>
        <tbody>${itemRows}</tbody>
    </table>
    <table style="width:100%;border-collapse:collapse;">
        <tbody>
            <tr>
                <td style="padding:3px 0;font-size:11px;color:#a1a1aa;">Original</td>
                <td style="padding:3px 0;text-align:right;font-size:11px;color:#a1a1aa;text-decoration:line-through;font-family:'Courier New',monospace;">$${orderInfo.totalOriginalPrice}</td>
            </tr>
            <tr>
                <td style="padding:3px 0;font-size:11px;color:#a1a1aa;">Discount</td>
                <td style="padding:3px 0;text-align:right;font-size:11px;color:#15803d;font-weight:600;font-family:'Courier New',monospace;">-$${orderInfo.discount}</td>
            </tr>
            ${couponRow}
            <tr>
                <td style="padding:3px 0;font-size:11px;color:#a1a1aa;">Tax (8%)</td>
                <td style="padding:3px 0;text-align:right;font-size:11px;color:#52525b;font-family:'Courier New',monospace;">$${(orderInfo.totalPrice * 0.08).toFixed(2)}</td>
            </tr>
            <tr style="border-top:1px solid #d4d4d8;">
                <td style="padding:8px 0 0 0;font-size:14px;font-weight:700;color:#18181b;">Total paid</td>
                <td style="padding:8px 0 0 0;text-align:right;font-size:18px;font-weight:700;color:#18181b;font-family:'Courier New',monospace;">$${orderInfo.totalPrice}</td>
            </tr>
        </tbody>
    </table>
    <div style="text-align:center;margin-top:16px;padding-top:10px;border-top:1px dashed #d4d4d8;font-size:10px;color:#a1a1aa;">
        ${dateStr} · ${timeStr}
    </div>
</div>
<script>window.onload = () => { window.print(); window.close(); }</script>
</body>
</html>`

    const iframe = document.createElement('iframe')
    iframe.style.cssText = 'position:fixed;top:-9999px;left:-9999px;width:460px;height:720px;border:none;'
    document.body.appendChild(iframe)

    iframe.srcdoc = html

    iframe.onload = () => {
        iframe.contentWindow.focus()
        iframe.contentWindow.print()
        setTimeout(() => iframe.remove(), 1000)
    }
    }

    const orderDateObj = orderInfo?.orderDate ? new Date(orderInfo.orderDate) : null
    const dateStr = orderDateObj
        ? orderDateObj.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
        : '—'
    const timeStr = orderDateObj
        ? orderDateObj.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', hour12: true })
        : '—'
    const payment = orderInfo
        ? (PAYMENT_LABELS[orderInfo.paymentType] || { label: orderInfo.paymentType, icon: "💰" })
        : null

    return (
        <div className="w-full max-w-md flex flex-col" style={{ maxHeight: '85vh' }}>
            <div className="flex items-center justify-between mb-3 shrink-0">
                <div>
                    <h2 className="text-base font-semibold text-zinc-900">Order placed</h2>
                    <p className="text-xs text-zinc-400 mt-0.5">Receipt ready to print</p>
                </div>
                <div className="flex items-center gap-1.5">
                    <button
                        onClick={handlePrint}
                        disabled={loading || !!error}
                        className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium border border-zinc-200 rounded-lg hover:bg-zinc-50 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer transition-colors"
                    >
                        🖨️ Print
                    </button>
                    <button
                        onClick={onClose}
                        className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium bg-zinc-900 text-white rounded-lg hover:bg-zinc-700 cursor-pointer transition-colors"
                    >
                        ← Back
                    </button>
                </div>
            </div>

            <div className="overflow-y-auto flex-1 rounded-xl border border-zinc-200 bg-white">

                {loading && (
                    <div className="flex flex-col items-center justify-center py-16 gap-3">
                        <svg className="animate-spin h-5 w-5 text-zinc-400" viewBox="0 0 24 24" fill="none">
                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z" />
                        </svg>
                        <p className="text-xs text-zinc-400">Loading receipt…</p>
                    </div>
                )}

                {!loading && error && (
                    <div className="flex flex-col items-center justify-center py-16 gap-2">
                        <p className="text-xs text-red-500">{error}</p>
                    </div>
                )}

                {!loading && !error && orderInfo && (
                    <>
                        <div className="text-center px-5 pt-4 pb-3 border-b border-dashed border-zinc-200">
                            <div className="text-base font-bold tracking-widest text-zinc-900">RECEIPT</div>
                            <div className="text-[10px] text-zinc-400 mt-0.5">Thank you for your purchase</div>
                        </div>

                        <div className="px-5 py-2.5 border-b border-dashed border-zinc-200 flex flex-col gap-1">
                            {[
                                ['Order ID', `#${orderId}`],
                                ['Customer', orderInfo.customerId || orderInfo.identifier],
                                ['Date', dateStr],
                                ['Time', timeStr],
                                ['Payment', payment ? `${payment.label}` : orderInfo.paymentType],
                            ].map(([k, v]) => (
                                <div key={k} className="flex justify-between text-[11px]">
                                    <span className="text-zinc-400">{k}</span>
                                    <span className="text-zinc-700 font-medium font-mono">{v}</span>
                                </div>
                            ))}
                        </div>

                        <div className="px-5 py-2.5 border-b border-dashed border-zinc-200">
                            <div className="grid text-[10px] font-semibold text-zinc-400 uppercase tracking-wide pb-1.5 border-b border-zinc-200 mb-1" style={{ gridTemplateColumns: '1fr 36px 58px 58px' }}>
                                <span>Item</span>
                                <span className="text-right">Qty</span>
                                <span className="text-right">Unit</span>
                                <span className="text-right">Total</span>
                            </div>
                            {orderEntries.map((item) => (
                                <div key={item.product} className="grid py-1.5 border-b border-zinc-50 text-[11px]" style={{ gridTemplateColumns: '1fr 36px 58px 58px' }}>
                                    <div>
                                        <div className="text-zinc-800 font-medium leading-tight">{item.productName || item.product}</div>
                                        <div className="text-[10px] text-zinc-400 font-mono">{item.product}</div>
                                    </div>
                                    <span className="text-right text-zinc-600 self-center">{item.quantity}</span>
                                    <span className="text-right text-zinc-500 self-center font-mono">${item.unitPrice}</span>
                                    <span className="text-right text-zinc-800 font-medium self-center font-mono">${item.totalPrice}</span>
                                </div>
                            ))}
                        </div>

                        <div className="px-5 py-2.5">
                            {[
                                ['Original', `$${orderInfo.totalOriginalPrice}`, 'struck'],
                                ['Discount', `-$${orderInfo.discount}`, 'green'],
                                ...(orderInfo.coupon ? [['Coupon', `-$${orderInfo.coupon}`, 'green']] : []),
                                ['Tax (8%)', `$${(orderInfo.totalPrice * 0.08).toFixed(2)}`, ''],
                            ].map(([lbl, val, style]) => (
                                <div key={lbl} className="flex justify-between items-center py-1 text-[11px]">
                                    <span className="text-zinc-400">{lbl}</span>
                                    <span className={`font-mono ${style === 'struck' ? 'line-through text-zinc-400' : ''} ${style === 'green' ? 'text-green-700 font-medium' : 'text-zinc-600'}`}>{val}</span>
                                </div>
                            ))}
                            <div className="flex justify-between items-baseline pt-2.5 mt-1 border-t border-zinc-200">
                                <span className="text-sm font-semibold text-zinc-900">Total paid</span>
                                <span className="text-lg font-bold font-mono text-zinc-900">${orderInfo.totalPrice}</span>
                            </div>
                        </div>

                        <div className="text-center px-5 py-2.5 bg-zinc-50 border-t border-zinc-100">
                            <p className="text-[10px] text-zinc-400">{dateStr} · {timeStr}</p>
                        </div>
                    </>
                )}
            </div>
        </div>
    )
}

BillModal.propTypes = {
    orderId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    onClose: PropTypes.func.isRequired,
}

export default BillModal