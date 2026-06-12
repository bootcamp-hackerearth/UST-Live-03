"use client";

import PropTypes from "prop-types";
import AddPage from "../../../components/Common/AddPage";
import Role from "../../../components/dropdown/Role";

const RoleField = ({ value, onChange }) => {
  return <Role value={value || []} onChange={onChange} />;
};

RoleField.propTypes = {
  value: PropTypes.array,
  onChange: PropTypes.func,
};

export default function NodeAdd() {
  const fields = [
    {
      name: "identifier",
      label: "Identifier",
      type: "text",
    },
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
    <AddPage
      fields={fields}
      modelName="node"
    />
  );
}