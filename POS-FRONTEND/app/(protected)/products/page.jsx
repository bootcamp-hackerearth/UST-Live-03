"use client";

import CommonList from "@/components/table/CommonList";

const productColumns = [

  {
    header: "Product Name",
    field: "identifier",
  },

  {
    header: "Brand",
    field: "brand",
  },

  {
    header: "Model",
    field: "model",
  },

  {
    header: "Unit",
    field: "unit",
  },

  {
    header: "Category",
    field: "category",
  },

];

export default function ProductsPage() {

  return (

    <CommonList
      title="Products"
      subtitle="Manage inventory products"
      entity="product"
      addPath="/products/add"
      editPath="/products/edit"
      columns={productColumns}
      showToggle={true}
    />
  );
}