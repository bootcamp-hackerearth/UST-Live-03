"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation, emailValidation, nameValidation, phoneValidation } from "@/validation/validation";

export default function CustomerPage() {
  return (
    <CommonList
      routeName="customer"
      editField="identifier"
      keys={["identifier", "name", "phoneNo", "userType", "balance", "creditLimit", "status"]}
      headers={["Email", "Name", "Phone", "User Type", "Balance", "Credit Limit", "Status"]}
      FormComponent={CustomerForm}
    />
  );
}

CustomerForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function CustomerForm({ mode, data, onClose, onSubmit }) {
  const customValidations = {
    identifier: emailValidation,
    name: nameValidation,
    phoneNo: phoneValidation,
    userType: requiredValidation,
    balance: requiredValidation,
    creditLimit: requiredValidation,
  };

  return (
    <CommonForm
      title="Customer"
      mode={mode}
      data={data}
      onClose={onClose}
      validate={customValidations}
      onSubmit={async (formData) => {
        const payload = {
          id: data?.id || null,
          identifier: formData.identifier,
          name: formData.name,
          phoneNo: formData.phoneNo,
          userType: formData.userType || "Customer",
          balance: Number(formData.balance || 0),
          creditLimit: Number(formData.creditLimit || 0),
          billingAddress: {
            id: data?.billingAddress?.id || null,
            addressline: formData["billingAddress.addressline"] || "",
            city: formData["billingAddress.city"] || "",
            state: formData["billingAddress.state"] || "",
            country: formData["billingAddress.country"] || "",
            zipcode: formData["billingAddress.zipcode"] ? Number(formData["billingAddress.zipcode"]) : null,
            addressType: "billingAddress",
            phoneNo: Number(formData.phoneNo || 0),
          },
          shippingAddress: {
            id: data?.shippingAddress?.id || null,
            addressline: formData["shippingAddress.addressline"] || "",
            city: formData["shippingAddress.city"] || "",
            state: formData["shippingAddress.state"] || "",
            country: formData["shippingAddress.country"] || "",
            zipcode: formData["shippingAddress.zipcode"] ? Number(formData["shippingAddress.zipcode"]) : null,
            addressType: "shippingAddress",
            phoneNo: Number(formData.phoneNo || 0),
          },
        };

        await onSubmit(payload);
      }}
      fields={[
        { name: "identifier", label: "Email", placeholder: "Email", required: true },
        { name: "name", label: "Customer Name", placeholder: "Customer Name", required: true },
        { name: "phoneNo", label: "Phone Number", placeholder: "Phone Number", type: "number", required: true },
        { name: "userType", label: "User Type", placeholder: "User Type", type: "staticSelect", options: ["Customer"], required: true },
        { name: "balance", label: "Balance", placeholder: "Balance", type: "number", required: true },
        { name: "creditLimit", label: "Credit Limit", placeholder: "Credit Limit", type: "number", required: true },
        { name: "billingAddress.addressline", label: "Billing Address", placeholder: "Billing Address" },
        { name: "billingAddress.city", label: "Billing City", placeholder: "Billing City" },
        { name: "billingAddress.state", label: "Billing State", placeholder: "Billing State" },
        { name: "billingAddress.country", label: "Billing Country", placeholder: "Billing Country" },
        { name: "billingAddress.zipcode", label: "Billing Zipcode", placeholder: "Billing Zipcode", type: "number" },
        { name: "shippingAddress.addressline", label: "Shipping Address", placeholder: "Shipping Address" },
        { name: "shippingAddress.city", label: "Shipping City", placeholder: "Shipping City" },
        { name: "shippingAddress.state", label: "Shipping State", placeholder: "Shipping State" },
        { name: "shippingAddress.country", label: "Shipping Country", placeholder: "Shipping Country" },
        { name: "shippingAddress.zipcode", label: "Shipping Zipcode", placeholder: "Shipping Zipcode", type: "number" },
      ]}
    />
  );
}