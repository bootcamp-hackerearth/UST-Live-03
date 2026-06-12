// components/useProductFields.jsx

"use client";
import { useState } from "react";
import SingleDropdown from "../components/dropDowns/singleDropDown";
import MultiDropDown from "../components/dropDowns/multiDropDown";

const PRODUCT_FIELDS_CONFIG = [
  { key: "name", type: "text", label: "Name" },
  { key: "categories", type: "custom" },
  { key: "brand", type: "custom" },
  { key: "model", type: "custom" },
];

export function useProductFields() {
  const [selectedName, setSelectedName] = useState("");
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [selectedBrand, setSelectedBrand] = useState("");
  const [selectedModel, setSelectedModel] = useState("");

  const productFields = PRODUCT_FIELDS_CONFIG.map((field) => {
    if (field.key === "categories") {
      return {
        ...field,
        component: (
          <MultiDropDown
            label="Categories"
            entity="category"
            selectedValues={selectedCategories}
            onChange={setSelectedCategories}
            valueField="identifier"
            labelField="identifier"
          />
        ),
      };
    }
    if (field.key === "brand") {
      return {
        ...field,
        component: (
          <SingleDropdown
            label="Brand"
            entity="brand"
            selectedValue={selectedBrand}
            onChange={setSelectedBrand}
            valueField="identifier"
            labelField="identifier"
          />
        ),
      };
    }
    if (field.key === "model") {
      return {
        ...field,
        component: (
          <SingleDropdown
            label="Model"
            entity="models"
            selectedValue={selectedModel}
            onChange={setSelectedModel}
            valueField="identifier"
            labelField="identifier"
          />
        ),
      };
    }
    return field;
  });

  const consolidatedDropdownData = {
    categories: selectedCategories,
    brand: selectedBrand,
    model: selectedModel,
  };

  const consolidatedEditData = {
    name: selectedName,
    categories: selectedCategories,
    brand: selectedBrand,
    model: selectedModel,
  };

  const functionalSetters = {
    name: setSelectedName,
    categories: setSelectedCategories,
    brand: setSelectedBrand,
    model: setSelectedModel,
  };

  return {
    productFields,
    consolidatedDropdownData,
    consolidatedEditData,
    functionalSetters,
  };
}