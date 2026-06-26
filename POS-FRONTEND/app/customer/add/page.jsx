"use client";

import React from "react";
import AddPage from "../../components/common/AddPage";

function CustomerAdd() {
  const sections = [
    {
      title: "Basic Information",
      columns: 2,
      fields: [
        {
          key: "customerName",
          label: "Customer Name",
          type: "text",
          placeholder: "Enter Customer Name",
          required: true,
        },
        {
          key: "identifier",
          label: "Email",
          type: "email",
          placeholder: "Enter Email",
          required: true,
        },
        {
          key: "phoneNumber",
          label: "Phone Number",
          type: "text",
          placeholder: "Enter Phone Number",
          required: true,
          isPhone: true,
        },
        {
          key: "partyType",
          label: "Party Type",
          type: "text",
          placeholder: "Enter Party Type",
          required: true,
        },
      ],
    },
    {
      title: "Credit & Balance Details",
      columns: 2,
      fields: [
        {
          key: "creditLimit",
          label: "Credit Limit",
          type: "number",
          placeholder: "Enter Credit Limit",
          required: true,
        },
        {
          key: "balance",
          label: "Balance",
          type: "number",
          placeholder: "Enter Balance",
          required: true,
        },
      ],
    },
    {
      key: "billing",
      title: "Billing Address",
      columns: 2,
      fields: [
        {
          key: "billingAddressLine",
          label: "Address Line",
          type: "text",
          placeholder: "Enter Address Line",
          required: true,
          fullWidth: true,
        },
        {
          key: "billingCity",
          label: "City",
          type: "text",
          placeholder: "Enter City",
          required: true,
        },
        {
          key: "billingState",
          label: "State",
          type: "text",
          placeholder: "Enter State",
          required: true,
        },
        {
          key: "billingCountry",
          label: "Country",
          type: "text",
          placeholder: "Enter Country",
          required: true,
          defaultValue: "India",
        },
        {
          key: "billingZipcode",
          label: "ZIP Code",
          type: "number",
          placeholder: "Enter ZIP Code",
          required: true,
        },
        {
          key: "billingPhoneNumber",
          label: "Phone Number",
          type: "text",
          placeholder: "Enter Phone Number",
          required: true,
          isPhone: true,
        },
      ],
    },
    {
      key: "shipping",
      title: "Shipping Address",
      subtitle: "Leave blank if same as billing",
      copyFrom: "billing",
      columns: 2,
      fields: [
        {
          key: "shippingAddressLine",
          label: "Address Line",
          type: "text",
          placeholder: "Enter Address Line",
          fullWidth: true,
        },
        {
          key: "shippingCity",
          label: "City",
          type: "text",
          placeholder: "Enter City",
        },
        {
          key: "shippingState",
          label: "State",
          type: "text",
          placeholder: "Enter State",
        },
        {
          key: "shippingCountry",
          label: "Country",
          type: "text",
          placeholder: "Enter Country",
          defaultValue: "India",
        },
        {
          key: "shippingZipcode",
          label: "ZIP Code",
          type: "number",
          placeholder: "Enter ZIP Code",
        },
        {
          key: "shippingPhoneNumber",
          label: "Phone Number",
          type: "text",
          placeholder: "Enter Phone Number",
          isPhone: true,
        },
      ],
    },
  ];

  const buildCustomPayload = (formData) => ({
    customerName: formData.customerName,
    identifier: formData.identifier,
    phoneNumber: Number(formData.phoneNumber),
    partyType: formData.partyType,
    creditLimit: Number(formData.creditLimit),
    balance: Number(formData.balance),
    billingAddress: {
      addressType: "Billing",
      addressLine: formData.billingAddressLine,
      city: formData.billingCity,
      state: formData.billingState,
      country: formData.billingCountry,
      zipcode: Number(formData.billingZipcode),
      phoneNumber: Number(formData.billingPhoneNumber),
    },
    shippingAddress: {
      addressType: "Shipping",
      addressLine: formData.shippingAddressLine || formData.billingAddressLine,
      city: formData.shippingCity || formData.billingCity,
      state: formData.shippingState || formData.billingState,
      country: formData.shippingCountry || formData.billingCountry,
      zipcode: Number(formData.shippingZipcode || formData.billingZipcode),
      phoneNumber: Number(
        formData.shippingPhoneNumber || formData.billingPhoneNumber
      ),
    },
  });

  return (
    <AddPage
      title="Add New Customer"
      submitUrl="/customer/add"
      backUrl="/customer"
      sections={sections}
      existingRoute="customer"
      uniqueFields={["identifier"]}
      buildCustomPayload={buildCustomPayload}
      initialValues={{
        customerName: "",
        identifier: "",
        phoneNumber: "",
        partyType: "",
        creditLimit: "",
        balance: "",
        billingAddressLine: "",
        billingCity: "",
        billingState: "",
        billingCountry: "India",
        billingZipcode: "",
        billingPhoneNumber: "",
        shippingAddressLine: "",
        shippingCity: "",
        shippingState: "",
        shippingCountry: "India",
        shippingZipcode: "",
        shippingPhoneNumber: "",
      }}
    />
  );
}

export default CustomerAdd;