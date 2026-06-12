"use client";
 
import CommonEditPage from "@/app/components/CommonEditPage";
import api from "@/app/services/api";
 
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
 
export default function PriceEditPage() {
  return (
    <CommonEditPage
      title="Edit Price"
 
      // ✅ FETCH EXISTING PRICE
      fetchApi={async (identifier) => {
        const res = await api.get("/api/price/get", {
          params: { identifier },
        });
        return res;
      }}
 
      // ✅ UPDATE PRICE
      updateApi={async (data) => {
        return await api.post("/api/price/update", data);
      }}
 
      // ✅ REDIRECT
      redirectRoute="/price/list"
 
      // ✅ PARAM
      identifierParam="identifier"
 
      fields={[
        // ✅ IDENTIFIER (same as Node)
        {
          label: "Identifier",
          name: "identifier",
          type: "text",
          readOnly: true,
        },
 
        // ✅ PRODUCT (same style as Node roles dropdown)
        {
          label: "Product",
          name: "product",
          type: "dropdown",
          api: "/api/product/list",
          payload: {
            page: 0,
            sizePerPage: 100,
            sortField: "identifier",
            sortDirection: "ASC",
          },
          optionLabel: "identifier",
          optionValue: "identifier",
          placeholder: "Select Product",
        },
 
        // ✅ PRICE FIELD (like Node path field)
        {
          label: "Price",
          name: "priceAmount",
          type: "number",
        },
 
        // ✅ TYPE (STATIC DROPDOWN — same structure as roles but without API)
        {
          label: "Type",
          name: "type",
          type: "dropdown",
          options: [
            { label: "MRP", value: "MRP" },
            { label: "Selling Price", value: "SELLING" },
            { label: "Cost Price", value: "COST" },
          ],
          optionLabel: "label",
          optionValue: "value",
          placeholder: "Select Type",
        },
      ]}
    />
  );
}
 