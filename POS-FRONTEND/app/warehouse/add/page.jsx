'use client';

import AddPage from "../../../components/common/AddPage";

export default function AddWareHouse() {
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
      <AddPage
        fields={fields}
        modelName="warehouse"
        initialData={{
          identifier: "",
          location: "",
          contactPerson: "",
          phoneNumber: "",
        }}
      />
  );
}