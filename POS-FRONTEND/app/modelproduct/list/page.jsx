"use client";

import List from "../../../components/List";

export default function ModelProductList() {
  return (
    <List
      title="MODEL PRODUCT"
      apiPath="modelProduct"
      addPath="/modelproduct/add"
      editPath="/modelproduct/edit"
      columns={[
        {
          key: "identifier",
          label: "MODEL NAME",
        },
      ]}
      showStatusToggle={true}
    />
  );
}
