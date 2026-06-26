"use client";

import { useState, useCallback } from "react";
import CommonAddPage from "@/app/components/CommonAddPage";
import ShippingAddressLabel from "@/app/components/ShippingAddressLabel";
import {
  PARTY_TYPE_OPTIONS,
  BILLING_FIELDS,
  SHIPPING_FIELDS,
} from "@/app/components/CustomerFields";
import api from "@/app/services/api";

export default function CustomerAddPage() {
  const [billingSnapshot, setBillingSnapshot] = useState({});
  const [formKey, setFormKey] = useState(0);

  const [shippingValues, setShippingValues] = useState({
    ship_address: "",
    ship_city: "",
    ship_state: "",
    ship_zip: "",
    ship_country: "",
  });

  const handleFormChange = useCallback((data) => {
    setTimeout(() => {
      setBillingSnapshot(data);
    }, 0);
  }, []);

  const handleSameAsBilling = (e) => {
    if (!e.target.checked) return;

    setShippingValues({
      ship_address: billingSnapshot.bill_address || "",
      ship_city: billingSnapshot.bill_city || "",
      ship_state: billingSnapshot.bill_state || "",
      ship_zip: billingSnapshot.bill_zip || "",
      ship_country: billingSnapshot.bill_country || "",
    });

    setFormKey((k) => k + 1);
  };

  const handleSubmit = async (data) => {
    const payload = {
      identifier: data.identifier,
      customerName: data.customerName,
      phoneNo: data.phoneNo,
      partyType: data.partyType,
      creditType: data.creditType,
      credit: data.credit ? Number(data.credit) : 0,
      creditLimit: data.creditLimit ? Number(data.creditLimit) : 0,

      billingAddress: {
        addressLine: data.bill_address,
        city: data.bill_city,
        state: data.bill_state,
        zipCode: data.bill_zip,
        country: data.bill_country,
      },

      shippingAddress: {
        addressLine: data.ship_address,
        city: data.ship_city,
        state: data.ship_state,
        zipCode: data.ship_zip,
        country: data.ship_country,
      },
    };

    try {
      const response = await api.post("/api/customer/add", payload);

      if (response.data?.success === false) {
        alert(response.data.message);
        return false;
      }

      alert(response.data?.message || "Customer added successfully");
      return true;
    } catch (err) {
      console.error(err);
      alert("Server error. Please try again.");
      return false;
    }
  };

  return (
    <CommonAddPage
      key={formKey}
      title="Add Customer"
      submitApi={handleSubmit}
      redirectRoute="/customer/list"
      submitButtonText="Save Customer"
      onFormChange={handleFormChange}
      initialValues={{
        identifier: billingSnapshot.identifier || "",
        customerName: billingSnapshot.customerName || "",
        phoneNo: billingSnapshot.phoneNo || "",
        partyType: billingSnapshot.partyType || "",
        creditType: billingSnapshot.creditType || "",
        credit: billingSnapshot.credit || "",
        creditLimit: billingSnapshot.creditLimit || "",

        bill_address: billingSnapshot.bill_address || "",
        bill_city: billingSnapshot.bill_city || "",
        bill_state: billingSnapshot.bill_state || "",
        bill_zip: billingSnapshot.bill_zip || "",
        bill_country: billingSnapshot.bill_country || "",

        ...shippingValues,
      }}
      fields={[
        {
          label: "Identifier *",
          name: "identifier",
          type: "text",
          required: true,
        },
        {
          label: "Customer Name *",
          name: "customerName",
          type: "text",
          required: true,
        },
        {
          label: "Phone *",
          name: "phoneNo",
          type: "text",
          required: true,
        },
        {
          label: "Party Type",
          name: "partyType",
          type: "dropdown",
          options: PARTY_TYPE_OPTIONS,
          optionLabel: "identifier",
          optionValue: "identifier",
          placeholder: "-- Select --",
        },
        {
          label: "Credit Type",
          name: "creditType",
          type: "text",
        },
        {
          label: "Credit",
          name: "credit",
          type: "number",
        },
        {
          label: "Credit Limit",
          name: "creditLimit",
          type: "number",
        },

        ...BILLING_FIELDS,

        {
          label: (
            <ShippingAddressLabel
              onChange={handleSameAsBilling}
            />
          ),
          name: "ship_address",
          type: "text",
        },

        ...SHIPPING_FIELDS,
      ]}
    />
  );
}