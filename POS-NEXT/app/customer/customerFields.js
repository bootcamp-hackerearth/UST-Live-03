const getAddressFields = (prefix, label) => [
    {
        type: "section",
        title: `${label} Address`
    },
    {
        name: `${prefix}.addressLine`,
        type: "text",
        placeholder: `${label} Address Line`,
        required: true,
        readOnly: false
    },
    {
        name: `${prefix}.city`,
        type: "text",
        placeholder: `${label} City`,
        required: true,
        readOnly: false
    },
    {
        name: `${prefix}.state`,
        type: "text",
        placeholder: `${label} State`,
        required: true,
        readOnly: false
    },
    {
        name: `${prefix}.zipcode`,
        type: "number",
        maxlength: 10,
        placeholder: `${label} ZipCode`,
        required: true,
        readOnly: false
    },
    {
        name: `${prefix}.country`,
        type: "text",
        placeholder: `${label} Country`,
        required: true,
        readOnly: false
    }
];

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

    ...getAddressFields("shippingAddress", "Shipping"),
    ...getAddressFields("billingAddress", "Billing")
];