'use client';

import ListPage from "../../components/Common/ListPage";
import Sidebar from "../../components/layout/Sidebar";

export default function ShelfList() {
  const keys = [
    "identifier",
    "shelfName",
  ];

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
    <Sidebar>
    <ListPage
      keys={keys}
      fields={fields}
      modelName="shelf"
    />
    </Sidebar>
  );
}