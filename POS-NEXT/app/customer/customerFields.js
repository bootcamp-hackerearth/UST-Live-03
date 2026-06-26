export const getCustomerFields = (isUpdate = false) => [
    {
        name: "name",
        type: "text",
        placeholder: "Name",
        minlength: 3,
        patternMessage: "Name must be at least 3 characters",
        dataKey: "name",
        required: true,
        readOnly: false
    },
    {
        name: "identifier",
        type: "email",
        placeholder: "Email",
        pattern: String.raw`^[^\s@]+@(ust\.com|gmail\.com)$`,
        patternMessage: "Please enter a valid UST email address",
        dataKey: "email",
        required: true,
        readOnly: isUpdate
    },
    {
        name: "phoneNo",
        type: "text",
        placeholder: "Phone Number",
        patternMessage: "Phone number must be 10 digits",
        minlength: 10,
        pattern: "^[0-9]{10}$",
        dataKey: "phoneNo",
        required: true,
        readOnly: isUpdate
    },
    {
        name: "balance",
        type: "text",
        placeholder: "Balance",
        patternMessage: "Balance must be digits",
        minlength: 1,
        pattern: "^[0-9]+$",
        dataKey: "balance",
        required: true,
        readOnly: false
    },
    {
        name: "creditLimit",
        type: "text",
        placeholder: "Credit Limit",
        patternMessage: "Credit must be digits",
        minlength: 1,
        pattern: "^[0-9]+$",
        dataKey: "creditLimit",
        required: true,
        readOnly: false
    },
    {
        name: "userType",
        type: "select",
        placeholder: "Select User-Type",
        hardCoded: true,
        hardCodedArray: ["Wholesaler", "Dealer", "Customer"],
        required: true,
        readOnly: false
    },

    {
        type: "section",
        title: "Shipping Address"
    },
    {
        name: "shippingAddress.addressLine",
        type: "text",
        placeholder: "Shipping Address Line",
        required: true,
        readOnly: false
    },
    {
        name: "shippingAddress.city",
        type: "text",
        placeholder: "Shipping City",
        required: true,
        readOnly: false
    },
    {
        name: "shippingAddress.state",
        type: "text",
        placeholder: "Shipping State",
        required: true,
        readOnly: false
    },
    {
        name: "shippingAddress.zipcode",
        type: "number",
        maxlength: 10,
        placeholder: "Shipping ZipCode",
        required: true,
        readOnly: false
    },
    {
        name: "shippingAddress.country",
        type: "text",
        placeholder: "Shipping Country",
        required: true,
        readOnly: false
    },

    {
        type: "section",
        title: "Billing Address"
    },
    {
        name: "billingAddress.addressLine",
        type: "text",
        placeholder: "Billing Address Line",
        required: true,
        readOnly: false
    },
    {
        name: "billingAddress.city",
        type: "text",
        placeholder: "Billing City",
        required: true,
        readOnly: false
    },
    {
        name: "billingAddress.state",
        type: "text",
        placeholder: "Billing State",
        required: true,
        readOnly: false
    },
    {
        name: "billingAddress.zipcode",
        type: "number",
        maxlength: 10,
        placeholder: "Billing ZipCode",
        required: true,
        readOnly: false
    },
    {
        name: "billingAddress.country",
        type: "text",
        placeholder: "Billing Country",
        required: true,
        readOnly: false
    }
];