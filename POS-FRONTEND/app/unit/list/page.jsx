'use client';

import StatusListPage from '@/components/StatusListPage';

import AddUnit from '../add/page';
import UpdateUnit from '../update/page';

export default function UnitList() {
  return (
    <StatusListPage
      title="Unit List"
      apiUrl="/unit/list"
      deleteUrl="/unit/delete"
      modelName="unit"
      toggleUrl="/unit/togglestatus"
      AddComponent={AddUnit}
      UpdateComponent={UpdateUnit}
    />
  );
}
