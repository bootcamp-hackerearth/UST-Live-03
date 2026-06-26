'use client';

import PropTypes from 'prop-types';

import CommonList from '@/components/ListPage';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';

import AddStock from '../add/page';
import UpdateStock from '../update/page';

const handleToggleStatus = async (
  row,
  refreshData
) => {
  try {
    await api.post(
      '/stock/togglestatus',
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

const renderExpiryDate = (row) =>
  row.expiryDate
    ? new Date(row.expiryDate).toLocaleDateString('en-IN')
    : 'N/A';

const renderShelf = (row) =>
  Array.isArray(row.shelf)
    ? row.shelf.join(', ')
    : '-';

const renderRack = (row) =>
  Array.isArray(row.rack)
    ? row.rack.join(', ')
    : '-';

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
    status: PropTypes.bool.isRequired
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
  'identifier',
  'product',
  'warehouse',
  'quantity',
  'expiryDate',
  'shelf',
  'rack',
  'status'
];

const customRender = {
  expiryDate: renderExpiryDate,
  shelf: renderShelf,
  rack: renderRack,
  status: renderStatus
};

const StockList = () => (
  <CommonList
    title="Stock List"
    apiUrl="/stock/list"
    deleteUrl="/stock/delete"
    modelName="stock"
    columns={columns}
    AddComponent={AddStock}
    UpdateComponent={UpdateStock}
    customRender={customRender}
  />
);

export default StockList;