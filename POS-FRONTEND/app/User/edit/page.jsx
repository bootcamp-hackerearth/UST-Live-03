"use client";
import Layout from "../../Components/Layout";
import CommonEdit from "../../Components/CommonEdit";

function EditUser() {

  const extraFields = [

    {
      key: "name",
      label: "Name",
      type: "text",
      required: true,
    },

    {
      key: "phoneNo",
      label: "Phone Number",
      type: "text",
      required: true,
    },

    {
      key: "roles",
      label: "Roles",
      type: "multiselect",
      apiPath: "role",
      required: true,
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
        title="User"
        apiPath="user"
        extraFields={extraFields}
        lookupParam="username"
        identityField="username"
        showDescription={false}
        onSuccessPath="/User/list"
      />

    </Layout>

  );
}

export default EditUser;    
