"use client";

import { useState } from "react";
import AddFormSkeleton from "../../components/AddFormSkeleton";
import MultiDropDown from "../../components/MultiDropDown";

export default function AddNodePage() {
  const [roles, setRoles] = useState([]);

  return (
    <AddFormSkeleton
      title="Node"
      apiPath="node"
      extraData={{ roles }}
      extraFields={[
        {
          key: "path",
          label: "Path",
          type: "text",
        },
        {
          key: "roles",
          label: "Roles",
          type: "custom",
          component: (
            <MultiDropDown
              label="Roles"
              apiUrl="/role/findByStatus"
              selectedValues={roles}
              onChange={setRoles}
              valueField="identifier"
              labelField="identifier"
            />
          ),
        },
      ]}
    />
  );
}