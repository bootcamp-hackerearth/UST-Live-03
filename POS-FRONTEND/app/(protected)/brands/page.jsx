"use client";

import CommonList from "@/components/table/CommonList";

export default function Brands() {

  const columns = [
    {
      header: "Brand Name",
      field: "identifier",
    },
    {
      header: "Description",
      field: "description",
    },
  ];

  return (
    <CommonList
      title="Brands"
      subtitle="Manage your product brands"
      entity="brand"
      addPath="/brands/add"
      editPath="/brands/edit"
      columns={columns}
    />
  );
}