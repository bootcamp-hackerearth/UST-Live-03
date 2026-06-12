"use client";

import { useState } from "react";
import AddFormSkeleton from "../../components/AddFormSkeleton";
import SingleDropDown from "../../components/SingleDropDown";
import MultiDropDown from "../../components/MultiDropDown";

export default function AddProduct() {
  const [brand, setBrand] = useState("");
  const [unit, setUnit] = useState("");
  const [model, setModel] = useState("");
  const [category, setCategory] = useState([]);

  const dropdownConfigs = [
    {
      key: "brand",
      label: "Brand",
      apiUrl: "/brand/findByStatus",
      value: brand,
      setter: setBrand,
      type: "single",
    },
    {
      key: "unit",
      label: "Unit",
      apiUrl: "/unit/findByStatus",
      value: unit,
      setter: setUnit,
      type: "single",
    },
    {
      key: "model",
      label: "Model",
      apiUrl: "/models/findByStatus",
      value: model,
      setter: setModel,
      type: "single",
    },
    {
      key: "category",
      label: "Category",
      apiUrl: "/category/getBySuperCategoryNotNull",
      value: category,
      setter: setCategory,
      type: "multi",
    },
  ];

  const extraFields = [
    { key: "name", label: "Product Name", type: "text", required: true },
    ...dropdownConfigs.map((config) => ({
      key: config.key,
      type: "custom",
      component:
        config.type === "single" ? (
          <SingleDropDown
            label={config.label}
            apiUrl={config.apiUrl}
            selectedValue={config.value}
            onChange={config.setter}
          />
        ) : (
          <MultiDropDown
            label={config.label}
            apiUrl={config.apiUrl}
            selectedValues={config.value}
            onChange={config.setter}
            valueField="identifier"
            labelField="identifier"
          />
        ),
    })),
  ];

  return (
    <AddFormSkeleton
      title="Product"
      apiPath="product"
      extraFields={extraFields}
      extraData={{ brand, unit, model, category }}
    />
  );
}