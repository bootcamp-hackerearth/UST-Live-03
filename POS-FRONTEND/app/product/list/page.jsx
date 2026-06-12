"use client";
import React from "react";
import CommonList from "../../Components/CommonList";
import Layout from "../../Components/Layout";
 
const ProductList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "SKU", field: "identifier" },
 
    {
      label: "category",
      render: (item) => {
        let categories = [];

        if (Array.isArray(item.category)) {
          categories = item.category;
        } else if (item.category) {
          categories = String(item.category).split(",").filter(Boolean);
        }

        return categories.map((c) => {
          const trimmedCategory = String(c).trim();
          const key = trimmedCategory || `category-${Math.random().toString(36).slice(2, 8)}`;

          return (
            <span
              key={key}
              className="bg-blue-100 text-blue-600 px-2 py-1 rounded text-xs mr-1"
            >
              {trimmedCategory}
            </span>
          );
        });
      },
    },
    
    { label: "Brand", field: "brand" },
    { label: "Model", field: "models" },
    { label: "Name", field: "productName" },
    { label: "Status", field: "status" },
  ];
 
  return (
    <Layout>
       <CommonList
      title="Product Management"
      columns={columns}
      urlName="product"
      showStatus={true} 
    />
    </Layout>
   
  );
};
export default ProductList;
