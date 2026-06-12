
import AddFormSkeleton from "@/app/components/AddFormSkeleton";

export default function AddPrice() {
  const fields = [
    {
      name: "productIdentifier", 
      label: "Product Identifier", 
      type: "select",
      api: "product", 
      optionLabel: "identifier",
      optionValue: "identifier",
      required: true,
      unique: true, 
    },
    {
      name: "effectiveFrom",
      label: "Effective From",
      type: "date", 
      required: true,
    },
    {
      name: "mrp",
      label: "MRP",
      type: "number",
      required: true,
      placeholder: "Enter Max Retail Price...",
    },
    {
      name: "sellingPrice",
      label: "Selling Price",
      type: "number",
      required: true,
      placeholder: "Enter Selling Price...",
    },
    {
      name: "costPrice",
      label: "Cost Price",
      type: "number",
      required: true,
      placeholder: "Enter Cost Price...",
    },
  ];

  return (
    <AddFormSkeleton
      title="Price"
      apiPath="price" 
      fields={fields}
    />
  );
}