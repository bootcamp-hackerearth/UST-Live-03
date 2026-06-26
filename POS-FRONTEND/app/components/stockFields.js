const commonStockFields = [
    {
        key: "productname",
        label: "Product",
        type: "select",
        apiEndpoint: "/product/findByStatus",
        labelKey: "productname",
        valueKey: "productname",
        required: true,
    },
    {
        key: "warehouse",
        label: "Warehouse",
        type: "select",
        apiEndpoint: "/warehouse/findByStatus",
        labelKey: "identifier",
        valueKey: "identifier",
        required: true,
    },
    {
        key: "quantity",
        label: "Quantity",
        type: "number",
        required: true,
    },
    {
        key: "status",
        label: "Status",
        type: "select",
        options: [
            { value: true, label: "Active" },
            { value: false, label: "Inactive" },
        ],
        valueType: "boolean",
        required: true,
    },
];

export const stockExtraFieldsAdd = [...commonStockFields];

export const stockExtraFieldsEdit = [...commonStockFields];