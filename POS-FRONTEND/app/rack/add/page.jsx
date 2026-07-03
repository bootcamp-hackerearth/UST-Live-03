'use client';

import AddPage from "../../../components/common/AddPage";
import shelves from "../../../components/dropdown/shelves";

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
component: shelves,
},
];

  return (
      <AddPage
        fields={fields}
        modelName="rack"
      />
  );
}