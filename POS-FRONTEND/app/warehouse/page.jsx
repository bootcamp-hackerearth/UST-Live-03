'use client';

import ListPage from "../../components/Common/ListPage";
import Sidebar from "../../components/layout/Sidebar";

export default function WareHouseList() {
  const keys = [
    "identifier",
    "location",
    "contactPerson",
    "phoneNo",
  ];

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      name: "location",
      label: "Location",
      type: "text",
    },
    {
      name: "contactPerson",
      label: "Contact Person",
      type: "text",
    },
    {
      name: "phoneNo",
      label: "Phone Number",
      type: "text",
    },
  ];

  return (
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        modelName="warehouse"
      />
    </Sidebar>
  );
}