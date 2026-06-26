'use client';

import CommonList from '@/components/ListPage';
import PropTypes from 'prop-types';
import AddRole from '../add/page';
import UpdateRole from '../update/page';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';

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

const getCustomRender = (toggleStatus) => ({
  status: (row, refreshData) => (
    <StatusRenderer
      row={row}
      refreshData={refreshData}
      toggleStatus={toggleStatus}
    />
  )
});

const RoleList = () => {

  const toggleStatus = async (
    row,
    refreshData
  ) => {

    try {

      await api.post(
        '/role/toggleStatus',
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

  const customRender = getCustomRender(toggleStatus);

  return (

    <CommonList
      title="Role List"
      apiUrl="/role/list"
      deleteUrl="/role/delete"
      modelName="role"
      columns={[
        'S.No',
        'identifier',
        'description',
        'status'
      ]}
      AddComponent={AddRole}
      UpdateComponent={UpdateRole}
      customRender={customRender}
    />

  );
};

StatusRenderer.propTypes = {
  row: PropTypes.shape({
    status: PropTypes.bool,
    identifier: PropTypes.string
  }).isRequired,
  refreshData: PropTypes.func.isRequired,
  toggleStatus: PropTypes.func.isRequired
};

export default RoleList;