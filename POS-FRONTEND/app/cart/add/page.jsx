"use client";
import React, { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import axiosInstance from "@/app/api/axiosInstance";
import Layout from "@/app/Components/Layout";

export default function AddCustomerPage() {
  const router = useRouter();
  const searchParams = useSearchParams();

  const [loading, setLoading] = useState(false);
  const [enableDelivery, setEnableDelivery] = useState(false);

  const [formData, setFormData] = useState({
    customerName: "",
    identifier: "",

    billingAddress: {
      addressLine: "",
      city: "",
      state: "",
      zipcode: "",
      country: "",
    },

    shippingAddress: {
      addressLine: "",
      city: "",
      state: "",
      zipcode: "",
      country: "",
    },
  });

  useEffect(() => {
    const phoneFromQuery = searchParams.get("phone");
    if (phoneFromQuery) {
      setFormData((prev) => ({
        ...prev,
        identifier: phoneFromQuery,
      }));
    }
  }, [searchParams]);

  const updateField = (name, value) => {
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const updateBilling = (name, value) => {
    setFormData((prev) => ({
      ...prev,
      billingAddress: {
        ...prev.billingAddress,
        [name]: value,
      },
    }));
  };

  const updateShipping = (name, value) => {
    setFormData((prev) => ({
      ...prev,
      shippingAddress: {
        ...prev.shippingAddress,
        [name]: value,
      },
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const phone = formData.identifier.trim();
    if (!phone) {
      alert("Phone Number is required");
      return;
    }

    const currentToken = globalThis.window === undefined ? null : localStorage.getItem("token");

    try {
      setLoading(true);

      const customerPayload = {
        customerName: formData.customerName.trim() || "Walk-in Customer",
        identifier: phone,
      };

      if (enableDelivery) {
        customerPayload.billingAddress = formData.billingAddress;
        customerPayload.shippingAddress = formData.shippingAddress;
      }

      await axiosInstance.post("/customer/add", customerPayload);

      const predictableCartString = `${phone}`;
      
      const cartPayload = {
        customerIdentifier: phone,
        identifier: predictableCartString, 
        status: 1, 
        totalDiscount: 0,
        totalPrice: 0,
        entryDtoList: [] 
      };

      const cartRes = await axiosInstance.post("/cart/add", cartPayload);

      const returnedCartId =
        cartRes?.data?.identifier ||
        cartRes?.data?.cart?.identifier ||
        cartRes?.data?.cart;

      const finalCartId = returnedCartId || predictableCartString;

      alert("Customer Profile & Database Cart Created Successfully!");

      if (currentToken && globalThis.window !== undefined) {
        localStorage.setItem("token", currentToken);
      }

      router.push(`/cart/list?cartId=${encodeURIComponent(finalCartId)}&customerPhone=${encodeURIComponent(phone)}`);
      
    } catch (error) {
      console.error("Failed to commit unified transaction sequence:", error);
      alert("Failed To Create Customer and Associated Cart Records.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="min-h-screen bg-slate-100 py-8 px-4">
        <div className="max-w-5xl mx-auto bg-white rounded-2xl shadow border">

          {/* Header */}
          <div className="bg-indigo-600 text-white px-8 py-6 rounded-t-2xl">
            <h1 className="text-2xl font-bold">
              Add Customer
            </h1>
            <p className="text-indigo-100 text-sm mt-1">
              Create customer profile for POS billing
            </p>
          </div>

          <form
            onSubmit={handleSubmit}
            className="p-8"
          >
            <div className="grid md:grid-cols-2 gap-5">

              <div>
                <label htmlFor="phone" className="block text-sm font-semibold mb-2">
                  Phone Number *
                </label>
                <input
                  type="tel"
                  id="phone"
                  required
                  value={formData.identifier}
                  onChange={(e) => updateField("identifier", e.target.value)}
                  placeholder="Enter Phone Number"
                  className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label htmlFor="customerName" className="block text-sm font-semibold mb-2">
                  Customer Name
                </label>
                <input
                  type="text"
                  id="customerName"
                  value={formData.customerName}
                  onChange={(e) => updateField("customerName", e.target.value)}
                  placeholder="Optional"
                  className="w-full border border-slate-300 rounded-xl px-4 py-3 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

            </div>

            <div className="mt-8 bg-slate-50 border rounded-xl p-4">
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  checked={enableDelivery}
                  onChange={(e) => setEnableDelivery(e.target.checked)}
                  className="h-5 w-5"
                />
                <span className="font-semibold text-slate-700">
                  Customer wants product delivery
                </span>
              </label>
            </div>

            {enableDelivery && (
              <div className="mt-8 space-y-8">

                {/* Billing */}
                <div>
                  <h2 className="text-lg font-bold text-indigo-600 mb-4">
                    Billing Address
                  </h2>
                  <div className="grid md:grid-cols-2 gap-4">
                    <input
                      id="billingAddressLine"
                      placeholder="Address Line"
                      value={formData.billingAddress.addressLine}
                      onChange={(e) => updateBilling("addressLine", e.target.value)}
                      className="border rounded-xl px-4 py-3"
                    />
                    <input
                      placeholder="City"
                      value={formData.billingAddress.city}
                      onChange={(e) => updateBilling("city", e.target.value)}
                      className="border rounded-xl px-4 py-3"
                    />
                    <input
                      placeholder="State"
                      value={formData.billingAddress.state}
                      onChange={(e) => updateBilling("state", e.target.value)}
                      className="border rounded-xl px-4 py-3"
                    />
                    <input
                      placeholder="Zip Code"
                      value={formData.billingAddress.zipcode}
                      onChange={(e) => updateBilling("zipcode", e.target.value)}
                      className="border rounded-xl px-4 py-3"
                    />
                    <input
                      placeholder="Country"
                      value={formData.billingAddress.country}
                      onChange={(e) => updateBilling("country", e.target.value)}
                      className="md:col-span-2 border rounded-xl px-4 py-3"
                    />
                  </div>
                </div>

                <div>
                  <h2 className="text-lg font-bold text-red-600 mb-4">
                    Shipping Address
                  </h2>
                  <div className="grid md:grid-cols-2 gap-4">
                    <input
                      placeholder="Address Line"
                      value={formData.shippingAddress.addressLine}
                      onChange={(e) => updateShipping("addressLine", e.target.value)}
                      className="border rounded-xl px-4 py-3"
                    />
                    <input
                      placeholder="City"
                      value={formData.shippingAddress.city}
                      onChange={(e) => updateShipping("city", e.target.value)}
                      className="border rounded-xl px-4 py-3"
                    />
                    <input
                      placeholder="State"
                      value={formData.shippingAddress.state}
                      onChange={(e) => updateShipping("state", e.target.value)}
                      className="border rounded-xl px-4 py-3"
                    />
                    <input
                      placeholder="Zip Code"
                      value={formData.shippingAddress.zipcode}
                      onChange={(e) => updateShipping("zipcode", e.target.value)}
                      className="border rounded-xl px-4 py-3"
                    />
                    <input
                      placeholder="Country"
                      value={formData.shippingAddress.country}
                      onChange={(e) => updateShipping("country", e.target.value)}
                      className="md:col-span-2 border rounded-xl px-4 py-3"
                    />
                  </div>
                </div>

              </div>
            )}

            <div className="flex gap-4 mt-10">
              <button
                type="button"
                onClick={() => router.push("/cart/list")}
                className="px-6 py-3 rounded-xl bg-slate-200 hover:bg-slate-300 font-semibold"
              >
                Cancel
              </button>

              <button
                type="submit"
                disabled={loading}
                className="px-6 py-3 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-semibold"
              >
                {loading ? "Saving..." : "Save Customer"}
              </button>
            </div>
          </form>

        </div>
      </div>
    </Layout>
  );
}