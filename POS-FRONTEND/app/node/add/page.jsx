"use client";
import React, { useState } from "react";
import PropTypes from "prop-types";
import Add from "../../../components/add";
import MultiDropdown from "../../../components/MultiDropdown";

function RolesField({ roles, onChange }) {
  return (
    <MultiDropdown
      value={roles}
      label="Roles"
      apiPath="role"
      required
      onChange={onChange}
    />
  );
}

RolesField.propTypes = {
  roles: PropTypes.array,
  onChange: PropTypes.func.isRequired,
};

function NodeAdd() {
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
      component: <RolesField roles={roles} onChange={(val) => setRoles(val)} />,
    },
  ];

  return (
    <Add
      title="Node"
      apiPath="node"
      extraFields={extraFields}
      extraData={{ roles }}
      showDescription={false}
    />
  );
}
export default NodeAdd;
