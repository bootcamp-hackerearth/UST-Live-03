import React from "react";
import List from "../../../components/List";

function Role() {
  const columns = [
    { key: "identifier", label: "IDENTIFIER" },
    { key: "description", label: "DESCRIPTION" },
  ];

  return (
    <List
      title="ROLE"
      apiPath="role"
      columns={columns}
      addPath="/role/add"
      editPath="/role/edit"
    />
  );
}
export default Role;
