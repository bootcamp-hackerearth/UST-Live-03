"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";

function EditCategory() {
  const extraFields = [
    {
      key: "superCategory",
      label: "Super Category",
      type: "select",
      apiPath: "category",
      placeholder: "-- None (Top Level) --",
    },
    {
      key: "status",
      label: "Availability Status",
      type: "select",
      required: true,
      valueType: "boolean",
      options: [
        {
          label: "Active",
          value: "true",
        },
        {
          label: "Inactive",
          value: "false",
        },
      ],
    },
  ];

  return (
    <Layout>
      <CommonEdit
        title="Category"
        apiPath="category"
        identifierParam="identifier"
        extraFields={extraFields}
        onSuccessPath="/category/list"
      />
    </Layout>
  );
}

export default EditCategory;