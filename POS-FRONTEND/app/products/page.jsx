"use client";

import CommonList from "@/components/CommonList";

function ProductList() {
  return (
    <CommonList
      title="Products"
      subtitle="Manage your products"
      apiUrl="http://localhost:8080/api/product/list"
      deleteUrl="http://localhost:8080/api/product/delete"
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
          required: true,
        },
        {
          name: "name",
          type: "text",
          placeholder: "Enter Product Name",
          required: true,
        },
        {
          name: "unit",
          type: "text",
          placeholder: "Enter Unit",
          required: true,
        },
        {
          name: "category",
          type: "select",
          placeholder: "Select Category",
          dataKey: "categories",
          hardCoded: "false",
          multiple: false,
          hardCodedArray: [],
          required: true,
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
          required: true,
          readOnly: false,
        },
      ]}
      dropdownApis={{
        categories: "http://localhost:8080/api/category/list",
        brands: "http://localhost:8080/api/brand/list",
      }}
    />
  );
}

export default ProductList;