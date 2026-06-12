'use client';

import CommonList from '@/components/ListPage';

import AddPrice from '../add/page';
import UpdatePrice from '../update/page';

const PriceList = () => {

  return (

    <CommonList
      title="Price List"
      apiUrl="/price/list"
      deleteUrl="/price/delete"
      modelName="price"
      columns={[
        'S.No',
        'identifier',
        'product',
        'type',
        'amount',
        'currency'
      ]}
      AddComponent={AddPrice}
      UpdateComponent={UpdatePrice}
    />

  );

};

export default PriceList;