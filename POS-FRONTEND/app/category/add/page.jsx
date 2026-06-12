"use client";

import CommonAddTemplate from "../../components/CommonAddTemplate";

export default function CategoryAdd() {
  const extraFields = [
    {
      key: "supercategory",
      label: "Super Category",
      type: "select",
      apiPath: "category",
      placeholder: "Select super category",
      required: false,
    },
  ];

  return (
    <CommonAddTemplate
      title="Category"
      apiPath="category"
      identifierKey="identifier"
      identifierLabel="Identifier"
      extraFields={extraFields}
      onSuccessPath="/category"
    />
  );
}
