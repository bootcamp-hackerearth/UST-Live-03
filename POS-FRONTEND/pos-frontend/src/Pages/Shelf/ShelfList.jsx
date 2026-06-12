import React, { useEffect, useState } from "react";
import axios from "axios";
import DynamicList from "../../components/common/DynamicList";
import POSLayout from "../../components/POSLayout";

function ShelfList() {
  const token = localStorage.getItem("token");

  const columns = [
    {
      key: "identifier",
      label: "Shelf",
      type: "text",
    },
    {
      key: "status",
      label: "Status",
      type: "toggle",
      toggleUrl: "http://localhost:8080/api/shelf/toggle",
    },
  ];

  // FORM FIELDS for the modal
  const formFields = [
    {
      key: "identifier",
      label: "Shelf Name",
      type: "text",
      placeholder: "Enter Name",
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
        title="Shelf List"
        apiUrl="http://localhost:8080/api/shelf/list"
        token={token}
        columns={columns}
        editUrl="/shelf/edit"
        deleteUrl="http://localhost:8080/api/shelf/delete"
        // These 3 props switch Add new → modal instead of navigate
        formFields={formFields}
        formSubmitUrl="http://localhost:8080/api/shelf/add"
        formTitle="Add new shelf"
        formUpdateUrl="http://localhost:8080/api/shelf/update"
        uniqueFields={["identifier"]} 
      />
    </POSLayout>
  );
}

export default ShelfList;