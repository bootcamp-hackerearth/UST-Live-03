'use client';

import AddPage from "../../../components/common/AddPage";
import Shelves from "../../../components/dropdown/shelves";

export default function RackAdd() {
const fields = [
{
name: "identifier",
label: "Identifier",
type: "text",
},
{
name: "name",
label: "Rack Name",
type: "text",
},
{
name: "shelves",
label: "Shelves",
component: Shelves,
},
];

  return (
      <AddPage
        fields={fields}
        modelName="rack"
      />
  );
}