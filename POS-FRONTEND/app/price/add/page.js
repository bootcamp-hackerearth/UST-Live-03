"use client";

import { useState } from "react";
import PropTypes from "prop-types";
import AddFormSkeleton from "../../components/AddFormSkeleton";
import SingleDropDown from "../../components/SingleDropDown";

function ProductDropdownWrapper({ identifier, setIdentifier }) {
  return (
    <SingleDropDown
      label="Product"
      apiUrl="/product/findByStatus"
      valueField="identifier"
      labelField="identifier"
      selectedValue={identifier}
      onChange={setIdentifier}
    />
  );
}

ProductDropdownWrapper.propTypes = {
  identifier: PropTypes.string.isRequired,
  setIdentifier: PropTypes.func.isRequired,
};

export default function AddPrice() {
  const [identifier, setIdentifier] = useState("");

  const priceFields = [
    {
      key: "identifier",
      type: "custom",
      label: "Product",
      component: (
        <ProductDropdownWrapper
          identifier={identifier}
          setIdentifier={setIdentifier}
        />
      ),
    },
    {
      key: "mrp",
      label: "MRP",
      type: "number",
    },
    {
      key: "sellingPrice",
      label: "Selling Price",
      type: "number",
    },
    {
      key: "costPrice",
      label: "Cost Price",
      type: "number",
    },
    {
      key: "effectiveFrom",
      label: "Effective From",
      type: "date",
    },
  ];

  return (
    <AddFormSkeleton
      title="Price"
      apiPath="price"
      extraFields={priceFields}
      extraData={{
        identifier,
      }}
    />
  );
}