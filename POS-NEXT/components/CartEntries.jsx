import PropTypes from "prop-types";
import { ShoppingCartIcon, Trash2 } from "lucide-react";

export default function CartEntries({
    cartEntries,
    products,
    loading,
    getProductName,
    getDisplayValue,
    onDelete,
    onUpdateQuantity,
}) {
    return (
        <div className="h-full flex flex-col rounded-2xl border border-slate-200 bg-slate-50/70 p-1.5">
            <div className="mb-1.5 flex items-center justify-between">
                <h3 className="text-[11px] font-semibold uppercase tracking-wide text-slate-500">
                    Cart Entries
                </h3>

                {loading && (
                    <span className="text-[11px] text-slate-400">
                        Loading...
                    </span>
                )}
            </div>

            <div className="flex-1 min-h-0 overflow-y-auto pr-1 scrollbar-thin">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-2">
                    {cartEntries.length === 0 ? (
                        <div className="flex items-center gap-2 rounded-xl p-2.5 text-[12px] text-slate-400">
                            <span>Cart is Empty</span>
                            <ShoppingCartIcon size={18} />
                        </div>
                    ) : (
                        cartEntries.map((item) => {

                            const product = products.find(
                                p => p.identifier === item.product
                            );

                            const disableIncrement =
                                item.quantity >= (product?.stockQuantity || 0);

                            return (
                                <div
                                    key={item.identifier ?? item.id}
                                    className="rounded-xl border border-slate-300 bg-white p-2 text-[10px]"
                                >
                                    <div className="flex items-start justify-between gap-2">
                                        <div className="min-w-0 font-medium text-slate-900 truncate">
                                            {getProductName(item.product)} [
                                            {getDisplayValue(item.product)}]
                                        </div>

                                        <Trash2
                                            onClick={() => onDelete(item.identifier)}
                                            size={15}
                                            className="shrink-0 text-red-600 cursor-pointer"
                                        />
                                    </div>

                                    <div className="mt-1 text-slate-500">
                                        Qty: {getDisplayValue(item.quantity)}
                                    </div>

                                    <div className="mt-1.5 flex items-center justify-between gap-2">
                                        <button
                                            onClick={() =>
                                                onUpdateQuantity(item, -1)
                                            }
                                            disabled={item.quantity==1}
                                            className="h-5 w-5 rounded-full border border-slate-300 text-[11px] hover:bg-slate-100 disabled:cursor-not-allowed"
                                        >
                                            -
                                        </button>

                                        <span className="text-[11px] font-semibold text-slate-900">
                                            {getDisplayValue(item.quantity)}
                                        </span>

                                        <button
                                            onClick={() => onUpdateQuantity(item, 1)}
                                            disabled={disableIncrement}
                                            className="h-5 w-5 rounded-full border border-slate-300 text-[11px]
                                                     hover:bg-slate-100 disabled:bg-gray-200
                                                     disabled:text-gray-400 disabled:cursor-not-allowed"
                                        >
                                            +
                                        </button>
                                    </div>

                                    <div className="mt-1 text-slate-500">
                                        Total: {getDisplayValue(item.totalPrice)}
                                    </div>
                                </div>
                            )
                        })
                    )}
                </div>
            </div>
        </div>
    );
}

CartEntries.propTypes = {
    cartEntries: PropTypes.array.isRequired,
    products: PropTypes.array.isRequired,
    loading: PropTypes.bool,
    getProductName: PropTypes.func.isRequired,
    getDisplayValue: PropTypes.func.isRequired,
    onDelete: PropTypes.func.isRequired,
    onUpdateQuantity: PropTypes.func.isRequired,
};