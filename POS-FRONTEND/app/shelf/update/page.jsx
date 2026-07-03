'use client';

import UpdatePage from "../../../components/common/UpdatePage";

export default function ShelfUpdate() {
  const fields = [
    {
      name: "shelfName",
      label: "Shelf Name",
      type: "text",
    },
  ];

  return (
    <UpdatePage
      fields={fields}
      modelName="shelf"
    />
  );
}