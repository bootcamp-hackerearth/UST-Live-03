"use client";

import React from "react";
import Add from "../../../components/add";

function PriceAdd() {
  const extraFields = [
    {
      key: "costPrice",
      label: "Cost Price",
      type: "number",
      required: true,
    },
    {
      key: "mrp",
      label: "MRP",
      type: "number",
      required: true,
    },
    {
      key: "sellingPrice",
      label: "Selling Price",
      type: "number",
      required: true,
    },
  ];

  return (
    <Add
      title="Price"
      apiPath="price"
      showDescription={false}
      identifierDropdownApi="product/getAllActive"
      extraFields={extraFields}
      urlMethod={"get"}
    />
  );
}

export default PriceAdd;
