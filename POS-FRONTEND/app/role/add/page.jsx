"use client";

import Layout from "../../Components/Layout";
import CommonAdd from "../../Components/CommonAdd";

export default function AddRole() {
  const extraFields = [
    {
      key: "status",
      label: "Status",
      type: "select",
      valueType: "boolean",
      required: true,
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
      <CommonAdd
        title="Role"
        apiPath="role"
        extraFields={extraFields}
        onSuccessPath="/role/list"
      />
    </Layout>
  );
}