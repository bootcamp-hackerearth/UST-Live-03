"use client";

import { useState, useMemo } from "react";
import SingleDropdown from "@/components/dropdowns/CommonSingleDropdown";

export function useProductFields() {
  const [brand,    setBrand]    = useState("");
  const [unit,     setUnit]     = useState("");
  const [model,    setModel]    = useState("");
  const [category, setCategory] = useState("");

  const setCategoryFromValue = (val) => {
    setCategory(Array.isArray(val) ? (val[0] ?? "") : (val ?? ""));
  };

  const extraFields = useMemo(() => [
    {
      key: "name",
      label: "Product Name",
      type: "text",
      required: true,
    },
    {
      key: "brand",
      type: "custom",
      label: "Brand",
      component: (
        <SingleDropdown
          label="Brand"
          apiUrl="/brand/findByStatus"
          selectedValue={brand}
          onChange={(val) => setBrand(val)}
        />
      ),
    },
    {
      key: "unit",
      type: "custom",
      label: "Unit",
      component: (
        <SingleDropdown
          label="Unit"
          apiUrl="/unit/findByStatus"
          selectedValue={unit}
          onChange={(val) => setUnit(val)}
        />
      ),
    },
    {
      key: "model",
      type: "custom",
      label: "Model",
      component: (
        <SingleDropdown
          label="Model"
          apiUrl="/models/findByStatus"
          selectedValue={model}
          onChange={(val) => setModel(val)}
        />
      ),
    },
    {
      key: "category",
      type: "custom",
      label: "Category",
      component: (
        <SingleDropdown
          label="Category"
          apiUrl="/category/getBySuperCategoryNotNull"
          valueField="identifier"
          labelField="identifier"
          selectedValue={category}
          onChange={(val) => setCategory(val)}
        />
      ),
    },
  ], [brand, unit, model, category]);

  return {
    extraFields,
    extraData: { brand, unit, model, category: category ? [category] : [] },
    setters: { brand: setBrand, unit: setUnit, model: setModel, category: setCategoryFromValue },
  };
}