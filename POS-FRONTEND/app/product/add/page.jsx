'use client';

import Category from "../../../components/dropdown/Category";
import Brand from "../../../components/dropdown/Brand";
import Model from "../../../components/dropdown/Model";

import AddPage from "../../../components/common/AddPage";

const ProductAdd = () => {
  const fields = [
    { name: "identifier", type: "text", label: "Identifier" },
    { name: "productName", type: "text", label: "Name" },
    { name: "description", type: "textarea", label: "Description" },

    {
      name: "brand",
      label: "Brand",
      component: Brand,
    },
    {
      name: "category",
      label: "Category",
      component: Category,
    },
    {
      name: "model",
      label: "Model",
      component: Model,
    },
  ];

  return (
    <AddPage
      fields={fields}
      modelName="product"
    />
  );
};

export default ProductAdd;