'use client';

import ListPage from "../../components/common/ListPage";
import Sidebar from "../../components/layout/Sidebar";

export default function UnitList() {
  const keys = [
    "identifier",
    "name",
  ];

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
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        modelName="unit"
      />
    </Sidebar>
  );
}