"use client";

import React from "react";
import CommonList from "@/components/CommonList";
import { idColumn, statusColumn, identifierColumn, identifierField, statusField } from "@/components/entityHelpers";
import { requiredValidation } from "@/validation/validation";

export default function RacksPage() {
  const columns = [
    idColumn,
    identifierColumn("Rack Code"),
    {
      key: "shelves",
      label: "Shelves",
      render: (item) => {
        let shelvesArray = [];
        if (Array.isArray(item.shelves)) {
          shelvesArray = item.shelves;
        } else if (typeof item.shelves === "string") {
          shelvesArray = JSON.parse(item.shelves || "[]");
        }
        return (
          <div className="flex flex-wrap gap-1">
            {shelvesArray.map((shelf) => (
              <span
                key={`${item.id}-${shelf}`}
                className="px-2 py-0.5 rounded-md bg-zinc-100 text-zinc-800 border border-zinc-200 text-[10px] font-medium"
              >
                {shelf}
              </span>
            ))}
            {shelvesArray.length === 0 && <span className="text-zinc-400">—</span>}
          </div>
        );
      },
    },
    statusColumn,
  ];

  const fields = [
    identifierField("Enter Rack Code", { validation: requiredValidation }),
    {
      name: "shelves",
      type: "select",
      placeholder: "Select Active Shelves",
      dataKey: "shelvesList",
      hardCoded: "false",
      multiple: true,
      hardCodedArray: [],
      validation: requiredValidation,
      readOnly: false,
    },
    statusField({ validation: requiredValidation }),
  ];

  const dropdownApis = {
    shelvesList: process.env.NEXT_PUBLIC_BASE_URL+"/shelf/list",
  };

  return (
    <CommonList
      title="Racks"
      subtitle="Manage terminal warehouse racks and physical placement segments"
      apiRoute="racks"
      columns={columns}
      searchKeys={["identifier"]}
      fields={fields}
      dropdownApis={dropdownApis}
    />
  );
}