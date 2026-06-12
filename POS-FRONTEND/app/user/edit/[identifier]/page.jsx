"use client";

import { useParams } from "next/navigation";
import { useState } from "react";
import PropTypes from "prop-types";
import Update from "../../../../components/edit";
import MultiDropdown from "../../../../components/MultiDropdown";

function RolesDropdownEditor({ value, onChange }) {
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

RolesDropdownEditor.propTypes = {
  value: PropTypes.array,
  onChange: PropTypes.func.isRequired,
};

export default function EditUser() {
  const params = useParams();
  const [roles, setRoles] = useState([]);
  const extraFields = [
    {
      key: "name",
      label: "Name",
      type: "text",
      required: true,
    },
    {
      key: "username",
      label: "Username",
      type: "text",
      required: true,
      readOnly: true,
    },
    {
      key: "phoneNo",
      label: "Phone Number",
      type: "number",
      required: true,
    },
    {
      key: "password",
      label: "Password",
      type: "password",
      required: false,
    },
    {
      key: "roles",
      type: "custom",
      onLoad: (val) => {
        if (Array.isArray(val)) {
          setRoles(
            val.map((role) =>
              typeof role === "object" ? role.identifier || role.name : role,
            ),
          );
        } else {
          setRoles([]);
        }
      },
      component: (
        <RolesDropdownEditor value={roles} onChange={(val) => setRoles(val)} />
      ),
    },
  ];

  return (
    <Update
      identifier={params.identifier}
      apiPath="user"
      title="User"
      extraFields={extraFields}
      extraData={{ roles }}
      hideIdentifierField={true}
    />
  );
}
