"use client";

import React from "react";
import CommonList from "@/app/components/CommonList" 

const ProductList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "SKU", field: "identifier" },

    {
      label: "Categories",
      render: (item) =>
        item.categories?.map((c) => (
          <span
            key={c}
            className="bg-blue-100 text-blue-600 px-2 py-1 rounded text-xs mr-1"
          >
            {c}
          </span>
        )),
    },

    { label: "Brand", field: "brand" },
    { label: "Unit", field: "unit" },
    { label: "Model", field: "model" },
    { label: "Name", field: "name" },

    { label: "Status", field: "status" },
  ];

  return (
    <CommonList
      title="Product Management"
      columns={columns}
      urlName="product"
      showStatus={true}
    />
  );
};

export default ProductList;