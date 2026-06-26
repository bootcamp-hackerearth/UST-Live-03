"use client";

import CommonList from "@/components/CommonList";
import { requiredValidation } from "@/validation/validation";

export default function PriceList() {

  const fields = [

    {
      name: "product",
      type: "select",
      placeholder: "Select Product",
      dataKey: "products",
      hardCoded: "false",
      multiple: false,
      hardCodedArray: [],
      readOnly: false,
      validation: requiredValidation
    },

    {
      name: "priceAmount",
      type: "number",
      placeholder: "Enter Price Amount",
      hardCoded: "false",
      hardCodedArray: [],
      validation: requiredValidation,
      readOnly: false,
    },

    {
      name: "priceType",
      type: "select",
      placeholder: "Select Price Type",
      hardCoded: "true",
      multiple: false,
      hardCodedArray: [
        "Cost Price",
        "Selling price",
        "MRP",
      ],
      validation: requiredValidation,
      readOnly: false,
    },

  ];

  const dropdownApis = {

    products:
      "http://localhost:8080/api/product/list",

  };

  const columns = [

    {
      key: "id",
      label: "ID",
    },

    {
      key: "identifier",
      label: "Identifier",
    },

    {
      key: "priceAmount",
      label: "Price Amount",
    },

    {
      key: "priceType",
      label: "Price Type",
    },

    {
      key: "product",
      label: "Product",

    },

  ];

  return (

    <CommonList
      title="Prices"
      subtitle="Manage product prices"
      apiUrl="http://localhost:8080/api/price/list"
      deleteUrl="http://localhost:8080/api/price/delete"
      apiRoute="price"
      columns={columns}
      searchKeys={[
        "identifier",
        "priceAmount",
        "priceType",
      ]}
      fields={fields}
      dropdownApis={dropdownApis}
    />

  );
}