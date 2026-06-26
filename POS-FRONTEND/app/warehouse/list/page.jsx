'use client';

import CommonList from '@/components/ListPage';
import AddWarehouse from '../add/page';
import UpdateWarehouse from '../update/page';

const WarehouseList = () => {

  const columns = [
    'S.No',
    'identifier',
    'region',
    'city',
    'state',
    'country',
    'capacity',
    'contactName',
    'contactNumber'
  ];

  const customRender = {
    capacity: (row) => row.capacity || '-',
    contactName: (row) => row.contactName || '-',
    contactNumber: (row) => row.contactNumber || '-',
  };

  return (
    <CommonList
      title="Warehouse List"
      apiUrl="/warehouse/list"
      deleteUrl="/warehouse/delete"
      modelName="warehouse"
      columns={columns}
      AddComponent={AddWarehouse}
      UpdateComponent={UpdateWarehouse}
      customRender={customRender}
    />
  );
};

export default WarehouseList;