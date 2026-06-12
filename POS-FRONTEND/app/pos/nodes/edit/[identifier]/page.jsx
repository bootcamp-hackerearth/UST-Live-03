// app/pos/nodes/edit/[identifier]/page.jsx

"use client";

import React, { useState, useMemo } from "react";
import BaseEditForm from "../../../../../components/edit/BaseEditForm";
import MultiDropDown from "../../../../../components/dropDowns/multiDropDown";

export default function EditNodePage() {
  const [roles, setRoles] = useState([]);

  const extraFields = useMemo(() => [
    {
      key: "path",
      label: "Node Path",
      type: "text",
    },
    {
      key: "roles",
      label: "Assign Roles",
      type: "custom",
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
  ], [roles]);

  const extraData = useMemo(() => ({ roles }), [roles]);

  const structuralSetters = useMemo(() => ({ roles: setRoles }), []);

  return (
    <BaseEditForm
      title="Node"
      apiPath="node"
      extraFields={extraFields}
      extraData={extraData}
      setters={structuralSetters}
    />
  );
}