"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import api from "../api/axios";

const CustomerRegistration = ({ onClose, refreshData }) => {
  const [formData, setFormData] = useState({
    identifier: "",
    email: "",
    phoneno: "",
    address: "",
    partytype: "",
    status: true,

    billing: {
      addressLine: "",
      city: "",
      state: "",
      pincode: "",
      country: "",
    },

    shipping: {
      addressLine: "",
      city: "",
      state: "",
      pincode: "",
      country: "",
    },
  });

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await api.post("/api/customer/add", formData);

      if (response.data.success === false) {
        alert(response.data.message);
        return;
      }

      alert("Customer Added Successfully");

      refreshData?.();
      onClose?.();
    } catch (err) {
      console.error(err);
      alert("Failed to add customer");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 text-gray-700">
      <div>
        <label htmlFor="customerName" className="block mb-1">
          Customer Name
        </label>

        <input
          id="customerName"
          type="text"
          value={formData.identifier}
          onChange={(e) =>
            setFormData({
              ...formData,
              identifier: e.target.value,
            })
          }
          className="w-full border rounded p-2"
          required
        />
      </div>

      <div>
        <label htmlFor="email" className="block mb-1">
          Email
        </label>

        <input
          id="customerName"
          type="email"
          value={formData.email}
          onChange={(e) =>
            setFormData({
              ...formData,
              email: e.target.value,
            })
          }
          className="w-full border rounded p-2"
          required
        />
      </div>

      <div>
        <label htmlFor="phone" className="block mb-1">
          Phone
        </label>

        <input
          id="phone"
          type="tel"
          maxLength={10}
          pattern="[0-9]{10}"
          value={formData.phoneno}
          onChange={(e) =>
            setFormData({
              ...formData,
              phoneno: e.target.value,
            })
          }
          className="w-full border rounded p-2"
        />
      </div>
      <div>
        <label htmlFor="address" className="block mb-1">
          Address
        </label>

        <input
          id="address"
          type="text"
          value={formData.address}
          onChange={(e) =>
            setFormData({
              ...formData,
              address: e.target.value,
            })
          }
          className="w-full border rounded p-2"
        />
      </div>
      <div>
        <label htmlFor="partyType" className="block mb-1">
          Party Type
        </label>

        <select
          id="partyType"
          value={formData.partytype}
          onChange={(e) =>
            setFormData({
              ...formData,
              partytype: e.target.value,
            })
          }
          className="w-full border rounded p-2"
        >
          <option value="">Select Party Type</option>
          <option value="Customer">Customer</option>
          <option value="Dealer">Dealer</option>
          <option value="Supplier">Supplier</option>
        </select>
      </div>
      <details>
        <summary className="cursor-pointer font-semibold">
          Billing Address
        </summary>

        <input
          type="text"
          placeholder="Address Line"
          value={formData.billing.addressLine}
          onChange={(e) =>
            setFormData({
              ...formData,
              billing: {
                ...formData.billing,
                addressLine: e.target.value,
              },
            })
          }
          className="w-full border rounded p-2 mt-2"
        />

        <input
          type="text"
          placeholder="City"
          value={formData.billing.city}
          onChange={(e) =>
            setFormData({
              ...formData,
              billing: {
                ...formData.billing,
                city: e.target.value,
              },
            })
          }
          className="w-full border rounded p-2 mt-2"
        />

        <input
          type="text"
          placeholder="State"
          value={formData.billing.state}
          onChange={(e) =>
            setFormData({
              ...formData,
              billing: {
                ...formData.billing,
                state: e.target.value,
              },
            })
          }
          className="w-full border rounded p-2 mt-2"
        />

        <input
          type="number"
          placeholder="Pincode"
          value={formData.billing.pincode}
          onChange={(e) =>
            setFormData({
              ...formData,
              billing: {
                ...formData.billing,
                pincode: e.target.value,
              },
            })
          }
          className="w-full border rounded p-2 mt-2"
        />

        <input
          type="text"
          placeholder="Country"
          value={formData.billing.country}
          onChange={(e) =>
            setFormData({
              ...formData,
              billing: {
                ...formData.billing,
                country: e.target.value,
              },
            })
          }
          className="w-full border rounded p-2 mt-2"
        />
      </details>
      <details>
        <summary className="cursor-pointer font-semibold">
          Shipping Address
        </summary>

        <input
          type="text"
          placeholder="Address Line"
          value={formData.shipping.addressLine}
          onChange={(e) =>
            setFormData({
              ...formData,
              shipping: {
                ...formData.shipping,
                addressLine: e.target.value,
              },
            })
          }
          className="w-full border rounded p-2 mt-2"
        />

        <input
          type="text"
          placeholder="City"
          value={formData.shipping.city}
          onChange={(e) =>
            setFormData({
              ...formData,
              shipping: {
                ...formData.shipping,
                city: e.target.value,
              },
            })
          }
          className="w-full border rounded p-2 mt-2"
        />

        <input
          type="text"
          placeholder="State"
          value={formData.shipping.state}
          onChange={(e) =>
            setFormData({
              ...formData,
              shipping: {
                ...formData.shipping,
                state: e.target.value,
              },
            })
          }
          className="w-full border rounded p-2 mt-2"
        />

        <input
          type="number"
          placeholder="Pincode"
          value={formData.shipping.pincode}
          onChange={(e) =>
            setFormData({
              ...formData,
              shipping: {
                ...formData.shipping,
                pincode: e.target.value,
              },
            })
          }
          className="w-full border rounded p-2 mt-2"
        />

        <input
          type="text"
          placeholder="Country"
          value={formData.shipping.country}
          onChange={(e) =>
            setFormData({
              ...formData,
              shipping: {
                ...formData.shipping,
                country: e.target.value,
              },
            })
          }
          className="w-full border rounded p-2 mt-2"
        />
      </details>

      <button
        type="submit"
        className="w-full bg-blue-600 text-white py-2 rounded"
      >
        Save Customer
      </button>
    </form>
  );
};
CustomerRegistration.propTypes = {
  onClose: PropTypes.func,
  refreshData: PropTypes.func,
};
export default CustomerRegistration;