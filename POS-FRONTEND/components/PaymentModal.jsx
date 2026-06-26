"use client"
import React, { useState } from 'react'
import PropTypes from 'prop-types'

const PAYMENT_METHODS = [
    {
        value: "CASH",
        label: "Cash",
        icon: "💵",
        description: "Pay with physical currency"
    },
    {
        value: "UPI",
        label: "UPI",
        icon: "📲",
        description: "GPay, PhonePe, Paytm & more"
    },
    {
        value: "CARD",
        label: "Card",
        icon: "💳",
        description: "Debit or credit card"
    },
]

const PaymentModal = ({ cartInfo, cartEntryList, onConfirm, onCancel, isLoading }) => {
    const [selectedMethod, setSelectedMethod] = useState(null)

    return (
        <div className="w-full max-w-sm">
            <div className="mb-5">
                <h2 className="text-base font-semibold text-zinc-900">Confirm payment</h2>
                <p className="text-xs text-zinc-400 mt-0.5">
                    {cartEntryList.length} item{cartEntryList.length === 1 ? '' : 's'} · Total due{' '}
                    <span className="font-mono font-medium text-zinc-700">${cartInfo.totalPrice}</span>
                </p>
            </div>

            <p className="text-[10px] font-medium text-zinc-400 uppercase tracking-widest mb-2">
                Payment method
            </p>

            <div className="flex flex-col gap-2 mb-6">
                {PAYMENT_METHODS.map((method) => (
                    <button
                        key={method.value}
                        onClick={() => setSelectedMethod(method.value)}
                        className={`flex items-center gap-3 px-3.5 py-3 rounded-xl border text-left transition-all cursor-pointer
                            ${selectedMethod === method.value
                                ? 'border-zinc-900 bg-zinc-900 text-white'
                                : 'border-zinc-200 bg-white hover:border-zinc-300 hover:bg-zinc-50 text-zinc-800'
                            }`}
                    >
                        <span className="text-xl leading-none">{method.icon}</span>
                        <div className="flex-1">
                            <div className={`text-sm font-medium ${selectedMethod === method.value ? 'text-white' : 'text-zinc-800'}`}>
                                {method.label}
                            </div>
                            <div className={`text-[11px] mt-0.5 ${selectedMethod === method.value ? 'text-zinc-300' : 'text-zinc-400'}`}>
                                {method.description}
                            </div>
                        </div>
                        <div className={`w-4 h-4 rounded-full border-2 flex items-center justify-center transition-all
                            ${selectedMethod === method.value ? 'border-white' : 'border-zinc-300'}`}>
                            {selectedMethod === method.value && (
                                <div className="w-2 h-2 rounded-full bg-white" />
                            )}
                        </div>
                    </button>
                ))}
            </div>

            <div className="flex gap-2">
                <button
                    onClick={onCancel}
                    disabled={isLoading}
                    className="flex-1 py-2.5 text-sm font-medium border border-zinc-200 rounded-xl hover:bg-zinc-50 disabled:opacity-50 cursor-pointer transition-colors"
                >
                    Cancel
                </button>
                <button
                    onClick={() => onConfirm(selectedMethod)}
                    disabled={isLoading || selectedMethod == null}
                    className="flex-1 py-2.5 text-sm font-medium bg-zinc-900 text-white rounded-xl hover:bg-zinc-700 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer transition-colors"
                >
                    {isLoading ? (
                        <span className="flex items-center justify-center gap-2">
                            <svg className="animate-spin h-4 w-4" viewBox="0 0 24 24" fill="none">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8z" />
                            </svg>
                            Processing…
                        </span>
                    ) : "Confirm & charge"}
                </button>
            </div>
        </div>
    )
}

export default PaymentModal

PaymentModal.propTypes = {
    cartInfo: PropTypes.shape({
        totalPrice: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
    }).isRequired,
    cartEntryList: PropTypes.array.isRequired,
    onConfirm: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired,
    isLoading: PropTypes.bool,
}

PaymentModal.defaultProps = {
    isLoading: false,
}