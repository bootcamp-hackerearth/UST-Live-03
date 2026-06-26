"use client";

import AddFormSkeleton from "@/components/AddSkeleton";
import SingleDropdown from "@/components/dropdowns/SingleDropdown";
import { useState } from "react";

export default function AddCategory() {

  const [superCategory, setSuperCategory] = useState("");

  const extraFields = [
    {
      key: "superCategory",
      type: "custom",
      label: "Super Category",
      optional: true,
      component: (
        <SingleDropdown
          label="Super Category"
          apiUrl="/category/getAllActiveCategories"
          valueField="identifier"
          labelField="identifier"
          selectedValue={superCategory}
          onChange={(val) => setSuperCategory(val)}
        />
      ),
    },
  ];

  return (
    <AddFormSkeleton
      title="Category"
      apiPath="category"
      identifierLabel="Category Name"
      extraFields={extraFields}
      extraData={{ superCategory }}
    />
  );
}