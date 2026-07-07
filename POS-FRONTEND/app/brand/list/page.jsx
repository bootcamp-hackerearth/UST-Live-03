"use client";

import List from "../../../components/List";

export default function BrandList() {
  return (
    <List
      title="BRAND"
      apiPath="brand"
      addPath="/brand/add"
      editPath="/brand/edit"
      columns={[
        {
          key: "identifier",
          label: "BRAND NAME",
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
