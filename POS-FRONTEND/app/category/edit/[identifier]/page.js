"use client";

import React, { useState } from "react";
import PropTypes from "prop-types";
import EditFormSkeleton from "../../../components/EditFormSkeleton";
import SingleDropDown from "../../../components/SingleDropDown";

function SuperCategoryDropdown({ value, onChange }) {
  return (
    <SingleDropDown
      label="Super Category"
      apiUrl="/category/findByStatus"
      selectedValue={value}
      onChange={onChange}
      valueField="identifier"
      labelField="identifier"
    />
  );
}

SuperCategoryDropdown.propTypes = {
  value: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
  onChange: PropTypes.func.isRequired,
};

export default function EditCategoryPage() {
  const [superCategory, setSuperCategory] = useState("");

  return (
    <EditFormSkeleton
      title="Category"
      apiPath="category"
      externalExtraData={{ superCategory }}
      extraFields={[
        {
          key: "superCategory",
          label: "Super Category",
          type: "custom",
          optional: true,
          CustomComponent: SuperCategoryDropdown,
        },
      ]}
      setters={{ superCategory: setSuperCategory }}
    />
  );
}