"use client";

import CommonList from "@/components/CommonList";
import CommonForm from "@/components/CommonForm";

const keys = ["id", "username", "name", "phoneNo", "roles"];

const fields = [
  {
    name: "username",
    placeholder: "Username",
    type: "text",
    disableOnEdit: true,
  },
  {
    name: "name",
    placeholder: "Name",
    type: "text",
  },
  {
    name: "phoneNo",
    placeholder: "Phone No",
    type: "text",
  },
  {
    name: "roles",
    placeholder: "Roles",
    type: "multiselect",
    apiUrl: "http://localhost:8080/api/role/list",
  },
  {
    name: "password",
    placeholder: "Password",
    type: "password",
    hideOnEdit: true,
  },
];

const validate = (formData, mode) => {
  const errors = {};

  if (!formData.username?.trim()) {
    errors.username = "Email is required";
  }

  if (!formData.name?.trim()) {
    errors.name = "Name is required";
  }

  if (!formData.phoneNo?.trim()) {
    errors.phoneNo = "Phone required";
  } else if (!/^\d{10}$/.test(formData.phoneNo)) {
    errors.phoneNo = "Must be 10 digits";
  }

  if (!formData.roles || formData.roles.length === 0) {
    errors.roles = "Select at least one role";
  }

 const CREDENTIALS_REQUIRED_MSG = "Password is required";
const CREDENTIALS_STRENGTH_MSG = "Password must be stronger";

if (mode === "add") {
  if (!formData.password?.trim()) {
    errors.password = CREDENTIALS_REQUIRED_MSG;
  } else if (
    !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(
      formData.password
    )
  ) {
    errors.password = CREDENTIALS_STRENGTH_MSG;
  }
}

  return errors;
};

function UserFormComponent(props) {
  const rest = { ...props };
  delete rest.fields;

  return (
    <CommonForm
      {...rest}
      title="User"
      fields={fields}
      validate={validate}
      editField="username"
    />
  );
}

export default function UserPage() {
  return (
    <div className="p-4">
      <CommonList
        routeName="user"
        keys={keys}
        editField="username"
        FormComponent={UserFormComponent}
      />
    </div>
  );
}