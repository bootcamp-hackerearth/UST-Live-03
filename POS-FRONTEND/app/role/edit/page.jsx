"use client";

import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";

export default function EditRole() {
  const extraFields = [
    {
      key: "description",
      label: "Description",
      type: "textarea", 
      placeholder: "Enter role description",
      required: false,
    },
    
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
      <CommonEdit
        title="Role"
        apiPath="role"
        identifierField="identifier"
        extraFields={extraFields}
        onSuccessPath="/role/list"
        showIdentifierDescription={true}

      />
    </Layout>
  );
}