"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const fields = [
  {
    name: "identifier",
    placeholder: "Role Name",
  },
  {
    name: "description",
    placeholder: "Description",
  },
];

const validate = (formData) => {
  const errors = {};

  if (!formData.identifier?.trim()) {
    errors.identifier = "Role name is required";
  } else if ((formData.identifier?.length ?? 0) < 3) {
    errors.identifier = "Role name must be at least 3 characters";
  } else if (!/^[A-Za-z_]+$/.test(formData.identifier)) {
    errors.identifier = "Role name must contain only letters and underscore";
  }

  if (!formData.description?.trim()) {
    errors.description = "Description is required";
  } else if ((formData.description?.length ?? 0) < 5) {
    errors.description = "Description must be at least 5 characters";
  } else if ((formData.description?.length ?? 0) > 200) {
    errors.description = "Description must not exceed 200 characters";
  }

  return errors;
};

function RoleForm(props) {
  return <CommonForm {...props} fields={fields} title="Role" validate={validate} />;
}

export default function RolePage() {
  const keys = ["identifier", "description"];

  return (
    <CommonList
      keys={keys}
      routeName="role"
      editField="identifier"
      FormComponent={RoleForm}
    />
  );
}