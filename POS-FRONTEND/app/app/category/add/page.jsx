"use client";

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import Add from "../../../components/add";
import SingleDropdown from "@/components/SingleDropdown";

function CategoryAdd() {
  const router = useRouter();
  const [selectedSuperCategory, setSelectedSuperCategory] = useState("");

  const extraFields = [
    {
      key: "superCategory",
      type: "custom",
      component: (
        <SingleDropdown
          value={selectedSuperCategory}
          label="Super Category"
          apiPath="category/list"
          onChange={(value) => setSelectedSuperCategory(value)}
          urlMethod={"post"}
        />
      ),
    },
  ];

  return (
    <Add
      title="Category"
      apiPath="category"
      showDescription={false}
      extraFields={extraFields}
      extraData={{
        superCategory: selectedSuperCategory || null,
      }}
      onClose={() => router.push("/category/list")}
      onSuccess={() => router.push("/category/list")}
    />
  );
}
export default CategoryAdd;
