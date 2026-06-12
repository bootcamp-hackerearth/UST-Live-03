"use client";
 
import EditFormSkeleton from "@/components/CommonEditForm";
import SingleDropdown from "@/components/dropdowns/CommonSingleDropdown";
import { useState } from "react";
 
export default function EditCategory() {
 
  const [superCategory, setSuperCategory] = useState("");
 
  const extraFields = [
    {
      key: "superCategory",
      type: "custom",
      label: "Super Category",
      component: (
        <SingleDropdown
          label="Super Category"
          apiUrl="/category/findByStatus"
          valueField="identifier"
          labelField="identifier"
          selectedValue={superCategory}
          onChange={(val) => setSuperCategory(val)}
        />
      ),
    },
  ];
 
  return (
    <EditFormSkeleton
      title="Category"
      apiPath="category"
      paramName="identifier"
      identifierField="identifier"
      extraFields={extraFields}
      extraData={{ superCategory }}
      setters={{ superCategory: setSuperCategory }}
    />
  );
}