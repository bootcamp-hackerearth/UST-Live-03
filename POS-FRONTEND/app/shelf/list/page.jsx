import StatusListPage from '@/components/StatusListPage';
import AddShelf from '../add/page';
import UpdateShelf from '../update/page';

export default function ShelfList() {
  return (
    <StatusListPage
      title="Shelf List"
      apiUrl="/shelf/list"
      deleteUrl="/shelf/delete"
      modelName="shelf"
      toggleUrl="/shelf/togglestatus"
      AddComponent={AddShelf}
      UpdateComponent={UpdateShelf}
    />
  );
}