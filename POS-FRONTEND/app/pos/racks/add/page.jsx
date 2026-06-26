// app/pos/racks/add/page.jsx

"use client";

import { useState } from "react";
import BaseAddForm from "@/components/add/BaseAddForm";
import MultiDropDown from "../../../../components/dropDowns/multiDropDown";

export default function RackAddPage() {
  const [selectedShelfs, setSelectedShelfs] = useState([]);

  const extraFields = [
    {
      key: "shelfs",
      label: "Shelves",
      type: "custom",
      required: false,
      render: () => (
        <MultiDropDown
          label="Shelves"
          entity="shelf"
          selectedValues={selectedShelfs}
          onChange={setSelectedShelfs}
          valueField="identifier"
          labelField="identifier"
        />
      ),
    },
  ];

  return (
    <BaseAddForm
      title="Rack"
      apiPath="rack"
      identifierKey="identifier"
      extraFields={extraFields}
      extraData={{ shelfs: selectedShelfs }}
    />
  );
}