'use client';

import CommonList from '@/components/ListPage';

import AddWarehouse from '../add/page';
import UpdateWarehouse from '../update/page';

const WarehouseList = () => {

    return (

        <CommonList
            title="Warehouse List"
            apiUrl="/warehouse/list"
            deleteUrl="/warehouse/delete"
            modelName="warehouse"
            columns={[
                'S.No',
                'identifier',
                'name',
                'contactName',
                'region',
                'city',
                'state',
                'country',
                'capacity',
                'contactNumber'
            ]}
            AddComponent={AddWarehouse}
            UpdateComponent={UpdateWarehouse}
        />
    );
};

export default WarehouseList;