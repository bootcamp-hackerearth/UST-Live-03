'use client';

import CommonList from '@/components/ListPage';
import AddStock from '../add/page';
import UpdateStock from '../update/page';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';
import PropTypes from 'prop-types';

function StockStatusRenderer({ row }) {
    return (
        <span
            className={`font-semibold ${row.stockStatus
                    ? 'text-green-600'
                    : 'text-red-600'
                }`}
        >
            {row.stockStatus
                ? 'In Stock'
                : 'Out Of Stock'}
        </span>
    );
}

function ExpiryDateRenderer({ row }) {
    return row.expiryDate
        ? new Date(row.expiryDate)
            .toLocaleDateString('en-IN')
        : '-';
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
                    toggleStatus(
                        row,
                        refreshData
                    )
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

const getCustomRender = (toggleStatus) => ({
    stockStatus: function renderStockStatus(row) {
        return (
            <StockStatusRenderer row={row} />
        );
    },

    expiryDate: function renderExpiryDate(row) {
        return (
            <ExpiryDateRenderer row={row} />
        );
    },

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

const StockList = () => {

    const toggleStatus = async (
        row,
        refreshData
    ) => {

        try {

            await api.post(
                '/stock/toggleStatus',
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

    const customRender =
        getCustomRender(toggleStatus);

    return (
        <CommonList
            title="Stock List"
            apiUrl="/stock/list"
            deleteUrl="/stock/delete"
            modelName="stock"
            columns={[
                'S.No',
                'identifier',
                'product',
                'warehouse',
                'quantity',
                'stockStatus',
                'expiryDate',
                'status'
            ]}
            AddComponent={AddStock}
            UpdateComponent={UpdateStock}
            customRender={customRender}
        />
    );
};

StockStatusRenderer.propTypes = {
    row: PropTypes.shape({
        stockStatus: PropTypes.bool
    }).isRequired
};

ExpiryDateRenderer.propTypes = {
    row: PropTypes.shape({
        expiryDate: PropTypes.string
    }).isRequired
};

StatusRenderer.propTypes = {
    row: PropTypes.shape({
        status: PropTypes.bool,
        identifier: PropTypes.string
    }).isRequired,
    refreshData: PropTypes.func.isRequired,
    toggleStatus: PropTypes.func.isRequired
};

export default StockList;