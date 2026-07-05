import AddEditForm from "@/components/AddEditForm";

export default function StockAdd() {

  const baseUrl = process.env.NEXT_PUBLIC_BASE_URL || "/api";
  const fields = [

    {
      name: "product", type: "select", placeholder: "Select Product", dataKey: "product",
      hardCoded: false, hardCodedArray: [], required: true, readOnly: false
    },
    {
      name: "quantity", type: "number", placeholder: "Quantity", required: true,
      readOnly: false
    },
    {
      name: "warehouse", type: "select", placeholder: "Select Warehouse", dataKey: "warehouse", required: true,
      hardCoded: false, hardCodedArray: [], readOnly: false
    }
  ];

  const dropdownApis = {
    product: `${baseUrl}/product/getactive`,
    warehouse: `${baseUrl}/warehouse/getactive`,
  };
  return (

    <AddEditForm title="Stock" fields={fields} apiRoute="stock" dropdownApis={dropdownApis} method="add" />

  )
}