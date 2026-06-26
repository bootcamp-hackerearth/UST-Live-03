export const getWarehouseFields = (isUpdate = false) => [
    {
        name: "identifier",
        type: "text",
        placeholder: "Warehouse Name",
        required: true,
        readOnly: isUpdate
    },
    {
        name: "contactName",
        type: "text",
        placeholder: "Contact Name",
        required: true,
        readOnly: false
    },
    {
        name: "contactNumber",
        type: "text",
        placeholder: "Contact Number",
        patternMessage: "Phone number must be 10 digits",
        minlength: 10,
        pattern: "^[0-9]{10}$",
        required: true
    },
    {
        name: "location",
        type: "text",
        placeholder: "Location",
        required: true,
        readOnly: false
    },
    {
        name: "region",
        type: "text",
        placeholder: "Region",
        required: true,
        pattern: "^[a-zA-Z]{10}$",
        patternMessage: "Enter Valid Region",
        readOnly: false
    },
    {
        name: "country",
        type: "text",
        placeholder: "Country",
        required: true,
        pattern: "^[a-zA-Z]{10}$",
        patternMessage: "Enter Valid Country",
        readOnly: false
    }
];