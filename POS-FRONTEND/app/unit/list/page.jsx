'use client';

import CommonList from '@/components/ListPage';
import AddUnit from '../add/page';
import UpdateUnit from '../update/page';
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

const UnitList = () => {

    const toggleStatus = async (
        row,
        refreshData
    ) => {

        try {

            await api.post(
                '/unit/toggleStatus',
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
            title="Unit List"
            apiUrl="/unit/list"
            deleteUrl="/unit/delete"
            modelName="unit"
            columns={[
                'S.No',
                'identifier',
                'name',
                'status'
            ]}
            AddComponent={AddUnit}
            UpdateComponent={UpdateUnit}
            customRender={customRender}
        />
    );
};

StatusRenderer.propTypes = {
    row: PropTypes.shape({
        status: PropTypes.bool
    }).isRequired,
    refreshData: PropTypes.func.isRequired,
    toggleStatus: PropTypes.func.isRequired
};

export default UnitList;