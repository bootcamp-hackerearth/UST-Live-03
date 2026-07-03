'use client';

import Brand from "../../components/dropdown/Brand";
import Category from "../../components/dropdown/Category";
import Model from "../../components/dropdown/Model";

import ListPage from "../../components/common/ListPage";
import Sidebar from "../../components/layout/Sidebar";

const Products = () => {
  const keys = [
    "id",
    "identifier",
    "productName",
    "brand",
    "category",
    "model",
    "description",
  ];

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
    },
    {
      name: "productName",
      label: "Product Name",
    },
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
    {
      name: "description",
      label: "Description",
    },
  ];

  return (
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        modelName="product"
      />
    </Sidebar>
  );
};

export default Products;