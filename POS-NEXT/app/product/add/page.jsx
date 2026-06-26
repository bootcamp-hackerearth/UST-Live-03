import AddEditForm from "@/components/AddEditForm";

export default function ProductAdd() {

  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "http://localhost:8080/api";
  const fields = [
    {
      name: "identifier", type: "text", placeholder: "Identifier",
      pattern: String.raw`^P\d{3,}$`, patternMessage: "Identifier must start with 'P' followed by at least 3 digits", required: true, readOnly: false
    },
    {
      name: "name", type: "text", placeholder: "name", required: true,
      readOnly: false
    },
    {
      name: "description", type: "text", placeholder: "Description", required: true,
      readOnly: false
    },
    {
      name: "category", type: "select", placeholder: "category", dataKey: "category",
      hardCoded: false, hardCodedArray: [], required: true, readOnly: false
    },
    {
      name: "brand", type: "select", placeholder: "brand", dataKey: "brand", required: true,
      hardCoded: false, hardCodedArray: [], readOnly: false
    },
    {
      name: "unit", type: "select", placeholder: "unit", dataKey: "unit", required: true,
      hardCoded: false, hardCodedArray: [], readOnly: false
    },
    {
      name: "model", type: "select", placeholder: "model", dataKey: "model", required: true,
      hardCoded: false, hardCodedArray: [], readOnly: false
    }
  ];

  const dropdownApis = {
    unit: `${baseUrl}/unit/getactive`,
    brand: `${baseUrl}/brand/getactive`,
    model: `${baseUrl}/models/getactive`,
    category: `${baseUrl}/category/getCategoriesWithoutParent`

  };
  return (

    <AddEditForm title="Product" fields={fields} apiRoute="product" dropdownApis={dropdownApis} method="add" />

  )
}