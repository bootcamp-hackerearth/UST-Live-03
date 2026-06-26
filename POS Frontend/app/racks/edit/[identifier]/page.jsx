"use client";

import { useState } from "react";
import EditFormSkeleton from "@/components/CommonEditForm";
import MultiDropDown from "@/components/dropdowns/CommonMultiDropDown";

export default function EditRacks() {
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
    <EditFormSkeleton
      title="Racks"
      apiPath="racks"
      extraFields={extraFields}
      extraData={{ shelfs }}
      setters={{ shelfs: setShelfs }}
    />
  );
}
