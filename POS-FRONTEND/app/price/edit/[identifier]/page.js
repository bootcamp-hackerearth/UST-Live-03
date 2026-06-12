import EditFormSkeleton from "@/app/components/EditFormSkeleton";

export default function EditPrice() {
  const fields = [
    {
      name: "productIdentifier",
      label: "Product Identifier",
      type: "select",
      api: "product",
      optionLabel: "identifier",
      optionValue: "identifier",
    },

    {
      name: "effectiveFrom",
      label: "Effective From",
      type: "date",
    },

    {
      name: "mrp",
      label: "MRP",
      type: "number",
    },

    {
      name: "sellingPrice",
      label: "Selling Price",
      type: "number",
    },

    {
      name: "costPrice",
      label: "Cost Price",
      type: "number",
    },
  ];

  return (
    <EditFormSkeleton
      title="Price"
      apiPath="price"
      fields={fields}
    />
  );
}