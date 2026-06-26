'use client';

import AddPage from "../../../components/Common/AddPage";

export default function BrandAdd() {
  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      name: "brandName",
      label: "Brand Name",
      type: "text",
    },
    {
      name: "description",
      label: "Description",
      type: "textarea",
    },
  ];

  return (
      <AddPage
        fields={fields}
        modelName="brand"
      />
  );
}