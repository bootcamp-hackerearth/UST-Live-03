'use client';

import CommonList from '@/components/ListPage';
import AddProduct from '../add/page';
import UpdateProduct from '../update/page';
import ToggleSwitch from '@/components/ToggleStatus';
import api from '@/app/services/api';


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

const ProductList = () => {

  const toggleStatus = async (
    row,
    refreshData
  ) => {

    try {

      await api.post(
        '/product/toggleStatus',
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

  const columns = [
    'S.No',
    'identifier',
    'name',
    'unit',
    'brand',
    'category',
    'description',
    'status'
  ];

  const customRender = {
    status: (row, refreshData) =>
      renderStatus(
        row,
        refreshData,
        toggleStatus
      )
  };

  return (

    <CommonList
      title="Product List"
      apiUrl="/product/list"
      deleteUrl="/product/delete"
      modelName="product"
      columns={columns}
      AddComponent={AddProduct}
      UpdateComponent={UpdateProduct}
      customRender={customRender}
    />

  );
};

export default ProductList;