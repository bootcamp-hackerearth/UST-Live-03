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
        <div className="flex flex-col gap-3 w-full">
          <SingleDropdown
            label="Super Category"
            entity="category"
            selectedValue={superCategory}
            onChange={setSuperCategory}
            valueField="identifier"
            labelField="identifier"
          />
          <p className="text-xs text-slate-600 bg-slate-50 border border-slate-200 rounded-lg p-2.5 font-medium leading-relaxed shadow-sm w-full">
            <strong>Note:</strong> If the category you are creating is a <strong>Super Category</strong> itself, leave this field completely blank.
          </p>
        </div>
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