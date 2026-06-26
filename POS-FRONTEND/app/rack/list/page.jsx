'use client';

import CommonList from '@/components/ListPage';
import AddRack from '../add/page';
import UpdateRack from '../update/page';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';

const renderStatus = (row, refreshData) => (
  <div className="flex items-center gap-3">
    <ToggleSwitch
      checked={row.status}
      onChange={async () => {
        await api.post('/rack/togglestatus', {
          identifier: row.identifier,
          status: !row.status
        });
        refreshData();
      }}
    />
    <span
      className={`font-semibold ${
        row.status ? 'text-green-600' : 'text-red-600'
      }`}
    >
      {row.status ? 'Active' : 'Inactive'}
    </span>
  </div>
);

export default function RackList() {
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
      customRender={{
        status: renderStatus
      }}
    />
  );
}