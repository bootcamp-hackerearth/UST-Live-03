'use client';

import AddPage from "../../../components/Common/AddPage";

export default function ModelsAdd() {
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
      <AddPage
        fields={fields}
        modelName="models"
      />
  );
}