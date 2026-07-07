"use client";

import List from "../../../components/List";

export default function UnitList() {
  return (
    <List
      title="Unit"
      apiPath="unit"
      addPath="/unit/add"
      editPath="/unit/edit"
      columns={[
        {
          key: "identifier",
          label: "Unit Name",
        },
        {
          key: "description",
          label: "Description",
        },
      ]}
      showStatusToggle={true}
    />
  );
}
