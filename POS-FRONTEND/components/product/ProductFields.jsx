"use client";

import PropTypes from "prop-types";
import SingleDropDown from "@/components/common/SingleDropDown";
import MultiCheckBox from "@/components/common/MultiCheckBox";

export default function ProductFields({
  brand,
  setBrand,
  model,
  setModel,
  unit,
  setUnit,
  category,
  setCategory,
}) {
  return (
    <>
      <SingleDropDown
        label="Brand"
        model="brand"
        value={brand}
        onChange={setBrand}
        placeholder="Select Brand"
        required
      />

      <SingleDropDown
        label="Model"
        model="model"
        value={model}
        onChange={setModel}
        placeholder="Select Model"
        required
      />

      <SingleDropDown
        label="Unit"
        model="unit"
        value={unit}
        onChange={setUnit}
        placeholder="Select Unit"
        required
      />

      <MultiCheckBox
        label="Category"
        model="category"
        values={category}
        onChange={setCategory}
        required
      />
    </>
  );
}

ProductFields.propTypes = {
  brand: PropTypes.string,
  setBrand: PropTypes.func.isRequired,
  model: PropTypes.string,
  setModel: PropTypes.func.isRequired,
  unit: PropTypes.string,
  setUnit: PropTypes.func.isRequired,
  category: PropTypes.array,
  setCategory: PropTypes.func.isRequired,
};