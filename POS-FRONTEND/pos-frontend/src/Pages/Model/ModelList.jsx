import React from "react";
import DynamicList from "../../components/common/DynamicList";
import POSLayout from "../../components/POSLayout";

function ModelList() {

    const token = localStorage.getItem("token");

    const columns = [
        {
            key: "identifier",
            label: "Model",
            type: "text",
        },
        {
            key: "status",
            label: "Status",
            type: "toggle",

            toggleUrl:
                "http://localhost:8080/api/model/toggle",

        },
    ];
    const formFields = [
        {
            key:"identifier",
            label:"Model",
            type:"text",
            placeholder:"Enter model name",
            required:true,
            
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
    }
    ];

    return (

        <POSLayout>
            <DynamicList
                title="Model List"
                apiUrl="http://localhost:8080/api/model/list"
                token={token}
                columns={columns}

                editUrl="/model/edit"

                deleteUrl="http://localhost:8080/api/model/delete"
                formFields={formFields}
                formSubmitUrl="http://localhost:8080/api/model/add"
                formTitle="Add new model"
                formUpdateUrl="http://localhost:8080/api/model/update"
                uniqueFields={["identifier"]}


            />
        </POSLayout>

    );
}

export default ModelList;