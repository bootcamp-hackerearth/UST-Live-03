"use client";

import { useState } from "react";
import api from "../services/api";
import PropTypes from "prop-types";

export default function AddCustomerForm({ onSaved, onCancel }) {
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrors({});
    
    const formData = new FormData(e.target);
    const customerName = formData.get("customerName")?.trim();
    const phoneNo = formData.get("phoneNo")?.trim();

    const validationErrors = {};
    if (!customerName || customerName.length < 2) {
      validationErrors.customerName = "Name must be at least 2 characters long.";
    }
    if (!/^\d{10}$/.test(phoneNo)) {
      validationErrors.phoneNo = "Please provide a valid 10-digit mobile number.";
    }

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    const payload = {
      customerName,
      phoneNo,
      status: true,
      partyType: "CUSTOMER",
      creditType: "",
      credit: 0,
      creditLimit: 0,
      billingAddress: { addressLine: "", city: "", state: "", zipCode: "", country: "" },
      shippingAddress: { addressLine: "", city: "", state: "", zipCode: "", country: "" },
    };

    try {
      setSubmitting(true);
      const response = await api.post("/api/customer/add", payload);

      if (response.data.success === false) {
        alert(response.data.message || "Failed request validation configuration code.");
        return;
      }

      alert("Customer profile attached and saved successfully.");
      if (onSaved) onSaved(payload.phoneNo);
    } catch (error) {
      console.error("ADD CUSTOMER ERROR =>", error);
      alert(error.response?.data?.message || "Internal network processing failure saved file logs.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label
  htmlFor="customerName"
  className="block mb-1 text-xs font-semibold text-gray-600 uppercase tracking-wider"
>
  Customer Full Name
</label>

<input
  id="customerName"
  name="customerName"
  type="text"
  required
  placeholder="e.g. John Doe"
/>
        {errors.customerName && <p className="text-xs text-red-500 mt-1 font-medium">{errors.customerName}</p>}
      </div>

      <div>
  <label
    htmlFor="phoneNo"
    className="block mb-1 text-xs font-semibold text-gray-600 uppercase tracking-wider"
  >
    Mobile Phone Number
  </label>

  <input
    id="phoneNo"
    name="phoneNo"
    type="text"
    maxLength={10}
    required
    placeholder="e.g. 9876543210"
    className={`w-full border rounded-lg p-2.5 text-sm bg-gray-50 focus:bg-white focus:outline-none focus:ring-2 transition-all ${
      errors.phoneNo
        ? "border-red-400 focus:ring-red-200"
        : "border-gray-200 focus:ring-blue-200 focus:border-blue-500"
    }`}
  />
</div>

      <div className="flex justify-end gap-2 pt-2 border-t border-gray-100 mt-4">
        <button
          type="button"
          onClick={onCancel}
          disabled={submitting}
          className="px-4 py-2 text-sm font-medium bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-lg transition-colors"
        >
          Dismiss
        </button>
        <button
          type="submit"
          disabled={submitting}
          className="px-4 py-2 text-sm font-medium bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow-sm transition-all disabled:opacity-50"
        >
          {submitting ? "Saving..." : "Save Record"}
        </button>
      </div>
    </form>
  );
  
}
AddCustomerForm.propTypes = {
  onSaved: PropTypes.func,
  onCancel: PropTypes.func,
};
