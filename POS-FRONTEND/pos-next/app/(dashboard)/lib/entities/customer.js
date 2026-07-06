import { buildEndpoints } from "./baseSchema";

export const customer = {
  ...buildEndpoints("/api/customer"),
  
  columns: [
    { field: "serialNumber", label: "S.No" },
    { field: "identifier", label: "Customer Email" },
    { field: "name", label: "Customer Name" },
    { field: "phoneNo", label: "Phone Number" },
    { field: "balance", label: "Balance" },
    { field: "creditLimit", label: "Credit Limit" },
    { field: "partyType", label: "Party Type" }
  ],
  
  formFields: [
    { name: "name", label: "Name", type: "text", required: true },
    { name: "phoneNo", label: "Phone Number", type: "tel", required: true, editable: false},
    { name: "identifier", label: "Email", type: "email", required: true, editable: false },
    {
      name: "partyType",
      label: "Party Type",
      type: "select",
      required: true, 
      options: [  
        { value: "customer", label: "Customer" },
        { value: "dealer", label: "Dealer" },
        { value: "wholesaler", label: "Wholesaler" }
      ]
    },
    { name: "balance", label: "Balance", type: "number", required: true, min: 0, step: "0.01" },
    { name: "creditLimit", label: "Credit Limit", type: "number", required: true, min: 0, step: "0.01" },
    { name: "shippingAddress.addressLine", label: "Shipping Address Line", type: "text", required: true },
    { name: "shippingAddress.city", label: "Shipping City", type: "text", required: true },
    { name: "shippingAddress.state", label: "Shipping State", type: "text", required: true },
    { name: "shippingAddress.zipcode", label: "Shipping Zip Code", type: "number", required: true },
    { name: "shippingAddress.country", label: "Shipping Country", type: "text", required: true },
    { name: "shippingAddress.addressType", label: "Shipping Address Type", type: "select", options: [{ value: "shippingAddress", label: "Shipping Address" }] },
    { name: "billingAddress.addressLine", label: "Billing Address Line", type: "text", required: true },
    { name: "billingAddress.city", label: "Billing City", type: "text", required: true },
    { name: "billingAddress.state", label: "Billing State", type: "text", required: true },
    { name: "billingAddress.zipcode", label: "Billing Zip Code", type: "number", required: true },
    { name: "billingAddress.country", label: "Billing Country", type: "text", required: true },
    { name: "billingAddress.addressType", label: "Billing Address Type", type: "select", options: [{ value: "billingAddress", label: "Billing Address" }] }
  ]
};