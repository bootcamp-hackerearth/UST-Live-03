'use client';

import PropTypes from 'prop-types';
import AddPage from "../../../components/Common/AddPage";
import Role from "../../../components/dropdown/Role";

function RoleField({ value, onChange }) {
  return (
    <Role
      value={value || []}
      onChange={onChange}
    />
  );
}

RoleField.propTypes = {
  value: PropTypes.array,
  onChange: PropTypes.func.isRequired,
};

export default function AddUserPage() {
  const fields = [
    {
      name: "name",
      label: "Name",
      type: "text",
      required: true,
    },
    {
      name: "username",
      label: "Username",
      type: "text",
      required: true,
    },
    {
      name: "phoneNo",
      label: "Phone Number",
      type: "phone",
      placeholder: "Enter 10-digit phone number",
      required: true,
      pattern: "[0-9]{10}",
      maxLength: 10,
      title: "Phone number must be exactly 10 digits",
    },
    {
      name: "roles",
      label: "Roles",
      required: true,
      component: RoleField,
    },
     {
      name: "password",
      label: "Password",
      type: "password",
      required: true,
    },
  ];

  return (
    <AddPage
      fields={fields}
      modelName="user"
    />
  );
}