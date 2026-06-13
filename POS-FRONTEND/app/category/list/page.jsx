'use client';
 
import CommonList from '@/components/ListPage';
 
import AddCategory from '../add/page';
import UpdateCategory from '../update/page';
 
const CategoryList = () => {
 
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
        'superCategory'
      ]}
      AddComponent={AddCategory}
      UpdateComponent={UpdateCategory}
      customRender={{
        superCategory: (row) =>
          row.superCategory || '-'
      }}
    />
 
  );
 
};
 
export default CategoryList;
 