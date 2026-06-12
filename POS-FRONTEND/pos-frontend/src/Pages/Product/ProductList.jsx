import React from "react";
import DynamicList from "../../components/common/DynamicList";
import POSLayout from "../../components/POSLayout";

function Products() {

  const columns = [
    {
      key: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      key: "name",
      label: "Product",
      type: "text",
    },
    {
      key: "categories",
      label: "Categories",
      type: "list",
      displayKey: "identifier",
    },
    {
      key: "brand",
      label: "Brand",
      type: "text",
    },
    {
      key: "model",
      label: "Model",
      type: "text",
    },
    {
      key: "unit",
      label: "Unit",
      type: "text",
    },
    {
      key: "status",
      label: "Status",
      type: "toggle",
    },
  ];

  return (
    <POSLayout>
      <DynamicList
        title="Product List"
        routeName="product"
        columns={columns}
        editUrl="/products/edit"
        addUrl="/add-product"
      />
    </POSLayout>
  );
}

export default Products;