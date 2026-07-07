"use client";

import React, { useState } from "react";
import Add from "../../../components/add";
import MultiDropdown from "../../../components/MultiDropdown";
import SingleDropdown from "../../../components/SingleDropdown";

function ProductAdd() {
  const [categories, setCategories] = useState([]);
  const [unit, setUnit] = useState("");
  const [brand, setBrand] = useState("");
  const [model, setModel] = useState("");

  const extraFields = [
    {
      key: "productName",
      label: "Product Name",
      type: "text",
      required: true,
    },
    {
      key: "category",
      type: "custom",
      component: (
        <MultiDropdown
          value={categories}
          label="Category"
          apiPath="category/list"
          required
          onChange={(val) => setCategories(val)}
          urlMethod={"post"}
        />
      ),
    },
    {
      key: "unit",
      type: "custom",
      component: (
        <SingleDropdown
          value={unit}
          label="Unit"
          apiPath="unit/getAllActive"
          required
          onChange={(val) => setUnit(val)}
          urlMethod={"get"}
        />
      ),
    },
    {
      key: "brand",
      type: "custom",
      component: (
        <SingleDropdown
          value={brand}
          label="Brand"
          apiPath="brand/getAllActive"
          required
          onChange={(val) => setBrand(val)}
          urlMethod={"get"}
        />
      ),
    },
    {
      key: "model",
      type: "custom",
      component: (
        <SingleDropdown
          value={model}
          label="Model"
          apiPath="modelProduct/getAllActive"
          required
          onChange={(val) => setModel(val)}
          urlMethod={"get"}
        />
      ),
    },
  ];

  return (
    <Add
      title="Product"
      apiPath="product"
      extraFields={extraFields}
      extraData={{
        unit,
        brand,
        model,
        category: categories.join(", "),
      }}
    />
  );
}
export default ProductAdd;
