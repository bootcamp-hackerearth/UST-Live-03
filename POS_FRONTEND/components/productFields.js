const PRODUCT_FIELDS = [
    {
        name: "identifier",
        label: "Identifier",
        type: "text",
        required: true,
    },
    {
        name: "name",
        label: "Product Name",
        type: "text",
        required: true,
    },
    {
        name: "brand",
        label: "Brand",
        type: "select",
        multiple: false,
        required: true,
        api: "/brand/list",
    },
    {
        name: "model",
        label: "Model",
        type: "select",
        multiple: false,
        required: true,
        api: "/model/list",
    },
    {
        name: "unit",
        label: "Unit",
        type: "select",
        multiple: false,
        required: true,
        api: "/unit/list",
    },
    {
        name: "price",
        label: "Price",
        type: "text",
        required: true,
    },
    {
        name: "category",
        label: "Category",
        type: "select",
        multiple: false,
        required: true,
        api: "/category/list",
    },
];

export default PRODUCT_FIELDS;
