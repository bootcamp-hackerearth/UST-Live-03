// app/pos/categories/edit/[identifier]/page.jsx

"use client";
import BaseEditForm from "@/components/edit/BaseEditForm";
import SingleDropdown from "@/components/dropdowns/singleDropDown";
import { useState } from "react";

export default function EditCategoryPage() {
  const [superCategory, setSuperCategory] = useState("");

  const extraFields = [
    {
      key: "superCategory",
      label: "Parent Category",
      type: "custom",
      component: (
        <SingleDropdown
          label="Parent Category"
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
    <BaseEditForm
      title="Category"
      apiPath="category"
      extraFields={extraFields}
      extraData={{ superCategory: superCategory || null }}
      setters={{ superCategory: setSuperCategory }}
    />
  );
}