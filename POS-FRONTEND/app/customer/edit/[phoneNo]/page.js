"use client";

import { useCallback, useRef } from "react";
import CommonEditPage from "@/app/components/CommonEditPage";
import ShippingAddressLabel from "@/app/components/ShippingAddressLabel";
import {
  PARTY_TYPE_OPTIONS,
  BILLING_FIELDS,
  SHIPPING_FIELDS,
} from "@/app/components/CustomerFields";
import api from "@/app/services/api";

export default function CustomerEditPage() {
  const formRef = useRef({});

  const handleFormChange = useCallback((data) => {
    formRef.current = data;
  }, []);

  const handleSameAsBilling = (e) => {
    if (!e.target.checked) return;

    const fieldMap = {
      ship_address: formRef.current?.bill_address || "",
      ship_city: formRef.current?.bill_city || "",
      ship_state: formRef.current?.bill_state || "",
      ship_zip: formRef.current?.bill_zip || "",
      ship_country: formRef.current?.bill_country || "",
    };

    Object.entries(fieldMap).forEach(([field, value]) => {
      const input = document.querySelector(`[name="${field}"]`);

      if (input) {
        input.value = value;

        input.dispatchEvent(
          new Event("input", {
            bubbles: true,
            cancelable: true,
          })
        );

        input.dispatchEvent(
          new Event("change", {
            bubbles: true,
            cancelable: true,
          })
        );
      }
    });
  };

  const handleSubmit = async (data) => {
    const payload = {
      identifier: data.identifier,
      customerName: data.customerName,
      phoneNo: data.phoneNo,
      partyType: data.partyType,
      creditType: data.creditType,
      credit: data.credit ? Number(data.credit) : 0,
      creditLimit: data.creditLimit
        ? Number(data.creditLimit)
        : 0,

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
      const res = await api.put(
        "/api/customer/update",
        payload
      );

      if (res.data?.success === false) {
        alert(res.data.message);
        return false;
      }

      alert("Customer updated successfully");
      return true;
    } catch (err) {
      console.error("UPDATE ERROR:", err.response || err);
      alert("Server error");
      return false;
    }
  };

  const fetchCustomer = async (phoneNo) => {
    try {
      const response = await api.get(
        "/api/customer/get",
        {
          params: { phoneNo },
        }
      );

      const d = response.data;

      return {
        data: {
          identifier: d.identifier || "",
          customerName: d.customerName || "",
          phoneNo: d.phoneNo || "",
          partyType: d.partyType || "",
          creditType: d.creditType || "",
          credit: d.credit || "",
          creditLimit: d.creditLimit || "",

          bill_address:
            d.billingAddress?.addressLine || "",
          bill_city:
            d.billingAddress?.city || "",
          bill_state:
            d.billingAddress?.state || "",
          bill_zip:
            d.billingAddress?.zipCode || "",
          bill_country:
            d.billingAddress?.country || "",

          ship_address:
            d.shippingAddress?.addressLine || "",
          ship_city:
            d.shippingAddress?.city || "",
          ship_state:
            d.shippingAddress?.state || "",
          ship_zip:
            d.shippingAddress?.zipCode || "",
          ship_country:
            d.shippingAddress?.country || "",

          createdBy: d.createdBy || "",
          createdOn: d.createdOn || "",
          modifiedBy: d.modifiedBy || "",
          modifiedOn: d.modifiedOn || "",
        },
      };
    } catch (err) {
      console.error("Fetch error:", err);
      throw err;
    }
  };

  return (
    <CommonEditPage
      title="Edit Customer"
      fetchApi={fetchCustomer}
      updateApi={handleSubmit}
      identifierParam="phoneNo"
      redirectRoute="/customer/list"
      submitButtonText="Update Customer"
      onChange={handleFormChange}
      fields={[
        {
          label: "Identifier *",
          name: "identifier",
          type: "text",
        },
        {
          label: "Customer Name *",
          name: "customerName",
          type: "text",
        },
        {
          label: "Phone *",
          name: "phoneNo",
          type: "text",
          readOnly: true,
        },
        {
          label: "Party Type",
          name: "partyType",
          type: "dropdown",
          options: PARTY_TYPE_OPTIONS,
          optionLabel: "identifier",
          optionValue: "identifier",
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
              compact
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