export const PARTY_TYPE_OPTIONS = [
  { identifier: "CUSTOMER" },
  { identifier: "WHOLESALER" },
  { identifier: "DEALER" },
];

export const BILLING_FIELDS = [
  { label: "Billing Address", name: "bill_address", type: "text" },
  { label: "Billing City", name: "bill_city", type: "text" },
  { label: "Billing State", name: "bill_state", type: "text" },
  { label: "Billing Zip", name: "bill_zip", type: "text" },
  { label: "Billing Country", name: "bill_country", type: "text" },
];

export const SHIPPING_FIELDS = [
  { label: "Shipping City", name: "ship_city", type: "text" },
  { label: "Shipping State", name: "ship_state", type: "text" },
  { label: "Shipping Zip", name: "ship_zip", type: "text" },
  { label: "Shipping Country", name: "ship_country", type: "text" },
];