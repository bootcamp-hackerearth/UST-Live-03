"use client";

import React, { useState } from "react";
import PropTypes from "prop-types";
import EditFormSkeleton from "../../../components/EditFormSkeleton";
import MultiDropDown from "../../../components/MultiDropDown";

function RolesDropdown({ value, onChange }) {
  const safeValues = Array.isArray(value) ? value : [];
  
  return (
    <MultiDropDown
      label="Roles"
      apiUrl="/role/findByStatus"
      selectedValues={safeValues}
      onChange={onChange}
      valueField="identifier"
      labelField="identifier"
    />
  );
}
RolesDropdown.propTypes = {
  value: PropTypes.arrayOf(PropTypes.oneOfType([PropTypes.string, PropTypes.number])),
  onChange: PropTypes.func.isRequired,
};

export default function EditNodePage() {
  const [roles, setRoles] = useState([]);

  return (
    <EditFormSkeleton
      title="Node"
      apiPath="node"
      externalExtraData={{ roles }}
      setters={{
        roles: setRoles,
      }}
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
          CustomComponent: RolesDropdown,
        },
      ]}
    />
  );
}