"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function PriceAdd() {
  const extraFields = [
    {
      key: "costprice",
      label: "Cost Price",
      type: "text",
      placeholder: "Enter cost price",
      required: true,
    },
    {
      key: "sellingprice",
      label: "Selling Price",
      type: "text",
      placeholder: "Enter selling price",
      required: true,
    },
    {
      key: "mrpprice",
      label: "MRP Price",
      type: "text",
      placeholder: "Enter MRP price",
      required: true,
    },
  ];

  return (
    <CommonAddTemplate
      title="Price"
      apiPath="price"
      identifierKey="identifier"
      identifierLabel="Identifier"
      identifierType="select"
      identifierApiPath="product"
      identifierApiEndpoint="findByStatus"
      identifierValueKey="identifier"
      identifierLabelKey="identifier"
      extraFields={extraFields}
      onSuccessPath="/price"
    />
  );
}