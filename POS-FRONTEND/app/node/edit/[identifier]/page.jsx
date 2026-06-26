"use client";

import { useParams } from "next/navigation";
import { useState } from "react";
import Update from "../../../../components/edit";
import MultiDropdown from "../../../../components/MultiDropdown";
import PropTypes from "prop-types";
import { useAuditField } from "../../../../utils/useAuditField";

function RolesField({ value, onChange }) {
  return (
    <MultiDropdown
      value={value}
      label="Roles"
      apiPath="role/list"
      required
      onChange={onChange}
      urlMethod={"post"}
    />
  );
}

RolesField.propTypes = {
  value: PropTypes.oneOfType([PropTypes.array, PropTypes.string]),
  onChange: PropTypes.func.isRequired,
};

export default function EditNode() {
  const params = useParams();
  const [roles, setRoles] = useState([]);
  const auditField = useAuditField();

  const extraFields = [
    {
      key: "path",
      label: "Path",
      required: true,
    },
    {
      key: "roles",
      type: "custom",
      onLoad: (value) => {
        if (value) {
          setRoles(Array.isArray(value) ? value : value.split(","));
        }
      },
      component: (
        <RolesField value={roles} onChange={setRoles} />
      ),
    },
    auditField,
  ];

  return (
    <Update
      identifier={params.identifier}
      apiPath="node"
      title="Node"
      extraFields={extraFields}
      extraData={{ roles }}
      showDescription={false}
    />
  );
}
