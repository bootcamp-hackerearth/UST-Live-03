"use client";
import PropTypes from "prop-types";

export default function ProductCard({product,onClick}){
  return(
    <button
      type="button"
      onClick={()=>onClick(product)}
      aria-label={`Add ${product.identifier} to cart`}
      className="group bg-white border border-[#e3e8ef] rounded-2xl p-6 cursor-pointer hover:border-blue-500 hover:shadow-md transition-all duration-150 flex flex-col justify-between">
  <div>
    <div className="h-12 w-12 rounded-xl bg-[#f0f4ff] flex items-center justify-center text-xl mb-4">📦</div>
    <p className="font-semibold text-[#101828] text-base leading-tight truncate">{product.identifier}</p>
    <p className="text-sm text-[#6b7a99] mt-1 truncate">{product.brand}</p>
    <p className="text-sm text-[#a0aab8] truncate">{product.model}</p>
  </div>
  <div className="mt-4 pt-4 border-t border-[#f0f2f5]">
    <span className="text-sm font-semibold text-blue-600 group-hover:text-blue-700">+ Add to cart</span>
  </div>
  </button>
  );
}

ProductCard.propTypes = {
  product: PropTypes.object,
  onClick: PropTypes.func,
};