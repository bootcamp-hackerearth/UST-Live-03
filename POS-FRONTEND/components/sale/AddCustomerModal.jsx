"use client";
import { useState } from "react";
import PropTypes from "prop-types";
import { addItem, getItem } from "@/services/api";
import { validateEmail, validatePhone } from "@/utils/validation";

export default function AddCustomerModal({ onClose, onCustomerAdded }) {
  
  const [identifier, setIdentifier] = useState("");
  const [phoneNo, setPhoneNo] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [emailError, setEmailError] = useState("");
  const [phoneError, setPhoneError] = useState("");

  const handlePhoneChange = (e) => {
    const val = e.target.value.replace(/\D/g, "").slice(0, 10);
    setPhoneNo(val);
    if (phoneError) setPhoneError("");
  };

  const handleEmailChange = (e) => {
    setIdentifier(e.target.value);
    if (emailError) setEmailError("");
  };

  const handleEmailBlur = async () => {

    console.log("blur token:", localStorage.getItem("token"));
    
    if (!identifier.trim()) return;
    if (!validateEmail(identifier.trim())) {
      setEmailError("Enter a valid email address.");
      return;
    }
    try {
      const existing = await getItem("customer", identifier.trim());
      if (existing?.identifier) {
        setEmailError("A customer with this email already exists.");
      } else {
        setEmailError("");
      }
    } catch {
      setEmailError("");
    }
  };

  console.log("token:", localStorage.getItem("token"));

  const handleSubmit = async () => {
    setError("");
    let valid = true;

    if (!identifier.trim() || !validateEmail(identifier.trim())) {
      setEmailError("Enter a valid email address.");
      valid = false;
    }

    if (!validatePhone(phoneNo)) {
      setPhoneError("Phone number must be exactly 10 digits.");
      valid = false;
    }

    if (emailError) valid = false;
    if (!valid) return;

    try {
      setLoading(true);
      const newCustomer = await addItem("customer", {
        identifier: identifier.trim(),
        phoneNo: Number(phoneNo),
      });
      onCustomerAdded(newCustomer);
    } catch (err) {
      setError("Failed to create customer. Please try again.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <button
        type="button"
        className="absolute inset-0 bg-black/40 backdrop-blur-sm"
        onClick={onClose}
        aria-label="Close modal"
      />

      <div className="relative bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 overflow-hidden">
        <div className="px-6 pt-6 pb-4 border-b border-[#e3e8ef]">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-base font-bold text-[#101828] tracking-tight">
                New Customer
              </h2>
              <p className="text-xs text-[#6b7a99] mt-0.5">
                Enter email and phone to create the customer record
              </p>
            </div>
            <button
              type="button"
              onClick={onClose}
              aria-label="Close"
              className="w-8 h-8 flex items-center justify-center rounded-full text-[#6b7a99] hover:bg-[#f4f6f8] hover:text-[#101828] transition-colors text-lg leading-none"
            >
              ×
            </button>
          </div>
        </div>

        <div className="px-6 py-5 space-y-4">
          <div>
            <label htmlFor="add-customer-email" className="block text-xs font-semibold text-[#344054] mb-1.5 uppercase tracking-wider">
              Email <span className="text-red-500">*</span>
            </label>
            <input
              id="add-customer-email"
              type="email"
              value={identifier}
              onChange={handleEmailChange}
              onBlur={handleEmailBlur}
              placeholder="customer@example.com"
              className={`w-full px-3.5 py-2.5 text-sm border rounded-lg text-[#101828] placeholder-[#98a2b3] focus:outline-none focus:ring-2 transition ${
                emailError
                  ? "border-red-400 focus:ring-red-300"
                  : "border-[#d0d5dd] focus:ring-blue-500 focus:border-blue-500"
              }`}
              onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
            />
            {emailError && (
              <p className="mt-1.5 text-xs text-red-500">{emailError}</p>
            )}
          </div>

          <div>
            <label htmlFor="add-customer-phone" className="block text-xs font-semibold text-[#344054] mb-1.5 uppercase tracking-wider">
              Phone Number <span className="text-red-500">*</span>
            </label>
            <input
              id="add-customer-phone"
              type="tel"
              value={phoneNo}
              onChange={handlePhoneChange}
              placeholder="9876543210"
              maxLength={10}
              className={`w-full px-3.5 py-2.5 text-sm border rounded-lg text-[#101828] placeholder-[#98a2b3] focus:outline-none focus:ring-2 transition ${
                phoneError
                  ? "border-red-400 focus:ring-red-300"
                  : "border-[#d0d5dd] focus:ring-blue-500 focus:border-blue-500"
              }`}
              onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
            />
            <div className="flex justify-between mt-1.5">
              {phoneError ? (
                <p className="text-xs text-red-500">{phoneError}</p>
              ) : (
                <span />
              )}
              <p className="text-xs text-[#98a2b3] ml-auto">{phoneNo.length}/10</p>
            </div>
          </div>

          {error && (
            <p className="text-xs text-red-500 bg-red-50 border border-red-100 rounded-lg px-3 py-2">
              {error}
            </p>
          )}
        </div>

        <div className="px-6 pb-6 flex gap-3">
          <button
            type="button"
            onClick={onClose}
            className="flex-1 px-4 py-2.5 text-sm font-medium text-[#344054] bg-white border border-[#d0d5dd] rounded-lg hover:bg-[#f9fafb] transition-colors"
          >
            Cancel
          </button>
          <button
            type="button"
            onClick={handleSubmit}
            disabled={loading || !!emailError}
            className="flex-1 px-4 py-2.5 text-sm font-semibold text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:opacity-60 disabled:cursor-not-allowed transition-colors"
          >
            {loading ? "Creating..." : "Create Customer"}
          </button>
        </div>
      </div>
    </div>
  );
}

AddCustomerModal.propTypes = {
  onClose: PropTypes.func,
  onCustomerAdded: PropTypes.func,
};