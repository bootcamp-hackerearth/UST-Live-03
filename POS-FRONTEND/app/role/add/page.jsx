'use client';

import AddPage from "../../../components/common/AddPage";

const RoleAdd = () => {

  const fields = [
    {
      name: "identifier",
      type: "text",
      label: "Identifier",
    },
    {
      name: "description",
      type: "textarea",
      label: "Description",
    }
  ];

  return (
    <AddPage
      fields={fields}
      modelName="role"
    />
  );
};

export default RoleAdd;