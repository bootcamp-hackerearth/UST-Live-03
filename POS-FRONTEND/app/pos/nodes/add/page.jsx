// app/pos/nodes/add/page.jsx

"use client";

import BaseAddForm from "../../../../components/add/BaseAddForm";
import MultiDropDown from "../../../../components/dropDowns/multiDropDown";
import { useState } from "react";

export default function AddNodePage() {
  const [roles, setRoles] = useState([]);

  const extraFields = [
    {
      key: "path",
      label: "Path",
      type: "text",
      required: true,
      placeholder: "e.g., /product/list"
    },
    {
      key: "roles",
      label: "Assign Roles",
      type: "custom",
      required: true,
      component: (
        <MultiDropDown
          label="Assign Roles"
          entity="role"
          selectedValues={roles}
          onChange={setRoles}
          valueField="identifier"
          labelField="identifier"
        />
      ),
    },
  ];

  return (
    <BaseAddForm
      title="Node"
      apiPath="node"
      extraFields={extraFields}
      extraData={{ roles }}
    />
  );
}
