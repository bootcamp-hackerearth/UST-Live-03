import React from "react";
import DynamicList from "../../components/common/DynamicList";
import POSLayout from "../../components/POSLayout";

function UnitList() {

  const columns = [
    {
      key: "identifier",
      label: "Unit",
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
      label: "Unit",
      type: "text",
      placeholder: "Enter Unit",
      required: true,
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
        title="Unit List"
        routeName="unit"
        columns={columns}
        editUrl="/unit/edit"
        formFields={formFields}
        formTitle="Unit"
        uniqueFields={["identifier"]}
      />
    </POSLayout>
  );
}

export default UnitList;