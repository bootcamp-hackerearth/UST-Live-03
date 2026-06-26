"use client";

import { useState } from "react";
import api, { addItem, clearCart, getOrderById } from "@/services/api";
import SingleDropDown from "@/components/common/SingleDropDown";
import ProductGrid from "./ProductGrid";
import CartTable from "./CartTable";
import SummaryCard from "./SummaryCard";
import Toast from "@/components/common/Toast";
import AddCustomerModal from "./AddCustomerModal";
import PrintPromptModal from "./PrintPromptModal";
import OrderBillModal from "@/components/order/OrderBillModal";

export default function SalePage() {

  const [selectedCustomer, setSelectedCustomer] = useState("");
  const [cartItems, setCartItems] = useState([]);
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState("");
  const [showAddCustomer, setShowAddCustomer] = useState(false);
  const [dropdownRefresh, setDropdownRefresh] = useState(0);
  const [isNewCustomer, setIsNewCustomer] = useState(false);
  const [showPrintPrompt, setShowPrintPrompt] = useState(false);
  const [completedOrderId, setCompletedOrderId] = useState(null);
  const [billOrder, setBillOrder] = useState(null);

  const showToast = (message) => {
    setToast(message);
    setTimeout(() => setToast(""), 3000);
  };

  const loadCart = async (customerId) => {
    try {
      const cartResponse = await api.get("/api/cart/get", { params: { identifier: customerId } });
      setCart(cartResponse.data);
      const entryResponse = await api.get("/api/cartEntry/cart", { params: { cartId: customerId } });
      setCartItems(entryResponse.data);
    } catch (error) {
      console.log(error);
      setCartItems([]);
      setCart(null);
    }
  };

  const handleCustomerChange = async (customerId) => {
    if (!customerId) {
      setSelectedCustomer("");
      setCartItems([]);
      setCart(null);
      return;
    }
    try {
      setLoading(true);
      setIsNewCustomer(false);
      setSelectedCustomer(customerId);
      await addItem("cart", { identifier: customerId });
      await loadCart(customerId);
    } catch (error) {
      console.log(error);
    } finally {
      setLoading(false);
    }
  };

  const handleChangeCustomer = () => {
    setSelectedCustomer("");
    setIsNewCustomer(false);
    setCart(null);
    setCartItems([]);
    setDropdownRefresh((prev) => prev + 1);
  };

  const addProductToCart = async (product) => {
    if (!selectedCustomer) {
      alert("Select customer first");
      return;
    }
    try {
      await addItem("cartEntry", { cartId: selectedCustomer, product: product.identifier, quantity: 1 });
      await loadCart(selectedCustomer);
    } catch (error) {
      console.log(error);
    }
  };

  const handleClearCart = async () => {
    if (!selectedCustomer) return;
    try {
      await clearCart(selectedCustomer);
      await loadCart(selectedCustomer);
    } catch (error) {
      console.log(error);
    }
  };

  const handleOrderPlaced = (orderId) => {
    showToast("Order placed successfully!");
    setCompletedOrderId(orderId);
    setShowPrintPrompt(true);
    setSelectedCustomer("");
    setCartItems([]);
    setCart(null);
    setIsNewCustomer(false);
  };

  const handlePrintYes = async () => {
    setShowPrintPrompt(false);
    try {
      const data = await getOrderById(completedOrderId);
      setBillOrder(data);
    } catch (err) {
      console.log(err);
    }
  };

  const handlePrintNo = () => {
    setShowPrintPrompt(false);
    setCompletedOrderId(null);
  };

  const handleCustomerCreated = async (newCustomer) => {
    setShowAddCustomer(false);
    setSelectedCustomer(newCustomer.identifier);
    setIsNewCustomer(true);
    showToast(`Customer ${newCustomer.identifier} created!`);
    try {
      setLoading(true);
      await addItem("cart", { identifier: newCustomer.identifier });
      await loadCart(newCustomer.identifier);
    } catch (error) {
      console.log(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex h-full overflow-hidden bg-[#f4f6f8]">

      <Toast message={toast} />

      {showAddCustomer && (
        <AddCustomerModal
          onClose={() => setShowAddCustomer(false)}
          onCustomerAdded={handleCustomerCreated}
        />
      )}

      {showPrintPrompt && (
        <PrintPromptModal
          onYes={handlePrintYes}
          onNo={handlePrintNo}
        />
      )}

      {billOrder && (
        <OrderBillModal
          order={billOrder}
          onClose={() => {
            setBillOrder(null);
            setCompletedOrderId(null);
          }}
        />
      )}

      <div className="flex flex-col w-130 min-w-120 max-w-135 bg-white border-r border-[#e3e8ef] h-full">
        <div className="px-5 py-4 border-b border-[#e3e8ef] bg-white">
          <p className="text-xs font-semibold uppercase tracking-widest text-[#6b7a99] mb-3">New Transaction</p>

          {isNewCustomer && selectedCustomer ? (
            <div>
              <p className="block mb-2 text-sm font-medium text-[#475467]">Customer</p>
              <div className="flex items-center justify-between w-full h-12 px-4 bg-blue-50 border border-blue-200 rounded-2xl">
                <span className="text-sm font-medium text-[#101828]">{selectedCustomer}</span>
                <button type="button" onClick={handleChangeCustomer} className="text-xs text-blue-600 hover:underline">
                  Change
                </button>
              </div>
            </div>
          ) : (
            <SingleDropDown
              label="Customer"
              model="customer"
              value={selectedCustomer}
              onChange={handleCustomerChange}
              placeholder="Search customer..."
              refreshTrigger={dropdownRefresh}
            />
          )}

          <button
            type="button"
            onClick={() => setShowAddCustomer(true)}
            className="mt-2 text-xs text-blue-600 hover:text-blue-800 hover:underline transition-colors"
          >
            + Create new
          </button>
        </div>

        <div className="flex-1 overflow-y-auto">
          <CartTable
            cartItems={cartItems}
            onRefresh={loadCart}
            selectedCustomer={selectedCustomer}
            onClear={handleClearCart}
          />
        </div>

        <div className="border-t border-[#e3e8ef]">
          <SummaryCard cart={cart} onOrderPlaced={handleOrderPlaced} />
        </div>
      </div>

      <div className="flex-1 flex flex-col h-full overflow-hidden">
        <div className="px-6 py-4 border-b border-[#e3e8ef] bg-white flex items-center justify-between">
          <div>
            <h1 className="text-lg font-bold text-[#101828] tracking-tight">Product Catalog</h1>
            <p className="text-xs text-[#6b7a99] mt-0.5">Click any product to add it to the cart</p>
          </div>
          {loading && (
            <span className="text-xs bg-blue-50 text-blue-600 px-3 py-1 rounded-full font-medium animate-pulse">Loading...</span>
          )}
        </div>
        <div className="flex-1 overflow-y-auto p-5">
          <ProductGrid onSelect={addProductToCart} />
        </div>
      </div>
    </div>
  );
}