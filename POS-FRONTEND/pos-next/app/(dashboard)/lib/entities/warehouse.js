import { buildEndpoints } from "./baseSchema";

export const warehouse = {
  ...buildEndpoints("/api/warehouse"),
  entityName: "Warehouse",

  columns: [
    { field: "serialNumber", label: "S.No" },
    { field: "identifier", label: "warehouse Identifier" },
    { field: "name", label: "Name" },
    { field: "phoneNo", label: "Phone Number" },
    { field: "address", label: "Address" },
    { field: "region", label: "Region" },
    { field: "country", label: "Country" },
  ],

  formFields: [
    { name: "identifier", label: "Identifier", type: "text", required: true, editable: false},
    { name: "name", label: "Name", type: "text", required: true },
    { name: "phoneNo", label: "Phone Number", type: "tel", required: true },
    { name: "address", label: "Address", type: "text", required: true },
    { name: "region", label: "Region", type: "text", required: true },
    { name: "country", label: "Country", type: "text", required: true },
  ],
};  