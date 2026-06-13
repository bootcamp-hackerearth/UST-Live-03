'use client';
 
import CommonList from '@/components/ListPage';
 
import AddRole from '../add/page';
import UpdateRole from '../update/page';
 
const RoleList = () => {
 
  return (
 
    <CommonList
      title="Role List"
      apiUrl="/role/list"
      deleteUrl="/role/delete"
      modelName="role"
      columns={[
        'S.No',
        'identifier',
        'description'
      ]}
      AddComponent={AddRole}
      UpdateComponent={UpdateRole}
    />
  );
};
 
export default RoleList;