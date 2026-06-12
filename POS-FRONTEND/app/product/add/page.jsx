"use client";
import CommonAddTemplate from "../../components/CommonAddTemplate";

const SELECT_FIELDS = [
  { key: "brand",    label: "Brand",    apiEndpoint: "/brand/findByStatus" },
  { key: "model",    label: "Model",    apiEndpoint: "/model/findByStatus" },
  { key: "category", label: "Category", apiEndpoint: "/category/subcategory" },
  { key: "unit",     label: "Unit",     apiEndpoint: "/unit/findByStatus" },
];

const extraFields = [
  {
    key: "productname",
    label: "Product Name",
    type: "text",
    placeholder: "Enter product name",
    required: true,
  },
  ...SELECT_FIELDS.map(({ key, label, apiEndpoint }) => ({
    key,
    label,
    type: "select",
    apiEndpoint,
    required: true,
  })),
];

export default function ProductAdd() {
  return (
    <CommonAddTemplate
      title="Product"
      apiPath="product"
      extraFields={extraFields}
      onSuccessPath="/product"
    />
  );
}