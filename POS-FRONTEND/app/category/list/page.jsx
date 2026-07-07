import React from "react";
import List from "../../../components/List";

function Category() {
  const columns = [
    { key: "identifier", label: "CATEGORY NAME" },
    { key: "superCategory", label: "SUPER CATEGORY" },
  ];

  return (
    <List
      title="CATEGORY"
      apiPath="category"
      columns={columns}
      addPath="/category/add"
      editPath="/category/edit"
    />
  );
}
export default Category;
