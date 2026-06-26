"use client";

import List from "../../../components/List";

export default function WarehouseList() {
  return (
    <List
      title="Warehouse"
      apiPath="warehouse"
      addPath="/warehouse/add"
      editPath="/warehouse/edit"
      columns={[
        {
          key: "identifier",
          label: "Warehouse Name",
        },
        {
          key: "country",
          label: "Country",
        },
        {
          key: "pincode",
          label: "Pincode",
        },
        {
          key: "address",
          label: "Address",
        },
      ]}
    />
  );
}
