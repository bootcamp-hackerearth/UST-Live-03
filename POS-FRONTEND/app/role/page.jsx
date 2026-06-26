"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { requiredValidation } from "@/validation/validation";

export default function RolesPage() {
  return (
    <CommonList
      routeName="role"
      editField="identifier"
      keys={["identifier"]}
      headers={["Role Name"]}
      FormComponent={RoleForm}
    />
  );
}

RoleForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function RoleForm({ mode, data, onClose, onSubmit }) {
  const customValidations = {
    identifier: requiredValidation,
  };

  return (
    <CommonForm
      title="Role"
      mode={mode}
      data={data}
      onClose={onClose}
      validate={customValidations}
      onSubmit={onSubmit}
      fields={[
        {
          name: "identifier",
          label: "Role Name",
          placeholder: "Role Name",
          required: true,
        },
      ]}
    />
  );
}