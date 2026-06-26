import AddEditForm from "@/components/AddEditForm";

export default function PriceUpdate() {

  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
  const fields = [

    {
      name: "priceAmount", type: "number", placeholder: "Price Amount", pattern: String.raw`\d+(\.\d{1,2})?`, patternMessage: "+ve Numbers only",
      required: true, readOnly: false
    },
    {
      name: "product", type: "select", placeholder: "Product", dataKey: "product",
      hardCoded: false, hardCodedArray: [], required: true, readOnly: true
    },
    {
      name: "priceType", type: "select", placeholder: "Price Type",
      hardCoded: true, hardCodedArray: ["MRP", "Selling Price"], required: true, readOnly: true
    }

  ];

  const dropdownApis = {
    product: `${baseUrl}/product/getactive`
  };

  return (

    <AddEditForm title="Price" dropdownApis={dropdownApis} apiRoute="price" fields={fields} method="update" />

  )
}