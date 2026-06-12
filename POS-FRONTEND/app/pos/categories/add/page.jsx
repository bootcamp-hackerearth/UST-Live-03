// app/pos/categories/add/page.jsx
"use client";

import BaseAddForm from "../../../../components/add/BaseAddForm";
import SingleDropdown from "../../../../components/dropDowns/singleDropDown.jsx";
import { useState } from "react";

export default function AddCategoryPage() {
  const [superCategory, setSuperCategory] = useState("");

  const extraFields = [
    {
      key: "superCategory",
      label: "Super Category",
      type: "custom",
      required: false,
      component: (
        <SingleDropdown
          label="Super Category"
          entity="category"
          selectedValue={superCategory}
          onChange={setSuperCategory}
          valueField="identifier"
          labelField="identifier"
        />
      ),
    },
  ];

  return (
    <BaseAddForm
      title="Category"
      apiPath="category"
      extraFields={extraFields}
      extraData={{ superCategory: superCategory || null }}
    />
  );
}