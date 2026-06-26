'use client';

import CommonList from '@/components/ListPage';
import AddCustomer from '../add/page';
import UpdateCustomer from '../update/page';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';

const renderBalance = (row) =>
    `${row.balance || 0} (${row.balanceType || ''})`;

const renderStatus = (row, refreshData, toggleStatus) => (
    <div className="flex items-center gap-3">
        <ToggleSwitch
            checked={row.status}
            onChange={() => toggleStatus(row, refreshData)}
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

const CustomerList = () => {

    const toggleStatus = async (
        row,
        refreshData
    ) => {

        try {

            await api.post(
                '/customer/toggleStatus',
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
        'name',
        'phoneNo',
        'email',
        'partyType',
        'creditLimit',
        'balance',
        'status'
    ];

    const customRender = {
        balance: renderBalance,
        status: (row, refreshData) =>
            renderStatus(row, refreshData, toggleStatus)
    };

    return (
        <CommonList
            title="Customer List"
            apiUrl="/customer/list"
            deleteUrl="/customer/delete"
            modelName="customer"
            columns={columns}
            AddComponent={AddCustomer}
            UpdateComponent={UpdateCustomer}
            customRender={customRender}
        />
    );
};

export default CustomerList;