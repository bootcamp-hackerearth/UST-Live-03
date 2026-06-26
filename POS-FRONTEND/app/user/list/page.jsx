'use client';

import CommonList from '@/components/ListPage';
import UpdateUser from '../update/page';
import AddUser from '../add/page';
import api from '@/app/services/api';

const UserList = () => {

  const userViewHandler = async (
    row,
    setViewData,
    setShowViewModal
  ) => {

    try {

      const response = await api.get(
        `/user/get?username=${row.username}`
      );

      setViewData(response.data);
      setShowViewModal(true);

    } catch (err) {

      console.error(err);
      alert('Failed to load details');

    }
  };

  return (
    <CommonList
      title="User List"
      apiUrl="/user/list"
      deleteUrl="/user/delete"
      modelName="user"
      columns={[
        'S.No',
        'username',
        'name',
        'phoneNo',
        'roles'
      ]}
      AddComponent={AddUser}
      UpdateComponent={UpdateUser}
      customViewHandler={userViewHandler}
    />
  );
};

export default UserList;