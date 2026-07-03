'use client';

import ListPage from "../../components/common/ListPage";
import Sidebar from "../../components/layout/Sidebar";

const RoleList = () => {

  const keys = [
    "identifier",
    "description",
  ];

  const fields = [
    {
      name: "identifier",
      label: "Identifier",
    },
    {
      name: "description",
      label: "Description",
    }
  ];

  return (
    <Sidebar>
      <ListPage
        keys={keys}
        fields={fields}
        showToggle={true}
        modelName="role"   
      />
    </Sidebar>
  );
};

export default RoleList;