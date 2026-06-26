'use client';

import ListPage from "../../components/Common/ListPage";
import Sidebar from "../../components/layout/Sidebar";

export default function ModelsList() {
  const keys = [
    "identifier",
    "modelName",
  ];

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      name: "modelName",
      label: "Model Name",
      type: "text",
    },
  ];

  return (
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        modelName="models"
      />
    </Sidebar>
  );
}