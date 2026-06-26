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
    {
      key: "status",
      label: "Status",
      type: "toggle",
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
    {
      key: "status",
      label: "Status",
      type: "select",
      options: [
        {
          label: "Active",
          value: true,
        },
        {
          label: "Inactive",
          value: false,
        },
      ],
      optionLabel: "label",
      optionValue: "value",
      required: true,
    },
    {
      key: "createdBy",
      label: "Created By",
      type: "text",
      required: false,
    },
    {
      key: "createdOn",
      label: "Created On",
      type: "text",
      required: false,
    },
    {
      key: "modifiedBy",
      label: "Modified By",
      type: "text",
      required: false,
    },
    {
      key: "modifiedOn",
      label: "Modified On",
      type: "text",
      required: false,
    }
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