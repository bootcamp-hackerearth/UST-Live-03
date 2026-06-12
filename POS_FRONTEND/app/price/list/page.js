"use client";
 
import CommonList from "../../components/CommonList";
 
export default function PricePage() {
  return (
    <CommonList
      title="Price Management"
 
      apiUrl="/api/price/list"
      method="POST"
 
      deleteApi="/api/price/delete"
      deleteParam="identifier"
 
      editRoute="/price/edit/:identifier"
      addRoute="/price/add"
 
      columns={[
        { header: "ID", field: "id" },
        { header: "Identifier", field: "identifier" },
        { header: "Product", field: "product" },
        { header: "Price", field: "priceAmount" },
        { header: "Type", field: "type" },
      ]}
    />
  );
}
 