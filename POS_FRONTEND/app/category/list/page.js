"use client";
 
import CommonList from "../../components/CommonList";
 
export default function CategoryPage() {
  return (
    <CommonList
      title="Category Management"
 
      // ✅ API URLs
      apiUrl="/api/category/list"
      method="POST"
 
      deleteApi="/api/category/delete"
      deleteParam="identifier"
 
      // ✅ ROUTES
      editRoute="/category/edit/:identifier"
      addRoute="/category/add"
 
      // ✅ STATUS TOGGLE (if used in CommonFields)
      showStatus={true}
      toggleApi="/api/category/toggle-status"
      toggleParam="identifier"
      toggleField="status"
      toggleMethod="POST"
 
      columns={[
        { header: "ID", field: "id" },
        { header: "Identifier", field: "identifier" },
 
        // ✅ superCategory field
        {
          header: "Super Category",
          field: "superCategory",
        },
 
        { header: "Status", field: "status" },
      ]}
    />
  );
}