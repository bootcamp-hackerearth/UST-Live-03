import AddEditForm from "@/components/AddEditForm";

export default function BrandUpdate() {

    const fields = [
        {
            name: "identifier", type: "text", placeholder: "Brand Name", required: true,
            readOnly: true
        },
        {
            name: "description", type: "text", placeholder: "Brand Description", required: false,
            readOnly: false
        },
    ];

    const dropdownApis = {
    };

    return (

        <AddEditForm
            title="Brand"
            dropdownApis={dropdownApis}
            apiRoute="brand"
            fields={fields}
            method="update" />

    )
}