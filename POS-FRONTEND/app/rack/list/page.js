"use client";
import React from "react";
import CommonList from "@/app/components/CommonList";

const RackList = () => {
  const columns = [
    { label: "ID", field: "id" },
    { label: "Rack", field: "identifier" },
    {
      label: "Shelfs",
      render: (item) =>
        item.shelfs?.map((shelf) => (
          <span
            key={shelf}
            className="bg-purple-100 text-purple-600 px-2 py-1 rounded text-xs mr-1"
          >
            {shelf}
          </span>
        )),
    },
    { label: "Status", field: "status" },
  ];

  return (
    <CommonList
      title="Rack Management"
      columns={columns}
      urlName="rack"
      showStatus={true}
    />
  );
};

export default RackList;