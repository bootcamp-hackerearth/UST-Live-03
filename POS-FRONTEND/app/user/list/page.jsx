'use client';

import CommonList from '@/components/ListPage';
import UpdateUser from '../update/page';
import AddUser from '../add/page';

const UserList = () => {
  
  return (
    <CommonList
      title="User List"
      apiUrl="/user/list"
      deleteUrl="/user/delete"
      modelName="user"
      columns={[
        'S.No',
        'identifier',
        'name',
        'phoneNo',
        'roles'
      ]}
      AddComponent={AddUser}
      UpdateComponent={UpdateUser}
    />
  );
};

export default UserList;