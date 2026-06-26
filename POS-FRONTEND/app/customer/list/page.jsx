'use client';

import PropTypes from 'prop-types';

import CommonList from '@/components/ListPage';
import AddCustomer from '../add/page';
import UpdateCustomer from '../update/page';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';

const handleToggleStatus = async (
  row,
  refreshData
) => {
  try {
    await api.post(
      '/customer/togglestatus',
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

const renderBalance = (row) =>
  `${row.balance || 0} (${row.balanceType || ''})`;

const StatusCell = ({
  row,
  refreshData
}) => (
  <div className="flex items-center gap-3">
    <ToggleSwitch
      checked={row.status}
      onChange={() =>
        handleToggleStatus(
          row,
          refreshData
        )
      }
    />

    <span
      className={`font-semibold ${
        row.status
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

StatusCell.propTypes = {
  row: PropTypes.shape({
    identifier: PropTypes.string,
    status: PropTypes.bool.isRequired,
    balance: PropTypes.oneOfType([
      PropTypes.number,
      PropTypes.string
    ]),
    balanceType: PropTypes.string
  }).isRequired,
  refreshData: PropTypes.func.isRequired
};

const renderStatus = (
  row,
  refreshData
) => (
  <StatusCell
    row={row}
    refreshData={refreshData}
  />
);

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
  status: renderStatus
};

const CustomerList = () => (
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

export default CustomerList;