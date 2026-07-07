"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation, pathValidation } from "@/validation/validation";

export default function NodesPage() {
  return (
    <CommonList
      routeName="node"
      editField="identifier"
      keys={["identifier", "path", "roles"]}
      headers={["Node Name", "Path", "Roles"]}
      FormComponent={NodeForm}
    />
  );
}

NodeForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function NodeForm({ mode, data, onClose, onSubmit }) {
  const customValidations = {
    identifier: requiredValidation,
    path: pathValidation,
    roles: requiredValidation,
  };

  return (
    <CommonForm
      title="Node"
      mode={mode}
      data={data}
      onClose={onClose}
      validate={customValidations}
      onSubmit={onSubmit}
      fields={[
        {
          name: "identifier",
          label: "Node Name",
          placeholder: "Node Name",
          required: true,
        },
        {
          name: "path",
          label: "Path",
          placeholder: "Path",
          required: true,
        },
        {
          name: "roles",
          label: "Roles",
          placeholder: "Roles",
          type: "multiselect",
          apiUrl: `${process.env.NEXT_PUBLIC_BASE_URL}/role/list`,
          required: true,
        },
      ]}
    />
  );
}