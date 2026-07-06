"use client";

import { Suspense } from "react";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";

export default function EditModel() {
  const extraFields = [
    {
      key: "status",
      label: "Status",
      type: "select",
      valueType: "boolean",
      required: true,
      options: [
        { label: "Active", value: "true" },
        { label: "Inactive", value: "false" },
      ],
    },
  ];

  return (
    <Layout>
      <Suspense fallback={<div>Loading...</div>}>
        <CommonEdit
          title="Model"
          apiPath="model"
          identityField="identifier"
          extraFields={extraFields}
          onSuccessPath="/model/list"
        />
      </Suspense>
    </Layout>
  );
}