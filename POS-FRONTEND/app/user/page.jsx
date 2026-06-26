'use client';

import PropTypes from 'prop-types';
import Sidebar from "../../components/layout/Sidebar";
import ListPage from "../../components/common/ListPage";
import Role from "../../components/dropdown/Role";

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

const UserList = () => {
  const keys = [
    "username",
    "name",
    "phoneNo",
    "roles",
  ];

  const fields = [
    {
      name: "username",
      label: "Username",
      type: "text",
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
      type: "text",
    },
    {
      name: "roles",
      label: "Roles",
      component: RoleField,
    },
  ];

  return (
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        showToggle={false}
        modelName="user"
      />
    </Sidebar>
  );
};

export default UserList;