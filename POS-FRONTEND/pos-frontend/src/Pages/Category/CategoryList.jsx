import React, { useEffect, useState } from "react";
import commonApi from "../../components/api/commonApi";
import DynamicList from "../../components/common/DynamicList";
import POSLayout from "../../components/POSLayout";

function CategoryList() {
  const [categories, setCategories] = useState([]);

  // LOAD ACTIVE CATEGORIES
  useEffect(() => {
    commonApi
      .active("category")
      .then((res) => {
        setCategories(res.data || []);
      })
      .catch((err) => {
        console.log(err);
      });
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
      placeholder: "Enter Category Name",
      required: true,
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
  ];

  return (
    <POSLayout>
      <DynamicList
        title="Category List"
        routeName="category"
        columns={columns}
        editUrl="/category/edit"
        addUrl="/category/add"
        formFields={formFields}
        formTitle="Category"
        uniqueFields={["identifier"]}
      />
    </POSLayout>
  );
}

export default CategoryList;