import React from "react";
import DynamicList from "../../components/common/DynamicList";
import POSLayout from "../../components/POSLayout";

function CustomerList() {

  const token = localStorage.getItem("token");

  const columns = [
    {
      key: "customerName",
      label: "Name",
      type: "text",
    },
    {
      key: "identifier",
      label: "Email",
      type: "text",
    },
    {
      key: "phoneNumber",
      label: "Phone",
      type: "text",
    },
    {
      key: "partyType",
      label: "Party Type",
      type: "text",
    },
    {
      key: "creditLimit",
      label: "Credit Limit",
      type: "text",
    },
    {
      key: "balance",
      label: "Balance",
      type: "text",
    },
  ];
  return (

    <POSLayout>
      <DynamicList
        title="Customer List"
        apiUrl="http://localhost:8080/api/customer/list"
        token={token}
        columns={columns}

        editUrl="/customer/edit"
        addUrl="/customer/add"
        deleteUrl="http://localhost:8080/api/customer/delete"
      />
    </POSLayout>

  );
}

export default CustomerList;