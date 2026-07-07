"use client";

import List from "../../../components/List";

export default function BrandList() {
  return (
    <List
      title="Brand"
      apiPath="brand"
      addPath="/brand/add"
      editPath="/brand/edit"
      columns={[
        {
          key: "identifier",
          label: "Brand Name",
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
