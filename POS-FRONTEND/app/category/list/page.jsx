'use client';
 
import CommonList from '@/components/ListPage';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';
 
import AddCategory from '../add/page';
import UpdateCategory from '../update/page';
 
const renderSuperCategory = (row) =>
  row.superCategory || '-';

const renderStatus = (
  row,
  refreshData,
  toggleStatus
) => (
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
const CategoryList = () => {
  const toggleStatus = async (row, refreshData) => {
    try {
      await api.post(
        '/category/toggleStatus',
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
  
  const customRender = {
    superCategory: renderSuperCategory,

    status: (row, refreshData) =>
      renderStatus(
        row,
        refreshData,
        toggleStatus
      )
  };
 
  return (
 
    <CommonList
      title="Category List"
      apiUrl="/category/list"
      deleteUrl="/category/delete"
      modelName="category"
      columns={[
        'S.No',
        'identifier',
        'name',
        'superCategory',
        'status'
      ]}
      AddComponent={AddCategory}
      UpdateComponent={UpdateCategory}
      customRender={customRender}
    />
 
  );
 
};
 
export default CategoryList;