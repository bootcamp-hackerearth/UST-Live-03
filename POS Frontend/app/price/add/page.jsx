"use client";

import { useState } from "react";
import CommonAddForm from "@/components/CommonAddForm";
import SingleDropdown from "@/components/dropdowns/CommonSingleDropdown";

export default function AddPrice() {
  const [identifier, setIdentifier] = useState("");
  const extraFields = [
    {
      key: "identifier",
      type: "custom",
      label: "Product Identifier",
      required: true,
      component: (
        <SingleDropdown
          label="Product Identifier"
          apiUrl="/product/findByStatus"
          valueField="identifier"
          labelField="identifier"
          selectedValue={identifier}
          onChange={(val) => setIdentifier(val)}
        />
      ),
    },
    { key: "mrp",           label: "MRP",           type: "number", required: true },
    { key: "sellingPrice",  label: "Selling Price",  type: "number", required: true },
    { key: "costPrice",     label: "Cost Price",     type: "number", required: true },
    { key: "effectiveFrom", label: "Effective From", type: "date", required: true },
  ];

  return (
    <CommonAddForm
      title="Price"
      apiPath="price"
      apiEndpoint="add"
      showIdentifier={false}
      extraFields={extraFields}
      extraData={{ identifier }}
      successKeys={["id", "mrp"]}
    />
  );
}