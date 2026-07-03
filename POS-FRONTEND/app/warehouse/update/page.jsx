'use client';

import UpdatePage from "../../../components/common/UpdatePage";
import Sidebar from "../../../components/layout/Sidebar";

export default function UpdateWareHouse() {
  const fields = [
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
      type: "phone",
    },
  ];

  return (
    <Sidebar>
      <UpdatePage
        fields={fields}
        modelName="warehouse"
      />
    </Sidebar>
  );
}