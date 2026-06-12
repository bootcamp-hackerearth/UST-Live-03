"use client";

import { useState } from "react";
import EditFormSkeleton from "@/components/EditSkeleton";
import MultiDropDown from "@/components/dropdowns/MultiDropDown";

export default function EditNode() {

  const [roles, setRoles] = useState([]);

  const extraFields = [
    {
      key: "path",
      label: "Path",
      type: "text",
      required: true,
    },
    {
      key: "roles",
      type: "custom",
      label: "Roles",
      component: (
        <MultiDropDown
          label="Roles"
          apiUrl="/role/findByStatus"
          valueField="identifier"
          labelField="identifier"
          selectedValues={roles}
          onChange={(val) => setRoles(val)}
        />
      ),
    },
  ];

  return (
    <EditFormSkeleton
      title="Node"
      apiPath="node"
      paramName="identifier"
      identifierField="identifier"
      extraFields={extraFields}
      extraData={{ roles }}
      setters={{ roles: setRoles }}
    />
  );
}