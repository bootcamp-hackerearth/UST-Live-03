'use client';

import PropTypes from 'prop-types';
import CommonList from '@/components/ListPage';
import AddBrand from '../add/page';
import UpdateBrand from '../update/page';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';

const toggleBrandStatus = async (
  row,
  refreshData
) => {

  try {

    await api.post(
      '/brand/togglestatus',
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

const StatusCell = ({
  row,
  refreshData
}) => (
  <div className="flex items-center gap-3">

    <ToggleSwitch
      checked={row.status}
      onChange={() =>
        toggleBrandStatus(
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
    status: PropTypes.bool
  }).isRequired,
  refreshData: PropTypes.func.isRequired
};

const renderStatusCell = (
  row,
  refreshData
) => (
  <StatusCell
    row={row}
    refreshData={refreshData}
  />
);

const customRender = {
  description: (row) =>
    row.description || '-',

  status: renderStatusCell
};

const columns = [
  'S.No',
  'identifier',
  'name',
  'description',
  'status'
];

const BrandList = () => {

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