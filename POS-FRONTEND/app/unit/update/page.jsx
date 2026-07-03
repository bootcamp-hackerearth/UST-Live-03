'use client';

import UpdatePage from "../../../components/common/UpdatePage";
import Sidebar from "../../../components/layout/Sidebar";

export default function UnitUpdate() {
  const fields = [
    {
      name: "name",
      label: "Unit Name",
      type: "text",
    },
  ];

  return (
    <Sidebar>
      <UpdatePage
        fields={fields}
        modelName="unit"
      />
    </Sidebar>
  );
}