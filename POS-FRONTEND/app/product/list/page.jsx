'use client';

import CommonList from '@/components/ListPage';

import AddProduct from '../add/page';
import UpdateProduct from '../update/page';

const ProductList = () => {

  return (

    <CommonList
      title="Product List"
      apiUrl="/product/list"
      deleteUrl="/product/delete"
      modelName="product"
      columns={[
        'S.No',
        'identifier',
        'name',
        'unit',
        'brand',
        'category',
        'description'
      ]}
      AddComponent={AddProduct}
      UpdateComponent={UpdateProduct}
    />

  );
};

export default ProductList;