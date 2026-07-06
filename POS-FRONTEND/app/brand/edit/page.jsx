"use client";
 
import React, { Suspense} from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";
 
export default function EditBrand() {
  const fields = [
    {
      key: "identifier",
      label: "Identifier",
      type: "text",
      readOnly: true,
    },
    {
      key: "description",
      label: "Description",
      type: "textarea",
      required: true,
    },
    {
      key: "status",
      label: "Status",
      type: "select",
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
      <Suspense fallback={<div>Loading...</div>}>
        <CommonEdit
          title="Brand"
          apiPath="brand"
          extraFields={fields}
          identityField="identifier"
          onSuccessPath="/brand/list"
        />
      </Suspense>
    </Layout>
  );
}