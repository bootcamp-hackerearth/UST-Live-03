"use client";

import PropTypes from "prop-types";
import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const nodeFields = [
  {
    name: "identifier",
    placeholder: "Identifier",
  },
  {
    name: "path",
    placeholder: "Path",
  },
  {
    name: "roles",
    placeholder: "Select Roles",
    type: "multiselect",
    apiUrl: "/api/role/list",
  },
];

const nodeValidate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Identifier is required";
  } else if (!/^[a-zA-Z0-9_-]+$/.test(formData.identifier)) {
    errors.identifier = "Only letters, numbers, _ and - allowed";
  }

  if (!formData.path?.trim()) {
    errors.path = "Path is required";
  } else if (!formData.path.startsWith("/")) {
    errors.path = "Path must start with /";
  }

  if (!formData.roles || formData.roles.length === 0) {
    errors.roles = "At least one role must be selected";
  }

  return errors;
};

const NodeForm = (props) => {
  const isEdit = !!props?.initialData?.identifier;

  const fields = nodeFields.map((field) =>
    field.name === "identifier"
      ? { ...field, disabled: isEdit }
      : field
  );

  const rest = { ...props };
  delete rest.fields;

  return (
    <CommonForm
      {...rest}
      title="Node"
      fields={fields}
      validate={nodeValidate}
    />
  );
};

NodeForm.propTypes = {
  initialData: PropTypes.shape({
    identifier: PropTypes.string,
    path: PropTypes.string,
    roles: PropTypes.arrayOf(PropTypes.any),
  }),
};

export default function NodePage() {
  const keys = ["identifier", "path", "roles"];

  return (
    <CommonList
      routeName="node"
      keys={keys}
      editField="identifier"
      FormComponent={NodeForm}
    />
  );
}