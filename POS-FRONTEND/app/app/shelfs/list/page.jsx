"use client";
import List from "../../../components/List";

export default function ShelfList() {
  return (
    <List
      title="Shelf"
      apiPath="shelfs"
      addPath="/shelfs/add"
      editPath="/shelfs/edit"
      columns={[
        {
          key: "identifier",
          label: "Identifier",
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
