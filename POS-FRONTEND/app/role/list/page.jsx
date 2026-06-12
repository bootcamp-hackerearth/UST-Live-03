import React from "react";
import List from "../../../components/List";

function Role() {
  const columns = [
    { key: "identifier", label: "Identifier" },
    { key: "description", label: "Description" },
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
