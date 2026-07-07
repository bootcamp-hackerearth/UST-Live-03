"use client";

import List from "../../../components/List";

export default function ModelProductList() {
  return (
    <List
      title="Model Product"
      apiPath="modelProduct"
      addPath="/modelproduct/add"
      editPath="/modelproduct/edit"
      columns={[
        {
          key: "identifier",
          label: "Model Name",
        },
      ]}
      showStatusToggle={true}
    />
  );
}
