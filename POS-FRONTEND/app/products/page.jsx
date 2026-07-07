"use client";

import CommonList from "@/components/CommonList";
import { requiredValidation, nameValidation } from "@/validation/validation";

function ProductList() {
  return (
    <CommonList
      title="Products"
      subtitle="Manage your products"
      apiRoute="product"
      searchKeys={[
        "name",
        "identifier",
        "unit",
      ]}
      columns={[
        {
          label: "Identifier",
          key: "identifier",
        },
        {
          label: "Name",
          key: "name",
        },
        {
          label: "Unit",
          key: "unit",
        },
        {
          label: "Category",
          key: "category",
          render: (item) =>
            typeof item.category === "object"
              ? item.category?.identifier ||
              item.category?.name
              : item.category,
        },
        {
          label: "Brand",
          key: "brand",
          render: (item) =>
            typeof item.brand === "object"
              ? item.brand?.identifier ||
              item.brand?.name
              : item.brand,
        },
      ]}
      fields={[
        {
          name: "identifier",
          type: "text",
          placeholder: "Enter Product Identifier",
          validation: requiredValidation
        },
        {
          name: "name",
          type: "text",
          placeholder: "Enter Product Name",
          validation: nameValidation
        },
        {
          name: "unit",
          type: "text",
          placeholder: "Enter Unit",
          validation: requiredValidation
        },
        {
          name: "category",
          type: "select",
          placeholder: "Select Category",
          dataKey: "categories",
          hardCoded: "false",
          multiple: false,
          hardCodedArray: [],
          validation: requiredValidation,
          readOnly: false,
        },
        {
          name: "brand",
          type: "select",
          placeholder: "Select Brand",
          dataKey: "brands",
          hardCoded: "false",
          multiple: false,
          hardCodedArray: [],
          validation: requiredValidation,
          readOnly: false,
        },
      ]}
      dropdownApis={{
        categories: process.env.NEXT_PUBLIC_BASE_URL+"/category/list",
        brands: process.env.NEXT_PUBLIC_BASE_URL+"/brand/list",
      }}
    />
  );
}

export default ProductList;