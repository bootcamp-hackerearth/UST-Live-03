'use client';
 
import CommonList from '@/components/ListPage';

import AddNode from '../add/page';
import UpdateNode from '../update/page';
 
const NodeList = () => {
 
  return (
    <CommonList
      title="Node List"
      apiUrl="/node/list"
      deleteUrl="/node/delete"
      modelName="node"
      columns={[
        'S.No',
        'identifier',
        'path',
        'roles'
      ]}
      AddComponent={AddNode}
      UpdateComponent={UpdateNode}
    />
  );
};
 
export default NodeList;