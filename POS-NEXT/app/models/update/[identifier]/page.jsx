import AddEditForm from "@/components/AddEditForm";

export default function ModelsUpdate() {

    const fields = [
        {
            name: "identifier", type: "text", placeholder: "Model Name", required: true,
            readOnly: true
        },
        {
            name: "description", type: "text", placeholder: "Model Description", required: false,
            readOnly: false
        },
    ];

    const dropdownApis = {
    };

    return (

        <AddEditForm
            title="Model"
            dropdownApis={dropdownApis}
            apiRoute="models"
            fields={fields}
            method="update" />

    )
}