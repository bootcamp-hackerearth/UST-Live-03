
export const productFields = [
  {
    key: "productName",
    label: "Product Name",
    type: "text",
    placeholder: "Enter product name",
    required: true,
  },
  {
    key: "brand",
    label: "Brand",
    type: "select",
    apiPath: "brand",
    required: true,
  },
  {
    key: "models",
    label: "Model",
    type: "select",
    apiPath: "model",
    required: true,
  },
  {
    key: "category",
    label: "Category",
    type: "multiselect",
    apiPath: "category",
    valueFormat: "csv",
    required: true,
  },
  {
    key: "status",
    label: "Status",
    type: "select",
    valueType: "boolean",
    required: true,
    options: [
      { label: "Active", value: "true" },
      { label: "Inactive", value: "false" },
    ],
  },
];

export const warehouseFields = [
  {
    key: "location",
    label: "Location Address / Name",
    type: "text",
    placeholder: "e.g. Industrial Area, Sector 5",
    required: true,
  },
  {
    key: "manager",
    label: "Manager In-Charge",
    type: "text",
    placeholder: "Enter manager's full name",
    required: true,
  },
  
  {
    key: "status",
    label: "Status",
    type: "select",
    valueType: "boolean",
    required: true,
    options: [
      { label: "Active", value: "true" },
      { label: "Inactive", value: "false" },
    ],
  },
];

export const customerFields = [
  {
    key: "customerName",
    label: "Customer Name",
    type: "text",
    placeholder: "John Doe",
    required: true,
  },
  {
    key: "identifier",
    label: "Phone Number (Identifier)",
    type: "text",
    placeholder: "+1 (555) 000-0000",
    required: true,
  },
  {
    key: "partyType",
    label: "Party Type",
    type: "select",
    required: true,
    options: [
      { label: "Customer", value: "Customer" },
      { label: "Dealer", value: "Dealer" },
      { label: "Wholesaler", value: "Wholesaler" },
    ],
  },
  {
    key: "balance",
    label: "Balance",
    type: "number",
    placeholder: "0.00",
  },
  {
    key: "balanceType",
    label: "Balance Type",
    type: "select",
    options: [
      { label: "Due", value: "Due" },
      { label: "Advance", value: "Advance" },
    ],
  },
  {
    key: "username",
    label: "Email Address",
    type: "email",
    placeholder: "email@example.com",
  },
  {
    key: "creditLimit",
    label: "Credit Limit",
    type: "number",
    placeholder: "5000",
  },
  {
    key: "address",
    label: "Primary Address",
    type: "text",
    placeholder: "123 Main St, Suite 400",
  },
  {
    key: "billingAddress.addressLine",
    label: "Billing Address Line",
    type: "text",
  },
  {
    key: "billingAddress.city",
    label: "Billing City",
    type: "text",
  },
  {
    key: "billingAddress.state",
    label: "Billing State",
    type: "text",
  },
  {
    key: "billingAddress.zipcode",
    label: "Billing Zip Code",
    type: "text",
  },
  {
    key: "billingAddress.country",
    label: "Billing Country",
    type: "text",
  },
  {
    key: "shippingAddress.addressLine",
    label: "Shipping Address Line",
    type: "text",
  },
  {
    key: "shippingAddress.city",
    label: "Shipping City",
    type: "text",
  },
  {
    key: "shippingAddress.state",
    label: "Shipping State",
    type: "text",
  },
  {
    key: "shippingAddress.zipcode",
    label: "Shipping Zip Code",
    type: "text",
  },
  {
    key: "shippingAddress.country",
    label: "Shipping Country",
    type: "text",
  },
  {
    key: "status",
    label: "Status",                                        
    type: "select",
    valueType: "boolean",
    options: [
      { label: "Active", value: "true" },
      { label: "Inactive", value: "false" },
    ],
  },
];