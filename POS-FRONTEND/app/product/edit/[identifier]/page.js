"use client";

import React, { useState } from "react";
import PropTypes from "prop-types";
import EditFormSkeleton from "../../../components/EditFormSkeleton";
import SingleDropDown from "../../../components/SingleDropDown";
import MultiDropDown from "../../../components/MultiDropDown";

function BrandDropdown({ value, onChange }) {
  return (
    <SingleDropDown
      label="Brand"
      apiUrl="/brand/findByStatus"
      selectedValue={value}
      onChange={onChange}
    />
  );
}

BrandDropdown.propTypes = {
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
};

function UnitDropdown({ value, onChange }) {
  return (
    <SingleDropDown
      label="Unit"
      apiUrl="/unit/findByStatus"
      selectedValue={value}
      onChange={onChange}
    />
  );
}

UnitDropdown.propTypes = {
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
};

function ModelDropdown({ value, onChange }) {
  return (
    <SingleDropDown
      label="Model"
      apiUrl="/models/findByStatus"
      selectedValue={value}
      onChange={onChange}
    />
  );
}

ModelDropdown.propTypes = {
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
};

function CategoryDropdown({ value, onChange }) {
  const safeValues = Array.isArray(value) ? value : [];
  return (
    <MultiDropDown
      label="Category"
      apiUrl="/category/getBySuperCategoryNotNull"
      valueField="identifier"
      labelField="identifier"
      selectedValues={safeValues}
      onChange={onChange}
    />
  );
}

CategoryDropdown.propTypes = {
  value: PropTypes.arrayOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
  onChange: PropTypes.func.isRequired,
};

export default function EditProduct() {
  const [brand, setBrand] = useState("");
  const [unit, setUnit] = useState("");
  const [model, setModel] = useState("");
  const [category, setCategory] = useState([]);

  const extraFields = [
    {
      key: "name",
      label: "Product Name",
      type: "text",
    },
    {
      key: "brand",
      label: "Brand",
      type: "custom",
      CustomComponent: BrandDropdown,
    },
    {
      key: "unit",
      label: "Unit",
      type: "custom",
      CustomComponent: UnitDropdown,
    },
    {
      key: "model",
      label: "Model",
      type: "custom",
      CustomComponent: ModelDropdown,
    },
    {
      key: "category",
      label: "Category",
      type: "custom",
      CustomComponent: CategoryDropdown,
    },
  ];

  return (
    <EditFormSkeleton
      title="Product"
      apiPath="product"
      extraFields={extraFields}
      externalExtraData={{ brand, unit, model, category }}
      setters={{
        brand: setBrand,
        unit: setUnit,
        model: setModel,
        category: setCategory,
      }}
    />
  );
}