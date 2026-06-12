import React from "react";
import List from "../../../components/List";

function Node() {
  const columns = [
    { key: "identifier", label: "IDENTIFIER" },
    { key: "path", label: "PATH" },
    { key: "roles", label: "ROLES" },
    // { key: 'description', label: 'Description' }
  ];

  return (
    <List
      title="NODE"
      apiPath="node"
      columns={columns}
      addPath="/node/add"
      editPath="/node/edit"
    />
  );
}
export default Node;
