'use client';

import ListPage from "../../components/common/ListPage";
import Sidebar from "../../components/layout/Sidebar";

export default function BrandList() {
  const keys = [
    "identifier",
    "brandName",
    "description",
  ];

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
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        modelName="brand"
      />
    </Sidebar>
  );
}