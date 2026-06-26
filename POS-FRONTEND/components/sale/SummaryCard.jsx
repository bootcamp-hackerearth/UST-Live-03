"use client";
import { useState } from "react";
import PropTypes from "prop-types";
import PaymentModal from "./PaymentModal";
import api from "@/services/api";

export default function SummaryCard({ cart, onOrderPlaced }) {

  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleConfirmPayment = async (paymentMethod) => {
    try {
      setLoading(true);
      const response = await api.post("/api/order/place", {
        identifier: cart?.identifier,
        paymentMethod,
      });

      if (response.data?.success === false) {
        alert(response.data?.message || "Failed to place order");
        return;
      }

      setShowModal(false);
      onOrderPlaced(response.data.id);
    } catch (error) {
      console.log(error);
      alert("Something went wrong. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="px-6 py-5 bg-white">

      <PaymentModal
        open={showModal}
        total={cart?.totalPrice || 0}
        onClose={() => setShowModal(false)}
        onConfirm={handleConfirmPayment}
        loading={loading}
      />

      <div className="space-y-3 mb-5">
        <div className="flex justify-between text-base">
          <span className="text-[#6b7a99]">Subtotal</span>
          <span className="text-[#344054] font-medium">₹{cart?.originalPrice || 0}</span>
        </div>
        <div className="flex justify-between text-base">
          <span className="text-[#6b7a99]">Discount</span>
          <span className="text-green-600 font-medium">−₹{cart?.discount || 0}</span>
        </div>
        <div className="flex justify-between items-center pt-3 border-t border-[#e3e8ef]">
          <span className="text-base font-bold text-[#101828]">Total</span>
          <span className="text-3xl font-extrabold text-[#101828]">₹{cart?.totalPrice || 0}</span>
        </div>
      </div>

      <button
        type="button"
        onClick={() => {
          if (!cart || cart.totalPrice === 0) {
            alert("Cart is empty");
            return;
          }
          setShowModal(true);
        }}
        className="w-full h-14 rounded-xl bg-[#1570ef] hover:bg-[#1264d3] text-white font-semibold text-base transition-colors shadow-sm"
      >
        Place Order ₹{cart?.totalPrice || 0}
      </button>
    </div>
  );
}

SummaryCard.propTypes = {
  cart: PropTypes.object,
  onOrderPlaced: PropTypes.func,
};