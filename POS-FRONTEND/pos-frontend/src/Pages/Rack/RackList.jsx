import React, { useEffect, useState } from "react";
import axios from "axios";
import DynamicList from "../../components/common/DynamicList";
import POSLayout from "../../components/POSLayout";

function RackList() {

  const token = localStorage.getItem("token");

  const [shelfs, setShelfs] = useState([]);

  // LOAD SHELFS
  useEffect(() => {

    axios
      .get(
        "http://localhost:8080/api/shelf/active",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      )
      .then((res) => setShelfs(res.data))
      .catch((err) => console.log(err));

  }, [token]);

  // TABLE COLUMNS
  const columns = [

    {
      key: "identifier",
      label: "Rack",
      type: "text",
    },

    {
      key: "shelf",
      label: "Shelf",
      type: "list",
    },

    {
      key: "status",
      label: "Status",
      type: "toggle",
      toggleUrl: "http://localhost:8080/api/rack/toggle",
    },
  ];

  // FORM FIELDS
  const formFields = [

    {
      key: "identifier",
      label: "Rack Name",
      type: "text",
      placeholder: "Enter Rack Name",
      required: true,
    },

    {
      key: "shelf",
      label: "Shelfs",
      type: "multiselect",
      options: shelfs,
      optionLabel: "identifier",
      optionValue: "identifier",
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
  title="Rack List"

  apiUrl="http://localhost:8080/api/rack/list"

  token={token}

  columns={columns}

  editUrl="/rack/edit"

  deleteUrl="http://localhost:8080/api/rack/delete"

  formFields={formFields}

  // ADD API
  formSubmitUrl="http://localhost:8080/api/rack/add"

  // UPDATE API
  formUpdateUrl="http://localhost:8080/api/rack/update"

  formTitle="Rack"

  uniqueFields={["identifier"]}
/>

    </POSLayout>
  );
}

export default RackList;