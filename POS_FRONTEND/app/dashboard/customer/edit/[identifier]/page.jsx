"use client";

import { useEffect, useState } from "react";
import axios from "@/config/axiosConfig";
import { useParams, useRouter } from "next/navigation";

export default function EditCustomer() {

  const { identifier } = useParams();

  const router = useRouter();

  const [form, setForm] = useState(null);

  useEffect(() => {
    if (identifier) {
      fetchCustomer();
    }
  }, [identifier]);

  const fetchCustomer = async () => {
    try {

      const res = await axios.get(
        `/customer/get?identifier=${identifier}`
      );

      setForm({
        ...res.data,

        billingAddress:
          res.data?.billingAddress || {
            addressType: "BILLING",
          },

        shippingAddress:
          res.data?.shippingAddress || {
            addressType: "SHIPPING",
          },
      });

    } catch (err) {
      console.log(err);
    }
  };

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

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {

      await axios.put(
        "/customer/update",
        form
      );

      alert("Customer updated successfully");

      router.push(
        "/dashboard/customer"
      );

    } catch (err) {
      console.log(err);
      alert("Update failed");
    }
  };

  if (!form) {
    return <div className="p-6">Loading...</div>;
  }
  return (
    <div className="min-h-screen bg-[#F6F7F9] p-8">

      <h2 className="text-2xl font-bold text-center mb-6">
        Update Customer
      </h2>

      <div className="max-w-4xl mx-auto bg-white p-6 rounded-xl shadow-lg">

        <form onSubmit={handleSubmit}>
          <label className="font-semibold" htmlFor="identifier">Email</label>
          <input
            id="identifier"
            name="identifier"
            value={form.identifier || ""}
            readOnly
            className="w-full border p-2 rounded mt-1 mb-3 bg-gray-100"
            required={true}
          />

          <label className="font-semibold" htmlFor="customerName">Customer Name</label>
          <input
            id="customerName"
            name="customerName"
            value={form.customerName || ""}
            onChange={handleChange}
            className="w-full border p-2 rounded mt-1 mb-3"
            required={true}
          />

          <label className="font-semibold" htmlFor="partyType">Party Type</label>
          <select
            id="partyType"
            name="partyType"
            value={form.partyType || ""}
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
            value={form.phoneNo || ""}
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
                value={form.balance || ""}
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
                value={form.creditLimit || ""}
                onChange={handleChange}
                className="w-full border p-2 rounded mt-1"
                required={true}
              />
            </div>
          </div>

          <div className="mt-6 border-t pt-4">
            <h3 className="text-lg font-semibold mb-3">
              Billing Address
            </h3>

            <div className="grid grid-cols-2 gap-4">
              <input
                name="billingAddress.addressLine"
                value={form.billingAddress?.addressLine || ""}
                onChange={handleChange}
                placeholder="Address Line"
                className="border p-2 rounded col-span-2"
                required={true}
              />

              <input
                name="billingAddress.city"
                value={form.billingAddress?.city || ""}
                onChange={handleChange}
                placeholder="City"
                className="border p-2 rounded"
                required={true}
              />

              <input
                name="billingAddress.state"
                value={form.billingAddress?.state || ""}
                onChange={handleChange}
                placeholder="State"
                className="border p-2 rounded"
                required={true}
              />

              <input
                name="billingAddress.zipcode"
                value={form.billingAddress?.zipcode || ""}
                onChange={handleChange}
                placeholder="Zipcode"
                className="border p-2 rounded"
                required={true}
              />

              <input
                name="billingAddress.country"
                value={form.billingAddress?.country || ""}
                onChange={handleChange}
                placeholder="Country"
                className="border p-2 rounded"
                required={true}
              />
            </div>
          </div>

          <div className="mt-6 border-t pt-4">
            <h3 className="text-lg font-semibold mb-3">
              Shipping Address
            </h3>

            <div className="grid grid-cols-2 gap-4">
              <input
                name="shippingAddress.addressLine"
                value={form.shippingAddress?.addressLine || ""}
                onChange={handleChange}
                placeholder="Address Line"
                className="border p-2 rounded col-span-2"
                required={true}
              />

              <input
                name="shippingAddress.city"
                value={form.shippingAddress?.city || ""}
                onChange={handleChange}
                placeholder="City"
                className="border p-2 rounded"
                required={true}
              />

              <input
                name="shippingAddress.state"
                value={form.shippingAddress?.state || ""}
                onChange={handleChange}
                placeholder="State"
                className="border p-2 rounded"
                required={true}
              />

              <input
                name="shippingAddress.zipcode"
                value={form.shippingAddress?.zipcode || ""}
                onChange={handleChange}
                placeholder="Zipcode"
                className="border p-2 rounded"
                required={true}
              />

              <input
                name="shippingAddress.country"
                value={form.shippingAddress?.country || ""}
                onChange={handleChange}
                placeholder="Country"
                className="border p-2 rounded"
                required={true}
              />
            </div>
          </div>

          <div className="flex gap-3 mt-6">
            <button
              type="submit"
              className="flex-1 bg-black text-white p-3 rounded-lg"
            >
              Update
            </button>

            <button
              type="button"
              onClick={() => router.push("/dashboard/customer")}
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