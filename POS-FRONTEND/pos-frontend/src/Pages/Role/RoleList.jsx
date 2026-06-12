import React from "react";

import DynamicList from "../../components/common/DynamicList";

import POSLayout from "../../components/POSLayout";

function RoleList() {

  const columns = [
    {
      key: "identifier",
      label: "Role",
      type: "text",
    },
    {
      key: "description",
      label: "Description",
      type: "text",
    },
  ];

  const formFields = [
    {
      key: "identifier",
      label: "Role Name",
      type: "text",
      placeholder: "Enter Role",
      required: true,
    },
    {
      key: "description",
      label: "Description",
      type: "text",
      placeholder: "Enter Description",
      required: false,
    },
  ];

  return (
    <POSLayout>

      <DynamicList
        title="Role List"

        routeName="role"

        columns={columns}

        formFields={formFields}

        formTitle="Role"

        uniqueFields={["identifier"]}
      />

    </POSLayout>
  );
}

export default RoleList;