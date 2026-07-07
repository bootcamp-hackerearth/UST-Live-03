"use client";

import List from "../../../components/List";

export default function RackList() {
  return (
    <List
      title="RACK"
      apiPath="rack"
      addPath="/rack/add"
      editPath="/rack/edit"
      columns={[
        {
          key: "identifier",
          label: "RACK NAME",
        },
        {
          key: "shelfs",
          label: "SHELF",
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
