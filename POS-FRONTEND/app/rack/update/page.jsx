'use client';

import UpdatePage from "../../../../components/common/UpdatePage";
import Sidebar from "../../../../components/layout/Sidebar";
import Shelves from "../../../../components/dropdown/shelves";

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
component: Shelves,
multiple: true,
},
];

return (
     <Sidebar> <UpdatePage
     fields={fields}
     modelName="rack"
   /> </Sidebar>
);
}
