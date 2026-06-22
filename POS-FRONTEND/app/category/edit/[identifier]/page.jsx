"use client";

import EditFormSkeleton from "@/components/EditSkeleton";
import SingleDropdown from "@/components/dropdowns/SingleDropdown";
import { useState, useMemo } from "react";
import { useParams } from "next/navigation";

export default function EditCategory() {
  const params = useParams();
  const currentIdentifier = decodeURIComponent(params?.identifier || "");

  const [superCategory, setSuperCategory] = useState("");

  const extraFields = useMemo(() => [
    {
      key: "superCategory",
      type: "custom",
      label: "Super Category",
      component: (
        <SingleDropdown
          label="Super Category"
          apiUrl="/category/getBySuperCategoryNotNull"
          valueField="identifier"
          labelField="identifier"
          selectedValue={superCategory}
          onChange={(val) => setSuperCategory(val)}
          filterOut={currentIdentifier}
        />
      ),
    },
  ], [superCategory, currentIdentifier]);  

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