"use client";
import PropTypes from "prop-types";
import {addItem,deleteItem} from "@/services/api";

export default function CartTable({cartItems,selectedCustomer,onRefresh,onClear}){

  const increaseQuantity=async(item)=>{
    try{
      await addItem("cartEntry",{cartId:selectedCustomer,product:item.product,quantity:1});
      await onRefresh(selectedCustomer);
    }catch(error){console.log(error);}
  };

  const decreaseQuantity=async(item)=>{
    try{
      await addItem("cartEntry",{cartId:selectedCustomer,product:item.product,quantity:-1});
      await onRefresh(selectedCustomer);
    }catch(error){console.log(error);}
  };

  const removeItem=async(identifier)=>{
    try{
      await deleteItem("cartEntry",identifier);
      await onRefresh(selectedCustomer);
    }catch(error){console.log(error);}
  };

  return(
    <div>
      {cartItems.length>0&&(
        <div className="px-6 py-3 flex items-center justify-between border-b border-[#f0f2f5]">
          
          <p className="text-sm font-semibold text-[#344054]">{cartItems.length} item{cartItems.length>1?"s":""}</p>
          <button
            type="button"
            onClick={onClear}
            className="text-xs font-medium text-red-500 hover:text-red-600 hover:bg-red-50 px-3 py-1.5 rounded-lg transition-colors"
          >
            Clear all
          
          </button>
        </div>
      )}

      {cartItems.length===0?(
        <div className="flex flex-col items-center justify-center py-16 px-6 text-center">
          <div className="h-14 w-14 rounded-2xl bg-[#f4f6f8] flex items-center justify-center text-2xl mb-3">🛒</div>
          
          <p className="text-sm font-medium text-[#344054]">Cart is empty</p>
          <p className="text-xs text-[#a0aab8] mt-1">Add products from the catalog</p>
        </div>
      ):(
        <div className="divide-y divide-[#f0f2f5]">
          {cartItems.map((item)=>(
            <div key={item.identifier} className="px-6 py-4 hover:bg-[#fafbfc] transition-colors">
              <div className="flex items-start justify-between mb-3">
                <p className="text-base font-semibold text-[#101828] leading-tight">{item.product}</p>
                <button
                  type="button"
                  onClick={()=>removeItem(item.identifier)}
                  className="text-[#c0c8d8] hover:text-red-500 transition-colors ml-2 text-lg leading-none"
                >×</button>
              </div>
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-1.5">
                  <button type="button" onClick={()=>decreaseQuantity(item)} className="h-7 w-7 rounded-lg border border-[#e3e8ef] text-[#344054] hover:bg-[#f0f2f5] text-sm font-bold transition-colors">−</button>
                  <span className="w-8 text-center text-sm font-semibold text-[#101828]">{item.quantity}</span>
                  <button type="button" onClick={()=>increaseQuantity(item)} className="h-7 w-7 rounded-lg border border-[#e3e8ef] text-[#344054] hover:bg-[#f0f2f5] text-sm font-bold transition-colors">+</button>
                </div>
                <div className="text-right">
                  <div className="flex items-center justify-end gap-1.5">
                    <p className="text-sm text-[#a0aab8] line-through">₹{item.originalPrice}</p>
                    <p className="text-base font-bold text-[#101828]">₹{item.totalPrice}</p>
                  </div>
                  <p className="text-xs text-[#a0aab8]">₹{item.unitPrice} each</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

CartTable.propTypes = {
  cartItems: PropTypes.array,
  selectedCustomer: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onRefresh: PropTypes.func,
  onClear: PropTypes.func,
};