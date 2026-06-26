"use client";

import { useState } from "react";
import AddFormSkeleton from "@/components/CommonAddForm";
import MultiDropDown from "@/components/dropdowns/CommonMultiDropDown";

export default function AddRacks() {
  const [shelfs, setShelfs] = useState([]);

  const extraFields = [
    {
      key: "shelfs",
      type: "custom",
      label: "Shelfs",
      component: (
        <MultiDropDown
          label="Shelfs"
          apiUrl="/shelfs/findByStatus"
          valueField="identifier"
          labelField="identifier"
          selectedValues={shelfs}
          onChange={(val) => setShelfs(val)}
        />
      ),
    },
  ];

  return (
    <AddFormSkeleton
      title="Racks"
      apiPath="racks"
      extraFields={extraFields}
      extraData={{ shelfs }}
    />
  );
}
