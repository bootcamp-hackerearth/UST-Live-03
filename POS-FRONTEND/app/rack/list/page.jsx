"use client";

import List from "../../../components/List";

export default function RackList() {
  return (
    <List
      title="Rack"
      apiPath="rack"
      addPath="/rack/add"
      editPath="/rack/edit"
      columns={[
        {
          key: "identifier",
          label: "Rack Name",
        },
        {
          key: "shelfs",
          label: "Shelf",
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
