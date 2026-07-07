
const getAddressFields = (prefix, labelPrefix) => [
  {
    name: `${prefix}AddressLine`,
    label: `${labelPrefix} Address`,
    type: "text",
  },
  {
    name: `${prefix}City`,
    label: `${labelPrefix} City`,
    type: "text",
  },
  {
    name: `${prefix}State`,
    label: `${labelPrefix} State`,
    type: "text",
  },
  {
    name: `${prefix}Zipcode`,
    label: `${labelPrefix} Zip`,
    type: "number",
  },
  {
    name: `${prefix}Country`,
    label: `${labelPrefix} Country`,
    type: "text",
  },
];

export const customerFields = [
  { name: "name", label: "Name", type: "text" },
  {
    name: "phoneNo",
    label: "Phone Number",
    type: "text",
    validation: "phone",
  },
  {
    name: "identifier",
    label: "Email",
    type: "text",
    validation: "email",
  },
  {
    name: "partyType",
    backendName: "party_type",
    label: "Party Type",
    type: "select",
    options: [
      { value: "customer", label: "Customer" },
      { value: "dealer", label: "Dealer" },
      { value: "wholesaler", label: "Wholesaler" },
    ],
  },
  { name: "balance", label: "Balance", type: "number" },
  { name: "creditLimit", label: "Credit Limit", type: "number" },

  ...getAddressFields("shipping", "Shipping"),
  ...getAddressFields("billing", "Billing"),
];
