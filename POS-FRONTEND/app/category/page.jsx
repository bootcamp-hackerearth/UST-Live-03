"use client";

import React, { useEffect, useState } from "react";

import DynamicList from "../components/common/DynamicList";
import POSLayout from "../components/PosLayout";
import commonApi from "../services/commonApi";
function CategoryList() {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    commonApi
      .active("category")
      .then((res) => setCategories(res.data || []))
      .catch((err) => console.log(err));
  }, []);

  const columns = [
    {
      key: "identifier",
      label: "Category",
      type: "text",
    },
    {
      key: "superCategory",
      label: "Super Category",
      type: "text",
    },
  ];

  const formFields = [
    {
      key: "identifier",
      label: "Category Name",
      type: "text",
      placeholder: "Enter Category",
      required: true,
      readOnlyOnEdit: true,
    },
    {
      key: "superCategory",
      label: "Super Category",
      type: "select",
      options: categories,
      optionLabel: "identifier",
      optionValue: "identifier",
      required: false,
    },
  ];

  return (
    <POSLayout>
      <DynamicList
        title="Category List"
        routeName="category"
        columns={columns}
        formFields={formFields}
        formTitle="Category"
        uniqueFields={["identifier"]}
      />
    </POSLayout>
  );
}

export default CategoryList;