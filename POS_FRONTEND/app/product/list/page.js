"use client";
 
import CommonList from "../../components/CommonList";
 
export default function ProductPage() {
  return (
    <CommonList
      title="Product Management"
 
      apiUrl="/api/product/list"
      method="POST"
 
      deleteApi="/api/product/delete"
      deleteParam="identifier"
 
      editRoute="/product/edit/:identifier"
 
      addRoute="/product/add"
 
      showStatus={true}
      toggleApi="/api/product/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      toggleMethod="POST"
 
      columns={[
        { header: "ID", field: "id" },
        { header: "Identifier", field: "identifier" },
        { header: "Category", field: "category" },
        { header: "Brand", field: "brand" },
        { header: "Model", field: "model" },
        { header: "Unit", field: "unit" },
        { header: "Quantity", field: "quantity" },
        { header: "Status", field: "status" },
      ]}
    />
  );
}