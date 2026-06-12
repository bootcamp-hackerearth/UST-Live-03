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
          apiPath="category"
          required
          onChange={(val) => setCategories(val)}
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
          apiPath="unit"
          required
          onChange={(val) => setUnit(val)}
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
          apiPath="brand"
          required
          onChange={(val) => setBrand(val)}
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
          apiPath="modelProduct"
          required
          onChange={(val) => setModel(val)}
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
