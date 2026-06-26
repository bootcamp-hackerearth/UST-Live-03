import PropTypes from 'prop-types';
import {
    CheckCircle2,
    Printer,
} from "lucide-react";

export default function BillModal({
    order,
    cartEntries,
    paymentType,
    selectedCustomer,
    getProductName,
    onClose,
    onPrint,
}) {
    return (
        <div className="space-y-3 text-slate-800">

            <div className="text-center">
                <CheckCircle2 size={24} />

                <h2 className="mt-0.5 text-xs font-bold tracking-wide">TRANSACTION SUCCESSFUL</h2>
            </div>

            <div className="rounded-2xl border border-slate-200 bg-slate-50 p-2.5">

                <div className="text-center">
                    <h3 className="text-xs font-bold tracking-[0.15em]">RETAIL STORE</h3>
                    <p className="mt-0.5 text-[9px] text-slate-500">Automated Terminal Register Node</p>
                </div>

                <div className="my-3 border-t border-dashed border-slate-400" />

                <div className="grid grid-cols-2 gap-x-4 gap-y-1 text-[10px]">

                    <div>
                        <span className="font-semibold">Invoice:</span>
                        <span className="ml-1">{order?.identifier}</span>
                    </div>

                    <div>
                        <span className="font-semibold">Payment:</span>
                        <span className="ml-1">{paymentType}</span>
                    </div>

                    <div>
                        <span className="font-semibold">Customer:</span>
                        <span className="ml-1">{selectedCustomer?.name}</span>
                    </div>

                    <div>
                        <span className="font-semibold">Date:</span>
                        <span className="ml-1">
                            {new Date().toLocaleDateString()}
                        </span>
                    </div>

                </div>

                <div className="my-3 border-t border-dashed border-slate-400" />

                <div className="grid grid-cols-12 text-[10px] font-bold uppercase tracking-wide">
                    <div className="col-span-6">
                        Description
                    </div>

                    <div className="col-span-3 text-center">
                        Qty
                    </div>

                    <div className="col-span-3 text-right">
                        Price
                    </div>
                </div>

                <div className="my-2 border-t border-dashed border-slate-400" />

                <div className="max-h-32 overflow-y-auto scrollbar-thin scrollbar-thumb-slate-300">

                    {cartEntries.map((item) => (
                        <div
                            key={item.identifier}
                            className="grid grid-cols-12 py-1 border-b border-slate-200"
                        >
                            <div className="col-span-6">
                                <div className="text-[11px] font-semibold">
                                    {getProductName(item.product)}
                                </div>

                                <div className="text-[8px] text-slate-400">
                                    {item.product}
                                </div>
                            </div>

                            <div className="col-span-3 text-center text-[10px] font-medium">
                                {item.quantity}
                            </div>

                            <div className="col-span-3 text-right text-[10px] font-semibold">
                                ₹{item.totalPrice}
                            </div>
                        </div>
                    ))}

                </div>

                <div className="my-3 border-t border-dashed border-slate-400" />

                {/* Totals */}
                <div className="space-y-0.5 text-[10px]">

                    <div className="flex justify-between text-slate-500">
                        <span>Gross Value</span>

                        <span>
                            ₹{order?.originalPrice ?? 0}
                        </span>
                    </div>

                    <div className="flex justify-between text-slate-500">
                        <span>Discount</span>

                        <span>
                            -₹{order?.discount ?? 0}
                        </span>
                    </div>

                    <div className="flex justify-between border-t border-slate-400 pt-2 text-xs font-bold">
                        <span>Total</span>

                        <span>
                            ₹{order?.totalPrice ?? 0}
                        </span>
                    </div>

                </div>

            </div>

            {/* Actions */}
            <div className="flex gap-2">

                <button
                    onClick={onClose}
                    className="flex-1 rounded-xl bg-slate-200 py-1.5 text-[10px] font-semibold text-slate-700 hover:bg-slate-300"
                >
                    Dismiss
                </button>

                <button
                    onClick={onPrint}
                    className="flex flex-1 items-center justify-center gap-1.5 rounded-xl bg-slate-900 py-1.5 text-[10px] font-semibold text-white hover:bg-black"
                >
                    <Printer size={13} />
                    Print Receipt
                </button>

            </div>

        </div>
    );
}

BillModal.propTypes = {
    order: PropTypes.object,
    cartEntries: PropTypes.array,
    paymentType: PropTypes.string,
    selectedCustomer: PropTypes.object,
    getProductName: PropTypes.func,
    onClose: PropTypes.func,
    onPrint: PropTypes.func,
};