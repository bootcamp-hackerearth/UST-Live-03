"use client";
 
import CommonAddPage from "@/app/components/CommonAddPage";
import api from "@/app/services/api";
 
// ✅ dropdown helper
const dropdown = (label, name, apiUrl, extra = {}) => ({
  label,
  name,
  type: "dropdown",
  api: apiUrl,
  payload: {
    page: 0,
    sizePerPage: 100,
    sortField: "identifier",
    sortDirection: "ASC",
  },
  optionLabel: "identifier",
  optionValue: "identifier",
  placeholder: `Select ${label}`,
  ...extra,
});
 
export default function PriceAddPage() {
  return (
    <CommonAddPage
      title="Add Price"
 
      submitApi={(data) => api.post("/api/price/add", data)}
 
      redirectRoute="/price/list"
 
      initialValues={{
        identifier: "",
        product: "",
        price: "",
        type: "",
        status: true,
      }}
 
      fields={[
        { label: "Identifier", name: "identifier", type: "text" },
 
        dropdown("Product", "product", "/api/product/list"),
 
        {
          label: "Price",
          name: "priceAmount",
          type: "number",
        },
 
        {
          label: "Type",
          name: "type",
          type: "dropdown",
          options: [
            { label: "MRP", value: "MRP" },
            { label: "SALE", value: "SALE" },
            { label: "COST", value: "COST" },
          ],
        },
      ]}
    />
  );
}