import StatusListPage from '@/components/StatusListPage';
import AddModel from '../add/page';
import UpdateModel from '../update/page';

export default function ModelList() {
  return (
    <StatusListPage
      title="Model List"
      apiUrl="/model/list"
      deleteUrl="/model/delete"
      modelName="model"
      toggleUrl="/model/togglestatus"
      AddComponent={AddModel}
      UpdateComponent={UpdateModel}
    />
  );
}