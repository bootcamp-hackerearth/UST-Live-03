'use client';

import AddPage from "../../../components/common/AddPage";

export default function UnitAdd() {
  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      name: "name",
      label: "Unit Name",
      type: "text",
    },
  ];

  return (
     <AddPage
        fields={fields}
        modelName="unit"
      />
  );
}