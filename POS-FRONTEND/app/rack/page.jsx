'use client';

import ListPage from "../../components/Common/ListPage";
import Sidebar from "../../components/layout/Sidebar";
import Shelves from "../../components/dropdown/shelves";

export default function RackList() {
  const keys = [
    "identifier",
    "name",
    "shelves",
  ];

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      name: "name",
      label: "Rack Name",
      type: "text",
    },
    {
      name: "shelves",
      label: "Shelves",
      type: "text",
      component: Shelves,
      multiple: true,
    },
  ];

  return (
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        modelName="rack"
      />
    </Sidebar>
  );
}