import React from "react";
import List from "../../../components/List";

function Customer() {
  const columns = [
    { key: "identifier", label: "IDENTIFIER" },
    { key: "name", label: "NAME" },
    { key: "phoneNo", label: "PHONE" },
    { key: "partyType", label: "PARTY TYPE" },
    { key: "balance", label: "BALANCE" },
    { key: "creditLimit", label: "CREDIT LIMIT" },
  ];

  return (
    <List
      title="CUSTOMER"
      apiPath="customer"
      columns={columns}
      addPath="/customer/add"
      editPath="/customer/edit"
    />
  );
}

export default Customer;
