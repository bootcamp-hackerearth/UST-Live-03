"use client";
 
import CommonListPage from "../../components/CommonList";
 
const RoleList = () => {
  return (
    <CommonListPage
      title="Role Management"
 
      apiUrl="/api/role/list"
      method="POST"
 
      deleteApi="/api/role/delete"
      deleteParam="identifier"
 
      editRoute="/role/edit/:identifier"
      addRoute="/role/add"
 
      sortField="id"
      sortOrder="ASC"
      itemsPerPage={10}
 
      columns={[
        { header: "ID", field: "id" },
        { header: "Identifier", field: "identifier" },
        { header: "Description", field: "description" },
      ]}
    />
  );
};
 
export default RoleList;