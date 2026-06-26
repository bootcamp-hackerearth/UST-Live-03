'use client';

import CommonList from '@/components/ListPage';
import AddShelf from '../add/page';
import UpdateShelf from '../update/page';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

function StatusRenderer({
    row,
    refreshData,
    toggleStatus
}) {
    return (
        <div className="flex items-center gap-3">

            <ToggleSwitch
                checked={row.status}
                onChange={() =>
                    toggleStatus(row, refreshData)
                }
            />

            <span
                className={
                    row.status
                        ? 'text-green-600 font-semibold'
                        : 'text-red-600 font-semibold'
                }
            >
                {row.status ? 'Active' : 'Inactive'}
            </span>

        </div>
    );
}

const getCustomRender = (toggleStatus) => ({
    status: function renderStatus(
        row,
        refreshData
    ) {
        return (
            <StatusRenderer
                row={row}
                refreshData={refreshData}
                toggleStatus={toggleStatus}
            />
        );
    }
});

const ShelfList = () => {

    const toggleStatus = async (
        row,
        refreshData
    ) => {

        try {

            await api.post(
                '/shelf/toggleStatus',
                {
                    identifier: row.identifier,
                    status: !row.status
                }
            );

            refreshData();

        } catch (err) {

            console.error(err);

            alert(
                'Failed to update status'
            );
        }
    };

    const columns = [
        'S.No',
        'identifier',
        'name',
        'status'
    ];

    const customRender = getCustomRender(toggleStatus);

    return (
        <CommonList
            title="Shelf List"
            apiUrl="/shelf/list"
            deleteUrl="/shelf/delete"
            modelName="shelf"
            columns={columns}
            AddComponent={AddShelf}
            UpdateComponent={UpdateShelf}
            customRender={customRender}
        />
    );
};


StatusRenderer.propTypes = {
    row: PropTypes.shape({
        identifier: PropTypes.string,
        status: PropTypes.bool
    }).isRequired,
    refreshData: PropTypes.func.isRequired,
    toggleStatus: PropTypes.func.isRequired
};

export default ShelfList;