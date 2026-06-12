import React from "react";
import DynamicList from "../../components/common/DynamicList";
import POSLayout from "../../components/POSLayout";

function WarehouseList() {

  const columns = [
    {
      key: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      key: "name",
      label: "Warehouse Name",
      type: "text",
    },
    {
      key: "address",
      label: "Address",
      type: "text",
    },
    {
      key: "region",
      label: "Region",
      type: "text",
    },
    {
      key: "country",
      label: "Country",
      type: "text",
    },
    {
      key: "phoneNo",
      label: "Phone Number",
      type: "text",
    },
  ];

  return (
    <POSLayout>

      <DynamicList
        title="Warehouse List"
        routeName="warehouse"
        columns={columns}
        editUrl="/warehouse/edit"
        addUrl="/warehouse/add"
      />

    </POSLayout>
  );
}

export default WarehouseList;