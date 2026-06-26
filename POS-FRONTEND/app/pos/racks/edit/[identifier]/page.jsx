// app/pos/racks/edit/[identifier]/page.jsx

"use client";

import { useState } from "react";
import BaseEditForm from "@/components/edit/BaseEditForm";
import MultiDropDown from "@/components/dropDowns/multiDropDown";

export default function RackEditPage() {
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
    <BaseEditForm
      title="Rack"
      apiPath="rack"
      identifierKey="identifier"
      extraFields={extraFields}
      extraData={{ shelfs: selectedShelfs }}
      setters={{ shelfs: setSelectedShelfs }}
    />
  );
}