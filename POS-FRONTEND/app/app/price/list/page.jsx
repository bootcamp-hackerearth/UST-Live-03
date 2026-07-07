import React from "react";
import List from "../../../components/List";

function Price() {
  const columns = [
    { key: "identifier", label: "Identifier" },
    { key: "costPrice", label: "Cost_Price" },
    { key: "sellingPrice", label: "Selling_Price" },
    { key: "mrp", label: "MRP" },
  ];

  return (
    <List
      title="PRICE"
      apiPath="price"
      columns={columns}
      addPath="/price/add"
      editPath="/price/edit"
    />
  );
}
export default Price;
