'use client';

import CommonList from '@/components/ListPage';
import AddBrand from '../add/page';
import UpdateBrand from '../update/page';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';

const renderStatus = (
    row,
    refreshData,
    toggleStatus
) => (
    <div className="flex items-center gap-3">
        <ToggleSwitch
            checked={row.status}
            onChange={() =>
                toggleStatus(row, refreshData)
            }
        />

        <span
            className={`font-semibold ${row.status
                    ? 'text-green-600'
                    : 'text-red-600'
                }`}
        >
            {row.status ? 'Active' : 'Inactive'}
        </span>
    </div>
);

const BrandList = () => {

    const toggleStatus = async (
        row,
        refreshData
    ) => {

        try {

            await api.post(
                '/brand/toggleStatus',
                {
                    identifier: row.identifier,
                    status: !row.status
                }
            );

            refreshData();

        } catch (err) {

            console.error(err);
            alert('Failed to update status');

        }

    };

    const columns = [
        'S.No',
        'identifier',
        'name',
        'description',
        'status'
    ];

    const customRender = {
        status: (row, refreshData) =>
            renderStatus(
                row,
                refreshData,
                toggleStatus
            )
    };
    
    return (

        <CommonList
            title="Brand List"
            apiUrl="/brand/list"
            deleteUrl="/brand/delete"
            modelName="brand"
            columns={columns}
            AddComponent={AddBrand}
            UpdateComponent={UpdateBrand}
            customRender={customRender}
        />

    );
};

export default BrandList;