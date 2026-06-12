"use client";
import { useState } from "react";
import AddFormSkeleton from "@/components/AddSkeleton";
import SingleDropdown from "@/components/dropdowns/SingleDropdown";

export default function AddPrice() {
  const [identifier, setIdentifier] = useState("");

  return (
    <AddFormSkeleton
      title="Price"
      apiPath="price"
      showIdentifier={false}
      extraData={{ identifier }}
      extraFields={[
        {
          key: "identifier",
          label: "Product",
          type: "custom",
          component: (
            <SingleDropdown
              label="Product"
              apiUrl="/product/findByStatus"
              valueField="identifier"
              labelField="identifier"
              selectedValue={identifier}
              onChange={(val) => setIdentifier(val)}
            />
          ),
        },
        { key: "mrp",          label: "MRP",            type: "number" },
        { key: "sellingPrice", label: "Selling Price",  type: "number" },
        { key: "costPrice",    label: "Cost Price",     type: "number" },
        { key: "effectiveFrom", label: "Effective From", type: "date"  },
      ]}
    />
  );
}