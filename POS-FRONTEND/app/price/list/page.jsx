import React from "react";
import List from "../../../components/List";

function Price() {
  const columns = [
    { key: "identifier", label: "IDENTIFIER" },
    { key: "costPrice", label: "COST_PRICE" },
    { key: "sellingPrice", label: "SELLING_PRICE" },
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
