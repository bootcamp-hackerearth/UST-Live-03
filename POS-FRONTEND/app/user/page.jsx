"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";
import PropTypes from "prop-types";
import { nameValidation, requiredValidation, phoneValidation, passwordValidation } from "@/validation/validation";

export default function UsersPage() {
  return (
    <CommonList
      routeName="user"
      editField="username"
      keys={["name", "username", "phoneNo", "roles"]}
      headers={["Name", "Username", "Phone Number", "Roles"]}
      FormComponent={UserForm}
    />
  );
}

UserForm.propTypes = {
  mode: PropTypes.string.isRequired,
  data: PropTypes.object,
  onClose: PropTypes.func.isRequired,
  onSubmit: PropTypes.func.isRequired,
};

function UserForm({ mode, data, onClose, onSubmit }) {
  const customValidations = {
    name: nameValidation,
    username: requiredValidation,
    phoneNo: phoneValidation,
    password: mode === "add" ? passwordValidation : {},
    roles: requiredValidation,
  };

  return (
    <CommonForm
      title="User"
      mode={mode}
      data={data}
      onClose={onClose}
      validate={customValidations}
      onSubmit={onSubmit}
      fields={[
        { name: "name", placeholder: "Name", required: true },
        { name: "username", placeholder: "Username", required: true },
        { name: "phoneNo", placeholder: "Phone Number", required: true },
        { name: "password", placeholder: "Password", type: "password", required: mode === "add" },
        {
          name: "roles",
          placeholder: "Roles",
          type: "multiselect",
          apiUrl: "http://localhost:8080/api/role/list",
          required: true,
        },
      ]}
    />
  );
}