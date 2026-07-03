'use client';

import PropTypes from 'prop-types';
import ListPage from "../../components/common/ListPage";
import Role from "../../components//dropdown/Role";
import Sidebar from "../../components/layout/Sidebar";

const RoleField = ({ value, onChange }) => (
  <Role value={value || []} onChange={onChange} />
);

RoleField.propTypes = {
  value: PropTypes.any,
  onChange: PropTypes.func,
};

const NodeList = () => {

  const keys = [
    "identifier",
    "path",
    "roles"
  ];

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
    },
    {
      name: "path",
      label: "Path",
    },
    {
      name: "roles",
      label: "Roles",
      component: RoleField,
    }
  ];

  return (
    <Sidebar>
    <ListPage
      keys={keys}
      fields={fields}
      modelName="node"
      showToggle={false}
    />
    </Sidebar>
  );
};

export default NodeList;