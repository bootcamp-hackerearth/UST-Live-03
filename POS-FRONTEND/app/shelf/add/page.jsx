'use client';

import AddPage from "../../../components/common/AddPage";

export default function ShelfAdd() {
  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
    {
      name: "shelfName",
      label: "Shelf Name",
      type: "text",
    },
  ];

  return (
    <AddPage
      fields={fields}
      modelName="shelf"
    />
  );
}