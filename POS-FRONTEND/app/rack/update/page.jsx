'use client';

import UpdatePage from "../../../components/common/UpdatePage";
import shelves from "../../../components/dropdown/shelves";

export default function RackUpdate() {
const fields = [
{
name: "name",
label: "Rack Name",
type: "text",
},
{
name: "shelves",
label: "Shelves",
component: shelves,
multiple: true,
},
];

return (
     <UpdatePage
     fields={fields}
     modelName="rack"
   />
);
}
