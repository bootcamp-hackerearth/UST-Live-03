import React from "react";
 
import DynamicList from "../../components/common/DynamicList";
 
import POSLayout from "../../components/POSLayout";
 
function BrandList() {
 
  const columns = [
    {
      key: "identifier",
      label: "Brand",
      type: "text",
    },
    {
      key: "status",
      label: "Status",
      type: "toggle",
    },
  ];
 
  const formFields = [
    {
      key: "identifier",
      label: "Brand",
      type: "text",
      required: true,
      placeholder: "Enter brand name",
    },
    {
      key: "status",
      label: "Status",
      type: "select",
      options: [
        {
          label: "Active",
          value: true,
        },
        {
          label: "Inactive",
          value: false,
        },
      ],
      optionLabel: "label",
      optionValue: "value",
    },
  ];
 
  return (
 
    <POSLayout>
 
      <DynamicList
        title="Brand List"
 
        routeName="brand"
 
        columns={columns}
 
        editUrl="/brand/edit"
 
        formFields={formFields}
 
        formTitle="Brand"
 
        uniqueFields={["identifier"]}
      />
 
    </POSLayout>
 
  );
}
 
export default BrandList;
 