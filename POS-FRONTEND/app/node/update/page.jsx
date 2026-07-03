"use client";
import PropTypes from "prop-types";
import UpdatePage from "../../../components/common/UpdatePage";
import Role from "../../../components/dropdown/Role";

function RoleField({ value, onChange }) {
  return <Role value={value || []} onChange={onChange} />;
}

RoleField.propTypes = {
  value: PropTypes.array,
  onChange: PropTypes.func,
};

export default function NodeUpdate() {
  const fields = [
    {
      name: "path",
      label: "Path",
      type: "text",
    },
    {
      name: "roles",
      label: "Roles",
      component: RoleField,
    },
  ];

  return (
    <UpdatePage
      modelName="node"
      fields={fields}
    />
  );
}