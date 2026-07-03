'use client';

import UpdatePage from "../../../components/common/UpdatePage";
import Sidebar from "../../../components/layout/Sidebar";

export default function BrandUpdate() {
  const fields = [
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
      <UpdatePage
        fields={fields}
        modelName="brand"
      />
    </Sidebar>
  );
}