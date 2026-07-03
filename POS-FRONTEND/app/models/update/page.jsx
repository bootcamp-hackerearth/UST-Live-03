'use client';

import UpdatePage from "../../../components/common/UpdatePage";
import Sidebar from "../../../components/layout/Sidebar";

export default function ModelsUpdate() {
  const fields = [
    {
      name: "modelName",
      label: "Model Name",
      type: "text",
    },
  ];

  return (
    <Sidebar>
      <UpdatePage
        fields={fields}
        modelName="models"
      />
    </Sidebar>
  );
}