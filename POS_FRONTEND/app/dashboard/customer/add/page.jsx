"use client";

import { useState } from "react";
import axios from "@/config/axiosConfig";
import { useRouter, useSearchParams } from "next/navigation";

export default function AddCustomer() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const from = searchParams.get("from");

  const [form, setForm] = useState({
    identifier: "",
    customerName: "",
    partyType: "INDIVIDUAL",
    phoneNo: "",
    balance: "",
    creditLimit: "",

    billingAddress: {
      addressType: "BILLING",
      addressLine: "",
      city: "",
      state: "",
      zipcode: "",
      country: "",
      required: true
    },

    shippingAddress: {
      addressType: "SHIPPING",
      addressLine: "",
      city: "",
      state: "",
      zipcode: "",
      country: "",
      required: true
    },
  });

  const handleChange = (e) => {
    const { name, value } = e.target;

    if (name.includes(".")) {
      const [parent, key] = name.split(".");

      if (parent === "billingAddress" || parent === "shippingAddress") {
        setForm((prev) => ({
          ...prev,
          [parent]: {
            ...(prev?.[parent]),
            [key]: value,
          },
        }));
        return;
      }
    }

    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await axios.post("/customer/add", form);
      alert("Customer created successfully");

      router.push(from === "cart" ? "/dashboard/cart" : "/dashboard/customer");
    } catch (err) {
      console.log(err);
      alert("Error creating customer");
    }
  };

  return (
    <div className="min-h-screen bg-[#F6F7F9] p-8">
      <h2 className="text-2xl font-bold text-center mb-6">Customer</h2>

      <div className="max-w-4xl mx-auto bg-white p-6 rounded-xl shadow-lg">

        <form onSubmit={handleSubmit}>
          <label className="font-semibold" htmlFor="identifier">Email</label>
          <input
            id="identifier"
            name="identifier"
            value={form.identifier}
            onChange={handleChange}
            type="email"
            className="w-full border p-2 rounded mt-1 mb-3"
            required={true}
          />

          <label className="font-semibold" htmlFor="customerName">Customer Name</label>
          <input
            id="customerName"
            name="customerName"
            value={form.customerName}
            onChange={handleChange}
            className="w-full border p-2 rounded mt-1 mb-3"
            required={true}
          />

          <label className="font-semibold" htmlFor="partyType">Party Type</label>
          <select
            id="partyType"
            name="partyType"
            value={form.partyType}
            onChange={handleChange}
            className="w-full border p-2 rounded mt-1 mb-3"
            required={true}
          >
            <option value="INDIVIDUAL">INDIVIDUAL</option>
            <option value="BUSINESS">BUSINESS</option>
          </select>

          <label className="font-semibold" htmlFor="phoneNo">Phone Number</label>
          <input
            id="phoneNo"
            name="phoneNo"
            value={form.phoneNo}
            onChange={handleChange}
            className="w-full border p-2 rounded mt-1 mb-3"
            required={true}
          />

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="font-semibold" htmlFor="balance">Balance</label>
              <input
                id="balance"
                name="balance"
                value={form.balance}
                onChange={handleChange}
                className="w-full border p-2 rounded mt-1"
                required={true}
              />
            </div>

            <div>
              <label className="font-semibold" htmlFor="creditLimit">Credit Limit</label>
              <input
                id="creditLimit"
                name="creditLimit"
                value={form.creditLimit}
                onChange={handleChange}
                className="w-full border p-2 rounded mt-1"
                required={true}
              />
            </div>
          </div>

          <div className="mt-6 border-t pt-4">
            <h3 className="text-lg font-semibold mb-3">Billing Address</h3>

            <div className="grid grid-cols-2 gap-4">
              <div className="col-span-2">
                <label className="font-semibold" htmlFor="billingAddress.addressLine">Address Line</label>
                <input
                  id="billingAddress.addressLine"
                  name="billingAddress.addressLine"
                  value={form.billingAddress.addressLine}
                  onChange={handleChange}
                  className="w-full border p-2 rounded mt-1"
                  required={true}
                />
              </div>

              <div>
                <label className="font-semibold" htmlFor="billingAddress.city">City</label>
                <input
                  id="billingAddress.city"
                  name="billingAddress.city"
                  value={form.billingAddress.city}
                  onChange={handleChange}
                  className="w-full border p-2 rounded mt-1"
                  required={true}
                />
              </div>

              <div>
                <label className="font-semibold" htmlFor="billingAddress.state">State</label>
                <input
                  id="billingAddress.state"
                  name="billingAddress.state"
                  value={form.billingAddress.state}
                  onChange={handleChange}
                  className="w-full border p-2 rounded mt-1"
                  required={true}
                />
              </div>

              <div>
                <label className="font-semibold" htmlFor="billingAddress.zipcode">Zip Code</label>
                <input
                  id="billingAddress.zipcode"
                  name="billingAddress.zipcode"
                  value={form.billingAddress.zipcode}
                  onChange={handleChange}
                  className="w-full border p-2 rounded mt-1"
                  required={true}
                />
              </div>

              <div>
                <label className="font-semibold" htmlFor="billingAddress.country">Country</label>
                <input
                  id="billingAddress.country"
                  name="billingAddress.country"
                  value={form.billingAddress.country}
                  onChange={handleChange}
                  className="w-full border p-2 rounded mt-1"
                  required={true}
                />
              </div>
            </div>
          </div>

          <div className="mt-6 border-t pt-4">
            <h3 className="text-lg font-semibold mb-3">Shipping Address</h3>

            <div className="grid grid-cols-2 gap-4">
              <div className="col-span-2">
                <label className="font-semibold" htmlFor="shippingAddress.addressLine">Address Line</label>
                <input
                  id="shippingAddress.addressLine"
                  name="shippingAddress.addressLine"
                  value={form.shippingAddress.addressLine}
                  onChange={handleChange}
                  className="w-full border p-2 rounded mt-1"
                  required={true}
                />
              </div>

              <div>
                <label className="font-semibold" htmlFor="shippingAddress.city">City</label>
                <input
                  id="shippingAddress.city"
                  name="shippingAddress.city"
                  value={form.shippingAddress.city}
                  onChange={handleChange}
                  className="w-full border p-2 rounded mt-1"
                  required={true}
                />
              </div>

              <div>
                <label className="font-semibold" htmlFor="shippingAddress.state">State</label>
                <input
                  id="shippingAddress.state"
                  name="shippingAddress.state"
                  value={form.shippingAddress.state}
                  onChange={handleChange}
                  className="w-full border p-2 rounded mt-1"
                  required={true}
                />
              </div>

              <div>
                <label className="font-semibold" htmlFor="shippingAddress.zipcode">Zip Code</label>
                <input
                  id="shippingAddress.zipcode"
                  name="shippingAddress.zipcode"
                  value={form.shippingAddress.zipcode}
                  onChange={handleChange}
                  className="w-full border p-2 rounded mt-1"
                  required={true}
                />
              </div>

              <div>
                <label className="font-semibold" htmlFor="shippingAddress.country">Country</label>
                <input
                  id="shippingAddress.country"
                  name="shippingAddress.country"
                  value={form.shippingAddress.country}
                  onChange={handleChange}
                  className="w-full border p-2 rounded mt-1"
                  required={true}
                />
              </div>
            </div>
          </div>

          <div className="flex gap-3 mt-6">
            <button
              type="submit"
              className="flex-1 bg-black text-white p-3 rounded-lg"
            >
              Save
            </button>

            <button
              type="button"
              onClick={() =>
                router.push(from === "cart" ? "/dashboard/cart" : "/dashboard/customer")
              }
              className="flex-1 bg-gray-200 p-3 rounded-lg"
            >
              Back
            </button>
          </div>
        </form>

      </div>
    </div>
  );
}