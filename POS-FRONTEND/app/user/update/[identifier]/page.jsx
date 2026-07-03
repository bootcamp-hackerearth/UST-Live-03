'use client';

import PropTypes from 'prop-types';
import UpdatePage from "../../components/common/pages/UpdatePage";
import Role from "../../components/Common/dropdown/Role";

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

const UserUpdate = () => {
  const fields = [
    {
      name: "username",
      label: "Username",
      type: "email",
      readOnly: true,
    },
    {
      name: "name",
      label: "Name",
      type: "text",
    },
    {
      name: "phoneNo",
      label: "Phone Number",
      type: "tel",
      required: true,
    },
    {
      name: "roles",
      label: "Roles",
      component: RoleField,
    },
  ];

  return (
    <UpdatePage
      fields={fields}
      modelName="user"
    />
  );
};

export default UserUpdate;