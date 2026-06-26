'use client';

import PropTypes from 'prop-types';
import CommonList from '@/components/ListPage';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';

const StatusCell = ({ row, refreshData, toggleUrl }) => {
  const handleToggle = async () => {
    try {
      await api.post(toggleUrl, {
        identifier: row.identifier,
        status: !row.status
      });
      refreshData();
    } catch (err) {
      console.error(err);
      alert('Failed to update status');
    }
  };

  return (
    <div className="flex items-center gap-3">
      <ToggleSwitch checked={row.status} onChange={handleToggle} />
      <span
        className={`font-semibold ${
          row.status ? 'text-green-600' : 'text-red-600'
        }`}
      >
        {row.status ? 'Active' : 'Inactive'}
      </span>
    </div>
  );
};

StatusCell.propTypes = {
  row: PropTypes.object.isRequired,
  refreshData: PropTypes.func.isRequired,
  toggleUrl: PropTypes.string.isRequired
};

export default function StatusListPage({
  title,
  apiUrl,
  deleteUrl,
  modelName,
  toggleUrl,
  AddComponent,
  UpdateComponent
}) {
  const renderStatus = (row, refreshData) => (
    <StatusCell
      row={row}
      refreshData={refreshData}
      toggleUrl={toggleUrl}
    />
  );

  return (
    <CommonList
      title={title}
      apiUrl={apiUrl}
      deleteUrl={deleteUrl}
      modelName={modelName}
      columns={['S.No', 'identifier', 'name', 'status']}
      AddComponent={AddComponent}
      UpdateComponent={UpdateComponent}
      customRender={{ status: renderStatus }}
    />
  );
}

StatusListPage.propTypes = {
  title: PropTypes.string.isRequired,
  apiUrl: PropTypes.string.isRequired,
  deleteUrl: PropTypes.string.isRequired,
  modelName: PropTypes.string.isRequired,
  toggleUrl: PropTypes.string.isRequired,
  AddComponent: PropTypes.elementType.isRequired,
  UpdateComponent: PropTypes.elementType.isRequired
};
