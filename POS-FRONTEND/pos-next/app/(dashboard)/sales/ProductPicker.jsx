"use client";

import PropTypes from "prop-types";
import { useState, useEffect } from "react";
import { proxyPost } from "./proxyClient";

function ProductCard({ product, onAddToCart, loading }) {
    const [quantity, setQuantity] = useState(1);

    const handleQuantityChange = (e) => {
        const val = Number.parseInt(e.target.value, 10);
        if (Number.isNaN(val)) {
            setQuantity("");
        } else {
            setQuantity(Math.max(1, val));
        }
    };

    const handleAddToCart = () => {
        const finalQuantity = quantity === "" ? 1 : quantity;
        onAddToCart(product, finalQuantity);
    };

    return (
        <div
            className="flex flex-col bg-white rounded-3xl overflow-hidden border border-slate-200 hover:border-orange-200 hover:shadow-lg transition-all duration-200 h-full w-full min-h-85 shrink-0"
        >
            <div className="aspect-square w-full bg-slate-100 flex items-center justify-center shrink-0">
                {product.image ? (
                    <img
                        src={product.image}
                        alt={product.name}
                        className="w-full h-full object-cover"
                    />) : (
                    <div className="text-slate-400 font-bold text-lg border border-slate-300 rounded-xl px-4 py-2 bg-white">
                        IMG
                    </div>
                )}
            </div>

            <div className="p-4 flex flex-col flex-1 justify-between">
                <div className="mb-2">
                    <div className="text-[11px] text-slate-400 uppercase tracking-wide mb-1">
                        Product
                    </div>
                    <h4
                        className="font-bold text-slate-900 text-sm line-clamp-2 h-10 overflow-hidden text-ellipsis"
                        title={product.name}
                    >
                        {product.name}
                    </h4>
                </div>

                <div className="space-y-3">
                    <div className="flex flex-col gap-1.5">
                        <label htmlFor="quantity" className="text-xs font-semibold text-slate-900">
                            Quantity
                        </label>
                        <input
                            id="quantity"
                            type="number"
                            min="0"
                            value={quantity}
                            onChange={handleQuantityChange}
                            className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm text-black
                                       outline-none transition focus:border-orange-300 focus:ring-2 focus:ring-orange-100"
                        />
                    </div>

                    <button
                        onClick={handleAddToCart}
                        disabled={loading}
                        className="w-full rounded-full border border-slate-200 bg-white py-2.5 text-sm
                                  font-semibold text-slate-700 transition hover:bg-orange-50 hover:border-orange-200
                                  disabled:opacity-40 shrink-0"
                    >
                        Add To Cart
                    </button>
                </div>
            </div>
        </div>
    );
}

ProductCard.propTypes = {
    product: PropTypes.object.isRequired,
    onAddToCart: PropTypes.func.isRequired,
    loading: PropTypes.bool,
};

export default function ProductPicker({ onAddToCart, loading }) {
    const [products, setProducts] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [loadingProducts, setLoadingProducts] = useState(true);

    useEffect(() => {
        const loadProducts = async () => {
            try {
                setLoadingProducts(true);

                const response = await proxyPost("/api/product/list", {
                    page: 0,
                    sizePerPage: 100,
                    sortField: "id",
                    sortDirection: "ASC",
                });

                setProducts(response.dtoList || []);
            } catch (error) {
                console.error("Failed to fetch products:", error);
            } finally {
                setLoadingProducts(false);
            }
        };

        loadProducts();
    }, []);

    const filteredProducts = products.filter((product) =>
        Object.values(product).some((value) =>
            String(value)
                .toLowerCase()
                .includes(searchTerm.toLowerCase())
        )
    );

    if (loadingProducts) {
        return (
            <div className="flex items-center justify-center h-full min-h-100">
                <div className="w-8 h-8 border-4 border-orange-500 border-t-transparent rounded-full animate-spin" />
            </div>
        );
    }

    return (
        <div className="h-full w-full flex flex-col bg-[#faf8f6] min-w-0">
            <div className="px-6 pt-6 pb-3 bg-white border-b border-slate-200 shrink-0">
                <div className="flex justify-between items-start gap-4">
                    <div>
                        <span className="text-[11px] uppercase tracking-[0.2em] font-bold text-orange-500">
                            Product Area
                        </span>
                        <h2 className="text-2xl font-bold text-slate-900 mt-1">
                            Product List
                        </h2>
                    </div>

                    <div className="bg-orange-50 border border-orange-100 rounded-2xl px-4 py-2 min-w-17.5 text-center shrink-0">
                        <div className="text-[10px] uppercase font-bold tracking-wider text-orange-500">
                            Items
                        </div>
                        <div className="text-lg font-bold text-orange-600">
                            {filteredProducts.length}
                        </div>
                    </div>
                </div>

                <div className="mt-4">
                    <input
                        type="text"
                        placeholder="Search product by name, code, brand..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="
                            w-full
                            rounded-2xl
                            border
                            border-slate-200
                            bg-white
                            text-black
                            px-4
                            py-3
                            text-sm
                            outline-none
                            transition
                            focus:border-orange-300
                            focus:ring-2
                            focus:ring-orange-100
                        "
                    />
                </div>
            </div>

            <div className="flex-1 overflow-y-auto p-5 min-h-0">
                {filteredProducts.length === 0 ? (
                    <div className="flex items-center justify-center h-full min-h-50 text-slate-400 font-medium">
                        No products found
                    </div>
                ) : (
                    <div className="grid grid-cols-2 gap-5 auto-rows-fr">
                        {filteredProducts.map((product) => (
                            <ProductCard
                                key={product.id}
                                product={product}
                                onAddToCart={onAddToCart}
                                loading={loading}
                            />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

ProductPicker.propTypes = {
    onAddToCart: PropTypes.func.isRequired,
    loading: PropTypes.bool,
};