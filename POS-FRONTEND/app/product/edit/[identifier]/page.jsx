"use client";
import { useParams } from "next/navigation";
import { useState } from "react";
import PropTypes from "prop-types";
import Update from "../../../../components/edit";
import SingleDropdown from "../../../../components/SingleDropdown";

function CategoryField({ value, onChange }) {
  return (
    <SingleDropdown
      value={value}
      label="Category"
      apiPath="category"
      required
      onChange={onChange}
    />
  );
}

function UnitField({ value, onChange }) {
  return (
    <SingleDropdown
      value={value}
      label="Unit"
      apiPath="unit"
      required
      onChange={onChange}
    />
  );
}

function BrandField({ value, onChange }) {
  return (
    <SingleDropdown
      value={value}
      label="Brand"
      apiPath="brand"
      required
      onChange={onChange}
    />
  );
}

function ModelField({ value, onChange }) {
  return (
    <SingleDropdown
      value={value}
      label="Model"
      apiPath="modelProduct"
      required
      onChange={onChange}
    />
  );
}

CategoryField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

UnitField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

BrandField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

ModelField.propTypes = {
  value: PropTypes.string,
  onChange: PropTypes.func.isRequired,
};

export default function EditProduct() {
  const params = useParams();
  const [category, setCategory] = useState("");
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
      onLoad: (val) => setCategory(val),
      component: (
        <CategoryField value={category} onChange={(val) => setCategory(val)} />
      ),
    },
    {
      key: "unit",
      type: "custom",
      onLoad: (val) => setUnit(val),
      component: <UnitField value={unit} onChange={(val) => setUnit(val)} />,
    },
    {
      key: "brand",
      type: "custom",
      onLoad: (val) => setBrand(val),
      component: <BrandField value={brand} onChange={(val) => setBrand(val)} />,
    },
    {
      key: "model",
      type: "custom",
      onLoad: (val) => setModel(val),
      component: <ModelField value={model} onChange={(val) => setModel(val)} />,
    },
  ];

  return (
    <Update
      identifier={params.identifier}
      apiPath="product"
      title="Product"
      extraFields={extraFields}
      extraData={{ category, unit, brand, model }}
    />
  );
}
