"use client";

import React from "react";
import Layout from "../../Components/Layout";
import CommonAdd from "../../Components/CommonAdd";

export default function AddUser() {
  const extraFields = [
    {
      key: "name",
      label: "Name",
      type: "text",
      placeholder: "Enter full name",
      required: true,
    },
    {
      key: "username",
      label: "Email",
      type: "email",
      placeholder: "Enter email address",
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
      key: "phoneNo",
      label: "Phone Number",
      type: "text",
      placeholder: "Enter phone number",
      required: true,
    },
    {
      key: "password",
      label: "Password",
      type: "password",
      placeholder: "Enter password",
      required: true,
    },
  ];

  return (
    <Layout>
      <CommonAdd
        title="User"
        apiPath="user"
        addEndpoint="/user/register"
        extraFields={extraFields}
        onSuccessPath="/User/list"
        showIdentifierDescription={false}
      />
    </Layout>
  );
}
