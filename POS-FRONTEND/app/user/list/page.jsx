"use client";

import React from "react";
import List from "../../../components/List";

function User() {
  const columns = [
    { key: "name", label: "NAME" },
    { key: "username", label: "USERNAME" },
    { key: "phoneNo", label: "PHONE NO" },
    { key: "roles", label: "ROLES" },
  ];

  return (
    <List
      title="USERS"
      apiPath="user"
      columns={columns}
      addPath="/user/add"
      editPath="/user/edit"
      identifierKey="username"
    />
  );
}

export default User;
