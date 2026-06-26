export const getCustomerFields = (isEdit = false) => [
  { name: "customerName", label: "Customer Name", type: "text" },
 { 
    name: "phoneNo", 
    label: "Phone Number", 
    type: "text", 
    readOnly: isEdit,
    validation: {
  pattern: /^\d{10}$/,
  message: "Must be exactly 10 digits."
}
  }, 
  { 
    name: "email", 
    label: "Email Address", 
    type: "text", 
    readOnly: isEdit,
    
validation: {
    validate: (value) => {
      const email = String(value || "").trim();
      const atIndex = email.indexOf("@");
      const dotIndex = email.lastIndexOf(".");

      return (
        atIndex > 0 &&
        dotIndex > atIndex + 1 &&
        dotIndex < email.length - 1 &&
        !email.includes(" ")
      );
    },
 message: "Invalid email format." }
  },

  {
  name: "partyType",
  label: "Party Type",
  type: "dropdown",
  optionLabel: "label",  
  optionValue: "value",   
  options: [
    { label: "Customer", value: "Customer" },
    { label: "Dealer", value: "Dealer" },
    { label: "Wholesaler", value: "Wholesaler" },
  ],
},
 
  { name: "creditLimit", label: "Credit Limit", type: "number" },

  { name: "billingAddress.addressLine", label: "Billing Address Line", type: "text" },
  { name: "billingAddress.city", label: "Billing City", type: "text" },
  { name: "billingAddress.state", label: "Billing State", type: "text" },
  { name: "billingAddress.zipCode", label: "Billing Zip Code", type: "text" },
  { name: "billingAddress.country", label: "Billing Country", type: "text" },
  { name: "sameAsBilling", label: "Same as Billing Address", type: "checkbox-action" },
  { name: "shippingAddress.addressLine", label: "Shipping Address Line", type: "text" },
  { name: "shippingAddress.city", label: "Shipping City", type: "text" },
  { name: "shippingAddress.state", label: "Shipping State", type: "text" },
  { name: "shippingAddress.zipCode", label: "Shipping Zip Code", type: "text" },
  { name: "shippingAddress.country", label: "Shipping Country", type: "text" },
];