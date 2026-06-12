"use client";
import { useState } from "react";
import AddFormSkeleton from "../../components/AddFormSkeleton";
import SingleDropDown from "../../components/SingleDropDown";

export default function AddCategoryPage() {
  const [superCategory, setSuperCategory] = useState("");

  return (
    <AddFormSkeleton
      title="Category"
      apiPath="category"
      extraData={{ superCategory }}
      extraFields={[
        {
          key: "superCategory",
          label: "Super Category",
          type: "custom",
          optional: true,
          component: (
            <SingleDropDown
              label="Super Category"
              apiUrl="/category/findByStatus"
              selectedValue={superCategory}
              onChange={setSuperCategory}
              valueField="identifier"
              labelField="identifier"
            />
          ),
        },
      ]}
    />
  );
}