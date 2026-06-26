'use client';

import CommonList from '@/components/ListPage';
import AddModel from '../add/page';
import UpdateModel from '../update/page';
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

const ModelList = () => {

    const toggleStatus = async (
        row,
        refreshData
    ) => {

        try {

            await api.post(
                '/model/toggleStatus',
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
            title="Model List"
            apiUrl="/model/list"
            deleteUrl="/model/delete"
            modelName="model"
            columns={[
                'S.No',
                'identifier',
                'name',
                'status'
            ]}
            AddComponent={AddModel}
            UpdateComponent={UpdateModel}
            customRender={customRender}
        />
    );
};

export default ModelList;