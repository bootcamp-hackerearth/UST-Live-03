'use client';

import CommonList from '@/components/ListPage';
import AddRack from '../add/page';
import UpdateRack from '../update/page';
import PropTypes from 'prop-types';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';

function ShelfRenderer({ row }) {
    return (
        <span>
            {row.shelfs?.join(', ') || '-'}
        </span>
    );
}

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
                className={`font-semibold ${row.status
                        ? 'text-green-600'
                        : 'text-red-600'
                    }`}
            >
                {row.status
                    ? 'Active'
                    : 'Inactive'}
            </span>
        </div>
    );
}

function renderShelfs(row) {
    return <ShelfRenderer row={row} />;
}

function renderStatus(
    row,
    refreshData,
    toggleStatus
) {
    return (
        <StatusRenderer
            row={row}
            refreshData={refreshData}
            toggleStatus={toggleStatus}
        />
    );
}

const RackList = () => {

    const toggleStatus = async (row, refreshData) => {
        try {

            await api.post('/rack/toggleStatus', {
                identifier: row.identifier,
                status: !row.status
            });

            refreshData();

        } catch (err) {

            console.error(err);
            alert('Failed to update status');
        }
    };

    const customRender = {
        shelfs: renderShelfs,

        status: (row, refreshData) =>
            renderStatus(
                row,
                refreshData,
                toggleStatus
            )
    };

    return (
        <CommonList
            title="Rack List"
            apiUrl="/rack/list"
            deleteUrl="/rack/delete"
            modelName="rack"
            columns={[
                'S.No',
                'identifier',
                'name',
                'shelfs',
                'status'
            ]}
            AddComponent={AddRack}
            UpdateComponent={UpdateRack}
            customRender={customRender}
        />
    );
};

ShelfRenderer.propTypes = {
    row: PropTypes.shape({
        shelfs: PropTypes.array
    }).isRequired
};

StatusRenderer.propTypes = {
    row: PropTypes.shape({
        status: PropTypes.bool
    }).isRequired,
    refreshData: PropTypes.func.isRequired,
    toggleStatus: PropTypes.func.isRequired
};

export default RackList;