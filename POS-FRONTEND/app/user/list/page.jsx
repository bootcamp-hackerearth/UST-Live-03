"use client";

import React from "react";
import List from "../../../components/List";

function User() {
  const columns = [
    { key: "name", label: "Name" },
    { key: "username", label: "Username" },
    { key: "phoneNo", label: "Phone No" },
    { key: "roles", label: "Roles" },
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
