import { ShoppingCartIcon } from "lucide-react";
import PropTypes from "prop-types";
import { getProductIdentifier, getItemLabel, getDisplayValue } from "@/utils/CartHelpers";

export default function ProductList({
    products,
    cartEntries,
    searchTerm,
    setSearchTerm,
    selectedCustomerId,
    addingProductId,
    onAddProduct,
}) {
    return (
        <section className="flex-1 min-h-0 max-h-104 rounded-3xl border border-slate-300 bg-white p-2 shadow-sm flex flex-col overflow-hidden">
            <div className="mb-2.5">
                <h2 className="text-sm font-semibold text-slate-900">
                    Products
                </h2>

                <div className="m-2">
                    <input
                        type="text"
                        placeholder="Search products..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="w-full rounded-xl border border-slate-400 px-1 py-1 text-[11px] placeholder:pl-2 focus:outline-none focus:ring-1 focus:ring-blue-500" />
                </div>
            </div>

            <div className="max-h-94 overflow-y-auto pr-1 scrollbar-thin">
                <div className="grid gap-1 p-2 pb-1">
                    {products.length === 0 ? (
                        <div className="rounded-xl border border-dashed border-slate-200 bg-slate-50 p-2.5 text-[11px] text-slate-400">
                            No products available.
                        </div>
                    ) : (
                        products.map((product) => {
                            const productIdentifier =
                                getProductIdentifier(product);

                            const isAdding =
                                addingProductId === productIdentifier;

                            const quantityInCart =
                                cartEntries.find(
                                    entry => entry.product === productIdentifier
                                )?.quantity || 0;

                            const disableAdd =
                                quantityInCart >= product.stockQuantity;

                            return (
                                <button
                                    key={productIdentifier}
                                    onClick={() => onAddProduct(product)}
                                    disabled={!selectedCustomerId || isAdding || disableAdd}
                                    className="rounded-xl border border-slate-400 bg-slate-50 p-2 text-left transition hover:border-slate-500 hover:bg-slate-300 disabled:opacity-60">
                                    <div className="flex justify-between items-start">
                                        <div className="text-[11px] font-semibold text-slate-900">
                                            {getItemLabel(product)}
                                        </div>

                                        <span>
                                            {isAdding ? ("...") : (
                                                <ShoppingCartIcon size={13} />
                                            )}
                                        </span>
                                    </div>

                                    <div className="mt-1 text-[11px] text-slate-500">
                                        {getDisplayValue(product.category)}
                                    </div>

                                    <div className="mt-1 text-[11px] text-slate-500">
                                        {disableAdd
                                            ? "Maximum stock added"
                                            : `Stock: ${product.stockQuantity}`}
                                    </div>
                                </button>
                            );
                        })
                    )}
                </div>
            </div>
        </section>
    );
}

ProductList.propTypes = {
    products: PropTypes.array.isRequired,
    cartEntries: PropTypes.array.isRequired,
    searchTerm: PropTypes.string.isRequired,
    setSearchTerm: PropTypes.func.isRequired,
    selectedCustomerId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    addingProductId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    onAddProduct: PropTypes.func.isRequired,
};
