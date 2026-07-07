"use client";

import List from "../../../components/List";

export default function UnitList() {
  return (
    <List
      title="UNIT"
      apiPath="unit"
      addPath="/unit/add"
      editPath="/unit/edit"
      columns={[
        {
          key: "identifier",
          label: "UNIT NAME",
        },
        {
          key: "description",
          label: "DESCRIPTION",
        },
      ]}
      showStatusToggle={true}
    />
  );
}
