"use client";
import PropTypes from "prop-types";
import { useParams } from "next/navigation";
import { useState } from "react";
import Update from "../../../../components/edit";
import MultiDropdown from "../../../../components/MultiDropdown";

function RolesField({ value, onChange }) {
  return (
    <MultiDropdown
      value={value}
      label="Roles"
      apiPath="role"
      required
      onChange={onChange}
    />
  );
}

RolesField.propTypes = {
  value: PropTypes.array,
  onChange: PropTypes.func.isRequired,
};

export default function EditNode() {
  const params = useParams();
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
      onLoad: (value) => {
        if (value) {
          setRoles(
            Array.isArray(value)
              ? value
              : value.split(",").map((item) => item.trim()),
          );
        }
      },
      component: (
        <RolesField value={roles} onChange={(value) => setRoles(value)} />
      ),
    },
  ];

  return (
    <Update
      identifier={params.identifier}
      apiPath="node"
      title="Node"
      extraFields={extraFields}
      extraData={{
        roles,
      }}
      showDescription={false}
    />
  );
}
